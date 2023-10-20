package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyBizTaskCollectPackageDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class JyBizTaskCollectPackageServiceImpl implements JyBizTaskCollectPackageService{
    @Autowired
    JyBizTaskCollectPackageDao jyBizTaskCollectPackageDao;

    @Override
    public JyBizTaskCollectPackageEntity findByBizId(String bizId) {
        return jyBizTaskCollectPackageDao.findByBizId(bizId);
    }

    @Override
    public JyBizTaskCollectPackageEntity findByBoxCode(String boxCode) {
        return jyBizTaskCollectPackageDao.findByBoxCode(boxCode);
    }

    @Override
    public Boolean save(JyBizTaskCollectPackageEntity record) {
        return jyBizTaskCollectPackageDao.insertSelective(record) > 0;
    }

    @Override
    public List<JyBizTaskCollectPackageEntity> pageQueryTask(JyBizTaskCollectPackageQuery query) {
        return jyBizTaskCollectPackageDao.pageQueryTask(query);
    }

    @Override
    public Long queryTaskCount(JyBizTaskCollectPackageQuery query) {
        return jyBizTaskCollectPackageDao.queryTaskCount(query);
    }

    @Override
    public Boolean updateById(JyBizTaskCollectPackageEntity entity) {
        return jyBizTaskCollectPackageDao.updateByPrimaryKeySelective(entity) > 0;
    }
}
