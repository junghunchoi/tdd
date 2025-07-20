# TDD (Test-Driven Development) 가이드

## 목차
1. [TDD란 무엇인가?](#tdd란-무엇인가)
2. [TDD의 장점](#tdd의-장점)
3. [TDD 개발 사이클](#tdd-개발-사이클)
4. [효과적인 테스트 코드 작성법](#효과적인-테스트-코드-작성법)
5. [엣지케이스 발견 및 처리 방법](#엣지케이스-발견-및-처리-방법)
6. [테스트 코드 패턴과 베스트 프랙티스](#테스트-코드-패턴과-베스트-프랙티스)
7. [실전 예제](#실전-예제)

## TDD란 무엇인가?

Test-Driven Development(TDD)는 테스트 코드를 먼저 작성하고, 그 테스트를 통과하는 최소한의 코드를 구현한 후, 리팩토링하는 개발 방법론입니다.

### TDD 3단계 사이클: Red → Green → Refactor
1. **Red**: 실패하는 테스트 코드 작성
2. **Green**: 테스트를 통과하는 최소한의 코드 작성
3. **Refactor**: 코드 개선 및 중복 제거

## TDD의 장점

- **코드 품질 향상**: 테스트로 인한 버그 사전 방지
- **리팩토링 안정성**: 기존 기능 보장 하에 코드 개선 가능
- **문서화 효과**: 테스트 코드가 명세서 역할
- **설계 개선**: 테스트 가능한 코드 구조로 자연스럽게 유도
- **개발 속도 향상**: 디버깅 시간 단축

## TDD 개발 사이클

### 1. Red 단계: 실패하는 테스트 작성
```java
@Test
void 라떼_추가_테스트() {
    // given
    CafeKiosk cafeKiosk = new CafeKiosk();
    Latte latte = new Latte();
    
    // when
    cafeKiosk.add(latte, 1);
    
    // then
    assertEquals(1, cafeKiosk.getBeverageCount());
    assertEquals(4500, cafeKiosk.calculateTotalPrice());
}
```

### 2. Green 단계: 최소한의 구현
```java
public void add(Beverage beverage, int count) {
    for (int i = 0; i < count; i++) {
        beverages.add(beverage);
    }
}
```

### 3. Refactor 단계: 코드 개선
```java
public void add(Beverage beverage, int count) {
    if (count <= 0) {
        throw new IllegalArgumentException("Count must be greater than zero.");
    }
    for (int i = 0; i < count; i++) {
        beverages.add(beverage);
    }
}
```

## 효과적인 테스트 코드 작성법

### 1. Given-When-Then 패턴 활용
```java
@Test
void 음료_수량이_0이하일때_예외발생() {
    // given: 테스트 데이터 준비
    CafeKiosk cafeKiosk = new CafeKiosk();
    Latte latte = new Latte();
    
    // when & then: 실행과 검증
    assertThrows(IllegalArgumentException.class, () -> {
        cafeKiosk.add(latte, 0);
    });
}
```

### 2. 의미있는 테스트 메서드명 작성
- **Bad**: `test1()`, `addTest()`
- **Good**: `음료_수량이_0이하일때_예외발생()`, `영업시간_외_주문시_예외발생()`

### 3. 하나의 테스트는 하나의 관심사만 검증
```java
// Bad: 여러 관심사 혼재
@Test
void addAndCalculateTest() {
    // 추가 기능과 계산 기능을 동시에 테스트
}

// Good: 단일 관심사
@Test
void 음료_추가시_리스트에_저장됨() {
    // 추가 기능만 테스트
}

@Test
void 총_가격_계산_정확성() {
    // 계산 기능만 테스트
}
```

## 엣지케이스 발견 및 처리 방법

### 1. 경계값 분석 (Boundary Value Analysis)
```java
// 정상값
@Test
void 음료_1개_추가() { /* count = 1 */ }

// 경계값
@Test
void 음료_0개_추가시_예외발생() { /* count = 0 */ }

@Test
void 음료_음수개_추가시_예외발생() { /* count = -1 */ }

@Test
void 음료_최대개수_추가() { /* count = Integer.MAX_VALUE */ }
```

### 2. 동등분할 (Equivalence Partitioning)
```java
// 영업시간 테스트
@Test
void 영업시간_전_주문시_예외발생() {
    // 09:59
}

@Test
void 영업시간_중_주문_성공() {
    // 14:00
}

@Test
void 영업시간_후_주문시_예외발생() {
    // 22:01
}
```

### 3. 널 값과 빈 값 처리
```java
@Test
void null_음료_추가시_예외발생() {
    assertThrows(NullPointerException.class, () -> {
        cafeKiosk.add(null, 1);
    });
}

@Test
void 빈_리스트에서_총가격_계산() {
    assertEquals(0, cafeKiosk.calculateTotalPrice());
}
```

### 4. 상태 기반 엣지케이스
```java
@Test
void 이미_제거된_음료_재제거시_예외없음() {
    // 음료를 추가하고 제거한 후 다시 제거
    cafeKiosk.add(latte, 1);
    cafeKiosk.remove(latte);
    
    // 예외가 발생하지 않아야 함
    assertDoesNotThrow(() -> cafeKiosk.remove(latte));
}
```

### 5. 엣지케이스 발견을 위한 체크리스트

#### 입력값 관련
- [ ] null 값
- [ ] 빈 문자열/컬렉션
- [ ] 경계값 (0, 1, -1, MAX_VALUE, MIN_VALUE)
- [ ] 잘못된 형식의 데이터
- [ ] 예상보다 큰/작은 값

#### 상태 관련
- [ ] 초기 상태
- [ ] 중간 상태
- [ ] 종료 상태
- [ ] 비정상 상태

#### 시간 관련
- [ ] 시작 시간
- [ ] 종료 시간
- [ ] 경계 시간
- [ ] 시간대 변경

#### 동시성 관련
- [ ] 여러 스레드 접근
- [ ] 순서 의존성
- [ ] 자원 경합

## 테스트 코드 패턴과 베스트 프랙티스

### 1. 테스트 데이터 빌더 패턴
```java
public class CafeKioskTestDataBuilder {
    public static CafeKiosk.Builder aCafeKiosk() {
        return CafeKiosk.builder()
            .beverages(new ArrayList<>());
    }
    
    public static Latte.Builder aLatte() {
        return Latte.builder()
            .name("라떼")
            .price(4500);
    }
}
```

### 2. 테스트 픽스처 활용
```java
@BeforeEach
void setUp() {
    cafeKiosk = new CafeKiosk();
    latte = new Latte();
    americano = new Americano();
}
```

### 3. 파라미터화 테스트
```java
@ParameterizedTest
@ValueSource(ints = {0, -1, -10})
void 잘못된_수량으로_음료_추가시_예외발생(int invalidCount) {
    assertThrows(IllegalArgumentException.class, () -> {
        cafeKiosk.add(latte, invalidCount);
    });
}
```

### 4. 테스트 더블 활용
```java
@Test
void 시간_의존성_제거_테스트() {
    // given
    LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 14, 0);
    TimeProvider timeProvider = mock(TimeProvider.class);
    when(timeProvider.now()).thenReturn(fixedTime);
    
    CafeKiosk cafeKiosk = new CafeKiosk(timeProvider);
    
    // when & then
    assertDoesNotThrow(() -> cafeKiosk.createOrder());
}
```

## 실전 예제

### 현재 프로젝트의 개선점

현재 `CafeKiosk` 클래스의 테스트를 보면:

```java
// src/test/java/junghun/tdd/unit/CafeKioskTest.java:19
@Test
void add() {
    // given
    CafeKiosk cafeKiosk = new CafeKiosk();
    Latte latte = new Latte();
    
    // when
    cafeKiosk.add(latte, 1);
    
    // then
    // 검증 로직이 누락됨
}
```

### 개선된 테스트 코드 예시
```java
@Test
void 라떼_1개_추가시_리스트에_저장되고_가격이_반영됨() {
    // given
    CafeKiosk cafeKiosk = new CafeKiosk();
    Latte latte = new Latte();
    
    // when
    cafeKiosk.add(latte, 1);
    
    // then
    assertEquals(1, cafeKiosk.getBeverageCount());
    assertEquals(4500, cafeKiosk.calculateTotalPrice());
    assertTrue(cafeKiosk.getBeverages().contains(latte));
}

@Test
void 영업시간_경계값_테스트() {
    // given
    CafeKiosk cafeKiosk = new CafeKiosk();
    
    // 영업 시작 시간 (10:00)
    try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
        LocalDateTime openTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        mockedTime.when(LocalDateTime::now).thenReturn(openTime);
        
        // when & then
        assertDoesNotThrow(() -> cafeKiosk.createOrder());
    }
    
    // 영업 종료 시간 (22:00)
    try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
        LocalDateTime closeTime = LocalDateTime.of(2023, 1, 1, 22, 0);
        mockedTime.when(LocalDateTime::now).thenReturn(closeTime);
        
        // when & then
        assertDoesNotThrow(() -> cafeKiosk.createOrder());
    }
    
    // 영업 시간 외 (22:01)
    try (MockedStatic<LocalDateTime> mockedTime = mockStatic(LocalDateTime.class)) {
        LocalDateTime afterCloseTime = LocalDateTime.of(2023, 1, 1, 22, 1);
        mockedTime.when(LocalDateTime::now).thenReturn(afterCloseTime);
        
        // when & then
        assertThrows(IllegalStateException.class, () -> cafeKiosk.createOrder());
    }
}
```

### 추가로 필요한 테스트 케이스들
```java
@Test
void 음료_제거_테스트() {
    // 정상 제거, 없는 음료 제거 등
}

@Test
void 리스트_초기화_테스트() {
    // clear 기능 검증
}

@Test
void 여러_종류_음료_추가_총가격_계산() {
    // 복합 시나리오 테스트
}

@Test
void 동일_음료_여러개_추가_테스트() {
    // 수량 기반 추가 검증
}
```

## 마무리

TDD는 단순히 테스트 코드를 작성하는 것이 아니라, **설계와 구현을 동시에 개선하는 개발 방법론**입니다. 

핵심은:
1. **작은 단위로 시작**하여 점진적으로 기능을 완성
2. **테스트를 통해 요구사항을 명확히** 하고 검증
3. **엣지케이스를 체계적으로 발견**하고 처리
4. **지속적인 리팩토링**으로 코드 품질 향상

TDD를 통해 더 안정적이고 유지보수하기 쉬운 코드를 작성할 수 있습니다.