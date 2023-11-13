package com.javalab.boot.dto;

import com.javalab.boot.constant.OrderStatus;
import com.javalab.boot.entity.Order;
import com.javalab.boot.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 한 건의 주문 내역정보를 전달하기 위한 Data Transfer Object (DTO)
 *  - 주문과 관련된 데이터를 클라이언트에 전달하는 데 사용되며,
 *  - 주문 내역 화면이나 API 응답에서 주문의 상세 정보를 보여주기 위한 용도.
 *  - 여러건의 주문 Item을 가질 수 있다.
 */
@Getter @Setter
public class OrderHistDto {

    private Long orderId;               //주문아이디
    private String orderDate;           //주문날짜
    private OrderStatus orderStatus;    //주문 상태
    // 주문 Item List 저장용 변수
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    // 추가(전체 주문금액)
    private Long totalAmount = 0L;  // Added field to store total amount

    /**
     * 생성자
     *  - 생성자 파라미터로 Order 엔티티를 받아서
     *    거기서 값을 꺼내서 자신의 멤버에 할당함.
     *    즉, 엔티티 -> Dto 변환.
     * @param order
     */
    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();

        // 추가
        for(OrderItem orderItem : order.getOrderItems()) {
            int orderPrice = orderItem.getOrderPrice();
            int count = orderItem.getCount();
            this.totalAmount += (orderPrice * count);
        }
    }

    // 주문 Item 추가
    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }

}