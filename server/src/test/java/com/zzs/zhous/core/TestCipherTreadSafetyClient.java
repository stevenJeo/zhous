/**
 *
 */
package com.zzs.zhous.core;

import com.zzs.zhous.node.key.KeyDES;
import com.zzs.zhous.node.key.KeyFile;
import com.zzs.zhous.node.key.RSAUtils;
import org.base.framework.chain.Handler;
import org.base.framework.jms.JmsHandlerChain;
import org.base.framework.task.TaskThread;
import org.base.framework.task.TaskThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Administrator
 */
public class TestCipherTreadSafetyClient implements Handler {

    private TaskThreadPool pool;
    private RSAUtils rsaUtils;
    private JmsHandlerChain receiveChain;

    private String userId;
    private String appId;


    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @param appId the appId to set
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }


    private final Logger log = LoggerFactory.getLogger(TestCipherTreadSafetyClient.class);


    /**
     * @param receiveChain the receiveChain to set
     */
    public void setReceiveChain(JmsHandlerChain receiveChain) {
        this.receiveChain = receiveChain;
    }

    /**
     * @param pool the pool to set
     */
    public void setPool(TaskThreadPool pool) {
        this.pool = pool;
    }

    /**
     * @param rsaUtils the rsaUtils to set
     */
    public void setRsaUtils(RSAUtils rsaUtils) {
        this.rsaUtils = rsaUtils;
    }

    /**
     *
     */
    public TestCipherTreadSafetyClient() {
        // TODO Auto-generated constructor stub
    }


    private char[] seed = new char[]{'国', '中', ':', 'a', 'b', 'c', 'd', 'f', 'g', 'A', 'B', 'C', 'D', 'E', 'F', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '汉'};

    private String randomString(int length) {

        double d = Math.random();
        length = (int) (length * d);
        if (length == 0) length = 100;


        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            d = Math.random();
            int j = (int) (seed.length * d);
            buf.append(seed[j]);
        }

        return buf.toString();


    }


    /* (non-Javadoc)
     * @see org.owens.framework.chain.Handler#exceute(java.util.Map)
     */
    public void exceute(Map<String, Object> arg0) throws Exception {


        int count = 0;
        String keyId = null;
        while (true) {
            try {

                if (count % 100000 == 0) {
                    String desKey = KeyDES.createDesKey();
                    String time = "" + System.currentTimeMillis();
                    rsaUtils.addDES(userId, appId, desKey, "aaaaaaaa", time);
                    keyId = time;
                    log.error("change des key count={} time={}", count, time);
                }


                TaskThread tt = pool.getTaskThread();

                if (tt == null) {
                    //if (log.isDebugEnabled())log.debug(" cann't find free taskThread sleep 100!");
                    Thread.currentThread().sleep(100);
                    continue;
                }
                double ii = Math.random();
                String key = null;
                String mapkey = KeyFile.getMapKey(userId, appId, true);
                KeyFile keyFile = (KeyFile) rsaUtils.getKeyMap().get(mapkey);
                if (ii < 0.5)
                    key = keyFile.getKey(true);//(userId,appId);
                else
                    key = keyFile.getKey(false);//rsaUtils.getKeyId(userId,appId,true,false);

//				    String keyPrefix=userId+"_"+appId+"_";
//				    String publicKey=keyPrefix+"publickey";
//				    String fullKeyId=publicKey+"_"+keyId;
                String publicKey = KeyFile.getMapKey(userId, appId, true);//userId+Key.separator+appId+"_publickey";
                String fullKeyId = key;//publicKey+Key.separator+key;


                String plain = randomString(2048);
                String encrypted = rsaUtils.encryptByPublicKey(plain, publicKey, fullKeyId);


                Map<String, Object> args = tt.getArgs();
                args.put("plain", plain);
                args.put("encrypted", encrypted);
                args.put("rsaUtils", rsaUtils);
                String privateKey = KeyFile.getMapKey(userId, appId, false);//keyPrefix+"privatekey";
                String privateKeyId = key;//privateKey+"_"+key;
                args.put("privateKey", privateKey);
                args.put("privateKeyId", privateKeyId);
                args.put("count", count + "");
                args.put("keyId", keyId);
                args.put("userId", userId);
                args.put("appId", appId);

//                tt.addTask(receiveChain);

                if (log.isInfoEnabled())
                    log.debug("add count=" + count + " userId=" + userId + " appId=" + appId + " keyId" + key);

                count++;
                //Thread.currentThread().sleep(100);

            } catch (Exception e) {
                log.error("TestCipherTreadSafetyClient ", e);
            } finally {

            }
        }


    }
}
