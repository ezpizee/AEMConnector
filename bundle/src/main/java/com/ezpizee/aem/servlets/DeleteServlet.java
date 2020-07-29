package com.ezpizee.aem.servlets;

import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AppConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.IOException;

import static com.ezpizee.aem.Constants.HEADER_VALUE_JSON;
import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;
import static com.ezpizee.aem.Constants.KEY_ENDPOINT;
import static com.ezpizee.aem.Constants.KEY_EZPZ_LOGIN;

@SlingServlet(
    paths = {"/bin/ezpizee/delete"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class DeleteServlet extends SlingAllMethodsServlet {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final long serialVersionUID = 1L;

    @Reference
    private AppConfig appConfig;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(HEADER_VALUE_JSON);
        Response ezResponse = new Response();
        final String endpoint = request.getParameterMap().containsKey(KEY_ENDPOINT) ? request.getParameter(KEY_ENDPOINT) : StringUtils.EMPTY;
        final String authCookie = getAuthCookie(request);
        appConfig.load(authCookie,  request.getSession());
        final Client client = new Client(appConfig);
        if (StringUtils.isNotEmpty(endpoint)) {
            ezResponse = client.delete(endpoint);
        }
        else {
            ezResponse.setCode(500);
            ezResponse.setStatus("ERROR");
            ezResponse.setMessage("invalid_request");
            logger.debug(request.getRequestParameterMap().toString());
        }
        if (ezResponse.getCode() != 200 && "INVALID_ACCESS_TOKEN".equals(ezResponse.getMessage())) {
            appConfig.clearAccessTokenSession(authCookie, request.getSession());
        }
        response.setStatus(ezResponse.getCode());
        response.getWriter().write(ezResponse.toString());
    }

    private String getAuthCookie(SlingHttpServletRequest request) {
        Cookie cookie = request.getCookie(KEY_EZPZ_LOGIN);
        if (cookie != null) {
            return cookie.getValue();
        }
        return KEY_ACCESS_TOKEN;
    }
}