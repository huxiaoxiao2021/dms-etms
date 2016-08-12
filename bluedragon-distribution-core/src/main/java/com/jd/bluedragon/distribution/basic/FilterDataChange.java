package com.jd.bluedragon.distribution.basic;

import java.lang.reflect.Field;
import org.apache.poi.ss.usermodel.Cell;

public class FilterDataChange extends DefaultDataChange {

	private DataChange dataChange;

	public FilterDataChange() {
		super();
	}

	public FilterDataChange(DataChange dataChange) {
		this();
		this.dataChange = dataChange;
	}

	@Override
	public Object getValue(Field field, Cell cell, MetaData metaData)
			throws Exception {
		Object rs = null;
		String val = this.getCellValue(cell);
		if (ExcelStringUtils.isNotNull(val) && dataChange != null) {
			rs = this.dataChange.getValue(field, cell, metaData);
		}
		return rs;
	}

}
