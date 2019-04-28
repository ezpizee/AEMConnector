package com.ezpizee.aem.models;

import com.ezpizee.aem.utils.ConfigUtil;
import net.minidev.json.JSONObject;

public class HBSTemplates extends BaseProperties {

    @Override
    public void activate() throws Exception {
        data = new JSONObject();
        data.put("hbs", ConfigUtil.getResource("hbs/templates.hbs").replace("\"/{{", "\"{{"));
    }
}
