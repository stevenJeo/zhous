package org.base.framework.jms;

import org.base.framework.exception.FrameworkException;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class JmsCoreServiceException extends FrameworkException {

    /**
     *
     */
    public JmsCoreServiceException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public JmsCoreServiceException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public JmsCoreServiceException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public JmsCoreServiceException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
