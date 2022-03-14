package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;

/**
 * @ClassName VehicleBaseInfo
 * @Description
 * @Author wyh
 * @Date 2022/3/2 19:59
 **/
public class VehicleBaseInfo implements Serializable {

    private static final long serialVersionUID = -5679990549749395497L;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 线路类型描述
     */
    private String lineTypeName;

    /**
     * 是否抽检
     */
    private Boolean spotCheck;

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public Boolean getSpotCheck() {
        return spotCheck;
    }

    public void setSpotCheck(Boolean spotCheck) {
        this.spotCheck = spotCheck;
    }
}
