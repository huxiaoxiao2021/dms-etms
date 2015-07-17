package com.jd.bluedragon.distribution.api.utils;

import java.io.StringWriter;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import static org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES;

public class JsonHelper {
    
    private final static Log logger = LogFactory.getLog(JsonHelper.class);
    
    private static ObjectMapper mapper = new ObjectMapper();
    /**
     * GSON序列化器
     */
    private static final Gson GSON_COMMON=new GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting().create();

    @SuppressWarnings("deprecation")
    public static <T> T fromJson(String json, Class<T> responseType) {
        try {
            JsonHelper.mapper.getSerializationConfig().setSerializationInclusion(
                    JsonSerialize.Inclusion.NON_NULL);
            JsonHelper.mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
            return JsonHelper.mapper.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.logger.error("反序列化JSON发生异常， 异常信息为：" + e.getMessage(), e);

            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.logger.error("GSON-反序列化JSON发生异常， 异常信息为：" +ex.getMessage(), ex);
            }
        }
        
        return null;
    }
    
    public static <T> T jsonToArray(String json, Class<T> responseType) {
        try {
            return JsonHelper.mapper.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.logger.error("反序列化JSON发生异常， 异常信息为：" + e.getMessage(), e);
            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.logger.error("GSON-反序列化JSON发生异常， 异常信息为：" +ex.getMessage(), ex);
            }
        }
        
        return null;
    }
    
    public static String toJson(Object obj) {
        return JsonHelper.toJson(obj, false);
    }
    
    @SuppressWarnings("deprecation")
    public static String toJson(Object object, boolean prettyPrint) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator generator = JsonHelper.mapper.getJsonFactory()
                    .createJsonGenerator(writer).useDefaultPrettyPrinter();
            JsonHelper.mapper.getSerializationConfig().setSerializationInclusion(
                    JsonSerialize.Inclusion.NON_NULL);
            JsonHelper.mapper.writeValue(generator, object);
            writer.close();
            return writer.getBuffer().toString();
        } catch (Exception e) {
            JsonHelper.logger.error("序列化JSON发生异常， 异常信息为：" + e.getMessage(), e);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Map<String, Object>> json2Map(String jsonVal) {
        try {
            Map<String, Map<String, Object>> maps = JsonHelper.mapper.readValue(jsonVal, Map.class);
            return maps;
        } catch (Exception e) {
            e.printStackTrace();
            try{
                return  GSON_COMMON.fromJson(jsonVal,new TypeToken<Map<String, Map<String, Object>>>(){}.getType());
            }catch (Exception ex){
                JsonHelper.logger.error("GSON-反序列化JSON发生异常， 异常信息为：" +ex.getMessage(), ex);
            }
            return null;
        }
    }
    
}
