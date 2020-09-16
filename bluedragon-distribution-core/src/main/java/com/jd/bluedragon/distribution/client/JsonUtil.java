package com.jd.bluedragon.distribution.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author miaoyanchao json pojo转化类
 */
public class JsonUtil {
	private ObjectMapper mapper;
	private static JsonUtil instance = new JsonUtil();

	private JsonUtil() {
		mapper = new ObjectMapper();// 设置输出包含的属性
		//mapper.setDateFormat(DateHelper.getDateFormat());
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
				false);
	}
	/**
	 * 取得jsonutil的实例，
	 * @author wangjinsheng
	 * @date 2012-4-26
	 * @return instance
	 */
	public static JsonUtil getInstance() {
		return instance;
	}

	/**
	 * 将对象转化为json
	 * 
	 * @param Object对象
	 * @return
	 */
	public String object2Json(Object o) {
		String jsonValue = null;
		try {
			Writer strWriter = new StringWriter();
			mapper.writeValue(strWriter, o);
			jsonValue = strWriter.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonValue;
	}

	/**
	 * 
	 * @param jsonValue
	 *            json字符串
	 * @param classValue
	 *            object.class
	 * @return
	 */
	public Object json2Object(String jsonValue, Class<?> classValue) {
		Object o = null;
		try {
			if (jsonValue != null) {
				o = mapper.readValue(jsonValue, classValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 将字符串转化成List<?>数组
	 * 
	 * @param jsonValue
	 *            json字符串
	 * @return
	 */
	public List<?> json2List(String jsonValue) {
		List<?> resultArr = new ArrayList<Object>();
		TypeReference<List<Object>> typeRef = null;
		try {
			if (jsonValue != null) {
				typeRef = new TypeReference<List<Object>>() {
				};
				resultArr = mapper.readValue(jsonValue, typeRef);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	/**
	 */
	@SuppressWarnings("rawtypes")
	public List<?> json2List(String jsonValue, TypeReference typeRef) {
		List<?> resultArr = new ArrayList<Object>();
		try {
			if (jsonValue != null) {
				resultArr = mapper.readValue(jsonValue, typeRef);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultArr;
	}

	/**
	 * 将list<Object>转化成json字符串
	 * 
	 * @param objList
	 * @return
	 */
	public String list2Json(List<?> objList) {
		String resultJson = null;
		try {
			if (objList.isEmpty()) {
				return null;
			} else {
				resultJson = mapper.writeValueAsString(objList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}

}
