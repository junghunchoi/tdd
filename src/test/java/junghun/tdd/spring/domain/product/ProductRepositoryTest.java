package junghun.tdd.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

import static junghun.tdd.spring.domain.product.ProductSellingStatus.*;
import static junghun.tdd.spring.domain.product.ProductType.HANDMADE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@DataJdbcTest // 좀 더 간단하게 테스트하고 싶다면 이걸 사용해도 된다.
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @DisplayName("원하는 판매상태를 가진 상품을 조회한다.")
    @Test
    void findAllBySellingStatusIn() {
        // given
        Product product1 = Product.builder()
                .productNumber("001")
                .productType(HANDMADE)
                .name("아메리카노")
                .price(4000)
                .sellingStatus(SELLING)
                .build();

        Product product2 = Product.builder()
                                  .productNumber("002")
                                  .productType(HANDMADE)
                                  .name("라뗴")
                                  .price(4000)
                                  .sellingStatus(SELLING)
                                  .build();

        Product product3 = Product.builder()
                                  .productNumber("003")
                                  .productType(HANDMADE)
                                  .name("빵")
                                  .price(4000)
                                  .sellingStatus(STOP_SELLING)
                                  .build();
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        var products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

        // then
        assertThat(products).hasSize(2)
                .extracting("productNumber", "name", "sellingStatus")
                .containsExactlyInAnyOrder(
                        tuple("001", "아메리카노", SELLING),
                        tuple("002", "라뗴", HOLD)
                );

    }
}
