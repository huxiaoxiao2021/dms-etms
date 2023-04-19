package com.jd.bluedragon.distribution.basic;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

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
}
