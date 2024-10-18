package com.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Config {
    private static final Properties properties = new Properties();

    private final Logger logger = Logger.getLogger(Config.class.getName());
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IllegalArgumentException("can't find application.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}