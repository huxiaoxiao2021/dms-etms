package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;

import java.util.List;

public interface JyCollectPackageScanRecordService {
    int saveJyCollectPackageRecord(JyCollectPackageEntity jyCollectPackageEntity);

    int editJyCollectPackageRecord(JyCollectPackageEntity jyCollectPackageEntity);

    JyCollectPackageEntity queryJyCollectPackageRecord(JyCollectPackageEntity query);

    List<JyCollectPackageEntity> listJyCollectPackageRecord(JyCollectPackageEntity query);

}
