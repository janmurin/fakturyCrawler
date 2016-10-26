package faktury.parser;

import java.io.IOException;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //new PDFDownloader();
        //new Spustac().execute(2255309);
        try {
            ParseCzechJson parse = new ParseCzechJson();
                        parse.parse();
                        
//                         ParseCzech parse = new ParseCzech();
//                        parse.parse("invoices/1364733_subor.pdf");


            //parse.parse("invoices/1841649_subor.pdf");
            //parse.parse("invoices/1375659_subor.pdf");
//            InvoiceChecker checker = new InvoiceChecker("444");
//            checker.checkInovice();
        } catch (IOException ex) {
            Logger.getLogger(PdfParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
