package com.jd.bluedragon.utils;

import com.jd.bluedragon.utils.converter.LocalDateConverter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.Date;

public class BeanHelper extends BeanUtils {

    private static final Log logger = LogFactory.getLog(BeanHelper.class);

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
            logger.error("BeanHelper.copyProperties()在进行对象属性复制时出现异常", e);
        }
    }

}
