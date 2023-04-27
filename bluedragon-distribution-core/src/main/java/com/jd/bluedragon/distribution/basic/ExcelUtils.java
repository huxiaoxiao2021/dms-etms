package com.jd.bluedragon.distribution.basic;

import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * excel工具类
 *
 * @author hujiping
 * @date 2023/4/14 3:35 PM
 */
@Component
public class ExcelUtils {

    public static final String EXCEL_SUFFIX_XLS = "xls";
    public static final String EXCEL_SUFFIX_XLSX = "xlsx";
    
    private static String fileTmpPath;

    @Value("${dms.tmpFile.path:}")
    public void setFileTmpPath(String fileTmpPath) {
        ExcelUtils.fileTmpPath = fileTmpPath;
    }

    /**
     * 读取excel第一个sheet页内容
     *
     * @param inputStream   文件流
     * @param xlsOrXlsx     excel类型
     * @param cls           生成对象class
     * @param fieldNameList 对象属性名（顺序与excel顺序一致）
     */
    public static <T> List<T> readFromExcel(InputStream inputStream, String xlsOrXlsx, Class<T> cls, List<String> fieldNameList) throws Exception {
        List<T> list = Lists.newArrayList();
        try {
            // 解析excel
            Workbook book;
            Sheet sheet = null;
            if (Objects.equals(xlsOrXlsx, EXCEL_SUFFIX_XLS)) {
                // 解析excel
                POIFSFileSystem pSystem = new POIFSFileSystem(inputStream);
                // 获取整个excel
                book = new HSSFWorkbook(pSystem);
                //获取第一个表单sheet
                sheet = book.getSheetAt(0);
            }else 
            if (Objects.equals(xlsOrXlsx, EXCEL_SUFFIX_XLSX)) {
                // 直接通过流获取整个excel
                book = new XSSFWorkbook(inputStream);
                // 获取第一个表单sheet
                sheet = book.getSheetAt(0);
            }
            if (sheet != null) {
                // 获取第一行
                int firstRow = sheet.getFirstRowNum();
                // 获取最后一行
                int lastRow = sheet.getLastRowNum();
                // 循环行数依次获取列数
                for (int i = firstRow + 1; i < lastRow + 1; i++) {
                    // 获取第 i 行
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        T t = cls.newInstance();
                        // 获取此行的第一列
                        int firstCell = 0;
                        /*
                         *获取此行的存在数据的第一列
                         * int firstCell = row.getFirstCellNum();
                         * */
                        // 获取此行的存在数据的最后一列
                        int lastCell = row.getLastCellNum();
                        for (int j = firstCell; j < lastCell; j++) {
                            setValue(t, cls, row, j, fieldNameList);
                        }
                        list.add(t);
                    }
                }
            }
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return list;
    }
    
    /**
     * 将数据写入Excel
     *
     * @param title     表头
     * @param data      数据内容
     * @param sheetName sheet名
     */
    public static Workbook writeExcel(List<String> title, List<List<String>> data, String sheetName, String excelSuffix) {
        Workbook workbook;
        switch (excelSuffix) {
            case EXCEL_SUFFIX_XLS:
                workbook = new HSSFWorkbook();
                break;
            case EXCEL_SUFFIX_XLSX:
                workbook = new XSSFWorkbook();
                break;
            default:
                return null;
        }
        // 在workbook中创建一个sheet对应excel中的sheet
        Sheet sheet = workbook.createSheet(sheetName);
        // 在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
        Row row = sheet.createRow(0);
        // 创建单元格，设置表头
        int titleSize = title.size();
        for (int i = 0; i < titleSize; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(title.get(i));
        }
        // 写入数据
        for (int i = 0; i < data.size(); i++) {
            Row row1 = sheet.createRow(i + 1);
            List<String> rowData = data.get(i);
            // 创建单元格设值
            for (int i1 = 0; i1 < rowData.size(); i1++) {
                row1.createCell(i1).setCellValue(rowData.get(i1));
            }
        }
        return workbook;
    }

    /**
     * 根据excel生成本地临时文件（临时文件注意删除）
     * 
     * @param fileName
     * @param workbook
     * @return
     * @throws IOException
     */
    public static File generateFileWithExcel(String fileName, Workbook workbook) throws IOException {
        File folder = new File(fileTmpPath);
        if(!folder.exists()){
            folder.setWritable(true, false);
            folder.mkdirs();
        }
        File file = new File(fileTmpPath + "/" + fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream os = null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
            workbook.write(os);
        } finally {
            if(os != null){
                os.close();
            }
        }
        return file;
    }

    /**
     * 文件转换为byte数组
     * 
     * @param file
     * @return
     */
    public static byte[] getBytesByFile(File file) throws Exception{
        FileInputStream fis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        try {
            fis = new FileInputStream(file);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            return bos.toByteArray();
        } finally {
            if (fis != null){
                fis.close();
            }
            bos.close();
        }
    }

    public static void deleteFile(File file) {
        try {
            if(file != null){
                file.delete();
            }
        } catch (Exception e) {
            System.out.println();;
        }
    }

    private static <T> void setValue(T t, Class<T> cls, Row row, int j, List<String> fieldNameList) throws Exception {
        Object cellContent = null;
        // 拿到单元格类型
        Cell cell = row.getCell(j);
        int cellType = cell.getCellType();
        switch (cellType) {
            // 字符串类型
            case Cell.CELL_TYPE_STRING:
                cellContent = cell.getStringCellValue();
                break;
            // 布尔类型
            case Cell.CELL_TYPE_BOOLEAN:
                cellContent = cell.getBooleanCellValue();
                break;
            // 数值类型
            case Cell.CELL_TYPE_NUMERIC:
                cellContent = cell.getNumericCellValue();
                break;
            // 取空串
            default:
                cellContent = "";
                break;
        }
        String currentFieldName = fieldNameList.get(j);
        // 获取该类的成员变量
        Field f = cls.getDeclaredField(currentFieldName);
        // 取消语言访问检查
        f.setAccessible(true);
        // 给变量赋值
        f.set(t, cellContent);
    }
}
