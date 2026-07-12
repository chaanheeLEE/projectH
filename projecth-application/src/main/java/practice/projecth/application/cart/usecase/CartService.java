package practice.projecth.application.cart.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.projecth.application.cart.port.CartRepositoryPort;
import practice.projecth.application.product.port.ProductRepositoryPort;
import practice.projecth.domain.cart.Cart;
import practice.projecth.domain.product.Product;
import practice.projecth.domain.product.ProductStatus;

@Service
@Transactional(readOnly = true)
public class CartService implements CartUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public CartService(CartRepositoryPort cartRepositoryPort, ProductRepositoryPort productRepositoryPort) {
        this.cartRepositoryPort = cartRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public Cart addCartItem(Long memberId, Long productId, int quantity) {
        // 1. 상품 존재 및 판매 가능 여부 검증
        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. ID: " + productId));

        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new IllegalStateException("현재 판매 중이 아닌 상품은 장바구니에 담을 수 없습니다. 상태: " + product.getStatus().getDescription());
        }

        // 2. 장바구니 조회 혹은 생성
        Cart cart = cartRepositoryPort.findByMemberId(memberId)
                .orElseGet(() -> new Cart(memberId));

        // 3. 상품 가격 스냅샷과 수량 반영하여 아이템 추가
        Cart updatedCart = cart.addItem(productId, quantity, product.getSalePrice());

        return cartRepositoryPort.save(updatedCart);
    }

    @Override
    @Transactional
    public Cart removeCartItem(Long memberId, Long productId) {
        Cart cart = cartRepositoryPort.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다. Member ID: " + memberId));

        Cart updatedCart = cart.removeItem(productId);
        return cartRepositoryPort.save(updatedCart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long memberId, Long productId, int quantity) {
        Cart cart = cartRepositoryPort.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 비어 있습니다. Member ID: " + memberId));

        Cart updatedCart = cart.updateItemQuantity(productId, quantity);
        return cartRepositoryPort.save(updatedCart);
    }

    @Override
    public Cart getCart(Long memberId) {
        return cartRepositoryPort.findByMemberId(memberId)
                .orElseGet(() -> new Cart(memberId));
    }
}
