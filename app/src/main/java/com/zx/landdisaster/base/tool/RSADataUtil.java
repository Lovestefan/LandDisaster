package com.zx.landdisaster.base.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class RSADataUtil {

    /**
     * RSA最大加密
     */
    public static final int MAX_ENCRYPT_BLOCK = 245;    //加密最大值

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt0AzK9l0mbgLrQNjtCAIHSjoYHGg2Egz" +
            "uulGSIbTBs/MXkGHBUEcCD+c7hq7BkgskSlMTo0zT768vKXHZo919Bxt+Qy/8fQnpBmV7pQtuyIA" +
            "l1x2RpsizidLbysMYWJNQMdbpnluPE7IkwBfFcSdAXiznjZCrqNxZgUpvIpsEngAQVr4kM/fzhFQ" +
            "vW56ipJnjJAQfGZK7B4ADdT0rM6ISWpUdCU8LKtTvdQcomcDw3e9YC5fa3S4UOELiguXbawqb7cw" +
            "t8ey0bC0oA8exilFkr8Zxts3NWggmYIBi3p3D/YwQozLFZfpXJyOAdAVfitFvRVEzKzJ8SjyCK7n" +
            "/YL3nQIDAQAB";
    public static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC3QDMr2XSZuAutA2O0IAgdKOhg" +
            "caDYSDO66UZIhtMGz8xeQYcFQRwIP5zuGrsGSCyRKUxOjTNPvry8pcdmj3X0HG35DL/x9CekGZXu" +
            "lC27IgCXXHZGmyLOJ0tvKwxhYk1Ax1umeW48TsiTAF8VxJ0BeLOeNkKuo3FmBSm8imwSeABBWviQ" +
            "z9/OEVC9bnqKkmeMkBB8ZkrsHgAN1PSszohJalR0JTwsq1O91ByiZwPDd71gLl9rdLhQ4QuKC5dt" +
            "rCpvtzC3x7LRsLSgDx7GKUWSvxnG2zc1aCCZggGLencP9jBCjMsVl+lcnI4B0BV+K0W9FUTMrMnx" +
            "KPIIruf9gvedAgMBAAECggEAXoMtITuJiNLEqtoprTgsFyYHG97fD03F6GvyBOwMoOHBzQdM5Sfc" +
            "lmCAxslglm/ZFJFROt5WjZWZcE6sVjg9pMaY7a9mvBqp61gK3T7tiyuRVH+qvOW8gMkBffec7gku" +
            "QO/RVD4i3dXgWvkBEZATTjP7p+jiYYIItY6KJSTbcnYx+9TOBlYChwzDymQwe7n2OdaQIi9oXLA5" +
            "RpUin4hABVEcvrCICxtz6TspKgbY13NmDvP6hve0hIHTRgSLlNcCNIcgfVSN0MkrKNBkEAzRZSjW" +
            "M2CteS9aYAR341k7HQoFy29MgryOBSMQsFSFZC5nj1m3wm0gNarFgF+7DuFZfQKBgQD7ZLxGjWeI" +
            "aiNq8N9F61V72jd+AbmZgvezUdJIX9KAaSh47rkVjVJ5wFEt3Ad1Oxrq5vCfYPxzku294H4eHV9v" +
            "BtDt2y8bq2FmlPd+CapJfGRQdfJWnNWy6NQ9wHBfcxCwoMhEjs+E3IrE1+skXLvEOp8yamUr71YK" +
            "6NF0T98ttwKBgQC6m9AhWKJ2me2HGnh6SyLC5/B6meHCEI4c/6QNgS8FRC8mX7vCrp6bBFuJ6AnY" +
            "gr21i/xmeWh/DuVcnTOEGnQbi/iPJ/ZGyDio59cGWY+ooegYMedZO0er4gXbD6BWK4BcLu8VL2lf" +
            "0ICAA3LK6H9rl9MbV7FJ7r7oKTWTnqQFSwKBgQC75dczhI1V0L8YN2EqiXMjVz8S14c5zkIFrapW" +
            "nghgIjk16ng8O5zhk3UZqdOJM9wyptpCxeLrH27C9QBO0fV6rsWKgsD7FWV/nug9NEwrEOaJnR68" +
            "zgyL5Kp+XU+giAh5fDMzyuogjBbMefYu/4D4cjh7HjrwU7sj/FBRd0GAkQKBgEfldGEEp0OOtsdQ" +
            "2Y40mKYDetGhrCt6+WYO0IQre5RJ2uisBclSQeVz3ljZv8FdTf4+evZ4XFxnvtLZ9lk1X/qlCRUO" +
            "zoIi7kGVYXEe3IyaJLP9tibZAholMSBfeuT4SbI6mv7Mj4rM6FVp/rZSsZUgn8NvG+1iQMdt6SiA" +
            "/KzVAoGAbLe9uTUdUBj87A4hDOkWe7O8DM29n+DlbpS3SkVO/JuNlpMu8DTOwTZO9hKGymQwU2B9" +
            "fUIWIUTPPWdB1ILkyGlE5LUay/CUhSNJ6RzILnAWcDXTRISmNVtnvL75HBqJlwZH/ElIo8VRxjLH" +
            "q6GlDw84yI5zFc54nMsT1kNAIYQ=";

    /**
     * 使用RSA私钥加密数据
     *
     * @param pubKeyInByte 打包的byte[]形式私钥
     * @param data         要加密的数据
     * @return 加密数据
     */
    public static byte[] encryptByRSA1(byte[] privKeyInByte, byte[] data) {
        try {
            PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(
                    privKeyInByte);
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
            Cipher cipher = Cipher.getInstance(mykeyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privKey);
            //				return cipher.doFinal(data);
            return doFinalMaxEncrypt(data, cipher);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 验证数字签名
     *
     * @param keyInByte 打包成byte[]形式的公钥
     * @param source    原文的数字摘要
     * @param sign      签名（对原文的数字摘要的签名）
     * @return 是否证实 boolean
     */
    public static boolean verify(byte[] keyInByte, byte[] source, byte[] sign) {
        try {
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            Signature sig = Signature.getInstance("SHA1withRSA");
            X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(keyInByte);
            PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
            sig.initVerify(pubKey);
            sig.update(source);
            return sig.verify(sign);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用RSA公钥解密
     *
     * @param privKeyInByte 公钥打包成byte[]形式
     * @param data          要解密的数据
     * @return 解密数据
     */
    public static byte[] decryptByRSA1(byte[] pubKeyInByte, byte[] data) {
        try {
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
            PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
            Cipher cipher = Cipher.getInstance(mykeyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            //				return cipher.doFinal(data);
            return doFinalMaxDecrypt(data, cipher);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算字符串的SHA数字摘要，以byte[]形式返回
     */
    public static byte[] MdigestSHA(String source) {
        //byte[] nullreturn = { 0 };
        try {
            MessageDigest thisMD = MessageDigest.getInstance("SHA");
            byte[] digest = thisMD.digest(source.getBytes("UTF-8"));
            return digest;
        } catch (Exception e) {
            return null;
        }
    }

    //生成秘钥对
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    //获取公钥(Base64编码)
    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byte2Base64(bytes);
    }

    //获取私钥(Base64编码)
    public static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }

    //将Base64编码后的公钥转换成PublicKey对象
    public static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = base642Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    //将Base64编码后的私钥转换成PrivateKey对象
    public static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = base642Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    //公钥加密
    public static byte[] publicEncrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //		byte[] bytes = cipher.doFinal(content);
        byte[] bytes = doFinalMaxEncrypt(content.getBytes(), cipher);
        return bytes;
    }

    //私钥解密
    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //		byte[] bytes = cipher.doFinal(content);
        byte[] bytes = doFinalMaxDecrypt(content, cipher);
        return bytes;
    }

    //字节数组转Base64编码
    public static String byte2Base64(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    //Base64编码转字节数组
    public static byte[] base642Byte(String base64Key) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64Key);
    }

    /**
     * 字段长度过长 分段加密
     *
     * @param data
     * @param cipher
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @author hgh
     */
    public static byte[] doFinalMaxEncrypt(byte[] data, Cipher cipher) {
        try {
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字段长度过长 分段解密
     *
     * @param data
     * @param cipher
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @author hgh
     */
    public static byte[] doFinalMaxDecrypt(byte[] data, Cipher cipher) {
        try {
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

