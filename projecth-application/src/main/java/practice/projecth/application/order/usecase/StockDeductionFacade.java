package practice.projecth.application.order.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import practice.projecth.application.order.event.StockDeductedEvent;
import practice.projecth.application.product.usecase.ProductUseCase;
import practice.projecth.domain.order.OrderItem;

import java.util.List;

@Slf4j
@Component
public class StockDeductionFacade {

    private final ProductUseCase productUseCase;
    private final OrderUseCase orderUseCase;
    private final ApplicationEventPublisher eventPublisher;

    public StockDeductionFacade(ProductUseCase productUseCase, OrderUseCase orderUseCase, ApplicationEventPublisher eventPublisher) {
        this.productUseCase = productUseCase;
        this.orderUseCase = orderUseCase;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 낙관적 락으로 인해 재고 차감 실패 시 최대 5회 재시도 (대기시간 50ms, 배수 2)
     * 이 메서드는 각각의 재고 차감 시도마다 새 트랜잭션을 실행하도록 REQUIRES_NEW로 설정하여
     * 개별 시도의 실패가 이전 트랜잭션 영역에 영향을 주지 않도록 격리합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockingFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 50, multiplier = 2.0)
    )
    public void deductStock(Long orderId, List<OrderItem> items) {
        log.info("[StockDeduction] 재고 차감 시작 - 주문 ID: {}", orderId);
        
        for (OrderItem item : items) {
            // 각 상품의 재고를 차감. 상품이 변경되면서 version이 증가하며, 동시 변경 시 OptimisticLockingFailureException 발생
            productUseCase.decreaseStock(item.getProductId(), item.getQuantity());
        }

        // 주문 상태를 재고 차감 완료(STOCK_DEDUCTED)로 변경
        orderUseCase.completeStockDeduction(orderId);

        log.info("[StockDeduction] 재고 차감 완료 - 주문 ID: {}", orderId);

        // 다음 단계인 결제 요청을 위한 이벤트 발행
        eventPublisher.publishEvent(new StockDeductedEvent(orderId, items));
    }

    /**
     * 재시도 횟수를 모두 초과하거나, 재고 부족 등의 비즈니스 예외 발생 시 호출되는 복구(보상 트랜잭션) 메서드
     */
    @Recover
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recover(Exception e, Long orderId, List<OrderItem> items) {
        log.error("[StockDeduction Fail] 주문 ID: {} - 재고 차감 최종 실패. 사유: {}", orderId, e.getMessage(), e);
        
        // 보상 트랜잭션: 주문을 FAILED 상태로 업데이트
        orderUseCase.failOrder(orderId, "재고 차감 실패: " + e.getMessage());
    }
}
