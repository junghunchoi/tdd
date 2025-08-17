package junghun.tdd.spring.api.service.order;

import static junghun.tdd.spring.domain.product.ProductSellingStatus.SELLING;
import static junghun.tdd.spring.domain.product.ProductSellingStatus.STOP_SELLING;
import static junghun.tdd.spring.domain.product.ProductType.HANDMADE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import junghun.tdd.spring.api.controller.order.request.OrderCreateRequest;
import junghun.tdd.spring.api.service.order.response.OrderResponse;
import junghun.tdd.spring.domain.product.Product;
import junghun.tdd.spring.domain.product.ProductRepository;
import junghun.tdd.spring.domain.product.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
//@DataJpaTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("주문번호 리스트를 받아 주문을 생성한다.")
    @Test
    void createOrder() {

        // given
        Product product1 = createProduct(HANDMADE, "001", 4000);
        Product product2 = createProduct(HANDMADE, "001", 4100);
        Product product3 = createProduct(HANDMADE, "001", 4200);

        productRepository.saveAll(List.of(product1, product2, product3));

        OrderCreateRequest request = OrderCreateRequest.builder()
            .productNumbers(List.of("001", "002", "003"))
            .build();

        //when
        OrderResponse response = orderService.createOrder(request);

        //then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
            .extracting("registeredDateTime", "totalPrice")
            .contains(LocalDateTime.now(),4000);
        assertThat(response.getProducts()).hasSize(2)
            .extracting("productNumber", "name", "price")
            .containsExactlyInAnyOrder(
                tuple("001", "메뉴이름", 4000),
                tuple("002", "메뉴이름", 4100)
            );


    }

    // builder 패턴을 사용하면 라인이 너무 길어져 이런식으로 간단하게 엔티티를 생성할 수 있는 메소드를 구현
    private Product createProduct(ProductType type, String productNumber,  int price) {
        return Product.builder()
                .productType(type)
                .productNumber(productNumber)
                .name("메뉴이름")
                .price(price)
                .sellingStatus(SELLING)
                .build();
    }
}