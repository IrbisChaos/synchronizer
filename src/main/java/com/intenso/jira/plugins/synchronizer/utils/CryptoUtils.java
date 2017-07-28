// 
// Decompiled by Procyon v0.5.30
// 

package com.intenso.jira.plugins.synchronizer.utils;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;

public class CryptoUtils
{
    private static final String HEX = "0123456789ABCDEF";
    
    public static void main(final String[] args) throws Exception {
        String seed = "com.intenso.jira";
        final String encrypted = encrypt(seed, "test");
        System.out.println(encrypted);
        System.out.println(decrypt(seed, encrypted));
        seed = "com.intenso.jira";
        System.out.println(decrypt(seed, "1D59B9A22A70728A2931094DF4561029"));
    }
    
    public static String encrypt(final String seed, final String cleartext) {
        try {
            final byte[] rawKey = getRawKey(seed.getBytes());
            final byte[] result = encrypt(rawKey, cleartext.getBytes());
            return toHex(result);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static String decrypt(final String seed, final String encrypted) {
        try {
            final byte[] rawKey = getRawKey(seed.getBytes());
            final byte[] enc = toByte(encrypted);
            final byte[] result = decrypt(rawKey, enc);
            return new String(result);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private static byte[] getRawKey(final byte[] seed) throws Exception {
        final KeyGenerator kgen = KeyGenerator.getInstance("AES");
        final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr);
        final SecretKey skey = kgen.generateKey();
        final byte[] raw = skey.getEncoded();
        return raw;
    }
    
    private static byte[] encrypt(final byte[] raw, final byte[] clear) throws Exception {
        final SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, skeySpec);
        final byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }
    
    private static byte[] decrypt(final byte[] raw, final byte[] encrypted) throws Exception {
        final SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, skeySpec);
        final byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
    
    public static String toHex(final String txt) {
        return toHex(txt.getBytes());
    }
    
    public static String fromHex(final String hex) {
        return new String(toByte(hex));
    }
    
    public static byte[] toByte(final String hexString) {
        final int len = hexString.length() / 2;
        final byte[] result = new byte[len];
        for (int i = 0; i < len; ++i) {
            result[i] = (byte)(Object)Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return result;
    }
    
    public static String toHex(final byte[] buf) {
        if (buf == null) {
            return "";
        }
        final StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; ++i) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    
    private static void appendHex(final StringBuffer sb, final byte b) {
        sb.append("0123456789ABCDEF".charAt(b >> 4 & 0xF)).append("0123456789ABCDEF".charAt(b & 0xF));
    }
}
