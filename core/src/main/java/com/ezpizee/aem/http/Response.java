package com.ezpizee.aem.http;

import com.ezpizee.aem.utils.DataUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response
{
    private static final Logger LOG = LoggerFactory.getLogger(Response.class);
    private static final String KEY_SUCESS = "success";
    private static final String KEY_STATUS_CODE = "statusCode";
    private static final String KEY_DATA = "data";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DEBUG = "debug";
    private JSONObject jsonObjectData = new JSONObject();
    private JSONArray jsonArrayData = new JSONArray();
    private boolean noData = false;
    private boolean success = true;
    private String message = "Successfully processed";
    private long statusCode = 200;
    private boolean dataIsJSONObject = false;
    private boolean dataIsJSONArray = false;
    private boolean isDebug = false;

    public Response(String content) {
        if (DataUtil.isJSONObjectString(content)) {
            //LOG.info("Commerce Admin API Call Response "+content);
            final JSONObject jsonData = DataUtil.toJSONObject(content);
            if (jsonData.containsKey(KEY_SUCESS) && jsonData.containsKey(KEY_STATUS_CODE) && jsonData.containsKey(KEY_MESSAGE) && jsonData.containsKey(KEY_DATA)) {
                this.success = jsonData.containsKey(KEY_SUCESS) && (boolean) jsonData.get(KEY_SUCESS);
                this.statusCode = jsonData.containsKey(KEY_STATUS_CODE) ? (long) jsonData.get(KEY_STATUS_CODE) : 500;
                this.message = jsonData.containsKey(KEY_MESSAGE) ? (String) jsonData.get(KEY_MESSAGE) : "Failed to process";
                this.isDebug = jsonData.containsKey(KEY_DEBUG) && (boolean) jsonData.get(KEY_DEBUG);
                if (jsonData.containsKey(KEY_DATA)) {
                    if (jsonData.get(KEY_DATA) instanceof JSONObject) {
                        this.setData((JSONObject) jsonData.get(KEY_DATA));
                        this.noData = this.getDataAsJSONObject().isEmpty();
                    }
                    else if (jsonData.get(KEY_DATA) instanceof JSONArray) {
                        this.setData((JSONArray) jsonData.get(KEY_DATA));
                        this.noData = this.getDataAsJSONArray().isEmpty();
                    }
                }
            }
            else {
                this.jsonObjectData = jsonData;
            }
        }
        else {
            this.resetAsOnError("Failed to process");
        }
    }

    public Response() {}

    public void setMessage(String a) {this.message=a;}
    public void setSuccess(boolean a) {this.success=a;}
    public void setStatusCode(int a) {this.statusCode=a;}
    public void setData(JSONObject a) {
        this.dataIsJSONObject = true;
        this.jsonObjectData = a;
    }
    public void setData(JSONArray a) {
        this.dataIsJSONArray = true;
        this.jsonArrayData = a;
    }

    public boolean isSuccess() {return this.success;}
    public String getMessage() {return this.message;}
    public long getStatusCode() {return this.statusCode;}
    public boolean messageIs(String msg) {return this.message.equals(msg);}
    public boolean hasData() {return !this.noData;}
    public boolean hasData(String key) { return this.getDataAsJSONObject().containsKey(key); }
    public boolean hasData(int key) { return this.getDataAsJSONArray().indexOf(key) != -1; }
    public boolean isDebug() { return this.isDebug; }
    public boolean isDataJSONObject() {return this.dataIsJSONObject;}
    public boolean isDataJSONArray() {return this.dataIsJSONArray;}

    public JSONObject getDataAsJSONObject() { return this.jsonObjectData; }
    public JSONArray getDataAsJSONArray() { return this.jsonArrayData; }

    public String toString() {
        final JSONObject object = new JSONObject();
        object.put(KEY_SUCESS, this.success);
        object.put(KEY_STATUS_CODE, this.statusCode);
        object.put(KEY_MESSAGE, this.message);
        if (this.dataIsJSONObject) {object.put(KEY_DATA, this.jsonObjectData);}
        else if (this.dataIsJSONArray) {object.put(KEY_DATA, this.jsonArrayData);}
        else {object.put(KEY_DATA, null);}
        return object.toJSONString();
    }

    private void resetAsOnError(String msg) {
        this.noData = true;
        this.success = false;
        this.statusCode = 500;
        this.message = msg;
    }
}