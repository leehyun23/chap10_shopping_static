package com.javalab.boot.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 화면에서 주문(Order)관련 정보를 받고 검증하는 역할
 *  - 상품(Item) 번호화 주문수량을 전달 받음.
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    // Item(상품) 번호
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    // 주문 수량
    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;

}