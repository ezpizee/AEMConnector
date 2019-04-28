package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.utils.*;
import net.minidev.json.JSONObject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            AppConfigLoader appConfigLoader = new AppConfigLoader(getResourceResolver(), getRequest().getSession());
            appConfig = appConfigLoader.getAppConfig();
        }
        return appConfig;
    }
}
