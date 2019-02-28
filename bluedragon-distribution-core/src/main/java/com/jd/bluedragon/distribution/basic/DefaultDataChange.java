package com.jd.bluedragon.distribution.basic;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

public class DefaultDataChange implements DataChange {

	private final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	@Override
	public Object getValue(Field field, Cell cell, MetaData metaData)
			throws Exception {
		Object ret = null;
		if (cell != null) {
			String val = this.getCellValue(cell);
			int columnIndex = cell.getColumnIndex();
			if (field.getType() == String.class) {
				if(val != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
					ret = val.replaceAll("^(\\d+)\\.\\d+$", "$1");
				}else{
					ret = val;
				}
			} else if (field.getType() == Date.class) {
				if(val != null && val.matches("^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}$")){
					ret = DATEFORMAT.parse(val);
				}
			} else if (this.isLong(field)) {
				ret = val == null?0l:this.getDouble(val, field, columnIndex).longValue();
			} else if (this.isInteger(field)) {
				ret = val == null?0:this.getDouble(val, field, columnIndex).intValue();
			} else if (this.isDouble(field)) {
				ret = val == null?0d:this.getDouble(val, field, columnIndex);
			} else if (this.isFloat(field)) {
				ret = val == null?0f : this.getDouble(val, field, columnIndex).floatValue();
			} else if (field.getType() == Boolean.class) {
				ret = val == null?false : Boolean.valueOf(val);
			} else if (field.getType() == BigDecimal.class) {
				ret = val == null?false : this.getBigDecimal(val, field, columnIndex);
			} else {
				throw new Exception(field.getName() + "没有找到匹配的类型"
						+ field.getType());
			}
		}
		return ret;
	}
	
	private Double getDouble(String val, Field field, int columnIndex){
		Double t = null;
		Row row = ExcelContext.getCurrentRow();
		if(val == null || "".equals(val.trim())){
			throw new IllegalArgumentException("第" + row.getRowNum() + "行,第" + (columnIndex+1) + "列," + field.getName() + "字段为空");
		}
		if(val.trim().matches("^(-|)\\d+(\\.\\d+|)$")){
			t = Double.valueOf(val.trim());
		}else{
			throw new IllegalArgumentException("第" + row.getRowNum() + "行,第" + (columnIndex+1) + "列," + field.getName() + "字段类型不匹配，应为数字类型");
		}
		return t;
	}


	private BigDecimal getBigDecimal(String val, Field field, int columnIndex){
		BigDecimal t = null;
		Row row = ExcelContext.getCurrentRow();
		if(val == null || "".equals(val.trim())){
			throw new IllegalArgumentException("第" + row.getRowNum() + "行,第" + (columnIndex+1) + "列," + field.getName() + "字段为空");
		}

		if(val.trim().matches("^\\d{1,10}(\\.\\d{2})?$")){
			if(val.indexOf('.') == -1){
				val = val+".00";
			}
			t = new BigDecimal(val);

		}else{
			throw new IllegalArgumentException("第" + row.getRowNum() + "行,第" + (columnIndex+1) + "列," + field.getName() + "字段类型不匹配，应为数字类型,小数点后保留两位且不得大于9999999999.99");
		}
		return t;
	}

	protected String getCellValue(Cell cell) {
		return this.getCellValue(cell, null);
	}
	
	private String getCellValue(Cell cell, String defValue) {
		String ret = null;
		if(cell != null){
			if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
				ret = String.valueOf(cell.getBooleanCellValue());
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(cell)) {
					ret = DATEFORMAT.format(cell.getDateCellValue());
				} else {
					ret = String.valueOf(cell.getNumericCellValue());
				}
			} else {
				ret =  String.valueOf(cell.getStringCellValue());
			}
		}
		if(ret == null || ret.trim().equals("")){
			ret = defValue;
		}
		return ret;
	}

	private boolean isInteger(Field field) {
		boolean ret = false;
		if (field.getType() == Integer.class) {
			ret = true;
		} else if (int.class.equals(field.getType())) {
			ret = true;
		}
		return ret;
	}

	private boolean isLong(Field field) {
		boolean ret = false;
		if (field.getType() == Long.class) {
			ret = true;
		} else if (long.class.equals(field.getType())) {
			ret = true;
		}
		return ret;
	}

	private boolean isDouble(Field field) {
		boolean ret = false;
		if (field.getType() == Double.class) {
			ret = true;
		} else if (double.class.equals(field.getType())) {
			ret = true;
		}
		return ret;
	}

	private boolean isFloat(Field field) {
		boolean ret = false;
		if (field.getType() == Float.class) {
			ret = true;
		} else if (float.class.equals(field.getType())) {
			ret = true;
		}
		return ret;
	}


}

