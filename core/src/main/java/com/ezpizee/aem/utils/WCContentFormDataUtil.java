package com.ezpizee.aem.utils;

import com.ezpizee.aem.Constants;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.sling.api.request.RequestParameter;

import java.util.List;

public class WCContentFormDataUtil {

    private WCContentFormDataUtil() {}

    public static JSONObject getJSONObject(List<RequestParameter> params) {
        JSONObject object = new JSONObject();
        for (RequestParameter param : params) {
            if (param.getName().startsWith(Constants.FORM_NAME)) {
                String fieldName = param.getName().replace(Constants.FORM_NAME+"[", "");
                if (fieldName.endsWith("[]")) {
                    fieldName = fieldName.replace("[]", "").replace("[", ".").replace("]", "");
                    String[] parts = fieldName.split("\\.");
                    if (parts.length == 1) {
                        if (!object.containsKey(parts[0])) {
                            object.put(parts[0], new JSONArray());
                        }
                        ((JSONArray)object.get(parts[0])).add(param.getString());
                    }
                    else if (parts.length == 2) {
                        if (!object.containsKey(parts[0])) {
                            object.put(parts[0], new JSONObject());
                            ((JSONObject)object.get(parts[0])).put(parts[1], new JSONArray());
                        }
                        else if (!((JSONObject)object.get(parts[0])).containsKey(parts[1])) {
                            ((JSONObject)object.get(parts[0])).put(parts[1], new JSONArray());
                        }
                        ((JSONArray)((JSONObject)object.get(parts[0])).get(parts[1])).add(param.getString());
                    }
                    else if (parts.length == 3) {
                        if (!object.containsKey(parts[0])) {
                            object.put(parts[0], new JSONObject());
                            ((JSONObject)object.get(parts[0])).put(parts[1], new JSONObject());
                            ((JSONObject)((JSONObject)object.get(parts[0])).get(parts[1])).put(parts[2], new JSONArray());
                        }
                        else if (!((JSONObject)object.get(parts[0])).containsKey(parts[1])) {
                            ((JSONObject)object.get(parts[0])).put(parts[1], new JSONObject());
                            ((JSONObject)((JSONObject)object.get(parts[0])).get(parts[1])).put(parts[2], new JSONArray());
                        }
                        else if (!((JSONObject)((JSONObject)object.get(parts[1])).get(parts[1])).containsKey(parts[2])) {
                            ((JSONObject)((JSONObject)object.get(parts[0])).get(parts[1])).put(parts[2], new JSONArray());
                        }
                        ((JSONArray)((JSONObject)((JSONObject)object.get(parts[0])).get(parts[1])).get(parts[2])).add(param.getString());
                    }
                }
                else {
                    fieldName = fieldName.replace("[", ".").replace("]", "");
                    String[] parts = fieldName.split("\\.");
                    if (parts.length == 1) {
                        object.put(parts[0], param.getString());
                    }
                    else if (parts.length == 2) {
                        if (!object.containsKey(parts[0])) {
                            object.put(parts[0], new JSONObject());
                        }
                        ((JSONObject)object.get(parts[0])).put(parts[1], param.getString());
                    }
                    else if (parts.length == 3) {
                        if (!object.containsKey(parts[0])) {
                            object.put(parts[0], new JSONObject());
                            ((JSONObject)object.get(parts[0])).put(parts[1], new JSONObject());
                        }
                        else if (!((JSONObject)object.get(parts[0])).containsKey(parts[1])) {
                            ((JSONObject)object.get(parts[0])).put(parts[1], new JSONObject());
                        }
                        ((JSONObject)((JSONObject)object.get(parts[0])).get(parts[1])).put(parts[2], param.getString());
                    }
                }
            }
        }
        return object;
    }
}
