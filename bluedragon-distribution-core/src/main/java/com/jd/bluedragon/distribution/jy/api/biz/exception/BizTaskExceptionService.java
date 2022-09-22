package com.jd.bluedragon.distribution.jy.api.biz.exception;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.distribution.jy.api.BizTaskService;
import com.jd.bluedragon.distribution.jy.api.BizType;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.dto.BizTaskConstraint;
import com.jd.bluedragon.distribution.jy.dto.JyBizTaskMessage;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTimeOutEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskNotifyTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionLogEntity;
import com.jdl.basic.common.utils.StringUtils;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component("bizTaskExceptionService")
@BizType(JyScheduleTaskTypeEnum.EXCEPTION)
public class BizTaskExceptionService implements BizTaskService {

    @Autowired
    private JyBizTaskExceptionDao bizTaskExceptionDao;
    @Autowired
    private JyBizTaskExceptionLogDao jyBizTaskExceptionLogDao;

    @Override
    public BizTaskConstraint bizConstraintAssemble(String bizId) {
        JyBizTaskExceptionEntity e = bizTaskExceptionDao.findByBizId(bizId.toString());
        if (e == null){
            return null;
        }
        BizTaskConstraint bizConstraint = new BizTaskConstraint();
        bizConstraint.setSiteCode(e.getSiteCode());
        bizConstraint.setFloor(e.getFloor());
        bizConstraint.setGridCode(e.getGridCode());
        return bizConstraint;
    }

    @Override
    public void bizTaskNotify(JyBizTaskMessage message) {
        JyBizTaskExceptionEntity jyBizTaskExceptionEntity = bizTaskExceptionDao.findByBizId(message.getBizId());
        switch (JyBizTaskNotifyTypeEnum.valueOf(message.getNotifyType())){
            case DISTRIBUTE:
                if (StringUtils.isEmpty(jyBizTaskExceptionEntity.getDistributionTarget())){
                    jyBizTaskExceptionEntity.setDistributionType(message.getDistributionType());
                    jyBizTaskExceptionEntity.setDistributionTarget(message.getDistributionTarget());
                    jyBizTaskExceptionEntity.setDistributionTime(message.getDistributionTime());
                    bizTaskExceptionDao.updateByBizId(jyBizTaskExceptionEntity);

                    JyBizTaskExceptionLogEntity bizLog = new JyBizTaskExceptionLogEntity();
                    bizLog.setBizId(jyBizTaskExceptionEntity.getBizId());
                    bizLog.setCycleType(JyBizTaskExceptionCycleTypeEnum.DISTRIBUTION.getCode());
                    bizLog.setType(jyBizTaskExceptionEntity.getType());
                    bizLog.setOperateTime(message.getDistributionTime());
                    bizLog.setOperateUser("sys");
                    bizLog.setOperateUserName("sys");
                    jyBizTaskExceptionLogDao.insertSelective(bizLog);
                }
                break;
            case TIMEOUT:
                if (Objects.equals(jyBizTaskExceptionEntity.getTimeOut(),JyBizTaskExceptionTimeOutEnum.UN_TIMEOUT.getCode())){
                    jyBizTaskExceptionEntity.setTimeOut(JyBizTaskExceptionTimeOutEnum.TIMEOUT.getCode());
                    bizTaskExceptionDao.updateByBizId(jyBizTaskExceptionEntity);
                }
                break;
            default:
                break;
        }

    }

}
