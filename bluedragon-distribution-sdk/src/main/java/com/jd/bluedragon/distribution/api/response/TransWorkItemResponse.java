package com.jd.bluedragon.distribution.api.response;

/**
 * 根据任务简码查询任务信息返回对象
 * Created by shipeilin on 2018/1/16.
 */
public class TransWorkItemResponse extends NewSealVehicleResponse{

    private static final long serialVersionUID = 1L;

    /** 车牌号 */
    private String vehicleNumber;

    /** 运输方式 */
    private Integer transType;

    public TransWorkItemResponse(){

    }

    public TransWorkItemResponse(Integer code, String message){
        super(code, message);
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }
}
