package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;

public class AppConfig extends WCMUsePojo {

    private AppConfig config;

    @Override
    public void activate() throws Exception {
        config = getSlingScriptHelper().getService(AppConfig.class);
    }

    public String getData() {return config.toString();}

    public AppConfig getConfig() {return config;}
}
