package practice.projecth.application.order.usecase;

import practice.projecth.domain.order.Order;
import practice.projecth.domain.order.OrderItem;

import java.util.List;

public interface OrderUseCase {
    Order createOrder(Long buyerId, List<OrderItem> items);
    Order completeStockDeduction(Long orderId);
    Order approvePayment(Long orderId);
    Order cancelOrder(Long orderId, String reason);
    Order failOrder(Long orderId, String reason);
    Order getOrder(Long orderId);
}
