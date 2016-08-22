package com.jd.bluedragon.distribution.basic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class PropertiesMetaDataFactory implements MetaDataFactory {

	private String path = null;
	private static ObjectMapper mapper;
	protected static Map<String, CellValidate> validateMap = new HashMap<String,CellValidate>();
	public static final int ROW_NUMBER_LINE = 1000;
	
	static {
		mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
	
		validateMap.put("require", new RequireCellValidate());
		validateMap.put("int", new IntegerCellValidate());
	}


	@Override
	public ExcelConfig getExcelConfig() {
		// TODO Auto-generated method stub
		return new ExcelConfig(){
			@Override
			public int getMaxNumber() {
				// TODO Auto-generated method stub
				return ROW_NUMBER_LINE;
			}

			@Override
			public RowFilter getRowFilter() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public PropertiesMetaDataFactory(String path) {
		if(path.startsWith("/") || path.startsWith("\\")){
			path = path.substring(1, path.length());
		}
		this.path = path;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, MetaData> getMetaDataMap() throws IOException {
		Properties pro = new Properties();
		pro.load(PropertiesMetaDataFactory.class.getResourceAsStream("/" + this.path));
		Map<String, MetaData> map = new HashMap<String, MetaData>();
		Enumeration en = pro.keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String json = pro.getProperty(key);
			if (json == null) {
				throw new IllegalArgumentException("属性key:" + key + ",没有对应的属性");
			}
			MetaData metaData = mapper.readValue(json, MetaData.class);
			metaData.setColumn(Integer.valueOf(key));
			this.initMetaData(metaData);
			map.put(key, metaData);
			
		}
		return map;
	}
	
	protected void initMetaData(MetaData metaData){
		this.initDataChange(metaData);
		this.initValidate(metaData);
	}
	
	protected void initDataChange(MetaData metaData){
		DataChange dataChange = null;
		if(MetaData.TYPE_DATE.equals(metaData.getType())){
			dataChange = new DateDataChange(metaData.getFormat());
		}else if(MetaData.TYPE_INTEGER.equals(metaData.getType())){
			dataChange = new FilterDataChange(new IntegerDataChange());
			addValidateNames(metaData, MetaData.TYPE_INTEGER);
		}else{
			dataChange = new DefaultDataChange();
		}
		metaData.setDataChange(dataChange);
	}

	protected void initValidate(MetaData metaData){
		List<String> validateNames = metaData.getValidateNames();
		if(validateNames != null){
			List<CellValidate> validates = new ArrayList<CellValidate>();
			for(String validateName:validateNames){
				if(validateMap.containsKey(validateName)){
					CellValidate cellValidate = validateMap.get(validateName);
					validates.add(cellValidate);
				}else{
					throw new IllegalArgumentException("没有找到validate:" + validateName + ",请在PropertiesMetaFactory里注册");
				}
			}
			metaData.setValidates(validates);
		}
	}
	
	protected void addValidateNames(MetaData metaData, String type){
		List<String> list = null;
		if(metaData.getValidateNames() == null){
			list = new ArrayList<String>();
			metaData.setValidateNames(list);
		}else{
			list = metaData.getValidateNames();
		}
		if(!list.contains(type)){
			list.add(type);
		}
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}

