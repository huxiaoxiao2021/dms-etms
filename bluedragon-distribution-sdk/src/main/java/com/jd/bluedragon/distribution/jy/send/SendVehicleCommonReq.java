package com.jd.bluedragon.distribution.jy.send;



import java.io.Serializable;

/**
 * @ClassName SendVehicleCommonRequest
 * @Description
 * @Author chenyaguo
 * @Date 2022/3/31 20:52
 **/
public class SendVehicleCommonReq implements Serializable {

    private static final long serialVersionUID = 8579173836769728478L;


    private Integer pageNumber;

    private Integer pageSize;

    /**
     * 业务主键
     */
    private String sendVehicleBizId;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 产品类型
     */
    private String productType;

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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}