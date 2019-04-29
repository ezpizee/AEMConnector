package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import com.ezpizee.aem.utils.AppConfigLoader;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@SlingServlet(
    paths = {Constants.SERVLET_DELETE},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class DeleteServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected final void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(Constants.HEADER_VALUE_JSON);
        Response responseObject = new Response();
        final String endpoint = request.getParameterMap().containsKey(Constants.KEY_ENDPOINT) ? request.getParameter(Constants.KEY_ENDPOINT) : StringUtils.EMPTY;
        final String id = request.getParameterMap().containsKey(Constants.KEY_ID) ? request.getParameter(Constants.KEY_ID) : StringUtils.EMPTY;
        final String hashedAppName = request.getParameterMap().containsKey(Constants.KEY_HASHED_APP_NAME) ? request.getParameter(Constants.KEY_HASHED_APP_NAME) : StringUtils.EMPTY;
        final AppConfigLoader appConfigLoader = new AppConfigLoader(request.getResourceResolver(), request.getSession());
        final AppConfig appConfig = appConfigLoader.getAppConfig();
        final Client client = new Client(appConfig);
        if (StringUtils.isNotEmpty(endpoint) && StringUtils.isNotEmpty(id)) {
            client.addHeader(Constants.HEADER_PARAM_DELETE_ID, id);
            responseObject = client.delete(endpoint);
        }
        else if (StringUtils.isNotEmpty(hashedAppName) && appConfig.isValid()) {
            client.addHeader(Constants.HEADER_PARAM_CLIENT_ID, appConfig.getClientId());
            client.addHeader(Constants.HEADER_PARAM_HASHED_APP_NAME, hashedAppName);
        }
        else {
            responseObject.setStatusCode(200);
            responseObject.setSuccess(false);
            responseObject.setMessage("invalid_request");
            LOG.debug(request.getRequestParameterMap().toString());
        }

        response.getWriter().write(responseObject.toString());
    }
}