package com.jd.bluedragon.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class JsonHelper {
    
    private final static Logger log = LoggerFactory.getLogger(JsonHelper.class);
    private static final String DATE_FORMAT_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectMapper dfOneJson2ListMapper = new ObjectMapper();

    /**
     * GSON序列化器
     */
    private static final Gson GSON_COMMON=new GsonBuilder()
    .enableComplexMapKeySerialization()
    .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss")
    .setPrettyPrinting().create();
    /**
     * jacjson序列化器-日期格式带毫秒
     */
    private static ObjectMapper objectMapperMs = null;
    /**
     * GSON序列化器-日期格式带毫秒
     */
    private static final Gson gsonParserMs=new GsonBuilder()
    .enableComplexMapKeySerialization()
    .serializeNulls().setDateFormat(DATE_FORMAT_MS)
    .setPrettyPrinting().create();
    private static final JsonParser GSON_PARSER= new JsonParser();


    static {
    	dfOneJson2ListMapper.getDeserializationConfig().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    	//毫秒格式的初始化
        objectMapperMs = new ObjectMapper();
        //序列化对象时：空对象不会出现在json中
        objectMapperMs.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
        //反序列化时：忽略未知属性（默认为true，会抛出异常）
        objectMapperMs.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapperMs.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapperMs.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_MS);
        objectMapperMs.getSerializationConfig().setDateFormat(dateFormat);
        objectMapperMs.getDeserializationConfig().setDateFormat(dateFormat);
    }
    
    @SuppressWarnings("deprecation")
    public static <T> T fromJson(String json, Class<T> responseType) {
        try {
            JsonHelper.mapper.getSerializationConfig().setSerializationInclusion(
                    JsonSerialize.Inclusion.NON_NULL);

            return JsonHelper.mapper.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.log.warn("Jackson反序列化JSON发生异常，将使用GSON重试");
            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.log.error("GSON-反序列化JSON发生异常", ex);
            }
        }
        return null;
    }
    /**
     * 支持日期格式为yyyy-MM-dd HH:mm:ss.SSS的序列化方法
     * @param obj
     * @return
     */
    public static String toJsonMs(Object obj) {
    	if(obj == null){
    		return null;
    	}
        try {
            return objectMapperMs.writeValueAsString(obj);
        } catch (Exception e) {
            JsonHelper.log.error("序列化JSON发生异常", e);
        }
        return null;
    }
    /**
     * 支持日期格式为yyyy-MM-dd HH:mm:ss.SSS的反序列化方法
     * @param json
     * @param responseType
     * @return
     */
    public static <T> T fromJsonMs(String json, Class<T> responseType) {
    	if(json == null){
    		return null;
    	}
        try {
            return objectMapperMs.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.log.warn("objectMapperMs-Jackson反序列化异常，将使用GSON重试");
            try{
                return  gsonParserMs.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.log.error("GSON-反序列化JSON发生异常", ex);
            }
        }
        return null;
    }
    public static <T> T fromJsonUseGson(String json,Class<T> responseType){
        return GSON_COMMON.fromJson(json,responseType);
    }

    public static <T> T fromJsonUseGson(String json,Type responseType){
        return (T)GSON_COMMON.fromJson(json,responseType);
    }
    @SuppressWarnings("deprecation")
    public static <T> T fromJsonDateFormat(String json, Class<T> responseType) {
        try {
            JsonHelper.dfOneJson2ListMapper.getSerializationConfig().setSerializationInclusion(
                    JsonSerialize.Inclusion.NON_NULL);
            return JsonHelper.dfOneJson2ListMapper.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.log.warn("Jackson反序列化JSON发生异常，将使用GSON重试");
            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.log.error("GSON-反序列化JSON发生异常", ex);
            }
        }
        
        return null;
    }

    public static <T> T fromJsonUseGsonMillisecondFormat(String json,Class<T> responseType){

        return null;
    }
    
    public static <T> T jsonToArray(String json, Class<T> responseType) {
        try {
            //设置Jackson反序列化未知字段忽略 不报错
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return JsonHelper.mapper.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.log.warn("Jackson反序列化JSON发生异常，将使用GSON重试");
            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.log.error("GSON-反序列化JSON发生异常", ex);
            }
        }
        
        return null;
    }

    public static <T> T jsonToObject(String json, Class<T> responseType) {
            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.log.error("GSON-反序列化JSON发生异常", ex);
            }
        return null;
    }

    public static <T> T jsonToArrayWithDateFormatOne(String json, Class<T> responseType) {
        try {
            return JsonHelper.dfOneJson2ListMapper.readValue(json, responseType);
        } catch (Exception e) {
            JsonHelper.log.warn("Jackson反序列化JSON发生异常，将使用GSON重试");
            try{
                return  GSON_COMMON.fromJson(json,responseType);
            }catch (Exception ex){
                JsonHelper.log.error("GSON-反序列化JSON发生异常", ex);
            }
        }
        return null;
    }
    
    public static String toJson(Object obj) {
        return JsonHelper.toJson(obj, false);
    }
    @SuppressWarnings("deprecation")
    public static String toJson(Object object, boolean prettyPrint) {
        JsonGenerator generator = null;
        try {
            StringWriter writer = new StringWriter();
            generator = JsonHelper.mapper.getJsonFactory().createJsonGenerator(writer);
            generator.useDefaultPrettyPrinter();
            JsonHelper.mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            JsonHelper.mapper.writeValue(generator, object);
            writer.close();
            return writer.getBuffer().toString();
        } catch (Exception e) {
            JsonHelper.log.error("序列化JSON发生异常", e);
        }finally {
            if(generator != null){
                try{
                    generator.close();
                }catch (Exception e){
                    log.error("generator关闭失败", e);
                }
            }
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
            return null;
        }
    }
    
    public static Boolean isJson(String json, Class<?> responseType) {
        if (StringHelper.isEmpty(json)) {
            return Boolean.FALSE;
        }
        
        try {
            if (JsonHelper.fromJson(json, responseType) != null) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
        }
        
        return Boolean.FALSE;
    }

    /*
    public static void main(String[] args) {
        System.out.printf(String.valueOf( isJsonString("201504161401010")));
        System.out.printf(String.valueOf( isJsonString("{\"test\":1}")));
        String aaaa="32322-21123";
        System.out.print(JsonHelper.toJson(aaaa));
        String test=JsonHelper.fromJson(aaaa, String.class);
        System.out.print(test);
    }
    */
    public static boolean isJsonString(String json){
        if (StringHelper.isEmpty(json)) {
            return Boolean.FALSE;
        }
        try {
            if(false==json.startsWith("{")&&false==json.startsWith("[")){
                return false;
            }
            GSON_PARSER.parse(json);
            return true;
        } catch (JsonParseException e) {
            log.error("bad json: {}" , json,e);
            return false;
        }
    }

    /**
     * 返回的key为String value为Object
     * 
     * @param jsonVal
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2MapNormal(String jsonVal) {
    	if(jsonVal == null){
    		return null;
    	}
        try {
            Map<String, Object> maps = JsonHelper.mapper.readValue(jsonVal, Map.class);
            return maps;
        } catch (Exception e) {
            log.error("bad json: {}" , jsonVal,e);
            return null;
        }
    }
    /**
     * json转成list对象
     * @param json
     * @param elementClasses 元素类型
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> elementClasses) {
        try {
            return JSONObject.parseArray(json, elementClasses);
        } catch (Exception e) {
        	log.error("GSON-反序列化JSON发生异常", e);
        }
        return null;
    }
    /**
     * added by zhanglei  统一对时间的处理
     * @param object
     * @return
     */
    public static String toJsonUseGson(Object object) {
        return GSON_COMMON.toJson(object);
    }


    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}
