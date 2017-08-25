/**
 *
 */
package com.zzs.zhous.node.key;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Administrator
 */
public class RSAUtils {
    private final static Logger logger = LoggerFactory.getLogger(RSAUtils.class);


    private final String corePrivateKey = "000_000_privatekey";
    private final int inputSizeLimit_1024 = 116;
    private final int inputSizeLimit_2048 = 245; //有待测试
    private final int defaultTimes = 3;
    private final int maxWrapperBuffer = 100;

    private String alogrithm = "RSA";
    private String keyPath;//"src/test/java/key";
    private String keyStorePwd = "123456";
    private String sysAlias = "ss";
    private String sysAliasPwd = "123456";
    private long keyTimeToUpdate = 24 * 60 * 60 * 1000; //1day;
    private int rsaRefreshInteral = 60 * 60 * 1000; // 1 hour

    private KeyUpdater rsaKeyUpdaterExceutor;
    private KeyUpdater workKeyUpdater;
    private KeyUpdaterExceutor workKeyUpdaterExceutor;
    private Map<String, WrapperBuffer<KeyCipher>> wrapBufferMap = new HashMap<String, WrapperBuffer<KeyCipher>>();
    private KeyMap<Key> keyMap;
    private MessageDigest md;
    private Provider provider;
    private static RSAUtils rsaUtils;


    public KeyMap<Key> getKeyMap() {
        return keyMap;
    }

    public static RSAUtils getInstance() {
        if (rsaUtils != null) return rsaUtils;
        synchronized (logger) {
            if (rsaUtils == null) {
                rsaUtils = new RSAUtils();
            }
        }
        return rsaUtils;
    }

    public void addDES(String userId, String appId, String session) { // throws Exception{
        String keyString = KeyDES.createDesKey();
        String time = "" + System.currentTimeMillis();
        addDES(userId, appId, keyString, session, time);
    }

    public void addDES(String userId, String appId, String keyString, String session, String time) {
        String encryptMapKey = KeyDES.getMapKey(userId, appId, Cipher.ENCRYPT_MODE);
        KeyDES keydes = (KeyDES) keyMap.get(encryptMapKey);

        if (keydes == null) {
            synchronized (keyMap) {
                if (keyMap.get(encryptMapKey) == null) {
                    String decryptMapKey = KeyDES.getMapKey(userId, appId, Cipher.DECRYPT_MODE);
                    KeyDES keyDes_d = new KeyDES(userId, appId, keyString, session, Cipher.DECRYPT_MODE, time);
                    keyMap.add(decryptMapKey, keyDes_d);

                    if (logger.isInfoEnabled())
                        logger.info(" add decrypt KeyDES, userId={}, appId={}, keyString={} keyId={}", userId, appId, keyString, time);

                    keydes = new KeyDES(userId, appId, keyString, session, Cipher.ENCRYPT_MODE, time);
                    keyMap.add(encryptMapKey, keydes);
                    if (logger.isInfoEnabled())
                        logger.info(" add encrypt KeyDES, userId={}, appId={}, keyString={}, keyId={}", userId, appId, keyString, time);

                }
            }
        } else {
            String curKey = keydes.getKeyId(true);
            if (!time.equals(curKey)) { //keydes.isTimeToUpdate()){ //maybe other thread has been update new aesKeyString
                synchronized (keyMap) {
                    curKey = keydes.getKeyId(true);
                    if (!time.equals(curKey)) { //check again
                        String decryptMapKey = KeyDES.getMapKey(userId, appId, Cipher.DECRYPT_MODE);
                        KeyDES keyDes = (KeyDES) keyMap.get(decryptMapKey);
                        keyDes.add(keyString, time);
                        if (logger.isInfoEnabled())
                            logger.info(" update decrypt addDES,keyString={} keyId={}", keyString, time);

                        keydes.add(keyString, time);
                        if (logger.isInfoEnabled())
                            logger.info(" update encrypt addDES,keyString={} keyId={}", keyString, time);
                    }
                }
            }
        }
    }

