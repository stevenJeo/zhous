package org.base.framework.jms;

import org.base.framework.context.SpringContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class ServiceImpl implements Service {

    private final static Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

    @Autowired
    private JmsCoreService jmsService;

    private boolean directCall = false;


    /**
     * @param directCall the callDirectly to set
     */
    public void setDirectCall(boolean directCall) {
        this.directCall = directCall;
    }

    /**
     *
     */
    public ServiceImpl() {
    }


    /**
     * 按约定
     * 按如下顺序查找
     * version_service_app_chianPostfix
     * service_app_chianPostfix
     *
     * @see //org.zzs.framework.core.jms.DefaultJmsService.getChainKey()
     * @see //org.zzs.framework.core.jms.backend.DefaultBackendHandler.getChain()
     */
    private JmsHandlerChain getChain(String app, String service, String version, StringBuilder kb) {
        //StringBuilder kb=getStringBuilder();
        kb.delete(0, kb.length());

        String chainKey = null;
        if (version != null && !(version.isEmpty())) {
            kb.append(version).append(underline).append(service).append(underline).append(app).append(chainPostfix);
            chainKey = kb.toString();
            JmsHandlerChain taskHandlerChain = (JmsHandlerChain) SpringContextHelper.getBean(chainKey);
            if (taskHandlerChain != null)
                return taskHandlerChain;
            else {
                if (logger.isDebugEnabled()) logger.debug("cannot find chain key={}", chainKey);
                kb.delete(0, version.length() + underline.length());
            }
        }

        if (kb.length() == 0) kb.append(service).append(underline).append(app).append(chainPostfix);
        chainKey = kb.toString();
        JmsHandlerChain taskHandlerChain = (JmsHandlerChain) SpringContextHelper.getBean(chainKey);
        return taskHandlerChain;
    }


    @Override
    public String callService(JmsMessage message, int delaySec) throws Exception {
        final String textMessage = JmsMessageHelper.toString(message);
        return callService(message.getApp(), message.getService(), message.getVersion(), textMessage, delaySec);
    }

    @Override
    public String callService(String app, String service, String version, String textMessage, int delaySecond) throws Exception {
        if (!directCall) {
            String replyString = jmsService.waitReply(app, service, version, textMessage, delaySecond);
            return replyString;
        }

        String result = null;

        try {
            String chainKey = service + "_" + app + "_chain";
            JmsHandlerChain taskHandlerChain = (JmsHandlerChain) SpringContextHelper.getBean(chainKey);
            if (taskHandlerChain == null) {
                logger.error("cannot find chain for " + chainKey);
                return null;
            }

            Map<String, Object> args = new HashMap<String, Object>();
            args.put(JmsHandlerChain.KeyJmsMessageString, textMessage);
            args.put(JmsHandlerChain.KeyJmsHandlerChain, taskHandlerChain);
            //args.put(JmsHandlerChain.KeyJmsMessageString, message);
            //taskHandlerChain.setDirectCall(true);
            taskHandlerChain.setDirectCall(true);
            taskHandlerChain.run(args);


            JmsMessage reply = (JmsMessage) taskHandlerChain.getJmsReply(args);
            if (reply != null) {
                if (reply.getResult() != null)
                    result = reply.getResult();
                else
                    result = JmsMessageHelper.toString(reply);
            }

        } catch (JmsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("ServiceImpl error", e);
        }

        return result;


    }
}
