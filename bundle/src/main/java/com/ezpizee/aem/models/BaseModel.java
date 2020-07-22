package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AdminService;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;

import static com.ezpizee.aem.Constants.KEY_ACCESS_TOKEN;
import static com.ezpizee.aem.Constants.KEY_EZPZ_LOGIN;

public class BaseModel extends WCMUsePojo {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected ResourceResolver resolver;

    @Override
    public void activate() throws Exception {
        AdminService adminService = getSlingScriptHelper().getService(AdminService.class);
        if (adminService != null) {
            resolver = adminService.getResourceResolver(Constants.EZPIZEE_SERVICE);
        }
        exec();
    }

    protected void exec() {}

    protected String getAuthCookie() {
        Cookie cookie = getRequest().getCookie(KEY_EZPZ_LOGIN);
        if (cookie != null) {
            return cookie.getValue();
        }
        return KEY_ACCESS_TOKEN;
    }
}
