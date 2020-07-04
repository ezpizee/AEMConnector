package com.ezpizee.aem.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
        JsonObject object = new JsonObject();
        object.add("token", new JsonPrimitive(token));
        object.add("jti", new JsonPrimitive(jti));
        object.add("env", new JsonPrimitive(env));
        object.add("appName", new JsonPrimitive(appName));
        object.add("issuer", new JsonPrimitive(issuer));
        object.add("audience", new JsonPrimitive(audience));
        object.add("ssh", new JsonPrimitive(ssh));
        object.add("client", new JsonPrimitive(client.toString()));
        object.add("access_token", new JsonPrimitive(access_token));
        return object.getAsString();
    }

    public class Client {
        public String client_id;
        public String client_secret;
        public String phrase;

        public String toString() {
            JsonObject object = new JsonObject();
            object.add("client_id", new JsonPrimitive(client_id));
            object.add("client_secret", new JsonPrimitive(client_secret));
            object.add("phrase", new JsonPrimitive(phrase));
            return object.getAsString();
        }
    }
}
