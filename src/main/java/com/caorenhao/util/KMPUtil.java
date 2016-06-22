package com.caorenhao.util;

/**
 * KMP匹配算法.
 *
 * @author renhao.cao.
 *         Created 2015年3月19日.
 */
public class KMPUtil {
	
	/**
	 * 对子串加以预处理，从而找到匹配失败时子串回退的位置
	 * 找到匹配失败时的最合适的回退位置，而不是回退到子串的第一个字符，即可提高查找的效率
	 * 因此为了找到这个合适的位置，先对子串预处理，从而得到一个回退位置的数组
	 * 
	 * @param P 待查找字符串
	 * @return int[]
	 */
	public static int[] preProcess(String P) {
		int size = P.length();
		int[] next = new int[size];
		next[0] = 0;
		int j = 0;
		for(int i = 1;i < size;i++){
			while(j > 0 && P.charAt(j) != P.charAt(i)) {  
                j = next[j-1];   
            }  
            if(P.charAt(j) == P.charAt(i))  
                j++;  
            next[i] = j;	
		}
		return next;
	}
	
	/**
	 * kmp匹配
	 * 
	 * @param parStr
	 * @param subStr
	 * @return int
	 */
	public static int kmp(String parStr, String subStr) {
		int subSize = subStr.length();
		int parSize = parStr.length();
		int[] P = preProcess(subStr);
		int j=0;
		int k =0;
		for(int i=0;i < parSize;i++){
			while(j > 0 && subStr.charAt(j) != parStr.charAt(i)){
				j=P[j-1];
			}
			if(subStr.charAt(j)==parStr.charAt(i)){
				j++;
			}
			if(j==subSize){
				j=P[j-1];
				k++;
				//System.out.printf("Find subString '%s' at %d\n",subStr,i-subSize+1);
			}
		}
		return k;
	}
	
	/**
	 * 字符串匹配，用于固定字符与用空格隔开的字典中字符串进行匹配
	 * parStr是字典,subStr是我们需要匹配的字符串.
	 *
	 * @param parStr
	 * @param subStr
	 * @return int
	 */
	public int kmpString(String parStr, String subStr) {
		int subSize = subStr.length();						//需要匹配的
		int parSize = parStr.length();						//字典中的
		int[] P = preProcess(subStr);
		int j = 0;
		int k = 0;
		
		for(int i = 0;i < parSize;i++){
			while(j > 0 && subStr.charAt(j) != parStr.charAt(i)){
				j = P[j-1];
			}
			if(subStr.charAt(j)==parStr.charAt(i)){
				j++;
			}
			if(j == subSize){				
				j = P[j-1];
				if(parSize == subSize){
					k++;
				}
				else if(i == 1 && String.valueOf(parStr.charAt(i+1)).equals(" ")){										//判断字典中所找出的需要匹配的词语后一位是否为空格
					k++;
				}
				else if(i == (parSize-1) && String.valueOf(parStr.charAt(i-subSize)).equals(" ")){						//判断字典中所找出的需要匹配的词语前一位是否为空格
					k++;
				}
				else if(i == (parSize-1) && !String.valueOf(parStr.charAt(i-subSize)).equals(" ")){						
				}
				else if(String.valueOf(parStr.charAt(i+1)).equals(" ") && String.valueOf(parStr.charAt(i-subSize)).equals(" ")){			
					k++;				
				}				
			}
		}
		//System.out.printf("Totally found %d times for '%s'.\n",k,subStr);
		return k;
	}
}