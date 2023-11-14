package com.javalab.boot.controller;

import com.javalab.boot.dto.*;
import com.javalab.boot.entity.Category;
import com.javalab.boot.entity.Item;
import com.javalab.boot.service.CategoryService;
import com.javalab.boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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


    // 메인 화면 및 상품 목록 조회
    @GetMapping({"/", "/category"})
    public String list(@PathVariable(required = false) Long categoryId,
                       @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       @ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       Model model) {

        log.info("getCategoryId : " + itemSearchDto.getCategoryId());
        // Check if categoryId is provided in the path
        if (categoryId != null) {
            // Handle logic for when categoryId is provided
//            List<MainItemDto> categoryItems = itemService.getItemsByCategory(categoryId); // Assuming you have a method like this in your service
//            model.addAttribute("categoryItems", categoryItems);
            PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);
            List<MainItemDto> items = responseDTO.getDtoList();
        } else {
            // Handle logic for the default "/" path
            PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);
            List<MainItemDto> items = responseDTO.getDtoList();

            // Other logic for the default path

            // Add items to the model
            model.addAttribute("responseDTO", responseDTO);
            model.addAttribute("items", items);
            model.addAttribute("maxPage", 5); // Modify later
        }

        // Other common logic
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

        return "main";
    }

}