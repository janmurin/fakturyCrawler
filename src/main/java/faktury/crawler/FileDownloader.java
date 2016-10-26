package faktury.crawler;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.springframework.jdbc.core.JdbcTemplate;

public class FileDownloader implements Runnable {

    private final CountDownLatch gate;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final int id;
    final JdbcTemplate jdbcTemplate;
    MysqlDataSource dataSource = new MysqlDataSource();
    private final long start;
    private final BlockingQueue<String> filesUrlsToDownload;
    private final BlockingQueue<String> log;
    private final AtomicInteger najdenych;
    private final AtomicInteger navstivenych;
    private int CHCEME_NAJST;

    FileDownloader(CountDownLatch gate, int id, Properties properties, AtomicInteger navstivenych, AtomicInteger najdenych, BlockingQueue<String> log, BlockingQueue<String> filesUrlsToDownload, long start, int CHCEME_NAJST) {
        this.gate = gate;
        this.id = id;
        dataSource.setURL(properties.getProperty("jdbc.faktury.url"));
        dataSource.setUser(properties.getProperty("jdbc.user"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
        jdbcTemplate = new JdbcTemplate(dataSource);

        this.navstivenych = navstivenych;
        this.najdenych = najdenych;
        this.log = log;
        this.filesUrlsToDownload = filesUrlsToDownload;
        this.start = start;
        this.CHCEME_NAJST = CHCEME_NAJST;
    }

    public void run() {
        try {
            String file = filesUrlsToDownload.take();
            while (file != null) {
                if (file.equals("poison.pill")) {
                    break;
                }
                downloadFile(file);

                file = filesUrlsToDownload.take();
                if (najdenych.get() > CHCEME_NAJST) {
                    throw new InterruptedException("naslo uz 10 000 suborov");
                }
                System.out.println("urlciek za minutu: " + getSuborovMinutu(navstivenych.incrementAndGet()));
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            gate.countDown();
        }
    }
    StringBuilder sb;

    private void downloadFile(String url) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        sb = new StringBuilder("downloader_" + id + " url -> " + url);
        try {
            inputStream = new URL(url).openStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            byte[] byteArray = buffer.toByteArray();

            if (isUPJS(byteArray, url)) {
                outputStream = new FileOutputStream(new File("downloads/" + url.substring(url.lastIndexOf("/"))));
                outputStream.write(byteArray, 0, byteArray.length);
                outputStream.flush();

                sb.append(String.format(" Done! %d", najdenych.incrementAndGet()));
            } else {
                sb.append("  not upjs");
            }
        } catch (Exception e) {
            sb.append("  not found");
            //Logger.getLogger(FileDownloader.class.getName()).log(Level.SEVERE, null, e);
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
        log.offer(sb.toString());
    }
    PdfReader reader = null;

    public boolean isUPJS(byte[] data, String url) {
        //System.out.println("opening file: " + file.getAbsolutePath());
        try {
            reader = new PdfReader(data);
            String text = PdfTextExtractor.getTextFromPage(reader, 1);
            if (text.toLowerCase().contains("univerzita pavla jozefa")) {
                return true;
            } else {
                //jdbcTemplate.execute("insert into faktury(url,pdftext)VALUES('skuska','skuska');");
                jdbcTemplate.update("insert into faktury(url,pdftext)VALUES(?,?);", url, text);
                return false;
            }
        } catch (IOException ex) {
            return false;
            //Logger.getLogger(FileDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private double getSuborovMinutu(int pocet) {
        double elapsedTime = ((System.currentTimeMillis() - start) / 1000.0);
        double minut = ((elapsedTime) / (60));

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

    public static void main(String[] args) {

        //http://cr.iedu.sk/data/att/1996443_subor.pdf
//        InputStream inputStream = null;
//        OutputStream outputStream = null;
//        try {
//            inputStream = new URL("http://cr.iedu.sk/data/att/1996443_subor.pdf").openStream();
//            outputStream = new FileOutputStream(new File("downloads/1996443_subor.pdf"));
//            byte[] bytes = new byte[1024];
//            int read = 0;
//
//            while ((read = inputStream.read(bytes)) != -1) {
//                outputStream.write(bytes, 0, read);
//            }
//            outputStream.flush();
//
//        } catch (IOException e) {
//            //e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (outputStream != null) {
//                try {
//                    // outputStream.flush();
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
    }
}
