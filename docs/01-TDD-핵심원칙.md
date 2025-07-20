# TDD 핵심 원칙과 이론

## 목차
1. [TDD의 정의와 본질](#tdd의-정의와-본질)
2. [TDD의 핵심 원칙](#tdd의-핵심-원칙)
3. [Red-Green-Refactor 사이클 심화](#red-green-refactor-사이클-심화)
4. [TDD vs 전통적 개발 방식](#tdd-vs-전통적-개발-방식)
5. [TDD의 철학적 배경](#tdd의-철학적-배경)
6. [TDD가 해결하는 근본적 문제들](#tdd가-해결하는-근본적-문제들)

## TDD의 정의와 본질

### TDD란 무엇인가?
Test-Driven Development는 **테스트가 개발을 주도하는** 개발 방법론입니다. 단순히 테스트를 먼저 작성하는 것이 아니라, **테스트를 통해 설계와 구현을 동시에 진행**하는 접근법입니다.

### TDD의 핵심 명제
1. **테스트는 명세(Specification)다**: 테스트 코드가 곧 요구사항의 실행 가능한 명세서
2. **설계는 진화한다**: 테스트를 통과하는 과정에서 자연스럽게 좋은 설계가 도출
3. **피드백은 즉각적이어야 한다**: 빠른 피드백 루프를 통한 지속적 검증

### TDD의 본질적 목표
- **Working Software**: 동작하는 소프트웨어
- **Clean Code**: 깨끗하고 유지보수 가능한 코드
- **Living Documentation**: 살아있는 문서로서의 테스트

## TDD의 핵심 원칙

### 1. 실패하는 테스트 없이는 제품 코드를 작성하지 않는다
```java
// ❌ 잘못된 접근: 구현부터 시작
public class Calculator {
    public int add(int a, int b) {
        return a + b;  // 테스트 없이 구현
    }
}

// ✅ 올바른 접근: 테스트부터 시작
@Test
void 두_숫자를_더하면_합이_반환된다() {
    // given
    Calculator calculator = new Calculator();
    
    // when
    int result = calculator.add(2, 3);
    
    // then
    assertEquals(5, result);
}
// 이 시점에서 Calculator 클래스는 존재하지 않음 (컴파일 에러)
```

### 2. 컴파일은 실패하지 않으면서 실행이 실패하는 정도로만 테스트를 작성한다
```java
// ❌ 너무 많은 것을 한 번에 테스트
@Test
void 계산기_모든_기능_테스트() {
    Calculator calc = new Calculator();
    assertEquals(5, calc.add(2, 3));
    assertEquals(1, calc.subtract(3, 2));
    assertEquals(6, calc.multiply(2, 3));
    assertEquals(2, calc.divide(6, 3));
    // 한 번에 너무 많은 기능을 요구
}

// ✅ 최소한의 실패하는 테스트
@Test
void 덧셈_기능_테스트() {
    Calculator calc = new Calculator();
    assertEquals(5, calc.add(2, 3));
    // 오직 add 메서드만 필요하도록 제한
}
```

### 3. 현재 실패하는 테스트를 통과할 정도로만 제품 코드를 작성한다
```java
// ❌ 과도한 구현
public int add(int a, int b) {
    // 일반화된 구현을 바로 작성
    return a + b;
}

// ✅ 최소한의 구현 (Fake it till you make it)
public int add(int a, int b) {
    return 5;  // 테스트를 통과하는 최소한의 구현
}

// 이후 테스트가 추가되면서 점진적으로 일반화
@Test
void 다른_숫자_덧셈_테스트() {
    assertEquals(7, calculator.add(3, 4));
    // 이제 하드코딩으로는 통과할 수 없음
}
```

## Red-Green-Refactor 사이클 심화

### Red 단계: 실패하는 테스트 작성

#### Red 단계의 목표
- **명확한 요구사항 정의**: 무엇을 만들 것인지 구체화
- **API 설계**: 클라이언트 관점에서 인터페이스 설계
- **실패 확인**: 테스트가 올바른 이유로 실패하는지 검증

#### Red 단계 체크리스트
- [ ] 테스트가 컴파일되는가?
- [ ] 테스트가 의도한 이유로 실패하는가?
- [ ] 테스트 메서드명이 의도를 명확히 표현하는가?
- [ ] 테스트가 하나의 관심사만 다루는가?

```java
@Test
void 주문_생성시_현재시간이_기록된다() {
    // given
    LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 10, 0);
    // 시간 의존성을 어떻게 처리할지 고민하게 됨
    
    // when
    Order order = orderService.createOrder(items);
    
    // then
    assertEquals(fixedTime, order.getCreatedAt());
    // 이 테스트를 통과하려면 시간 의존성 주입이 필요함을 깨닫게 됨
}
```

### Green 단계: 테스트를 통과하는 최소한의 코드

#### Green 단계의 목표
- **빠른 피드백**: 가능한 한 빨리 테스트를 통과
- **단순함 우선**: 복잡한 구현보다는 단순한 해결책
- **동작하는 코드**: 완벽하지 않아도 동작하는 코드

#### Green 단계의 전략들

**1. Fake It (가짜 구현)**
```java
public int calculateDiscount(int price, String customerType) {
    return 100;  // 특정 테스트만 통과하는 하드코딩
}
```

**2. Obvious Implementation (명백한 구현)**
```java
public int add(int a, int b) {
    return a + b;  // 구현이 명백한 경우
}
```

**3. Triangulation (삼각측량)**
```java
// 첫 번째 테스트
@Test void test1() { assertEquals(100, discount(1000, "VIP")); }

// 두 번째 테스트 추가로 일반화 유도
@Test void test2() { assertEquals(50, discount(500, "VIP")); }

// 이제 하드코딩으로는 불가능, 일반화된 로직 필요
public int discount(int price, String customerType) {
    if ("VIP".equals(customerType)) {
        return price / 10;
    }
    return 0;
}
```

### Refactor 단계: 코드 개선

#### Refactor 단계의 목표
- **중복 제거**: DRY 원칙 적용
- **가독성 향상**: 의도를 명확히 표현
- **설계 개선**: 더 나은 구조로 발전

#### 리팩토링 시 주의사항
- **테스트는 항상 통과해야 함**: 리팩토링 중 기능이 깨지면 안됨
- **작은 단위로**: 한 번에 하나의 개선사항만
- **테스트 먼저**: 테스트 코드도 리팩토링 대상

```java
// 리팩토링 전
@Test
void 주문_총액_계산() {
    Order order = new Order();
    order.addItem(new Item("라떼", 4500));
    order.addItem(new Item("아메리카노", 4000));
    
    assertEquals(8500, order.getTotalPrice());
}

// 리팩토링 후 (테스트 헬퍼 메서드 도입)
@Test
void 주문_총액_계산() {
    Order order = orderWith(라떼(4500), 아메리카노(4000));
    
    assertEquals(8500, order.getTotalPrice());
}

private Order orderWith(Item... items) {
    Order order = new Order();
    Arrays.stream(items).forEach(order::addItem);
    return order;
}
```

## TDD vs 전통적 개발 방식

### 전통적 개발 방식의 문제점

#### 1. 늦은 피드백
```
요구사항 분석 → 설계 → 구현 → 테스트 → 버그 발견 → 수정
(몇 주 후에 문제 발견)
```

#### 2. 복합적 변경
- 여러 기능을 동시에 구현하다가 문제 발생 시 원인 파악 어려움
- 테스트 작성 시 이미 구현된 코드에 맞춰 테스트를 작성하게 됨

#### 3. 테스트 커버리지 문제
```java
// 구현 후 테스트 작성 시 발생하는 문제
public class UserService {
    public User createUser(String name, String email) {
        if (name == null) throw new IllegalArgumentException("Name required");
        if (email == null) throw new IllegalArgumentException("Email required");
        if (!email.contains("@")) throw new IllegalArgumentException("Invalid email");
        
        // 이 분기는 테스트하기 어려움
        if (isWeekend()) {
            sendWelcomeEmailLater(email);
        } else {
            sendWelcomeEmailNow(email);
        }
        
        return new User(name, email);
    }
}
```

### TDD 방식의 장점

#### 1. 즉각적 피드백
```
테스트 작성 → 구현 → 피드백 → 리팩토링
(몇 분 내에 피드백)
```

#### 2. 단일 변경
- 한 번에 하나의 테스트만 통과시키므로 문제 발생 시 원인이 명확
- 각 단계에서 모든 테스트가 통과하므로 회귀 버그 방지

#### 3. 자연스러운 100% 커버리지
```java
// TDD로 작성된 코드는 자연스럽게 테스트 가능한 구조
public class UserService {
    private final EmailService emailService;
    private final TimeProvider timeProvider;
    
    // 의존성 주입으로 테스트 가능한 구조
    public User createUser(String name, String email) {
        validateInput(name, email);
        
        User user = new User(name, email);
        scheduleWelcomeEmail(user);
        
        return user;
    }
    
    // 각 메서드가 독립적으로 테스트 가능
    private void validateInput(String name, String email) { /* ... */ }
    private void scheduleWelcomeEmail(User user) { /* ... */ }
}
```

## TDD의 철학적 배경

### 1. 진화적 설계 (Evolutionary Design)
TDD는 **Big Design Up Front**를 거부하고, 테스트를 통해 설계가 점진적으로 진화한다고 봅니다.

```java
// 진화 과정 예시
// 1단계: 단순한 구현
public class PriceCalculator {
    public int calculate(int price) {
        return price;  // 할인 없음
    }
}

// 2단계: 할인 로직 추가
public class PriceCalculator {
    public int calculate(int price, boolean isVip) {
        return isVip ? price * 9 / 10 : price;
    }
}

// 3단계: 전략 패턴으로 진화
public class PriceCalculator {
    private final DiscountStrategy discountStrategy;
    
    public int calculate(int price) {
        return discountStrategy.applyDiscount(price);
    }
}
```

### 2. 명세로서의 테스트 (Tests as Specification)
테스트는 단순한 검증 도구가 아니라 **살아있는 명세서**입니다.

```java
public class OrderSpecificationTest {
    
    @Test
    void 주문은_최소_1개_이상의_상품을_포함해야_한다() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order(Collections.emptyList());
        });
    }
    
    @Test
    void 주문_총액은_모든_상품_가격의_합이다() {
        Order order = new Order(List.of(
            new Item("A", 1000),
            new Item("B", 2000)
        ));
        
        assertEquals(3000, order.getTotalAmount());
    }
    
    @Test
    void 취소된_주문은_수정할_수_없다() {
        Order order = new Order(List.of(new Item("A", 1000)));
        order.cancel();
        
        assertThrows(IllegalStateException.class, () -> {
            order.addItem(new Item("B", 2000));
        });
    }
}
```

### 3. 신뢰할 수 있는 변경 (Confident Change)
테스트가 있기 때문에 언제든지 안전하게 리팩토링할 수 있습니다.

```java
// 리팩토링 전: 절차적 코드
public class OrderService {
    public void processOrder(Order order) {
        // 재고 확인
        for (Item item : order.getItems()) {
            if (inventory.getStock(item.getId()) < item.getQuantity()) {
                throw new OutOfStockException();
            }
        }
        
        // 재고 차감
        for (Item item : order.getItems()) {
            inventory.reduce(item.getId(), item.getQuantity());
        }
        
        // 결제 처리
        paymentService.charge(order.getTotalAmount());
        
        // 알림 발송
        notificationService.send(order.getCustomer().getEmail());
    }
}

// 리팩토링 후: 객체지향적 설계
public class OrderService {
    public void processOrder(Order order) {
        order.validateStock(inventory);
        order.reserveItems(inventory);
        order.processPayment(paymentService);
        order.sendNotification(notificationService);
    }
}
```

## TDD가 해결하는 근본적 문제들

### 1. 요구사항의 모호성
```java
// 모호한 요구사항: "사용자 등급에 따라 할인을 적용한다"

// TDD를 통한 명확한 명세
@Test
void VIP_고객은_10퍼센트_할인을_받는다() {
    Customer vip = new Customer("VIP");
    int discountedPrice = priceCalculator.calculate(10000, vip);
    assertEquals(9000, discountedPrice);
}

@Test
void 일반_고객은_할인을_받지_않는다() {
    Customer regular = new Customer("REGULAR");
    int price = priceCalculator.calculate(10000, regular);
    assertEquals(10000, price);
}

@Test
void GOLD_고객은_5퍼센트_할인을_받는다() {
    Customer gold = new Customer("GOLD");
    int discountedPrice = priceCalculator.calculate(10000, gold);
    assertEquals(9500, discountedPrice);
}
```

### 2. 과도한 설계 (Over-engineering)
TDD는 **YAGNI(You Aren't Gonna Need It)** 원칙을 자연스럽게 적용합니다.

```java
// ❌ 과도한 설계 (TDD 없이)
public abstract class AbstractDiscountCalculatorFactory {
    public abstract DiscountCalculator createCalculator(
        DiscountType type, 
        CustomerSegment segment,
        SeasonalRule rule,
        RegionalPolicy policy
    );
}

// ✅ TDD를 통한 점진적 설계
// 첫 번째 테스트
@Test
void VIP_고객_할인_계산() {
    DiscountCalculator calculator = new DiscountCalculator();
    assertEquals(900, calculator.calculate(1000, "VIP"));
}

// 단순한 구현부터 시작
public class DiscountCalculator {
    public int calculate(int price, String customerType) {
        if ("VIP".equals(customerType)) {
            return (int) (price * 0.9);
        }
        return price;
    }
}
```

### 3. 회귀 버그 (Regression Bug)
```java
// 기존 기능을 보호하는 테스트들
public class OrderTest {
    @Test
    void 주문_생성_기본_기능() {
        // 기본 기능 보호
    }
    
    @Test
    void 주문_취소_기능() {
        // 취소 기능 보호
    }
    
    @Test
    void 주문_상태_변경_추적() {
        // 상태 관리 보호
    }
}

// 새로운 기능 추가 시에도 기존 테스트들이 모두 통과해야 함
@Test
void 새로운_기능_추가() {
    // 새 기능 테스트
    // 기존 모든 테스트가 여전히 통과하는지 확인
}
```

### 4. 디버깅 시간 단축
```java
// TDD로 작성된 코드는 문제 발생 시 원인을 빠르게 파악 가능
@Test
void 할인_계산_로직_검증() {
    // given
    DiscountPolicy policy = new VipDiscountPolicy();
    
    // when
    int result = policy.calculateDiscount(10000);
    
    // then
    assertEquals(1000, result);
    // 이 테스트가 실패하면 할인 계산 로직에 문제가 있음을 즉시 알 수 있음
}
```

## 마무리

TDD는 단순한 개발 기법이 아니라 **소프트웨어 개발에 대한 철학적 접근**입니다. 

핵심은:
- **테스트가 설계를 이끈다**
- **작은 단위의 빠른 피드백**
- **진화적 설계를 통한 유연성**
- **신뢰할 수 있는 변경**

TDD를 마스터하기 위해서는 단순히 기술적 측면뿐만 아니라 이러한 철학적 배경을 이해하는 것이 중요합니다.