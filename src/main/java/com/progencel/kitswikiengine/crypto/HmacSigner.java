package com.progencel.kitswikiengine.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacSigner {

    private static String HMAC_KEY_BASE64 = "usLugVL+S2VHjZWyKIX0nw==";
    private static final int MIN_KEY_BYTES = 32;

    public static String calculate(String data) throws Exception
    {
        Mac mac = Mac.getInstance("HmacSHA256");
        byte[] keyBytes = Base64.getDecoder().decode(HMAC_KEY_BASE64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes,"HmacSHA256");
        mac.init(keySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static boolean verify(String data, String expectedHmac) throws Exception
    {
        String calcHmac = calculate(data);
        return calcHmac.equals(expectedHmac);
    }

    public static void setHmacKeyBase64(String key)
    {
        if(key == null || key.trim().isEmpty())
        {
            throw new IllegalArgumentException("HMAC key cannot be null or empty.");
        }
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(key);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid key format please try another one", e);
        }
        if (keyBytes.length < MIN_KEY_BYTES) {
            throw new IllegalArgumentException("HMAC key must be at least " + MIN_KEY_BYTES + " bytes long.");
        }

        HMAC_KEY_BASE64 = key;

    }

}
