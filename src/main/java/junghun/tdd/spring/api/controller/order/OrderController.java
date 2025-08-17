package junghun.tdd.spring.api.controller.order;

import junghun.tdd.spring.api.controller.order.request.OrderCreateRequest;
import junghun.tdd.spring.api.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/api/v1/orders/new")
    public void createOrder(OrderCreateRequest request) {
         orderService.createOrder(request);
    }
}
