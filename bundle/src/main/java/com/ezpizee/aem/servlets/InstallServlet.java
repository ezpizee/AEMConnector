package com.ezpizee.aem.servlets;

import com.day.cq.i18n.I18n;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.*;
import com.google.gson.JsonObject;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.io.IOException;

import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;

@SlingServlet(
    paths = {"/bin/ezpizee/install"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"html"}
)
public class InstallServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private AppConfig appConfig;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(Constants.HEADER_VALUE_JSON);
        Response resp = new Response();
        final I18n i18n = new I18n(request);
        final String payload = FileUtil.bufferedReaderToString(request.getReader());
        if (DataUtil.isJsonObjectString(payload)) {
            final JsonObject data = DataUtil.toJsonObject(payload);
            if (data.has(Constants.KEY_ENV)) {
                final String endpoint = HostName.getAPIServer(data.get(Constants.KEY_ENV).getAsString()) + Endpoints.install();
                appConfig.load(data);
                final Client client = new Client(appConfig);
                resp = client.install(endpoint, data.toString());
                if (resp.isNotError() && resp.hasData()) {
                    appConfig.storeConfig();
                    appConfig.keepAccessTokenInSession(KEY_ACCESS_TOKEN, resp.getDataAsJsonObject(), request.getSession());
                }
            }
            else {
                resp.setCode(500);
                resp.setMessage(i18n.get("REQUIRED_FIELD_MISSING"));
            }
        }
        else {
            resp.setCode(500);
            resp.setMessage(i18n.get("INVALID_PAYLOAD"));
        }
        response.setStatus(resp.getCode());
        response.getWriter().write(response.toString());
    }
}