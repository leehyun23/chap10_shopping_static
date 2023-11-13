package com.javalab.boot.entity;

import com.javalab.boot.constant.OrderStatus;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "orderItems")
public class Order extends BaseEntity {

    // Order key
    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // 주문회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //주문일
    @Column(nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private LocalDateTime orderDate;

    //주문상태
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * 주문Items(연관관계매핑 - OrderItem)
     *  Order 엔티티를 영속화할 때 OrderItem 엔티티도 자동으로
     * 영속화되도록 하려면, Order의 @OneToMany 관계에
     * cascade = CascadeType.ALL을 적용해야.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 10) // CartItem을 즉시 로딩할 때, 지정된 크기만큼 미리 로딩합니다.
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 주문등록 과정에서 주문Item으로 주문Entity 생성.
     * @param member
     * @param orderItemList
     */
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);    // 주문자 정보 세팅

        // 주문Item 갯수만큼 주문Item에 추가
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }

        // 주문상태 세팅
        order.setOrderStatus(OrderStatus.ORDER);
        // 주문일자를 오늘날짜로 세팅
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
}