package com.javalab.boot.controller;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.ItemFormDTO;
import com.javalab.boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*********************************************************
 * Item Controller 상품의 C/R/U/D
 ********************************************************/
@Controller
@RequestMapping("/item")
@RequiredArgsConstructor
@Log4j2
public class ItemController {

    private final ItemService itemService;

    /**
     * 공통메소드
     *  - ItemSellStatus enum 타입
     *  - sellStatus라는 이름으로 판매상태 정보를 세팅해서 Model에 저장해줌.
     */
    @ModelAttribute("sellStatus")
    public List<ItemSellStatus> getSellStatus() {
        // 데이터베이스에서 판매 상태 값을 읽어옵니다.
        List<ItemSellStatus> sellStatus = itemService.getSellStatusOptions();
        log.info("sellStatus.size() : " + sellStatus.size());
        return sellStatus;
    }



    // 사용자가 상품 주문하기 위해서 보는 상품 상세 화면
    @GetMapping(value = "/view/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDTO itemFormDto = itemService.readOne(itemId);
        model.addAttribute("item", itemFormDto);
        return "/item/itemDetail";
    }

}
