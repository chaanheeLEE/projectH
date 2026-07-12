package practice.projecth.api.cart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.projecth.api.cart.dto.AddCartItemRequest;
import practice.projecth.api.cart.dto.CartResponse;
import practice.projecth.api.cart.dto.UpdateCartItemRequest;
import practice.projecth.application.cart.usecase.CartUseCase;
import practice.projecth.domain.cart.Cart;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private final CartUseCase cartUseCase;

    public CartController(CartUseCase cartUseCase) {
        this.cartUseCase = cartUseCase;
    }

    @PostMapping
    public ResponseEntity<CartResponse> addCartItem(@RequestBody AddCartItemRequest request) {
        Cart cart = cartUseCase.addCartItem(
                request.getMemberId(),
                request.getProductId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            @PathVariable Long productId,
            @RequestBody UpdateCartItemRequest request
    ) {
        Cart cart = cartUseCase.updateCartItemQuantity(
                request.getMemberId(),
                productId,
                request.getQuantity()
        );
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable Long productId,
            @RequestParam Long memberId
    ) {
        Cart cart = cartUseCase.removeCartItem(memberId, productId);
        return ResponseEntity.ok(CartResponse.from(cart));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestParam Long memberId) {
        Cart cart = cartUseCase.getCart(memberId);
        return ResponseEntity.ok(CartResponse.from(cart));
    }
}
