package junghun.tdd.unit;


import junghun.tdd.unit.beverage.Beverage;
import junghun.tdd.unit.order.Order;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CafeKiosk {
    private static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10, 0);
    private static final LocalTime SHOP_CLOSE_TIME = LocalTime.of(22, 0);
    private final List<Beverage> beverages = new ArrayList<>();

    public void add(Beverage beverage, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than zero.");
        }
        for (int i = 0; i < count; i++) {
            beverages.add(beverage);
        }
    }

    public void remove(Beverage beverage) {
        beverages.remove(beverage);
    }

    public void clear()       {
        beverages.clear();
    }

    public int calculateTotalPrice() {
        int totalaPrice = 0;
        for (Beverage beverage : beverages) {
            totalaPrice += beverage.getPrice();
        }
        return totalaPrice;
    }

    public Order createOrder() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalTime currentLocalTime = currentTime.toLocalTime();
        if(currentLocalTime.isBefore(SHOP_OPEN_TIME) || currentLocalTime.isAfter(SHOP_CLOSE_TIME)) {
            throw new IllegalStateException("The cafe is closed. Please come back during business hours.");
        }
        return new Order(LocalDateTime.now(), beverages);
    }
}
