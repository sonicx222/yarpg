package de.pho.descent.fxclient.business.config;

import de.pho.descent.shared.auth.ParamValue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author pho
 */
public class Configuration {

    public static final String LOGGING_PROPERTIES_FILE = "logging.properties";
    public static final String PROLOG_PROPERTIES_FILE = "prolog.properties";
    public static final String ERROR_LOGGING_PROPERTIES = "Error loading the configuration from 'logging.properties'";
    
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");
    
    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());
    private static final Properties CONFIG_PROPERTIES = new Properties();
    
    public static void loadProperties() throws UnsupportedEncodingException, IOException {
        // config file needs to be in same folder as app .jar
        String path = "." + File.separator + ParamValue.CONFIG_FILE;
        InputStreamReader in = null;
        try {
            in = new InputStreamReader(new FileInputStream(path), "UTF-8");
            CONFIG_PROPERTIES.load(in);
        } catch (FileNotFoundException ex) {
            LOGGER.info(ParamValue.CONFIG_FILE + " not found. Using default settings...");
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }
        }
    }

    public static String get(String key) {
        return CONFIG_PROPERTIES.getProperty(key);
    }
}
