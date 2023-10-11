package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyBizTaskCollectPackageDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JyBizTaskCollectPackageServiceImpl implements JyBizTaskCollectPackageService{
    @Autowired
    JyBizTaskCollectPackageDao jyBizTaskCollectPackageDao;

    @Override
    public JyBizTaskCollectPackageEntity findByBizId(String bizId) {
        return jyBizTaskCollectPackageDao.findByBizId(bizId);
    }
}
