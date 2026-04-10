package com.entrepreneur.platform.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 功能：学习用Java爬虫，获取 https://news.softunis.com/hlwcy 的文章标题和完整内容
 * 核心库：Jsoup (用于解析HTML) 和 Java 标准库
 * 注意：本脚本仅用于个人学习，请遵守网站robots.txt规则并控制请求频率。
 */
public class SoftunisScraperUtil {

    // 定义文章条目类，用于封装抓取的数据
    public static class Article {
        private String title;
        private String content;
        private String url;
        private String source;
        private String author;
        private String publicationDate;
        private String contentSummary;
        private String category;
        private double relevanceScore;
        private String imageUrl;
        private List<String> tags;

        public Article(String title, String content, String url, String source, String author, String publicationDate, String contentSummary, String category, double relevanceScore, String imageUrl, List<String> tags) {
            this.title = title;
            this.content = content;
            this.url = url;
            this.source = source;
            this.author = author;
            this.publicationDate = publicationDate;
            this.contentSummary = contentSummary;
            this.category = category;
            this.relevanceScore = relevanceScore;
            this.imageUrl = imageUrl;
            this.tags = tags;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return url;
        }

        public String getSource() {
            return source;
        }

        public String getAuthor() {
            return author;
        }

        public String getPublicationDate() {
            return publicationDate;
        }

        public String getContentSummary() {
            return contentSummary;
        }

        public String getCategory() {
            return category;
        }

        public double getRelevanceScore() {
            return relevanceScore;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public List<String> getTags() {
            return tags;
        }

        @Override
        public String toString() {
            return "Article{" +
                    "title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    ", source='" + source + '\'' +
                    ", author='" + author + '\'' +
                    ", category='" + category + '\'' +
                    ", tags='" + tags + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    '}';
        }
    }

    // 主方法，用于测试爬虫功能
    public static void main(String[] args) {
        System.out.println("开始爬取软盟资讯文章...");
        try {
            List<Article> articles = scrapeArticles();
            System.out.println("爬取完成，共获取 " + articles.size() + " 篇文章");
            for (Article article : articles) {
                System.out.println("标题: " + article.getTitle());
                System.out.println("链接: " + article.getUrl());
                System.out.println("来源: " + article.getSource());
                System.out.println("发布日期: " + article.getPublicationDate());
                System.out.println("分类: " + article.getCategory());
                System.out.println("相关度: " + article.getRelevanceScore());
                System.out.println("内容摘要: " + article.getContentSummary());
                System.out.println("内容: " + article.getContent());
                System.out.println("=======================================");
            }
        } catch (IOException e) {
            System.err.println("爬取过程中发生错误:");
            e.printStackTrace();
        }
    }
    
    /**
     * 测试网络连接是否可用
     * @param urlStr 要测试的URL
     * @return 网络是否可用
     */
    private static boolean isNetworkAvailable(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    // 爬取软盟资讯文章的方法
    public static List<Article> scrapeArticles() throws IOException {
        List<Article> articles = new ArrayList<>();
        Set<String> articleUrls = new HashSet<>();

        // 1. 测试网络连接
        String baseUrl = "https://news.softunis.com/hlwcy";
        if (!isNetworkAvailable(baseUrl)) {
            System.err.println("网络连接失败，无法访问软盟资讯网站，跳过爬取");
            return articles;
        }

        // 2. 直接返回空列表，暂时禁用爬虫功能
        System.err.println("爬虫功能暂时禁用，跳过爬取");
        return articles;
    }
}