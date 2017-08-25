/**
 * 
 */
package com.zzs.zhous.node.core;


/**
 * @author Administrator
 *
 */
public class Request {

	public static final String  NullReplyId="nullReplyIdString";
	
	/**
	 * 消息Id
	 */
	private long id;
	/**
	 * 表示应用
	 * 
	 */
	private String app;
	
	/**
	 * 服务的名称，一般按业务逻辑的方法名来定义，比如getUser,addUser
	 * 
	 * @see app
	 * 
	 */
	private String service;
	
	/**
	 * 服务的版本，比如v1，v2
	 * 
	 * @see app
	 * 
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
	private String replyId=NullReplyId;
	/**
	 * 结果的json字符串，可以直接返回给前台
	 * 框架使用
	 */
	private String result;
	
	
	/**
	 * 构造函数
	 */
	public Request() {}



	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the cmdVersion
	 */
	public String getVersion() {
		return version;
	}



	/**
	 * @return the app
	 */
	public String getApp() {
		return app;
	}



	/**
	 * @param app the app to set
	 */
	public void setApp(String app) {
		this.app = app;
	}



	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}



	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}



	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}



	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}



	/**
	 * @return the replyId
	 */
	public String getReplyId() {
		return replyId;
	}



	/**
	 * @param replyId the replyId to set
	 */
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}



	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}



	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}



	/**
	 * @param cmdVersion the cmdVersion to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
}
