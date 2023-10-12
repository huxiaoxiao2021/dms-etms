package com.jd.bluedragon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ducc.DuccHystrixRoutePropertyConfig;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;

@Service("dmsConfigManager")
public class DmsConfigManager {
	
	@Autowired
	@Qualifier("duccPropertyConfig")
	private DuccPropertyConfig duccPropertyConfig;
	
	@Autowired
	@Qualifier("duccHystrixRoutePropertyConfig")
	private DuccHystrixRoutePropertyConfig duccHystrixRoutePropertyConfig;
	
	public DuccPropertyConfig getDuccPropertyConfig() {
		return duccPropertyConfig;
	}
	public DuccPropertyConfig getPropertyConfig() {
		return duccPropertyConfig;
	}
	public DuccHystrixRoutePropertyConfig getDuccHystrixRoutePropertyConfig() {
		return duccHystrixRoutePropertyConfig;
	}
}