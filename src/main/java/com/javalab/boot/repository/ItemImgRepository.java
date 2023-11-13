package com.javalab.boot.repository;

import com.javalab.boot.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, String> {

//    List<ItemImg> findByItemIdOrderByIdAsc(String itemId);

    /**
     * Item Img 중에서 대표 이미지 찾기(파라미터:ItemId, 대표이미지여부)
     */
    ItemImg findByItemIdAndRepimgYn(Long itemId, String repimgYn);

}