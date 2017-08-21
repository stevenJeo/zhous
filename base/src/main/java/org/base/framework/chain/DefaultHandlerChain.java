package org.base.framework.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class DefaultHandlerChain {

    private final static Logger log = LoggerFactory.getLogger(DefaultHandlerChain.class);

    private List<Handler> handlers;

    public List<Handler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler> handlers) {
        this.handlers = handlers;
    }

//	private Map<String, Object> args=new HashMap<String,Object>();

//	/**
//	 * @return the args
//	 */
//	public Map<String, Object> getArgs() {
//		return args;
//	}
//
//	/**
//	 * @param args the args to set
//	 */
//	public void setArgs(Map<String, Object> args) {
//		this.args = args;
//	}

    /**
     *
     */
    public DefaultHandlerChain() {
        //handlers=new ArrayList<Handler>();
    }

    /* (non-Javadoc)
     * @see org.mmo.framework.Handler#run(java.util.Map)
     */
    public void run(Map<String, Object> args) throws Exception {
        for (int i = 0; i < handlers.size(); i++) {
            Handler handler = handlers.get(i);
            if (log.isDebugEnabled()) log.debug("handler no." + i + " begin");
            handler.exceute(args);
            if (log.isDebugEnabled()) log.debug("handler no." + i + " end");
        }
    }

}
