package com.entrepreneur.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Web MVC 配置：跨域、静态资源（上传文件访问）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;
    @Value("${cors.allowed-methods}")
    private String allowedMethods;
    @Value("${cors.allowed-headers}")
    private String allowedHeaders;
    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.access-prefix}")
    private String accessPrefix;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        // 配置文件上传目录的访问
        registry.addResourceHandler(accessPrefix + "/**")
                .addResourceLocations("file:" + path + "/");
        // 配置静态资源目录的访问，特别是头像图片
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + path + "/static/");
        // 配置根路径访问静态资源
        registry.addResourceHandler("/")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径重定向到index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 设置字符串响应的编码为UTF-8
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        converters.add(0, stringConverter);

        // 配置Jackson消息转换器，确保JSON响应使用UTF-8编码
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }
}