package junghun.tdd.spring.api.service.product.response;


import junghun.tdd.spring.domain.product.Product;
import junghun.tdd.spring.domain.product.ProductSellingStatus;
import junghun.tdd.spring.domain.product.ProductType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {
    private Long id;

    private String productNumber;

    private ProductType productType;

    private ProductSellingStatus productSellingType;

    private String name;

    private int price;

    @Builder
    private ProductResponse(Long id, String productNumber, ProductType productType, ProductSellingStatus productSellingType, String name, int price) {
        this.id = id;
        this.productNumber = productNumber;
        this.productType = productType;
        this.productSellingType = productSellingType;
        this.name = name;
        this.price = price;
    }

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productNumber(product.getProductNumber())
                .productType(product.getProductType())
                .productSellingType(product.getSellingStatus())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}
