/**
 * 
 */
package org.base.framework.jms;

import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 *
 */
@Data
public class MessageFromDotNet extends JmsMessage {
	
   private String testString;

   private String buffer;
   
   private String reply;
   
   private List<MessageFromDotNetElement> list;
   


}
