package practice.projecth.infrastructure.persistence.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.projecth.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long buyerId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemJpaEntity> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Version
    private Long version;

    public OrderJpaEntity(Long id, Long buyerId, List<OrderItemJpaEntity> orderItems, BigDecimal totalPrice, OrderStatus status, Long version) {
        this.id = id;
        this.buyerId = buyerId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.version = version;
        if (orderItems != null) {
            orderItems.forEach(this::addOrderItem);
        }
    }

    public void addOrderItem(OrderItemJpaEntity item) {
        this.orderItems.add(item);
        item.setOrder(this);
    }
}
