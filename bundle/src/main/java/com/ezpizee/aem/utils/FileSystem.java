package com.ezpizee.aem.utils;

import net.minidev.json.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileSystem {

    private static final Logger LOG = LoggerFactory.getLogger(FileSystem.class);
    private static final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
    private static List<InputStream> isList = new ArrayList<>();

    private FileSystem() {}

    public static void fetchFilesAsJsonArray(String folderPath, JSONArray files) {

        File dir = new File(folderPath);

        if (dir.isDirectory()) {
            File[] filesList = dir.listFiles();
            if (filesList != null && filesList.length > 0) {
                for (File file : filesList) {
                    if (file.isFile()) {
                        files.add(file.getPath());
                    } else if (file.isDirectory()) {
                        fetchFilesAsJsonArray(file.getAbsolutePath(), files);
                    }
                }
            }
        }
    }

    public static void fetchFiles(String folderPath, List<File> files) {

        File dir = new File(folderPath);

        if (dir.isDirectory()) {
            File[] filesList = dir.listFiles();
            if (filesList != null && filesList.length > 0) {
                for (File file : filesList) {
                    if (file.isFile()) {
                        files.add(file);
                    } else if (file.isDirectory()) {
                        fetchFiles(file.getAbsolutePath(), files);
                    }
                }
            }
        }
    }

    public static InputStream getFileContentAsInputStream(String path) { return getFileContentAsInputStream(getFile(path)); }

    public static InputStream getFileContentAsInputStream(File file) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            isList.add(is);
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return is;
    }

    /**
     * SHOULD BE INVOKED WHEN INVOKED getInputStream
     */
    public static void closeInputStream() {
        if (!isList.isEmpty()) {
            for (InputStream is : isList) {
                closeInputStream(is);
            }
            isList.clear();
        }
    }

    public static void closeInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static String getMimeType(File file) {
        if (file != null) {
            return mimetypesFileTypeMap.getContentType(file);
        }
        return null;
    }

    public static File getFile(String path) {
        return new File(path);
    }

    public static String getFileContentAsString(String path) {
        String content = StringUtils.EMPTY;
        try {
            File file = getFile(path);
            if (file.exists()) {
                byte[] bytes = IOUtils.toByteArray(getFileContentAsInputStream(file));
                content = new String(bytes);
            }
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return content;
    }

    public static boolean hasFile(String path) { return getFile(path).exists(); }

    public static BufferedReader getBufferedReader(String path) {
        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(getFile(path)), "UTF8"));
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}