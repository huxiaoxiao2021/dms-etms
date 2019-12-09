package com.jd.bluedragon.utils.canal;

import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanalHelper {
    private final static Logger log = LoggerFactory.getLogger(CanalHelper.class);
    /**
     * 将canal消息转换为CanalEvent
     * @param canalMsg
     * @return
     * @throws Exception 
     */
    public static <T> CanalEvent<T> parseCanalMsg(String canalMsg,Class<T> targetType) throws Exception {
		if (null != canalMsg) {
			CanalRow canalRow = JsonHelper.jsonToObject(canalMsg,
					CanalRow.class);
			if (null != canalRow) {
				CanalEvent<T> canalEvent = new CanalEvent<T>();
				/**
				 * 1、插入事件，设置dataAfter
				 * 2、更新事件，设置dataBefore和dataAfter
				 * 3、删除事件，设置dataAfter
				 */
				if (DbOperation.INSERT.equals(DbOperation.parse(canalRow
						.getEventType()))) {
					canalEvent.setDbOperation(DbOperation.INSERT);
					canalEvent.setDataAfter(
							parse2Object(canalRow.getAfterChangeOfColumns(), targetType));
				} else if (DbOperation.UPDATE.equals(DbOperation.parse(canalRow
						.getEventType()))) {
					canalEvent.setDbOperation(DbOperation.UPDATE);
					canalEvent.setDataBefore(
							parse2Object(canalRow.getBeforeChangeOfColumns(), targetType));
					canalEvent.setDataAfter(
							parse2Object(canalRow.getAfterChangeOfColumns(), targetType));
				} else if (DbOperation.DELETE.equals(DbOperation.parse(canalRow
						.getEventType()))) {
					canalEvent.setDbOperation(DbOperation.DELETE);
					canalEvent.setDataBefore(
							parse2Object(canalRow.getBeforeChangeOfColumns(), targetType));
				}
				return canalEvent;
			}
		}
		return null;
    }
    /**
     * canal字段列表转换成特定对象
     * @param columns
     * @param targetType
     * @return 
     * @throws Exception 
     */
    public static <T> T parse2Object(List<CanalColumn> columns,Class<T> targetType) throws Exception {
		if (null != columns && !columns.isEmpty()) {
			T obj = targetType.newInstance();
			Map<String, Field> fields = ObjectHelper.getAllFields(targetType);
			/**
			 * 1、获取类对象的所有属性
			 * 2、将数据字段设置到对应的属性中
			 */
			for (CanalColumn column : columns) {
				String columnName = column.getName();
				String columnValue = column.getValue();
				String fieldName = formatJavaName(columnName);
				Field field = fields.get(fieldName);
				Object fieldValue = null;
				if (field != null) {
					Class<?> fieldType = field.getType();
					if (fieldType.equals(String.class)) {
						fieldValue = columnValue;
					} else if (StringHelper.isNotEmpty(columnValue)) {
						if (fieldType.equals(boolean.class)
								|| fieldType.equals(Boolean.class)) {
							fieldValue = Boolean.parseBoolean(columnValue);
						} else if (fieldType.equals(int.class)
								|| fieldType.equals(Integer.class)) {
							fieldValue = Integer.parseInt(columnValue);
						} else if (fieldType.equals(long.class)
								|| fieldType.equals(Long.class)) {
							fieldValue = Long.parseLong(columnValue);
						} else if (fieldType.equals(float.class)
								|| fieldType.equals(Float.class)) {
							fieldValue = Float.parseFloat(columnValue);
						} else if (fieldType.equals(double.class)
								|| fieldType.equals(Double.class)) {
							fieldValue = Double.parseDouble(columnValue);
						} else if (fieldType.equals(java.util.Date.class)) {
							fieldValue = DateUtils.parseDate(columnValue,
									DateHelper.DATE_TIME_FORMAT);
						} else if (fieldType.equals(java.sql.Date.class)) {
						} else if (fieldType.equals(java.sql.Timestamp.class)) {
						}
					} else {
						log.warn("数据转换失败：name:{}->{} val:{}",columnName,fieldName, columnValue);
					}
					if (fieldValue != null) {
						field.setAccessible(true);
						field.set(obj, fieldValue);
					}
				}
			}
			return obj;
		}
		return null;
    }
    public Map<String, Object> parse2Map(List<CanalColumn> columns) {
        if (null == columns || columns.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> param = new HashMap<String, Object>();
        for (CanalColumn column : columns) {
            param.put(column.getName(), column.isNull() ? null : column.getValue());
        }
        return param;
    }
	/**
	 * 数据库字段转换成属性名,create_time->createTime
	 * @param dbName
	 * @return
	 */
	public static String formatJavaName(String dbName){
		StringBuffer sf = new StringBuffer();
		int count = 0;
		char before = 'a';
		for(char c: dbName.toLowerCase().toCharArray()){
			count++;
			if(c!='_'){
				if(before=='_' && count>2){
					sf.append(Character.toUpperCase(c));
				}else{
					sf.append(c);
				}
			}
			before = c;
		}
		return sf.toString();
	}

}