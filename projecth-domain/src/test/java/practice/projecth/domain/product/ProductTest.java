package practice.projecth.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.projecth.domain.common.Money;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("재고가 차감되어 0이 되면 상품 상태가 자동으로 OUT_OF_STOCK으로 변경된다")
    void decreaseStockToZeroChangesStatusToOutOfStock() {
        // given
        Product product = new Product(
                1L,
                "테스트 상품",
                new Money(BigDecimal.valueOf(10000)),
                new Stock(10),
                ProductStatus.ON_SALE,
                100L
        );

        // when
        Product updatedProduct = product.decreaseStock(10);

        // then
        assertThat(updatedProduct.getStock().getQuantity()).isZero();
        assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("일시 품절 상태에서 재고가 추가되면 상품 상태가 자동으로 ON_SALE로 복구된다")
    void increaseStockFromOutOfStockChangesStatusToOnSale() {
        // given
        Product product = new Product(
                1L,
                "테스트 상품",
                new Money(BigDecimal.valueOf(10000)),
                new Stock(0),
                ProductStatus.OUT_OF_STOCK,
                100L
        );

        // when
        Product updatedProduct = product.increaseStock(5);

        // then
        assertThat(updatedProduct.getStock().getQuantity()).isEqualTo(5);
        assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("수량이 부족할 때 재고를 차감하면 예외가 발생한다")
    void decreaseStockMoreThanAvailableThrowsException() {
        // given
        Product product = new Product(
                1L,
                "테스트 상품",
                new Money(BigDecimal.valueOf(10000)),
                new Stock(5),
                ProductStatus.ON_SALE,
                100L
        );

        // when & then
        assertThatThrownBy(() -> product.decreaseStock(6))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고가 부족합니다");
    }

    @Test
    @DisplayName("판매 종료된 상품은 준비중 상태로만 강제 전환이 가능하다")
    void suspendedProductCanOnlyChangeToPreparing() {
        // given
        Product product = new Product(
                1L,
                "테스트 상품",
                new Money(BigDecimal.valueOf(10000)),
                new Stock(5),
                ProductStatus.SUSPENDED,
                100L
        );

        // when
        Product updatedProduct = product.changeStatus(ProductStatus.PREPARING);

        // then
        assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.PREPARING);

        // when & then (판매중으로 직접 전이 시 예외 발생 확인)
        assertThatThrownBy(() -> product.changeStatus(ProductStatus.ON_SALE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("판매 종료된 상품은 준비중 상태로만 변경할 수 있습니다");
    }
}
