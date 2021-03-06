package common;

import org.junit.Test;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author tongqing.hu
 * @Date 2019/10/11
 */
public class RsaUtil {

    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f"};

    private static final String RSA = "RSA";
    private static final String CHARSET_CODING = "UTF-8";
    private static final String SHA256withRSA = "SHA256withRSA";
    private static final String MD5withRSA = "MD5withRSA";
    /***************************************************************************/
    private static final String MD5 = "MD5";
    private static final String SHA_1 = "SHA-1";
    private static final String SHA_256 = "SHA-256";

    private static String byteToHexString(byte b) {
        /**
         * @Description: 字节转换成字符串
         * @methodName: byteToHexString
         * @Param: [b]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/26 21:48
         */
        int n = b;
        if (n < 0) {
            n = n + 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static String byteArrayToHexString(byte[] byteArray) {
        /**
         * @Description: 字节数组转换成字符串
         * @methodName: byteArrayToHexString
         * @Param: [b]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/26 22:28
         */
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            sb.append(byteToHexString(byteArray[i]));
        }
        return sb.toString();
    }

    public static String Encode(String origin, String algorithm) throws Exception {
        /**
         * @Description: 签名对象
         * @methodName: MD5Encode
         * @Param: [origin]
         * @return: java.lang.String
         * @Author: scyang
         * @Date: 2019/10/28 21:25
         */
        MessageDigest md = MessageDigest.getInstance(algorithm);
        // md.update(origin.getBytes("UTF-8"));
        byte[] digest = md.digest(origin.getBytes(CHARSET_CODING));
        origin = byteArrayToHexString(digest);
        return origin;
    }

    @Test
    public void test_() throws Exception {
        System.out.println(Encode("盛重阳", "MD5"));
        System.out.println(Encode("盛重阳", MD5));
        System.out.println(Encode("盛重阳", SHA_1));
        System.out.println(Encode("盛重阳", SHA_256));
       // System.out.println(Encode("盛重阳", MD5withRSA));
    }

    /**
     * 加密
     *
     * @param content   待加密内容
     * @param publicKey 密钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        Key key = getPublicKeyByContent(publicKey, RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = content.getBytes(CHARSET_CODING);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        int maxEncryptBlock = calcMaxEncryptBlock((RSAKey) key);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxEncryptBlock) {
                cache = cipher.doFinal(data, offSet, maxEncryptBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxEncryptBlock;
        }

        return new String(Base64.getMimeEncoder().encode(out.toByteArray()), StandardCharsets.UTF_8);
    }

    /**
     * 解密
     *
     * @param encryptedContent 待解密内容
     * @param charset          字符集
     * @param privateKey       密钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedContent, String privateKey) throws Exception {

        Key key = getPrivateKeyByContent(privateKey, RSA);

        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedData = Base64.getMimeDecoder().decode(encryptedContent);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        int maxDecryptBlock = calcMaxDecryptBlock((RSAKey) key);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxDecryptBlock) {
                cache = cipher.doFinal(encryptedData, offSet, maxDecryptBlock);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxDecryptBlock;
        }

        return new String(out.toByteArray(), CHARSET_CODING);
    }

    /**
     * 签名
     *
     * @param body
     * @param charSet
     * @param privateKey
     * @param encryptAlgorithm
     * @param signAlgorithm
     * @return
     * @throws Exception
     */
    public static String generateSign(String body, String privateKey, String signAlgorithm) throws Exception {
        Signature signature = Signature.getInstance(signAlgorithm);

        PrivateKey key = getPrivateKeyByContent(privateKey, RSA);
        signature.initSign(key);
        byte[] dataInBytes = body.getBytes(CHARSET_CODING);
        signature.update(dataInBytes);
        byte[] signedInfo = signature.sign();

        return new String(Base64.getMimeEncoder().encode(signedInfo), StandardCharsets.UTF_8);
    }

    /**
     * 验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static boolean verifyWithMd5(String content, String sign, String publicKey, String algorithm) throws Exception {

        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(getPublicKeyByContent(publicKey, RSA));
        signature.update(content.getBytes(CHARSET_CODING));

        byte[] keyByte = Base64.getMimeDecoder().decode(sign);

        return signature.verify(keyByte);
    }

    /**
     * 计算加密分块最大长度
     */
    private static int calcMaxEncryptBlock(RSAKey key) {
        return key.getModulus().bitLength() / 8 - 11;
    }

    /**
     * 计算解密分块最大长度
     */
    private static int calcMaxDecryptBlock(RSAKey key) {
        return key.getModulus().bitLength() / 8;
    }

    public static PublicKey getPublicKeyByContent(String publicKey, String algorithm) throws Exception {

        byte[] keyByte = Base64.getMimeDecoder().decode(publicKey);

        KeyFactory kf = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
        return kf.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKeyByContent(String privateKey, String algorithm) throws Exception {

        byte[] keyByte = Base64.getMimeDecoder().decode(privateKey);

        KeyFactory kf = KeyFactory.getInstance(algorithm);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
        return kf.generatePrivate(keySpec);
    }

    public static void main(String[] args) {
        //String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDg3Zq4TgR4Isb5+cbYawiDQ5DH3BJ5v7OXZmiDen/G5Y41wKSxbSJQTJqFe7e7ODPB2I/ATmRX6D6u9dqcjMnlWJeZQWjWkQhRUlwcV+oolL+GnY+VTMAYiD2UJhVK86vgyUOkdo0PVgX1yp7lRdrZyIOnjaIIyMwHpLYR0j6xuQIDAQAB";
        //String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAODdmrhOBHgixvn5xthrCINDkMfcEnm/s5dmaIN6f8bljjXApLFtIlBMmoV7t7s4M8HYj8BOZFfoPq712pyMyeVYl5lBaNaRCFFSXBxX6iiUv4adj5VMwBiIPZQmFUrzq+DJQ6R2jQ9WBfXKnuVF2tnIg6eNogjIzAekthHSPrG5AgMBAAECgYEAjVk/pJMGJC5YPVJROEEvvLOAEuG3i2iBj64EDIG4SGKMHiXWYOFlLGy6xSWbvMf7YOKsmgms8cU/geNxY+7sCV0W0u/3f0BjTKYL7ZbhJYf0bcNi2z+bQDInzHNUfDApVSPPbGHsca2FWlxXja0uiqFU36CIcze2abIoFm3HogECQQDzk+JOk8xCtQL0ueDfSRTmvXhtcTHdVLNpRJg99SxJTglooy8Byrqu8TV/FuYZYiqislkwAsKdFDSIi4fLwPYJAkEA7FVrMU6YQ4WPYfrmEhpTYXyWs9sVsVlZpzzNTkGUe41CjYO/fv611JmPyfc+QXL+PifNQDdTGGTrJJs8delKMQJBAOvLhnHQ7BTLjEACzSj6Y2x3kORJhx8fBstqJwMbm7KfA8ay6ieO7Ke8Q7BzJ279NA7qHiK3GVTcoXppsJzTgJkCQHB7V0Foft/kOZIQsF/9V6IWgkOoCnMWa61FKxIL9He5i+t+wS3YXQKK8/zUSDUuXgOHPhFKQ6pgneoWWHhmSIECQBFKsU2WN/KvVq0/KJ/MvLa2nhcBIpZmoW5s83GZ3K6+Z/ECOfK33c2GgG1ezFfYmB2QS0JU9pWH4vy9c6Q/sUo=";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDH5SB7flrPNDDZY1axTlKyBDJ8\n" +
                "4LUioTc8CgxDOcqbfBzlvZT1Lb62rLr4aaUfKd5Mq/o/has0zwUecdHaxCFWFzHD\n" +
                "dYLx1sFnExvXjajs9ea4d6cPr7pCh21AzBxNuBsZQGldtIU9HqSHGM6dIuaZ0lvQ\n" +
                "SHnJRbiD2tVHMC1G3wIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMflIHt+Ws80MNlj\n" +
                "VrFOUrIEMnzgtSKhNzwKDEM5ypt8HOW9lPUtvrasuvhppR8p3kyr+j+FqzTPBR5x\n" +
                "0drEIVYXMcN1gvHWwWcTG9eNqOz15rh3pw+vukKHbUDMHE24GxlAaV20hT0epIcY\n" +
                "zp0i5pnSW9BIeclFuIPa1UcwLUbfAgMBAAECgYEAxHBy32lorD9J0hhBVbT2p2Jg\n" +
                "iz/4QbPUp/fn3Rip9uqdK1iHTfpKdRKCGyUW0+09PHO1VT9y5E6dZTHNXQNpDYff\n" +
                "6x0abJ5IjIudqImNYsFwvz178VqKsbAREKyJThPvKaAyuBUaVlIKOwZzFkTuZ4vz\n" +
                "O1H8rdCbaSejSqF75WECQQDxqSuJekxgRhmAACwl8ICdHFQ/z3CqC7fTV/0TEVkU\n" +
                "1VEUV1bJ2GWVroqQKd5fQveV7IILfLOG+weC0+sn6lh7AkEA08GIt8gOIJZSN3hA\n" +
                "pGrlOHPlD1VP+alXetFKaQMXPxFzNQEwLU0IdOjFBoM1wcrK6fPM15i+PolcFlJ4\n" +
                "bQoH7QJAEO3H5f6SQSBIAwaAETxS1i643Ct8+fyOKRj92osZkXMMFf+0TbmCDEVI\n" +
                "4aZ9NHEthOwnhAR9UxRcx1ZvH8Gz5wJAfAdVft+sdqXVTiwfIK6DyAcJ2WtMxpzL\n" +
                "YmKHxO5MhWJBKoChqUb/dwqHrILZz8JqC5IDHxyWAacOyvxPEP7ZGQJBAJPgscnM\n" +
                "kPEKNUSClYb576tXBBx6eIS4SfP/0RsLdt+5dcFstKkD1ItJQyR8wjyOmFjP6Bdi\n" +
                "02HSsidUepaZdW4=";
        //String content = "{\"service\":\"SETUP_CUST_CREDIT\",\"service_version\":\"1\",\"partner\":\"1001\",\"product_no\":\"001503\",\"service_sn\" :\"UUID\",\"product_name\" : \"xx项目\",\"content\" :\"GllcmVzaXMvRXRoL050aWxkZS9PZ3JhdmUvT2FjdXRlL09jaXJjd\",\"sign\":\"vZWFjdXRlL2VjaXJj}";
        String content = "{\"reqData\":{\"ifCar\":\"2\",\"country\":\"CHN\",\"dbBankCode\":\"403100000004\",\"lnRate\":0.000180,\"endDate\":\"2020-06-05\",\"workDuty\":\"3\",\"homeAddr\":\"湖南省怀化市靖州苗族侗族自治县渠阳镇渠阳中路214号\",\"idNo\":\"411321198608288156\",\"sales\":\"01\",\"homeCode\":\"000000\",\"ifCarCred\":\"2\",\"payType\":\"02\",\"isBelowRisk\":\"1\",\"children\":\"2\",\"workCode\":\"000000\",\"vouType\":\"4\",\"cardAmt\":0.00,\"homeTel\":\"13431386064\",\"profession\":\"06\",\"idType\":\"0\",\"authNo\":\"1120040208133512324016\",\"mincome\":25000.00,\"postAddr\":\"湖南省怀化市靖州苗族侗族自治县渠阳镇渠阳中路214号\",\"workTitle\":\"1\",\"ifCard\":\"2\",\"degree\":\"9\",\"ifAgent\":\"02\",\"birth\":\"19860828\",\"ifId\":\"1\",\"workName\":\"地球公司\",\"pactAmt\":1000.00,\"ifPact\":\"0\",\"ifApp\":\"0\",\"edu\":\"20\",\"custType\":\"99\",\"loanDate\":\"2020-04-02\",\"postCode\":\"000000\",\"hasOverdueLoan\":\"0\",\"income\":\"02\",\"appUse\":\"07\",\"riskLevel\":\"P3\",\"zxhomeIncome\":300000.00,\"rpyMethod\":\"03\",\"launder\":\"03\",\"dbAccountName\":\"郑茂栋\",\"phoneNo\":\"13431386064\",\"ifMort\":\"1\",\"loanTime\":\"2020-04-02 15:52:30\",\"marriage\":\"20\",\"idEndDate\":\"2038-08-10\",\"idPreDate\":\"2018-08-10\",\"sex\":\"1\",\"ifRoom\":\"1\",\"appArea\":\"000000\",\"ifLaunder\":\"02\",\"custName\":\"郑茂栋\",\"homeIncome\":\"01\",\"dbBankName\":\"邮储银行\",\"homeArea\":\"000000\",\"homeSts\":\"2\",\"loanTerm\":1,\"trade\":\"26\",\"applyNo\":\"1120040208133512324016\",\"dbBankAccount\":\"6210985770765003240\",\"workType\":\"1\",\"workWay\":\"G\",\"payDay\":15,\"dbOpenBankName\":\"邮储银行\",\"age\":34}}";
        try {
            String encryptStr = encrypt(content, publicKey);
            System.out.println("encryptStr: " + encryptStr);
            System.out.println(decrypt(encryptStr, privateKey));

            String signStr = generateSign(content, privateKey, SHA256withRSA);
            System.out.println("signStr: " + signStr);

            System.out.println(verifyWithMd5(content, signStr, publicKey, SHA256withRSA));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
