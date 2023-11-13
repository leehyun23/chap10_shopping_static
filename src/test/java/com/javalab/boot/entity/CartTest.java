package com.javalab.boot.entity;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.MemberFormDto;
import com.javalab.boot.repository.CartRepository;
import com.javalab.boot.repository.ItemRepository;
import com.javalab.boot.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Disabled
    @Test
    @Commit
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {
        // 1. 데이터베이스에서 id가 1L인 Member 조회
        Optional<Member> result = memberRepository.findById(1L);
        Member member = result.orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // 2. Cart 생성 (장바구니)
        Cart cart = Cart.createCart(member);

        // 3. CartItem 생성 및 Cart에 연결
        List<Item> cartItems = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            // 3.1. Item 생성(디비에서 로드, 영속화)
            Optional<Item> itemResult = itemRepository.findById(1L + i);
            Item item = itemResult.orElseThrow();

            // 3.2. Cart Item 객체 생성
            CartItem cartItem = CartItem.builder()
                    .item(item)
                    .cart(cart) // 상위 객체인 Cart와 연결
                    .count(1 + i)
                    .build();

            // 3.3. 만들어진 cartItem을 Cart에 추가
            cart.getCartItems().add(cartItem);
        }

        // 4. Cart 영속화
        cartRepository.save(cart);

        // 5. 저장된 Cart 조회
        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        assertEquals(3, savedCart.getCartItems().size());
    }

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }
    @Disabled
    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);
        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        Cart savedCart = cartRepository.findById(cart.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(savedCart.getMember().getId(), member.getId());
    }
}