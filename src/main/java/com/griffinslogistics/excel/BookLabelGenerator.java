/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.griffinslogistics.models.BookLabelModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Georgi
 */
public class BookLabelGenerator {

    private static final String PULSIO_START = "Pulsio Start";
    private static final String ADDRESS = "Adresse";
    private static final String CLIENT = "Editeur";
    private static final String TRANSPORT_NUMBER = "Num Tpt";
    private static final String TITLE = "TITRE";
    private static final String COUNT_PER_BOX = "Nbre ex./carton";
    private static final String COUNT_PER_ADDRESS = "Nbre ex./adresse";
    private static final String EX = "ex.";
    
    private static final String STYLE_SMALL_FONT = "smallFont";
    private static final String STYLE_LABEL = "labelStyle";
    private static final String STYLE_CONTENT = "contentStyle";
    
    public static void generateLabel(OutputStream outputStream, BookLabelModel bookLabelModel) {

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Map<String, CellStyle> styles = createStyles(workbook);

            Sheet sheet = workbook.createSheet(bookLabelModel.getBookNumber() + " " + bookLabelModel.getTitle());

            for (int i = 0; i < 20; i++) {
                Row row = sheet.createRow(i);
                if (i != 0 && i != 10) {
                    row.setHeightInPoints(25);
                } else {
                    row.setHeightInPoints(12);
                }
            }

            //column widths
            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 10000);

            sheet.setColumnWidth(3, 5000);
            sheet.setColumnWidth(4, 10000);

            generateHeaders(sheet, styles);
            generateAddress(sheet, styles, bookLabelModel);
            generateClient(sheet, styles, bookLabelModel);
            generateTransportation(sheet, styles, bookLabelModel);
            generateTitle(sheet, styles, bookLabelModel);
            generateCountPerBox(sheet, styles, bookLabelModel);
            generateCountPerAddress(sheet, styles, bookLabelModel);

