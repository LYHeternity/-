package com.entrepreneur.platform.service;

import com.entrepreneur.platform.entity.SystemLog;
import com.entrepreneur.platform.mapper.SystemLogMapper;
import com.entrepreneur.platform.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志记录（异步写入）
 */
@Service
@RequiredArgsConstructor
public class LogService {

    private final SystemLogMapper systemLogMapper;

    @Async
    public void log(String level, String module, String action, String message, HttpServletRequest request) {
        try {
            SystemLog log = new SystemLog();
            log.setTimestamp(LocalDateTime.now());
            log.setLevel(level != null ? level : "INFO");
            log.setModule(module);
            log.setAction(action);
            log.setMessage(message);
            Long uid = SecurityUtil.getCurrentUserId();
            log.setUserId(uid);
            log.setUsername(SecurityUtil.getCurrentUsername());
            if (request != null) {
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
                log.setIp(ip);
            }
            systemLogMapper.insert(log);
        } catch (Exception ignored) {}
    }

    public void info(String module, String action, String message) {
        log("INFO", module, action, message, null);
    }
}
