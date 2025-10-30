package com.utils;

/**
 * @Author RenBo
 * @Date 2024-02-18 16:50
 * @PackageName:com.bot.cat.common.utils
 * @ClassName: TextUtils
 * @Description: TODO
 * @Version 1.0
 */
public class TextUtils {

    /**
     * 检查字符串中是否包含中文字符，并将所有中文字符替换为指定的英文字符串。
     * @param input 原始字符串
     * @return 替换中文字符后的字符串
     */
    public static String replaceChineseWithEnglish(String input) {
        // 检测并替换中文字符的正则表达式
        String chineseRegex = "[\\u4e00-\\u9fa5]";
        // 将中文字符替换为"Chinese"（或其他英文单词）
        String replacement = " english "; // 示例替换为英文单词"English"

        // 替换操作
        String output = input.replaceAll(chineseRegex, replacement);

        return output;
    }

    public static void main(String[] args) {
        String originalText = "这是一个示例文本，包含English和中文字符。";
        String processedText = replaceChineseWithEnglish(originalText);
        System.out.println("原始文本: " + originalText);
        System.out.println("处理后文本: " + processedText);
    }
}

