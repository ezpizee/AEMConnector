package com.ezpizee.aem.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ezpizee.aem.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sothea Nim
 *
 * Utility class for hashing data
 */

public class HashUtil {

    private HashUtil() {}

    private static final String MD5TERM = "MD5";
    private static final String UTF_8 = Constants.UTF_8;
    private static final String MD5FORMATSTR = "%02x";
    private static MessageDigest md;

    public static String uuid() {return uuid(StringUtils.EMPTY);}

    public static String uuid(String pfx) {return pfx.toUpperCase()+UUID.randomUUID().toString().toUpperCase();}

    private static void setMd5() {
        if (md == null) {
            try {
                md = MessageDigest.getInstance(MD5TERM);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(HashUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String md5(String text) {

        setMd5();

        StringBuilder sb = new StringBuilder();
        try {
            byte[] digest = md.digest(text.getBytes(UTF_8));
            for (byte b : digest) {
                sb.append(String.format(MD5FORMATSTR, b & 0xff));
            }
        }
        catch(UnsupportedEncodingException ex) {
            Logger.getLogger(HashUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();
    }

    public static String base64Decode(String s) {
        return new String(Base64.getDecoder().decode(s));
    }

    public static String base64Encode(String s) {
        return new String(Base64.getEncoder().encode(s.getBytes()));
    }

    public static String base64Encode(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static String rawurlencode(String url) {
        try {
            return URLEncoder.encode(convertToUTF8(url), UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String rawurldecode(String url) {
        try {
            return URLDecoder.decode(convertToUTF8(url), UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    // convert from UTF-8 -> internal Java String format
    public static String convertFromUTF8(String s) {
        return new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    // convert from internal Java String format -> UTF-8
    public static String convertToUTF8(String s) {
        return new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }
}