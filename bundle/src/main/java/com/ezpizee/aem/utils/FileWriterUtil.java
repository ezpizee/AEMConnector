package com.ezpizee.aem.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileWriterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileWriterUtil.class);
    private FileWriter writer;

    public static void writeBuffer(String path, List<String> records) {
        try {
            final FileWriter writer = new FileWriter(FileSystem.getFile(path));
            final BufferedWriter bw = new BufferedWriter(writer, 100*1000*1024);
            for (String record : records) {
                bw.write(record + "\n");
            }
            bw.flush();
            bw.close();
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void writeBuffer(String path, String content) {
        try {
            final FileWriter writer = new FileWriter(FileSystem.getFile(path));
            final BufferedWriter bw = new BufferedWriter(writer, content.getBytes().length);
            bw.write(content);
            bw.flush();
            bw.close();
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public FileWriterUtil(String path, int bfz) {
        try {
            writer = new FileWriter(FileSystem.getFile(path));
            final BufferedWriter bw;
            if (bfz > 0) {
                bw = new BufferedWriter(writer, bfz);
            }
            else {
                bw = new BufferedWriter(writer);
            }
            bw.write("");
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void write(String content) {
        try {
            if (writer != null) {
                writer.write(content);
            }
            else {
                LOG.error("writer of FileWriterUtil is null");
            }
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
            else {
                LOG.error("writer of FileWriterUtil is null");
            }
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
