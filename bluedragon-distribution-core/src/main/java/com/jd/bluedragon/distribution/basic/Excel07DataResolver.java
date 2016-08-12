package com.jd.bluedragon.distribution.basic;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class Excel07DataResolver extends AbstractExcelDataResolver{

	@Override
	public Workbook createWorkbook(InputStream in) throws IOException {
		return new XSSFWorkbook(in);
	}

}
