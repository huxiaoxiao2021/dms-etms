package com.jd.bluedragon.distribution.basic;

public interface ExcelConfig {

	/**
	 * 允许解析的最大记录数
	 * @return
	 */
	public int getMaxNumber();
	
	public RowFilter getRowFilter();
	
}
