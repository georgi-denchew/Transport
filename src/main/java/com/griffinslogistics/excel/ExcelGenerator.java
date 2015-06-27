/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.excel;

import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Georgi
 */
public abstract class ExcelGenerator {
    public static void fitSheetToA4Page(Sheet sheet){
//        PrintSetup ps = sheet.getPrintSetup();
//            sheet.setAutobreaks(true);
//            ps.setFitHeight((short) 1);
//            ps.setFitWidth((short) 1);
            sheet.setFitToPage(true);
    }
}
