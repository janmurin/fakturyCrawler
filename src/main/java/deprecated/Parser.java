package deprecated;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno Lowagie (iText Software)
 */
public class Parser {

//    public static final String SRC = "sources/skuska.pdf";
//    public static final String DEST = "results/";
//
//    public static void main(String[] args) throws IOException, DocumentException {
//        File file = new File(DEST);
//        file.getParentFile().mkdirs();
//        new ParseCzech().parse(SRC);
//    }
    public boolean isUPJS(File file) throws IOException {
        //System.out.println("opening file: " + file.getAbsolutePath());
        PdfReader reader = null;
        try {
            reader = new PdfReader(new FileInputStream(file));
        } catch (Exception iOException) {
            return false;
        }
        String text = PdfTextExtractor.getTextFromPage(reader, 1);
        return text.contains("Univerzita Pavla Jozefa");
    }

    public String getRawTextFromPDF(File file) throws IOException {
        //System.out.println("opening file: " + file.getAbsolutePath());
        PdfReader reader = null;
        try {
            reader = new PdfReader(new FileInputStream(file));
            String text = PdfTextExtractor.getTextFromPage(reader, 1);
            return text;
        } catch (Exception iOException) {
            return "EXCEPTION";
        }
    }

    public void parse(String filename) throws IOException {
        int faktura_id = 0;
        int polozka_id = 0;
        int dodavatel_id = 0;
        Map<String, String> faktura = new HashMap<>();
        Map<String, String> dodavatel = new HashMap<>();
        Map<String, String> polozka = new HashMap<>();
        PdfReader reader = new PdfReader(filename);
        faktura_id++;
        polozka_id++;
        dodavatel_id++;
        //FileOutputStream fos = new FileOutputStream(DEST);
        String text = PdfTextExtractor.getTextFromPage(reader, 1);
        System.out.println("TEXT:\n" + text);
        System.out.println("\n");
        String lines[] = text.split("\\r?\\n", -1);
        try {
            if (lines[9].contains("DIČ")) {
                dodavatel.put("Nazov", lines[1]);
                if (!lines[1].equals("Drobný nákup")) {
                    dodavatel.put("Ulica", lines[2].replace(" Univerzita Pavla Jozefa Šafárika", "").replace(" Univerzita Pavla Jozefa", ""));
                    dodavatel.put("Ico", lines[6].substring(6, 14));
                    if (lines[6].length() > 25) {
                        dodavatel.put("Dic", lines[6].substring(21, 31));
                        dodavatel.put("Ic_dph", "SK" + lines[6].substring(21, 31));
                    }
                }
                dodavatel.put("Mesto", lines[3].replace(" Šafárika v Košiciach", "").replace(" v Košiciach", "").substring(7));
                dodavatel.put("Psc", lines[3].replace(" Šafárika v Košiciach", "").replace(" v Košiciach", "").substring(0, 6));

            } else {
                dodavatel.put("Nazov", lines[1] + " " + lines[2].replace(" Univerzita Pavla Jozefa Šafárika", "").replace(" Univerzita Pavla Jozefa", ""));
                dodavatel.put("Ulica", lines[3].replace(" Šafárika v Košiciach", "").replace(" v Košiciach", ""));
                dodavatel.put("Mesto", lines[4].replace(" Šrobárova 2", "").substring(7));
                dodavatel.put("Psc", lines[4].replace(" Šafárika v Košiciach", "").replace(" v Košiciach", "").substring(0, 6));
                dodavatel.put("Ico", lines[6].substring(6, 14));
                if (lines[6].length() > 35) {
                    dodavatel.put("Dic", lines[6].substring(21, 31));
                }
                if (lines[7].length() > 25) {
                    dodavatel.put("Ic_dph", "SK" + lines[7].substring(10, 20));
                }
            }
            faktura.put("Poznavacie_cislo", lines[10].substring(9).replaceAll("\\s+", ""));
            DateFormat defaultFormat = new SimpleDateFormat("dd.MM.yyyy");
            DateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
            faktura.put("Datum_vyhotovenia", dbFormat.format(defaultFormat.parse(lines[12].replace("Dátum vyhotovenia:", "").replaceAll("\\s+", ""))));
            faktura.put("Splatnost", dbFormat.format(defaultFormat.parse(lines[13].replace("Splatnosť:", "").replaceAll("\\s+", ""))));
            faktura.put("Datum_dodania", dbFormat.format(defaultFormat.parse(lines[14].replace("Dátum dodania:", "").replaceAll("\\s+", ""))));
            faktura.put("Mena", lines[15].substring(5).replaceAll("\\s+", ""));
            faktura.put("Celkova_suma", lines[lines.length - 1].replace("Konečná čiastka", "").replaceAll("\\s+", "").replace(".", "").replace(",", "."));
            String item = "";
            for (int i = 16; i < lines.length - 1; i++) {
                item += lines[i];
            }
            item = item.replaceAll("_", "").replace("Pol. Množst. Názov Cena s DPH", "").replaceAll("\\s+", " ");
            String polozka_parse[] = item.split(" ", -1);
            polozka.put("Cislo_polozky", polozka_parse[0]);
            polozka.put("Mnozstvo", polozka_parse[1]);
            String real_item = "";
            for (int i = 3; i < polozka_parse.length - 1; i++) {
                real_item += " " + polozka_parse[i];
            }
            real_item = real_item.substring(1);
            polozka.put("Nazov", real_item);
            polozka.put("Cena_s_dph", polozka_parse[polozka_parse.length - 1].replace(".", "").replace(",", "."));

            System.out.println("DODAVATEL:");
            for (Entry<String, String> e : dodavatel.entrySet()) {
                System.out.println(e.getKey() + " = " + e.getValue());
            }

//            try {
//                Class.forName(DatabaseSetting.DRIVER_CLASS);
//                Connection connection = null;
//                try {
//                    connection = DriverManager.getConnection(DatabaseSetting.URL, DatabaseSetting.USER, DatabaseSetting.PASSWORD);
//                } catch (SQLException ex) {
//                    Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                try {
//                    java.sql.Statement stm = connection.createStatement();
//                    ResultSet rs = stm.executeQuery("SELECT Dodavatel_id FROM dodavatel WHERE Nazov = '" + dodavatel.get("Nazov") + "'AND Psc='" + dodavatel.get("Psc") + "'");
//                    while (rs.next()) {
//                        dodavatel_exist = rs.getInt("Dodavatel_id");
//                    }
//                    stm.close();
//                } catch (SQLException ex) {
//                    Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } catch (ClassNotFoundException ex) {
//                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            String keys;
//            String values;
//            
//            keys = "Dodavatel_id";
//            if (dodavatel_exist != 0) {
//                dodavatel_id--;
//            } else {
//                dodavatel_exist = dodavatel_id;
//                values = "'" + String.valueOf(dodavatel_exist) + "'";
//                for (Map.Entry<String, String> entry3 : dodavatel.entrySet()) {
//                    keys += ", " + entry3.getKey();
//                    values += ", '" + entry3.getValue() + "'";
//                    //System.out.println(entry3.getKey() + " : " + entry3.getValue());
//                }
//                String dodavatel_sql = "INSERT INTO dodavatel(" + keys + ") VALUES( " + values + " )";
//                System.out.println(dodavatel_sql);
//            }
//
//            keys = "Faktura_id, Dodavatel_Dodavatel_id";
//            values = "'" + String.valueOf(faktura_id) + "', '" + "'" + dodavatel_exist + "'";
//            for (Map.Entry<String, String> entry2 : faktura.entrySet()) {
//                keys += ", " + entry2.getKey();
//                values += ", '" + entry2.getValue() + "'";
//                //System.out.println(entry2.getKey() + " : " + entry2.getValue());
//            }
//            String faktura_sql = "INSERT INTO faktura(" + keys + ") VALUES( " + values + " )";
//            System.out.println(faktura_sql);
//
//            keys = "Polozka_id, Faktura_Faktura_id, Faktura_Dodavatel_Dodavatel_id";
//            values = "'" + String.valueOf(polozka_id) + "', ";
//            for (Map.Entry<String, String> entry : polozka.entrySet()) {
//                keys += ", " + entry.getKey();
//                values += ", '" + entry.getValue() + "'";
//                //System.out.println(entry.getKey() + " : " + entry.getValue());
//            }
//            String polozka_sql = "INSERT INTO polozka(" + keys + ") VALUES( " + values + " )";
//            System.out.println(polozka_sql);
        } catch (ParseException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }

//        try {
//            Class.forName(DatabaseSetting.DRIVER_CLASS);
//            Connection connection = null;
//            try {
//                connection = DriverManager.getConnection(DatabaseSetting.URL,
//                        DatabaseSetting.USER, DatabaseSetting.PASSWORD);
//            } catch (SQLException ex) {
//                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            java.sql.Statement stm = null;
//            try {
//                stm = connection.createStatement();
//            } catch (SQLException ex) {
//                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            try {
//                stm.close();
//                //stm.e
//            } catch (SQLException ex) {
//                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
//        }
        //System.out.println(PdfTextExtractor.getTextFromPage(reader, 1));
        //fos.write(PdfTextExtractor.getTextFromPage(reader, page).getBytes("UTF-8"));
        //fos.flush();
        //fos.close();
    }
}
