package com.jd.bluedragon.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jd.bluedragon.configuration.ducc.DuccPropertyConfig;
import com.jd.bluedragon.configuration.ducc.DuccPropertyConfiguration;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.test.utils.FileHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.laf.config.ConfiguratorManager;
import com.jd.laf.config.Property;
import com.jd.ql.dms.print.utils.StringHelper;

public class TestDucc {
	public static void main(String args[]) throws Exception {
		String tmpConfigPath = new File("").getCanonicalPath();
		String rootConfigPath = tmpConfigPath+"\\src\\test\\resources\\files\\";
//		String rootConfigPath = "/src/test/resources/files";
		 String configs = FileHelper.loadFile(rootConfigPath+"ducc-online.text");
		 DuccResult duccResult = JsonHelper.fromJson(configs, DuccResult.class);
		 Map<String,DuccItem> duccMap = new HashMap<>();
		 if(duccResult != null && duccResult.data != null) {
			 for(DuccItem item : duccResult.data) {
				 duccMap.put(item.key, item);
			 }
		 }
		 
		final Logger log = LoggerFactory.getLogger(TestDucc.class); 
		
		 ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
		 "/distribution-core-context-ducc-test.xml");
		 ConfiguratorManager configuratorManager = (ConfiguratorManager)appContext.getBean("configuratorManager") ;
		 DuccPropertyConfiguration duccDefault = (DuccPropertyConfiguration)appContext.getBean("duccPropertyConfiguration") ;
		 UccPropertyConfiguration ucc = (UccPropertyConfiguration)appContext.getBean("uccPropertyConfiguration") ;
		 DuccPropertyConfig ducc = (DuccPropertyConfig)appContext.getBean("duccPropertyConfig") ;
		 com.jd.coo.ucc.client.config.UccPropertyConfig uccConfig = (com.jd.coo.ucc.client.config.UccPropertyConfig)appContext.getBean("propertyConfig") ;
		 
		 while(true) {
			Property property1 = configuratorManager.getProperty("uccPropertyConfiguration.asynbufferEnabledTaskType");
			System.out.println("uccPropertyConfiguration.asynbufferEnabledTaskType:" + property1.getString());
			
			 System.err.println("ducc-getProperties:"+JsonHelper.toJson(configuratorManager.getProperties()));
			 
			 System.err.println("ducc:"+JsonHelper.toJson(duccDefault));
			 
			 System.err.println("ucc:"+JsonHelper.toJson(ucc));
			 
			 System.err.println("ducc:"+JsonHelper.toJson(ducc));
			 
			 StringBuffer sfNeedCheck = new StringBuffer();
			 StringBuffer sfCodes = new StringBuffer();
			 List<Field> fieldList = ObjectHelper.getAllFieldsList(UccPropertyConfiguration.class);
			 Collections.sort(fieldList,new Comparator<Field>() {
				@Override
				public int compare(Field o1, Field o2) {
					return o1.getName().compareTo(o2.getName());
				}
			 });
			 for(Field field: fieldList) {
				 Class t =field.getType();
				 String typeName = t.getSimpleName();
				 String fieldName = field.getName();
				 
				 Object uccValue = ObjectHelper.getValue(ucc, field.getName());
				 Object duccValue = ObjectHelper.getValue(ducc, field.getName());
				 Object defaultValue = ObjectHelper.getValue(duccDefault, field.getName());
				 

				 if(!typeName.toLowerCase().contains("lis")) {
//					 String key = "duccPropertyConfig."+field.getName();
					 String key = "uccPropertyConfiguration."+field.getName();
					 if(duccMap.containsKey(key)) {
						 DuccItem duccItem = duccMap.get(key);
						 
						 /** 开启的多级异步缓冲组件的任务类型列表 **/
						 sfCodes.append("\t/**\n");
						 sfCodes.append("\t *"+duccItem.description+"\n");
						 sfCodes.append("\t */\n");
					 }
					 if(defaultValue != null) {
						 sfCodes.append("\t@Value(\"${duccPropertyConfig."+fieldName +":"+defaultValue+ "}\")\n");
					 }else {
						 sfCodes.append("\t@Value(\"${duccPropertyConfig."+fieldName + ":''}\")\n");
					 }
					 sfCodes.append("\tprivate "+typeName +" "+fieldName+ ";\n\n");
				 }else {
					 ParameterizedType type = (ParameterizedType)field.getGenericType();
					 if(type != null) {
						 Class a =(Class)type.getActualTypeArguments()[0];
						 sfCodes.append("\tprivate "+typeName +"<"+a.getSimpleName()+"> "+fieldName+ ";\n\n");
					 }else {
						 sfCodes.append("\tprivate "+typeName +" "+fieldName+ ";\n\n");
					 }
					 
					 
				 }
				 boolean checkResult = ObjectUtils.equals(uccValue, duccValue);
				 if(!checkResult) {
					 sfNeedCheck.append(fieldName+":值不相同"+" \nucc="+uccValue +"\nducc="+duccValue+"\n");
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
			 
			 System.err.println("sfCodes-s\n");
			 System.err.println(sfCodes.toString());
			 System.err.println("\nsfCodes-e");
			 
			 
			 log.debug("log-test:debug-msg");
			 log.info("log-test:info-msg");
			 log.warn("log-test:warn-msg");
			 log.error("log-test:error-msg");
			 
			 Thread.sleep(5000);
		 }

		}
	public static class DuccResult{
		private List<DuccItem> data;

		public List<DuccItem> getData() {
			return data;
		}

		public void setData(List<DuccItem> data) {
			this.data = data;
		}
	}
	public static class DuccItem{
		
		private String key;
		private String value;
		private String description;
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
	}
}
