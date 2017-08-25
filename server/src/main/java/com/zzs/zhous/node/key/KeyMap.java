/**
 * 
 */
package com.zzs.zhous.node.key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public class KeyMap<T extends Key> {

	/**
	 * 
	 */
	public KeyMap() {
		map=new HashMap<String,T>();
	}

	
	private Map<String,T> map;
	
	
//	public KeyFile getKeyFile(String userId,String appId){
//		String key=userId+"_"+appId;
//		return getKeyFile(key);
//	}
	
	public T get(String key){
		if(map.containsKey(key))
			return map.get(key);
		else
			return null;
	}
	
//	public void addKeyFile(String userId,String appId,KeyFile keyFile){
//		String key=userId+"_"+appId;
//		addKeyFile(key,keyFile);
//	}
	public void add(String key,T t){
		map.put(key, t);
	}
	
	/**
	 * 获取keyMap中所有的DES
	 * 
	 * @version 2016-01-05
	 * @author zhsh-zhou
	 * 
	 */
	public List<KeyDES> getAllDES(){
		if(null!=map){
			List<KeyDES> keys = new ArrayList<KeyDES>();
			for(Map.Entry<String, T> entry : map.entrySet()){
				T t = entry.getValue();
				if(t instanceof KeyDES){
					keys.add((KeyDES)t);
				}
			}
			return keys;
		}else
			return null;
	}
	/**
	 * 获取keyMap
	 * 
	 * @version 2016-01-05
	 * @author zhsh-zhou
	 * 
	 */
	public Map<String,T> getKeyMap(){
		return map;
	}
	


	public String toString(){
		StringBuffer buf=new StringBuffer();
		buf.append("\n----keyMap id="+super.toString()+" dump------ \n");
		for(String key:map.keySet()){
			T t=map.get(key);
			buf.append("\n").append(key).append("--").append(t.toString());
		}
		
		return buf.toString();
	}

	

}
