package com.javalab.boot.config;

import com.javalab.boot.handler.AuthFailureHandler;
import com.javalab.boot.handler.AuthSucessHandler;
import com.javalab.boot.security.handler.CustomAccessDeniedHandler;
import com.javalab.boot.security.handler.CustomSocialLoginSuccessHandler;
import com.javalab.boot.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
@EnableWebSecurity // 시큐리티 필터 등록
@EnableGlobalMethodSecurity(prePostEnabled = true) // 컨트롤러의 메소드에 직접 권한 설정
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// 실제 인증을 담당하는 loadUserByUsername 메소드 보유 클래스
	private final MemberService memberService;
	// 인증 성공 다음 처리 클래스
	private final AuthSucessHandler authSucessHandler;
	// 인증 실패 다음 처리 클래스
	private final AuthFailureHandler authFailureHandler;

	// BCryptPasswordEncoder는 Spring Security에서 제공하는 비밀번호 암호화 클래스
	// BCrypt라는 해시 함수를 이용하여 패스워드를 암호화
	@Bean
	public BCryptPasswordEncoder encryptPassword() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * [접근권한없음 핸들러 세팅]
	 *  - 권한이 없을 경우 처리를 담당할 클래스 설정
	 */
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}

	/**
	 * 스프링 시큐리티가 어떻게 사용자 인증 절차를 거쳐야 할지 설정
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(memberService)
			.passwordEncoder(encryptPassword());
	}

	/**
	 * 로그인 성공후 처리
	 */
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomSocialLoginSuccessHandler(encryptPassword());
	}

	/**
	 * 스프링 시큐리티를 사용하여 웹 애플리케이션의 보안/인가/인증 설정.
	 * 인가(Authorization) : 요청한 url에 대한 사용 권한이 있는지 판단.
	 * [Error] localhost에서 리디렉션한 횟수가 너무 많습니다. 메시지가 나오면
	 * antMatchers() 에서 "/member/login/**" 즉, 로그인 페이지 요청경로 확인필요.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 1. 인증설정(로그인/로그아웃)
		http.formLogin()
				.loginPage("/member/login")
				.successHandler(authSucessHandler) // 성공시 요청을 처리할 핸들러
				.failureHandler(authFailureHandler) // 실패시 요청을 처리할 핸들러
			.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 로그아웃 URL
				.logoutSuccessUrl("/member/login")
				.invalidateHttpSession(true) // 인증정보를 지우하고 세션을 무효화
				.deleteCookies("JSESSIONID") // JSESSIONID 쿠키 삭제
				.permitAll();
		;

		// 2. 인가설정(권한)
		http.authorizeRequests()
				// 정적 자원들은 누구나 호출 가능
				.mvcMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/ckeditor2/**", "/vendor/**", "/").permitAll()
				// 컨트롤러의 이미지 제공 매소드는 누구나 호출 가능
				.mvcMatchers("/view/**").permitAll()
				// 로그인, 회원가입 메소드는 누구나 호출 가능
				.mvcMatchers("/member/login", "/member/join/**").permitAll()
				// 게시판은 USER/ADMIN 호출(사용) 가능
				.mvcMatchers("/board/**").hasAnyRole("USER", "ADMIN")
				// 상품 주문 관련 기능은 USER/ADMIN 호출 가능
				.mvcMatchers("/item/**").hasAnyRole("USER", "ADMIN")
				// 상품관리(등록/수정/삭제)는 관리자만 이용가능
				.mvcMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated();

		// 3. 보안설정(CSRF 토큰 비활성화) : C/U/D(데이베이스)작업시 주석 해제 해야됨.
		// http.csrf().disable();

		// 4. 로그인 유지설정
		http.rememberMe()
				.alwaysRemember(false) // 항상 기억할 것인지 여부
				.tokenValiditySeconds(43200) // in seconds, 12시간 유지
				.rememberMeParameter("remember-me")
		;

		//http.exceptionHandling().accessDeniedPage("/access-denied");
 		http.exceptionHandling().accessDeniedHandler(accessDeniedHandler()); // 커스텀 AccessDeniedHandler 설정

		// OAuth 설정
		/*
		http.oauth2Login()
				.loginPage("/member/login")
				.successHandler(authenticationSuccessHandler());*/
	}
}
