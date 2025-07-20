# 실전 TDD 케이스 스터디

## 목차
1. [케이스 스터디 개요](#케이스-스터디-개요)
2. [Case 1: 간단한 계산기 구현](#case-1-간단한-계산기-구현)
3. [Case 2: 사용자 등록 시스템](#case-2-사용자-등록-시스템)
4. [Case 3: 주문 관리 시스템](#case-3-주문-관리-시스템)
5. [Case 4: 할인 정책 엔진](#case-4-할인-정책-엔진)
6. [Case 5: 파일 처리 시스템](#case-5-파일-처리-시스템)
7. [Case 6: API 클라이언트 구현](#case-6-api-클라이언트-구현)
8. [Case 7: 이벤트 기반 시스템](#case-7-이벤트-기반-시스템)
9. [실전 팁과 학습 포인트](#실전-팁과-학습-포인트)

## 케이스 스터디 개요

### 학습 목표
- 실제 개발 시나리오에서 TDD 적용 방법 습득
- Red-Green-Refactor 사이클의 실전 활용
- 복잡한 비즈니스 로직을 TDD로 구현하는 방법
- 외부 의존성이 있는 코드의 TDD 접근법

### 사용 기술 스택
- Java 21
- JUnit 5
- AssertJ
- Mockito
- Spring Boot (일부 케이스)

## Case 1: 간단한 계산기 구현

### 요구사항
- 두 수의 사칙연산 (덧셈, 뺄셈, 곱셈, 나눗셈)
- 0으로 나누기 예외 처리
- 소수점 계산 지원

### Step 1: 첫 번째 테스트 (덧셈)

#### Red - 실패하는 테스트 작성
```java
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CalculatorTest {
    
    @Test
    void 두_숫자를_더할_수_있다() {
        // given
        Calculator calculator = new Calculator();
        
        // when
        double result = calculator.add(2, 3);
        
        // then
        assertThat(result).isEqualTo(5);
    }
}
```

**결과**: 컴파일 에러 - Calculator 클래스가 존재하지 않음

#### Green - 최소한의 구현
```java
public class Calculator {
    public double add(double a, double b) {
        return 5; // 하드코딩으로 테스트만 통과
    }
}
```

**결과**: 테스트 통과

#### 더 많은 테스트 추가
```java
@Test
void 다른_숫자들의_덧셈() {
    Calculator calculator = new Calculator();
    
    assertThat(calculator.add(1, 4)).isEqualTo(5);
    assertThat(calculator.add(10, 15)).isEqualTo(25);
}
```

#### Refactor - 일반화된 구현
```java
public class Calculator {
    public double add(double a, double b) {
        return a + b;
    }
}
```

### Step 2: 뺄셈 구현

#### Red
```java
@Test
void 두_숫자를_뺄_수_있다() {
    Calculator calculator = new Calculator();
    
    double result = calculator.subtract(5, 3);
    
    assertThat(result).isEqualTo(2);
}
```

#### Green
```java
public class Calculator {
    public double add(double a, double b) {
        return a + b;
    }
    
    public double subtract(double a, double b) {
        return 2; // 하드코딩
    }
}
```

#### 더 많은 테스트와 리팩토링
```java
@Test
void 음수_결과_뺄셈() {
    Calculator calculator = new Calculator();
    
    double result = calculator.subtract(3, 5);
    
    assertThat(result).isEqualTo(-2);
}

// 구현 일반화
public double subtract(double a, double b) {
    return a - b;
}
```

### Step 3: 곱셈과 나눗셈

#### 곱셈 구현
```java
@Test
void 두_숫자를_곱할_수_있다() {
    Calculator calculator = new Calculator();
    
    double result = calculator.multiply(4, 5);
    
    assertThat(result).isEqualTo(20);
}

@Test
void 영과의_곱셈() {
    Calculator calculator = new Calculator();
    
    double result = calculator.multiply(5, 0);
    
    assertThat(result).isEqualTo(0);
}

public double multiply(double a, double b) {
    return a * b;
}
```

#### 나눗셈과 예외 처리
```java
@Test
void 두_숫자를_나눌_수_있다() {
    Calculator calculator = new Calculator();
    
    double result = calculator.divide(10, 2);
    
    assertThat(result).isEqualTo(5);
}

@Test
void 영으로_나누면_예외가_발생한다() {
    Calculator calculator = new Calculator();
    
    assertThatThrownBy(() -> calculator.divide(10, 0))
        .isInstanceOf(ArithmeticException.class)
        .hasMessage("0으로 나눌 수 없습니다");
}

public double divide(double a, double b) {
    if (b == 0) {
        throw new ArithmeticException("0으로 나눌 수 없습니다");
    }
    return a / b;
}
```

### Step 4: 최종 리팩토링
```java
public class Calculator {
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public double subtract(double a, double b) {
        return a - b;
    }
    
    public double multiply(double a, double b) {
        return a * b;
    }
    
    public double divide(double a, double b) {
        validateDivisor(b);
        return a / b;
    }
    
    private void validateDivisor(double divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("0으로 나눌 수 없습니다");
        }
    }
}
```

### 학습 포인트
1. **점진적 구현**: 하드코딩 → 일반화 과정
2. **예외 처리**: 비즈니스 규칙을 테스트로 먼저 정의
3. **리팩토링**: 중복 제거와 가독성 향상

## Case 2: 사용자 등록 시스템

### 요구사항
- 이메일과 비밀번호로 사용자 등록
- 이메일 형식 검증
- 비밀번호 강도 검증
- 중복 이메일 방지
- 등록 완료 시 환영 이메일 발송

### Step 1: 기본 사용자 등록

#### Red
```java
class UserRegistrationServiceTest {
    
    @Test
    void 유효한_정보로_사용자를_등록할_수_있다() {
        // given
        UserRegistrationService service = new UserRegistrationService();
        UserRegistrationRequest request = new UserRegistrationRequest(
            "john@example.com", "StrongPassword123!"
        );
        
        // when
        User user = service.registerUser(request);
        
        // then
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
    }
}
```

#### Green - 최소 구현
```java
public class UserRegistrationService {
    
    public User registerUser(UserRegistrationRequest request) {
        return User.builder()
            .id(UUID.randomUUID().toString())
            .email(request.getEmail())
            .createdAt(LocalDateTime.now())
            .build();
    }
}

public class UserRegistrationRequest {
    private final String email;
    private final String password;
    
    public UserRegistrationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // getters...
}

@Builder
public class User {
    private String id;
    private String email;
    private String hashedPassword;
    private LocalDateTime createdAt;
    
    // getters...
}
```

### Step 2: 이메일 형식 검증

#### Red
```java
@Test
void 잘못된_이메일_형식으로_등록하면_예외가_발생한다() {
    UserRegistrationService service = new UserRegistrationService();
    UserRegistrationRequest request = new UserRegistrationRequest(
        "invalid-email", "StrongPassword123!"
    );
    
    assertThatThrownBy(() -> service.registerUser(request))
        .isInstanceOf(InvalidEmailFormatException.class)
        .hasMessage("올바른 이메일 형식이 아닙니다");
}

@ParameterizedTest
@ValueSource(strings = {"", "invalid", "@domain.com", "user@", "user name@domain.com"})
void 다양한_잘못된_이메일_형식_검증(String invalidEmail) {
    UserRegistrationService service = new UserRegistrationService();
    UserRegistrationRequest request = new UserRegistrationRequest(
        invalidEmail, "StrongPassword123!"
    );
    
    assertThatThrownBy(() -> service.registerUser(request))
        .isInstanceOf(InvalidEmailFormatException.class);
}
```

#### Green
```java
public class UserRegistrationService {
    
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    public User registerUser(UserRegistrationRequest request) {
        validateEmail(request.getEmail());
        
        return User.builder()
            .id(UUID.randomUUID().toString())
            .email(request.getEmail())
            .createdAt(LocalDateTime.now())
            .build();
    }
    
    private void validateEmail(String email) {
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            throw new InvalidEmailFormatException("올바른 이메일 형식이 아닙니다");
        }
    }
}

public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
```

### Step 3: 비밀번호 강도 검증

#### Red
```java
@Test
void 약한_비밀번호로_등록하면_예외가_발생한다() {
    UserRegistrationService service = new UserRegistrationService();
    UserRegistrationRequest request = new UserRegistrationRequest(
        "john@example.com", "weak"
    );
    
    assertThatThrownBy(() -> service.registerUser(request))
        .isInstanceOf(WeakPasswordException.class)
        .hasMessage("비밀번호는 8자 이상이어야 하고, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다");
}

@ParameterizedTest
@ValueSource(strings = {"short", "nouppercase123!", "NOLOWERCASE123!", "NoNumbers!", "NoSpecialChar123"})
void 다양한_약한_비밀번호_검증(String weakPassword) {
    UserRegistrationService service = new UserRegistrationService();
    UserRegistrationRequest request = new UserRegistrationRequest(
        "john@example.com", weakPassword
    );
    
    assertThatThrownBy(() -> service.registerUser(request))
        .isInstanceOf(WeakPasswordException.class);
}
```

#### Green
```java
public class UserRegistrationService {
    
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    public User registerUser(UserRegistrationRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        
        String hashedPassword = hashPassword(request.getPassword());
        
        return User.builder()
            .id(UUID.randomUUID().toString())
            .email(request.getEmail())
            .hashedPassword(hashedPassword)
            .createdAt(LocalDateTime.now())
            .build();
    }
    
    private void validateEmail(String email) {
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            throw new InvalidEmailFormatException("올바른 이메일 형식이 아닙니다");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new WeakPasswordException(getPasswordRequirementMessage());
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new WeakPasswordException(getPasswordRequirementMessage());
        }
    }
    
    private String getPasswordRequirementMessage() {
        return "비밀번호는 8자 이상이어야 하고, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다";
    }
    
    private String hashPassword(String password) {
        // 실제로는 BCrypt 등을 사용
        return "hashed_" + password;
    }
}
```

### Step 4: 중복 이메일 방지

#### Red
```java
@Test
void 이미_존재하는_이메일로_등록하면_예외가_발생한다() {
    // given
    UserRepository mockRepository = mock(UserRepository.class);
    when(mockRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.of(existingUser()));
    
    UserRegistrationService service = new UserRegistrationService(mockRepository);
    UserRegistrationRequest request = new UserRegistrationRequest(
        "john@example.com", "StrongPassword123!"
    );
    
    // when & then
    assertThatThrownBy(() -> service.registerUser(request))
        .isInstanceOf(DuplicateEmailException.class)
        .hasMessage("이미 등록된 이메일입니다");
}

@Test
void 새로운_이메일로_등록하면_성공한다() {
    // given
    UserRepository mockRepository = mock(UserRepository.class);
    when(mockRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.empty());
    when(mockRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    
    UserRegistrationService service = new UserRegistrationService(mockRepository);
    UserRegistrationRequest request = new UserRegistrationRequest(
        "john@example.com", "StrongPassword123!"
    );
    
    // when
    User user = service.registerUser(request);
    
    // then
    assertThat(user.getEmail()).isEqualTo("john@example.com");
    verify(mockRepository).save(any(User.class));
}

private User existingUser() {
    return User.builder()
        .id("existing-id")
        .email("john@example.com")
        .hashedPassword("hashed_password")
        .createdAt(LocalDateTime.now().minusDays(1))
        .build();
}
```

#### Green
```java
public class UserRegistrationService {
    
    private final UserRepository userRepository;
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User registerUser(UserRegistrationRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        checkEmailDuplication(request.getEmail());
        
        String hashedPassword = hashPassword(request.getPassword());
        
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .email(request.getEmail())
            .hashedPassword(hashedPassword)
            .createdAt(LocalDateTime.now())
            .build();
        
        return userRepository.save(user);
    }
    
    private void checkEmailDuplication(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("이미 등록된 이메일입니다");
        }
    }
    
    // 기존 메서드들...
}

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
}
```

### Step 5: 이메일 발송 기능

#### Red
```java
@Test
void 사용자_등록_성공시_환영_이메일이_발송된다() {
    // given
    UserRepository mockRepository = mock(UserRepository.class);
    EmailService mockEmailService = mock(EmailService.class);
    
    when(mockRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.empty());
    when(mockRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    
    UserRegistrationService service = new UserRegistrationService(
        mockRepository, mockEmailService
    );
    
    UserRegistrationRequest request = new UserRegistrationRequest(
        "john@example.com", "StrongPassword123!"
    );
    
    // when
    User user = service.registerUser(request);
    
    // then
    verify(mockEmailService).sendWelcomeEmail("john@example.com");
}

@Test
void 등록_실패시_이메일이_발송되지_않는다() {
    // given
    UserRepository mockRepository = mock(UserRepository.class);
    EmailService mockEmailService = mock(EmailService.class);
    
    when(mockRepository.findByEmail("john@example.com"))
        .thenReturn(Optional.of(existingUser()));
    
    UserRegistrationService service = new UserRegistrationService(
        mockRepository, mockEmailService
    );
    
    UserRegistrationRequest request = new UserRegistrationRequest(
        "john@example.com", "StrongPassword123!"
    );
    
    // when & then
    assertThatThrownBy(() -> service.registerUser(request))
        .isInstanceOf(DuplicateEmailException.class);
    
    verify(mockEmailService, never()).sendWelcomeEmail(any());
}
```

#### Green
```java
public class UserRegistrationService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    public UserRegistrationService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    
    public User registerUser(UserRegistrationRequest request) {
        validateEmail(request.getEmail());
        validatePassword(request.getPassword());
        checkEmailDuplication(request.getEmail());
        
        String hashedPassword = hashPassword(request.getPassword());
        
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .email(request.getEmail())
            .hashedPassword(hashedPassword)
            .createdAt(LocalDateTime.now())
            .build();
        
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(savedUser.getEmail());
        
        return savedUser;
    }
    
    // 기존 메서드들...
}

public interface EmailService {
    void sendWelcomeEmail(String email);
}
```

### 학습 포인트
1. **외부 의존성 격리**: Repository와 EmailService를 Mock으로 처리
2. **예외 상황 테스트**: 다양한 유효성 검증 시나리오
3. **파라미터화 테스트**: 여러 입력값을 효율적으로 테스트
4. **부작용 검증**: 이메일 발송 등의 부작용을 verify로 검증

## Case 3: 주문 관리 시스템

### 요구사항
- 고객이 상품을 주문할 수 있다
- 재고가 충분해야 주문 가능
- 주문 총액 계산
- 할인 적용 (회원 등급별)
- 주문 상태 관리 (PENDING → CONFIRMED → SHIPPED → DELIVERED)

### Step 1: 기본 주문 생성

#### Red
```java
class OrderServiceTest {
    
    @Test
    void 고객이_상품을_주문할_수_있다() {
        // given
        OrderService orderService = new OrderService();
        Customer customer = new Customer("customer1", "John Doe");
        Product product = new Product("product1", "Laptop", 1000, 10);
        OrderRequest request = new OrderRequest(customer.getId(), product.getId(), 2);
        
        // when
        Order order = orderService.createOrder(request);
        
        // then
        assertThat(order.getCustomerId()).isEqualTo("customer1");
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getOrderItems().get(0).getProductId()).isEqualTo("product1");
        assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }
}
```

#### Green
```java
public class OrderService {
    
    public Order createOrder(OrderRequest request) {
        OrderItem item = new OrderItem(
            request.getProductId(),
            request.getQuantity(),
            1000 // 하드코딩된 가격
        );
        
        return Order.builder()
            .id(UUID.randomUUID().toString())
            .customerId(request.getCustomerId())
            .orderItems(List.of(item))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();
    }
}

@Builder
public class Order {
    private String id;
    private String customerId;
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private int totalAmount;
    
    // getters...
}

public class OrderItem {
    private String productId;
    private int quantity;
    private int unitPrice;
    
    public OrderItem(String productId, int quantity, int unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public int getTotalPrice() {
        return unitPrice * quantity;
    }
    
    // getters...
}

public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

public class OrderRequest {
    private String customerId;
    private String productId;
    private int quantity;
    
    // constructor, getters...
}
```

### Step 2: 재고 확인 기능

#### Red
```java
@Test
void 재고가_부족하면_주문할_수_없다() {
    // given
    ProductRepository mockProductRepository = mock(ProductRepository.class);
    Product product = new Product("product1", "Laptop", 1000, 1); // 재고 1개
    when(mockProductRepository.findById("product1")).thenReturn(Optional.of(product));
    
    OrderService orderService = new OrderService(mockProductRepository);
    OrderRequest request = new OrderRequest("customer1", "product1", 5); // 5개 주문
    
    // when & then
    assertThatThrownBy(() -> orderService.createOrder(request))
        .isInstanceOf(InsufficientStockException.class)
        .hasMessage("재고가 부족합니다. 요청: 5, 재고: 1");
}

@Test
void 재고가_충분하면_주문할_수_있다() {
    // given
    ProductRepository mockProductRepository = mock(ProductRepository.class);
    Product product = new Product("product1", "Laptop", 1000, 10); // 재고 10개
    when(mockProductRepository.findById("product1")).thenReturn(Optional.of(product));
    
    OrderService orderService = new OrderService(mockProductRepository);
    OrderRequest request = new OrderRequest("customer1", "product1", 5); // 5개 주문
    
    // when
    Order order = orderService.createOrder(request);
    
    // then
    assertThat(order.getOrderItems().get(0).getQuantity()).isEqualTo(5);
}
```

#### Green
```java
public class OrderService {
    
    private final ProductRepository productRepository;
    
    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Order createOrder(OrderRequest request) {
        Product product = findProduct(request.getProductId());
        validateStock(product, request.getQuantity());
        
        OrderItem item = new OrderItem(
            request.getProductId(),
            request.getQuantity(),
            product.getPrice()
        );
        
        return Order.builder()
            .id(UUID.randomUUID().toString())
            .customerId(request.getCustomerId())
            .orderItems(List.of(item))
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .totalAmount(item.getTotalPrice())
            .build();
    }
    
    private Product findProduct(String productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다: " + productId));
    }
    
    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException(
                String.format("재고가 부족합니다. 요청: %d, 재고: %d", 
                    requestedQuantity, product.getStock())
            );
        }
    }
}

public class Product {
    private String id;
    private String name;
    private int price;
    private int stock;
    
    // constructor, getters...
}

public interface ProductRepository {
    Optional<Product> findById(String productId);
}
```

### Step 3: 할인 적용

#### Red
```java
@Test
void VIP_고객은_10퍼센트_할인을_받는다() {
    // given
    ProductRepository mockProductRepository = mock(ProductRepository.class);
    CustomerRepository mockCustomerRepository = mock(CustomerRepository.class);
    DiscountPolicy mockDiscountPolicy = mock(DiscountPolicy.class);
    
    Product product = new Product("product1", "Laptop", 1000, 10);
    Customer vipCustomer = new Customer("customer1", "John", CustomerGrade.VIP);
    
    when(mockProductRepository.findById("product1")).thenReturn(Optional.of(product));
    when(mockCustomerRepository.findById("customer1")).thenReturn(Optional.of(vipCustomer));
    when(mockDiscountPolicy.calculateDiscount(vipCustomer, 2000)).thenReturn(200); // 10% 할인
    
    OrderService orderService = new OrderService(
        mockProductRepository, mockCustomerRepository, mockDiscountPolicy
    );
    
    OrderRequest request = new OrderRequest("customer1", "product1", 2); // 2000원
    
    // when
    Order order = orderService.createOrder(request);
    
    // then
    assertThat(order.getTotalAmount()).isEqualTo(1800); // 2000 - 200
    assertThat(order.getDiscountAmount()).isEqualTo(200);
}

@Test
void 일반_고객은_할인을_받지_않는다() {
    // given
    ProductRepository mockProductRepository = mock(ProductRepository.class);
    CustomerRepository mockCustomerRepository = mock(CustomerRepository.class);
    DiscountPolicy mockDiscountPolicy = mock(DiscountPolicy.class);
    
    Product product = new Product("product1", "Laptop", 1000, 10);
    Customer regularCustomer = new Customer("customer1", "John", CustomerGrade.REGULAR);
    
    when(mockProductRepository.findById("product1")).thenReturn(Optional.of(product));
    when(mockCustomerRepository.findById("customer1")).thenReturn(Optional.of(regularCustomer));
    when(mockDiscountPolicy.calculateDiscount(regularCustomer, 2000)).thenReturn(0);
    
    OrderService orderService = new OrderService(
        mockProductRepository, mockCustomerRepository, mockDiscountPolicy
    );
    
    OrderRequest request = new OrderRequest("customer1", "product1", 2);
    
    // when
    Order order = orderService.createOrder(request);
    
    // then
    assertThat(order.getTotalAmount()).isEqualTo(2000);
    assertThat(order.getDiscountAmount()).isEqualTo(0);
}
```

#### Green
```java
public class OrderService {
    
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final DiscountPolicy discountPolicy;
    
    public OrderService(
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            DiscountPolicy discountPolicy) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.discountPolicy = discountPolicy;
    }
    
    public Order createOrder(OrderRequest request) {
        Product product = findProduct(request.getProductId());
        Customer customer = findCustomer(request.getCustomerId());
        validateStock(product, request.getQuantity());
        
        OrderItem item = new OrderItem(
            request.getProductId(),
            request.getQuantity(),
            product.getPrice()
        );
        
        int originalAmount = item.getTotalPrice();
        int discountAmount = discountPolicy.calculateDiscount(customer, originalAmount);
        int finalAmount = originalAmount - discountAmount;
        
        return Order.builder()
            .id(UUID.randomUUID().toString())
            .customerId(request.getCustomerId())
            .orderItems(List.of(item))
            .status(OrderStatus.PENDING)
            .originalAmount(originalAmount)
            .discountAmount(discountAmount)
            .totalAmount(finalAmount)
            .createdAt(LocalDateTime.now())
            .build();
    }
    
    private Customer findCustomer(String customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException("고객을 찾을 수 없습니다: " + customerId));
    }
    
    // 기존 메서드들...
}

public class Customer {
    private String id;
    private String name;
    private CustomerGrade grade;
    
    // constructor, getters...
}

public enum CustomerGrade {
    REGULAR, SILVER, GOLD, VIP
}

public interface CustomerRepository {
    Optional<Customer> findById(String customerId);
}

public interface DiscountPolicy {
    int calculateDiscount(Customer customer, int amount);
}
```

### Step 4: 주문 상태 관리

#### Red
```java
@Test
void 주문을_확정할_수_있다() {
    // given
    Order order = createPendingOrder();
    OrderService orderService = new OrderService(/* dependencies */);
    
    // when
    orderService.confirmOrder(order.getId());
    
    // then
    Order confirmedOrder = orderService.findOrder(order.getId());
    assertThat(confirmedOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
}

@Test
void 이미_확정된_주문은_다시_확정할_수_없다() {
    // given
    Order confirmedOrder = createConfirmedOrder();
    OrderService orderService = new OrderService(/* dependencies */);
    
    // when & then
    assertThatThrownBy(() -> orderService.confirmOrder(confirmedOrder.getId()))
        .isInstanceOf(InvalidOrderStatusException.class)
        .hasMessage("PENDING 상태의 주문만 확정할 수 있습니다");
}

@Test
void 확정된_주문을_배송할_수_있다() {
    // given
    Order confirmedOrder = createConfirmedOrder();
    OrderService orderService = new OrderService(/* dependencies */);
    
    // when
    orderService.shipOrder(confirmedOrder.getId());
    
    // then
    Order shippedOrder = orderService.findOrder(confirmedOrder.getId());
    assertThat(shippedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    assertThat(shippedOrder.getShippedAt()).isNotNull();
}
```

#### Green
```java
public class OrderService {
    
    private final OrderRepository orderRepository;
    // 기존 dependencies...
    
    public void confirmOrder(String orderId) {
        Order order = findOrder(orderId);
        order.confirm();
        orderRepository.save(order);
    }
    
    public void shipOrder(String orderId) {
        Order order = findOrder(orderId);
        order.ship();
        orderRepository.save(order);
    }
    
    public Order findOrder(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다: " + orderId));
    }
    
    // 기존 메서드들...
}

@Builder
public class Order {
    private String id;
    private String customerId;
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime shippedAt;
    private int originalAmount;
    private int discountAmount;
    private int totalAmount;
    
    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("PENDING 상태의 주문만 확정할 수 있습니다");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    public void ship() {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new InvalidOrderStatusException("CONFIRMED 상태의 주문만 배송할 수 있습니다");
        }
        this.status = OrderStatus.SHIPPED;
        this.shippedAt = LocalDateTime.now();
    }
    
    // getters...
}

public interface OrderRepository {
    Optional<Order> findById(String orderId);
    Order save(Order order);
}
```

### 학습 포인트
1. **도메인 모델링**: 주문, 고객, 상품 등의 도메인 객체 설계
2. **상태 관리**: 주문 상태 변경과 관련 비즈니스 규칙
3. **의존성 주입**: 여러 Repository와 Service의 조합
4. **예외 처리**: 비즈니스 규칙 위반 시 적절한 예외 발생

## Case 4: 할인 정책 엔진

### 요구사항
- 다양한 할인 정책 지원 (비율 할인, 고정 금액 할인)
- 할인 정책 조합 (복수 할인 적용)
- 할인 한도 설정
- 특정 조건에서만 할인 적용

### Step 1: 기본 할인 정책

#### Red
```java
class DiscountPolicyTest {
    
    @Test
    void 비율_할인_정책_적용() {
        // given
        DiscountPolicy discountPolicy = new PercentageDiscountPolicy(10); // 10% 할인
        Customer customer = new Customer("customer1", CustomerGrade.VIP);
        
        // when
        int discount = discountPolicy.calculateDiscount(customer, 10000);
        
        // then
        assertThat(discount).isEqualTo(1000);
    }
    
    @Test
    void 고정_금액_할인_정책_적용() {
        // given
        DiscountPolicy discountPolicy = new FixedAmountDiscountPolicy(500);
        Customer customer = new Customer("customer1", CustomerGrade.GOLD);
        
        // when
        int discount = discountPolicy.calculateDiscount(customer, 10000);
        
        // then
        assertThat(discount).isEqualTo(500);
    }
}
```

#### Green
```java
public interface DiscountPolicy {
    int calculateDiscount(Customer customer, int amount);
}

public class PercentageDiscountPolicy implements DiscountPolicy {
    
    private final int percentage;
    
    public PercentageDiscountPolicy(int percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public int calculateDiscount(Customer customer, int amount) {
        return amount * percentage / 100;
    }
}

public class FixedAmountDiscountPolicy implements DiscountPolicy {
    
    private final int discountAmount;
    
    public FixedAmountDiscountPolicy(int discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    @Override
    public int calculateDiscount(Customer customer, int amount) {
        return discountAmount;
    }
}
```

### Step 2: 조건부 할인 정책

#### Red
```java
@Test
void VIP_고객만_할인_적용() {
    // given
    DiscountPolicy basePolicy = new PercentageDiscountPolicy(20);
    DiscountCondition vipCondition = new GradeDiscountCondition(CustomerGrade.VIP);
    DiscountPolicy conditionalPolicy = new ConditionalDiscountPolicy(basePolicy, vipCondition);
    
    Customer vipCustomer = new Customer("vip", CustomerGrade.VIP);
    Customer regularCustomer = new Customer("regular", CustomerGrade.REGULAR);
    
    // when & then
    assertThat(conditionalPolicy.calculateDiscount(vipCustomer, 10000)).isEqualTo(2000);
    assertThat(conditionalPolicy.calculateDiscount(regularCustomer, 10000)).isEqualTo(0);
}

@Test
void 최소_구매_금액_조건_할인() {
    // given
    DiscountPolicy basePolicy = new FixedAmountDiscountPolicy(1000);
    DiscountCondition amountCondition = new MinimumAmountDiscountCondition(50000);
    DiscountPolicy conditionalPolicy = new ConditionalDiscountPolicy(basePolicy, amountCondition);
    
    Customer customer = new Customer("customer1", CustomerGrade.REGULAR);
    
    // when & then
    assertThat(conditionalPolicy.calculateDiscount(customer, 60000)).isEqualTo(1000);
    assertThat(conditionalPolicy.calculateDiscount(customer, 30000)).isEqualTo(0);
}
```

#### Green
```java
public interface DiscountCondition {
    boolean isSatisfied(Customer customer, int amount);
}

public class GradeDiscountCondition implements DiscountCondition {
    
    private final CustomerGrade requiredGrade;
    
    public GradeDiscountCondition(CustomerGrade requiredGrade) {
        this.requiredGrade = requiredGrade;
    }
    
    @Override
    public boolean isSatisfied(Customer customer, int amount) {
        return customer.getGrade() == requiredGrade;
    }
}

public class MinimumAmountDiscountCondition implements DiscountCondition {
    
    private final int minimumAmount;
    
    public MinimumAmountDiscountCondition(int minimumAmount) {
        this.minimumAmount = minimumAmount;
    }
    
    @Override
    public boolean isSatisfied(Customer customer, int amount) {
        return amount >= minimumAmount;
    }
}

public class ConditionalDiscountPolicy implements DiscountPolicy {
    
    private final DiscountPolicy basePolicy;
    private final DiscountCondition condition;
    
    public ConditionalDiscountPolicy(DiscountPolicy basePolicy, DiscountCondition condition) {
        this.basePolicy = basePolicy;
        this.condition = condition;
    }
    
    @Override
    public int calculateDiscount(Customer customer, int amount) {
        if (condition.isSatisfied(customer, amount)) {
            return basePolicy.calculateDiscount(customer, amount);
        }
        return 0;
    }
}
```

### Step 3: 복합 할인 정책

#### Red
```java
@Test
void 여러_할인_정책_조합_적용() {
    // given
    List<DiscountPolicy> policies = Arrays.asList(
        new PercentageDiscountPolicy(10),  // 10% 할인
        new FixedAmountDiscountPolicy(500) // 500원 할인
    );
    
    DiscountPolicy combinedPolicy = new CompositeDiscountPolicy(policies);
    Customer customer = new Customer("customer1", CustomerGrade.VIP);
    
    // when
    int discount = combinedPolicy.calculateDiscount(customer, 10000);
    
    // then
    assertThat(discount).isEqualTo(1500); // 1000 + 500
}

@Test
void 할인_한도_제한() {
    // given
    DiscountPolicy basePolicy = new PercentageDiscountPolicy(50); // 50% 할인
    DiscountPolicy limitedPolicy = new LimitedDiscountPolicy(basePolicy, 3000); // 최대 3000원
    
    Customer customer = new Customer("customer1", CustomerGrade.VIP);
    
    // when & then
    assertThat(limitedPolicy.calculateDiscount(customer, 10000)).isEqualTo(3000); // 5000원이지만 3000원으로 제한
    assertThat(limitedPolicy.calculateDiscount(customer, 4000)).isEqualTo(2000);  // 2000원 (제한 미적용)
}
```

#### Green
```java
public class CompositeDiscountPolicy implements DiscountPolicy {
    
    private final List<DiscountPolicy> policies;
    
    public CompositeDiscountPolicy(List<DiscountPolicy> policies) {
        this.policies = new ArrayList<>(policies);
    }
    
    @Override
    public int calculateDiscount(Customer customer, int amount) {
        return policies.stream()
            .mapToInt(policy -> policy.calculateDiscount(customer, amount))
            .sum();
    }
}

public class LimitedDiscountPolicy implements DiscountPolicy {
    
    private final DiscountPolicy basePolicy;
    private final int maxDiscount;
    
    public LimitedDiscountPolicy(DiscountPolicy basePolicy, int maxDiscount) {
        this.basePolicy = basePolicy;
        this.maxDiscount = maxDiscount;
    }
    
    @Override
    public int calculateDiscount(Customer customer, int amount) {
        int discount = basePolicy.calculateDiscount(customer, amount);
        return Math.min(discount, maxDiscount);
    }
}
```

### Step 4: 할인 정책 팩토리

#### Red
```java
@Test
void 고객_등급별_할인_정책_생성() {
    // given
    DiscountPolicyFactory factory = new DiscountPolicyFactory();
    
    // when
    DiscountPolicy vipPolicy = factory.createPolicyForGrade(CustomerGrade.VIP);
    DiscountPolicy goldPolicy = factory.createPolicyForGrade(CustomerGrade.GOLD);
    DiscountPolicy regularPolicy = factory.createPolicyForGrade(CustomerGrade.REGULAR);
    
    Customer vipCustomer = new Customer("vip", CustomerGrade.VIP);
    Customer goldCustomer = new Customer("gold", CustomerGrade.GOLD);
    Customer regularCustomer = new Customer("regular", CustomerGrade.REGULAR);
    
    // then
    assertThat(vipPolicy.calculateDiscount(vipCustomer, 10000)).isEqualTo(2000);    // 20% 할인
    assertThat(goldPolicy.calculateDiscount(goldCustomer, 10000)).isEqualTo(1000); // 10% 할인  
    assertThat(regularPolicy.calculateDiscount(regularCustomer, 10000)).isEqualTo(0); // 할인 없음
}
```

#### Green
```java
public class DiscountPolicyFactory {
    
    public DiscountPolicy createPolicyForGrade(CustomerGrade grade) {
        switch (grade) {
            case VIP:
                return createVipPolicy();
            case GOLD:
                return createGoldPolicy();
            case SILVER:
                return createSilverPolicy();
            case REGULAR:
            default:
                return createRegularPolicy();
        }
    }
    
    private DiscountPolicy createVipPolicy() {
        DiscountPolicy basePolicy = new PercentageDiscountPolicy(20);
        DiscountCondition condition = new GradeDiscountCondition(CustomerGrade.VIP);
        return new ConditionalDiscountPolicy(basePolicy, condition);
    }
    
    private DiscountPolicy createGoldPolicy() {
        DiscountPolicy basePolicy = new PercentageDiscountPolicy(10);
        DiscountCondition condition = new GradeDiscountCondition(CustomerGrade.GOLD);
        return new ConditionalDiscountPolicy(basePolicy, condition);
    }
    
    private DiscountPolicy createSilverPolicy() {
        DiscountPolicy basePolicy = new PercentageDiscountPolicy(5);
        DiscountCondition condition = new GradeDiscountCondition(CustomerGrade.SILVER);
        return new ConditionalDiscountPolicy(basePolicy, condition);
    }
    
    private DiscountPolicy createRegularPolicy() {
        return new FixedAmountDiscountPolicy(0);
    }
}
```

### 학습 포인트
1. **전략 패턴**: 다양한 할인 정책을 인터페이스로 추상화
2. **데코레이터 패턴**: 기존 정책에 조건이나 제한을 추가
3. **컴포지트 패턴**: 여러 할인 정책을 조합
4. **팩토리 패턴**: 복잡한 정책 조합을 캡슐화

## Case 5: 파일 처리 시스템

### 요구사항
- CSV 파일에서 사용자 데이터 읽기
- 데이터 유효성 검증
- 중복 데이터 제거
- 처리 결과 리포트 생성

### Step 1: CSV 파일 읽기

#### Red
```java
class CsvUserImporterTest {
    
    @Test
    void CSV_파일에서_사용자_데이터를_읽을_수_있다() {
        // given
        String csvContent = "email,name,age\n" +
                           "john@example.com,John Doe,25\n" +
                           "jane@example.com,Jane Smith,30";
        
        FileReader mockFileReader = mock(FileReader.class);
        when(mockFileReader.read("users.csv")).thenReturn(csvContent);
        
        CsvUserImporter importer = new CsvUserImporter(mockFileReader);
        
        // when
        List<UserData> users = importer.importUsers("users.csv");
        
        // then
        assertThat(users).hasSize(2);
        assertThat(users.get(0))
            .extracting(UserData::getEmail, UserData::getName, UserData::getAge)
            .containsExactly("john@example.com", "John Doe", 25);
        assertThat(users.get(1))
            .extracting(UserData::getEmail, UserData::getName, UserData::getAge)
            .containsExactly("jane@example.com", "Jane Smith", 30);
    }
}
```

#### Green
```java
public class CsvUserImporter {
    
    private final FileReader fileReader;
    
    public CsvUserImporter(FileReader fileReader) {
        this.fileReader = fileReader;
    }
    
    public List<UserData> importUsers(String filename) {
        String content = fileReader.read(filename);
        return parseUsers(content);
    }
    
    private List<UserData> parseUsers(String content) {
        String[] lines = content.split("\n");
        List<UserData> users = new ArrayList<>();
        
        // 헤더 스킵
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            UserData user = new UserData(
                fields[0], // email
                fields[1], // name
                Integer.parseInt(fields[2]) // age
            );
            users.add(user);
        }
        
        return users;
    }
}

public class UserData {
    private String email;
    private String name;
    private int age;
    
    public UserData(String email, String name, int age) {
        this.email = email;
        this.name = name;
        this.age = age;
    }
    
    // getters...
}

public interface FileReader {
    String read(String filename);
}
```

### Step 2: 데이터 유효성 검증

#### Red
```java
@Test
void 잘못된_형식의_데이터는_예외가_발생한다() {
    // given
    String invalidCsvContent = "email,name,age\n" +
                              "invalid-email,John Doe,25\n" +
                              "jane@example.com,,30\n" +
                              "bob@example.com,Bob,-5";
    
    FileReader mockFileReader = mock(FileReader.class);
    when(mockFileReader.read("users.csv")).thenReturn(invalidCsvContent);
    
    CsvUserImporter importer = new CsvUserImporter(mockFileReader);
    
    // when & then
    assertThatThrownBy(() -> importer.importUsers("users.csv"))
        .isInstanceOf(DataValidationException.class)
        .hasMessageContaining("유효하지 않은 데이터");
}

@Test
void 유효성_검증_통과시_정상_처리() {
    // given
    String validCsvContent = "email,name,age\n" +
                            "john@example.com,John Doe,25\n" +
                            "jane@example.com,Jane Smith,30";
    
    FileReader mockFileReader = mock(FileReader.class);
    when(mockFileReader.read("users.csv")).thenReturn(validCsvContent);
    
    CsvUserImporter importer = new CsvUserImporter(mockFileReader);
    
    // when
    List<UserData> users = importer.importUsers("users.csv");
    
    // then
    assertThat(users).hasSize(2);
}
```

#### Green
```java
public class CsvUserImporter {
    
    private final FileReader fileReader;
    private final UserDataValidator validator;
    
    public CsvUserImporter(FileReader fileReader) {
        this.fileReader = fileReader;
        this.validator = new UserDataValidator();
    }
    
    public List<UserData> importUsers(String filename) {
        String content = fileReader.read(filename);
        List<UserData> users = parseUsers(content);
        validateUsers(users);
        return users;
    }
    
    private List<UserData> parseUsers(String content) {
        String[] lines = content.split("\n");
        List<UserData> users = new ArrayList<>();
        
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            try {
                UserData user = new UserData(
                    fields[0], 
                    fields[1], 
                    Integer.parseInt(fields[2])
                );
                users.add(user);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new DataFormatException("잘못된 데이터 형식: " + lines[i]);
            }
        }
        
        return users;
    }
    
    private void validateUsers(List<UserData> users) {
        List<String> errors = new ArrayList<>();
        
        for (int i = 0; i < users.size(); i++) {
            try {
                validator.validate(users.get(i));
            } catch (ValidationException e) {
                errors.add(String.format("라인 %d: %s", i + 2, e.getMessage()));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new DataValidationException("유효하지 않은 데이터: " + String.join(", ", errors));
        }
    }
}

public class UserDataValidator {
    
    private static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    public void validate(UserData userData) {
        validateEmail(userData.getEmail());
        validateName(userData.getName());
        validateAge(userData.getAge());
    }
    
    private void validateEmail(String email) {
        if (email == null || !email.matches(EMAIL_PATTERN)) {
            throw new ValidationException("잘못된 이메일 형식: " + email);
        }
    }
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("이름은 필수입니다");
        }
    }
    
    private void validateAge(int age) {
        if (age < 0 || age > 150) {
            throw new ValidationException("나이는 0-150 사이여야 합니다: " + age);
        }
    }
}
```

### Step 3: 중복 제거 및 처리 결과

#### Red
```java
@Test
void 중복된_이메일은_제거된다() {
    // given
    String csvWithDuplicates = "email,name,age\n" +
                              "john@example.com,John Doe,25\n" +
                              "jane@example.com,Jane Smith,30\n" +
                              "john@example.com,John Different,26"; // 중복
    
    FileReader mockFileReader = mock(FileReader.class);
    when(mockFileReader.read("users.csv")).thenReturn(csvWithDuplicates);
    
    CsvUserImporter importer = new CsvUserImporter(mockFileReader);
    
    // when
    ImportResult result = importer.importUsersWithResult("users.csv");
    
    // then
    assertThat(result.getUsers()).hasSize(2);
    assertThat(result.getTotalProcessed()).isEqualTo(3);
    assertThat(result.getDuplicatesRemoved()).isEqualTo(1);
    assertThat(result.getSuccessful()).isEqualTo(2);
}

@Test
void 처리_결과_리포트_생성() {
    // given
    String csvContent = "email,name,age\n" +
                       "john@example.com,John Doe,25\n" +
                       "invalid-email,Jane,30\n" +
                       "bob@example.com,Bob,35\n" +
                       "john@example.com,John Duplicate,26";
    
    FileReader mockFileReader = mock(FileReader.class);
    when(mockFileReader.read("users.csv")).thenReturn(csvContent);
    
    CsvUserImporter importer = new CsvUserImporter(mockFileReader);
    
    // when
    ImportResult result = importer.importUsersWithResult("users.csv");
    
    // then
    assertThat(result.getTotalProcessed()).isEqualTo(4);
    assertThat(result.getSuccessful()).isEqualTo(2);
    assertThat(result.getValidationErrors()).isEqualTo(1);
    assertThat(result.getDuplicatesRemoved()).isEqualTo(1);
}
```

#### Green
```java
public class CsvUserImporter {
    
    private final FileReader fileReader;
    private final UserDataValidator validator;
    
    public CsvUserImporter(FileReader fileReader) {
        this.fileReader = fileReader;
        this.validator = new UserDataValidator();
    }
    
    public ImportResult importUsersWithResult(String filename) {
        String content = fileReader.read(filename);
        
        ImportResult.Builder resultBuilder = ImportResult.builder();
        List<UserData> allUsers = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();
        
        // 파싱 및 기본 유효성 검증
        String[] lines = content.split("\n");
        for (int i = 1; i < lines.length; i++) {
            resultBuilder.incrementTotalProcessed();
            
            try {
                String[] fields = lines[i].split(",");
                UserData user = new UserData(fields[0], fields[1], Integer.parseInt(fields[2]));
                validator.validate(user);
                allUsers.add(user);
            } catch (Exception e) {
                validationErrors.add(String.format("라인 %d: %s", i + 1, e.getMessage()));
                resultBuilder.incrementValidationErrors();
            }
        }
        
        // 중복 제거
        Set<String> seenEmails = new HashSet<>();
        List<UserData> uniqueUsers = new ArrayList<>();
        
        for (UserData user : allUsers) {
            if (seenEmails.contains(user.getEmail())) {
                resultBuilder.incrementDuplicatesRemoved();
            } else {
                seenEmails.add(user.getEmail());
                uniqueUsers.add(user);
                resultBuilder.incrementSuccessful();
            }
        }
        
        return resultBuilder
            .users(uniqueUsers)
            .validationErrorMessages(validationErrors)
            .build();
    }
}

@Builder
public class ImportResult {
    private List<UserData> users;
    private int totalProcessed;
    private int successful;
    private int validationErrors;
    private int duplicatesRemoved;
    private List<String> validationErrorMessages;
    
    public static class Builder {
        private List<UserData> users = new ArrayList<>();
        private int totalProcessed = 0;
        private int successful = 0;
        private int validationErrors = 0;
        private int duplicatesRemoved = 0;
        private List<String> validationErrorMessages = new ArrayList<>();
        
        public Builder incrementTotalProcessed() {
            this.totalProcessed++;
            return this;
        }
        
        public Builder incrementSuccessful() {
            this.successful++;
            return this;
        }
        
        public Builder incrementValidationErrors() {
            this.validationErrors++;
            return this;
        }
        
        public Builder incrementDuplicatesRemoved() {
            this.duplicatesRemoved++;
            return this;
        }
        
        public Builder users(List<UserData> users) {
            this.users = users;
            return this;
        }
        
        public Builder validationErrorMessages(List<String> messages) {
            this.validationErrorMessages = messages;
            return this;
        }
        
        public ImportResult build() {
            return new ImportResult(users, totalProcessed, successful, 
                                  validationErrors, duplicatesRemoved, validationErrorMessages);
        }
    }
    
    // getters...
}
```

### 학습 포인트
1. **파일 처리 추상화**: FileReader 인터페이스로 파일 시스템 의존성 제거
2. **데이터 검증**: 별도의 Validator 클래스로 책임 분리
3. **결과 객체**: 복잡한 처리 결과를 구조화된 객체로 반환
4. **빌더 패턴**: 복잡한 객체 생성을 단순화

## Case 6: API 클라이언트 구현

### 요구사항
- 외부 REST API 호출
- 재시도 메커니즘
- 응답 캐싱
- 에러 처리 및 로깅

### Step 1: 기본 API 호출

#### Red
```java
class ExternalUserApiClientTest {
    
    @Test
    void 사용자_정보를_조회할_수_있다() {
        // given
        String userId = "user123";
        String expectedResponse = """
            {
                "id": "user123",
                "name": "John Doe",
                "email": "john@example.com"
            }
            """;
        
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockHttpClient.get("/users/" + userId)).thenReturn(expectedResponse);
        
        ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient);
        
        // when
        UserResponse user = apiClient.getUser(userId);
        
        // then
        assertThat(user.getId()).isEqualTo("user123");
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
    }
}
```

#### Green
```java
public class ExternalUserApiClient {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ExternalUserApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }
    
    public UserResponse getUser(String userId) {
        String response = httpClient.get("/users/" + userId);
        
        try {
            return objectMapper.readValue(response, UserResponse.class);
        } catch (Exception e) {
            throw new ApiException("응답 파싱 실패", e);
        }
    }
}

public class UserResponse {
    private String id;
    private String name;
    private String email;
    
    // getters, setters...
}

public interface HttpClient {
    String get(String path);
    String post(String path, String body);
}

public class ApiException extends RuntimeException {
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Step 2: 에러 처리

#### Red
```java
@Test
void API_호출_실패시_예외가_발생한다() {
    // given
    HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.get("/users/user123"))
        .thenThrow(new HttpException(404, "Not Found"));
    
    ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient);
    
    // when & then
    assertThatThrownBy(() -> apiClient.getUser("user123"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("사용자를 찾을 수 없습니다: user123");
}

@Test
void 서버_에러시_적절한_예외가_발생한다() {
    // given
    HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.get("/users/user123"))
        .thenThrow(new HttpException(500, "Internal Server Error"));
    
    ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient);
    
    // when & then
    assertThatThrownBy(() -> apiClient.getUser("user123"))
        .isInstanceOf(ApiServerException.class)
        .hasMessage("API 서버 오류: 500 - Internal Server Error");
}
```

#### Green
```java
public class ExternalUserApiClient {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ExternalUserApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
    }
    
    public UserResponse getUser(String userId) {
        try {
            String response = httpClient.get("/users/" + userId);
            return objectMapper.readValue(response, UserResponse.class);
        } catch (HttpException e) {
            handleHttpException(e, userId);
            return null; // 실행되지 않음
        } catch (Exception e) {
            throw new ApiException("응답 파싱 실패", e);
        }
    }
    
    private void handleHttpException(HttpException e, String userId) {
        switch (e.getStatusCode()) {
            case 404:
                throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
            case 500:
            case 502:
            case 503:
                throw new ApiServerException("API 서버 오류: " + e.getStatusCode() + " - " + e.getMessage());
            default:
                throw new ApiException("API 호출 실패: " + e.getStatusCode() + " - " + e.getMessage());
        }
    }
}

public class HttpException extends RuntimeException {
    private final int statusCode;
    
    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

public class ApiServerException extends RuntimeException {
    public ApiServerException(String message) {
        super(message);
    }
}
```

### Step 3: 재시도 메커니즘

#### Red
```java
@Test
void 일시적_오류시_재시도한다() {
    // given
    HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.get("/users/user123"))
        .thenThrow(new HttpException(503, "Service Unavailable"))  // 첫 번째 시도
        .thenThrow(new HttpException(503, "Service Unavailable"))  // 두 번째 시도
        .thenReturn("""
            {
                "id": "user123",
                "name": "John Doe",
                "email": "john@example.com"
            }
            """);  // 세 번째 시도 성공
    
    RetryPolicy retryPolicy = new RetryPolicy(3, Duration.ofMillis(100));
    ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient, retryPolicy);
    
    // when
    UserResponse user = apiClient.getUser("user123");
    
    // then
    assertThat(user.getId()).isEqualTo("user123");
    verify(mockHttpClient, times(3)).get("/users/user123");
}

@Test
void 최대_재시도_횟수_초과시_예외_발생() {
    // given
    HttpClient mockHttpClient = mock(HttpClient.class);
    when(mockHttpClient.get("/users/user123"))
        .thenThrow(new HttpException(503, "Service Unavailable"));
    
    RetryPolicy retryPolicy = new RetryPolicy(2, Duration.ofMillis(10));
    ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient, retryPolicy);
    
    // when & then
    assertThatThrownBy(() -> apiClient.getUser("user123"))
        .isInstanceOf(ApiServerException.class);
    
    verify(mockHttpClient, times(2)).get("/users/user123");
}
```

#### Green
```java
public class ExternalUserApiClient {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RetryPolicy retryPolicy;
    
    public ExternalUserApiClient(HttpClient httpClient, RetryPolicy retryPolicy) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.retryPolicy = retryPolicy;
    }
    
    public UserResponse getUser(String userId) {
        return retryPolicy.execute(() -> doGetUser(userId));
    }
    
    private UserResponse doGetUser(String userId) {
        try {
            String response = httpClient.get("/users/" + userId);
            return objectMapper.readValue(response, UserResponse.class);
        } catch (HttpException e) {
            handleHttpException(e, userId);
            return null;
        } catch (Exception e) {
            throw new ApiException("응답 파싱 실패", e);
        }
    }
    
    private void handleHttpException(HttpException e, String userId) {
        switch (e.getStatusCode()) {
            case 404:
                throw new UserNotFoundException("사용자를 찾을 수 없습니다: " + userId);
            case 500:
            case 502:
            case 503:
                throw new ApiServerException("API 서버 오류: " + e.getStatusCode() + " - " + e.getMessage());
            default:
                throw new ApiException("API 호출 실패: " + e.getStatusCode() + " - " + e.getMessage());
        }
    }
}

public class RetryPolicy {
    
    private final int maxAttempts;
    private final Duration delay;
    
    public RetryPolicy(int maxAttempts, Duration delay) {
        this.maxAttempts = maxAttempts;
        this.delay = delay;
    }
    
    public <T> T execute(Supplier<T> operation) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.get();
            } catch (ApiServerException e) {
                lastException = e;
                
                if (attempt == maxAttempts) {
                    break;
                }
                
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 중 인터럽트 발생", ie);
                }
            }
        }
        
        throw new RuntimeException("최대 재시도 횟수 초과", lastException);
    }
}
```

### Step 4: 응답 캐싱

#### Red
```java
@Test
void 같은_요청은_캐시에서_반환된다() {
    // given
    HttpClient mockHttpClient = mock(HttpClient.class);
    String response = """
        {
            "id": "user123",
            "name": "John Doe",
            "email": "john@example.com"
        }
        """;
    
    when(mockHttpClient.get("/users/user123")).thenReturn(response);
    
    Cache<String, UserResponse> cache = new InMemoryCache<>();
    ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient, cache);
    
    // when
    UserResponse user1 = apiClient.getUser("user123");
    UserResponse user2 = apiClient.getUser("user123");
    
    // then
    assertThat(user1.getId()).isEqualTo("user123");
    assertThat(user2.getId()).isEqualTo("user123");
    assertThat(user1).isSameAs(user2); // 같은 인스턴스
    
    // HTTP 호출은 한 번만
    verify(mockHttpClient, times(1)).get("/users/user123");
}

