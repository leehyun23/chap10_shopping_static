package com.javalab.boot.service;

import com.javalab.boot.dto.OrderDto;
import com.javalab.boot.dto.OrderHistDto;
import com.javalab.boot.dto.OrderItemDto;
import com.javalab.boot.entity.*;
import com.javalab.boot.repository.ItemImgRepository;
import com.javalab.boot.repository.ItemRepository;
import com.javalab.boot.repository.MemberRepository;
import com.javalab.boot.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    /**
     * 주문 1건 처리
     * @param orderDto : 상품번호, 수량
     * @param email : 어느 회원의 주문인지 식별
     */
    public Long order(OrderDto orderDto, String email){

        // 1. 상품 객체 영속화
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상품이 존재하지 않습니다."));

        // 2. 주문회원 객체 영속화(디비에서 로드)
        Member member = memberRepository.findByEmail(email);

        // 3. 주문 ITem List
        List<OrderItem> orderItemList = new ArrayList<>(); // OrderItem 보관 객체

        // OrderItem 객체 얻기(디비에서 로딩한 item 정보, 주문수량)
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        // 4. 주문 객체 생성(회원, OrderItemList)
        Order order = Order.createOrder(member, orderItemList);

        // 5. 주문 객체 영속화
        Order savedOrder = orderRepository.save(order);

        log.info("service savedOrder : " + savedOrder.getOrderItems());

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        orders.stream().forEach(oi -> {log.info("getOrderList orders : " + oi.getOrderDate());});

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                // 주문 Item(상품)의 대표이미지 객체 얻기
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                // 대표이미지 경로와 이름, 대표 이미지가 없으면 기본 이미지 설정
                String imgUrl = (itemImg != null) ? itemImg.getUuid() + "_" + itemImg.getFileName() : "noImage.jpg"; // Adjust "default-image.jpg" as needed.

                OrderItemDto orderItemDto = new OrderItemDto(orderItem, imgUrl);
                // 생성된 OrderItemDto를 주문내역 Dto에 저장
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        log.info("주문건수 : " + orderHistDtos.size());

        /**
         * PageImpl : 왜 PageImpl<T> 형태로 만드나?
         *  - PageImpl 에 목록리스트, 조회할때 사용했던 페이징 조건, 총건수를 전달하면
         *    거기서 화면에서 페이지네이션에 사용할 모든 값들을 자동으로 생성해준다.
         *  - 여기서 목록리스트를 꺼낼때는 getContent()하면 된다.
         */
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    /**
     * 구매를 진행한 회원과 수정하려고 하는 회원의 같음을 비교
     * 즉, 자기 주문만 취소할 수 있어야 하니까.
     * @param orderId
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    /**
     * 장바구니 목록에서 여러 장바구니 아이템(상품)을 받아서 주문 처리(영속화)
     * @param orderDtoList
     * @param email
     */
    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        // 여러개의 장바구니 아이템으로 주문Item 엔티티를 생성하고 그걸
        // 주문아이템 객체로 생성해서 orderItemList에 저장
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        // 주문 객체 생성 영속화.
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

}