package com.jd.bluedragon.distribution.jy.service.strand.impl;

import com.jd.bluedragon.distribution.jy.dao.strand.JyBizTaskStrandReportDao;
import com.jd.bluedragon.distribution.jy.dto.strand.JyStrandTaskPageCondition;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportService;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 拣运-滞留任务接口实现类
 *
 * @author hujiping
 * @date 2023/4/19 8:28 PM
 */
@Service("jyBizTaskStrandReportService")
public class JyBizTaskStrandReportServiceImpl implements JyBizTaskStrandReportService {
    
    @Autowired
    private JyBizTaskStrandReportDao jyBizTaskStrandReportDao;

    @Override
    public Integer insert(JyBizTaskStrandReportEntity entity) {
        return jyBizTaskStrandReportDao.insert(entity);
    }

    @Override
    public Integer updateStatus(JyBizTaskStrandReportEntity updateEntity) {
        return jyBizTaskStrandReportDao.updateStatus(updateEntity);
    }

    @Override
    public JyBizTaskStrandReportEntity queryOneByBiz(String bizId) {
        return jyBizTaskStrandReportDao.queryOneByBiz(bizId);
    }

    @Override
    public Integer queryTotalCondition(JyStrandTaskPageCondition pageCondition) {
        return jyBizTaskStrandReportDao.queryTotalCondition(pageCondition);
    }

    @Override
    public List<JyBizTaskStrandReportEntity> queryPageListByCondition(JyStrandTaskPageCondition pageCondition) {
        return jyBizTaskStrandReportDao.queryPageListByCondition(pageCondition);
    }

    @Override
    public JyBizTaskStrandReportEntity queryOneByTransportRejectBiz(String transPlanCode) {
        return jyBizTaskStrandReportDao.queryOneByTransportRejectBiz(transPlanCode);
    }
}
