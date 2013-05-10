package org.ucair.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Configuration;

@Configuration("appConfig")
public class UCAIRConfig {

    private final String propertiesFilePath = "etc/ucair.properties";

    private final Properties properties = new Properties();

    public UCAIRConfig() throws IOException {
        load();
    }

    public void load() throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(
                propertiesFilePath));
        try {
            properties.load(reader);
        } finally {
            reader.close();
        }
    }

    public void store() throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(
                propertiesFilePath));
        try {
            properties.store(writer, "UCAIR configuration");
        } finally {
            writer.close();
        }
    }

    public String get(final String key) {
        return properties.getProperty(key);
    }

    public void set(final String key, final String value) {
        properties.setProperty(key, value);
    }
}
