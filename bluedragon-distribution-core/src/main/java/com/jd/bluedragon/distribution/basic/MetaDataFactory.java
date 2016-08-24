package com.jd.bluedragon.distribution.basic;

import java.io.IOException;
import java.util.Map;

public interface MetaDataFactory {

	public Map<String, MetaData> getMetaDataMap() throws IOException;
	
	public ExcelConfig getExcelConfig();
}

