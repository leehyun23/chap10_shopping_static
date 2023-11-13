package com.javalab.boot.controller;

import com.javalab.boot.constant.OrderStatus;
import com.javalab.boot.dto.OrderDto;
import com.javalab.boot.dto.OrderHistDto;
import com.javalab.boot.entity.OrderItem;
import com.javalab.boot.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 1건 처리
     * @param orderDto : 커맨드 객체
     * @param bindingResult
     * @param principal
     * @return
     */
    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(
                            @RequestBody @Valid OrderDto orderDto
                            , BindingResult bindingResult,
                            Principal principal){

        log.info("order() itemId : " + orderDto.getItemId());

        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long orderId;

        try {
            // 서비스 메소드 호출
            orderId = orderService.order(orderDto, email);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    /**
     * 구매 이력 조회
     * @param page
     * @param principal
     * @param model
     * @return
     */
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model){

        log.info("orderHist principal.getName() : " + principal.getName() );
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        // 총 결제금액
        Long sum = 0L;
        for (OrderHistDto orderHistDto : ordersHistDtoList) {
            if (orderHistDto.getOrderStatus() == OrderStatus.ORDER) { // 주문취소 상태는 누적에서 제외.
                sum += orderHistDto.getTotalAmount(); // getTotalAmount() method는 OrderHistDto 있음.
            }
        }
        log.info("주문의 총금액 : " + sum);


        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("orderSum", sum);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

//        return "order/orderHist";
        return "order/orderDetails";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId , Principal principal){

        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}