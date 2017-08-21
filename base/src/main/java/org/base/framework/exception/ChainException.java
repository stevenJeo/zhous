package org.base.framework.exception;

import org.base.framework.exception.FrameworkException;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class ChainException extends FrameworkException {

    /**
     *
     */
    public ChainException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ChainException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ChainException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ChainException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}
