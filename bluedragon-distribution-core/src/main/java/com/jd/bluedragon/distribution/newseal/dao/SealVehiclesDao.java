package com.jd.bluedragon.distribution.newseal.dao;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: SealVehiclesDao
 * @Description: 封车数据表--Dao接口
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public interface SealVehiclesDao extends Dao<SealVehicles> {

    /**
     * 根据封车业务数据更新封车信息
     * @param sealVehicles
     * @return
     */
    boolean updateBySealDataCode(SealVehicles sealVehicles);

    /**
     * 根据起始时间查询场地已使用预封车的运力编码
     * @param createSiteCode
     * @return
     */
    List<String> findUsedTransports(Integer createSiteCode, Date startDate);
}
