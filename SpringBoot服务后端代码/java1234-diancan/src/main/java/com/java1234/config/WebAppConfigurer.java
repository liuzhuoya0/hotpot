package com.java1234.config;

import com.java1234.interceptor.SysInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE","OPTIONS")
                .maxAge(3600);
    }

    @Bean
    public SysInterceptor sysInterceptor(){
        return new SysInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] patterns=new String[]{"/adminLogin","/login","/product/**","/category/**","/bigType/**","/users/wxlogin","/weixinpay/**","/dish/**","/order/**","/order-review/**","/image/**","/images/**","/table/**","/shopInfo","/admin/order/export"};
        registry.addInterceptor(sysInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(patterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/qrcode/**").addResourceLocations("file:D:\\java1234-diancan\\qrcodeImgs\\");
        registry.addResourceHandler("/image/dish/**").addResourceLocations("file:D:\\java1234-diancan\\dishImgs\\");
        registry.addResourceHandler("/image/review/**").addResourceLocations("file:D:\\java1234-diancan\\reviewImgs\\");
    }



}
