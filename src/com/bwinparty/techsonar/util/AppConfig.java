/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Lukas Prettenthaler
 */
public class AppConfig extends XMLConfiguration {

    private static final Log LOG = LogFactory.getLog(AppConfig.class);
    private static AppConfig instance;
    private static final String configFile = "config.xml";

    static {
        instance = new AppConfig(configFile);
    }

    private AppConfig(final String fileName) {
        super();
        this.setReloadingStrategy(new FileChangedReloadingStrategy());
        this.setDelimiterParsingDisabled(true);
        init(fileName);
    }

    private void init(final String fileName) {
        setFileName(fileName);
        try {
            load();
            LOG.info("Configuration loaded");
        } catch (ConfigurationException ex) {
            LOG.error("Configuration not loaded!");
        }
    }

    public static AppConfig getInstance() {
        return instance;
    }
}
