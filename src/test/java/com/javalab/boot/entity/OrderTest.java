package com.javalab.boot.entity;

import com.javalab.boot.constant.OrderStatus;
import com.javalab.boot.repository.ItemRepository;
import com.javalab.boot.repository.MemberRepository;
import com.javalab.boot.repository.OrderItemRepository;
import com.javalab.boot.repository.OrderRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    // Item 생성 메소드
//    public Item createItem(){
//        Item item = Item.builder()
//                .itemNm("테스트 상품")
//                .price(10000)
//                .itemDetail("상세설명")
//                .itemSellStatus(ItemSellStatus.SELL)
//                .stockNumber(100)
//                .receiptDate(LocalDate.now())
//                .build();
//        return item;
//    }

    /**
     * [주문 데이터 생성]
     * Order 엔티티의 저장으로 OrderItem 엔티티도 영속화됨
     *  - 영속성 전이, 상위 객체의 변화가 하위 객체에 영향.
     *  -  Order에 cascade = CascadeType.ALL 설정해놓음.
     */
    @Disabled
    @Test
    @Commit
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {

        // 1. 데이터베이스에서 id가 1L인 Member 조회
        Optional<Member> result = memberRepository.findById(1L);
        Member member = result.orElseThrow();

        // 2. Order 생성 (주문)
        Order order = Order.builder()
                .member(member) // 주문 회원 설정
                .orderDate(LocalDateTime.now()) // 주문일 설정
                .orderStatus(OrderStatus.ORDER) // 주문 상태 설정
                .build();

        // Item table에 1,2,3 번 상품이 있어야 함.
        for(int i=0; i<3; i++){
            // 3. Item 생성(디비에서 로드, 영속화)
            Optional<Item> itemResult = itemRepository.findById(1L + i);
            Item item = itemResult.orElseThrow();

            // 3.1. OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .item(item) // 위에서 만든 Item을 OrderItem에 세팅
                    .count(1 + i) // 주문수량
                    .orderPrice(1500) // 주문단가
                    .order(order)   // 상위 객체와 연결
                    .build();

            // 3.2. 위에서 생성한 OrderItem을 상위 객체의 orderItems에 추가
            order.getOrderItems().add(orderItem);
        }

        // 4. 상위 객체 영속화(Order 저장, 주문아이템 함께 저장됨)
        orderRepository.save(order);

        // 5. 저장된 Order 조회
        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다."));

        assertEquals(3, savedOrder.getOrderItems().size());
    }

//    public Order createOrder(){
//        Order order = new Order();
//        for(int i=0;i<3;i++){
//            Item item = createItem();
//            itemRepository.save(item);
//            OrderItem orderItem = new OrderItem();
//            orderItem.setItem(item);
//            orderItem.setCount(10);
//            orderItem.setOrderPrice(1000);
//            orderItem.setOrder(order);
//            order.getOrderItems().add(orderItem);
//        }
//        Member member = new Member();
//        memberRepository.save(member);
//        order.setMember(member);
//        orderRepository.save(order);
//        return order;
//    }

//    @Test
//    @DisplayName("고아객체 제거 테스트")
//    public void orphanRemovalTest(){
//        Order order = this.createOrder();
//        order.getOrderItems().remove(0);
//        //em.flush();
//    }
//
//    @Test
//    @DisplayName("지연 로딩 테스트")
//    public void lazyLoadingTest(){
//        Order order = this.createOrder();
//        Long orderItemId = order.getOrderItems().get(0).getId();
//        //em.flush();
//        //em.clear();
//        OrderItem orderItem = orderItemRepository.findById(orderItemId)
//                .orElseThrow(EntityNotFoundException::new);
//        System.out.println("Order class : " + orderItem.getOrder().getClass());
//        System.out.println("===========================");
//        orderItem.getOrder().getOrderDate();
//        System.out.println("===========================");
//    }

}