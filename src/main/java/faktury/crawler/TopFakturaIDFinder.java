/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faktury.crawler;

import static faktury.crawler.FileDownloader.getElapsedTime;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

/**
 *
 * @author janmu
 */
public class TopFakturaIDFinder { // BINARNE VYHLADAVANIE NEFUNGUJE LEBO SU CHYBNE URLCKA MEDZI DOBRYMI

    public static void main(String[] args) {
        TopFakturaIDFinder m = new TopFakturaIDFinder();
        m.execute();
    }

// vzdy plati ze min vyhovuje a max nevyhovuje
    public int getCislo(int min, int max) {
        // maximum nevyhovuje tak sa pytame na stred
        // ak vyhovuje stred tak spravna hodnota je v intervale stred-max
        // inak je hodnota v min-stred
        if (min + 1 == max) {
            return min;
        }
        int stred = min + (max - min) / 2 + (max - min) % 2;
        if (vyhovuje(stred)) {
            return getCislo(stred, max);
        } else {
            return getCislo(min, stred);
        }
    }

    public void execute() {
        int max = 246900;

        int maximum;
        if (vyhovuje(max)) {
            maximum = max;
        } else {
            // musime rozdelovat na podintervaly
            maximum = getCislo(2000000, max);// max je vzdy vecsie ako 1, lebo ak by bolo 1 tak by uz vyhovovalo v ife
        }

        System.out.println("max cislo faktury je:" + maximum);
    }
    public static final String TEMPLATE = "http://cr.iedu.sk/data/att/%d_subor.pdf";

    private boolean vyhovuje(int cislo) {
        String url = String.format(TEMPLATE, cislo);
        System.out.print("url => "+url);
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new URL(url).openStream();
            System.out.println(" == true;");
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
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
        System.out.println(" == false;");
        return false;
    }

    private void downloadFile(String url) {

    }
}
