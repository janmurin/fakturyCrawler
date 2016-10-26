package unifikator.crawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import utils.LoggerThread;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import utils.PropertiesLoader;

public class UnifikatorDownloader {

    public static final int NUMBER_OF_DOWNLOADERS = 20;
    public static final BlockingQueue<String> fakturyIDs = new LinkedBlockingQueue<>();
    public static final BlockingQueue<String> log = new LinkedBlockingQueue<>();
    public static final AtomicInteger najdenych = new AtomicInteger(0);
    public static final long start = System.currentTimeMillis();
    public static final CountDownLatch searcherGate = new CountDownLatch(3);

    public static void main(String[] args) {
        Properties properties = new PropertiesLoader().properties;

        try {
            LoggerThread lt = new LoggerThread(log, "unifikatorCrawler.log");
            Thread loggerThread = new Thread(lt);
            loggerThread.start();

            UnifikatorSearcher searcher = new UnifikatorSearcher(3, properties);
            Thread searcherThread = new Thread(searcher);
            searcherThread.start();

            UnifikatorSearcher searcher2 = new UnifikatorSearcher(4, properties);
            Thread searcherThread2 = new Thread(searcher2);
            searcherThread2.start();

            UnifikatorSearcher searcher3 = new UnifikatorSearcher(5, properties);
            Thread searcherThread3 = new Thread(searcher3);
            searcherThread3.start();

            CountDownLatch gate = new CountDownLatch(NUMBER_OF_DOWNLOADERS);

            for (int i = 0; i < NUMBER_OF_DOWNLOADERS; i++) {
                UnifikatorParser a = new UnifikatorParser(gate, i, properties, start);
                Thread analyzerThread = new Thread(a);
                analyzerThread.start();
            }
            searcherGate.await();
            for (int i = 0; i < UnifikatorDownloader.NUMBER_OF_DOWNLOADERS; i++) {
                UnifikatorDownloader.fakturyIDs.offer("poison.pill");
            }
            gate.await();
            log.offer("poison.pill");

            System.out.println("Running time: " + (System.nanoTime() - start) / 1000000.0 + " ms");

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



}
