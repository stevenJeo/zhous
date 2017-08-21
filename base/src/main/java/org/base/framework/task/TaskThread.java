package org.base.framework.task;

import org.base.framework.chain.HandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TaskThread implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(TaskThread.class);
    /**
     * 判断线程是否在执行
     */
    private boolean amIRunning = false;

    //private Object jsonTask = null;

    private HandlerChain handlerChain;

    private Thread thread;

    private int id;

    private Object locker = new Object();


    public TaskThread() {
    }

    private boolean stopFlag = false;


    public void setThred(Thread thread) {
        this.thread = thread;
    }

    public void setThreadPriority(int priotity) {
        thread.setPriority(priotity);
    }

    public void softStop() {
        this.stopFlag = true;
    }


    private Map<String, Object> args = new HashMap<String, Object>();

    /**
     * @return the args
     */
    public Map<String, Object> getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }


    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {
        while (!stopFlag) {
            try {
                synchronized (locker) {
                    amIRunning = false;
                    if (logger.isDebugEnabled()) logger.debug("Thread sleep ..");
                    locker.wait();
                    if (logger.isDebugEnabled()) logger.debug("Thread wake up ..");
                    amIRunning = true;
                }
//                bizRun(handlerChain, args);
                handlerChain.run(args);
                args.clear();


            } catch (Exception e) {
                logger.error("taskThread error", e);
                args.clear();
            }

        }
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //public abstract void bizRun(HandlerChain chain,Map<String, Object> task)throws Exception;

    public boolean isBusy() {
        return amIRunning;
    }

//	/**
//	 * @param obj
//	 */
//	public boolean addTask(Object jsonTask,HandlerChain taskHandlerChain)
//	{
//		boolean retv=false;
//		if(logger.isDebugEnabled())logger.debug("send task to Thread ");
//		synchronized (locker){
//			if(!amIRunning){
//				//this.jsonTask=jsonTask;
//				this.handlerChain = taskHandlerChain;
//				locker.notifyAll();
//				if(logger.isDebugEnabled())logger.debug("wake up Thread ");
//				retv=true;
//			}
//		}
//
//		return retv;
//	}

    /**
     * @param taskHandlerChain
     */
    public boolean addTask(HandlerChain taskHandlerChain) {
        boolean retv = false;
        if (logger.isDebugEnabled()) logger.debug("send task to Thread ");
        synchronized (locker) {
            if (!amIRunning) {
                //this.jsonTask=jsonTask;
                amIRunning = true;
                this.handlerChain = taskHandlerChain;
                locker.notifyAll();
                if (logger.isDebugEnabled()) logger.debug("wake up Thread ");
                retv = true;
            }
        }

        return retv;
    }
}
