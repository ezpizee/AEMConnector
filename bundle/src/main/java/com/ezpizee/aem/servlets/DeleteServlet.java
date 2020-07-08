package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
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

import java.io.IOException;

@SlingServlet(
    paths = {"/bin/ezpizee/delete"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class DeleteServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteServlet.class);
    private static final long serialVersionUID = 1L;

    @Reference
    private AppConfig appConfig;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(Constants.HEADER_VALUE_JSON);
        Response responseObject = new Response();
        final String endpoint = request.getParameterMap().containsKey(Constants.KEY_ENDPOINT) ? request.getParameter(Constants.KEY_ENDPOINT) : StringUtils.EMPTY;
        appConfig.load(request.getSession());
        final Client client = new Client(appConfig);
        if (StringUtils.isNotEmpty(endpoint)) {
            responseObject = client.delete(endpoint);
        }
        else {
            responseObject.setCode(500);
            responseObject.setStatus("ERROR");
            responseObject.setMessage("invalid_request");
            LOG.debug(request.getRequestParameterMap().toString());
        }
        response.setStatus(responseObject.getCode());
        response.getWriter().write(responseObject.toString());
    }
}