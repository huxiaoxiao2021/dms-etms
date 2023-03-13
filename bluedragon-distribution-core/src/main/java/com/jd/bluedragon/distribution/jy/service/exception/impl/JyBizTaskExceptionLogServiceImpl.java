package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionLogEntity;
import com.jd.bluedragon.distribution.jy.service.exception.JyBizTaskExceptionLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/10 18:24
 * @Description:
 */
@Service("jyBizTaskExceptionLogService")
public class JyBizTaskExceptionLogServiceImpl implements JyBizTaskExceptionLogService {


    private String msg ="任务状态由%s变更为%s";

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private JyBizTaskExceptionLogDao jyBizTaskExceptionLogDao;

    @Override
    public void recordLog(JyBizTaskExceptionCycleTypeEnum cycle, JyBizTaskExceptionEntity entity) {

        JyBizTaskExceptionEntity task = jyBizTaskExceptionDao.findByBizId(entity.getBizId());
        JyBizTaskExceptionLogEntity bizLog = new JyBizTaskExceptionLogEntity();
        bizLog.setBizId(task.getBizId());
        bizLog.setCycleType(cycle.getCode());
        bizLog.setType(task.getType());
        bizLog.setOperateTime(task.getUpdateTime()==null?task.getCreateTime():task.getUpdateTime());
        bizLog.setOperateUser(StringUtils.isEmpty(task.getUpdateUserErp())?task.getCreateUserErp():task.getUpdateUserErp());
        bizLog.setOperateUserName(StringUtils.isEmpty(task.getUpdateUserName())?task.getCreateUserName():task.getUpdateUserErp());
        String.format(msg,cycle.getName(),task.getStatus()+"-"+task.getProcessingStatus());
        jyBizTaskExceptionLogDao.insertSelective(bizLog);
    }
}
