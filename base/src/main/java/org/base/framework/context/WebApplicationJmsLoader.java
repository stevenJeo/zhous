package org.base.framework.context;

import org.base.framework.chain.HandlerChain;
import org.base.framework.jms.JmsTaskThread;
import org.base.framework.jms.ObjectWait;
import org.base.framework.task.TaskThread;
import org.base.framework.task.TaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;


/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class WebApplicationJmsLoader implements ServletContextAware, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(WebApplicationJmsLoader.class);

    private ApplicationContext context;
    /**
     * number of Receiver which receive reply from backend
     */
    private int numberofReceiver;
    /**
     * the buffer Size which keep request waitting for reply from backend.
     */
    private int bufferSize;


    private HandlerChain receiveChain;

    private int receiveChainThreadPriority = Thread.NORM_PRIORITY + 2;


    public void setNumberofReceiver(int numberofReceiver) {
        this.numberofReceiver = numberofReceiver;
    }


    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }


    public void setReceiveChain(HandlerChain receiveChain) {
        this.receiveChain = receiveChain;
    }

    public void setReceiveChainThreadPriority(int priority) {
        this.receiveChainThreadPriority = priority;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.context.ServletContextAwar
     *      e#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext) {
        //context=WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        if (log.isInfoEnabled()) log.info("load jms_mq components....");

        ObjectWait ow = ObjectWait.getInstance();
        ow.init(bufferSize);
        if (log.isDebugEnabled()) log.debug("ObjectWait buffer=" + bufferSize);

        TaskThreadPool pool = TaskThreadPool.getInstance();
        pool.init(numberofReceiver, JmsTaskThread.class);
        if (log.isDebugEnabled()) log.debug("TaskThreadPool size=" + numberofReceiver);

        try {
            Thread.currentThread().sleep(1000);

            for (int i = 0; i < numberofReceiver; i++) {
                TaskThread tt = pool.getTaskThread();
                //这里不初始化map,在收到reply后,会初始化.
                tt.addTask(receiveChain);
                tt.setThreadPriority(receiveChainThreadPriority);
                if (log.isDebugEnabled()) log.debug("start reply receiver" + tt.toString());
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            log.error("setServletContext", e);
        }
        if (log.isInfoEnabled()) log.info("load jms_mq components done!");
    }


    @Override
    public void destroy() throws Exception {
        TaskThreadPool pool = TaskThreadPool.getInstance();
        pool.stop();
        log.warn("stop rsaKeyUpdater...DisposableBean");
    }

}
