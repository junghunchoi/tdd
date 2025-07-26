package junghun.tdd.spring.api.service.product;

import junghun.tdd.spring.api.service.product.response.ProductResponse;
import junghun.tdd.spring.domain.product.Product;
import junghun.tdd.spring.domain.product.ProductRepository;
import junghun.tdd.spring.domain.product.ProductSellingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;


    public List<ProductResponse> getProducts() {
        List<Product> products =  productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

        return products.stream()
                .map(ProductResponse::of)
                .toList();
    }
}
