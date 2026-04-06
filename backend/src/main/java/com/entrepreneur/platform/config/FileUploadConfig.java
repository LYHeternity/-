package com.entrepreneur.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件上传存储路径配置
 */
@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Bean
    public Path uploadPath() {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            java.nio.file.Files.createDirectories(path);
        } catch (Exception e) {
            throw new RuntimeException("创建上传目录失败: " + path, e);
        }
        return path;
    }
}
