/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.excel;

import db.Bookspackage;
import db.Pulsiodetails;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.BookspackageCMRModel;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Georgi
 */
public class CMRGenerator {

    public static void generateAll(OutputStream outputStream, List<BookspackageCMRModel> bookspackageCMRModels, Pulsiodetails pulsioDetails) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            for (BookspackageCMRModel bookspackageCMRModel : bookspackageCMRModels) {

                XSSFSheet sheet = workbook.createSheet(bookspackageCMRModel.getPackageNumber());
                setDefaultSheetStyles(sheet);

                Map<String, CellStyle> styles = createStyles(workbook);
                int currentRow = 2;
                Row headerRow = sheet.createRow(currentRow);

                generateHeaderRow(headerRow, styles);
                currentRow = generateSenderAndDriver(sheet, styles, currentRow);
                currentRow = generatePoint2Till9(sheet, styles, currentRow);

                double weight = bookspackageCMRModel.getTotalWeight();
                currentRow = generatePoints10Till15(weight, sheet, styles, currentRow);
                currentRow = generatePoint15Till19(sheet, styles, currentRow);
                currentRow = generatePoint20Till24(sheet, styles, currentRow, pulsioDetails);

            }

            workbook.write(outputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CMRGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CMRGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void generateSingle(OutputStream outputStream, BookspackageCMRModel bookspackageCMRModel, Pulsiodetails pulsioDetails) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.createSheet(bookspackageCMRModel.getPackageNumber());
            setDefaultSheetStyles(sheet);

            Map<String, CellStyle> styles = createStyles(workbook);
            int currentRow = 2;
            Row headerRow = sheet.createRow(currentRow);

            generateHeaderRow(headerRow, styles);
            currentRow = generateSenderAndDriver(sheet, styles, currentRow);
            currentRow = generatePoint2Till9(sheet, styles, currentRow);

            double weight = bookspackageCMRModel.getTotalWeight();
            currentRow = generatePoints10Till15(weight, sheet, styles, currentRow);
            currentRow = generatePoint15Till19(sheet, styles, currentRow);
            currentRow = generatePoint20Till24(sheet, styles, currentRow, pulsioDetails);

            workbook.write(outputStream);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CMRGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CMRGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        styles.put(DEFAULT_STYLE, style);

        CellStyle labelStyle = workbook.createCellStyle();
        font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setColor(HSSFColor.DARK_RED.index);
        labelStyle.setFont(font);
        labelStyle.setWrapText(true);
        styles.put(LABEL_STYLE, labelStyle);

        CellStyle labelTopStyle = workbook.createCellStyle();
        labelTopStyle.cloneStyleFrom(labelStyle);
        labelTopStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        labelTopStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        labelTopStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        labelTopStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put(LABEL_TOP_STYLE, labelTopStyle);

        CellStyle labelMiddleStyle = workbook.createCellStyle();
        labelMiddleStyle.cloneStyleFrom(labelStyle);
        labelMiddleStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        labelMiddleStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        labelMiddleStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put(LABEL_MIDDLE_STYLE, labelMiddleStyle);

        CellStyle labelLeftStyle = workbook.createCellStyle();
        labelLeftStyle.cloneStyleFrom(labelStyle);
        labelLeftStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        labelLeftStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put(LABEL_LEFT_STYLE, labelLeftStyle);

        CellStyle labelRightStyle = workbook.createCellStyle();
        labelRightStyle.cloneStyleFrom(labelStyle);
        labelRightStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        labelRightStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put(LABEL_RIGHT_STYLE, labelRightStyle);

        CellStyle labelBottomStyle = workbook.createCellStyle();
        labelBottomStyle.cloneStyleFrom(labelStyle);
        labelBottomStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        labelBottomStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        labelBottomStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        styles.put(LABEL_BOTTOM_STYLE, labelBottomStyle);

        CellStyle labelWholeStyle = workbook.createCellStyle();
        labelWholeStyle.cloneStyleFrom(labelStyle);
        labelWholeStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        labelWholeStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        labelWholeStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        labelWholeStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        labelWholeStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
        styles.put(LABEL_WHOLE_STYLE, labelWholeStyle);

        Font contentFont = workbook.createFont();
        contentFont.setFontHeightInPoints((short) 12);
        contentFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

        CellStyle contentTopStyle = workbook.createCellStyle();
        contentTopStyle.cloneStyleFrom(labelTopStyle);
        contentTopStyle.setFont(contentFont);
        styles.put(CONTENT_TOP_STYLE, contentTopStyle);

        CellStyle contentMiddleStyle = workbook.createCellStyle();
        contentMiddleStyle.cloneStyleFrom(labelMiddleStyle);
        contentMiddleStyle.setFont(contentFont);
        styles.put(CONTENT_MIDDLE_STYLE, contentMiddleStyle);

        CellStyle contentMiddleAllignRightStyle = workbook.createCellStyle();
        contentMiddleAllignRightStyle.cloneStyleFrom(contentMiddleStyle);
        contentMiddleAllignRightStyle.setBorderRight(CellStyle.BORDER_NONE);
        contentMiddleAllignRightStyle.setFont(contentFont);
        contentMiddleAllignRightStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        styles.put(CONTENT_MIDDLE_ALLIGN_RIGHT_STYLE, contentMiddleAllignRightStyle);

        CellStyle contentBottomAllignCenterStyle = workbook.createCellStyle();
        contentBottomAllignCenterStyle.cloneStyleFrom(contentMiddleStyle);
        contentBottomAllignCenterStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        contentBottomAllignCenterStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        contentBottomAllignCenterStyle.setFont(contentFont);
        contentBottomAllignCenterStyle.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put(CONTENT_BOTTOM_ALLIGN_CENTER_STYLE, contentBottomAllignCenterStyle);

        CellStyle contentMiddleNoBordersAllignRightStyle = workbook.createCellStyle();
        contentMiddleNoBordersAllignRightStyle.cloneStyleFrom(contentMiddleAllignRightStyle);
        contentMiddleNoBordersAllignRightStyle.setBorderLeft(CellStyle.BORDER_NONE);
        contentMiddleNoBordersAllignRightStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentMiddleNoBordersAllignRightStyle.setFont(contentFont);
        styles.put(CONTENT_MIDDLE_NO_BORDERS_STYLE, contentMiddleNoBordersAllignRightStyle);

        CellStyle contentMiddleAllignCenterStyle = workbook.createCellStyle();
        contentMiddleAllignCenterStyle.cloneStyleFrom(contentMiddleStyle);
        contentMiddleAllignCenterStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentMiddleAllignCenterStyle.setFont(contentFont);
        styles.put(CONTENT_MIDDLE_ALLIGN_CENTER_STYLE, contentMiddleAllignCenterStyle);

        CellStyle contentRightStyle = workbook.createCellStyle();
        contentRightStyle.cloneStyleFrom(labelRightStyle);
        contentRightStyle.setFont(contentFont);
        styles.put(CONTENT_RIGHT_STYLE, contentRightStyle);

        CellStyle contentBottomStyle = workbook.createCellStyle();
        contentBottomStyle.cloneStyleFrom(labelBottomStyle);
        contentBottomStyle.setFont(contentFont);
        styles.put(CONTENT_BOTTOM_STYLE, contentBottomStyle);

        CellStyle contentWholeStyle = workbook.createCellStyle();
        contentWholeStyle.cloneStyleFrom(labelWholeStyle);
        contentWholeStyle.setFont(contentFont);
        styles.put(CONTENT_WHOLE_STYLE, contentWholeStyle);

        return styles;
    }
    private static final String CONTENT_BOTTOM_ALLIGN_CENTER_STYLE = "contentBottomAllignCenterStyle";
    private static final String CONTENT_MIDDLE_ALLIGN_CENTER_STYLE = "contentMiddleAllignCenterStyle";
    private static final String CONTENT_MIDDLE_NO_BORDERS_STYLE = "contentMiddleNoBordersStyle";
    private static final String CONTENT_MIDDLE_ALLIGN_RIGHT_STYLE = "contentMiddleAllignRightStyle";

