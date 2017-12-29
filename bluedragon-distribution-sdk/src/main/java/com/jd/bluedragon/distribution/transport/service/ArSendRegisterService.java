package com.jd.bluedragon.distribution.transport.service;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: ArSendRegisterService
 * @Description: 发货登记表--Service接口
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public interface ArSendRegisterService extends Service<ArSendRegister> {
    /**
     * 从发货登记表中获取已经登记的所有始发城市的信息
     * @return
     */
    public List<City> queryStartCityInfo() ;

    /**
     * 从发货登记表中获取已经登记的所有目的城市的信息
     * @return
     */
    public List<City> queryEndCityInfo();

    /**
     * 从发货登记表中获取24小时内到达所选城市的航班/铁路信息
     * @param arSendRegister
     * @return
     */
    public List<ArSendRegister> queryWaitReceive(ArSendRegister arSendRegister);
}
