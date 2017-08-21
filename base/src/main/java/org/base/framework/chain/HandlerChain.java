package org.base.framework.chain;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public interface HandlerChain {

    public void run(Map<String, Object> args) throws Exception;

}
