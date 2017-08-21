/**
 * 
 */
package org.base.framework.jms;

import java.util.Date;

import org.base.framework.context.SpringContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author ruby
 *
 */
public class SendMessage implements Runnable {
	private final Logger log = LoggerFactory.getLogger(SendMessage.class);

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		String threadId=Thread.currentThread().toString()+"_";
		CoreService jmsCoreService = (JmsCoreService) SpringContextHelper.getBean("jmsCoreService");
		int i=0;
		long max_ts=0;
		double avg_ts=0;
		
		StringBuffer buf=new StringBuffer();
		for(int j=0;j<1024;j++)buf.append("1234567890");
		String bufString=buf.toString();
		
		while(i<1000000){
			try {
				OrderMessage message = new OrderMessage();
				message.setApp("appTest");
				message.setService("serviceTest");
				message.setOrderId(123);
//				message.setSubDate(new Date());
				String index = threadId + i+bufString;
				message.setAbc(index);
				i++;
				//message.setBody("body"+System.currentTimeMillis());
				String replyString = jmsCoreService.waitReply(message, 100);
				Date curDate=new Date();
				//if(log.isInfoEnabled())log.info(threadId+"-------replyString----i="+i+" "+replyString );
				OrderMessage reply = (OrderMessage) JmsMessageHelper.toJmsMessage(replyString, OrderMessage.class);
//				long ts=curDate.getTime()-reply.getSubDate().getTime();
//				Assert.isTrue(message.getSubDate().equals(reply.getSubDate()));
				Assert.isTrue(reply.getSign() != null);
				//Assert.isTrue(message.getAbc().endsWith(reply.getAbc()));
				
				
//				if(max_ts<ts)max_ts=ts;
//				avg_ts=avg_ts-(avg_ts-ts)/i;
				if(i%100==0){
					log.info(threadId+"----ok-----i="+i+" max="+max_ts+" avg="+avg_ts);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
