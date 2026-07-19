package practice.projecth.application.order.usecase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practice.projecth.domain.order.OrderItem;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PaymentService {

    /**
     * 가상 결제 요청 메서드.
     * 특정 금액(예: 1,000,000원)을 넘거나, 특정 결제 조건에 따라 실패를 시뮬레이션하여 보상 트랜잭션 테스트를 가능하게 합니다.
     */
    public void requestPayment(Long orderId, List<OrderItem> items) {
        log.info("[Payment] 결제 요청 시작 - 주문 ID: {}", orderId);

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .map(practice.projecth.domain.common.Money::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 시뮬레이션: 총 결제 금액이 500,000원을 초과하면 한도 초과 등의 이유로 결제 실패 발생
        if (totalAmount.compareTo(new BigDecimal("500000")) > 0) {
            log.warn("[Payment Fail] 한도 초과 결제 실패 시뮬레이션 - 주문 ID: {}, 금액: {}", orderId, totalAmount);
            throw new IllegalArgumentException("결제 한도를 초과하여 결제에 실패하였습니다. (최대 50만원)");
        }

        log.info("[Payment] 결제 승인 성공 - 주문 ID: {}, 금액: {}", orderId, totalAmount);
    }
}
