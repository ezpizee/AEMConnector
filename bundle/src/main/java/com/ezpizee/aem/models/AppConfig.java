package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;

public class AppConfig extends WCMUsePojo {

    private com.ezpizee.aem.services.AppConfig config;

    @Override
    public void activate() throws Exception {
        config = getSlingScriptHelper().getService(com.ezpizee.aem.services.AppConfig.class);
    }

    public com.ezpizee.aem.services.AppConfig getConfig() {return config;}
}
