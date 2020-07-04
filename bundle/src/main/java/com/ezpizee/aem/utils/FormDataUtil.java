package com.ezpizee.aem.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.sling.api.request.RequestParameter;

import java.util.List;

public class FormDataUtil {

    private FormDataUtil() {}

    public static JsonObject getJsonObject(List<RequestParameter> params, String formName) {
        JsonObject object = new JsonObject();
        for (RequestParameter param : params) {
            if (param.getName().startsWith(formName)) {
                String fieldName = param.getName().replace(formName+"[", "");
                if (fieldName.endsWith("[]")) {
                    fieldName = fieldName.replace("[]", "").replace("[", ".").replace("]", "");
                    String[] parts = fieldName.split("\\.");
                    if (parts.length == 1) {
                        if (!object.has(parts[0])) {
                            object.add(parts[0], new JsonArray());
                        }
                        ((JsonArray)object.get(parts[0])).add(param.getString());
                    }
                    else if (parts.length == 2) {
                        if (!object.has(parts[0])) {
                            object.add(parts[0], new JsonObject());
                            ((JsonObject)object.get(parts[0])).add(parts[1], new JsonArray());
                        }
                        else if (!((JsonObject)object.get(parts[0])).has(parts[1])) {
                            ((JsonObject)object.get(parts[0])).add(parts[1], new JsonArray());
                        }
                        ((JsonArray)((JsonObject)object.get(parts[0])).get(parts[1])).add(param.getString());
                    }
                    else if (parts.length == 3) {
                        if (!object.has(parts[0])) {
                            object.add(parts[0], new JsonObject());
                            ((JsonObject)object.get(parts[0])).add(parts[1], new JsonObject());
                            ((JsonObject)((JsonObject)object.get(parts[0])).get(parts[1])).add(parts[2], new JsonArray());
                        }
                        else if (!((JsonObject)object.get(parts[0])).has(parts[1])) {
                            ((JsonObject)object.get(parts[0])).add(parts[1], new JsonObject());
                            ((JsonObject)((JsonObject)object.get(parts[0])).get(parts[1])).add(parts[2], new JsonArray());
                        }
                        else if (!((JsonObject)((JsonObject)object.get(parts[1])).get(parts[1])).has(parts[2])) {
                            ((JsonObject)((JsonObject)object.get(parts[0])).get(parts[1])).add(parts[2], new JsonArray());
                        }
                        ((JsonArray)((JsonObject)((JsonObject)object.get(parts[0])).get(parts[1])).get(parts[2])).add(param.getString());
                    }
                }
                else {
                    fieldName = fieldName.replace("[", ".").replace("]", "");
                    String[] parts = fieldName.split("\\.");
                    if (parts.length == 1) {
                        object.add(parts[0], new JsonPrimitive(param.getString()));
                    }
                    else if (parts.length == 2) {
                        if (!object.has(parts[0])) {
                            object.add(parts[0], new JsonObject());
                        }
                        ((JsonObject)object.get(parts[0])).add(parts[1], new JsonPrimitive(param.getString()));
                    }
                    else if (parts.length == 3) {
                        if (!object.has(parts[0])) {
                            object.add(parts[0], new JsonObject());
                            ((JsonObject)object.get(parts[0])).add(parts[1], new JsonObject());
                        }
                        else if (!((JsonObject)object.get(parts[0])).has(parts[1])) {
                            ((JsonObject)object.get(parts[0])).add(parts[1], new JsonObject());
                        }
                        ((JsonObject)((JsonObject)object.get(parts[0])).get(parts[1])).add(parts[2], new JsonPrimitive(param.getString()));
                    }
                }
            }
        }
        return object;
    }
}
