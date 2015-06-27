/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.excel;

import com.griffinslogistics.db.entities.Pulsiodetails;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.griffinslogistics.models.BookBoxModel;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Georgi
 */
public class BDLGenerator {

    public static final Logger logger = Logger.getLogger(BDLGenerator.class.getSimpleName());

    public static void generateAll(OutputStream outputStream, Map<String, List<BookBoxModel>> bookBoxModelsForTransportation, Pulsiodetails pulsioDetails, String packageNumber) {
        try {
            Workbook workbook = new XSSFWorkbook();

            for (String bookspackageNumber : bookBoxModelsForTransportation.keySet()) {
                List<BookBoxModel> bookBoxModels = bookBoxModelsForTransportation.get(bookspackageNumber);

                generate(workbook, bookBoxModels, pulsioDetails, bookspackageNumber);

//                Sheet sheet = workbook.createSheet(bookspackageNumber);
//
//                //Default sheet styles
//                sheet.setDisplayGridlines(false);
//                sheet.setPrintGridlines(false);
//                sheet.getPrintSetup().setLandscape(true);
//                sheet.setFitToPage(true);
//                sheet.setHorizontallyCenter(true);
//                sheet.setDefaultRowHeight((short) 300);
//                sheet.setDefaultColumnWidth(15);
//
//                sheet.setColumnWidth(1, 6000);
//                sheet.setColumnWidth(5, 3000);
//                sheet.setColumnWidth(6, 3000);
//                sheet.setColumnWidth(7, 3000);
//                sheet.setColumnWidth(8, 3000);
//
//                // Generate header part
//                insertPulsioImage(workbook, sheet, pulsioDetails);
//                insertDate(sheet, styles.get("title"));
//                insertContacts(sheet, styles.get("pulsioName"), styles.get("contacts"), pulsioDetails);
//                insertTitle(sheet, styles.get("title"));
//
//                // Generate table part
//                insertTableHeaders(sheet, styles.get("tableHeadersLeft"), styles.get("tableHeadersMiddle"), styles.get("tableHeadersRight"));
//                int index = insertTableBody(sheet, styles.get("tableBodyLeft"), styles.get("tableBodyMiddle"), styles.get("tableBodyRight"), styles.get("tableFooters"), bookBoxModels);
//
//                String deliveryAddress = bookBoxModels.get(0).getDeliveryAddress();
//                String client = bookBoxModels.get(0).getClient();
//                insertFooter(sheet, styles.get("footer"), index, bookspackageNumber, deliveryAddress, client);
            }

            workbook.write(outputStream);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private static void generate(Workbook workbook, List<BookBoxModel> bookBoxModels, Pulsiodetails pulsiodetails, String packageNumber) {

        Map<String, CellStyle> styles = createStyles(workbook);

        Sheet sheet = workbook.createSheet(packageNumber);

        // Generate header part
        insertPulsioImage(workbook, sheet, pulsiodetails);
        insertDate(sheet, styles.get("title"));
        insertContacts(sheet, styles.get("pulsioName"), styles.get("contacts"), pulsiodetails);
        insertTitle(sheet, styles.get("title"));
        // Generate table part
        insertTableHeaders(sheet, styles.get("tableHeadersLeft"), styles.get("tableHeadersMiddle"), styles.get("tableHeadersRight"));
        int index = insertTableBody(sheet, styles.get("tableBodyLeft"), styles.get("tableBodyMiddle"), styles.get("tableBodyRight"), styles.get("tableFooters"), bookBoxModels);

        String deliveryAddress = bookBoxModels.get(0).getDeliveryAddress();
        String client = bookBoxModels.get(0).getClient();

        insertFooter(sheet, styles.get("footer"), index, packageNumber, deliveryAddress, client);

//        Default sheet styles
        sheet.setDisplayGridlines(false);
        sheet.setPrintGridlines(false);

        sheet.autoSizeColumn(1, true);
        sheet.autoSizeColumn(2, false);
        sheet.autoSizeColumn(5, false);
        sheet.autoSizeColumn(6, false);
        sheet.autoSizeColumn(7, false);
        sheet.autoSizeColumn(8, false);

        sheet.setFitToPage(true);
    }

    public static void generateSingle(OutputStream outputStream, List<BookBoxModel> bookBoxModels, Pulsiodetails pulsiodetails, String packageNumber) {

        try {
            Workbook workbook = new XSSFWorkbook();
            generate(workbook, bookBoxModels, pulsiodetails, packageNumber);
            workbook.write(outputStream);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private static void insertPulsioImage(Workbook workbook, Sheet sheet, Pulsiodetails pulsiodetails) {

        Row imageRow = sheet.createRow(0);
        imageRow.setHeightInPoints(55);

        byte[] imageBytes = pulsiodetails.getLogo();
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        //set top-left corner for the image
        anchor.setCol1(7);
        anchor.setRow1(0);

        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize();
    }

    private static Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style;
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 26);
        titleFont.setFontName("Calibri");
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = workbook.createCellStyle();
        style.setFont(titleFont);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("title", style);

        titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setFontName("Calibri");
        style = workbook.createCellStyle();
        style.setFont(titleFont);
        style.setAlignment(CellStyle.ALIGN_JUSTIFY);

        styles.put("pulsioName", style);

        titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setFontName("Calibri");
        style = workbook.createCellStyle();
        style.setFont(titleFont);
        style.setAlignment(CellStyle.ALIGN_JUSTIFY);

        styles.put("contacts", style);

        CellStyle footerStyle = workbook.createCellStyle();
        Font footerFont = workbook.createFont();
        footerFont.setFontHeightInPoints((short) 14);
        footerFont.setFontName("Calibri");
        footerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        footerStyle.setFont(footerFont);
        footerStyle.setAlignment(CellStyle.ALIGN_JUSTIFY);

        styles.put("footer", footerStyle);

        titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setFontName("Calibri");
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont.setItalic(true);
        style = workbook.createCellStyle();
        style.setFont(titleFont);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);

        // Override 25% grey to lighter grey
        XSSFWorkbook hssfWorkbook = (XSSFWorkbook) workbook;
//        SSFPalette palette = hssfWorkbook.getCustomPalette();
//        palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index,
//                (byte) 242, //RGB red (0-255)
//                (byte) 242, //RGB green
//                (byte) 242 //RGB blue
//        );

        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setBorderLeft(CellStyle.BORDER_MEDIUM);
        style.setBorderTop(CellStyle.BORDER_MEDIUM);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);

        styles.put("tableHeadersLeft", style);

        CellStyle headerRowMiddleCellStyle = workbook.createCellStyle();
        headerRowMiddleCellStyle.cloneStyleFrom(style);
        headerRowMiddleCellStyle.setBorderLeft(CellStyle.BORDER_THIN);

        styles.put("tableHeadersMiddle", headerRowMiddleCellStyle);

        CellStyle headerRowRightCellStyle = workbook.createCellStyle();
        headerRowRightCellStyle.cloneStyleFrom(style);
        headerRowRightCellStyle.setBorderRight(CellStyle.BORDER_MEDIUM);

        styles.put("tableHeadersRight", headerRowRightCellStyle);

        CellStyle footerRowRightCellStyle = workbook.createCellStyle();
        footerRowRightCellStyle.cloneStyleFrom(style);
        footerRowRightCellStyle.setFillPattern(CellStyle.NO_FILL);
        footerRowRightCellStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        footerRowRightCellStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);

