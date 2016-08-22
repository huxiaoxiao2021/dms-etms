package com.jd.bluedragon.distribution.basic;

import java.lang.reflect.Field;

import org.apache.poi.ss.usermodel.Cell;

public interface DataChange {
	public Object getValue(Field field, Cell cell, MetaData metaData)
			throws Exception;
}
