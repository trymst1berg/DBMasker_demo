/*
 * Copyright 2018-2021 Esito AS Licensed under the g9 Anonymizer Runtime License Agreement (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import no.esito.anonymizer.core.FileUtils;

/**
 * This utility handles runtime properties for the Anonymizer.<br>
 * It has methods to read and write config file and get the Properties object in order to manipulate the values.
 */
public class ConfigUtil {

    /**
     * Config file name to use.
     */
    public static String CONFIG_FILE = "config.properties";

    private static Properties config;

    private static String connectionName;

    /**
     * Get current configuration properties. If not present the properties are loaded
     *
     * @return Properties object
     * @throws IOException when problems reading config file
     */
    public static Properties getConfig() throws IOException {
        if (config == null)
            config = ConfigUtil.readConfig();
        return config;
    }

    /**
     * Set current connection name.
     *
     * @param name name
     */
    public static void setConnectionName(String name) {
        connectionName = name;
    }

    /**
     * Read properties file from current directory or if not available the internal one.
     *
     * @return Properties object
     * @throws IOException when problems reading config file
     */
    public static Properties readConfig() throws IOException {
        Properties prop = new Properties();
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file);) {
                Log.info("Reading local config from " + file.getCanonicalPath());
                prop.load(input);
                return prop;
            }
        }
        return readInternalConfig();
    }

    /**
     * Force config to be re-read.
     */
    public static void resetConfig() {
        config = null;
    }

    private static Properties readInternalConfig() throws IOException {
        Properties prop = new Properties();
        try (InputStream input = FileUtils.getResourceAsStream(CONFIG_FILE);) {
            Log.info("Reading internal " + CONFIG_FILE);
            prop.load(input);
        } catch (FileNotFoundException e) {
            // Just ignore this and return the empty property set
        }
        return prop;
    }

    /**
     * Write the current config to current directory.
     * 
     * @throws IOException when problems writing config file
     */
    public static void writeConfig() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("# Copy of current config: ");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:MM:SS")));
        sb.append("\n");
        try (InputStream input = FileUtils.getResourceAsStream(CONFIG_FILE);) {
            Log.info("Reading internal " + CONFIG_FILE);
            sb.append(FileUtils.stream2String(input));
            input.close();
        }
        File file = new File(CONFIG_FILE);
        Log.info("Writing to local config " + file.getCanonicalPath());
        FileUtils.string2File(sb.toString(), file);
    }

    /**
     * Gets the current passkey.
     *
     * @return passkey String
     * @throws IOException when problems reading properties
     */
    public static String getPassKey() throws IOException {
        return getConfig().getProperty("file.encryptionkey");
    }


    public static String getSQLWrapper() throws IOException {
        return getConfig().getProperty("sql.wrapper");
    }

    /**
     * Gets the schema name being used.
     *
     * @return schema name
     * @throws IOException when problems reading schema file
     */
    public static String getSchema() throws IOException {
        return getConfig()
            .getProperty("connection." + (connectionName == null ? "" : (connectionName + ".")) + "schema");
    }

    /**
     * Adds schema prefix to table if necessary.
     *
     * @param table name
     * @return schema prefix name
     * @throws IOException when problems reading config file
     */
    public static String schemaPrefix(String table) throws IOException {
        String schema = getSchema();
        table = wrap(table);
        return (schema != null && !schema.isEmpty()) ? wrap(schema) + "." + table : table;
    }

    /**
     * Wraps/qutoes in " " if it contains spaces.
     *
     * @param name schema/table/column name to be wrapped
     * @return wrapped version if necessary
     */
    public static String wrap(String name) {
        return name.contains(" ") ? "\"" + name + "\"" : name;
    }

}
