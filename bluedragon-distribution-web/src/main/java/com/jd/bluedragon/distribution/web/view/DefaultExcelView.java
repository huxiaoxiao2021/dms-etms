package com.jd.bluedragon.distribution.web.view;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * 默认的Excel视图
 * @author suihonghua
 *
 */
public class DefaultExcelView extends AbstractExcelView {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultExcelView.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		try {
			String filename = (String) map.get("filename");
			if (filename == null || filename.trim().length() == 0) {
				filename = System.currentTimeMillis() + ".xls";
			}
			response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));// 设定输出文件头
			this.buildExcelDocument(map, workbook);
		} catch (Exception e) {
			log.error("buildExcelDocument-error:", e);
			throw e;
		} 
	}
	
	/**
	 * 构建Excel文本
	 * @param map
	 * @param workbook
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void buildExcelDocument(Map<String, Object> map, HSSFWorkbook workbook) throws Exception {
		String sheetname = (String) map.get("sheetname");
		
		if (sheetname == null || sheetname.trim().length() == 0) {
			sheetname = "Sheet1";
		}
		
		List<List<Object>> contents = (List<List<Object>>) map.get("contents");
		if (contents == null) {
			throw new RuntimeException("DefaultExcelView Attribute[contents] in Model can't be null! ");
		}

		ExcelWriter exporter = new ExcelWriter();
		exporter.setWorkbook(workbook);
		exporter.writeSheet(sheetname, contents);
	}
}
