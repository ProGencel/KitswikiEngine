package com.progencel.kitswikiengine.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacSigner {

    private static final String HMAC_KEY_BASE64 = "usLugVL+S2VHjZWyKIX0nw==";
    // You can replace this with your own custom 128-bit key (Base64 encoded).

    public static String calculate(String data) throws Exception
    {
        Mac mac = Mac.getInstance("HmacSHA256");
        byte[] keyBytes = Base64.getDecoder().decode(HMAC_KEY_BASE64);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes,"HmacSHA256");
        mac.init(keySpec);

        byte[] hmacBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static boolean verify(String data, String expectedHmac) throws Exception
    {
        String calcHmac = calculate(data);
        return calcHmac.equals(expectedHmac);
    }

}
