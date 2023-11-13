package com.javalab.boot.service;

import com.javalab.boot.dto.CartDetailDto;
import com.javalab.boot.dto.CartItemDto;
import com.javalab.boot.dto.CartOrderDto;
import com.javalab.boot.dto.OrderDto;
import com.javalab.boot.entity.Cart;
import com.javalab.boot.entity.CartItem;
import com.javalab.boot.entity.Item;
import com.javalab.boot.entity.Member;
import com.javalab.boot.repository.CartItemRepository;
import com.javalab.boot.repository.CartRepository;
import com.javalab.boot.repository.ItemRepository;
import com.javalab.boot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    /**
     * 화면에서 카트Item과 사용자 이메일을 받아서 카트생성.
     *  - 최초시 카트 먼저 생성하고 그 다음에 카트 아이템 생성 순으로 진행함.
     * @param cartItemDto
     * @param email
     */
    public Long addCart(CartItemDto cartItemDto, String email){

        // 1. 카트에 추가하려는 상품 조회해서 영속화
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException());

        // 2. 카트를 만들 회원 조회해서 영속화
        Member member = memberRepository.findByEmail(email);

        // 3. 현재 상품을 카트에 추가하려는 회원의 카트 조회
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 4. 카트가 없다. 즉 처음 추가하는 상품이다. 고로 카트 먼저 생성
        if(cart == null){
            // 4.1. 해당 회원의 카트 생성
            cart = Cart.createCart(member);
            // 4.2. 카트 영속화, 회원의 카트 생성
            cartRepository.save(cart);
        }

        // 5. 카트 아이템에 지금 추가하려는 상품(item)이 있는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        // 6.1. 이미 카트 아이템에 저장된 상품이면 수량만 증가
        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {    // 6.2. 최초 상품이면 카트 아이템 생성해서 영속화
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    /**
     * 현재 로그인 회원의 장바구니 상품 조회
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    /**
     * 수정하려고 하는 회원과 실제 카트 주인 비교해서 다르면 false 같으면 true
     * @param cartItemId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    /**
     * 카트 아이템 수량 업데이트
     * @param cartItemId
     * @param count
     */
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    /**
     * 카트 아이템 삭제
     * @param cartItemId
     */
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException());
        cartItemRepository.delete(cartItem);
    }

    /**
     * 장바구니 목록에 있는 여러건의 상품이 전달됨.
     * @param cartItemIds
     * @param email
     * @return
     */
    public Long orderCartItem(List<Long> cartItemIds, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        // 1. 전달받은 카트 아이템으로 CartItem 엔티티를 조회하고
        // 2. 그걸 OrderDto 형태로 변환해서 OrderDtoList에 저장.

        for (Long cartItemId : cartItemIds) {
            // 카트item 얻기
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                                .orElseThrow(EntityNotFoundException::new);

            // OrderDto 객체 생성(CartItem -> OrderDto)
            OrderDto orderDto = OrderDto.builder()
                    .itemId(cartItem.getItem().getId())
                    .count(cartItem.getCount())
                    .build();
            orderDtoList.add(orderDto);
        }

        // 3. 저장된 orderDto List를 orders()메소드에 보내서 영속화.
        // 변환된 CartItem 엔티티를 주문처리
        Long orderId = orderService.orders(orderDtoList, email);

        // 장바구니에서는 주문된 상품들 삭제 처리
        for (Long cartItemId : cartItemIds) {
            CartItem cartItem = cartItemRepository
                            .findById(cartItemId)
                            .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}