package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * 异常-客服回传MQ实体
 *
 * @author chenyaguo
 * @date 2023/8/2 10:36 AM
 */
public class JyExpCustomerReturnMQ implements Serializable {

    private static final long serialVersionUID = 100L;

    //消息id
    private String id;

    //业务码（回传值）
    private String businessId;

    //客服输入描述 回传客服处理描述
    private String desc;

    //操作人erp账号    回传erp或者姓名
    private String erpCode;

    //事件编号 回传客服事件号
    private String eventNo;
    //线索值  回传exptId,和传入值一致
    private String exptId;

    //唯一码
    private String msgId;

    //反馈结果类型 回传客服处理结果，具体值需要和客服确认
    private String resultType;

    //发送时间
    private String sendTime;
    //时间戳
    private String timeStamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public String getExptId() {
        return exptId;
    }

    public void setExptId(String exptId) {
        this.exptId = exptId;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
