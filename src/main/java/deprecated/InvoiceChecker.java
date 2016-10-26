package deprecated;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InvoiceChecker {

    private String id;
    private String text = "";

    public InvoiceChecker(String id) {
        try {
            this.id = id;
            File file = new File("downloads/" + this.id + "_subor.pdf");
            if ((file.length()/1024) < 2000) {
                PdfReader reader = new PdfReader("downloads/" + this.id + "_subor.pdf");
                this.text = PdfTextExtractor.getTextFromPage(reader, 1);
            }
        } catch (IOException ex) {
            Logger.getLogger(InvoiceChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkInovice() {
        Path path = Paths.get("downloads/" + this.id + "_subor.pdf");
        Path path2 = Paths.get("invoices/" + this.id + "_subor.pdf");
        if (this.text.startsWith("DODÁVATEĽ: ODBERATEĽ:")) {
            if (this.text.contains("Univerzita Pavla Jozefa")) {
                try {
                    Files.copy(path, path2);
                    System.err.println("--- Copied! ---");
                } catch (IOException ex) {
                    Logger.getLogger(InvoiceChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