    public void addAES(String userId, String appId, String keyString, String session, String time) {
        String encryptMapKey = KeyAES.getMapKey(userId, appId, Cipher.ENCRYPT_MODE);
        KeyAES keyAes = (KeyAES) keyMap.get(encryptMapKey);
        if (keyAes == null) {
            synchronized (keyMap) {
                if (keyMap.get(encryptMapKey) == null) {
                    String decryptMapKey = KeyAES.getMapKey(userId, appId, Cipher.DECRYPT_MODE);
                    KeyAES keyAes_d = new KeyAES(userId, appId, keyString, session, Cipher.DECRYPT_MODE, time);
                    keyMap.add(decryptMapKey, keyAes_d);

                    if (logger.isInfoEnabled())
                        logger.info(Thread.currentThread() + " add decrypt keyAES,keyString=" + keyString + " keyId=" + time);

                    keyAes = new KeyAES(userId, appId, keyString, session, Cipher.ENCRYPT_MODE, time);
                    keyMap.add(encryptMapKey, keyAes);
                    if (logger.isInfoEnabled())
                        logger.info(Thread.currentThread() + " add encrypt keyAES,keyString=" + keyString + " keyId=" + time);

                }
            }
        } else if (keyAes.isTimeToUpdate()) { //maybe other thread has been update new aesKeyString
            synchronized (keyMap) {
                if (keyAes.isTimeToUpdate()) { //check again
                    String decryptMapKey = KeyAES.getMapKey(userId, appId, Cipher.DECRYPT_MODE);
                    KeyAES keyAes_d = (KeyAES) keyMap.get(decryptMapKey);
                    keyAes_d.add(keyString, time);
                    if (logger.isInfoEnabled())
                        logger.info(Thread.currentThread() + " update decrypt keyAES,keyString=" + keyString + " keyId=" + time);

                    keyAes.add(keyString, time);
                    if (logger.isInfoEnabled())
                        logger.info(Thread.currentThread() + " update encrypt keyAES,keyString=" + keyString + " keyId=" + time);
                }
            }
        }
    }

    public void addAES(String userId, String appId, String session) { // throws Exception{
        String keyString = KeyAES.createAesKey();
        String time = "" + System.currentTimeMillis();
        addAES(userId, appId, keyString, session, time);
    }

    public Key getKeyProperties(String mapKey) {
        return keyMap.get(mapKey);
    }

    public void stop() {
        if (rsaKeyUpdaterExceutor != null) rsaKeyUpdaterExceutor.stop();
        if (workKeyUpdaterExceutor != null) workKeyUpdaterExceutor.stop();
    }

    public void init(String path, int interval) throws Exception {
        md = MessageDigest.getInstance("MD5");
        provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();

        //aesMap=new KeyMap<KeyAES>();

        //keyMap=new KeyMap<KeyFile>();
        keyMap = new KeyMap<Key>();

        rsaKeyUpdaterExceutor = new KeyUpdater(new RsaKeyUpdaterExceutor(path, interval, keyMap, wrapBufferMap));
        rsaKeyUpdaterExceutor.start();

        //workKeyUpdaterExceutor=new KeyUpdater(new DesKeyUpdaterExceutor());
        if (workKeyUpdaterExceutor != null) {
            workKeyUpdater = new KeyUpdater(workKeyUpdaterExceutor);
            workKeyUpdater.start();
        }
    }


//	/**
//	 * @param rsaKeyUpdater the rsaKeyUpdater to set
//	 */
//	public void setRsaKeyUpdater(KeyUpdater rsaKeyUpdater) {
//		this.rsaKeyUpdaterExceutor = rsaKeyUpdater;
//	}


    private Cipher initCipher(String mapkey, String key) {
        Key.timeToUpdate = this.keyTimeToUpdate;
        Cipher cipher = null;
        FileInputStream fis = null;
        //String cipherKey=null;
        try {
            Key keyProperties = keyMap.get(mapkey);
            if (keyProperties != null) {
                if (keyProperties.getType() == Key.Type.RSA)
                    cipher = createRsaCipher((KeyFile) keyProperties, fis, key);
                else if (keyProperties.getType() == Key.Type.AES)
                    cipher = createAesCipher((KeyAES) keyProperties, key);
                else if (keyProperties.getType() == Key.Type.DES)
                    cipher = createDesCipher((KeyDES) keyProperties, key);
            }
        } catch (Exception e) {
            logger.error("load " + mapkey, e);
        } finally {
            ////TODO fis能返回?
            try {
                if (fis != null) fis.close();
            } catch (Exception e) {
                logger.error("fis.close()error:", e);
            }
        }
        return cipher;
    }