@Test
void 캐시_만료시_새로_요청한다() throws InterruptedException {
    // given
    HttpClient mockHttpClient = mock(HttpClient.class);
    String response = """
        {
            "id": "user123",
            "name": "John Doe",
            "email": "john@example.com"
        }
        """;
    
    when(mockHttpClient.get("/users/user123")).thenReturn(response);
    
    Cache<String, UserResponse> cache = new InMemoryCache<>(Duration.ofMillis(50));
    ExternalUserApiClient apiClient = new ExternalUserApiClient(mockHttpClient, cache);
    
    // when
    apiClient.getUser("user123");
    Thread.sleep(100); // 캐시 만료 대기
    apiClient.getUser("user123");
    
    // then
    verify(mockHttpClient, times(2)).get("/users/user123");
}
```

#### Green
```java
public class ExternalUserApiClient {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, UserResponse> cache;
    
    public ExternalUserApiClient(HttpClient httpClient, Cache<String, UserResponse> cache) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.cache = cache;
    }
    
    public UserResponse getUser(String userId) {
        String cacheKey = "user:" + userId;
        
        UserResponse cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        UserResponse user = doGetUser(userId);
        cache.put(cacheKey, user);
        
        return user;
    }
    
    private UserResponse doGetUser(String userId) {
        try {
            String response = httpClient.get("/users/" + userId);
            return objectMapper.readValue(response, UserResponse.class);
        } catch (HttpException e) {
            handleHttpException(e, userId);
            return null;
        } catch (Exception e) {
            throw new ApiException("응답 파싱 실패", e);
        }
    }
    
    // handleHttpException 메서드...
}

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    void clear();
}

