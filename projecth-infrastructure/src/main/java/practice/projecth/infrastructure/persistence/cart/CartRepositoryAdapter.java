package practice.projecth.infrastructure.persistence.cart;

import org.springframework.stereotype.Repository;
import practice.projecth.application.cart.port.CartRepositoryPort;
import practice.projecth.domain.cart.Cart;

import java.util.Optional;

@Repository
public class CartRepositoryAdapter implements CartRepositoryPort {

    private final SpringDataCartRepository springDataCartRepository;
    private final CartMapper cartMapper;

    public CartRepositoryAdapter(SpringDataCartRepository springDataCartRepository, CartMapper cartMapper) {
        this.springDataCartRepository = springDataCartRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public Cart save(Cart cart) {
        Optional<CartJpaEntity> existingEntityOpt = springDataCartRepository.findById(cart.getMemberId());

        CartJpaEntity jpaEntity;
        if (existingEntityOpt.isPresent()) {
            jpaEntity = existingEntityOpt.get();
            CartJpaEntity newValues = cartMapper.toJpaEntity(cart);
            jpaEntity.updateItems(newValues.getItems());
        } else {
            jpaEntity = cartMapper.toJpaEntity(cart);
        }

        CartJpaEntity savedEntity = springDataCartRepository.save(jpaEntity);
        return cartMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Cart> findByMemberId(Long memberId) {
        return springDataCartRepository.findById(memberId)
                .map(cartMapper::toDomain);
    }
}
