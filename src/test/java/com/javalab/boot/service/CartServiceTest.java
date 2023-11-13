package com.javalab.boot.service;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.CartItemDto;
import com.javalab.boot.entity.CartItem;
import com.javalab.boot.entity.Item;
import com.javalab.boot.entity.Member;
import com.javalab.boot.repository.CartItemRepository;
import com.javalab.boot.repository.ItemRepository;
import com.javalab.boot.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Log4j2
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setReceiptDate(LocalDate.now());
        return itemRepository.save(item);
    }

    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Disabled
    @Commit
    @Test
    @DisplayName("장바구니 담기 테스트")
    public void addCart(){
        // 1. Item 생성(디비에서 로드, 영속화)
        Optional<Item> itemResult = itemRepository.findById(108L); // 실제 상품(Item)번호
        Item item = itemResult.orElseThrow();

        // 2. 데이터베이스에서 Member id가 1L인  조회
        // Optional<Member> result = memberRepository.findById(1L);
        //Member member = result.orElseThrow();
        Member member = memberRepository.findByEmail("magicoh@naver.com"); // 실제 이메일 번호

        // 3. 장바구니 Item 생성
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);    // 장바구니 수량 세팅
        // 3.1 장바구니(Cart)와 연동(테이블 FK연결)
        cartItemDto.setItemId(item.getId()); // 카트아이템에 상품번호 세팅

        // 4. 생성한 장바구니와 회원정보로 장바구니 영속화(장바구니 아이템 함께 영속화)
        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());

        // 5. 검증(임의로 지정한 상품번호와 그 상품으로 영속화된 Cart Item의 item_id비교)
        assertEquals(item.getId(), cartItem.getItem().getId());
    }

}