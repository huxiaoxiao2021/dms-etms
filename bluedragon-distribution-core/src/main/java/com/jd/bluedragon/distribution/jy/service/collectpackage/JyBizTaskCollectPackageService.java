package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackage;

public interface JyBizTaskCollectPackageService {
    /**
     * 根据任务bizId查询 集包任务详情
     * @param bizId
     * @return
     */
    JyBizTaskCollectPackage findByBizId(String bizId);

}
