package com.javalab.boot.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 장바구니 엔티티
 *  - cartId, Membmer, cartItems
 */
@Entity
@Table(name = "cart")
@Getter @Setter
@ToString(exclude = "cartItems")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    // 카트 List<CartItem> cartItems
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 10) // CartItem을 즉시 로딩할 때, 지정된 크기만큼 미리 로딩합니다.
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * 특정 회원의 장바구니 생성
     * @param member
     * @return
     */
    public static Cart createCart(Member member){
        Cart cart = Cart.builder()
                .member(member)
                .build();

        //Cart cart = new Cart();
        //cart.setMember(member);
        return cart;
    }

}