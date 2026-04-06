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

    // 爬取软盟资讯文章的方法
    public static List<Article> scrapeArticles() throws IOException {
        List<Article> articles = new ArrayList<>();
        Set<String> articleUrls = new HashSet<>();

        // 1. 爬取多个页面，获取文章链接
        String baseUrl = "https://news.softunis.com/hlwcy";
        int maxPages = 5; // 爬取前5页
        
        for (int page = 1; page <= maxPages; page++) {
            String pageUrl = page == 1 ? baseUrl : baseUrl + "/page/" + page;
            System.out.println("正在访问页面: " + pageUrl);

            try {
                // 增加超时时间到20秒，添加更多的请求头
                Document pageDoc = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                        .header("Connection", "keep-alive")
                        .header("Upgrade-Insecure-Requests", "1")
                        .timeout(20000)
                        .get();
                System.out.println("页面访问成功: " + pageUrl);

                // 提取文章链接
                Elements articleElements = pageDoc.select("a[href]");
                System.out.println("找到 " + articleElements.size() + " 个链接");

                // 过滤出文章详情页链接
                for (Element element : articleElements) {
                    String href = element.attr("href");
                    // 过滤出新闻详情页链接，排除导航链接和其他非文章链接
                    if (href.endsWith(".html") && !href.contains("/page/") && !href.contains("#")) {
                        String fullUrl = href.startsWith("http") ? href : "https://news.softunis.com" + (href.startsWith("/") ? href : "/" + href);
                        articleUrls.add(fullUrl);
                        System.out.println("添加链接: " + fullUrl);
                    }
                }
            } catch (Exception e) {
                System.err.println("访问页面失败: " + pageUrl);
                e.printStackTrace();
            } finally {
                // 控制请求频率，避免被网站封禁
                try {
                    System.out.println("等待1秒...");
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        System.out.println("共提取 " + articleUrls.size() + " 个文章链接");

        // 2. 访问每个文章链接，提取标题和内容
        int count = 0;
        int successCount = 0;
        int errorCount = 0;
        System.out.println("开始爬取文章内容...");
        for (String url : articleUrls) {
            try {
                System.out.println("正在爬取: " + url);
                // 增加超时时间到20秒，添加更多的请求头
                Document articleDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                        .header("Connection", "keep-alive")
                        .header("Upgrade-Insecure-Requests", "1")
                        .timeout(20000)
                        .get();

                // 提取标题
                String title = "未知标题";
                Element h1Element = articleDoc.selectFirst("h1");
                if (h1Element != null) {
                    title = h1Element.text();
                } else {
                    // 尝试其他标题选择器
                    Element titleElement = articleDoc.selectFirst("h2, .title, .article-title, .headline");
                    if (titleElement != null) {
                        title = titleElement.text();
                    }
                }
                // 清理标题
                title = ContentProcessorUtil.cleanTitle(title);
                System.out.println("提取的标题: " + title);

                // 提取内容 - 增强版
                String content = "无内容";

                // 尝试1: 查找常见的文章内容容器
                String[] contentSelectors = {
                        "div.article-content",
                        "div.content",
                        "article",
                        "div.post-content",
                        "div.article-body",
                        "div.content-wrapper",
                        "div.main-content",
                        "div.article-main",
                        "div.article-detail",
                        "div.article-text",
                        "div.news-content",
                        "div.content-body"
                };

                for (String selector : contentSelectors) {
                    Elements elements = articleDoc.select(selector);
                    for (Element element : elements) {
                        String text = element.text();
                        if (text.length() > content.length()) {
                            content = text;
                        }
                    }
                }

                // 尝试2: 查找所有段落标签并拼接
                if (content.length() < 500) {
                    Elements pElements = articleDoc.select("p");
                    StringBuilder sb = new StringBuilder();
                    for (Element p : pElements) {
                        String pText = p.text();
                        if (!pText.isEmpty()) {
                            sb.append(pText).append("\n\n");
                        }
                    }
                    if (sb.length() > content.length()) {
                        content = sb.toString();
                    }
                }

                // 尝试3: 查找所有div标签，选择内容最长的
                if (content.length() < 500) {
                    Elements divElements = articleDoc.select("div");
                    for (Element div : divElements) {
                        String divText = div.text();
                        if (divText.length() > content.length() && divText.length() > 500) {
                            content = divText;
                        }
                    }
                }

                // 尝试4: 获取整个body内容
                if (content.length() < 500) {
                    content = articleDoc.body().text();
                }

                // 清理内容
                content = ContentProcessorUtil.cleanContent(content);
                System.out.println("提取的内容长度: " + content.length() + " 字符");

                // 提取文章图片
                String imageUrl = "";
                try {
                    // 尝试提取文章中的第一张图片
                    Element imgElement = articleDoc.selectFirst("img[src$=.jpg], img[src$=.jpeg], img[src$=.png], img[src$=.gif]");
                    if (imgElement != null) {
                        imageUrl = imgElement.attr("src");
                        // 确保图片URL是完整的
                        if (!imageUrl.startsWith("http")) {
                            // 如果是相对路径，拼接完整URL
                            if (imageUrl.startsWith("/")) {
                                imageUrl = "https://news.softunis.com" + imageUrl;
                            } else {
                                // 尝试从当前文章URL中提取基础URL
                                int lastSlashIndex = url.lastIndexOf("/");
                                if (lastSlashIndex != -1) {
                                    String baseUrlFromArticle = url.substring(0, lastSlashIndex + 1);
                                    imageUrl = baseUrlFromArticle + imageUrl;
                                }
                            }
                        }
                        System.out.println("提取的图片URL: " + imageUrl);
                    } else {
                        // 如果文章中没有图片，从Bing搜索获取相关图片
                        String searchQuery = title + " 科技";
                        String encodedQuery = java.net.URLEncoder.encode(searchQuery, "UTF-8");
                        String bingImageUrl = "https://cn.bing.com/images/search?q=" + encodedQuery + "&form=IACFSM&first=1";
                        System.out.println("从Bing搜索获取图片: " + bingImageUrl);
                        
                        // 访问Bing图片搜索页面并提取第一张图片
                        Document bingDoc = Jsoup.connect(bingImageUrl)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                                .header("Connection", "keep-alive")
                                .header("Upgrade-Insecure-Requests", "1")
                                .timeout(20000)
                                .get();
                        
                        // 尝试提取Bing图片搜索结果中的第一张图片
                        Element bingImgElement = bingDoc.selectFirst("img[src^=https://tse1.mm.bing.net]");
                        if (bingImgElement != null) {
                            imageUrl = bingImgElement.attr("src");
                            System.out.println("从Bing获取的图片URL: " + imageUrl);
                        }
                    }
                } catch (Exception e) {
                    // 忽略图片提取错误
                    System.err.println("提取图片失败: " + e.getMessage());
                }

                // 提取来源
                String source = "软盟资讯";
                
                // 提取作者
                String author = "未知作者";
                try {
                    // 尝试从常见的作者元素提取
                    Element authorElement = articleDoc.selectFirst(".author, .by, .writer, .source, meta[name='author'], meta[property='article:author']");
                    if (authorElement != null) {
                        if (authorElement.tagName().equals("meta")) {
                            author = authorElement.attr("content");
                        } else {
                            author = authorElement.text();
                        }
                    }
                } catch (Exception e) {
                    // 忽略作者提取错误
                    System.err.println("提取作者失败: " + e.getMessage());
                }
                
                // 提取发布日期
                String publicationDate = "未知日期";
                try {
                    // 尝试从meta标签或时间元素提取日期
                    Element timeElement = articleDoc.selectFirst("time, .time, .date, .publish-time, .post-date");
                    if (timeElement != null) {
                        publicationDate = timeElement.text();
                    } else {
                        // 尝试从meta标签提取
                        Element metaDate = articleDoc.selectFirst("meta[name='publishDate'], meta[property='article:published_time'], meta[name='datePublished']");
                        if (metaDate != null) {
                            publicationDate = metaDate.attr("content");
                        }
                    }
                } catch (Exception e) {
                    // 忽略日期提取错误
                    System.err.println("提取日期失败: " + e.getMessage());
                }
                // 规范化日期格式
                publicationDate = ContentProcessorUtil.normalizeDate(publicationDate);
                
                // 提取标签
                List<String> tags = new ArrayList<>();
                try {
                    // 尝试从常见的标签元素提取
                    Elements tagElements = articleDoc.select(".tag, .tags, .keywords, .keyword, a[rel='tag'], meta[name='keywords']");
                    for (Element tagElement : tagElements) {
                        if (tagElement.tagName().equals("meta")) {
                            String keywords = tagElement.attr("content");
                            if (!keywords.isEmpty()) {
                                String[] keywordArray = keywords.split(",");
                                for (String keyword : keywordArray) {
                                    String trimmedKeyword = keyword.trim();
                                    if (!trimmedKeyword.isEmpty()) {
                                        tags.add(trimmedKeyword);
                                    }
                                }
                            }
                        } else {
                            String tagText = tagElement.text().trim();
                            if (!tagText.isEmpty()) {
                                tags.add(tagText);
                            }
                        }
                    }
                } catch (Exception e) {
                    // 忽略标签提取错误
                    System.err.println("提取标签失败: " + e.getMessage());
                }
                
                // 生成内容摘要
                String contentSummary = ContentProcessorUtil.generateSummary(content, 200);
                
                // 确定分类
                String category = ContentProcessorUtil.determineCategory(title, content);
                
                // 计算相关度分数
                double relevanceScore = ContentProcessorUtil.calculateQualityScore(title, content);
                
                // 降低阈值，确保能获取到更多内容
                if (!"未知标题".equals(title) && !"无内容".equals(content) && content.length() > 300) {
                    articles.add(new Article(title, content, url, source, author, publicationDate, contentSummary, category, relevanceScore, imageUrl, tags));
                    successCount++;
                    System.out.println("成功添加文章 " + successCount);
                } else {
                    System.out.println("跳过无效文章");
                }

                count++;
                // 控制请求频率，避免被网站封禁
                System.out.println("等待2秒...");
                Thread.sleep(2000);
            } catch (Exception e) {
                // 忽略个别文章的错误，继续爬取其他文章
                System.err.println("爬取文章失败: " + url);
                e.printStackTrace();
                // 发生错误也要增加计数，避免无限循环
                count++;
                errorCount++;
                // 等待更长时间再继续
                try {
                    System.out.println("等待3秒...");
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }

        System.out.println("爬取完成，共获取 " + articles.size() + " 篇有效文章");
        System.out.println("爬取统计: 成功 " + successCount + " 篇, 失败 " + errorCount + " 篇, 总链接 " + articleUrls.size() + " 个");
        return articles;
    }
}