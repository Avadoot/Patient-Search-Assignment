package com.tests.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class BaseExcel {
    public static String[][] readExcel(String filepath, int no) {

        String[][] excel = new String[16][5];
        try {

            FileInputStream excelFile = new FileInputStream(new File(filepath));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(no);
            Iterator<Row> iterator = datatypeSheet.iterator();

            int i = 0;
            int j;

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                j = 0;
                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();

                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        excel[i][j] = currentCell.getStringCellValue();
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        excel[i][j] = String.valueOf((int) currentCell.getNumericCellValue());
                    }

                    j++;
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return excel;
    }

    public void writeExcel(String filepath, int no, int colNo, int rowNo, String value) {

        FileInputStream excelFile;
        Workbook workbook;
        try {
            excelFile = new FileInputStream(new File(filepath));
            workbook = new XSSFWorkbook(excelFile);

            Sheet sheet = workbook.getSheetAt(no);
            Cell cell = sheet.getRow(rowNo).getCell(colNo);
            cell.setCellValue(value);

            excelFile.close();

            FileOutputStream outputStream = new FileOutputStream(filepath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

