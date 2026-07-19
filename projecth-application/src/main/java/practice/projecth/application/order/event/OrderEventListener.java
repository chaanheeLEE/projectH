package practice.projecth.application.order.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import practice.projecth.application.order.usecase.OrderUseCase;
import practice.projecth.application.order.usecase.PaymentService;
import practice.projecth.application.order.usecase.StockDeductionFacade;
import practice.projecth.application.product.usecase.ProductUseCase;
import practice.projecth.domain.order.OrderItem;

@Slf4j
@Component
public class OrderEventListener {

    private final StockDeductionFacade stockDeductionFacade;
    private final PaymentService paymentService;
    private final OrderUseCase orderUseCase;
    private final ProductUseCase productUseCase;

    public OrderEventListener(StockDeductionFacade stockDeductionFacade, PaymentService paymentService,
                              OrderUseCase orderUseCase, ProductUseCase productUseCase) {
        this.stockDeductionFacade = stockDeductionFacade;
        this.paymentService = paymentService;
        this.orderUseCase = orderUseCase;
        this.productUseCase = productUseCase;
    }

    /**
     * 1단계: 주문 접수 완료 후 비동기로 재고 차감 처리
     * 메인 주문 트랜잭션이 성공적으로 DB에 Commit된 직후 실행됩니다.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("[Event Listener] OrderPlacedEvent 수신 - 주문 ID: {}", event.getOrderId());
        try {
            stockDeductionFacade.deductStock(event.getOrderId(), event.getOrderItems());
        } catch (Exception e) {
            log.error("[Event Listener Exception] OrderPlacedEvent 처리 중 오류 발생", e);
        }
    }

    /**
     * 2단계: 재고 차감 완료 후 비동기로 결제 처리 및 실패 시 보상 트랜잭션 실행
     * 재고 차감 트랜잭션이 정상 Commit된 직후 실행됩니다.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockDeducted(StockDeductedEvent event) {
        log.info("[Event Listener] StockDeductedEvent 수신 - 주문 ID: {}", event.getOrderId());
        try {
            // 1. 가상 결제 서비스 호출
            paymentService.requestPayment(event.getOrderId(), event.getOrderItems());

            // 2. 결제 성공 시 -> 주문 완료 처리 (PAYMENT_APPROVED)
            orderUseCase.approvePayment(event.getOrderId());
            log.info("[Event Listener] 주문 결제 완료 처리 성공 - 주문 ID: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("[Payment Failure] 결제 실패 발생 - 주문 ID: {}. 보상 트랜잭션(재고 복구 및 주문 취소)을 시작합니다. 사유: {}",
                    event.getOrderId(), e.getMessage());

            // 3. 결제 실패 시 -> 보상 트랜잭션(Compensating Transaction) 실행
            rollbackStockAndCancelOrder(event);
        }
    }

    /**
     * 보상 트랜잭션: 차감했던 재고를 다시 추가하고, 주문 상태를 CANCELLED로 변경
     */
    private void rollbackStockAndCancelOrder(StockDeductedEvent event) {
        try {
            // 각 상품 재고 원복
            for (OrderItem item : event.getOrderItems()) {
                productUseCase.increaseStock(item.getProductId(), item.getQuantity());
                log.info("[Compensating Tx] 재고 롤백 완료 - 상품 ID: {}, 수량: {}", item.getProductId(), item.getQuantity());
            }

            // 주문 취소 처리
            orderUseCase.cancelOrder(event.getOrderId(), "결제 실패로 인한 자동 취소 및 재고 롤백");
            log.info("[Compensating Tx] 주문 취소 완료 - 주문 ID: {}", event.getOrderId());

        } catch (Exception ex) {
            log.error("[Compensating Tx CRITICAL FAIL] 보상 트랜잭션 중 치명적 오류 발생! 주문 ID: {}", event.getOrderId(), ex);
            // 실무에서는 대기 큐나 Dead Letter Queue(DLQ), 혹은 관리자 알림 등으로 격리하여 수동 처리하도록 유도해야 함
        }
    }
}
