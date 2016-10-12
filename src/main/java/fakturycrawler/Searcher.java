

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

    public Searcher(Queue<String> queue, int start) {
        this.queue = queue;
        this.start = start;
    }

    public void run() {
        String urlTemplate = "http://cr.iedu.sk/data/att/%d_subor.pdf";
        int count = 0;

        while (start > 0) {

            String url = String.format(urlTemplate, start);
            queue.offer(url);

            this.start -= 2;
        }
    }

}
