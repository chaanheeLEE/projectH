package practice.projecth.application.order.usecase;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.projecth.application.order.event.OrderPlacedEvent;
import practice.projecth.application.order.port.OrderRepositoryPort;
import practice.projecth.domain.order.Order;
import practice.projecth.domain.order.OrderItem;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepositoryPort orderRepositoryPort, ApplicationEventPublisher eventPublisher) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Order createOrder(Long buyerId, List<OrderItem> items) {
        Order order = Order.create(buyerId, items);
        Order savedOrder = orderRepositoryPort.save(order);

        // 주문 생성 완료 이벤트 발행 (재고 차감을 위함)
        eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder.getId(), savedOrder.getOrderItems()));

        return savedOrder;
    }

    @Override
    @Transactional
    public Order completeStockDeduction(Long orderId) {
        Order order = getOrderById(orderId);
        Order updatedOrder = order.completeStockDeduction();
        return orderRepositoryPort.save(updatedOrder);
    }

    @Override
    @Transactional
    public Order approvePayment(Long orderId) {
        Order order = getOrderById(orderId);
        Order updatedOrder = order.approvePayment();
        return orderRepositoryPort.save(updatedOrder);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);
        Order updatedOrder = order.cancel(reason);
        return orderRepositoryPort.save(updatedOrder);
    }

    @Override
    @Transactional
    public Order failOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);
        Order updatedOrder = order.fail(reason);
        return orderRepositoryPort.save(updatedOrder);
    }

    @Override
    public Order getOrder(Long orderId) {
        return getOrderById(orderId);
    }

    private Order getOrderById(Long orderId) {
        return orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다. ID: " + orderId));
    }
}
