package com.jd.bluedragon.dms.binlake;

import com.google.common.collect.Lists;
import com.jd.binlog.client.EntryMessage;
import com.jd.binlog.client.MessageDeserialize;
import com.jd.binlog.client.WaveEntry;
import com.jd.binlog.client.impl.JMQMessageDeserialize;
import com.jd.jmq.common.message.Message;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-04 16:02
 */
public class BinLakeUtils {

    public static List<ColumnRecord> getColumnRecords(EntryMessage entryMessage) {
        List<WaveEntry.RowData> rowDatas = entryMessage.getRowChange().getRowDatasList();
        List<ColumnRecord> afterChangeOfColumns = Lists.newArrayList();
        for (WaveEntry.RowData rowData : rowDatas) {
            List<WaveEntry.Column> afterColumns = rowData.getAfterColumnsList();
            for (WaveEntry.Column column : afterColumns) {
                ColumnRecord col = new ColumnRecord();
                col.setIndex(column.getIndex());
                col.setKey(column.getIsKey());
                col.setLength(column.getLength());
                col.setName(column.getName());
                col.setValue(column.getValue());
                col.setMysqlType(column.getMysqlType());
                col.setSqlType(column.getSqlType());
                col.setUpdate(column.getUpdated());
                afterChangeOfColumns.add(col);
            }
        }
        return afterChangeOfColumns;
    }

    public static List<EntryMessage> deserialize(List<Message> messages) throws Exception {
        MessageDeserialize deserialize = new JMQMessageDeserialize();
        return deserialize.deserialize(messages);
    }

    public static <T> T copyByList(EntryMessage entryMessage, Class<T> tClass) {
        return copyByList(getColumnRecords(entryMessage),tClass);
    }

    public static <T> T copyByList(List sourceList, Class<T> tClass) {
        try {
            T t = tClass.newInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Object source : sourceList) {
                Field[] sourceFields = source.getClass().getDeclaredFields();
                String column = null;
                Object value = null;
                for (Field field : sourceFields) {
                    field.setAccessible(true);
                    if (field.getName().equals("name")){
                        column = (String) field.get(source);
                    }
                    if (field.getName().equals("value")){
                        value = field.get(source);
                    }
                }
                if (column != null && value != null ) {
                    column = toCamelCase(column);
                    // 日期格式
                    if (isDate(t,column)) {
                        Date date = dateFormat.parse((String) value);
                        org.apache.commons.beanutils.BeanUtils
                                .copyProperty(t, column, date);
                        continue;
                    }
                    org.apache.commons.beanutils.BeanUtils
                            .copyProperty(t, column, value);
                }
            }
            return t;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> copy(List sourceList, Class<T> tClass) {

        List<T> list=new ArrayList<T>();

        for (Object source : sourceList) {

            T target=copy(source,tClass);

            list.add(target);
        }

        return list;
    }

    public static <T> T copy(Object source, Class<T> tClass) {

        try {
            T t = tClass.newInstance();
            Field[] sourceFields = source.getClass().getDeclaredFields();
            for (Field field : sourceFields) {
                field.setAccessible(true);
                String type = containFieldName(t, field.getName());
                if (!"".equals(type) && field.getType().toString().equals(type) && null!=field.get(source)) {
                    org.apache.commons.beanutils.BeanUtils
                        .copyProperty(t, field.getName(), field.get(source));
                }
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String containFieldName(Object obj, String filedName)
        throws Exception {
        String type = "";
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (filedName.equals(f.getName())) {
                type = f.getType().toString();
                break;
            }
        }
        return type;
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isDate(Object obj, String filedName)
            throws Exception {
        String type = "";
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (filedName.equals(f.getName())) {
                type = f.getType().toString();
                break;
            }
        }
        return type.equals("class java.util.Date");
    }
}
