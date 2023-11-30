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
    @PostMapping("/register")
    public String register(@Valid ItemFormDTO itemFormDTO,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "item/register";
        }
        Long itemId = itemService.register(itemFormDTO);
        return "redirect:/admin/item/list";
    }
    @GetMapping("/list")
    public String list(@ModelAttribute("pageRequestDTO") PageRequestDTO pageRequestDTO,
                       @ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto, Model model) {
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
        model.addAttribute("categories", categories);
        model.addAttribute("item", itemFormDTO);
        return "item/read";
    }
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid @ModelAttribute("item") ItemFormDTO itemFormDTO,
                         BindingResult bindingResult,
                         Model model) {
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
