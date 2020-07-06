package com.ezpizee.aem.models;

import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.utils.HostName;

import java.util.List;

public class PageProperties extends WCMUsePojo {

    private boolean validAppConfig;
    private AppConfig appConfig;
    private String cdnServer;

    @Override
    public void activate() throws Exception {
        if (appConfig == null) {
            appConfig = new AppConfig(getCurrentPage().getContentResource());
            validAppConfig = appConfig.isValid();
            cdnServer = HostName.getCDNServer(appConfig.getEnv());
        }
    }

    public boolean isValidAppConfig() {return validAppConfig;}

    public AppConfig getAppConfig() {return appConfig;}

    public List<String> getEnvironments() {return Constants.ENVIRONMENTS;}

    public String getCdnServer() {return cdnServer;}
}
