package org.base.framework.jms;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.base.framework.context.SpringContextHelper;
import org.base.framework.context.WebApplicationJmsLoader;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestFrontendNBackend_fronted {
    private final static Logger log = LoggerFactory.getLogger(TestFrontendNBackend_fronted.class);

    @Before
    public void init() {
        if(log.isDebugEnabled())log.debug("curPath6="+Thread.currentThread().getContextClassLoader().getResource("./"));
        //System.out.println("curPath7="+System.getProperties().getProperty("user.dir"));
        String path=System.getProperties().getProperty("user.dir")+System.getProperties().getProperty("file.separator");
        //System.out.println("path="+path);
        //path=Thread.currentThread().getContextClassLoader().getResource("./").toString();


        String logXmlCfg=path+"bin\\resource\\logback.xml";
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        try {
            joranConfigurator.doConfigure(logXmlCfg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("logback initialization error",e);
        }

        if(log.isDebugEnabled())log.debug("backend env setup begin...logXmlCfg={}",logXmlCfg);

        String[] xmlCfg=new String[] {
                "resource/jms/ctx-basic.xml",
                "resource/jms/ctx-mq.xml",
                "resource/jms/ctx-service.xml",
        };

        if(log.isDebugEnabled()){
            log.debug("xml configuration file:");
            for(int i=0;i<xmlCfg.length;i++)log.debug(xmlCfg[i]);
        }
        SpringContextHelper.init(xmlCfg);


        WebApplicationJmsLoader webApplicationJmsLoader=(WebApplicationJmsLoader)SpringContextHelper.getBean("webApplicationJmsLoader");
        webApplicationJmsLoader.setServletContext(null);


        if(log.isDebugEnabled())log.debug("TestFrontendNBackend_init OK");

    }

//  @After
//	public void stop() throws Exception {
//		WebApplicationJmsLoader webApplicationJmsLoader=(WebApplicationJmsLoader)SpringContextHelper.getBean("SpringContextHelper");
//		webApplicationJmsLoader.destroy();
//	}


    @Test
    public void testall() throws Exception {
        testSendNReply();
        testSendNReplyWithVersion();
        testSendNBreakChianReply();
        testSendNBreakChianNoReply();
        testSendNBreakChianExceptionReply();
        testSendNBreakChianExceptionReplyAsExceptionMessage();
        testSendNBreakException();
    }




    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 基本功能
     * 	根据service_app找到处理链
     * 	返回结果
     *
     * @throws Exception
     */
    public void testSendNReply() throws Exception{

        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setService("simpleSendNReply");

        Service cs=(Service)SpringContextHelper.getBean("service");

        String reply=cs.callService(t, 30);

        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());
        SimpleSendNReplyMessage t1=JmsMessageHelper.toObject(reply, new TypeReference<SimpleSendNReplyMessage>() {});
        Assert.assertEquals(t.getService(),t1.getSign());

        if(log.isInfoEnabled())log.info("testSendNReply()...OK!");

    }
    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 基本功能
     * 	根据verison_service_app找到处理链
     * 	返回结果
     * 	查找不到verison_service_app时，按service_app
     *
     * @throws Exception
     */
    public void testSendNReplyWithVersion() throws Exception{

        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setVersion("1");
        t.setService("simpleSendNReply");

        Service cs=(Service)SpringContextHelper.getBean("service");

        String reply=cs.callService(t, 30);

        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());
        SimpleSendNReplyMessage t1=JmsMessageHelper.toObject(reply, new TypeReference<SimpleSendNReplyMessage>() {});
        Assert.assertEquals(t.getService()+t.getVersion(),t1.getSign());

        if(log.isInfoEnabled())log.info("testSendNReplyWithVersion_1...OK!");


        t.setVersion("2"); //doesnot exist,will call "SimpleSendNReply_unitTest_chain"
        reply=cs.callService(t, 30);
        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());
        t1=JmsMessageHelper.toObject(reply, new TypeReference<SimpleSendNReplyMessage>() {});

        Assert.assertEquals(t.getService(),t1.getSign());

        if(log.isInfoEnabled())log.info("testSendNReplyWithVersion_2...OK!");

    }
    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 测试功能
     * 	根据verison_service_app找到处理链
     * 	返回结果
     * 	设置返回结果(t1.sign)，使用breakChain中断chain执行，
     *
     */
    @SuppressWarnings("deprecation")
    public void testSendNBreakChianReply() throws Exception{

        if(log.isInfoEnabled())log.info(" 发送消息到 Q，后台接收后再返回\n 确认Q服务器和后端都Ok！");


        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setSign("breakChain");
        t.setService("simpleSendNBreakChainReply");

        Service cs=(Service)SpringContextHelper.getBean("service");

        String reply=cs.callService(t, 30);


        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());
        SimpleSendNReplyMessage t1=JmsMessageHelper.toObject(reply, new TypeReference<SimpleSendNReplyMessage>() {});
        Assert.assertEquals(t.getService(),t1.getSign());

        if(log.isInfoEnabled())log.info("testSendNBreakChianReply...OK!");

    }


    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 测试功能
     * 	根据verison_service_app找到处理链
     * 	返回结果
     * 	使用breakChain中断chain执行，没有设置返回结果，默认返回CoreHandlerChain.NoReply
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public void testSendNBreakChianNoReply() throws Exception{



        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setSign("breakChainWithoutReply");
        t.setService("simpleSendNBreakChainReply");

        Service cs=(Service)SpringContextHelper.getBean("service");
        long t0=System.currentTimeMillis();
        String reply=cs.callService(t, 30);
        long t1=System.currentTimeMillis();


        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());

        Assert.assertEquals(JmsHandlerChain.NoReply, reply);

        if(log.isInfoEnabled())log.info("testSendNBreakChianWithoutReply...OK! t1-t0={}",t1-t0);




    }


    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 测试功能
     * 	根据verison_service_app找到处理链
     * 	返回结果
     * 	使用breakChainException中断chain执行，定义了返回结果
     */
    public void testSendNBreakChianExceptionReply() throws Exception{

        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setSign("breakChainException");
        t.setService("simpleSendNBreakChainExceptionReply");

        Service cs=(Service)SpringContextHelper.getBean("service");

        String reply=cs.callService(t, 30);


        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());
        SimpleSendNReplyMessage t1=JmsMessageHelper.toObject(reply, new TypeReference<SimpleSendNReplyMessage>() {});
        Assert.assertEquals(t.getService(),t1.getSign());

        if(log.isInfoEnabled())log.info("testSendNBreakChianExceptionReply...OK!");

    }
    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 测试功能
     * 	根据verison_service_app找到处理链
     * 	返回结果
     * 	使用breakChainException中断chain执行，返回结果=Exception。Message
     */
    public void testSendNBreakChianExceptionReplyAsExceptionMessage() throws Exception{


        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setSign("breakChainExceptionReplyAsExceptionMessage");
        t.setService("simpleSendNBreakChainExceptionReply");

        Service cs=(Service)SpringContextHelper.getBean("service");

        String reply=cs.callService(t, 30);


        Assert.assertNotNull(reply);
        Assert.assertTrue(!reply.isEmpty());
        //SimpleSendNReplyMessage t1=JsonHelper.toObject(reply, new TypeReference<SimpleSendNReplyMessage>() {});
        Assert.assertEquals(t.getService(),reply);

        if(log.isInfoEnabled())log.info("breakChainExceptionReplyAsExceptionMessage...OK!");

    }

    /**
     * 发送消息到 Q，后台接收后再返回
     *
     * 测试功能
     * 	根据verison_service_app找到处理链
     * 	返回结果
     * 	执行过程中抛Exception，返回结果=Exception。Message
     */
    public void testSendNBreakException() throws Exception{

        SimpleSendNReplyMessage t=new SimpleSendNReplyMessage();
        t.setSign("occurException");
        t.setService("simpleSendNBreakChainExceptionReply");

        Service cs=(Service)SpringContextHelper.getBean("service");

        JmsException ce=null;
        try {
            String reply=cs.callService(t, 30);

            reply=null;
        }catch(Exception e) {
            if(e instanceof JmsException)
                ce=(JmsException)e;
        }

        Assert.assertNotNull(ce);
        Assert.assertEquals(t.getSign(),ce.getMessage());
        if(log.isInfoEnabled())log.info("occurException...OK!");

    }
}
