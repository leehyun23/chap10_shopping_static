package com.javalab.boot.controller;

import com.javalab.boot.dto.CartDetailDto;
import com.javalab.boot.dto.CartItemDto;
import com.javalab.boot.dto.CartOrderDto;
import com.javalab.boot.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

/**
 * 장바구니(카트) 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;

    /**
     * 카트 아이템 저장
     * @param cartItemDto
     * @param bindingResult
     * @param principal
     * @ResponseBody는 메서드가 HTTP 응답 본문(response body)에 직접 데이터를 쓰기 위해 사용
     *  -즉, view가 필요 없음.
     *  ResponseEntity는 Spring에서 HTTP 요청에 대한 응답 데이터를 포함하는 클래스
     *   - 개발자는 응답 상태 코드, 헤더, 응답 본문 등을 커스터마이즈하여 제공할 수 있습니다.
     *  @RequestBody:
     *   - 이 어노테이션이 붙은 파라미터는 HTTP 요청의 본문(body)에 담긴 내용을 Java 객체로 매핑 해줌.
     */
    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(
                        @RequestBody @Valid CartItemDto cartItemDto,
                        BindingResult bindingResult,
                        Principal principal){

        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName();
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    /**
     * 카트 아이템 조회
     * @param principal
     * @param model
     * principal : UsernamePasswordAuthenticationToken [Principal=org.springframework.security.core.userdetails.User [Username=user93@zerock.org, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_ADMIN]], Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=FCA590B271CE9C212DBAEEDED6622496], Granted Authorities=[ROLE_ADMIN]]
     * @return
     */
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        log.info("principal : " + principal.getName());
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartDetailList);
        return "cart/cartList";
//        return "cart/shoping-cart";
    }

    /**
     * 장바구니 상품 수량 업데이트
     * @param cartItemId
     * @param count
     * @param principal
     */
    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        if(count <= 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요", HttpStatus.BAD_REQUEST);
        } else if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 아이템 삭제(화면에서 x 표시 클릭시)
     * @param cartItemId
     * @param principal
     */
    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(
                        @PathVariable("cartItemId") Long cartItemId,
                        Principal principal){

        // 장바구니 아이템 작성자와 삭제하려고 시도하는 사용자의 같음 유무 확인
        if(!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 카트 아이템 삭제
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    /**
     * 장바구니 목록에서 여러건 주문시 호출
     * cartOrderDto : [{"cartItemId":"56"},{"cartItemId":"55"}]
     * @param cartItemIds // 예: cartItemIds -> [56, 55]
     * @param principal
     */
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(
                                        //@RequestBody CartOrderDto cartOrderDto,
                                        @RequestBody List<Long> cartItemIds,
                                        Principal principal){

        log.info("cartItemIds : " + cartItemIds);

        if(cartItemIds == null || cartItemIds.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요", HttpStatus.FORBIDDEN);
        }

        // 주문 카트 item 갯수만큼 반복하면서 카트 아이템 소유자와 주문자가 같은지 확인
        for (Long cartItemId : cartItemIds) {
            if(!cartService.validateCartItem(cartItemId, principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        // 카트 아이템 주문처리
        Long orderId = cartService.orderCartItem(cartItemIds, principal.getName());
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}