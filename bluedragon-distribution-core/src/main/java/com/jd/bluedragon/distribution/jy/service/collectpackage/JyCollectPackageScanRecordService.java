package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;

public interface JyCollectPackageScanRecordService {
    int saveJyCollectPackageRecord(JyCollectPackageEntity jyCollectPackageEntity);

    int editJyCollectPackageRecord(JyCollectPackageEntity jyCollectPackageEntity);
}
