package com.ezpizee.aem.http;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.models.AppConfig;
import com.ezpizee.aem.security.Jwt;
import com.ezpizee.aem.utils.EndpointUtil;
import com.ezpizee.aem.utils.HashUtil;
import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;


public class Client
{
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private static final String KEY_AUTH = "Authorization";
    private Map<String, String> headers;
    private Map<String, Object> formParams;
    private Map<String, String> queries;
    private String body;
    private Map<String, File> files;
    private String uri;
    private MethodEnum method;
    private boolean requiredAccessToken = true, bypassAppConfigValidation = false, htmlResponse = false;
    private AppConfig appConfig;

    public Client(AppConfig config, boolean htmlResponse, boolean bypassAppConfigValidation) {
        this.htmlResponse = htmlResponse;
        this.bypassAppConfigValidation = bypassAppConfigValidation;
        if (this.bypassAppConfigValidation) {
            setRequiredAccessToken(false);
        }
        setConfig(config);
    }

    public Client(AppConfig config, boolean htmlResponse) {
        this.htmlResponse = htmlResponse;
        setConfig(config);
    }

    public Client(AppConfig config) { setConfig(config); }

    private void setConfig(AppConfig config) {
        headers = new HashMap<>();
        appConfig = config;
        method = MethodEnum.GET;
    }

    public void setRequiredAccessToken(boolean b) { requiredAccessToken = b; }
    public void setAuth(String username, String password) { this.addHeader(KEY_AUTH, "Basic "+HashUtil.base64Encode(username+":"+password)); }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public void addHeader(String key, String value) { this.headers.put(key, value); }
    public void setFormParams(Map<String, Object> params) { formParams = params; }
    public void setQueries(Map<String, String> params) { queries = params; }
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

    private Response getRequest() {
        if (StringUtils.isNotEmpty(this.uri) && (this.appConfig.isValid() || this.bypassAppConfigValidation)) {
            if (!htmlResponse) {
                this.defaultHeaders();
            }
            try {
                if (queries != null && !queries.isEmpty()) {
                    StringJoiner joiner = new StringJoiner("&");
                    for (String key : queries.keySet()) {
                        joiner.add(key+"="+ queries.get(key));
                    }
                    this.uri = this.uri + (this.uri.contains("?")?"&":"?") + joiner.toString();
                }
                GetRequest request = Unirest.get(this.uri);
                if (!this.headers.isEmpty()) { request.headers(this.headers); }
                if (htmlResponse) {
                    LOG.info("Request method & url: " + this.method + " " + this.uri);
                    Response resp = new Response();
                    resp.setData(new String(request.asString().getBody().getBytes()));
                    return resp;
                }
                else {
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
        return new Response();
    }

    private Response request() {
        if (StringUtils.isNotEmpty(this.uri) && (this.appConfig.isValid() || this.bypassAppConfigValidation)) {
            if (!this.htmlResponse) {
                this.defaultHeaders();
            }
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
                    if (this.htmlResponse) {
                        Response resp = new Response();
                        resp.setData(new String(request.asString().getBody().getBytes()));
                        return resp;
                    }
                    else {
                        HttpResponse<JsonNode> jsonResponse = request.asJson();
                        LOG.info("Commerce Admin API Call: " + this.method + " " + this.uri);
                        if (jsonResponse.getStatus() == 200) {
                            return new Response(jsonResponse.getBody().getObject().toString());
                        }
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
        return new Response();
    }

    private void defaultHeaders() {
        this.addHeader(Constants.HEADER_PARAM_ACCEPT, Constants.HEADER_VALUE_JSON);
        this.addHeader(Constants.HEADER_PARAM_USER_AGENT, Constants.HEADER_VALUE_USER_AGENT);
        if (this.requiredAccessToken) {
            this.addHeader(Constants.HEADER_PARAM_ACCESS_TOKEN, Jwt.clientRequestToken(
                appConfig.getEnv(), "TODO", appConfig.getAppName()
            ));
        }
    }
}