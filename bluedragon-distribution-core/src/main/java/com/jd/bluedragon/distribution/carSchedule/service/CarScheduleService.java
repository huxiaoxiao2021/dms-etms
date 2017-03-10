package com.jd.bluedragon.distribution.carSchedule.service;

import com.jd.bluedragon.distribution.carSchedule.domain.CancelScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleTo;
import com.jd.bluedragon.distribution.send.domain.SendDetail;

import java.util.Date;
import java.util.List;

/**
 * Created by wuzuxiang on 2017/3/6.
 */
public interface CarScheduleService {

    /**
     * 持久化消息数据
     * @param carScheduleTo
     */
    void persistData(CarScheduleTo carScheduleTo);

    /**
     * 取消发车消息
     */
    Boolean cancelSchedule(CancelScheduleTo cancelScheduleTo);

    /**
     * 根据车牌号获取车辆的线路类型
     */
    Integer routeTypeByVehicleNo(String vehicleNo);

    /**
     * 根据车牌号以及分拣中心ID获取车辆的线路类型
     */
    Integer routeTypeByVehicleNoAndSiteCode(String vehicleNo,String siteCode);

    /**
     * 根据车牌号获取车辆的包裹数量（值得注意的是同一辆车中并不是所有的包裹都给到一个目的分拣中心，有可能是两个）
     */
    Integer packageNumByVehicleNo(String vehicleNo);

    /**
     * 根据车牌号和分拣中心ID获取车辆的包裹数量（值得注意的是同一辆车中并不是所有的包裹都给到一个目的分拣中心，有可能是两个）
     */
    Integer packageNumByVehicleNoAndSiteCode(String vehicleNo,String siteCode);

    /**
     * 根据车牌号获取当前分拣中心本次车辆任务的载货量明细
     */
    List<SendDetail> sendDetailByCar(String vehicleNo , Integer siteCode);

    /**
     * 根据车牌号获取车辆的运输包裹信息
     */
    CarScheduleTo packageInfoByVehicleNo(String vehicleNo,Integer siteCode);

    /**
     * 根据车牌号获取车辆的预计达到时间
     */
    Date expectArriveTimeByVehicleNo(String vehicleNo);
}
