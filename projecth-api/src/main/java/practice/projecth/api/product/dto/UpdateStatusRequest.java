package practice.projecth.api.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import practice.projecth.domain.product.ProductStatus;

@Getter
@Setter
@NoArgsConstructor
public class UpdateStatusRequest {
    private ProductStatus status;

    public UpdateStatusRequest(ProductStatus status) {
        this.status = status;
    }
}
