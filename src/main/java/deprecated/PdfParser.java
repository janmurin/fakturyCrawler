package deprecated;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Maroš
 */
public class PdfParser {

    public static void filterUpjsFromDownloads() {
        try {
            Parser p = new Parser();
            File dir = new File("downloads");
            File[] files = dir.listFiles();

            for (File f : files) {
                if (f.getName().endsWith(".pdf")) {
                    if (p.isUPJS(f)) {
                        System.out.println(f.getName() + " IS UPJS.");
                        Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(new File("upjsFaktury/" + f.getName()).getAbsolutePath()), REPLACE_EXISTING);
                    } else {
                        System.out.println(f.getName() + " not upjs.");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getTextFromPDF() {
        try {
            Parser p = new Parser();
            File dir = new File("C:\\Users\\janmu\\.m2\\repository\\com\\mycompany\\fc\\1\\downloads");
            File[] files = dir.listFiles();
            int count = 0;
            for (File f : files) {
                count++;
                System.out.println(count + "/" + files.length);
                if (f.getName().endsWith(".pdf")) {
                    Writer out = null;
                    try {
                        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\janmu\\.m2\\repository\\com\\mycompany\\fc\\1\\txt\\" + f.getName().replace(".pdf", ".txt")), "UTF-8"));
                        out.write(p.getRawTextFromPDF(f));
                        out.flush();
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        //new PDFDownloader();
        //new Spustac().execute(2255309);
        getTextFromPDF();
    }

}
