package com.javalab.boot.service;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.*;
import com.javalab.boot.entity.Category;
import com.javalab.boot.entity.Item;
import com.javalab.boot.entity.ItemImg;
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
    @Override
    public Long register(ItemFormDTO itemFormDTO) {
        Item item = itemFormDTO.createItem();
        log.info("Service register 변환된 Item : " + item.getImageSet());
        Long itemId = itemRepository.save(item).getId();
        return itemId;
    }
    @Override
    @Transactional(readOnly = true)
    public ItemFormDTO readOne(Long itemId) {
        Optional<Item> result = itemRepository.findByIdWithImages(itemId);
        Item item = result.orElseThrow();
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
        Optional<Item> result = itemRepository.findById(itemFormDTO.getId());
        Item item = result.orElseThrow();
        item.updateItem(itemFormDTO);
        item.clearImages();
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
        List<ItemFormDTO> dtoList = result.getContent().stream()
                .map(item -> modelMapper.map(item, ItemFormDTO.class))
                .collect(Collectors.toList());
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
        // 가격이 높은 순으로 상품 조회 (검색 조건 적용)
        Page<Object[]> itemsByLowPrice = itemRepository.findByOrderByPriceAscWithUuid(pageable);
        // Item을 MainItemDto로 변환
        List<MainItemDto> mainItemDtos = itemsByLowPrice.getContent().stream()
                .map(itemArray -> {
                    Item item = (Item) itemArray[0];
                    String fileName = (String) itemArray[1];
                    String uuid = (String) itemArray[2];

                    MainItemDto mainItemDto = MainItemDto.builder()
                            .id(item.getId())
                            .itemNm(item.getItemNm())
                            .itemDetail(item.getItemDetail())
                            .price(item.getPrice())
                            .uuid(uuid)
                            .fileName(fileName)
                            .build();

                     //log.info("mainItemDto :" + mainItemDto.getFileName() + " " + mainItemDto.getUuid() + " " + mainItemDto.getImgUrl());
                    return mainItemDto;
                })
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        PageResponseDTO<MainItemDto> pageResponseDTO = PageResponseDTO.<MainItemDto>builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(mainItemDtos)
                .total((int) itemsByLowPrice.getTotalElements()) // 전체 상품 수
                .build();
        return pageResponseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<MainItemDto> searchMainPageByHighPrice(PageRequestDTO pageRequestDTO, ItemSearchDto itemSearchDto) {
        // 페이지 요청 정보를 이용해 Pageable 객체 생성
        Pageable pageable = pageRequestDTO.getPageable("id");
        // 가격이 높은 순으로 상품 조회 (검색 조건 적용)
        Page<Object[]> itemsByHighPrice = itemRepository.findByOrderByPriceDescWithUuid(pageable);
        // Item을 MainItemDto로 변환
        List<MainItemDto> mainItemDtos = itemsByHighPrice.getContent().stream()
                .map(itemArray -> {
                    Item item = (Item) itemArray[0];
                    String fileName = (String) itemArray[1];
                    String uuid = (String) itemArray[2];

                    MainItemDto mainItemDto = MainItemDto.builder()
                            .id(item.getId())
                            .itemNm(item.getItemNm())
                            .itemDetail(item.getItemDetail())
                            .price(item.getPrice())
                            .uuid(uuid)
                            .fileName(fileName)
                            .build();

                    //log.info("mainItemDto :" + mainItemDto.getFileName() + " " + mainItemDto.getUuid() + " " + mainItemDto.getImgUrl());
                    return mainItemDto;
                })
                .collect(Collectors.toList());


        // PageResponseDTO 생성
        PageResponseDTO<MainItemDto> pageResponseDTO = PageResponseDTO.<MainItemDto>builder()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(mainItemDtos)
                .total((int) itemsByHighPrice.getTotalElements()) // 전체 상품 수
                .build();
        return pageResponseDTO;
    }

}
