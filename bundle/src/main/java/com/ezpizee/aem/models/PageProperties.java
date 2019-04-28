package com.ezpizee.aem.models;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.utils.DateFormatUtil;
import com.ezpizee.aem.utils.EnvironmentEnum;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PageProperties extends BaseProperties {

    @Override
    public void activate() throws Exception {
        data = new JSONObject();
        data.put("baseUrl", Constants.CONTENT_PATH_DASHBOARD);
        data.put("logo", Constants.IMG_LOGO);
        data.put("sitename", Constants.SITENAME);
        data.put("copyright", "&copy; "+DateFormatUtil.getCurrentYear()+" "+Constants.SITENAME);
        data.put("version", Constants.VERSION);
        data.put("validAppConfig", getAppConfig().isValid());
        data.put("installServlet", Constants.SERVLET_INSTALL);

        Map<String, String> environments = new HashMap<>();
        for (EnvironmentEnum env : EnvironmentEnum.values()) {
            environments.put(env.name().toLowerCase(), env.name().toLowerCase());
        }
        data.put("environments", environments);
    }
}
