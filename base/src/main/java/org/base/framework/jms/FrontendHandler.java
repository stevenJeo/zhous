package org.base.framework.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.base.framework.chain.Handler;
import org.base.framework.chain.HandlerChain;
import org.base.framework.context.SpringContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class FrontendHandler implements Handler {

    private final Logger logger = LoggerFactory.getLogger(FrontendHandler.class);


    private long timeout = 1000;

    public static boolean doesPerformanceTrack = true;
    public static long timeThreshold = 1000;
    public static long maxErrorNum = 10;
    public final String doesPerformanceTrackKey = "doesPerformanceTrack";

    private String qname;

    private int sessionAckMode = Session.AUTO_ACKNOWLEDGE;

    private ActiveMQConnectionFactory connectionFactory;


    public void setTimeout(long timeout) {
        this.timeout = timeout;
        //jmsTemplate.setTimeToLive(timeout);
    }


    /**
     * @param qname the qname to set
     */
    public void setQname(String qname) {
        this.qname = qname;
    }


    /**
     * @param sessionAckMode the sessionAckMode to set
     */
    public void setSessionAckMode(int sessionAckMode) {
        this.sessionAckMode = sessionAckMode;
    }


    /**
     * @param connectionFactory the connectionFactory to set
     */
    public void setConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    private boolean stopFlag = false;


    /**
     * 按约定，请求方会传来可能的chainkey组合,而且按优先级存放，优先级高的放在前面。
     * 使用CoreService.uprightSlash分割。
     *
     * @see org.owens.framework.core.jms.DefaultJmsService。getChainKey()
     * @see
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


    /* (non-Javadoc)
     * @see org.owens.framework.chain.Handler#exceute(java.util.Map)
     *
     * @Todo--是否能用Jms来改进性能，
     * 好处，jms的Message，已经有messageID，reletiveId，timestamp等信息，不需要还原消息即可获得相应的信息。
     *
     * 坏处，不知道扩展性如何，比如.net的后台不支持jms如何处理？
     *
     */
    public void exceute(Map args) throws Exception {

        int i = 0;
        if (logger.isInfoEnabled()) logger.info("Start frontendHandler..." + Thread.currentThread().getName());

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
                    if (logger.isDebugEnabled()) logger.debug("frontend start receiving reply");
                    if (doesPerformanceTrack) t00 = System.currentTimeMillis();
                    Message message = consumer.receive(timeout);
                    if (message != null && message instanceof TextMessage) {
                        if (logger.isDebugEnabled()) logger.debug("frontend end receiving reply message=" + message);
                        TextMessage tmsg = (TextMessage) message;
                        if (doesPerformanceTrack) {
                            t1 = System.currentTimeMillis();
                            t0 = tmsg.getLongProperty(doesPerformanceTrackKey);
                            if (t1 - t0 > timeThreshold)
                                logger.warn("get reply:Q deliver delay=" + (t0 - t1) + " jmsTemplate.receive=" + (t1 - t00));
                        }
                        if (logger.isDebugEnabled()) logger.debug(" receive reply content : " + tmsg.getText());
                        //是用TextMessage的JMSCorrelationID来指定id，避免了一次还原对象
                        if (tmsg.getJMSCorrelationID() == null) {
                            //表示需要异步处理结果
                            //JmsHandlerChain taskHandlerChain=(JmsHandlerChain)SpringContextHelper.getBean(tmsg.getJMSType());

                            HandlerChain taskHandlerChain = getChain(tmsg.getJMSType());

                            if (taskHandlerChain == null)
                                logger.error("cannot find chain for " + tmsg.getJMSType());
                            else {
                                //当前是taskThread,args是Map<String,Object>
                                args.clear();
                                args.put(JmsHandlerChain.KeyJmsHandlerChain, taskHandlerChain);
                                args.put(JmsHandlerChain.KeyJmsMessageString, tmsg.getText());
                                if (!JmsMessage.NullReplyId.equals(tmsg.getJMSCorrelationID()))
                                    args.put(JmsHandlerChain.KeyJmsReplyId, tmsg.getJMSCorrelationID());
//								taskHandlerChain.initArgs();
//								Map<String,Object> args1=taskHandlerChain.getArgs();
//								args1.put(JmsHandlerChain.KeyJmsMessageString, tmsg.getText());
                                taskHandlerChain.run(args);
                            }

                        } else {
                            //表示有线程再等待结果
//							int index=Integer.parseInt(tmsg.getJMSCorrelationID());
//							InvokerObject iobj=(InvokerObject)ObjectWait.getInstance().getObject(index);
                            String index = tmsg.getJMSCorrelationID();
                            InvokerObject iobj = ObjectWait.getInstance().getObject(index);
                            iobj.setResMessage(tmsg.getText());

                            if (tmsg.getBooleanProperty(JmsMessage.IsException))
                                iobj.setException(true);
                            else
                                iobj.setException(false);

                            synchronized (iobj) {
                                iobj.notify();
                            }
                        }
                    }

                }
                //提示
                if (logger.isDebugEnabled()) {
                    i++;
                    if (i > 1000000) i = 0;
                    logger.debug("frontend i=" + i);
                }

                //error occour: connection? session? consumer?

            } catch (Exception e) {
                logger.error("forintendHandler", e);
            } finally {
                if (consumer != null) try {
                    consumer.close();
                } catch (Exception fe) {
                    logger.error("forintendHandler-consumer-finally", fe);
                } finally {
                    consumer = null;
                }
                if (destination != null) try {
                    destination = null;
                } catch (Exception fe) {
                    logger.error("forintendHandler-destination-finally", fe);
                }
                if (session != null) try {
                    session.close();
                } catch (Exception fe) {
                    logger.error("forintendHandler-session-finally", fe);
                } finally {
                    session = null;
                }
                if (connection != null) try {
                    connection.close();
                } catch (Exception fe) {
                    logger.error("forintendHandler-connection-finally", fe);
                } finally {
                    connection = null;
                }
                if (errCount < maxErrorNum) errCount++;
                Thread.currentThread().sleep(errCount * 1000);
                logger.warn("forintendHandler try to reconnect to QServer--{}", errCount);
            }
        }

        logger.warn("forintendHandler-exit");

    }
}
