package org.base.framework.jms;

import java.io.Serializable;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class InvokerObject implements Serializable {

    private String reqMessage;

    private String resMessage;

    private boolean isException=false;

    public String getReqMessage()
    {
        return reqMessage;
    }
    public void setReqMessage(String reqMessage)
    {
        this.reqMessage = reqMessage;
    }

    public String getResMessage()
    {
        return resMessage;
    }

    public void setResMessage(String resMessage)
    {
        this.resMessage = resMessage;
    }

    public boolean isException() {
        return isException;
    }
    public void setException(boolean isException) {
        this.isException = isException;
    }


}
