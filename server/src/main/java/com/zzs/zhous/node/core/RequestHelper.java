/**
 * 
 */
package com.zzs.zhous.node.core;



/**
 * @author Administrator
 *
 */
public interface RequestHelper {

	public Request toRequest(String messageJsonString, Class clazz) throws Exception;

	public String toJson(Object request) throws Exception;

	public boolean register();

	public String sendRequest(String cmd, String version, String requestJsonString) throws Exception;
	public String sendRequest(Request request) throws Exception;
	
}
