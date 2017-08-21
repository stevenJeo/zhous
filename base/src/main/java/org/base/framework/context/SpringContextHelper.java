package org.base.framework.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class SpringContextHelper implements ServletContextAware {

    private final static Logger log = LoggerFactory.getLogger(SpringContextHelper.class);

    private static ApplicationContext act = null;

    private static ServletContext servletContext = null;

    public static void init() {
//        String ContextPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//        String log4jProperties = ContextPath + "log4j.properties";
//
//        if (log.isInfoEnabled()) log.info("log4jProperties=" + log4jProperties);
//        //String contextResourceLocation = (String) initObj;
//        //PropertyConfigurator.configure (log4jProperties);
//        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
//        loggerContext.reset();
//        JoranConfigurator joranConfigurator = new JoranConfigurator();
//        joranConfigurator.setContext(loggerContext);
//        try {
//            joranConfigurator.doConfigure(log4jProperties);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //BasicConfigurator.configureByResource(new URL(""));
        //if(log.isInfoEnabled())log.info("using default context xml configuration="+ContextPath+"\\applicationContext.xml");
        //ApplicationContext act = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});

        SpringContextHelper.init(new String[]{"applicationContext.xml"});
    }


    public static void init(String[] filenameArray) {
        if (act == null) act = new ClassPathXmlApplicationContext(filenameArray);
    }

    public static Object getBean(String beanKey) {
        if (beanKey == null || beanKey.isEmpty()) return null;
        if (act == null)
            act = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        Object obj = null;
        try {
            obj = act.getBean(beanKey);
        } catch (NoSuchBeanDefinitionException e) {
            //ignore;
            if (log.isInfoEnabled()) log.info("cannot find bean=" + beanKey);
        } catch (Exception ee) {
            log.error("error SpringContextHelper.getBean(\"" + beanKey + "\")", ee);
        }

        return obj;
    }


    @Override
    public void setServletContext(ServletContext sc) {
        servletContext = sc;
    }
}
