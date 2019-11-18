package com.jd.bluedragon.distribution.transBillSchedule.service;

import com.jd.bluedragon.distribution.api.response.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;

/**
 * Created by wuzuxiang on 2017/4/26.
 */
public interface TransBillScheduleService {

    /**
     * pandaun 此运单号的派车单是不是与此箱号中的之前的运单的派车单号一致
     * @param request
     * @return
     */
    public Boolean checkSameScheduleBill(TransBillScheduleRequest request);

    /**
     * 获取派车单号，通过运单号
     * @return
     */
    String queryScheduleCode(String waybillCode);

    /**
     * 获取此运单号的路区字段
     * @param waybillCode
     * @return
     */
    public String queryTruckSpotByWaybillCode(String waybillCode);

    /**
     * 获取箱号对应的派车单号
     * @param boxCode
     * @return
     */
    public String getKey(String boxCode);

    /**
     * 设置派车单的redis记录，统一前缀
     * @param boxCode 箱号
     * @param waybillCode 包裹号运单号
     * @return
     */
    public void setKey(String boxCode,String waybillCode);

    /**
     * 检查该箱子的派车单信息是不是存在
     * @param boxCode
     * @return
     */
    public Boolean existsKey(String boxCode);

    /**
     * 删除该箱号的派车单信息
     * @param boxCode
     * @return
     */
    public boolean delete(String boxCode);

    /**
     * 获取派车单信息
     * @param request
     * @return
     */
    TransBillScheduleResponse checkScheduleBill(TransBillScheduleRequest request);

}
