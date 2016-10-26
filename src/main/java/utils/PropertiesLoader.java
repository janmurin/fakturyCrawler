/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author janmu
 */
public class PropertiesLoader {

    public final Properties properties;

    public PropertiesLoader(){
        properties = loadProperties();
    }
    
    private Properties loadProperties() {
        InputStream input = null;
        Properties properties = new Properties();

        try {
            input = new FileInputStream("application.properties");

            properties.load(input);
//            System.out.println(properties.getProperty("jdbc.unifikator.url"));
//            System.out.println(properties.getProperty("jdbc.user"));
//            System.out.println(properties.getProperty("jdbc.password"));
            return properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
