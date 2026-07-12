package practice.projecth.api.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.projecth.api.product.dto.ProductResponse;
import practice.projecth.api.product.dto.RegisterProductRequest;
import practice.projecth.api.product.dto.StockRequest;
import practice.projecth.api.product.dto.UpdateStatusRequest;
import practice.projecth.application.product.usecase.ProductUseCase;
import practice.projecth.domain.product.Product;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> registerProduct(@RequestBody RegisterProductRequest request) {
        Product registered = productUseCase.registerProduct(
                request.getName(),
                request.getPrice(),
                request.getInitialStock(),
                request.getCategoryId()
        );
        return ResponseEntity.ok(ProductResponse.from(registered));
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductResponse> updateStatus(
            @PathVariable Long productId,
            @RequestBody UpdateStatusRequest request
    ) {
        Product updated = productUseCase.updateStatus(productId, request.getStatus());
        return ResponseEntity.ok(ProductResponse.from(updated));
    }

    @PostMapping("/{productId}/stock/increase")
    public ResponseEntity<ProductResponse> increaseStock(
            @PathVariable Long productId,
            @RequestBody StockRequest request
    ) {
        Product updated = productUseCase.increaseStock(productId, request.getQuantity());
        return ResponseEntity.ok(ProductResponse.from(updated));
    }

    @PostMapping("/{productId}/stock/decrease")
    public ResponseEntity<ProductResponse> decreaseStock(
            @PathVariable Long productId,
            @RequestBody StockRequest request
    ) {
        Product updated = productUseCase.decreaseStock(productId, request.getQuantity());
        return ResponseEntity.ok(ProductResponse.from(updated));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        Product product = productUseCase.getProduct(productId);
        return ResponseEntity.ok(ProductResponse.from(product));
    }
}
