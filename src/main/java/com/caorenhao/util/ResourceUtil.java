package com.caorenhao.util;

import java.io.File;

/**
 * 资源路径辅助类
 * @author Vernkin
 *
 */
public class ResourceUtil {
	
	public static final String DICL_ROOT_KEY = "MedRec";

	private static File rootDir;
	
	private static File dataDir;
	
	/**
	 * 获取root目录，一般位于data目录父类
	 * @return
	 */
	public static File getRootDir() {
		if(rootDir != null)
			return rootDir;
		String envVar = System.getenv(DICL_ROOT_KEY);
		if(envVar != null) {
			rootDir = new File(envVar);
		} else if(OSUtil.getOSType().isWindows()) {
			rootDir = new File(".");		
		} else {
			rootDir = new File("/home/dicl/iyq/iyuqing");
		}
		return rootDir;
	}
	
	public static File getDataDir() {
		if(dataDir != null)
			return dataDir;
		dataDir = new File(getRootDir(), "data");
		return dataDir;
	}
}
