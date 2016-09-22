/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fakturycrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author janmu
 */
public class Spustac {

    public void execute() {
        int start = 2255309;
        String urlTemplate = "http://cr.iedu.sk/data/att/%d_subor.pdf";
        int count=0;

        while (start > 0) {

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                // read this file into InputStream
                String url = String.format(urlTemplate, start);
                System.out.println("url -> "+url);
                inputStream = new URL(url).openStream();

                String filename = url.substring(url.lastIndexOf("/"));
                // write the inputStream to a FileOutputStream
                outputStream = new FileOutputStream(new File("downloads/" + filename));

                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                count++;
                System.out.println("Done!\n");

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        // outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            start -= 2;
        }
    }

    public static void main(String[] args) {
        new Spustac().execute();
    }
}
