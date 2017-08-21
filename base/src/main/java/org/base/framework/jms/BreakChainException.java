package org.base.framework.jms;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class BreakChainException extends JmsException {

    /**
     *
     */
    public BreakChainException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public BreakChainException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public BreakChainException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public BreakChainException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
