package practice.projecth.application.order.port;

import practice.projecth.domain.order.Order;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
}
