/**
 *
 */
package com.zzs.zhous.core;

import com.zzs.zhous.node.key.RSAUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author Administrator
 */
public class RSAUtilsTest {

    private final static Logger logger = LoggerFactory.getLogger(RSAUtilsTest.class);

    private RSAUtils rsaUtils = new RSAUtils();



    /**
     *
     */
    public RSAUtilsTest() {
    }

    @Test
    public void logTest(){
        logger.info("sdas:{}", "asd");
    }


    /**
     * 测试key管理
     * 测试加密解密_java
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {

        rsaUtils.init("src/test/java/key", 30 * 1000);

//        String publicKey = "003_001_publickey";
//        String publicKeyId = "003_001_publickey_20140909";
//
//        String privateKey = "000_000_privatekey";
//        String privateKeyId = "000_000_privatekey_20141031";


        String publicKey = "003_001_publickey";
        String publicKeyId = "003_001_publickey_20141101";

        String privateKey = "003_001_privatekey";
        String privateKeyId = "003_001_privatekey_20141101";


        String plainText = randomString(2048);
        for (int i = 0; i < 1000; i++) {


            //rsaUtils.setKeystorePath("key/java.keystore");

            String encryptedString = rsaUtils.encryptByPublicKey(plainText, publicKey, publicKeyId);

            String originalString = rsaUtils.decryptByPrivateKey(encryptedString, privateKey, privateKeyId);

            if (plainText.equals(originalString))
                System.out.println("ok");
            else
                System.out.println("error");
        }

    }

    /**
     * 测试来自dotnet2.0，3.0，4.0的RSA 1024bit的RSA解密
     *
     * @throws Exception
     */
    public void fromdotnet() throws Exception {

        String publicKey = "003_001_publickey";
        String publicKeyId = "003_001_publickey_20140909";

        String privateKey = "000_000_privatekey";
        String privateKeyId = "000_000_privatekey_20141031";

        rsaUtils.init("src/test/java/key", 30 * 1000);


        for (int j = 2; j < 5; j++) {

            String fileName = "dotnetRSA_" + j + ".0.txt";

            BufferedReader br = new BufferedReader(new FileReader(fileName));


            int i = 0;
            try {
                while (true) {
                    String plainText = br.readLine();
                    if (plainText == null) break;
                    String encryptedString = br.readLine();
                    if (encryptedString == null) break;

                    String originalString = rsaUtils.decryptByPrivateKey(encryptedString, privateKey, privateKeyId);

                    if (plainText.equals(originalString))
                        System.out.println("OK i=" + i);
                    else
                        System.out.println("failure i=" + i);

                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                br.close();
            }

        }


    }


    public void toDotnet() throws Exception {

        String publicKey = "001_001_publickey";
        String publicKeyId = "001_001_publickey_20141031";

        String privateKey = "000_000_privatekey";
        String privateKeyId = "000_000_privatekey_20141031";

        rsaUtils.init("src/test/java/key", 30 * 1000);


        BufferedWriter br = new BufferedWriter(new FileWriter("toDotnetRSA.txt"));
        //rsaUtils.setKeystorePath("key/java.keystore");

        try {
            for (int i = 0; i < 10000; i++) {
                double d = Math.random();
                int j = (int) (2048 * d);
                j = j + 100;
                String plainText = randomString(j);
                br.write(plainText);
                br.write("\n");

                String encryptedString = rsaUtils.encryptByPublicKey(plainText, publicKey, publicKeyId);// "appA_publicKey");
                br.write(encryptedString);
                br.write("\n");
            }

            br.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            br.close();
        }

        ///////////////////////
    }


    private char[] seed = new char[]{'国', '中', ':', 'a', 'b', 'c', 'd', 'f', 'g', 'A', 'B', 'C', 'D', 'E', 'F', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '汉'};

    private String randomString(int length) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            double d = Math.random();
            int j = (int) (seed.length * d);
            buf.append(seed[j]);
        }

        return buf.toString();


    }


}
