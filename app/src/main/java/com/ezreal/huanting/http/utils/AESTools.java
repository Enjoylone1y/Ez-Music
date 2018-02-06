package com.ezreal.huanting.http.utils;

import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESTools {

    private static final String INPUT = "2012171402992850";
    private static final String IV = "2012061402992850";
    private static final char[] CHARS = {48, 49, 50, 51, 52, 53, 54, 55,
            56, 57, 65, 66, 67, 68, 69, 70};

    public static String encrpty(String paramString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(INPUT.getBytes());
            byte[] stringBytes = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder(stringBytes.length * 2);
            for (byte stringByte : stringBytes) {
                stringBuilder.append(CHARS[((stringByte & 0xF0) >>> 4)]);
                stringBuilder.append(CHARS[(stringByte & 0xF)]);
            }
            String str = stringBuilder.toString();
            SecretKeySpec localSecretKeySpec = new SecretKeySpec(
                    str.substring(str.length() / 2).getBytes(), "AES");
            Cipher localCipher;
            localCipher = Cipher
                    .getInstance("AES/CBC/PKCS5Padding");
            localCipher.init(1, localSecretKeySpec,
                    new IvParameterSpec(IV.getBytes()));
            return URLEncoder.encode(
                    new String(BytesHandler.getChars(localCipher
                            .doFinal(paramString.getBytes()))),
                    "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}