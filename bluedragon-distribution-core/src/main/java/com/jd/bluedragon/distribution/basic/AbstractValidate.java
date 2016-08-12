package com.jd.bluedragon.distribution.basic;

import org.apache.poi.ss.usermodel.Row;

public abstract class AbstractValidate {

	protected String getPositionMsg(MetaData metaData) {
		Row row = ExcelContext.getCurrentRow();
		return this.getPositionMsg(row, metaData);
	}

	protected String getPositionMsg(Row row, MetaData metaData) {
		String msg = "";
		if (row != null) {
			msg = "[第" + this.getColumnNumber(metaData) + "列,第"
					+ this.getRowNumber(row) + "行]";
		}
		return msg;
	}

	private int getColumnNumber(MetaData metaData) {
		return metaData.getColumn() + 1;
	}

	private int getRowNumber(Row row) {
		return row.getRowNum() + 1;
	}

}
