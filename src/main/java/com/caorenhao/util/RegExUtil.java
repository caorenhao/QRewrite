package com.caorenhao.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 常用正则表达式.
 * 
 * @author renhao.cao
 * 		   Create 2016年3月21日.
 */
public class RegExUtil {
	
	/** yyyy-MM-dd*/
	public static String yearMonthDay = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
	
	/** yyyy-MM-dd HH:mm*/
	public static String yearMonthDayHourMinute = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29) (20|21|22|23|[0-1]?\\d):[0-5]?\\d";
	
	/** yyyy-MM-dd HH:mm:ss*/
	public static String yearMonthDayHourMinuteSecond = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d";
	
	/** 中文*/
	public static String chinese = "[\u4e00-\u9fa5]";
	
	public static String getContentByReg(String text, String reg) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(text);
		StringBuilder sb = new StringBuilder();
		while (m.find()){
			sb.append(m.group());
		}
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String text = "2016-02-29 13:10:21 ddg5444 ";
		Pattern p = Pattern.compile(chinese);
		Matcher m = p.matcher(text);
		StringBuilder sb = new StringBuilder();
		while (m.find()){
			sb.append(m.group());
		}
		System.out.println(sb.toString());
	}
	
}
