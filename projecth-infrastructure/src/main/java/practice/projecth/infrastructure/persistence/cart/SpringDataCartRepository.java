package practice.projecth.infrastructure.persistence.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataCartRepository extends JpaRepository<CartJpaEntity, Long> {

    @Query("select distinct c from CartJpaEntity c left join fetch c.items where c.memberId = :memberId")
    Optional<CartJpaEntity> findByMemberIdWithItems(@Param("memberId") Long memberId);
}
