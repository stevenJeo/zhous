package org.base.framework.jms;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class ExceptionMessage extends JmsMessage {
    private boolean isException = false;

    public boolean isException() {
        return isException;
    }

    public void setException(boolean isException) {
        this.isException = isException;
    }

}
