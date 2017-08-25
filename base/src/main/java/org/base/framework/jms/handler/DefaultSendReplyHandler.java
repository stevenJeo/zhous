package org.base.framework.jms.handler;

import org.base.framework.chain.Handler;
import org.base.framework.exception.FrameworkException;
import org.base.framework.jms.JmsCoreService;
import org.base.framework.jms.JmsHandlerChain;
import org.base.framework.jms.JmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
@Component
public class DefaultSendReplyHandler implements Handler {
    private final Logger log = LoggerFactory.getLogger(DefaultSendReplyHandler.class);


    @Autowired(required = false)
    private JmsCoreService jmsCoreService = null;

    /**
     *
     */
    public DefaultSendReplyHandler() {
        //jmsCoreService=(JmsCoreService)SpringContextHelper.getBean("jmsCoreService");
    }

    /* (non-Javadoc)
     * @see org.owens.framework.chain.Handler#exceute(java.util.Map)
     */
    @Override
    public void exceute(Map args) throws Exception {
        JmsHandlerChain chain = (JmsHandlerChain) args.get(JmsHandlerChain.KeyJmsHandlerChain);
        JmsMessage msg = (JmsMessage) chain.getJmsReply(args);
        if (msg == null
                || msg.getApp() == null || msg.getApp().isEmpty()
                || msg.getService() == null || msg.getService().isEmpty()
                || msg.getReplyId() == null || msg.getReplyId().isEmpty()) {
            log.error("reply message=null!");
            throw new FrameworkException(" reply message==null || message.app,serviec,replyId=null");
        }
        if (chain.isDirectCall()) return;
        jmsCoreService.send(msg);

        if (log.isWarnEnabled())
            log.warn("thread DefaultSendReplyHandler end={} {}", Thread.currentThread().getName(), Thread.currentThread().getId());

    }

}
