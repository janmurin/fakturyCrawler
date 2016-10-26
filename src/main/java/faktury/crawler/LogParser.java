/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package faktury.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author janmu
 */
public class LogParser {

    public static void parseLog() {
        // hlavnym cielom je rozparsovat log a vypisat ktore linky neboli navstivene
        int URL_COUNT = 2 * 1000 * 1000 + 500 * 1000;
        byte[] urlcka = new byte[URL_COUNT];// -1: nic tam nie je, 0: nenavstivene, 1: upjs, 2: nie upjs
        //Arrays.fill(urlcka, (byte) (-1));

        
        String tag = "http://cr.iedu.sk/data/att/";
        Scanner s = null;

        try {
            s = new Scanner(new File("log.txt"));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.contains("not upjs") || line.contains("not found") || line.contains("Done!")) {
                    line = line.substring(line.indexOf(tag) + tag.length());
                    int id = Integer.parseInt(line.substring(0, line.indexOf("_")));
                    byte val = -1; // not found defaultne
                    if (line.contains("not upjs")) {
                        val = 2;
                    }
                    if (line.contains("Done!")) {
                        val = 1;
                    }
                    urlcka[id] = val;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }

        int[] pocty = new int[4];
        for (int i = 0; i < URL_COUNT; i++) {
            pocty[urlcka[i] + 1]++;
        }
        System.out.println(Arrays.toString(pocty));
        for (int i = 0; i < URL_COUNT; i++) {
            if (urlcka[i] == 1 || urlcka[i] == 2) {
                System.out.println("faktury zacinaju na " + i);
                break;
            }
        }

        for (int i = URL_COUNT - 1; i > 0; i--) {
            if (urlcka[i] == 1 || urlcka[i] == 2) {
                System.out.println("faktury koncia na " + i);
                break;
            }
        }
        System.out.println("vsetkych faktur/objednavok: "+(pocty[2]+pocty[3]));
        System.out.println("navstivenych celkovo: "+(pocty[2]+pocty[3]+pocty[0]));
//        Writer out = null;
//        try {
//            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("nenavstivene.txt"), "UTF-8"));
//            boolean start = false;
//            for (int i = 0; i < URL_COUNT; i++) {
//                if (start) {
//                    if (urlcka[i] == 0) {
//                        out.write(i + "\n");
//                    }
//                } else if (urlcka[i] == 1) {
//                    start = true;
//                }
//            }
//            out.flush();
//        } catch (UnsupportedEncodingException ex) {
//            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
    }

    public static void skontroluj() {
        // skontrolujeme ci nenavstivene su naozaj nenavstivene
        File dir = new File("C:\\DATA\\TSSU\\fakturypdf");
        File[] pdfka = dir.listFiles();
        boolean[] navstivene = new boolean[2 * 1000 * 1000];
        for (File f : pdfka) {
            int id = Integer.parseInt(f.getName().substring(0, f.getName().indexOf("_")));
            navstivene[id] = true;
        }

        Scanner s = null;

        List<Integer> naozajNenavstivene = new ArrayList<>();
        int riadkov = 0;
        try {
            s = new Scanner(new File("nenavstivene.txt"));
            while (s.hasNextLine()) {
                String line = s.nextLine();
                riadkov++;
                if (line.length() < 3) {
                    break;
                }
                int id = Integer.parseInt(line);
                if (!navstivene[id]) {
                    naozajNenavstivene.add(id);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }

        System.out.println("naozaj nenavstivenych je: " + naozajNenavstivene.size() + " nespravne nenavstivenych je: " + (riadkov - naozajNenavstivene.size()));
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("naozaj_nenavstivene.txt"), "UTF-8"));
            boolean start = false;
            for (int i = 0; i < naozajNenavstivene.size(); i++) {
                out.write(naozajNenavstivene.get(i) + "\n");
            }
            out.flush();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(LogParser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        parseLog();
    }

}
