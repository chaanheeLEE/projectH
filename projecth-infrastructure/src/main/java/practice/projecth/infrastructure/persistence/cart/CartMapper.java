package practice.projecth.infrastructure.persistence.cart;

import org.springframework.stereotype.Component;
import practice.projecth.domain.cart.Cart;
import practice.projecth.domain.cart.CartItem;
import practice.projecth.domain.common.Money;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public Cart toDomain(CartJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        List<CartItem> domainItems = jpaEntity.getItems().stream()
                .map(itemEntity -> new CartItem(
                        itemEntity.getProductId(),
                        itemEntity.getQuantity(),
                        new Money(itemEntity.getPriceSnapshot())
                ))
                .collect(Collectors.toList());

        return new Cart(jpaEntity.getMemberId(), domainItems);
    }

    public CartJpaEntity toJpaEntity(Cart domain) {
        if (domain == null) {
            return null;
        }

        CartJpaEntity jpaEntity = new CartJpaEntity(domain.getMemberId());
        
        List<CartItemJpaEntity> itemEntities = domain.getItems().stream()
                .map(domainItem -> new CartItemJpaEntity(
                        null, // 새로운 Entity 생성을 위함
                        domainItem.getProductId(),
                        domainItem.getQuantity(),
                        domainItem.getPriceSnapshot().getAmount()
                ))
                .collect(Collectors.toList());

        jpaEntity.updateItems(itemEntities);
        return jpaEntity;
    }
}
