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
    paths = {Constants.SERVLET_EZPIZEE_API},
    methods = {HttpConstants.METHOD_GET, HttpConstants.METHOD_POST, HttpConstants.METHOD_DELETE, HttpConstants.METHOD_PUT},
    extensions = {"json"}
)
public class EzpizeeApiForward extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EzpizeeApiForward.class);
    private static final long serialVersionUID = 1L;
    private Response responseObject;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_POST);
        response.getWriter().write(responseObject.toString());
    }

    @Override
    protected final void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_GET);
        response.getWriter().write(responseObject.toString());
    }

    @Override
    protected final void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_PUT);
        response.getWriter().write(responseObject.toString());
    }

    @Override
    protected final void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response, HttpConstants.METHOD_DELETE);
        response.getWriter().write(responseObject.toString());
    }

    private void process(SlingHttpServletRequest request, SlingHttpServletResponse response, final String method) {
        response.setContentType(Constants.HEADER_VALUE_JSON);
        responseObject = new Response();
        final String endpoint = request.getParameterMap().containsKey(Constants.KEY_ENDPOINT) ? request.getParameter(Constants.KEY_ENDPOINT) : StringUtils.EMPTY;
        if (StringUtils.isNotEmpty(endpoint)) {
            final AppConfigLoader appConfigLoader = new AppConfigLoader(request.getResourceResolver(), request.getSession());
            final AppConfig appConfig = appConfigLoader.getAppConfig();
            final Client client = new Client(appConfig);

            switch (method) {
                case HttpConstants.METHOD_POST:
                    break;

                case HttpConstants.METHOD_GET:
                    responseObject = client.post(endpoint);
                    break;

                case HttpConstants.METHOD_PUT:
                    break;

                case HttpConstants.METHOD_DELETE:
                    responseObject.setCode(500);
                    responseObject.setStatus("ERROR");
                    responseObject.setMessage("invalid_request");
                    LOG.debug(request.getRequestParameterMap().toString());
                    break;
            }
        }
    }
}