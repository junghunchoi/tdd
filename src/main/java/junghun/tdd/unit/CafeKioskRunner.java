package junghun.tdd.unit;

import junghun.tdd.unit.beverage.Americano;
import junghun.tdd.unit.beverage.Latte;

public class CafeKioskRunner {
    public static void main(String[] args) {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Latte(), 1);
        System.out.println("카페 키오스크에 라떼가 추가되었습니다.");

        cafeKiosk.add(new Americano(), 1);
        System.out.println("카페 키오스크에 아메리카노가 추가되었습니다.");

        int totalPrice = cafeKiosk.calculateTotalPrice();
        System.out.println("총 가격은 " + totalPrice + "원입니다.");
    }


}
