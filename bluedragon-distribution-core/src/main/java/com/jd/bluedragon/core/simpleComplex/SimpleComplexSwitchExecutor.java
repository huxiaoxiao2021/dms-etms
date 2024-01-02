package com.jd.bluedragon.core.simpleComplex;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.google.common.collect.Lists;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 简繁转换处理器
 *
 * @author hujiping
 * @date 2023/7/27 8:01 PM
 */
@Slf4j
@Component("simpleComplexSwitchExecutor")
public class SimpleComplexSwitchExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SimpleComplexSwitchExecutor.class);

    public <T> T copyAndReturnNewInstance(T source) throws Exception {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
            out.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            in = new ObjectInputStream(bis);
            T copiedObject = (T) in.readObject();
            in.close();
            return copiedObject;
        }catch (Exception e){
            logger.error("通过序列化方式复制对象异常!对象:{}", source.getClass().getName(), e);
            return null;
        }finally {
            if(out != null){
                out.close();
            }
            if(in != null){
                in.close();
            }
        }
    }

    public void recursiveDeal(Object result, Integer switchType) {
        try {
            if(result instanceof String){
                Field field = result.getClass().getDeclaredField("value");
                field.setAccessible(true);
                field.set(result, Objects.equals(switchType, SimpleComplexSwitchContext.SIMPLE_TYPE)
                        ? complexToSimple((String) result).toCharArray()
                        : simpleToComplex((String) result).toCharArray());
            }
            else if(result instanceof List){
                extractedList((List) result, switchType);
            }
            else if(result instanceof Map){
                extractedMap((Map) result, switchType);
            }
            else {
                String str = JsonHelper.toJson(result);
                if (str.startsWith("{") && str.endsWith("}")){
                    // 判断是对象
                    recursiveDealOfVO(result, switchType);
                }
            }
        }catch (Exception e){
            logger.error("转换异常!", e);
        }
    }

    void recursiveDealOfVO(Object result, Integer switchType) throws Exception {
        List<Field> declaredFields = getAllFiled(result);
        Class<?> clz = result.getClass();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object fieldValue = field.get(result);
            if ("serialVersionUID".equals(field.getName())){
                continue;
            }
            if(fieldValue instanceof String){
                // 字符串则简繁切换
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                try {
                    clz.getMethod("set" + captureName(field.getName()), String.class)
                            .invoke(result, Objects.equals(switchType, SimpleComplexSwitchContext.SIMPLE_TYPE)
                                    ? complexToSimple((String) field.get(result))
                                    : simpleToComplex((String) field.get(result)));
                }catch (NoSuchMethodException e){
                    logger.error("当前字段:{}没有set方法!", field.getName());
                }
                
            }
            else if(fieldValue instanceof List){
                // list
                extractedList((List) fieldValue, switchType);
            }
            else if(fieldValue instanceof Map){
                // map
                extractedMap((Map<?, ?>) fieldValue, switchType);
            }
            else {
                String str = JsonHelper.toJson(fieldValue);
                if (str.startsWith("{") && str.endsWith("}")){
                    // 判断是对象
                    recursiveDealOfVO(fieldValue, switchType);
                }
            }
        }
    }

    private List<Field> getAllFiled(Object result) {
        List<Field> fieldList = Lists.newArrayList();
        Class<?> tempClass = result.getClass();
        // 当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        return fieldList;
    }

    private void extractedList(List<?> fieldValue, Integer switchType) {
        fieldValue.forEach(item -> {
            try {
                recursiveDeal(item, switchType);
            } catch (Exception e) {
                logger.error("转换异常!", e);
            }
        });
    }

    private void extractedMap(Map<?, ?> fieldValue, Integer switchType) {
        fieldValue.forEach((k, v) -> {
            try {
                // k的值判断
                recursiveDeal(k, switchType);
                // v的值判断
                recursiveDeal(v, switchType);
            } catch (Exception e) {
                logger.error("转换异常!", e);
            }
        });
    }

    /**
     * 首字母大写
     *
     * @param name
     * @return
     */
    public static String captureName(String name) {
        char[] cs=name.toCharArray();
        cs[0]-=32;
        return String.valueOf(cs);

    }

    String simpleToComplex(String oldStr){
        // 简体转换为繁体
        return ZhConverterUtil.convertToTraditional(oldStr);
    }
    String complexToSimple(String oldStr){
        // 繁体转换为简体
        return ZhConverterUtil.convertToSimple(oldStr);
    }
}
