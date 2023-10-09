package com.jd.bluedragon.configuration;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.configuration.ducc.DuccHystrixRoutePropertyConfig;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;

@Service("dmsConfigManager")
public class DmsConfigManager {
	
	private static final Logger log = LoggerFactory.getLogger(DmsConfigManager.class);
	
	@Autowired
	@Qualifier("duccPropertyConfig")
	private DuccPropertyConfig duccPropertyConfig;
	
	@Autowired
	@Qualifier("uccPropertyConfiguration")
	private UccPropertyConfiguration uccPropertyConfiguration;
	
	@Autowired
	@Qualifier("duccHystrixRoutePropertyConfig")
	private DuccHystrixRoutePropertyConfig duccHystrixRoutePropertyConfig;
	
	public DuccPropertyConfig getDuccPropertyConfig() {
		return duccPropertyConfig;
	}
	public DuccPropertyConfig getPropertyConfig() {
		return duccPropertyConfig;
	}
	public UccPropertyConfiguration getUccPropertyConfiguration() {
		return uccPropertyConfiguration;
	}

	public DuccHystrixRoutePropertyConfig getDuccHystrixRoutePropertyConfig() {
		return duccHystrixRoutePropertyConfig;
	}

    public String check() throws Exception {
    	StringBuffer sf = new StringBuffer();
    	Map<String, Field> duccFilesMap = ObjectHelper.getDeclaredFields(DuccPropertyConfig.class);
		 for(Field field: ObjectHelper.getDeclaredFieldsList(UccPropertyConfiguration.class)) {
			 Object uccValue = ObjectHelper.getValue(uccPropertyConfiguration, field.getName());
			 Object duccValue = ObjectHelper.getValue(duccPropertyConfig, field.getName());
			 String checkStr = "";
			 if(!duccFilesMap.containsKey(field.getName())) {
				 checkStr ="ducc缺少字段";
			 }
			 else if(!field.getType().equals(duccFilesMap.get(field.getName()).getType())){
				 checkStr = "ducc类型不一致 ucc类型"+field.getType().getSimpleName()+" ducc类型"+duccFilesMap.get(field.getName()).getType().getSimpleName()+"";
			 }
			 boolean checkResult = isSameValue(uccValue, duccValue);
			 if(checkResult) {
				 log.info(field.getName()+":equal");
			 }else {
				 log.error(field.getName()+":no-equal"+",ucc="+uccValue +",ducc="+duccValue);
				 sf.append(field.getName()+":no-equal"+checkStr+" \nucc="+uccValue +"\nducc="+duccValue+"\n");
			 }
		 }
		 sf.append("\n");
        return sf.toString();
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