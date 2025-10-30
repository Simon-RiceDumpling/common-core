package com.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author zhanghao
 * @description 简单拼音
 * @date 2023/8/11 10:31
 */
public class PinyinUtil {
    public static String getPinYinHeadChar(String str) {
        StringBuilder convert = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert.append(pinyinArray[0].charAt(0));
            } else {
                convert.append(word);
            }
        }
        return convert.toString().toUpperCase();
    }
}
