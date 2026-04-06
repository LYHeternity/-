package com.entrepreneur.platform.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 数据备份与恢复服务接口
 */
public interface BackupService {

    /**
     * 备份数据
     * @return 备份文件名
     * @throws Exception 备份异常
     */
    String backupData() throws Exception;

    /**
     * 恢复数据
     * @param file 备份文件
     * @throws Exception 恢复异常
     */
    void restoreData(MultipartFile file) throws Exception;

    /**
     * 获取备份列表
     * @return 备份列表
     * @throws Exception 获取异常
     */
    List<Map<String, Object>> getBackupList() throws Exception;

    /**
     * 下载备份文件
     * @param backupName 备份文件名
     * @param response 响应对象
     * @throws Exception 下载异常
     */
    void downloadBackup(String backupName, HttpServletResponse response) throws Exception;

    /**
     * 从备份恢复数据
     * @param backupName 备份文件名
     * @throws Exception 恢复异常
     */
    void restoreFromBackup(String backupName) throws Exception;
}
