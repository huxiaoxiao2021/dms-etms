package com.jd.bluedragon.distribution.carSchedule.domain;

import com.jd.bluedragon.utils.DateHelper;

import java.util.Date;

/**
 * TMS取消发车实体
 * Created by wuzuxiang on 2017/3/6.
 */
public class CancelScheduleTo {

    /**
     * 发车条码
     */
    private String sendCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 取消发车的操作时间(消息内容对应的对方是时间戳)
     */
    private Long operateTime;

    public String getSendCarCode() {
        return sendCarCode;
    }

    public void setSendCarCode(String sendCarCode) {
        this.sendCarCode = sendCarCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CancelScheduleTo{" + "sendCarCode='" + sendCarCode + '\'' + ", vehicleNumber='" + vehicleNumber + '\'' + ", operateTime="
                + operateTime + '}';
    }

    /** 获取Date类型的时间 **/
    public Date getOperateTimeDate(){
        return DateHelper.toDate(operateTime);
    }
}
