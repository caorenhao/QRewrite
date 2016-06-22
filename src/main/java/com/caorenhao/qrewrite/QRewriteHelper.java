package com.caorenhao.qrewrite;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author renhao.cao
 * 		   Create 2016年6月22日.
 */
public class QRewriteHelper {
	
	/**
     * 根据Unicode编码完美的判断中文汉字和符号.
     * 
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }
    
    /**
     * 抽取出中文字符.
     * 
     * @param text
     * @return
     */
    public static List<String> extraChinese(String text) {
    	String patternStr = "[\u4e00-\u9fa5]";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        List<String> ret = new ArrayList<String>();
        while(matcher.find()) {
        	ret.add(matcher.group());
        }
        
        return ret;
    }

}
