package com.zzs.zhous.node.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class KeyCipher {

    private final static Logger log = LoggerFactory.getLogger(KeyCipher.class);

    /**
     *
     */
    public KeyCipher() {}

    private Cipher workCipher;
    private Cipher oldCipher;
    private Object lockObject=new Object();

    private String workCipherKey;
    private String oldCipherKey;




    private String wString;
    private String bString;
    private List<String> cList=new ArrayList<String>();

    public void addNewCipher(Cipher cipher,String cipherKey,String aesKey){
        synchronized (lockObject){
            if(workCipher==null) {
                workCipher=cipher;
                workCipherKey=cipherKey;
                wString=aesKey;

                cList.add(cipherKey+"-"+cipher+"-"+aesKey);
                if(log.isDebugEnabled())log.debug("set "+workCipher+" as workCipher-key="+workCipherKey);
            }else if(workCipherKey.compareTo(cipherKey)<0){
                oldCipher=null; //release
                oldCipherKey=null;
                oldCipher=workCipher;
                oldCipherKey=workCipherKey;
                workCipher=cipher;
                workCipherKey=cipherKey;

                cList.add(cipherKey+"-"+cipher+"-"+aesKey);
                if(log.isDebugEnabled())log.debug("exchange workCipher to backupCipher: workCipher"+workCipher+"-key="+workCipherKey+", backupCipher"+oldCipher+"-key="+oldCipherKey);
            }else if(oldCipher==null || oldCipherKey.compareTo(cipherKey)<0){
                //因为是动态增加的,
                //会出现这种情况:有些keyCipher是直接用新的ID生成的,这时b=null
                //而碰巧,远端用了bk来访问时,需要增加b
                if(log.isDebugEnabled())log.debug("release "+oldCipher+"-key="+oldCipherKey+", set "+cipher+" as backup cipher-key="+cipherKey);
                oldCipher=null; //release
                oldCipherKey=null;
                oldCipher=cipher;
                oldCipherKey=cipherKey;
                bString=aesKey;

                cList.add(cipherKey+"-"+cipher+"-"+aesKey);
            }else{
                if(log.isDebugEnabled())log.debug("add duplicated workCipher,may be due to multithreading request ");
            }
        }
    }

//	public Cipher getCipher(){
//		return workCipher;
//	}

    public Cipher getCipher(String cipherKey){
        if(cipherKey==null)return workCipher;
        if(workCipherKey.equals(cipherKey)) return workCipher;
        if(oldCipherKey!=null && oldCipherKey.equals(cipherKey)) return oldCipher;

        if(log.isInfoEnabled())log.info("cannot find cipher for key="+cipherKey);
        return null;
    }

    public String getHistroy(){
        StringBuffer buf=new StringBuffer();
        for(String cString:cList) buf.append(cString).append("|");
        buf.append("|");
        return buf.toString();

    }


    public String toString(){
        StringBuffer buf=new StringBuffer();
        buf.append(super.toString()).append("-\n\tW-").append(workCipher).append("-").append(workCipherKey);
        buf.append("-\n\tB-").append(oldCipher).append("-").append(oldCipherKey);

        buf.append("\n\thistroy:");
        for(String cString:cList) buf.append("\n\t").append(cString);
        return buf.toString();
    }
}
