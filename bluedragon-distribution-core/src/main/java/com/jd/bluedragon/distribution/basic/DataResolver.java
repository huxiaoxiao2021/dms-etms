package com.jd.bluedragon.distribution.basic;

import java.io.InputStream;
import java.util.List;

public interface DataResolver {

	public <T> List<T> resolver(InputStream in, Class<T> cls, MetaDataFactory metaDataFactory) throws Exception;
	
}
