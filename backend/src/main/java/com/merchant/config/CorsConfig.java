package com.merchant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许跨域的源
        config.addAllowedOrigin("http://localhost:5173"); // 前端开发服务器
        config.addAllowedOrigin("http://localhost:4173"); // 前端生产构建
        
        // 允许跨域的请求头
        config.addAllowedHeader("*");
        // 允许跨域的请求方法
        config.addAllowedMethod("*");
        // 允许携带认证信息
        config.setAllowCredentials(true);
        // 暴露响应头
        config.addExposedHeader("Authorization");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
