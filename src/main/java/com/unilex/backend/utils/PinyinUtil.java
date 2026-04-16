package com.unilex.backend.utils;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinUtil {

    public static String toPinyin(String chinese) {
        StringBuilder result = new StringBuilder();

        for (char c : chinese.toCharArray()) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
                if (pinyinArray != null) {
                    result.append(pinyinArray[0].replaceAll("\\d", ""));
                }
            } else {
                result.append(c);
            }
        }
        return result.toString().toLowerCase();
    }
}
