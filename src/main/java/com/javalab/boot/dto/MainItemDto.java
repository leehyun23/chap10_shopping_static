package com.javalab.boot.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

/**
 * 메인화면 관련된 정보를 보관하고 또 전달하는 클래스
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainItemDto {

    private Long id;
    private String itemNm;
    private String itemDetail;
    private Integer price;
    private String uuid;
    private String fileName;

//    1. 섬네일이 나오도록
//    public String getImgUrl(){
//        return "s_" + this.uuid + "_" + this.fileName;
//    }

    // 2. 원본 이미지가 나오도록
    public String getImgUrl(){
        // uuid + filename
        return this.uuid + "_" + this.fileName;
    }

}