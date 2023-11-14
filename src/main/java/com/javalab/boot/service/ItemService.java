package com.javalab.boot.service;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.*;
import com.javalab.boot.entity.Category;
import com.javalab.boot.entity.Item;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ItemService {
    Long register(ItemFormDTO itemFormDTO);
    ItemFormDTO readOne(Long itemId);
    void modify(ItemFormDTO itemFormDTO);
    void remove(Long itemId);

//    PageResponseDTO<ItemFormDTO> list(PageRequestDTO pageRequestDTO);
    PageResponseDTO<ItemFormDTO> list(PageRequestDTO pageRequestDTO);

    // 변경
    PageResponseDTO<ItemFormDTO> searchList(PageRequestDTO pageRequestDTO, ItemSearchDto itemSearchDto);

    // sellStatus 그룹바이 결과 조회
    List<ItemSellStatus> getSellStatusOptions();



    // 메인 화면
    PageResponseDTO<MainItemDto> searchMainPage(PageRequestDTO pageRequestDTO, ItemSearchDto itemSearchDto);

    // 메인 화면에 카타고리 별로 아이템 조회
    public List<Item> getItemsByCategory(Long categoryId);
}
