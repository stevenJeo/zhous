package org.base.framework.jms;

import org.base.framework.jms.server.Server;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TestFrontendNBackend_backend {

    private final static Logger log = LoggerFactory.getLogger(TestFrontendNBackend_backend.class);

    @Test
    public void init() throws Exception {
        if(log.isDebugEnabled())log.debug("curPath6="+Thread.currentThread().getContextClassLoader().getResource("./"));
        //System.out.println("curPath7="+System.getProperties().getProperty("user.dir"));
        String path=System.getProperties().getProperty("user.dir")+System.getProperties().getProperty("file.separator");
        //System.out.println("path="+path);
        //path=Thread.currentThread().getContextClassLoader().getResource("./").toString();

        Server.main(new String[] {"resource/jms/backend-ctx-basic.xml,resource/jms/backend-ctx-mq.xml,resource/jms/ctx-service.xml"
                ,"10"
                ,path+"bin/resource/logback.xml"});

        if(log.isDebugEnabled())log.debug("TestFrontendNBackend_backend_init OK11");

        if(log.isDebugEnabled())log.debug("any key to stop!");
        System.console().readLine();


    }
}
