package com.entrepreneur.platform.controller;

import com.entrepreneur.platform.common.PageResult;
import com.entrepreneur.platform.common.Result;
import com.entrepreneur.platform.entity.Article;
import com.entrepreneur.platform.mapper.ArticleMapper;
import com.entrepreneur.platform.service.ArticleService;
import com.entrepreneur.platform.service.UserFavoriteService;
import com.entrepreneur.platform.util.ContentProcessorUtil;
import com.entrepreneur.platform.util.MarkdownFormatterUtil;
import com.entrepreneur.platform.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 创业资讯文章控制器
 */
@RestController
@RequestMapping("/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private UserFavoriteService userFavoriteService;
    
    @Autowired
    private ArticleMapper baseMapper;
    
    /**
     * 获取文章列表
     * @param category 分类（可选）
     * @param page 页码
     * @param size 每页数量
     * @param sortBy 排序方式（latest或relevance）
     * @return 包含文章列表和总条数的对象
     */
    @GetMapping
    public Result<PageResult<Article>> getArticles(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy) {
        // 处理 category 参数，如果是 "0" 或 0，就设置为 null，这样会查询所有文章
        if (category != null && (category.equals("0") || category.equals("0.0"))) {
            category = null;
        }
        PageResult<Article> result = articleService.getArticlesByCategory(category, page, size, sortBy);
        // 对文章列表数据进行清洗
        if (result != null && result.getRecords() != null) {
            for (Article article : result.getRecords()) {
                // 清洗标题
                article.setTitle(ContentProcessorUtil.cleanTitle(article.getTitle()));
                // 清洗作者
                // if (article.getAuthor() != null) {
                //     article.setAuthor(article.getAuthor().trim());
                // }
                // 清洗内容摘要
                article.setContentSummary(ContentProcessorUtil.generateSummary(article.getContent(), 200));
                // 规范化发布日期
                article.setPublicationDate(ContentProcessorUtil.normalizeDate(article.getPublicationDate()));
                // 清洗标签
                // if (article.getTags() != null) {
                //     article.setTags(article.getTags().trim());
                // }
            }
        }
        return Result.ok(result);
    }
    
    /**
     * 搜索文章
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页数量
     * @param sortBy 排序方式（latest或relevance）
     * @return 包含搜索结果和总条数的对象
     */
    @GetMapping("/search")
    public Result<PageResult<Article>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy) {
        PageResult<Article> result = articleService.searchArticles(keyword, page, size, sortBy);
        // 对搜索结果数据进行清洗
        if (result != null && result.getRecords() != null) {
            for (Article article : result.getRecords()) {
                // 清洗标题
                article.setTitle(ContentProcessorUtil.cleanTitle(article.getTitle()));
                // 清洗作者
                // if (article.getAuthor() != null) {
                //     article.setAuthor(article.getAuthor().trim());
                // }
                // 清洗内容摘要
                article.setContentSummary(ContentProcessorUtil.generateSummary(article.getContent(), 200));
                // 规范化发布日期
                article.setPublicationDate(ContentProcessorUtil.normalizeDate(article.getPublicationDate()));
                // 清洗标签
                // if (article.getTags() != null) {
                //     article.setTags(article.getTags().trim());
                // }
            }
        }
        return Result.ok(result);
    }
    
    /**
     * 获取热门文章
     * @param limit 数量限制
     * @return 热门文章列表
     */
    @GetMapping("/hot")
    public Result<List<Article>> getHotArticles(@RequestParam(defaultValue = "5") int limit) {
        List<Article> articles = articleService.getHotArticles(limit);
        // 对热门文章数据进行清洗
        if (articles != null) {
            for (Article article : articles) {
                // 清洗标题
                article.setTitle(ContentProcessorUtil.cleanTitle(article.getTitle()));
                // 清洗作者
                // if (article.getAuthor() != null) {
                //     article.setAuthor(article.getAuthor().trim());
                // }
                // 清洗内容摘要
                article.setContentSummary(ContentProcessorUtil.generateSummary(article.getContent(), 200));
                // 规范化发布日期
                article.setPublicationDate(ContentProcessorUtil.normalizeDate(article.getPublicationDate()));
                // 清洗标签
                // if (article.getTags() != null) {
                //     article.setTags(article.getTags().trim());
                // }
            }
        }
        return Result.ok(articles);
    }
    
    /**
     * 触发爬取文章
     * @return 爬取结果
     */
    @PostMapping("/scrape")
    public Result<Integer> scrapeArticles() {
        int count = articleService.scrapeAndSaveArticles();
        return Result.ok(count);
    }
    
    /**
     * 获取文章详情
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public Result<Article> getArticleById(@PathVariable Long id) {
        // 使用ArticleService的getArticleById方法，利用Redis缓存
        Article article = articleService.getArticleById(id);
        // 对文章数据进行清洗和格式转换
        if (article != null) {
            // 清洗标题
            article.setTitle(ContentProcessorUtil.cleanTitle(article.getTitle()));
            // 清洗内容并转换为Markdown格式（使用综合排版处理）
            String markdownContent = MarkdownFormatterUtil.comprehensiveFormat(article.getContent());
            article.setContent(markdownContent);
            // 清洗内容摘要
            article.setContentSummary(ContentProcessorUtil.generateSummary(article.getContent(), 200));
            // 规范化发布日期
            article.setPublicationDate(ContentProcessorUtil.normalizeDate(article.getPublicationDate()));
        }
        return Result.ok(article);
    }
    
    /**
     * 切换文章收藏状态
     * @param id 文章ID
     * @return 收藏状态
     */
    @PostMapping("/{id}/favorite")
    public Result<Boolean> toggleFavorite(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        userFavoriteService.toggleFavorite(userId, "article", id);
        boolean isFavorited = userFavoriteService.isFavorited(userId, "article", id);
        return Result.ok(isFavorited);
    }
    
    /**
     * 检查文章是否被收藏
     * @param id 文章ID
     * @return 收藏状态
     */
    @GetMapping("/{id}/favorite")
    public Result<Boolean> isFavorited(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        boolean isFavorited = userFavoriteService.isFavorited(userId, "article", id);
        return Result.ok(isFavorited);
    }
}

