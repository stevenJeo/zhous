/**
 * 
 */
package org.base.framework.utils;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.PropertyDefiner;
import ch.qos.logback.core.status.Status;

import java.lang.management.ManagementFactory;

/**
 * 给logback文件名添加pid
 * 如下配置（在logback.xml）
 * 
 * 	<define name="PID" class="org.owens.framework.utils.LogbackPropertyDefiner" />
 *   <!-- 按照每天生成日志文件 -->   
 *   <appender name="FILE"  
 *       class="ch.qos.logback.core.rolling.RollingFileAppender">   
 *       <Encoding>UTF-8</Encoding>   
 *       <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">   
 *           <FileNamePattern>${LOG_HOME}/myApp.log.%d{yyyy-MM-dd}-${PID}.log</FileNamePattern>   
 *           <MaxHistory>30</MaxHistory>   
 *       </rollingPolicy>   
 *       <layout class="ch.qos.logback.classic.PatternLayout">   
 *           <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n   
 *           </pattern>   
 *      </layout>   
 *   </appender>   
 */
public class LogbackPropertyDefiner implements PropertyDefiner {

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addError(java.lang.String)
	 */
	@Override
	public void addError(String arg0) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addError(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void addError(String arg0, Throwable arg1) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addInfo(java.lang.String)
	 */
	@Override
	public void addInfo(String arg0) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addInfo(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void addInfo(String arg0, Throwable arg1) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addStatus(ch.qos.logback.core.status.Status)
	 */
	@Override
	public void addStatus(Status arg0) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addWarn(java.lang.String)
	 */
	@Override
	public void addWarn(String arg0) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#addWarn(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void addWarn(String arg0, Throwable arg1) {
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#getContext()
	 */
	@Override
	public Context getContext() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.ContextAware#setContext(ch.qos.logback.core.Context)
	 */
	@Override
	public void setContext(Context arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ch.qos.logback.core.spi.PropertyDefiner#getPropertyValue()
	 */
	@Override
	public String getPropertyValue() {
		String name = ManagementFactory.getRuntimeMXBean().getName();    
		// get pid    
		String pid = name.split("@")[0];    
		System.out.println("current java pid is:" + pid); 
		return pid;
	}

}
