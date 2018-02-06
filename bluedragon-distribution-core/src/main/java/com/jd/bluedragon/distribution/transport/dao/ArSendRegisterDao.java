package com.jd.bluedragon.distribution.transport.dao;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.ql.dms.common.domain.City;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: ArSendRegisterDao
 * @Description: 发货登记表--Dao接口
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public interface ArSendRegisterDao extends Dao<ArSendRegister> {

    /**
     * 根据ids批量获取
     * @param ids
     * @return
     */
    List<ArSendRegister> getList(List<Long> ids);

    /**
     * 根据IDS删除
     *
     * @param ids
     * @param userCode
     * @return
     */
    int deleteByIds(List<Long> ids, String userCode);

    /**
     * 从发货登记表中获取所有的已经登记的始发城市的信息
     * @return
     */
    List<ArSendRegister> queryStartCityInfo();

    /**
     * 从发货登记表中获取所有的已经登记的目的城市的信息
     * @return
     */
    List<ArSendRegister> queryEndCityInfo();

    /**
     * 从发货登记表中获取24小时内到达所选城市的航班/铁路信息
     * @param arSendRegister
     * @return
     */
    List<ArSendRegister> queryWaitReceive(ArSendRegister arSendRegister);

}
