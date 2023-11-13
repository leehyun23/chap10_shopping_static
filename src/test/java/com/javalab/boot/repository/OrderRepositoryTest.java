package com.javalab.boot.repository;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.constant.OrderStatus;
import com.javalab.boot.entity.Item;
import com.javalab.boot.entity.Member;
import com.javalab.boot.entity.Order;
import com.javalab.boot.entity.OrderItem;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
@Log4j2
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    /**
     * [주문 데이터 생성]
     * Order 엔티티의 저장으로 OrderItem 엔티티도 영속화됨
     *  - 영속성 전이, 상위 객체의 변화가 하위 객체에 영향.
     *  - Order에 cascade = CascadeType.ALL 설정해놓음.
     */
    @Disabled
    @Test
    @Commit
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {

        // 1. 데이터베이스에서 Member id가 1L인  조회
        Optional<Member> result = memberRepository.findById(1L);
        Member member = result.orElseThrow();

        // 2. Order 생성 (주문)
        Order order = Order.builder()
                .member(member) // 주문 회원 설정
                .orderDate(LocalDateTime.now()) // 주문일 설정
                .orderStatus(OrderStatus.ORDER) // 주문 상태 설정
                .build();

        // Item table에 item_id가 1,2,3 번 상품이 있어야 함.
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


    /**
     * 주문번호로 조회
     */
    @Disabled
    @Test
    @DisplayName("주문 및 주문 항목 조회 테스트")
    public void findOrderWithOrderIdTest() {
        // 주어진 주문 ID를 가진 주문 엔티티 조회
        Long orderId = 1L; // 실제로 등록된 주문번호(키)
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다. 주문 ID: " + orderId));

        // 주문(Ordr) 정보 출력
        log.info("주문 ID: " + order.getId());
        log.info("주문 회원ID: " + order.getMember().getEmail());
        log.info("주문 날짜: " + order.getOrderDate());
        log.info("주문 상태: " + order.getOrderStatus());

        // 주문 항목(Order Item) 정보 출력
        log.info("주문 항목 리스트:");
        for (OrderItem orderItem : order.getOrderItems()) {
            log.info(" - 상품명: " + orderItem.getItem().getItemNm());
            log.info("   주문 수량: " + orderItem.getCount());
            log.info("   주문 가격: " + orderItem.getOrderPrice());
        }

        // 검증: 실제로 주문 항목이 존재하는지 확인
        assertFalse(order.getOrderItems().isEmpty());
    }
    @Disabled
    @Test
    @DisplayName("특정 사용자의 주문 목록 조회 테스트")
    public void findOrdersByMemberTest() {
        // 데이터베이스에서 이메일을 갖고 있는 회원 조회
        String email = "user93@zerock.org";
        Member member = memberRepository.findByEmail(email);

        // 회원 객체로 주문 목록 조회
        List<Order> orders = orderRepository.findByMemberEmail(member.getEmail());

        // 조회된 주문 목록 출력
        for (Order order : orders) {
            System.out.println("주문 ID: " + order.getId());
            System.out.println("주문 날짜: " + order.getOrderDate());
            System.out.println("주문 상태: " + order.getOrderStatus());
            // 주문 항목 정보 출력 등...
        }

        // 검증: 주문 목록이 실제로 존재하는지 확인
        assertFalse(orders.isEmpty());
    }





    // 샘플 Item 생성
    public Item createItem(){
        Item item = Item.builder()
                .id(109L) // DB에 있는 상품번호
                .itemNm("새로운 상품4.1")
                .price(10000)
                .itemDetail("새로운 상품4 상세설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(10)
                .receiptDate(LocalDate.now())
                .build();
        return item;
    }

    /**
     * 주문생성 메소드
     * @return
     */
    public Order createOrder(){
        Order order = new Order();
        for(int i=0;i<3;i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);
        order.setMember(member);
        orderRepository.save(order);
        return order;
    }
    @Disabled
    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        //em.flush();
    }
    @Disabled
    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        log.info("Order class : " + orderItem.getOrder().getClass());
        log.info("===========================");
        orderItem.getOrder().getOrderDate();
        log.info("===========================");
    }

}