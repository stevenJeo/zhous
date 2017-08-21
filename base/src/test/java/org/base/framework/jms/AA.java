/**
 * 
 */
package org.base.framework.jms;

import java.util.List;

/**
 * @author Administrator
 *
 */
public class AA<T> { 
	
	private String name;
	
	private List<T> list;

	public AA(){}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list =list;
	}
	
	
	
	
}
