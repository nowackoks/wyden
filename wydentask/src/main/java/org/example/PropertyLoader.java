package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;


public class PropertyLoader {
    private static final Properties properties;

    // of course spring app would load these properties by simply using @Value on class property,
    // but I would like to show that http controller can be written without spring boot
    static {
        properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static BigDecimal getMarginPercent() {
        return new BigDecimal(properties.getProperty("price.margin.percent"));
    }

}
