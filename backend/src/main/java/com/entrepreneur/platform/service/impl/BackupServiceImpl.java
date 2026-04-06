package com.entrepreneur.platform.service.impl;

import com.entrepreneur.platform.service.BackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据备份与恢复服务实现
 */
@Service
public class BackupServiceImpl implements BackupService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Value("${spring.datasource.name:entrepreneur_platform}")
    private String dbName;

    private static final String BACKUP_DIR = "backup";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * 获取备份目录
     * @return 备份目录
     */
    private String getBackupDir() {
        String backupDir = uploadDir + File.separator + BACKUP_DIR;
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return backupDir;
    }

    /**
     * 备份数据
     * @return 备份文件名
     * @throws Exception 备份异常
     */
    @Override
    public String backupData() throws Exception {
        String backupDir = getBackupDir();
        String backupName = "backup_" + DATE_FORMAT.format(new Date()) + ".sql";
        String backupPath = backupDir + File.separator + backupName;

        // 使用配置的数据库名

        // 构建mysqldump命令
        String[] command = new String[]{
            "cmd", "/c", "mysqldump", "-u" + dbUsername, "-p" + dbPassword, "--databases", dbName, ">", backupPath
        };

        // 执行命令
        Process process = Runtime.getRuntime().exec(command);
        
        // 读取错误输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            StringBuilder errorMessage = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new Exception("备份失败，退出码: " + exitCode + "\n错误信息: " + errorMessage.toString());
            }
        }

        return backupName;
    }

    /**
     * 恢复数据
     * @param file 备份文件
     * @throws Exception 恢复异常
     */
    @Override
    public void restoreData(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("备份文件为空");
        }

        String backupDir = getBackupDir();
        String tempFile = backupDir + File.separator + "temp_" + DATE_FORMAT.format(new Date()) + ".sql";

        // 保存上传的文件
        file.transferTo(new File(tempFile));

        try {


            // 构建mysql命令
            String[] command = new String[]{
                "cmd", "/c", "mysql", "-u" + dbUsername, "-p" + dbPassword, dbName, "<", tempFile
            };

            // 执行命令
            Process process = Runtime.getRuntime().exec(command);
            
            // 读取错误输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                StringBuilder errorMessage = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    errorMessage.append(line).append("\n");
                }
                
                int exitCode = process.waitFor();
                
                if (exitCode != 0) {
                    throw new Exception("恢复失败，退出码: " + exitCode + "\n错误信息: " + errorMessage.toString());
                }
            }
        } finally {
            // 删除临时文件
            new File(tempFile).delete();
        }
    }

    /**
     * 获取备份列表
     * @return 备份列表
     * @throws Exception 获取异常
     */
    @Override
    public List<Map<String, Object>> getBackupList() throws Exception {
        String backupDir = getBackupDir();
        File dir = new File(backupDir);

        if (!dir.exists()) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> backupList = Files.list(Paths.get(backupDir))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".sql"))
                .map(path -> {
                    File file = path.toFile();
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", file.getName().replace(".sql", ""));
                    map.put("name", file.getName());
                    map.put("time", new Date(file.lastModified()));
                    map.put("formattedTime", formatDateTime(file.lastModified()));
                    map.put("formattedDate", formatDate(file.lastModified()));
                    map.put("formattedTimeOnly", formatTime(file.lastModified()));
                    map.put("size", file.length());
                    map.put("formattedSize", formatFileSize(file.length()));
                    map.put("status", "success");
                    map.put("database", dbName);
                    map.put("version", "1.0");
                    return map;
                })
                .sorted((a, b) -> Long.compare(((Date) b.get("time")).getTime(), ((Date) a.get("time")).getTime()))
                .collect(Collectors.toList());
        
        return backupList;
    }
    
    /**
     * 格式化日期
     * @param timestamp 时间戳
     * @return 格式化后的日期
     */
    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 格式化时间
     * @param timestamp 时间戳
     * @return 格式化后的时间
     */
    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 格式化文件大小
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }
    
    /**
     * 格式化日期时间
     * @param timestamp 时间戳
     * @return 格式化后的日期时间
     */
    private String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    /**
     * 下载备份文件
     * @param backupName 备份文件名
     * @param response 响应对象
     * @throws Exception 下载异常
     */
    @Override
    public void downloadBackup(String backupName, HttpServletResponse response) throws Exception {
        String backupDir = getBackupDir();
        File backupFile = new File(backupDir + File.separator + backupName);

        if (!backupFile.exists()) {
            throw new Exception("备份文件不存在");
        }

        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + backupName);
        response.setContentLength((int) backupFile.length());

        // 读取文件并写入响应
        try (FileInputStream fis = new FileInputStream(backupFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        }
    }

    /**
     * 从备份恢复数据
     * @param backupName 备份文件名
     * @throws Exception 恢复异常
     */
    @Override
    public void restoreFromBackup(String backupName) throws Exception {
        String backupDir = getBackupDir();
        File backupFile = new File(backupDir + File.separator + backupName);

        if (!backupFile.exists()) {
            throw new Exception("备份文件不存在");
        }

        // 使用配置的数据库名

        // 构建mysql命令
        String[] command = new String[]{
            "cmd", "/c", "mysql", "-u" + dbUsername, "-p" + dbPassword, dbName, "<", backupFile.getAbsolutePath()
        };

        // 执行命令
        Process process = Runtime.getRuntime().exec(command);
        
        // 读取错误输出
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            StringBuilder errorMessage = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }
            
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new Exception("恢复失败，退出码: " + exitCode + "\n错误信息: " + errorMessage.toString());
            }
        }
    }
}
