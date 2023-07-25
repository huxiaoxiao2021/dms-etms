package com.jd.bluedragon.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfiguration;
import com.jd.bluedragon.configuration.ducc.HystrixRouteDuccPropertyConfiguration;
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
	@Qualifier("duccPropertyConfiguration")
	private DuccPropertyConfiguration duccPropertyConfiguration;
	
	@Autowired
	@Qualifier("hystrixRouteDuccPropertyConfiguration")
	private HystrixRouteDuccPropertyConfiguration hystrixRouteDuccPropertyConfiguration;

	public UccPropertyConfiguration getUccPropertyConfiguration() {
		return uccPropertyConfiguration;
	}
	
	public DuccPropertyConfiguration getDuccPropertyConfiguration() {
		return duccPropertyConfiguration;
	}
	
	public DuccPropertyConfig getDuccPropertyConfig() {
		return duccPropertyConfig;
	}

	public HystrixRouteDuccPropertyConfiguration getHystrixRouteDuccPropertyConfiguration() {
		return hystrixRouteDuccPropertyConfiguration;
	}
	
}