public class InMemoryCache<K, V> implements Cache<K, V> {
    
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final Duration ttl;
    
    public InMemoryCache() {
        this(Duration.ofMinutes(10)); // 기본 10분
    }
    
    public InMemoryCache(Duration ttl) {
        this.ttl = ttl;
    }
    
    @Override
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.getValue();
    }
    
    @Override
    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, Instant.now().plus(ttl)));
    }
    
    @Override
    public void remove(K key) {
        cache.remove(key);
    }
    
    @Override
    public void clear() {
        cache.clear();
    }
    
    private static class CacheEntry<V> {
        private final V value;
        private final Instant expireTime;
        
        public CacheEntry(V value, Instant expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
        
        public V getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return Instant.now().isAfter(expireTime);
        }
    }
}
```

### 학습 포인트
1. **외부 의존성 모킹**: HTTP 클라이언트를 Mock으로 처리
2. **에러 처리 전략**: HTTP 상태 코드별 다른 예외 처리
3. **재시도 패턴**: 일시적 오류에 대한 견고한 처리
4. **캐싱 전략**: 성능 최적화와 테스트 검증

## Case 7: 이벤트 기반 시스템

### 요구사항
- 도메인 이벤트 발행
- 이벤트 핸들러 등록 및 처리
- 비동기 이벤트 처리
- 이벤트 순서 보장

### Step 1: 기본 이벤트 시스템

#### Red
```java
class EventPublisherTest {
    
