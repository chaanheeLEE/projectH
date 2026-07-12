package practice.projecth.domain.cart;

import lombok.Getter;
import practice.projecth.domain.common.Money;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class Cart {
    private final Long memberId;
    private final List<CartItem> items;

    public Cart(Long memberId) {
        this(memberId, new ArrayList<>());
    }

    public Cart(Long memberId, List<CartItem> items) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 필수입니다.");
        }
        this.memberId = memberId;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // 아이템 추가 로직
    public Cart addItem(Long productId, int quantity, Money currentPrice) {
        List<CartItem> updatedItems = new ArrayList<>(this.items);
        Optional<CartItem> existingItemOpt = updatedItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            updatedItems.remove(existingItem);
            // 이미 장바구니에 있다면 수량 누적 및 가격 스냅샷은 현재 가격으로 업데이트
            updatedItems.add(new CartItem(productId, existingItem.getQuantity() + quantity, currentPrice));
        } else {
            updatedItems.add(new CartItem(productId, quantity, currentPrice));
        }

        return new Cart(this.memberId, updatedItems);
    }

    // 아이템 제거 로직
    public Cart removeItem(Long productId) {
        List<CartItem> updatedItems = new ArrayList<>(this.items);
        updatedItems.removeIf(item -> item.getProductId().equals(productId));
        return new Cart(this.memberId, updatedItems);
    }

    // 아이템 수량 수정 로직
    public Cart updateItemQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            return removeItem(productId);
        }

        List<CartItem> updatedItems = new ArrayList<>(this.items);
        for (int i = 0; i < updatedItems.size(); i++) {
            CartItem item = updatedItems.get(i);
            if (item.getProductId().equals(productId)) {
                updatedItems.set(i, item.changeQuantity(quantity));
                return new Cart(this.memberId, updatedItems);
            }
        }
        // 장바구니에 존재하지 않으면 변경하지 않음 (혹은 예외 발생도 가능하나 여기서는 미동작 처리)
        return this;
    }

    // 장바구니 내 총 합계 금액 산출
    public Money getTotalCartPrice() {
        return this.items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(Money.ZERO, Money::plus);
    }
}
