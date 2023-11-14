package com.javalab.boot.controller;

import com.javalab.boot.constant.ItemSellStatus;
import com.javalab.boot.dto.ItemFormDTO;
import com.javalab.boot.dto.ItemSearchDto;
import com.javalab.boot.dto.PageRequestDTO;
import com.javalab.boot.dto.PageResponseDTO;
import com.javalab.boot.entity.Category;
import com.javalab.boot.service.CategoryService;
import com.javalab.boot.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

/*********************************************************
 * 관리자용 Item Controller 상품의 C/R/U/D
 ********************************************************/
@Controller
@RequestMapping("/admin/item")
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final ItemService itemService;
    private final CategoryService categoryService;
    /**
     * 공통메소드
     *  - ItemSellStatus enum 타입
     *  - sellStatus라는 이름으로 판매상태 정보를 세팅해서 Model에 저장해줌.
     *  - 화면에서는 리턴하는 sellStatus 라는 이름으로 꺼내 쓸 수 있음.
     */
    @ModelAttribute("sellStatus")
    public List<ItemSellStatus> getSellStatus() {
        // 데이터베이스에서 판매 상태 값을 읽어옵니다.
        List<ItemSellStatus> sellStatus = itemService.getSellStatusOptions();
        log.info("sellStatus.size() : " + sellStatus.size());
        return sellStatus;
    }

    /**
     * 상품 등록폼 오픈 메소드
     *  - @PreAuthorize : 메서드에 대한 인가 설정
     *  - 시큐리티 환경설정에 안해도 됨.
     */
    //@PreAuthorize("hasRole('ADMIN')") // 등록은 관리자만 가능하도록 설정
    @GetMapping("/register")
    public String registerForm(Model model) {
        // 데이터베이스에서 카테고리 값을 읽어옵니다.
        List<Category> categories = categoryService.getCategoryOptions();

        log.info("category : " + categories.size());
        model.addAttribute("categories", categories);
        model.addAttribute("itemFormDTO", new ItemFormDTO());
        return "item/register";
    }

    @PostMapping("/register")
    public String register(@Valid ItemFormDTO itemFormDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {

        log.info("itemFormDTO : " + itemFormDTO);

        if(bindingResult.hasErrors()) {
            log.info("등록 화면 오류 있음");
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "item/register";
        }
        Long itemId = itemService.register(itemFormDTO);
        return "redirect:/admin/item/list";
    }

    // 복잡한 조건으로 상품 조회
    @GetMapping("/list")
    public String list(@ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       @ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       Model model) {

        log.info("ItemSearchDto: " + itemSearchDto.toString());
        PageResponseDTO<ItemFormDTO> responseDTO = itemService.searchList(pageRequestDTO, itemSearchDto);
        model.addAttribute("responseDTO", responseDTO);
        return "item/list";
    }
    // 관리자 전용으로 변환 예정
    @GetMapping({"/read"})
    public String read(Long id,
                       @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       Model model) {
        ItemFormDTO itemFormDTO = itemService.readOne(id);
        List<Category> categories = categoryService.getCategoryOptions();
        log.info("category read : " + categories);
        model.addAttribute("categories", categories);
        model.addAttribute("item", itemFormDTO);
        return "item/read";
    }

    //@PreAuthorize("hasRole('ADMIN')") // 상품 수정도 관리자만 가능하도록 설정
    @GetMapping({"/modify"})
    public String modifyGet(Long id, @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO, Model model) {
        ItemFormDTO itemFormDTO = itemService.readOne(id);
        List<Category> categories = categoryService.getCategoryOptions();
        model.addAttribute("categories", categories);
        model.addAttribute("item", itemFormDTO);
        return "item/modify";
    }

    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid @ModelAttribute("item") ItemFormDTO itemFormDTO,
                         BindingResult bindingResult,
                         Model model) {

        log.info("itemFormDTO : " + itemFormDTO);

        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "item/modify"; // 수정폼으로 다시 이동
        }
        itemService.modify(itemFormDTO);
        return "redirect:/admin/item/read?id=" + itemFormDTO.getId() + "&" + pageRequestDTO.getLink();
    }

    @PostMapping("/remove")
    public String remove(Long id,
                         @ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO) {
        itemService.remove(id);
        return "redirect:/admin/item/list?" + pageRequestDTO.getLink();
    }
}
