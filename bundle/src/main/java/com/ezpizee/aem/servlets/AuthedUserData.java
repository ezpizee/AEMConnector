package com.ezpizee.aem.servlets;


import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.AuthUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import java.io.IOException;

import static com.ezpizee.aem.Constants.HEADER_VALUE_JSON;
import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;

@SlingServlet(
    paths = {"/bin/ezpizee/user/auth/data"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class AuthedUserData extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AppConfig appConfig;

    @Reference
    private AccessToken accessToken;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(HEADER_VALUE_JSON);
        final Response ezResponse = new Response();
        final JsonObject object = new JsonObject();

        if (appConfig != null && accessToken != null) {
            accessToken.load(KEY_ACCESS_TOKEN, request.getSession());
            object.add("validAppConfig", new JsonPrimitive(appConfig.isValid()));
            final JsonObject user = AuthUtil.getUser(request);
            final boolean isAuthed = user.size() > 0 && user.has("id");
            if (appConfig.isValid() && isAuthed) {
                object.add("user", user);
            }
            object.add("isAuthed", new JsonPrimitive(isAuthed));
        }
        else {
            object.add("validAppConfig", new JsonPrimitive(false));
            object.add("isAuthed", new JsonPrimitive(false));
        }

        ezResponse.setData(object);
        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.getDataAsJsonObject().toString());
    }
}