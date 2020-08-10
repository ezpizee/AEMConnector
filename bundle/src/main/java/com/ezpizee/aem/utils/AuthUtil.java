package com.ezpizee.aem.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

public class AuthUtil {

    public static JsonObject getUser(String key, SlingHttpServletRequest request) {
        JsonObject object = new JsonObject();
        String cookieVal = CookieUtil.getAuthCookie(key, request);
        if (StringUtils.isNotEmpty(cookieVal)) {
            Object sessionVal = request.getSession().getAttribute(cookieVal);
            object = getUser(sessionVal);
        }
        return object;
    }

    public static JsonObject getUser(SlingHttpServletRequest request) {
        String cookieVal = CookieUtil.getAuthCookie(request);
        if (StringUtils.isNotEmpty(cookieVal)) {
            Object sessionVal = request.getSession().getAttribute(cookieVal);
            return getUser(sessionVal);
        }
        return new JsonObject();
    }

    private static JsonObject getUser(Object sessionVal) {
        if (sessionVal != null) {
            try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(sessionVal.toString());
                if (jsonElement != null) {
                    JsonObject object = jsonElement.getAsJsonObject();
                    if (object != null && object.has("user")) {
                        return object.get("user").getAsJsonObject();
                    }
                }
            }
            catch (JsonSyntaxException e) { }
        }
        return new JsonObject();
    }
}
