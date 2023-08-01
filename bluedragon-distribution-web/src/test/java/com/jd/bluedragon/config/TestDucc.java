package com.jd.bluedragon.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
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
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.test.utils.FileHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.laf.config.ConfiguratorManager;
import com.jd.ql.dms.print.utils.StringHelper;
/**
 * 1、同步配置，ducc提供的工具
 * 2、发布uat,检查ducc加载配置是否和ucc一致
 * 3、切换
 * @author wuyoude
 *
 */
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
		 UccPropertyConfiguration ucc = (UccPropertyConfiguration)appContext.getBean("uccPropertyConfiguration") ;
		 UccPropertyConfiguration duccDefault = ucc;
		 DuccPropertyConfig ducc = (DuccPropertyConfig)appContext.getBean("duccPropertyConfig") ;
		 com.jd.coo.ucc.client.config.UccPropertyConfig uccConfig = (com.jd.coo.ucc.client.config.UccPropertyConfig)appContext.getBean("propertyConfig") ;
		 
		 while(true) {
			 String uccStr = FileHelper.loadFile(rootConfigPath+"ucc-config-test.properties");
			 UccPropertyConfiguration uccData = JsonHelper.fromJson(uccStr, UccPropertyConfiguration.class);
			 
			 System.err.println("ducc-getProperties:"+JsonHelper.toJson(configuratorManager.getProperties()));
			 
			 System.err.println("ducc:"+JsonHelper.toJson(duccDefault));
			 
			 System.err.println("ucc:"+JsonHelper.toJson(ucc));
			 
			 System.err.println("ducc:"+JsonHelper.toJson(ducc));
			 
			 StringBuffer sfNeedCheck = new StringBuffer();
			 StringBuffer sfCodes = new StringBuffer();
			 StringBuffer sfCodes1 = new StringBuffer();
			 List<Field> fieldList = ObjectHelper.getDeclaredFieldsList(UccPropertyConfiguration.class);
			 Map<String, Field> duccKeyMap = ObjectHelper.getDeclaredFields(DuccPropertyConfig.class);
			 Collections.sort(fieldList,new Comparator<Field>() {
				@Override
				public int compare(Field o1, Field o2) {
					return o1.getName().compareTo(o2.getName());
				}
			 });
			 StringBuffer duccConfig = new StringBuffer();
			 StringBuffer duccConfig1 = new StringBuffer();
			 for(Field field: fieldList) {
				 Class t =field.getType();
				 String typeName = t.getSimpleName();
				 String fieldName = field.getName();
				 StringBuffer sfCodes0 = new StringBuffer();
				 Object uccValue = ObjectHelper.getValue(ucc, field.getName());
				 Object duccValue = ObjectHelper.getValue(ducc, field.getName());
				 Object defaultValue = ObjectHelper.getValue(duccDefault, field.getName());
				 
				 Object duccConfigValue = ObjectHelper.getValue(uccData, field.getName());
				 
				 if(!typeName.toLowerCase().contains("lis")) {
//					 String key = "duccPropertyConfig."+field.getName();
					 String key = "uccPropertyConfiguration."+field.getName();
					 if(duccMap.containsKey(key)) {
						 DuccItem duccItem = duccMap.get(key);
						 
						 /** 开启的多级异步缓冲组件的任务类型列表 **/
						 sfCodes0.append("\t/**\n");
						 sfCodes0.append("\t *"+duccItem.description+"\n");
						 sfCodes0.append("\t */\n");
						 duccConfig.append("#"+duccItem.description+"\n");
					 }
					 if(defaultValue != null) {
						 sfCodes0.append("\t@Value(\"${duccPropertyConfig."+fieldName +":"+toJson(duccConfigValue)+ "}\")\n");
						 duccConfig.append("duccPropertyConfig."+fieldName +"="+toJson(duccConfigValue)+ "\n");
					 }else {
						 sfCodes0.append("\t@Value(\"${duccPropertyConfig."+fieldName + ":''}\")\n");
						 duccConfig.append("duccPropertyConfig."+fieldName +"="+ "\n");
					 }
					 sfCodes0.append("\tprivate "+typeName +" "+fieldName+ ";\n\n");
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
					 
					 
				 }else {
					 ParameterizedType type = (ParameterizedType)field.getGenericType();
					 if(type != null) {
						 Class a =(Class)type.getActualTypeArguments()[0];
						 sfCodes0.append("\tprivate "+typeName +"<"+a.getSimpleName()+"> "+fieldName+ ";\n\n");
					 }else {
						 sfCodes0.append("\tprivate "+typeName +" "+fieldName+ ";\n\n");
					 }
				 }
				 sfCodes.append(sfCodes0.toString());
				 if(!duccKeyMap.containsKey(fieldName)) {
					 sfCodes1.append(sfCodes0.toString());
				 }
				 boolean checkResult = isSame(uccValue, duccValue);
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
			 System.err.println(duccConfig.toString());
			 System.err.println("\nduuc-config-e");
			 
			 System.err.println("duuc-config1-s\n");
			 System.err.println(duccConfig1.toString());
			 System.err.println("\nduuc-config1-e");
			 
			 FileHelper.save(duccConfig1.toString(), rootConfigPath+"duuc-config1.text", "UTF-8");
			 
			 System.err.println("sfNeedCheck-s\n");
			 System.err.println(sfNeedCheck.toString());
			 System.err.println("\nsfNeedCheck-e");
			 
			 System.err.println("duuc-propeties-s\n");
			 System.err.println(sf.toString());
			 System.err.println("\nduuc-propeties--e");
			 
			 System.err.println("sfCodes-s\n");
			 System.err.println(sfCodes.toString());
			 System.err.println("\nsfCodes-e");
			 
			 System.err.println("sfCodes1-s\n");
			 System.err.println(sfCodes1.toString());
			 System.err.println("\nsfCodes1-e");			 
			 
			 log.debug("log-test:debug-msg");
			 log.info("log-test:info-msg");
			 log.warn("log-test:warn-msg");
			 log.error("log-test:error-msg");
			 System.err.println(toUccJson(uccData));
			 Thread.sleep(5000);
		 }

		}
    private static boolean isSame(Object o1,Object o2) {
    	if(o1 == o2) {
    		return true;
    	}
    	if(o1 == null && o2== null) {
    		return true;
    	}    	
    	return JsonHelper.toJson(o1).equals(JsonHelper.toJson(o2));
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