    @Test
    void 이벤트를_발행하고_핸들러가_처리한다() {
        // given
        EventPublisher eventPublisher = new EventPublisher();
        TestEventHandler handler = new TestEventHandler();
        
        eventPublisher.subscribe(UserRegisteredEvent.class, handler);
        
        UserRegisteredEvent event = new UserRegisteredEvent("user123", "john@example.com");
        
        // when
        eventPublisher.publish(event);
        
        // then
        assertThat(handler.getHandledEvents()).hasSize(1);
        assertThat(handler.getHandledEvents().get(0)).isEqualTo(event);
    }
    
    @Test
    void 여러_핸들러가_같은_이벤트를_처리한다() {
        // given
        EventPublisher eventPublisher = new EventPublisher();
        TestEventHandler handler1 = new TestEventHandler();
        TestEventHandler handler2 = new TestEventHandler();
        
        eventPublisher.subscribe(UserRegisteredEvent.class, handler1);
        eventPublisher.subscribe(UserRegisteredEvent.class, handler2);
        
        UserRegisteredEvent event = new UserRegisteredEvent("user123", "john@example.com");
        
        // when
        eventPublisher.publish(event);
        
        // then
        assertThat(handler1.getHandledEvents()).hasSize(1);
        assertThat(handler2.getHandledEvents()).hasSize(1);
    }
}
```

#### Green
```java
public class EventPublisher {
    
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    
    @SuppressWarnings("unchecked")
    public void publish(Object event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : eventHandlers) {
                handler.handle(event);
            }
        }
    }
}

