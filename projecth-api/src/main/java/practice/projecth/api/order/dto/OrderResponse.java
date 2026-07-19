package practice.projecth.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import practice.projecth.domain.order.Order;
import practice.projecth.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long buyerId;
    private List<OrderItemResponse> orderItems;
    private BigDecimal totalPrice;
    private OrderStatus status;

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(item.getProductId(), item.getPrice().getAmount(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getBuyerId(),
                itemResponses,
                order.getTotalPrice().getAmount(),
                order.getStatus()
        );
    }
}
