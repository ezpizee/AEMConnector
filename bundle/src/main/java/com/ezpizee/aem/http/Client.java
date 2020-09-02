package com.ezpizee.aem.http;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.services.AppConfig;
import com.ezpizee.aem.utils.Endpoints;
import com.ezpizee.aem.utils.HashUtil;
import com.ezpizee.aem.utils.HostName;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.RequestBodyEntity;
import kong.unirest.MultipartBody;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;
import kong.unirest.HttpResponse;
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
    private Map<String, Object> formParams;
    private JSONObject body;
    private Map<String, File> files;
    private List<Map<String, Object>> inputStreamList;
    private String uri, method, bearerToken;
    private boolean isMultipart = false;
    private AppConfig appConfig;

    public Client() {
        this.headers = new HashMap<>();
    }

    public Client(AppConfig config) {
        this.headers = new HashMap<>();
        this.appConfig = config;
    }

    public Client(AppConfig config, String token) {
        this.headers = new HashMap<>();
        this.appConfig = config;
        this.bearerToken = token;
        setBearerToken(token);
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

    public String getContent(String url) {
        try {
            GetRequest request = Unirest.get(url);
            logger.debug("GET request: {}", url);
            return new String(request.asString().getBody().getBytes());
        }
        catch (UnirestException e) {
            logger.error(e.getMessage(), e);
            return StringUtils.EMPTY;
        }
    }

    public Response install(String uri, String jsonStr) {
        this.body = new JSONObject(jsonStr);
        this.uri = uri;
        this.method = HttpConstants.METHOD_POST;
        if (this.formParams != null && !this.formParams.isEmpty()) {this.isMultipart=true;}
        return this.request();
    }

    public Response logout() {
        Response response = new Response();
        if (appConfig != null) {
            this.uri = HostName.getAPIServer(appConfig.getEnv())+ Endpoints.logout();
            this.method = HttpConstants.METHOD_POST;
            response = this.request();
        }
        else {
            response.setStatus("ERROR");
            response.setCode(500);
            response.setMessage("APP_CONFIG_MISSING");
        }
        return response;
    }

    public Response login(String user, String pwd) {
        if (appConfig != null) {
            setBasicAuth(user, pwd);
            this.uri = HostName.getAPIServer(appConfig.getEnv()) + Endpoints.login();
            this.method = HttpConstants.METHOD_POST;
            if (this.formParams != null && !this.formParams.isEmpty()) {this.isMultipart=true;}
            return this.request();
        }
        else {
            Response response = new Response();
            response.setStatus("ERROR");
            response.setCode(500);
            response.setMessage("APP_CONFIG_MISSING");
            return response;
        }
    }

    public Response getAccessToken(String uri) {
        setBasicAuth(appConfig.getClientId(), appConfig.getClientSecret());
        addHeader(Constants.HEADER_PARAM_APP_NAME, appConfig.getAppName());
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
        if (this.formParams != null && !this.formParams.isEmpty()) {this.isMultipart=true;}
        return this.request();
    }

    public Response delete(String uri) {
        this.uri = uri;
        this.method = HttpConstants.METHOD_DELETE;
        if (this.formParams != null && !this.formParams.isEmpty()) {this.isMultipart=true;}
        return this.request();
    }

    public Response put(String uri) {
        this.uri = uri;
        this.method = HttpConstants.METHOD_PUT;
        if (this.formParams != null && !this.formParams.isEmpty()) {this.isMultipart=true;}
        return this.request();
    }

    private Response getRequest() {
        if (StringUtils.isNotEmpty(this.uri) && this.appConfig != null && this.appConfig.isValid()) {
            this.defaultHeaders();
            try {
                GetRequest request = Unirest.get(this.uri);
                if (!this.headers.isEmpty()) { request.headers(this.headers); }
                HttpResponse<JsonNode> jsonResponse = request.asJson();
                logger.info("Commerce Admin API Call: " + this.method + " " + this.uri);
                return new Response(jsonResponse.getBody().getObject().toString());
            }
            catch (UnirestException e) {
                logger.error(e.getMessage(), e);
                Response response = new Response();
                response.setCode(500);
                response.setMessage(e.getMessage());
                return response;
            }
        }
        else {
            Response response = new Response();
            if (StringUtils.isEmpty(this.uri)) {
                logger.error("uri is missing");
                response.setData("uri is missing");
            }
            else if (this.appConfig == null || !this.appConfig.isValid()) {
                logger.error("appConfig is not valid");
                response.setData("appConfig is not valid");
            }
            response.setCode(500);
            return response;
        }
    }

    private Response request() {
        if (StringUtils.isNotEmpty(this.uri) && this.appConfig != null && this.appConfig.isValid()) {
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
                    this.isMultipart = false;
                    this.formParams = null;
                    this.inputStreamList = null;
                    this.files = null;
                    this.headers = null;
                    return new Response(jsonResponse.getBody().getObject().toString());
                }
                else {
                    HttpRequestWithBody request = Unirest.request(this.method, this.uri);
                    this.defaultHeaders();
                    if (this.headers != null && !this.headers.isEmpty()) { request.headers(this.headers); this.headers = null; }
                    HttpResponse<JsonNode> jsonResponse = request.asJson();
                    if (jsonResponse.getStatus() != 200) { logger.debug(jsonResponse.getBody().getObject().toString()); }
                    return new Response(jsonResponse.getBody().getObject().toString());
                }
            }
            catch (UnirestException e) {
                logger.error(e.getMessage(), e);
                Response response = new Response();
                response.setCode(500);
                response.setMessage(e.getMessage());
                return response;
            }
        }
        else {
            Response response = new Response();
            if (StringUtils.isEmpty(this.uri)) {
                logger.error("uri is missing");
                response.setData("uri is missing");
            }
            else if (this.appConfig == null || !this.appConfig.isValid()) {
                logger.error("appConfig is not valid");
                response.setData("appConfig is not valid");
            }
            response.setCode(500);
            return response;
        }
    }

    private void defaultHeaders() {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
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
        if (!this.headers.containsKey(Constants.HEADER_PARAM_APP_NAME) && appConfig != null && appConfig.isValid()) {
            this.addHeader(Constants.HEADER_PARAM_APP_NAME, appConfig.getAppName());
        }
        if (!this.headers.containsKey(Constants.HEADER_PARAM_ACCESS_TOKEN) && StringUtils.isNotEmpty(this.bearerToken)) {
            this.setBearerToken(this.bearerToken);
        }
    }
}