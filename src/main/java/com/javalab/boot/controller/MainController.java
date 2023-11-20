package com.javalab.boot.controller;

import com.javalab.boot.dto.*;
import com.javalab.boot.entity.Category;
import com.javalab.boot.service.CategoryService;
import com.javalab.boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;

/**
 * 메인 화면용 컨트롤러
 */
@Log4j2
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;
    private final CategoryService categoryService;

//    @GetMapping()
    // 메인 화면 및 상품 목록 조회
    @GetMapping({"/", "/category"})
    public String list(@PathVariable(required = false) Long categoryId,String sort,
                       @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       @ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       Model model) {


        // 카테고리 ID가 경로에 제공되는지 확인
        if (categoryId != null) {
            // 카테고리 ID가 제공될 때의 로직 처리

            PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);
            List<MainItemDto> items = responseDTO.getDtoList();
            log.info("카테고리 ID 조회: " + itemSearchDto.getCategoryId());
    }else {
            // 기본 경로인 "/"에 대한 로직 처리
            PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);
            List<MainItemDto> items = responseDTO.getDtoList();

            // 모델에 아이템 추가
            model.addAttribute("responseDTO", responseDTO);
            model.addAttribute("items", items);
            model.addAttribute("maxPage", 5); // 이후 수정 예정
        }

        //슬라이드
        List<SlideDto> slides = Arrays.asList(
                new SlideDto("images/main01.jpg", "  ", " ", "/item/list"),
                new SlideDto("images/main02.jpg", " ", " ", "/item/list"),
                new SlideDto("images/main03.jpg", " ", " ", "/item/list"),
                new SlideDto("images/main04.jpg", " ", " ", "/item/list"),
                new SlideDto("images/main05.jpg", "  ", " ", "/item/list")
        );
        List<Category> categories = categoryService.getCategoryOptions();

        model.addAttribute("slides", slides);
        model.addAttribute("categories", categories);


        PageResponseDTO<MainItemDto> pageResponseDTO = itemService.searchMainPageByLowPrice(pageRequestDTO, itemSearchDto);

        log.info("pageResponseDTO :" +pageResponseDTO);



        return "main";
    }

    @GetMapping("/items/low-price")
    public String listByLowPrice(Model model, PageRequestDTO pageRequestDTO, ItemSearchDto itemSearchDto, String sort) {
        // 매인화면 상품
        PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPageByLowPrice(pageRequestDTO, itemSearchDto);
        List<MainItemDto> items = responseDTO.getDtoList();

        // 모델에 아이템 추가
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("maxPage", 5); // 이후 수정 예정


        List<Category> categories = categoryService.getCategoryOptions();
        model.addAttribute("categories", categories);

        // 가격이 낮은 순으로 상품 조회 (검색 조건 적용)
        PageResponseDTO<MainItemDto> pageResponseDTO = itemService.searchMainPageByLowPrice(pageRequestDTO, itemSearchDto);
        List<MainItemDto> itemsByLowPrice = pageResponseDTO.getDtoList();


        model.addAttribute("items", itemsByLowPrice);

        return "main";
    }

}
