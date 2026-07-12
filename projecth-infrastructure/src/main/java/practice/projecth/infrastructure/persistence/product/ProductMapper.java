package practice.projecth.infrastructure.persistence.product;

import org.springframework.stereotype.Component;
import practice.projecth.domain.common.Money;
import practice.projecth.domain.product.Product;
import practice.projecth.domain.product.Stock;

@Component
public class ProductMapper {

    public Product toDomain(ProductJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        return new Product(
                jpaEntity.getId(),
                jpaEntity.getName(),
                new Money(jpaEntity.getSalePrice()),
                new Stock(jpaEntity.getStockQuantity()),
                jpaEntity.getStatus(),
                jpaEntity.getCategoryId()
        );
    }

    public ProductJpaEntity toJpaEntity(Product domain) {
        if (domain == null) {
            return null;
        }
        return new ProductJpaEntity(
                domain.getId(),
                domain.getName(),
                domain.getSalePrice().getAmount(),
                domain.getStock().getQuantity(),
                domain.getStatus(),
                domain.getCategoryId()
        );
    }
}
