package org.base.framework.jms;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public interface Service {
    public final static String underline = "_";
    public final static String chainPostfix = "_chain";

    String callService(JmsMessage message, int delaySec) throws Exception;

    String callService(String app, String service, String version, final String textMessage, int delaySecond) throws Exception;
}
