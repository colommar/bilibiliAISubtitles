package com.github.colommar.infrastructure.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置加载器
 */
@Slf4j
public class ConfigLoader {
    
    private static final String CONFIG_FILE = "config.properties";
    
    /**
     * 从配置文件加载SESSDATA
     */
    public static String loadSessdataFromConfig() throws IOException {
        Properties properties = loadProperties();
        String sessdata = properties.getProperty("sessdata");
        if (sessdata == null || sessdata.trim().isEmpty()) {
            throw new IOException("SESSDATA not found or empty in config.properties");
        }
        return sessdata;
    }
    
    /**
     * 从配置文件加载视频ID
     */
    public static String loadVideoIdFromConfig() throws IOException {
        Properties properties = loadProperties();
        String videoId = properties.getProperty("videoId");
        if (videoId == null || videoId.trim().isEmpty()) {
            throw new IOException("videoId not found or empty in config.properties");
        }
        return videoId;
    }
    
    /**
     * 从配置文件加载是否显示时间详情
     */
    public static boolean loadIsTimeDetailOnFromConfig() throws IOException {
        Properties properties = loadProperties();
        String isTimeDetailOn = properties.getProperty("isTimeDetailOn");
        if (isTimeDetailOn == null || isTimeDetailOn.trim().isEmpty()) {
            throw new IOException("isTimeDetailOn not found or empty in config.properties");
        }
        
        if ("true".equals(isTimeDetailOn)) {
            return true;
        } else if ("false".equals(isTimeDetailOn)) {
            return false;
        } else {
            log.error("Invalid isTimeDetailOn property: {}", isTimeDetailOn);
            throw new IOException("Invalid isTimeDetailOn property");
        }
    }
    
    /**
     * 从配置文件加载是否输出到文件
     */
    public static boolean loadIsFileOutputFromConfig() throws IOException {
        Properties properties = loadProperties();
        String isFileOutput = properties.getProperty("isFileOutput");
        if (isFileOutput == null || isFileOutput.trim().isEmpty()) {
            throw new IOException("isFileOutput not found or empty in config.properties");
        }
        
        if ("true".equals(isFileOutput)) {
            return true;
        } else if ("false".equals(isFileOutput)) {
            return false;
        } else {
            log.error("Invalid isFileOutput property: {}", isFileOutput);
            throw new IOException("Invalid isFileOutput property");
        }
    }
    
    /**
     * 加载配置文件
     */
    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
            return properties;
        }
    }
}
