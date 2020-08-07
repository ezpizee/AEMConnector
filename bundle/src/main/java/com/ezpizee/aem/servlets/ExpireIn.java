package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.utils.CookieUtil;
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

@SlingServlet(
    paths = {"/bin/ezpizee/user/auth/expire-in"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class ExpireIn extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AccessToken accessToken;

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(HEADER_VALUE_JSON);
        Response ezResponse = new Response();
        final JsonObject object = new JsonObject();

        if (accessToken != null) {
            accessToken.load(CookieUtil.getAuthCookie(request), request.getSession());
            object.add(Constants.KEY_EXPIRE_IN, new JsonPrimitive(accessToken.expireIn()));
        }
        else {
            object.add("access_token", new JsonPrimitive("null"));
        }

        ezResponse.setData(object);
        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.getDataAsJsonObject().toString());
    }
}