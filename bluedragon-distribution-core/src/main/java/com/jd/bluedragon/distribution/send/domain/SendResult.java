package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2015/5/27.
 */
public class SendResult implements Serializable {
    private static final long serialVersionUID = -5706252377345301775L;
    
    public static final Integer CODE_OK = 1;
    public static final String MESSAGE_OK = "发货成功";
    
    public static final Integer CODE_SERVICE_ERROR = 3;
    public static final String MESSAGE_SERVICE_ERROR = "服务异常";
    
    public static final Integer CODE_SENDED = 2;
    public static final String MESSAGE_SENDED = "箱子已经在该批次中发货";

    public SendResult(){

    }

    public SendResult(Integer k,String v){
        this.key=k;
        this.value=v;
    }
    public SendResult(Integer k,String v,Integer interceptResultCode,Integer presortingSiteCode){
        this.key=k;
        this.value=v;
        this.interceptCode=interceptResultCode;
        this.receiveSiteCode=presortingSiteCode;
    }
    /**
     * 操作结果【1：发货成功  2：发货失败  4：需要用户确认】
     */
    private Integer key;

    /**
     * 提示信息
     */
    private String value;

    /**
     * 收货站点
     */
    private Integer receiveSiteCode;

    /**
     *分拣拦截编号
     */
    private Integer interceptCode;

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
