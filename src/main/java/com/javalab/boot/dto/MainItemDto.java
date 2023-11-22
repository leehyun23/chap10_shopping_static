
package com.javalab.boot.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 메인화면 관련된 정보를 보관하고 또 전달하는 클래스
 */

@Getter
@Setter
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
    private List<ItemImgDTO> itemImgList;

    public String getImgUrl() {
        return this.uuid + "_" + this.fileName;
    }
}
