package junghun.tdd.unit.order;

import junghun.tdd.unit.beverage.Beverage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Order {
    private LocalDateTime orderTime;
    private List<Beverage> beverages;

    public Order(LocalDateTime now, List<Beverage> beverages) {
        this.orderTime = now;
        this.beverages = beverages;
    }
}
