package com.jd.bluedragon.distribution.newseal.service;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: PreSealVehicleService
 * @Description: 预封车数据表--Service接口
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
public interface PreSealVehicleService extends Service<PreSealVehicle> {

    /**
     * 新增一条预封车数据
     * @param preSealVehicle
     * @return
     */
    boolean insert(PreSealVehicle preSealVehicle);

    /**
     * 新增一条预封车数据,并取消之前的预封车（取消条件：始发，目的，状态预封车）
     * @param preSealVehicle
     * @return
     */
    boolean cancelPreSealBeforeInsert(PreSealVehicle preSealVehicle);

    /**
     * 根据ID更新预封车数据
     * @param preSealVehicle
     * @return
     */
    boolean updateById(PreSealVehicle preSealVehicle);

    /**
     * 根据Id更新单条预封车数据状态
     * @param id
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    boolean updateStatusById(Long id, String updateUserErp, String updateUserName, SealVehicleEnum status);

    /**
     * 根据Ids批量更新预封车数据状态
     * @param ids
     * @param updateUserErp
     * @param updateUserName
     * @param status
     * @return
     */
    int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, SealVehicleEnum status);

    /**
     * 根据始发目的查询预封车状态的数据
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 查询场地在startDate以内的预封车数据
     *
     * @param createSiteCode
     * @return
     */
    Map<Integer, PreSealVehicle> queryBySiteCode(Integer createSiteCode);

    /**
     * 根据始发查询当天已使用预封车的运力编码
     * @param createSiteCode
     * @return
     */
    List<String> findTodayUsedTransports(Integer createSiteCode);
}
