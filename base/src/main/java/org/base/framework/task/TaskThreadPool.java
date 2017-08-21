package org.base.framework.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 目前只是支持单一分配者，多个分配者，存在问题。
 * <p>
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TaskThreadPool {

    private final Logger logger = LoggerFactory.getLogger(TaskThreadPool.class);

    private TaskThread[] pool;

//    private int p = 0;

    private static TaskThreadPool taskPool = null;

    private TaskThreadPool() {
    }


    /**
     * 只能单线程初始化
     *
     * @return
     */
    public static TaskThreadPool getInstance() {

        if (taskPool == null) {
            synchronized (taskPool) {
                if (taskPool == null)
                    taskPool = new TaskThreadPool();
            }
        }


        return taskPool;
    }

    public int getSize() {
        return pool.length;
    }


    /**
     * @param taskCount init task pool count
     */
    public void init(int taskCount, Class clazz) {
        if (pool != null) {
            logger.info("duplicated initiation of task thread pool,buffer remains " + taskCount);
            return;
        }
        pool = new TaskThread[taskCount];


        try {
            for (int i = 0; i < taskCount; i++) {
                TaskThread taskThread = (TaskThread) clazz.newInstance();//Class.forName("GG").newInstance();//new TaskThread();
                Thread thread = new Thread(taskThread);
                thread.setName("taskThread-" + i);
                taskThread.setThred(thread);
                taskThread.setId(i);

                pool[i] = taskThread;
                thread.start();
            }
        } catch (Exception e) {
            logger.error("taskInit", e);
        }
    }

    /**
     * @param taskCount init task pool count
     */
    public void init(int taskCount, String clazz) {
        try {
            init(taskCount, Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            logger.error("taskInit", e);
        }
    }


    public TaskThread getTaskThread() {
        return getTaskThread(0, 1);
    }

    public void stop() {
        for (TaskThread tt : pool) {
            tt.softStop();
        }
    }

    /**
     * @return
     */
    public TaskThread getTaskThread(int cursor, int step) {
        for (int i = cursor; i < pool.length; i = i + step)
            if (!pool[i].isBusy()) return pool[i];

        return null;
    }
}
