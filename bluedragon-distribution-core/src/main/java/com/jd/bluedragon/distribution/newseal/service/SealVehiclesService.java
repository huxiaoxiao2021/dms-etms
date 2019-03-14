package com.jd.bluedragon.distribution.newseal.service;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

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
}
