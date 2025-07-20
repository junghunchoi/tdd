package junghun.tdd.unit.beverage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AmericanoTest {
    @Test
    void getPrice() {
        // given
        Americano americano = new Americano();

        // when
        int price = americano.getPrice();

        // then
        assertEquals(4000, price);
    }

}