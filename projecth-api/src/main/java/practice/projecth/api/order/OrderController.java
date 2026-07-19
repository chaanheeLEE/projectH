package practice.projecth.api.order;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.projecth.api.order.dto.CreateOrderRequest;
import practice.projecth.api.order.dto.OrderResponse;
import practice.projecth.application.order.usecase.OrderUseCase;
import practice.projecth.domain.common.Money;
import practice.projecth.domain.order.Order;
import practice.projecth.domain.order.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderItem> domainItems = request.getItems().stream()
                .map(item -> new OrderItem(item.getProductId(), new Money(item.getPrice()), item.getQuantity()))
                .collect(Collectors.toList());

        Order order = orderUseCase.createOrder(request.getBuyerId(), domainItems);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderUseCase.getOrder(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
