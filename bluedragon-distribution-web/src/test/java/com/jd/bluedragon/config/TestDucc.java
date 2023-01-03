package com.jd.bluedragon.config;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jd.bluedragon.configuration.ducc.DuccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.laf.config.Configuration;
import com.jd.laf.config.ConfiguratorManager;
import com.jd.laf.config.Property;
import com.jd.ql.dms.print.utils.StringHelper;

public class TestDucc {
	public static void main(String args[]) throws Exception {
		
		final Logger log = LoggerFactory.getLogger(TestDucc.class); 
		
		 ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
		 "/distribution-core-context-ducc-test.xml");
		 ConfiguratorManager configuratorManager = (ConfiguratorManager)appContext.getBean("configuratorManager") ;
		 while(true) {
			Property property1 = configuratorManager.getProperty("uccPropertyConfiguration.asynbufferEnabledTaskType");
			System.out.println("uccPropertyConfiguration.asynbufferEnabledTaskType:" + property1.getString());
			
			//获取配置 方式3 (获取指定配置源下配置集合)
			Configuration configuraiton = configuratorManager.getConfiguration("ucc_test");
			System.out.println("resource configuration:" + configuraiton);
			System.out.println("uccPropertyConfiguration.asynbufferEnabledTaskType:" + configuraiton.getProperty("uccPropertyConfiguration.asynbufferEnabledTaskType").getString());
			//java进程退出时，可进行关闭
			 DuccPropertyConfiguration ducc = (DuccPropertyConfiguration)appContext.getBean("duccPropertyConfiguration") ;
			 UccPropertyConfiguration ucc = (UccPropertyConfiguration)appContext.getBean("uccPropertyConfiguration") ;
			 com.jd.coo.ucc.client.config.UccPropertyConfig uccConfig = (com.jd.coo.ucc.client.config.UccPropertyConfig)appContext.getBean("propertyConfig") ;
			 System.err.println("ducc:"+JsonHelper.toJson(ducc));
			 
			 System.err.println("ucc:"+JsonHelper.toJson(ucc));
			 
			 StringBuffer sfNeedCheck = new StringBuffer();
			 for(Field field: ObjectHelper.getAllFieldsList(UccPropertyConfiguration.class)) {
				 Object uccValue = ObjectHelper.getValue(ucc, field.getName());
				 Object duccValue = ObjectHelper.getValue(ducc, field.getName());
				 boolean checkResult = ObjectUtils.equals(uccValue, duccValue);
				 if(checkResult) {
					 System.out.println(field.getName()+":相同");
				 }else {
					 System.err.println(field.getName()+":值不相同"+",ucc="+uccValue +",ducc="+duccValue);
					 sfNeedCheck.append(field.getName()+":值不相同"+" \nucc="+uccValue +"\nducc="+duccValue+"\n");
				 }
			 }
			 StringBuffer sf = new StringBuffer();
			 for(String item:uccConfig.getWeakList()) {
				 sf.append("<laf-config:listener-field key=\"");
				 sf.append(item+"\" field=\"");
				 String beanName = item;
				 String field = item;
				 List<String> strs = StringHelper.splitToList(item, ".");
				 if(strs != null && strs.size() >1) {
					 field=strs.get(strs.size()-1);
					 beanName = "d"+strs.get(0);
				 }
				 sf.append(field+"\" beanName=\"");
				 sf.append(beanName+"\"");
				 sf.append("/>\n");
			 }
			 System.err.println("duuc-config-s\n");
			 System.err.println(sf.toString());
			 System.err.println("\nduuc-config-e");
			 
			 System.err.println("sfNeedCheck-s\n");
			 System.err.println(sfNeedCheck.toString());
			 System.err.println("\nsfNeedCheck-e");
			 
			 log.debug("log-test:debug-msg");
			 log.info("log-test:info-msg");
			 log.warn("log-test:warn-msg");
			 log.error("log-test:error-msg");
			 
			 Thread.sleep(5000);
		 }

		}
}
