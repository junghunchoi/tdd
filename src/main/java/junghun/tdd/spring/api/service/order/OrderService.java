package junghun.tdd.spring.api.service.order;

import java.util.List;
import junghun.tdd.spring.api.controller.order.request.OrderCreateRequest;
import junghun.tdd.spring.api.service.order.response.OrderResponse;
import junghun.tdd.spring.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
//    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    public OrderResponse createOrder(OrderCreateRequest request) {
        List<String> productNumbers = request.getProductNumbers();

        //product
        return null;
    }
}
