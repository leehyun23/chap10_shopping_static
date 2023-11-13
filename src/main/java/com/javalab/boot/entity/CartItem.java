package com.javalab.boot.entity;

import lombok.*;

import javax.persistence.*;

/**
 * 카트 아이템 엔티티
 *  - 하나의 카트에 여러 카트 아이템이 존재할 수 있다.
 *  - 멤버 : Cart cart, Item item, 주문수량
 */
@Entity
@Getter @Setter
@Table(name="cart_item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count; // 주문 수량

    /**
     * 전달받은 장바구니(cart_id)와 Item(item_id)로 장바구니 Item 생성
     * @param cart
     * @param item
     * @param count
     */
    public static CartItem createCartItem(Cart cart,
                                          Item item,
                                          int count) {
        CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .item(item)
                            .count(count)
                            .build();

        //CartItem cartItem = new CartItem();
        //cartItem.setCart(cart);
        //cartItem.setItem(item);
        //cartItem.setCount(count);

        return cartItem;
    }

    /**
     * 기존에 담겨 있는 상품을 또 장바구니에 넣을때 기존 수량 증가
     * @param count
     */
    public void addCount(int count){
        this.count += count;
    }

    public void updateCount(int count){
        this.count = count;
    }

}