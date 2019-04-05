package com.ezpizee.aem.models;

import com.ezpizee.aem.Constants;
import net.minidev.json.JSONObject;

public class PageProperties extends BaseProperties {

    @Override
    public void activate() throws Exception {
        data = new JSONObject();
        data.put("baseUrl", Constants.CONTENT_PATH_DASHBOARD);
        data.put("logo", Constants.IMG_LOGO);
        data.put("sitename",Constants.SITENAME);
    }
}
