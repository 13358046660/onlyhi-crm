package cn.onlyhi.common.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ExportUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportUtil.class);

    public static void wirteExcelFile2(Map<Map<String, String>, List<Map<String, String>>> map, List<String> sheetNames, File saveFile) {
        LOGGER.info("导出开始...");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(saveFile);
            //创建Workbook对象
            Workbook workbook = new XSSFWorkbook();
            Iterator<Map.Entry<Map<String, String>, List<Map<String, String>>>> entryIterator = map.entrySet().iterator();
            int i = 0;
            while (entryIterator.hasNext()) {
                Map.Entry<Map<String, String>, List<Map<String, String>>> entry = entryIterator.next();
                Map<String, String> headMap = entry.getKey();
                List<Map<String, String>> contentMapList = entry.getValue();
                String sheetName = sheetNames.get(i);
                wirteSheet(workbook, i, sheetName, contentMapList, headMap);
                i++;
            }
            workbook.write(outputStream);
            LOGGER.info("导出成功!");
        } catch (Exception e) {
            LOGGER.info("导出失败:");
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    private static void wirteSheet(Workbook workbook, int sheetNum, String sheetName, List<Map<String, String>> contentMapList, Map<String, String> headMap) throws Exception {
        //创建Sheet对象
        Sheet sheet = workbook.createSheet();
        workbook.setSheetName(sheetNum, sheetName);
        //创建Row对象
        Integer rowNum = 0;
        Row title = sheet.createRow(rowNum++);
        Integer i = 0;
        // 写入文件头部
        for (Iterator propertyIterator = headMap.entrySet().iterator(); propertyIterator
                .hasNext(); ) {
            Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
            Cell cell = title.createCell(i++);
            cell.setCellValue("" + (propertyEntry.getValue() != null ? (String) propertyEntry.getValue() : "") + "");

        }
        // 写入文件内容
        for (Iterator iterator = contentMapList.iterator(); iterator.hasNext(); ) {
            Object row = iterator.next();
            //创建Row对象
            Row sheetRow = sheet.createRow(rowNum++);
            Integer cell = 0;
            for (Iterator propertyIterator = headMap.entrySet().iterator(); propertyIterator.hasNext(); ) {
                //创建Cell对象
                Cell sheetRowCell = sheetRow.createCell(cell++);

                Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                String key = (String) propertyEntry.getKey();
                String value = BeanUtils.getProperty(row, key);
                value = value == null ? "" : value;

                sheetRowCell.setCellValue(value);
            }
        }
    }

    public static void wirteExcelFile(List exportData, LinkedHashMap map, File saveFile) {
        LOGGER.info("导出开始...");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(saveFile);
            //创建HSSFWorkbook对象
            HSSFWorkbook wb = new HSSFWorkbook();

            //创建HSSFSheet对象
            HSSFSheet sheet = wb.createSheet("sheet1");
            //创建HSSFRow对象
            Integer rowNum = 0;
            HSSFRow title = sheet.createRow(rowNum++);
            Integer i = 0;
            // 写入文件头部
            for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator
                    .hasNext(); ) {
                Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                HSSFCell cell = title.createCell(i++);
                cell.setCellValue("" + ((String) propertyEntry.getValue() != null ? (String) propertyEntry.getValue() : "") + "");

            }
            // 写入文件内容
            for (Iterator iterator = exportData.iterator(); iterator.hasNext(); ) {
                Object row = (Object) iterator.next();
                //创建HSSFRow对象
                HSSFRow hssfRow = sheet.createRow(rowNum++);
                Integer cell = 0;
                for (Iterator propertyIterator = map.entrySet().iterator(); propertyIterator.hasNext(); ) {
                    //创建HSSFCell对象
                    HSSFCell hssfCell = hssfRow.createCell(cell++);

                    Map.Entry propertyEntry = (Map.Entry) propertyIterator.next();
                    String key = (String) propertyEntry.getKey();
                    String value = BeanUtils.getProperty(row, key);
                    value = value == null ? "" : value;

                    hssfCell.setCellValue(value);
                }
            }
            wb.write(outputStream);
            LOGGER.info("导出成功！");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

}
