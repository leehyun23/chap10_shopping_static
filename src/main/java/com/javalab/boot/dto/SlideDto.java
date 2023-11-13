package com.javalab.boot.dto;

import lombok.*;

/**
 * 메인화면 카루셀에 보여질 슬라이드 보관 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public class SlideDto {
    private String imageUrl;
    private String title;
    private String subtitle;
    private String link;

}
