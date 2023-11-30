package com.javalab.boot.repository.search;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.BoardListReplyCountDTO;
import com.javalab.boot.dto.ItemSearchDto;
import com.javalab.boot.dto.MainItemDto;
import com.javalab.boot.entity.*;
import com.javalab.boot.repository.ItemRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 1. 이 클래스(ItemSearchImpl)는 Spring Data JPA와 Querydsl을
 *   함께 사용하여 Item 엔티티에 대한 사용자 정의 쿼리 메서드를 제공하는 역할
 * - QuerydslRepositorySupport는 JPQL 쿼리를 쉽게 작성하고
 *  실행할 수 있는 여러 도움 메서드를 제공하는 클래스.
 *  이를 통해 타입 안전한 쿼리를 작성 가능.
 */
@Log4j2
public class ItemSearchImpl extends QuerydslRepositorySupport implements ItemSearch {
    /**
     * QuerydslRepositorySupport는 엔티티 타입에 따른 Querydsl
     * 쿼리를 작성하게 도와주는데, 생성자에서 사용할 엔티티의
     * 타입(Item.class)을 지정합니다. 이로써 Item 엔티티에
     * 대한 Querydsl 쿼리 작성이 가능해집니다.
     */
    public ItemSearchImpl() {
        super(Item.class);
    }
    @Override
    public Page<Item> search(Pageable pageable) {

        QItem qItem = QItem.item;

        JPQLQuery<Item> query = from(qItem);

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.or(qItem.itemNm.contains("상품")); // 상품명 like ...
        booleanBuilder.or(qItem.itemDetail.contains("상품")); // 상품상세설명 like ....

        query.where(booleanBuilder);
        query.where(qItem.id.gt(0L));

        /*
         * 페이징
         * query : JPQL 쿼리객체
         * this.getQuerydsl() : 반환 타입이 Querydsl
         * applyPagination() : Querydsl에 Pageable 적용하여
         *  페이징 처리하는 역할.
         */
        this.getQuerydsl().applyPagination(pageable, query);

        // JPQLQuery 쿼리 실행
        List<Item> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    } // end searchOnlyPaging

    @Override
    public Page<Item> searchCondition(String[] types, String keyword, Pageable pageable) {

        QItem qItem = QItem.item;
        JPQLQuery<Item> query = from(qItem);

        if( (types != null && types.length > 0) && keyword != null ){ //검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); //

            for(String type: types){

                switch (type){
                    case "n":   // 상품명
                        booleanBuilder.or(qItem.itemNm.contains(keyword));
                        break;
                    case "d":   // 상품상세설명
                        booleanBuilder.or(qItem.itemDetail.contains(keyword));
                        break;
                    case "s":   // 판매상태
                        // ItemSellStatus 열거형(Enum)을 사용하여 검색 조건
                        if ("SELL".equals(keyword) || "SOLD_OUT".equals(keyword)) {
                            booleanBuilder.or(qItem.itemSellStatus.eq(ItemSellStatus.valueOf(keyword)));
                        }
                }
            }//end for
            query.where(booleanBuilder);
        }//end if

        query.where(qItem.id.gt(0L));

        this.getQuerydsl().applyPagination(pageable, query);

        // fetch 결과 List<Item>
        List<Item> list = query.fetch();
        // fetchCount 전체 레코드 갯수 가져옴.
        long count = query.fetchCount();
        /*
          Spring Data의 Page 인터페이스의 구현체
          Page 인터페이스는 페이징 처리와 관련된 여러 메서드와
          정보를 제공. PageImpl<>을 사용 페이징된 데이터
          관련된 정보를 캡슐화 함.
         */
        return new PageImpl<>(list, pageable, count);
    }

    /**
     * 상품 목록 상세 조회
     *  - ItemSearchDto(다양한 검색 조건) 사용한 조회
     *  - 다양한 조건으로 상품 목록 검색
     */
    @Override
    public Page<Item> searchItems(Pageable pageable, ItemSearchDto itemSearchDto) {
        QItem qItem = QItem.item;
        JPQLQuery<Item> query = from(qItem);

        BooleanBuilder 조회조건Builder = new BooleanBuilder();

        //// 조회조건
        // 상품명(Like 검색)
        if (itemSearchDto.getItemNm() != null) {
            조회조건Builder.and(qItem.itemNm.like("%" + itemSearchDto.getItemNm() + "%"));
        }
        // 상품 상세 설명(Like 검색)
        if (itemSearchDto.getItemDetail() != null) {
            조회조건Builder.and(qItem.itemDetail.contains(itemSearchDto.getItemDetail()));
        }
        // 상품 가격
        if (itemSearchDto.getPrice() != null) {
            조회조건Builder.and(qItem.price.eq(itemSearchDto.getPrice()));
        }
        // 상품 판매 상태
        if (itemSearchDto.getSellStatCd() != null) {
            조회조건Builder.and(qItem.itemSellStatus.stringValue().eq(itemSearchDto.getSellStatCd()));
        }
        // 재고 수량
        if (itemSearchDto.getStockNumber() != null) {
            조회조건Builder.and(qItem.stockNumber.eq(itemSearchDto.getStockNumber()));
        }
        // Item 입고일 범위 조건 추가
        if (itemSearchDto.getReceiptDateFrom() != null) {
            // receipt_date from 입고일 시작
            String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(itemSearchDto.getReceiptDateFrom());
            StringExpression formatDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", qItem.receiptDate);
            조회조건Builder.and(formatDate.goe(formattedDate));
        }
        if (itemSearchDto.getReceiptDateTo() != null) {
            // receipt_date to 입고일 미만
            String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(itemSearchDto.getReceiptDateTo());
            StringExpression formatDate = Expressions.stringTemplate("FUNCTION('DATE_FORMAT', {0}, '%Y-%m-%d')", qItem.receiptDate);
            조회조건Builder.and(formatDate.loe(formattedDate));
        }
        query.where(조회조건Builder);

        query.where(qItem.id.gt(0L));
        // 페이징 처리
        this.getQuerydsl().applyPagination(pageable, query);

        // fetch 결과와 fetchCount 가져오기
        List<Item> items = query.fetch();
        long totalItemCount = query.fetchCount();

        return new PageImpl<>(items, pageable, totalItemCount);
    }


