package junghun.tdd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TddApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("사용자 정의 테스트 이름")
    void customTestName() {
        assertTrue(true);
    }


}
