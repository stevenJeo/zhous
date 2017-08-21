package org.base.framework.jms.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.base.framework.chain.HandlerChain;
import org.base.framework.context.SpringContextHelper;
import org.base.framework.jms.JmsTaskThread;
import org.base.framework.task.TaskThread;
import org.base.framework.task.TaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class Server {
    private final static Logger log = LoggerFactory.getLogger(Server.class);

    private static TaskThreadPool pool;

    public static void main(String[] args) throws Exception {

//		String dirPath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
//		String log4jProperties=dirPath+"log4j.properties";
//		//PropertyConfigurator.configure (log4jProperties);
//
        if (args.length < 2) {
            System.out.print("java -Djava.ext.dirs=dirPath mainClass xmlcfg poolSize logxmlcfg\n");
            System.out.print("java -Djava.ext.dirs=. org.owens.framework.jms.server.Server \"server-basic.xml,server-xdemomq.xml\" 200");
            return;
        }


        String logXmlCfg = null;
        if (args.length == 2) logXmlCfg = "logback.xml";
        else logXmlCfg = args[2];
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        try {
            joranConfigurator.doConfigure(logXmlCfg);
        } catch (Exception e) {
            //log.error("logback initialization error",e);
            e.printStackTrace();
        }

        if (log.isDebugEnabled()) log.debug("backend env setup begin...logXmlCfg=" + logXmlCfg);


        String[] xmlCfg = args[0].split(",");
        if (xmlCfg == null || xmlCfg.length == 0) xmlCfg = new String[]{"server_data-applicationContext.xml"};
        if (log.isDebugEnabled()) {
            log.debug("xml configuration file:");
            for (int i = 0; i < xmlCfg.length; i++) log.debug(xmlCfg[i]);
        }
        SpringContextHelper.init(xmlCfg);

        log.debug("backend env setup end");

        pool = TaskThreadPool.getInstance();
        pool.init(Integer.parseInt(args[1]), JmsTaskThread.class);
        if (log.isDebugEnabled()) log.debug("poolSize=" + args[1]);


        //start coordinator

        HandlerChain backendChain = (HandlerChain) SpringContextHelper.getBean("backendHandlerChain");

        //startBackendChain(backendChain);
        TaskThread tt = pool.getTaskThread();//分配线程1
        tt.setThreadPriority(Thread.NORM_PRIORITY + 2);
        Map<String, Object> taskArgs = tt.getArgs();
        taskArgs.clear();
        taskArgs.put(BackendHandler.cursorKey, "2");
        taskArgs.put(BackendHandler.cursorStepKey, "1");
        if (!tt.addTask(backendChain))
            log.error("cannot start backendHandlerChain!");
        else if (log.isInfoEnabled()) log.info("start backendHandlerChain.1:next=2,cursor=1,args={}", taskArgs);

        tt = pool.getTaskThread();//分配线程2
        tt.setThreadPriority(Thread.NORM_PRIORITY + 2);
        taskArgs = tt.getArgs();
        taskArgs.clear();
        taskArgs.put(BackendHandler.cursorKey, "3");
        taskArgs.put(BackendHandler.cursorStepKey, "1");
        if (!tt.addTask(backendChain))
            log.error("cannot start backendHandlerChain!");
        else if (log.isInfoEnabled()) log.info("start backendHandlerChain.1:next=2,cursor=1,args={}", taskArgs);


        if (log.isInfoEnabled()) log.info("main runs sleep loop ");
        String line = null;
        while (true) {
            //System.console().writer().write("input=");
            //line=System.console().readLine();
//			if(line.indexOf("exit")>-1){
//				//backendHandler.stop();
//				//stop all?
//				break;
//			}

            Thread.currentThread().sleep(100000);

        }

    }
}
