package com.ezpizee.aem.utils;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;

public class OSGiConfigUtil {

    private static final Logger LOG = LoggerFactory.getLogger(OSGiConfigUtil.class);

    private OSGiConfigUtil() {}

    public static void save(final ConfigurationAdmin configAdmin, final String className, final Map<String, Object> props) {
        if (configAdmin != null && StringUtils.isNotEmpty(className) && !props.isEmpty()) {
            try {
                Configuration appConfigImplConfig = configAdmin.getConfiguration(className);
                Dictionary<String, Object> properties = appConfigImplConfig.getProperties();
                for (String key : props.keySet()) {
                    props.put(key, props.get(key));
                }
                appConfigImplConfig.update(properties);
            }
            catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
