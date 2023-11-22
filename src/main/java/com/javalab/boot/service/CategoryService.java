package com.javalab.boot.service;

import com.javalab.boot.dto.ItemFormDTO;
import com.javalab.boot.entity.Category;

import java.util.List;

public interface CategoryService {
     List<Category> getCategoryOptions();
//     ItemFormDTO readOne(Long categoryId);
}
