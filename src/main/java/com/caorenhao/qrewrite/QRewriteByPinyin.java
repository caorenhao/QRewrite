package com.caorenhao.qrewrite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.caorenhao.util.IOUtil;
import com.caorenhao.util.SortUtil;

/**
 * 使用拼音纠正错别字.
 * 	单个字不纠正,中文字符超过20个也不纠正
 * 
 * @author renhao.cao
 * 		   Create 2015年12月31日.
 */
public class QRewriteByPinyin {
	
	/** 字的多音表*/
	private ConcurrentHashMap<String, List<String>> map;
	
	/** 词拼音表*/
	private ConcurrentHashMap<String, List<String>> wordPinyinMap;
	
	/** 拼音词表*/
	private ConcurrentHashMap<String, List<String>> pinyinWordMap;
	
	/** 不修改词集合(避免将正确的词由于同音修改为了错误的词)*/
	private ConcurrentHashMap<String, Integer> correctionMap;
	
	/** AC自动机*/
	private AhoCorasickDoubleArrayTrie<String> acdat;
	
	/**
	 * 初始化拼音表.
	 *
	 * @throws Exception
	 */
	public QRewriteByPinyin() throws Exception {
		map = new ConcurrentHashMap<String, List<String>>();
		pinyinWordMap = new ConcurrentHashMap<String, List<String>>();
		correctionMap = new ConcurrentHashMap<String, Integer>();
		List<String> list = new ArrayList<String>();
		list = IOUtil.readStringListFromFile(new File(QRewriteConst.PATH_PINYIN_DIC), list);
		for(String line : list) {
			if(!line.startsWith("*"))
				continue;
			String[] strs = line.split(" ");
			if(strs.length == 2) {
				String word = strs[0].replace("*", "");
				String[] pinyinStr = strs[1].split("（");
				String[] pinyin = pinyinStr[1].replace("）", "").replaceAll("\\d+","").split("/");
				List<String> pinyins = new ArrayList<String>();
				Set<String> filter = new HashSet<String>();
				for(String str : pinyin) {
					if(filter.add(str))
						pinyins.add(str);
				}
				map.put(word, pinyins);
			}
		}
		
		diTrans();
		String[] keyArray = getdata();
		acdat = buildASimpleAhoCorasickDoubleArrayTrie(keyArray);
	}
	
	/**
	 * 将待纠正词表转化为拼音表.
	 * 
	 * @throws Exception
	 */
	private void diTrans() throws Exception {
		wordPinyinMap = new ConcurrentHashMap<String, List<String>>();
		List<String> list = new ArrayList<String>();
		list = IOUtil.readStringListFromFile(new File(QRewriteConst.PATH_WORD_DIC), list);
		for(String di : list) {
			// 过滤掉单个字的词
			if(di.length() == 1)
				continue;
			
			correctionMap.put(di, 1);
			List<String> pinyins = transform(di);
			for(String pinyin : pinyins) {
				List<String> dis = new ArrayList<String>();
				if(wordPinyinMap.containsKey(pinyin)) {
					dis = wordPinyinMap.get(pinyin);
				}
				dis.add(di);
				wordPinyinMap.put(pinyin, dis);
				
				List<String> dis1 = new ArrayList<String>();
				if(pinyinWordMap.containsKey(pinyin)) {
					dis1 = pinyinWordMap.get(pinyin);
				}
				dis1.add(di);
				pinyinWordMap.put(pinyin, dis1);
			}
		}
	}
	
