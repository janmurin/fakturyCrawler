package com.mycompany.adresaconverter;

import java.io.File;

public class Spustac {

	public static void main(String[] args) {
		AdresaConverter adresaConverter = new AdresaConverter();
		adresaConverter.convertExcelToJSON(new File("/home/jakub/Sources/fakturyCrawler/src/main/resources/ULICE.xlsx"));
	}
}
