package org.base.framework.jms;

import org.base.framework.context.SpringContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class JmsCoreService implements CoreService {
    private final Logger log = LoggerFactory.getLogger(JmsCoreService.class);

    public final String doesPerformanceTrackKey = "doesPerformanceTrack";
    private boolean doesPerformanceTrack = true;
    private long timeThreshold = 500;


    /*
     * templateMap,
     *Specific messages should be sent to the predefined destination
     *For jmsTemplate is jmsTemplate and destination
     *TemplateMap saved these definitions.
     *According to the agreement, will be in accordance with the following priority matching:
     * 1.version_service_app_jmsTemplate
     * 2.service_app_jmsTemplate
     * 3.app_jmsTemplate
     */
    private Map<String, String> templateMap;
    /*
     *Specific messages should be sent to the predefined destination
     *For jmsTemplate is jmsTemplate and destination
     *destinationMap saved these definitions.
     *According to the agreement, will be in accordance with the following priority matching:
     * 1.version_service_app_jmsTemplate
     * 2.service_app_jmsTemplate
     * 3.app_jmsTemplate
     *
     */
    private Map<String, String> destinationMap;


    /**
     * @param templateMap the templateMap to set
     */
    public void setTemplateMap(Map<String, String> templateMap) {
        this.templateMap = templateMap;
    }

    public void setDestinationMap(Map<String, String> destinationMap) {
        this.destinationMap = destinationMap;
    }


    /**
     * @param doesPerformanceTrack the doesPerformanceTrack to set
     */
    public void setDoesPerformanceTrack(boolean doesPerformanceTrack) {
        this.doesPerformanceTrack = doesPerformanceTrack;
    }

    /**
     * @param timeThreshold the timeThreshold to set
     */
    public void setTimeThreshold(long timeThreshold) {
        this.timeThreshold = timeThreshold;
    }


    private final String underline = "_";
    private final String destinationPostfix = "_jmsDestination";
    private final String templatePostfix = "_jmsTemplate";
    private final String topicPostfix = "_jmsTopic";
    private final String chainPostfix = "_chain";

    private JmsTemplate getJmsTemplateKey(String app, String service, String version, StringBuilder kb) {
        String key = null;
        kb.delete(0, kb.length());
        if (version != null && !(version.isEmpty())) {
            kb.append(version).append(underline).append(service).append(underline).append(app).append(templatePostfix);
            key = kb.toString();
            if (templateMap.containsKey(key))
                return (JmsTemplate) SpringContextHelper.getBean(templateMap.get(key));
            else
                kb.delete(0, version.length() + underline.length());
        }

        if (kb.length() == 0) kb.append(service).append(underline).append(app).append(templatePostfix);
        key = kb.toString();
        if (templateMap.containsKey(key))
            return (JmsTemplate) SpringContextHelper.getBean(templateMap.get(key));
        else
            kb.delete(0, service.length() + underline.length());

        key = kb.toString();
        if (templateMap.containsKey(key)) return (JmsTemplate) SpringContextHelper.getBean(templateMap.get(key));

        return null;
    }

//	public JmsTemplate getJmsTemplateKey(JmsMessage message){
//		String templateKey=null;
//		if(message.getVersion()!=null && !(message.getVersion().isEmpty())){
//			templateKey=message.getVersion()+"_"+message.getService()+"_"+message.getApp()+"_jmsTemplate";
//			if(templateMap.containsKey(templateKey)) return (JmsTemplate)SpringContextHelper.getBean(templateMap.get(templateKey));
//		}
//
//		templateKey=message.getService()+"_"+message.getApp()+"_jmsTemplate";
//		if(templateMap.containsKey(templateKey)) return (JmsTemplate)SpringContextHelper.getBean(templateMap.get(templateKey));
//
//		templateKey=message.getApp()+"_jmsTemplate";
//		if(templateMap.containsKey(templateKey)) return (JmsTemplate)SpringContextHelper.getBean(templateMap.get(templateKey));
//
//		return null;
//	}


    private Destination getDestinationKey(String app, String service, String version, StringBuilder kb) {
        String key = null;
        kb.delete(0, kb.length());
        if (version != null && !(version.isEmpty())) {
            kb.append(version).append(underline).append(service).append(underline).append(app).append(destinationPostfix);
            key = kb.toString();
            if (destinationMap.containsKey(key))
                return (Destination) SpringContextHelper.getBean(destinationMap.get(key));
            else
                kb.delete(0, version.length() + underline.length());
        }

        if (kb.length() == 0) kb.append(service).append(underline).append(app).append(destinationPostfix);
        key = kb.toString();
        if (destinationMap.containsKey(key))
            return (Destination) SpringContextHelper.getBean(destinationMap.get(key));
        else
            kb.delete(0, service.length() + underline.length());

        key = kb.toString();
        if (destinationMap.containsKey(key)) return (Destination) SpringContextHelper.getBean(destinationMap.get(key));

        return null;
    }


//	private String getChainKey(String app,String service,String version,StringBuilder kb){
//		kb.delete(0, kb.length());
//		if(version!=null && !(version.isEmpty()))
//			kb.append(version).append(underline).append(service).append(underline).append(app).append(chainPostfix);
//		else
//			kb.append(service).append(underline).append(app).append(chainPostfix);
//
//		return kb.toString();
//	}


    /**
     * 按约定
     * 将chianKey包装成为，version_service_app_chianPostfix|service_app_chianPostfix
     *
     * @org.owens.framework.core.jms.backend.DefaultBackendHandler.getChain()
     */
    private String getChainKey(String app, String service, String version, StringBuilder kb) {
        //StringBuilder kb= getStringBuilder();
        kb.delete(0, kb.length());

        String chainKey = null;
        if (version != null && !(version.isEmpty()))
            kb.append(version).append(underline)
                    .append(service).append(underline)
                    .append(app)
                    .append(chainPostfix)
                    .append(uprightSlash);

        kb.append(service).append(underline)
                .append(app)
                .append(chainPostfix);

        return kb.toString();

    }


//	public Destination getDestinationKey(JmsMessage message){
//		String key=null;
//		if(message.getVersion()!=null && !(message.getVersion().isEmpty())){
//			key=message.getVersion()+"_"+message.getService()+"_"+message.getApp()+"_jmsDestination";
//			if(destinationMap.containsKey(key)) return (Destination)SpringContextHelper.getBean(destinationMap.get(key));
//		}
//
//		key=message.getService()+"_"+message.getApp()+"_jmsDestination";
//		if(destinationMap.containsKey(key)) return (Destination)SpringContextHelper.getBean(destinationMap.get(key));
//
//		key=message.getApp()+"_jmsDestination";
//		if(destinationMap.containsKey(key)) return (Destination)SpringContextHelper.getBean(destinationMap.get(key));
//
//		return null;
//	}


    private Topic getTopicKey(String app, String service, String version, StringBuilder kb) {
        String key = null;
        kb.delete(0, kb.length());
        if (version != null && !(version.isEmpty())) {
            kb.append(version).append(underline).append(service).append(underline).append(app).append(topicPostfix);
            key = kb.toString();
            if (destinationMap.containsKey(key))
                return (Topic) SpringContextHelper.getBean(destinationMap.get(key));
            else
                kb.delete(0, version.length() + underline.length());
        }

        if (kb.length() == 0) kb.append(service).append(underline).append(app).append(topicPostfix);
        key = kb.toString();
        if (destinationMap.containsKey(key))
            return (Topic) SpringContextHelper.getBean(destinationMap.get(key));
        else
            kb.delete(0, service.length() + underline.length());

        key = kb.toString();
        if (destinationMap.containsKey(key)) return (Topic) SpringContextHelper.getBean(destinationMap.get(key));

        return null;
    }


    @Override
    public void send(JmsMessage message) throws Exception {
        if (message == null) throw new JmsCoreServiceException("message is null");
        if (message.getApp() == null || message.getApp().isEmpty()) throw new JmsCoreServiceException(" app is null");
        if (message.getService() == null || message.getService().isEmpty())
            throw new JmsCoreServiceException(" service is null");

        //按约定
        //后台默认返回的结果jsonString（保存在result中）。
        //result=null表示特殊的返回结果
        String textMessage1 = null;
        if (message.getResult() == null || message.getResult().isEmpty())
            textMessage1 = JmsMessageHelper.toString((JmsMessage) message);
        else
            textMessage1 = message.getResult();

        final String textMessage = textMessage1;
        final String replyId = message.getReplyId();

        //返回主动异常，BreakChainException
        if (message instanceof ExceptionMessage) {
            ExceptionMessage eMsg = (ExceptionMessage) message;
            send(message.getApp(), message.getService(), message.getVersion(), textMessage, replyId, eMsg.isException());
        } else
            send(message.getApp(), message.getService(), message.getVersion(), textMessage, replyId, false);

        //send(message.getApp(),message.getService(), message.getVersion(),textMessage,replyId);
    }

    /* (non-Javadoc)
     * @see net.r66r.framework.CoreService#send(net.r66r.framework.Message)
     */
    @Override
    public void send(String app, String service, String version, final String textMessage, final String replyId) throws Exception {
        send(app, service, version, textMessage, replyId, false);
    }


    public void send(String app, String service, String version, final String textMessage, final String replyId, final boolean isException) throws Exception {
        if (app == null || app.isEmpty()) throw new JmsCoreServiceException(" app=null ");
        if (service == null || service.isEmpty()) throw new JmsCoreServiceException(" service=null ");

        StringBuilder sb = new StringBuilder();

        JmsTemplate jmsTemplate = getJmsTemplateKey(app, service, version, sb);
        if (jmsTemplate == null) throw new JmsCoreServiceException(" can not find jmsTemplate!");

        final Destination destination = getDestinationKey(app, service, version, sb);
        if (destination == null) throw new JmsCoreServiceException(" can not find destination!");

        final String chainKey = getChainKey(app, service, version, sb);

        jmsTemplate.send(destination, new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                if (log.isDebugEnabled()) log.debug("Sending to:{} text:{}", destination.toString(), textMessage);
                TextMessage m = session.createTextMessage(textMessage);
                if (!JmsMessage.NullReplyId.equals(replyId)) m.setJMSCorrelationID(replyId);
                if (doesPerformanceTrack) m.setLongProperty(doesPerformanceTrackKey, System.currentTimeMillis());

                m.setJMSType(chainKey);
                if (isException) m.setBooleanProperty(JmsMessage.IsException, true);
                return m;
            }
        });
    }


    @Override
    public void publish(JmsMessage message) throws Exception {
        if (message == null) throw new JmsCoreServiceException("message is null");
        if (message.getApp() == null || message.getApp().isEmpty()) throw new JmsCoreServiceException(" app is null");
        if (message.getService() == null || message.getService().isEmpty())
            throw new JmsCoreServiceException(" service is null");

        //按约定
        //后台默认返回的结果jsonString（保存在result中）。
        //result=null表示特殊的返回结果
        String textMessage1 = null;
        if (message.getResult() == null || message.getResult().isEmpty())
            textMessage1 = JmsMessageHelper.toString((JmsMessage) message);
        else
            textMessage1 = message.getResult();

        final String textMessage = textMessage1;
        publish(message.getApp(), message.getService(), message.getVersion(), textMessage);

    }

    @Override
    public void publish(String app, String service, String version, final String textMessage) throws Exception {
        if (app == null || app.isEmpty()) throw new JmsCoreServiceException(" app=null ");
        if (service == null || service.isEmpty()) throw new JmsCoreServiceException(" service=null ");

        StringBuilder sb = new StringBuilder();

        JmsTemplate jmsTemplate = getJmsTemplateKey(app, service, version, sb);
        if (jmsTemplate == null) throw new JmsCoreServiceException(" can not find jmsTemplate!");

        final Topic topic = getTopicKey(app, service, version, sb);
        if (topic == null) throw new JmsCoreServiceException(" can not find topic!");

        final String chainKey = getChainKey(app, service, version, sb);

        jmsTemplate.send(topic, new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                if (log.isDebugEnabled()) log.debug("topic={} text:{}", topic.toString(), textMessage);
                TextMessage m = session.createTextMessage(textMessage);
                m.setJMSCorrelationID(JmsMessage.NullReplyId);
                if (doesPerformanceTrack) m.setLongProperty(doesPerformanceTrackKey, System.currentTimeMillis());
                m.setJMSType(chainKey);

                return m;
            }
        });

    }


    @Override
    public String waitReply(JmsMessage message, int delaySecond) throws Exception {
        if (message == null) throw new JmsCoreServiceException("message is null");
        if (message.getApp() == null || message.getApp().isEmpty()) throw new JmsCoreServiceException(" app is null");
        if (message.getService() == null || message.getService().isEmpty())
            throw new JmsCoreServiceException(" service is null");

        final String textMessage = JmsMessageHelper.toString((JmsMessage) message);

        return waitReply(message.getApp(), message.getService(), message.getVersion(), textMessage, delaySecond);

    }

    @Override
    public String waitReply(String app, String service, String version, final String textMessage, int delaySecond) throws Exception {
        long t1 = 0, t2 = 0, t3 = 0, t4 = 0, t5 = 0;
        if (doesPerformanceTrack) t1 = System.currentTimeMillis();

        if (app == null || app.isEmpty()) throw new JmsCoreServiceException(" app=null ");
        if (service == null || service.isEmpty()) throw new JmsCoreServiceException(" service=null ");

        StringBuilder sb = new StringBuilder();

        JmsTemplate jmsTemplate = getJmsTemplateKey(app, service, version, sb);
        if (jmsTemplate == null) throw new JmsCoreServiceException(" can not find jmsTemplate!");

        final Destination destination = getDestinationKey(app, service, version, sb);
        if (destination == null) throw new JmsCoreServiceException(" can not find destination!");

        final String chainKey = getChainKey(app, service, version, sb);
        if (doesPerformanceTrack) t2 = System.currentTimeMillis();

        //add reply_key
//		InvokerObject iobj = new InvokerObject();
//		int key = ObjectWait.getInstance().getKey(iobj);
//		final String replyId=""+key;
//		iobj.setReqMessage(textMessage);

        InvokerObject iobj = ObjectWait.getInstance().getKey(textMessage);
        final String replyId = iobj.getResMessage();


        if (doesPerformanceTrack) t2 = System.currentTimeMillis();
        jmsTemplate.send(destination, new MessageCreator() {
            public javax.jms.Message createMessage(Session session) throws JMSException {
                if (log.isDebugEnabled())
                    log.debug("Sending to:{} send message :{}" + destination.toString(), textMessage);
                TextMessage m = session.createTextMessage(textMessage);
                m.setJMSCorrelationID(replyId);
                m.setJMSType(chainKey);
                if (doesPerformanceTrack) m.setLongProperty(doesPerformanceTrackKey, System.currentTimeMillis());
                return m;
            }
        });

        if (doesPerformanceTrack) t3 = System.currentTimeMillis();
        synchronized (iobj) {
            iobj.wait(delaySecond * 1000);
        }

        if (doesPerformanceTrack) {
            t4 = System.currentTimeMillis();
            if (t4 - t1 > timeThreshold)
                log.warn("waitReply slow:total={} send={} else={}", (t4 - t1), (t3 - t2), (t2 - t1));
        }


        if (iobj.isException())
            throw new JmsException(iobj.getResMessage());

        if (log.isDebugEnabled()) log.debug("reply={}", iobj.getResMessage());
        return iobj.getResMessage();
    }


