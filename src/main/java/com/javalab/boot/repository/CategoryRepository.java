package com.javalab.boot.repository;

import com.javalab.boot.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    //카테고리를 그룹화 하여 가져오는 쿼리


}
