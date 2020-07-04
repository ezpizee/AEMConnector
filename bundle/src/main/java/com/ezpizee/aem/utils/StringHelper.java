package com.ezpizee.aem.utils;

import java.nio.charset.StandardCharsets;

public class StringHelper {

    private StringHelper() {}

    // convert from UTF-8 -> internal Java String format
    public static String convertFromUTF8(String s) {
        return new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    // convert from internal Java String format -> UTF-8
    public static String convertToUTF8(String s) {
        return new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }
}