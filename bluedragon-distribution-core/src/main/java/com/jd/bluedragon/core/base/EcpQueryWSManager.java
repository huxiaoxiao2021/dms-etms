package com.jd.bluedragon.core.base;

import com.jd.tms.ecp.dto.AirPortDto;
import com.jd.tms.ecp.dto.AirTplBillDto;
import com.jd.tms.ecp.dto.BasicRailTrainDto;

import java.util.List;

/**
 * 运输铁路信息接口
 * @author : xumigen
 * @date : 2019/9/13
 */
public interface EcpQueryWSManager {

    /**
     * 根据查询条件获取列车车次信息列表
     * @param trainNumber 铁路车次号
     * @param beginCityId 始发城市
     * @param endCityId 目的城市
     * @return
     */
    BasicRailTrainDto getRailTrainListByCondition(String trainNumber, Integer beginCityId, Integer endCityId);

    /**
     * 根据航班号查起末机场列表
     * @param flightNumber
     * @return
     */
    List<AirPortDto> getAirPortListByFlightNumber(String flightNumber);

    /**
     * 根据主运单号查询主运单详情接口
     *
     * @param billCode 主运单/包裹号
     * @return
     */
    AirTplBillDto getAirTplBillDetailInfo(String billCode);
}
