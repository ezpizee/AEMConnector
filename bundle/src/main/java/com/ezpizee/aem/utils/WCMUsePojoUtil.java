package com.ezpizee.aem.utils;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.Arrays;

public class WCMUsePojoUtil {

    public static final String KEY_ITEMS = "items";
    public static final String KEY_URL = "url";
    public static final String KEY_LINK = "link";
    public static final String KEY_IS_EXTERNAL = "isExternal";
    public static final String KEY_IS_CURRENT_PAGE = "currentPage";
    public static final String KEY_FRAGMENT = "fragment";

    private WCMUsePojoUtil() {}

    public static JSONObject loadData(Resource resource) throws Exception { return loadData(null, resource, null); }

    public static JSONObject loadData(Resource resource, JSONObject mockedData) throws Exception { return loadData(null, resource, mockedData); }

    public static JSONObject loadData(SlingHttpServletRequest request,
                                      Resource resource,
                                      JSONObject mockedData) throws Exception {

        JSONObject data = ResourceUtil.toJSONObject(resource);

        for (String propName : data.keySet()) {

            Object object = data.get(propName);

            if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray)object;
                for (int i = 0; i < jsonArray.size(); i++) {
                    object = jsonArray.get(i);
                    if (object instanceof String && !StringUtils.isNumeric((String)object)) {
                        if (DataUtil.isJSONObjectString((String)object)) {
                            jsonArray.set(i, DataUtil.toJSONObject((String)object));
                        }
                        else if (DataUtil.isJSONArrayString((String)object)) {
                            jsonArray.set(i, DataUtil.toJSONArray((String)object));
                        }
                    }
                }
                data.put(propName, jsonArray);
            }
            else if (object instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject)object;
                for (String k : jsonObject.keySet()) {
                    object = jsonObject.get(k);
                    if (object instanceof String && !StringUtils.isNumeric((String)object)) {
                        if (DataUtil.isJSONObjectString((String)object)) {
                            jsonObject.put(k, DataUtil.toJSONObject((String)object));
                        }
                        else if (DataUtil.isJSONArrayString((String)object)) {
                            jsonObject.put(k, DataUtil.toJSONArray((String)object));
                        }
                    }
                }
                data.put(propName, jsonObject);
            }
            else if (object instanceof String) {
                data.put(propName, object);
            }
            else if (object instanceof String[]) {
                JSONArray arr = new JSONArray();
                arr.addAll(Arrays.asList((String[]) object));
                data.put(propName, arr);
            }
        }

        if (data.containsKey(KEY_ITEMS)) {
            Object object = data.get(KEY_ITEMS);
            JSONArray newItems = new JSONArray();
            if (object instanceof String) {
                JSONObject item = DataUtil.toJSONObject((String)object);
                newItems.add(jsonObjectItem(item));
            }
            else if (object instanceof JSONArray) {
                JSONArray items = (JSONArray) object;
                if (items.size() > 0) {
                    for (Object item : items) {
                        newItems.add(jsonObjectItem((JSONObject)item));
                    }
                }
            }
            if (!newItems.isEmpty() && request != null) {
                setActiveItem(newItems, request.getPathInfo());
                data.put(KEY_ITEMS, newItems);
            }
        }

        if (mockedData != null) {
            for (String key : mockedData.keySet()) {
                if (!data.containsKey(key) && mockedData.containsKey(key)) {
                    data.put(key, mockedData.get(key));
                }
            }
        }

        return data;
    }

    private static void setActiveItem(JSONArray jsonArray, String currentPagePath) {

        if (!jsonArray.isEmpty()) {
            for (Object object : jsonArray) {
                JSONObject item = (JSONObject)object;
                if (item.containsKey(KEY_URL)) {
                    item.put(KEY_IS_CURRENT_PAGE, item.get(KEY_URL).toString().equals(currentPagePath));
                }
                else if (item.containsKey(KEY_LINK)) {
                    item.put(KEY_IS_CURRENT_PAGE, item.get(KEY_LINK).toString().equals(currentPagePath));
                }
            }
        }
    }

    private static JSONObject jsonObjectItem(JSONObject item) {
        if (item.containsKey(KEY_URL)) {
            item.put(KEY_URL, PathUtil.url(item.get(KEY_URL).toString()));
            item.put(KEY_IS_EXTERNAL, PathUtil.isExternalLink(item.get(KEY_URL).toString()));
        }
        else if (item.containsKey(KEY_LINK)) {
            item.put(KEY_LINK, PathUtil.url(item.get(KEY_LINK).toString()));
            item.put(KEY_IS_EXTERNAL, PathUtil.isExternalLink(item.get(KEY_LINK).toString()));
        }
        return item;
    }
}