package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.AuthUtil;
import com.ezpizee.aem.utils.CookieUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;

import static com.ezpizee.aem.Constants.*;

@SlingServlet(
    paths = {"/bin/ezpizee/refresh/token"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class RefreshToken extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AppConfig appConfig;

    @Reference
    private AccessToken accessToken;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(HEADER_VALUE_JSON);
        final Response ezResponse = new Response();
        final JsonObject user = AuthUtil.getUser(request, response);

        if (appConfig != null && accessToken != null && user.size() > 0 && user.has("id")) {
            accessToken.refresh(CookieUtil.getAuthCookie(request), request.getSession());
            JsonObject object = new JsonObject();
            object.add(Constants.KEY_EXPIRE_IN, new JsonPrimitive(accessToken.expireIn()));
            ezResponse.setData(object);
            ezResponse.setStatus("OK");
            ezResponse.setCode(200);
        }
        else {
            JsonObject object = new JsonObject();
            object.add("app_config", new JsonPrimitive("appConfig is null - "+(appConfig == null)));
            object.add("access_token", new JsonPrimitive("accessToken is null - "+(accessToken == null)));
            ezResponse.setData(object);
            ezResponse.setMessage("USER_IS_NOT_LOGGED_IN");
        }

        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.getDataAsJsonObject().toString());
    }
}