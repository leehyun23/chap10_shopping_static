package com.javalab.boot.repository.search;

import com.javalab.boot.dto.BoardListAllDTO;
import com.javalab.boot.dto.BoardListReplyCountDTO;
import com.javalab.boot.dto.ItemSearchDto;
import com.javalab.boot.dto.MainItemDto;
import com.javalab.boot.entity.Board;
import com.javalab.boot.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemSearch {
    // 조회조건 없이 페이징만
    Page<Item> search(Pageable pageable);
    // 조회조건으로 검색
    Page<Item> searchCondition(String[] types, String keyword, Pageable pageable);

    // 복잡한 조건 검색
    Page<Item> searchItems(Pageable pageable, ItemSearchDto itemSearchDto);

    // 추가 메인화면용
    Page<MainItemDto> searchByComplexConditions(Pageable pageable, ItemSearchDto itemSearchDto);

    // 추가 메인화면용
    Page<MainItemDto> searchMainPage(Pageable pageable, ItemSearchDto itemSearchDto);


}
