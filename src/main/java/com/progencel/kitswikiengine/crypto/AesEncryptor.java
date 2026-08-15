package com.progencel.kitswikiengine.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AesEncryptor {

    private static final String KEY_64 = "0awjAkCXEC1/swmKp+Hs/Q==";
    // You can replace this with your own custom 128-bit key (Base64 encoded).

    private static SecretKeySpec createKey()
    {
        byte[] keyBytes = Base64.getDecoder().decode(KEY_64);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String plainText) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        cipher.init(Cipher.ENCRYPT_MODE, createKey(),iv);
        byte[] encryptBytes = cipher.doFinal(plainText.getBytes());

        byte[] result = new byte[iv.getIV().length + encryptBytes.length];
        System.arraycopy(ivBytes, 0, result, 0, ivBytes.length);
        System.arraycopy(encryptBytes, 0, result, ivBytes.length, encryptBytes.length);

        return Base64.getEncoder().encodeToString(result);
    }

    public static String deEncrypt(String encryptedText) throws Exception
    {
        byte[] allBytes = Base64.getDecoder().decode(encryptedText);

        byte[] ivBytes = new byte[16];
        byte[] encryptBytes = new byte[allBytes.length - ivBytes.length];
        System.arraycopy(allBytes,0,ivBytes,0,ivBytes.length);
        System.arraycopy(allBytes,16,encryptBytes,0,encryptBytes.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, createKey(), new IvParameterSpec(ivBytes));
        byte[] plainBytes = cipher.doFinal(encryptBytes);
        return new String(plainBytes);
    }

}
