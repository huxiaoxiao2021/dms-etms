package com.jd.bluedragon.distribution.basic;

import java.lang.reflect.Field;
import org.apache.poi.ss.usermodel.Cell;

public class IntegerDataChange extends DefaultDataChange {

	@Override
	public Object getValue(Field field, Cell cell, MetaData metaData)
			throws Exception {
		String val = this.getCellValue(cell);
		Object obj = null;
		if (val.matches("^(-|)\\d+$")) {
			obj = Integer.valueOf(val);
		} else {
			obj = val;
		}
		return obj;
	}
}