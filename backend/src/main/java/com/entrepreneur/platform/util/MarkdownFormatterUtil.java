package com.entrepreneur.platform.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown格式转换工具类，用于将文章正文转换为结构规范的Markdown格式
 */
public class MarkdownFormatterUtil {
    
    /**
     * 将文章正文转换为Markdown格式
     * @param content 原始文章正文
     * @return 转换后的Markdown格式内容
     */
    public static String convertToMarkdown(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 1. 清理原始内容（不使用ContentProcessorUtil.cleanContent，因为它会移除换行符）
        // 移除广告内容
        content = content.replaceAll("[广告推广].*?[结束]", "");
        content = content.replaceAll("[推广].*?[点击]", "");
        // 去除首尾空白
        content = content.trim();
        
        // 2. 处理标题层级
        content = processHeadings(content);
        
        // 3. 处理段落
        content = processParagraphs(content);
        
        // 4. 处理列表
        content = processLists(content);
        
        // 5. 处理重点内容强调
        content = processEmphasis(content);
        
        // 6. 处理其他格式优化
        content = processOtherFormats(content);
        
        return content;
    }
    
    /**
     * 处理标题层级
     * @param content 原始内容
     * @return 处理后的内容
     */
    private static String processHeadings(String content) {
        // 处理二级标题（主要章节）- 使用更严格的模式，只匹配以数字或中文数字开头的行
        Pattern h2Pattern = Pattern.compile("^(\\s*)([一二三四五六七八九十]+、|\\d+\\.)\\s*(\\w+.*?)$", Pattern.MULTILINE);
        Matcher h2Matcher = h2Pattern.matcher(content);
        content = h2Matcher.replaceAll("$1## $3\n\n");
        
        // 处理三级标题（子章节）- 使用更严格的模式
        Pattern h3Pattern = Pattern.compile("^(\\s{2,})([一二三四五六七八九十]+\\.|\\d+\\.)\\s*(\\w+.*?)$", Pattern.MULTILINE);
        Matcher h3Matcher = h3Pattern.matcher(content);
        content = h3Matcher.replaceAll("$1### $3\n\n");
        
        // 处理四级标题（小节）- 使用更严格的模式
        Pattern h4Pattern = Pattern.compile("^(\\s{4,})([一二三四五六七八九十]+\\.|\\d+\\.)\\s*(\\w+.*?)$", Pattern.MULTILINE);
        Matcher h4Matcher = h4Pattern.matcher(content);
        content = h4Matcher.replaceAll("$1#### $3\n\n");
        
        // 确保内容中没有以#开头的行，避免被识别为一级标题
        content = content.replaceAll("^#\\s+(.+?)$", "$1");
        
        return content;
    }
    
    /**
     * 处理段落
     * @param content 原始内容
     * @return 处理后的内容
     */
    private static String processParagraphs(String content) {
        // 将多个连续的换行符替换为两个换行符（Markdown段落分隔）
        content = content.replaceAll("\\n{3,}", "\n\n");
        
        // 确保每个段落之间有两个换行符
        content = content.replaceAll("([^\\n])\\n([^\\n])", "$1\n\n$2");
        
        // 为每个段落添加首行缩进（使用两个空格），但不处理标题、列表和引用
        Pattern paragraphPattern = Pattern.compile("^(?!#|\\-|\\d+\\.|>)(.*?)$", Pattern.MULTILINE);
        Matcher paragraphMatcher = paragraphPattern.matcher(content);
        content = paragraphMatcher.replaceAll("  $1");
        
        return content;
    }
    
    /**
     * 处理列表
     * @param content 原始内容
     * @return 处理后的内容
     */
    private static String processLists(String content) {
        // 处理无序列表
        Pattern unorderedListPattern = Pattern.compile("^(\\s*)([•·、]|\\*|\\-|\\+)(\\s+)(.*?)$", Pattern.MULTILINE);
        Matcher unorderedListMatcher = unorderedListPattern.matcher(content);
        content = unorderedListMatcher.replaceAll("$1- $4");
        
        // 处理有序列表
        Pattern orderedListPattern = Pattern.compile("^(\\s*)(\\d+)([.)、])(\\s+)(.*?)$", Pattern.MULTILINE);
        Matcher orderedListMatcher = orderedListPattern.matcher(content);
        content = orderedListMatcher.replaceAll("$1$2. $5");
        
        return content;
    }
    
    /**
     * 处理重点内容强调
     * @param content 原始内容
     * @return 处理后的内容
     */
    private static String processEmphasis(String content) {
        // 处理加粗（使用**包围）
        Pattern boldPattern = Pattern.compile("([\"'《》])(.*?)\\1");
        Matcher boldMatcher = boldPattern.matcher(content);
        content = boldMatcher.replaceAll("**$2**");
        
        // 处理斜体（使用*包围）
        // 这里可以根据需要添加斜体处理逻辑
        
        // 处理引用（使用>前缀）
        Pattern quotePattern = Pattern.compile("^(\\s*)(引用|摘要|提示|注意|警告):(\\s+)(.*?)$", Pattern.MULTILINE);
        Matcher quoteMatcher = quotePattern.matcher(content);
        content = quoteMatcher.replaceAll("$1> $4");
        
        return content;
    }
    
