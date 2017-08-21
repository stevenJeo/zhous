package org.base.framework.jms.handler;

import org.base.framework.chain.Handler;
import org.base.framework.jms.JmsHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
@Component
public class DefaultCommitTransactionHandler implements Handler {
    private final Logger logger = LoggerFactory.getLogger(DefaultCommitTransactionHandler.class);

    @Autowired(required = false)
    private PlatformTransactionManager transactionManager;


    public void exceute(Map<String, Object> args) throws Exception {
        JmsHandlerChain chain = (JmsHandlerChain) args.get(JmsHandlerChain.KeyJmsHandlerChain);
        ////TODO 定义jms的Exception？
        if (chain == null) throw new Exception("必须定义使用JmsHandlerChain");

        if (args.containsKey(JmsHandlerChain.KeyJmsTransaction)) {
            TransactionStatus status = (TransactionStatus) args.get(JmsHandlerChain.KeyJmsTransaction);
            if (status != null && !status.isCompleted()) {
                transactionManager.commit(status);
                if (logger.isDebugEnabled())
                    logger.debug("transaction {} has been commit,and completed={})", status, status.isCompleted());
                status = null;
            } else if (logger.isDebugEnabled()) logger.debug("transaction has been close(commit,or rollback)");
        } else if (logger.isDebugEnabled()) logger.debug("NoTransaction ");
    }
}
