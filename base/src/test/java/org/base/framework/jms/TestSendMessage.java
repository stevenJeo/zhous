package org.base.framework.jms;

import org.base.framework.context.SpringContextHelper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestSendMessage {
    private final Logger log = LoggerFactory.getLogger(TestSendMessage.class);

    @Before
    public void setUp() throws Exception {
//		String dirPath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
//		String log4jProperties=dirPath+"log4j.properties";
//        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
//	    loggerContext.reset();
//	    JoranConfigurator joranConfigurator = new JoranConfigurator();
//	    joranConfigurator.setContext(loggerContext);
//	    try {
//	    	joranConfigurator.doConfigure(log4jProperties);
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    }
        log.debug("SaveData main now begin...");
        //ApplicationContext act = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});

        SpringContextHelper.init(new String[]{"data-applicationContext.xml"});

        log.debug("SaveData main  end initation...");
    }


    @Test
    public void test() throws Exception {
        OrderMessage message = new OrderMessage();
        message.setApp("appTest");
        message.setService("serviceTest");

        message.setOrderId(123);
//        message.setSubDate(new Date());

        //message.setBody("body"+System.currentTimeMillis());


        CoreService jmsCoreService = (JmsCoreService) SpringContextHelper.getBean("jmsCoreService");

        jmsCoreService.send((JmsMessage) message);
        jmsCoreService.send((JmsMessage) message);
        jmsCoreService.send((JmsMessage) message);
        jmsCoreService.send((JmsMessage) message);
        jmsCoreService.send((JmsMessage) message);
    }
}
