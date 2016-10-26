package faktury.parser;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Bruno Lowagie (iText Software)
 */
public class ParseCzechJson {

//    public static final String SRC = "sources/skuska.pdf";
//    public static final String DEST = "results/";
//
//    public static void main(String[] args) throws IOException, DocumentException {
//        File file = new File(DEST);
//        file.getParentFile().mkdirs();
//        new ParseCzech().parse(SRC);
//    }
    public void parse() throws IOException {
        int faktura_id = 0;
        int polozka_id = 0;
        int dodavatel_id = 0;
        int succeeddItems = 0;
        int errorsItems = 0;
        JSONObject json = new JSONObject();
        JSONObject errors = new JSONObject();
        File folder = new File(System.getProperty("user.dir") + "\\invoices");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            System.out.println("Proccessing file " + file.getName());
            Map<String, String> faktura = new HashMap<String, String>();
            Map<String, String> dodavatel = new HashMap<String, String>();
            Map<String, String> polozka = new HashMap<String, String>();
            Map<String, String> mapJson = new HashMap<String, String>();
            PdfReader reader = new PdfReader("invoices/" + file.getName());
            faktura_id++;
            polozka_id++;
            dodavatel_id++;
            //FileOutputStream fos = new FileOutputStream(DEST);
            String text = PdfTextExtractor.getTextFromPage(reader, 1);
            String lines[] = text.split("\\r?\\n", -1);
            if (lines[1].startsWith("Univerzita Pavla Jozefa Šafárika")) {
                continue;
            }
            try {
                if (lines[9].contains("DIČ")) {
                    dodavatel.put("Nazov", lines[1]);
                    if (!lines[1].equals("Drobný nákup") && lines[6].length() > 13) {
                        dodavatel.put("Ulica", lines[2].replace(" Univerzita Pavla Jozefa Šafárika", "").replace(" Univerzita Pavla Jozefa", ""));
                        dodavatel.put("Ico", lines[6].substring(6, 14));
                        if (lines[6].length() > 25) {
                            dodavatel.put("Dic", lines[6].substring(21, lines[6].length()));
                            dodavatel.put("Ic_dph", "SK" + lines[6].substring(21, lines[6].length()));
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

                mapJson.putAll(faktura);
                mapJson.putAll(dodavatel);
                mapJson.putAll(polozka);
                json.put(file.getName(), mapJson);
                succeeddItems++;

            } catch (ParseException | StringIndexOutOfBoundsException ex) {
                errorsItems++;
                errors.put(file.getName(), "error");
                Logger.getLogger(ParseCzechJson.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.err.println("Parsing finished. Please wait...");

        try (FileWriter file = new FileWriter("json.json")) {
            file.write(json.toJSONString());
            System.out.println("Successfully Copied JSON Object to File json.json");
            //System.out.println("\nJSON Object: " + json);
        }

        try (FileWriter file = new FileWriter("errors.json")) {
            file.write(errors.toJSONString());
            System.out.println("Successfully Copied JSON Object to File errors.json");
            //System.out.println("\nJSON Object: " + errors);
        }

        System.err.println("Succeed items: " + succeeddItems);
        System.err.println("Errors items: " + errorsItems);

        //System.out.println(PdfTextExtractor.getTextFromPage(reader, 1));
        //fos.write(PdfTextExtractor.getTextFromPage(reader, page).getBytes("UTF-8"));
        //fos.flush();
        //fos.close();
    }

}
