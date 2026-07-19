package practice.projecth.api.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import practice.projecth.domain.product.ProductStatus;

@Getter
@Setter
@NoArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "변경할 상태는 필수입니다.")
    private ProductStatus status;

    public UpdateStatusRequest(ProductStatus status) {
        this.status = status;
    }
}
