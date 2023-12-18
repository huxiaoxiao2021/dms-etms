package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingGoodsRes;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodAggsDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingSendGoodAggsDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:16
 * @Description
 */
public interface JyPickingTaskAggsService {
    /**
     * 修改统计数据
     */
    void saveCacheAggStatistics(PickingGoodsReq request, PickingGoodsRes resData, JyBizTaskPickingGoodEntity entity);

    /**
     * 提货任务维度统计数据
     * @param curSiteId
     * @param bizId
     * @return
     */
    PickingGoodAggsDto findTaskPickingAgg(Integer curSiteId, String bizId);

    /**
     * 发货流向提货任务统计
     * @param curSiteId
     * @param nextSiteId
     * @param bizId
     * @return
     */
    PickingSendGoodAggsDto findTaskPickingSendAgg(Integer curSiteId, Integer nextSiteId, String bizId);

    /**
     * 统计数据count回刷
     * @param bizId
     */
    void aggRefresh(String bizId, Long nextSiteId);
}
