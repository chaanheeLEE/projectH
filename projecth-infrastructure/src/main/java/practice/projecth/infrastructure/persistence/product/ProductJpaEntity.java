package practice.projecth.infrastructure.persistence.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.projecth.domain.product.ProductStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    private Long categoryId;

    public ProductJpaEntity(Long id, String name, BigDecimal salePrice, int stockQuantity, ProductStatus status, Long categoryId) {
        this.id = id;
        this.name = name;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.categoryId = categoryId;
    }
}
