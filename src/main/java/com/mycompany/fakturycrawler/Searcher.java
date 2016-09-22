package com.mycompany.fakturycrawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Queue;

public class Searcher implements Runnable {

    private Queue<String> queue;
    public static final String TEMPLATE = "http://cr.iedu.sk/data/att/%d_subor.pdf";

    public Searcher(Queue<String> queue) {
        this.queue = queue;
    }

    public void run() {
        int start = 2255309;
        String urlTemplate = "http://cr.iedu.sk/data/att/%d_subor.pdf";
        int count = 0;

        while (start > 0) {

            String url = String.format(urlTemplate, start);
            queue.offer(url);

            start -= 2;
        }
    }

}
