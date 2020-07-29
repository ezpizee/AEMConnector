package com.ezpizee.aem.services;

import aQute.bnd.annotation.ProviderType;
import com.ezpizee.aem.http.Response;

import javax.servlet.http.HttpSession;

@ProviderType
public interface AccessToken {

    String get();
    void load(String key, HttpSession httpSession);
    void destroy();
    void refresh(String key, HttpSession httpSession);
    void keepToken(Response response, String key, HttpSession httpSession);
}
