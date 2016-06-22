package com.caorenhao.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 排序算法.
 * 
 * @author renhao.cao
 * 		   Create 2016年1月5日.
 */
public class SortUtil {
	
	/** 将Map<String,Integer>的数据按照value降序排列*/
	public static List<Map.Entry<String, Integer>> sortIntValueDesc(Map<String,Integer> map) throws IOException{ 
	    List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
	    Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {    
	        
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
	            return (o2.getValue() - o1.getValue());    
	        }
	    }); 
	    
	    return infoIds;
	}
	
	/** 将Map<String,Integer>的数据按照key升序排列*/
	public static List<Map.Entry<String, Integer>> sortStringKeyAsc(Map<String,Integer> map) throws IOException{ 
	    List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
	    Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {    
	        
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
	            return (Integer.parseInt(o1.getKey()) - Integer.parseInt(o2.getKey()));    
	        }
	    }); 
	    
	    return infoIds;
	}
	
	/** 将Map<String,Integer>的数据按照key升序排列*/
	public static List<Map.Entry<String, Integer>> sortMonthKeyAsc(Map<String, Integer> map) 
			throws IOException{ 
	    List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
	    Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {    
	        
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
	            return (int)(dateToLong(o1.getKey(), "yyyy-MM") - dateToLong(o2.getKey(), "yyyy-MM"));    
	        }
	    }); 
	    
	    return infoIds;
	}
	
	/** 将Map<String,Double>的数据按照value降序排列*/
	public static List<Map.Entry<String, Double>> sortDoubleValueDesc(Map<String,Double> map) throws IOException{ 
	    List<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(map.entrySet());  
	    Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {    
	        
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {    
	        	if (o2.getValue() > o1.getValue())
    				return 1;
    			else if (o1.getValue() > o2.getValue())
    				return -1;
    			else
    				return 0;
	        }
	    }); 
	    
	    return infoIds;
	}
	
	/** 将Map<Integer,Integer>的数据按照key升序排列*/
	public static List<Map.Entry<Integer, Integer>> sortIntKeyAsc(Map<Integer,Integer> map) 
			throws IOException{
	    List<Map.Entry<Integer, Integer>> infoIds = new ArrayList<Map.Entry<Integer, Integer>>(map.entrySet());
	    Collections.sort(infoIds, new Comparator<Map.Entry<Integer, Integer>>() {
	        
			public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
	            return (o1.getKey() - o2.getKey());
	        }
	    });
	    
	    return infoIds;
	}
	
	/** 将Map<Long,Integer>的数据按照key升序排列*/
	public static List<Map.Entry<Long, Integer>> sortLongKeyAsc(Map<Long,Integer> map) throws IOException{
	    List<Map.Entry<Long, Integer>> infoIds = new ArrayList<Map.Entry<Long, Integer>>(map.entrySet());
	    Collections.sort(infoIds, new Comparator<Map.Entry<Long, Integer>>() {
	        
			public int compare(Map.Entry<Long, Integer> o1, Map.Entry<Long, Integer> o2) {
	        	if (o1.getKey() > o2.getKey())
    				return 1;
    			else if (o2.getKey() > o1.getKey())
    				return -1;
    			else
    				return 0;
	        }
	    });
	    
	    return infoIds;
	}
	
	/** 将Map<Long,Map<String,Integer>>的数据按照key升序排列*/
	public static List<Map.Entry<Long,Map<String,Integer>>> sortLongStringCntASC(Map<Long,Map<String,Integer>> map) 
			throws IOException{
	    List<Map.Entry<Long,Map<String,Integer>>> infoIds = 
	    		new ArrayList<Map.Entry<Long,Map<String,Integer>>>(map.entrySet());
	    Collections.sort(infoIds, new Comparator<Map.Entry<Long,Map<String,Integer>>>() {
	        
			public int compare(Map.Entry<Long,Map<String,Integer>> o1, Map.Entry<Long,Map<String,Integer>> o2) {
	        	if (o1.getKey() > o2.getKey())
    				return 1;
    			else if (o2.getKey() > o1.getKey())
    				return -1;
    			else
    				return 0;
	        }
	    });
	    
	    return infoIds;
	}
	
	/** 将Map<String,Integer>的数据按照value升序排列*/
	public static List<Map.Entry<String, Integer>> sortIntegerV(Map<String,Integer> map) throws IOException{ 
	    List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());  
	    Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {    
	        
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
	            return (o1.getValue() - o2.getValue());    
	        }
	    }); 
	    
	    return infoIds;
	}
	
	/** 将String格式的时间转换成Long格式的时间*/
	public static Long dateToLong(String time, String dateFormat) {
		try {
			Date datet = new SimpleDateFormat(dateFormat).parse(time);
			long datettime = datet.getTime()/1000;
			
			return datettime;
		} catch (ParseException exception) {
			return 0L;
		}
	}
	
	/** 将Long格式的时间转换成String格式的时间*/
	public static String longToDate(long time, String dateFormat) {
		SimpleDateFormat sdf= new SimpleDateFormat(dateFormat);
		Date dt = new Date(time * 1000);  
		String sDateTime = sdf.format(dt);
		
		return sDateTime;
	}
	
	/** 将String格式的时间转换成Date格式的时间*/
	public static Date stringToDate(String str, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException exception) {
			exception.printStackTrace();
		}
		
		return date;
	}
	
	/** 将Date格式的时间转换成String格式的时间*/
	public static String dateToString(Date str, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String time = null;
		sdf = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL);  
		time = sdf.format(str);
		
		return time;
	}
	
	/**
	 * 获取百分比
	 * @param total 全部，部分
	 * @param part
	 * @return
	 */
	public static String getPercent(int total, int part) {
		DecimalFormat df = new DecimalFormat("#0.00%");
		float result = (float)part / (float)total;
		if(result == 0)
			return "0.00%";
		return df.format(result);
	}
	
	public static String getPercent(double number) {
		DecimalFormat df = new DecimalFormat("#0.00%");
		return df.format(number);
	}
	
	public static String getDouble(double number) {
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(number);
	}
	
}
