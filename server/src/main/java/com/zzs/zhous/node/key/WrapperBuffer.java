/**
 *
 */
package com.zzs.zhous.node.key;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class WrapperBuffer<T> {  ////TODO 采用的BlockingQ等方式如何?
    private final static Logger log = LoggerFactory.getLogger(WrapperBuffer.class);

    //private AtomicInteger lock;

    public WrapperBuffer() {
        //lock=new AtomicInteger(0);
        list = new ArrayList<Wrapper<T>>();
    }

    public WrapperBuffer(int maxLength) {
        //lock=new AtomicInteger(0);
        this.maxLength = maxLength;
        list = new ArrayList<Wrapper<T>>();
    }


    //private int curLength=0;

    private int maxLength = 100;

    private List<Wrapper<T>> list;  ////TODO 数组的开销应该小,但是考虑到动态增加,所以...


//	public boolean add(Wrapper<T> wrap){
//		return add(wrap,false);
//	}

    /**
     * 添加一个包装对象。默认对象状态为空闲。
     *
     * @param wrap
     * @return
     */
    public boolean add(Wrapper<T> wrap) {
        //if(curLength>=maxLength) return false;
        if (list.size() >= maxLength) return false;
        //wrap.compareAndSet(false, isBusy);
        synchronized (this) { ////TODO 改成并发list是不是能快些?
            //if(curLength>=maxLength) return false;
            if (list.size() >= maxLength) return false;
            list.add(wrap);
            //curLength++;
            if (log.isDebugEnabled()) log.debug("this.id=" + this + "add wrap,curLength=" + list.size());
        }
        return true;
    }


    /**
     * 获取一个空闲的wrap对象。
     * 算法：
     * 多线程随机获得一个指针,
     * 判断所指指对象忙，+1继续尝试，直到遇到一个wrap对象，
     * 或者超过curLength则放弃，返回空。
     *
     * @return
     */
    public Wrapper<T> getFree() {
        int p = RandomUtils.nextInt(list.size());//curLength);//(int)(Math.random()*curLength);
        int c = 0;
        while (!list.get(p).compareAndSet(false, true)) {
            //p++;
            p = RandomUtils.nextInt(list.size());//(int)(Math.random()*curLength);
            //if(p>=curLength)p=0;
            c++;
            if (c > 10) {
                c = -1;
                break;
            }
        }

        if (c == -1)
            return null;
        else
            return list.get(p);
    }

    public boolean ifFull() {
        return list.size() >= maxLength;
    }

    public String statistics() {
        StringBuffer buf = new StringBuffer("wrapperBuf=").append(list);
        for (int i = 0; i < list.size(); i++) {
            buf.append("-" + list.get(i).isBusy() + list.get(i).getCount());
        }
        return buf.toString();
    }


    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Wrapper<T> w : list) buf.append("\n").append(w.getT().toString());

        return buf.toString();
    }


    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        if (maxLength < list.size())
            this.maxLength = list.size();
        else
            this.maxLength = maxLength;
    }

    public int getCurLength() {
        return list.size();
    }


}
