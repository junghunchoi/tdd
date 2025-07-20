# AssertJ 및 검증 라이브러리 완벽 가이드

## 목차
1. [AssertJ 개요](#assertj-개요)
2. [AssertJ vs JUnit Assertions](#assertj-vs-junit-assertions)
3. [기본 Assertions](#기본-assertions)
4. [컬렉션 Assertions](#컬렉션-assertions)
5. [객체 Assertions](#객체-assertions)
6. [예외 Assertions](#예외-assertions)
7. [커스텀 Assertions](#커스텀-assertions)
8. [Soft Assertions](#soft-assertions)
9. [조건부 Assertions](#조건부-assertions)
10. [실전 활용 패턴](#실전-활용-패턴)

## AssertJ 개요

### AssertJ란?
AssertJ는 Java용 유창한(fluent) assertion 라이브러리입니다. 메서드 체이닝을 통해 더 읽기 쉽고 표현력 있는 테스트 코드를 작성할 수 있게 해줍니다.

### 주요 특징
- **메서드 체이닝**: 자연스러운 문장처럼 읽히는 코드
- **풍부한 API**: 다양한 타입과 상황에 최적화된 assertion 메서드
- **명확한 에러 메시지**: 실패 시 상세하고 이해하기 쉬운 메시지
- **IDE 지원**: 자동완성과 타입 안전성

### 의존성 설정
```gradle
dependencies {
    // Spring Boot Starter Test에 포함됨
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    
    // 또는 직접 추가
    testImplementation 'org.assertj:assertj-core:3.24.2'
}
```

### 기본 사용법
```java
import static org.assertj.core.api.Assertions.*;

@Test
void assertj_기본_사용법() {
    // 정적 임포트로 간결하게 사용
    assertThat(5).isEqualTo(5);
    assertThat("Hello").startsWith("He").endsWith("lo");
    assertThat(Arrays.asList(1, 2, 3)).hasSize(3).contains(2);
}
```

## AssertJ vs JUnit Assertions

### 가독성 비교
```java
// JUnit 방식
@Test
void junit_assertions() {
    List<String> fruits = Arrays.asList("apple", "banana", "orange");
    
    assertEquals(3, fruits.size());
    assertTrue(fruits.contains("apple"));
    assertFalse(fruits.contains("grape"));
    assertEquals("apple", fruits.get(0));
}

// AssertJ 방식
@Test
void assertj_assertions() {
    List<String> fruits = Arrays.asList("apple", "banana", "orange");
    
    assertThat(fruits)
        .hasSize(3)
        .contains("apple")
        .doesNotContain("grape")
        .startsWith("apple");
}
```

### 에러 메시지 비교
```java
@Test
void 에러_메시지_비교() {
    List<String> actual = Arrays.asList("apple", "banana");
    List<String> expected = Arrays.asList("apple", "orange");
    
    // JUnit - 간단한 메시지
    // assertEquals(expected, actual);
    // Expected :[apple, orange] but was:[apple, banana]
    
    // AssertJ - 상세한 메시지
    assertThat(actual).isEqualTo(expected);
    // Expecting:
    //   <["apple", "banana"]>
    // to be equal to:
    //   <["apple", "orange"]>
    // but was not.
}
```

### 체이닝의 장점
```java
@Test
void 체이닝_장점() {
    String text = "Hello World";
    
    // JUnit - 여러 줄의 독립적인 검증
    assertNotNull(text);
    assertTrue(text.length() > 5);
    assertTrue(text.startsWith("Hello"));
    assertTrue(text.endsWith("World"));
    
    // AssertJ - 하나의 체인으로 연결된 검증
    assertThat(text)
        .isNotNull()
        .hasSizeGreaterThan(5)
        .startsWith("Hello")
        .endsWith("World");
}
```

## 기본 Assertions

### 기본 타입 검증
```java
class BasicAssertionsTest {
    
    @Test
    void 숫자_검증() {
        int number = 42;
        
        assertThat(number)
            .isEqualTo(42)
            .isNotEqualTo(0)
            .isPositive()
            .isGreaterThan(40)
            .isLessThan(50)
            .isBetween(40, 45);
    }
    
    @Test
    void 부동소수점_검증() {
        double pi = 3.14159;
        
        assertThat(pi)
            .isEqualTo(3.14159)
            .isCloseTo(3.14, within(0.01))  // 근사값 비교
            .isGreaterThan(3.0)
            .isLessThan(4.0);
    }
    
    @Test
    void 불린_검증() {
        boolean isTrue = true;
        boolean isFalse = false;
        
        assertThat(isTrue).isTrue();
        assertThat(isFalse).isFalse();
    }
    
    @Test
    void 문자_검증() {
        char character = 'A';
        
        assertThat(character)
            .isEqualTo('A')
            .isUpperCase()
            .isNotEqualTo('a');
    }
}
```

### 문자열 검증
```java
class StringAssertionsTest {
    
    @Test
    void 기본_문자열_검증() {
        String text = "Hello World";
        
        assertThat(text)
            .isNotNull()
            .isNotEmpty()
            .isNotBlank()
            .hasSize(11)
            .isEqualTo("Hello World")
            .isEqualToIgnoringCase("hello world");
    }
    
    @Test
    void 문자열_패턴_검증() {
        String email = "user@example.com";
        
        assertThat(email)
            .startsWith("user")
            .endsWith(".com")
            .contains("@")
            .containsIgnoringCase("EXAMPLE")
            .matches("\\w+@\\w+\\.\\w+");  // 정규식
    }
    
    @Test
    void 문자열_부분_검증() {
        String text = "The quick brown fox";
        
        assertThat(text)
            .containsSequence("quick", "brown")  // 순서대로 포함
            .containsSubsequence("The", "fox")   // 순서대로 포함 (사이에 다른 문자 허용)
            .doesNotContain("cat")
            .containsOnlyOnce("quick");
    }
    
    @Test
    void 빈_문자열_검증() {
        String empty = "";
        String blank = "   ";
        String nullString = null;
        
        assertThat(empty).isEmpty().isNotNull();
        assertThat(blank).isBlank().isNotEmpty();
        assertThat(nullString).isNull();
    }
}
```

### 날짜와 시간 검증
```java
class DateTimeAssertionsTest {
    
    @Test
    void LocalDateTime_검증() {
        LocalDateTime now = LocalDateTime.of(2023, 12, 25, 15, 30, 45);
        LocalDateTime future = now.plusDays(1);
        
        assertThat(now)
            .isEqualTo("2023-12-25T15:30:45")
            .isBefore(future)
            .isAfter(now.minusMinutes(1))
            .isInSameYearAs(future)
            .isInSameMonthAs(future);
    }
    
    @Test
    void LocalDate_검증() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        
        assertThat(date)
            .isEqualTo("2023-12-25")
            .isAfter(LocalDate.of(2023, 12, 24))
            .isBefore(LocalDate.of(2023, 12, 26))
            .hasYear(2023)
            .hasMonthValue(12)
            .hasDayOfMonth(25);
    }
    
    @Test
    void Duration_검증() {
        Duration duration = Duration.ofMinutes(30);
        
        assertThat(duration)
            .isEqualTo(Duration.ofSeconds(1800))
            .isPositive()
            .hasSeconds(1800);
    }
}
```

## 컬렉션 Assertions

### List 검증
```java
class ListAssertionsTest {
    
    @Test
    void 기본_리스트_검증() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange");
        
        assertThat(fruits)
            .isNotNull()
            .isNotEmpty()
            .hasSize(3)
            .contains("apple", "banana")
            .containsOnly("apple", "banana", "orange")
            .containsExactly("apple", "banana", "orange")  // 순서까지 정확히
            .doesNotContain("grape");
    }
    
    @Test
    void 리스트_순서_검증() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        assertThat(numbers)
            .startsWith(1, 2)
            .endsWith(4, 5)
            .containsSequence(2, 3, 4)
            .isSorted()  // 정렬되어 있는지
            .doesNotHaveDuplicates();
    }
    
    @Test
    void 리스트_조건_검증() {
        List<Integer> numbers = Arrays.asList(2, 4, 6, 8, 10);
        
        assertThat(numbers)
            .allMatch(n -> n % 2 == 0, "모든 수가 짝수")  // 모든 요소가 조건을 만족
            .anyMatch(n -> n > 5, "5보다 큰 수가 존재")    // 하나 이상이 조건을 만족
            .noneMatch(n -> n < 0, "음수가 없음");        // 모든 요소가 조건을 만족하지 않음
    }
    
    @Test
    void 리스트_변환_검증() {
        List<Person> people = Arrays.asList(
            new Person("John", 25),
            new Person("Jane", 30),
            new Person("Bob", 35)
        );
        
        assertThat(people)
            .extracting(Person::getName)  // 이름만 추출하여 검증
            .containsExactly("John", "Jane", "Bob");
        
        assertThat(people)
            .extracting("name", "age")    // 여러 필드 추출
            .containsExactly(
                tuple("John", 25),
                tuple("Jane", 30),
                tuple("Bob", 35)
            );
    }
}
```

### Set과 Map 검증
```java
class SetMapAssertionsTest {
    
    @Test
    void Set_검증() {
        Set<String> colors = Set.of("red", "green", "blue");
        
        assertThat(colors)
            .hasSize(3)
            .contains("red", "blue")
            .containsOnly("red", "green", "blue")
            .doesNotContain("yellow");
    }
    
    @Test
    void Map_검증() {
        Map<String, Integer> ages = Map.of(
            "John", 25,
            "Jane", 30,
            "Bob", 35
        );
        
        assertThat(ages)
            .hasSize(3)
            .containsKey("John")
            .containsValue(25)
            .containsEntry("Jane", 30)
            .doesNotContainKey("Alice")
            .doesNotContainValue(40);
    }
    
    @Test
    void Map_상세_검증() {
        Map<String, Person> personMap = Map.of(
            "person1", new Person("John", 25),
            "person2", new Person("Jane", 30)
        );
        
        assertThat(personMap)
            .extractingByKey("person1")  // 특정 키의 값 추출
            .extracting(Person::getName)
            .isEqualTo("John");
        
        assertThat(personMap)
            .extractingByKeys("person1", "person2")  // 여러 키의 값 추출
            .extracting(Person::getAge)
            .containsExactly(25, 30);
    }
}
```

### 배열 검증
```java
class ArrayAssertionsTest {
    
    @Test
    void 배열_기본_검증() {
        int[] numbers = {1, 2, 3, 4, 5};
        
        assertThat(numbers)
            .hasSize(5)
            .contains(3)
            .containsExactly(1, 2, 3, 4, 5)
            .startsWith(1, 2)
            .endsWith(4, 5)
            .isSorted();
    }
    
    @Test
    void 문자열_배열_검증() {
        String[] fruits = {"apple", "banana", "orange"};
        
        assertThat(fruits)
            .hasSize(3)
            .contains("apple")
            .doesNotContain("grape")
            .containsOnly("apple", "banana", "orange");
    }
    
    @Test
    void 2차원_배열_검증() {
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6}
        };
        
        assertThat(matrix)
            .hasSize(2)  // 행의 개수
            .satisfies(m -> {
                assertThat(m[0]).containsExactly(1, 2, 3);
                assertThat(m[1]).containsExactly(4, 5, 6);
            });
    }
}
```

## 객체 Assertions

### 기본 객체 검증
```java
class ObjectAssertionsTest {
    
    @Test
    void 객체_기본_검증() {
        Person person = new Person("John", 25);
        Person samePerson = new Person("John", 25);
        Person differentPerson = new Person("Jane", 30);
        
        assertThat(person)
            .isNotNull()
            .isEqualTo(samePerson)  // equals() 메서드 사용
            .isNotSameAs(samePerson)  // 참조 비교
            .isNotEqualTo(differentPerson);
    }
    
    @Test
    void 객체_필드_검증() {
        Person person = new Person("John", 25);
        
        // 필드별 개별 검증
        assertThat(person.getName()).isEqualTo("John");
        assertThat(person.getAge()).isEqualTo(25);
        
        // 한 번에 여러 필드 검증
        assertThat(person)
            .extracting(Person::getName, Person::getAge)
            .containsExactly("John", 25);
    }
    
    @Test
    void 객체_프로퍼티_검증() {
        Person person = new Person("John", 25);
        
        assertThat(person)
            .extracting("name")  // 프로퍼티명으로 접근
            .isEqualTo("John");
        
        assertThat(person)
            .hasFieldOrProperty("name")  // 필드나 프로퍼티 존재 확인
            .hasFieldOrPropertyWithValue("name", "John")  // 값까지 확인
            .hasFieldOrPropertyWithValue("age", 25);
    }
}
```

### 복잡한 객체 검증
```java
class ComplexObjectAssertionsTest {
    
    @Test
    void 중첩_객체_검증() {
        Address address = new Address("Seoul", "12345", "Korea");
        Person person = new Person("John", 25, address);
        
        assertThat(person)
            .extracting(Person::getAddress)
            .extracting(Address::getCity, Address::getZipCode, Address::getCountry)
            .containsExactly("Seoul", "12345", "Korea");
        
        // 또는 중첩된 방식으로
        assertThat(person)
            .extracting("address.city")  // 중첩 프로퍼티 접근
            .isEqualTo("Seoul");
    }
    
    @Test
    void 객체_상태_검증() {
        Order order = new Order();
        
        assertThat(order)
            .matches(o -> o.getStatus() == OrderStatus.PENDING, "주문 상태가 PENDING")
            .satisfies(o -> {
                assertThat(o.getItems()).isEmpty();
                assertThat(o.getTotalAmount()).isZero();
            });
    }
    
    @Test
    void 타입_검증() {
        Object obj = "Hello World";
        
        assertThat(obj)
            .isInstanceOf(String.class)
            .isNotInstanceOf(Integer.class)
            .isExactlyInstanceOf(String.class);  // 정확한 타입
    }
    
    @Test
    void 빌더_패턴_객체_검증() {
        User user = User.builder()
            .name("John")
            .email("john@example.com")
            .age(25)
            .active(true)
            .build();
        
        assertThat(user)
            .hasFieldOrPropertyWithValue("name", "John")
            .hasFieldOrPropertyWithValue("email", "john@example.com")
            .hasFieldOrPropertyWithValue("age", 25)
            .hasFieldOrPropertyWithValue("active", true);
    }
}
```

### 객체 컬렉션 검증
```java
class ObjectCollectionAssertionsTest {
    
    @Test
    void 객체_리스트_검증() {
        List<Person> people = Arrays.asList(
            new Person("John", 25),
            new Person("Jane", 30),
            new Person("Bob", 35)
        );
        
        assertThat(people)
            .hasSize(3)
            .extracting(Person::getName)
            .containsExactly("John", "Jane", "Bob");
        
        assertThat(people)
            .extracting(Person::getAge)
            .allMatch(age -> age > 20, "모든 사람이 20세 이상");
    }
    
    @Test
    void 특정_조건의_객체_검증() {
        List<Person> people = Arrays.asList(
            new Person("John", 25),
            new Person("Jane", 17),
            new Person("Bob", 35)
        );
        
        assertThat(people)
            .filteredOn(p -> p.getAge() >= 18)  // 성인만 필터링
            .hasSize(2)
            .extracting(Person::getName)
            .containsExactly("John", "Bob");
        
        assertThat(people)
            .filteredOn("age", 25)  // 특정 나이만 필터링
            .hasSize(1)
            .extracting(Person::getName)
            .containsExactly("John");
    }
    
    @Test
    void 그룹핑_검증() {
        List<Person> people = Arrays.asList(
            new Person("John", 25, "Engineering"),
            new Person("Jane", 30, "Marketing"),
            new Person("Bob", 35, "Engineering")
        );
        
        assertThat(people)
            .extracting(Person::getDepartment)
            .containsExactly("Engineering", "Marketing", "Engineering");
        
        // 부서별 그룹핑 검증
        Map<String, List<Person>> byDepartment = people.stream()
            .collect(Collectors.groupingBy(Person::getDepartment));
        
        assertThat(byDepartment)
            .containsKey("Engineering")
            .containsKey("Marketing");
        
        assertThat(byDepartment.get("Engineering"))
            .hasSize(2)
            .extracting(Person::getName)
            .containsExactly("John", "Bob");
    }
}
```

## 예외 Assertions

### 기본 예외 검증
```java
class ExceptionAssertionsTest {
    
    @Test
    void 예외_타입_검증() {
        Calculator calculator = new Calculator();
        
        // 예외 발생 검증
        assertThatThrownBy(() -> calculator.divide(10, 0))
            .isInstanceOf(ArithmeticException.class);
        
        // 정확한 예외 타입 검증
        assertThatThrownBy(() -> calculator.divide(10, 0))
            .isExactlyInstanceOf(ArithmeticException.class);
    }
    
    @Test
    void 예외_메시지_검증() {
        UserService userService = new UserService();
        
        assertThatThrownBy(() -> userService.createUser(null, "email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이름은 필수입니다")
            .hasMessageContaining("이름")
            .hasMessageStartingWith("이름은")
            .hasMessageEndingWith("필수입니다");
    }
    
    @Test
    void 예외_원인_검증() {
        DatabaseService dbService = new DatabaseService();
        
        assertThatThrownBy(() -> dbService.connect())
            .isInstanceOf(ServiceException.class)
            .hasCause(SQLException.class)  // 원인 예외 타입
            .hasRootCauseInstanceOf(ConnectionException.class);  // 근본 원인
    }
    
    @Test
    void 여러_예외_시나리오() {
        ValidationService service = new ValidationService();
        
        // 여러 예외 상황을 한 번에 검증
        assertThatThrownBy(() -> service.validate(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("null");
        
        assertThatThrownBy(() -> service.validate(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("empty");
        
        assertThatThrownBy(() -> service.validate("a"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("length");
    }
}
```

### 특정 예외 타입 검증
```java
class SpecificExceptionAssertionsTest {
    
    @Test
    void IllegalArgumentException_검증() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Person(null, 25))
            .withMessage("이름은 필수입니다");
    }
    
    @Test
    void NullPointerException_검증() {
        assertThatNullPointerException()
            .isThrownBy(() -> {
                String str = null;
                str.length();
            })
            .withMessageContaining("null");
    }
    
    @Test
    void IOException_검증() {
        FileService fileService = new FileService();
        
        assertThatExceptionOfType(IOException.class)
            .isThrownBy(() -> fileService.readFile("nonexistent.txt"))
            .withMessageContaining("파일을 찾을 수 없습니다");
    }
    
    @Test
    void 예외가_발생하지_않음_검증() {
        Calculator calculator = new Calculator();
        
        assertThatNoException()
            .isThrownBy(() -> calculator.divide(10, 2));
    }
}
```

### 복합 예외 시나리오
```java
class ComplexExceptionScenariosTest {
    
    @Test
    void 예외_체인_검증() {
        ServiceLayer service = new ServiceLayer();
        
        assertThatThrownBy(() -> service.processData("invalid"))
            .isInstanceOf(ProcessingException.class)
            .hasMessage("데이터 처리 실패")
            .hasCauseInstanceOf(ValidationException.class)
            .hasRootCauseMessage("잘못된 형식");
    }
    
    @Test
    void 조건부_예외_검증() {
        PaymentService paymentService = new PaymentService();
        
        // 금액에 따른 다른 예외
        assertThatThrownBy(() -> paymentService.processPayment(-100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("음수");
        
        assertThatThrownBy(() -> paymentService.processPayment(1000000))
            .isInstanceOf(PaymentLimitExceededException.class)
            .hasMessageContaining("한도 초과");
    }
    
    @Test
    void 예외_필드_검증() {
        CustomException exception = assertThrows(CustomException.class, () -> {
            throw new CustomException("에러", 500, "INTERNAL_ERROR");
        });
        
        assertThat(exception)
            .hasMessage("에러")
            .extracting("errorCode", "errorType")
            .containsExactly(500, "INTERNAL_ERROR");
    }
}
```

## 커스텀 Assertions

### 기본 커스텀 Assertion
```java
// Person용 커스텀 Assertion 클래스
public class PersonAssert extends AbstractAssert<PersonAssert, Person> {
    
    public PersonAssert(Person person) {
        super(person, PersonAssert.class);
    }
    
    public static PersonAssert assertThat(Person person) {
        return new PersonAssert(person);
    }
    
    public PersonAssert hasName(String expectedName) {
        isNotNull();
        
        String actualName = actual.getName();
        if (!Objects.equals(actualName, expectedName)) {
            failWithMessage("Expected person's name to be <%s> but was <%s>", 
                expectedName, actualName);
        }
        
        return this;
    }
    
    public PersonAssert hasAge(int expectedAge) {
        isNotNull();
        
        int actualAge = actual.getAge();
        if (actualAge != expectedAge) {
            failWithMessage("Expected person's age to be <%d> but was <%d>", 
                expectedAge, actualAge);
        }
        
        return this;
    }
    
    public PersonAssert isAdult() {
        isNotNull();
        
        if (actual.getAge() < 18) {
            failWithMessage("Expected person to be adult but age was <%d>", 
                actual.getAge());
        }
        
        return this;
    }
    
    public PersonAssert isMinor() {
        isNotNull();
        
        if (actual.getAge() >= 18) {
            failWithMessage("Expected person to be minor but age was <%d>", 
                actual.getAge());
        }
        
        return this;
    }
    
    public PersonAssert livesIn(String expectedCity) {
        isNotNull();
        
        if (actual.getAddress() == null) {
            failWithMessage("Expected person to have address but was null");
        }
        
        String actualCity = actual.getAddress().getCity();
        if (!Objects.equals(actualCity, expectedCity)) {
            failWithMessage("Expected person to live in <%s> but lives in <%s>", 
                expectedCity, actualCity);
        }
        
        return this;
    }
}

// 사용 예시
class CustomAssertionUsageTest {
    
    @Test
    void 커스텀_assertion_사용() {
        Person person = new Person("John", 25, new Address("Seoul", "12345", "Korea"));
        
        PersonAssert.assertThat(person)
            .hasName("John")
            .hasAge(25)
            .isAdult()
            .livesIn("Seoul");
    }
}
```

### 복잡한 커스텀 Assertion
```java
// Order용 커스텀 Assertion
public class OrderAssert extends AbstractAssert<OrderAssert, Order> {
    
    public OrderAssert(Order order) {
        super(order, OrderAssert.class);
    }
    
    public static OrderAssert assertThat(Order order) {
        return new OrderAssert(order);
    }
    
    public OrderAssert hasStatus(OrderStatus expectedStatus) {
        isNotNull();
        
        OrderStatus actualStatus = actual.getStatus();
        if (actualStatus != expectedStatus) {
            failWithMessage("Expected order status to be <%s> but was <%s>", 
                expectedStatus, actualStatus);
        }
        
        return this;
    }
    
    public OrderAssert hasItemCount(int expectedCount) {
        isNotNull();
        
        int actualCount = actual.getItems().size();
        if (actualCount != expectedCount) {
            failWithMessage("Expected order to have <%d> items but had <%d>", 
                expectedCount, actualCount);
        }
        
        return this;
    }
    
    public OrderAssert containsItem(String itemName) {
        isNotNull();
        
        boolean contains = actual.getItems().stream()
            .anyMatch(item -> Objects.equals(item.getName(), itemName));
        
        if (!contains) {
            failWithMessage("Expected order to contain item <%s> but it didn't. " +
                "Actual items: <%s>", itemName, actual.getItems());
        }
        
        return this;
    }
    
    public OrderAssert hasTotalAmount(BigDecimal expectedAmount) {
        isNotNull();
        
        BigDecimal actualAmount = actual.getTotalAmount();
        if (actualAmount.compareTo(expectedAmount) != 0) {
            failWithMessage("Expected order total to be <%s> but was <%s>", 
                expectedAmount, actualAmount);
        }
        
        return this;
    }
    
    public OrderAssert isPending() {
        return hasStatus(OrderStatus.PENDING);
    }
    
    public OrderAssert isCompleted() {
        return hasStatus(OrderStatus.COMPLETED);
    }
    
    public OrderAssert isCancelled() {
        return hasStatus(OrderStatus.CANCELLED);
    }
}
```

### Assertion Generator 사용
```java
// AssertJ Generator를 사용하면 자동으로 커스텀 assertion 생성 가능
// Maven/Gradle 플러그인으로 설정하여 사용

// 자동 생성된 Assertion 사용 예시
@Test
void 자동생성된_assertion_사용() {
    Person person = new Person("John", 25);
    
    // 자동 생성된 PersonAssert 사용
    assertThat(person)
        .hasName("John")
        .hasAge(25);
}
```

## Soft Assertions

### 기본 Soft Assertions
```java
import org.assertj.core.api.SoftAssertions;

class SoftAssertionsTest {
    
    @Test
    void soft_assertions_기본_사용() {
        SoftAssertions softly = new SoftAssertions();
        
        Person person = new Person("John", 25);
        
        // 모든 검증을 수행하고 마지막에 한 번에 실패 보고
        softly.assertThat(person.getName()).isEqualTo("Jane");  // 실패
        softly.assertThat(person.getAge()).isEqualTo(30);       // 실패
        softly.assertThat(person.getAge()).isPositive();        // 성공
        
        // 모든 검증 결과를 확인
        softly.assertAll();
        // 위의 두 실패한 검증에 대한 모든 에러 메시지가 출력됨
    }
    
    @Test
    void soft_assertions_try_with_resources() {
        Person person = new Person("John", 25);
        
        // try-with-resources로 자동으로 assertAll() 호출
        try (SoftAssertions softly = new SoftAssertions()) {
            softly.assertThat(person.getName()).isEqualTo("Jane");
            softly.assertThat(person.getAge()).isEqualTo(30);
            softly.assertThat(person.getAge()).isPositive();
        }
        // 블록을 벗어날 때 자동으로 assertAll() 호출됨
    }
}
```

### JUnit 5와 Soft Assertions
```java
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;

@ExtendWith(SoftAssertionsExtension.class)
class JUnit5SoftAssertionsTest {
    
    @Test
    void soft_assertions_with_junit5(SoftAssertions softly) {
        // JUnit 5 extension이 SoftAssertions 인스턴스를 주입
        Person person = new Person("John", 25);
        
        softly.assertThat(person.getName()).isEqualTo("Jane");
        softly.assertThat(person.getAge()).isEqualTo(30);
        softly.assertThat(person.getAge()).isPositive();
        
        // assertAll()은 자동으로 호출됨
    }
    
    @Test
    void 복잡한_객체_soft_assertions(SoftAssertions softly) {
        List<Person> people = Arrays.asList(
            new Person("John", 25),
            new Person("Jane", 30),
            new Person("Bob", 35)
        );
        
        softly.assertThat(people).hasSize(3);
        softly.assertThat(people).extracting(Person::getName)
            .containsExactly("John", "Jane", "Bob");
        softly.assertThat(people).extracting(Person::getAge)
            .allMatch(age -> age > 20);
        
        // 각 개별 객체에 대한 검증
        people.forEach(person -> {
            softly.assertThat(person.getName()).isNotNull();
            softly.assertThat(person.getAge()).isPositive();
        });
    }
}
```

### BDD Style Soft Assertions
```java
import static org.assertj.core.api.BDDSoftAssertions.then;

class BDDSoftAssertionsTest {
    
    @Test
    void bdd_style_soft_assertions() {
        // given
        Person person = new Person("John", 25);
        
        // when
        person.celebrateBirthday();
        
        // then
        try (BDDSoftAssertions softly = new BDDSoftAssertions()) {
            softly.then(person.getAge()).isEqualTo(26);
            softly.then(person.getName()).isEqualTo("John");
            softly.then(person.isBirthdayCelebrated()).isTrue();
        }
    }
}
```

## 조건부 Assertions

### Assumptions 사용
```java
import static org.assertj.core.api.Assumptions.*;

class AssumptionsTest {
    
    @Test
    void 조건부_테스트_실행() {
        String environment = System.getProperty("test.env");
        
        // 특정 환경에서만 테스트 실행
        assumeThat(environment).isEqualTo("production");
        
        // 이 이후의 코드는 assumption이 true일 때만 실행
        ExpensiveService service = new ExpensiveService();
        assertThat(service.performComplexOperation()).isNotNull();
    }
    
    @Test
    void 복합_조건_assumption() {
        String os = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");
        
        assumeThat(os).startsWith("Windows");
        assumeThat(javaVersion).startsWith("11");
        
        // Windows + Java 11 환경에서만 실행되는 테스트
        WindowsSpecificService service = new WindowsSpecificService();
        assertThat(service.getWindowsSpecificData()).isNotEmpty();
    }
}
```

### 조건부 실행과 결합
```java
class ConditionalExecutionTest {
    
    @Test
    void 데이터베이스_연결_가능시에만_테스트() {
        DatabaseConnectionChecker checker = new DatabaseConnectionChecker();
        
        assumeThat(checker.isConnectable()).isTrue();
        
        // 데이터베이스 연결이 가능할 때만 실행
        DatabaseService service = new DatabaseService();
        List<User> users = service.getAllUsers();
        
        assertThat(users).isNotNull().isNotEmpty();
    }
    
    @Test
    void 외부_API_응답시에만_테스트() {
        ExternalApiClient client = new ExternalApiClient();
        
        try {
            ApiResponse response = client.ping();
            assumeThat(response.isSuccessful()).isTrue();
            
            // API가 응답할 때만 실제 테스트 실행
            List<Data> data = client.fetchData();
            assertThat(data).isNotEmpty();
            
        } catch (Exception e) {
            // API 호출 실패 시 테스트 스킵
            assumeThat(false).isTrue();
        }
    }
}
```

## 실전 활용 패턴

### 도메인별 Assertion 패턴
```java
// 도메인 객체별 검증 패턴
class DomainAssertionPatternsTest {
    
    @Test
    void 사용자_도메인_검증() {
        User user = User.builder()
            .name("John Doe")
            .email("john@example.com")
            .age(25)
            .status(UserStatus.ACTIVE)
            .build();
        
        assertThat(user)
            .satisfies(u -> {
                assertThat(u.getName()).matches("[A-Za-z\\s]+");
                assertThat(u.getEmail()).matches("\\w+@\\w+\\.\\w+");
                assertThat(u.getAge()).isBetween(18, 100);
                assertThat(u.getStatus()).isEqualTo(UserStatus.ACTIVE);
            });
    }
    
    @Test
    void 주문_도메인_검증() {
        Order order = new Order();
        order.addItem(new OrderItem("Laptop", 2, 50000));
        order.addItem(new OrderItem("Mouse", 1, 2000));
        
        assertThat(order)
            .extracting(Order::getTotalAmount, Order::getItemCount)
            .containsExactly(102000, 3);
        
        assertThat(order.getItems())
            .hasSize(2)
            .extracting(OrderItem::getName, OrderItem::getQuantity, OrderItem::getPrice)
            .containsExactly(
                tuple("Laptop", 2, 50000),
                tuple("Mouse", 1, 2000)
            );
    }
}
```

### 컬렉션 변환 검증 패턴
```java
class CollectionTransformationTest {
    
    @Test
    void 데이터_변환_파이프라인_검증() {
        List<RawData> rawData = Arrays.asList(
            new RawData("user1", "JOHN", "25"),
            new RawData("user2", "JANE", "30"),
            new RawData("user3", "BOB", "35")
        );
        
        List<ProcessedData> processed = rawData.stream()
            .map(DataProcessor::process)
            .collect(Collectors.toList());
        
        assertThat(processed)
            .hasSize(3)
            .extracting(ProcessedData::getId, ProcessedData::getName, ProcessedData::getAge)
            .containsExactly(
                tuple("user1", "John", 25),    // 이름 정규화, 나이 파싱
                tuple("user2", "Jane", 30),
                tuple("user3", "Bob", 35)
            );
    }
    
    @Test
    void 필터링_및_그룹핑_검증() {
        List<Employee> employees = Arrays.asList(
            new Employee("John", "Engineering", 5000),
            new Employee("Jane", "Marketing", 4500),
            new Employee("Bob", "Engineering", 5500),
            new Employee("Alice", "Marketing", 4800)
        );
        
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingDouble(Employee::getSalary)
            ));
        
        assertThat(avgSalaryByDept)
            .hasSize(2)
            .containsEntry("Engineering", 5250.0)
            .containsEntry("Marketing", 4650.0);
        
        assertThat(avgSalaryByDept.values())
            .allMatch(salary -> salary > 4000);
    }
}
```

### API 응답 검증 패턴
```java
class ApiResponseAssertionTest {
    
    @Test
    void REST_API_응답_검증() {
        // API 호출 시뮬레이션
        ApiResponse<List<User>> response = userApi.getUsers();
        
        assertThat(response)
            .extracting(ApiResponse::getStatusCode, ApiResponse::isSuccessful)
            .containsExactly(200, true);
        
        assertThat(response.getData())
            .isNotNull()
            .isNotEmpty()
            .allSatisfy(user -> {
                assertThat(user.getId()).isNotNull().isPositive();
                assertThat(user.getEmail()).contains("@");
                assertThat(user.getCreatedAt()).isNotNull().isBefore(LocalDateTime.now());
            });
    }
    
    @Test
    void 페이징_응답_검증() {
        PagedResponse<Product> response = productApi.getProducts(0, 10);
        
        assertThat(response)
            .satisfies(r -> {
                assertThat(r.getContent()).hasSize(10);
                assertThat(r.getPageNumber()).isZero();
                assertThat(r.getPageSize()).isEqualTo(10);
                assertThat(r.getTotalElements()).isPositive();
                assertThat(r.getTotalPages()).isPositive();
            });
        
        assertThat(response.getContent())
            .extracting(Product::getPrice)
            .allMatch(price -> price.compareTo(BigDecimal.ZERO) > 0);
    }
}
```

### 비동기 코드 검증 패턴
```java
class AsynchronousCodeTest {
    
    @Test
    void CompletableFuture_검증() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
                return "Hello World";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        
        assertThat(future)
            .succeedsWithin(Duration.ofSeconds(1))
            .isEqualTo("Hello World");
    }
    
    @Test
    void 비동기_작업_결과_검증() throws Exception {
        AsyncService service = new AsyncService();
        Future<ProcessingResult> future = service.processDataAsync("input");
        
        ProcessingResult result = future.get(2, TimeUnit.SECONDS);
        
        assertThat(result)
            .isNotNull()
            .satisfies(r -> {
                assertThat(r.isSuccessful()).isTrue();
                assertThat(r.getProcessedData()).isNotEmpty();
                assertThat(r.getProcessingTime()).isPositive();
            });
    }
}
```

### 에러 케이스 검증 패턴
```java
class ErrorCaseAssertionTest {
    
    @Test
    void 에러_상태_코드별_검증() {
        ApiClient client = new ApiClient();
        
        // 400 Bad Request
        assertThatThrownBy(() -> client.createUser(new InvalidUserData()))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("잘못된 요청")
            .extracting("statusCode")
            .isEqualTo(400);
        
        // 404 Not Found
        assertThatThrownBy(() -> client.getUser("nonexistent"))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("사용자를 찾을 수 없습니다")
            .extracting("statusCode")
            .isEqualTo(404);
        
        // 500 Internal Server Error
        assertThatThrownBy(() -> client.getUser("error-trigger"))
            .isInstanceOf(InternalServerException.class)
            .hasMessageContaining("서버 오류")
            .extracting("statusCode")
            .isEqualTo(500);
    }
    
    @Test
    void 비즈니스_로직_에러_검증() {
        OrderService orderService = new OrderService();
        
        // 재고 부족
        assertThatThrownBy(() -> orderService.createOrder("out-of-stock-item", 10))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessage("재고가 부족합니다")
            .extracting("availableStock", "requestedQuantity")
            .containsExactly(5, 10);
        
        // 결제 한도 초과
        assertThatThrownBy(() -> orderService.processPayment(1000000))
            .isInstanceOf(PaymentLimitException.class)
            .satisfies(ex -> {
                assertThat(ex.getAttemptedAmount()).isEqualTo(1000000);
                assertThat(ex.getMaxLimit()).isLessThan(1000000);
            });
    }
}
```

## 마무리

AssertJ는 테스트 코드의 가독성과 표현력을 크게 향상시키는 강력한 도구입니다.

**핵심 장점:**
- **유창한 API**로 자연스러운 테스트 코드 작성
- **풍부한 검증 메서드**로 다양한 상황 대응
- **명확한 에러 메시지**로 빠른 문제 파악
- **메서드 체이닝**으로 복합적인 검증 수행

TDD와 함께 AssertJ를 활용하면 더 표현력 있고 유지보수하기 쉬운 테스트 코드를 작성할 수 있습니다.