package junghun.tdd.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import junghun.tdd.unit.beverage.Latte;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Slf4j
public class BasicAssertionsTest {
    @Nested
    @DisplayName("연산")
    class OperationTests {
        @Test
        @DisplayName("add 메서드가 음수를 허용하지 않아야 한다")
        void addNegativeQuantity() {
            // given
            CafeKiosk cafeKiosk = new CafeKiosk();
            Latte latte = new Latte();

            // when, then
            assertThrows(IllegalArgumentException.class, () -> {
                cafeKiosk.add(latte, -1);
            });
        }

        @Test
        @DisplayName("calculateTotalPrice 메서드는 올바른 총 가격을 반환해야 한다")
        void calculateTotalPriceReturnsCorrectValue() {
            // given
            CafeKiosk cafeKiosk = new CafeKiosk();
            Latte latte = new Latte();
            cafeKiosk.add(latte, 3);

            // when
            int totalPrice = cafeKiosk.calculateTotalPrice();

            // then
            assertEquals(13500, totalPrice);
        }
    }

    @Test
    void 기본_assertEquals() {
        // 기본 비교
        assertEquals(4, 2 + 2);
        assertEquals("Hello", "He" + "llo");
        log.info("gd?");

        // 메시지와 함께
        assertEquals(4, 2 + 2, "2 + 2는 4여야 한다");
        assertThrows(IllegalArgumentException.class, ()->{
            throw new IllegalArgumentException("예외 발생");
        }, "예외가 발생해야 한다");
        }
    }