public interface EventHandler<T> {
    void handle(T event);
}

public class UserRegisteredEvent {
    private final String userId;
    private final String email;
    
    public UserRegisteredEvent(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }
    
    // equals, hashCode, getters...
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegisteredEvent that = (UserRegisteredEvent) o;
        return Objects.equals(userId, that.userId) && Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }
    
    // getters...
}

// 테스트용 핸들러
public class TestEventHandler implements EventHandler<UserRegisteredEvent> {
    
    private final List<UserRegisteredEvent> handledEvents = new ArrayList<>();
    
    @Override
    public void handle(UserRegisteredEvent event) {
        handledEvents.add(event);
    }
    
    public List<UserRegisteredEvent> getHandledEvents() {
        return new ArrayList<>(handledEvents);
    }
}
```

### Step 2: 이벤트 처리 중 예외 상황

#### Red
```java
@Test
void 핸들러_예외가_다른_핸들러에_영향을_주지_않는다() {
    // given
    EventPublisher eventPublisher = new EventPublisher();
    ExceptionThrowingHandler exceptionHandler = new ExceptionThrowingHandler();
    TestEventHandler normalHandler = new TestEventHandler();
    
    eventPublisher.subscribe(UserRegisteredEvent.class, exceptionHandler);
    eventPublisher.subscribe(UserRegisteredEvent.class, normalHandler);
    
    UserRegisteredEvent event = new UserRegisteredEvent("user123", "john@example.com");
    
    // when
    eventPublisher.publish(event);
    
    // then
    assertThat(normalHandler.getHandledEvents()).hasSize(1);
}

