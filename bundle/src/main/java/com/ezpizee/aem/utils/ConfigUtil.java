package com.ezpizee.aem.utils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConfigUtil
{
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

    public static void loadResourceByDir(String dir, StringBuilder sb) {
        URL url = ConfigUtil.class.getClassLoader().getResource(dir);
        File file = new File(url.getFile());
        if (file.exists()) {
            if (file.isDirectory()) {
                loadResourceByDir(file.getAbsolutePath(), sb);
            }
            else if (file.isFile()) {
                sb.append(FileSystem.getFileContentAsString(file.getAbsolutePath()));
            }
        }
    }
}
