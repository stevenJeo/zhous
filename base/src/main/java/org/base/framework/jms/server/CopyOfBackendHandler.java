package org.base.framework.jms.server;

import org.base.framework.chain.Handler;
import org.base.framework.chain.HandlerChain;
import org.base.framework.context.SpringContextHelper;
import org.base.framework.jms.JmsHandlerChain;
import org.base.framework.jms.JmsMessage;
import org.base.framework.jms.JmsTaskThread;
import org.base.framework.task.TaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * 使用spring提供的jmsTemplate.receive方式读取请求,
 * 在高并发访问下,经常出现超过1秒才能读取到请求的情形,
 * <p>
 * 尝试通过自行管理con,session,consumer的方式
 * <p>
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class CopyOfBackendHandler implements Handler {
    private final Logger logger = LoggerFactory.getLogger(CopyOfBackendHandler.class);

    public static boolean doesPerformanceTrack = true;
    public static long timeThreshold = 1000;
    public final String doesPerformanceTrackKey = "doesPerformanceTrack";

    private long timeout = 1000;
    private JmsTemplate jmsTemplate;
    private Destination destination;
    private int taskCount;


    public void setTimeout(long timeout) {
        this.timeout = timeout;
        //jmsTemplate.setTimeToLive(timeout);
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }


    private boolean stopFlag = false;


    /**
     *
     */
    public CopyOfBackendHandler() {
    }


    public void exceute(Map args) throws Exception {
        //jmsTemplate.resetReceiveTimeout(timeout);
        if (logger.isInfoEnabled()) logger.info("Start backendHandler...");
        long t00 = 0, t0 = 0, t1 = 0, t2 = 0, t3 = 0;
        while (!stopFlag) {
            try {
                if (logger.isDebugEnabled()) logger.debug("Start receive message");
                if (doesPerformanceTrack) t00 = System.currentTimeMillis();
                Message message = jmsTemplate.receive(destination);
                if (message != null && message instanceof TextMessage) {
                    TextMessage tmsg = (TextMessage) message;
                    if (doesPerformanceTrack) {
                        t1 = System.currentTimeMillis();
                        t0 = tmsg.getLongProperty(doesPerformanceTrackKey);
                        if (t1 - t0 > timeThreshold)
                            logger.warn("Q deliver delay=" + (t0 - t1) + " jmsTemplate.receive=" + (t1 - t00));
                    }
                    if (logger.isDebugEnabled()) logger.debug(" Message content : " + tmsg.getText());

                    HandlerChain taskHandlerChain = (HandlerChain) SpringContextHelper.getBean(tmsg.getJMSType());
                    if (taskHandlerChain == null) {
                        logger.error("cannot find chain for " + tmsg.getJMSType());
                    } else {
                        while (true) {
                            JmsTaskThread taskThread = (JmsTaskThread) TaskThreadPool.getInstance().getTaskThread();
                            if (taskThread != null) {
                                if (doesPerformanceTrack) t2 = System.currentTimeMillis();
                                Map<String, Object> taskArgs = taskThread.getArgs();
                                taskArgs.clear();
                                taskArgs.put(JmsHandlerChain.KeyJmsHandlerChain, taskHandlerChain);
                                taskArgs.put(JmsHandlerChain.KeyJmsMessageString, tmsg.getText());
                                if (!JmsMessage.NullReplyId.equals(tmsg.getJMSCorrelationID()))
                                    taskArgs.put(JmsHandlerChain.KeyJmsReplyId, tmsg.getJMSCorrelationID());
                                taskThread.addTask(taskHandlerChain);
                                if (doesPerformanceTrack) {
                                    t3 = System.currentTimeMillis();
                                    if (t3 - t1 > timeThreshold)
                                        logger.warn("BackendHandler slow total=" + (t3 - t1) + " addTask=" + (t3 - t2) + " getIdleThread=" + (t2 - t1));
                                }
                                break;
                            } else {
                                Thread.currentThread().sleep(100);
                                if (logger.isDebugEnabled()) logger.debug(" cann't find free taskThread sleep 100!");
                            }
                        }
                    }
                } else if (message != null)
                    logger.error("uknown message received " + message.getJMSCorrelationID());
            } catch (Exception e) {
                logger.error("backendHandler", e);
            }
        }
    }


    public void stop() {

        stopFlag = false;
    }

/*	private HandlerChain getTaskHandlerChain(JmsMessage message){
        String key=null;
		HandlerChain chain=null;
		if(message.getVersion()!=null && !(message.getVersion().isEmpty())
			&& message.getService()!=null && !(message.getService().isEmpty())){
			key=message.getVersion()+"_"+message.getService()+"_"+message.getApp()+"_chain";
		}
		chain=(HandlerChain)SpringContextHelper.getBean(key);
		if(chain!=null) return chain;

		if(message.getService()!=null && !(message.getService().isEmpty())){
			key=message.getService()+"_"+message.getApp()+"_chain";
		}
		chain=(HandlerChain)SpringContextHelper.getBean(key);
		if(chain!=null) return chain;

		key=message.getApp()+"_chain";
		chain=(HandlerChain)SpringContextHelper.getBean(key);

		return chain;

	}*/


}
