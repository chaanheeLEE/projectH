package practice.projecth.infrastructure.persistence.cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartJpaEntity {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemJpaEntity> items = new ArrayList<>();

    public CartJpaEntity(Long memberId) {
        this.memberId = memberId;
    }

    public void updateItems(List<CartItemJpaEntity> newItems) {
        this.items.clear();
        if (newItems != null) {
            for (CartItemJpaEntity item : newItems) {
                item.setCart(this);
                this.items.add(item);
            }
        }
    }
}
