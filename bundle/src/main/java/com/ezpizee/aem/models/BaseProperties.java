package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.security.Jwt;
import com.ezpizee.aem.utils.AdminServiceUtil;
import com.ezpizee.aem.utils.NodeUtil;
import com.ezpizee.aem.utils.RunModesUtil;
import com.ezpizee.aem.utils.WCMUsePojoUtil;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaseProperties extends WCMUsePojo {

    protected static AppConfig appConfig;
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected SlingSettingsService slingSettingsService;
    protected ResourceResolverFactory resolverFactory;
    protected boolean isAuthor=false, isPublish=false, isStage=false, isProd=false, isLocal=false, isDev=false, isQA=false, isStageOrProd=false;
    protected JSONObject data = new JSONObject();

    @Override
    public void activate() throws Exception {
        slingSettingsService = getSlingScriptHelper().getService(SlingSettingsService.class);
        setRunModes();
        this.loadData();
    }

    protected final ResourceResolver getResourceResolver(String systemUser) {
        ResourceResolver resolver = null;
        if (resolverFactory == null) { resolverFactory = getSlingScriptHelper().getService(ResourceResolverFactory.class); }
        if (resolverFactory != null) { resolver = AdminServiceUtil.getResourceResolver(resolverFactory, systemUser); }
        return resolver;
    }

    protected final void loadData(Resource resource) throws Exception {
        data = WCMUsePojoUtil.loadData(getRequest(), resource, mockedData());
    }

    protected final void loadData() throws Exception {
        data = WCMUsePojoUtil.loadData(getRequest(), getResource(), mockedData());
    }

    public final JSONObject getData() { return data; }

    protected JSONObject mockedData() {
        return null;
    }

    private void setRunModes() {
        isAuthor = RunModesUtil.isAuthor(slingSettingsService);
        if (!isAuthor) {
            isPublish = RunModesUtil.isPublish(slingSettingsService);
        }
        isLocal = RunModesUtil.isLocal(slingSettingsService);
        if (!isLocal) {
            isDev = RunModesUtil.isDev(slingSettingsService);
            if (!isDev) {
                isQA = RunModesUtil.isQA(slingSettingsService);
                if (!isQA) {
                    isStage = RunModesUtil.isStage(slingSettingsService);
                    if (!isStage) {
                        isProd = RunModesUtil.isProd(slingSettingsService);
                    }
                }
            }
        }
        isStageOrProd = isStage || isProd;
    }

    protected AppConfig getAppConfig() {
        if (appConfig == null || !appConfig.isValid()) {
            Resource resource = getResourceResolver().getResource(Constants.APP_CONFIG_PATH);
            if (resource != null) {
                ValueMap props = resource.getValueMap();
                appConfig = new AppConfig(props);
            }
            else {
                Map<String, String> props = new HashMap<>();
                appConfig = new AppConfig(props);
            }
        }
        if (appConfig.isValid()) {
            Object obj = getRequest().getSession().getAttribute(AppConfig.KEY_ACCESS_TOKEN);
            if (obj != null) {
                appConfig.setAccessToken((String)obj);
            }
            else {
                loadAccessToken();
            }
        }
        return appConfig;
    }

    private void loadAccessToken() {
        if (appConfig != null && appConfig.isValid()) {
            Client client = new Client(appConfig);
            client.setRequiredAccessToken(false);
            client.setAuth(appConfig.getClientId(), appConfig.getClientSecret());
            client.setHeader(Constants.HEADER_PARAM_CTYPE, Constants.HEADER_VALUE_FORM);
            client.setHeader(Constants.HEADER_PARAM_JWT, Jwt.clientTokenForAccessTokenRequest(
                appConfig.getEnv(), appConfig.getPublicKey(), appConfig.getClientId(), appConfig.getPhrase(), appConfig.getAppName()
            ));
            Map<String, Object> formParams = new HashMap<>();
            formParams.put("grant_type", "client_credentials");
            client.setFormParams(formParams);
            Response response = client.post(Constants.ENDPOINT_GET_TOKEN);
            if (response.isSuccess()) {
                JSONObject data = response.getDataAsJSONObject();
                if (data.containsKey(AppConfig.KEY_ACCESS_TOKEN)) {
                    appConfig.setAccessToken((String)data.get(AppConfig.KEY_ACCESS_TOKEN));
                    getRequest().getSession().setAttribute(AppConfig.KEY_ACCESS_TOKEN, data.get(AppConfig.KEY_ACCESS_TOKEN));
                }
            }
        }
    }
}
