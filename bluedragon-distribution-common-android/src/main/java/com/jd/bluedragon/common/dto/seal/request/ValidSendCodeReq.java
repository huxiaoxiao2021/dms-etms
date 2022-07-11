package com.jd.bluedragon.common.dto.seal.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.Date;

public class ValidSendCodeReq extends BaseReq implements Serializable {


    private static final long serialVersionUID = 8030587778588887945L;

    /**
     * 封车方式
     *  10：按运力
     *  20：按任务
     *  @see com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum
     * */
    private Integer sealCarType;
    /**
     * 封车来源：
     *  10：普通
     *  20：传摆
     *  @see com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum
     * */
    private Integer sealCarSource;

    /**
     * 运力编码
     * */
    private String transportCode;
    /**
     * 车牌号
     * */
    private String vehicleNumber;

    /**
     * 批次号
     */
    private String sendCode;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getSealCarType() {
        return sealCarType;
    }

    public void setSealCarType(Integer sealCarType) {
        this.sealCarType = sealCarType;
    }

    public Integer getSealCarSource() {
        return sealCarSource;
    }

    public void setSealCarSource(Integer sealCarSource) {
        this.sealCarSource = sealCarSource;
    }



    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }






}
