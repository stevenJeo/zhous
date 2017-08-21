package com.zzs.zhous.node.core;

import lombok.Data;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
@Data
public class Request {

    public static final String NullReplyId = "nullReplyIdString";

    /**
     * 消息Id
     */
    private long id;
    /**
     * 表示应用
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
