package com.javalab.boot.controller;

import com.javalab.boot.entity.Category;
import com.javalab.boot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/category")
@RequiredArgsConstructor
@Log4j2
@Controller
public class CategoryController {
    private final CategoryService categoryService;

    public List<Category> getcategory() {
        // 데이터베이스에서 카테고리 값을 읽어옵니다.
        List<Category> category = categoryService.getCategoryOptions();
        log.info("category : " + category.size());
        return category;
    }

}
