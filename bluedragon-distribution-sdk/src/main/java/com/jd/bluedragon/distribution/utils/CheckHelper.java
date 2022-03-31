package com.jd.bluedragon.distribution.utils;

import java.util.regex.Pattern;

import com.jd.bluedragon.distribution.api.response.base.Result;

/**
 * 校验工具类
 * @author wuyoude
 *
 */
public class CheckHelper {
    /**
     * 网格号
     */
    public static final Pattern GRID_NO_REGEX = Pattern.compile("^0[1-9]|[1-9][0-9]$");
	/**
	 * 字符串校验，不为空并且长度不超过指定的length
	 * @param <T>
	 * @param name 字段描述
	 * @param value 值
	 * @param checkResult 返回结果
	 * @return
	 */
	public static <T> Result<T> checkStr(String name,String value,int length,Result<T> checkResult) {
		Result<T> result = checkResult;
		if(result == null) {
			result = Result.success();
		}
		String preStr = name;
		if(preStr == null || preStr.trim().length() == 0) {
			preStr = "字段值";
		}
		if(value == null || value.trim().length() == 0) {
			return result.toFail(preStr.concat("为空！"));
		}
		if(length <= 0) {
			length = 1;
		}
		if(value.trim().length() > length) {
			return result.toFail(preStr.concat("长度超过"+length+"位！"));
		}
		return result;
	}
	/**
	 * 数字类型校验
	 * @param <T>
	 * @param name
	 * @param value
	 * @param min
	 * @param max
	 * @param checkResult
	 * @return
	 */
	public static <T> Result<T> checkInteger(String name,Integer value,int min,int max,Result<T> checkResult) {
		Result<T> result = checkResult;
		if(result == null) {
			result = Result.success();
		}
		String preStr = name;
		if(preStr == null || preStr.trim().length() == 0) {
			preStr = "字段值";
		}
		if(value == null) {
			return result.toFail(preStr.concat("为空！"));
		}
		if(value < min || value > max) {
			return result.toFail(preStr.concat("只能是["+min+"~"+max+"]的值！"));
		}
		return result;
	}	
	/**
	 * 网格号校验
	 * @param <T>
	 * @param gridNo 值
	 * @param checkResult 返回结果
	 * @return
	 */
	public static <T> Result<T> checkGridNo(String gridNo,Result<T> checkResult) {
		Result<T> result = checkResult;
		if(result == null) {
			result = Result.success();
		}
		String preStr = "网格号";
		if(gridNo == null || gridNo.trim().length() == 0) {
			return result.toFail(preStr.concat("为空！"));
		}
		if(!GRID_NO_REGEX.matcher(gridNo).matches()) {
			return result.toFail(preStr.concat("只能是01~99！"));
		}
		return result;
	}
}
