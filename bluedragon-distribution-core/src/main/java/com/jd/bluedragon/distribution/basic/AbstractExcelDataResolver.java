package com.jd.bluedragon.distribution.basic;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public abstract class AbstractExcelDataResolver implements DataResolver{

	private static final Logger logger = Logger
			.getLogger(AbstractExcelDataResolver.class);
	
	@Override
	public <T> List<T> resolver(InputStream in, Class<T> cls, MetaDataFactory metaDataFactory) throws Exception {
		List<T> ret = new ArrayList<T>();
		long startTime = System.currentTimeMillis();
		Workbook workbook = this.createWorkbook(in);
		int sheets = workbook.getNumberOfSheets();
		logger.debug("workbook.getNumberOfSheets()=" + sheets);

		if (sheets <= 0) {
			throw new IllegalArgumentException("excel没有sheet");
		}
		Sheet sheet = workbook.getSheetAt(0);
		logger.debug("sheet.getLastRowNum()=" + sheet.getLastRowNum());
		ExcelConfig excelConfig = metaDataFactory.getExcelConfig();
		if(excelConfig == null){
			throw new IllegalArgumentException("ExcelConfig对象为Null");
		}
		RowFilter rowFilter = excelConfig.getRowFilter();
		if (null == sheet || sheet.getLastRowNum() > excelConfig.getMaxNumber() + 1
				|| sheet.getLastRowNum() <= 0) {
			return null;
		}
		int cellNum = sheet.getRow(0).getLastCellNum();
		Map<String, MetaData> metaMap = metaDataFactory.getMetaDataMap();
		
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			ExcelContext.setCurrentRow(row);
			T t = this.newObject(cls);
			for (int j = 0; j < cellNum; j++) {
				Cell cell = row.getCell(j);
				MetaData metaData = metaMap.get(String.valueOf(j));
				if(metaData != null){
					this.setObjectValue(t, cell, metaData);
				}else{
					throw new IllegalArgumentException("[导入excel列数与预设规则不一致],column:" + j);
				}
				
			}
			if(rowFilter != null){
				if(!rowFilter.isFilter(t)){
					ret.add(t);
				}
			}else{
				ret.add(t);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("解析excel耗时:" + (endTime - startTime));
		return ret;
	}

	protected void setObjectValue(Object t, Cell cell, MetaData metaData) throws Exception {
		Field field = this.getField(t, metaData.getFieldName());
		if(field != null){
			Object value = null;
			if(cell != null){
				value = metaData.getDataChange().getValue(field, cell, metaData);
			}
			this.validate(value, metaData);
			this.setValue(field, t, value);
		}else{
			throw new IllegalArgumentException("没有找到属性" + metaData.getFieldName());
		}
	}
	
	protected void validate(Object value, MetaData metaData){
		List<CellValidate> validates = metaData.getValidates();	
		if(validates != null){
			for(CellValidate cellValidate:validates){
				String msg = cellValidate.validate(value, metaData) ;
				if(msg != null && !"".equals(msg)){
					throw new IllegalArgumentException("验证错误：" + msg);
				}
			}
		}
	}
	
	private void setValue(Field field, Object t, Object value){
		try {
			field.set(t, value);
		} catch (IllegalArgumentException e) {
			logger.error("设置Field错误", e);
			throw new IllegalArgumentException("设置Field错误", e);
		} catch (IllegalAccessException e) {
			logger.error("设置Field错误", e);
			throw new IllegalArgumentException("设置Field错误", e);
		}
	}
	
	private Field getField(Object t, String fieldName){
		Field ret = null; 
		try {
			Class<?> cls = t.getClass();
			while(!"java.lang.Object".equals(cls.getName())){
				Field[] fields = cls.getDeclaredFields();
				for(Field field:fields){
					if(field.getName().equals(fieldName)){
						field.setAccessible(true);
						ret = field;
						break;
					}
				}
				cls = cls.getSuperclass();
			}
		} catch (SecurityException e) {
			logger.error("获得Field错误", e);
			throw new IllegalArgumentException("获得Field错误", e);
		}
		return ret;
	}

	protected <T> T newObject(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("实例化" + cls.getSimpleName() + "错误", e);
			throw new IllegalArgumentException("实例化" + cls.getSimpleName() + "错误", e);
		} catch (IllegalAccessException e) {
			logger.error("不合法的访问" + cls.getSimpleName() + "错误", e);
			throw new IllegalArgumentException("实例化" + cls.getSimpleName() + "错误", e);
		}
	}

	public abstract Workbook createWorkbook(InputStream in) throws IOException;
}
