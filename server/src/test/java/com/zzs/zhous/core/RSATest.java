/**
 *
 */
package com.zzs.zhous.core;

import com.zzs.zhous.node.key.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author Administrator
 */
public class RSATest {

    /**
     *
     */
    public RSATest() {
    }


    @Test
    public void test() throws Exception {
        Cipher cipher1 = null;
        Cipher cipher2 = null;
        FileInputStream fis = null;

        //fis = new FileInputStream("src/test/java/key/core/000_000_privateKey_20141031.keystore");
        fis = new FileInputStream("src/test/java/key/java.keystore");
        cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //cipher1=Cipher.getInstance("RSA");
        //加载密钥库文件
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(fis, "123456".toCharArray());
        //获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) ks.getKey("ss", "123456".toCharArray());
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        fis.close();

        //fis = new FileInputStream("src/test/java/key/003/003_001_publicKey_20140909.cer");
        fis = new FileInputStream("src/test/java/key/java.cer");
        Certificate cer = CertificateFactory.getInstance("X.509").generateCertificate(fis);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) cer.getPublicKey();
        //cipher2=Cipher.getInstance("RSA");
        cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher2.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        fis.close();

        //String plainText=randomString(129);
        //String plainText="C2dfE:ad42";//汉66fD0汉f4cc5国c:48BCg71D96BED:9中中cd汉b汉cdg16B0E:中BDCcfE4汉aC6cfE37f081cf4f9dB5国8E76dA4c:国309F1汉:ECFCgfF09C37d36国fbB724cgF汉";
        String plainText = "003_001_1415938576715";
        byte[] plainBytes = plainText.getBytes("UTF-8");
        byte[] raw = cipher2.doFinal(plainBytes);
        byte[] out = cipher1.doFinal(raw);


    }


    public void test1() throws Exception {
        Cipher cipher1 = null;
        Cipher cipher2 = null;
        FileInputStream fis = null;

        //fis = new FileInputStream("src/test/java/key/core/000_000_privateKey_20141031.keystore");
        fis = new FileInputStream("src/test/java/key/java.keystore");
        cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //cipher1=Cipher.getInstance("RSA");
        //加载密钥库文件
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(fis, "123456".toCharArray());
        //获取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) ks.getKey("ss", "123456".toCharArray());
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        fis.close();

        //fis = new FileInputStream("src/test/java/key/003/003_001_publicKey_20140909.cer");
        fis = new FileInputStream("src/test/java/key/java.cer");
        Certificate cer = CertificateFactory.getInstance("X.509").generateCertificate(fis);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) cer.getPublicKey();
        //cipher2=Cipher.getInstance("RSA");
        cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher2.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        fis.close();

        //String plainText=randomString(129);
        String plainText = "C2dfE:ad42汉66fD0汉f4cc5国c:48BCg71D96BED:9中中cd汉b汉cdg16B0E:中BDCcfE4汉aC6cfE37f081cf4f9dB5国8E76dA4c:国309F1汉:ECFCgfF09C37d36国fbB724cgF汉";

        byte[] plainBytes = plainText.getBytes("UTF-8");
        int len = plainBytes.length;

        int bs = 117;
        int o_bs = 128;
        //PKCS1Padding模式下，sunjce，bouncycastle，blocksize=117--key_size=1024
        //cipher.getBlockSize()--sunjce有时不能返回。
        //RSA最少输出128，总的数量=117快数*128
        //int bb=cipher.getOutputSize(10);

        //计算输出长度
        int srcL = plainBytes.length;
        int ob = srcL / bs + 1; //输出块数
        if (srcL % bs == 0) ob = srcL / bs;
        byte[] out = new byte[ob * o_bs];
        ByteArrayOutputStream bout1 = new ByteArrayOutputStream(128);

        int k = 0;
        int i = 0; //输入指针
        int j = 0; //输出指针
        int l = srcL; //剩余
        while (l > 0) {
            //byte[] outBytes=cipher.doFinal(plainBytes, i*blockSize, blockLength);
            //bout.write(outBytes);
            if (l > bs) {
                cipher2.doFinal(plainBytes, i, bs, out, j);
                //bout1.write(cipher2.doFinal(plainBytes, i, bs));
            } else {
                cipher2.doFinal(plainBytes, i, l, out, j);
                //bout1.write(cipher2.doFinal(plainBytes, i, l));
            }
            k++;
            i = k * bs;
            j = k * o_bs;
            l = srcL - i;
        }

        int bb = out.length;
        byte[] out1 = bout1.toByteArray();
        bb = out1.length;
        //out=out1;
        String encryptedString = Base64.encodeBase64String(out);

        byte[] encryptedBytes = Base64.decodeBase64(encryptedString);
        srcL = encryptedBytes.length;
        bs = 128;

        ob = srcL / bs + 1; //输出块数
        if (srcL % bs == 0) ob = srcL / bs;

        out = new byte[ob * bs];
        ByteArrayOutputStream bout = new ByteArrayOutputStream(64);

        //解密  

        k = 0;
        i = 0; //输入指针
        j = 0; //输出指针
        l = srcL; //剩余
        while (l > 0) {
            //bout.write(cipher1.doFinal(encryptedBytes, i, bs));
            int j1 = cipher1.doFinal(encryptedBytes, i, bs, out, j);
            k++;
            i = k * bs;
            j = j + j1;
            l = srcL - i;

        }
        String originalText = new String(out, 0, j, "UTF-8");


        if (originalText.equals(plainText))
            System.out.println("OK");
        else
            System.out.println("error");


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


    /**
     * <pre>获取真实路径</pre>
     */
    private String getRealPath(String orgPath) {
        String path = RSAUtils.class.getResource("/").getPath().substring(1);
        String filePath = path + orgPath;
        if (!new File(filePath).exists()) {
            filePath = "/" + filePath;
        }
        return filePath;
    }

}
