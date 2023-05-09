package com.ynov.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    /**
     * Loads the configuration from the specified properties file.
     *
     * @return A properties containing the necessary configuration.
     */
    public static Properties loadConfig() {
        Properties properties = new Properties();
        String configFileName = "config.properties";
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                System.err.println("Properties not found");
                return null;
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}