    private Cipher createAesCipher(KeyAES keyAes, String key) throws Exception {
        String aes = keyAes.getAesKey(key);
        md.update(aes.getBytes("UTF-8"));
        byte[] enCodeFormat = md.digest();
        if (enCodeFormat == null || enCodeFormat.length == 0) {
            logger.error("cannot find aesKey for " + key);
            return null;
        }

        //md.update(key.getBytes("UTF-8"));
        //byte[] enCodeFormat = md.digest();	
        SecretKeySpec skeySpec = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        if (keyAes.isEncryptMode())
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        else
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher;
    }

    private Cipher createDesCipher(KeyDES keyDes, String key) throws Exception {
        String des = keyDes.getDesKey(key);
        if (des == null) {
            //TODO 使用了过期的Key,待解决,
            //按理,10分钟才更新一次des密钥,
            //观察日志,发现都已经过去20分钟,应该不会出现这个情况...
            logger.error("使用了过期的Key,keyDes.getDesKey(key)==null key=" + key + " keyDEs=" + keyDes.toString());
            return null;
        }
//	    md.update(des.getBytes("UTF-8"));
//	    byte[] enCodeFormat = md.digest();
//		if(enCodeFormat==null || enCodeFormat.length==0){
//			logger.error("cannot find desKey for "+key);
//			return null;
//		}
//		
//        //md.update(key.getBytes("UTF-8"));
//        //byte[] enCodeFormat = md.digest();	
//        SecretKeySpec skeySpec = new SecretKeySpec(enCodeFormat , "DES");	
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");


        //http://www.cnblogs.com/yidinghe/articles/449212.html 
        //参照这篇文章,据说能和.net互通

        //Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding",new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding", provider);
        DESKeySpec desKeySpec = new DESKeySpec(des.getBytes("UTF-8"));
        //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES",new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES", provider);
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        IvParameterSpec iv = new IvParameterSpec(des.getBytes("UTF-8"));

        if (keyDes.isEncryptMode())
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        else
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        return cipher;
    }


