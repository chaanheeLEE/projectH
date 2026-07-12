package practice.projecth.domain.product;

import lombok.Getter;

@Getter
public enum ProductStatus {
    PREPARING("판매 준비중"),
    ON_SALE("판매중"),
    OUT_OF_STOCK("일시 품절"),
    SUSPENDED("판매 종료");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

}
