package com.ezpizee.aem.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVContentParser {

    private static final Logger LOG = LoggerFactory.getLogger(CSVContentParser.class);
    private static final String DELIMITER = ",";
    private Map<String, Map<String, Map<String, String>>> idx;

    public CSVContentParser(final String path, final List<String> fields, final List<String> indexFields)
    {
        idx = new HashMap<>();
        if (FileSystem.hasFile(path) && !fields.isEmpty() && !indexFields.isEmpty()) {

            BufferedReader buf = FileSystem.getBufferedReader(path);

            if (buf != null) {
                Map<String, String> headings = new HashMap<>();
                String[] wordsArray;
                String line, skuKeyName = "", fileNameKeyName = "", styleKeyName = "";
                int numRecords = 0;
                try {
                    while (((line = buf.readLine()) != null)) {
                        wordsArray = line.split(DELIMITER);
                        if (numRecords == 0) {
                            for (int j = 0; j < wordsArray.length; j++) {
                                headings.put(Integer.toString(j), fields.get(j));
                            }
                        } else if (StringUtils.isNotEmpty(skuKeyName) || StringUtils.isNotEmpty(fileNameKeyName) || StringUtils.isNotEmpty(styleKeyName)) {
                            final Map<String, String> data = new HashMap<>();
                            for (int j = 0; j < wordsArray.length; j++) {
                                data.put(headings.get(Integer.toString(j)), wordsArray[j].trim());
                            }
                            for (String indexField : indexFields) {
                                if (data.containsKey(indexField)) {
                                    if (!idx.containsKey(indexField)) {
                                        final Map<String, Map<String, String>> tmpMap = new HashMap<>();
                                        idx.put(indexField, tmpMap);
                                    }
                                    idx.get(indexField).put(data.get(indexField).trim(), data);
                                }
                            }
                        }
                        numRecords++;
                    }
                }
                catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        else
        {
            LOG.error("File {} doesn't exist", path);
        }
    }

    public boolean hasData() { return idx.size() > 0; }

    public Map<String, Map<String, Map<String, String>>> getData() { return idx; }

    public Map<String, Map<String, String>> getDataByIndexField(String indexField) {
        return hasData() ? idx.getOrDefault(indexField, null) : null;
    }

    public Map<String, String> getDataByIndexFieldValue(String indexField, String key) {
        if (hasData() && idx.containsKey(indexField)) {
            if (idx.get(indexField).containsKey(key)) {
                return idx.get(indexField).get(key);
            }
        }
        return null;
    }
}