package com.javalab.boot.controller;

import com.javalab.boot.dto.MemberFormDto;
import com.javalab.boot.entity.Member;
import com.javalab.boot.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@RequestMapping("/member")
@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입 화면
    @GetMapping(value = "/join")
    public String memberForm(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/join";
    }

    // 회원 가입 처리
    @PostMapping(value = "/join")
    public String newMember(@Valid MemberFormDto memberFormDto,
                            BindingResult bindingResult,
                            Model model){

        if(bindingResult.hasErrors()){
            log.info("회원가입 데이터 검증 오류 있음");
            return "member/join";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            log.info("회원가입 데이터 member : " + member);
            memberService.saveMember(member);
        } catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            log.info("MemberController 회원가입시 중복 오류 : " + e.getMessage());
            return "member/join";
        }

        return "redirect:/member/login"; //회원 가입 후 로그인
    }

    // 로그인 화면
    @GetMapping(value = "/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception){
        log.info("MemberController loginMember 메소드");

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "/member/login";
    }


}