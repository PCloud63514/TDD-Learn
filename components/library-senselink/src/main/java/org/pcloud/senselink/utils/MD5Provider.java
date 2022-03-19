package org.pcloud.senselink.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Provider {
    public static String encode(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BigInteger i = new BigInteger(1, md5.digest(str.getBytes(StandardCharsets.UTF_8)));
            return String.format("%032x", i);
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}