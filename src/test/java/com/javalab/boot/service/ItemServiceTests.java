package com.javalab.boot.service;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.*;
import com.javalab.boot.repository.ItemRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Item Service 단위 테스트 케이스
 */
@SpringBootTest
@Log4j2
public class ItemServiceTests {

    @Autowired
    private ItemService itemService;

    // Item 등록 테스트
    @Disabled
    @Test
    public void registerTest() {
        // ItemFormDTO 객체 생성
        ItemFormDTO itemFormDTO = ItemFormDTO.builder()
                .itemNm("새로운 상품명")
                .itemDetail("새로운 상품명 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .price(55000)
                .stockNumber(500)
                .receiptDate(LocalDate.now()).build();

        Long item_id = itemService.register(itemFormDTO);
        log.info("저장한 엔티티의 item_id: " + item_id);
    }

    // Item + Item Image 함께 등록
    @Disabled
    @Test
    public void registerWithImageTest() {
        // ItemFormDTO 객체 생성
        ItemFormDTO itemFormDTO = ItemFormDTO.builder()
                .itemNm("새로운 상품명-1")
                .itemDetail("새로운 상품명-1 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .price(55000)
                .stockNumber(500)
                .receiptDate(LocalDate.now())
                .build();

        // Item Image 세팅(3개)
        itemFormDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID() + "_aaa.jpg",
                        UUID.randomUUID() + "_bbb.jpg",
                        UUID.randomUUID() + "_ccc.jpg"
                ));

        Long item_id = itemService.register(itemFormDTO);
        //log.info("item_id: " + item_id);
    }


    // Item 한개 조회(상세보기)
    @Disabled
    @Test
    public void readOneTest() {
        // 존재하는 itemId 값을 사용하여 테스트
        Long itemId = 107L;  // 실제 데이터베이스에 존재하는 값
        ItemFormDTO itemFormDTO = itemService.readOne(itemId);
        log.info(itemFormDTO);

        Assertions.assertNotNull(itemFormDTO);
        Assertions.assertEquals(itemId, itemFormDTO.getId());
    }

    /**
     * 삭제
     */
    @Disabled
    @Test
    public void removeAllTest(){
        itemService.remove(107L);
    }


    // 게시물 목록 조회 - 검색어 없이 페이징만
//    @Disabled
//    @Test
//    public void listTest() {
//        // 화면에서 넘어온 정보라고 가정 PageRequestDTO 객체 생성
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
//                .type("nds") // name, detail, sellStatus
//                .keyword("상품")// 검색어
//                .page(1) // 요청 페이지
//                .size(10) // 페이지당 게시물수
//                .build();
//
//        // 조회 결과를 PageResponseDTO에 저장.
//        PageResponseDTO<ItemFormDTO> responseDTO = itemService.list(pageRequestDTO);
//
//        List<ItemFormDTO> boardDTOList = responseDTO.getDtoList();
//        boardDTOList.stream().forEach(b->log.info(b.toString()));
//        //log.info(responseDTO);
//    }


    // 게시물 목록 조회 - 검색 조건 포함
    @Disabled
    @Test
    public void itemTest() {
        // 화면에서 넘어온 정보라고 가정 PageRequestDTO 객체 생성
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("nds") // name, detail, sellStatus
                .keyword("상품")// 검색어
                .page(1) // 요청 페이지
                .size(10) // 페이지당 게시물수
                .build();

        // 조회 결과를 PageResponseDTO에 저장.
        PageResponseDTO<ItemFormDTO> result = itemService.list(pageRequestDTO);

        List<ItemFormDTO> itemFormDTO = result.getDtoList();
        itemFormDTO.stream().forEach(item -> {
            if(item.getFileNames() != null)
            log.info(item.getId() + " 의 첨부이미지수 : " + item.getFileNames().size());
            log.info("---------------------------------------------------------");
        });

    }

    /*

    @Disabled
    @Test
    public void testListWithAll() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                                        .page(1)
                                        .size(10)
                                        .build();

        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {
            log.info(boardListAllDTO.getBno()+":"+boardListAllDTO.getTitle());

            if(boardListAllDTO.getBoardImages() != null) {
                for (BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info(boardImage);
                }
            }
            log.info("-------------------------------");
        });
    }
    */

    /**
     * 메인화면 서비스 레이어 단위테스트
     *  - 데이터베이스(Item, ItemImg)에 조회 조건과 같은 데이터 세팅되어 있어야 함.
     *  - 대표이미지는 left Join 조건에 포함되어야 함.
     */
    @Disabled
    @Test
    public void testSearchMainPage() {
        // 테스트에 사용할 조회 조건 입력
        ItemSearchDto itemSearchDto = ItemSearchDto.builder()
                .itemNm("테스트 상품")
                .build();

        // 페이지 요청 객체 생성
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1) // 요청 페이지
                .size(10) // 페이지당 게시물수
                .build();

        // 실제 메서드 호출
        PageResponseDTO<MainItemDto> responseDTO = itemService.searchMainPage(pageRequestDTO, itemSearchDto);

        responseDTO.getDtoList().forEach(item -> log.info(item.getId() + " " + item.getItemNm() + " " + item.getImgUrl()));
    }
}
