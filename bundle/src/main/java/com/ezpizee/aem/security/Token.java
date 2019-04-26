package com.ezpizee.aem.security;

import net.minidev.json.JSONObject;

public class Token {

    public String token = "";
    public String jti = "";
    public String env = "";
    public String appName = "";
    public String issuer = "";
    public String audience = "";
    public String ssh = "";
    public Client client;
    public String access_token = "";

    public Token() {
        client = new Client();
        client.client_id = "";
        client.client_secret = "";
        client.phrase = "";
    }

    public String toString() {
        JSONObject object = new JSONObject();
        object.put("token", token);
        object.put("jti", jti);
        object.put("env", env);
        object.put("appName", appName);
        object.put("issuer", issuer);
        object.put("audience", audience);
        object.put("ssh", ssh);
        object.put("client", client);
        object.put("access_token", access_token);
        return object.toJSONString();
    }

    public class Client {
        public String client_id;
        public String client_secret;
        public String phrase;

        public String toString() {
            JSONObject object = new JSONObject();
            object.put("client_id", client_id);
            object.put("client_secret", client_secret);
            object.put("phrase", phrase);
            return object.toJSONString();
        }
    }
}
