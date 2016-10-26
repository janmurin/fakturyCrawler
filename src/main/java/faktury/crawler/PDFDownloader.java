package faktury.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import utils.LoggerThread;
import utils.PropertiesLoader;

public class PDFDownloader {

    private static int NUMBER_OF_DOWNLOADERS = 10;
    private static int CHCEME_NAJST;

    /*
    tento program prechadza vsetky idcka faktur a pomocou vygenerovanych url adries stahuje vsetky pdfka 
    ak sa v danom pdfku nachadza referencia na UPJS, tak sa ulozi cely subor pdf, inak sa ulozi do databazy len text a url adresa na ktorej bol text ziskany
     */
    public static void main(String[] args) {
        AtomicInteger navstivenych = new AtomicInteger(0);
        AtomicInteger najdenych = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        BlockingQueue<String> log = new LinkedBlockingQueue<>();
        BlockingQueue<String> filesUrlsToDownload = new LinkedBlockingQueue<>();

        Properties properties = new PropertiesLoader().properties;

        try {
            if (args[1] != null) {
                NUMBER_OF_DOWNLOADERS = Integer.parseInt(args[1]);
            }

            if (args[1] != null) {
                CHCEME_NAJST = Integer.parseInt(args[2]);
            }

            LoggerThread lt = new LoggerThread(log, "fakturyCrawler.log");
            Thread loggerThread = new Thread(lt);
            loggerThread.start();

            Searcher searcher = new Searcher(filesUrlsToDownload, Integer.parseInt(args[0]), NUMBER_OF_DOWNLOADERS);
            //Searcher searcher = new Searcher(filesUrlsToDownload, 1998484);
            Thread searcherThread = new Thread(searcher);
            searcherThread.start();

            CountDownLatch gate = new CountDownLatch(NUMBER_OF_DOWNLOADERS);

            for (int i = 0; i < NUMBER_OF_DOWNLOADERS; i++) {
                FileDownloader a = new FileDownloader(gate, i, properties, navstivenych, najdenych, log, filesUrlsToDownload, start, CHCEME_NAJST);
                Thread analyzerThread = new Thread(a);
                analyzerThread.start();
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
