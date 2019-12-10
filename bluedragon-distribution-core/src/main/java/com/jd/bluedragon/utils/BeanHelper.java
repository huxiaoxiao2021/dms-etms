package com.jd.bluedragon.utils;

import com.jd.bluedragon.utils.converter.LocalDateConverter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

public class BeanHelper extends BeanUtils {

    private static final Logger log = LoggerFactory.getLogger(BeanHelper.class);

    public static void copyProperties(Object dest, Object orig) {
        //ConvertUtils.register(new StringToDateConverter(), Date.class);
        ConvertUtils.register(new LocalDateConverter(), Date.class);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new ShortConverter(null), Short.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new DoubleConverter(null), Double.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (Exception e) {
            log.error("BeanHelper.copyProperties()在进行对象属性复制时出现异常", e);
        }
    }

}