@Test
void 핸들러_예외는_기록된다() {
    // given
    EventErrorLogger mockLogger = mock(EventErrorLogger.class);
    EventPublisher eventPublisher = new EventPublisher(mockLogger);
    ExceptionThrowingHandler exceptionHandler = new ExceptionThrowingHandler();
    
    eventPublisher.subscribe(UserRegisteredEvent.class, exceptionHandler);
    
    UserRegisteredEvent event = new UserRegisteredEvent("user123", "john@example.com");
    
    // when
    eventPublisher.publish(event);
    
    // then
    verify(mockLogger).logError(eq(event), any(RuntimeException.class));
}
```

#### Green
```java
public class EventPublisher {
    
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new HashMap<>();
    private final EventErrorLogger errorLogger;
    
    public EventPublisher() {
        this(new NoOpEventErrorLogger());
    }
    
    public EventPublisher(EventErrorLogger errorLogger) {
        this.errorLogger = errorLogger;
    }
    
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    
    @SuppressWarnings("unchecked")
    public void publish(Object event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : eventHandlers) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    errorLogger.logError(event, e);
                }
            }
        }
    }
}

public interface EventErrorLogger {
    void logError(Object event, Exception exception);
}

public class NoOpEventErrorLogger implements EventErrorLogger {
    @Override
    public void logError(Object event, Exception exception) {
        // 아무것도 하지 않음
    }
}

