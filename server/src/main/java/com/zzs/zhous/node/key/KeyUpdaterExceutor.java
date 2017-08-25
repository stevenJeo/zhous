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
	Object run(Map<String, Object> args);
	
	void stop();
	
}
