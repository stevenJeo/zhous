package com.zzs.zhous.node.key;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public class Key {
    public enum Type{AES,RSA,DES};

    public final static String separator="_";

    public static long timeToUpdate=600000;



    public boolean isDebug(){
        return true;
    }



    private Type type;


    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }


}
