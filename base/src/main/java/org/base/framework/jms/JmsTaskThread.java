package org.base.framework.jms;

import org.base.framework.task.TaskThread;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class JmsTaskThread extends TaskThread {

    /**
     *
     */
    public JmsTaskThread() {
        super();
    }


    public void initTaskArgs() {
        super.getArgs().clear();
    }
}
