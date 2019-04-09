package com.ezpizee.aem.security;

import net.minidev.json.JSONObject;

import java.util.Map;

public class SSHFormData {

    public String client_id;
    public String app_name;
    public String phrase;

    public SSHFormData(Map<String, String> setting) {
        this.client_id = setting.get("client_id");
        this.app_name = setting.get("app_name");
        this.phrase = setting.get("phrase");
    }

    public SSHFormData(JSONObject setting) {
        this.client_id = (String)setting.get("client_id");
        this.app_name = (String)setting.get("app_name");
        this.phrase = (String)setting.get("phrase");
    }

    public String toString() {
        JSONObject object = new JSONObject();
        object.put("client_id", client_id);
        object.put("app_name", app_name);
        object.put("phrase", phrase);
        return object.toJSONString();
    }
}
