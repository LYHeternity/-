package com.entrepreneur.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章收藏实体类
 */
@Data
@TableName("article_favorite")
public class ArticleFavorite {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long articleId;
    
    private LocalDateTime createdAt;
}
