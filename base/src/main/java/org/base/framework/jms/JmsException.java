package org.base.framework.jms;

import org.base.framework.exception.FrameworkException;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class JmsException extends FrameworkException {

    /**
     *
     */
    public JmsException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public JmsException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public JmsException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public JmsException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
