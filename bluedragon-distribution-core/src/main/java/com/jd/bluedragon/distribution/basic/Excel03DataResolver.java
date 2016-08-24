package com.jd.bluedragon.distribution.basic;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

class Excel03DataResolver extends AbstractExcelDataResolver {

	@Override
	public Workbook createWorkbook(InputStream in) throws IOException {
		return new HSSFWorkbook(in);
	}
}