    private Cipher createRsaCipher(KeyFile keyFile, FileInputStream fis, String key) throws Exception {
        Cipher cipher = null;
        //cipherKey=key+"_"+keyId;
        String fileName = keyFile.getFieName(key);
        if (fileName == null) {
            logger.error("cannot find fileName for " + key);
            return null;
        }
        fis = new FileInputStream(fileName);
        //cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding");//javax.crypto.IllegalBlockSizeException: Input length must be multiple of 16 when decrypting with padded cipher
        //cipher=Cipher.getInstance("RSA/ECB/PKCS1Padding",new org.bouncycastle.jce.provider.BouncyCastleProvider());
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
        if (keyFile.isPriviatedKey()) {
            //加载密钥库文件
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, keyStorePwd.toCharArray());
            //获取私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) ks.getKey(sysAlias, sysAliasPwd.toCharArray());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        } else {
            Certificate cer = CertificateFactory.getInstance("X.509").generateCertificate(fis);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) cer.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        }
        return cipher;
    }

    public Wrapper<KeyCipher> getCipherWrapper(String mapkey, String key) throws Exception {
        if (!wrapBufferMap.containsKey(mapkey)) {
            Wrapper<KeyCipher> wrapper = null;
            synchronized (wrapBufferMap) {
                if (!wrapBufferMap.containsKey(mapkey)) {
                    Cipher cipher = initCipher(mapkey, key);
                    if (cipher == null) {
                        logger.error("cannot find key=" + key + " mapkey=" + mapkey);
                        return null;
                    }
                    KeyCipher keyCipher = new KeyCipher();
//					String aesKeyLog=null;
//					Key key1=keyMap.get(mapkey);
//					if(key1 instanceof KeyAES){
//						KeyAES keyAes=(KeyAES)key1;
//						aesKeyLog=keyAes.getAesKey(key);
//					}

                    keyCipher.addNewCipher(cipher, key, null);//aesKeyLog);

                    ////TODO 默认的WrapperBuffer长度?
                    WrapperBuffer<KeyCipher> buf = new WrapperBuffer<KeyCipher>(maxWrapperBuffer);
                    wrapper = new Wrapper<KeyCipher>(keyCipher, true);
                    buf.add(wrapper);
                    wrapBufferMap.put(mapkey, buf);
                    if (logger.isDebugEnabled())
                        logger.debug(" first add\nkeyCipher=" + keyCipher + "\nwrapper=" + wrapper + "\nwrapperBuf=" + buf);//+"\n aesKeyLog="+aesKeyLog);
                    return wrapper;
                }
            }
        }

        long t1 = System.nanoTime();
        WrapperBuffer<KeyCipher> buf = wrapBufferMap.get(mapkey);
        //if(logger.isInfoEnabled())logger.info(buf.statistics());
        Wrapper<KeyCipher> wrapper = buf.getFree();
        long t2 = System.nanoTime();
        if (t2 - t1 > threshold_2 && logger.isInfoEnabled()) logger.info("buf free time=" + (t2 - t1) / secUnit);
        if (wrapper == null) {
            if (!buf.ifFull()) {
                synchronized (buf) {
                    if (logger.isDebugEnabled()) logger.debug("buf.isFull=false lock buf to add");
                    if (!buf.ifFull()) {
                        Cipher cipher1 = initCipher(mapkey, key);
                        if (cipher1 == null) {
                            logger.error(" add cipher,but cannot find key=" + key + " mapkey=" + mapkey);
                            return null;
                        }
                        KeyCipher keyCipher = new KeyCipher();
//						Key key1=keyMap.get(mapkey);
//						String aesKeyLog=null;
//						if(key1 instanceof KeyAES){
//							KeyAES keyAes=(KeyAES)key1;
//							aesKeyLog=keyAes.getAesKey(key);
//						}

                        keyCipher.addNewCipher(cipher1, key, null);//aesKeyLog);
                        Wrapper<KeyCipher> wrapper1 = new Wrapper<KeyCipher>(keyCipher, true);
                        buf.add(wrapper1);
                        if (logger.isDebugEnabled()) logger.debug(" next add wrapper" + wrapper + " wrapperBuf=" + buf);
                        //if(logger.isInfoEnabled())logger.info(" next add\nkeyCipher="+keyCipher+"\nwrapper="+wrapper1+"\nwrapperBuf="+buf+"\n aesKeyLog="+aesKeyLog);
                        return wrapper1;
                    }
                }
            }
            logger.error("cannot find free cipher for-" + mapkey + "-" + key + "\n" + buf.statistics());
            return null;
        }

        //idle cipher
        //and only current Thread can access this wrapper.
        KeyCipher keyCipher = wrapper.getT();
        Cipher cipher = keyCipher.getCipher(key);
        if (cipher == null) {
            //new cipher
            Cipher cipher1 = initCipher(mapkey, key);
            if (cipher1 == null) {
                logger.error(" add cipher,but cannot find key=" + key + " mapkey=" + mapkey);
                return null;
            }
//			Key key1=keyMap.get(mapkey);
//			String aesKeyLog=null;
//			if(key1 instanceof KeyAES){
//				KeyAES keyAes=(KeyAES)key1;
//				aesKeyLog=keyAes.getAesKey(key);
//			}

            keyCipher.addNewCipher(cipher1, key, null);//aesKeyLog);
            if (logger.isDebugEnabled())
                logger.debug(" 3rd add\nkeyCipher=" + keyCipher + "\nwrapper=" + wrapper + "\nwrapperBuf=" + buf);//+"\n aesKeyLog="+aesKeyLog);

        }

//		if(t6-t1>maxTimes1){
//			logger.error(" getCipherWrapper free wrapper but cipher=null: \tt6-t1="+(t6-t1)/timeUnit+"\tt6-t5="+(t6-t5)/timeUnit
//					+"\tt5-t4="+(t5-t4)/timeUnit
//					+"\tt4-t3="+(t4-t3)/timeUnit
//					+"\tt3-t1="+(t3-t1)/timeUnit
//					
//					);
//			
//		}

        return wrapper;
    }

    public String encryptByDES(String plainText, String mapKey, String keyId) {
        return encryptByDES(plainText, mapKey, keyId, defaultTimes);
    }

    public String encryptByDES(String plainText, String mapKey, String keyId, int times) {
        Wrapper<KeyCipher> wrapper = null;
        String encryptedString = null;
        Cipher cipher = null;
        try {
            int t = 0;
            while (t < times) {
                t++;
                wrapper = getCipherWrapper(mapKey, keyId);
                if (wrapper == null) {
                    if (wrapBufferMap.get(mapKey) == null)
                        //未建立工作密钥
                        return null;

                    if (logger.isDebugEnabled())
                        logger.debug(" cannot find any idle cipher for {} keyId={} times={}\n{}", mapKey, keyId, t, wrapBufferMap.get(mapKey).toString());
                    Thread.currentThread().sleep(50);
                    continue;
                }
                cipher = wrapper.getT().getCipher(keyId);
                if (cipher == null) {
                    logger.error("{} cipher for key={} mapKey={} times{}", wrapper.getT(), keyId, mapKey, t);
                    Thread.currentThread().sleep(50);
                    continue;
                }

                byte[] plainBytes = plainText.getBytes("UTF-8");
                byte[] encrypted = cipher.doFinal(plainBytes);
                //encryptedString=wrapper.getT().getHistroy();
                wrapper.free();
                wrapper = null; ////TODO当前wrapper=null,还是wrapper指向的对象=null?
                encryptedString = Base64.encodeBase64URLSafeString(encrypted);//encodeBase64String(encrypted);
                break;
            }
        } catch (Exception e) {
            KeyDES keyAes = (KeyDES) keyMap.get(mapKey);
            logger.error("encryptByDES:\tkeyCipher={}\n\tcur:cipher={} mapKey={} keyId={}\n\tkeyAes={}\n\tplainText.length={}\n{}"
                    , wrapper.getT(), cipher, mapKey, keyId, keyAes, plainText.length(), plainText, e);
//			logger.error("encryptByAES:cipher="+cipher+" mapKey="+aesMapKey+" keyId="+keyId+" plainText.length="+plainText.length()+"\n"+plainText,e);
//			logger.error("encryptByAES:keyCipher="+wrapper.getT()+" cipher="+cipher+"\n"+wrapBufferMap.get(aesMapKey).toString());

            ////TODO cipher==null
        } finally {
            if (wrapper != null) wrapper.free();
        }
        return encryptedString;
    }


    private long threshold_2 = 2000000000;
    private long secUnit = 1000000;

    public String decryptByDES(String encryptedText, String mapKey, String keyId) {
        return decryptByDES(encryptedText, mapKey, keyId, defaultTimes);
    }

    public String decryptByDES(String encryptedText, String mapKey, String keyId, int times) {
        Wrapper<KeyCipher> wrapper = null;
        String plainText = null;
        Cipher cipher = null;

        try {
            int t = 0;
            while (t < times) {
                t++;
                wrapper = getCipherWrapper(mapKey, keyId);
                if (wrapper == null) {
                    if (wrapBufferMap.get(mapKey) == null)
                        //未建立工作密钥
                        return null;

                    if (logger.isDebugEnabled())
                        logger.debug(" cannot find any idle cipher for " + mapKey + "keyId=" + keyId + "times=" + t + "\n" + wrapBufferMap.get(mapKey).toString());
                    Thread.currentThread().sleep(50);
                    continue;
                }
                cipher = wrapper.getT().getCipher(keyId);
                if (cipher == null) {
                    logger.error(wrapper.getT() + " cipher for key=" + keyId + " mapKey=" + mapKey + " times" + t);
                    Thread.currentThread().sleep(50);
                    continue;
                }

                byte[] encryptedBytes = Base64.decodeBase64(encryptedText);
                byte[] plainBytes = cipher.doFinal(encryptedBytes);
                wrapper.free();
                wrapper = null;
                plainText = new String(plainBytes, "UTF-8");
                break;
            }
        } catch (Exception e) {
            Key key = keyMap.get(mapKey);
            KeyDES keyAes = (KeyDES) keyMap.get(mapKey);
            logger.error("decryptByDES:\tkeyCipher=" + wrapper.getT() + "\n\tcur:cipher=" + cipher
                            + " mapKey=" + mapKey
                            + " keyId=" + keyId + "\n\tkeyAes=" + keyAes
                            + "\n\tencryptedText.length=" + encryptedText.length() + "\n" + encryptedText
                    //+"\n"+wrapBufferMap.get(mapKey).toString()
                    , e);

            //logger.error("decryptByAES:keyCipher="+wrapper.getT()+" cipher="+cipher+"\n"+wrapBufferMap.get(aesMapKey).toString());
        } finally {
            if (wrapper != null) wrapper.free();
        }
        return plainText;
    }

    public String encryptByAES(String plainText, String aesMapKey, String keyId) {
        return encryptByAES(plainText, aesMapKey, keyId, defaultTimes);
    }

    public String encryptByAES(String plainText, String aesMapKey, String keyId, int times) {

        Wrapper<KeyCipher> wrapper = null;
        String encryptedString = null;
        Cipher cipher = null;
        try {
            int t = 0;
            while (t < times) {
                t++;
                wrapper = getCipherWrapper(aesMapKey, keyId);
                if (wrapper == null) {
                    if (logger.isDebugEnabled())
                        logger.debug(" cannot find any idle cipher for " + aesMapKey + "keyId=" + keyId + "times=" + t + "\n" + wrapBufferMap.get(aesMapKey).toString());
                    Thread.currentThread().sleep(50);
                    continue;
                }
                cipher = wrapper.getT().getCipher(keyId);
                if (cipher == null) {
                    logger.error(wrapper.getT() + " cipher for key=" + keyId + " mapKey=" + aesMapKey + " times" + t);
                    Thread.currentThread().sleep(50);
                    continue;
                }

                byte[] plainBytes = plainText.getBytes("UTF-8");
                byte[] encrypted = cipher.doFinal(plainBytes);
                //encryptedString=wrapper.getT().getHistroy();
                wrapper.free();
                wrapper = null; ////TODO当前wrapper=null,还是wrapper指向的对象=null?
                encryptedString = Base64.encodeBase64URLSafeString(encrypted);//encodeBase64String(encrypted);
                break;
            }
        } catch (Exception e) {
            KeyAES keyAes = (KeyAES) keyMap.get(aesMapKey);
            logger.error("encryptByAES:\tkeyCipher=" + wrapper.getT() + "\n\tcur:cipher=" + cipher
                            + " mapKey=" + aesMapKey
                            + " keyId=" + keyId + "\n\tkeyAes=" + keyAes
                            + "\n\tplainText.length=" + plainText.length() + "\n" + plainText
                    //+"\n"+wrapBufferMap.get(aesMapKey).toString()
                    , e);

        } finally {
            if (wrapper != null) wrapper.free();
        }
        return encryptedString;
    }


    public String decryptByAES(String encryptedText, String aesMapKey, String keyId) {
        return decryptByAES(encryptedText, aesMapKey, keyId, defaultTimes);
    }

    public String decryptByAES(String encryptedText, String aesMapKey, String keyId, int times) {
        Wrapper<KeyCipher> wrapper = null;
        String plainText = null;
        Cipher cipher = null;
        try {
            int t = 0;
            while (t < times) {
                t++;
                wrapper = getCipherWrapper(aesMapKey, keyId);
                if (wrapper == null) {
                    if (logger.isDebugEnabled())
                        logger.debug(" cannot find any idle cipher for " + aesMapKey + "keyId=" + keyId + "times=" + t + "\n" + wrapBufferMap.get(aesMapKey).toString());
                    Thread.currentThread().sleep(50);
                    continue;
                }
                cipher = wrapper.getT().getCipher(keyId);
                if (cipher == null) {
                    logger.error(wrapper.getT() + " cipher for key=" + keyId + " mapKey=" + aesMapKey + " times" + t);
                    Thread.currentThread().sleep(50);
                    continue;
                }

                byte[] encryptedBytes = Base64.decodeBase64(encryptedText);
                byte[] plainBytes = cipher.doFinal(encryptedBytes);
                wrapper.free();
                wrapper = null;
                plainText = new String(plainBytes, "UTF-8");
                break;
            }
        } catch (Exception e) {
            Cipher cipher1 = wrapper.getT().getCipher(keyId);
            if (!cipher1.equals(cipher))
                logger.error("cipher1 doesnot equals to cipher ");
            else
                logger.error("cipher1 does equals to cipher ");

            KeyAES keyAes = (KeyAES) keyMap.get(aesMapKey);
            logger.error("decryptByAES:\tkeyCipher=" + wrapper.getT() + "\n\tcur:cipher=" + cipher
                            + " mapKey=" + aesMapKey + " keyId=" + keyId + "\n\tkeyAes=" + keyAes
                            + "\n\tencryptedText.length=" + encryptedText.length() + "\n" + encryptedText
                    //+"\n"+wrapBufferMap.get(aesMapKey).toString()
                    , e);
            //logger.error("decryptByAES:keyCipher="+wrapper.getT()+" cipher="+cipher+"\n"+wrapBufferMap.get(aesMapKey).toString());

        } finally {
            if (wrapper != null) wrapper.free();
        }

        return plainText;

    }


    /**
     * publicKey=userId_appId_publickey
     * keyId=userId_appId_publickey_20141031
     *
     * @param plainText
     * @param publicKey
     * @param keyId
     * @return
     * @throws Exception
     */
    public String encryptByPublicKey(String plainText, String publicMapKey, String publicKey) {
        return encryptByPublicKey(plainText, publicMapKey, publicKey, defaultTimes);
    }

    public String encryptByPublicKey(String plainText, String publicMapKey, String publicKey, int times) {
        Wrapper<KeyCipher> wrapper = null;
        String encryptedString = null;

        try {
            int t = 0;
            while (t < times) {
                t++;
                wrapper = getCipherWrapper(publicMapKey, publicKey);
                if (wrapper == null) {
                    if (logger.isDebugEnabled())
                        logger.debug(" cannot find any idle cipher for " + publicMapKey + " time=" + t + "\n" + wrapBufferMap.get(publicMapKey).toString());
                    //				privateKey=corePrivateKey;
                    //				wrapper=getCipherWrapper(privateKey,keyId);
                    Thread.currentThread().sleep(50);
                    continue;
                }
                Cipher cipher = wrapper.getT().getCipher(publicKey);

                byte[] plainBytes = plainText.getBytes("UTF-8");
                //int len=plainBytes.length;

                //PKCS1Padding模式下，sunjce，bouncycastle，blocksize=117--key_size=1024
                //cipher.getBlockSize()--sunjce有时不能返回。
                //RSA最少输出128，总的数量=117快数*128
                //int bb=cipher.getOutputSize(10);
                int bs = inputSizeLimit_1024;
                int o_bs = 128;

                //计算输出长度
                int srcL = plainBytes.length;
                int ob = srcL / bs + 1; //输出块数
                if (srcL % bs == 0) ob = srcL / bs;
                byte[] out = new byte[ob * o_bs];
                //ByteArrayOutputStream bout = new ByteArrayOutputStream(128);

                int k = 0;
                int i = 0; //输入指针
                int j = 0; //输出指针
                int l = srcL; //剩余
                while (l > 0) {
                    //byte[] outBytes=cipher.doFinal(plainBytes, i*blockSize, blockLength);
                    //bout.write(outBytes);
                    if (l > bs)
                        cipher.doFinal(plainBytes, i, bs, out, j);
                    else
                        cipher.doFinal(plainBytes, i, l, out, j);
                    k++;
                    i = k * bs;
                    j = k * o_bs;
                    l = srcL - i;
                }
                wrapper.free();
                wrapper = null;
                encryptedString = Base64.encodeBase64URLSafeString(out);//encodeBase64String(out);
                break;
            }
        } catch (Exception e) {
            logger.error("publicMapKey=" + publicMapKey + " publicKey=" + publicKey + " encryptByPublicKey\n" + "plainText=" + plainText, e);
        } finally {
            if (wrapper != null) wrapper.free();
        }

        return encryptedString;
    }

    /**
     * privateKey=userId_appId_privatekey
     * keyId=userId_appId_privatekey_20141031
     *
     * @param encrypted
     * @param privateKey
     * @param keyId
     * @return
     * @throws Exception
     */
    public String decryptByPrivateKey(String encrypted, String privateMapKey, String privateKey) {
        return decryptByPrivateKey(encrypted, privateMapKey, privateKey, defaultTimes);
    }

    public String decryptByPrivateKey(String encrypted, String privateMapKey, String privateKey, int times) {
        //注意Cipher初始化时的参数“RSA/ECB/PKCS1Padding”,
        //代表和.NET用相同的填充算法，如果是标准RSA加密，则参数为“RSA”
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");     
//        cipher.init(Cipher.DECRYPT_MODE, privateKey); 

        Wrapper<KeyCipher> wrapper = null;
        String plainText = null;

        try {
            int t = 0;
            while (t < times) {
                t++;
                //TODO 默认只有一个私钥？
                //多个私钥，密码如何获取？
                wrapper = getCipherWrapper(privateMapKey, privateKey);
                if (wrapper == null) {
                    if (logger.isDebugEnabled())
                        logger.debug(t + " cannot find any idle cipher for " + privateMapKey + "\n" + wrapBufferMap.get(privateMapKey).toString());
                    //				privateKey=corePrivateKey;
                    //				wrapper=getCipherWrapper(privateKey,keyId);
                    Thread.currentThread().sleep(10);
                    continue;
                }

                Cipher cipher = wrapper.getT().getCipher(privateKey);

                byte[] encryptedBytes = Base64.decodeBase64(encrypted);
                int srcL = encryptedBytes.length;
                int bs = 128;
                int o_bs = inputSizeLimit_1024;

                int ob = srcL / bs + 1; //输出块数
                if (srcL % bs == 0) ob = srcL / bs;

                //byte[] out = new byte[ob*bs];//几乎浪费（128-117）*ob的空间。
                //其实加密解密配合，可以知道有效每块有效长度的。，所以可以乘上输出块数即可

                //发现，doFinal(encryptedBytes, i,bs,out, j);在执行时不论 输入有多少，必须准备一块输出缓存，
                //所以，必须多定义一块，这样算来最多浪费2块，大多数浪费1.x块。
                //不知道这样的开销，对比下面如何。
                //ByteArrayOutputStream bout = new ByteArrayOutputStream(64);

                //经过计算，ob*inputSizeLimit_1024+inputSizeLimit_1024，和ob*128对比
                //ob>11;inputSizeLimit_1024总空间小于128，

                //TODO,cipher已经缓存，这样，有可能缓存out，
                //特别是对应于ota的项目，rsa只是用来做key交换，key是固定的。可以考虑缓存。
                //或者采用ByteArrayOutputStream方案，ByteArrayOutputStream有reset
                byte[] out = null;
                if (ob > 11) {
                    out = new byte[ob * o_bs + bs];
                } else {
                    out = new byte[ob * bs];
                }

                //解密
                int k = 0;
                int i = 0; //输入指针
                int j = 0; //输出指针
                int l = srcL; //剩余
                while (l > 0) {
                    //bout.write(cipher.doFinal(encryptedBytes, i, bs));
                    int j1 = cipher.doFinal(encryptedBytes, i, bs, out, j);
                    k++;
                    i = k * bs;
                    j = j + j1;
                    l = srcL - i;

                }

                wrapper.free();
                wrapper = null;
                plainText = new String(out, 0, j, "UTF-8"); //j为输出长度；
                //plainText = new String(bout.toByteArray(),"UTF-8");

                break;
            }
        } catch (Exception e) {
            logger.error("privateMapKey=" + privateMapKey + " privateKey=" + privateKey + " decryptByPrivateKey\n" + "encrypted=" + encrypted, e);
        } finally {
            if (wrapper != null) wrapper.free();
        }

        return plainText;
    }

    /**
     * @param workKeyUpdaterExceutor the workKeyUpdaterExceutor to set
     */
    public void setWorkKeyUpdaterExceutor(KeyUpdaterExceutor workKeyUpdaterExceutor) {
        this.workKeyUpdaterExceutor = workKeyUpdaterExceutor;
    }


    public WrapperBuffer<KeyCipher> getWrapperBuffer(String mapKey) {
        return wrapBufferMap.get(mapKey);
    }





    public String getKeyPath() {
        return keyPath;
    }
    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }
    public int getRsaRefreshInteral() {
        return rsaRefreshInteral;
    }
    public void setRsaRefreshInteral(int rsaRefreshInteral) {
        this.rsaRefreshInteral = rsaRefreshInteral;
    }

    public void setKeyTimeToUpdate(long keyTimeToUpdate) {
        this.keyTimeToUpdate = keyTimeToUpdate;
    }

    public String getAlogrithm() {
        return alogrithm;
    }

    public void setAlogrithm(String alogrithm) {
        this.alogrithm = alogrithm;
    }

    public String getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setKeyStorePwd(String keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }

    public String getSysAlias() {
        return sysAlias;
    }

    public void setSysAlias(String sysAlias) {
        this.sysAlias = sysAlias;
    }

    public String getSysAliasPwd() {
        return sysAliasPwd;
    }

    public void setSysAliasPwd(String sysAliasPwd) {
        this.sysAliasPwd = sysAliasPwd;
    }

}
