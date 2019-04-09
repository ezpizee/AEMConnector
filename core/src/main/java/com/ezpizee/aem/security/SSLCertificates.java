package com.ezpizee.aem.security;

import com.ezpizee.aem.utils.ConfigUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SSLCertificates {

    private static Map<String, String> certs = new HashMap<>();

    private SSLCertificates() {}

    public static String certificate(String env) {
        if (certs.containsKey(env)) {
            return certs.get(env);
        }
        String cert = ConfigUtil.getResource("ssl-cert/"+env+"-cacert.pem");
        if (StringUtils.isNotEmpty(cert)) {
            certs.put(env, cert);
        }
        return cert;
    }
}
