package org.base.framework.jms;

import java.util.List;

public class CC extends JmsMessage{
	
	private String name;
	
	private List<String> s;
	
	public List<String> getS() {
		return s;
	}

	public void setS(List<String> s) {
		this.s = s;
	}

	public CC(){}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
