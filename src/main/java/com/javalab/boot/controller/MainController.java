package com.javalab.boot.controller;

import com.javalab.boot.dto.*;
import com.javalab.boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

/**
 메인 화면용 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping("/")
    public String list(@ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       @ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       Model model){

        PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);
        List<MainItemDto> items = responseDTO.getDtoList();

        // 슬라이드 정보 세팅
        List<SlideDto> slides = Arrays.asList(
                new SlideDto("images/banner_1.jpg", "2023 Women Collection Spring", "NEW SEASON", "/item/list"),
                new SlideDto("images/banner_2.jpg", "2023 Men New-Season Autumn", "Jackets & Coats",  "/item/list"),
                new SlideDto("images/banner_3.jpg", "2023 Men Collection Winter", "New arrivals",  "/item/list"),
                new SlideDto("images/banner_4.jpg", "2023 Men Collection Winter", "New arrivals",  "/item/list")
        );

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("items", items);
        model.addAttribute("maxPage", 5); // 나중에 수정
        model.addAttribute("slides", slides); // 슬라이드

        return "main";
    }

}