package com.zzs.zhous.node.key;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class KeyMap<T> {

    /**
     *
     */
    public KeyMap() {
        map = new HashMap<String, T>();
    }


    private Map<String, T> map;


//	public KeyFile getKeyFile(String userId,String appId){
//		String key=userId+"_"+appId;
//		return getKeyFile(key);
//	}

    public T get(String key) {
        if (map.containsKey(key))
            return map.get(key);
        else
            return null;
    }

    //	public void addKeyFile(String userId,String appId,KeyFile keyFile){
//		String key=userId+"_"+appId;
//		addKeyFile(key,keyFile);
//	}
    public void add(String key, T t) {
        map.put(key, t);
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("\n----keyMap id=" + super.toString() + " dump------ \n");
        for (String key : map.keySet()) {
            T t = map.get(key);
            buf.append("\n").append(key).append("--").append(t.toString());
        }

        return buf.toString();
    }
}