	/**
	 * 输出一个词可能的拼音组成.
	 * 
	 * @param words
	 * @return
	 */
	public List<String> transform(String words) {
		List<String> rets = new ArrayList<String>();
		for(int i = 0; i < words.length(); i++) {
			char ch = words.charAt(i);
			String word = String.valueOf(words.charAt(i));
			
			List<String> temp = new ArrayList<String>();
			if(QRewriteHelper.isChinese(ch)) {
				if(!map.containsKey(word))
					continue;
				
				List<String> pinyins = map.get(word);
				for(String ret : rets) {
					for(String pinyin : pinyins) {
						StringBuilder sb = new StringBuilder();
						sb.append(ret);
						sb.append(pinyin);
						temp.add(sb.toString());
					}
				}
				
				if(rets.size() == 0) {
					for(String pinyin : pinyins) {
						StringBuilder sb = new StringBuilder();
						sb.append(pinyin);
						temp.add(sb.toString());
					}
				}
			} else {
				if(rets.size() == 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(word);
					temp.add(sb.toString());
				} else {
					for(String ret : rets) {
						StringBuilder sb = new StringBuilder();
						sb.append(ret);
						sb.append(word);
						temp.add(sb.toString());
					}
				}
			}
			
			rets.clear();
			rets.addAll(temp);
		}
		
		return rets;
	}
	
	/**
	 * 输出一个词可能的拼音组成.
	 * 
	 * @param words
	 * @return
	 */
	public Map<String, List<QRewritePinyinModel>> transformByAC(String words) {
		List<String> rets = new ArrayList<String>();
		Map<String, List<QRewritePinyinModel>> yingsheMap = new HashMap<String, List<QRewritePinyinModel>>();
		for(int i = 0; i < words.length(); i++) {
			char ch = words.charAt(i);
			String word = String.valueOf(words.charAt(i));
			List<String> temp = new ArrayList<String>();
			if(QRewriteHelper.isChinese(ch)) {
				if(!map.containsKey(word))
					continue;
				
				List<String> pinyins = map.get(word);
				for(String ret : rets) {
					Map<String, List<QRewritePinyinModel>> tempMap = new HashMap<String, List<QRewritePinyinModel>>();
					for(String pinyin : pinyins) {
						StringBuilder sb = new StringBuilder();
						sb.append(ret);
						sb.append(pinyin);
						temp.add(sb.toString());
						
						QRewritePinyinModel pinyinModel = new QRewritePinyinModel();
						pinyinModel.setStart(ret.length());
						pinyinModel.setEnd((ret.length()+pinyin.length()-1));
						pinyinModel.setPinyin(pinyin);
						pinyinModel.setWord(word);
						
						List<QRewritePinyinModel> list = yingsheMap.get(ret);
						List<QRewritePinyinModel> result = new ArrayList<QRewritePinyinModel>();
						result.addAll(list);
						result.add(pinyinModel);
						tempMap.put(sb.toString(), result);
					}
					yingsheMap.remove(ret);
					yingsheMap.putAll(tempMap);
				}
				
				if(rets.size() == 0) {
					for(String pinyin : pinyins) {
						StringBuilder sb = new StringBuilder();
						sb.append(pinyin);
						temp.add(sb.toString());
						
						QRewritePinyinModel pinyinModel = new QRewritePinyinModel();
						pinyinModel.setStart(0);
						pinyinModel.setEnd((pinyin.length()-1));
						pinyinModel.setPinyin(pinyin);
						pinyinModel.setWord(word);
						List<QRewritePinyinModel> list = new ArrayList<QRewritePinyinModel>();
						list.add(pinyinModel);
						yingsheMap.put(pinyin, list);
					}
				}
			} else {
				if(rets.size() == 0) {
					StringBuilder sb = new StringBuilder();
					sb.append(word);
					temp.add(sb.toString());
					QRewritePinyinModel pinyinModel = new QRewritePinyinModel();
					pinyinModel.setStart(0);
					pinyinModel.setEnd(word.length()-1);
					pinyinModel.setPinyin(word);
					pinyinModel.setWord(word);
					List<QRewritePinyinModel> list = new ArrayList<QRewritePinyinModel>();
					list.add(pinyinModel);
					yingsheMap.put(word, list);
				} else {
					for(String ret : rets) {
						StringBuilder sb = new StringBuilder();
						sb.append(ret);
						sb.append(word);
						temp.add(sb.toString());
						QRewritePinyinModel pinyinModel = new QRewritePinyinModel();
						pinyinModel.setStart(ret.length());
						pinyinModel.setEnd(ret.length()+word.length()-1);
						pinyinModel.setPinyin(word);
						pinyinModel.setWord(word);
						
						List<QRewritePinyinModel> list = yingsheMap.get(ret);
						List<QRewritePinyinModel> result = new ArrayList<QRewritePinyinModel>();
						result.addAll(list);
						result.add(pinyinModel);
						Map<String, List<QRewritePinyinModel>> tempMap = new HashMap<String, List<QRewritePinyinModel>>();
						tempMap.put(sb.toString(), result);
						yingsheMap.remove(ret);
						yingsheMap.putAll(tempMap);
					}
				}
			}
			
			rets.clear();
			rets.addAll(temp);
		}
		
		return yingsheMap;
	}
	
