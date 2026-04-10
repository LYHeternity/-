package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 本地文件上传（图片/PDF/Word/PPT，最大 50MB）
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private static final List<String> ALLOWED_EXT = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx"
    );

    private static final List<String> IMAGE_EXT = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("请选择文件");
        }
        
        // 检查文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.fail("文件大小不能超过5MB");
        }
        
        String ext = getExtension(file.getOriginalFilename());
        if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase())) {
            return Result.fail("仅支持图片、PDF、Word、PPT 格式");
        }
        
        try {
            // 为不同类型的图片创建专门的存储目录
            String subDir = "static";
            if (IMAGE_EXT.contains(ext.toLowerCase())) {
                subDir = "static/images";
            }
            
            Path dir = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            
            // 生成文件名：用户ID+时间戳+随机字符串
            Long userId = SecurityUtil.getCurrentUserId();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String randomStr = UUID.randomUUID().toString().substring(0, 8);
            String name;
            
            if (IMAGE_EXT.contains(ext.toLowerCase()) && userId != null) {
                name = userId + "_" + timestamp + "_" + randomStr + "." + ext;
            } else {
                name = UUID.randomUUID().toString() + "." + ext;
            }
            
            Path target = dir.resolve(name);
            file.transferTo(target.toFile());
            
            // 生成访问URL
            String url;
            if (IMAGE_EXT.contains(ext.toLowerCase())) {

                url = "/api/static/images/" + name;

                url = "/static/images/" + name;

            } else {
                url = "/api/files/" + name;
            }
            
            return Result.ok(url);
        } catch (Exception e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return null;
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
