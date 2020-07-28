package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;

public class AppConfig extends WCMUsePojo {

    private AppConfig appConfig;

    @Override
    public void activate() throws Exception {
        appConfig = getSlingScriptHelper().getService(AppConfig.class);
    }

    public AppConfig getAppConfig() {return appConfig;}
}
