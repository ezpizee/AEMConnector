package com.ezpizee.aem.utils;

public class Endpoints {

    private Endpoints() {}

    public static String install() {return "/api/install";}
    public static String token() {return "/api/user/token";}
    public static String refreshToken() {return "/api/user/token/refresh/{token_uuid}/{user_id}";}
    public static String login() {return "/api/user/login";}
    public static String logout() {return "/api/user/logout";}
    public static String signup() {return "/api/user/add";}
    public static String activateUserWithID() {return "/api/user/activate/{id}";}
    public static String activateUserWithIDAndCode() {return "/api/user/activate/{id}/{verification_pin}";}
}
