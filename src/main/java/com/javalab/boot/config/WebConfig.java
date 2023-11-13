package com.javalab.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 기본 이미지, CSS, JS 폴더
        // 이미지, CSS, JS 폴더
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");

        // Vendor CSS
        // 각 Vendor 디렉토리에 대한 설정
        registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:/static/vendor/");
        //registry.addResourceHandler("/vendor/jquery/**").addResourceLocations("classpath:/static/vendor/jquery/");
    }
}
