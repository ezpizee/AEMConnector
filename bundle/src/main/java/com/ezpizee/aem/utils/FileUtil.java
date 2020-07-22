package com.ezpizee.aem.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(com.ezpizee.aem.utils.FileUtil.class);

    private FileUtil() {}

    public static String bufferedReaderToString(BufferedReader bufferedReader) {
        StringBuilder sb = new StringBuilder();
        try {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                sb.append(currentLine);
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return sb.toString();
    }

    public static String getContentAsStringFromFileOnCRX(ResourceResolver resolver, String path) {
        String str = "";
        byte[] bytes = getContentAsByteArrayFromFileOnCRX(resolver, path);
        if (bytes != null) {
            str = new String(bytes);
        }
        return str;
    }

    public static byte[] getContentAsByteArrayFromFileOnCRX(ResourceResolver resolver, String path) {
        byte[] result = null;
        try {
            Session session = resolver.adaptTo(Session.class);
            if (session != null && resolver.getResource(path) != null) {
                Node fileNode = session.getNode(path);
                if (fileNode != null && fileNode.hasNode(JcrConstants.JCR_CONTENT)) {
                    Node jcrNode = fileNode.getNode(JcrConstants.JCR_CONTENT);
                    if (jcrNode != null && jcrNode.hasProperty(JcrConstants.JCR_DATA)) {
                        InputStream is = jcrNode.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                        BufferedInputStream bin = new BufferedInputStream(is);
                        result = IOUtils.toByteArray(bin);
                        bin.close();
                        is.close();
                    }
                }
            }
        }
        catch (RepositoryException | IOException e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    public static String getExtension(String filename) { return FilenameUtils.getExtension(filename); }

    public static String getBasename(String filename) { return FilenameUtils.getBaseName(filename); }

    public static String getFilename(String filename) { return FilenameUtils.getName(filename); }
}
