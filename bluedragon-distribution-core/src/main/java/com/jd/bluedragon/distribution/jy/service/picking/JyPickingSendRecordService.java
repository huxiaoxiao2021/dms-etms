package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:29
 * @Description
 */
public interface JyPickingSendRecordService {

    /**
     * 根据待扫单据查询待提货任务bizId
     * [ ** 这里查待提， 非已提任务， 已提可能因为多提存在多个实际提货的任务]
     * @param curSiteId 待提货场地
     * @param barCode 待提货单据【包裹号或箱号...】
     * @return
     */
    String fetchWaitPickingBizIdByBarCode(Long curSiteId, String barCode);

    /**
     * 根据指定任务bizId和单据查询提货记录
     * @param curSiteId
     * @param barCode
     * @param bizId
     * @return
     */
    JyPickingSendRecordEntity fetchRealPickingRecordByBarCodeAndBizId(Long curSiteId, String barCode, String bizId);

    /**
     * 查询最近的已提任务
     * @param curSiteId
     * @param barCode
     * @return
     */
    JyPickingSendRecordEntity latestPickingRecord(Long curSiteId, String barCode);




}
