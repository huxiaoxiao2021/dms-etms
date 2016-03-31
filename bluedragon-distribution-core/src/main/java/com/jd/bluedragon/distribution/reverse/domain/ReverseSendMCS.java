package com.jd.bluedragon.distribution.reverse.domain;

import java.util.Date;

/**
 * Created by yangbo7 on 2016/3/16.
 */
public class ReverseSendMCS {

    private String pickWareCode;   //取件单号
    private String operatorName;   //操作人名
    private Integer operatorId;     //操作人ID
    private Date operateTime;    //操作时间
    private String sendCode;       //发货批次
    private String packageCode;    //包裹号

    public String getPickWareCode() {
        return pickWareCode;
    }

    public void setPickWareCode(String pickWareCode) {
        this.pickWareCode = pickWareCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
