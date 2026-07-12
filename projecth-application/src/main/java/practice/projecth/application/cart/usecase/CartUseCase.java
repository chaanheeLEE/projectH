package practice.projecth.application.cart.usecase;

import practice.projecth.domain.cart.Cart;

public interface CartUseCase {
    Cart addCartItem(Long memberId, Long productId, int quantity);
    Cart removeCartItem(Long memberId, Long productId);
    Cart updateCartItemQuantity(Long memberId, Long productId, int quantity);
    Cart getCart(Long memberId);
}
