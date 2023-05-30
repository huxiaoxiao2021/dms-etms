package com.jd.bluedragon.distribution.collection.service;

import com.jd.bluedragon.distribution.collection.entity.CollectionRecordCondition;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordDetailCondition;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordDetailPo;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordPo;
import com.jd.bluedragon.distribution.jy.dto.collectNew.JyScanCollectMqDto;

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
    void insertCollectionRecordDetailInBizId(JyScanCollectMqDto collectDto);
    /**
     * 按任务维度插入或修改一条集齐运单数据
     */
    void upInsertCollectionRecordInBizId(JyScanCollectMqDto collectDto);

    List<CollectionRecordPo> findCollectRecordByCondition(CollectionRecordCondition condition);

    List<CollectionRecordDetailPo> findCollectRecordDetailByCondition(CollectionRecordDetailCondition jqDetailQueryParam);
}
