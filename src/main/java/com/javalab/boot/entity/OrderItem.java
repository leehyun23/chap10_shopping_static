package com.javalab.boot.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 각 주문에 달린 상품(Item) 보관용 Entity
 */
@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    //주문(연관관계매핑 - Order)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문가격
    private int count; //수량

    /**
     * OrderItem 생성
     *  - 파라미터로 전달된 Item을 자신의 item 속성에 세팅함.
     *    이렇게 되면 상위 객체와 연결되는 통로가 만들어짐.
     */
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = OrderItem.builder()
                .item(item)    // 자신의 item 변수에 상위객체(Item) 주소 할당. 상위객체와 매핑
                .count(count)
                .orderPrice(item.getPrice())
                .build();
        item.removeStock(count);
        return orderItem;
    }

    // 주문금액(수량 * 가격)
    public int getTotalPrice(){
        return orderPrice * count;
    }

    // 주문취소(재고 증가)
    public void cancel() {
        this.getItem().addStock(count);
    }

}