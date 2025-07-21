package junghun.tdd.unit;

import junghun.tdd.unit.beverage.Latte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CafeKioskTest {
    @Test
    void add() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Latte latte = new Latte();

        // when
        cafeKiosk.add(latte, 1);

        // then

    }

    @Test
    public void addIllegalArgumentException() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Latte latte = new Latte();

        // when, then
        assertThrows(IllegalArgumentException.class, () -> {
            cafeKiosk.add(latte, 0);
        });
    }

    @Test
    void calculateTotalPrice() {
        // given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Latte latte = new Latte();
        cafeKiosk.add(latte, 2);

        // when
        int totalPrice = cafeKiosk.calculateTotalPrice();

        // then
        assertEquals(9000, totalPrice);
    }


}