package com.entrepreneur.platform.util;

/**
 * Markdown格式化测试类
 */
public class MarkdownFormatterTest {
    public static void main(String[] args) {
        // 测试文章内容
        String testContent = "这是一篇关于创业的文章。\n\n创业是一个充满挑战的过程，需要创业者具备多方面的能力。\n\n首先，创业者需要有敏锐的市场洞察力，能够发现市场机会。\n\n其次，创业者需要有坚定的信念和毅力，能够在困难面前不放弃。\n\n最后，创业者需要有良好的团队协作能力，能够组建和管理一个高效的团队。\n\n总之，创业是一个需要综合能力的过程，只有不断学习和提升自己，才能在创业道路上走得更远。";
        
        System.out.println("原始内容:");
        System.out.println(testContent);
        System.out.println("\n=== 处理过程 ===");
        
        // 1. 移除HTML标签
        String step1 = MarkdownFormatterUtil.removeHtmlTags(testContent);
        System.out.println("1. 移除HTML标签:");
        System.out.println(step1);
        
        // 2. 移除Emoji表情
        String step2 = MarkdownFormatterUtil.removeEmoji(step1);
        System.out.println("\n2. 移除Emoji表情:");
        System.out.println(step2);
        
        // 3. 智能分段
        String step3 = MarkdownFormatterUtil.smartSegmentation(step2);
        System.out.println("\n3. 智能分段:");
        System.out.println(step3);
        
        // 4. 转换为Markdown格式
        String step4 = MarkdownFormatterUtil.convertToMarkdown(step3);
        System.out.println("\n4. 转换为Markdown格式:");
        System.out.println(step4);
        
        // 5. 综合排版处理
        String finalResult = MarkdownFormatterUtil.comprehensiveFormat(testContent);
        System.out.println("\n=== 最终结果 ===");
        System.out.println(finalResult);
    }
}