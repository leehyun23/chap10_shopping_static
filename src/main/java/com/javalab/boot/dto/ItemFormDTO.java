package com.javalab.boot.dto;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.entity.Item;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/***********************************************
 * 화면에서 입력한 Item(상품)정보를 받아주는 역할.
 * 엔티티 정보를 담아서 화면에 출력해주는 역할.
 **********************************************/
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Log4j2
public class ItemFormDTO {
    private Long id;
    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;
    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;
    @NotBlank(message = "상품 상세는 필수 입력 값입니다.")
    private String itemDetail;
    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;
    private ItemSellStatus itemSellStatus;
    @NotNull(message = "상품 입고일은 필수 입력 값입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiptDate;
    @NotNull(message = "카타고리 입력은 필수 입력 값입니다.")
    private Long categoryId;
    private List<String> fileNames;
    // 추가
    private Double dcRate; // 할인율
    private Integer saleCount; // 판매수량(인기상품 판단)
    private static ModelMapper modelMapper = new ModelMapper();
    // 화면에서 받아온 Dto -> Entity 변환 메소드
    public Item createItem() {
        Item item = modelMapper.map(this, Item.class);
        log.info("createItem item : " + item);

        // 첨부 이미지 파일이 있을 경우
        if(this.getFileNames() != null){
            List<String> fileNames = this.getFileNames();
            for (int i = 0; i < fileNames.size(); i++) {
                String fileName = fileNames.get(i);
                String[] arr = fileName.split("_");
                String repimgYn = (i == 0) ? "Y" : "N";
                item.addImage(arr[0], arr[1], repimgYn);
            }
        }
        log.info("createItem item.getImageSet : " + item.getImageSet());

        return item;
    }



}