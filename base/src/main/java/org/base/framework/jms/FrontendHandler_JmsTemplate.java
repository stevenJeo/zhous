package org.base.framework.jms;

import org.base.framework.chain.Handler;
import org.base.framework.context.SpringContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class FrontendHandler_JmsTemplate implements Handler {


    private final Logger logger = LoggerFactory.getLogger(FrontendHandler_JmsTemplate.class);

    private long timeout = 1000;

    private JmsTemplate jmsTemplate;

    private Destination destination;


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

    private boolean stopFlag=false;


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

        int i=0;
        jmsTemplate.setReceiveTimeout(timeout);
        if (logger.isInfoEnabled())logger.info("Start frontendHandler..."+Thread.currentThread().getName());

        while(!stopFlag){
            try {
                if (logger.isDebugEnabled())logger.debug("frontend start receiving reply");
                Message message = jmsTemplate.receive(destination);
                if (logger.isDebugEnabled())logger.debug("frontend end receiving reply message="+message);
                if (message != null && message instanceof TextMessage) {
                    TextMessage tmsg = (TextMessage) message;
                    if (logger.isDebugEnabled())logger.debug(" receive reply content : " + tmsg.getText());
                    //是用TextMessage的JMSCorrelationID来指定id，避免了一次还原对象
                    if(tmsg.getJMSCorrelationID()==null){
                        //表示需要异步处理结果
                        JmsHandlerChain taskHandlerChain=(JmsHandlerChain) SpringContextHelper.getBean(tmsg.getJMSType());
                        if(taskHandlerChain==null)
                            logger.error("cannot find chain for "+tmsg.getJMSType());
                        else{
                            //当前是taskThread,args是Map<String,Object>
                            args.clear();
                            args.put(JmsHandlerChain.KeyJmsHandlerChain, taskHandlerChain);
                            args.put(JmsHandlerChain.KeyJmsMessageString,tmsg.getText());
                            if(!JmsMessage.NullReplyId.equals(tmsg.getJMSCorrelationID()))
                                args.put(JmsHandlerChain.KeyJmsReplyId,tmsg.getJMSCorrelationID());
//							taskHandlerChain.initArgs();
//							Map<String,Object> args1=taskHandlerChain.getArgs();
//							args1.put(JmsHandlerChain.KeyJmsMessageString, tmsg.getText());
                            taskHandlerChain.run(args);
                        }

                    }else{
                        //表示有线程再等待结果
//						int index=Integer.parseInt(tmsg.getJMSCorrelationID());
//						InvokerObject iobj=(InvokerObject)ObjectWait.getInstance().getObject(index);
                        String index=tmsg.getJMSCorrelationID();
                        InvokerObject iobj=ObjectWait.getInstance().getObject(index);
                        iobj.setResMessage(tmsg.getText());
                        //if(tmsg.getBooleanProperty(CoreMessage.IsException)) iobj.setException(true);

                        synchronized (iobj){iobj.notify();}
                    }
                }
            } catch (Exception e) {
                logger.error("forintendHandler",e);
            }

            //提示
            i++;
            if(i>1000000)i=0;
            if (logger.isDebugEnabled())logger.debug("frontend i="+i);

        }
    }
}
