package com.jd.bluedragon.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @User：zhaohengchong
 * @E-mail: zhaohengchong@360buy.com
 * @Date：2011-10-27
 * @Time：上午09:54:17 
 * 类说明:使用POI导出数据
 */
public class ExportByPOIUtil {
	private final static Log log = LogFactory.getLog(ExportByPOIUtil.class);

	public final static int DEFAULT_ROW_HEIGHT = 0;
	
	private ExportByPOIUtil() {
	}


	/**
	 * 功能：将HSSFWorkbook写入Excel文件
	 * @param wb  HSSFWorkbook
	 * @param absPath 写入文件的相对路径
	 * @param wbName  文件名
	 */
	public static void writeWorkbook(HSSFWorkbook wb, String fileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fileName);
			wb.write(fos);
		} catch (FileNotFoundException e) {
			log.error(new StringBuffer("[").append(e.getMessage()).append("]")
					.append(e.getCause()));
		} catch (IOException e) {
			log.error(new StringBuffer("[").append(e.getMessage()).append("]")
					.append(e.getCause()));
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				log.error(new StringBuffer("[").append(e.getMessage()).append(
						"]").append(e.getCause()));
			}
		}
	}

	/**
	 * 
	 * 功能：创建HSSFSheet工作簿
	 * @param wb HSSFWorkbook
	 * @param sheetName String 工作薄名称
	 * @return HSSFSheet
	 */
	public static HSSFSheet createSheet(HSSFWorkbook wb, String sheetName) {
		HSSFSheet sheet = wb.createSheet(sheetName);
		return sheet;

	}

	/**
	 * 功能：创建HSSFRow
	 * @param sheet HSSFSheet
	 * @param rowNum int
	 * @param height int
	 * @return HSSFRow
	 */
	public static HSSFRow createRow(HSSFSheet sheet, int rowNum) {
		return createRow(sheet, rowNum, DEFAULT_ROW_HEIGHT);
	}
	
	/**
	 * 功能：创建HSSFRow
	 * @param sheet HSSFSheet
	 * @param rowNum int
	 * @param height int
	 * @return HSSFRow
	 */
	public static HSSFRow createRow(HSSFSheet sheet, int rowNum, int height) {
		HSSFRow row = sheet.createRow(rowNum);
		if (height != DEFAULT_ROW_HEIGHT) {
			row.setHeight((short) height);
		}
		return row;
	}

	/**
	 * 功能：创建CellStyle样式
	 * @param wb HSSFWorkbook
	 * @param backgroundColor 背景色
	 * @param foregroundColor 前置色
	 * @param font 字体
	 * @return CellStyle
	 */
	public static CellStyle createCellStyle(HSSFWorkbook wb,
			short backgroundColor, short foregroundColor, short halign,
			Font font) {
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(halign);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cs.setFillBackgroundColor(backgroundColor);
		cs.setFillForegroundColor(foregroundColor);
		cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cs.setFont(font);
		return cs;
	}
	
	/**
	 * 设置自定义颜色
	 * @param wb
	 * @param R
	 * @param B
	 * @param G
	 */
	public static void setMineColorAtIndex(HSSFWorkbook wb,short index,short R,short B,short G){
		 HSSFPalette palette = wb.getCustomPalette(); 
		 palette.setColorAtIndex((short)index, (byte) (R), (byte) (B), (byte) (G));
	}

	/**
	 * 功能：创建带边框的CellStyle样式
	 * @param wb HSSFWorkbook
	 * @param backgroundColor 背景色
	 * @param foregroundColor 前置色
	 * @param font 字体
	 * @return CellStyle
	 */
	public static CellStyle createBorderCellStyle(HSSFWorkbook wb,
			short backgroundColor, short foregroundColor, short halign,
			Font font) {
		CellStyle cs = wb.createCellStyle();
		cs.setAlignment(halign);
		cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cs.setFillBackgroundColor(backgroundColor);
		cs.setFillForegroundColor(foregroundColor);
		cs.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cs.setFont(font);
		cs.setBorderLeft(CellStyle.BORDER_DASHED);
		cs.setBorderRight(CellStyle.BORDER_DASHED);
		cs.setBorderTop(CellStyle.BORDER_DASHED);
		cs.setBorderBottom(CellStyle.BORDER_DASHED);
		return cs;
	}

	/**
	 * 功能：创建CELL
	 * @param row HSSFRow
	 * @param cellNum int
	 * @param style HSSFStyle
	 * @return HSSFCell
	 */
	public static HSSFCell createCell(HSSFRow row, int cellNum) {
		return createCell(row, cellNum, null);
	}
	
	/**
	 * 功能：创建CELL
	 * @param row HSSFRow
	 * @param cellNum int
	 * @param style HSSFStyle
	 * @return HSSFCell
	 */
	public static HSSFCell createCell(HSSFRow row, int cellNum, CellStyle style) {
		HSSFCell cell = row.createCell(cellNum);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		return cell;
	}

	/**
	 * 功能：合并单元格
	 * @param sheet HSSFSheet
	 * @param firstRow int
	 * @param lastRow int
	 * @param firstColumn int
	 * @param lastColumn int
	 * @return int 合并区域号码
	 */
	public static int mergeCell(HSSFSheet sheet, int firstRow, int lastRow,
			int firstColumn, int lastColumn) {
		return sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow,
				firstColumn, lastColumn));
	}

	/**
	 * 功能：创建字体
	 * @param wb HSSFWorkbook
	 * @param boldweight short
	 * @param color short
	 * @return Font
	 */
	public static Font createFont(HSSFWorkbook wb, short boldweight,
			short color, short size) {
		Font font = wb.createFont();
		font.setBoldweight(boldweight);
		font.setColor(color);
		font.setFontHeightInPoints(size);
		return font;
	}

	/**
	 * 设置合并单元格的边框样式
	 * @param sheet HSSFSheet
	 * @param ca CellRangAddress
	 * @param style CellStyle
	 */
	public static void setRegionStyle(HSSFSheet sheet, CellRangeAddress ca,
			CellStyle style) {
		for (int i = ca.getFirstRow(); i <= ca.getLastRow(); i++) {
			HSSFRow row = HSSFCellUtil.getRow(i, sheet);
			for (int j = ca.getFirstColumn(); j <= ca.getLastColumn(); j++) {
				HSSFCell cell = HSSFCellUtil.getCell(row, j);
				cell.setCellStyle(style);
			}
		}
	}

}
