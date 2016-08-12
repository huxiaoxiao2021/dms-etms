package com.jd.bluedragon.distribution.web.view;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel 导出实现
 * 
 * @author suihonghua
 * 
 */
public class ExcelWriter {

	private OutputStream outputStream;

	private Workbook workbook = new HSSFWorkbook();

	public ExcelWriter() {

	}

	public ExcelWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void writeSheet(String sheetName, List<List<Object>> contents) throws Exception {
		Sheet sheet = workbook.createSheet(sheetName);
		this.writeSheet(sheet, contents);
	}

	/**
	 * 写入工作簿一sheet
	 * 
	 * @author suihonghua
	 * @param sheet
	 * @param content
	 * @throws Exception
	 */
	protected void writeSheet(Sheet sheet, List<List<Object>> contents) throws Exception {
		int contentsSize = contents.size();
		for (int i = 0; i < contentsSize; i++) {
			List<Object> content = contents.get(i);
			Row c_row = sheet.createRow(i);
			this.writeRow(c_row, content);
		}
	}

	/**
	 * 写入工作簿一行数据
	 * 
	 * @author suihonghua
	 * @param row
	 * @param content
	 * @throws Exception
	 */
	protected void writeRow(Row row, List<Object> content) throws Exception {
		int contentSize = content.size();
		for (int i = 0; i < contentSize; i++) {
			Cell cell = row.createCell(i);
			Object val = content.get(i) == null ? "" : content.get(i);
			this.writeCell(cell, val);
		}
	}

	/**
	 * 写入工作簿单元格数据
	 * 
	 * @author suihonghua
	 * @param cell
	 * @param val
	 * @throws Exception
	 */
	protected void writeCell(Cell cell, Object val) throws Exception {
		cell.setCellValue(String.valueOf(val));
	}

	public void flush() throws IOException {
		workbook.write(this.getOutputStream());
	}

	public void close() throws IOException {
		if (outputStream != null) {
			outputStream.close();
		}
	}

	/**
	 * 获得Workbook对象
	 * 
	 * @author suihonghua
	 * @return
	 */
	public Workbook getWorkbook() {
		return workbook;
	}

	/**
	 * 设置Workbook对象（默认会产生一个可用对象，非必设项）
	 * 
	 * @author suihonghua
	 * @param workbook
	 */
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	// ---------- getter and setter ----------//
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

}
