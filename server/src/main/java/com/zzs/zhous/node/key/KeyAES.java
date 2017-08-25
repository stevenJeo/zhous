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
public class KeyAES extends Key {
	private final static Logger logger = LoggerFactory.getLogger(KeyAES.class);
	
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
	
	private List<String> aesList=new ArrayList<String>();
	
	
	
//	private String createKey(){
//		String k=userId+Key.separator+appId+Key.separator+AES+Key.separator+time;
//		return k;
//	}
	
	public boolean isTimeToUpdate(){
		
		
		
		long wt=Long.parseLong(w_time);
		long ct=System.currentTimeMillis();
		if(ct-wt>200000) 
			return true;
		else
			return false;
		
		
	}
	
	
	/**
	 * 
	 */
	public KeyAES(String userId, String appId, String keyString, String session, int mode, String time) {
		super.setType(Key.Type.AES);
		
		aesList.add(keyString); //test only
		
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
	
	public static String createAesKey(){
		//String[] tmp=UUID.randomUUID().toString().split("_");
		String k=UUID.randomUUID().toString().trim().replaceAll("-", "").substring(0,16);
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
	
	
	
	public String getAesKey(String fullKeyId){
		if(w_key!=null && w_key.equals(fullKeyId))
			return w_k;
		else if(b_key!=null && b_key.equals(fullKeyId))
			return b_k;
		
		return null;
	}
	
	private final static String AES="AES";
	//private final static String sparator="_";
	public static String getMapKey(String userId,String appId,int mode){
		//return userId+KeyProperties.separator+appId+KeyProperties.separator+session+KeyProperties.separator+mode;
		return userId+Key.separator+appId+Key.separator+AES+Key.separator+mode;

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
			
			aesList.add(keyString); //test only 
	
			b_k=w_k;
			b_time=w_time;
			b_key=w_key;
			
			w_k=keyString;
			w_time=time;
			w_key=time;//this.getMapKey()+Key.separator+time;
		
		}
		
		
//		synchronized(this){
////			if(this.session!=null && !this.session.equals(session))
////				return false; //session mismatch;
////			else if(session==null)
////				this.session=session; //error?
//			
//////TODO 执行add,session 不能为空,所以不应该检查session,或者把参数session删除?
//			
//			if(w_k==null){
//				w_k=keyString;
//				w_time=time;
//				w_key=time;//this.getMapKey()+Key.separator+time;
//				logger.error("w_k==null");
//			}else{
//				if(w_time.compareToIgnoreCase(time)<0){
//					if(b_k!=null)aesList.add(b_k); //test only 
//
//					b_k=w_k;
//					b_time=w_time;
//					b_key=w_key;
//					
//					w_k=keyString;
//					w_time=time;
//					w_key=time;//this.getMapKey()+Key.separator+time;
//					
//				}else if(w_time.compareToIgnoreCase(time)==0){
//					return true;
//				}else if(b_k!=null && b_time.compareToIgnoreCase(time)<0){
//					if(b_k!=null)aesList.add(b_k); //test only 
//					
//					b_k=keyString;
//					b_time=time;
//					b_key=time;//this.getMapKey()+Key.separator+time;
//					logger.error("b_k!=null && b_time.compareToIgnoreCase(time)<0");
//				}else 
//					//b_k!=null &&  b_time.compareToIgnoreCase(time)>=0
//					//this case cannot be replace;
//					return true;
//			}
//		}
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
		StringBuffer buf=new StringBuffer();
		buf.append("\nid="+super.toString()).append(" userId="+userId+" appId="+appId+" session="+session)
		  .append("\nwork   w_k="+w_k+" Key="+w_key+" Id="+w_time)
		  .append("\nbackup b_k="+b_k+" Key="+b_key+" Id="+b_time).append("\n");

		
		for(String aes:aesList) buf.append(" ").append(aes);
		
		return buf.toString();
	}
	
	
}
