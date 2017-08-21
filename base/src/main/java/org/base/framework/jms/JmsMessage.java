package org.base.framework.jms;

import lombok.Data;

/**
 * 前后台之间交互的基类，
 * 应用需要定义一个消息类继承该类。扩展属性表示请求参数。
 * <br>后台默认返回的结果jsonString（直接传输给页面）。
 * <br>需要返回特殊结果，也扩展属性表示（此时result=null,后台返回该类的序列化字符串，应用自行解释，比如反系列化成对象）
 * <p>
 * Created by zhishuai.zhou on 2017/8/21.
 */
@Data
public class JmsMessage {

    public static final String NullReplyId = "nullReplyIdString";
    public static final String IsException = "isExceptionMessage";

    /**
     * 消息Id
     */
    private long id;
    /**
     * 表示应用，和service，version一起，决定
     * 消息往的Q服务器和队列名
     * 消息在服务端的处理对象，比如chain的key
     */
    private String app;
    /**
     * 服务的名称，一般按业务逻辑的方法名来定义，比如getUser,addUser
     */
    private String service;
    /**
     * 服务的版本，比如v1，v2
     */
    private String version;
    /**
     * 签名字符串，用来验证参数的篡改
     */
    private String sign;
    /**
     * 返回消息的id，用以处理后台返回结果和请求的对应。
     * 框架使用
     */
    private String replyId = NullReplyId;
    /**
     * 结果的json字符串，可以直接返回给前台
     * 框架使用
     */
    private String result;


}