//	/* (non-Javadoc)
//	 * @see net.r66r.framework.CoreService#waitReply(net.r66r.framework.Message, int)
//	 */
//	@Override
//	public String waitReply(JmsMessage message, int delaySecond) throws Exception {
//
//
//		if(message==null) throw new JmsCoreServiceException("message is null");
//		if(message.getApp()==null || message.getApp().isEmpty()) throw new JmsCoreServiceException(" app is null");
//		if(message.getService()==null || message.getService().isEmpty()) throw new JmsCoreServiceException(" service is null");
//		//add reply_key
//		InvokerObject iobj = new InvokerObject();
//		int key = ObjectWait.getInstance().getKey(iobj);
//		message.setReplyId(""+key);
//
//		final String textMessage=JmsMessageHelper.toString((JmsMessage)message);
//		iobj.setReqMessage(textMessage);
//
//		JmsTemplate jmsTemplate=getJmsTemplateKey(message);
//		if(jmsTemplate==null)throw new JmsCoreServiceException(" can not find jmsTemplate!");
//
//		final Destination destination=getDestinationKey(message);
//		if(destination==null) throw new JmsCoreServiceException(" can not find destination!");
//
//		final JmsMessage msg=message;
//
//		jmsTemplate.send(destination,new MessageCreator() {
//			public javax.jms.Message createMessage(Session session) throws JMSException
//			{
//				if(log.isDebugEnabled()) log.debug("Sending to :" + destination.toString()+" send message : "+textMessage);
//				TextMessage m = session.createTextMessage(textMessage);
//				if(msg.getReplyId()!=null) m.setJMSCorrelationID(msg.getReplyId());
//				String key=null;
//				if(msg.getVersion()!=null)
//					key=msg.getVersion()+"_"+msg.getService()+"_"+msg.getApp()+"_chain";
//				else
//					key=msg.getService()+"_"+msg.getApp()+"_chain";
//
//				m.setJMSType(key);
//				return m;
//			}
//		});
//
//		synchronized(iobj){
//			iobj.wait(delaySecond*1000);
//		}
//
//		if(log.isDebugEnabled()) log.debug("reply="+iobj.getResMessage());
//
//		return iobj.getResMessage();
//
//	}
}