    /**
     * 메인페이지 조회용 메소드
     * @param pageable
     * @param itemSearchDto
     * @return
     */
    @Override
    public Page<MainItemDto> searchMainPage(Pageable pageable, ItemSearchDto itemSearchDto) {

        QItem qItem = QItem.item;
        QItemImg qItemImg = QItemImg.itemImg;

        /**
         * [데이터를 받아와서 처리할 방법을 미리 설정해놓음]
         * Projections.bean
         *  - select 처리 결과를 MainItemDto 객체로 바인딩해줌(변환)
         */
        JPQLQuery<MainItemDto> query = from(qItem)
                .leftJoin(qItemImg).on(qItem.eq(qItemImg.item).and(qItemImg.repimgYn.eq("Y")))
                .select(Projections.bean(
                        MainItemDto.class,
                        qItem.id,
                        qItem.itemNm,
                        qItem.itemDetail,
                        qItem.price,
                        qItemImg.uuid,
                        qItemImg.fileName)
                );

        // 쿼리 객체에 BooleanBuilder로 설정한 조건 지정
        // 상품명, 상품상세 설명으로 조회 조건만 있음
        BooleanBuilder 조회조건Builder = new BooleanBuilder();

        if (itemSearchDto.getItemNm() != null) {
            조회조건Builder.and(qItem.itemNm.like("%" + itemSearchDto.getItemNm() + "%"));
        }
        // 상품 상세 설명(Like 검색)
        if (itemSearchDto.getItemDetail() != null) {
            조회조건Builder.and(qItem.itemDetail.contains(itemSearchDto.getItemDetail()));
        }


        query.where(조회조건Builder);
        //query.where(qItemImg.repimgYn.eq("Y")); // 대표이미지만 선정

        // 쿼리 객체에 and 조건 설정
        query.where(qItem.id.gt(0L));
        // 카타고리 아이디 조건이 있을경우
        if (itemSearchDto.getCategoryId() != null) {
            log.info("itemSearchDto.getCategoryId():" + itemSearchDto.getCategoryId());
            query.where(qItem.category.id.eq(itemSearchDto.getCategoryId()));
        }
        // 현재 쿼리 객체에 페이징 조건 설정
        this.getQuerydsl().applyPagination(pageable, query);
        // 만들어진 쿼리 객체 실행해서 결과 받아옴
        List<MainItemDto> list = query.fetch();
        // 조건과 일치하는 결과 행수 조회
        long count = query.fetchCount();
        // 데이터베이스에서 받아온 데이터와 페이징 조건, 결과 행수로
        // Page객체 생성해서 반환
        return new PageImpl<>(list, pageable, count);
    }



    /**
     * 메인화면에서 조회용
     *  - 여러가지 조회 조건
     * @param pageable
     * @param itemSearchDto
     */
    @Override
    public Page<MainItemDto> searchByComplexConditions(Pageable pageable, ItemSearchDto itemSearchDto) {
        QItem qItem = QItem.item;
        QItemImg qItemImg = QItemImg.itemImg;

        JPQLQuery<MainItemDto> query = from(qItem)
                .leftJoin(qItemImg).on(qItem.eq(qItemImg.item).and(qItemImg.repimgYn.eq("Y")))
                .select(Projections.bean(
                        MainItemDto.class,
                        qItem.id,
                        qItem.itemNm,
                        qItem.itemDetail,
                        qItem.price,
                        qItemImg.uuid,
                        qItemImg.fileName)
                );
        BooleanBuilder 조회조건Builder = new BooleanBuilder();

        if (itemSearchDto.getItemNm() != null) {
            조회조건Builder.and(qItem.itemNm.like("%" + itemSearchDto.getItemNm() + "%"));
        }
        // 상품 상세 설명(Like 검색)
        if (itemSearchDto.getItemDetail() != null) {
            조회조건Builder.and(qItem.itemDetail.contains(itemSearchDto.getItemDetail()));
        }


        query.where(조회조건Builder);
        // 쿼리 객체에 and 조건 설정
        query.where(qItem.id.gt(0L));
        // 카타고리 아이디 조건이 있을경우
        if (itemSearchDto.getCategoryId() != null) {
            log.info("itemSearchDto.getCategoryId():" + itemSearchDto.getCategoryId());
            query.where(qItem.category.id.eq(itemSearchDto.getCategoryId()));
        }
        // 현재 쿼리 객체에 페이징 조건 설정
        this.getQuerydsl().applyPagination(pageable, query);
        // 만들어진 쿼리 객체 실행해서 결과 받아옴
        List<MainItemDto> list = query.fetch();
        // 조건과 일치하는 결과 행수 조회
        long count = query.fetchCount();
        // 데이터베이스에서 받아온 데이터와 페이징 조건, 결과 행수로
        // Page객체 생성해서 반환
        log.info("get Uuid + getFileName :"+list.get(0).getUuid() + list.get(0).getFileName());
        return new PageImpl<>(list, pageable, count);

    }




}
