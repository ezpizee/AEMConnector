package com.ezpizee.aem.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sothea Nim
 *
 * Utility class for data conversion
 */

public class DataUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DataUtil.class);

    private DataUtil() {}

    public static Map<String, Object> toMapObject(final RequestParameterMap map) {
        final Map<String, Object> obj = new HashMap<>();
        for (String key : map.keySet()) {
            if (!"file".equals(key)) {
                final RequestParameter param = map.getValue(key);
                if (param != null) {
                    obj.put(key, param.getString());
                }
                else {
                    final RequestParameter[] params = map.getValues(key);
                    if (params != null) {
                        JsonArray jsonArray = new JsonArray();
                        for(RequestParameter p : params) {
                            jsonArray.add(p.getString());
                        }
                        obj.put(key, jsonArray.toString());
                    }
                }
            }
        }
        return obj;
    }

    public static JsonObject map2JsonObject(final Map<String, String> valueMap) {
        final JsonObject obj = new JsonObject();
        for (String key : valueMap.keySet()) {
            obj.add(key, new JsonPrimitive(valueMap.get(key)));
        }
        return obj;
    }

    public static Map<String, Object> valueMap2Map(final ValueMap valueMap) {
        final Map<String, Object> map = new HashMap<>();
        for (String key : valueMap.keySet()) {
            map.put(key, valueMap.get(key));
        }
        return map;
    }

    public static boolean isJsonObjectString(String str) {
        boolean flag = false;
        if (isJsonString(str)) {
            try {
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(str);
                flag = jsonElement.getAsJsonObject() != null;
            }
            catch (JsonSyntaxException e) {
                flag = false;
            }
        }
        return flag;
    }

    public static boolean isJsonArrayString(String str) {
        boolean flag = false;
        if (isJsonString(str)) {
            try {
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(str);
                flag = jsonElement.getAsJsonArray() != null;
            }
            catch (JsonSyntaxException e) {
                flag = false;
            }
        }
        return flag;
    }

    public static boolean isJsonString(String str) {
        boolean flag;
        Gson gson = new Gson();
        try {
            Object obj = gson.fromJson(str, Object.class);
            flag = obj != null;
        }
        catch (JsonSyntaxException e) {
            flag = false;
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