        styles.put("tableFooters", footerRowRightCellStyle);

        CellStyle bodyRowLeftCellStyle = workbook.createCellStyle();
        bodyRowLeftCellStyle.cloneStyleFrom(style);
        Font titleBodyFont = workbook.createFont();
        titleBodyFont.setFontHeightInPoints((short) 14);
        titleBodyFont.setFontName("Calibri");
        bodyRowLeftCellStyle.setFont(titleBodyFont);
        bodyRowLeftCellStyle.setBorderTop(CellStyle.BORDER_THIN);
        bodyRowLeftCellStyle.setFillPattern(CellStyle.NO_FILL);

        styles.put("tableBodyLeft", bodyRowLeftCellStyle);

        CellStyle bodyRowMiddleCellStyle = workbook.createCellStyle();
        bodyRowMiddleCellStyle.cloneStyleFrom(bodyRowLeftCellStyle);
        bodyRowMiddleCellStyle.setBorderLeft(CellStyle.BORDER_THIN);

        styles.put("tableBodyMiddle", bodyRowMiddleCellStyle);

        CellStyle bodyRowRightCellStyle = workbook.createCellStyle();
        bodyRowRightCellStyle.cloneStyleFrom(bodyRowMiddleCellStyle);
        bodyRowRightCellStyle.setBorderRight(CellStyle.BORDER_MEDIUM);

        styles.put("tableBodyRight", bodyRowRightCellStyle);

