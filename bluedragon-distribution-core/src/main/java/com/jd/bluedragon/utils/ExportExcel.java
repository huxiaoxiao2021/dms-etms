package com.jd.bluedragon.utils;

import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jinjiaoyang
 * Date: 14-8-28
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
public class ExportExcel {

    private static final Logger log = LoggerFactory.getLogger(ExportExcel.class);

    private static String PARAM_TITLE = "文件标题不能为空";
    private static String PARAM_FIELDNAMES = "列表列名不能为空";
    private static String PARAM_LIST = "列表不能为空";
    private static String MAX_NUM_INFO = "数据量太大，不能导出";
    private static int MAX_NUM = 80000;
    private static int PAGE_NUM = 50000;
    public static String EXPORT_TYPE_EXCEL="1";
    public static String EXPORT_TYPE_CSV="2";


    /**
     * 导出EXCEL公共方法 导出带标题的空文件
     */
    public static <T> HSSFWorkbook exportExcel(String fileName, String[] fieldNames) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(fileName);
        sheet.setDefaultColumnWidth(20);
        // 设置报表标题
        createColumHeader(workbook, sheet, fieldNames);
        return workbook;
    }

    
    /**
     * 导出EXCEL公共方法 导出带数据的文件
     */
    public static void  exportFile(HttpServletResponse response, String fileName, String[] titles, String[] properties, List<T> list, String type) throws Exception {
    	if(type.contains(EXPORT_TYPE_EXCEL)){//excel导出
    		setResponseParam(response, fileName);
    		HSSFWorkbook workbook = null;
        	if(list.size()==0){
        		workbook = exportExcel(fileName,titles);
        	}else{
        		workbook = exportExcel( fileName, titles,  properties,list);
        	}
        	workbook.write(response.getOutputStream());
    	}else if(type.contains(EXPORT_TYPE_CSV)){
            FileInputStream fis = null;
            try{
                CsvExportUtil.setResponseParam(response, fileName);
                File  tmpFile = File.createTempFile("so-export-template", ".csv");
                CsvExportUtil.createCSVFile(tmpFile, properties,titles, list);
                fis = new FileInputStream(tmpFile);
                CsvExportUtil.writeResponse(fis, response.getOutputStream());
            }catch (Exception e){
                log.error("导出CSV文件失败：{}" ,fileName, e);
            }finally {
                try{
                    if(fis != null){
                        fis.close();
                    }
                }catch (Exception e1){
                    log.error("生成CSV时输入流关闭失败:{}" ,fileName , e1);
                }
            }
        }else{ //
    		//excel导出
    		setResponseParam(response, fileName);
    		HSSFWorkbook workbook = null;
        	if(list.size()==0){
        		workbook = exportExcel(fileName,titles);
        	}else{
        		workbook = exportExcel( fileName, titles,  properties,list);
        	}
        	workbook.write(response.getOutputStream());
    	
    	}
    }
    /**
     * 导出EXCEL公共方法 导出带数据的文件
     */
    public static <T> HSSFWorkbook exportExcel(String fileName, String[] fieldNames, String[] fieldPreNames, List<T> list) throws Exception {
        // 1.校验传入参数是否正确
        if (null == fileName || fileName.equals("")) {
            throw new Exception(PARAM_TITLE);
        }
        if (null == fieldNames || fieldNames.length == 0) {
            throw new Exception(PARAM_FIELDNAMES);
        }
        if (null == list || list.size() == 0) {
            throw new Exception(PARAM_LIST);
        }
        if (list.size() > MAX_NUM){
            throw new Exception(MAX_NUM_INFO);
        }

        HSSFWorkbook workbook = new HSSFWorkbook();
        
        int size = (list.size()-1)/PAGE_NUM+1;
        for (int i = 0; i < size; i++) {
        	List<T> subList = list.subList(PAGE_NUM*i, PAGE_NUM*(i+1)>list.size()?list.size():PAGE_NUM*(i+1));
        	if(i>=1){
        		fileName = fileName+i;
        	}
        	 HSSFSheet sheet = workbook.createSheet(fileName);
             sheet.setDefaultColumnWidth(20);
             // 设置报表标题
             createColumHeader(workbook, sheet, fieldNames);

             try {
                 //实体类处理
                 Field[] columnFields = createColumnFileds(fieldPreNames, subList.get(0).getClass());
                 //创建单元格
                
                 int rowCount = 1;
                 
                 HSSFCellStyle style = workbook.createCellStyle();
                 HSSFFont font = workbook.createFont();
                 font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                 style.setFont(font);  
                 style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                 style.setBorderTop(HSSFCellStyle.BORDER_THIN);
                 style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                 style.setBorderRight(HSSFCellStyle.BORDER_THIN);
                 HSSFRow row = null;
                 for (T t : subList) {
                     row = sheet.createRow(rowCount);
                     createCell( row, style, columnFields, t);
                     rowCount++;
                 }
             } catch (Exception e) {
                 log.error("导出excel创建单元格是出现异常",e);
             }
		}
       
        return workbook;
    }

    /**
     * 设置列头
     * @param workbook
     * @param sheet
     * @param fieldNames
     */
    private static void createColumHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] fieldNames) {
        // 设置列头
        HSSFRow row = sheet.createRow(0);
        // 指定行高
        row.setHeight((short) 400);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        // 指定单元格居中对齐
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 指定单元格垂直居中对齐
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 指定单元格自动换行
        cellStyle.setWrapText(true);

        // 单元格字体
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short) 200);
        cellStyle.setFont(font);
        // 设置单元格的边框为粗体
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        // 设置单元格的边框颜色
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);

        // 设置单元格背景色
        //cellStyle.setFillForegroundColor(HSSFColor.RED.index);
        //cellStyle.setFillPattern(HSSFCellStyle.NO_FILL);

        HSSFCell cell = null;

        for (int i = 0; i < fieldNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellType(HSSFCell.ENCODING_UTF_16);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(new HSSFRichTextString(fieldNames[i]));
        }

    }

    /**
     * 实体类处理
     * @param <T>
     * @param fieldNames
     * @param fieldClass
     * @return
     * @throws NoSuchFieldException
     */
    private static <T> Field[] createColumnFileds(String[] fieldNames, Class<T> fieldClass) throws NoSuchFieldException {
        Field[] fields = new Field[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            Field field = fieldClass.getDeclaredField(fieldNames[i]);
            field.setAccessible(true);
            fields[i] = field;
        }
        return fields;
    }

    /**
     * 创建单元格
     * @param <T>
     * @param row
     * @param style
     * @param columnFields
     * @param t
     */
    private static  <T> void createCell(HSSFRow row, HSSFCellStyle style, Field[] columnFields, T t) {
    	 PropertyDescriptor pd = null;
    	 Method getMethod =  null;
    	 HSSFCell cell = null;
    	 Object field;
    	for (int i = 0; i < columnFields.length; i++) {
            cell = row.createCell(i);
            try {
            	pd =  new PropertyDescriptor(columnFields[i].getName(),  
                         t.getClass()); 
            	  getMethod = pd.getReadMethod();//获得get方法  
            	   field = getMethod.invoke(t);//
               
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                
                //判断值是否为空
                if(null == field){
                    cell.setCellValue("");
                }else if (field instanceof Date){
                    cell.setCellValue((Date) field);
                }else {
                    cell.setCellValue((String) (field+""));
                }
                cell.setCellStyle(style);
            } catch (Exception e) {
                log.error("导出excel创建单元格是出现异常",e);
            }
        }
    }

    /**
     * 导出设置参数
     * @param response
     * @param fileName
     */
    public static void setResponseParam(HttpServletResponse response, String fileName) {
        response.setContentType("application/msexcel;charset=GBK");
        fileName = fileName + ".xls";
        try {
            response.setHeader("Content-Disposition", "attachment;filename=".concat(new String(fileName.getBytes("GBK"),"iso8859-1")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Connection", "close");
        response.setHeader("Content-Type", "application/vnd.ms-excel");
    }
public static void main(String[] args) {
	System.out.println((200000-1)/PAGE_NUM+1);
}

}
