package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.HostName;

import java.util.List;

public class PageProperties extends WCMUsePojo {

    private String cdnServer;
    private boolean validAppConfig;

    @Override
    public void activate() throws Exception {
        final AppConfig appConfig = getSlingScriptHelper().getService(AppConfig.class);
        if (appConfig != null) {
            appConfig.load(getRequest().getSession());
            validAppConfig = appConfig.isValid();
            cdnServer = HostName.getCDNServer(appConfig.getEnv());
        }
    }

    public boolean isValidAppConfig() {return validAppConfig;}

    public List<String> getEnvironments() {return Constants.ENVIRONMENTS;}

    public String getCdnServer() {return cdnServer;}
}
