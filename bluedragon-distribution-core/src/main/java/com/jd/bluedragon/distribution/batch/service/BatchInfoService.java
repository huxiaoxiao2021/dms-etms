package com.jd.bluedragon.distribution.batch.service;


import com.jd.bluedragon.distribution.batch.domain.BatchInfo;

import java.util.Date;
import java.util.List;

/**
 * Created by yanghongqiang on 14-8-1.
 */
public interface BatchInfoService {

    Integer add(BatchInfo batchInfo);

    List<BatchInfo> findBatchInfoes(BatchInfo batchInfo);

    BatchInfo findBatchInfoByCode(String batchInfoCode);
    
    List<BatchInfo> findBatchInfo(BatchInfo batchInfo);
    
    List<BatchInfo> findAllBatchInfo(BatchInfo batchInfo);

    List<BatchInfo> findMaxCreateTimeBatchInfo(BatchInfo batchInfo);

    /**
     * 获取当前波次信息
     * @param sortingCenterId    分拣中心ID
     * @param operateTime        操作时间
     * @return
     */
    BatchInfo findCurrentBatchInfo(Integer sortingCenterId,Date operateTime);
}
