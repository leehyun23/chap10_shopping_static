package com.javalab.boot.repository;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.ItemSearchDto;
import com.javalab.boot.dto.MainItemDto;
import com.javalab.boot.dto.PageRequestDTO;
import com.javalab.boot.dto.PageResponseDTO;
import com.javalab.boot.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Commit;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 단위 테스트 케이스
 *  - 클래스 차원에 @Transactional 걸면 메소드 테스트 후 자동 롤백
 *  - @Commit 있으면 실제 데이터베이스 저장
 */
@SpringBootTest
@Log4j2
@Transactional
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Disabled
    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = Item.builder()
                .itemNm("테스트 상품")
                .price(10000)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .build();
        Item savedItem = itemRepository.save(item);
        log.info(savedItem.toString());
    }

    @Disabled
    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemsTest() {
        IntStream.rangeClosed(1, 100)
            .forEach(i -> {
                Item item = Item.builder()
                        .itemNm("테스트 상품 " + i)
                        .price(10000 + i)
                        .itemDetail("테스트 상품 상세 설명 " + i)
                        .itemSellStatus(ItemSellStatus.SELL)
                        .stockNumber(10+i)
                        .receiptDate(LocalDate.now())
                        .build();

                Item savedItem = itemRepository.save(item);
                log.info(savedItem.toString());
            });
    }

    @Disabled
    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest(){
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품 1");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }
    @Disabled
    @Test
    @DisplayName("상품명 조회 테스트 Like")
    public void findByItemNmLikeTest(){
        List<Item> itemList = itemRepository.findByItemNmContaining("테스트 상품");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }
    @Disabled
    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest(){
        List<Item> itemList = itemRepository
            .findByItemNmOrItemDetail("테스트 상품 1",
                    "테스트 상품 상세 설명 1");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }
    @Disabled
    @Test
    @DisplayName("가격이 1000보다 크고 5000보다 작은 상품")
    public void findByItemBetweenTest(){
        int a = 10000;
        int b = 10010;
        
        List<Item> itemList = itemRepository
                    .findByPriceGreaterThanAndPriceLessThan(a, b);
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Disabled
    @Test
    @DisplayName("@Query 어노테이션을 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Disabled
    @Test
    @DisplayName("@Query 어노테이션을 이용한 상품 조회 테스트")
    public void findByItemDetailAndPriceRange() {

        // 테스트 실행
        List<Item> itemList = itemRepository.findByItemDetailAndPriceRange("테스트 상품 상세 설명", 10000, 10010);

        // 검증
        for (Item item : itemList) {
            log.info(item.toString());
        }
    }

    @Disabled
    @Test
    @DisplayName("@Query 어노테이션을 이용한 상품 조회 테스트")
    public void findByItemDetailNativeTest(){
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList){
            log.info(item.toString());
        }
    }

    @Disabled
    @Test
    public void findByIdTest() {
        Long id = 100L;

        Optional<Item> result = itemRepository.findById(id);
        if(result.isPresent()){
            Item item = result.get();
            log.info("여기는 findByIdTest item : " + item);
        }else{
            log.info("해당 상품이 존재하지 않습니다.");
        }
    }

    @Disabled
    @Test
    public void paginationTest() {

        // 1. 페이징을 위한 조건 생성(0-첫번째 페이지, 10-10개 게시물, 정렬컬럼, 정렬방식
        Pageable pageable = PageRequest.of(0,10, Sort.by("id").descending());

        Page<Item> result = itemRepository.findAll(pageable);

        // 페이징 관련 여러 정보를 담고 있음.
        log.info("total count: " + result.getTotalElements());
        log.info( "total pages:" + result.getTotalPages());
        log.info("page number: " + result.getNumber());
        log.info("page size: " + result.getSize());

        List<Item> itemList = result.getContent();
        itemList.forEach(item -> log.info(item));
    }


    /****************************************
     * 수정
     ****************************************/
    @Disabled
    @Test
    public void modifyTest() {
        Long bno = 100L;
        Optional<Item> result = itemRepository.findById(bno);
        Item item = result.orElseThrow();
        item.setItemNm("수정된 상품명");
        item.setItemDetail("수정된 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
        itemRepository.save(item);
    }

    /****************************************
     * 삭제
     ****************************************/
    @Disabled
    @Test
    public void testDelete() {
        Long bno = 1L;
        itemRepository.deleteById(bno);
    }

    /****************************************
     * Item 엔티티 + ItemImg 엔티티 동시에 생성
     ****************************************/
    @Disabled
    @Test
    public void itemAndImgInsertTest() {
        Item item = Item.builder()
                .itemNm("세탁기")
                .itemDetail("참좋은 세탁기")
                .itemSellStatus(ItemSellStatus.SELL)
                .price(3000000)
                .stockNumber(1000)
                .build();

        // 상품 이미지 엔티티 3개 생성
        for (int i = 0; i < 3; i++) {
            // 첫 번째 이미지대표 이미지로 설정
            String repimgYn = (i == 0) ? "Y" : "N";
            item.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg", repimgYn);
        }
        // 영속화
        itemRepository.save(item);
    }

    /**
     * Item + Item Image 조회
     *  - findById() 사용
     */
    @Test
    @Transactional
    @Disabled
    public void readItemWithImageTest() {
        // 실제 상품 번호
        Optional<Item> result = itemRepository.findById(102L);
        Item item = result.orElseThrow();

        log.info("조회한 상품의 이미지 getImageSet(): " + item.getImageSet());
    }

    /**
     * Eager Loading 테스트
     * - findByIdWithImages() 사용자 정의 메소드 사용
     * - 한번에 Item ItemImg Left Join 으로 한 번에 실행
     */
    @Disabled
    @Test
    public void readItemWithImageEagerTest() {

        // DB에 존재하는 Item id 테스트
        Optional<Item> result = itemRepository.findByIdWithImages(106L);

        Item item = result.orElseThrow();

        for (ItemImg itemImg : item.getImageSet()) {
            log.info(itemImg);
        }
    }

    /**
     * Item + Item Image 수정
     */
    @Disabled
    @Transactional
    @Commit
    @Test
    public void modifyImageTest() {

        // 조회하면 영속성 컨텍스트에 Item 엔티티가 영속화됨.
        Optional<Item> result = itemRepository.findByIdWithImages(107L);

        Item item = result.orElseThrow();

        //기존의 첨부파일들은 삭제
        item.clearImages();

        //새로운 첨부파일 등록(기존 파일 삭제후 등록됨)
        for (int i = 0; i < 2; i++) {
            // 첫 번째 이미지대표 이미지로 설정
            String repimgYn = (i == 0) ? "Y" : "N";
            item.addImage(UUID.randomUUID().toString(), "업데이트파일" + i + ".jpg", repimgYn);
        }
        itemRepository.save(item);
    }

    /**
     * Item + Item Image 삭제
     */
    @Disabled
    @Test
    @Transactional
    @Commit
    public void removeAllTest() {

        Long item_id = 102L;
        itemRepository.deleteById(item_id);

    }

    /**
     * 복잡한 검색 조건으로 상품 목록 조회
     */
    @Disabled
    @Test
    public void testSearchItems() {
        // 테스트에 사용할 입력 매개변수 설정
        ItemSearchDto searchDto = new ItemSearchDto();
        searchDto.setItemNm("테스트 상품 98");
        searchDto.setItemDetail("테스트 상품 상세 설명 98");
        searchDto.setPrice(10098); // 가격 검색 조건
        searchDto.setStockNumber(100); // 재고 수량 검색 조건
        searchDto.setSellStatCd("SELL"); // 판매 상태 검색 조건
//        searchDto.setSellStatCd("SOLD_OUT"); // 판매 상태 검색 조건
        searchDto.setReceiptDateFrom(LocalDate.parse("2023-11-02")); // 입고일 시작 범위 검색 조건
        searchDto.setReceiptDateTo(LocalDate.parse("2023-11-02")); // 입고일 종료 범위 검색 조건

        //Pageable pageable = PageRequest.of(0, 10); // 페이지 및 페이지 크기 설정
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));

        // 실제 메서드 호출
        Page<Item> result = itemRepository.searchItems(pageable, searchDto);

        // 검증: 예상 결과와 실제 결과가 일치하는지 확인
        assertTrue(result.getContent().size() > 0);

    }

    /**
     * 상품 판매 상태(SellStatus)를 그룹화하여 가져오는 테스트 메소드
     */
    @Disabled
    @Test
    public void testFindSellStatus() {
        // 판매 상태 목록 조회
        List<ItemSellStatus> sellStatusList = itemRepository.findSellStatus();

        // 검증: 판매 상태 목록이 null이 아니고 비어있지 않아야 함
        assertNotNull(sellStatusList);
        assertFalse(sellStatusList.isEmpty());

        // 판매 상태 목록 출력
        for (ItemSellStatus sellStatus : sellStatusList) {
            log.info("Sell Status: " + sellStatus);
        }
    }

    @Disabled
    @Test
    @Transactional
    public void fineAllItemsReceiptDateDesc() {
        // 페이지 요청 객체 생성, 첫 페이지의 10개 항목, id 기준 내림차순 정렬
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("id").descending());

        // 메소드 호출
        Page<Item> page = itemRepository.findAllByOrderByIdDesc(pageRequest);
        page.getContent().forEach(item -> log.info(item.toString()));

        assertThat(page).isNotEmpty();
    }

    @Disabled
    @Test
    public void testSearchMainPage() {
        // 테스트에 사용할 입력 매개변수 설정
        ItemSearchDto itemSearchDto = ItemSearchDto.builder()
                                        .itemNm("테스트 상품 98")
                                        .build();

        //Pageable pageable = PageRequest.of(0, 10); // 페이지 및 페이지 크기 설정
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));

        // 실제 메서드 호출
        Page<MainItemDto> result = itemRepository.searchMainPage(pageable, itemSearchDto);

        result.getContent().forEach(mi -> log.info(mi.getId() + " " + mi.getItemNm() + " " + mi.getImgUrl()));

        // 검증: 예상 결과와 실제 결과가 일치하는지 확인
        assertTrue(result.getContent().size() > 0);

    }
}