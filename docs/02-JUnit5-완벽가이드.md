# JUnit 5 완벽 가이드

## 목차
1. [JUnit 5 개요](#junit-5-개요)
2. [JUnit 5 아키텍처](#junit-5-아키텍처)
3. [기본 어노테이션](#기본-어노테이션)
4. [Assertions 완벽 가이드](#assertions-완벽-가이드)
5. [파라미터화 테스트](#파라미터화-테스트)
6. [동적 테스트](#동적-테스트)
7. [테스트 라이프사이클](#테스트-라이프사이클)
8. [조건부 테스트 실행](#조건부-테스트-실행)
9. [고급 기능](#고급-기능)
10. [실전 팁과 베스트 프랙티스](#실전-팁과-베스트-프랙티스)

## JUnit 5 개요

### JUnit 5란?
JUnit 5는 자바 플랫폼에서 테스트를 작성하고 실행하기 위한 최신 테스팅 프레임워크입니다. JUnit 4의 한계를 극복하고 Java 8+의 새로운 기능들을 활용합니다.

### JUnit 4와의 주요 차이점
| 기능 | JUnit 4 | JUnit 5 |
|------|---------|---------|
| 최소 Java 버전 | Java 5 | Java 8 |
| 아키텍처 | 단일 jar | 모듈형 아키텍처 |
| 어노테이션 | `@Test`, `@Before` | `@Test`, `@BeforeEach` |
| Assertions | 제한적 | 풍부하고 유연함 |
| 파라미터화 테스트 | 별도 라이브러리 | 내장 지원 |
| 동적 테스트 | 지원 안함 | 지원 |
| 람다 지원 | 없음 | 완전 지원 |

## JUnit 5 아키텍처

### 3개의 서브 프로젝트
```
JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage
```

#### 1. JUnit Platform
- JVM에서 테스팅 프레임워크를 실행하기 위한 기반
- TestEngine API 정의
- 콘솔 런처, Gradle/Maven 플러그인 제공

#### 2. JUnit Jupiter
- JUnit 5에서 테스트를 작성하고 실행하기 위한 새로운 프로그래밍 모델
- 새로운 assertion, annotation 제공
- TestEngine 구현체

#### 3. JUnit Vintage
- JUnit 3, 4로 작성된 테스트를 JUnit 5 플랫폼에서 실행
- 하위 호환성 제공

### 의존성 설정 (Gradle)
```gradle
dependencies {
    // Spring Boot Starter Test에 JUnit 5가 포함됨
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    
    // 또는 직접 추가
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
    useJUnitPlatform()  // JUnit 5 사용 설정
}
```

## 기본 어노테이션

### 테스트 메서드 정의
```java
import org.junit.jupiter.api.*;

class BasicAnnotationTest {
    
    @Test
    void 기본_테스트() {
        // 테스트 로직
        assertEquals(4, 2 + 2);
    }
    
    @Test
    @DisplayName("사용자 정의 테스트 이름")
    void customTestName() {
        assertTrue(true);
    }
    
    @Test
    @Disabled("아직 구현 중")
    void 비활성화된_테스트() {
        // 실행되지 않음
    }
}
```

### 라이프사이클 어노테이션
```java
class LifecycleTest {
    
    @BeforeAll
    static void 모든_테스트_실행_전() {
        // 클래스 레벨 초기화
        // static 메서드여야 함
        System.out.println("모든 테스트 시작 전 한 번 실행");
    }
    
    @AfterAll
    static void 모든_테스트_실행_후() {
        // 클래스 레벨 정리
        // static 메서드여야 함
        System.out.println("모든 테스트 완료 후 한 번 실행");
    }
    
    @BeforeEach
    void 각_테스트_실행_전() {
        // 각 테스트 메서드 실행 전 실행
        System.out.println("테스트 메서드 실행 전");
    }
    
    @AfterEach
    void 각_테스트_실행_후() {
        // 각 테스트 메서드 실행 후 실행
        System.out.println("테스트 메서드 실행 후");
    }
    
    @Test
    void 첫_번째_테스트() {
        System.out.println("첫 번째 테스트 실행");
    }
    
    @Test
    void 두_번째_테스트() {
        System.out.println("두 번째 테스트 실행");
    }
}

// 실행 순서:
// 모든 테스트 시작 전 한 번 실행
// 테스트 메서드 실행 전
// 첫 번째 테스트 실행
// 테스트 메서드 실행 후
// 테스트 메서드 실행 전
// 두 번째 테스트 실행
// 테스트 메서드 실행 후
// 모든 테스트 완료 후 한 번 실행
```

### 중첩 테스트 (@Nested)
```java
@DisplayName("계산기 테스트")
class CalculatorTest {
    
    Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Nested
    @DisplayName("덧셈 연산")
    class AdditionTest {
        
        @Test
        @DisplayName("양수 덧셈")
        void 양수_덧셈() {
            assertEquals(5, calculator.add(2, 3));
        }
        
        @Test
        @DisplayName("음수 덧셈")
        void 음수_덧셈() {
            assertEquals(-5, calculator.add(-2, -3));
        }
        
        @Nested
        @DisplayName("특수한 경우")
        class SpecialCases {
            
            @Test
            @DisplayName("0과의 덧셈")
            void 영과의_덧셈() {
                assertEquals(5, calculator.add(5, 0));
                assertEquals(5, calculator.add(0, 5));
            }
        }
    }
    
    @Nested
    @DisplayName("나눗셈 연산")
    class DivisionTest {
        
        @Test
        @DisplayName("정상적인 나눗셈")
        void 정상_나눗셈() {
            assertEquals(2, calculator.divide(6, 3));
        }
        
        @Test
        @DisplayName("0으로 나누기")
        void 영으로_나누기() {
            assertThrows(ArithmeticException.class, () -> {
                calculator.divide(5, 0);
            });
        }
    }
}
```

## Assertions 완벽 가이드

### 기본 Assertions
```java
import static org.junit.jupiter.api.Assertions.*;

class BasicAssertionsTest {
    
    @Test
    void 기본_assertEquals() {
        // 기본 비교
        assertEquals(4, 2 + 2);
        assertEquals("Hello", "He" + "llo");
        
        // 메시지와 함께
        assertEquals(4, 2 + 2, "2 + 2는 4여야 한다");
        
        // 람다를 이용한 지연 메시지 계산
        assertEquals(4, 2 + 2, () -> "복잡한 계산 결과: " + getComplexMessage());
    }
    
    @Test
    void 부동소수점_비교() {
        // 부동소수점은 delta 값으로 비교
        assertEquals(0.1 + 0.2, 0.3, 0.000001);
        
        // 또는 assertThat (AssertJ)를 사용 권장
    }
    
    @Test
    void boolean_검증() {
        assertTrue(5 > 3);
        assertFalse(5 < 3);
    }
    
    @Test
    void null_검증() {
        String str = null;
        assertNull(str);
        
        str = "not null";
        assertNotNull(str);
    }
    
    @Test
    void 동일성_검증() {
        String str1 = new String("Hello");
        String str2 = new String("Hello");
        String str3 = str1;
        
        // 값 비교
        assertEquals(str1, str2);
        
        // 참조 비교
        assertSame(str1, str3);
        assertNotSame(str1, str2);
    }
    
    private String getComplexMessage() {
        // 복잡한 계산이나 IO 작업
        return "계산 완료";
    }
}
```

### 예외 검증
```java
class ExceptionAssertionsTest {
    
    @Test
    void 예외_발생_검증() {
        // 예외 타입만 검증
        assertThrows(IllegalArgumentException.class, () -> {
            validateAge(-1);
        });
        
        // 예외 객체를 받아서 상세 검증
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> validateAge(-1)
        );
        
        assertEquals("나이는 0 이상이어야 합니다", exception.getMessage());
    }
    
    @Test
    void 예외가_발생하지_않음을_검증() {
        // 예외가 발생하지 않아야 하는 경우
        assertDoesNotThrow(() -> {
            validateAge(25);
        });
        
        // 반환값도 함께 검증
        String result = assertDoesNotThrow(() -> {
            return processAge(25);
        });
        
        assertEquals("성인", result);
    }
    
    @Test
    void 실행_시간_검증() {
        // 1초 이내에 실행되어야 함
        assertTimeout(Duration.ofSeconds(1), () -> {
            // 빠른 작업
            Thread.sleep(100);
        });
        
        // 시간 초과 시 즉시 중단
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            // 이 작업이 1초를 넘으면 즉시 중단됨
            Thread.sleep(500);
        });
    }
    
    private void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다");
        }
    }
    
    private String processAge(int age) {
        return age >= 18 ? "성인" : "미성년자";
    }
}
```

### 복합 Assertions
```java
class GroupedAssertionsTest {
    
    @Test
    void 그룹화된_검증() {
        Person person = new Person("John", "Doe", 30);
        
        // 모든 검증을 실행하고 결과를 종합
        assertAll("person 검증",
            () -> assertEquals("John", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName()),
            () -> assertEquals(30, person.getAge())
        );
    }
    
    @Test
    void 중첩된_그룹화() {
        Address address = new Address("Seoul", "123-456", "Korea");
        Person person = new Person("John", "Doe", 30, address);
        
        assertAll("person과 address 검증",
            () -> assertAll("person 기본 정보",
                () -> assertEquals("John", person.getFirstName()),
                () -> assertEquals("Doe", person.getLastName())
            ),
            () -> assertAll("address 정보",
                () -> assertEquals("Seoul", person.getAddress().getCity()),
                () -> assertEquals("Korea", person.getAddress().getCountry())
            )
        );
    }
    
    @Test
    void 컬렉션_검증() {
        List<String> fruits = Arrays.asList("apple", "banana", "orange");
        
        assertAll("과일 리스트 검증",
            () -> assertEquals(3, fruits.size()),
            () -> assertTrue(fruits.contains("apple")),
            () -> assertTrue(fruits.contains("banana")),
            () -> assertFalse(fruits.contains("grape"))
        );
    }
}
```

## 파라미터화 테스트

### @ValueSource
```java
class ParameterizedTestExamples {
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void 양수_검증(int number) {
        assertTrue(number > 0);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void 빈_문자열_검증(String input) {
        assertTrue(input.trim().isEmpty());
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.5, 0.9})
    void 확률값_검증(double probability) {
        assertTrue(probability > 0 && probability < 1);
    }
}
```

### @EnumSource
```java
enum Status {
    PENDING, APPROVED, REJECTED
}

class EnumParameterizedTest {
    
    @ParameterizedTest
    @EnumSource(Status.class)
    void 모든_상태_검증(Status status) {
        assertNotNull(status);
        assertTrue(status.name().length() > 0);
    }
    
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"APPROVED", "REJECTED"})
    void 특정_상태만_검증(Status status) {
        assertNotEquals(Status.PENDING, status);
    }
    
    @ParameterizedTest
    @EnumSource(value = Status.class, mode = EnumSource.Mode.EXCLUDE, names = {"PENDING"})
    void PENDING_제외_검증(Status status) {
        assertNotEquals(Status.PENDING, status);
    }
}
```

### @CsvSource
```java
class CsvParameterizedTest {
    
    @ParameterizedTest
    @CsvSource({
        "1, 1, 2",
        "2, 3, 5", 
        "5, 7, 12"
    })
    void 덧셈_테스트(int a, int b, int expected) {
        Calculator calculator = new Calculator();
        assertEquals(expected, calculator.add(a, b));
    }
    
    @ParameterizedTest
    @CsvSource(value = {
        "apple:5",
        "banana:6", 
        "orange:6"
    }, delimiter = ':')
    void 문자열_길이_테스트(String input, int expectedLength) {
        assertEquals(expectedLength, input.length());
    }
    
    @ParameterizedTest
    @CsvSource({
        "John, Doe, 'John Doe'",
        "Jane, Smith, 'Jane Smith'",
        "'', Johnson, ' Johnson'"
    })
    void 이름_결합_테스트(String firstName, String lastName, String expectedFullName) {
        Person person = new Person(firstName, lastName);
        assertEquals(expectedFullName, person.getFullName());
    }
}
```

### @CsvFileSource
```java
// resources/test-data.csv 파일 내용:
// firstName,lastName,age,expectedCategory
// John,Doe,25,Adult
// Jane,Smith,17,Minor
// Bob,Johnson,65,Senior

class CsvFileParameterizedTest {
    
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    void 나이_카테고리_테스트(String firstName, String lastName, int age, String expectedCategory) {
        Person person = new Person(firstName, lastName, age);
        assertEquals(expectedCategory, person.getAgeCategory());
    }
}
```

### @MethodSource
```java
class MethodSourceParameterizedTest {
    
    @ParameterizedTest
    @MethodSource("stringProvider")
    void 문자열_검증(String argument) {
        assertNotNull(argument);
        assertFalse(argument.isEmpty());
    }
    
    static Stream<String> stringProvider() {
        return Stream.of("apple", "banana", "orange");
    }
    
    @ParameterizedTest
    @MethodSource("complexObjectProvider")
    void 복합_객체_테스트(Person person, String expectedCategory) {
        assertEquals(expectedCategory, person.getAgeCategory());
    }
    
    static Stream<Arguments> complexObjectProvider() {
        return Stream.of(
            Arguments.of(new Person("John", 25), "Adult"),
            Arguments.of(new Person("Jane", 17), "Minor"),
            Arguments.of(new Person("Bob", 65), "Senior")
        );
    }
    
    @ParameterizedTest
    @MethodSource("invalidEmailProvider")
    void 잘못된_이메일_검증(String email) {
        EmailValidator validator = new EmailValidator();
        assertFalse(validator.isValid(email));
    }
    
    static Stream<String> invalidEmailProvider() {
        return Stream.of(
            "invalid-email",
            "@domain.com",
            "user@",
            "user name@domain.com",
            ""
        );
    }
}
```

### 커스텀 ArgumentsProvider
```java
class CustomArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of("test1", 1),
            Arguments.of("test2", 2),
            Arguments.of("test3", 3)
        );
    }
}

class CustomParameterizedTest {
    
    @ParameterizedTest
    @ArgumentsSource(CustomArgumentsProvider.class)
    void 커스텀_인자_테스트(String name, int number) {
        assertTrue(name.startsWith("test"));
        assertTrue(number > 0);
    }
}
```

## 동적 테스트

### 기본 동적 테스트
```java
import org.junit.jupiter.api.*;
import java.util.stream.Stream;

class DynamicTestExamples {
    
    @TestFactory
    Stream<DynamicTest> 동적_덧셈_테스트() {
        Calculator calculator = new Calculator();
        
        return Stream.of(
            DynamicTest.dynamicTest("2 + 3 = 5", 
                () -> assertEquals(5, calculator.add(2, 3))),
            DynamicTest.dynamicTest("5 + 7 = 12", 
                () -> assertEquals(12, calculator.add(5, 7))),
            DynamicTest.dynamicTest("10 + 0 = 10", 
                () -> assertEquals(10, calculator.add(10, 0)))
        );
    }
    
    @TestFactory
    Collection<DynamicTest> 컬렉션_기반_동적_테스트() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        return numbers.stream()
            .map(number -> DynamicTest.dynamicTest(
                "Testing number: " + number,
                () -> assertTrue(number > 0)
            ))
            .collect(Collectors.toList());
    }
}
```

### 복잡한 동적 테스트 시나리오
```java
class AdvancedDynamicTestExamples {
    
    @TestFactory
    Stream<DynamicNode> 계층적_동적_테스트() {
        return Stream.of(
            DynamicContainer.dynamicContainer("양수 테스트",
                Stream.of(
                    DynamicTest.dynamicTest("1은 양수", () -> assertTrue(1 > 0)),
                    DynamicTest.dynamicTest("100은 양수", () -> assertTrue(100 > 0))
                )
            ),
            DynamicContainer.dynamicContainer("음수 테스트",
                Stream.of(
                    DynamicTest.dynamicTest("-1은 음수", () -> assertTrue(-1 < 0)),
                    DynamicTest.dynamicTest("-100은 음수", () -> assertTrue(-100 < 0))
                )
            )
        );
    }
    
    @TestFactory
    Stream<DynamicTest> 파일_기반_동적_테스트() {
        // 실제로는 파일이나 데이터베이스에서 테스트 데이터를 읽어올 수 있음
        List<TestCase> testCases = loadTestCases();
        
        return testCases.stream()
            .map(testCase -> DynamicTest.dynamicTest(
                "Test: " + testCase.getName(),
                testCase.getUri(),  // 테스트 소스 위치
                () -> executeTestCase(testCase)
            ));
    }
    
    private List<TestCase> loadTestCases() {
        return Arrays.asList(
            new TestCase("case1", "input1", "expected1"),
            new TestCase("case2", "input2", "expected2")
        );
    }
    
    private void executeTestCase(TestCase testCase) {
        // 테스트 케이스 실행 로직
        String result = processInput(testCase.getInput());
        assertEquals(testCase.getExpected(), result);
    }
    
    private String processInput(String input) {
        return input.toUpperCase();
    }
    
    static class TestCase {
        private final String name;
        private final String input;
        private final String expected;
        
        public TestCase(String name, String input, String expected) {
            this.name = name;
            this.input = input;
            this.expected = expected;
        }
        
        // getters...
        public String getName() { return name; }
        public String getInput() { return input; }
        public String getExpected() { return expected; }
        public URI getUri() { 
            try {
                return new URI("test://dynamic/" + name);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
```

## 테스트 라이프사이클

### 인스턴스 라이프사이클 제어
```java
import org.junit.jupiter.api.TestInstance;

// 기본값: 각 테스트 메서드마다 새 인스턴스 생성
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PerMethodLifecycleTest {
    
    private int counter = 0;
    
    @Test
    void 첫_번째_테스트() {
        counter++;
        assertEquals(1, counter);  // 항상 1
    }
    
    @Test
    void 두_번째_테스트() {
        counter++;
        assertEquals(1, counter);  // 새 인스턴스이므로 1
    }
}

// 클래스당 하나의 인스턴스 사용
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerClassLifecycleTest {
    
    private int counter = 0;
    
    @BeforeAll
    void setUp() {
        // PER_CLASS에서는 static이 아니어도 됨
        System.out.println("Setup for class");
    }
    
    @Test
    void 첫_번째_테스트() {
        counter++;
        assertEquals(1, counter);
    }
    
    @Test
    void 두_번째_테스트() {
        counter++;
        assertEquals(2, counter);  // 같은 인스턴스이므로 2
    }
    
    @AfterAll
    void tearDown() {
        // PER_CLASS에서는 static이 아니어도 됨
        System.out.println("Teardown for class");
    }
}
```

### 테스트 순서 제어
```java
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedTestExample {
    
    @Test
    @Order(1)
    void 첫_번째_실행() {
        System.out.println("1번 테스트");
    }
    
    @Test
    @Order(2)
    void 두_번째_실행() {
        System.out.println("2번 테스트");
    }
    
    @Test
    @Order(3)
    void 세_번째_실행() {
        System.out.println("3번 테스트");
    }
}

@TestMethodOrder(MethodOrderer.DisplayName.class)
class DisplayNameOrderedTest {
    
    @Test
    @DisplayName("A - 첫 번째")
    void test1() { }
    
    @Test
    @DisplayName("B - 두 번째")
    void test2() { }
    
    @Test
    @DisplayName("C - 세 번째")
    void test3() { }
}

@TestMethodOrder(MethodOrderer.MethodName.class)
class MethodNameOrderedTest {
    
    @Test
    void aFirstTest() { }
    
    @Test
    void bSecondTest() { }
    
    @Test
    void cThirdTest() { }
}
```

## 조건부 테스트 실행

### 운영체제 기반 조건부 실행
```java
import org.junit.jupiter.api.condition.*;

class ConditionalTestExamples {
    
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void 윈도우에서만_실행() {
        // Windows에서만 실행됨
        assertTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
    }
    
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void 리눅스_맥에서만_실행() {
        // Linux 또는 Mac에서만 실행됨
        String os = System.getProperty("os.name").toLowerCase();
        assertTrue(os.contains("linux") || os.contains("mac"));
    }
    
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void 윈도우에서는_비활성화() {
        // Windows가 아닌 환경에서만 실행됨
    }
}
```

### Java 버전 기반 조건부 실행
```java
class JavaVersionConditionalTest {
    
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void Java8에서만_실행() {
        assertEquals("1.8", System.getProperty("java.specification.version"));
    }
    
    @Test
    @EnabledOnJre({JRE.JAVA_17, JRE.JAVA_21})
    void Java17_또는_21에서만_실행() {
        String version = System.getProperty("java.specification.version");
        assertTrue(version.equals("17") || version.equals("21"));
    }
    
    @Test
    @EnabledForJreRange(min = JRE.JAVA_11, max = JRE.JAVA_21)
    void Java11부터_21까지_실행() {
        // Java 11부터 21까지의 버전에서만 실행
    }
    
    @Test
    @DisabledOnJre(JRE.JAVA_8)
    void Java8에서는_비활성화() {
        // Java 8이 아닌 버전에서만 실행
    }
}
```

### 시스템 속성 기반 조건부 실행
```java
class SystemPropertyConditionalTest {
    
    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
    void 64비트_시스템에서만_실행() {
        assertTrue(System.getProperty("os.arch").contains("64"));
    }
    
    @Test
    @DisabledIfSystemProperty(named = "ci-server", matches = "true")
    void CI서버에서는_비활성화() {
        // ci-server 속성이 true가 아닌 경우에만 실행
    }
    
    @Test
    @EnabledIfSystemProperty(named = "test.environment", matches = "development")
    void 개발환경에서만_실행() {
        assertEquals("development", System.getProperty("test.environment"));
    }
}
```

### 환경변수 기반 조건부 실행
```java
class EnvironmentVariableConditionalTest {
    
    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging")
    void 스테이징환경에서만_실행() {
        assertEquals("staging", System.getenv("ENV"));
    }
    
    @Test
    @DisabledIfEnvironmentVariable(named = "SKIP_INTEGRATION_TESTS", matches = "true")
    void 통합테스트_스킵설정시_비활성화() {
        // SKIP_INTEGRATION_TESTS가 true가 아닌 경우에만 실행
    }
}
```

### 커스텀 조건
```java
class CustomConditionalTest {
    
    @Test
    @EnabledIf("customCondition")
    void 커스텀_조건부_테스트() {
        // customCondition() 메서드가 true를 반환할 때만 실행
        assertTrue(isSpecialConditionMet());
    }
    
    boolean customCondition() {
        // 복잡한 조건 로직
        return LocalDateTime.now().getHour() > 9 && 
               LocalDateTime.now().getHour() < 17;
    }
    
    @Test
    @DisabledIf("isSlowTestEnvironment")
    void 느린_환경에서는_비활성화() {
        // isSlowTestEnvironment()가 false일 때만 실행
    }
    
    boolean isSlowTestEnvironment() {
        return "slow".equals(System.getProperty("test.environment"));
    }
    
    private boolean isSpecialConditionMet() {
        return true;
    }
}
```

## 고급 기능

### 반복 테스트 (@RepeatedTest)
```java
class RepeatedTestExample {
    
    @RepeatedTest(5)
    void 반복_테스트() {
        // 5번 반복 실행
        assertTrue(Math.random() >= 0);  // 항상 참
    }
    
    @RepeatedTest(value = 3, name = "반복 {currentRepetition}/{totalRepetitions}")
    void 커스텀_이름_반복_테스트() {
        System.out.println("반복 테스트 실행 중");
    }
    
    @RepeatedTest(10)
    void 반복_정보_활용_테스트(RepetitionInfo repetitionInfo) {
        int current = repetitionInfo.getCurrentRepetition();
        int total = repetitionInfo.getTotalRepetitions();
        
        System.out.printf("실행 중: %d/%d%n", current, total);
        
        if (current == total) {
            System.out.println("마지막 반복!");
        }
    }
}
```

### 태그를 이용한 테스트 분류
```java
import org.junit.jupiter.api.Tag;

class TaggedTestExample {
    
    @Test
    @Tag("fast")
    void 빠른_테스트() {
        assertTrue(2 + 2 == 4);
    }
    
    @Test
    @Tag("slow")
    @Tag("integration")
    void 느린_통합_테스트() {
        // 시간이 오래 걸리는 테스트
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        assertTrue(true);
    }
    
    @Test
    @Tag("unit")
    void 단위_테스트() {
        assertEquals(5, 2 + 3);
    }
    
    @Test
    @Tag("database")
    @Tag("slow")
    void 데이터베이스_테스트() {
        // 데이터베이스 관련 테스트
    }
}

// build.gradle에서 특정 태그만 실행
// test {
//     useJUnitPlatform {
//         includeTags 'fast'
//         excludeTags 'slow'
//     }
// }
```

### 커스텀 어노테이션
```java
// 커스텀 어노테이션 정의
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Test
@Tag("fast")
@Tag("unit")
public @interface FastUnitTest {
}

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Test
@Tag("slow")
@Tag("integration")
public @interface SlowIntegrationTest {
}

// 사용 예시
class CustomAnnotationExample {
    
    @FastUnitTest
    void 빠른_단위_테스트() {
        assertEquals(4, 2 + 2);
    }
    
    @SlowIntegrationTest
    void 느린_통합_테스트() {
        // 시간이 오래 걸리는 테스트
    }
}
```

## 실전 팁과 베스트 프랙티스

### 1. 테스트 이름 짓기
```java
class TestNamingExamples {
    
    // ❌ 나쁜 예
    @Test
    void test1() { }
    
    @Test
    void userTest() { }
    
    // ✅ 좋은 예 - 메서드명으로 의도 표현
    @Test
    void 사용자_나이가_음수일때_예외가_발생한다() { }
    
    @Test
    void 잘못된_이메일_형식일때_검증에_실패한다() { }
    
    // ✅ DisplayName으로 더 자세한 설명
    @Test
    @DisplayName("사용자가 VIP 등급일 때 10% 할인이 적용된다")
    void vipDiscountTest() { }
    
    @Test
    @DisplayName("주문 금액이 10만원 이상일 때 무료배송이 적용된다")
    void freeShippingTest() { }
}
```

### 2. Given-When-Then 패턴 활용
```java
class GivenWhenThenExample {
    
    @Test
    void 상품을_장바구니에_추가할때_수량이_증가한다() {
        // given (준비)
        ShoppingCart cart = new ShoppingCart();
        Product product = new Product("노트북", 1000000);
        int initialCount = cart.getItemCount();
        
        // when (실행)
        cart.addProduct(product, 2);
        
        // then (검증)
        assertEquals(initialCount + 2, cart.getItemCount());
        assertTrue(cart.contains(product));
        assertEquals(2000000, cart.getTotalPrice());
    }
}
```

### 3. 테스트 데이터 빌더 패턴
```java
// 테스트 데이터 빌더
class UserTestDataBuilder {
    private String name = "기본이름";
    private String email = "default@example.com";
    private int age = 25;
    private UserRole role = UserRole.MEMBER;
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestDataBuilder withAge(int age) {
        this.age = age;
        return this;
    }
    
    public UserTestDataBuilder withRole(UserRole role) {
        this.role = role;
        return this;
    }
    
    public User build() {
        return new User(name, email, age, role);
    }
}

// 사용 예시
class UserTestWithBuilder {
    
    @Test
    void VIP_사용자는_특별할인을_받는다() {
        // given
        User vipUser = aUser()
            .withName("VIP고객")
            .withRole(UserRole.VIP)
            .withAge(35)
            .build();
        
        DiscountService discountService = new DiscountService();
        
        // when
        int discount = discountService.calculateDiscount(vipUser, 100000);
        
        // then
        assertEquals(10000, discount);  // 10% 할인
    }
}
```

### 4. 커스텀 Matcher 사용
```java
// 커스텀 Matcher 정의
class CustomMatchers {
    
    public static Matcher<String> validEmail() {
        return new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String email) {
                return email != null && 
                       email.contains("@") && 
                       email.contains(".");
            }
            
            @Override
            public void describeTo(Description description) {
                description.appendText("유효한 이메일 형식");
            }
        };
    }
    
    public static Matcher<User> adult() {
        return new TypeSafeMatcher<User>() {
            @Override
            protected boolean matchesSafely(User user) {
                return user.getAge() >= 18;
            }
            
            @Override
            public void describeTo(Description description) {
                description.appendText("성인 사용자");
            }
        };
    }
}

// 사용 예시 (Hamcrest와 함께)
class CustomMatcherUsageExample {
    
    @Test
    void 이메일_형식_검증() {
        String email = "user@example.com";
        assertThat(email, validEmail());
    }
    
    @Test
    void 성인_사용자_검증() {
        User user = new User("John", 25);
        assertThat(user, adult());
    }
}
```

### 5. 테스트 픽스처 관리
```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestFixtureExample {
    
    private DatabaseTestContainer database;
    private UserRepository userRepository;
    
    @BeforeAll
    void setUpTestEnvironment() {
        // 테스트 환경 전체 설정 (무거운 작업)
        database = new DatabaseTestContainer();
        database.start();
        userRepository = new UserRepository(database.getConnection());
    }
    
    @BeforeEach
    void setUpEachTest() {
        // 각 테스트별 초기화 (가벼운 작업)
        database.cleanup();
        userRepository.deleteAll();
    }
    
    @AfterEach
    void tearDownEachTest() {
        // 각 테스트 후 정리
        userRepository.deleteAll();
    }
    
    @AfterAll
    void tearDownTestEnvironment() {
        // 전체 테스트 환경 정리
        database.stop();
    }
    
    @Test
    void 사용자_저장_테스트() {
        // given
        User user = new User("John", "john@example.com");
        
        // when
        User saved = userRepository.save(user);
        
        // then
        assertNotNull(saved.getId());
        assertEquals("John", saved.getName());
    }
}
```

### 6. 예외 상황 테스트
```java
class ExceptionTestingBestPractices {
    
    @Test
    void 필수_파라미터_누락시_적절한_예외_발생() {
        // given
        UserService userService = new UserService();
        
        // when & then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(null, "email@example.com")
        );
        
        // 예외 메시지도 검증
        assertEquals("이름은 필수입니다", exception.getMessage());
    }
    
    @Test
    void 여러_예외_시나리오_테스트() {
        UserService userService = new UserService();
        
        assertAll("사용자 생성 예외 시나리오",
            () -> assertThrows(IllegalArgumentException.class, 
                () -> userService.createUser(null, "email@example.com")),
            () -> assertThrows(IllegalArgumentException.class, 
                () -> userService.createUser("name", null)),
            () -> assertThrows(IllegalArgumentException.class, 
                () -> userService.createUser("", "email@example.com"))
        );
    }
}
```

## 마무리

JUnit 5는 현대적인 자바 개발에 최적화된 강력한 테스팅 프레임워크입니다.

**핵심 포인트:**
- **람다와 스트림 API 활용**으로 더 표현력 있는 테스트
- **파라미터화 테스트**로 반복 코드 제거
- **동적 테스트**로 런타임 테스트 생성
- **조건부 실행**으로 환경별 테스트 제어
- **풍부한 Assertion**으로 정확한 검증

TDD와 함께 JUnit 5를 마스터하면 더 안정적이고 유지보수하기 쉬운 코드를 작성할 수 있습니다.