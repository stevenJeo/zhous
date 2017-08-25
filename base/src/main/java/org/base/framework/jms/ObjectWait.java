package org.base.framework.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class ObjectWait {

    private final static Logger logger = LoggerFactory.getLogger(ObjectWait.class);

    private AtomicInteger lock;

    //private Object[] objBuf;

    //private Integer key=0;

    private int maxKey;

    public static int MIN_BUFFER_LENGTH = 10000;

    private static ObjectWait objectWait = null;

    private ObjectWait() {
    }

    public static ObjectWait getInstance() {
        if (objectWait == null) objectWait = new ObjectWait();

        return objectWait;
    }

    public void init(int tBufferLength) {
//			if(objBuf!=null) {
//				logger.info("duplicated initiation of ObjectWait,buffer remains " +maxKey );
//				return;
//			}

        lock = new AtomicInteger(0);
        maxKey = tBufferLength;
//			objBuf=new Object[tBufferLength];
        mapBuf = new HashMap<String, InvokerObject>(tBufferLength);

    }

//		public int getKey(Object t){
//			int old=-1;
//			boolean sucessfull=false;
//			while(!sucessfull){
//				old=lock.get();
//				int newV=old+1;
//				if(newV>=maxKey)newV=0;
//				sucessfull=lock.compareAndSet(old, newV);
//			}
//
//	        if(objBuf[old]!=null) logger.error("ObjectWait buffer is full,current buffer = " +maxKey );
//	        objBuf[old]=t;
//			return old;
//		}


    private Map<String, InvokerObject> mapBuf;


//		public int getKey(Object t){
//			String key=""+Thread.currentThread().getId();
//			//String key=Thread.currentThread().getName();
//	        if(mapBuf.get(key)!=null) logger.error("getKey :mapBuf.get(key)!=null key=" +key ); //testonly
//			mapBuf.put(key, t);
//
//			return (int)Thread.currentThread().getId();
//		}

    public InvokerObject getKey(Object t) {
        String key = Thread.currentThread().getName();
        InvokerObject iobj = mapBuf.get(key);

        if (iobj == null) {
            iobj = new InvokerObject();
            //iobj.setReqMessage((String)t);
            mapBuf.put(key, iobj);
        }

        if (iobj.getReqMessage() != null) logger.error("getKey :iobj.getReqMessage()!=null key=" + key); //testonly
        iobj.setReqMessage((String) t);
        iobj.setResMessage(key);
        iobj.setException(false);

        return iobj;
    }


//		public Object getObject(int key){
//			Object t=objBuf[key];
//			if(objBuf[key]!=null){
//				objBuf[key]=null;
//			}else{
//
//				 logger.error("null for key="+ key);
//			}
//
//			return t;
//		}


//		public Object getObject(int key){
//			String key1=""+key;
//			//String key1=""+Thread.currentThread().getId();
//	        if(mapBuf.get(key1)==null) logger.error("getObject:mapBuf.get(key)==null key=" +key1 );
//	        //if(Thread.currentThread().getId()!=key)logger.error("getObject:Thread.currentThread().getId()!=key " +key +" id="+Thread.currentThread().getId());
//
//	        Object t=mapBuf.get(key1);
//	        mapBuf.put(key1, null); //test only
//
//	        return t;
//		}

    public InvokerObject getObject(String key) {
        InvokerObject iobj = mapBuf.get(key);
        if (iobj == null || !iobj.getResMessage().equals(key)) {
            logger.error("getObject :iobj==null || !iobj.getResMessage().equals(key) key=" + key);
            return null;
        }
        iobj.setReqMessage(null); //testonly
        return iobj;
    }


//		public Object[] getArray(){
//			return objBuf;
//		}


}
