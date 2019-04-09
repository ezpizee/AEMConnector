package com.ezpizee.aem.utils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sothea Nim
 *
 * Utility class for data conversion
 */

public class DataUtil {

    private DataUtil() {}

    public static boolean isJSONObjectString(String str) {
        boolean flag;
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(str);
            flag = jsonObject != null;
        }
        catch (ParseException e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isJSONArrayString(String str) {
        boolean flag;
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(str);
            flag = jsonArray != null;
        }
        catch (ParseException e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isJSONString(String str) {
        boolean flag = DataUtil.isJSONObjectString(str);
        if (!flag) {
            flag = DataUtil.isJSONArrayString(str);
        }
        return flag;
    }

    public static JSONArray toJSONArray(String str) {
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONArray jsonArray;
        try {
            jsonArray = (JSONArray) parser.parse(str);
        }
        catch (ParseException e) {
            jsonArray = null;
        }
        return jsonArray;
    }

    public static JSONObject toJSONObject(String str) {
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(str);
        }
        catch (ParseException e) {
            jsonObject = null;
        }
        return jsonObject;
    }

    public static JSONArray toJSONArray(String[] arr) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(Arrays.asList(arr));
        return jsonArray;
    }

    public static Map<String, Object> map2MapObject(Map<String, String> map) {
        Map<String, Object> data = new HashMap<>();
        for (String key : map.keySet()) {
            data.put(key, map.get(key));
        }
        return data;
    }

    public static Map<String, String> jsonObject2MapString(JSONObject object) {
        Map<String, String> data = new HashMap<>();
        for (String key : object.keySet()) {
            data.put(key, (String)object.get(key));
        }
        return data;
    }

    public static Map<String, Object> jsonObject2MapObject(JSONObject object) {
        Map<String, Object> data = new HashMap<>();
        for (String key : object.keySet()) {
            data.put(key, object.get(key));
        }
        return data;
    }

    public static Map<String, String> map2MapString(Map<String, Object> map) {
        Map<String, String> data = new HashMap<>();
        for (String key : map.keySet()) {
            data.put(key, map.get(key).toString());
        }
        return data;
    }
}