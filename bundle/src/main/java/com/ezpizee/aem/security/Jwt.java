package com.ezpizee.aem.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ezpizee.aem.utils.HashUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Jwt {

    private static final Logger LOG = LoggerFactory.getLogger(Jwt.class);
    private static String issSfx = "api.ezpizee.com:client";
    private static String audSfx = "EzpizeeServer";

    private Jwt() {}

    public static String clientRequestToken(String env, String accessToken, String appName) {
        if (StringUtils.isNotEmpty(accessToken)) {
            return JWT.create()
                .withIssuer(env+":"+issSfx)
                .withAudience(HashUtil.md5(env+":"+audSfx))
                .withHeader(headers())
                .withJWTId(env+":"+appName)
                .withClaim("access_token", accessToken)
                .sign(algorithm());
        }
        else {
            LOG.error("Required data for token generation missing (Jwt.clientRequestToken).");
        }
        return "";
    }

    public static String clientTokenForAccessTokenRequest(String env, String publicKey, String clientId, String phrase, String appName) {
        if (StringUtils.isNotEmpty(env) && StringUtils.isNotEmpty(publicKey) && StringUtils.isNotEmpty(clientId) &&
            StringUtils.isNotEmpty(phrase) && StringUtils.isNotEmpty(appName)
        ) {
            return JWT.create()
                .withIssuer(env+":"+issSfx)
                .withAudience(HashUtil.md5(env+":"+audSfx))
                .withHeader(headers())
                .withJWTId(env+":"+appName)
                .withClaim("ssh", publicKey)
                .withClaim("client", HashUtil.base64Encode(clientId+":"+phrase))
                .sign(algorithm());
        }
        else {
            LOG.error("Required data for token generation missing (Jwt.clientTokenForAccessTokenRequest).");
        }
        return "";
    }

    public static String sshGenFormDataToken(String env, SSHFormData formData) {
        if (StringUtils.isNotEmpty(formData.client_id) && StringUtils.isNotEmpty(formData.phrase) && StringUtils.isNotEmpty(formData.app_name)) {
            return JWT.create()
                .withIssuer(env+":"+issSfx)
                .withAudience(HashUtil.md5(env+":"+audSfx))
                .withHeader(headers())
                .withJWTId(env+":EzpizeeApp")
                .withClaim("phrase", formData.phrase)
                .withClaim("client_id", formData.client_id)
                .withClaim("app_name", formData.app_name)
                .sign(algorithm());
        }
        return "";
    }

    public static Token decryptToken(String tokenData) {

        Token decryptedToken = new Token();
        try {
            JWTVerifier verifier = JWT.require(algorithm()).build();
            DecodedJWT jwt = verifier.verify(tokenData);
            decryptedToken.token = tokenData;
            decryptedToken.jti = jwt.getId();
            decryptedToken.issuer = jwt.getIssuer();
            decryptedToken.audience = jwt.getAudience().get(0);
            String[] jti = decryptedToken.jti.split(":");
            if (jti.length == 2) {
                decryptedToken.env = jti[0];
                decryptedToken.appName = jti[1];
            }
            Map<String, Claim> claims = jwt.getClaims();
            decryptedToken.ssh = claims.containsKey("ssh") ? claims.get("ssh").toString() : "";
            decryptedToken.access_token = claims.containsKey("access_token") ? claims.get("access_token").toString() : "";
            String client = claims.containsKey("client") ? claims.get("client").toString() : "";
            if (StringUtils.isNotEmpty(client)) {
                client = HashUtil.base64Decode(client);
                String[] parts = client.split(":");
                if (parts.length > 1) {
                    if (parts[0].length() == 32) {
                        decryptedToken.client.client_id = parts[0];
                        if (parts[1].length() == 32) {
                            decryptedToken.client.client_secret = parts[1];
                            if (parts.length > 2) {
                                decryptedToken.client.phrase = parts[2];
                            }
                        }
                        else {
                            decryptedToken.client.phrase = parts[2];
                        }
                    }
                }
            }
        }
        catch (JWTVerificationException e) {
            LOG.error(e.getMessage(), e);
        }
        return decryptedToken;
    }

    private static Map<String, Object> headers() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");
        return headers;
    }

    private static Algorithm algorithm() { return Algorithm.HMAC256(ecdsaKey()); }

    private static String ecdsaKey() {
        return "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBJL2OA0xJsYJz+Va+ayzfBqbMsPRy2wIMDbPHSS0xVoTj6Vl+Mcl5WHAmudwhie5k8DnWKssCPJEhUkVY7a7I18= info@webconsol.com";
    }
}
