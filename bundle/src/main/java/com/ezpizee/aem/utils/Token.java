package com.ezpizee.aem.utils;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

public class Token {

    private static final String KEY_SESSION_ID = "Session-Id";
    private static final String KEY_TOKEN_ID = "token_uuid";
    private static final String KEY_TOKEN_PARAM_NAME = "token_param_name";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EXPIRE_IN = "expire_in";

    private JsonObject jsonObject;
    private JsonObject user;
    private String sessionId;
    private String tokenId;
    private String bearerToken;
    private String userId;
    private long expireIn;

    public Token() { reset(); }

    public Token(String token) { loadData(DataUtil.toJsonObject(token)); }

    public Token (JsonObject token) { loadData(token); }

    public JsonObject getUser() {return user;}
    public String getTokenId() {return tokenId;}
    public String getBearerToken() {return bearerToken;}
    public String getUserId() {return userId;}
    public long getExpireIn() {return expireIn;}
    public String getSessionId() {return sessionId;}

    public boolean isExpired() {
        long now = DateFormatUtil.now();
        return now > getExpireIn();
    }

    public boolean timeToRefresh() { return (DateFormatUtil.now() + (8*60*100)) > getExpireIn(); }

    public void destroy() { reset(); }

    public String toString() { return jsonObject != null ? jsonObject.toString() : ""; }

    private void reset() {
        jsonObject = new JsonObject();
        user = new JsonObject();
        sessionId = StringUtils.EMPTY;
        tokenId = StringUtils.EMPTY;
        bearerToken = StringUtils.EMPTY;
        userId = StringUtils.EMPTY;
        expireIn = 0;
    }

    private void loadData(JsonObject token) {
        if (token != null) {
            String tokenParamName = token.has(KEY_TOKEN_PARAM_NAME) ? token.get(KEY_TOKEN_PARAM_NAME).getAsString() : StringUtils.EMPTY;
            jsonObject = token;
            sessionId = token.has(KEY_SESSION_ID) ? token.get(KEY_SESSION_ID).getAsString() : StringUtils.EMPTY;
            tokenId = token.has(KEY_TOKEN_ID) ? token.get(KEY_TOKEN_ID).getAsString() : StringUtils.EMPTY;
            userId = token.has(KEY_USER_ID) ? token.get(KEY_USER_ID).getAsString() : StringUtils.EMPTY;
            expireIn = DateFormatUtil.now() + (token.has(KEY_EXPIRE_IN) ? token.get(KEY_EXPIRE_IN).getAsInt() : 0);
            bearerToken = token.has(tokenParamName) ? token.get(tokenParamName).getAsString() : StringUtils.EMPTY;
            if(token.has("user")) {
                user = token.getAsJsonObject("user");
                if (StringUtils.isEmpty(userId)) {
                    userId = user.has("id") ? user.get("id").getAsString() : StringUtils.EMPTY;
                }
            }
        }
        else {
            reset();
        }
    }
}
