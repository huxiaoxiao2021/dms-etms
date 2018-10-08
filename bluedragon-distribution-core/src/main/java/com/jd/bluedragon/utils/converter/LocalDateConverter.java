package com.jd.bluedragon.utils.converter;

import com.jd.bluedragon.utils.DateHelper;
import org.apache.commons.beanutils.Converter;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * Created by lixin39 on 2018/10/1.
 */
public class LocalDateConverter implements Converter {

    @Override
    public Object convert(Class targetType, Object value) {
        if (value == null) {
            return null;
        }

        // Handle String
        if (value instanceof String) {
            return DateHelper.parseDateTime(value.toString());
        }

        // Handle Date (includes java.sql.Date & java.sql.Time)
        if (value instanceof Date) {
            Date date = (Date) value;
            return toDate(targetType, date.getTime());
        }

        // Handle java.sql.Timestamp
        if (value instanceof java.sql.Timestamp) {
            java.sql.Timestamp timestamp = (java.sql.Timestamp) value;
            long timeInMillis = ((timestamp.getTime() / 1000) * 1000);
            timeInMillis += timestamp.getNanos() / 1000000;
            return toDate(targetType, timeInMillis);
        }

        // Handle Calendar
        if (value instanceof Calendar) {
            Calendar calendar = (Calendar) value;
            return toDate(targetType, calendar.getTime().getTime());
        }

        // Handle Long
        if (value instanceof Long) {
            Long longObj = (Long) value;
            return toDate(targetType, longObj.longValue());
        }

        // Default conversion failed
        return null;

    }

    private Object toDate(Class type, long value) {
        // java.util.Date
        if (type.equals(Date.class)) {
            return new Date(value);
        }

        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            return new java.sql.Date(value);
        }

        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            return new java.sql.Time(value);
        }

        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            return new java.sql.Timestamp(value);
        }

        // java.util.Calendar
        if (type.equals(Calendar.class)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(value));
            calendar.setLenient(false);
            return calendar;
        }
        return null;
    }
}
