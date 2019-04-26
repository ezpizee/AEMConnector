package com.ezpizee.aem.security;

import com.ezpizee.aem.Constants;
import com.ezpizee.aem.http.Client;
import com.ezpizee.aem.http.Response;
import com.ezpizee.aem.models.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SSHAgent {

    private static final Logger LOG = LoggerFactory.getLogger(SSHAgent.class);

    public static String generatePublicKey(AppConfig appConfig, SSHFormData sshFormData) {
        String data = Jwt.sshGenFormDataToken(appConfig.getEnv(), sshFormData);
        Map<String, Object> formData = new HashMap<>();
        formData.put("data", data);
        Client client = new Client(appConfig);
        client.setRequiredAccessToken(false);
        client.setByPassAppConfigValidation(true);
        client.setFormParams(formData);
        Response response = client.post(Constants.ENDPOINT_GEN_SSH_KEYS);
        if (response.isSuccess() && response.hasData("public_key")) {
            return (String)response.getDataAsJSONObject().get("public_key");
        }
        return "";
    }
}
