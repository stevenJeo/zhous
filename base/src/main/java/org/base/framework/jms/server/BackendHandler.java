package org.base.framework.jms.server;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.base.framework.chain.Handler;
import org.base.framework.chain.HandlerChain;
import org.base.framework.context.SpringContextHelper;
import org.base.framework.jms.CoreService;
import org.base.framework.jms.JmsHandlerChain;
import org.base.framework.jms.JmsMessage;
import org.base.framework.jms.JmsTaskThread;
import org.base.framework.task.TaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Map;

/**
 * <p/>
 * 使用spring提供的jmsTemplate.receive方式读取请求,
 * 在高并发访问下,经常出现超过1秒才能读取到请求的情形,
 * <p/>
 * 尝试通过自行管理con,session,consumer的方式
 * <p>
 * Created by zhishuai.zhou on 2017/8/21.
 */
@Component
public class BackendHandler implements Handler {
    private final Logger logger = LoggerFactory.getLogger(BackendHandler.class);


    public static String cursorKey = "cursorKeyString";
    public static String cursorStepKey = "cursorStepKeyString";
    public static boolean doesPerformanceTrack = true;
    public static long timeThreshold = 1000;
    public static long maxErrorNum = 10;
    public final String doesPerformanceTrackKey = "doesPerformanceTrack";

    private long timeout = 1000;
    private String qname;
    private int sessionAckMode = Session.AUTO_ACKNOWLEDGE;

    private ActiveMQConnectionFactory connectionFactory;

    private int taskCount;

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * @param qname the qname to set
     */
    public void setQname(String qname) {
        this.qname = qname;
    }


    /**
     * @param connectionFactory the connectionFactory to set
     */
    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    /**
     * @param sessionAckMode the sessionAckMode to set
     */
    public void setSessionAckMode(int sessionAckMode) {

        if (sessionAckMode == Session.AUTO_ACKNOWLEDGE)
            this.sessionAckMode = Session.AUTO_ACKNOWLEDGE;
        else if (sessionAckMode == Session.CLIENT_ACKNOWLEDGE)
            this.sessionAckMode = Session.CLIENT_ACKNOWLEDGE;
        else if (sessionAckMode == Session.DUPS_OK_ACKNOWLEDGE)
            this.sessionAckMode = Session.DUPS_OK_ACKNOWLEDGE;
        else if (sessionAckMode == Session.SESSION_TRANSACTED)
            this.sessionAckMode = Session.SESSION_TRANSACTED;
        else {

            logger.error("unknow sessionAckMode, see Session for details.and current ackMode=Session.AUTO_ACKNOWLEDGE");

            this.sessionAckMode = Session.AUTO_ACKNOWLEDGE;
        }

    }


    private boolean stopFlag = false;

    //private ClientInternalExceptionListener clientInternalExceptionListener;
    //(TransportListener transportListener)


    /**
     *
     */
    public BackendHandler() {
    }


    /**
     * 按约定，请求方会传来可能的chainkey组合,而且按优先级存放，优先级高的放在前面。
     * 使用CoreService.uprightSlash分割。
     * <p>
     * 参考： org.owens.framework.core.jms.DefaultJmsService.getChainKey()
     */
    private HandlerChain getChain(String key) {
        HandlerChain taskHandlerChain = null;
        String[] ks = key.split(CoreService.uprightSlashRegexp);
        for (String k : ks) {
            taskHandlerChain = (HandlerChain) SpringContextHelper.getBean(k);
            if (taskHandlerChain != null) return taskHandlerChain;
        }

        return taskHandlerChain;

    }


