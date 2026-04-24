package com.chitchat.config;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static final Properties props = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }
            props.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load application.properties", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
