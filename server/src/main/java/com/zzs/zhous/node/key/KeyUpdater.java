/**
 * 
 */
package com.zzs.zhous.node.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class KeyUpdater implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(KeyUpdater.class);

	private Thread thread;
	private boolean stopFlag=false;
	
	
	/**
	 * 
	 */
//	public RsaKeyUpdater(String absolutePath,int updateInterval,KeyMap<KeyFile> keyMap) {
	public KeyUpdater() {}
	public KeyUpdater(KeyUpdaterExceutor keyUpdateExceutor) {
		this.keyUpdateExceutor=keyUpdateExceutor;
	}

	
	public void start() throws Exception{
		if(thread==null){
			synchronized(this){
				if(thread==null){
					thread=new Thread(this);
					stopFlag=false;
					
					
					thread.start();
				}
			}
		}
	}
	
	public void stop(){
		stopFlag=true;
	}

	
	private KeyUpdaterExceutor keyUpdateExceutor;
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
//		Object[] logObjectArray=new Object[]{keyUpdateExceutor.getClass().getName(),thread.getName()};
//		Map<String,Object> args=new HashMap<String,Object>();
//		while(!stopFlag){
//			try{
//				log.warn("{} thread= {} is running...",logObjectArray);
//				keyUpdateExceutor.run(args);
//				
//			}catch(Exception e){
//				log.error("RsaKeyUpdater thread error",e);
//			}
//		}
//		log.warn("{} thread= {} has been terminated",logObjectArray);

		Map<String,Object> args=new HashMap<String,Object>();
		keyUpdateExceutor.run(args);
		
	}

}
