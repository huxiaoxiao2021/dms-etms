package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

/**
 * @ClassName SendAbnormalPackRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:55
 **/
public class SendAbnormalReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -7121343145796741992L;

    /**
     * send_detail业务主键
     */
    private String sendDetailBizId;

    /**
     * 发货任务主键
     */
    private String sendVehicleBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 是否强制前往封车 默认：false
     */
    private Boolean forceGoToSeal = false;


    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Boolean getForceGoToSeal() {
        return forceGoToSeal;
    }

    public void setForceGoToSeal(Boolean forceGoToSeal) {
        this.forceGoToSeal = forceGoToSeal;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
