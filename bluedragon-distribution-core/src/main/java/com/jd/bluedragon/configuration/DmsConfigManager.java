package com.jd.bluedragon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ducc.DuccHystrixRoutePropertyConfig;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;
import com.jd.bluedragon.configuration.ucc.HystrixRouteUccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;

@Service("dmsConfigManager")
public class DmsConfigManager {
	
	@Autowired
	@Qualifier("duccPropertyConfig")
	private DuccPropertyConfig duccPropertyConfig;
	
	@Autowired
	@Qualifier("uccPropertyConfiguration")
	private UccPropertyConfiguration uccPropertyConfiguration;
	
	@Autowired
	@Qualifier("duccHystrixRoutePropertyConfig")
	private DuccHystrixRoutePropertyConfig duccHystrixRoutePropertyConfig;
	
	@Autowired
	@Qualifier("hystrixRouteUccPropertyConfiguration")
	private HystrixRouteUccPropertyConfiguration hystrixRouteUccPropertyConfiguration;

	public DuccPropertyConfig getDuccPropertyConfig() {
		return duccPropertyConfig;
	}

	public UccPropertyConfiguration getUccPropertyConfiguration() {
		return uccPropertyConfiguration;
	}

	public DuccHystrixRoutePropertyConfig getDuccHystrixRoutePropertyConfig() {
		return duccHystrixRoutePropertyConfig;
	}

	public HystrixRouteUccPropertyConfiguration getHystrixRouteUccPropertyConfiguration() {
		return hystrixRouteUccPropertyConfiguration;
	}

}
