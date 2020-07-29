package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;
import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AdminService;
import com.ezpizee.aem.utils.CookieUtil;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected String getAuthCookie() {return CookieUtil.getAuthCookie(getRequest());}
}
