package com.jd.bluedragon.distribution.api.response.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 基础结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-16 20:18:38 周二
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -3656546124640167302L;

    /**
     * 泛型数据
     */
    private T data;

    /**
     * 返回码
     */
    private int code = ResultCodeConstant.SUCCESS;

    /**
     * 消息
     */
    private String message = "";

    /**
     * 响应时间毫秒级时间戳
     */
    private Long responseTimeMillSeconds;

    /**
     * 响应时间格式化字符串
     */
    private String responseTimeFormative;

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Long getResponseTimeMillSeconds() {
        return responseTimeMillSeconds;
    }

    public Result<T> setResponseTimeMillSeconds(Long responseTimeMillSeconds) {
        this.responseTimeMillSeconds = responseTimeMillSeconds;
        return this;
    }

    public String getResponseTimeFormative() {
        return responseTimeFormative;
    }

    public Result<T> setResponseTimeFormative(String responseTimeFormative) {
        this.responseTimeFormative = responseTimeFormative;
        return this;
    }

    /**
     * 无参构造方法
     */
    public Result() {
        super();
    }

    /**
     * 构造方法
     * @param data 数据
     * @param code 返回码
     * @param message 消息
     */
    public Result(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     * @param data 数据
     */
    public Result(T data) {
        this.data = data;
    }

    /**
     * 构造方法
     * @param code 返回码
     * @param message 消息
     */
    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设置result值
     * @param code 返回码
     * @param message 消息
     */
    public static <T> Result<T> init(int code, String message, T data) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param <T>  返回结果泛型
     * @return Result对象
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setMessage("ok");
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param data 数据
     * @param <T>  返回结果泛型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setData(data);
        result.setMessage("ok");
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param data    数据
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<T>();
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    /**
     * 返回成功结果，静态方法
     *
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<T>();
        result.setMessage(message);
        return result;
    }

    /**
     * 返回失败结果，静态方法
     *
     * @param code    返回码
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<T>(code, message);
    }

    /**
     * 返回失败结果，静态方法
     *
     * @param message 消息
     * @param <T>     返回结果泛型
     * @return Result对象
     */
    public static <T> Result<T> fail(String message) {
        return new Result<T>(ResultCodeConstant.FAIL, message);
    }

    /**
     * 置为成功
     */
    public Result<T> toSuccess(){
        this.code = ResultCodeConstant.SUCCESS;
        this.message = "ok";
        return this;
    }

    /**
     * 置为成功
     */
    public Result<T> toSuccess(String message){
        this.code = ResultCodeConstant.SUCCESS;
        this.message = message;
        return this;
    }

    /**
     * 置为成功
     */
    public Result<T> toSuccess(T data, String message) {
        this.code = ResultCodeConstant.SUCCESS;
        this.message = message;
        this.data = data;
        return this;
    }

    /**
     * 置为失败
     */
    public Result<T> toFail(){
        this.code = ResultCodeConstant.FAIL;
        this.message = "fail";
        return this;
    }

    /**
     * 置为失败
     */
    public Result<T> toFail(String message, Integer code) {
        this.code = code;
        this.message = message;
        return this;
    }

    /**
     * 置为失败
     */
    public Result<T> toFail(String message, Integer code, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        return this;
    }

    /**
     * 置为失败
     */
    public Result<T> toFail(String message){
        this.code = ResultCodeConstant.FAIL;
        this.message = message;
        return this;
    }

    /**
     * 是否成功
     * @return boolean 成功标志
     */
    public boolean isSuccess(){
        return this.code == ResultCodeConstant.SUCCESS;
    }

    /**
     * 是否失败
     * @return boolean 失败标志
     */
    public boolean isFail(){
        return !this.isSuccess();
    }

    /**
     * 是否空数据
     * @return boolean 空数据标志，true：空，false：不为空
     */
    public boolean isEmptyData(){
        if(this.data == null){
            return true;
        }
        if(this.data instanceof Collections){
            return ((Collection<?>) this.data).size() == 0;
        }
        if(this.data instanceof Map){
            return ((Map<?, ?>) this.data).size() == 0;
        }
        return false;
    }
}
