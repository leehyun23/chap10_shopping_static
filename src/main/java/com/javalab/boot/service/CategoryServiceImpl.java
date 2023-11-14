package com.javalab.boot.service;

import com.javalab.boot.dto.ItemFormDTO;
import com.javalab.boot.entity.Category;
import com.javalab.boot.entity.Item;
import com.javalab.boot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    @Override
    public List<Category> getCategoryOptions() {
        // 레포지토리 레이어를 호출하여 데이터베이스에서 카타고리 값을 가져옵니다.
        List<Category> categoriesOptions = categoryRepository.findAll();
        log.info("categoriesOptions :" + categoriesOptions.size());
        return categoriesOptions;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public ItemFormDTO readOne(Long categoryId) {
//        /*
//          [Category 엔티티 객체 얻기]
//        */
//        Optional<Category> result = categoryRepository.findByIdWithImages(categoryId);
//        Category category = result.orElseThrow();
//
//
//        return ;
//    }
}
