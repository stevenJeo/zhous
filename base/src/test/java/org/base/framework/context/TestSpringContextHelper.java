package org.base.framework.context;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestSpringContextHelper {
    private final Logger log = LoggerFactory.getLogger(TestSpringContextHelper.class);


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
    }

    @Test
    public void test() {
        log.debug("SaveData main now begin...");

        SpringContextHelper.init(new String[]{"data-applicationContext.xml"});

        log.debug("SaveData main  end initation...");

        Object bean=SpringContextHelper.getBean("defaultHandlerChain");

        Assert.isTrue(bean!=null,"bean="+bean.toString());



    }
}
