package junghun.tdd.spring.api.service.order.response;


import java.time.LocalDateTime;
import java.util.List;
import junghun.tdd.spring.api.service.product.response.ProductResponse;
import junghun.tdd.spring.domain.order.OrderStatus;
import lombok.Getter;

@Getter
public class OrderResponse {

    private Long id;

    private int totalPrice;

    private LocalDateTime registeredDateTime;

    private List<ProductResponse> products;
}
