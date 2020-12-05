package com.house.rent.config;

import com.house.rent.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private SessionInterceptor sessionInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**");
//                .excludePathPatterns("/css/**","/js/**","/index.html","/img/**","/fonts/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //图片映射
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:D:/upload/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:8080")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(30*1000);
    }
}
