package com.ezpizee.aem.utils;

public class Endpoints {

    private Endpoints() {}

    public static String install() {return "/api/install";}
    public static String token() {return "/api/user/token";}
    public static String refreshToken(String uuid, String userId) {return "/api/user/token/refresh/"+uuid+"/"+userId;}
    public static String login() {return "/api/user/login";}
    public static String logout() {return "/api/user/logout";}
    public static String signup() {return "/api/user/add";}
    public static String activateUserWithID(String userId) {return "/api/user/activate/"+userId;}
    public static String activateUserWithIDAndCode(String userId, String verifyToken) {return "/api/user/activate/"+userId+"/"+verifyToken;}
    public static String appExists(String clientId, String md5AppName) {return "/api/install/"+clientId+"/"+md5AppName;}
    public static String appExistsWithStoreID(String clientId, String md5AppName, String storeId) {return "/api/install/"+clientId+"/"+md5AppName+"/"+storeId;}
}
