package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:13
 * @Description
 */
public interface JyBizTaskPickingGoodService {
    /**
     * 根据bizId待提货任务
     * [调用方自己考虑是否查询yn=0]
     * @param bizId
     * @param ignoreYn  true: 忽略yn   false or null：只查yn=1
     * @return
     */
    JyBizTaskPickingGoodEntity findByBizIdWithYn(String bizId, Boolean ignoreYn);

    /**
     * 自建提货任务生成
     * @return
     */
    JyBizTaskPickingGoodEntity generateManualCreateTask(CurrentOperate site, User user, Integer taskType);

    /**
     * 获取自建任务唯一bizId
     * @param isNoTaskFlag  true： 无任务bizId
     * @return
     */
    String genPickingGoodTaskBizId(Boolean isNoTaskFlag);

    /**
     * 根据bizId修改任务信息
     * @param entity
     * @return
     */
    int updateTaskByBizIdWithCondition(JyBizTaskPickingGoodEntityCondition entity);

    boolean updateStatusByBizId(String bizId, Integer status);

    /**
     * 根据场地查找最新的可发货自建任务
     * @param siteId
     * @return
     */
    JyBizTaskPickingGoodEntity findLatestEffectiveManualCreateTask(Long siteId);

    /**
     * 根据空铁批货流水号查询最近提货任务
     * 注：空铁业务特点，一批货为一组，一组存在多个场地提货，生成多个提货任务，一组任务唯一个整体，存在都存在，删除都删除
     * @param businessNumber
     * @return
     */
    JyBizTaskPickingGoodEntity findLatestTaskByBusinessNumber(String businessNumber);

    /**
     * 根据空铁批货流水号删除一批任务
     * @param businessNumber
     * @return
     */
    int deleteByBusinessNumber(String businessNumber);
}
