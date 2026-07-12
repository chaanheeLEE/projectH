package practice.projecth.infrastructure.persistence.cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCartRepository extends JpaRepository<CartJpaEntity, Long> {
}
