package com.ezpizee.aem.servlets;

import com.day.cq.i18n.I18n;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.utils.*;
import net.minidev.json.JSONObject;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Servlet to output Veeva manifest
 *
 * @author Sothea Nim (sothea.nim@webconsol.com)
 */

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
        JSONObject data;

        final AppConfigLoader appConfigLoader = new AppConfigLoader(request.getResourceResolver(), request.getSession());
        final Resource jcrRes = ResourceUtil.getJCRContentResource(request.getResourceResolver(), request.getResource());
        if (jcrRes != null) {
            final Map<String, Object> props = DataUtil.valueMap2Map(jcrRes.getValueMap());
            if (request.getRequestParameterMap().containsKey(Constants.KEY_EDIT_ID)) {
                props.put(Constants.KEY_EDIT_ID, request.getParameter(Constants.KEY_EDIT_ID));
            }
            // for getting list by parent id
            if (request.getRequestParameterMap().containsKey(Constants.KEY_PARENT_ID)) {
                JSONObject uriParams = new JSONObject();
                uriParams.put(Constants.KEY_ID, request.getParameter(Constants.KEY_PARENT_ID));
                props.put(Constants.KEY_REST_API_URI_PARAMS, uriParams);
            }
            // for getting data by id
            if (request.getRequestParameterMap().containsKey(Constants.KEY_ID)) {
                JSONObject uriParams = new JSONObject();
                uriParams.put(Constants.KEY_ID, request.getParameter(Constants.KEY_ID));
                props.put(Constants.KEY_REST_API_URI_PARAMS, uriParams);
                props.put(Constants.KEY_ID, request.getParameter(Constants.KEY_ID));
            }
            final CommerceDataUtil commerceDataUtil = new CommerceDataUtil();
            data = commerceDataUtil.fetch(appConfigLoader.getAppConfig(), props);
            if (data.containsKey(Constants.KEY_FIELD_LABELS)) {
                I18n i18n = new I18n(request);
                JSONObject fieldLabels = (JSONObject)data.get(Constants.KEY_FIELD_LABELS);
                for (String key : fieldLabels.keySet()) {
                    fieldLabels.put(key, i18n.get(fieldLabels.getAsString(key)));
                }
                data.put(Constants.KEY_FIELD_LABELS, fieldLabels);
            }
            for (String key : props.keySet()) {
                if (!key.startsWith("jcr:") && !key.startsWith("cq:") && !key.startsWith("sling:") && !Constants.KEY_FIELDS.equals(key)) {
                    if (!data.containsKey(key)) {
                        Object val = props.get(key);
                        if (val instanceof String) {
                            data.put(key, val);
                        }
                        else if (val instanceof String[]) {
                            data.put(key, DataUtil.toJSONArray((String[])val));
                        }
                    }
                }
            }
        }
        else {
            data = new JSONObject();
            data.put("error", "jcrPath is missing for: "+request.getResource().getPath());
        }
        response.getWriter().write(data.toJSONString());
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        response.setContentType(Constants.HEADER_VALUE_JSON);
        response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        Response responseObject = new Response();

        final JSONObject object = WCContentFormDataUtil.getJSONObject(request.getRequestParameterList(), Constants.FORM_WC_CONTENT_FORM_NAME);

        if (!object.isEmpty()) {
            final Resource jcrRes = ResourceUtil.getJCRContentResource(request.getResourceResolver(), request.getResource());
            if (jcrRes != null) {
                final ValueMap props = jcrRes.getValueMap();
                if (props.containsKey(Constants.KEY_FORM_API_ENDPOINT)) {
                    final AppConfigLoader appConfigLoader = new AppConfigLoader(request.getResourceResolver(), request.getSession());
                    final Client client = new Client(appConfigLoader.getAppConfig());
                    LOG.debug("formData: {}", object.toString());
                    client.setBody(object.toJSONString());
                    responseObject = client.post(props.get(Constants.KEY_FORM_API_ENDPOINT).toString());
                }
                else {
                    LOG.debug("props: {}", props.toString());
                }
            }
            else {
                LOG.debug("jcrRes is null: true");
            }
        }
        else {
            LOG.debug("object is empty: {}", object.toString());
        }

        response.getWriter().write(responseObject.toString());
    }
}