package practice.projecth.infrastructure.persistence.product;

import org.springframework.stereotype.Repository;
import practice.projecth.application.product.port.ProductRepositoryPort;
import practice.projecth.domain.product.Product;

import java.util.Optional;

@Repository
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository springDataProductRepository;
    private final ProductMapper productMapper;

    public ProductRepositoryAdapter(SpringDataProductRepository springDataProductRepository, ProductMapper productMapper) {
        this.springDataProductRepository = springDataProductRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity jpaEntity = productMapper.toJpaEntity(product);
        ProductJpaEntity savedEntity = springDataProductRepository.save(jpaEntity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return springDataProductRepository.findById(id)
                .map(productMapper::toDomain);
    }
}
