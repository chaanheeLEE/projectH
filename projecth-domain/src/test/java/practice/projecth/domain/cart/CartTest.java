package practice.projecth.domain.cart;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.projecth.domain.common.Money;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CartTest {

    @Test
    @DisplayName("장바구니에 새 아이템을 추가하면 품목이 생성되고 가격 스냅샷이 기록된다")
    void addNewItemToCart() {
        // given
        Cart cart = new Cart(1L);

        // when
        Cart updatedCart = cart.addItem(100L, 2, new Money(BigDecimal.valueOf(5000)));

        // then
        assertThat(updatedCart.getItems()).hasSize(1);
        CartItem addedItem = updatedCart.getItems().get(0);
        assertThat(addedItem.getProductId()).isEqualTo(100L);
        assertThat(addedItem.getQuantity()).isEqualTo(2);
        assertThat(addedItem.getPriceSnapshot()).isEqualTo(new Money(BigDecimal.valueOf(5000)));
        assertThat(updatedCart.getTotalCartPrice()).isEqualTo(new Money(BigDecimal.valueOf(10000)));
    }

    @Test
    @DisplayName("장바구니에 동일한 상품을 추가하면 수량이 누적되고 스냅샷 가격이 갱신된다")
    void addDuplicateItemAccumulatesQuantityAndUpdatesPriceSnapshot() {
        // given
        Cart cart = new Cart(1L);
        Cart updatedCart = cart.addItem(100L, 2, new Money(BigDecimal.valueOf(5000)));

        // when
        Cart doubleUpdatedCart = updatedCart.addItem(100L, 3, new Money(BigDecimal.valueOf(6000)));

        // then
        assertThat(doubleUpdatedCart.getItems()).hasSize(1);
        CartItem item = doubleUpdatedCart.getItems().get(0);
        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getPriceSnapshot()).isEqualTo(new Money(BigDecimal.valueOf(6000)));
        assertThat(doubleUpdatedCart.getTotalCartPrice()).isEqualTo(new Money(BigDecimal.valueOf(30000))); // 6000 * 5 = 30000
    }

    @Test
    @DisplayName("장바구니에서 특정 상품을 제거할 수 있다")
    void removeItemFromCart() {
        // given
        Cart cart = new Cart(1L);
        Cart updatedCart = cart.addItem(100L, 2, new Money(BigDecimal.valueOf(5000)))
                .addItem(200L, 1, new Money(BigDecimal.valueOf(15000)));

        // when
        Cart itemRemovedCart = updatedCart.removeItem(100L);

        // then
        assertThat(itemRemovedCart.getItems()).hasSize(1);
        assertThat(itemRemovedCart.getItems().get(0).getProductId()).isEqualTo(200L);
        assertThat(itemRemovedCart.getTotalCartPrice()).isEqualTo(new Money(BigDecimal.valueOf(15000)));
    }

    @Test
    @DisplayName("장바구니의 상품 수량을 직접 수정할 수 있으며 수량이 0 이하면 상품이 제거된다")
    void updateItemQuantityInCart() {
        // given
        Cart cart = new Cart(1L);
        Cart updatedCart = cart.addItem(100L, 2, new Money(BigDecimal.valueOf(5000)));

        // when
        Cart quantityUpdatedCart = updatedCart.updateItemQuantity(100L, 5);

        // then
        assertThat(quantityUpdatedCart.getItems().get(0).getQuantity()).isEqualTo(5);

        // when (수량을 0으로 갱신)
        Cart zeroQuantityCart = quantityUpdatedCart.updateItemQuantity(100L, 0);

        // then (상품이 제거됨)
        assertThat(zeroQuantityCart.getItems()).isEmpty();
    }
}
