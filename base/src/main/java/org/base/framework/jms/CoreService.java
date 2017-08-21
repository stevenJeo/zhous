package org.base.framework.jms;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public interface CoreService {
    public final static String uprightSlash = "|";
    public final static String uprightSlashRegexp = "\\|";


    public void send(JmsMessage message) throws Exception;

    public void send(String app, String service, String version, final String textMessage, final String replyId) throws Exception;

    public String waitReply(JmsMessage message, int delaySecond) throws Exception;

    public String waitReply(String app, String service, String version, final String textMessage, int delaySecond) throws Exception;

    public void publish(JmsMessage message) throws Exception;

    public void publish(String app, String service, String Version, final String textMessager) throws Exception;
}
