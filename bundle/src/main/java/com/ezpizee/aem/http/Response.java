package com.ezpizee.aem.http;

import com.ezpizee.aem.utils.DataUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response
{
    private static final Logger LOG = LoggerFactory.getLogger(Response.class);
    private static final String KEY_STATUS = "status";
    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DATA = "data";
    private JsonObject jsonObjectData;
    private JsonArray jsonArrayData;
    private boolean noData = false;
    private boolean success = true;
    private String message = "Successfully processed";
    private long code = 200;
    private boolean dataIsJsonObject = false;
    private boolean dataIsJsonArray = false;

    public Response(String content) {
        if (DataUtil.isJsonObjectString(content)) {
            jsonObjectData = new JsonObject();
            jsonArrayData = new JsonArray();
            //LOG.info("Commerce Admin API Call Response "+content);
            final JsonObject jsonData = DataUtil.toJsonObject(content);
            if (jsonData.has(KEY_STATUS) && jsonData.has(KEY_CODE) && jsonData.has(KEY_MESSAGE) && jsonData.has(KEY_DATA)) {
                this.success = jsonData.has(KEY_STATUS) && !jsonData.get(KEY_STATUS).getAsString().equals("ERROR");
                this.code = jsonData.has(KEY_CODE) ? jsonData.get(KEY_CODE).getAsLong() : 500;
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
        else {
            this.resetAsOnError("Failed to process");
        }
    }

    public Response() {}

    public void setMessage(String a) {this.message=a;}
    public void setSuccess(boolean a) {this.success=a;}
    public void setCode(int a) {this.code =a;}
    public void setData(JsonObject a) {
        this.dataIsJsonObject = true;
        this.jsonObjectData = a;
    }
    public void setData(JsonArray a) {
        this.dataIsJsonArray = true;
        this.jsonArrayData = a;
    }

    public boolean isSuccess() {return this.success;}
    public String getMessage() {return this.message;}
    public long getCode() {return this.code;}
    public boolean messageIs(String msg) {return this.message.equals(msg);}
    public boolean hasData() {return !this.noData;}
    public boolean hasData(String key) { return this.getDataAsJsonObject().has(key); }
    public boolean hasData(int key) { return this.getDataAsJsonArray().get(key) != null; }
    public boolean isDataJsonObject() {return this.dataIsJsonObject;}
    public boolean isDataJsonArray() {return this.dataIsJsonArray;}
    public Object getData(String key) {return this.getDataAsJsonObject().get(key);}

    public JsonObject getDataAsJsonObject() { return this.jsonObjectData; }
    public JsonArray getDataAsJsonArray() { return this.jsonArrayData; }

    public String toString() {
        final JsonObject object = new JsonObject();
        object.add(KEY_STATUS, new JsonPrimitive(this.success));
        object.add(KEY_CODE, new JsonPrimitive(this.code));
        object.add(KEY_MESSAGE, new JsonPrimitive(this.message));
        if (this.dataIsJsonObject) {object.add(KEY_DATA, this.jsonObjectData);}
        else if (this.dataIsJsonArray) {object.add(KEY_DATA, this.jsonArrayData);}
        else {object.add(KEY_DATA, null);}
        return object.getAsString();
    }

    private void resetAsOnError(String msg) {
        this.noData = true;
        this.success = false;
        this.code = 500;
        this.message = msg;
        this.jsonObjectData = null;
        this.jsonArrayData = null;
    }
}