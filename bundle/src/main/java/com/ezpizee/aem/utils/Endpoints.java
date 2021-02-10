package com.ezpizee.aem.utils;

public class Endpoints {

    private Endpoints() {}

    public static String install() {return "/api/v1/install";}
    public static String token() {return "/api/v1/user/token";}
    public static String refreshToken(String uuid, String userId) {return "/api/v1/user/token/refresh/"+uuid+"/"+userId;}
    public static String login() {return "/api/v1/user/login";}
    public static String logout() {return "/api/v1/user/logout";}
    public static String signup() {return "/api/v1/user/add";}
    public static String activateUserWithID(String userId) {return "/api/v1/user/activate/"+userId;}
    public static String activateUserWithIDAndCode(String userId, String verifyToken) {return "/api/v1/user/activate/"+userId+"/"+verifyToken;}
    public static String appExists(String clientId, String md5AppName) {return "/api/v1/install/"+clientId+"/"+md5AppName;}
    public static String appExistsWithStoreID(String clientId, String md5AppName, String storeId) {return "/api/v1/install/"+clientId+"/"+md5AppName+"/"+storeId;}
}
