package unifikator.crawler;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.util.Properties;
import utils.JSoup;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;

public class UnifikatorSearcher implements Runnable {

    private int start;
    public static final String TEMPLATE = "http://cr.iedu.sk/univerzita-pavla-jozefa-safarika-v-kosiciach/faktury/?page=%d";
    private JSoup jsoup = new JSoup();
    final JdbcTemplate jdbcTemplate;
    MysqlDataSource dataSource = new MysqlDataSource();

    UnifikatorSearcher(int i, Properties properties) {
               this.start = start;
        dataSource.setURL(properties.getProperty("jdbc.unifikator.url"));
        dataSource.setUser(properties.getProperty("jdbc.user"));
        dataSource.setPassword(properties.getProperty("jdbc.password"));
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run() {
        try {
            int page = start;
            //UnifikatorDownloader.log;

            while (true) {
                String url = String.format(TEMPLATE, page);
                UnifikatorDownloader.log.offer("searching url: " + url);
                try {
                    Document doc = jsoup.getPage(url);
                    jdbcTemplate.update("insert into searcher_data(url,html)VALUES(?,?);", url, doc.html());
                    
                    Elements linky = doc.select("tr.list_zmluva .cell1");
                    
                    for (int i = 0; i < linky.size(); i++) {
                        Element el = linky.get(i);
                        UnifikatorDownloader.fakturyIDs.offer(el.text());
                        UnifikatorDownloader.log.offer("najdene id: " + el.text());
                    }
                    if (linky.size() == 0) {
                        UnifikatorDownloader.log.offer("linky size = 0: ");
                        break;
                    }
                    
                    page += 3;

                    //break;
                } catch (Exception ex) {
                    UnifikatorDownloader.log.offer("exception: " + ex.getMessage());
                    Logger.getLogger(UnifikatorSearcher.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
            }
        } catch (Exception e) {
        }
        UnifikatorDownloader.searcherGate.countDown();
    }

}
