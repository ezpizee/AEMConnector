package com.ezpizee.aem.models;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.utils.ConfigUtil;
import net.minidev.json.JSONObject;

public class HBSTemplates extends BaseProperties {

    @Override
    public void activate() throws Exception {
        data = new JSONObject();
        data.put("hbs", hbs());
    }

    private String hbs() {
        //name="CSRF-Token"
        String hbs = ConfigUtil.getResource("hbs/templates.hbs")
            .replace("\"/{{", "\"{{")
            .replace("name=\"csrftoken\"", "name=\"CSRF-Token\"")
            .replace("\""+ Constants.FORM_COMMERCE_FORM_NAME, "\""+ Constants.FORM_WC_CONTENT_FORM_NAME);
        return hbs;
    }
}