            workbook.write(outputStream);
        } catch (IOException ex) {
            Logger.getLogger(BookLabelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Map<String, CellStyle> createStyles(XSSFWorkbook workbook) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle smallStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        smallStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        smallStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        smallStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        smallStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        smallStyle.setFont(font);
        smallStyle.setAlignment(CellStyle.ALIGN_CENTER);
        smallStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styles.put(STYLE_SMALL_FONT, smallStyle);

        CellStyle labelStyle = workbook.createCellStyle();
        Font labelFont = workbook.createFont();
        labelFont.setFontHeightInPoints((short) 13);
        labelStyle.setBorderBottom(CellStyle.BORDER_THIN);
        labelStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        labelStyle.setBorderRight(CellStyle.BORDER_THIN);
        labelStyle.setBorderTop(CellStyle.BORDER_THIN);
        labelStyle.setFont(labelFont);
        labelStyle.setAlignment(CellStyle.ALIGN_CENTER);
        labelStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styles.put(STYLE_LABEL, labelStyle);

        CellStyle contentStyle = workbook.createCellStyle();
        Font contentFont = workbook.createFont();
        contentFont.setFontHeightInPoints((short) 15);
        contentFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
        contentStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
        contentStyle.setFont(contentFont);
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styles.put(STYLE_CONTENT, contentStyle);

        return styles;
    }

    private static void generateHeaders(Sheet sheet, Map<String, CellStyle> styles) {
        Row row0 = sheet.getRow(0);
        Row row11 = sheet.getRow(10);

        Cell a1 = row0.createCell(0);
        a1.setCellStyle(styles.get(STYLE_SMALL_FONT));
        Cell b1 = row0.createCell(1);
        b1.setCellStyle(styles.get(STYLE_SMALL_FONT));
        b1.setCellValue(PULSIO_START);

        Cell d1 = row0.createCell(3);
        d1.setCellStyle(styles.get(STYLE_SMALL_FONT));
        Cell e1 = row0.createCell(4);
        e1.setCellStyle(styles.get(STYLE_SMALL_FONT));
        e1.setCellValue(PULSIO_START);

        Cell a11 = row11.createCell(0);
        a11.setCellStyle(styles.get(STYLE_SMALL_FONT));
        Cell b11 = row11.createCell(1);
        b11.setCellStyle(styles.get(STYLE_SMALL_FONT));
        b11.setCellValue(PULSIO_START);

        Cell d11 = row11.createCell(3);
        d11.setCellStyle(styles.get(STYLE_SMALL_FONT));
        Cell e11 = row11.createCell(4);
        e11.setCellStyle(styles.get(STYLE_SMALL_FONT));
        e11.setCellValue(PULSIO_START);
    }

    private static void generateAddress(Sheet sheet, Map<String, CellStyle> styles, BookLabelModel bookLabelModel) {
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A2:A3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A12:A13"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D2:D3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D12:D13"));

        Row row2 = sheet.getRow(1);
        row2.setHeightInPoints(25);

        Row row3 = sheet.getRow(2);
        row3.setHeightInPoints(25);

        Cell a2 = row2.createCell(0);
        a2.setCellValue(ADDRESS);
        a2.setCellStyle(styles.get(STYLE_LABEL));

        Cell a3 = row3.createCell(0);
        a3.setCellStyle(styles.get(STYLE_LABEL));

        Cell d2 = row2.createCell(3);
        d2.setCellValue(ADDRESS);
        d2.setCellStyle(styles.get(STYLE_LABEL));

        Cell d3 = row3.createCell(3);
        d3.setCellStyle(styles.get(STYLE_LABEL));

        Cell b2 = row2.createCell(1);
        b2.setCellValue(bookLabelModel.getAddress());
        b2.setCellStyle(styles.get(STYLE_CONTENT));

        Cell b3 = row3.createCell(1);
        b3.setCellValue(bookLabelModel.getPostalCode());
        b3.setCellStyle(styles.get(STYLE_CONTENT));

        Cell e2 = row2.createCell(4);
        e2.setCellValue(bookLabelModel.getAddress());
        e2.setCellStyle(styles.get(STYLE_CONTENT));

        Cell e3 = row3.createCell(4);
        e3.setCellValue(bookLabelModel.getPostalCode());
        e3.setCellStyle(styles.get(STYLE_CONTENT));

        Row row12 = sheet.getRow(11);
        row12.setHeightInPoints(25);

        Row row13 = sheet.getRow(12);
        row13.setHeightInPoints(25);

        Cell a12 = row12.createCell(0);
        a12.setCellValue(ADDRESS);
        a12.setCellStyle(styles.get(STYLE_LABEL));

        Cell a13 = row13.createCell(0);
        a13.setCellStyle(styles.get(STYLE_LABEL));

        Cell d12 = row12.createCell(3);
        d12.setCellValue(ADDRESS);
        d12.setCellStyle(styles.get(STYLE_LABEL));

        Cell d13 = row13.createCell(3);
        d13.setCellStyle(styles.get(STYLE_LABEL));

        Cell b12 = row12.createCell(1);
        b12.setCellValue(bookLabelModel.getAddress());
        b12.setCellStyle(styles.get(STYLE_CONTENT));

        Cell b13 = row13.createCell(1);
        b13.setCellValue(bookLabelModel.getPostalCode());
        b13.setCellStyle(styles.get(STYLE_CONTENT));

        Cell e12 = row12.createCell(4);
        e12.setCellValue(bookLabelModel.getAddress());
        e12.setCellStyle(styles.get(STYLE_CONTENT));

        Cell e13 = row13.createCell(4);
        e13.setCellValue(bookLabelModel.getPostalCode());
        e13.setCellStyle(styles.get(STYLE_CONTENT));
    }

    private static void generateClient(Sheet sheet, Map<String, CellStyle> styles, BookLabelModel bookLabelModel) {
        Row row4 = sheet.getRow(3);

        Cell a4 = row4.createCell(0);
        a4.setCellStyle(styles.get(STYLE_LABEL));
        a4.setCellValue(CLIENT);

        Cell b4 = row4.createCell(1);
        b4.setCellStyle(styles.get(STYLE_CONTENT));
        b4.setCellValue(bookLabelModel.getClient());

        Cell d4 = row4.createCell(3);
        d4.setCellStyle(styles.get(STYLE_LABEL));
        d4.setCellValue(CLIENT);

        Cell e4 = row4.createCell(4);
        e4.setCellStyle(styles.get(STYLE_CONTENT));
        e4.setCellValue(bookLabelModel.getClient());

        Row row14 = sheet.getRow(13);

        Cell a14 = row14.createCell(0);
        a14.setCellStyle(styles.get(STYLE_LABEL));
        a14.setCellValue(CLIENT);

        Cell b14 = row14.createCell(1);
        b14.setCellStyle(styles.get(STYLE_CONTENT));
        b14.setCellValue(bookLabelModel.getClient());

        Cell d14 = row14.createCell(3);
        d14.setCellStyle(styles.get(STYLE_LABEL));
        d14.setCellValue(CLIENT);

        Cell e14 = row14.createCell(4);
        e14.setCellStyle(styles.get(STYLE_CONTENT));
        e14.setCellValue(bookLabelModel.getClient());
    }

    private static void generateTransportation(Sheet sheet, Map<String, CellStyle> styles, BookLabelModel bookLabelModel) {
        Row row5 = sheet.getRow(4);

        Cell a5 = row5.createCell(0);
        a5.setCellValue(TRANSPORT_NUMBER);
        a5.setCellStyle(styles.get(STYLE_LABEL));

        Cell b5 = row5.createCell(1);
        b5.setCellValue(bookLabelModel.getTransportNumber());
        b5.setCellStyle(styles.get(STYLE_CONTENT));

        Cell d5 = row5.createCell(3);
        d5.setCellValue(TRANSPORT_NUMBER);
        d5.setCellStyle(styles.get(STYLE_LABEL));

        Cell e5 = row5.createCell(4);
        e5.setCellValue(bookLabelModel.getTransportNumber());
        e5.setCellStyle(styles.get(STYLE_CONTENT));

        Row row15 = sheet.getRow(14);

        Cell a15 = row15.createCell(0);
        a15.setCellValue(TRANSPORT_NUMBER);
        a15.setCellStyle(styles.get(STYLE_LABEL));

        Cell b15 = row15.createCell(1);
        b15.setCellValue(bookLabelModel.getTransportNumber());
        b15.setCellStyle(styles.get(STYLE_CONTENT));

        Cell d15 = row15.createCell(3);
        d15.setCellValue(TRANSPORT_NUMBER);
        d15.setCellStyle(styles.get(STYLE_LABEL));

        Cell e15 = row15.createCell(4);
        e15.setCellValue(bookLabelModel.getTransportNumber());
        e15.setCellStyle(styles.get(STYLE_CONTENT));
    }

    private static void generateTitle(Sheet sheet, Map<String, CellStyle> styles, BookLabelModel bookLabelModel) {
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A6:A7"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D6:D7"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A16:A17"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D16:D17"));

        Row row6 = sheet.getRow(5);
        Row row7 = sheet.getRow(6);

        Cell a6 = row6.createCell(0);
        a6.setCellStyle(styles.get(STYLE_LABEL));
        a6.setCellValue(TITLE);

        Cell a7 = row7.createCell(0);
        a7.setCellStyle(styles.get(STYLE_LABEL));

        Cell b6 = row6.createCell(1);
        b6.setCellStyle(styles.get(STYLE_CONTENT));
        b6.setCellValue(bookLabelModel.getTitle());

        Cell b7 = row7.createCell(1);
        b7.setCellStyle(styles.get(STYLE_CONTENT));
        b7.setCellValue(bookLabelModel.getBookNumber());

        Cell d6 = row6.createCell(3);
        d6.setCellStyle(styles.get(STYLE_LABEL));
        d6.setCellValue(TITLE);

        Cell d7 = row7.createCell(3);
        d7.setCellStyle(styles.get(STYLE_LABEL));

        Cell e6 = row6.createCell(4);
        e6.setCellStyle(styles.get(STYLE_CONTENT));
        e6.setCellValue(bookLabelModel.getTitle());

        Cell e7 = row7.createCell(4);
        e7.setCellStyle(styles.get(STYLE_CONTENT));
        e7.setCellValue(bookLabelModel.getBookNumber());

        Row row16 = sheet.getRow(15);
        Row row17 = sheet.getRow(16);

        Cell a16 = row16.createCell(0);
        a16.setCellStyle(styles.get(STYLE_LABEL));
        a16.setCellValue(TITLE);

        Cell a17 = row17.createCell(0);
        a17.setCellStyle(styles.get(STYLE_LABEL));

        Cell b16 = row16.createCell(1);
        b16.setCellStyle(styles.get(STYLE_CONTENT));
        b16.setCellValue(bookLabelModel.getTitle());

        Cell b17 = row17.createCell(1);
        b17.setCellStyle(styles.get(STYLE_CONTENT));
        b17.setCellValue(bookLabelModel.getBookNumber());
        
        Cell d16 = row16.createCell(3);
        d16.setCellStyle(styles.get(STYLE_LABEL));
        d16.setCellValue(TITLE);

        Cell d17 = row17.createCell(3);
        d17.setCellStyle(styles.get(STYLE_LABEL));

        Cell e16 = row16.createCell(4);
        e16.setCellStyle(styles.get(STYLE_CONTENT));
        e16.setCellValue(bookLabelModel.getTitle());

        Cell e17 = row17.createCell(4);
        e17.setCellStyle(styles.get(STYLE_CONTENT));
        e17.setCellValue(bookLabelModel.getBookNumber());
    }

    private static void generateCountPerBox(Sheet sheet, Map<String, CellStyle> styles, BookLabelModel bookLabelModel) {
        Row row8 = sheet.getRow(7);
        
        Cell a8 = row8.createCell(0);
        a8.setCellStyle(styles.get(STYLE_LABEL));
        a8.setCellValue(COUNT_PER_BOX);
        
        Cell b8 = row8.createCell(1);
        b8.setCellStyle(styles.get(STYLE_CONTENT));
        b8.setCellValue(EX);
        
        Cell d8 = row8.createCell(3);
        d8.setCellStyle(styles.get(STYLE_LABEL));
        d8.setCellValue(COUNT_PER_BOX);
        
        Cell e8 = row8.createCell(4);
        e8.setCellStyle(styles.get(STYLE_CONTENT));
        e8.setCellValue(EX);
        
        Row row18 = sheet.getRow(17);
        
        Cell a18 = row18.createCell(0);
        a18.setCellStyle(styles.get(STYLE_LABEL));
        a18.setCellValue(COUNT_PER_BOX);
        
        Cell b18 = row18.createCell(1);
        b18.setCellStyle(styles.get(STYLE_CONTENT));
        b18.setCellValue(EX);
        
        Cell d18 = row18.createCell(3);
        d18.setCellStyle(styles.get(STYLE_LABEL));
        d18.setCellValue(COUNT_PER_BOX);
        
        Cell e18 = row18.createCell(4);
        e18.setCellStyle(styles.get(STYLE_CONTENT));
        e18.setCellValue(EX);
    }

    private static void generateCountPerAddress(Sheet sheet, Map<String, CellStyle> styles, BookLabelModel bookLabelModel) {
        Row row9 = sheet.getRow(8);
        
        Cell a9 = row9.createCell(0);
        a9.setCellStyle(styles.get(STYLE_LABEL));
        a9.setCellValue(COUNT_PER_ADDRESS);
        
        Cell b9 = row9.createCell(1);
        b9.setCellStyle(styles.get(STYLE_CONTENT));
        b9.setCellValue(bookLabelModel.getCount().toString() + " " + EX);
        
        Cell d9 = row9.createCell(3);
        d9.setCellStyle(styles.get(STYLE_LABEL));
        d9.setCellValue(COUNT_PER_ADDRESS);
        
        Cell e9 = row9.createCell(4);
        e9.setCellStyle(styles.get(STYLE_CONTENT));
        e9.setCellValue(bookLabelModel.getCount().toString() + " " + EX);
        
        Row row19 = sheet.getRow(18);
        
        Cell a19 = row19.createCell(0);
        a19.setCellStyle(styles.get(STYLE_LABEL));
        a19.setCellValue(COUNT_PER_ADDRESS);
        
        Cell b19 = row19.createCell(1);
        b19.setCellStyle(styles.get(STYLE_CONTENT));
        b19.setCellValue(bookLabelModel.getCount().toString() + " " + EX);
        
        Cell d19 = row19.createCell(3);
        d19.setCellStyle(styles.get(STYLE_LABEL));
        d19.setCellValue(COUNT_PER_ADDRESS);
        
        Cell e19 = row19.createCell(4);
        e19.setCellStyle(styles.get(STYLE_CONTENT));
        e19.setCellValue(bookLabelModel.getCount().toString() + " " + EX);
    }
}
