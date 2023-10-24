package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;

import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-10-17 17:03
 */
public interface JyBizTaskCollectPackageFlowService {

    /**
     * 批量保存
     * @param flowList
     * @return
     */
    boolean batchInsert(List<JyBizTaskCollectPackageFlowEntity> flowList);

    /**
     * 根据集包任务ID查询流向信息
     * @return
     */
    List<JyBizTaskCollectPackageFlowEntity> queryListByBizIds(List<String> bizIds);
}
