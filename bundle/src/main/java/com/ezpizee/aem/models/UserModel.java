package com.ezpizee.aem.models;


import com.adobe.cq.sightly.WCMUsePojo;

public class UserModel extends WCMUsePojo {

    private boolean isRegistered;
    private String userId;

    @Override
    public void activate() throws Exception {
        process();
    }

    public void process() {
        isRegistered = true;
        userId = "123456789";
    }

    public boolean isRegistered() {return isRegistered;}

    public String getUserId() {return userId;}
}
