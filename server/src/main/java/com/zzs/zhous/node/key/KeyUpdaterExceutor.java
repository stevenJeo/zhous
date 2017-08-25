/**
 * 
 */
package com.zzs.zhous.node.key;

import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface KeyUpdaterExceutor {
	public Object run(Map<String, Object> args);
	
	public void stop();
	
}
