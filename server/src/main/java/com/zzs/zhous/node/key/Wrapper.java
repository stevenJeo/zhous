/**
 *
 */
package com.zzs.zhous.node.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Administrator
 */
public class Wrapper<T> {
    private final static Logger logger = LoggerFactory.getLogger(Wrapper.class);

    //private boolean busy=false;
    private int count = 0;
    private String comment;
    //private String[] log=new String[10];

    private T t;
    private final AtomicBoolean busy;


    public Wrapper(T t, boolean isBusy) {
        this.t = t;
        busy = new AtomicBoolean(isBusy);
    }

    public boolean isBusy() {
        return busy.get();
    }

    public boolean compareAndSet(boolean expect, boolean update) {
        return busy.compareAndSet(expect, update);
    }

    public int getCount() {
        return count;
    }

    public T getT() {
        count++;
        return t;
    }

    /**
     * 释放wrap对象。
     * 同时计数被使用的次数。
     */
    public void free() {
        while (!busy.compareAndSet(true, false))
            logger.error(" multiThread set wrapper free ");
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

}
