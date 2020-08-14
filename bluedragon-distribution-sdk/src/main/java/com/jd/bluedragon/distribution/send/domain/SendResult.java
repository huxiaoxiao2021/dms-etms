package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangtingwei on 2015/5/27.
 */
public class SendResult implements Serializable {

    private static final long serialVersionUID = -5706252377345301775L;
    /**
     * 发货成功
     */
    public static final Integer CODE_OK = 1;
    public static final String MESSAGE_OK = "发货成功";
    /**
     * 服务异常
     */
    public static final Integer CODE_SERVICE_ERROR = 3;
    public static final String MESSAGE_SERVICE_ERROR = "服务异常";
    /**
     * 发货失败
     */
    public static final Integer CODE_SENDED = 2;
    public static final String MESSAGE_SENDED = "箱子已经在该批次中发货";
    public static final String FRESH_MESSAGE_SENDED = "当前批次为生鲜批次专用，而此单是非生鲜运单，请换普通批次";
    /**
     * 发货确认
     */
    public static final Integer CODE_CONFIRM = 4;

    /**
     * 发货警告,用于PDA发货加急提示语展示
     */
    public static final Integer CODE_WARN = 6;

    public SendResult() {

    }

    public SendResult(Integer k, String v) {
        this.key = k;
        this.value = v;
    }

    public SendResult(Integer k, String v, Integer interceptResultCode, Integer presortingSiteCode) {
        this.key = k;
        this.value = v;
        this.interceptCode = interceptResultCode;
        this.receiveSiteCode = presortingSiteCode;
    }

    /**
     * 操作结果【1：发货成功  2：发货失败  4：需要用户确认  6: 发货成功，但是有警告性提示信息展示】
     */
    private Integer key;

    /**
     * 提示信息
     */
    private String value;

    /**
     * 确认消息提示盒子
     */
    private List<ConfirmMsgBox> confirmMsgBox;

    /**
     * 收货站点
     */
    private Integer receiveSiteCode;

    /**
     * 分拣拦截编号
     */
    private Integer interceptCode;

    public void init(Integer key) {
        this.key = key;
    }

    public void init(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public void init(Integer k, String v, Integer interceptResultCode, Integer presortingSiteCode) {
        this.key = k;
        this.value = v;
        this.interceptCode = interceptResultCode;
        this.receiveSiteCode = presortingSiteCode;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<ConfirmMsgBox> getConfirmMsgBox() {
        return confirmMsgBox;
    }

    public void setConfirmMsgBox(List<ConfirmMsgBox> confirmMsgBox) {
        this.confirmMsgBox = confirmMsgBox;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getInterceptCode() {
        return interceptCode;
    }

    public void setInterceptCode(Integer interceptCode) {
        this.interceptCode = interceptCode;
    }

}
