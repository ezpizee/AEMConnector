package com.ezpizee.aem.utils;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Sothea Nim
 *
 * Utility class for data conversion
 */

public class DataUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DataUtil.class);

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

    public static Map<String, Object> jsonToMap(String jsonString) {
        if (DataUtil.isJsonArrayString(jsonString) || DataUtil.isJsonObjectString(jsonString)) {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(jsonString);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonToMap(jsonObject);
            }
        }
        return null;
    }

    public static Map<String, Object> jsonToMap(JsonObject json) {
        Map<String, Object> retMap = new HashMap<>();
        if(json != null) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JsonObject object) {
        Map<String, Object> map = new HashMap<>();

        for (String key : object.keySet()) {
            Object value = object.get(key);

            if(value instanceof JsonArray) {
                value = toList((JsonArray) value);
            }

            else if(value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }

            map.put(key, value);
        }

        return map;
    }

    public static List<Object> toList(JsonArray array) {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            if(value instanceof JsonArray) {
                value = toList((JsonArray) value);
            }

            else if(value instanceof JsonObject) {
                value = toMap((JsonObject) value);
            }
            list.add(value);
        }
        return list;
    }

    public static List<String> toList(String[] array) {return new ArrayList<>(Arrays.asList(array));}

    public static String getAuthor(TagManager tm, ValueMap props, Locale locale) {
        if (props != null && props.containsKey("author") && tm != null) {
            String author = ((String[])props.get("author"))[0];
            if (StringUtils.isNotEmpty(author) && author.startsWith("ezpizee:authors")) {
                Tag tag = tm.resolve(author);
                if (tag != null) {
                    String tagTitle = tag.getLocalizedTitle(locale);
                    return StringUtils.isNotEmpty(tagTitle) ? tagTitle : tag.getTitle();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    public static String removeTags(String string) {
        if (StringUtils.isEmpty(string)) {
            return string;
        }
        return string.replaceAll("<(.[^>]*)>", "");
    }

    public static boolean isHideInNav(Resource jcrContentResource) {
        return StringUtils.isNotEmpty(jcrContentResource.getValueMap().get("hideInNav", StringUtils.EMPTY));
    }

    public static boolean isHideInNav(Page page) { return isHideInNav(page.getContentResource()); }

    public static void loadList(List<Map<String, Object>> items, Resource resource, String nodeName) {
        if (resource != null && resource.hasChildren()) {
            Iterator<Resource> ir = resource.listChildren();
            while (ir.hasNext()) {
                Resource child = ir.next();
                Map<String, Object> props = valueMap2Map(child.getValueMap());
                for (String key : props.keySet()) {
                    if (props.get(key) instanceof String) {
                        props.put(key, props.get(key).toString());
                    }
                }
                props.put("id", UUID.randomUUID().toString());
                if (child.getChild(nodeName) != null) {
                    List<Map<String, Object>> children = new ArrayList<>();
                    loadList(children, child.getChild(nodeName), nodeName);
                    props.put("children", children);
                }
                items.add(props);
            }
        }
    }

    public static Resource getPageResource(Resource resource) {
        if (resource != null) {
            if (resource.getName().equals("jcr:content")) {
                return resource.getParent();
            }
            else if (resource.hasChildren() && resource.getChild("jcr:content") != null) {
                return resource;
            }
            else if (resource.getParent() != null && resource.getParent().getName().equals("jcr:content")) {
                return resource.getParent().getParent();
            }
            else {
                return getPageResource(resource.getParent());
            }
        }
        return resource;
    }
}