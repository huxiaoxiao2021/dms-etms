package com.jd.bluedragon.configuration;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ucc.HystrixRouteUccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;

@Service("dmsConfigManager")
public class DmsConfigManager {
	
	private static final Logger log = LoggerFactory.getLogger(DmsConfigManager.class);
	
	@Autowired
	@Qualifier("uccPropertyConfiguration")
	private UccPropertyConfiguration uccPropertyConfiguration;
	
	@Autowired
	@Qualifier("hystrixRouteUccPropertyConfiguration")
	private HystrixRouteUccPropertyConfiguration hystrixRouteUccPropertyConfiguration;

	public UccPropertyConfiguration getPropertyConfig() {
		return uccPropertyConfiguration;
	}

	public UccPropertyConfiguration getUccPropertyConfiguration() {
		return uccPropertyConfiguration;
	}
	public HystrixRouteUccPropertyConfiguration getHystrixRoutePropertyConfiguration() {
		return hystrixRouteUccPropertyConfiguration;
	}
    private boolean isSameValue(Object o1,Object o2) {
    	if(o1 == o2) {
    		return true;
    	}
    	if(o1 == null && o2== null) {
    		return true;
    	}
    	if(o1 == null || o2== null) {
    		return false;
    	}
    	if(o1.equals(o2)) {
    		return true;
    	}
    	return JsonHelper.toJson(o1).equals(JsonHelper.toJson(o2));
    }
}