// 테스트용 예외 발생 핸들러
public class ExceptionThrowingHandler implements EventHandler<UserRegisteredEvent> {
    
    @Override
    public void handle(UserRegisteredEvent event) {
        throw new RuntimeException("Handler failed");
    }
}
```

### Step 3: 비동기 이벤트 처리

#### Red
```java
@Test
void 이벤트를_비동기로_처리한다() throws InterruptedException {
    // given
    ExecutorService executor = Executors.newSingleThreadExecutor();
    AsyncEventPublisher eventPublisher = new AsyncEventPublisher(executor);
    
    CountDownLatch latch = new CountDownLatch(1);
    AsyncTestEventHandler handler = new AsyncTestEventHandler(latch);
    
    eventPublisher.subscribe(UserRegisteredEvent.class, handler);
    
    UserRegisteredEvent event = new UserRegisteredEvent("user123", "john@example.com");
    
    // when
    eventPublisher.publish(event);
    
    // then
    boolean completed = latch.await(1, TimeUnit.SECONDS);
    assertThat(completed).isTrue();
    assertThat(handler.getHandledEvents()).hasSize(1);
    
    executor.shutdown();
}

@Test
void 메인_스레드는_이벤트_처리를_기다리지_않는다() {
    // given
    ExecutorService executor = Executors.newSingleThreadExecutor();
    AsyncEventPublisher eventPublisher = new AsyncEventPublisher(executor);
    
    SlowEventHandler slowHandler = new SlowEventHandler(Duration.ofSeconds(2));
    eventPublisher.subscribe(UserRegisteredEvent.class, slowHandler);
    
    UserRegisteredEvent event = new UserRegisteredEvent("user123", "john@example.com");
    
    // when
    long startTime = System.currentTimeMillis();
    eventPublisher.publish(event);
    long endTime = System.currentTimeMillis();
    
    // then
    assertThat(endTime - startTime).isLessThan(100); // 즉시 반환
    
    executor.shutdown();
}
```

#### Green
```java
public class AsyncEventPublisher {
    
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new HashMap<>();
    private final ExecutorService executorService;
    private final EventErrorLogger errorLogger;
    
