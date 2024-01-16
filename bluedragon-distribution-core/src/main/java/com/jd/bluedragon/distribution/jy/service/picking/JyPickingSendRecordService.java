package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
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
     * 查询最近的已提任务
     * @param curSiteId
     * @param bizId  可选
     * @param barCode
     * @return
     */
    JyPickingSendRecordEntity latestPickingRecord(Long curSiteId, String bizId, String barCode);

    /**
     * 统计
     * @param siteId 操作场地
     * @param bizId 提货任务bizId
     * @param nextSiteId  发货流向，非空是统计该流向发货数据
     */
//    PickingGoodTaskStatisticsDto statisticsByBizId(Long siteId, String bizId, Long nextSiteId);

    /**
     * 已提件数
     * @param bizId
     * @param siteId
     * @return
     */
    Integer countTaskRealScanItemNum(String bizId, Long siteId);
    /**
     * 待提件数
     * @param bizId
     * @param siteId
     * @return
     */
    Integer countTaskWaitScanItemNum(String bizId, Long siteId);

    public JyPickingSendRecordEntity fetchByPackageCodeAndCondition(Long curSiteId, String packageCode, String bizId);
    /**
     * 待提明细初始化或修改
     * @param detailInitDto
     */
    void initOrUpdateNeedScanDetail(PickingGoodTaskDetailInitDto detailInitDto);

    /**
     * 提货扫描保存
     * @param scanDto
     */
    void pickingRecordSave(JyPickingGoodScanDto scanDto);
}
