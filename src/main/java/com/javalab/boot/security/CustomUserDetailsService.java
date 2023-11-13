package com.javalab.boot.security;

import com.javalab.boot.entity.Member;
import com.javalab.boot.repository.MemberRepository;
import com.javalab.boot.security.dto.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: " + username);

//        Optional<Member> result = memberRepository.getWithRoles(username);
        Member member = memberRepository.findByEmail(username);

        if(member == null){
            throw new UsernameNotFoundException(username);
        }

//        Member member = result.get();

        // 권한을 생성하고 authorities 컬렉션에 추가
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));

        MemberSecurityDTO memberSecurityDTO =
                new MemberSecurityDTO(
                        member.getEmail(),
                        member.getPassword(),
                        authorities
                );

        memberSecurityDTO.setEmail(member.getEmail());
        memberSecurityDTO.setDel(member.isDel());
        memberSecurityDTO.setSocial(false); // 소셜 로그인이 아닌 경우 false

        log.info("memberSecurityDTO");
        log.info("memberSecurityDTO" + memberSecurityDTO);

        return memberSecurityDTO;
    }

}

