package com.example.yzn;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESCipher {

    /**
     * 加密方法，使用key充当向量iv，增加加密算法的强度
     *
     * @param key 密钥
     * @param raw 需要加密的内容
     * @return
     */
    public static String encrypt(byte[] key, String raw) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance(Config.AES_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(Cipher.ENCRYPT_MODE, seckey, iv);
        byte[] result = cipher.doFinal(raw.getBytes());
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(result);
    }

    /**
     * 解密方法，使用key充当向量iv，增加加密算法的强度
     *
     * @param key 密钥
     * @param enc 待解密内容
     * @return
     */
    public static String decrypt(byte[] key, String enc) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
        Cipher cipher = Cipher.getInstance(Config.AES_ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(Cipher.DECRYPT_MODE, seckey, iv);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] result = cipher.doFinal(decoder.decode(enc));
        return new String(result);
    }

    public static void main(String[] args) throws Exception {
        //客户端代码
        String text = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAPGkxlAJPKR3BRxT\n" +
                "PIeB3pDv117j8XbpuEik5UIOlY3GUtAV1sad5NNDUAnP/DB80yAQ8ycm9Xdkutuo" +
                "f25Xlb7w0bRQNpfJlijx9eF8PsB6t63r8KAfWJlqbNHgN8AMK9P5XzVyN4YiEnUl" +
                "Jh/EYiwLiYzflNnmnnfRrI4nUo8fAgMBAAECgYEAvwTxm81heeV4Tcbi33/jUBG4" +
                "4BMzCzyA6DQp4wkiYju3tTS+Xq3seLEKcWdPxYi3YO7lODsM6j/fksrlSXXFMe1i" +
                "ZAF3FNuDVZPz2zdFYS8vh6kdlDHMJAUnU/POMMWJ880MQDtkwTuzH8Tao8OKcAP4" +
                "kc0QuG00wOrmuE+5gZECQQD9bqZkJsN+tj3+pxs57azy6B6gOqgm54/ujB+u63XU" +
                "rO9Sf57asgF4OfUFltaVhjlUMSrWcgp6f4HSy7hBSKJpAkEA9BeML5iDIHOgTIws" +
                "+ID55ELbzO7A/YtcYnUU09mkKCdonMXbXke+EhLApf5vX9ZmreoEfJCdsTnMEcQi" +
                "fkjkRwJBALpf2TXl2/cfhs/zjG45f+rTEVK8UFTsDklb+yDkQC87TnTZLbWfGr2T" +
                "wcFugDhOEXL9BYfXLiWQB6VB9Crug6ECQGEmTiFTbj0oSBCvaeauTsdO5PS3whAn" +
                "u2lkeBmpcfCZXsWm6hyoKTpARHTMw789Mjjd/1Mkq96xxkr76U6h7FkCQHRc2elg" +
                "Dh84wqHIptwa+moosVvd7aSzktuOB4CQRO10qKkSHVFuI+sl47A4KGzH/nX9ydUm" +
                "tpsTnQAlXwBczd4=";
        //随机生成16位aes密钥
        byte[] aesKey = SecureRandomUtil.getRandom(16).getBytes();
        System.out.println(SecureRandomUtil.getRandom(16));
        String encryptText = AESCipher.encrypt(aesKey, text);
        System.out.println("加密后:\n" + encryptText);
        String decryptText = AESCipher.decrypt(aesKey, encryptText);
        System.out.println("解密后:\n" + decryptText);
    }
}