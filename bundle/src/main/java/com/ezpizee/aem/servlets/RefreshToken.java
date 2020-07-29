package com.ezpizee.aem.servlets;

import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.CookieUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import java.io.IOException;

import static com.ezpizee.aem.Constants.*;

@SlingServlet(
    paths = {"/bin/ezpizee/refresh/token"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class RefreshToken extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AppConfig appConfig;

    @Reference
    private AccessToken accessToken;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(HEADER_VALUE_JSON);
        Response ezResponse = new Response();

        if (appConfig != null && isLogin(request, response)) {
            accessToken.refresh(CookieUtil.getAuthCookie(request), request.getSession());
            ezResponse.setStatus("OK");
            ezResponse.setCode(200);
        }

        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.toString());
    }

    private boolean isLogin(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        String cookieVal = CookieUtil.getAuthCookie(request);
        if (StringUtils.isNotEmpty(cookieVal)) {
            Object sessionVal = request.getSession().getAttribute(cookieVal);
            if (sessionVal != null) {
                JsonObject user = getUserObject(sessionVal.toString());
                return user.size() > 0;
            }
            else if (cookieVal.equals(KEY_EZPZ_LOGIN)) {
                CookieUtil.remove(request, response, KEY_EZPZ_LOGIN);
            }
        }
        return false;
    }

    private static JsonObject getUserObject(String jsonStr) {
        JsonObject object = new JsonObject();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(jsonStr);
            if (jsonElement != null) {
                object = jsonElement.getAsJsonObject();
                if (jsonElement.getAsJsonObject().has("user")) {
                    object = object.get("user").getAsJsonObject();
                }
                else {
                    object = new JsonObject();
                }
            }
        }
        catch (JsonSyntaxException e) {
            object = new JsonObject();
        }
        return object;
    }
}