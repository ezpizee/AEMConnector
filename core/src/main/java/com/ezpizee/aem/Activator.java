package com.ezpizee.aem;


import org.apache.felix.scr.annotations.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(immediate = true)
public class Activator implements BundleActivator {

    private static final Logger LOG = LoggerFactory.getLogger(Activator.class);

    @Activate
    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Activated!!!");
    }

    @Deactivate
    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("DeActivated!!!");
    }
}
