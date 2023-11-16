package com.javalab.boot.service;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.*;
import com.javalab.boot.entity.Category;
import com.javalab.boot.entity.Item;
import com.javalab.boot.repository.CategoryRepository;
import com.javalab.boot.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 데이터베이스 트랜잭션 처리용
public class ItemServiceImpl implements ItemService{
    // 생성자 의존성 주입됨.
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    /**
     * Item 등록
     */
    @Override
    public Long register(ItemFormDTO itemFormDTO) {
        Item item = itemFormDTO.createItem();
        log.info("Service register 변환된 Item : " + item.getImageSet());
        Long itemId = itemRepository.save(item).getId();
        return itemId;
    }

    /**
     * Item 상세보기
     * @param itemId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public ItemFormDTO readOne(Long itemId) {
        /*
          [Item 엔티티 객체 얻기]
          findByIdWithImages() 메소드에는 @EntityGraph가 있어서
          한번 쿼리할 때 BoardImage들도 동시에 Left Join 해서 갖고옴.
        */
        Optional<Item> result = itemRepository.findByIdWithImages(itemId);
        Item item = result.orElseThrow();

        // 엔티티 -> Dto 로 변환
        ItemFormDTO itemFormDTO = item.entityToDto();
        log.info("Service readOne 메소드 첨부 이미지 갯수 : " + itemFormDTO.getFileNames().size());

