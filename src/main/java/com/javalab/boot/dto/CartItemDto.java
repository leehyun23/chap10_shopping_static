package com.javalab.boot.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 상품 상세 페이지에서 장바구니에 담을 상품과 수량을 저장하는 역할
 * 상품을 장바구니에 최초에 넣을 때는 장바구니와 장바구니 아이템을
 * 같이 생성해야 하고 나중에 같은 장바구니에 상품을 추가할 때는
 * 장바구니 아이템의 수량만 증가시키면 된다.
 */
@Getter @Setter
public class CartItemDto {

    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 1개 이상 담아주세요")
    private int count;

}