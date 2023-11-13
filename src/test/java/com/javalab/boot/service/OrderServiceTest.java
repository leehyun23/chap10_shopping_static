package com.javalab.boot.service;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.constant.OrderStatus;
import com.javalab.boot.dto.OrderDto;
import com.javalab.boot.entity.Item;
import com.javalab.boot.entity.Member;
import com.javalab.boot.entity.Order;
import com.javalab.boot.entity.OrderItem;
import com.javalab.boot.repository.ItemRepository;
import com.javalab.boot.repository.MemberRepository;
import com.javalab.boot.repository.OrderRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    // 아이템을 생성해서 영속화 하고 그 객체를 반환
    public Item getItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setReceiptDate(LocalDate.now());
        return itemRepository.save(item);
    }

    // 샘플 사용자 영속화후 객체 반환
    public Member saveMember(){
        Member member = new Member();
        member.setEmail("dragon@test.com");
        return memberRepository.save(member);
    }
    
    // 주문
    @Disabled
    @Test
    @Commit
    @DisplayName("주문 테스트")
    public void order(){
        Item item = getItem();  // 아이템 준비
        Member member = saveMember(); // Member 준비
        // 주문 객체 생성
        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10); // 주문수량 10개로 세팅
        orderDto.setItemId(item.getId()); // 영속화시킨 아이템ID(상품번호) 세팅

        // 서비스 레리어의 주문 메소드 호출(영속화후 주문번호 반환) 
        Long orderId = orderService.order(orderDto, member.getEmail());
        
        // 영속화시킨 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new EntityNotFoundException());

        // 영속화된 주문items 조회
        List<OrderItem> orderItems = order.getOrderItems();

        // 주문 item 숫자
        int totalPrice = orderDto.getCount() * item.getPrice();

        assertEquals(totalPrice, order.getTotalPrice());
    }

    @Disabled
    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder(){
        Item item = getItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId = orderService.order(orderDto, member.getEmail());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
        assertEquals(100, item.getStockNumber());
    }

}