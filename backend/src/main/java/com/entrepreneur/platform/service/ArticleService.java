package com.entrepreneur.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entrepreneur.platform.common.PageResult;
import com.entrepreneur.platform.entity.Article;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 创业资讯文章服务接口
 */
public interface ArticleService extends IService<Article> {
    
    /**
     * 从科技资讯网站爬取文章并保存到数据库
     * @return 爬取的文章数量
     */
    @CacheEvict(value = {"articles", "hotArticles"}, allEntries = true)
    int scrapeAndSaveArticles();
    
    /**
     * 根据分类获取文章列表
     * @param category 分类名称
     * @param page 页码
     * @param size 每页数量
     * @param sortBy 排序方式
     * @return 包含文章列表和总条数的对象
     */
    @Cacheable(value = "articles", key = "#category + '-' + #page + '-' + #size + '-' + #sortBy")
    PageResult<Article> getArticlesByCategory(String category, int page, int size, String sortBy);
    
    /**
     * 搜索文章
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页数量
     * @param sortBy 排序方式
     * @return 包含文章列表和总条数的对象
     */
    PageResult<Article> searchArticles(String keyword, int page, int size, String sortBy);
    
    /**
     * 获取热门文章
     * @param limit 数量限制
     * @return 热门文章列表
     */
    @Cacheable(value = "hotArticles", key = "#limit")
    List<Article> getHotArticles(int limit);
    
    /**
     * 根据ID获取文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    Article getArticleById(Long id);
}
