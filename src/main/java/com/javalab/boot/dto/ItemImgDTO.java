package com.javalab.boot.dto;

import com.javalab.boot.entity.ItemImg;
import lombok.*;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImgDTO {

    private Long id;
    // 상품 이미지명
    private String imgName;
    // 상품 오리지널 이미지명
    private String oriImgName;
    // 이미지 Url
    private String imgUrl;
    // 대표이미지 여부
    private String repImgYn;

    // 모델매퍼
    private static ModelMapper modelMapper = new ModelMapper();

    // Entity -> Dto 변환
    public static ItemImgDTO of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDTO.class);
    }

}