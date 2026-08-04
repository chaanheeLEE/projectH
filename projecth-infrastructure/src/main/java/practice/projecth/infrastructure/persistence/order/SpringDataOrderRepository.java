package practice.projecth.infrastructure.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, Long> {

    @Query("select distinct o from OrderJpaEntity o left join fetch o.orderItems where o.id = :id")
    Optional<OrderJpaEntity> findByIdWithItems(@Param("id") Long id);
}
