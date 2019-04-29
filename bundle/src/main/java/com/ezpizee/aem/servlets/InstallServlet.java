package com.ezpizee.aem.servlets;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import com.ezpizee.aem.security.SSHAgent;
import com.ezpizee.aem.security.SSHFormData;
import com.ezpizee.aem.utils.DataUtil;
import com.ezpizee.aem.utils.NodeUtil;
import com.ezpizee.aem.utils.WCContentFormDataUtil;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        final Response responseObject = new Response();
        final JSONObject object = WCContentFormDataUtil.getJSONObject(request.getRequestParameterList(), Constants.FORM_WC_CONTENT_FORM_NAME);
        if (!object.isEmpty()) {
            AppConfig appConfig = new AppConfig(DataUtil.jsonObject2MapString(object));
            SSHFormData sshFormData = new SSHFormData(object);
            String publicKey = SSHAgent.generatePublicKey(appConfig, sshFormData);
            if (StringUtils.isNotEmpty(publicKey)) {
                // save
                Map<String, String> props = appConfig.toMap();
                props.put(AppConfig.KEY_PUBLIC_KEY, publicKey);
                Map<String, String> folderProps = new HashMap<>();
                folderProps.put("jcr:title", Constants.SITENAME);
                NodeUtil.addIfNotAlreadyExist(request.getResourceResolver(), Constants.ETC_COMMERCE_PATH, Constants.PROP_SLING_FOLDER, folderProps);
                NodeUtil.save(request.getResourceResolver(), Constants.APP_CONFIG_PATH, props);
                JSONObject data = new JSONObject();
                data.put("redirect", Constants.CONTENT_PATH+".html");
                responseObject.setData(data);
            }
            else {
                responseObject.setSuccess(false);
                responseObject.setStatusCode(500);
                responseObject.setMessage("Failed to generate SSH Keys. Failed to install the application.");
            }
        }
        response.getWriter().write(responseObject.toString());
    }
}