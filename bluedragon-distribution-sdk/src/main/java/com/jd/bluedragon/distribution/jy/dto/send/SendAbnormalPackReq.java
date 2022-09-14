package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;
import com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum;

import java.io.Serializable;

/**
 * @ClassName SendAbnormalPackRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:55
 **/
public class SendAbnormalPackReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -7121343145796741992L;


    private Integer pageNumber;

    private Integer pageSize;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    private ExcepScanTypeEnum excepScanTypeEnum;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;


    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public ExcepScanTypeEnum getExcepScanTypeEnum() {
        return excepScanTypeEnum;
    }

    public void setExcepScanTypeEnum(ExcepScanTypeEnum excepScanTypeEnum) {
        this.excepScanTypeEnum = excepScanTypeEnum;
    }
}
