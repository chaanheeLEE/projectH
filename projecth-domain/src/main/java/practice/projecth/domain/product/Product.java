package practice.projecth.domain.product;

import lombok.Getter;
import practice.projecth.domain.common.Money;

@Getter
public class Product {
    private final Long id;
    private final String name;
    private final Money salePrice;
    private final Stock stock;
    private final ProductStatus status;
    private final Long categoryId;

    public Product(Long id, String name, Money salePrice, Stock stock, ProductStatus status, Long categoryId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }
        if (salePrice == null) {
            throw new IllegalArgumentException("상품 가격은 필수입니다.");
        }
        if (stock == null) {
            throw new IllegalArgumentException("상품 재고는 필수입니다.");
        }
        if (status == null) {
            throw new IllegalArgumentException("상품 상태는 필수입니다.");
        }
        this.id = id;
        this.name = name;
        this.salePrice = salePrice;
        this.stock = stock;
        this.status = status;
        this.categoryId = categoryId;
    }

    // 재고 차감 비즈니스 로직
    public Product decreaseStock(int quantity) {
        Stock updatedStock = this.stock.decrease(quantity);
        ProductStatus updatedStatus = this.status;

        // 재고가 0이 되면 자동으로 품절 상태로 변경
        if (updatedStock.getQuantity() == 0 && this.status == ProductStatus.ON_SALE) {
            updatedStatus = ProductStatus.OUT_OF_STOCK;
        }

        return new Product(this.id, this.name, this.salePrice, updatedStock, updatedStatus, this.categoryId);
    }

    // 재고 추가 비즈니스 로직
    public Product increaseStock(int quantity) {
        Stock updatedStock = this.stock.increase(quantity);
        ProductStatus updatedStatus = this.status;

        // 일시 품절 상태였던 상품에 재고가 공급되면 자동으로 판매중으로 복구
        if (this.status == ProductStatus.OUT_OF_STOCK && updatedStock.getQuantity() > 0) {
            updatedStatus = ProductStatus.ON_SALE;
        }

        return new Product(this.id, this.name, this.salePrice, updatedStock, updatedStatus, this.categoryId);
    }

    // 상품 상태 강제 변경 (예: 수동 판매 종료, 판매 개시 등)
    public Product changeStatus(ProductStatus newStatus) {
        if (this.status == ProductStatus.SUSPENDED && newStatus != ProductStatus.PREPARING) {
            throw new IllegalStateException("판매 종료된 상품은 준비중 상태로만 변경할 수 있습니다.");
        }

        ProductStatus targetStatus = newStatus;
        // 상태를 판매중으로 변경하려고 할 때 재고가 0이면 자동으로 OUT_OF_STOCK으로 설정
        if (newStatus == ProductStatus.ON_SALE && this.stock.getQuantity() == 0) {
            targetStatus = ProductStatus.OUT_OF_STOCK;
        }

        return new Product(this.id, this.name, this.salePrice, this.stock, targetStatus, this.categoryId);
    }
}