    private static void generateHeaderRow(Row headerRow, Map<String, CellStyle> styles) {
        Cell headingCell = headerRow.createCell(2);
        headingCell.setCellValue(HEADING_INTERNATIONAL_BILL);
        headingCell.setCellStyle(styles.get(DEFAULT_STYLE));

        Cell CMRCell = headerRow.createCell(3);
        CMRCell.setCellValue(HEADING_CMR);
        CMRCell.setCellStyle(styles.get(DEFAULT_STYLE));

        Cell internationalHeadingCell = headerRow.createCell(4);
        internationalHeadingCell.setCellValue(HEADING_CONSIGNMENT);
        internationalHeadingCell.setCellStyle(styles.get(DEFAULT_STYLE));

        Cell countryHeadingCell = headerRow.createCell(8);
        countryHeadingCell.setCellValue(HEADING_COUNTRY);
        countryHeadingCell.setCellStyle(styles.get(DEFAULT_STYLE));

        Cell numberHeadingCell = headerRow.createCell(10);
        numberHeadingCell.setCellValue(HEADING_NUMBER);
        numberHeadingCell.setCellStyle(styles.get(DEFAULT_STYLE));
    }

    private static void setDefaultSheetStyles(XSSFSheet sheet) {
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 8000);
    }

    private static int generateSenderAndDriver(XSSFSheet sheet, Map<String, CellStyle> styles, int currentRow) {
        // new value is 5
        currentRow = currentRow + 3;

        Row labelRow = sheet.createRow(currentRow);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$6:$C6"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$7:$C7"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$8:$C8"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$9:$C9"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$10:$C10"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$6:$I$10"));

        Cell senderLabelCell = labelRow.createCell(1);
        senderLabelCell.setCellValue(LABEL_SENDER);
        senderLabelCell.setCellStyle(styles.get(LABEL_TOP_STYLE));
        labelRow.createCell(2).setCellStyle(styles.get(LABEL_TOP_STYLE));

        currentRow++;
        Row addressRow1 = sheet.createRow(currentRow);
        Cell addressCell1 = addressRow1.createCell(1);
        addressCell1.setCellValue(ADDRESS_PART_1);
        addressCell1.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        currentRow++;
        Row addressRow2 = sheet.createRow(currentRow);
        Cell addressCell2 = addressRow2.createCell(1);
        addressCell2.setCellValue(ADDRESS_PART_2);
        addressCell2.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        currentRow++;
        Row addressRow3 = sheet.createRow(currentRow);
        Cell addressCell3 = addressRow3.createCell(1);
        addressCell3.setCellValue(ADDRESS_PART_3);
        addressCell3.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        currentRow++;
        Row addressRow4 = sheet.createRow(currentRow);
        Cell addressCell4 = addressRow4.createCell(1);
        addressCell4.setCellValue(ADDRESS_PART_4);
        addressCell4.setCellStyle(styles.get(CONTENT_BOTTOM_STYLE));
        addressRow4.createCell(2).setCellStyle(styles.get(CONTENT_BOTTOM_STYLE));

        //set styles for driver cells
        for (int i = 5; i <= 9; i++) {
            // the rows are already initialized above
            Row row = sheet.getRow(i);

            for (int j = 3; j <= 8; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(styles.get(LABEL_WHOLE_STYLE));
            }
        }

        Cell driverCell = labelRow.createCell(3);
        driverCell.setCellValue(LABEL_DRIVER);
        driverCell.setCellStyle(styles.get(LABEL_WHOLE_STYLE));

        return currentRow;
    }

    private static int generatePoint2Till9(XSSFSheet sheet, Map<String, CellStyle> styles, int currentRow) {
        for (int i = 11; i < 27; i++) {
            sheet.addMergedRegion(CellRangeAddress.valueOf("$B$" + i + ":$C$" + i));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$D$" + i + ":$I$" + i));
        }

        currentRow++;
        Row row11 = sheet.createRow(currentRow);
        Cell receiverLabelCell = row11.createCell(1);
        receiverLabelCell.setCellValue(LABEL_RECEIVER);
        receiverLabelCell.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row11.createCell(2).setCellStyle(styles.get(LABEL_TOP_STYLE));

        currentRow++;
        Row row12 = sheet.createRow(currentRow);
        row12.createCell(1).setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        row12.createCell(2).setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        currentRow++;
        Row row13 = sheet.createRow(currentRow);
        Cell receiverCell1 = row13.createCell(1);
        receiverCell1.setCellValue(RECEIVER_PART_1);
        receiverCell1.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        row13.createCell(2).setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        currentRow++;
        Row row14 = sheet.createRow(currentRow);
        Cell receiverCell2 = row14.createCell(1);
        receiverCell2.setCellValue(RECEIVER_PART_2);
        receiverCell2.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        row14.createCell(2).setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        row14.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row15 = sheet.createRow(currentRow);
        Cell receiverCell3 = row15.createCell(1);
        receiverCell3.setCellValue(RECEIVER_PART_3);
        receiverCell3.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        row15.createCell(2).setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        Cell successiveCarrierCell1 = row11.createCell(3);
        successiveCarrierCell1.setCellValue(LABEL_SUCCESSIVE_CARRIERS_PART_1);
        successiveCarrierCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row11.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell successiveCarrierCell2 = row12.createCell(3);
        successiveCarrierCell2.setCellValue(LABEL_SUCCESSIVE_CARRIERS_PART_2);
        successiveCarrierCell2.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row12.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell successiveCarrierCell3 = row13.createCell(3);
        successiveCarrierCell3.setCellValue(LABEL_SUCCESSIVE_CARRIERS_PART_3);
        successiveCarrierCell3.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row13.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell reservationsCell1 = row15.createCell(3);
        reservationsCell1.setCellValue(RESERVATIONS_PART_1);
        reservationsCell1.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row15.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row16 = sheet.createRow(currentRow);
        Cell reservationsCell2 = row16.createCell(3);
        reservationsCell2.setCellValue(RESERVATIONS_PART_2);
        reservationsCell2.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row16.createCell(1).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row16.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row16.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row17 = sheet.createRow(currentRow);
        row17.createCell(1).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row17.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row17.createCell(3).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row17.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row18 = sheet.createRow(currentRow);
        Cell takingOverCellLabel = row18.createCell(1);
        takingOverCellLabel.setCellValue(LABEL_TAKING_OVER);
        takingOverCellLabel.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row18.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row18.createCell(3).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row18.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row19 = sheet.createRow(currentRow);
        Cell takingOverContentCell = row19.createCell(1);
        takingOverContentCell.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        takingOverContentCell.setCellValue(TAKING_OVER_CONTENT);

        row19.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row19.createCell(3).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row19.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row20 = sheet.createRow(currentRow);
        Cell countryLabelCell = row20.createCell(1);
        countryLabelCell.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        countryLabelCell.setCellValue(LABEL_COUNTRY);

        row20.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row20.createCell(3).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row20.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row21 = sheet.createRow(currentRow);
        Cell countryContentCell = row21.createCell(1);
        countryContentCell.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        countryContentCell.setCellValue(ADDRESS_PART_4.trim());

        row21.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row21.createCell(3).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row21.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row22 = sheet.createRow(currentRow);
        Cell dateCell = row22.createCell(1);
        dateCell.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        dateCell.setCellValue(LABEL_DATE);

        row22.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row22.createCell(3).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row22.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row23 = sheet.createRow(currentRow);
        Cell arrivalTime = row23.createCell(1);
        arrivalTime.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        arrivalTime.setCellValue(LABEL_ARRIVAL_TIME);

        row23.createCell(2).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        for (int i = 3; i < 9; i++) {
            row23.createCell(i).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        }

        currentRow++;
        Row row24 = sheet.createRow(currentRow);
        Cell goodsDeliveryLabelCell = row24.createCell(1);
        goodsDeliveryLabelCell.setCellValue(LABEL_DELIVERY_OF_GOODS);
        goodsDeliveryLabelCell.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row24.createCell(2).setCellStyle(styles.get(LABEL_TOP_STYLE));

        currentRow++;
        Row row25 = sheet.createRow(currentRow);
        Cell placeLabelCell = row25.createCell(1);
        placeLabelCell.setCellValue(LABEL_PLACE);
        placeLabelCell.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row25.createCell(2).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row26 = sheet.createRow(currentRow);
        Cell placeContentCell = row26.createCell(1);
        placeContentCell.setCellValue("Editions Intervalles 80 Boulevard Haussmann 75008 Paris");
        placeContentCell.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        row26.createCell(2).setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));

        currentRow++;
        Row row27 = sheet.createRow(currentRow);
        Cell deliveryCountryLabelCell = row27.createCell(1);
        deliveryCountryLabelCell.setCellValue(LABEL_COUNTRY);
        deliveryCountryLabelCell.setCellStyle(styles.get(LABEL_LEFT_STYLE));
        Cell deliveryCountryContentCell = row27.createCell(2);
        deliveryCountryContentCell.setCellValue(FRANCE);
        deliveryCountryContentCell.setCellStyle(styles.get(CONTENT_RIGHT_STYLE));

        currentRow++;
        Row row28 = sheet.createRow(currentRow);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$28:$C$28"));
        Cell openingHoursCell = row28.createCell(1);
        openingHoursCell.setCellValue(LABEL_OPENING_HOURS);
        openingHoursCell.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        row28.createCell(2).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        currentRow++;
        Row row29 = sheet.createRow(currentRow);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$29:$C$29"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$30:$C$30"));
        Cell senderInstructionsCell1 = row29.createCell(1);
        senderInstructionsCell1.setCellValue(SENDER_INSTRUCTIONS_BULGARIAN);
        senderInstructionsCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row29.createCell(2).setCellStyle(styles.get(LABEL_TOP_STYLE));

        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$29:$I$29"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$30:$I$30"));
        Cell carrierDocumentsCell1 = row29.createCell(3);
        carrierDocumentsCell1.setCellValue(CARRIER_DOCUMENTS_BULGARIAN);
        carrierDocumentsCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));

        for (int i = 4; i < 9; i++) {
            row29.createCell(i).setCellStyle(styles.get(LABEL_TOP_STYLE));
        }

        currentRow++;
        Row row30 = sheet.createRow(currentRow);
        Cell senderInstructionsCell2 = row30.createCell(1);
        senderInstructionsCell2.setCellValue(SENDERS_INSTRUCTIONS_ENGLISH);
        senderInstructionsCell2.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        row30.createCell(2).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        Cell carrierDocumentsCell2 = row30.createCell(3);
        carrierDocumentsCell2.setCellValue(CARRIER_DOCUMENTS_ENGLISH);
        carrierDocumentsCell2.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        for (int i = 4; i < 9; i++) {
            row30.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        }

        for (int i = 23; i < 28; i++) {
            sheet.getRow(i).createCell(8).setCellStyle(styles.get(LABEL_RIGHT_STYLE));
        }

        return currentRow;
    }

    private static int generatePoints10Till15(double weight, XSSFSheet sheet, Map<String, CellStyle> styles, int currentRow) {

        currentRow++;
        Row row31 = sheet.createRow(currentRow);
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$31:$E$31"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$F$31:$G$31"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$H$31:$I$31"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$32:$E$32"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$F$32:$G$32"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$H$32:$I$32"));

        Cell labels10Till13Cell = row31.createCell(1);
        labels10Till13Cell.setCellValue(LABELS_10_TILL_13_BULGARIAN);
        labels10Till13Cell.setCellStyle(styles.get(LABEL_TOP_STYLE));

        Cell weightLabelCell1 = row31.createCell(5);
        weightLabelCell1.setCellValue(LABEL_WEIGHT_BULGARIAN);
        weightLabelCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));

        Cell volumeLabelCell1 = row31.createCell(7);
        volumeLabelCell1.setCellValue(LABEL_VOLUME);
        volumeLabelCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row31.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row32 = sheet.createRow(currentRow);

        Cell labels10Till13Cel2 = row32.createCell(1);
        labels10Till13Cel2.setCellValue(LABEL_10_TILL_13_ENGLISH);
        labels10Till13Cel2.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell weightLabelCell2 = row32.createCell(5);
        weightLabelCell2.setCellValue(LABEL_WEIGHT_ENGLISH);
        weightLabelCell2.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell volumeLabelCell2 = row32.createCell(7);
        volumeLabelCell2.setCellValue(LABEL_VOLUME_ENGLISH);
        volumeLabelCell2.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
        row32.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        // DYNAMIC CONTENT FOR BOOKS
        sheet.addMergedRegion(CellRangeAddress.valueOf("$D$33:$E$33"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$F$33:$G$33"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$H$33:$I$33"));

        currentRow++;
        Row row33 = sheet.createRow(currentRow);
        Cell packagesCountCell = row33.createCell(1);
        packagesCountCell.setCellValue(1300);
        packagesCountCell.setCellStyle(styles.get(CONTENT_MIDDLE_ALLIGN_RIGHT_STYLE));

        Cell packagingMethodCell = row33.createCell(2);
        packagingMethodCell.setCellValue("CARTONS");
        packagingMethodCell.setCellStyle(styles.get(CONTENT_MIDDLE_NO_BORDERS_STYLE));

        Cell goodsNatureCell = row33.createCell(3);
        goodsNatureCell.setCellValue("BOOKS");
        goodsNatureCell.setCellStyle(styles.get(CONTENT_MIDDLE_ALLIGN_CENTER_STYLE));

        Cell grossWeightCell = row33.createCell(5);

        try {
            BigDecimal bd = new BigDecimal(weight);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            weight = bd.doubleValue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        grossWeightCell.setCellValue(weight);
        grossWeightCell.setCellStyle(styles.get(CONTENT_MIDDLE_ALLIGN_CENTER_STYLE));

        for (int j = 6; j <= 8; j++) {
            row33.createCell(j).setCellStyle(styles.get(CONTENT_MIDDLE_ALLIGN_CENTER_STYLE));
        }

        currentRow++;

        Row totalsRow = sheet.createRow(currentRow);
        String cellMergeString = String.format("$B$%s:$E$%s", currentRow + 1, currentRow + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(cellMergeString));

        cellMergeString = String.format("$F$%s:$G$%s", currentRow + 1, currentRow + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(cellMergeString));

        cellMergeString = String.format("$H$%s:$I$%s", currentRow + 1, currentRow + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(cellMergeString));

        Cell totalsLabelCell = totalsRow.createCell(1);
        totalsLabelCell.setCellValue(LABEL_TOTALS_10_TILL_15);
        totalsLabelCell.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        for (int i = 2; i <= 4; i++) {
            totalsRow.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        }

        Cell totalWeightCell = totalsRow.createCell(5);
        totalWeightCell.setCellStyle(styles.get(CONTENT_BOTTOM_ALLIGN_CENTER_STYLE));
        totalWeightCell.setCellFormula("SUM(F33)");
        totalsRow.createCell(6).setCellStyle(styles.get(CONTENT_BOTTOM_ALLIGN_CENTER_STYLE));

        Cell totalVolumeCell = totalsRow.createCell(7);
        totalVolumeCell.setCellStyle(styles.get(CONTENT_BOTTOM_ALLIGN_CENTER_STYLE));
        totalVolumeCell.setCellFormula("SUM(H33)");
        totalsRow.createCell(8).setCellStyle(styles.get(CONTENT_BOTTOM_ALLIGN_CENTER_STYLE));

        return currentRow;

    }

    private static int generatePoint15Till19(XSSFSheet sheet, Map<String, CellStyle> styles, int currentRow) {
        currentRow++;

        String mergeString;

        for (int i = currentRow; i < currentRow + 7; i++) {
            sheet.createRow(i).setHeightInPoints(25);
            mergeString = String.format("$B$%s:$C$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

            mergeString = String.format("$D$%s:$E$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

            mergeString = String.format("$F$%s:$G$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

            mergeString = String.format("$H$%s:$I$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));
        }

        Row row35 = sheet.getRow(currentRow);
        Cell agreementsLabelCell1 = row35.createCell(1);
        agreementsLabelCell1.setCellValue(LABEL_AGREEMENTS_BULGARIAN);
        agreementsLabelCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));

        Cell paidByCell1 = row35.createCell(3);
        paidByCell1.setCellValue(LABEL_PAID_BY_BULGARIAN);
        paidByCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));

        Cell SenderCell1 = row35.createCell(5);
        SenderCell1.setCellValue(LABEL_SENDER_17_BULGARIAN);
        SenderCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row35.createCell(6).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell ReceiverCell1 = row35.createCell(7);
        ReceiverCell1.setCellValue(LABEL_RECEIVER_17_BULGARIAN);
        ReceiverCell1.setCellStyle(styles.get(LABEL_TOP_STYLE));
        row35.createCell(8).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        currentRow++;
        Row row36 = sheet.getRow(currentRow);
        Cell agreementsLabelCell2 = row36.createCell(1);
        agreementsLabelCell2.setCellValue(LABEL_AGREEMENTS_ENGLISH);
        agreementsLabelCell2.setCellStyle(styles.get(LABEL_MIDDLE_STYLE));

        Cell paidByCell2 = row36.createCell(3);
        paidByCell2.setCellValue(LABEL_PAID_BY_ENGLISH);
        paidByCell2.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        row36.createCell(4).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        Cell SenderCell2 = row36.createCell(5);
        SenderCell2.setCellValue(LABEL_SENDER_17_ENGLISH);
        SenderCell2.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        row36.createCell(6).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        Cell ReceiverCell2 = row36.createCell(7);
        ReceiverCell2.setCellValue(LABEL_RECEIVER_17_ENGLISH);
        ReceiverCell2.setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        row36.createCell(8).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));

        currentRow++;
        Row row37 = sheet.getRow(currentRow);
        currentRow++;
        Row row38 = sheet.getRow(currentRow);
        currentRow++;
        Row row39 = sheet.getRow(currentRow);
        currentRow++;
        Row row40 = sheet.getRow(currentRow);
        currentRow++;
        Row row41 = sheet.getRow(currentRow);

        for (int i = 1; i < 9; i++) {
            row37.createCell(i).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
            row38.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
            row39.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
            row40.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
            row41.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        }

        Cell carriageChargesCell1 = row37.getCell(3);
        carriageChargesCell1.setCellValue(LABEL_CARRIAGE_CHARGES_BULGARIAN);

        Cell carriageChargesCell2 = row38.getCell(3);
        carriageChargesCell2.setCellValue(LABEL_CARRIAGE_CHANGES_ENGLISH);

        Cell supplementaryChargesCell = row39.getCell(3);
        row39.setHeightInPoints(80);
        supplementaryChargesCell.setCellValue(LABEL_SUPPLEMENTARY_CHARGES);

        Cell customsDutiesCell = row40.getCell(3);
        row40.setHeightInPoints(50);
        customsDutiesCell.setCellValue(LABEL_CUSTOMS_DUTIES);

        Cell otherChargesCell = row41.getCell(3);
        row41.setHeightInPoints(50);
        otherChargesCell.setCellValue(LABEL_OTHER_CHARGES);

        currentRow++;

        for (int i = currentRow; i < currentRow + 2; i++) {
            sheet.createRow(i);
            mergeString = String.format("$B$%s:$C$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

            mergeString = String.format("$D$%s:$I$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));
        }

        Row row42 = sheet.getRow(currentRow);
        currentRow++;
        Row row43 = sheet.getRow(currentRow);

        for (int i = 1; i < 9; i++) {
            row42.createCell(i).setCellStyle(styles.get(LABEL_TOP_STYLE));
            row43.createCell(i).setCellStyle(styles.get(LABEL_BOTTOM_STYLE));
        }

        Cell remarksCell1 = row42.getCell(1);
        remarksCell1.setCellValue(LABEL_REMARKS_BULGARIAN);
        Cell cashOnDeliveryCell1 = row42.getCell(3);
        cashOnDeliveryCell1.setCellValue(LABEL_CASH_ON_DELIVERY_BULGARIAN);

        Cell remarksCell2 = row43.getCell(1);
        remarksCell2.setCellValue(LABEL_REMARKS_ENGLISH);
        Cell cashOnDeliveryCell2 = row43.getCell(3);
        cashOnDeliveryCell2.setCellValue(LABEL_CASH_ON_DELIVERY_ENGLISH);

        return currentRow;
    }

    private static final String HEADING_INTERNATIONAL_BILL = "МЕЖДУНАРОДНА ТОВАРИТЕЛНИЦА";
    private static final String HEADING_CONSIGNMENT = "INTERNATIONAL CONSIGNMENT NOTE";
    private static final String HEADING_COUNTRY = ".......... Държава/Country";
    private static final String HEADING_NUMBER = "No ..........";
    private static final String HEADING_CMR = "CMR";

    private static final String LABEL_SENDER = "1 Изпращач (име, адрес, държава) Sender (name, address, country)";
    private static final String LABEL_DRIVER = "6 Превозвач (име, адрес, държава, други референции) Carrier (name, address, country, other references)";

    private static final String ADDRESS_PART_4 = "   BULGARIA";
    private static final String ADDRESS_PART_3 = "   Bl. 3 ENTR. A";
    private static final String ADDRESS_PART_2 = "   BOJURISHTE PROLET DISTR.";
    private static final String ADDRESS_PART_1 = "   BOOKPRINT LTD.";

    private static final String CONTENT_RIGHT_STYLE = "contentRightStyle";
    private static final String LABEL_RIGHT_STYLE = "labelRightStyle";
    private static final String LABEL_LEFT_STYLE = "labelLeftStyle";
    private static final String CONTENT_WHOLE_STYLE = "contentWholeStyle";
    private static final String CONTENT_BOTTOM_STYLE = "contentBottomStyle";
    private static final String CONTENT_MIDDLE_STYLE = "contentMiddleStyle";
    private static final String CONTENT_TOP_STYLE = "contentTopStyle";
    private static final String LABEL_STYLE = "labelStyle";
    private static final String DEFAULT_STYLE = "defaultStyle";
    private static final String LABEL_WHOLE_STYLE = "labelWholeStyle";
    private static final String LABEL_BOTTOM_STYLE = "labelBottomStyle";
    private static final String LABEL_MIDDLE_STYLE = "labelMiddleStyle";
    private static final String LABEL_TOP_STYLE = "labelTopStyle";

    private static final String CARRIER_DOCUMENTS_ENGLISH = "Documents handed to the carrier by the sender";

    private static final String SENDERS_INSTRUCTIONS_ENGLISH = "Sender`s instructions";
    private static final String CARRIER_DOCUMENTS_BULGARIAN = "9  Документи в превозвача от изпращача";
    private static final String SENDER_INSTRUCTIONS_BULGARIAN = "5 Указания на изпращача";
    private static final String LABEL_OPENING_HOURS = "      Работно време на склада / Warehouse opening hours";

    private static final String FRANCE = "France";
    private static final String LABEL_PLACE = "       Място / Place";
    private static final String LABEL_DELIVERY_OF_GOODS = "4 Разтоварен пункт / Delivery of the goods: ";

    private static final String LABEL_ARRIVAL_TIME = "Време на пристигане / Time of arrival Време на тръгване / Time of departure";
    private static final String LABEL_DATE = "Date";
    private static final String LABEL_COUNTRY = "Държава / Country";
    private static final String TAKING_OVER_CONTENT = "  DRUJBA DISTR.  SOFIA";
    private static final String LABEL_TAKING_OVER = "3 Товарен пункт / Taking over the goods: Място / Place";
    private static final String RESERVATIONS_PART_2 = "Carrier`s reservations and observations on taking over the goods";
    private static final String RESERVATIONS_PART_1 = "8   Възражения и бележки на превозвача при вземане на колетите";
    private static final String LABEL_SUCCESSIVE_CARRIERS_PART_3 = "Receipt and Acceptance Date Signature";
    private static final String LABEL_SUCCESSIVE_CARRIERS_PART_2 = "Име / Name Адрес / Address Държава / Country Получаване и приемане";
    private static final String LABEL_SUCCESSIVE_CARRIERS_PART_1 = "7  Следващ превозвач / Successive carriers";
    private static final String RECEIVER_PART_3 = "     PARIS FRANCE";
    private static final String RECEIVER_PART_2 = "     38 RUE DURANTIN";
    private static final String RECEIVER_PART_1 = "     PULSIO S.A.R.L.";
    private static final String LABEL_RECEIVER = "2 Получател (име, адрес, държава) Consignee (name, address, country)";

    private static final String LABEL_TOTALS_10_TILL_15 = "UN Номер Име виж   13 Етикет номер Опаковка група (ADR*) UN Number Name see  13 Label Number ";
    private static final String LABEL_VOLUME_ENGLISH = "Volume in m3";
    private static final String LABEL_WEIGHT_ENGLISH = "Gross weight in kg";
    private static final String LABEL_10_TILL_13_ENGLISH = "Marks and Nos                Number of packages             Method of packing               Nature of the goods";
    private static final String LABEL_VOLUME = "15 Обем m3";
    private static final String LABEL_WEIGHT_BULGARIAN = "14 Тегло бруто, kg";
    private static final String LABELS_10_TILL_13_BULGARIAN = "10 Знаци и номера               11 Брой колети                 12 Вид опаковка                13 Вид на стоката";

    private static final String LABEL_CASH_ON_DELIVERY_ENGLISH = "Cash on delivery";
    private static final String LABEL_REMARKS_ENGLISH = "18 Other useful particulars";
    private static final String LABEL_CASH_ON_DELIVERY_BULGARIAN = "19  Наложен платеж";
    private static final String LABEL_REMARKS_BULGARIAN = "Други особености";
    private static final String LABEL_OTHER_CHARGES = "Други такси / Other charges";
    private static final String LABEL_CUSTOMS_DUTIES = "Митнически такси / Customs duties";
    private static final String LABEL_SUPPLEMENTARY_CHARGES = "Допълнителни такси / Supplementary charges";
    private static final String LABEL_CARRIAGE_CHANGES_ENGLISH = "Carriage charges";
    private static final String LABEL_CARRIAGE_CHARGES_BULGARIAN = "Превозна цена";
    private static final String LABEL_RECEIVER_17_ENGLISH = "Consignee";
    private static final String LABEL_SENDER_17_ENGLISH = "Sender";
    private static final String LABEL_PAID_BY_ENGLISH = "17 To be paid by:";
    private static final String LABEL_AGREEMENTS_ENGLISH = "16 Special agreements between the sender and the carrier";
    private static final String LABEL_RECEIVER_17_BULGARIAN = "Получател";
    private static final String LABEL_SENDER_17_BULGARIAN = "Изпращач";
    private static final String LABEL_PAID_BY_BULGARIAN = "За плащане от";
    private static final String LABEL_AGREEMENTS_BULGARIAN = "Специални споразомения между изпращача и превозвача";

    private static int generatePoint20Till24(XSSFSheet sheet, Map<String, CellStyle> styles, int currentRow, Pulsiodetails pulsiodetails) {
        currentRow++;

        String mergeString;

        for (int i = currentRow; i < currentRow + 2; i++) {
            Row row = sheet.createRow(i);
            mergeString = String.format("$B$%s:$I$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

            for (int j = 1; j < 9; j++) {
                row.createCell(j).setCellStyle(styles.get(i == currentRow ? LABEL_MIDDLE_STYLE : LABEL_BOTTOM_STYLE));
            }
        }

        for (int i = currentRow + 2; i < currentRow + 16; i++) {
            Row row = sheet.createRow(i);
            mergeString = String.format("$D$%s:$I$%s", i + 1, i + 1);
            sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));
            for (int j = 1; j < 9; j++) {
                row.createCell(j).setCellStyle(styles.get(LABEL_MIDDLE_STYLE));
            }
        }

        currentRow++;
        Row row45 = sheet.getRow(currentRow);
        row45.setHeightInPoints(30);
        row45.getCell(1).setCellValue(LABEL_POINT_20);

        currentRow++;
        Row row46 = sheet.getRow(currentRow);

        Cell establishedInCell = row46.getCell(1);
        establishedInCell.setCellValue(LABEL_ESTABLISHED_IN);

        Cell establishedOnCell = row46.getCell(2);
        establishedOnCell.setCellValue(LABEL_ESTABLISHED_ON);

        Cell goodsDeliveredCell = row46.getCell(3);
        goodsDeliveredCell.setCellValue(LABEL_GOODS_RECEIVED);

        currentRow++;
        Row row47 = sheet.getRow(currentRow);
        row47.getCell(3).setCellValue(LABEL_TIME_OF_ARRIVAL);

        currentRow++;
        Row row48 = sheet.getRow(currentRow);
        Cell cityCell = row48.getCell(1);
        cityCell.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        cityCell.setCellValue("Sofia, Bulgaria");

        Cell dateCell = row48.getCell(2);
        dateCell.setCellStyle(styles.get(CONTENT_MIDDLE_STYLE));
        dateCell.setCellValue(new SimpleDateFormat("dd.MM.yyyy").format(new Date()));

        currentRow += 2;
        sheet.getRow(currentRow).getCell(3).setCellValue(LABEL_PLACE_20);

        currentRow++;
        Row row51 = sheet.getRow(currentRow);

        currentRow++;

        row51.getCell(1).setCellValue(TWENTY_TWO);
        row51.getCell(2).setCellValue(TWENTY_THREE);

        // Insert signature picture
        Workbook workbook = sheet.getWorkbook();
        byte[] imageBytes = pulsiodetails.getSignature();
        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();

        //set top-left corner for the image
        anchor.setCol1(1);
        anchor.setRow1(currentRow);

        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize();

        currentRow += 4;
        Row row56 = sheet.getRow(currentRow);
        row56.getCell(3).setCellValue(LABEL_SIGNATURE_STAMP);

        currentRow += 2;
        Row row58 = sheet.getRow(currentRow);
        Cell signatureLabelCell1 = row58.getCell(1);
        signatureLabelCell1.setCellValue(LABEL_SENDER_SIGNATURE_BULGARIAN);

        Cell carrierSignatureCell = row58.getCell(2);
        carrierSignatureCell.setCellValue(LABEL_CARRIER_SIGNATURE_BULGARIAN);

        Cell receiverSignatureCell = row58.getCell(3);
        receiverSignatureCell.setCellValue(LABEL_RECEIVER_SIGNATURE_BULGARIAN);

        currentRow++;
        Row row59 = sheet.getRow(currentRow);
        Cell signatureLabelCell2 = row59.getCell(1);
        signatureLabelCell2.setCellValue(LABEL_SENDER_SIGNATURE_ENGLISH);

        Cell carrierSignatureCel2 = row59.getCell(2);
        carrierSignatureCel2.setCellValue(LABEL_CARRIER_SIGNATURE_BULGARIAN);

        Cell receiverSignatureCel2 = row59.getCell(3);
        receiverSignatureCel2.setCellValue(LABEL_RECEIVER_SIGNATURE_ENGLISH);

        currentRow++;
        Row row60 = sheet.createRow(currentRow);
        mergeString = String.format("$B$%s:$I$%s", currentRow + 1, currentRow + 1);
        sheet.addMergedRegion(CellRangeAddress.valueOf(mergeString));

        for (int i = 1; i < 9; i++) {
            row60.createCell(i).setCellStyle(styles.get(LABEL_WHOLE_STYLE));
        }

        Cell additionalSpaceCell = row60.getCell(1);
        additionalSpaceCell.setCellValue(LABEL_ADDITIONAL_SPACE);

        return currentRow;
    }
    private static final String LABEL_ADDITIONAL_SPACE = "Допълнителна част резервирана за превозвача / Non-contractual part reserved for the carrier";
    private static final String LABEL_RECEIVER_SIGNATURE_ENGLISH = "Signature and stamp of the consignee";
    private static final String LABEL_CARRIER_SIGNATURE_BULGARIAN = "Signature or stamp of the carrier";
    private static final String LABEL_SENDER_SIGNATURE_ENGLISH = "Signature or stamp of the sender";
    private static final String LABEL_RECEIVER_SIGNATURE_BULGARIAN = "Подпис и печат на получателя";
    private static final String LABEL_SENDER_SIGNATURE_BULGARIAN = "Подпис и печат на изпращача";
    private static final String LABEL_SIGNATURE_STAMP = "Signature and stamp of the consignee";
    private static final String TWENTY_THREE = "23";
    private static final String TWENTY_TWO = "22";
    private static final String LABEL_PLACE_20 = "Място На 20.. Place on 20..";
    private static final String LABEL_GOODS_RECEIVED = "24 Стоките получени / Goods received";
    private static final String LABEL_TIME_OF_ARRIVAL = "Време на пристигане / Time of arrival Времена тръгване / Time of departure";
    private static final String LABEL_ESTABLISHED_ON = " на / on .........................";
    private static final String LABEL_ESTABLISHED_IN = "21 Изготвена в / Established in..................... ";
    private static final String LABEL_POINT_20 = "20 Този превоз, независимо от всяка противна клауза е подчинен на Конвенцията относно договора за международен превоз на стоки по пътищата(CMR) This carriage is subject, notwithstanding any clause to the contrary, to the Convention on the Contract for the";
}
