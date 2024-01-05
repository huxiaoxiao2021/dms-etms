package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res.PickingGoodsRes;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingTaskAggQueryDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodAggsDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingSendGoodAggsDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;

import java.util.List;

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

    /**
     * 根据bizId集合查询初始化之后的待提数量
     * 【初始化之前入口：取DB中上游发货登记数量】
     * ** 注： 这里查的任务未开始前的应提总数，非实时待提   实时待提数量=应提总数-应提已提总数
     * @param bizIdList
     * @param siteId
     * @param sendNextSiteId   选填，非空，会查该流向待发数量
     * @return
     */
    List<PickingSendGoodAggsDto> waitPickingInitTotalNum(List<String> bizIdList, Long siteId, Long sendNextSiteId);

    List<String> pageRecentWaitScanEqZero(JyPickingTaskAggQueryDto queryDto);
}
