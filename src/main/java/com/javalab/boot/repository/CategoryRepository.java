package com.javalab.boot.repository;

import com.javalab.boot.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select c from Category c where c.id =:id")
    Optional<Category> findByIdWithImages(@Param("id") Long id);


}
