package unifikator.crawler;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import faktury.crawler.PDFDownloader;
import utils.JSoup;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.springframework.jdbc.core.JdbcTemplate;

public class UnifikatorParser implements Runnable {

    private final CountDownLatch gate;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final int id;
    public static final String TEMPLATE = "http://cr.iedu.sk/univerzita-pavla-jozefa-safarika-v-kosiciach/faktury/%s/";
    private JSoup jsoup = new JSoup();
    final JdbcTemplate jdbcTemplate;
    MysqlDataSource dataSource = new MysqlDataSource();
    private final long start;

    UnifikatorParser(CountDownLatch gate, int id, Properties properties, long start) {
        this.gate = gate;
        this.id = id;
        dataSource.setURL(properties.getProperty("jdbc.unifikator.url"));
        dataSource.setUser(properties.getProperty("jdbc.user"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.start=start;
    }

    public void run() {
        try {
            String fakturaID = UnifikatorDownloader.fakturyIDs.take();
            while (fakturaID != null) {
                if (fakturaID.equals("poison.pill")) {
                    break;
                }
                String url = String.format(TEMPLATE, fakturaID);
                UnifikatorDownloader.log.offer(id + " downloading url: " + url);
                try {
                    Document doc = jsoup.getPage(url);
                    jdbcTemplate.update("insert into crawler_data(url,html)VALUES(?,?);", url, doc.html());

                    UnifikatorDownloader.log.offer(id + " inserted crawler_data size: " + doc.html().length() + ""
                            + "urlciek za minutu: " + getSuborovMinutu(UnifikatorDownloader.najdenych.incrementAndGet()));
                } catch (Exception ex) {
                    UnifikatorDownloader.log.offer(id + " exception: " + ex.getMessage());
                    Logger.getLogger(UnifikatorParser.class.getName()).log(Level.SEVERE, null, ex);
                }

                fakturaID = UnifikatorDownloader.fakturyIDs.take();
                //return;
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            gate.countDown();
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

}
