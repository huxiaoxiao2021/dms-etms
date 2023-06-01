package com.jd.bluedragon.distribution.jy.service.strand.impl;

import com.jd.bluedragon.distribution.jy.dao.strand.JyBizStrandReportDetailDao;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizStrandReportDetailService;
import com.jd.bluedragon.distribution.jy.strand.JyBizStrandReportDetailEntity;
import com.jd.bluedragon.distribution.jy.strand.StrandDetailSumEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 拣运-滞留任务明细接口实现
 *
 * @author hujiping
 * @date 2023/4/19 8:31 PM
 */
@Service("jyBizStrandReportDetailService")
public class JyBizStrandReportDetailServiceImpl implements JyBizStrandReportDetailService {

    @Autowired
    private JyBizStrandReportDetailDao jyBizStrandReportDetailDao;
    
    @Override
    public Integer queryTotalScanNum(String bizId) {
        return jyBizStrandReportDetailDao.queryTotalScanNum(bizId);
    }

    @Override
    public List<JyBizStrandReportDetailEntity> queryPageListByCondition(Map<String, Object> paramsMap) {
        return jyBizStrandReportDetailDao.queryPageListByCondition(paramsMap);
    }

    @Override
    public Integer queryTotalInnerScanNum(String bizId) {
        return jyBizStrandReportDetailDao.queryTotalInnerScanNum(bizId);
    }

    @Override
    public List<StrandDetailSumEntity> queryTotalInnerScanNumByBizIds(List<String> bizIds) {
        return jyBizStrandReportDetailDao.queryTotalInnerScanNumByBizIds(bizIds);
    }

    @Override
    public JyBizStrandReportDetailEntity queryOneByCondition(JyBizStrandReportDetailEntity condition) {
        return jyBizStrandReportDetailDao.queryOneByCondition(condition);
    }

    @Override
    public Integer insert(JyBizStrandReportDetailEntity entity) {
        return jyBizStrandReportDetailDao.insert(entity);
    }

    @Override
    public Integer cancel(JyBizStrandReportDetailEntity cancelEntity) {
        return jyBizStrandReportDetailDao.cancel(cancelEntity);
    }

    @Override
    public List<String> queryContainerByBizId(String bizId) {
        return jyBizStrandReportDetailDao.queryContainerByBizId(bizId);
    }
}
