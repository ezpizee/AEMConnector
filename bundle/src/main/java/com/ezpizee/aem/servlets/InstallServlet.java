package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
@SlingServlet(
    paths = {Constants.SERVLET_INSTALL},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"html"}
)
public class InstallServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(InstallServlet.class);
    private static final long serialVersionUID = 1L;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(Constants.HEADER_VALUE_JSON);
        response.getWriter().write("TODO");
    }
}