    public AsyncEventPublisher(ExecutorService executorService) {
        this(executorService, new NoOpEventErrorLogger());
    }
    
    public AsyncEventPublisher(ExecutorService executorService, EventErrorLogger errorLogger) {
        this.executorService = executorService;
        this.errorLogger = errorLogger;
    }
    
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    
    @SuppressWarnings("unchecked")
    public void publish(Object event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : eventHandlers) {
                executorService.submit(() -> {
                    try {
                        handler.handle(event);
                    } catch (Exception e) {
                        errorLogger.logError(event, e);
                    }
                });
            }
        }
    }
}

// 테스트용 비동기 핸들러
public class AsyncTestEventHandler implements EventHandler<UserRegisteredEvent> {
    
    private final List<UserRegisteredEvent> handledEvents = new ArrayList<>();
    private final CountDownLatch latch;
    
    public AsyncTestEventHandler(CountDownLatch latch) {
        this.latch = latch;
    }
    
    @Override
    public void handle(UserRegisteredEvent event) {
        handledEvents.add(event);
        latch.countDown();
    }
    
    public List<UserRegisteredEvent> getHandledEvents() {
        return new ArrayList<>(handledEvents);
    }
}

// 테스트용 느린 핸들러
public class SlowEventHandler implements EventHandler<UserRegisteredEvent> {
    
    private final Duration delay;
    
    public SlowEventHandler(Duration delay) {
        this.delay = delay;
    }
    
    @Override
    public void handle(UserRegisteredEvent event) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Step 4: 이벤트 순서 보장

#### Red
```java
@Test
void 같은_사용자_이벤트는_순서대로_처리된다() throws InterruptedException {
    // given
    ExecutorService executor = Executors.newFixedThreadPool(3);
    OrderedEventPublisher eventPublisher = new OrderedEventPublisher(executor);
    
    CountDownLatch latch = new CountDownLatch(3);
    OrderedEventHandler handler = new OrderedEventHandler(latch);
    
    eventPublisher.subscribe(UserEvent.class, handler);
    
    // when - 같은 사용자의 이벤트들을 빠르게 발행
    eventPublisher.publish(new UserLoginEvent("user123", 1));
    eventPublisher.publish(new UserUpdateEvent("user123", 2));
    eventPublisher.publish(new UserLogoutEvent("user123", 3));
    
    // then
    boolean completed = latch.await(2, TimeUnit.SECONDS);
    assertThat(completed).isTrue();
    
    List<Integer> processedOrder = handler.getProcessedOrder();
    assertThat(processedOrder).containsExactly(1, 2, 3);
    
    executor.shutdown();
}
```

#### Green
```java
public class OrderedEventPublisher {
    
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new HashMap<>();
    private final ExecutorService executorService;
    private final Map<String, ExecutorService> userExecutors = new ConcurrentHashMap<>();
    
    public OrderedEventPublisher(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    @SuppressWarnings("unchecked")
    public <T> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    
    @SuppressWarnings("unchecked")
    public void publish(Object event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            if (event instanceof UserEvent) {
                publishUserEvent((UserEvent) event, eventHandlers);
            } else {
                publishGeneralEvent(event, eventHandlers);
            }
        }
    }
    
    private void publishUserEvent(UserEvent event, List<EventHandler<?>> eventHandlers) {
        String userId = event.getUserId();
        ExecutorService userExecutor = userExecutors.computeIfAbsent(userId, 
            k -> Executors.newSingleThreadExecutor());
        
        for (EventHandler handler : eventHandlers) {
            userExecutor.submit(() -> handler.handle(event));
        }
    }
    
    private void publishGeneralEvent(Object event, List<EventHandler<?>> eventHandlers) {
        for (EventHandler handler : eventHandlers) {
            executorService.submit(() -> handler.handle(event));
        }
    }
}

public interface UserEvent {
    String getUserId();
    int getSequence();
}

public class UserLoginEvent implements UserEvent {
    private final String userId;
    private final int sequence;
    
    public UserLoginEvent(String userId, int sequence) {
        this.userId = userId;
        this.sequence = sequence;
    }
    
    @Override
    public String getUserId() {
        return userId;
    }
    
    @Override
    public int getSequence() {
        return sequence;
    }
}

public class UserUpdateEvent implements UserEvent {
    private final String userId;
    private final int sequence;
    
    public UserUpdateEvent(String userId, int sequence) {
        this.userId = userId;
        this.sequence = sequence;
    }
    
    @Override
    public String getUserId() {
        return userId;
    }
    
    @Override
    public int getSequence() {
        return sequence;
    }
}

public class UserLogoutEvent implements UserEvent {
    private final String userId;
    private final int sequence;
    
    public UserLogoutEvent(String userId, int sequence) {
        this.userId = userId;
        this.sequence = sequence;
    }
    
    @Override
    public String getUserId() {
        return userId;
    }
    
    @Override
    public int getSequence() {
        return sequence;
    }
}

// 테스트용 순서 확인 핸들러
public class OrderedEventHandler implements EventHandler<UserEvent> {
    
    private final List<Integer> processedOrder = Collections.synchronizedList(new ArrayList<>());
    private final CountDownLatch latch;
    
    public OrderedEventHandler(CountDownLatch latch) {
        this.latch = latch;
    }
    
    @Override
    public void handle(UserEvent event) {
        processedOrder.add(event.getSequence());
        latch.countDown();
    }
    
    public List<Integer> getProcessedOrder() {
        return new ArrayList<>(processedOrder);
    }
}
```

### 학습 포인트
1. **이벤트 주도 아키텍처**: 느슨한 결합을 통한 시스템 설계
2. **예외 격리**: 하나의 핸들러 실패가 다른 핸들러에 영향을 주지 않도록 처리
3. **비동기 처리**: ExecutorService를 활용한 비동기 이벤트 처리
4. **순서 보장**: 특정 키(사용자 ID)별로 이벤트 순서 보장

## 실전 팁과 학습 포인트

### 1. TDD 사이클 실천
- **Red**: 실패하는 테스트 먼저 작성, 요구사항을 명확히 정의
- **Green**: 최소한의 구현으로 테스트 통과, 하드코딩도 괜찮음
- **Refactor**: 중복 제거와 설계 개선, 테스트는 여전히 통과해야 함

### 2. 테스트 작성 순서
1. **Happy Path** 먼저 구현
2. **Edge Case**와 예외 상황 추가
3. **Business Rule** 검증
4. **Integration** 테스트로 확장

### 3. Mock 사용 가이드라인
- **외부 의존성**만 Mock (Database, File System, External API)
- **Value Object**는 실제 객체 사용
- **과도한 Mocking** 피하기

### 4. 테스트 데이터 관리
- **Test Data Builder** 패턴 활용
- **Magic Number** 피하고 의미 있는 상수 사용
- **독립적인 테스트** 데이터로 격리

### 5. 리팩토링 전략
- **한 번에 하나씩** 개선
- **테스트가 보호하는** 상태에서 진행
- **의미 있는 개선**에 집중

TDD는 단순한 테스트 작성 기법이 아니라 **설계 방법론**입니다. 이 케이스 스터디들을 통해 실제 프로젝트에서 TDD를 효과적으로 적용할 수 있는 능력을 기르시기 바랍니다.