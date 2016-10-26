package faktury.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Queue;

public class Searcher implements Runnable {

    private Queue<String> queue;
    private int start;
    public static final String TEMPLATE = "http://cr.iedu.sk/data/att/%d_subor.pdf";
    private final int downloaders;

    public Searcher(Queue<String> queue, int start, int downloaders) {
        this.queue = queue;
        this.start = start;
        this.downloaders = downloaders;
    }

    public void run() {
        String urlTemplate = "http://cr.iedu.sk/data/att/%d_subor.pdf";
        int count = 0;

        while (start < 2500000) {

            String url = String.format(urlTemplate, start);
            queue.offer(url);

            //this.start -= 2;
            start++;
        }
        for (int i = 0; i < downloaders; i++) {
            queue.offer("poison.pill");
        }
    }

}
