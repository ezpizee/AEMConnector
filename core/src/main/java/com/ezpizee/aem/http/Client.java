package com.ezpizee.aem.http;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.models.AppConfig;
import com.ezpizee.aem.security.Jwt;
import com.ezpizee.aem.utils.EndpointUtil;
import com.ezpizee.aem.utils.HashUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Client
{
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private static final String KEY_AUTH = "Authorization";
    private Map<String, String> headers;
    private Map<String, Object> formParams;
    private String body;
    private Map<String, File> files;
    private String uri;
    private MethodEnum method;
    private boolean requiredAccessToken = true;
    private AppConfig appConfig;

    public Client(AppConfig config) {
        headers = new HashMap<>();
        appConfig = config;
        method = MethodEnum.GET;
    }

    public void setRequiredAccessToken(boolean b) { requiredAccessToken = b; }
    public void setAuth(String username, String password) { this.setHeader(KEY_AUTH, "Basic "+HashUtil.base64Encode(username+":"+password)); }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public void setHeader(String key, String value) { this.headers.put(key, value); }
    public void setFormParams(Map<String, Object> params) { formParams = params; }
    public void setBody(String body) { this.body = body; }
    public void setFiles(Map<String, File> files) { this.files = files; }

    public Response get(String uri) {
        this.uri = EndpointUtil.getService(this.appConfig.getEnv(), uri);
        this.method = MethodEnum.GET;
        return this.getRequest();
    }

    public Response post(String uri) {
        this.uri = EndpointUtil.getService(this.appConfig.getEnv(), uri);
        this.method = MethodEnum.POST;
        return this.request();
    }

    public Response delete(String uri) {
        this.uri = EndpointUtil.getService(this.appConfig.getEnv(), uri);
        this.method = MethodEnum.DELETE;
        return this.request();
    }

    public Response put(String uri) {
        this.uri = EndpointUtil.getService(this.appConfig.getEnv(), uri);
        this.method = MethodEnum.PUT;
        return this.request();
    }

    public Response patch(String uri) {
        this.uri = EndpointUtil.getService(this.appConfig.getEnv(), uri);
        this.method = MethodEnum.PATCH;
        return this.request();
    }

    public void disconnect() {
        try {
            Unirest.shutdown();
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private Response getRequest() {
        if (StringUtils.isNotEmpty(this.uri)) {
            this.defaultHeaders();
            try {
                GetRequest request = Unirest.get(this.uri);
                if (!this.headers.isEmpty()) { request.headers(this.headers); }
                HttpResponse<JsonNode> jsonResponse = request.asJson();
                LOG.info("Commerce Admin API Call: " + this.method + " " + this.uri);
                if (jsonResponse.getStatus() == 200) {
                    return new Response(jsonResponse.getBody().getObject().toString());
                }
            }
            catch (UnirestException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        else {
            LOG.error("uri is missing");
        }
        return new Response(StringUtils.EMPTY);
    }

    private Response request() {
        if (StringUtils.isNotEmpty(this.uri)) {
            this.defaultHeaders();
            try {
                HttpRequestWithBody request = null;
                if (MethodEnum.POST.equals(this.method)) {
                    request = Unirest.post(this.uri);
                }
                else if (MethodEnum.PUT.equals(this.method)) {
                    request = Unirest.put(this.uri);
                }
                else if (MethodEnum.DELETE.equals(this.method)) {
                    request = Unirest.delete(this.uri);
                }
                else if (MethodEnum.PATCH.equals(this.method)) {
                    request = Unirest.patch(this.uri);
                }
                if (request != null) {
                    if (formParams != null && !formParams.isEmpty()) {
                        for(String key : formParams.keySet()) {
                            request.field(key, formParams.get(key));
                        }
                    }
                    else if (StringUtils.isNotEmpty(this.body)) {
                        request.body(body);
                    }
                    if (files != null && !files.isEmpty()) {
                        for(String key : files.keySet()) {
                            request.field(key, files.get(key));
                        }
                    }
                    if (!this.headers.isEmpty()) {
                        request.headers(this.headers);
                    }
                    HttpResponse<JsonNode> jsonResponse = request.asJson();
                    LOG.info("Commerce Admin API Call: " + this.method + " " + this.uri);
                    if (jsonResponse.getStatus() == 200) {
                        return new Response(jsonResponse.getBody().getObject().toString());
                    }
                }
            }
            catch (UnirestException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        else {
            LOG.error("uri is missing");
        }
        return new Response(StringUtils.EMPTY);
    }

    private void defaultHeaders() {
        this.setHeader(Constants.HEADER_PARAM_ACCEPT, Constants.HEADER_VALUE_JSON);
        this.setHeader(Constants.HEADER_PARAM_USER_AGENT, Constants.HEADER_VALUE_USER_AGENT);
        this.setHeader(Constants.HEADER_PARAM_USER_NAME, this.appConfig.getUserName());
        if (this.requiredAccessToken) {
            this.setHeader(Constants.HEADER_PARAM_ACCESS_TOKEN, Jwt.clientRequestToken(
                appConfig.getEnv(), appConfig.getAccessToken(), appConfig.getAppName()
            ));
        }
    }
}