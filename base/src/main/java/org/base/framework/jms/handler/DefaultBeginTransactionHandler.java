package org.base.framework.jms.handler;

import org.base.framework.chain.Handler;
import org.base.framework.jms.JmsHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
@Component
public class DefaultBeginTransactionHandler implements Handler {
    private final Logger logger = LoggerFactory.getLogger(DefaultBeginTransactionHandler.class);

    @Autowired(required = false)
    private PlatformTransactionManager transactionManager;

//    @Autowired(required=false)
//    @Qualifier("dataSource")
//    private BoneCPDataSource datasource;


    /* (non-Javadoc)
     * @see org.owens.framework.chain.Handler#exceute(java.util.Map)
     */
    @Override
    public void exceute(Map<String, Object> args) throws Exception {
        JmsHandlerChain chain = (JmsHandlerChain) args.get(JmsHandlerChain.KeyJmsHandlerChain);
        ////TODO 定义jms的Exception？
        if (chain == null) throw new Exception("必须定义使用JmsHandlerChain");

        //debug datasource.pool
//        if(logger.isDebugEnabled()){
//            if(null==datasource)datasource = (BoneCPDataSource) SpringContextHelper.getBean("dataSource");
//            if(null!=datasource){
//                logger.debug("datasource.using={}, PoolAvailabilityThreshold={}",datasource.getTotalLeased(),datasource.getPoolAvailabilityThreshold());
//                logger.debug("datasource.ReleaseHelperThreads={},",datasource.getReleaseHelperThreads());
//            }else
//                logger.debug("get.datasource is null...");
//        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // explicitly setting the transaction name is something that can only be done programmatically
        ////TODO add System.nanoTime()? Thread.currentThread().getName() should be unique?
        def.setName(Thread.currentThread().getName());//+System.nanoTime());
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        args.put(JmsHandlerChain.KeyJmsTransaction, status);

        if (logger.isDebugEnabled())
            logger.debug("begin new transaction name={}:id={},completed={},newTransaction={}", def.getName(), status, status.isCompleted(), status.isNewTransaction());

        //debug datasource.pool
//        if(logger.isDebugEnabled()){
//            if(null!=datasource){
//                logger.debug("datasource.using={}, PoolAvailabilityThreshold={}",datasource.getTotalLeased(),datasource.getPoolAvailabilityThreshold());
//                logger.debug("datasource.ReleaseHelperThreads={},",datasource.getReleaseHelperThreads());
//            }else
//                logger.debug("get.datasource is null...");
//        }
    }
}
