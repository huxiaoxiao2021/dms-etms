package com.jd.bluedragon.distribution.base.controller;

import java.lang.reflect.Field;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ducc.DhystrixRouteUccPropertyConfiguration;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.HystrixRouteUccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.uim.annotation.Authorization;

/**
 * 
 * @author wuyoude
 *
 */
@Controller
@RequestMapping("base/dmsUcc")
public class DmsUccController {
	
	private static final Logger log = LoggerFactory.getLogger(DmsUccController.class);
	
	@Autowired
	UccPropertyConfiguration ucc;
	
	@Autowired
	HystrixRouteUccPropertyConfiguration ucc1;
	
	@Autowired
	DuccPropertyConfiguration ducc;
	
	@Autowired
	DhystrixRouteUccPropertyConfiguration ducc1;
	
	
    
    /**
     * 获取ucc
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/ucc")
    public @ResponseBody UccPropertyConfiguration ucc() {
        return ucc;
    }
    /**
     * 获取ducc
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/ducc")
    public @ResponseBody DuccPropertyConfiguration ducc() {
        return ducc;
    } 
    /**
     * 获取ducc
     * @return
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/check")
    public @ResponseBody String check() throws Exception {
    	StringBuffer sf = new StringBuffer();
		 for(Field field: ObjectHelper.getAllFieldsList(UccPropertyConfiguration.class)) {
			 Object uccValue = ObjectHelper.getValue(ucc, field.getName());
			 Object duccValue = ObjectHelper.getValue(ducc, field.getName());
			 boolean checkResult = ObjectUtils.equals(uccValue, duccValue);
			 if(checkResult) {
				 log.info(field.getName()+":相同");
			 }else {
				 log.error(field.getName()+":值不相同"+",ucc="+uccValue +",ducc="+duccValue);
				 sf.append(field.getName()+":值不相同"+" \nucc="+uccValue +"\nducc="+duccValue+"\n");
			 }
		 }
		 sf.append("\n");
		 for(Field field: ObjectHelper.getAllFieldsList(HystrixRouteUccPropertyConfiguration.class)) {
			 Object uccValue = ObjectHelper.getValue(ucc1, field.getName());
			 Object duccValue = ObjectHelper.getValue(ducc1, field.getName());
			 boolean checkResult = ObjectUtils.equals(uccValue, duccValue);
			 if(checkResult) {
				 log.info(field.getName()+":相同");
			 }else {
				 log.error(field.getName()+":值不相同"+",ucc1="+uccValue +",ducc1="+duccValue);
				 sf.append(field.getName()+":值不相同"+" \nucc1="+uccValue +"\nducc1="+duccValue+"\n");
			 }
		 }		 
        return sf.toString();
    }    
}