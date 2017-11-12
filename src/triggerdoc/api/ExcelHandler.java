/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.2 $
 * Last changed:   $Date: 2017/11/12 12:12:58CET $
 */
package triggerdoc.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.exit;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import triggerdoc.TriggerDoc;
import static triggerdoc.TriggerDoc.log;

/**
 *
 * @author veckardt
 */
public class ExcelHandler {

    private static XSSFWorkbook workbook;

    // location for the excel sheets
    private static final String userHome = System.getProperty("user.home");

    /**
     * Fills the Excel Sheet with the Trigger Data
     *
     * @param row
     * @param col
     * @param text
     * @param isActive
     */
    public static void writeXLSXData(int row, int col, String text, Boolean isActive) {

        if (workbook == null) {
            try (FileInputStream file = new FileInputStream(userHome + "\\TriggerDocTemplate.xlsx")) {
                workbook = new XSSFWorkbook(file);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                exit(2);
            } catch (IOException ex) {
                Logger.getLogger(ExcelHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                exit(3);
            }
        }

        XSSFSheet sheet = workbook.getSheetAt(0);

        // define Style Copy Cell
        XSSFRow copyRow = sheet.getRow(row > 6 ? 6 : 4);
        Cell copyCell = copyRow.getCell((col - 2) % 8 + 1);
        CellStyle copyStyle = copyCell.getCellStyle();

        // Define Cell to add text
        XSSFRow sheetrow = sheet.getRow(row - 1);
        if (sheetrow == null) {
            sheetrow = sheet.createRow(row - 1);
        }
        Cell cell = sheetrow.getCell(col - 1);
        if (cell == null) {
            cell = sheetrow.createCell(col - 1);
        }
        // copy the style from the 4 provided cells when row >4
        if (row > 4) {
            cell.setCellStyle(copyStyle);
        }

        // Plain data entry, in black
        if (isActive) {
            cell.setCellValue(text);
        } else {
            // data entry in gray for deactivated triggers
            XSSFFont greenFont = workbook.createFont();
            greenFont.setColor(HSSFColor.GREY_50_PERCENT.index);

            XSSFRichTextString richString = new XSSFRichTextString(text);
            richString.applyFont(0, text.length(), greenFont);
            cell.setCellValue(richString);

//                CellStyle style = cell.getCellStyle();
//                
//                CellStyle style = workbook.createCellStyle();
//                Font font = workbook.createFont();
//                font.setColor(HSSFColor.GREY_50_PERCENT.index);
//                style.setFont(font);
//                cell.setCellStyle(style);
        }

    }

    /**
     * Writes the new Excel file to the disk
     */
    public static void writeXLSXFile() {

        String outFileName = new SimpleDateFormat("'TriggerDoc-'yyyy-MM-dd'.xlsx'").format(new Date());

        try (FileOutputStream outFile = new FileOutputStream(new File(userHome + "\\" + outFileName))) {
            workbook.write(outFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TriggerDoc.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            exit(4);
        } catch (IOException ex) {
            Logger.getLogger(TriggerDoc.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            exit(5);
        }
        log("File " + userHome + "\\" + outFileName + " created.");

    }
}
