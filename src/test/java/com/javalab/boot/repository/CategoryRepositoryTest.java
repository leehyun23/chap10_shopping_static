package com.javalab.boot.repository;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.entity.Category;
import com.javalab.boot.entity.Item;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;

@SpringBootTest
@Log4j2
@Transactional
public class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Commit
    @Test
    @DisplayName("상품 저장 테스트")
    public void createCategory(){
        Category category = Category.builder()
                .name("육류")
                .build();
        Category savedCategory = categoryRepository.save(category);
        log.info(savedCategory.toString());
    }

}
