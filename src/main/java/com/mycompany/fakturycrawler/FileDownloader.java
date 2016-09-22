package com.mycompany.fakturycrawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileDownloader implements Runnable {

    private final BlockingQueue<String> filesToAnalyze;
    private final CountDownLatch gate;
    private final AtomicInteger count;
    private final long start;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final BlockingQueue<String> log;
    private final int id;

    FileDownloader(BlockingQueue<String> filesUrlsToDownload, CountDownLatch gate, AtomicInteger count, long start, BlockingQueue<String> log, int id) {
        this.gate = gate;
        this.filesToAnalyze = filesUrlsToDownload;
        this.count = count;
        this.start = start;
        this.log = log;
        this.id = id;
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
            //System.out.println("url -> " + url);
            log.offer(id + " url -> " + url);
            inputStream = new URL(url).openStream();

            String filename = url.substring(url.lastIndexOf("/"));
            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(new File("downloads/" + filename));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            int pocet = count.incrementAndGet();
            //System.out.println("Done! " + pocet + " elapsed: " + getElapsed() + " suborov za minutu: " + getSuborovMinutu(pocet));
            log.offer(id + " Done! " + pocet + " elapsed: " + getElapsedTime(start) + " suborov za minutu: " + getSuborovMinutu(pocet));

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

    private double getSuborovMinutu(int pocet) {
        double elapsedTime = ((System.currentTimeMillis() - start) / 1000.0);
        double minut =  ((elapsedTime) / (60));

        return pocet / minut;
    }

      public static String getElapsedTime(long startTime) {
        double elapsedTime = ((System.currentTimeMillis() - startTime) / 1000.0);
        int hodinE = (int) ((elapsedTime) / (3600));
        int minutE = (int) ((elapsedTime) / (60));
        int sekundE = (int) ((elapsedTime));
        sekundE %= 60;
        minutE %= 60;
        String hodinStringE = "" + hodinE;
        if (hodinE < 10) {
            hodinStringE = "0" + hodinE;
        }
        String minutStringE = "" + minutE;
        if (minutE < 10) {
            minutStringE = "0" + minutE;
        }
        String sekundStringE = "" + sekundE;
        if (sekundE < 10) {
            sekundStringE = "0" + sekundE;
        }
        //System.out.println("ETA:" + (hodinStringE + ":" + minutStringE + ":" + sekundStringE));
        return (hodinStringE + ":" + minutStringE + ":" + sekundStringE);
    }
}
