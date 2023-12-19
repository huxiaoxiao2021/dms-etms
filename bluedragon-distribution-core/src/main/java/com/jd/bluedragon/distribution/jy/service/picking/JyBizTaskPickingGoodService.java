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
    JyBizTaskPickingGoodEntity generateManualCreateTask(CurrentOperate site, User user);

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
}
