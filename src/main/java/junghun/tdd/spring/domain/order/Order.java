package junghun.tdd.spring.domain.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import junghun.tdd.spring.domain.OrderProduct;
import junghun.tdd.spring.domain.product.BaseEntity;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private int totalPrice;

    private LocalDateTime registeredDateTime;

    /*
      * 주문 상품은 지연로딩으로 설정한다.
      * OrderProduct는 Order를 참조하고 있으므로, OrderProduct가 먼저 로딩되어야 한다.
      * 따라서, OrderProduct를 먼저 로딩하고, 그 다음에 Order를 로딩하는 것이 좋다.
      * 이 경우, OrderProduct가 먼저 로딩되면, Order도 함께 로딩된다.
      * 하나의 주문엔 여러 상품이 존재할 수 있다.
     */
    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>(); // 지연로딩시 npe를 방지
}
