package com.ksyun.trade.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

//工具类把汉字转换成拼音
public class PinyinUtils {
    public static String getPinYin(String chinese) {
        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : chars) {
            try {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyins != null && pinyins.length > 0) {
                    sb.append(pinyins[0]);
                } else {
                    sb.append(c);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
