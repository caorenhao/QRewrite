package com.caorenhao.util;

/**
 * 资源生命包装类
 * @author vernkin
 *
 */
public class LocalResLifeWrapper<T extends LocalResLifeItf> {

	/** 默认资源过期周期（单位是秒），为十分钟 */
	public static final long DEF_RES_EXPIRED_PERIOD = 10 * 60;
	
	/** 是否已经获取 */
	private boolean isAcquired = false;
	
	/** 资源过期周期(单位是秒) */
	private long expiredPeriod = 0L;
	
	/** 上一次激活的时间戳（单位是秒） */
	private long lastActiveTime = 0;
	
	private T res;
	
	/**
	 * 完整的构造函数
	 * @param res 代表资源的对象
	 * @param isInitacquired 是否初始化（构造）时已经获取资源
	 * @param expiredPeriod 过期的周期，单位是秒
	 */
	public LocalResLifeWrapper(T res, boolean isInitacquired, long expiredPeriod) {
		this.res = res;
		this.isAcquired = isInitacquired;
		this.expiredPeriod = expiredPeriod;
	}
	
	/**
	 * 默认过期时间是 {@link #DEF_RES_EXPIRED_PERIOD}
	 * @param res 代表资源的对象
	 * @param isInitacquired 是否初始化（构造）时已经获取资源
	 */
	public LocalResLifeWrapper(T res, boolean isInitacquired) {
		this(res, isInitacquired, DEF_RES_EXPIRED_PERIOD);
	}
	
	/**
	 * 默认过期时间是 {@link #DEF_RES_EXPIRED_PERIOD}, 默认已经获取资源
	 * @param res 代表资源的对象
	 */
	public LocalResLifeWrapper(T res) {
		this(res, true);
	}
	
	/**
	 * 获取没有过期的资源
	 * @return
	 * @throws Exception
	 */
	public synchronized T getRes() throws Exception {
		// 没有获取资源或者资源已经过期时候重新获取
		if(isResExpired()) {
			if(isAcquired) {
				res.releaseRes();
			}
			res.acquireRes();
			isAcquired = true;
		}
		this.lastActiveTime = NetUtil.getTimeInSecs();
		return res;
	}
	
	/**
	 * 释放资源
	 * @throws Exception
	 */
	public synchronized void releaseRes() throws Exception {
		if(isAcquired) {
			res.releaseRes();
			isAcquired = false;
		}
	}
	
	/**
	 * 强制重新获取资源
	 * @return
	 * @throws Exception
	 */
	public synchronized T reAcquireRes() throws Exception {
		releaseRes();
		res.acquireRes();
		isAcquired = true;
		this.lastActiveTime = NetUtil.getTimeInSecs();
		return res;
	}
	
	public synchronized long getExpiredPeriod() {
		return expiredPeriod;
	}
	
	public synchronized void setExpiredPeriod(long expiredPeriod) {
		this.expiredPeriod = expiredPeriod;
	}
	
	/**
	 * 是否已经获取到资源
	 * @return
	 */
	public synchronized boolean isAcquired() {
		return isAcquired;
	}
	
	/**
	 * 资源是否存在，或者资源是否过期。 资源没有获取或则已经超过过期时间都是过期的
	 * 过期的资源会重新获取
	 * @return true表示需要重新获取资源
	 */
	public synchronized boolean isResExpired() {
		return !res.hasResource() || !isAcquired || (this.lastActiveTime + this.expiredPeriod < 
				NetUtil.getTimeInSecs());
	}
}
