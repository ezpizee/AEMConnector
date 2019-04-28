package com.ezpizee.aem.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class ConfigUtil
{
    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    public static String getResource(String filename) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = ConfigUtil.class.getClassLoader().getResourceAsStream(filename);
            InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(streamReader);
            for (String line; (line = reader.readLine()) != null; ) {
                sb.append(line);
            }
        }
        catch (IOException e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
}
