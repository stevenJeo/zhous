package org.base.framework.jms;

import org.base.framework.chain.HandlerChain;
import org.base.framework.context.SpringContextHelper;
import org.base.framework.task.TaskThread;
import org.base.framework.task.TaskThreadPool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestSendnWait {
    private final Logger log = LoggerFactory.getLogger(TestSendMessage.class);

    private int count = 10;
    private int count1 = 200;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
/*		String dirPath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String log4jProperties=dirPath+"log4j.properties";
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
	    loggerContext.reset();
	    JoranConfigurator joranConfigurator = new JoranConfigurator();
	    joranConfigurator.setContext(loggerContext);
	    try {
	    	joranConfigurator.doConfigure(log4jProperties);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }*/
        log.debug("SaveData main now begin...");
        //ApplicationContext act = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});

        SpringContextHelper.init(new String[]{"data-applicationContext.xml"});

        log.debug("SaveData main  end initation...");

        TaskThreadPool pool = TaskThreadPool.getInstance();
        pool.init(count, JmsTaskThread.class);


//		tt=pool.getTaskThread();
//		tt.addTask(null, chain);

        ObjectWait ow = ObjectWait.getInstance();
        ow.init(80000);


    }

    public void test() throws Exception {
        HandlerChain chain = (HandlerChain) SpringContextHelper.getBean("frontendHandlerChain");
        TaskThread tt = TaskThreadPool.getInstance().getTaskThread();
        tt.addTask(chain);


        OrderMessage message = new OrderMessage();
        message.setApp("appTest");
        message.setService("serviceTest");

        message.setOrderId(123);
//        message.setSubDate(new Date());

        //message.setBody("body"+System.currentTimeMillis());


        CoreService jmsCoreService = (JmsCoreService) SpringContextHelper.getBean("jmsCoreService");

        String replyString = jmsCoreService.waitReply(message, 10);
        OrderMessage reply = (OrderMessage) JmsMessageHelper.toJmsMessage(replyString, OrderMessage.class);
//        Assert.isTrue(message.getSubDate().equals(reply.getSubDate()));
        Assert.isTrue(reply.getSign() != null);
    }


    @Test
    public void test1() throws Exception {
        HandlerChain chain = (HandlerChain) SpringContextHelper.getBean("frontendHandlerChain");
        for (int i = 0; i < count; i++) {
            TaskThread tt = TaskThreadPool.getInstance().getTaskThread();
            Thread.sleep(1000);
            tt.addTask(chain);
        }

        Thread.sleep(1000);

        Thread[] tbuf = new Thread[count1];

        SendMessage sm = new SendMessage();

        for (int i = 0; i < count1; i++) {
            Thread t = new Thread(sm);
            t.start();
            tbuf[i] = t;

        }
        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            //System.console().writer().print("input, quit for exit");
            String chainKey = bufferReader.readLine();

            if (chainKey != null && !chainKey.isEmpty()
                    && chainKey.startsWith("quit")) {
                break;
            }

        }

        int i = 0;
        i++;

    }

}
