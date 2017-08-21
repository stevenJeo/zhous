package org.base.framework.jms;

import org.base.framework.chain.Handler;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class SimpleSendNBreakChainRreplyHandler implements Handler {



    /* (non-Javadoc)
     * @see org.owens.framework.chain.Handler#exceute(java.util.Map)
     */
    public void exceute(Map<String, Object> args) throws Exception {
        JmsHandlerChain chain=(JmsHandlerChain)args.get(JmsHandlerChain.KeyJmsHandlerChain);
        SimpleSendNReplyMessage message=(SimpleSendNReplyMessage)chain.getJmsMessage(SimpleSendNReplyMessage.class,args);


        if("breakChain".equals(message.getSign())) {
            message.setSign(message.getService());
            chain.setJmsReply(message,args); // 返回信息
            chain.breakChain(args, false);
        }
        if("breakChainWithoutReply".equals(message.getSign())) {
            message.setSign(message.getService());
            //chain.setReply(message,args); // 没有返回信息
            chain.breakChain(args, false);
        }

        if("breakChainException".equals(message.getSign())) {
            message.setSign(message.getService());
            chain.setJmsReply(message,args); //返回信息
            throw new BreakChainException(message.getService());
        }
        if("breakChainExceptionReplyAsExceptionMessage".equals(message.getSign())) {
            //不建议
            //通过breakChainException结束chain执行，并返回消息
            throw new BreakChainException(message.getService());
        }

        if("occurException".equals(message.getSign())) {
            //模拟先前的handler已经返回信息
            message.setSign(message.getService());
            chain.setJmsReply(message,args);

            //模拟链执行过程中，发生意外。
            throw new Exception("occurException");
        }

        if("occurExceptionWithoutReply".equals(message.getSign())) {
            //模拟链执行过程中，发生意外。而且没有任何信息返回。
            throw new Exception("occurExceptionWithNoReply");
        }


    }
}
