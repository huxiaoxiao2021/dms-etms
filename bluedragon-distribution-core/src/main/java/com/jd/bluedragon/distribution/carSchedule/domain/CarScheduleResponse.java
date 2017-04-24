package com.jd.bluedragon.distribution.carSchedule.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * Created by wuzuxiang on 2017/3/10.
 */
public class CarScheduleResponse extends JdResponse {
    private static final long serialVersionUID = -7054157331566721963L;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 车辆的线路类型1.干线 2.支线 3.短驳 4.上门提送 5.摆渡 6.司机接货 8.站点接货 9.传站返回 10.长途传站 11.室内转战 16.摆渡返回
     */
    private Integer routeType;

    /**
     * 车载的全部包裹总量
     */
    private Integer totalPackageNum;

    /**
     * 当前分拣中心的载货总量
     */
    private Integer localPackageNum;

    /**
     * 当前分拣中心的载货明细
     */
//    private List<SendDetail> sendDetails;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Integer getRouteType() {
        return routeType;
    }

    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    public Integer getTotalPackageNum() {
        return totalPackageNum;
    }

    public void setTotalPackageNum(Integer totalPackageNum) {
        this.totalPackageNum = totalPackageNum;
    }

    public Integer getLocalPackageNum() {
        return localPackageNum;
    }

    public void setLocalPackageNum(Integer localPackageNum) {
        this.localPackageNum = localPackageNum;
    }
//
//    public List<SendDetail> getSendDetails() {
//        return sendDetails;
//    }
//
//    public void setSendDetails(List<SendDetail> sendDetails) {
//        this.sendDetails = sendDetails;
//    }
}
