package org.base.framework.chain;

import org.base.framework.context.SpringContextHelper;
import org.junit.Test;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestDefaultHandlerChain {
    private final Logger log = LoggerFactory.getLogger(TestDefaultHandlerChain.class);

    @Before
    public void setUp() throws Exception {

//		String dirPath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
//		String log4jProperties=dirPath+"logback.xml";
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

        SpringContextHelper.init(new String[]{"data-applicationContext.xml"});

        log.debug("SaveData main  end initation...");

    }

    @Test
    public void test() throws Exception {
        HandlerChain chain = (HandlerChain) SpringContextHelper.getBean("defaultHandlerChain");

        Assert.isTrue(chain != null, "bean=" + chain.toString());

        chain.run(null);
    }
}
