package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendAviationPlanDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:08
 * @Description  空铁库表查询服务
 */
@Service
public class JyBizTaskSendAviationPlanServiceImpl implements JyBizTaskSendAviationPlanService {

    @Autowired
    private JyBizTaskSendAviationPlanDao jyBizTaskSendAviationPlanDao;


    @Override
    public int initTaskSendVehicle(JyBizTaskSendAviationPlanEntity entity) {
        JyBizTaskSendAviationPlanEntity aviationPlanEntity = jyBizTaskSendAviationPlanDao.findByBizId(entity.getBizId());
        if(!Objects.isNull(aviationPlanEntity)) {
            return 0;
        }
        return jyBizTaskSendAviationPlanDao.insertSelective(entity);
    }

}
