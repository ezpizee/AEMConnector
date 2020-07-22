package com.ezpizee.aem.http;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.Endpoints;
import com.ezpizee.aem.utils.HashUtil;
import com.ezpizee.aem.utils.HostName;
import kong.unirest.*;
import kong.unirest.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.servlets.HttpConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Client
{
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, String> headers;
    private Map<String, Object> formParams, queries;
    private JSONObject body;
    private Map<String, File> files;
    private List<Map<String, Object>> inputStreamList;
    private String uri, method;
    private boolean requiredAccessToken = true, bypassAppConfigValidation = false, htmlResponse = false, isMultipart = false;
    private AppConfig appConfig;

    public Client(AppConfig config, boolean htmlResponse, boolean bypassAppConfigValidation) {
        this.htmlResponse = htmlResponse;
        this.bypassAppConfigValidation = bypassAppConfigValidation;
        if (this.bypassAppConfigValidation) {
            this.requiredAccessToken = false;
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
        method = HttpConstants.METHOD_GET;
    }

    public void setBasicAuth(String username, String password) {
        this.addHeader(Constants.HEADER_PARAM_ACCESS_TOKEN, "Basic "+HashUtil.base64Encode(username+":"+password));
    }
    public void setBearerToken(String token) { this.addHeader(Constants.HEADER_PARAM_ACCESS_TOKEN, "Bearer "+token); }
    public void addHeader(String key, String value) { this.headers.put(key, value); }
    public void setFormParams(Map<String, Object> params) { formParams = params; }
    public void setBody(String jsonStr) { this.body = new JSONObject(jsonStr); }
    public void setIsMultipart(boolean b) {this.isMultipart = b;}
    public void addInputStream(String fieldName, InputStream is, String fileName) {
        if (this.inputStreamList == null) {this.inputStreamList = new ArrayList<>();}
        Map<String, Object> o = new HashMap<>();
        o.put("fieldName", fieldName);
        o.put("is", is);
        o.put("fileName", fileName);
        this.inputStreamList.add(o);
    }
    public void setRequiredAccessToken(boolean b) { requiredAccessToken = b; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public void setFiles(Map<String, File> files) { this.files = files; }
    public void setQueries(Map<String, Object> params) { queries = params; }

    public Response install(String uri, String jsonStr) {
        this.body = new JSONObject(jsonStr);
        this.requiredAccessToken = false;
        this.uri = uri;
        this.method = HttpConstants.METHOD_POST;
        return this.request();
    }

    public Response logout() {
        addHeader(Constants.HEADER_PARAM_APP_NAME, appConfig.getAppName());
        this.uri = HostName.getAPIServer(appConfig.getEnv())+ Endpoints.logout();
        this.method = HttpConstants.METHOD_POST;
        return this.request();
    }

    public Response login(String user, String pwd) {
        setBasicAuth(user, pwd);
        addHeader(Constants.HEADER_PARAM_APP_NAME, appConfig.getAppName());
        this.requiredAccessToken = false;
        this.uri = HostName.getAPIServer(appConfig.getEnv())+ Endpoints.login();
        this.method = HttpConstants.METHOD_POST;
        return this.request();
    }

    public Response getAccessToken(String uri) {
        setBasicAuth(appConfig.getClientId(), appConfig.getClientSecret());
        addHeader(Constants.HEADER_PARAM_APP_NAME, appConfig.getAppName());
        this.requiredAccessToken = false;
        this.uri = uri;
        this.method = HttpConstants.METHOD_POST;
        return this.request();
    }

    public Response get(String uri) {
        this.uri = uri;
        this.method = HttpConstants.METHOD_GET;
        return this.getRequest();
    }

    public Response post(String uri) {
        this.uri = uri;
        this.method = HttpConstants.METHOD_POST;
        return this.request();
    }

    public Response delete(String uri) {
        this.uri = uri;
        this.method = HttpConstants.METHOD_DELETE;
        return this.request();
    }

    public Response put(String uri) {
        this.uri = uri;
        this.method = HttpConstants.METHOD_PUT;
        return this.request();
    }

    private Response getRequest() {
        if (StringUtils.isNotEmpty(this.uri) && (this.appConfig.isValid() || this.bypassAppConfigValidation)) {
            if (!this.htmlResponse) {
                this.defaultHeaders();
            }
            try {
                GetRequest request = Unirest.get(this.uri);
                if (this.queries != null && !this.queries.isEmpty()) {
                    request.queryString(this.queries);
                }
                if (!this.headers.isEmpty()) {
                    request.headers(this.headers);
                }
                if (this.htmlResponse) {
                    logger.info("Request method & url: " + this.method + " " + this.uri);
                    Response resp = new Response();
                    resp.setData(new String(request.asString().getBody().getBytes()));
                    return resp;
                }
                else {
                    HttpResponse<JsonNode> jsonResponse = request.asJson();
                    logger.info("Commerce Admin API Call: " + this.method + " " + this.uri);
                    return new Response(jsonResponse.getBody().getObject().toString());
                }
            }
            catch (UnirestException e) {
                logger.error(e.getMessage(), e);
            }
        }
        else {
            logger.error("uri is missing");
        }
        return new Response();
    }

    private Response request() {
        if (StringUtils.isNotEmpty(this.uri) && (this.appConfig.isValid() || this.bypassAppConfigValidation)) {
            try {
                logger.info("Commerce Admin API Call: " + this.method + " " + this.uri);
                // request with body
                if (this.body != null && !this.body.isEmpty()) {
                    RequestBodyEntity request = Unirest.request(this.method, this.uri).body(this.body);
                    this.defaultHeaders();
                    if (this.headers != null && !this.headers.isEmpty()) { request.headers(this.headers); }

                    final HttpResponse<JsonNode> jsonResponse = request.asJson();
                    if (jsonResponse.getStatus() != 200) { logger.debug(jsonResponse.getBody().getObject().toString()); }
                    this.body = null;
                    this.headers = null;
                    return new Response(jsonResponse.getBody().getObject().toString());
                }
                // multipart request
                else if (this.isMultipart && this.formParams != null && !this.formParams.isEmpty()) {
                    MultipartBody request = Unirest.request(this.method, this.uri).fields(formParams);
                    this.defaultHeaders();
                    if (this.headers != null && !this.headers.isEmpty()) { request.headers(this.headers); }
                    if (this.files != null && !this.files.isEmpty()) {
                        for(String key : this.files.keySet()) {
                            request.field(key, this.files.get(key));
                        }
                    }
                    if (this.inputStreamList != null && !this.inputStreamList.isEmpty()) {
                        for(Map<String, Object> map : this.inputStreamList) {
                            request.field(map.get("fieldName").toString(), (InputStream)map.get("is"), map.get("fileName").toString());
                        }
                    }

                    final HttpResponse<JsonNode> jsonResponse = request.asJson();
                    if (jsonResponse.getStatus() != 200) { logger.debug(jsonResponse.getBody().getObject().toString()); }
                    this.formParams = null;
                    this.inputStreamList = null;
                    this.files = null;
                    this.headers = null;
                    return new Response(jsonResponse.getBody().getObject().toString());
                }
                else {
                    HttpRequestWithBody request = Unirest.request(this.method, this.uri);
                    if (!this.htmlResponse) { this.defaultHeaders(); }
                    if (this.headers != null && !this.headers.isEmpty()) { request.headers(this.headers); this.headers = null; }
                    if (this.htmlResponse) {
                        Response resp = new Response();
                        resp.setData(new String(request.asString().getBody().getBytes()));
                        return resp;
                    }
                    else {
                        HttpResponse<JsonNode> jsonResponse = request.asJson();
                        if (jsonResponse.getStatus() != 200) { logger.debug(jsonResponse.getBody().getObject().toString()); }
                        return new Response(jsonResponse.getBody().getObject().toString());
                    }
                }
            }
            catch (UnirestException e) {
                logger.error(e.getMessage(), e);
            }
        }
        else {
            logger.error("uri is missing");
        }
        return new Response();
    }

    private void defaultHeaders() {
        if (!this.isMultipart && !this.headers.containsKey(Constants.HEADER_PARAM_CTYPE)) {
            this.addHeader(Constants.HEADER_PARAM_CTYPE, Constants.HEADER_VALUE_JSON);
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_ACCEPT)) {
            this.addHeader(Constants.HEADER_PARAM_ACCEPT, Constants.HEADER_VALUE_JSON);
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_USER_AGENT)) {
            this.addHeader(Constants.HEADER_PARAM_USER_AGENT, Constants.HEADER_VALUE_USER_AGENT);
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_APP_VERSION)) {
            this.addHeader(Constants.HEADER_PARAM_APP_VERSION, Constants.HEADER_VALUE_APP_VERSION);
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_APP_PLATFORM)) {
            this.addHeader(Constants.HEADER_PARAM_APP_PLATFORM, Constants.HEADER_VALUE_APP_PLATFORM);
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_OS_PLATFORM_VERSION)) {
            this.addHeader(Constants.HEADER_PARAM_OS_PLATFORM_VERSION, Constants.HEADER_VALUE_OS_PLATFORM_VERSION);
        }
        if (this.requiredAccessToken) {
            if (!this.headers.containsKey(Constants.HEADER_PARAM_ACCESS_TOKEN)) {
                logger.debug("BearerToken: {}", appConfig.getBearerToken());
                this.setBearerToken(appConfig.getBearerToken());
            }
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_APP_NAME) &&
            appConfig != null &&
            StringUtils.isNotEmpty(appConfig.getAppName())) {
            this.addHeader(Constants.HEADER_PARAM_APP_NAME, appConfig.getAppName());
        }
    }
}