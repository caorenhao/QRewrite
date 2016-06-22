package com.caorenhao.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 空设备，用来重定向 第三方的输入流到空设备，避免内存堵塞
 * @author vernkin
 *
 */
public final class NullDev extends Thread {

	private static NullDev instance = new NullDev();
	
	/**
	 * 重定向一个InputStream 到 null设备
	 * @param is
	 */
	public static void redirectInputStream(InputStream is) {
		instance.addInputStream(is, null);
	}
	
	/**
	 * 重定向一个InputStream 到 文件输出
	 * @param is
	 */
	public static void redirectInputStream(InputStream is, File outFile) {
		instance.addInputStream(is, outFile);
	}	
	
	private class StreamEntry {
		public InputStream inputStream;
		public OutputStream outputStream;	
		
		public StreamEntry(InputStream inputStream, File outFile) {
			if(outFile != null) {
				try {
					outputStream = new FileOutputStream(outFile, true);
				} catch(Throwable t) {
					outputStream = null;
				}
			}
			this.inputStream = inputStream;
		}
		
		public void close() {
			IOUtil.forceClose(inputStream);
			IOUtil.forceClose(outputStream);
		}
	}
	
	private List<StreamEntry> streams = new LinkedList<StreamEntry>();
	private byte[] buffer = new byte[512];
	
	private NullDev() {
		super.setName("NullDevThread");
		super.setPriority(Thread.NORM_PRIORITY);
		super.setDaemon(true);
		super.start();
	}
	
	public void addInputStream(InputStream is, File outFile) {
		synchronized(streams) {
			streams.add(new StreamEntry(is, outFile));
		}
	}

	@Override
	public void run() {
		while(true) {
			NetUtil.sleep(100);
			synchronized(streams) {
				Iterator<StreamEntry> itr = streams.iterator();
				while(itr.hasNext()) {
					try {
						StreamEntry se = itr.next();
						InputStream is = se.inputStream;
						try {
							if(is.available() <= 0) {
								continue;
							}
						} catch (IOException ioe) {
							se.close();
							itr.remove();
							continue;
						}
						
						int readLen = is.read(buffer);
						//System.out.println("NullDev readlen " + readLen);
						if(readLen == -1)
							itr.remove();
						if(readLen > 0 && se.outputStream != null) {
							se.outputStream.write(buffer, 0, readLen);
							se.outputStream.flush();
						}
					} catch(Throwable t){
						t.printStackTrace();
					}
				}
			}
		}
	}
}
