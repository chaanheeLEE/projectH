package practice.projecth.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.projecth.application.order.port.OrderRepositoryPort;
import practice.projecth.application.order.usecase.OrderUseCase;
import practice.projecth.application.product.usecase.ProductUseCase;
import practice.projecth.domain.order.Order;
import practice.projecth.domain.order.OrderItem;
import practice.projecth.domain.order.OrderStatus;
import practice.projecth.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderIntegrationTest {

    @Autowired
    private OrderUseCase orderUseCase;

    @Autowired
    private ProductUseCase productUseCase;

    @Autowired
    private OrderRepositoryPort orderRepositoryPort;

    private Product product;

    @BeforeEach
    void setUp() {
        product = productUseCase.registerProduct("아이폰", new BigDecimal("100000"), 10, 1L);
        productUseCase.updateStatus(product.getId(), practice.projecth.domain.product.ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("주문 생성 시 비동기로 재고 차감 및 결제 승인이 완료된다")
    void orderSuccessFlow() throws InterruptedException {
        // given
        OrderItem item = new OrderItem(product.getId(), product.getSalePrice(), 2);

        // when
        Order order = orderUseCase.createOrder(1L, List.of(item));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING); // 생성 직후 상태

        // 비동기 처리 대기 (최대 2초)
        boolean completed = waitForOrderStatus(order.getId(), OrderStatus.PAYMENT_APPROVED, 2000);

        // then
        assertThat(completed).isTrue();
        Order finalOrder = orderUseCase.getOrder(order.getId());
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.PAYMENT_APPROVED);

        Product finalProduct = productUseCase.getProduct(product.getId());
        assertThat(finalProduct.getStock().getQuantity()).isEqualTo(8); // 10 - 2 = 8
    }

    @Test
    @DisplayName("결제 실패 시 보상 트랜잭션이 작동하여 재고가 복구되고 주문은 취소된다")
    void paymentFailFlow() throws InterruptedException {
        // given - 50만원을 초과하는 결제 금액을 설정하여 결제 실패 시뮬레이션 유도 (총액: 60만원)
        OrderItem item = new OrderItem(product.getId(), product.getSalePrice(), 6);

        // when
        Order order = orderUseCase.createOrder(1L, List.of(item));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 비동기 처리 대기 (최대 2초)
        boolean completed = waitForOrderStatus(order.getId(), OrderStatus.CANCELLED, 2000);

        // then
        assertThat(completed).isTrue();
        Order finalOrder = orderUseCase.getOrder(order.getId());
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Product finalProduct = productUseCase.getProduct(product.getId());
        assertThat(finalProduct.getStock().getQuantity()).isEqualTo(10); // 10개로 원복됨
    }

    @Test
    @DisplayName("낙관적 락 재시도: 동시에 여러 주문이 들어와도 낙관적 락 재시도를 통해 재고 차감이 안전하게 이루어진다")
    void optimisticLockRetry() throws InterruptedException {
        // given
        int threadCount = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    OrderItem item = new OrderItem(product.getId(), product.getSalePrice(), 1);
                    orderUseCase.createOrder(1L, List.of(item));
                } catch (Exception e) {
                    System.out.println("Error during order placement: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // 비동기 이벤트가 모두 처리될 때까지 충분히 대기
        Thread.sleep(2000);

        // then
        Product finalProduct = productUseCase.getProduct(product.getId());
        // 10개 중 4번의 주문(각 1개)이 정상적으로 차감되어 6개가 남아 있어야 함
        assertThat(finalProduct.getStock().getQuantity()).isEqualTo(6);
    }

    private boolean waitForOrderStatus(Long orderId, OrderStatus targetStatus, long timeoutMs) throws InterruptedException {
        long limit = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < limit) {
            try {
                Order order = orderUseCase.getOrder(orderId);
                if (order.getStatus() == targetStatus) {
                    return true;
                }
            } catch (Exception ignored) {}
            Thread.sleep(100);
        }
        return false;
    }
}
