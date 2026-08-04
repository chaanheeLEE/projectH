package practice.projecth.infrastructure.persistence.order;

import org.springframework.stereotype.Repository;
import practice.projecth.application.order.port.OrderRepositoryPort;
import practice.projecth.domain.order.Order;

import java.util.Optional;

@Repository
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;
    private final OrderMapper orderMapper;

    public OrderRepositoryAdapter(SpringDataOrderRepository springDataOrderRepository, OrderMapper orderMapper) {
        this.springDataOrderRepository = springDataOrderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity jpaEntity = orderMapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = springDataOrderRepository.save(jpaEntity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return springDataOrderRepository.findByIdWithItems(id)
                .map(orderMapper::toDomain);
    }
}
