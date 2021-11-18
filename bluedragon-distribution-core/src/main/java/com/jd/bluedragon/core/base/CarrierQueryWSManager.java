package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.*;

import java.util.List;

public interface CarrierQueryWSManager {

    CommonDto<TransportResourceDto> getTransportResourceByTransCode(String capacityCode);

    /**
     * 根据条件模糊查询承运商
     * @see 'https://cf.jd.com/pages/viewpage.action?pageId=280673746'
     *
     * @param condition
     * @return
     */
    List<SimpleCarrierDto> queryCarrierByLikeCondition(CarrierDto condition);

    /**
     * 根据条件获取承运商司机
     * @param paramDto 入参
     * @return 结果
     * @author fanggang7
     * @time 2021-11-16 13:40:28 周二
     */
    CommonDto<CarrierDriverDto> getCarrierDriverByParam(CarrierDriverParamDto paramDto);
}
