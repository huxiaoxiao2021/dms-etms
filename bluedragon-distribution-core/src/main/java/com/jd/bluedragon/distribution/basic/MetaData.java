package com.jd.bluedragon.distribution.basic;
import java.util.List;

public class MetaData {
	
	public static final String TYPE_DATE = "date";
	public static final String TYPE_INTEGER = "int";
	private int column;
	private String fieldName;
	private List<CellValidate> validates;
	
	private List<String> validateNames;
	private String format;
	private String type;
	private DataChange dataChange;
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public List<CellValidate> getValidates() {
		return validates;
	}
	public void setValidates(List<CellValidate> validates) {
		this.validates = validates;
	}
	public DataChange getDataChange() {
		return dataChange;
	}
	public void setDataChange(DataChange dataChange) {
		this.dataChange = dataChange;
	}
	public List<String> getValidateNames() {
		return validateNames;
	}
	public void setValidateNames(List<String> validateNames) {
		this.validateNames = validateNames;
	}

	
}