    public void exceute(Map args) throws Exception {
        int cursor = 0;
        if (args != null && args.containsKey(cursorKey)) cursor = Integer.parseInt((String) args.get(cursorKey));
        int cursorStep = 1;
        if (args != null && args.containsKey(cursorStepKey))
            cursorStep = Integer.parseInt((String) args.get(cursorStepKey));
        int next = cursor;

        logger.warn("Start backendHandler...cursor={} step={}", cursor, cursorStep);
        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        Destination destination = null;
        int errCount = 0;
        while (!stopFlag) {
            try {
                //TODO 错误处理
                //if(consumer!=null) {consumer.close(); consumer=null;}
                //if(session!=null){session.close();session=null;}
                if (connection == null) {
                    connection = connectionFactory.createConnection();
                    connection.start();
                    //connection.setExceptionListener(this);
                    if (consumer != null) {
                        consumer.close();
                        consumer = null;
                    }
                    if (destination != null) destination = null;
                    if (session != null) {
                        session.close();
                        session = null;
                    }
                }
                if (session == null) {
                    session = connection.createSession(false, sessionAckMode);
                    if (destination != null) destination = null;
                    destination = session.createQueue(qname);
                    if (consumer != null) {
                        consumer.close();
                        consumer = null;
                    }
                    consumer = session.createConsumer(destination);
                }

                errCount = 0;
                long t00 = 0, t0 = 0, t1 = 0, t2 = 0, t3 = 0;
                while (!stopFlag) {
                    if (logger.isDebugEnabled()) logger.debug("Start receive message");
                    if (doesPerformanceTrack) t00 = System.currentTimeMillis();
                    Message message = consumer.receive(timeout);
                    if (message != null && message instanceof TextMessage) {
                        TextMessage tmsg = (TextMessage) message;
                        if (doesPerformanceTrack) {
                            t1 = System.currentTimeMillis();
                            t0 = tmsg.getLongProperty(doesPerformanceTrackKey);
                            if (t1 - t0 > timeThreshold)
                                logger.warn("Q deliver delay={} jmsTemplate.receive={}", (t0 - t1), (t1 - t00));
                        }
                        if (logger.isDebugEnabled())
                            logger.debug(" Message content:{},JMSCorrelationID ", tmsg.getText(), tmsg.getJMSCorrelationID());

                        //HandlerChain taskHandlerChain=(HandlerChain)SpringContextHelper.getBean(tmsg.getJMSType());
                        HandlerChain taskHandlerChain = getChain(tmsg.getJMSType());
                        if (taskHandlerChain == null) {
                            logger.error("cannot find chain for " + tmsg.getJMSType());
                        } else {
                            while (true) {
                                JmsTaskThread taskThread = (JmsTaskThread) TaskThreadPool.getInstance().getTaskThread(next, cursorStep);
                                if (taskThread != null) {
                                    next = taskThread.getId() + cursorStep;
                                    if (next >= TaskThreadPool.getInstance().getSize()) next = cursor;
                                    if (logger.isDebugEnabled())
                                        logger.debug(" next={},cursor={},cursorStep={},poolSize={}", next, cursor, cursorStep, TaskThreadPool.getInstance().getSize());

                                    if (doesPerformanceTrack) t2 = System.currentTimeMillis();
                                    Map<String, Object> taskArgs = taskThread.getArgs();
                                    taskArgs.clear();
                                    taskArgs.put(JmsHandlerChain.KeyJmsHandlerChain, taskHandlerChain);
                                    taskArgs.put(JmsHandlerChain.KeyJmsMessageString, tmsg.getText());
                                    if (!JmsMessage.NullReplyId.equals(tmsg.getJMSCorrelationID()))
                                        taskArgs.put(JmsHandlerChain.KeyJmsReplyId, tmsg.getJMSCorrelationID());
                                    taskThread.addTask(taskHandlerChain);//业务链处理
                                    if (doesPerformanceTrack) {
                                        t3 = System.currentTimeMillis();
                                        if (t3 - t1 > timeThreshold)
                                            logger.warn("BackendHandler slow total={} addTask={} getIdleThread={} send-assign={}", (t3 - t1), (t3 - t2), (t2 - t1), (t3 - t0));
                                    }
                                    break;
                                } else {
                                    logger.warn("cann't find free taskThread, try after 100ms! next={},cursor={},cursorStep={},poolSize={}"
                                            , next, cursor, cursorStep, TaskThreadPool.getInstance().getSize());
                                    Thread.currentThread().sleep(100);
                                }
                            }
                        }
                    } else if (message != null)
                        logger.error("uknown message received " + message.getJMSCorrelationID());
                    //else ==null nothing receive, try next
                }
                //error occour: connection? session? consumer?
            } catch (Exception e) {
                logger.error("backendHandler-", e);
            } finally {
                if (consumer != null) try {
                    consumer.close();
                } catch (Exception fe) {
                    logger.error("backendHandler-consumer-finally", fe);
                } finally {
                    consumer = null;
                }
                if (destination != null) try {
                    destination = null;
                } catch (Exception fe) {
                    logger.error("backendHandler-destination-finally", fe);
                }
                if (session != null) try {
                    session.close();
                } catch (Exception fe) {
                    logger.error("backendHandler-session-finally", fe);
                } finally {
                    session = null;
                }
                if (connection != null) try {
                    connection.close();
                } catch (Exception fe) {
                    logger.error("backendHandler-connection-finally", fe);
                } finally {
                    connection = null;
                }
                if (errCount < maxErrorNum)
                    errCount++;
                else
                    errCount = 1;
                logger.warn("backendHandler try to reconnect to QServer-- after {} seconds", errCount);
                Thread.currentThread().sleep(errCount * 1000);

            }
        }

        logger.warn("backendHandler-exit");


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
