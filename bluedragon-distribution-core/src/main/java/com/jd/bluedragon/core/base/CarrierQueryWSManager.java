package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.CarrierDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.SimpleCarrierDto;
import com.jd.tms.basic.dto.TransportResourceDto;

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
}
