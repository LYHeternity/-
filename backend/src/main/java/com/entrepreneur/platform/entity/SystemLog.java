package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志表
 */
@Data
@TableName("system_log")
public class SystemLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime timestamp;
    private String level;
    private String module;
    private String username;
    private Long userId;
    private String action;
    private String message;
    private String ip;
}
