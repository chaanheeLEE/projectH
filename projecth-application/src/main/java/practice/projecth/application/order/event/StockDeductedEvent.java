package practice.projecth.application.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import practice.projecth.domain.order.OrderItem;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class StockDeductedEvent {
    private final Long orderId;
    private final List<OrderItem> orderItems;
}
