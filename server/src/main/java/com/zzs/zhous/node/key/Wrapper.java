/**
 * 
 */
package com.zzs.zhous.node.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Administrator
 *
 */
public class Wrapper<T> {
	
	private final static Logger logger = LoggerFactory.getLogger(Wrapper.class);
	/**
	 * 
	 */
	public Wrapper(T t, boolean isBusy) {
		this.t=t;
		busy=new AtomicBoolean(isBusy);
	}
	
	//private boolean busy=false;
	private int count=0;
	private String comment;
	//private String[] log=new String[10];
	
	private T t;

	private final AtomicBoolean busy;
	
	

	/**
	 * @return the busy
	 */
	public boolean isBusy() {
		return busy.get();
	}

//	/**
//	 * @param busy the busy to set
//	 */
//	public void setBusy(boolean busy) {
//		
//		
//		this.busy = busy;
//	}

	
	public boolean compareAndSet(boolean expect,boolean update){
		return busy.compareAndSet(expect, update);
	}
	
	
	
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

//	/**
//	 * @param count the count to set
//	 */
//	public void addCount() {
//		this.count++;
//	}

	/**
	 * @return the t
	 */
	public T getT() {
		count++;
		return t;
	}

	
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * 释放wrap对象。
	 * 同时计数被使用的次数。
	 * @param wrap
	 */
	public void free(){
		while(!busy.compareAndSet(true, false))
			logger.error(" multiThread set wrapper free ");
	}
	
}
