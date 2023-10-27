package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyCollectPackageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JyCollectPackageScanRecordServiceImpl implements JyCollectPackageScanRecordService{

    @Autowired
    JyCollectPackageDao jyCollectPackageDao;
    @Override
    public int saveJyCollectPackageRecord(JyCollectPackageEntity jyCollectPackageEntity) {
        return jyCollectPackageDao.insertSelective(jyCollectPackageEntity);
    }

    @Override
    public int editJyCollectPackageRecord(JyCollectPackageEntity jyCollectPackageEntity) {
        return jyCollectPackageDao.updateByPrimaryKeySelective(jyCollectPackageEntity);
    }

    @Override
    public JyCollectPackageEntity queryJyCollectPackageRecord(JyCollectPackageEntity query) {
        return jyCollectPackageDao.queryJyCollectPackageRecord(query);
    }
}