        return itemFormDTO;
    }

    /**
     * Item 수정
     * @param itemFormDTO
     */
    @Override
    public void modify(ItemFormDTO itemFormDTO) {
        // 수정할 게시물 조회해서 영속 영역에 보관
        Optional<Item> result = itemRepository.findById(itemFormDTO.getId());

        // 영속 영역의 엔티티 참조 얻기
        Item item = result.orElseThrow();

        /*
          영속 영역에 있는 엔티티 객체의 값 변경하면 디티체킹 대상이 됨.
          더티 체킹은 최초의 상태와 지금의 상태 비교해서 서로 달라진 경우.
          JPA는 트랜잭션의 커밋 시점에 이 더티체킹을 수행하여, 변경된
          엔티티에 대한 UPDATE SQL을 데이터베이스에 실행.
        */
        item.updateItem(itemFormDTO);

        // 기존 첨부 파일들은 정리 즉, 참조하고 있는 Item Image 객체들의
        // item 속성을 null로 처리, 상위 객체의 참조 제거됨. 고아객체됨.
        // orphanRemoval = true로 고아 객체 자동 삭제 대상
        item.clearImages();

        // 저장할 파일들이 전달되어 온 경우
        if(itemFormDTO.getFileNames() != null){
            log.info("첨부 이미지 존재");
            List<String> fileNames = itemFormDTO.getFileNames();
            for (int i = 0; i < fileNames.size(); i++) {
                String fileName = fileNames.get(i);
                log.info("fileName : " + fileName);
                String[] arr = fileName.split("_");
                String repimgYn = (i == 0) ? "Y" : "N";
                item.addImage(arr[0], arr[1], repimgYn);
            }
        }

        log.info("board imageSet size() : " + item.getImageSet().size());
        itemRepository.save(item);
    }

    /**
     * 삭제
     * @param itemId
     */
    @Override
    public void remove(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    /**
     * 목록 조회 (No Searching)
     */
//    @Override
//    public PageResponseDTO<ItemFormDTO> list(PageRequestDTO pageRequestDTO) {
//
//        String[] types = pageRequestDTO.getTypes();
//        String keyword = pageRequestDTO.getKeyword();
//        Pageable pageable = pageRequestDTO.getPageable("id");
//
//        // result : 1.엔티티 목록, 2.페이징 관련 정보
//        Page<Item> result = itemRepository.search(pageable);
//
//        List<ItemFormDTO> dtoList = result.getContent().stream()
//                .map(item -> modelMapper.map(item, ItemFormDTO.class))
//                .collect(Collectors.toList());
//
//        PageResponseDTO<ItemFormDTO> pageResponseDTO =
//                PageResponseDTO.<ItemFormDTO>builder()
//                        .pageRequestDTO(pageRequestDTO) /* 페이지 요청정보 */
//                        .dtoList(dtoList) /* 목록 데이터 */
//                        .total((int)result.getTotalElements())
//                        .build();
//
//        return pageResponseDTO;
//    }

    /**
     * 목록 조회 (Searching)
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ItemFormDTO> list(PageRequestDTO pageRequestDTO) {

        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Item> result = itemRepository.searchCondition(types, keyword, pageable);

        // modelMapper를 통해서 Entity -> Dto 변환
        List<ItemFormDTO> dtoList = result.getContent().stream()
                .map(item -> modelMapper.map(item, ItemFormDTO.class))
                .collect(Collectors.toList());

        // 변환시킨 Dto, PageRequest 등을 PageResponseDTO 저장
        PageResponseDTO<ItemFormDTO> pageResponseDTO = PageResponseDTO.<ItemFormDTO>builder()
                        .pageRequestDTO(pageRequestDTO)
                        .dtoList(dtoList)
                        .total((int)result.getTotalElements())
                        .build();

        return pageResponseDTO;
    }

    // 복합 조건 조회
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ItemFormDTO> searchList(PageRequestDTO pageRequestDTO,
                                                    ItemSearchDto itemSearchDto) {

        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<Item> result = itemRepository.searchItems(pageable, itemSearchDto);

        // modelMapper를 통해서 Entity -> Dto 변환
        List<ItemFormDTO> dtoList = result.getContent().stream()
                .map(item -> modelMapper.map(item, ItemFormDTO.class))
                .collect(Collectors.toList());

        // 변환시킨 Dto, PageRequest 등을 PageResponseDTO 저장
        PageResponseDTO<ItemFormDTO> pageResponseDTO = PageResponseDTO.<ItemFormDTO>builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

        return pageResponseDTO;
    }

    public List<ItemSellStatus> getSellStatusOptions() {
        // 레포지토리 레이어를 호출하여 데이터베이스에서 판매 상태 값을 가져옵니다.
        List<ItemSellStatus> sellStatusOptions = itemRepository.findSellStatus();
        return sellStatusOptions;
    }




    /**
     * 메인 페이지용 조회
     *  - MainItemDto에 정보 저장해서 반환
     * @param pageRequestDTO
     * @param itemSearchDto
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MainItemDto> searchMainPage(PageRequestDTO pageRequestDTO, ItemSearchDto itemSearchDto) {

        Pageable pageable = pageRequestDTO.getPageable("id");

        Page<MainItemDto> result = itemRepository.searchMainPage(pageable, itemSearchDto);

        // modelMapper를 통해서 Entity -> Dto 변환
        List<MainItemDto> dtoList = result.getContent().stream()
                .map(item -> modelMapper.map(item, MainItemDto.class))
                .collect(Collectors.toList());

        // 변환시킨 Dto, PageRequest 등을 PageResponseDTO 저장
        PageResponseDTO<MainItemDto> pageResponseDTO = PageResponseDTO.<MainItemDto>builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

        return pageResponseDTO;


    }

    /**
     * 상품 가격 낮은순 조회
     * @param pageRequestDTO
     * @param itemSearchDto
     * @return
     */

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MainItemDto> searchMainPageByLowPrice(PageRequestDTO pageRequestDTO, ItemSearchDto itemSearchDto) {
        // 페이지 요청 정보를 이용해 Pageable 객체 생성
        Pageable pageable = pageRequestDTO.getPageable("id");
        // 가격이 낮은 순으로 상품 조회 (검색 조건 적용)
        Page<Item> itemsByLowPrice = itemRepository.findByOrderByPriceAsc(pageable);

        // Item을 MainItemDto로 변환
        List<MainItemDto> mainItemDtos = itemsByLowPrice.getContent().stream()
                .map(item -> modelMapper.map(item, MainItemDto.class)) // Item을 MainItemDto로 변환하는 메소드 호출
                .collect(Collectors.toList());

        // PageResponseDTO 생성
        PageResponseDTO<MainItemDto> pageResponseDTO = PageResponseDTO.<MainItemDto>builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(mainItemDtos)
                .total((int) itemsByLowPrice.getTotalElements()) // 전체 상품 수
                .build();

        return pageResponseDTO;
    }


//
//    @Override
//    public PageResponseDTO<MainItemDto> searchMainPageByHighPrice(PageRequestDTO pageRequestDTO) {
//        // 높은 가격순으로 메인 화면의 상품을 조회하는 로직 구현
//    }
//
//    @Override
//    public PageResponseDTO<MainItemDto> searchMainPageForNewItems(PageRequestDTO pageRequestDTO) {
//        // 신상품으로 메인 화면의 상품을 조회하는 로직 구현
//    }

}
