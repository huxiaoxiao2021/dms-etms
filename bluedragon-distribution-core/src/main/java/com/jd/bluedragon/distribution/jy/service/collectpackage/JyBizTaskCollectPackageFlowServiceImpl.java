package com.jd.bluedragon.distribution.jy.service.collectpackage;

import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;
import com.jd.bluedragon.distribution.jy.dao.collectpackage.JyBizTaskCollectPackageFlowDao;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-10-17 17:03
 */
@Service
@Slf4j
public class JyBizTaskCollectPackageFlowServiceImpl implements JyBizTaskCollectPackageFlowService {
    
    @Autowired
    private JyBizTaskCollectPackageFlowDao jyBizTaskCollectPackageFlowDao;
    
    @Override
    public boolean batchInsert(List<JyBizTaskCollectPackageFlowEntity> flowList) {
        log.info("批量保存集包任务流向信息：{}", JsonHelper.toJson(flowList));
        return jyBizTaskCollectPackageFlowDao.batchInsert(flowList) > 0;
    }

    @Override
    public List<JyBizTaskCollectPackageFlowEntity> queryListByBizIds(List<String> bizIds) {
        return jyBizTaskCollectPackageFlowDao.queryListByBizIds(bizIds);
    }

    @Override
    public boolean deleteByIds(JyBizTaskCollectPackageQuery query) {
        return jyBizTaskCollectPackageFlowDao.deleteByIds(query) > 0;
    }
}
