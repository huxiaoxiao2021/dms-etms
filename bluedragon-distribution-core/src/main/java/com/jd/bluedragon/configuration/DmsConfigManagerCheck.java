
package com.jd.bluedragon.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;
import com.jd.jsf.gd.util.StringUtils;

public class DmsConfigManagerCheck implements ApplicationListener {
	
	private static final Logger log = LoggerFactory.getLogger(DmsConfigManagerCheck.class);
	
	@Autowired
	@Qualifier("dmsConfigManager")
	private DmsConfigManager dmsConfigManager;
	
	public DmsConfigManager getDmsConfigManager() {
		return dmsConfigManager;
	}
	public void setDmsConfigManager(DmsConfigManager dmsConfigManager) {
		this.dmsConfigManager = dmsConfigManager;
	}
	public void check(){
		
	}
    public void doCheck(){
    	//未加载到ucc配置
    	if(DuccPropertyConfig.CONFIG_FROM_LOCAL.equals(dmsConfigManager.getDuccPropertyConfig().getConfigFrom())) {
    		log.error("加载ucc配置失败！请检查ducc配置文件及duccPropertyConfig.configFrom的值！");
//    		throw new RuntimeException("加载ucc配置失败！请检查ducc配置文件及duccPropertyConfig.configFrom的值！");
    	}
    	String checkResult = "";
		try {
			checkResult = dmsConfigManager.check();
		} catch (Exception e) {
			e.printStackTrace();
			checkResult = e.getMessage();
		}
    	
    	if(StringUtils.isNotBlank(checkResult)) {
    		if(dmsConfigManager.getDuccPropertyConfig().isCheckConfig()) {
    			log.error("ducc配置检查失败！ucc和ducc存在以下不一样的配置\n"+checkResult);
    			throw new RuntimeException("ducc配置检查失败！ucc和ducc存在不一样的配置，请根据日志，检查修改ducc配置！");
    		}else {
    			log.warn("ducc配置检查失败！ucc和ducc存在以下不一样的配置\n"+checkResult);
    		}
    		
    	}
		
    }

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		//监听初始化、刷新事件，isActive标识为true
		if (event instanceof ContextRefreshedEvent) {
			log.warn("ducc配置检查onApplicationEvent-ContextRefreshedEvent");
			doCheck();
		}
		if (event instanceof ContextStartedEvent) {
			log.warn("ducc配置检查onApplicationEvent-ContextStartedEvent");
			doCheck();
		}
		//监听关闭事件，停止异步缓冲任务
		if (event instanceof ContextClosedEvent) {
		}
	}
}
