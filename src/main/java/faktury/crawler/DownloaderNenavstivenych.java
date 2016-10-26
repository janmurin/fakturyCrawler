/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faktury.crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.LoggerThread;
import utils.PropertiesLoader;

/**
 *
 * @author janmu
 */
public class DownloaderNenavstivenych {

    private static int NUMBER_OF_DOWNLOADERS = 20;

    private static int CHCEME_NAJST=30000;

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

        List<Integer> naozajNenavstivene = new ArrayList<>();
        String TEMPLATE = "http://cr.iedu.sk/data/att/%d_subor.pdf";
        Scanner s = null;
        try {
            s = new Scanner(new File("nenavstivene.txt"));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.length() < 3) {
                    break;
                }
                int id = Integer.parseInt(line);
                filesUrlsToDownload.offer(String.format(TEMPLATE, id));
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }
        log.offer("pridanych nenavstivenych do queue: " + filesUrlsToDownload.size());

        try {
            if (args != null) {
                if (args[1] != null) {
                    NUMBER_OF_DOWNLOADERS = Integer.parseInt(args[1]);
                }

                if (args[2] != null) {
                    CHCEME_NAJST = Integer.parseInt(args[2]);
                }
            }

            LoggerThread lt = new LoggerThread(log, "fakturyCrawler.log");
            Thread loggerThread = new Thread(lt);
            loggerThread.start();

//            Searcher searcher = new Searcher(filesUrlsToDownload, Integer.parseInt(args[0]));
//            //Searcher searcher = new Searcher(filesUrlsToDownload, 1998484);
//            Thread searcherThread = new Thread(searcher);
//            searcherThread.start();
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
