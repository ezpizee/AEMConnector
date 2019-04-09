package com.ezpizee.aem.security;

import net.minidev.json.JSONObject;

public class OpenSSLKeyChain {

    public String csr = "";
    public String cert = "";
    public String key = "";
    public String phrase = "";

    public String toString() {
        JSONObject object = new JSONObject();
        object.put("csr", csr);
        object.put("cert", cert);
        object.put("key", key);
        object.put("phrase", phrase);
        return object.toJSONString();
    }
}
