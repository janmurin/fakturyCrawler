package com.mycompany.fakturycrawler;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class PDFDownloader {

    
    public static final int NUMBER_OF_DOWNLOADERS = 7;

    public static void main(String[] args) {
        try {
            AtomicInteger count=new AtomicInteger(0);
            BlockingQueue<String> filesUrlsToDownload = new LinkedBlockingQueue<>();

            long start = System.nanoTime();
            Searcher searcher = new Searcher(filesUrlsToDownload);
            Thread searcherThread = new Thread(searcher);
            searcherThread.start();
            CountDownLatch gate = new CountDownLatch(NUMBER_OF_DOWNLOADERS);
            FileDownloader a = new FileDownloader(filesUrlsToDownload,  gate, count);
            for (int i = 0; i < NUMBER_OF_DOWNLOADERS; i++) {
                Thread analyzerThread = new Thread(a);
                analyzerThread.start();
            }
            gate.await();
            System.out.println("Running time: " + (System.nanoTime() - start) / 1000000.0 + " ms");

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
