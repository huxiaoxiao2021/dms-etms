package com.jd.bluedragon.distribution.collectNew.service;

import com.jd.bluedragon.distribution.collectNew.entity.*;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCancelScanDto;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/5/25 16:51
 * @Description 作业app集齐服务
 */
public interface JyScanCollectService {

    /**
     * 按任务维度插入一条集齐运单明细数据
     */
    void insertCollectionRecordDetail(JyScanCollectMqDto collectDto);

    /**
     * 按任务维度插入或修改一条集齐运单数据
     * @param collectRecordPo
     * @param insertFlag true 存在修改，不存在插入， false 存在修改，不存在不处理，用于取消扫描
     */
    void upInsertCollectionRecord(JyCollectRecordPo collectRecordPo, boolean insertFlag);

    List<JyCollectRecordStatistics> findBuQiWaybillByCollectionCodes(JyCollectRecordCondition condition);

    List<JyCollectRecordDetailPo> findByCollectionCodesAndAggCode(JyCollectRecordDetailCondition jqDetailQueryParam);

    /**
     * 查询所有不齐运单号
     * @param mqBody
     * @return
     */
    List<JyCollectRecordStatistics> getAllBuQiWaybillCodes(JySendCancelScanDto mqBody);

    List<JyCollectRecordDetailPo> findPageCollectDetailByCondition(JyCollectRecordDetailCondition dPo);

    void deleteByAggCode(JyCollectRecordDetailCondition condition);
    void deleteByScanCode(JyCollectRecordDetailCondition condition);

    List<JyCollectRecordPo> findPageCollectByCondition(JyCollectRecordCondition dPo);


    /**
     * 校验B网
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    String toBNetFlag(String waybillCode, String waybillSign);
}
