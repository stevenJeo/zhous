package com.zzs.zhous.node.key;

/**
 * Created by zhishuai.zhou on 2017/8/21.
 */
public interface CipherUtils {

    public void addDES(String userId,String appId,String session);
    public void addDES(String userId,String appId,String keyString,String session,String time);
    public Key getKeyProperties(String mapKey);

    public Wrapper<KeyCipher> getCipherWrapper(String mapkey,String key) throws Exception;

    public String encryptByDES(String plainText,String mapKey,String keyId);
    public String encryptByDES(String plainText,String mapKey,String keyId,int times);
    public String decryptByDES(String encryptedText,String mapKey,String keyId);
    public String decryptByDES(String encryptedText,String mapKey,String keyId,int times);

    public String encryptByPublicKey(String plainText,String publicMapKey,String publicKey);
    public String encryptByPublicKey(String plainText,String publicMapKey,String publicKey,int times);
    public String decryptByPrivateKey(String encrypted,String privateMapKey,String privateKey);
    public String decryptByPrivateKey(String encrypted,String privateMapKey,String privateKey,int times);


    public WrapperBuffer<KeyCipher> getWrapperBuffer(String mapKey);
    public void setWorkKeyUpdaterExceutor(KeyUpdaterExceutor workKeyUpdaterExceutor);



    public void stop();
    public void init(String path,int interval) throws Exception;

}
