package com.entrepreneur.platform.controller;


import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据备份与恢复控制器
 */
@RestController
@RequestMapping("/backup")
public class BackupController {

    @Autowired
    private BackupService backupService;

    /**
     * 备份数据
     * @return 备份结果
     */
    @PostMapping("/create")
    public Result<String> backupData() {
        try {
            String backupName = backupService.backupData();
            return Result.ok(backupName);
        } catch (Exception e) {
            return Result.fail("备份失败: " + e.getMessage());
        }
    }

    /**
     * 恢复数据
     * @param file 备份文件
     * @return 恢复结果
     */
    @PostMapping("/restore")
    public Result<String> restoreData(@RequestParam("file") MultipartFile file) {
        try {
            backupService.restoreData(file);
            return Result.ok("恢复成功");
        } catch (Exception e) {
            return Result.fail("恢复失败: " + e.getMessage());
        }
    }

    /**
     * 获取备份列表
     * @return 备份列表
     */
    @GetMapping("/list")
    public Result<?> getBackupList() {
        try {
            return Result.ok(backupService.getBackupList());
        } catch (Exception e) {
            return Result.fail("获取备份列表失败: " + e.getMessage());
        }
    }

    /**
     * 下载备份文件
     * @param backupName 备份文件名
     * @param response 响应对象
     */
    @GetMapping("/download/{backupName}")
    public void downloadBackup(@PathVariable String backupName, HttpServletResponse response) {
        try {
            backupService.downloadBackup(backupName, response);
        } catch (Exception e) {
            try {
                response.getWriter().write("下载失败: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 从备份恢复数据
     * @param backupName 备份文件名
     * @return 恢复结果
     */
    @PostMapping("/restore/{backupName}")
    public Result<String> restoreFromBackup(@PathVariable String backupName) {
        try {
            backupService.restoreFromBackup(backupName);
            return Result.ok("恢复成功");
        } catch (Exception e) {
            return Result.fail("恢复失败: " + e.getMessage());
        }
    }
}
