# Mockito 및 테스트 더블 완벽 가이드

## 목차
1. [테스트 더블 개념](#테스트-더블-개념)
2. [Mockito 개요](#mockito-개요)
3. [Mock 객체 생성과 기본 사용법](#mock-객체-생성과-기본-사용법)
4. [Stubbing (행동 정의)](#stubbing-행동-정의)
5. [Verification (검증)](#verification-검증)
6. [ArgumentMatchers](#argumentmatchers)
7. [Spy 객체](#spy-객체)
8. [Annotation 기반 사용법](#annotation-기반-사용법)
9. [고급 기능](#고급-기능)
10. [실전 패턴과 베스트 프랙티스](#실전-패턴과-베스트-프랙티스)

## 테스트 더블 개념

### 테스트 더블이란?
테스트 더블(Test Double)은 테스트 목적으로 실제 객체를 대체하는 객체입니다. 영화에서 위험한 장면을 대신하는 스턴트 더블에서 이름이 유래되었습니다.

### 테스트 더블의 필요성
```java
// 문제가 있는 테스트 - 외부 의존성
@Test
void 사용자_이메일_발송_테스트() {
    UserService userService = new UserService();
    
    // 실제 이메일이 발송됨! (문제)
    // 네트워크 연결 필요 (문제)
    // 외부 서비스 의존성 (문제)
    userService.registerUser("test@example.com");
    
    // 검증이 어려움
}

// 해결된 테스트 - 테스트 더블 사용
@Test
void 사용자_이메일_발송_테스트_개선() {
    EmailService mockEmailService = mock(EmailService.class);
    UserService userService = new UserService(mockEmailService);
    
    userService.registerUser("test@example.com");
    
    // 이메일 발송 메서드가 호출되었는지 검증
    verify(mockEmailService).sendWelcomeEmail("test@example.com");
}
```

### 테스트 더블의 종류

#### 1. Dummy
아무 기능도 하지 않는 객체, 단순히 자리를 채우는 용도
```java
@Test
void dummy_객체_예시() {
    // UserValidator는 사용되지 않지만 생성자에 필요
    UserValidator dummyValidator = mock(UserValidator.class);
    UserService userService = new UserService(dummyValidator, emailService);
    
    // validator는 실제로 호출되지 않음
}
```

#### 2. Fake
실제 구현을 가지고 있지만 단순한 버전
```java
// 실제 데이터베이스 대신 메모리 기반 구현
public class FakeUserRepository implements UserRepository {
    private Map<String, User> users = new HashMap<>();
    
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
    }
    
    @Override
    public User findById(String id) {
        return users.get(id);
    }
}
```

#### 3. Stub
미리 정의된 답변을 제공하는 객체
```java
@Test
void stub_예시() {
    PaymentGateway stubPaymentGateway = mock(PaymentGateway.class);
    
    // 특정 입력에 대해 미리 정의된 응답 설정
    when(stubPaymentGateway.processPayment(any()))
        .thenReturn(new PaymentResult(true, "SUCCESS"));
    
    PaymentService paymentService = new PaymentService(stubPaymentGateway);
    PaymentResult result = paymentService.processPayment(payment);
    
    assertTrue(result.isSuccessful());
}
```

#### 4. Mock
호출에 대한 기대값을 명세하고 검증하는 객체
```java
@Test
void mock_예시() {
    EmailService mockEmailService = mock(EmailService.class);
    UserService userService = new UserService(mockEmailService);
    
    userService.registerUser("test@example.com");
    
    // 정확한 인자로 호출되었는지 검증
    verify(mockEmailService).sendWelcomeEmail("test@example.com");
    verify(mockEmailService, never()).sendSpamEmail(any());
}
```

#### 5. Spy
실제 객체를 감시하면서 일부 메서드만 stubbing
```java
@Test
void spy_예시() {
    UserService userService = spy(new UserService());
    
    // 일부 메서드만 stubbing
    doReturn("MOCKED_ID").when(userService).generateUserId();
    
    // 나머지는 실제 메서드 호출
    User user = userService.createUser("John");
    
    assertEquals("MOCKED_ID", user.getId());
    verify(userService).generateUserId();
}
```

## Mockito 개요

### Mockito란?
Mockito는 Java에서 가장 인기 있는 모킹 프레임워크입니다. 깔끔하고 간단한 API로 테스트 더블을 쉽게 생성하고 사용할 수 있게 해줍니다.

### 주요 특징
- **간단한 API**: 직관적이고 사용하기 쉬운 메서드들
- **타입 안전성**: 컴파일 타임에 타입 체크
- **풍부한 검증**: 다양한 검증 옵션 제공
- **어노테이션 지원**: `@Mock`, `@Spy` 등 편리한 어노테이션
- **Spring 통합**: Spring Boot Test에 기본 포함

### 의존성 설정
```gradle
dependencies {
    // Spring Boot Starter Test에 포함됨
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    
    // 또는 직접 추가
    testImplementation 'org.mockito:mockito-core:5.7.0'
    testImplementation 'org.mockito:mockito-junit-jupiter:5.7.0'
}
```

## Mock 객체 생성과 기본 사용법

### Mock 객체 생성 방법
```java
import static org.mockito.Mockito.*;

class MockCreationTest {
    
    @Test
    void mock_생성_방법들() {
        // 1. 정적 메서드 사용
        List<String> mockedList = mock(List.class);
        
        // 2. 클래스 지정
        UserService userService = mock(UserService.class);
        
        // 3. 제네릭 타입 지정
        List<User> userList = mock(List.class);
        
        // 4. 이름 지정 (디버깅 시 유용)
        EmailService emailService = mock(EmailService.class, "emailService");
    }
    
    @Test
    void mock_기본_동작() {
        List<String> mockedList = mock(List.class);
        
        // 기본적으로 모든 메서드는 기본값 반환
        assertEquals(0, mockedList.size());         // int -> 0
        assertNull(mockedList.get(0));              // Object -> null
        assertFalse(mockedList.isEmpty());          // boolean -> false (주의!)
    }
}
```

### 인터페이스 vs 클래스 모킹
```java
class InterfaceVsClassMockingTest {
    
    @Test
    void 인터페이스_모킹() {
        // 인터페이스는 쉽게 모킹 가능
        UserRepository userRepository = mock(UserRepository.class);
        EmailService emailService = mock(EmailService.class);
        
        when(userRepository.findById("1")).thenReturn(new User("John"));
        when(emailService.sendEmail(any())).thenReturn(true);
    }
    
    @Test
    void 클래스_모킹() {
        // 구체 클래스도 모킹 가능 (final이 아닌 경우)
        ArrayList<String> mockedArrayList = mock(ArrayList.class);
        UserService mockedUserService = mock(UserService.class);
        
        when(mockedArrayList.size()).thenReturn(100);
        when(mockedUserService.createUser(any())).thenReturn(new User("Mock"));
    }
    
    @Test
    void final_클래스_모킹() {
        // Mockito 3.4.0+에서는 final 클래스도 모킹 가능
        String mockedString = mock(String.class);
        LocalDateTime mockedDateTime = mock(LocalDateTime.class);
        
        when(mockedString.length()).thenReturn(10);
        when(mockedDateTime.getYear()).thenReturn(2023);
    }
}
```

## Stubbing (행동 정의)

### 기본 Stubbing
```java
class BasicStubbingTest {
    
    @Test
    void when_thenReturn_기본사용() {
        UserRepository mockRepository = mock(UserRepository.class);
        
        // 특정 입력에 대한 반환값 정의
        when(mockRepository.findById("1"))
            .thenReturn(new User("John", "john@example.com"));
        
        // 테스트 실행
        User user = mockRepository.findById("1");
        
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
    }
    
    @Test
    void 여러_반환값_정의() {
        PaymentService mockPaymentService = mock(PaymentService.class);
        
        // 여러 번 호출 시 다른 값 반환
        when(mockPaymentService.processPayment(any()))
            .thenReturn(new PaymentResult(true))    // 첫 번째 호출
            .thenReturn(new PaymentResult(false))   // 두 번째 호출
            .thenReturn(new PaymentResult(true));   // 세 번째 호출
        
        assertTrue(mockPaymentService.processPayment(payment1).isSuccessful());
        assertFalse(mockPaymentService.processPayment(payment2).isSuccessful());
        assertTrue(mockPaymentService.processPayment(payment3).isSuccessful());
    }
    
    @Test
    void 조건부_반환값() {
        DiscountService mockDiscountService = mock(DiscountService.class);
        
        // 다른 인자에 대해 다른 반환값
        when(mockDiscountService.calculateDiscount("VIP")).thenReturn(0.2);
        when(mockDiscountService.calculateDiscount("REGULAR")).thenReturn(0.1);
        when(mockDiscountService.calculateDiscount("GUEST")).thenReturn(0.0);
        
        assertEquals(0.2, mockDiscountService.calculateDiscount("VIP"));
        assertEquals(0.1, mockDiscountService.calculateDiscount("REGULAR"));
        assertEquals(0.0, mockDiscountService.calculateDiscount("GUEST"));
    }
}
```

### 예외 발생 Stubbing
```java
class ExceptionStubbingTest {
    
    @Test
    void thenThrow_사용법() {
        DatabaseService mockDatabaseService = mock(DatabaseService.class);
        
        // 예외 발생 정의
        when(mockDatabaseService.connect())
            .thenThrow(new ConnectionException("Database unavailable"));
        
        assertThrows(ConnectionException.class, () -> {
            mockDatabaseService.connect();
        });
    }
    
    @Test
    void 다양한_예외_시나리오() {
        FileService mockFileService = mock(FileService.class);
        
        // 다른 인자에 대해 다른 예외
        when(mockFileService.readFile("nonexistent.txt"))
            .thenThrow(new FileNotFoundException("파일을 찾을 수 없습니다"));
        
        when(mockFileService.readFile("corrupted.txt"))
            .thenThrow(new IOException("파일이 손상되었습니다"));
        
        // 첫 번째는 예외, 두 번째는 성공
        when(mockFileService.readFile("unstable.txt"))
            .thenThrow(new IOException("일시적 오류"))
            .thenReturn("file content");
        
        assertThrows(FileNotFoundException.class, () -> 
            mockFileService.readFile("nonexistent.txt"));
        
        assertThrows(IOException.class, () -> 
            mockFileService.readFile("corrupted.txt"));
        
        assertThrows(IOException.class, () -> 
            mockFileService.readFile("unstable.txt"));
        
        assertEquals("file content", mockFileService.readFile("unstable.txt"));
    }
}
```

### 콜백과 동적 응답
```java
class CallbackStubbingTest {
    
    @Test
    void thenAnswer_사용법() {
        CalculatorService mockCalculator = mock(CalculatorService.class);
        
        // 입력에 따라 동적으로 응답 생성
        when(mockCalculator.add(anyInt(), anyInt()))
            .thenAnswer(invocation -> {
                Integer arg1 = invocation.getArgument(0);
                Integer arg2 = invocation.getArgument(1);
                return arg1 + arg2;
            });
        
        assertEquals(5, mockCalculator.add(2, 3));
        assertEquals(10, mockCalculator.add(4, 6));
    }
    
    @Test
    void 복잡한_콜백_로직() {
        UserService mockUserService = mock(UserService.class);
        
        when(mockUserService.createUser(any(UserCreateRequest.class)))
            .thenAnswer(invocation -> {
                UserCreateRequest request = invocation.getArgument(0);
                
                // 요청 검증
                if (request.getName() == null || request.getName().isEmpty()) {
                    throw new IllegalArgumentException("이름은 필수입니다");
                }
                
                // 동적 응답 생성
                User user = new User();
                user.setId(UUID.randomUUID().toString());
                user.setName(request.getName());
                user.setEmail(request.getEmail());
                user.setCreatedAt(LocalDateTime.now());
                
                return user;
            });
        
        UserCreateRequest request = new UserCreateRequest("John", "john@example.com");
        User user = mockUserService.createUser(request);
        
        assertNotNull(user.getId());
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertNotNull(user.getCreatedAt());
    }
    
    @Test
    void 지연_응답_시뮬레이션() {
        ExternalApiService mockApiService = mock(ExternalApiService.class);
        
        when(mockApiService.fetchData())
            .thenAnswer(invocation -> {
                // 네트워크 지연 시뮬레이션
                Thread.sleep(100);
                return new ApiResponse("success", "data");
            });
        
        long startTime = System.currentTimeMillis();
        ApiResponse response = mockApiService.fetchData();
        long endTime = System.currentTimeMillis();
        
        assertTrue(endTime - startTime >= 100);
        assertEquals("success", response.getStatus());
    }
}
```

### Void 메서드 Stubbing
```java
class VoidMethodStubbingTest {
    
    @Test
    void doThrow_사용법() {
        EmailService mockEmailService = mock(EmailService.class);
        
        // void 메서드에 예외 발생 정의
        doThrow(new EmailSendException("SMTP 서버 오류"))
            .when(mockEmailService).sendEmail(any());
        
        assertThrows(EmailSendException.class, () -> {
            mockEmailService.sendEmail("test@example.com");
        });
    }
    
    @Test
    void doNothing_사용법() {
        AuditService mockAuditService = mock(AuditService.class);
        
        // void 메서드가 아무것도 하지 않도록 (기본 동작이지만 명시적 표현)
        doNothing().when(mockAuditService).logActivity(any());
        
        // 예외 없이 실행되어야 함
        assertDoesNotThrow(() -> {
            mockAuditService.logActivity("user login");
        });
    }
    
    @Test
    void doAnswer_void_메서드() {
        NotificationService mockNotificationService = mock(NotificationService.class);
        List<String> sentNotifications = new ArrayList<>();
        
        // void 메서드에 콜백 정의
        doAnswer(invocation -> {
            String message = invocation.getArgument(0);
            sentNotifications.add(message);
            return null;  // void 메서드는 null 반환
        }).when(mockNotificationService).sendNotification(any());
        
        mockNotificationService.sendNotification("Hello");
        mockNotificationService.sendNotification("World");
        
        assertEquals(Arrays.asList("Hello", "World"), sentNotifications);
    }
}
```

## Verification (검증)

### 기본 Verification
```java
class BasicVerificationTest {
    
    @Test
    void verify_기본_사용법() {
        EmailService mockEmailService = mock(EmailService.class);
        UserService userService = new UserService(mockEmailService);
        
        userService.registerUser("john@example.com");
        
        // 메서드가 정확히 한 번 호출되었는지 검증
        verify(mockEmailService).sendWelcomeEmail("john@example.com");
        
        // 다른 메서드는 호출되지 않았는지 검증
        verify(mockEmailService, never()).sendSpamEmail(any());
    }
    
    @Test
    void 호출_횟수_검증() {
        PaymentService mockPaymentService = mock(PaymentService.class);
        BillingService billingService = new BillingService(mockPaymentService);
        
        billingService.processMonthlyBilling(Arrays.asList("user1", "user2", "user3"));
        
        // 정확히 3번 호출되었는지 검증
        verify(mockPaymentService, times(3)).processPayment(any());
        
        // 최소 2번 호출되었는지 검증
        verify(mockPaymentService, atLeast(2)).processPayment(any());
        
        // 최대 5번 호출되었는지 검증
        verify(mockPaymentService, atMost(5)).processPayment(any());
    }
    
    @Test
    void 호출_순서_검증() {
        DatabaseService mockDatabaseService = mock(DatabaseService.class);
        LogService mockLogService = mock(LogService.class);
        
        TransactionService transactionService = 
            new TransactionService(mockDatabaseService, mockLogService);
        
        transactionService.executeTransaction();
        
        // 호출 순서 검증
        InOrder inOrder = inOrder(mockDatabaseService, mockLogService);
        inOrder.verify(mockDatabaseService).beginTransaction();
        inOrder.verify(mockDatabaseService).executeQuery(any());
        inOrder.verify(mockDatabaseService).commit();
        inOrder.verify(mockLogService).log("Transaction completed");
    }
}
```

### 고급 Verification
```java
class AdvancedVerificationTest {
    
    @Test
    void 시간_기반_검증() {
        EmailService mockEmailService = mock(EmailService.class);
        UserService userService = new UserService(mockEmailService);
        
        userService.registerUser("john@example.com");
        
        // 5초 이내에 호출되었는지 검증
        verify(mockEmailService, timeout(5000)).sendWelcomeEmail("john@example.com");
        
        // 1초 이내에 2번 호출되었는지 검증
        verify(mockEmailService, timeout(1000).times(2)).sendEmail(any());
    }
    
    @Test
    void 인자_캡처_검증() {
        EmailService mockEmailService = mock(EmailService.class);
        UserService userService = new UserService(mockEmailService);
        
        userService.registerUser("john@example.com");
        
        // 인자 캡처를 위한 ArgumentCaptor 사용
        ArgumentCaptor<EmailMessage> emailCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(mockEmailService).sendEmail(emailCaptor.capture());
        
        EmailMessage capturedEmail = emailCaptor.getValue();
        assertEquals("john@example.com", capturedEmail.getTo());
        assertEquals("Welcome!", capturedEmail.getSubject());
        assertThat(capturedEmail.getBody()).contains("환영합니다");
    }
    
    @Test
    void 여러_인자_캡처() {
        UserRepository mockUserRepository = mock(UserRepository.class);
        UserService userService = new UserService(mockUserRepository);
        
        userService.updateUser("user1", "New Name", "new@example.com");
        
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        
        verify(mockUserRepository).update(idCaptor.capture(), userCaptor.capture());
        
        assertEquals("user1", idCaptor.getValue());
        assertEquals("New Name", userCaptor.getValue().getName());
        assertEquals("new@example.com", userCaptor.getValue().getEmail());
    }
    
    @Test
    void 모든_호출_검증() {
        List<String> mockList = mock(List.class);
        
        mockList.add("first");
        mockList.add("second");
        mockList.clear();
        
        // 모든 상호작용 검증
        verify(mockList).add("first");
        verify(mockList).add("second");
        verify(mockList).clear();
        
        // 더 이상 상호작용이 없는지 검증
        verifyNoMoreInteractions(mockList);
    }
}
```

## ArgumentMatchers

### 기본 ArgumentMatchers
```java
import static org.mockito.ArgumentMatchers.*;

class ArgumentMatchersTest {
    
    @Test
    void 기본_matchers() {
        UserService mockUserService = mock(UserService.class);
        
        // any() - 모든 값
        when(mockUserService.findById(any())).thenReturn(new User("John"));
        
        // anyString() - 모든 문자열
        when(mockUserService.findByName(anyString())).thenReturn(new User("Jane"));
        
        // anyInt() - 모든 정수
        when(mockUserService.findByAge(anyInt())).thenReturn(Arrays.asList(new User("Bob")));
        
        // eq() - 정확한 값 (다른 matcher와 함께 사용할 때)
        when(mockUserService.updateUser(eq("user1"), any(User.class)))
            .thenReturn(new User("Updated"));
        
        assertNotNull(mockUserService.findById("any-id"));
        assertNotNull(mockUserService.findByName("any-name"));
        assertNotNull(mockUserService.findByAge(25));
    }
    
    @Test
    void 타입별_matchers() {
        PaymentService mockPaymentService = mock(PaymentService.class);
        
        when(mockPaymentService.processPayment(
            anyString(),           // 사용자 ID
            any(BigDecimal.class), // 금액
            anyBoolean(),          // 즉시 처리 여부
            anyList()              // 결제 옵션들
        )).thenReturn(new PaymentResult(true));
        
        PaymentResult result = mockPaymentService.processPayment(
            "user1", 
            new BigDecimal("100.00"), 
            true, 
            Arrays.asList("CREDIT_CARD")
        );
        
        assertTrue(result.isSuccessful());
    }
    
    @Test
    void 컬렉션_matchers() {
        OrderService mockOrderService = mock(OrderService.class);
        
        // anyList(), anySet(), anyMap(), anyCollection()
        when(mockOrderService.createOrder(anyList())).thenReturn(new Order("ORDER-1"));
        when(mockOrderService.updateOrderTags(anyString(), anySet()))
            .thenReturn(new Order("ORDER-1"));
        when(mockOrderService.processOrderWithMetadata(anyString(), anyMap()))
            .thenReturn(true);
        
        Order order = mockOrderService.createOrder(Arrays.asList("item1", "item2"));
        assertNotNull(order);
        
        mockOrderService.updateOrderTags("ORDER-1", Set.of("urgent", "priority"));
        mockOrderService.processOrderWithMetadata("ORDER-1", Map.of("source", "web"));
    }
}
```

### 조건부 ArgumentMatchers
```java
class ConditionalArgumentMatchersTest {
    
    @Test
    void 문자열_조건_matchers() {
        EmailService mockEmailService = mock(EmailService.class);
        
        // startsWith, endsWith, contains
        when(mockEmailService.sendEmail(startsWith("admin@")))
            .thenReturn(true);
        when(mockEmailService.sendEmail(endsWith("@company.com")))
            .thenReturn(true);
        when(mockEmailService.sendEmail(contains("test")))
            .thenReturn(false);
        
        assertTrue(mockEmailService.sendEmail("admin@company.com"));
        assertTrue(mockEmailService.sendEmail("user@company.com"));
        assertFalse(mockEmailService.sendEmail("test@example.com"));
    }
    
    @Test
    void 정규식_matcher() {
        ValidationService mockValidationService = mock(ValidationService.class);
        
        // matches - 정규식 패턴
        when(mockValidationService.validateEmail(matches("\\w+@\\w+\\.\\w+")))
            .thenReturn(true);
        when(mockValidationService.validatePhone(matches("\\d{3}-\\d{4}-\\d{4}")))
            .thenReturn(true);
        
        assertTrue(mockValidationService.validateEmail("user@example.com"));
        assertTrue(mockValidationService.validatePhone("010-1234-5678"));
    }
    
    @Test
    void 범위_조건_matchers() {
        PricingService mockPricingService = mock(PricingService.class);
        
        // intThat, longThat, doubleThat 등으로 조건 지정
        when(mockPricingService.calculateDiscount(intThat(price -> price > 100000)))
            .thenReturn(0.1);  // 10만원 이상 10% 할인
        
        when(mockPricingService.calculateDiscount(intThat(price -> price > 50000)))
            .thenReturn(0.05); // 5만원 이상 5% 할인
        
        assertEquals(0.1, mockPricingService.calculateDiscount(150000));
        assertEquals(0.05, mockPricingService.calculateDiscount(70000));
    }
    
    @Test
    void 커스텀_matcher() {
        UserService mockUserService = mock(UserService.class);
        
        // argThat으로 커스텀 조건 정의
        when(mockUserService.createUser(argThat(user -> 
            user.getAge() >= 18 && user.getEmail().contains("@")
        ))).thenReturn(new User("Created User"));
        
        User validUser = new User("John", 25, "john@example.com");
        User invalidUser = new User("Jane", 16, "invalid-email");
        
        assertNotNull(mockUserService.createUser(validUser));
        assertNull(mockUserService.createUser(invalidUser));  // stubbing되지 않음
    }
    
    @Test
    void null_관련_matchers() {
        DatabaseService mockDatabaseService = mock(DatabaseService.class);
        
        // isNull(), isNotNull()
        when(mockDatabaseService.findUser(isNull())).thenReturn(null);
        when(mockDatabaseService.findUser(isNotNull())).thenReturn(new User("Found"));
        
        assertNull(mockDatabaseService.findUser(null));
        assertNotNull(mockDatabaseService.findUser("user-id"));
    }
}
```

### ArgumentMatchers 조합
```java
class ArgumentMatchersCombinationTest {
    
    @Test
    void 여러_matcher_조합() {
        OrderService mockOrderService = mock(OrderService.class);
        
        // 여러 조건을 조합
        when(mockOrderService.processOrder(
            argThat(orderId -> orderId.startsWith("ORDER-")),  // 주문 ID 형식
            intThat(amount -> amount > 0),                     // 양수 금액
            anyString(),                                       // 결제 방법
            argThat(options -> options.size() <= 5)            // 옵션 최대 5개
        )).thenReturn(new ProcessResult(true));
        
        ProcessResult result = mockOrderService.processOrder(
            "ORDER-123",
            50000,
            "CREDIT_CARD",
            Arrays.asList("express", "gift-wrap")
        );
        
        assertTrue(result.isSuccessful());
    }
    
    @Test
    void matcher_와_실제값_혼용() {
        NotificationService mockNotificationService = mock(NotificationService.class);
        
        // 일부는 matcher, 일부는 정확한 값
        when(mockNotificationService.sendNotification(
            eq("EMAIL"),          // 정확한 값은 eq() 사용
            anyString(),          // 수신자는 아무나
            contains("중요")       // 메시지에 "중요" 포함
        )).thenReturn(true);
        
        assertTrue(mockNotificationService.sendNotification(
            "EMAIL", 
            "user@example.com", 
            "중요한 알림입니다"
        ));
    }
}
```

## Spy 객체

### 기본 Spy 사용법
```java
class SpyBasicsTest {
    
    @Test
    void spy_기본_사용법() {
        // 실제 객체를 spy로 감싸기
        List<String> realList = new ArrayList<>();
        List<String> spyList = spy(realList);
        
        // 실제 메서드 호출
        spyList.add("hello");
        spyList.add("world");
        
        // 실제 동작 확인
        assertEquals(2, spyList.size());
        assertEquals("hello", spyList.get(0));
        
        // 메서드 호출 검증
        verify(spyList).add("hello");
        verify(spyList).add("world");
        verify(spyList, times(2)).add(anyString());
    }
    
    @Test
    void spy_부분_stubbing() {
        UserService realUserService = new UserService();
        UserService spyUserService = spy(realUserService);
        
        // 일부 메서드만 stubbing
        doReturn("MOCKED_ID").when(spyUserService).generateUserId();
        
        // stubbing된 메서드는 mock 동작
        assertEquals("MOCKED_ID", spyUserService.generateUserId());
        
        // 나머지는 실제 메서드 호출
        User user = spyUserService.createUser("John", "john@example.com");
        assertEquals("MOCKED_ID", user.getId());  // stubbing된 ID
        assertEquals("John", user.getName());     // 실제 로직
    }
    
    @Test
    void spy_void_메서드_stubbing() {
        EmailService realEmailService = new EmailService();
        EmailService spyEmailService = spy(realEmailService);
        
        // void 메서드 stubbing
        doNothing().when(spyEmailService).sendEmail(anyString());
        
        // 실제로는 이메일이 발송되지 않음
        spyEmailService.sendEmail("test@example.com");
        
        verify(spyEmailService).sendEmail("test@example.com");
    }
}
```

### Spy vs Mock 비교
```java
class SpyVsMockTest {
    
    @Test
    void mock_vs_spy_비교() {
        // Mock - 완전히 가짜 객체
        List<String> mockList = mock(List.class);
        
        // Spy - 실제 객체를 감시
        List<String> spyList = spy(new ArrayList<>());
        
        // Mock은 stubbing하지 않으면 기본값 반환
        assertEquals(0, mockList.size());        // 기본값
        assertNull(mockList.get(0));             // 기본값 (예외 발생하지 않음)
        
        // Spy는 실제 메서드 호출
        assertEquals(0, spyList.size());         // 실제 동작
        assertThrows(IndexOutOfBoundsException.class, () -> {
            spyList.get(0);                      // 실제 예외 발생
        });
        
        // Spy에 실제 데이터 추가
        spyList.add("hello");
        assertEquals(1, spyList.size());         // 실제 동작
        assertEquals("hello", spyList.get(0));   // 실제 동작
    }
    
    @Test
    void spy_실제_객체_기반() {
        // 실제 서비스 객체 생성
        Calculator realCalculator = new Calculator();
        Calculator spyCalculator = spy(realCalculator);
        
        // 일부 메서드만 stubbing
        when(spyCalculator.add(10, 10)).thenReturn(100);  // 특별한 경우만 조작
        
        // stubbing된 경우
        assertEquals(100, spyCalculator.add(10, 10));
        
        // 실제 계산 (stubbing되지 않은 경우)
        assertEquals(5, spyCalculator.add(2, 3));
        assertEquals(6, spyCalculator.multiply(2, 3));
        
        // 실제 메서드 호출 검증
        verify(spyCalculator).add(10, 10);
        verify(spyCalculator).add(2, 3);
        verify(spyCalculator).multiply(2, 3);
    }
}
```

### Spy 사용 시 주의사항
```java
class SpyCautionsTest {
    
    @Test
    void spy_stubbing_주의사항() {
        List<String> spyList = spy(new ArrayList<>());
        
        // ❌ 잘못된 방법 - 실제 메서드가 호출됨
        // when(spyList.get(0)).thenReturn("mocked");  // IndexOutOfBoundsException 발생!
        
        // ✅ 올바른 방법 - doReturn 사용
        doReturn("mocked").when(spyList).get(0);
        
        assertEquals("mocked", spyList.get(0));  // 정상 동작
    }
    
    @Test
    void spy_final_메서드_제한() {
        StringBuilder spyBuilder = spy(new StringBuilder());
        
        // final 메서드는 stubbing할 수 없음
        spyBuilder.append("hello");
        
        // toString()은 final이므로 stubbing 불가
        // doReturn("mocked").when(spyBuilder).toString();  // 효과 없음
        
        assertEquals("hello", spyBuilder.toString());  // 실제 메서드 호출
        verify(spyBuilder).append("hello");
    }
    
    @Test
    void spy_성능_고려사항() {
        // 무거운 객체를 spy로 만들 때 주의
        // 실제 객체가 생성되므로 비용이 큼
        
        // 가벼운 객체나 필요한 경우에만 사용
        Map<String, String> lightweightMap = new HashMap<>();
        Map<String, String> spyMap = spy(lightweightMap);
        
        spyMap.put("key", "value");
        assertEquals("value", spyMap.get("key"));
        
        verify(spyMap).put("key", "value");
        verify(spyMap).get("key");
    }
}
```

## Annotation 기반 사용법

### 기본 Annotations
```java
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnnotationBasedMockingTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @InjectMocks
    private UserService userService;  // 위의 mock들이 자동 주입됨
    
    @Test
    void mock_자동_주입_테스트() {
        // given
        User user = new User("john@example.com", "password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        // when
        User createdUser = userService.createUser("john@example.com", "password");
        
        // then
        assertNotNull(createdUser);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(eq("john@example.com"));
        verify(passwordEncoder).encode("password");  // spy 호출 검증
    }
    
    @Test
    void annotation_기반_검증() {
        // Mock 객체들이 자동으로 생성되고 주입됨
        assertNotNull(userRepository);
        assertNotNull(emailService);
        assertNotNull(passwordEncoder);
        assertNotNull(userService);
        
        // InjectMocks 객체가 실제 인스턴스인지 확인
        assertFalse(Mockito.mockingDetails(userService).isMock());
        assertTrue(Mockito.mockingDetails(userRepository).isMock());
        assertTrue(Mockito.mockingDetails(emailService).isMock());
        assertTrue(Mockito.mockingDetails(passwordEncoder).isSpy());
    }
}
```

### Constructor, Setter, Field 주입
```java
@ExtendWith(MockitoExtension.class)
class DependencyInjectionTest {
    
    @Mock
    private DatabaseService databaseService;
    
    @Mock
    private CacheService cacheService;
    
    @Mock
    private LoggingService loggingService;
    
    // Constructor 주입 (권장)
    @InjectMocks
    private UserService userServiceWithConstructor;
    
    @Test
    void constructor_주입_테스트() {
        // UserService(DatabaseService, CacheService, LoggingService) 생성자로 주입
        when(databaseService.findUser("user1")).thenReturn(new User("John"));
        when(cacheService.get("user1")).thenReturn(null);
        
        User user = userServiceWithConstructor.getUser("user1");
        
        assertNotNull(user);
        verify(databaseService).findUser("user1");
        verify(cacheService).get("user1");
        verify(cacheService).put("user1", user);
    }
    
    // 복잡한 생성자가 있는 경우
    @InjectMocks
    private OrderService orderService;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private InventoryService inventoryService;
    
    @Mock
    private NotificationService notificationService;
    
    @Test
    void 복잡한_의존성_주입() {
        // OrderService에 여러 의존성이 자동 주입됨
        Order order = new Order("item1", 2, 50000);
        
        when(inventoryService.checkStock("item1", 2)).thenReturn(true);
        when(paymentService.processPayment(100000)).thenReturn(new PaymentResult(true));
        
        OrderResult result = orderService.processOrder(order);
        
        assertTrue(result.isSuccessful());
        verify(inventoryService).checkStock("item1", 2);
        verify(paymentService).processPayment(100000);
        verify(notificationService).sendOrderConfirmation(order);
    }
}
```

### @MockBean (Spring Boot Test)
```java
@SpringBootTest
class SpringBootMockingTest {
    
    @MockBean
    private UserRepository userRepository;  // Spring 컨텍스트의 빈을 Mock으로 대체
    
    @MockBean
    private EmailService emailService;
    
    @Autowired
    private UserService userService;  // 실제 Spring 빈 (Mock들이 주입됨)
    
    @Test
    void spring_context_mock_테스트() {
        // given
        User user = new User("john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail("john@example.com")).thenReturn(user);
        
        // when
        User createdUser = userService.registerUser("john@example.com", "password");
        User foundUser = userService.findByEmail("john@example.com");
        
        // then
        assertNotNull(createdUser);
        assertNotNull(foundUser);
        assertEquals("john@example.com", foundUser.getEmail());
        
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail("john@example.com");
    }
    
    @Test
    void spring_profile_활용() {
        // 특정 프로파일에서만 실행되는 테스트
        // Mock을 통해 외부 의존성 제거
        when(userRepository.count()).thenReturn(100L);
        
        long userCount = userService.getTotalUserCount();
        
        assertEquals(100L, userCount);
        verify(userRepository).count();
    }
}
```

### Custom Annotations
```java
// 커스텀 테스트 어노테이션 정의
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MockitoExtension.class)
@Tag("unit-test")
public @interface UnitTest {
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
public @interface IntegrationTest {
}

// 사용 예시
@UnitTest
class CustomAnnotationUnitTest {
    
    @Mock
    private PaymentGateway paymentGateway;
    
    @InjectMocks
    private PaymentService paymentService;
    
    @Test
    void 단위_테스트_예시() {
        when(paymentGateway.charge(100)).thenReturn(new ChargeResult(true));
        
        boolean result = paymentService.processPayment(100);
        
        assertTrue(result);
        verify(paymentGateway).charge(100);
    }
}

@IntegrationTest
class CustomAnnotationIntegrationTest {
    
    @MockBean
    private ExternalApiService externalApiService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Test
    void 통합_테스트_예시() {
        when(externalApiService.validatePayment(any())).thenReturn(true);
        
        boolean result = paymentService.processPayment(100);
        
        assertTrue(result);
    }
}
```

## 고급 기능

### ArgumentCaptor 심화
```java
class AdvancedArgumentCaptorTest {
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private AuditService auditService;
    
    @InjectMocks
    private UserRegistrationService registrationService;
    
    @Test
    void 복잡한_인자_캡처() {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
            "john@example.com", "John Doe", 25
        );
        
        // when
        registrationService.registerUser(request);
        
        // then - 이메일 내용 캡처
        ArgumentCaptor<EmailMessage> emailCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(emailService).sendEmail(emailCaptor.capture());
        
        EmailMessage email = emailCaptor.getValue();
        assertEquals("john@example.com", email.getRecipient());
        assertEquals("Welcome to our service!", email.getSubject());
        assertThat(email.getBody()).contains("John Doe");
        
        // 감사 로그 캡처
        ArgumentCaptor<AuditEvent> auditCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditService).logEvent(auditCaptor.capture());
        
        AuditEvent auditEvent = auditCaptor.getValue();
        assertEquals("USER_REGISTRATION", auditEvent.getEventType());
        assertEquals("john@example.com", auditEvent.getUserId());
        assertNotNull(auditEvent.getTimestamp());
    }
    
    @Test
    void 여러_호출_인자_캡처() {
        // given
        List<String> userIds = Arrays.asList("user1", "user2", "user3");
        
        // when
        registrationService.sendBulkWelcomeEmails(userIds);
        
        // then - 모든 호출의 인자 캡처
        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(3)).sendWelcomeEmail(recipientCaptor.capture());
        
        List<String> allRecipients = recipientCaptor.getAllValues();
        assertEquals(Arrays.asList("user1@example.com", "user2@example.com", "user3@example.com"), 
                     allRecipients);
    }
}
```

### Mock 생성 옵션
```java
class MockCreationOptionsTest {
    
    @Test
    void mock_설정_옵션() {
        // 기본 Answer 설정
        UserService mockUserService = mock(UserService.class, Answers.RETURNS_DEEP_STUBS);
        
        // Deep stubbing을 통해 체인 호출 가능
        when(mockUserService.getCurrentUser().getProfile().getName())
            .thenReturn("John Doe");
        
        assertEquals("John Doe", mockUserService.getCurrentUser().getProfile().getName());
    }
    
    @Test
    void mock_이름_지정() {
        // 디버깅을 위한 이름 지정
        EmailService emailService = mock(EmailService.class, "mockEmailService");
        PaymentService paymentService = mock(PaymentService.class, "mockPaymentService");
        
        // 실패 시 메시지에서 mock 이름 확인 가능
        when(emailService.sendEmail(any())).thenReturn(true);
        when(paymentService.processPayment(any())).thenReturn(new PaymentResult(true));
    }
    
    @Test
    void mock_serializable() {
        // Serializable mock 생성
        UserRepository serializableMock = mock(UserRepository.class, 
            withSettings().serializable());
        
        // 직렬화 가능한 mock (분산 시스템 테스트 등에 유용)
        assertTrue(serializableMock instanceof Serializable);
    }
    
    @Test
    void mock_extra_interfaces() {
        // 추가 인터페이스 구현
        UserService mockWithExtraInterface = mock(UserService.class,
            withSettings().extraInterfaces(Closeable.class));
        
        assertTrue(mockWithExtraInterface instanceof UserService);
        assertTrue(mockWithExtraInterface instanceof Closeable);
    }
}
```

### Reset과 Clear
```java
class ResetAndClearTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Test
    void mock_reset() {
        // given
        when(userRepository.findById("1")).thenReturn(new User("John"));
        
        User user = userRepository.findById("1");
        assertEquals("John", user.getName());
        
        // when - mock 초기화
        reset(userRepository);
        
        // then - 이전 stubbing이 제거됨
        assertNull(userRepository.findById("1"));
        
        // 새로운 stubbing 가능
        when(userRepository.findById("1")).thenReturn(new User("Jane"));
        assertEquals("Jane", userRepository.findById("1").getName());
    }
    
    @Test
    void interaction_clear() {
        // given
        userRepository.findById("1");
        userRepository.findById("2");
        
        // 상호작용 기록 확인
        verify(userRepository, times(2)).findById(anyString());
        
        // when - 상호작용 기록만 제거 (stubbing은 유지)
        clearInvocations(userRepository);
        
        // then - 새로운 상호작용만 추적
        userRepository.findById("3");
        verify(userRepository, times(1)).findById("3");  // 새로운 호출만 기록됨
    }
}
```

## 실전 패턴과 베스트 프랙티스

### 테스트 더블 선택 가이드
```java
class TestDoubleSelectionGuideTest {
    
    @Test
    void 외부_시스템_통합_테스트() {
        // 외부 API는 Mock 사용
        ExternalPaymentApi mockPaymentApi = mock(ExternalPaymentApi.class);
        when(mockPaymentApi.charge(any())).thenReturn(new ApiResponse("SUCCESS"));
        
        PaymentService paymentService = new PaymentService(mockPaymentApi);
        boolean result = paymentService.processPayment(new Payment(100));
        
        assertTrue(result);
        verify(mockPaymentApi).charge(any());
    }
    
    @Test
    void 복잡한_비즈니스_로직_테스트() {
        // 간단한 유틸리티는 실제 객체 사용
        PriceCalculator realCalculator = new PriceCalculator();
        
        // 외부 의존성은 Mock
        TaxService mockTaxService = mock(TaxService.class);
        when(mockTaxService.getTaxRate()).thenReturn(0.1);
        
        OrderService orderService = new OrderService(realCalculator, mockTaxService);
        
        Order order = new Order(Arrays.asList(
            new OrderItem("item1", 100),
            new OrderItem("item2", 200)
        ));
        
        int total = orderService.calculateTotal(order);
        assertEquals(330, total);  // (100 + 200) * 1.1
    }
    
    @Test
    void 레거시_코드_테스트() {
        // 레거시 코드는 Spy 사용하여 부분적으로 제어
        LegacyUserService legacyService = spy(new LegacyUserService());
        
        // 문제가 있는 메서드만 stubbing
        doReturn("SAFE_VALUE").when(legacyService).problematicMethod(any());
        
        // 나머지는 실제 로직 실행
        String result = legacyService.processUser("input");
        
        assertNotNull(result);
        verify(legacyService).problematicMethod(any());
    }
}
```

### 테스트 조직화 패턴
```java
class TestOrganizationPatternsTest {
    
    // 테스트 전용 빌더 패턴
    static class TestDataBuilder {
        public static User.Builder aUser() {
            return User.builder()
                .id("default-id")
                .name("Default User")
                .email("default@example.com")
                .status(UserStatus.ACTIVE);
        }
        
        public static Order.Builder anOrder() {
            return Order.builder()
                .id("default-order")
                .userId("default-user")
                .status(OrderStatus.PENDING)
                .items(Arrays.asList(new OrderItem("item1", 100)));
        }
    }
    
    // Mock 설정 헬퍼 메서드
    private void setupSuccessfulPayment(PaymentService mockPaymentService) {
        when(mockPaymentService.processPayment(any()))
            .thenReturn(new PaymentResult(true, "SUCCESS"));
    }
    
    private void setupFailedPayment(PaymentService mockPaymentService, String errorMessage) {
        when(mockPaymentService.processPayment(any()))
            .thenReturn(new PaymentResult(false, errorMessage));
    }
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private OrderProcessingService orderProcessingService;
    
    @Test
    void 성공적인_주문_처리() {
        // given
        User user = TestDataBuilder.aUser()
            .name("John Doe")
            .email("john@example.com")
            .build();
        
        Order order = TestDataBuilder.anOrder()
            .userId(user.getId())
            .build();
        
        setupSuccessfulPayment(paymentService);
        
        // when
        OrderResult result = orderProcessingService.processOrder(order);
        
        // then
        assertTrue(result.isSuccessful());
        verify(paymentService).processPayment(any());
        verify(notificationService).sendOrderConfirmation(order);
    }
    
    @Test
    void 결제_실패_주문_처리() {
        // given
        Order order = TestDataBuilder.anOrder().build();
        setupFailedPayment(paymentService, "INSUFFICIENT_FUNDS");
        
        // when
        OrderResult result = orderProcessingService.processOrder(order);
        
        // then
        assertFalse(result.isSuccessful());
        assertEquals("INSUFFICIENT_FUNDS", result.getErrorMessage());
        verify(notificationService).sendPaymentFailureNotification(order);
    }
}
```

### 복잡한 시나리오 테스트
```java
class ComplexScenarioTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private PaymentService paymentService;
    
    @Mock
    private InventoryService inventoryService;
    
    @InjectMocks
    private ECommerceService eCommerceService;
    
    @Test
    void 전체_구매_플로우_테스트() {
        // given - 복잡한 시나리오 설정
        User user = new User("john@example.com");
        Product product = new Product("laptop", 1000);
        
        when(userRepository.findByEmail("john@example.com")).thenReturn(user);
        when(inventoryService.checkAvailability("laptop", 1)).thenReturn(true);
        when(inventoryService.reserve("laptop", 1)).thenReturn(true);
        when(paymentService.processPayment(any())).thenReturn(new PaymentResult(true));
        
        // when
        PurchaseResult result = eCommerceService.purchaseProduct(
            "john@example.com", "laptop", 1
        );
        
        // then - 전체 플로우 검증
        assertTrue(result.isSuccessful());
        
        // 호출 순서 검증
        InOrder inOrder = inOrder(userRepository, inventoryService, paymentService, emailService);
        inOrder.verify(userRepository).findByEmail("john@example.com");
        inOrder.verify(inventoryService).checkAvailability("laptop", 1);
        inOrder.verify(inventoryService).reserve("laptop", 1);
        inOrder.verify(paymentService).processPayment(any());
        inOrder.verify(emailService).sendPurchaseConfirmation(eq("john@example.com"), any());
        
        // 인자 상세 검증
        ArgumentCaptor<PaymentRequest> paymentCaptor = 
            ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentService).processPayment(paymentCaptor.capture());
        
        PaymentRequest capturedPayment = paymentCaptor.getValue();
        assertEquals(1000, capturedPayment.getAmount());
        assertEquals("john@example.com", capturedPayment.getUserEmail());
    }
    
    @Test
    void 재고_부족_시나리오() {
        // given
        when(userRepository.findByEmail("john@example.com"))
            .thenReturn(new User("john@example.com"));
        when(inventoryService.checkAvailability("laptop", 1))
            .thenReturn(false);
        
        // when
        PurchaseResult result = eCommerceService.purchaseProduct(
            "john@example.com", "laptop", 1
        );
        
        // then
        assertFalse(result.isSuccessful());
        assertEquals("재고 부족", result.getErrorMessage());
        
        // 재고가 없으면 결제는 시도하지 않아야 함
        verify(paymentService, never()).processPayment(any());
        verify(emailService).sendOutOfStockNotification(eq("john@example.com"), eq("laptop"));
    }
    
    @Test
    void 결제_실패_시나리오() {
        // given
        when(userRepository.findByEmail("john@example.com"))
            .thenReturn(new User("john@example.com"));
        when(inventoryService.checkAvailability("laptop", 1))
            .thenReturn(true);
        when(inventoryService.reserve("laptop", 1))
            .thenReturn(true);
        when(paymentService.processPayment(any()))
            .thenReturn(new PaymentResult(false, "카드 한도 초과"));
        
        // when
        PurchaseResult result = eCommerceService.purchaseProduct(
            "john@example.com", "laptop", 1
        );
        
        // then
        assertFalse(result.isSuccessful());
        assertEquals("결제 실패: 카드 한도 초과", result.getErrorMessage());
        
        // 결제 실패 시 재고 해제되어야 함
        verify(inventoryService).releaseReservation("laptop", 1);
        verify(emailService).sendPaymentFailureNotification(
            eq("john@example.com"), eq("카드 한도 초과"));
    }
}
```

### 테스트 성능 최적화
```java
class TestPerformanceOptimizationTest {
    
    // 공유 Mock 설정 (BeforeEach에서 초기화)
    @Mock
    private DatabaseService databaseService;
    
    @Mock
    private CacheService cacheService;
    
    @InjectMocks
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        // 공통 stubbing을 BeforeEach에서 설정
        when(databaseService.isConnected()).thenReturn(true);
        when(cacheService.isEnabled()).thenReturn(true);
    }
    
    @Test
    void 가벼운_mock_사용() {
        // 무거운 객체 대신 인터페이스나 가벼운 클래스 mocking
        UserValidator lightValidator = mock(UserValidator.class);
        when(lightValidator.validate(any())).thenReturn(true);
        
        boolean result = userService.validateUser(new User("test"));
        assertTrue(result);
    }
    
    @Test
    void 최소한의_stubbing() {
        // 테스트에 필요한 최소한의 stubbing만 수행
        when(databaseService.findUser("user1"))
            .thenReturn(new User("John"));
        
        // 불필요한 stubbing 피하기
        User user = userService.getUser("user1");
        assertEquals("John", user.getName());
    }
    
    @Test
    void mock_재사용() {
        // 같은 mock을 여러 테스트에서 재사용
        // 단, 상태를 변경하지 않는 경우에만
        
        when(databaseService.getUserCount()).thenReturn(100L);
        
        assertEquals(100L, userService.getTotalUsers());
        assertEquals(100L, userService.getTotalUsers());  // 같은 mock 재사용
    }
}
```

## 마무리

Mockito는 TDD와 단위 테스트에서 핵심적인 역할을 하는 강력한 도구입니다.

**핵심 포인트:**
- **적절한 테스트 더블 선택**: Mock, Spy, Stub 등을 상황에 맞게 사용
- **깔끔한 API**: 직관적이고 읽기 쉬운 테스트 코드 작성
- **검증의 다양성**: 호출 여부, 순서, 인자 등 다각도 검증
- **Spring과의 통합**: `@MockBean`으로 통합 테스트까지 지원

TDD의 핵심인 "빠른 피드백"과 "의존성 격리"를 Mockito가 완벽하게 지원합니다.