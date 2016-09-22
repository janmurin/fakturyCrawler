package com.mycompany.fakturycrawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDownloader implements Runnable {

    private final BlockingQueue<String> filesToAnalyze;
    private final CountDownLatch gate;
    private final AtomicInteger count;

    FileDownloader(BlockingQueue<String> filesUrlsToDownload, CountDownLatch gate, AtomicInteger count) {
        this.gate = gate;
        this.filesToAnalyze = filesUrlsToDownload;
        this.count = count;
    }

    public void run() {
        try {
            String file = filesToAnalyze.take();
            while (file != null) {
                if (file.equals("poison.pill")) {
                    break;
                }
                downloadFile(file);

                file = filesToAnalyze.take();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            gate.countDown();
        }
    }

    private void downloadFile(String url) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            System.out.println("url -> " + url);
            inputStream = new URL(url).openStream();

            String filename = url.substring(url.lastIndexOf("/"));
            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(new File("downloads/" + filename));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            System.out.println("Done! " + count.incrementAndGet() + "\n");

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
    }
}