    /**
     * 处理其他格式优化
     * @param content 原始内容
     * @return 处理后的内容
     */
    private static String processOtherFormats(String content) {
        // 处理链接
        Pattern linkPattern = Pattern.compile("(https?:\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?)");
        Matcher linkMatcher = linkPattern.matcher(content);
        content = linkMatcher.replaceAll("[$0]($0)");
        
        // 处理数字列表的特殊情况
        content = content.replaceAll("^(\\s*)([一二三四五六七八九十]+)、(\\s+)(.*?)$", "$1- $4");
        
        // 处理常见的分隔符
        content = content.replaceAll("[=]{3,}", "---");
        content = content.replaceAll("[_]{3,}", "---");
        
        // 处理代码块
        Pattern codeBlockPattern = Pattern.compile("```([\\s\\S]*?)```");
        Matcher codeBlockMatcher = codeBlockPattern.matcher(content);
        content = codeBlockMatcher.replaceAll("```\n$1\n```");
        
        // 处理表格
        // 这里可以根据需要添加表格处理逻辑
        
        return content;
    }
    
    /**
     * 格式化文章标题为Markdown格式
     * @param title 原始标题
     * @return 格式化后的标题
     */
    public static String formatTitle(String title) {
        if (title == null || title.isEmpty()) {
            return "";
        }
        
        // 清理标题
        title = ContentProcessorUtil.cleanTitle(title);
        
        // 转换为Markdown一级标题
        return "# " + title + "\n\n";
    }
    
    /**
     * 完整处理文章，包括标题和正文
     * @param title 文章标题
     * @param content 文章正文
     * @return 完整的Markdown格式文章
     */
    public static String processArticle(String title, String content) {
        StringBuilder markdown = new StringBuilder();
        
        // 处理标题
        markdown.append(formatTitle(title));
        
        // 处理正文
        markdown.append(convertToMarkdown(content));
        
        return markdown.toString();
    }
    
    /**
     * 智能分段处理
     * @param content 原始内容
     * @return 分段后的内容
     */
    public static String smartSegmentation(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 基于标点符号和语义进行智能分段
        // 1. 保留原始空格，只清理多余的连续空格
        content = content.replaceAll("\\s{2,}", " ");
        
        // 2. 在句号、问号、感叹号后添加换行，无论后面是否有空格
        content = content.replaceAll("([。！？.!?])", "$1\n\n");
        
        // 3. 处理过长的段落
        String[] paragraphs = content.split("\\n\\n");
        StringBuilder result = new StringBuilder();
        
        for (String paragraph : paragraphs) {
            // 去除段落开头的空格
            String trimmedParagraph = paragraph.trim();
            
            if (trimmedParagraph.isEmpty()) {
                continue;
            }
            
            if (trimmedParagraph.length() > 500) {
                // 将长段落分割为多个短段落
                String[] sentences = trimmedParagraph.split("[。！？.!?]");
                StringBuilder newParagraph = new StringBuilder();
                int sentenceCount = 0;
                
                for (String sentence : sentences) {
                    if (!sentence.trim().isEmpty()) {
                        newParagraph.append(sentence.trim()).append("。");
                        sentenceCount++;
                        
                        // 每3-4个句子为一个段落
                        if (sentenceCount >= 3 && newParagraph.length() > 200) {
                            result.append(newParagraph.toString()).append("\n\n");
                            newParagraph = new StringBuilder();
                            sentenceCount = 0;
                        }
                    }
                }
                
                if (newParagraph.length() > 0) {
                    result.append(newParagraph.toString()).append("\n\n");
                }
            } else {
                result.append(trimmedParagraph).append("\n\n");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * 清理HTML标签
     * @param content 原始内容
     * @return 清理后的内容
     */
    public static String removeHtmlTags(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 移除HTML标签
        content = content.replaceAll("<[^>]+>", "");
        
        // 移除HTML实体
        content = content.replaceAll("&nbsp;", " ");
        content = content.replaceAll("&lt;", "<");
        content = content.replaceAll("&gt;", ">");
        content = content.replaceAll("&amp;", "&");
        content = content.replaceAll("&quot;", "\"");
        content = content.replaceAll("&apos;", "'");
        
        return content;
    }
    
    /**
     * 移除Emoji表情
     * @param content 原始内容
     * @return 清理后的内容
     */
    public static String removeEmoji(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 移除Emoji表情
        content = content.replaceAll("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+", "");
        
        return content;
    }
    
    /**
     * 综合排版处理
     * @param content 原始内容
     * @return 排版后的内容
     */
    public static String comprehensiveFormat(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // 1. 移除HTML标签
        content = removeHtmlTags(content);
        
        // 2. 移除Emoji表情
        content = removeEmoji(content);
        
        // 3. 智能分段
        content = smartSegmentation(content);
        
        // 4. 转换为Markdown格式
        content = convertToMarkdown(content);
        
        return content;
    }
}