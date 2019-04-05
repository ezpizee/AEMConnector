package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;

public class StringHelper {

    private StringHelper() {}

    // convert from UTF-8 -> internal Java String format
    public static String convertFromUTF8(String s) {
        String out;
        try {
            out = new String(s.getBytes(Constants.ISO_8859_1), Constants.UTF_8);
        } catch (java.io.UnsupportedEncodingException e) {
            out = null;
        }
        return out;
    }

    // convert from internal Java String format -> UTF-8
    public static String convertToUTF8(String s) {
        String out;
        try {
            out = new String(s.getBytes(Constants.UTF_8), Constants.ISO_8859_1);
        } catch (java.io.UnsupportedEncodingException e) {
            out = null;
        }
        return out;
    }
}