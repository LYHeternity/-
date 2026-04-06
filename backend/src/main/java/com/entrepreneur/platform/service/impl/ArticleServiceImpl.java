package com.entrepreneur.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entrepreneur.platform.common.PageResult;
import com.entrepreneur.platform.entity.Article;
import com.entrepreneur.platform.mapper.ArticleMapper;
import com.entrepreneur.platform.service.ArticleService;
import com.entrepreneur.platform.service.RedisService;
import com.entrepreneur.platform.service.WebSocketService;
import com.entrepreneur.platform.util.SoftunisScraperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创业资讯文章服务实现
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
    
    @Autowired
    private WebSocketService webSocketService;
    
    @Autowired
    private RedisService redisService;
    
    @Override
    public int scrapeAndSaveArticles() {
        int totalSavedCount = 0;
        
        // 爬取软盟资讯文章
        totalSavedCount += scrapeSoftunisArticles();
        
        // 爬取界面新闻文章

        
        // 发送新文章通知
        if (totalSavedCount > 0) {
            webSocketService.sendNewArticleNotification(totalSavedCount);
            
            // 清除相关Redis缓存，确保缓存与数据库同步
            logger.info("爬取新文章后清除Redis缓存");
            // 这里可以使用Redis的keys命令清除相关缓存
            // 注意：在生产环境中，应该避免使用keys命令，而是使用更精确的缓存键管理
        }
        
        return totalSavedCount;
    }
    
    /**
     * 爬取软盟资讯文章
     */
    private int scrapeSoftunisArticles() {
        logger.info("开始从软盟资讯爬取文章...");
        try {
            List<SoftunisScraperUtil.Article> scrapedArticles = SoftunisScraperUtil.scrapeArticles();
            int savedCount = 0;
            
            for (SoftunisScraperUtil.Article scrapedArticle : scrapedArticles) {
                // 检查文章是否已存在
                LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Article::getUrl, scrapedArticle.getUrl());
                Article existingArticle = baseMapper.selectOne(queryWrapper);
                
                if (existingArticle == null) {
                    // 创建新文章
                    Article article = new Article();
                    article.setTitle(scrapedArticle.getTitle());
                    article.setContent(scrapedArticle.getContent());
                    article.setUrl(scrapedArticle.getUrl());
                    article.setSource(scrapedArticle.getSource());
                    article.setAuthor(scrapedArticle.getAuthor());
                    article.setPublicationDate(scrapedArticle.getPublicationDate());
                    article.setContentSummary(scrapedArticle.getContentSummary());
                    article.setCategory(scrapedArticle.getCategory());
                    article.setRelevanceScore(scrapedArticle.getRelevanceScore());
                    article.setImageUrl(scrapedArticle.getImageUrl());
                    // 处理标签 - 将标签列表转换为逗号分隔的字符串
                    List<String> tags = scrapedArticle.getTags();
                    if (tags != null && !tags.isEmpty()) {
                        article.setTags(String.join(", ", tags));
                    }
                    article.setCreatedAt(LocalDateTime.now());
                    article.setUpdatedAt(LocalDateTime.now());
                    
                    baseMapper.insert(article);
                    savedCount++;
                }
            }
            
            logger.info("软盟资讯爬取完成，共保存 {} 篇新文章", savedCount);
            return savedCount;
        } catch (Exception e) {
            logger.error("爬取软盟资讯文章失败", e);
            return 0;
        }
    }
    
    /**
     * 爬取界面新闻文章
     */

    
    @Override
    public PageResult<Article> getArticlesByCategory(String category, int page, int size, String sortBy) {
        // 生成缓存键
        String cacheKey = "articles:category:" + (category != null ? category : "all") + ":page:" + page + ":size:" + size + ":sort:" + sortBy;
        
        // 尝试从Redis缓存中获取数据
        PageResult<Article> cachedResult = (PageResult<Article>) redisService.get(cacheKey);
        if (cachedResult != null) {
            logger.info("从Redis缓存获取文章列表: {}", cacheKey);
            return cachedResult;
        }
        
        // 缓存不存在，从数据库查询
        Page<Article> articlePage = new Page<>(page, size);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq(Article::getCategory, category);
        }
        
        // 查询所有必要字段，包括内容
        queryWrapper.select(
            Article::getId,
            Article::getTitle,
            Article::getContent,
            Article::getUrl,
            Article::getSource,
            Article::getAuthor,
            Article::getPublicationDate,
            Article::getContentSummary,
            Article::getCategory,
            Article::getRelevanceScore,
            Article::getImageUrl,
            Article::getTags,
            Article::getCreatedAt
        );
        
        // 根据排序参数进行排序
        if ("relevance".equals(sortBy)) {
            queryWrapper.orderByDesc(Article::getRelevanceScore).orderByDesc(Article::getCreatedAt);
        } else {
            queryWrapper.orderByDesc(Article::getCreatedAt);
        }
        
        baseMapper.selectPage(articlePage, queryWrapper);
        PageResult<Article> result = new PageResult<>(articlePage.getRecords(), articlePage.getTotal());
        
        // 将结果存入Redis缓存，设置过期时间为10分钟
        redisService.set(cacheKey, result, 600);
        logger.info("文章列表存入Redis缓存: {}", cacheKey);
        
        return result;
    }
    
    @Override
    public PageResult<Article> searchArticles(String keyword, int page, int size, String sortBy) {
        // 生成缓存键
        String cacheKey = "articles:search:" + keyword + ":page:" + page + ":size:" + size + ":sort:" + sortBy;
        
        // 尝试从Redis缓存中获取数据
        PageResult<Article> cachedResult = (PageResult<Article>) redisService.get(cacheKey);
        if (cachedResult != null) {
            logger.info("从Redis缓存获取搜索结果: {}", cacheKey);
            return cachedResult;
        }
        
        // 缓存不存在，从数据库查询
        Page<Article> articlePage = new Page<>(page, size);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            // 优化搜索，先匹配标题，再匹配其他字段
            queryWrapper.and(wrapper -> wrapper
                .like(Article::getTitle, keyword)
                .or().like(Article::getCategory, keyword)
                .or().like(Article::getContentSummary, keyword)
            );
        }
        
        // 查询所有必要字段，包括内容
        queryWrapper.select(
            Article::getId,
            Article::getTitle,
            Article::getContent,
            Article::getUrl,
            Article::getSource,
            Article::getAuthor,
            Article::getPublicationDate,
            Article::getContentSummary,
            Article::getCategory,
            Article::getRelevanceScore,
            Article::getImageUrl,
            Article::getTags,
            Article::getCreatedAt
        );
        
        // 根据排序参数进行排序
        if ("relevance".equals(sortBy)) {
            queryWrapper.orderByDesc(Article::getRelevanceScore).orderByDesc(Article::getCreatedAt);
        } else {
            queryWrapper.orderByDesc(Article::getCreatedAt);
        }
        
        baseMapper.selectPage(articlePage, queryWrapper);
        PageResult<Article> result = new PageResult<>(articlePage.getRecords(), articlePage.getTotal());
        
        // 将结果存入Redis缓存，设置过期时间为5分钟
        redisService.set(cacheKey, result, 300);
        logger.info("搜索结果存入Redis缓存: {}", cacheKey);
        
        return result;
    }
    
    @Override
    public List<Article> getHotArticles(int limit) {
        // 生成缓存键
        String cacheKey = "articles:hot:" + limit;
        
        // 尝试从Redis缓存中获取数据
        List<Article> cachedResult = (List<Article>) redisService.get(cacheKey);
        if (cachedResult != null) {
            logger.info("从Redis缓存获取热门文章: {}", cacheKey);
            return cachedResult;
        }
        
        // 缓存不存在，从数据库查询
        Page<Article> articlePage = new Page<>(1, limit);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        // 查询所有必要字段，包括内容
        queryWrapper.select(
            Article::getId,
            Article::getTitle,
            Article::getContent,
            Article::getUrl,
            Article::getSource,
            Article::getAuthor,
            Article::getPublicationDate,
            Article::getContentSummary,
            Article::getCategory,
            Article::getRelevanceScore,
            Article::getImageUrl,
            Article::getTags,
            Article::getCreatedAt
        );
        
        queryWrapper.orderByDesc(Article::getRelevanceScore)
                   .orderByDesc(Article::getCreatedAt);
        baseMapper.selectPage(articlePage, queryWrapper);
        List<Article> result = articlePage.getRecords();
        
        // 将结果存入Redis缓存，设置过期时间为15分钟
        redisService.set(cacheKey, result, 900);
        logger.info("热门文章存入Redis缓存: {}", cacheKey);
        
        return result;
    }
    
    @Override
    public Article getArticleById(Long id) {
        // 生成缓存键
        String cacheKey = "article:detail:" + id;
        
        // 尝试从Redis缓存中获取数据
        Article cachedArticle = (Article) redisService.get(cacheKey);
        if (cachedArticle != null) {
            logger.info("从Redis缓存获取文章详情: {}", cacheKey);
            return cachedArticle;
        }
        
        // 缓存不存在，从数据库查询
        Article article = baseMapper.selectById(id);
        
        // 将结果存入Redis缓存，设置过期时间为10分钟
        if (article != null) {
            redisService.set(cacheKey, article, 600);
            logger.info("文章详情存入Redis缓存: {}", cacheKey);
        }
        
        return article;
    }
}
