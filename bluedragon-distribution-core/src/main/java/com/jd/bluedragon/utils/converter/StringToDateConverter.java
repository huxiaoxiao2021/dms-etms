package com.jd.bluedragon.utils.converter;

import org.apache.commons.beanutils.Converter;

import com.jd.bluedragon.utils.DateHelper;

public class StringToDateConverter implements Converter {
    
    @SuppressWarnings("rawtypes")
    public Object convert(Class arg0, Object sDate) {
        if (sDate == null) {
            return null;
        }
        
        return DateHelper.parseDateTime(sDate.toString());
    }
    
}
