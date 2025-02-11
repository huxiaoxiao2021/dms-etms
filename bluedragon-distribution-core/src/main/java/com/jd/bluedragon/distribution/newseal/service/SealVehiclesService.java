package com.jd.bluedragon.distribution.newseal.service;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;
import java.util.Set;

/**
 *
 * @ClassName: SealVehiclesService
 * @Description: 封车数据表--Service接口
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public interface SealVehiclesService extends Service<SealVehicles> {

    /**
     * 更新解封车数据信息
     * @param sealVehicles
     * @return
     */
    boolean updateDeSealBySealDataCode(List<SealVehicles> sealVehicles);

    /**
     * 按照批次号更新数据状态
     * @param sealVehicles
     * @return
     */
    boolean updateBySealDataCode(List<SealVehicles> sealVehicles);

    /**
     * 根据始发查询当天已使用预封车的运力编码
     * @param createSiteCode
     * @return
     */
    List<String> findTodayUsedTransports(Integer createSiteCode);

    /**
     * 根据封车的业务数据查询已封车的数据
     * @param sendCodeSet
     * @return
     */
    List<String> findBySealDataCodes(Set<String> sendCodeSet);
}
