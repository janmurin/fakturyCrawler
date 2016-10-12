package zmluvy;

import fakturycrawler.*;
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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ZmluvaParser implements Runnable {

    private final BlockingQueue<String> filesToAnalyze;
    private final CountDownLatch gate;
    private final AtomicInteger count;
    private final long start;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final BlockingQueue<String> log;
    private final int id;
    private JSoup jsoup = new JSoup();

    ZmluvaParser(BlockingQueue<String> filesUrlsToDownload, CountDownLatch gate, AtomicInteger count, long start, BlockingQueue<String> log, int id) {
        this.gate = gate;
        this.filesToAnalyze = filesUrlsToDownload;
        this.count = count;
        this.start = start;
        this.log = log;
        this.id = id;
    }

    public void run() {
        try {
            String url = filesToAnalyze.take();
            while (url != null) {
                if (url.equals("poison.pill")) {
                    break;
                }
                parseZmluva(url);

                url = filesToAnalyze.take();
                //return;
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            gate.countDown();
        }
    }

    private void parseZmluva(String url) {

        try {
            Document doc = jsoup.getPage(url);

            String datum_zverejnenia = doc.select("div.area:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)").text();
            String datum_uzavretia = doc.select("div.area:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2)").text();
            String datum_ucinnosti = doc.select("div.area:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(2)").text();
            String datum_platnosti = doc.select("div.area:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(2)").text();
            String subor_url = doc.select(".nobullet > a:nth-child(2)").attr("href");
            String typ = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2)").text();
            String rezort = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2)").text();
            String objednavatel = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(2)").html();
            String ico_objednavatel = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(4) > td:nth-child(2)").text();
            String dodavatel = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(5) > td:nth-child(2)").html();
            String tableText = doc.select(".b_right > table:nth-child(2)").text();
            if (tableText.substring(tableText.indexOf("IČO") + 3).indexOf("IČO") == -1) {
                String nazov_zmluvy = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(6) > td:nth-child(2)").text();
                String cislo_zmluvy = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2)").text();
                String crz_id = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(10) > td:nth-child(2)").text();
                String posledna_zmena = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(11) > td:nth-child(2)").text();
                String celkova_ciastka = doc.select("div.last > span:nth-child(2)").text();
                Zmluva zmluva = new Zmluva(datum_platnosti, datum_ucinnosti, datum_uzavretia, datum_zverejnenia, subor_url, typ, rezort, objednavatel, ico_objednavatel, dodavatel, nazov_zmluvy, cislo_zmluvy, crz_id, posledna_zmena, celkova_ciastka, url, "");
                ZmluvyDownloader.zmluvy.offer(zmluva);
            } else {
                String ico_dodavatel = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(6) > td:nth-child(2)").text();
                String nazov_zmluvy = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(7) > td:nth-child(2)").text();
                String cislo_zmluvy = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(8) > td:nth-child(2)").text();
                String crz_id = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(11) > td:nth-child(2)").text();
                String posledna_zmena = doc.select(".b_right > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(12) > td:nth-child(2)").text();
                String celkova_ciastka = doc.select("div.last > span:nth-child(2)").text();
                Zmluva zmluva = new Zmluva(datum_platnosti, datum_ucinnosti, datum_uzavretia, datum_zverejnenia, subor_url, typ, rezort, objednavatel, ico_objednavatel, dodavatel, nazov_zmluvy, cislo_zmluvy, crz_id, posledna_zmena, celkova_ciastka, url, ico_dodavatel);
                ZmluvyDownloader.zmluvy.offer(zmluva);
            }

            log.offer("parser " + id + " nacital zmluvu cislo " + count.incrementAndGet());
//System.out.println(z);
        } catch (Exception ex) {
            log.offer("bad url: " + url);
            Logger.getLogger(ZmluvaParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
