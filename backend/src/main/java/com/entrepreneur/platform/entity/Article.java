package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创业资讯文章实体类
 */
@Data
@TableName("article")
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    private String content;
    private String url;
    private String source;
    private String author;
    private String publicationDate;
    private String contentSummary;
    private String category;
    private double relevanceScore;
    private String tags;
    private String imageUrl;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
