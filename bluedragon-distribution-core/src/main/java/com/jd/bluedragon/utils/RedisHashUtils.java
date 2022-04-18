package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.jy.annotation.RedisHashColumn;
import com.jd.bluedragon.dms.utils.MathUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RedisHashUtils
 * @Description
 * @Author wyh
 * @Date 2022/4/5 15:43
 **/
public class RedisHashUtils {

    public static <T> Map<String, String> objConvertToMap(T obj) throws IllegalAccessException {
        Map<String, String> result = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field item : fields) {
            item.setAccessible(true);
            Object value = item.get(obj);
            if (value == null) {
                continue;
            }

            mappingFieldValue(result, item, value);
        }

        return result;
    }

    private static void mappingFieldValue(Map<String, String> result, Field item, Object value) {
        RedisHashColumn hashColumn = item.getAnnotation(RedisHashColumn.class);
        if (hashColumn != null) {
            result.put(hashColumn.hashField(), String.valueOf(value));
        }
    }

    public static <T> T mapConvertBean(Map<String, String> objectMap, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        if (objectMap == null) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        T object = clazz.newInstance();
        for (Field item : fields) {
            RedisHashColumn annotation = item.getAnnotation(RedisHashColumn.class);
            item.setAccessible(true);
            if (annotation != null) {
                String fieldName = item.getName();
                Object value = objectMap.get(fieldName);
                setField(item, value, object);
            }
        }
        return object;
    }

    private static <T> void setField(Field field, Object fieldValue, T object) throws IllegalAccessException{
        if (fieldValue == null) {
            return;
        }
        Class<?> clz = field.getType();
        String clzName = clz.getSimpleName();
        switch(clzName) {
            case "String":
                String strValue = String.valueOf(fieldValue);
                field.set(object, strValue);
                break;
            case "Integer":
                Integer intValue = MathUtils.objToInt(fieldValue);
                field.set(object, intValue);
                break;
            case "BigDecimal":
                BigDecimal bigDecimal = BigDecimal.valueOf(Double.parseDouble(fieldValue.toString()));
                field.set(object, bigDecimal);
                break;
            case "Double":
                Double doubleValue = MathUtils.objToDoubleOrZero(fieldValue);
                field.set(object, doubleValue);
                break;
            case "Float":
                Float floatValue = MathUtils.objToFloatOrZero(fieldValue);
                field.set(object, floatValue);
                break;
            case "Long":
                Long longValue = MathUtils.objToZeroLong(fieldValue);
                field.set(object, longValue);
                break;
            case "Byte":
                Byte byteValue = Byte.valueOf(String.valueOf(fieldValue));
                field.set(object,byteValue);
                break;
            case "Date":
                Date dateValue;
                if (String.valueOf(fieldValue).equals("0")) {
                    dateValue = null;
                }
                else {
                    if (fieldValue instanceof Date) {
                        dateValue = (Date)fieldValue;
                    }
                    else if (fieldValue instanceof Long) {
                        DateTimeFormat dateTimeFormat = field.getAnnotation(DateTimeFormat.class);
                        if (dateTimeFormat != null) {
                            String date = DateHelper.formatDate(new Date((long)fieldValue), dateTimeFormat.pattern());
                            dateValue = DateHelper.parseDate(date, dateTimeFormat.pattern());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dateValue);
                            dateValue = calendar.getTime();
                        }
                        else {
                            String date = DateHelper.formatDate(new Date((long)fieldValue));
                            dateValue = DateHelper.parseDate(date, DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2);
                        }
                    }
                    else{
                        DateTimeFormat dateTimeFormat = field.getAnnotation(DateTimeFormat.class);
                        if(dateTimeFormat != null){
                            dateValue = DateHelper.parseDate(String.valueOf(fieldValue), dateTimeFormat.pattern());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(dateValue);
                            calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
                            dateValue = calendar.getTime();
                        }else{
                            dateValue = DateHelper.parseDate(String.valueOf(fieldValue), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2);
                        }
                    }
                }
                field.set(object, dateValue);
                break;
            default: throw new IllegalAccessException("不支持的类型"+clzName);
        }
    }
}
