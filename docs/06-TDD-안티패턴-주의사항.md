# TDD 안티패턴 및 주의사항

## 목차
1. [TDD 안티패턴 개요](#tdd-안티패턴-개요)
2. [테스트 작성 안티패턴](#테스트-작성-안티패턴)
3. [테스트 설계 안티패턴](#테스트-설계-안티패턴)
4. [Mock과 Stub 안티패턴](#mock과-stub-안티패턴)
5. [테스트 데이터 안티패턴](#테스트-데이터-안티패턴)
6. [테스트 구조 안티패턴](#테스트-구조-안티패턴)
7. [성능 관련 안티패턴](#성능-관련-안티패턴)
8. [TDD 프로세스 안티패턴](#tdd-프로세스-안티패턴)
9. [팀 차원의 안티패턴](#팀-차원의-안티패턴)
10. [안티패턴 해결 전략](#안티패턴-해결-전략)

## TDD 안티패턴 개요

### 안티패턴이란?
안티패턴(Anti-pattern)은 처음에는 효과적으로 보이지만 실제로는 문제를 더 악화시키는 해결책을 의미합니다. TDD에서의 안티패턴은 테스트는 작성하지만 TDD의 진정한 가치를 얻지 못하게 하는 패턴들입니다.

### TDD 안티패턴의 특징
- **가짜 안전감**: 테스트가 있지만 실제로는 버그를 잡지 못함
- **높은 유지보수 비용**: 테스트가 코드 변경을 방해함
- **느린 피드백**: 테스트 실행이 너무 오래 걸림
- **잘못된 설계 유도**: 테스트 때문에 나쁜 설계가 강화됨

## 테스트 작성 안티패턴

### 1. 테스트를 위한 테스트 (Testing for Coverage)

#### ❌ 안티패턴
```java
// 단순히 커버리지를 높이기 위한 무의미한 테스트
@Test
void testGetter() {
    User user = new User("john@example.com");
    assertEquals("john@example.com", user.getEmail());
}

@Test
void testSetter() {
    User user = new User();
    user.setEmail("john@example.com");
    assertEquals("john@example.com", user.getEmail());
}

@Test
void testConstructor() {
    User user = new User("john@example.com", "John");
    assertNotNull(user);  // 의미 없는 검증
}

@Test
void testToString() {
    User user = new User("john@example.com", "John");
    assertNotNull(user.toString());  // 문자열 내용 검증 없음
}
```

#### ✅ 개선된 패턴
```java
// 비즈니스 의미가 있는 행동을 테스트
@Test
void 사용자_이메일_형식_검증() {
    // given
    String invalidEmail = "invalid-email";
    
    // when & then
    assertThrows(IllegalArgumentException.class, () -> {
        new User(invalidEmail, "John");
    });
}

@Test
void 사용자_상태_변경_시_알림_발송() {
    // given
    User user = new User("john@example.com", "John");
    NotificationService mockNotificationService = mock(NotificationService.class);
    UserService userService = new UserService(mockNotificationService);
    
    // when
    userService.activateUser(user);
    
    // then
    assertEquals(UserStatus.ACTIVE, user.getStatus());
    verify(mockNotificationService).sendActivationNotification(user);
}
```

### 2. 구현 세부사항 테스트 (Testing Implementation Details)

#### ❌ 안티패턴
```java
// 내부 구현에 의존하는 테스트
@Test
void 사용자_저장_시_내부_메서드_호출() {
    UserService userService = spy(new UserService());
    User user = new User("john@example.com", "John");
    
    userService.saveUser(user);
    
    // 내부 메서드 호출을 검증 (구현 세부사항)
    verify(userService).validateUser(user);
    verify(userService).generateUserId(user);
    verify(userService).sendWelcomeEmail(user);
    verify(userService).updateLastLoginTime(user);
}

// 프라이빗 메서드를 억지로 테스트
@Test
void 프라이빗_메서드_테스트() throws Exception {
    UserService userService = new UserService();
    Method privateMethod = UserService.class.getDeclaredMethod("calculateAge", LocalDate.class);
    privateMethod.setAccessible(true);
    
    int age = (int) privateMethod.invoke(userService, LocalDate.of(1990, 1, 1));
    
    assertEquals(33, age);  // 현재 날짜에 의존
}
```

#### ✅ 개선된 패턴
```java
// 공개 인터페이스와 비즈니스 결과에 집중
@Test
void 사용자_저장_시_정상_결과_반환() {
    UserService userService = new UserService();
    User user = new User("john@example.com", "John");
    
    SaveResult result = userService.saveUser(user);
    
    // 비즈니스 결과에 집중
    assertTrue(result.isSuccessful());
    assertNotNull(result.getUserId());
    assertEquals("john@example.com", result.getUser().getEmail());
}

@Test
void 미성년자_사용자_생성_제한() {
    UserService userService = new UserService();
    
    // 나이 계산 로직은 공개 메서드를 통해 간접적으로 테스트
    assertThrows(UnderageException.class, () -> {
        userService.createUser("child@example.com", "Child", LocalDate.now().minusYears(10));
    });
}
```

### 3. 과도한 테스트 (Over-Testing)

#### ❌ 안티패턴
```java
// 모든 가능한 조합을 테스트하려는 시도
@Test
void 모든_가능한_할인_조합_테스트() {
    DiscountCalculator calculator = new DiscountCalculator();
    
    // 불필요하게 세분화된 테스트
    assertEquals(90, calculator.calculatePrice(100, CustomerType.VIP, SeasonType.SPRING, DayType.WEEKDAY));
    assertEquals(92, calculator.calculatePrice(100, CustomerType.VIP, SeasonType.SPRING, DayType.WEEKEND));
    assertEquals(88, calculator.calculatePrice(100, CustomerType.VIP, SeasonType.SUMMER, DayType.WEEKDAY));
    assertEquals(90, calculator.calculatePrice(100, CustomerType.VIP, SeasonType.SUMMER, DayType.WEEKEND));
    // ... 수십 개의 조합 테스트
}

// 이미 잘 테스트된 라이브러리 기능까지 테스트
@Test
void LocalDateTime_더하기_기능_테스트() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime later = now.plusHours(1);
    
    assertTrue(later.isAfter(now));  // Java 표준 라이브러리 테스트
}
```

#### ✅ 개선된 패턴
```java
// 중요한 비즈니스 케이스에 집중
@ParameterizedTest
@CsvSource({
    "VIP, 100, 80",      // VIP 20% 할인
    "PREMIUM, 100, 90",  // 프리미엄 10% 할인
    "REGULAR, 100, 100"  // 일반 할인 없음
})
void 고객_등급별_기본_할인율(CustomerType type, int originalPrice, int expectedPrice) {
    DiscountCalculator calculator = new DiscountCalculator();
    
    int actualPrice = calculator.calculatePrice(originalPrice, type);
    
    assertEquals(expectedPrice, actualPrice);
}

@Test
void 특별_프로모션_기간_추가_할인() {
    DiscountCalculator calculator = new DiscountCalculator();
    LocalDate promotionDate = LocalDate.of(2023, 12, 25);  // 크리스마스
    
    int price = calculator.calculatePriceWithPromotion(100, CustomerType.REGULAR, promotionDate);
    
    assertEquals(85, price);  // 크리스마스 15% 추가 할인
}
```

## 테스트 설계 안티패턴

### 1. 상호 의존적 테스트 (Interdependent Tests)

#### ❌ 안티패턴
```java
// 테스트 간 의존성이 있는 설계
class InterdependentTestExample {
    
    private static User createdUser;
    private static Order createdOrder;
    
    @Test
    @Order(1)
    void step1_사용자_생성() {
        createdUser = userService.createUser("john@example.com", "John");
        assertNotNull(createdUser.getId());
    }
    
    @Test
    @Order(2) 
    void step2_주문_생성() {
        // 이전 테스트에 의존
        createdOrder = orderService.createOrder(createdUser.getId(), "product1");
        assertNotNull(createdOrder.getId());
    }
    
    @Test
    @Order(3)
    void step3_주문_완료() {
        // 이전 테스트들에 의존
        orderService.completeOrder(createdOrder.getId());
        assertEquals(OrderStatus.COMPLETED, createdOrder.getStatus());
    }
}
```

#### ✅ 개선된 패턴
```java
// 독립적인 테스트 설계
class IndependentTestExample {
    
    @Test
    void 사용자_생성_테스트() {
        User user = userService.createUser("john@example.com", "John");
        assertNotNull(user.getId());
    }
    
    @Test
    void 주문_생성_테스트() {
        // 독립적으로 사용자 생성
        User user = createTestUser();
        
        Order order = orderService.createOrder(user.getId(), "product1");
        assertNotNull(order.getId());
    }
    
    @Test
    void 주문_완료_테스트() {
        // 독립적으로 주문 생성
        Order order = createTestOrder();
        
        orderService.completeOrder(order.getId());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }
    
    private User createTestUser() {
        return userService.createUser("test@example.com", "Test User");
    }
    
    private Order createTestOrder() {
        User user = createTestUser();
        return orderService.createOrder(user.getId(), "test-product");
    }
}
```

### 2. 신비한 게스트 (Mystery Guest)

#### ❌ 안티패턴
```java
// 테스트가 외부 데이터에 의존
@Test
void 관리자_권한_테스트() {
    // 데이터베이스나 외부 파일의 특정 사용자에 의존
    User admin = userRepository.findByEmail("admin@company.com");  // 이 사용자가 존재한다고 가정
    
    boolean hasPermission = permissionService.hasAdminPermission(admin);
    
    assertTrue(hasPermission);
}

// 외부 파일에 의존하는 테스트
@Test
void 설정_파일_읽기_테스트() {
    // config.properties 파일이 특정 내용을 가지고 있다고 가정
    ConfigService configService = new ConfigService();
    
    String dbUrl = configService.getDatabaseUrl();
    
    assertEquals("jdbc:mysql://localhost:3306/testdb", dbUrl);
}
```

#### ✅ 개선된 패턴
```java
// 테스트 내에서 모든 데이터를 생성
@Test
void 관리자_권한_테스트() {
    // 테스트 내에서 명시적으로 관리자 생성
    User admin = User.builder()
        .email("test.admin@example.com")
        .role(Role.ADMIN)
        .permissions(Arrays.asList(Permission.USER_MANAGEMENT, Permission.SYSTEM_ADMIN))
        .build();
    
    boolean hasPermission = permissionService.hasAdminPermission(admin);
    
    assertTrue(hasPermission);
}

// Mock을 사용하여 외부 의존성 제거
@Test
void 설정_파일_읽기_테스트() {
    Properties mockProperties = new Properties();
    mockProperties.setProperty("database.url", "jdbc:mysql://localhost:3306/testdb");
    
    ConfigService configService = new ConfigService(mockProperties);
    
    String dbUrl = configService.getDatabaseUrl();
    
    assertEquals("jdbc:mysql://localhost:3306/testdb", dbUrl);
}
```

### 3. 거대한 테스트 (Bloated Test)

#### ❌ 안티패턴
```java
@Test
void 전체_주문_프로세스_테스트() {
    // 너무 많은 것을 한 번에 테스트
    
    // 사용자 생성
    User user = new User("john@example.com", "John");
    userRepository.save(user);
    
    // 상품 생성
    Product product1 = new Product("laptop", 1000000);
    Product product2 = new Product("mouse", 30000);
    productRepository.save(product1);
    productRepository.save(product2);
    
    // 장바구니에 추가
    Cart cart = new Cart(user.getId());
    cart.addItem(product1.getId(), 2);
    cart.addItem(product2.getId(), 1);
    cartRepository.save(cart);
    
    // 주문 생성
    Order order = orderService.createOrderFromCart(cart.getId());
    
    // 결제 처리
    PaymentRequest paymentRequest = new PaymentRequest(order.getId(), "CREDIT_CARD");
    PaymentResult paymentResult = paymentService.processPayment(paymentRequest);
    
    // 재고 차감
    inventoryService.reduceStock(product1.getId(), 2);
    inventoryService.reduceStock(product2.getId(), 1);
    
    // 배송 처리
    ShippingRequest shippingRequest = new ShippingRequest(order.getId(), user.getAddress());
    ShippingResult shippingResult = shippingService.scheduleShipping(shippingRequest);
    
    // 이메일 발송
    emailService.sendOrderConfirmation(user.getEmail(), order.getId());
    
    // 모든 것을 한 번에 검증
    assertNotNull(order.getId());
    assertTrue(paymentResult.isSuccessful());
    assertEquals(OrderStatus.COMPLETED, order.getStatus());
    assertTrue(shippingResult.isScheduled());
    assertEquals(8, product1.getStockQuantity());
    assertEquals(19, product2.getStockQuantity());
}
```

#### ✅ 개선된 패턴
```java
// 단일 책임의 작은 테스트들로 분리
@Test
void 주문_생성_테스트() {
    User user = createTestUser();
    List<CartItem> items = Arrays.asList(
        new CartItem("product1", 2),
        new CartItem("product2", 1)
    );
    
    Order order = orderService.createOrder(user.getId(), items);
    
    assertNotNull(order.getId());
    assertEquals(user.getId(), order.getCustomerId());
    assertEquals(2, order.getItems().size());
}

@Test
void 결제_처리_테스트() {
    Order order = createTestOrder();
    PaymentRequest request = new PaymentRequest(order.getId(), "CREDIT_CARD");
    
    PaymentResult result = paymentService.processPayment(request);
    
    assertTrue(result.isSuccessful());
    assertNotNull(result.getTransactionId());
}

@Test
void 재고_차감_테스트() {
    Product product = createTestProduct("laptop", 10);
    
    inventoryService.reduceStock(product.getId(), 3);
    
    Product updatedProduct = productRepository.findById(product.getId());
    assertEquals(7, updatedProduct.getStockQuantity());
}
```

## Mock과 Stub 안티패턴

### 1. 과도한 모킹 (Excessive Mocking)

#### ❌ 안티패턴
```java
@Test
void 과도한_모킹_예시() {
    // 모든 의존성을 Mock으로 처리
    UserRepository mockUserRepository = mock(UserRepository.class);
    EmailService mockEmailService = mock(EmailService.class);
    PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);
    TimeService mockTimeService = mock(TimeService.class);
    IdGenerator mockIdGenerator = mock(IdGenerator.class);
    ValidationService mockValidationService = mock(ValidationService.class);
    AuditService mockAuditService = mock(AuditService.class);
    
    UserService userService = new UserService(
        mockUserRepository, mockEmailService, mockPasswordEncoder,
        mockTimeService, mockIdGenerator, mockValidationService, mockAuditService
    );
    
    // 과도한 stubbing
    when(mockValidationService.validateEmail(any())).thenReturn(true);
    when(mockValidationService.validatePassword(any())).thenReturn(true);
    when(mockPasswordEncoder.encode(any())).thenReturn("encoded_password");
    when(mockTimeService.getCurrentTime()).thenReturn(LocalDateTime.now());
    when(mockIdGenerator.generateId()).thenReturn("user_123");
    when(mockUserRepository.save(any())).thenReturn(new User("user_123", "john@example.com"));
    
    User user = userService.createUser("john@example.com", "password");
    
    // 실제로는 Mock 객체들만 테스트하고 있음
    assertEquals("user_123", user.getId());
}
```

#### ✅ 개선된 패턴
```java
@Test
void 적절한_모킹_예시() {
    // 외부 의존성만 Mock으로 처리
    UserRepository mockUserRepository = mock(UserRepository.class);
    EmailService mockEmailService = mock(EmailService.class);
    
    // 단순한 유틸리티는 실제 객체 사용
    PasswordEncoder realPasswordEncoder = new BCryptPasswordEncoder();
    IdGenerator realIdGenerator = new UUIDGenerator();
    
    UserService userService = new UserService(
        mockUserRepository, mockEmailService, realPasswordEncoder, realIdGenerator
    );
    
    // 핵심 외부 의존성만 stubbing
    when(mockUserRepository.save(any())).thenAnswer(invocation -> {
        User user = invocation.getArgument(0);
        return user;  // 저장된 사용자 반환
    });
    
    User user = userService.createUser("john@example.com", "password");
    
    // 실제 비즈니스 로직 검증
    assertNotNull(user.getId());
    assertEquals("john@example.com", user.getEmail());
    assertTrue(realPasswordEncoder.matches("password", user.getEncodedPassword()));
    verify(mockEmailService).sendWelcomeEmail("john@example.com");
}
```

### 2. 모킹된 객체 리턴 (Mocking Concrete Classes)

#### ❌ 안티패턴
```java
@Test
void 구체_클래스_모킹() {
    // 구체 클래스를 직접 모킹
    OrderCalculator mockCalculator = mock(OrderCalculator.class);
    when(mockCalculator.calculateTotal(any())).thenReturn(100.0);
    when(mockCalculator.calculateTax(any())).thenReturn(10.0);
    
    OrderService orderService = new OrderService(mockCalculator);
    
    Order order = orderService.processOrder(orderRequest);
    
    // 실제 계산 로직이 테스트되지 않음
    assertEquals(110.0, order.getTotal());
}

// 값 객체를 Mock으로 만드는 실수
@Test
void 값_객체_모킹() {
    Money mockMoney = mock(Money.class);
    when(mockMoney.getAmount()).thenReturn(BigDecimal.valueOf(100));
    when(mockMoney.getCurrency()).thenReturn("USD");
    
    // Money는 값 객체이므로 Mock 대신 실제 객체를 사용해야 함
    PaymentProcessor processor = new PaymentProcessor();
    processor.processPayment(mockMoney);
}
```

#### ✅ 개선된 패턴
```java
@Test
void 인터페이스_기반_테스트() {
    // 인터페이스 정의
    interface PriceCalculator {
        double calculateTotal(Order order);
        double calculateTax(Order order);
    }
    
    // 테스트에서는 인터페이스를 Mock
    PriceCalculator mockCalculator = mock(PriceCalculator.class);
    when(mockCalculator.calculateTotal(any())).thenReturn(100.0);
    when(mockCalculator.calculateTax(any())).thenReturn(10.0);
    
    OrderService orderService = new OrderService(mockCalculator);
    
    Order order = orderService.processOrder(orderRequest);
    
    assertEquals(110.0, order.getTotal());
}

@Test
void 실제_값_객체_사용() {
    // 값 객체는 실제 객체 사용
    Money money = new Money(BigDecimal.valueOf(100), "USD");
    
    PaymentProcessor processor = new PaymentProcessor();
    PaymentResult result = processor.processPayment(money);
    
    assertTrue(result.isSuccessful());
    assertEquals(BigDecimal.valueOf(100), result.getProcessedAmount().getAmount());
}
```

### 3. 스터빙 남용 (Stubbing Overuse)

#### ❌ 안티패턴
```java
@Test
void 스터빙_남용_예시() {
    UserService mockUserService = mock(UserService.class);
    
    // 모든 메서드를 스터빙
    when(mockUserService.createUser(any(), any())).thenReturn(new User("1", "john@example.com"));
    when(mockUserService.updateUser(any())).thenReturn(new User("1", "john.doe@example.com"));
    when(mockUserService.deleteUser(any())).thenReturn(true);
    when(mockUserService.findUser(any())).thenReturn(new User("1", "john@example.com"));
    when(mockUserService.validateUser(any())).thenReturn(true);
    when(mockUserService.activateUser(any())).thenReturn(true);
    
    UserController controller = new UserController(mockUserService);
    
    // 실제로는 Mock의 동작만 테스트
    ResponseEntity<User> response = controller.createUser(new CreateUserRequest("john@example.com", "John"));
    
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("john@example.com", response.getBody().getEmail());
}
```

#### ✅ 개선된 패턴
```java
@Test
void 최소한의_스터빙() {
    UserService mockUserService = mock(UserService.class);
    
    // 테스트에 필요한 최소한의 스터빙만
    User expectedUser = new User("1", "john@example.com");
    when(mockUserService.createUser("john@example.com", "John")).thenReturn(expectedUser);
    
    UserController controller = new UserController(mockUserService);
    CreateUserRequest request = new CreateUserRequest("john@example.com", "John");
    
    ResponseEntity<User> response = controller.createUser(request);
    
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("john@example.com", response.getBody().getEmail());
    
    // 실제 메서드 호출 검증
    verify(mockUserService).createUser("john@example.com", "John");
}
```

## 테스트 데이터 안티패턴

### 1. 하드코딩된 테스트 데이터 (Hard-coded Test Data)

#### ❌ 안티패턴
```java
@Test
void 하드코딩된_데이터_테스트() {
    // 날짜가 하드코딩됨
    LocalDate fixedDate = LocalDate.of(2023, 1, 1);
    User user = new User("john@example.com", "John", fixedDate);
    
    // 현재 시간에 의존적인 테스트
    int age = user.getAge();
    assertEquals(20, age);  // 2024년이 되면 실패
    
    // 특정 환경에 의존하는 데이터
    String homeDirectory = System.getProperty("user.home");
    String configPath = homeDirectory + "/config/app.properties";
    Configuration config = configLoader.load(configPath);
    
    assertNotNull(config);  // 특정 OS나 사용자에게만 동작
}
```

#### ✅ 개선된 패턴
```java
@Test
void 유연한_테스트_데이터() {
    // 상대적인 날짜 사용
    LocalDate birthDate = LocalDate.now().minusYears(25);
    User user = new User("john@example.com", "John", birthDate);
    
    int age = user.getAge();
    assertEquals(25, age);  // 항상 25세
    
    // 환경에 독립적인 테스트
    Properties testProperties = new Properties();
    testProperties.setProperty("database.url", "jdbc:h2:mem:testdb");
    
    Configuration config = new Configuration(testProperties);
    
    assertEquals("jdbc:h2:mem:testdb", config.getDatabaseUrl());
}

@Test
void Clock_주입으로_시간_제어() {
    // 고정된 시간 사용
    Clock fixedClock = Clock.fixed(
        Instant.parse("2023-12-25T10:00:00Z"), 
        ZoneOffset.UTC
    );
    
    OrderService orderService = new OrderService(fixedClock);
    Order order = orderService.createOrder(orderRequest);
    
    assertEquals(LocalDateTime.of(2023, 12, 25, 10, 0), order.getCreatedAt());
}
```

### 2. 공유 테스트 데이터 (Shared Test Data)

#### ❌ 안티패턴
```java
// 테스트 간 데이터 공유
class SharedDataTest {
    
    private static final User SHARED_USER = new User("shared@example.com", "Shared User");
    private static final Product SHARED_PRODUCT = new Product("shared-product", 1000);
    
    @Test
    void 첫_번째_테스트() {
        // 공유 데이터 수정
        SHARED_USER.setStatus(UserStatus.ACTIVE);
        SHARED_PRODUCT.setPrice(1500);
        
        Order order = orderService.createOrder(SHARED_USER, SHARED_PRODUCT);
        assertEquals(1500, order.getTotal());
    }
    
    @Test
    void 두_번째_테스트() {
        // 이전 테스트의 변경사항에 영향받음
        assertEquals(UserStatus.ACTIVE, SHARED_USER.getStatus());  // 예상과 다를 수 있음
        assertEquals(1500, SHARED_PRODUCT.getPrice());  // 예상과 다를 수 있음
    }
}
```

#### ✅ 개선된 패턴
```java
class IsolatedDataTest {
    
    @Test
    void 첫_번째_테스트() {
        // 각 테스트에서 독립적인 데이터 생성
        User user = createTestUser();
        Product product = createTestProduct();
        
        user.setStatus(UserStatus.ACTIVE);
        product.setPrice(1500);
        
        Order order = orderService.createOrder(user, product);
        assertEquals(1500, order.getTotal());
    }
    
    @Test
    void 두_번째_테스트() {
        // 독립적인 데이터로 예측 가능한 테스트
        User user = createTestUser();
        Product product = createTestProduct();
        
        assertEquals(UserStatus.PENDING, user.getStatus());  // 초기 상태
        assertEquals(1000, product.getPrice());  // 기본 가격
    }
    
    private User createTestUser() {
        return new User("test@example.com", "Test User");
    }
    
    private Product createTestProduct() {
        return new Product("test-product", 1000);
    }
}
```

### 3. 마법의 숫자 (Magic Numbers)

#### ❌ 안티패턴
```java
@Test
void 마법의_숫자_사용() {
    User user = new User("john@example.com", "John");
    
    // 의미를 알 수 없는 숫자들
    user.addPoints(1000);
    user.setPurchaseAmount(50000);
    
    MembershipLevel level = membershipService.calculateLevel(user);
    
    assertEquals(2, level.getValue());  // 2가 무엇을 의미하는지 불명확
    
    double discount = discountService.calculateDiscount(user, 30000);
    assertEquals(3000, discount);  // 계산 근거가 불명확
}
```

#### ✅ 개선된 패턴
```java
@Test
void 의미있는_상수_사용() {
    // 의미있는 상수 정의
    final int GOLD_MEMBERSHIP_REQUIRED_POINTS = 1000;
    final int GOLD_MEMBERSHIP_REQUIRED_PURCHASE = 50000;
    final MembershipLevel EXPECTED_GOLD_LEVEL = MembershipLevel.GOLD;
    final int TEST_PURCHASE_AMOUNT = 30000;
    final double GOLD_DISCOUNT_RATE = 0.1;
    final double EXPECTED_DISCOUNT = TEST_PURCHASE_AMOUNT * GOLD_DISCOUNT_RATE;
    
    User user = new User("john@example.com", "John");
    user.addPoints(GOLD_MEMBERSHIP_REQUIRED_POINTS);
    user.setPurchaseAmount(GOLD_MEMBERSHIP_REQUIRED_PURCHASE);
    
    MembershipLevel level = membershipService.calculateLevel(user);
    assertEquals(EXPECTED_GOLD_LEVEL, level);
    
    double discount = discountService.calculateDiscount(user, TEST_PURCHASE_AMOUNT);
    assertEquals(EXPECTED_DISCOUNT, discount);
}
```

## 테스트 구조 안티패턴

### 1. 단일 거대 테스트 클래스 (Monolithic Test Class)

#### ❌ 안티패턴
```java
// 모든 테스트가 하나의 클래스에 집중
class UserServiceTestMegaClass {
    
    // 사용자 생성 관련 테스트
    @Test void testCreateUser() { }
    @Test void testCreateUserWithInvalidEmail() { }
    @Test void testCreateUserWithDuplicateEmail() { }
    @Test void testCreateUserWithNullName() { }
    
    // 사용자 조회 관련 테스트
    @Test void testFindUser() { }
    @Test void testFindUserNotFound() { }
    @Test void testFindUserByEmail() { }
    @Test void testFindAllUsers() { }
    
    // 사용자 업데이트 관련 테스트
    @Test void testUpdateUser() { }
    @Test void testUpdateUserInvalidData() { }
    @Test void testUpdateUserNotFound() { }
    
    // 사용자 삭제 관련 테스트
    @Test void testDeleteUser() { }
    @Test void testDeleteUserNotFound() { }
    @Test void testDeleteUserWithOrders() { }
    
    // 권한 관련 테스트
    @Test void testCheckPermission() { }
    @Test void testGrantPermission() { }
    @Test void testRevokePermission() { }
    
    // 50개 이상의 테스트 메서드...
}
```

#### ✅ 개선된 패턴
```java
// 기능별로 분리된 테스트 클래스
@DisplayName("사용자 서비스")
class UserServiceTest {
    
    @Nested
    @DisplayName("사용자 생성")
    class UserCreation {
        
        @Test
        @DisplayName("유효한 데이터로 사용자 생성 성공")
        void createUser_WithValidData_Success() { }
        
        @Test
        @DisplayName("잘못된 이메일 형식으로 사용자 생성 실패")
        void createUser_WithInvalidEmail_ThrowsException() { }
        
        @Test
        @DisplayName("중복 이메일로 사용자 생성 실패")
        void createUser_WithDuplicateEmail_ThrowsException() { }
    }
    
    @Nested
    @DisplayName("사용자 조회")
    class UserRetrieval {
        
        @Test
        @DisplayName("ID로 사용자 조회 성공")
        void findUser_WithValidId_ReturnsUser() { }
        
        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외 발생")
        void findUser_WithInvalidId_ThrowsException() { }
    }
}

// 별도의 테스트 클래스로 분리
@DisplayName("사용자 권한 관리")
class UserPermissionTest {
    
    @Test
    @DisplayName("관리자 권한 확인")
    void checkAdminPermission() { }
    
    @Test
    @DisplayName("권한 부여")
    void grantPermission() { }
}
```

### 2. 테스트 메서드명 안티패턴 (Poor Test Naming)

#### ❌ 안티패턴
```java
class PoorTestNamingExample {
    
    @Test
    void test1() { }  // 의미 없는 이름
    
    @Test
    void userTest() { }  // 너무 일반적
    
    @Test
    void testCreateUser() { }  // 무엇을 테스트하는지 불명확
    
    @Test
    void shouldWork() { }  // 모호함
    
    @Test
    void createUserShouldReturnUser() { }  // should는 불필요
}
```

#### ✅ 개선된 패턴
```java
class GoodTestNamingExample {
    
    @Test
    void 유효한_이메일과_이름으로_사용자_생성_성공() { }
    
    @Test
    void 중복된_이메일로_사용자_생성시_예외_발생() { }
    
    @Test
    void 존재하지_않는_사용자_조회시_UserNotFoundException_발생() { }
    
    @Test
    @DisplayName("VIP 고객은 주문 시 10% 할인을 받는다")
    void vipCustomerGets10PercentDiscount() { }
    
    @Test
    @DisplayName("재고가 부족한 상품 주문 시 OutOfStockException이 발생한다")
    void throwsOutOfStockExceptionWhenOrderingUnavailableProduct() { }
}
```

## 성능 관련 안티패턴

### 1. 느린 테스트 (Slow Tests)

#### ❌ 안티패턴
```java
@Test
void 느린_테스트_예시() {
    // 실제 데이터베이스 연결
    try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/realdb", "user", "password")) {
        
        // 대량 데이터 삽입
        for (int i = 0; i < 10000; i++) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (email, name) VALUES (?, ?)");
            stmt.setString(1, "user" + i + "@example.com");
            stmt.setString(2, "User " + i);
            stmt.executeUpdate();
        }
        
        // 복잡한 쿼리 실행
        ResultSet rs = conn.createStatement().executeQuery(
            "SELECT * FROM users u JOIN orders o ON u.id = o.user_id " +
            "WHERE u.created_at > DATE_SUB(NOW(), INTERVAL 1 YEAR)");
        
        assertTrue(rs.next());
    }
    
    // 외부 API 호출
    Thread.sleep(5000);  // 5초 대기
    
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(
        "http://external-api.com/slow-endpoint", String.class);
    
    assertNotNull(result);
}
```

#### ✅ 개선된 패턴
```java
@Test
void 빠른_테스트_예시() {
    // 인메모리 데이터베이스 사용
    UserRepository mockRepository = mock(UserRepository.class);
    
    // 필요한 데이터만 모킹
    List<User> mockUsers = Arrays.asList(
        new User("user1@example.com", "User1"),
        new User("user2@example.com", "User2")
    );
    
    when(mockRepository.findActiveUsersWithOrders()).thenReturn(mockUsers);
    
    UserService userService = new UserService(mockRepository);
    List<User> activeUsers = userService.getActiveUsersWithOrders();
    
    assertEquals(2, activeUsers.size());
    verify(mockRepository).findActiveUsersWithOrders();
}

@Test
void 외부_서비스_모킹() {
    // 외부 API를 Mock으로 대체
    ExternalApiService mockApiService = mock(ExternalApiService.class);
    when(mockApiService.callSlowEndpoint()).thenReturn("mocked response");
    
    ServiceUnderTest service = new ServiceUnderTest(mockApiService);
    String result = service.processWithExternalCall();
    
    assertEquals("processed: mocked response", result);
    verify(mockApiService).callSlowEndpoint();
}
```

### 2. 메모리 누수 테스트 (Memory Leaking Tests)

#### ❌ 안티패턴
```java
class MemoryLeakingTest {
    
    private static List<Object> staticList = new ArrayList<>();  // 정적 컬렉션
    
    @Test
    void 메모리_누수_테스트() {
        // 대량 데이터를 정적 컬렉션에 저장
        for (int i = 0; i < 100000; i++) {
            staticList.add(new LargeObject("data" + i));
        }
        
        // 테스트 종료 후에도 메모리에 남아있음
        assertEquals(100000, staticList.size());
    }
    
    @Test
    void 파일_핸들_누수() throws IOException {
        // 파일을 닫지 않음
        FileInputStream fis = new FileInputStream("test.txt");
        byte[] data = fis.readAllBytes();
        // fis.close() 누락
        
        assertNotNull(data);
    }
}
```

#### ✅ 개선된 패턴
```java
class MemoryEfficientTest {
    
    @Test
    void 메모리_효율적_테스트() {
        // 지역 변수 사용
        List<Object> localList = new ArrayList<>();
        
        for (int i = 0; i < 1000; i++) {  // 적절한 크기
            localList.add(new SmallTestObject("data" + i));
        }
        
        assertEquals(1000, localList.size());
        // 메서드 종료시 자동으로 GC 대상이 됨
    }
    
    @Test
    void 리소스_자동_관리() throws IOException {
        // try-with-resources 사용
        try (FileInputStream fis = new FileInputStream("test.txt")) {
            byte[] data = fis.readAllBytes();
            assertNotNull(data);
        }  // 자동으로 파일 핸들 정리
    }
    
    @AfterEach
    void tearDown() {
        // 테스트 후 명시적 정리
        System.gc();  // 필요한 경우에만
    }
}
```

## TDD 프로세스 안티패턴

### 1. 구현 먼저 작성 (Implementation First)

#### ❌ 안티패턴
```java
// 1. 먼저 구현을 작성
public class PriceCalculator {
    public double calculateDiscount(Customer customer, double amount) {
        if (customer.getType() == CustomerType.VIP) {
            return amount * 0.2;
        } else if (customer.getType() == CustomerType.PREMIUM) {
            return amount * 0.1;
        }
        return 0;
    }
}

// 2. 나중에 테스트 작성 (구현에 맞춰서)
@Test
void testCalculateDiscount() {
    PriceCalculator calculator = new PriceCalculator();
    Customer vipCustomer = new Customer(CustomerType.VIP);
    
    double discount = calculator.calculateDiscount(vipCustomer, 100);
    
    assertEquals(20, discount);  // 구현에 맞춘 테스트
}
```

#### ✅ TDD 올바른 접근
```java
// 1. 먼저 실패하는 테스트 작성
@Test
void VIP_고객은_20퍼센트_할인을_받는다() {
    PriceCalculator calculator = new PriceCalculator();
    Customer vipCustomer = new Customer(CustomerType.VIP);
    
    double discount = calculator.calculateDiscount(vipCustomer, 100);
    
    assertEquals(20, discount);  // 아직 구현 없음 - 컴파일 에러 또는 실패
}

// 2. 최소한의 구현으로 테스트 통과
public class PriceCalculator {
    public double calculateDiscount(Customer customer, double amount) {
        return 20;  // 하드코딩으로 테스트만 통과
    }
}

// 3. 더 많은 테스트 추가 후 일반화
@Test
void 프리미엄_고객은_10퍼센트_할인을_받는다() {
    PriceCalculator calculator = new PriceCalculator();
    Customer premiumCustomer = new Customer(CustomerType.PREMIUM);
    
    double discount = calculator.calculateDiscount(premiumCustomer, 100);
    
    assertEquals(10, discount);  // 이제 하드코딩으로는 안됨
}

// 4. 일반화된 구현
public class PriceCalculator {
    public double calculateDiscount(Customer customer, double amount) {
        if (customer.getType() == CustomerType.VIP) {
            return amount * 0.2;
        } else if (customer.getType() == CustomerType.PREMIUM) {
            return amount * 0.1;
        }
        return 0;
    }
}
```

### 2. 과도한 리팩토링 (Premature Refactoring)

#### ❌ 안티패턴
```java
// Red -> Green 후 바로 복잡한 리팩토링
@Test
void 사용자_생성_테스트() {
    UserService service = new UserService();
    User user = service.createUser("john@example.com", "John");
    assertNotNull(user);
}

// 테스트가 하나만 있는데 바로 복잡한 구조로 리팩토링
public class UserService {
    private final UserFactory userFactory;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    
    // 복잡한 의존성 주입, 디자인 패턴 적용
    public User createUser(String email, String name) {
        UserCreationRequest request = UserCreationRequest.builder()
            .email(email)
            .name(name)
            .build();
            
        userValidator.validate(request);
        User user = userFactory.create(request);
        userRepository.save(user);
        eventPublisher.publish(new UserCreatedEvent(user));
        
        return user;
    }
}
```

#### ✅ 올바른 접근
```java
// 1. 첫 번째 테스트 - 단순한 구현
@Test
void 사용자_생성_테스트() {
    UserService service = new UserService();
    User user = service.createUser("john@example.com", "John");
    assertNotNull(user);
}

public class UserService {
    public User createUser(String email, String name) {
        return new User(email, name);  // 가장 단순한 구현
    }
}

// 2. 더 많은 테스트 추가
@Test
void 잘못된_이메일_형식_예외() {
    UserService service = new UserService();
    assertThrows(IllegalArgumentException.class, () -> {
        service.createUser("invalid-email", "John");
    });
}

// 3. 점진적 개선
public class UserService {
    public User createUser(String email, String name) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        return new User(email, name);
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@");  // 단순한 검증
    }
}

// 4. 필요에 따라서만 리팩토링
```

### 3. 테스트 스킵 (Skipping Tests)

#### ❌ 안티패턴
```java
// "간단한 기능이니까 테스트 없이 구현"
public class SimpleCalculator {
    public int add(int a, int b) {
        return a + b;  // 간단해 보이지만...
    }
    
    public double divide(double a, double b) {
        return a / b;  // 0으로 나누기 예외 처리 없음
    }
    
    public int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);  // 음수 입력 처리 없음, 스택 오버플로우 가능
    }
}

// "시간이 없어서 나중에 테스트 작성"
public class PaymentProcessor {
    public PaymentResult processPayment(PaymentRequest request) {
        // 복잡한 결제 로직
        // 테스트 없이 프로덕션 배포
        return new PaymentResult(true);
    }
}
```

#### ✅ 올바른 접근
```java
// 간단해 보이는 기능도 테스트 먼저
@Test
void 두_숫자_더하기() {
    SimpleCalculator calc = new SimpleCalculator();
    assertEquals(5, calc.add(2, 3));
}

@Test
void 영으로_나누기_예외() {
    SimpleCalculator calc = new SimpleCalculator();
    assertThrows(ArithmeticException.class, () -> {
        calc.divide(10, 0);
    });
}

@Test
void 음수_팩토리얼_예외() {
    SimpleCalculator calc = new SimpleCalculator();
    assertThrows(IllegalArgumentException.class, () -> {
        calc.factorial(-1);
    });
}

// 복잡한 기능은 더욱 철저히 테스트
@Test
void 결제_처리_성공() {
    PaymentProcessor processor = new PaymentProcessor();
    PaymentRequest request = new PaymentRequest(100, "CREDIT_CARD");
    
    PaymentResult result = processor.processPayment(request);
    
    assertTrue(result.isSuccessful());
}

@Test
void 잘못된_카드_정보_결제_실패() {
    PaymentProcessor processor = new PaymentProcessor();
    PaymentRequest request = new PaymentRequest(100, "INVALID_CARD");
    
    PaymentResult result = processor.processPayment(request);
    
    assertFalse(result.isSuccessful());
    assertEquals("Invalid card information", result.getErrorMessage());
}
```

## 팀 차원의 안티패턴

### 1. 테스트 책임 회피 (Test Ownership Avoidance)

#### ❌ 안티패턴
```java
// "나는 기능 개발만, 테스트는 QA팀이"
public class OrderService {
    public Order createOrder(CreateOrderRequest request) {
        // 복잡한 비즈니스 로직
        // 테스트 없이 개발
        return new Order();
    }
}

// "레거시 코드라서 테스트 추가 안함"
public class LegacyUserService {
    public void updateUserStatus(String userId, String status) {
        // 복잡하고 위험한 레거시 로직
        // "건드리면 망가질까봐" 테스트 추가 안함
    }
}

// "시간 없어서 테스트는 나중에"
public class FeatureService {
    public void newFeature() {
        // 새로운 기능 구현
        // 데드라인 때문에 테스트 스킵
    }
}
```

#### ✅ 개선된 접근
```java
// 개발자가 직접 테스트 작성
@Test
void 주문_생성_성공() {
    OrderService orderService = new OrderService();
    CreateOrderRequest request = new CreateOrderRequest("user1", "product1");
    
    Order order = orderService.createOrder(request);
    
    assertNotNull(order.getId());
    assertEquals("user1", order.getUserId());
}

// 레거시 코드도 점진적으로 테스트 추가
@Test
void 사용자_상태_업데이트_정상_동작() {
    LegacyUserService service = new LegacyUserService();
    
    // 현재 동작을 테스트로 고정 (Characterization Test)
    assertDoesNotThrow(() -> {
        service.updateUserStatus("user1", "ACTIVE");
    });
    
    // 점진적으로 더 상세한 테스트 추가
}

// 새 기능은 TDD로 개발
@Test
void 새로운_기능_요구사항_검증() {
    FeatureService service = new FeatureService();
    
    // 먼저 테스트로 요구사항 명확히 정의
    FeatureResult result = service.newFeature();
    
    assertTrue(result.isSuccessful());
    assertEquals("expected_outcome", result.getOutcome());
}
```

### 2. 테스트 무시 문화 (Test Ignorance Culture)

#### ❌ 안티패턴
```java
// 실패하는 테스트를 @Disabled로 숨기기
@Test
@Disabled("가끔 실패해서 일단 비활성화")
void 불안정한_테스트() {
    // 때때로 실패하는 테스트
    // 원인 파악하지 않고 숨김
}

// 테스트 실패 시 테스트 수정 (구현이 아닌)
@Test
void 사용자_할인율_계산() {
    User user = new User(CustomerType.VIP);
    
    double discount = discountService.calculateDiscount(user, 100);
    
    // 구현이 10% 할인으로 변경됨
    // 테스트를 수정해서 맞춤 (원래는 20%였음)
    assertEquals(10, discount);  // 요구사항과 다를 수 있음
}

// CI에서 테스트 실패 무시
// build.gradle
test {
    ignoreFailures = true  // 테스트 실패해도 빌드 성공으로 처리
}
```

#### ✅ 개선된 문화
```java
// 실패하는 테스트의 원인 파악하고 수정
@Test
void 안정적인_테스트() {
    // given - 명확한 전제 조건
    User user = new User(CustomerType.VIP);
    Clock fixedClock = Clock.fixed(Instant.parse("2023-01-01T10:00:00Z"), ZoneOffset.UTC);
    DiscountService service = new DiscountService(fixedClock);
    
    // when
    double discount = service.calculateDiscount(user, 100);
    
    // then
    assertEquals(20, discount);  // 요구사항에 맞는 검증
}

// 테스트가 실패하면 구현 검토
@Test
void 요구사항_기반_검증() {
    // 비즈니스 요구사항: VIP 고객은 20% 할인
    User vipUser = new User(CustomerType.VIP);
    
    double discount = discountService.calculateDiscount(vipUser, 100);
    
    assertEquals(20, discount);  // 요구사항이 변경되지 않았다면 이 테스트가 맞음
}

// CI 설정에서 테스트 실패 시 빌드 실패
// build.gradle
test {
    useJUnitPlatform()
    testLogging {
        events "passed", "skipped", "failed"
    }
    // ignoreFailures = false (기본값, 명시적으로 설정 안함)
}
```

## 안티패턴 해결 전략

### 1. 코드 리뷰를 통한 안티패턴 방지

#### 체크리스트 활용
```java
// 코드 리뷰 체크리스트 예시
public class CodeReviewChecklist {
    
    /*
     * 테스트 작성 체크리스트:
     * 
     * ✅ 테스트가 단일 책임을 가지는가?
     * ✅ 테스트 이름이 의도를 명확히 표현하는가?
     * ✅ Given-When-Then 구조를 따르는가?
     * ✅ 외부 의존성을 적절히 Mock했는가?
     * ✅ 테스트 데이터가 하드코딩되지 않았는가?
     * ✅ 구현 세부사항이 아닌 행동을 테스트하는가?
     * ✅ 테스트가 빠르게 실행되는가?
     * ✅ 테스트가 다른 테스트와 독립적인가?
     */
    
    @Test
    @DisplayName("유효한 이메일로 사용자 생성 시 환영 이메일이 발송된다")
    void createUser_WithValidEmail_SendsWelcomeEmail() {
        // given
        String email = "john@example.com";
        String name = "John Doe";
        EmailService mockEmailService = mock(EmailService.class);
        UserService userService = new UserService(mockEmailService);
        
        // when
        User user = userService.createUser(email, name);
        
        // then
        assertThat(user.getEmail()).isEqualTo(email);
        verify(mockEmailService).sendWelcomeEmail(email);
    }
}
```

### 2. 정적 분석 도구 활용

#### SpotBugs, SonarQube 설정
```gradle
// build.gradle
plugins {
    id 'com.github.spotbugs' version '5.0.14'
    id 'org.sonarqube' version '4.0.0.2929'
}

spotbugs {
    ignoreFailures = false
    effort = 'max'
    reportLevel = 'medium'
}

sonarqube {
    properties {
        property 'sonar.projectKey', 'my-project'
        property 'sonar.coverage.jacoco.xmlReportPaths', 'build/reports/jacoco/test/jacocoTestReport.xml'
        property 'sonar.test.exclusions', '**/*Test.java'
        
        // 테스트 품질 규칙
        property 'sonar.java.test.binaries', 'build/classes/java/test'
        property 'sonar.junit.reportPaths', 'build/test-results/test'
    }
}
```

### 3. 팀 교육 및 가이드라인

#### 팀 TDD 가이드라인 문서
```markdown
# 팀 TDD 가이드라인

## 필수 사항
1. 모든 새로운 기능은 TDD로 개발
2. 테스트 커버리지 80% 이상 유지
3. 테스트 실행 시간 전체 5분 이내
4. CI에서 테스트 실패 시 배포 중단

## 테스트 작성 원칙
1. 테스트 이름은 한글로 의도 명확히 표현
2. Given-When-Then 구조 준수
3. 하나의 테스트는 하나의 관심사만 검증
4. Mock은 외부 의존성에만 사용

## 금지 사항
1. @Disabled 테스트 커밋 금지
2. Thread.sleep() 사용 금지
3. 현재 시간에 의존하는 테스트 금지
4. 테스트 순서에 의존하는 코드 금지

## 코드 리뷰 필수 체크
1. 모든 새로운 코드에 테스트 존재
2. 테스트가 실제 비즈니스 가치 검증
3. 테스트 실행 시간 확인
4. 안티패턴 존재 여부 확인
```

### 4. 지속적 개선

#### 회고를 통한 테스트 품질 개선
```java
// 회고 액션 아이템 예시
public class TestQualityImprovement {
    
    /*
     * 이번 스프린트 회고:
     * 
     * 문제점:
     * - 통합 테스트가 너무 느림 (10분 이상)
     * - 테스트 실패 시 원인 파악이 어려움
     * - Mock 사용이 과도함
     * 
     * 개선 액션:
     * 1. 통합 테스트를 단위 테스트로 분리
     * 2. 테스트 에러 메시지 개선
     * 3. Mock 사용 가이드라인 수립
     */
    
    // Before: 느린 통합 테스트
    @Test
    @Disabled("너무 느려서 비활성화")
    void 전체_주문_프로세스_통합_테스트() {
        // 10분 이상 걸리는 테스트
    }
    
    // After: 빠른 단위 테스트들로 분리
    @Test
    void 주문_생성_단위_테스트() {
        // 밀리초 단위 실행
    }
    
    @Test
    void 결제_처리_단위_테스트() {
        // 밀리초 단위 실행
    }
}
```

### 5. 레거시 코드 개선 전략

#### 점진적 테스트 추가
```java
// 레거시 코드 개선 전략
public class LegacyCodeImprovement {
    
    // 1단계: Characterization Test (현재 동작 보호)
    @Test
    void 레거시_메서드_현재_동작_보호() {
        LegacyService service = new LegacyService();
        
        // 현재 동작을 테스트로 고정
        String result = service.legacyMethod("input");
        
        // 정확한 동작을 모르더라도 현재 결과를 고정
        assertEquals("current_output", result);
    }
    
    // 2단계: 새로운 요구사항은 새로운 메서드로 TDD
    @Test
    void 새로운_요구사항_TDD_개발() {
        LegacyService service = new LegacyService();
        
        // 새로운 기능은 TDD로 개발
        String result = service.newMethod("input");
        
        assertEquals("expected_output", result);
    }
    
    // 3단계: 점진적으로 레거시 코드 리팩토링
    @Test
    void 리팩토링된_메서드_테스트() {
        RefactoredService service = new RefactoredService();
        
        String result = service.improvedMethod("input");
        
        assertEquals("expected_output", result);
    }
}
```

## 마무리

TDD 안티패턴을 인식하고 피하는 것은 효과적인 TDD 적용의 핵심입니다.

**핵심 포인트:**
- **인식**: 안티패턴을 빨리 인식하는 것이 중요
- **예방**: 코드 리뷰와 가이드라인으로 예방
- **개선**: 지속적인 회고를 통한 품질 향상
- **교육**: 팀 전체의 TDD 역량 강화

좋은 테스트는 개발자의 생산성을 높이고, 나쁜 테스트는 오히려 발목을 잡습니다. 안티패턴을 피하고 올바른 TDD를 실천하여 진정한 TDD의 가치를 얻어보세요.