package com.entrepreneur.platform.util;

/**
 * 内容处理工具类，用于清理和规整爬取的文章内容
 */
public class ContentProcessorUtil {
    
    /**
     * 清理文章内容，去除冗余信息、广告内容及无关数据
     * @param content 原始内容
     * @return 清理后的内容
     */
    public static String cleanContent(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 去除广告内容
        content = content.replaceAll("[广告推广].*?[结束]", "");
        content = content.replaceAll("[推广].*?[点击]", "");
        
        // 去除冗余信息
        content = content.replaceAll("\\s+", " "); // 替换多个空格为单个空格
        content = content.replaceAll("(?i)(来源|出处|原文链接).*$", ""); // 去除来源信息
        
        // 去除首尾空白
        content = content.trim();
        
        return content;
    }
    
    /**
     * 生成内容摘要
     * @param content 文章内容
     * @param maxLength 摘要最大长度
     * @return 内容摘要
     */
    public static String generateSummary(String content, int maxLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 清理内容
        content = cleanContent(content);
        
        // 截取摘要
        if (content.length() <= maxLength) {
            return content;
        }
        
        // 尝试在句子结尾处截取
        int endIndex = content.substring(0, maxLength).lastIndexOf("。");
        if (endIndex == -1) {
            endIndex = content.substring(0, maxLength).lastIndexOf(".");
        }
        if (endIndex == -1) {
            endIndex = maxLength;
        }
        
        return content.substring(0, endIndex) + "...";
    }
    
    /**
     * 清理标题，去除冗余信息
     * @param title 原始标题
     * @return 清理后的标题
     */
    public static String cleanTitle(String title) {
        if (title == null || title.isEmpty()) {
            return "";
        }
        
        // 去除首尾空白
        title = title.trim();
        
        // 去除标题中的冗余信息
        title = title.replaceAll("\\s+-.*$", ""); // 去除末尾的破折号及后面的内容
        title = title.replaceAll("\\s+-.*$", ""); // 去除末尾的破折号及后面的内容
        
        return title;
    }
    
    /**
     * 规范化发布日期格式
     * @param date 原始日期字符串
     * @return 规范化后的日期
     */
    public static String normalizeDate(String date) {
        if (date == null || date.isEmpty()) {
            return "未知日期";
        }
        
        // 去除冗余信息
        date = date.trim();
        
        // 处理常见的日期格式
        if (date.contains("T")) {
            // ISO格式：2023-10-01T12:00:00
            date = date.split("T")[0];
        } else if (date.contains(" ")) {
            // 包含时间的格式：2023-10-01 12:00:00
            date = date.split(" ")[0];
        }
        
        return date;
    }
    
    /**
     * 确定文章分类
     * @param title 文章标题
     * @param content 文章内容
     * @return 文章分类
     */
    public static String determineCategory(String title, String content) {
        // 清理内容
        title = cleanTitle(title);
        content = cleanContent(content);
        
        // 分类关键词
        if (containsAny(title, content, new String[]{"投资", "融资", "资金", "资本", "风投", "创投"})) {
            return "投资融资";
        } else if (containsAny(title, content, new String[]{"科技", "技术", "创新", "研发", "AI", "人工智能", "区块链"})) {
            return "科技创新";
        } else {
            return "创业资讯";
        }
    }
    
    /**
     * 检查文本是否包含指定关键词中的任何一个
     * @param title 标题
     * @param content 内容
     * @param keywords 关键词数组
     * @return 是否包含任何关键词
     */
    private static boolean containsAny(String title, String content, String[] keywords) {
        for (String keyword : keywords) {
            if (title.contains(keyword) || content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 计算内容质量评分
     * @param title 标题
     * @param content 内容
     * @return 质量评分（0-1）
     */
    public static double calculateQualityScore(String title, String content) {
        double score = 0.0;
        
        // 标题长度评分
        if (title != null && title.length() > 5 && title.length() < 50) {
            score += 0.2;
        }
        
        // 内容长度评分
        if (content != null && content.length() > 300) {
            score += Math.min(content.length() / 1000.0, 0.4);
        }
        
        // 关键词评分
        String[] importantKeywords = {"创业", "投资", "科技", "创新", "融资", "政策", "市场", "机会"};
        int keywordCount = 0;
        for (String keyword : importantKeywords) {
            if (title.contains(keyword) || content.contains(keyword)) {
                keywordCount++;
            }
        }
        score += keywordCount * 0.05;
        
        // 确保评分在0-1之间
        return Math.min(score, 1.0);
    }
}