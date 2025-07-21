# TDD 실전 학습 시나리오: Smart Cafe Kiosk

이 시나리오는 제공된 TDD 가이드 문서를 바탕으로, `CafeKiosk` 프로젝트를 점진적으로 발전시키며 TDD를 심도 있게 학습하는 것을 목표로 합니다.

## Phase 1: 테스트 개선과 시간 의존성 리팩토링

현재 테스트 코드는 개선의 여지가 많고, `createOrder` 메서드는 시간에 의존하고 있어 테스트하기 어렵습니다. 먼저 이 문제를 해결해 봅시다.

### Step 1-1: 기존 테스트 리팩토링 (Refactor)

`CafeKioskTest.java`의 기존 테스트들은 검증이 부족하거나 의도를 파악하기 어렵습니다. 학습한 내용을 바탕으로 개선해 보세요.

**Action:**
1.  `add()` 테스트에 AssertJ를 사용하여 검증 구문을 추가하세요.
    -   음료가 정확히 추가되었는지 (`beverages` 리스트의 크기)
    -   총 주문 금액이 올바르게 계산되었는지
2.  `@DisplayName`을 사용하여 각 테스트의 의도를 명확히 표현하세요.
3.  `@Nested`를 사용하여 '음료 추가', '주문 생성' 등 기능별로 테스트를 그룹화하여 구조를 개선하세요.

**개선된 테스트 코드 예시:**
```java
@DisplayName("카페 키오스크 테스트")
class CafeKioskTest {

    private CafeKiosk cafeKiosk;
    private Latte latte;

    @BeforeEach
    void setUp() {
        cafeKiosk = new CafeKiosk();
        latte = new Latte();
    }

    @Nested
    @DisplayName("음료 추가 기능")
    class AddBeverage {
        @Test
        @DisplayName("음료를 1개 추가하면 주문 목록에 담기고 총 가격이 업데이트된다")
        void add() {
            // when
            cafeKiosk.add(latte, 1);

            // then
            assertThat(cafeKiosk.getBeverages()).hasSize(1);
            assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("라떼");
            assertThat(cafeKiosk.calculateTotalPrice()).isEqualTo(4500);
        }

        @Test
        @DisplayName("음료 수량은 0이하일 수 없다")
        void addWithZeroOrLessQuantity() {
            // when & then
            assertThatThrownBy(() -> cafeKiosk.add(latte, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Count must be greater than zero.");
        }
    }
    // ... 다른 기능 그룹
}
```

### Step 1-2: 시간 의존성 제거 (Red-Green-Refactor)

`createOrder()`는 `LocalDateTime.now()`에 직접 의존하여 영업시간 테스트를 작성하기 까다롭습니다. Mockito를 활용하여 이 문제를 해결해 봅시다.

#### Red: 실패하는 테스트 작성
영업시간 경계값을 테스트하는 코드를 작성하세요. 이 테스트는 현재 구현으로는 안정적으로 통과하기 어렵습니다.

```java
@Nested
@DisplayName("주문 생성 기능")
class CreateOrder {
    @Test
    @DisplayName("영업 종료 시간(22:01)에는 주문할 수 없다")
    void createOrderOutsideBusinessHours() {
        // given
        // 특정 시간을 반환하도록 제어해야 함

        // when & then
        assertThatThrownBy(() -> cafeKiosk.createOrder())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("The cafe is closed. Please come back during business hours.");
    }
}
```

#### Green: 테스트 통과를 위한 최소 구현
`LocalDateTime.now()`를 직접 호출하는 대신, 시간을 제공하는 `TimeProvider` 인터페이스를 만들고 `CafeKiosk`가 이를 주입받도록 변경하세요.

**`TimeProvider.java` (신규 생성)**
```java
package junghun.tdd.unit;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
}
```

**`CafeKiosk.java` 수정**
```java
public class CafeKiosk {
    // ... 기존 필드
    private final TimeProvider timeProvider;

    public CafeKiosk(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public Order createOrder() {
        LocalDateTime currentTime = timeProvider.now(); // 주입받은 객체 사용
        // ... 기존 로직
    }
}
```

