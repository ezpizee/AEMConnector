package com.ezpizee.aem.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import static com.ezpizee.aem.Constants.KEY_EZPZ_LOGIN;

public class AuthUtil {

    public static JsonObject getUser(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        JsonObject object = new JsonObject();
        String cookieVal = CookieUtil.getAuthCookie(request);
        if (StringUtils.isNotEmpty(cookieVal)) {
            Object sessionVal = request.getSession().getAttribute(cookieVal);
            if (sessionVal != null) {
                try {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(sessionVal.toString());
                    if (jsonElement != null) {
                        object = jsonElement.getAsJsonObject();
                        if (object != null && object.has("user")) {
                            object = object.get("user").getAsJsonObject();
                        }
                        else {
                            object = new JsonObject();
                        }
                    }
                }
                catch (JsonSyntaxException e) { }
            }
            else if (cookieVal.equals(KEY_EZPZ_LOGIN)) {
                CookieUtil.remove(request, response, KEY_EZPZ_LOGIN);
            }
        }
        return object;
    }
}