        return styles;
    }

    private static void insertDate(Sheet sheet, CellStyle style) {
        Row dateRow = sheet.createRow(6);

        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$7:$C$7"));

        Locale frenchLocale = new Locale("fr", "FR");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", frenchLocale);

        String dateString = dateFormat.format(new Date());
        Cell dateCell = dateRow.createCell(1);
        dateCell.setCellStyle(style);
        dateCell.setCellValue(dateString);
    }

    private static void insertContacts(Sheet sheet, CellStyle pulsioNameStyle, CellStyle contactsStyle, Pulsiodetails pulsiodetails) {
        Row pulsioNameRow = sheet.createRow(9);
        Cell pulsioNameCell = pulsioNameRow.createCell(1);
        pulsioNameCell.setCellValue("PULSIO");
        pulsioNameCell.setCellStyle(pulsioNameStyle);

        Row firstContactsRow = sheet.createRow(10);
        Cell firstContactsCell = firstContactsRow.createCell(1);
        firstContactsCell.setCellValue("Contact: " + pulsiodetails.getContact1());
        firstContactsCell.setCellStyle(contactsStyle);

        Row secondContactsRow = sheet.createRow(11);
        Cell secondContactsCell = secondContactsRow.createCell(1);
        secondContactsCell.setCellValue(pulsiodetails.getContact2());
        secondContactsCell.setCellStyle(contactsStyle);

        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$11:$C$11"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$12:$C$12"));
    }

    private static void insertTitle(Sheet sheet, CellStyle style) {
        Row titleRow = sheet.createRow(14);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Bon de livraison");
        titleCell.setCellStyle(style);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$15:$J$15"));
    }

    private static void insertTableHeaders(Sheet sheet,
            CellStyle headerRowLeftCellStyleStyle,
            CellStyle headerRowMiddleCellStyle,
            CellStyle headerRowRightCellStyle) {
        Row tableHeadersRow = sheet.createRow(18);
        tableHeadersRow.setHeightInPoints((short) 35);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$19:$E$19"));

        Cell titleCell = tableHeadersRow.createCell(1);
        titleCell.setCellValue("Titre");
        titleCell.setCellStyle(headerRowLeftCellStyleStyle);

        for (int i = 2; i <= 4; i++) {
            tableHeadersRow.createCell(i).setCellStyle(headerRowMiddleCellStyle);
        }

        Cell quantityCell = tableHeadersRow.createCell(5);
        quantityCell.setCellValue("Qunatite/carton");
        quantityCell.setCellStyle(headerRowMiddleCellStyle);

        Cell boxesCountCell = tableHeadersRow.createCell(6);
        boxesCountCell.setCellValue("Nbre cartons");
        boxesCountCell.setCellStyle(headerRowMiddleCellStyle);

        Cell totalQuantityCell = tableHeadersRow.createCell(7);
        totalQuantityCell.setCellValue("Quantite totale");
        totalQuantityCell.setCellStyle(headerRowMiddleCellStyle);

        Cell palettesCountCell = tableHeadersRow.createCell(8);
        palettesCountCell.setCellValue("Nbre Palettes");
        palettesCountCell.setCellStyle(headerRowRightCellStyle);

    }

    /**
     *
     * @param sheet
     * @param leftStyle
     * @param middleStyle
     * @param rightStyle
     * @param footerStyle
     * @return index of the last table row created
     */
    private static int insertTableBody(Sheet sheet, CellStyle leftStyle,
            CellStyle middleStyle, CellStyle rightStyle, CellStyle footerStyle, List<BookBoxModel> bookBoxModels) {
        Integer index = 18;
        Integer currentBookTitleIndex = 1;
        Set<Integer> rowsToSum = new HashSet<Integer>();
        Set<Integer> totalsToSum = new HashSet<Integer>();
        String cellMergeString;
        String cellFormula;

        try {
            int currentBookNumber = bookBoxModels.get(0).getBookNumber();

            for (int i = 0; i < bookBoxModels.size(); i++) {
                index++;

                BookBoxModel currentModel = bookBoxModels.get(i);

                Row row = sheet.createRow(index);

                if (currentBookNumber != currentModel.getBookNumber()) {
                    for (int j = 2; j <= 4; j++) {
                        row.createCell(j).setCellStyle(footerStyle);
                    }

                    addTotalTitleRow(row, index, sheet, footerStyle, currentBookTitleIndex, rowsToSum, totalsToSum);
                    index++;
                    currentBookTitleIndex++;

                    row = sheet.createRow(index);
                }

                for (int j = 2; j <= 4; j++) {
                    row.createCell(j).setCellStyle(middleStyle);
                }

                //Book Title Row
                Cell titleCell = row.createCell(1);

                cellMergeString = String.format("$B$%s:$E$%s",
                        index + 1, index + 1);

                sheet.addMergedRegion(CellRangeAddress.valueOf(cellMergeString));
                titleCell.setCellStyle(leftStyle);
                titleCell.setCellValue(currentModel.getTitle());

                Cell quantityCell = row.createCell(5);
                quantityCell.setCellStyle(middleStyle);
                quantityCell.setCellValue(Double.parseDouble(currentModel.getBooksCount().toString()));

                Cell boxCountCell = row.createCell(6);
                boxCountCell.setCellStyle(middleStyle);
                boxCountCell.setCellValue(Double.parseDouble(currentModel.getBoxesCount().toString()));

                Cell totalQuantityCell = row.createCell(7);
                totalQuantityCell.setCellStyle(rightStyle);
                cellFormula = String.format("F%s*G%s", index + 1, index + 1);
                totalQuantityCell.setCellFormula(cellFormula);

                Cell palettesCountCell = row.createCell(8);
                palettesCountCell.setCellStyle(rightStyle);

                // excel is not 0-based!
                rowsToSum.add(index + 1);

                currentBookNumber = currentModel.getBookNumber();
            }

            index++;
            Row row = sheet.createRow(index);

            for (int i = 2; i <= 4; i++) {
                row.createCell(i).setCellStyle(footerStyle);
            }

            addTotalTitleRow(row, index, sheet, footerStyle, currentBookTitleIndex, rowsToSum, totalsToSum);

            //Total row
            index++;
            Row footerTotalRow = sheet.createRow(index);
            Cell footerTotalRowTitleCell = footerTotalRow.createCell(1);

            for (int i = 2; i <= 4; i++) {
                footerTotalRow.createCell(i).setCellStyle(footerStyle);
            }

            cellMergeString = String.format("$B$%s:$E$%s",
                    index + 1, index + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(cellMergeString));

            footerTotalRowTitleCell.setCellStyle(footerStyle);
            footerTotalRowTitleCell.setCellValue("Total");

            Cell footerTotalRowQuantityCell = footerTotalRow.createCell(5);
            footerTotalRowQuantityCell.setCellStyle(footerStyle);

            Cell footerTotalRowBoxCountCell = footerTotalRow.createCell(6);
            footerTotalRowBoxCountCell.setCellStyle(footerStyle);

            Cell footerTotalRowTotalQuantityCell = footerTotalRow.createCell(7);
            footerTotalRowTotalQuantityCell.setCellStyle(footerStyle);

            Cell footerTotalRowPalettesCountCell = footerTotalRow.createCell(8);
            footerTotalRowPalettesCountCell.setCellStyle(footerStyle);

            //build cell formulas
            StringBuilder totalBoxesCountformulaBuilder = new StringBuilder();
            StringBuilder totalBooksCountformulaBuilder = new StringBuilder();
            StringBuilder totalPaletsCountformulaBuilder = new StringBuilder();
            // Example: SUM(H22;H25;H28;H31;H34)
            totalBoxesCountformulaBuilder.append("SUM(");
            totalBooksCountformulaBuilder.append("SUM(");
            totalPaletsCountformulaBuilder.append("SUM(");

            for (Integer integer : totalsToSum) {
                totalBoxesCountformulaBuilder.append("G").append(integer).append(",");
                totalBooksCountformulaBuilder.append("H").append(integer).append(",");
                totalPaletsCountformulaBuilder.append("I").append(integer).append(",");
            }

            totalBoxesCountformulaBuilder.deleteCharAt(totalBoxesCountformulaBuilder.length() - 1);
            totalBooksCountformulaBuilder.deleteCharAt(totalBooksCountformulaBuilder.length() - 1);
            totalPaletsCountformulaBuilder.deleteCharAt(totalPaletsCountformulaBuilder.length() - 1);

            totalBoxesCountformulaBuilder.append(")");
            totalBooksCountformulaBuilder.append(")");
            totalPaletsCountformulaBuilder.append(")");

            footerTotalRowBoxCountCell.setCellFormula(totalBoxesCountformulaBuilder.toString());
            footerTotalRowTotalQuantityCell.setCellFormula(totalBooksCountformulaBuilder.toString());
            footerTotalRowPalettesCountCell.setCellFormula(totalPaletsCountformulaBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    private static void addTotalTitleRow(Row row, Integer index, Sheet sheet, CellStyle footerStyle, Integer currentBookTitleIndex, Set<Integer> rowsToSum, Set<Integer> totalsToSum) {

        //Total Book Title Row
        Cell totalRowTitleCell = row.createCell(1);

        String cellMergeString = String.format("$B$%s:$E$%s",
                index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(cellMergeString));

        totalRowTitleCell.setCellStyle(footerStyle);
        totalRowTitleCell.setCellValue("Total Title " + currentBookTitleIndex++);

        Cell totalRowQuantityCell = row.createCell(5);
        totalRowQuantityCell.setCellStyle(footerStyle);

        Cell totalRowBoxCountCell = row.createCell(6);
        totalRowBoxCountCell.setCellStyle(footerStyle);

        Cell totalRowTotalQuantityCell = row.createCell(7);
        totalRowTotalQuantityCell.setCellStyle(footerStyle);

        Cell totalRowPalettesCountCell = row.createCell(8);
        totalRowPalettesCountCell.setCellStyle(footerStyle);

        //build cell formulas
        StringBuilder totalBoxesCountformulaBuilder = new StringBuilder();
        StringBuilder totalBooksCountformulaBuilder = new StringBuilder();
        StringBuilder totalPaletsCountformulaBuilder = new StringBuilder();
        // Example: SUM(H22;H25;H28;H31;H34)
        totalBoxesCountformulaBuilder.append("SUM(");
        totalBooksCountformulaBuilder.append("SUM(");
        totalPaletsCountformulaBuilder.append("SUM(");

        for (Integer integer : rowsToSum) {
            totalBoxesCountformulaBuilder.append("G").append(integer).append(",");
            totalBooksCountformulaBuilder.append("H").append(integer).append(",");
            totalPaletsCountformulaBuilder.append("I").append(integer).append(",");
        }

        totalBoxesCountformulaBuilder.deleteCharAt(totalBoxesCountformulaBuilder.length() - 1);
        totalBooksCountformulaBuilder.deleteCharAt(totalBooksCountformulaBuilder.length() - 1);
        totalPaletsCountformulaBuilder.deleteCharAt(totalPaletsCountformulaBuilder.length() - 1);

        totalBoxesCountformulaBuilder.append(")");
        totalBooksCountformulaBuilder.append(")");
        totalPaletsCountformulaBuilder.append(")");

        totalRowBoxCountCell.setCellFormula(totalBoxesCountformulaBuilder.toString());
        totalRowTotalQuantityCell.setCellFormula(totalBooksCountformulaBuilder.toString());
        totalRowPalettesCountCell.setCellFormula(totalPaletsCountformulaBuilder.toString());
        // excel is not 0-based                
        totalsToSum.add(index + 1);

        rowsToSum.clear();
    }

    private static void insertFooter(Sheet sheet, CellStyle footerStyle, int index, String packageNumber, String deliveryAddress, String client) {
        String mergeString;

        index += 2;
        Row transportNumberRow = sheet.createRow(index);
        Cell transportNumberCell = transportNumberRow.createCell(1);

        transportNumberCell.setCellValue("Num Tpt: " + packageNumber);
        transportNumberCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$C$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        index += 2;
        Row addressLabelRow = sheet.createRow(index);
        Cell addressLabelCell = addressLabelRow.createCell(1);
        addressLabelCell.setCellValue("A livrer chez: ");
        addressLabelCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$C$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        index += 1;
        Row addressRow = sheet.createRow(index);
        Cell addressCell = addressRow.createCell(1);
        addressCell.setCellValue(deliveryAddress);
        addressCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$I$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        index += 3;
        Row clientLabelRow = sheet.createRow(index);
        Cell clientLabelCell = clientLabelRow.createCell(1);
        clientLabelCell.setCellValue("Pour le compte des Editions: ");
        clientLabelCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$C$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        index += 1;
        Row clientRow = sheet.createRow(index);
        Cell clientCell = clientRow.createCell(1);
        clientCell.setCellValue(client);
        clientCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$I$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        index += 4;
        Row dateRow = sheet.createRow(index);
        Cell dateCell = dateRow.createCell(1);
        dateCell.setCellValue("Date: ................");
        dateCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$C$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        index += 3;
        Row signatureRow = sheet.createRow(index);
        Cell signatureCell = signatureRow.createCell(1);
        signatureCell.setCellValue("Signature et tampon: ...................");
        signatureCell.setCellStyle(footerStyle);
        mergeString = String.format("$B$%s:$C$%s", index + 1, index + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));
    }
}
