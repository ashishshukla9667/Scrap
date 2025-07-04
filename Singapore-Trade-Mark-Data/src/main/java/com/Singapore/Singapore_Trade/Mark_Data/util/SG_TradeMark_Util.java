package com.Singapore.Singapore_Trade.Mark_Data.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SG_TradeMark_Util {

    public static List<String> getApplicationNumbersForSG(String fileName) {
        List<String> regNumbers = new ArrayList<>();

        try (InputStream inputStream = new ClassPathResource(fileName).getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // assuming data is in the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            int regNumCol = -1;
            int countryCodeCol = -1;

            // Read header row to determine column indexes
            if (rowIterator.hasNext()) {
                Row headerRow = rowIterator.next();
                for (Cell cell : headerRow) {
                    String colName = cell.getStringCellValue().trim();
                    if (colName.equalsIgnoreCase("REGISTRATION NUMBER")) {
                        regNumCol = cell.getColumnIndex();
                    } else if (colName.equalsIgnoreCase("COUNTRY CODE")) {
                        countryCodeCol = cell.getColumnIndex();
                    }
                }
            }

            if (regNumCol == -1 || countryCodeCol == -1) {
                throw new RuntimeException("Required columns 'REGISTRATION NUMBER' or 'COUNTRY CODE' not found in Excel file.");
            }

            // Read data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Cell countryCell = row.getCell(countryCodeCol);
                Cell regNumCell = row.getCell(regNumCol);

                if (countryCell != null && "SG".equalsIgnoreCase(countryCell.getStringCellValue().trim())) {

                    String regNumber = getCellAsString(regNumCell);
                    if (regNumber != null && !regNumber.isEmpty()) {
                        regNumbers.add(regNumber);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error Occurred : " + e.getMessage());
        }

        return regNumbers;
    }

    private static String getCellAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // remove .0 from integers represented as double
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.valueOf((long) value);
                } else {
                    return String.valueOf(value);
                }
            case FORMULA:
                return cell.getCellFormula();
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            case BLANK:
            default:
                return null;
        }
    }
}

