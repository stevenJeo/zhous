package com.zzs.zhous.node.key;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public interface KeyUpdaterExceutor {
    public Object run(Map<String,Object> args);

    public void stop();
}
