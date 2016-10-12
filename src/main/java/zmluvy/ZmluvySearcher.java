package zmluvy;



import static com.sun.org.apache.bcel.internal.util.SecuritySupport.getResourceAsStream;
import fakturycrawler.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ZmluvySearcher implements Runnable {

    private Queue<String> queue;
    private int start;
    public static final String TEMPLATE = "https://www.crz.gov.sk/index.php?ID=2171273&art_zs1=UPJS&page=%d";
    private JSoup jsoup=new JSoup();

    public ZmluvySearcher(Queue<String> queue, int start) {
        this.queue = queue;
        this.start = start;
    }

  @Override
    public void run() {
       int page=start;
       
       while(true){
           String url = String.format(TEMPLATE, page);
           
           try {
               Document doc = jsoup.getPage(url);
               
               Elements linky = doc.select("table.table_list a");
               
               for(int i=10; i<linky.size(); i++){
                   Element el = linky.get(i);
                   System.out.println(i+". a="+el.attr("href"));
               }
               
               break;
           } catch (Exception ex) {
               Logger.getLogger(ZmluvySearcher.class.getName()).log(Level.SEVERE, null, ex);
               break;
           }
           
       }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException {
             TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream keystoreStream = getResourceAsStream("crz.gov.crt");
        keystore.load(keystoreStream, new char[0]);
        trustManagerFactory.init(keystore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustManagers, null);
        SSLContext.setDefault(sc);
        new ZmluvySearcher(null, 0).run();
    }

}
