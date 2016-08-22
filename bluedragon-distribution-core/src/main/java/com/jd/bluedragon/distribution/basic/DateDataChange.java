package com.jd.bluedragon.distribution.basic;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

public class DateDataChange implements DataChange{

	private String format = null;
	private SimpleDateFormat fateFormat = null;
	
	public DateDataChange(String format){
		this.format = format;
		this.fateFormat = new SimpleDateFormat(this.format);
	}
	
	@Override
	public Object getValue(Field field, Cell cell, MetaData metaData) throws Exception {
		String source = cell.getStringCellValue();
		Date date = this.fateFormat.parse(source);
		return date;
	}

}

