package junghun.tdd.spring.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    INIT,
    CANCLED,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    RECEIVED,
    COMPLETED;



}
