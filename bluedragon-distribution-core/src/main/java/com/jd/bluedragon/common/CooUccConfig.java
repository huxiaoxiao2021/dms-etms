package com.jd.bluedragon.common;

import IceInternal.Ex;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by xumei3 on 2017/12/12.
 */
public class CooUccConfig {
    private String asynbuffer;

    public String getAsynbuffer() {
        return asynbuffer;
    }

    public void setAsynbuffer(String asynbuffer) {
        this.asynbuffer = asynbuffer;
    }

    public static void main(String [] args) throws Exception{
        CooUccConfig config = new CooUccConfig();
        PropertyDescriptor e1 = BeanUtils.getPropertyDescriptor(config.getClass(), "asynbuffer");
        Method writeMethod = e1.getWriteMethod();
        if(writeMethod != null) {
            if(!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                writeMethod.setAccessible(true);
            }

            writeMethod.invoke(config, new Object[]{"Fail"});
            Field field = ReflectionUtils.findField(config.getClass(), "asynbuffer");
            ReflectionUtils.makeAccessible(field);
            Object value = ReflectionUtils.getField(field, config);
            System.out.println(value);
        }
    }
}
