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
        final JsonObject object = new JsonObject();

        if (appConfig != null && accessToken != null) {
            if (user.size() > 0 && user.has("id")) {
                accessToken.refresh(CookieUtil.getAuthCookie(request), request.getSession());
                object.add(Constants.KEY_EXPIRE_IN, new JsonPrimitive(accessToken.expireIn()));
            }
            else {
                object.add(Constants.KEY_EXPIRE_IN, new JsonPrimitive(0));
            }
        }
        else {
            object.add(Constants.KEY_EXPIRE_IN, new JsonPrimitive(0));
        }

        ezResponse.setData(object);
        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.getDataAsJsonObject().toString());
    }
}