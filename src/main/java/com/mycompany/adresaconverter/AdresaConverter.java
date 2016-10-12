package com.mycompany.adresaconverter;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.WorkbookFactory;


public class AdresaConverter {



	public void convertExcelToJSON(File file) {

		List<Adresa> valueList = new ArrayList<Adresa>();

		System.out.println("uploadExcel method");
		Workbook workbook = null;
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			workbook = WorkbookFactory.create(fileInputStream);
			System.out.println("workbook: " + workbook);
			Sheet sheet = workbook.getSheetAt(0);
			System.out.println("worksheet: " + sheet);
			Row row;

			Iterator<Row> iterator = sheet.iterator();

			while (iterator.hasNext()) {
				Adresa adresaValue = new Adresa();
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				Cell cell = cellIterator.next();
				Iterator cells = nextRow.cellIterator();
				cell = (Cell) cells.next();
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				}
				//tu setujeme hodnoty
				adresaValue.setdUlica(nextRow.getCell(0).getStringCellValue());
				adresaValue.setUlica(nextRow.getCell(1).getStringCellValue());
				adresaValue.setPsc(nextRow.getCell(2).getStringCellValue());
				adresaValue.setdPosta(nextRow.getCell(3).getStringCellValue());
				adresaValue.setPosta(nextRow.getCell(4).getStringCellValue());
				adresaValue.setObce(nextRow.getCell(6).getStringCellValue());

				valueList.add(adresaValue);

				// JSON CONVERTER
				ObjectMapper mapper = new ObjectMapper();

				System.out.println("list: " + adresaValue);

				// Convert object to JSON string
				String jsonInString = mapper.writeValueAsString(adresaValue);
				System.out.println("JsonInString " + jsonInString);

				// Convert object to JSON string and pretty print
				jsonInString = mapper.writerWithDefaultPrettyPrinter()
						.writeValueAsString(adresaValue);

				mapper.writeValue(new File("/home/jakub/Sources/fakturyCrawler/src/main/resources/adresy.json"), valueList);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fileInputStream);
		}
	}

}
