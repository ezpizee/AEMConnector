package com.ezpizee.aem.datasource;

public class DisplayTemplates extends Fields {

    @Override
    public void activate() throws Exception {
        this.dataPath = "data/hbs.json";
        super.activate();
    }
}
