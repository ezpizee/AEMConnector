package com.ezpizee.aem.http;

import com.ezpizee.aem.utils.DataUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;

public class Response
{
    private static final String KEY_STATUS = "status";
    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private JsonObject jsonObjectData;
    private JsonArray jsonArrayData;
    private String htmlData, status = "OK", message = "SUCCESS";
    private int code = 200;
    private boolean noData = true, dataIsJsonObject = false, dataIsJsonArray = false, dataAsString = false;

    public Response(String content) {
        if (DataUtil.isJsonObjectString(content)) {
            jsonObjectData = new JsonObject();
            jsonArrayData = new JsonArray();
            //LOG.info("Commerce Admin API Call Response "+content);
            final JsonObject jsonData = DataUtil.toJsonObject(content);
            if (jsonData.has(KEY_STATUS) && jsonData.has(KEY_CODE) && jsonData.has(KEY_MESSAGE) && jsonData.has(KEY_DATA)) {
                this.status = jsonData.has(KEY_STATUS) ? jsonData.get(KEY_STATUS).getAsString() : "ERROR";
                this.code = jsonData.has(KEY_CODE) ? jsonData.get(KEY_CODE).getAsInt() : 500;
                this.message = jsonData.has(KEY_MESSAGE) ? jsonData.get(KEY_MESSAGE).getAsString() : "INTERNAL_SERVER_ERROR";
                if (jsonData.has(KEY_DATA)) {
                    if (jsonData.get(KEY_DATA) instanceof JsonObject) {
                        this.setData(jsonData.get(KEY_DATA).getAsJsonObject());
                        this.noData = this.getDataAsJsonObject().size() == 0;
                    }
                    else if (jsonData.get(KEY_DATA) instanceof JsonArray) {
                        this.setData((JsonArray) jsonData.get(KEY_DATA));
                        this.noData = this.getDataAsJsonArray().size() == 0;
                    }
                }
            }
            else {
                this.jsonObjectData = jsonData;
            }
        }
        else if (StringUtils.isNotEmpty(content)) {
            this.noData = false;
            this.htmlData = content;
        }
        else {
            this.resetAsOnError("INVALID_JSON_STRING");
        }
    }

    public Response() {}

    public void setStatus(String a) {this.status =a;}
    public void setCode(int a) {this.code =a;}
    public void setMessage(String a) {this.message=a;}
    public void setData(JsonObject a) {
        this.dataIsJsonObject = true;
        this.jsonObjectData = a;
    }
    public void setData(JsonArray a) {
        this.dataIsJsonArray = true;
        this.jsonArrayData = a;
    }
    public void setData(String content) {
        dataAsString = true;
        htmlData = content;
    }

    public boolean isError() {return this.code != 200;}
    public boolean isNotError() {return this.code == 200;}
    public String getStatus() {return this.status;}
    public String getMessage() {return this.message;}
    public int getCode() {return this.code;}
    public boolean messageIs(String msg) {return this.message.equals(msg);}
    public boolean hasData() {return !this.noData;}
    public boolean hasData(String key) { return this.getDataAsJsonObject().has(key); }
    public boolean hasData(int key) { return this.getDataAsJsonArray().get(key) != null; }
    public boolean isDataIsJsonObject() {return this.dataIsJsonObject;}
    public boolean isDataIsJsonArray() {return this.dataIsJsonArray;}
    public boolean isDataAsString() {return this.dataAsString;}
    public Object getData(String key) {return this.getDataAsJsonObject().get(key);}

    public JsonObject getDataAsJsonObject() { return this.jsonObjectData; }
    public JsonArray getDataAsJsonArray() { return this.jsonArrayData; }
    public String getDataAsString() {return this.htmlData;}

    public String toString() {
        final JsonObject object = new JsonObject();
        object.add(KEY_STATUS, new JsonPrimitive(this.status));
        object.add(KEY_CODE, new JsonPrimitive(this.code));
        object.add(KEY_MESSAGE, new JsonPrimitive(this.message));
        if (this.dataAsString) {object.add(KEY_DATA, new JsonPrimitive(htmlData));}
        else if (this.dataIsJsonObject) {object.add(KEY_DATA, this.jsonObjectData);}
        else if (this.dataIsJsonArray) {object.add(KEY_DATA, this.jsonArrayData);}
        else {object.add(KEY_DATA, null);}
        return object.toString();
    }

    private void resetAsOnError(String msg) {
        this.noData = true;
        this.status = StringUtils.EMPTY;
        this.code = 500;
        this.message = msg;
        this.jsonObjectData = null;
        this.jsonArrayData = null;
        this.htmlData = null;
    }
}