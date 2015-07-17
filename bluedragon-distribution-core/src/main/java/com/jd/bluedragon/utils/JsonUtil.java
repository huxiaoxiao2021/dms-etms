package com.jd.bluedragon.utils;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * @author miaoyanchao
 * json pojo转化类
 */
public class JsonUtil {
    private static JsonUtil jsonUtil=null;
	private ObjectMapper mapper;

    /**
     * GSON序列化器
     */
    private static final Gson GSON_COMMON=new GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting().create();

	private JsonUtil() {
		mapper = new ObjectMapper();
		// 设置输出包含的属性
	}

	public static JsonUtil getInstance(){
		if(jsonUtil==null){
			jsonUtil=new JsonUtil(); 
		}
		return jsonUtil;
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
            try {
                jsonValue = GSON_COMMON.toJson(o);
            }catch (Exception ex){
                ex.printStackTrace();
            }
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
			//e.printStackTrace();
            try {
                o = GSON_COMMON.fromJson(jsonValue,classValue);
            }catch (Exception ex){
                ex.printStackTrace();
            }
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
            try {
                resultArr = GSON_COMMON.fromJson(jsonValue, new TypeToken<List<Object>>(){}.getType());
            }catch (Exception ex){
                ex.printStackTrace();
            }
		}
		return resultArr;
	}
	
	public <T> List<T> json2List(String jsonValue, Class<T> classValue) {
		List<T> resultArr = new ArrayList<T>();
		TypeReference<List<T>> typeRef = null;
		try {
			if (jsonValue != null) {
				typeRef = new TypeReference<List<T>>() {
				};
				resultArr = mapper.readValue(jsonValue, typeRef);
			}
		} catch (Exception e) {
			e.printStackTrace();
            try {
                resultArr = GSON_COMMON.fromJson(jsonValue, new TypeToken<List<T>>(){}.getType());
            }catch (Exception ex){
                ex.printStackTrace();
            }
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
            try {
                resultJson = GSON_COMMON.toJson(objList);
            }catch (Exception ex){
                ex.printStackTrace();
            }
		}
		return resultJson;
	}
}
