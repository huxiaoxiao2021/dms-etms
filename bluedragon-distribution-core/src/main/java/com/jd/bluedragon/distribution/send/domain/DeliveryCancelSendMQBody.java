package com.jd.bluedragon.distribution.send.domain;

import java.util.Date;

/**
 * 取消发货MQ消息体
 * <p>
 * <p>
 * Created by lixin39 on 2018/2/22.
 */
public class DeliveryCancelSendMQBody {
    /**
     * 包裹号
     */
    private String packageBarcode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 取消发货实操时间
     */
    private Date operateTime;

    public String getPackageBarcode() {
        return packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
