package com.intuit.crowdfundingRestAPI;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //.allowedOrigins("http://localhost:3000")
                .allowedOrigins("*") // Changed to allow all origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                //.allowCredentials(true)
                .maxAge(3600);
    }
}

