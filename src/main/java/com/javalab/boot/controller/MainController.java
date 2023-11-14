package com.javalab.boot.controller;

import com.javalab.boot.dto.*;
import com.javalab.boot.entity.Category;
import com.javalab.boot.service.CategoryService;
import com.javalab.boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

/**
 * 메인 화면용 컨트롤러
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    @GetMapping("/")
    public String list(@ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       @ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       Model model) {

        PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);
        List<MainItemDto> items = responseDTO.getDtoList();

        // 슬라이드 정보 세팅
        List<SlideDto> slides = Arrays.asList(

                new SlideDto("images/main01.jpg", "  ", " ", "/item/list"),
                new SlideDto("images/main02.jpg", " ", " ", "/item/list"),
                new SlideDto("images/main03.jpg", " ", " ", "/item/list"),
                new SlideDto("images/main04.jpg", " ", " ", "/item/list"),
                new SlideDto("images/main05.jpg", "  ", " ", "/item/list")
                );
        // 데이터베이스에서 카테고리 값을 읽어옵니다.
        List<Category> categories = categoryService.getCategoryOptions();
                model.addAttribute("responseDTO", responseDTO);
                model.addAttribute("items", items);
                model.addAttribute("maxPage", 5); // 나중에 수정
        model.addAttribute("slides", slides); // 슬라이드
        model.addAttribute("categories", categories);
        return "main";
    }

}