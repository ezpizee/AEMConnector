package com.ezpizee.aem.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ezpizee.aem.Constants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

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
        return StringUtils.newStringUtf8(Base64.decodeBase64(StringUtils.getBytesUtf8(s)));
    }

    public static String base64Encode(String s) {
        return StringUtils.newStringUtf8(Base64.encodeBase64(StringUtils.getBytesUtf8(s)));
    }

    public static String base64Encode(byte[] bytes) {
        return StringUtils.newStringUtf8(Base64.encodeBase64(bytes));
    }

    public static String rawurlencode(String url) {
        try {
            return URLEncoder.encode(StringHelper.convertToUTF8(url), UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String rawurldecode(String url) {
        try {
            return URLDecoder.decode(StringHelper.convertToUTF8(url), UTF_8);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static Map<String, Object> jsonToMap(String jsonString) throws ParseException {
        if (DataUtil.isJSONArrayString(jsonString) || DataUtil.isJSONObjectString(jsonString)) {
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
            return jsonToMap(jsonObject);
        }
        return null;
    }

    public static Map<String, Object> jsonToMap(JSONObject json) {
        Map<String, Object> retMap = new HashMap<>();
        if(json != null) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) {
        Map<String, Object> map = new HashMap<>();

        for (String key : object.keySet()) {
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }

            map.put(key, value);
        }

        return map;
    }

    public static List<Object> toList(JSONArray array) {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}