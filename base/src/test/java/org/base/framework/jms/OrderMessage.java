/**
 * 
 */
package org.base.framework.jms;

import lombok.Data;


/**
 * @author ruby
 *
 */
@Data
public class OrderMessage extends JmsMessage {

	public OrderMessage() {
		super();
		super.setApp("testApp");
		super.setService("submitOrder");
		//super.setVersion("V1");
	}

	private long orderId;
	private String abc;



	
	
}
