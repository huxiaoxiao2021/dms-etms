package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

/**
 * sql相关工具类
 * @author wuyoude
 *
 */
public class SqlUtils {
	/**
	 * 降序-desc
	 */
	private static String ORDER_DESC = "desc";
	/**
	 * 降序-页面传入值descending
	 */
	private static String ORDER_DESCENDING = "descending";
	/**
	 * 升序-asc
	 */
	private static String ORDER_ASC = "asc";
	/**
	 * 升序-ascending
	 */
	private static String ORDER_ASCENDING = "ascending";
	/**
	 * 标识排序字段-orderColumn
	 */
	private static String KEY_ORDER_COLUMN = "orderColumn";
	/**
	 * 标识升序降序-orderState
	 */
	private static String KEY_ORDER_STATE = "orderState";
	/**
	 * 生成排序sql,示例：[{orderColumn:"column1",orderState:"descending"},{orderColumn:"column2",orderState:"asc"}],{"column1":"db_column1","column2":"db_column2"}，返回结果 db_column1 desc,db_column2 asc
	 * @param orderByList 排序列表
	 * @param columNameMap 字段映射关系
	 * @return
	 */
	public static String genOrderBySql(List<Map<String,String>> orderByList,Map<String,String> columNameMap) {
		if(CollectionUtils.isEmpty(orderByList)) {
			return null;
		}
		boolean isFirst = true;
		StringBuffer sf = new StringBuffer();
		for(Map<String,String> item : orderByList) {
			String orderColumn = item.get(KEY_ORDER_COLUMN);
			if(!CollectionUtils.isEmpty(columNameMap) && columNameMap.containsKey(orderColumn)) {
				orderColumn = columNameMap.get(orderColumn);
			}
			String orderState = item.get(KEY_ORDER_STATE);
			if(ORDER_DESC.equalsIgnoreCase(orderState) || ORDER_DESCENDING.equalsIgnoreCase(orderState)) {
				orderState = ORDER_DESC;
			}else if (ORDER_ASC.equalsIgnoreCase(orderState) || ORDER_ASCENDING.equalsIgnoreCase(orderState)) {
				orderState = ORDER_ASC;
			}else {
				orderState = null;
			}
			if(StringHelper.isNotEmpty(orderColumn) && StringHelper.isNotEmpty(orderState)) {
				if(!isFirst) {
					sf.append(",");
				}else {
					isFirst = false;
				}
				sf.append(orderColumn);
				sf.append(" ");
				sf.append(orderState);
			}
		}
		if(sf.length() > 0) {
			return sf.toString();
		}
		return null;
	}
	/**
	 * 格式化orderByList列表,示例：[{orderColumn:"column1",orderState:"descending"},{orderColumn:"column2",orderState:"asc"}],{"column1":"db_column1","column2":"db_column2"}，返回结果 [{orderColumn:"column1",orderState:"desc"},{orderColumn:"column2",orderState:"asc"}]
	 * @param orderByList 排序列表
	 * @param columNameMap 字段映射关系
	 * @return
	 */
	public static List<Map<String,String>> formatOrderByList(List<Map<String,String>> orderByList,Map<String,String> columNameMap) {
		if(CollectionUtils.isEmpty(orderByList)) {
			return null;
		}
		List<Map<String,String>> formatOrderByList = new ArrayList<Map<String,String>>();
		for(Map<String,String> item : orderByList) {
			String orderColumn = item.get(KEY_ORDER_COLUMN);
			if(!CollectionUtils.isEmpty(columNameMap) && columNameMap.containsKey(orderColumn)) {
				orderColumn = columNameMap.get(orderColumn);
			}
			String orderState = item.get(KEY_ORDER_STATE);
			if(ORDER_DESC.equalsIgnoreCase(orderState) || ORDER_DESCENDING.equalsIgnoreCase(orderState)) {
				orderState = ORDER_DESC;
			}else if (ORDER_ASC.equalsIgnoreCase(orderState) || ORDER_ASCENDING.equalsIgnoreCase(orderState)) {
				orderState = ORDER_ASC;
			}else {
				orderState = null;
			}
			if(StringHelper.isNotEmpty(orderColumn) && StringHelper.isNotEmpty(orderState)) {
				Map<String,String> newItem = new HashMap<String,String>();
				newItem.put(KEY_ORDER_COLUMN, orderColumn);
				newItem.put(KEY_ORDER_STATE, orderState);
				formatOrderByList.add(newItem);
			}
		}
		return formatOrderByList;
	}
}
