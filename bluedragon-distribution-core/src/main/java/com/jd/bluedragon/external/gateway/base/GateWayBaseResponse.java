package com.jd.bluedragon.external.gateway.base;

import java.io.Serializable;

/**
 * 基础返回结果
 * @author : xumigen
 * @date : 2020/1/2
 */
public class GateWayBaseResponse<E> implements Serializable{
    private static final long serialVersionUID = 1L;

    public static final Integer CODE_SUCCESS = 1;
    public static final String MESSAGE_SUCCESS = "success";

    public static final Integer CODE_FAIL = -1;
    public static final String MESSAGE_FAIL = "系统异常！";
    public static final String MESSAGE_BOX_SEND_FAIL = "箱子已发货！";
    public static final String MESSAGE_BOX_MAX_FAIL= "箱子集包数量超过2万上限！";

    public static final Integer CODE_ERROR = -2;
    public static final String MESSAGE_ERROR = "invalid parameter";
    public static final String MESSAGE_BOX_ERROR = "箱号参数为空！";
    public static final String MESSAGE_PACKAGECODE_ERROR = "包裹号参数为空！";
    public static final String MESSAGE_START_SITE_ERROR = "始发站点参数为空！";
    public static final String MESSAGE_BOX_SITE_ERROR = "箱子始发地与集包始发地不一致！";
    public static final String  MESSAGE_OPERATION_TYPE_ERROR = "不支持的操作类型！";

    public static final Integer CODE_CONFIRM = -3;
    public static final String  MESSAGE_CONFIRM = "Data does not exist";
    public static final String  MESSAGE_BOX_CONFIRM = "箱子不存在！";
    public static final String  MESSAGE_START_SITE_CONFIRM = "始发站点不存在！";
    public static final String  MESSAGE_END_SITE_CONFIRM = "目的站点不存在！";
    public static final String  MESSAGE_BOX_PACKAGE_CONFIRM = "箱包关系不存在，无法取消集包！";
    public static final String  MESSAGE_START_SITE_TYPE_CONFIRM = "始发站点非经济网网点！";


    /** 响应状态码 */
    protected Integer resultCode;

    /** 响应消息 */
    protected String message;

    /** 响应数据 */
    protected E data;
    /**
     * 构造方法，默认为成功
     */
    public GateWayBaseResponse() {
        init(CODE_SUCCESS,MESSAGE_SUCCESS);
    }
    /**
     * 指定code和message的构造方法
     * @param code
     * @param message
     */
    public GateWayBaseResponse(Integer code, String message) {
        this.init(code, message);
    }
    /**
     * 指定code、message、data的构造方法
     * @param code
     * @param message
     * @param data
     */
    public GateWayBaseResponse(Integer code, String message, E data) {
        this.init(code, message, data);
    }
    /**
     * 初始化方法
     * @param resultCode
     */
    public void init(Integer resultCode) {
        this.resultCode = resultCode;
    }
    public void init(Integer resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }
    public void init(Integer resultCode, String message, E data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }
    /**
     * 状态判断
     * @return
     */
    public boolean isSucceed() {
        return CODE_SUCCESS.equals(this.resultCode);
    }

    public boolean isFail() {
        return CODE_FAIL.equals(this.resultCode);
    }

    public boolean isError() {
        return CODE_ERROR.equals(this.resultCode);
    }
    /**
     * 状态转换
     */
    public void toSucceed() {
        init(CODE_SUCCESS);
    }
    /**
     * 状态转换并设置返回信息
     * @param message
     */
    public void toSucceed(String message) {
        init(CODE_SUCCESS,message);
    }
    public void toFail() {
        init(CODE_FAIL);
    }
    public void toFail(String message) {
        init(CODE_FAIL,message);
    }
    public void toError() {
        init(CODE_ERROR);
    }
    public void toError(String message) {
        init(CODE_ERROR,message);
    }

    public void toConfirm(){
        init(CODE_CONFIRM);
    }
    public void toConfirm(String message){
        init(CODE_CONFIRM,message);

    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the data
     */
    public E getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(E data) {
        this.data = data;
    }
}
