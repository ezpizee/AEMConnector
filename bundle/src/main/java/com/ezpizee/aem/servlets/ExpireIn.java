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

        if (accessToken != null) {
            accessToken.load(CookieUtil.getAuthCookie(request), request.getSession());
            JsonObject object = new JsonObject();
            object.add(Constants.KEY_EXPIRE_IN, new JsonPrimitive(accessToken.expireIn()));
            ezResponse.setData(object);
            ezResponse.setStatus("OK");
            ezResponse.setCode(200);
        }
        else {
            ezResponse.setStatus("ERROR");
            ezResponse.setCode(500);
            ezResponse.setMessage("USER_IS_NOT_LOGGED_IN");
        }

        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.getDataAsJsonObject().toString());
    }
}