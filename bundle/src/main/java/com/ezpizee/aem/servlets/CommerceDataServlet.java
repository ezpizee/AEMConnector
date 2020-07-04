package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.utils.*;
import com.google.gson.JsonObject;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component(metatype = true, label = "Ezpizee Commerce Data Manifest Servlet", description = "For fetching commerce data", configurationFactory = true)
@Service
@SuppressWarnings("serial")
@Properties({
    @Property(name = "sling.servlet.resourceTypes", value = "cq:Page", label = "Sling Resource Type", description = "Sling Resource Type for build manifest.")
    ,@Property(name = "sling.servlet.selectors", value = "ezpz", propertyPrivate = true)
    ,@Property(name = "sling.servlet.extensions", value = "json", propertyPrivate = true)
    ,@Property(name = "sling.servlet.methods", value = {"GET", "POST"}, propertyPrivate = true)
})
public final class CommerceDataServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CommerceDataServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        response.setContentType(Constants.HEADER_VALUE_JSON);
        response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        JsonObject data;

        final AppConfigLoader appConfigLoader = new AppConfigLoader(request.getResourceResolver(), request.getSession());
        final Resource jcrRes = ResourceUtil.getJCRContentResource(request.getResourceResolver(), request.getResource());
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        response.setContentType(Constants.HEADER_VALUE_JSON);
        response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        Response responseObject = new Response();

        final JsonObject object = FormDataUtil.getJsonObject(request.getRequestParameterList(), Constants.FORM_WC_CONTENT_FORM_NAME);

        response.getWriter().write(responseObject.toString());
    }
}