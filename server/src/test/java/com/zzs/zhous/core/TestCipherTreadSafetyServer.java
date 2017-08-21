/**
 *
 */
package com.zzs.zhous.core;

import com.zzs.zhous.node.key.KeyDES;
import com.zzs.zhous.node.key.RSAUtils;
import org.base.framework.chain.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.util.Map;

/**
 * @author Administrator
 */
public class TestCipherTreadSafetyServer implements Handler {
    private final static Logger logger = LoggerFactory.getLogger(TestCipherTreadSafetyServer.class);

    /**
     *
     */
    public TestCipherTreadSafetyServer() {
        // TODO Auto-generated constructor stub
    }

    public void exceute(Map<String, Object> args) throws Exception {


        String plain = (String) args.get("plain");
        String encrypted = (String) args.get("encrypted");
        RSAUtils rsaUtils = (RSAUtils) args.get("rsaUtils");
        String privateKey = (String) args.get("privateKey");
        String privateKeyId = (String) args.get("privateKeyId");
        String count = (String) args.get("count");
        int i = Integer.parseInt(count);

        String originalString = rsaUtils.decryptByPrivateKey(encrypted, privateKey, privateKeyId);
        if (!plain.equals(originalString)) {
            logger.error("count=" + count + " error!" + originalString + " privateKeyId=" + privateKeyId);
        } else {
            if (logger.isDebugEnabled()) logger.debug("count=" + count + " Ok!" + " privateKeyId=" + privateKeyId);
        }


        String keyId = (String) args.get("keyId");
        String userId = (String) args.get("userId");
        String appId = (String) args.get("appId");

        String mapKey = KeyDES.getMapKey(userId, appId, Cipher.ENCRYPT_MODE);
        encrypted = rsaUtils.encryptByDES(plain, mapKey, keyId);
        if (encrypted != null) {
            originalString = rsaUtils.decryptByDES(encrypted, KeyDES.getMapKey(userId, appId, Cipher.DECRYPT_MODE), keyId);
            if (originalString != null && originalString.equals(plain)) {
                if (logger.isDebugEnabled()) logger.debug("count=" + count + " Ok!" + " keyId=" + keyId);
                return;
            }
        }

        logger.error("count=" + count + " error!" + originalString + " privateKeyId=" + privateKeyId);


        //Thread.currentThread().sleep(200);


    }


}
