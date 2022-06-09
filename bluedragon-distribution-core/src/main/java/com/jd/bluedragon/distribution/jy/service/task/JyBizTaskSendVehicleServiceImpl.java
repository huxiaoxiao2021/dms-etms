package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("jyBizTaskSendVehicleService")
public class JyBizTaskSendVehicleServiceImpl implements JyBizTaskSendVehicleService{

    private static final Logger logger = LoggerFactory.getLogger(JyBizTaskSendVehicleServiceImpl.class);

    @Autowired
    JyBizTaskSendVehicleDao jyBizTaskSendVehicleDao;

    @Autowired
    private UccPropertyConfiguration ucc;

    @Override
    public JyBizTaskSendVehicleEntity findByBizId(String bizId) {
        return jyBizTaskSendVehicleDao.findByBizId(bizId);
    }

    @Override
    public int saveSendVehicleTask(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.insert(entity);
    }

    @Override
    public int updateSendVehicleTask(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByTransWorkAndStartSite(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.findByTransWorkAndStartSite(entity);
    }

    @Override
    public int initTaskSendVehicle(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.initTaskSendVehicle(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.sumTaskByVehicleStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatus(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByVehicleStatus(entity, sendVehicleBizList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.querySendTaskOfPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                                List<String> sendVehicleBizList,
                                                                JyBizTaskSendSortTypeEnum typeEnum,
                                                                Integer pageNum, Integer pageSize) {
        Integer limit = pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > ucc.getJyTaskPageMax()) {
            return new ArrayList<>();
        }

        return jyBizTaskSendVehicleDao.querySendTaskOfPage(entity, sendVehicleBizList, typeEnum, offset, limit);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateLastPlanDepartTime",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateLastPlanDepartTime(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateLastSealCarTime",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateLastSealCarTime(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateStatus(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        return jyBizTaskSendVehicleDao.updateStatus(entity, oldStatus);
    }
}
