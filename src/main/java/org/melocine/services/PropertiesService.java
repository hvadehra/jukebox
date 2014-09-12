package org.melocine.services;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.util.Iterator;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: hemanshu.v
 * Date: 8/7/14
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesService {
    private final Properties properties;

    public PropertiesService() {
        properties = loadConfig("./.config");
    }

    private static Properties loadConfig(String propertiesFileName) {
        try {
            Properties properties = new Properties();
            Configuration config = new PropertiesConfiguration(propertiesFileName);
            Iterator<String> keys = config.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                properties.put(key, config.getString(key));
            }

            return properties;
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public Properties properties() {
        return properties;
    }
}
