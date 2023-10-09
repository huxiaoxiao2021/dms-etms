package com.jd.bluedragon.distribution.base.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;
import com.jd.bluedragon.configuration.ucc.HystrixRouteUccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
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
	HystrixRouteUccPropertyConfiguration ucc1;
	
	@Autowired
	DmsConfigManager dmsConfigManager;
    /**
     * 获取ucc
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/ucc")
    public @ResponseBody UccPropertyConfiguration ucc() {
        return dmsConfigManager.getUccPropertyConfiguration();
    }
    /**
     * 获取ducc
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/ducc")
    public @ResponseBody DuccPropertyConfig ducc() {
        return dmsConfigManager.getDuccPropertyConfig();
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
        return dmsConfigManager.check();
    }
    /**
     * 获取ucc
     * @return
     * @throws Exception 
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/ucc1")
    public @ResponseBody String ucc1() throws Exception {
        return toUccJson(dmsConfigManager.getUccPropertyConfiguration());
    }
    /**
     * 获取ducc
     * @return
     * @throws Exception 
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_DICT_R)
    @RequestMapping(value = "/ducc1")
    public @ResponseBody String ducc1() throws Exception {
        return toUccJson(dmsConfigManager.getDuccPropertyConfig());
    }
	   public static String toUccJson(Object obj) throws Exception {
			 List<Field> fieldList = ObjectHelper.getDeclaredFieldsList(obj.getClass());
			 Collections.sort(fieldList,new Comparator<Field>() {
				@Override
				public int compare(Field o1, Field o2) {
					return o1.getName().compareTo(o2.getName());
				}
			 });
			 StringBuffer duccConfig1 = new StringBuffer();
			 for(Field field: fieldList) {
				 Class t =field.getType();
				 String typeName = t.getSimpleName();
				 String fieldName = field.getName();
				 Object duccConfigValue = ObjectHelper.getValue(obj, field.getName());
				 
				 if(!typeName.toLowerCase().contains("lis")) {
					 if(duccConfigValue != null) {
						 String val = toJson(duccConfigValue);
						 if(val.startsWith("\"")) {
							 val = val.substring(1, val.length());
						 }
						 if(val.endsWith("\"")) {
							 val = val.substring(0, val.length()-1);
						 }
						 duccConfig1.append("duccPropertyConfig."+fieldName +"="+val+ "\n");
						 
					 }else {
						 duccConfig1.append("duccPropertyConfig."+fieldName +"=null"+ "\n");
					 }
				 }
			 }
			 return duccConfig1.toString();
	   }
	   public static String toJson(Object obj) throws Exception {
	       

	        // 1.字符串转为字节数组
	        byte[] byteArray = JsonHelper.toJson(obj).getBytes(StandardCharsets.UTF_8);

	        // 2.构造字节数组输入流
	        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

	        // 3.构造输入流读取器
	        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream); //, StandardCharsets.UTF_8);

	        // 4.构造缓冲型读取器
	        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

	        String line;
	        StringBuilder stringBuilder = new StringBuilder();
	        // 5.循环读取每行字符串，并做必要处理
	        while ((line = bufferedReader.readLine()) != null) {
	            // 去掉每行两端的空格，并重新拼接为一行
	            stringBuilder.append(line.trim());
	        }

	        // 6.结果输出
	        return stringBuilder.toString();
	    }    
}