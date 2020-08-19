package com.ezpizee.aem.servlets;

import com.day.cq.i18n.I18n;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.services.AccessToken;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.services.impl.AppConfigImpl;
import com.ezpizee.aem.utils.*;
import com.ezpizee.aem.utils.detection.UserAgentDetectionResult;
import com.ezpizee.aem.utils.detection.UserAgentDetector;
import com.google.gson.JsonObject;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import static com.ezpizee.aem.Constants.*;

@SlingServlet(
    paths = {"/bin/ezpizee/install"},
    methods = {HttpConstants.METHOD_POST},
    extensions = {"json"}
)
public class InstallServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Reference
    private AppConfig appConfig;

    @Reference
    private AccessToken accessToken;

    @Reference
    private ConfigurationAdmin configAdmin;

    @Reference
    private SlingSettingsService sss;

    @Override
    protected final void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType(Constants.HEADER_VALUE_JSON);
        Response resp = new Response();
        final I18n i18n = new I18n(request);
        final String payload = FileUtil.bufferedReaderToString(request.getReader());
        final String authCookie = CookieUtil.getAuthCookie(request);

        if (DataUtil.isJsonObjectString(payload)) {
            final JsonObject data = DataUtil.toJsonObject(payload);
            appConfig.setData(DataUtil.jsonObject2MapString(data));
            if (data.has(Constants.KEY_ENV) && appConfig.isValid()) {
                final Client client = new Client(appConfig);

                final UserAgentDetector uaDetector = new UserAgentDetector();
                final UserAgentDetectionResult userAgentDetectionResult = uaDetector.parseUserAgent(request.getHeader(HEADER_PARAM_USER_AGENT));
                client.addHeader(HEADER_PARAM_APP_PLATFORM, userAgentDetectionResult.getOperatingSystem().getFamily().getLabel());
                client.addHeader(HEADER_PARAM_OS_PLATFORM_VERSION, userAgentDetectionResult.getOperatingSystem().getVersion());

                final String existsEndpoint = HostName.getAPIServer(data.get(Constants.KEY_ENV).getAsString()) +
                    Endpoints.appExists(appConfig.getClientId(), HashUtil.md5(appConfig.getAppName()));
                resp = client.get(existsEndpoint);
                if (resp.isNotError()) {
                    if (resp.getDataAsJsonObject().has("exists") && !resp.getDataAsJsonObject().get("exists").getAsBoolean()) {
                        final String endpoint = HostName.getAPIServer(data.get(Constants.KEY_ENV).getAsString()) + Endpoints.install();
                        resp = client.install(endpoint, payload);
                        if (resp.isNotError() && resp.hasData()) {
                            accessToken.keepToken(resp, authCookie, request.getSession());
                        }
                    }
                    else {
                        accessToken.load(authCookie, request.getSession());
                        resp.setCode(500);
                        resp.setMessage(i18n.get("APP_ALREADY_EXISTS"));
                    }
                }
                else {
                    resp.setCode(500);
                    resp.setMessage(i18n.get("FAILED_TO_VALIDATE_APP_EXISTENCE"));
                }

                if (configAdmin != null && RunModesUtil.isAuthor(sss)) {
                    Configuration configuration = configAdmin.getConfiguration(AppConfigImpl.class.getName());
                    if (configuration != null) {
                        Dictionary<String, Object> properties = configuration.getProperties();
                        if (properties == null) {
                            properties = new Hashtable<>();
                        }
                        for(String key : data.keySet()) {
                            properties.put(key, data.get(key).getAsString());
                        }
                        configuration.update(properties);
                    }
                }
                else if (RunModesUtil.isPublish(sss)) {
                    appConfig.storeConfig();
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
        response.getWriter().write(resp.toString());
    }
}