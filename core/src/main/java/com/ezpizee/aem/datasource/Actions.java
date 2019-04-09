package com.ezpizee.aem.datasource;

public class Actions extends Fields {

    @Override
    public void activate() throws Exception {
        this.dataPath = "data/actions.json";
        super.activate();
    }
}
