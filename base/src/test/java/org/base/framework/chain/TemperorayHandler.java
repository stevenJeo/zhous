package org.base.framework.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class TemperorayHandler implements Handler {

    private final Logger log = LoggerFactory.getLogger(TemperorayHandler.class);


    /**
     *
     */
    public TemperorayHandler() {
        // TODO Auto-generated constructor stub
    }



    public void exceute(Map args) throws Exception {
        log.debug("handler="+TemperorayHandler.class.getName());

    }
}
