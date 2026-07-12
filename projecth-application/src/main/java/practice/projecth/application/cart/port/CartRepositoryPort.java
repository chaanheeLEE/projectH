package practice.projecth.application.cart.port;

import practice.projecth.domain.cart.Cart;

import java.util.Optional;

public interface CartRepositoryPort {
    Cart save(Cart cart);
    Optional<Cart> findByMemberId(Long memberId);
}
