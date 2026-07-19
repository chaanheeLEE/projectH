package practice.projecth.infrastructure.persistence.order;

import org.springframework.stereotype.Component;
import practice.projecth.domain.common.Money;
import practice.projecth.domain.order.Order;
import practice.projecth.domain.order.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toDomain(OrderJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        List<OrderItem> items = jpaEntity.getOrderItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        new Money(item.getPrice()),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new Order(
                jpaEntity.getId(),
                jpaEntity.getBuyerId(),
                items,
                jpaEntity.getStatus(),
                jpaEntity.getVersion()
        );
    }

    public OrderJpaEntity toJpaEntity(Order domain) {
        if (domain == null) {
            return null;
        }
        List<OrderItemJpaEntity> itemEntities = domain.getOrderItems().stream()
                .map(item -> new OrderItemJpaEntity(
                        null,
                        item.getProductId(),
                        item.getPrice().getAmount(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderJpaEntity(
                domain.getId(),
                domain.getBuyerId(),
                itemEntities,
                domain.getTotalPrice().getAmount(),
                domain.getStatus(),
                domain.getVersion()
        );
    }
}
