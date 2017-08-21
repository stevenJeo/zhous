/**
 *
 */
package com.zzs.zhous.core;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import com.zzs.zhous.node.key.RSAUtils;
import org.base.framework.chain.Handler;
import org.base.framework.jms.JmsHandlerChain;
import org.base.framework.jms.JmsTaskThread;
import org.base.framework.task.TaskThreadPool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Administrator
 */
public class TestCipherTreadSafety {
    private final static Logger log = LoggerFactory.getLogger(TestCipherTreadSafety.class);

    @Test
    public void testMutliThreadOneProvider() {

        //System.out.println("curPath6="+Thread.currentThread().getContextClassLoader().getResource("./"));
        //System.out.println("curPath7="+System.getProperties().getProperty("user.dir"));
        String path = System.getProperties().getProperty("user.dir") + System.getProperties().getProperty("file.separator");
        path = Thread.currentThread().getContextClassLoader().getResource("./").getPath();
        //System.out.println("path="+path);
        //path=Thread.currentThread().getContextClassLoader().getResource("./").toString();


        String logXmlCfg = path + "server-logback.xml";


        //String path=this.getClass().getClassLoader().getResource("/").getPath()+System.getProperties().getProperty("file.separator");
        //String logXmlCfg="logback.xml";
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        try {
            joranConfigurator.doConfigure(logXmlCfg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.warn("logback init Ok,cfg={}", logXmlCfg);

        int numberofReceiver = 100;
        String appId = "001";
        String userId = "003";

        String alogrithm = "RSA";
        String keyPath;//"src/test/java/key";
        String keyStorePwd = "123456";
        String sysAlias = "ss";
        String sysAliasPwd = "123456";
        int rsaRefreshInteral = 60 * 60 * 1000; // 1 hour


        RSAUtils rsautils = RSAUtils.getInstance();
        try {
            log.warn("init rsaUtils....");

            //path=this.getClass().getClassLoader().getResource("./").getPath()+System.getProperties().getProperty("file.separator");
            //String path=WebApplicationRSAUtilsLoader.class.getResource("../../../../../../")+System.getProperties().getProperty("file.separator");
            keyPath = path + "key" + System.getProperties().getProperty("file.separator");
            log.warn("keyPath={}", keyPath);

            rsautils.setAlogrithm(alogrithm);
            rsautils.setKeyPath(keyPath);
            rsautils.setKeyStorePwd(keyStorePwd);
            rsautils.setRsaRefreshInteral(rsaRefreshInteral);
            rsautils.setSysAlias(sysAlias);
            rsautils.setSysAliasPwd(sysAliasPwd);
            //rsautils.setWorkKeyUpdaterExceutor(syncWorkKeyHandler);


            rsautils.init(keyPath, rsaRefreshInteral);


            TaskThreadPool pool = TaskThreadPool.getInstance();
            pool.init(numberofReceiver, JmsTaskThread.class);

            TestCipherTreadSafetyClient handler = new TestCipherTreadSafetyClient();
            handler.setPool(pool);
            handler.setAppId(appId);
            handler.setUserId(userId);
            handler.setRsaUtils(rsautils);

            JmsHandlerChain chain = new JmsHandlerChain();
            List<Handler> list = new ArrayList<Handler>();
            list.add(new TestCipherTreadSafetyServer());
            chain.setHandlers(list);

            handler.setReceiveChain(chain);


            handler.exceute(null);

        } catch (Exception e) {
            log.error("TestCipherTreadSafety", e);
        }


    }


}
