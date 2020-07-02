package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.air.request.AirDepartRequest;
import com.jd.bluedragon.common.dto.air.request.AirPortRequest;
import com.jd.bluedragon.common.dto.air.request.AirTplBillRequest;
import com.jd.bluedragon.common.dto.air.request.TmsDictRequest;
import com.jd.bluedragon.common.dto.air.response.AirContrabandReason;
import com.jd.bluedragon.common.dto.air.response.AirPortResponseDto;
import com.jd.bluedragon.common.dto.air.response.AirTplBillRespDto;
import com.jd.bluedragon.common.dto.air.response.TmsDictDto;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;

import java.util.List;

/**
 * 新发货登记 发布物流网关 给安卓用
 * @author : xumigen
 * @date : 2019/11/4
 */
public interface AirNewPickupRegisterGateWayService {


    /**
     * 调用运输基础资料获取 货物类型
     * @param tmsDictRequest
     * @return
     */
    JdCResponse<List<TmsDictDto>> getGoodsTypeFromTms(TmsDictRequest tmsDictRequest);

    /**
     * 根据航班取始末机场
     * @param airPortRequest
     * @return
     */
    JdCResponse<List<AirPortResponseDto>> getAirPortListByFlightNumber(AirPortRequest airPortRequest);

    /**
     * 新分拣发货登记提交接口
     * @param airDepartRequest
     * @return
     */
    JdCResponse<String> submitSortAirDepartInfo(AirDepartRequest airDepartRequest);

    /**
     * 新分拣发货登记补交提交接口
     * @param airDepartRequest
     * @return
     */
    JdCResponse<String> supplementSortAirDepartInfo(AirDepartRequest airDepartRequest);

    /**
     * 根据主运单号查询主运单详情接口
     * @param airTplBillRequest
     * @return
     */
    JdCResponse<AirTplBillRespDto> getAirTplBillDetailInfo(AirTplBillRequest airTplBillRequest);

    /**
     * 获取运输方式变更原因列表
     * @param request
     * @return
     */
    JdCResponse<List<AirContrabandReason>> getArContrabandReasonList(String request);
}