	/**
	 * 使用AC自动机结合双数组树,匹配拼音字串的方式进行错别字纠正及拼音转中文功能
	 * 	这里如果query内中文字符的长度超过20则不进行错别字纠正
	 * 
	 * @param query 输入字串
	 * @return
	 */
	public String getPinyinCorrectionByAC(String query) {
		List<String> ret = QRewriteHelper.extraChinese(query);
		if(ret.size() > QRewriteConst.LIMIT_MAX_PINYIN_TRANS || 
				(query.length() < QRewriteConst.LIMIT_MIN_PINYIN_TRANS))
			return query;
		
		List<Term> terms = ToAnalysis.parse(query).getTerms();
		// 原句子中的词集合(这些词不可完整修改)
		Map<String, Integer> noRewriteWordsMap = new HashMap<String, Integer>();
		for(Term term : terms) {
			noRewriteWordsMap.put(term.getRealName(), 1);
		}
		
		Map<String, List<QRewritePinyinModel>> pinyins = transformByAC(query);
		if(query != null && !query.isEmpty()) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			for(Map.Entry<String, List<QRewritePinyinModel>> pinyin : pinyins.entrySet()) {
				List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> wordList = 
						validateASimpleAhoCorasickDoubleArrayTrie(acdat, pinyin.getKey());
				
				// 找出最长匹配词,其余不做计算
				// 取匹配长度最长的词作为有效词
				List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> effectiveWords = 
						new ArrayList<AhoCorasickDoubleArrayTrie<String>.Hit<String>>();
				boolean stopFlag = true;
				while(stopFlag) {
					AhoCorasickDoubleArrayTrie<String>.Hit<String> effectWord = 
							findEffectWords(wordList, effectiveWords);
					if(effectWord == null)
						stopFlag = false;
					else
						effectiveWords.add(effectWord);
				}
				
				// 将有效词的原词替换为有效词
				for(AhoCorasickDoubleArrayTrie<String>.Hit<String> effectWord : effectiveWords) {
					int begin = effectWord.begin;
					int end = effectWord.end;
					String value = effectWord.value;
					List<String> temp = pinyinWordMap.get(value);
					
					boolean flag = false;
					StringBuilder sb = new StringBuilder();
					for(QRewritePinyinModel model : pinyin.getValue()) {
						if(model.getStart() == begin && model.getEnd() < end) {
							sb.append(model.getWord());
							flag = true;
						} else if(flag && model.getEnd() < (end-1)) {
							sb.append(model.getWord());
						} else if(flag && model.getEnd() == (end-1)) {
							sb.append(model.getWord());
							break;
						} else {
							sb = new StringBuilder();
						}
					}
					String originalWord = sb.toString();
					
					if(sb.length() == 0)
						continue;
					
					// 若替换的原文本为一个完整的词时,不作替换(此处定义不可改写可分为一个完整词的文本)
					if(QRewriteHelper.extraChinese(originalWord).size() == 0) {
						String replaceStr = temp.get(0);
						query = query.replace(originalWord, replaceStr);
						
						if(map.containsKey(query)) {
							map.put(query, map.get(query)+1);
						} else {
							map.put(query, 1);
						}
					} else if(!correctionMap.containsKey(originalWord)
							&& !noRewriteWordsMap.containsKey(originalWord)) {
						boolean replaceFlag = false;
						// 当被替换部分为原句子中多个完整的词(或者字)组合时可以替换
						int cnt = 0;
						for(Map.Entry<String, Integer> entry : noRewriteWordsMap.entrySet()) {
							if(originalWord.contains(entry.getKey()))
								cnt++;
						}
						if(cnt >= 2)
							replaceFlag = true;
						
						if(replaceFlag) {
							String replaceStr = temp.get(0);
							query = query.replace(originalWord, replaceStr);
							
							if(map.containsKey(query)) {
								map.put(query, map.get(query)+1);
							} else {
								map.put(query, 1);
							}
						}
					}
				}
			}
			
			try {
				if(map.size() != 0) {
					List<Map.Entry<String, Integer>> list = SortUtil.sortIntValueDesc(map);
					query = list.get(0).getKey();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return query;
	}
	
	/**
	 * 找到队列中长度最长的词.
	 * 
	 * @param wordList
	 * @return
	 */
	public AhoCorasickDoubleArrayTrie<String>.Hit<String> findEffectWords(
			List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> wordList, 
			List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> effectWords) {
		int sum = 0;
		AhoCorasickDoubleArrayTrie<String>.Hit<String> effectWord = null;
		for(AhoCorasickDoubleArrayTrie<String>.Hit<String> word : wordList) {
			int begin = word.begin;
			int end = word.end;
			// 将位置与有效词有重叠部分的词都过滤
			boolean flag = true;
			for(AhoCorasickDoubleArrayTrie<String>.Hit<String> eff : effectWords) {
				if((begin >= eff.begin && begin < eff.end) || (end > eff.begin && end < eff.end)) {
					flag = false;
					break;
				}
			}
			if(flag) {
				int length = end-begin;
				if(length > sum) {
					sum = length;
					effectWord = word;
				}
			}
		}
		return effectWord;
	}
	
	/**
	 * 获取构建自动机的数据.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String[] getdata() throws Exception {
		List<String> list = new ArrayList<String>();
		for(Map.Entry<String, List<String>> di : wordPinyinMap.entrySet()) {
			list.add(di.getKey());
		}
    	
    	String[] datas = new String[list.size()];
    	int i = 0;
    	for(String str : list) {
    		datas[i] = str;
    		i++;
    	}
    	
    	return datas;
	}
	
	private AhoCorasickDoubleArrayTrie<String> buildASimpleAhoCorasickDoubleArrayTrie(String[] keyArray) {
		// Collect test data set
		TreeMap<String, String> map = new TreeMap<String, String>();
		for (String key : keyArray) {
			map.put(key, key);
		}
		// Build an AhoCorasickDoubleArrayTrie
		AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
		acdat.build(map);
		return acdat;
	}
	
	private List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> validateASimpleAhoCorasickDoubleArrayTrie(
			AhoCorasickDoubleArrayTrie<String> acdat, String text) {
		List<AhoCorasickDoubleArrayTrie<String>.Hit<String>> wordList = acdat
				.parseText(text);
		
		return wordList;
	}
	
	public static void main(String[] args) throws Exception {
		QRewriteByPinyin process = new QRewriteByPinyin();
//		long start = System.currentTimeMillis();
		String query = "萎病";
		query = "臀部疼";
//		query = "腰退骨折";
		long start = System.currentTimeMillis();
		String[] sentences = query.split("[；。！？;.!?\\s]");
		for(String sentence : sentences) {
			String word = process.getPinyinCorrectionByAC(sentence);
			System.out.println("query:" + sentence);
			System.out.println("result:" + word);
		}
		
		long end = System.currentTimeMillis();
		System.out.println("time:" + (end-start));
//		List<String> ret = process.transform("上呼吸道感染");
//		for(String str : ret) {
//			System.out.println(str);
//		}
	}
	
}
