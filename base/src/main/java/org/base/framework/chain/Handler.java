package org.base.framework.chain;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public interface Handler {
    public void exceute(Map<String, Object> args) throws Exception;
}
