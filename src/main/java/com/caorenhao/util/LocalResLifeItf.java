package com.caorenhao.util;

/**
 * 资源生命周期接口
 * @author vernkin
 *
 */
public interface LocalResLifeItf {

	/** 释放资源 */
	void releaseRes() throws Exception;
	
	/** 获取资源 */
	void acquireRes() throws Exception;
	
	/**
	 * 是否存在资源
	 * @return true表示存在资源
	 */
	boolean hasResource();
}
