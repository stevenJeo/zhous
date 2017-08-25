/**
 * 
 */
package com.zzs.zhous.node.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Administrator
 *
 */
public class KeyDES extends Key {
	private final static Logger logger = LoggerFactory.getLogger(KeyDES.class);
	
	private String w_k;
	private String w_time;
	private String w_key;
	
	private String b_k;
	private String b_time;
	private String b_key;

	private String userId;
	private String appId;
	private String session;
	private int mode;
	
	private List<String> desList=new ArrayList<String>();
	
	
	
	public void appendSynchronizeString(StringBuffer sb){
		sb.append(userId).append(Key.separator)
		  .append(appId).append(Key.separator)
		  .append(session).append(Key.separator);
		if(b_k!=null)sb.append(b_k).append(Key.separator).append(b_key).append(Key.separator);
		sb.append(w_k).append(Key.separator).append(w_key);
	}
	
	
	
//	private String createKey(){
//		String k=userId+Key.separator+appId+Key.separator+AES+Key.separator+time;
//		return k;
//	}
	
	/**
	 * Key.timeToUpdate,默认600秒交换密钥
	 * 太短会出现,服务端已经更新密钥,但是客户端.....
	 * @return
	 */
	public boolean isTimeToUpdate(){
		return isTimeToUpdate(w_time);
	}
	
	public boolean isTimeToUpdate(String keyId) {
		if(w_time.equals(keyId)) {
			long wt=Long.parseLong(w_time);
			long ct=System.currentTimeMillis();
			if(ct-wt>Key.timeToUpdate) 
				return true;
			else
				return false;
			
		}else if(b_time.equals(keyId)) {
			long ct=System.currentTimeMillis();
			long bt=Long.parseLong(b_time);
			if(ct-bt>Key.timeToUpdate) 
				return true;
			else
				return false;
			
		}else {
			/*
			 * 请求keyId与w_k和b_k均不一致，默认更新密钥。(可能是客户端实例密钥过期太久)
			 * modify by zhsh-zhou@2015-10-29
			 */
			logger.warn("keyId!=(w_time||b_time). keyId isTimeToUpdate=true! keyId={}, w_time={}, b_time={}",keyId,w_time,b_time);
			return true;
		}
	}
	
	
	/**
	 * 
	 */
	public KeyDES(String userId, String appId, String keyString, String session, int mode, String time) {
		super.setType(Key.Type.DES);
		
		if(isDebug())desList.add(keyString+"-"+time); //test only
		
		w_k=keyString;
		w_time=time;
		
		this.session=session;
		this.userId=userId;
		this.appId=appId;
		this.mode=mode;
		
		w_key=time;//userId+Key.separator+appId+Key.separator+AES+Key.separator+time;
		//userId+Key.separator+appId+Key.separator+session+Key.separator+time;


	}
	
	
	public static String createSession(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String createDesKey(){
		String k=UUID.randomUUID().toString().split("-")[0];
		return k;
	}
	
	////TODO 待优化
	public String register(){
		String reply=userId+Key.separator+appId+Key.separator+w_k+Key.separator
		+session+Key.separator+w_time+Key.separator+System.currentTimeMillis();
		return reply;
	}
	
	public String getSession(){
		return this.session;
	}
	
	
	
	public String getDesKey(String fullKeyId){
		if(w_key!=null && w_key.equals(fullKeyId))
			return w_k;
		else if(b_key!=null && b_key.equals(fullKeyId))
			return b_k;
		
		logger.error("getDesKey=null fullKeyId="+fullKeyId+" w_key="+w_key+" b_key="+b_key+" w_k="+w_k+" b_k="+b_k);
		return null;
	}
	
	private final static String DES="DES";
	//private final static String sparator="_";
	public static String getMapKey(String userId,String appId,int mode){
		//return userId+KeyProperties.separator+appId+KeyProperties.separator+session+KeyProperties.separator+mode;
		return userId+Key.separator+appId+Key.separator+DES+Key.separator+mode;

	}
	
	public boolean isEncryptMode(){
		if(mode==Cipher.ENCRYPT_MODE) 
			return true;
		else
			return false;
		
	}
	
	
	
	private String mapKey;
	public String getMapKey(){
		if(mapKey!=null)return mapKey;
		//mapKey=userId+KeyProperties.separator+appId+KeyProperties.separator+session+KeyProperties.separator+mode;
		mapKey=getMapKey(userId, appId, mode);//userId+Key.separator+appId+Key.separator+AES+Key.separator+mode;
		return mapKey;
	}
	
//	public String getCipherKey(String userId,String appId,String session,String time){
//		if(wkey!=null) return key;
//		
//	}
	
	
	public boolean add(String keyString,String time){
		if(w_time.compareToIgnoreCase(time)<0){
			
			if(isDebug())desList.add(keyString); //test only 
	
			b_k=w_k;
			b_time=w_time;
			b_key=w_key;
			
			w_k=keyString;
			w_time=time;
			w_key=time;//this.getMapKey()+Key.separator+time;
		
		}else{
			logger.error("server site has changed System time "+w_time+">"+time);
		}
		return true;
	}
	
	public boolean contains(String key){
		if((w_key!=null && w_key.equals(key)) 
				||(b_key!=null && b_key.equals(key)))
				return true;
		else
			return false;
	}
	
	public String getKeyId(boolean isWorkKey){
		if(isWorkKey)
			return w_time;
		else if(b_time!=null)
			return b_time;
		else
			return w_time;
	}
	
	
	public String toString(){
		if(!isDebug())return super.toString();
		
		StringBuffer buf=new StringBuffer();
		buf.append("\nid="+super.toString()).append(" userId="+userId+" appId="+appId+" session="+session)
		  .append("\nwork   w_k="+w_k+" Key="+w_key+" Id="+w_time)
		  .append("\nbackup b_k="+b_k+" Key="+b_key+" Id="+b_time).append("\n");

		
		for(String des:desList) buf.append(" ").append(des);
		
		return buf.toString();
	}
	
	
}
