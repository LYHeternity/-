package com.entrepreneur.platform.config;

import com.entrepreneur.platform.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务配置
 */
@Component
public class ScheduledTasks {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    @Autowired
    private ArticleService articleService;
    
    /**
     * 定时爬取创头条文章
     * 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scrapeCtoutiaoArticles() {
        logger.info("开始执行定时爬取创头条文章任务...");
        try {
            int count = articleService.scrapeAndSaveArticles();
            logger.info("定时爬取任务完成，共保存 {} 篇新文章", count);
        } catch (Exception e) {
            logger.error("定时爬取任务失败", e);
        }
    }
    
    /**
     * 启动时爬取一次文章
     * 应用启动后延迟1分钟执行
     */
    @Scheduled(initialDelay = 60000, fixedRate = Long.MAX_VALUE)
    public void scrapeArticlesOnStartup() {
        logger.info("应用启动，开始爬取创头条文章...");
        try {
            int count = articleService.scrapeAndSaveArticles();
            logger.info("启动时爬取完成，共保存 {} 篇新文章", count);
        } catch (Exception e) {
            logger.error("启动时爬取失败", e);
        }
    }
}
