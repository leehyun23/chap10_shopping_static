package com.javalab.boot.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ItemSearchDto {

    private Long id;

    @Builder.Default
    private String itemNm = null;

    private Integer price;

    private Integer stockNumber;

    @Builder.Default
    private String itemDetail = null;
    @Builder.Default
    private String sellStatCd = null;
    @Builder.Default
    private String searchQuery = null;

    private Long categoryId;

    private String sort;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiptDateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiptDateTo;

}