**테스트 코드 수정**
```java
@Test
@DisplayName("영업 종료 시간(22:01)에는 주문할 수 없다")
void createOrderOutsideBusinessHours() {
    // given
    TimeProvider mockTimeProvider = mock(TimeProvider.class);
    when(mockTimeProvider.now()).thenReturn(LocalDateTime.of(2025, 7, 21, 22, 1));
    
    CafeKiosk cafeKiosk = new CafeKiosk(mockTimeProvider);
    cafeKiosk.add(new Latte());

    // when & then
    assertThatThrownBy(() -> cafeKiosk.createOrder())
        .isInstanceOf(IllegalStateException.class);
}
```

#### Refactor: 코드 개선
- `CafeKiosk`의 기본 생성자가 기존처럼 동작하도록 `new CafeKiosk()` 호출 시 실제 시간을 제공하는 `SystemTimeProvider`를 사용하게 만드세요.
- 테스트 코드의 중복을 제거하고 가독성을 높이세요.

## Phase 2: 신규 기능 개발 - 주문 관리

이제 새로운 기능을 TDD로 개발해 봅시다. 주문 상태를 관리하고, 주문을 취소하는 기능을 추가합니다.

### Step 2-1: 주문 상태 관리 (Red-Green-Refactor)

**요구사항:**
- `Order` 객체는 생성 시 `PENDING`(주문 대기) 상태를 가진다.
- 주문을 `CONFIRMED`(주문 확정) 상태로 변경할 수 있다.
- `PENDING` 상태의 주문만 `CONFIRMED`로 변경할 수 있다.

#### Red: 실패하는 테스트 작성
`OrderTest.java`를 새로 만들고 다음을 검증하는 테스트를 작성하세요.
1.  `Order` 생성 시 상태가 `PENDING`인지.
2.  `confirm()` 메서드 호출 시 상태가 `CONFIRMED`로 바뀌는지.
3.  `CONFIRMED` 상태의 주문에 `confirm()`을 다시 호출하면 `IllegalStateException`이 발생하는지.

```java
// OrderTest.java
@Test
@DisplayName("주문 확정 시 상태가 CONFIRMED로 변경된다")
void confirmOrder() {
    // given
    Order order = new Order(LocalDateTime.now(), List.of(new Latte()));

    // when
    order.confirm();

    // then
    assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
}
```

#### Green & Refactor: 기능 구현
`Order` 클래스에 `OrderStatus` enum과 상태 변경 로직을 추가하여 테스트를 통과시키세요.

## Phase 3: 복합 기능 개발 - 할인 정책

더 복잡한 비즈니스 로직을 TDD로 개발해 봅니다.

### Step 3-1: 아침 할인 기능 (Red-Green-Refactor)

**요구사항:**
- 아침 10시부터 11시까지 주문 시 전체 금액의 10%를 할인해준다.

#### Red: 실패하는 테스트 작성
`OrderService` 또는 `DiscountService` 같은 새로운 서비스 클래스를 만들고, 다음을 검증하는 테스트를 작성하세요.
1.  오전 10시 30분에 주문 시 10% 할인이 적용되는지.
2.  오후 12시에 주문 시 할인이 적용되지 않는지.

**힌트:** `TimeProvider`를 사용하여 특정 시간대의 주문을 시뮬레이션하세요.

#### Green & Refactor: 기능 구현
- 할인 로직을 구현하여 테스트를 통과시키세요.
- 할인 정책이 여러 개가 될 것을 대비하여 `DiscountPolicy` 인터페이스를 도입하는 리팩토링을 고려해 보세요. (전략 패턴)

---

이 시나리오를 통해 TDD의 흐름을 익히고, 테스트 가능한 코드를 작성하는 훈련을 할 수 있습니다. 각 단계를 완료한 후에는 `git commit`으로 진행 상황을 저장하는 습관을 들이는 것이 좋습니다.

궁금한 점이 있거나 다음 단계로 나아갈 준비가 되면 언제든지 말씀해 주세요!
