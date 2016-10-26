package zmluvy.crawler;

import utils.LoggerThread;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZmluvyDownloader {

    public static final int NUMBER_OF_DOWNLOADERS = 30;
    public static BlockingQueue<Zmluva> zmluvy = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        try {
            AtomicInteger count = new AtomicInteger(0);
            final long start = System.currentTimeMillis();
            BlockingQueue<String> zmluvyUrlsToDownload = new LinkedBlockingQueue<>();
            BlockingQueue<String> log = new LinkedBlockingQueue<>();

            LoggerThread lt = new LoggerThread(log,"zmluvyCrawler.log");
            Thread loggerThread = new Thread(lt);
            loggerThread.start();

//            ZmluvySearcher searcher = new ZmluvySearcher(filesUrlsToDownload, Integer.parseInt(args[0]));
//            Thread searcherThread = new Thread(searcher);
//            searcherThread.start();
            loadZmluvyUrls(zmluvyUrlsToDownload);
            if (!zmluvyUrlsToDownload.isEmpty()) {
                CountDownLatch gate = new CountDownLatch(NUMBER_OF_DOWNLOADERS);

                for (int i = 0; i < NUMBER_OF_DOWNLOADERS; i++) {
                    ZmluvaParser a = new ZmluvaParser(zmluvyUrlsToDownload, gate, count, start, log, i);
                    Thread analyzerThread = new Thread(a);
                    analyzerThread.start();
                }
                gate.await();
                log.offer("poison.pill");
                System.out.println("naloadovanych zmluv: " + zmluvy.size());
                if (zmluvy.size() > 0) {
                    System.out.println("zapisujem zmluvy do zmluvy.json");
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        mapper.writeValue(new File("zmluvy.json"), new ZmluvyJson(zmluvy));
                    } catch (Exception ex) {
                        Logger.getLogger(ZmluvyDownloader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("nenaparsovalo ziadne nove zmluvy");
                }
            }
            System.out.println("Running time: " + (System.nanoTime() - start) / 1000000.0 + " ms");

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void loadZmluvyUrls(BlockingQueue<String> zmluvyUrlsToDownload) {

        ObjectMapper mapper = new ObjectMapper();
        Set<String> naparsovane = new HashSet<>();
        try {
            ZmluvyJson zml = mapper.readValue(new File("zmluvy.json"), ZmluvyJson.class);
            for (Zmluva z : zml.getZmluvy()) {
                naparsovane.add(z.getUrl());
                zmluvy.offer(z);
            }

        } catch (Exception ex) {
            Logger.getLogger(ZmluvyDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scanner s = null;

        try {
            s = new Scanner(new File("zmluvy_crz.txt"));

            while (s.hasNextLine()) {
                String line = s.nextLine();
                String url = line.substring(line.indexOf("_") + 1);
                if (!line.startsWith("url") && !naparsovane.contains(url)) {
                    zmluvyUrlsToDownload.offer(url);
                }
//                if (zmluvyUrlsToDownload.size() > 100) {
//                    break;
//                }
            }
            System.out.println("pridanych na parsovanie do queue urlciek: " + zmluvyUrlsToDownload.size());
            for (int i = 0; i < NUMBER_OF_DOWNLOADERS; i++) {
                zmluvyUrlsToDownload.offer("poison.pill");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ZmluvyDownloader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

}
