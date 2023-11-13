package com.javalab.boot.repository;

import com.javalab.boot.dto.ItemSearchDto;
import com.javalab.boot.dto.MainItemDto;
import com.javalab.boot.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MainItemRepository {

    // 인기 상품 조회 메서드
    Page<Item> findTop6ByOrderBySaleCountDesc(Pageable pageable);

    // 최신 상품 조회 메서드
    Page<Item> findTop6ByOrderByRegisterDateDesc(Pageable pageable);

}