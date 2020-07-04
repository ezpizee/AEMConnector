package com.ezpizee.aem.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sothea Nim
 *
 * Utility class for data conversion
 */

public class DataUtil {

    private DataUtil() {}

    public static Map<String, Object> valueMap2Map(final ValueMap valueMap) {
        final Map<String, Object> map = new HashMap<>();
        for (String key : valueMap.keySet()) {
            map.put(key, valueMap.get(key));
        }
        return map;
    }

    public static boolean isJsonObjectString(String str) {
        boolean flag;
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(str).getAsJsonObject();
            flag = jsonObject != null;
        }
        catch (JsonSyntaxException e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isJsonArrayString(String str) {
        boolean flag;
        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(str).getAsJsonArray();
            flag = jsonArray != null;
        }
        catch (JsonSyntaxException e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isJsonString(String str) {
        boolean flag = DataUtil.isJsonObjectString(str);
        if (!flag) {
            flag = DataUtil.isJsonArrayString(str);
        }
        return flag;
    }

    public static JsonArray toJsonArray(String str) {
        JsonParser parser = new JsonParser();
        JsonArray jsonArray;
        try {
            jsonArray = parser.parse(str).getAsJsonArray();
        }
        catch (JsonSyntaxException e) {
            jsonArray = null;
        }
        return jsonArray;
    }

    public static JsonObject toJsonObject(String str) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject;
        try {
            jsonObject = parser.parse(str).getAsJsonObject();
        }
        catch (JsonSyntaxException e) {
            jsonObject = null;
        }
        return jsonObject;
    }

    public static JsonArray toJsonArray(String[] arr) {
        JsonArray jsonArray = new JsonArray();
        for (String s : arr) {
            jsonArray.add(s);
        }
        return jsonArray;
    }

    public static Map<String, Object> map2MapObject(Map<String, String> map) {
        Map<String, Object> data = new HashMap<>();
        for (String key : map.keySet()) {
            data.put(key, map.get(key));
        }
        return data;
    }

    public static Map<String, String> jsonObject2MapString(JsonObject object) {
        return (new Gson().fromJson(object, new TypeToken<HashMap<String, String>>() {}.getType()));
    }

    public static Map<String, Object> jsonObject2MapObject(JsonObject object) {
        return (new Gson().fromJson(object, new TypeToken<HashMap<String, Object>>() {}.getType()));
    }

    public static Map<String, String> map2MapString(Map<String, Object> map) {
        Map<String, String> data = new HashMap<>();
        for (String key : map.keySet()) {
            data.put(key, map.get(key).toString());
        }
        return data;
    }
}