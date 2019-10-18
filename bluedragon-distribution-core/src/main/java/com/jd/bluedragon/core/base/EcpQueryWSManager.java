package com.jd.bluedragon.core.base;

import com.jd.tms.ecp.dto.BasicRailTrainDto;

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
}
