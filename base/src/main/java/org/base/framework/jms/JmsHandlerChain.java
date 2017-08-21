package org.base.framework.jms;

import org.base.framework.chain.DefaultHandlerChain;
import org.base.framework.chain.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class JmsHandlerChain extends DefaultHandlerChain {
    private final static Logger log = LoggerFactory.getLogger(JmsHandlerChain.class);

    public final static String KeyJmsHandlerChain = "keyJmsHandlerChain";
    public final static String KeyJmsMessageString = "keyJmsMessageString";
    public final static String KeyJmsMessageObject = "keyJmsMessageObject";
    public final static String KeyJmsReplyId = "keyJmsReplyId";
    public final static String KeyJmsReply = "keyJmsReply(jmsMessageObject)";
    public final static String KeyJmsTransaction = "keyJmsTransaction(transactionStatus)";
    public final static String KeyJmsBreakChain = "KeyJmsBreakChain(KeyJmsBreakChain)";
    public final static String KeyJmsBreakChainCommit = "KeyJmsBreakChainCommit(KeyJmsBreakChainCommit)";
    public final static String NoReply = "NoReply";

    public static boolean performanceCheck = true;

    public static long threshold = 5000;

    @Autowired(required = false)
    private JmsCoreService jmsCoreService = null;
    private boolean directCall = false;

    /**
     *
     */
    public JmsHandlerChain() {
        super();
    }


    /**
     * @return the directCall
     */
    public boolean isDirectCall() {
        return directCall;
    }

    /**
     * @param directCall the directCall to set
     */
    public void setDirectCall(boolean directCall) {
        this.directCall = directCall;
    }


    @Autowired(required = false)
    private PlatformTransactionManager transactionManager;


    public JmsMessage getJmsMessage(Class clazz, Map<String, Object> taskArgs) throws Exception {
        if (taskArgs.containsKey(KeyJmsMessageObject))
            return (JmsMessage) taskArgs.get(KeyJmsMessageObject);
        else {
            Object obj = taskArgs.get(KeyJmsMessageString);
            JmsMessage msg = null;
            if (obj instanceof String)
                msg = JmsMessageHelper.toJmsMessage((String) obj, clazz);
            else
                msg = (JmsMessage) obj;

            if (taskArgs.containsKey(KeyJmsReplyId)) msg.setReplyId((String) taskArgs.get(KeyJmsReplyId));
            taskArgs.put(KeyJmsMessageObject, msg);
            return msg;
        }
    }

    /**
     * 设置返回结果。
     *
     * @param message
     * @param taskArgs
     */
    public void setJmsReply(JmsMessage message, Map<String, Object> taskArgs) {
        taskArgs.put(KeyJmsReply, message);
    }

    public JmsMessage getJmsReply(Map<String, Object> taskArgs) {
        return (JmsMessage) taskArgs.get(KeyJmsReply);
    }

    /**
     * 增加breakChain(args,doescommit)终止执行，同时把通过setReply（）添加的结果返回。没有设置则返回noReply
     *
     * @param taskArgs
     * @param doesCommit
     * 参考： setJmsReply
     */
    public void breakChain(Map<String, Object> taskArgs, boolean doesCommit) {
        taskArgs.put(KeyJmsBreakChain, KeyJmsBreakChain);
        if (doesCommit) taskArgs.put(KeyJmsBreakChainCommit, KeyJmsBreakChainCommit);
    }


    public JmsHandlerChain getJmsHandlerChain(Map<String, Object> taskArgs) {
        return (JmsHandlerChain) taskArgs.get(KeyJmsHandlerChain);
    }


    private void transactionHandler(Map<String, Object> args, boolean doesCommit) {
        if (args.containsKey(KeyJmsTransaction)) {
            TransactionStatus status = (TransactionStatus) args.get(KeyJmsTransaction);
            if (status != null && !status.isCompleted()) {
                if (doesCommit) {
                    transactionManager.commit(status);
                    log.info("transaction id={} completed={} has been commit", status, status.isCompleted());
                } else {
                    transactionManager.rollback(status);
                    log.info("transaction id={} completed={} has been rollback", status, status.isCompleted());
                }
            }
        }
    }

    private JmsMessage buildExceptionMessage(Map<String, Object> args, boolean isException, Exception e) throws Exception {
        JmsMessage m = getJmsReply(args);
        if (m == null || isException) {
            //说明，chain没有生成返回信息，根据请求消息复制一个exceptionMessage
            ExceptionMessage exceptionMessage = new ExceptionMessage();
            exceptionMessage.setException(isException);
            m = getJmsMessage(ExceptionMessage.class, args);
            exceptionMessage.setApp(m.getApp());
            exceptionMessage.setService(m.getService());
            exceptionMessage.setVersion(m.getVersion());
            exceptionMessage.setReplyId(m.getReplyId());
            if (e != null)
                exceptionMessage.setResult(e.getMessage());
            else
                exceptionMessage.setResult(NoReply); //没有结果

            return exceptionMessage;
        } else {
            return m;
        }
    }


//	private void populatExceptionMessage(Map<String,Object> args,ExceptionMessage exceptionMessage,boolean isBreakChainException,Exception e) throws Exception{
//		exceptionMessage.setBreakChainException(isBreakChainException);
//		JmsMessage m=getJmsReply(args);
//		if(m==null)
//			//说明，chain没有生成返回信息，根据请求消息复制一个exceptionMessage
//			m=getJmsMessage(JmsMessage.class,args);
//		exceptionMessage.setApp(m.getApp());
//		exceptionMessage.setService(m.getService());
//		exceptionMessage.setVersion(m.getVersion());
//		exceptionMessage.setReplyId(m.getReplyId());
//		if(m.getResult()==null||m.getResult().isEmpty())
//			if(e!=null)
//				exceptionMessage.setResult(e.getMessage());
//			else
//				exceptionMessage.setResult(NoReply); //没有结果
//		else
//			exceptionMessage.setResult(m.getResult());
//	}


    /* (non-Javadoc)
     * @see org.mmo.framework.Handler#run(java.util.Map)
     *
     *
     * 2015.8.14
     *增加BreakChainException
     *默认在链执行过程中，
     *抛出BreakChainException（终止执行），则提交事务，同时把e.getMessage()当作结果返回。
     *抛出其他异常（被动终止执行），则回滚事务，同时把e.getMessage()当作异常信息，在调用端以JmsException形式抛出。
     *
     *增加breakChain(args,doescommit)终止执行，同时把通过setReply（）添加的结果返回。没有设置则返回noReply
     *
     *
     *
     */
    @Override
    public void run(Map<String, Object> args) throws Exception {
        long t0 = 0;
        long t1 = 0;
        long t2 = 0;
        long t3 = 0;
        JmsMessage exceptionMessage = null;
        try {
            if (performanceCheck) t0 = System.currentTimeMillis();
            List<Handler> handlers = this.getHandlers();
            int size = handlers.size();

            for (int i = 0; i < size; i++) {
                if (args.containsKey(KeyJmsBreakChain)) {
                    transactionHandler(args, args.containsKey(KeyJmsBreakChainCommit));
//                    if (args.containsKey(KeyJmsBreakChainCommit)) {
//                        //提交事务
//                        if (args.containsKey(KeyJmsTransaction)) {
//                            TransactionStatus status = (TransactionStatus) args.get(JmsHandlerChain.KeyJmsTransaction);
//                            if (status != null && !status.isCompleted()) {
//                                log.info("JmsHandlerChain break,transaction id={} completed={} has been submit", status, status.isCompleted());
//                                transactionManager.commit(status);
//                            }
//                        }
//                    } else {
//                        if (args.containsKey(KeyJmsTransaction)) {
//                            TransactionStatus status = (TransactionStatus) args.get(JmsHandlerChain.KeyJmsTransaction);
//                            if (status != null && !status.isCompleted()) {
//                                log.info("JmsHandlerChain break,transaction id={} completed={} has been rollback", status, status.isCompleted());
//                                transactionManager.rollback(status);
//                            }
//                        }
//                    }
                    //生成返回结果.（ExceptionMessage,true）
                    exceptionMessage = buildExceptionMessage(args, false, null);
                    if (log.isDebugEnabled()) log.debug("handler no." + i + " asks to break");
                    break; //ask to break;
                }

                Handler handler = handlers.get(i);
                if (log.isDebugEnabled()) log.debug("handler no." + i + " begin");
                if (performanceCheck) t1 = System.currentTimeMillis();
                handler.exceute(args);
                if (performanceCheck) {
                    t2 = System.currentTimeMillis();
                    if ((t2 - t1) > threshold)
                        log.error(String.format("[chain=%s handler=%s] t=%d", this, handler, t2 - t1));
                }
                if (log.isDebugEnabled()) log.debug("handler no." + i + " end");
            }

        } catch (BreakChainException e) {
            //提交事务
            transactionHandler(args, true);
            //生成返回结果.（ExceptionMessage,true）
            exceptionMessage = buildExceptionMessage(args, false, e);
            //记录日志
            if (log.isDebugEnabled()) log.debug("JmsHandlerChain break by", e);

//		}catch(发送失败？) {
//


        } catch (Exception e) {
            //记录日志
            log.error("JmsHandlerChain error", e);
            //rollback？
            transactionHandler(args, false);
//			if(args.containsKey(KeyJmsTransaction)){
//				TransactionStatus status = (TransactionStatus)args.get(JmsHandlerChain.KeyJmsTransaction);
//				if(status!=null && !status.isCompleted()){
//					log.info("transaction id={} completed={} has been rollback",status,status.isCompleted());
//					transactionManager.rollback(status);
//				}
//			}
            if (this.isDirectCall()) {
                //抛异常
                throw e;
            } else {
                //生成结果（ExceptionMessage,false）
                exceptionMessage = buildExceptionMessage(args, true, e);
            }
        } finally {
            //anything to resume?

            //发送结果
            if (!this.isDirectCall() && exceptionMessage != null) {
                try {
                    jmsCoreService.send(exceptionMessage);
                } catch (Exception ee) {
                    log.error("JmsHandlerChain tryCatch error:app={},service={},version={} result={}",
                            exceptionMessage.getApp(), exceptionMessage.getService(),
                            exceptionMessage.getVersion(), exceptionMessage.getResult(), ee);
                }
            }

            if (performanceCheck) {
                t3 = System.currentTimeMillis();
                if ((t3 - t0) > threshold) log.error(String.format("[chain=%s] t=%d", this, t3 - t0));
            }
        }


    }
}
