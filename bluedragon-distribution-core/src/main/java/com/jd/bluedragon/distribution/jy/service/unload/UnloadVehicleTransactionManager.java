package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName UnloadVehicleTransactionManager
 * @Description
 * @Author wyh
 * @Date 2022/4/9 14:09
 **/
@Component("unloadVehicleTransactionManager")
public class UnloadVehicleTransactionManager {

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    /**
     * 卸车任务完成
     *
     * @param dto
     * @return
     */
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "UnloadVehicleTransactionManager.completeUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public boolean completeUnloadTask(JyBizTaskUnloadDto dto) {
        //修改业务任务状态
        JyBizTaskUnloadVehicleEntity changeStatusParam = new JyBizTaskUnloadVehicleEntity();
        changeStatusParam.setBizId(dto.getBizId());
        changeStatusParam.setUpdateUserErp(dto.getOperateUserErp());
        changeStatusParam.setUpdateUserName(dto.getOperateUserName());
        changeStatusParam.setUpdateTime(dto.getOperateTime());
        changeStatusParam.setVehicleStatus(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode());
        if(!jyBizTaskUnloadVehicleService.changeStatus(changeStatusParam)){
            throw new JyBizException(String.format("更新任务状态异常！bizId:%s",dto.getBizId()));
        }
        //关闭调度任务
        if(!closeScheduleTask(dto, JyScheduleTaskTypeEnum.UNLOAD)){
            throw new JyBizException(String.format("关闭调度任务失败！bizId:%s",dto.getBizId()));
        }

        return true;
    }

    /**
     * 关闭调度任务
     * @param dto
     * @param typeEnum
     * @return
     */
    private boolean closeScheduleTask(JyBizTaskUnloadDto dto,JyScheduleTaskTypeEnum typeEnum){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(String.valueOf(typeEnum.getCode()));
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.closeScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 卸车任务领取和分配
     *
     * @param dto
     * @return
     */
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "UnloadVehicleTransactionManager.drawUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public boolean drawUnloadTask(JyBizTaskUnloadDto dto) {
        //更新状态
        JyBizTaskUnloadVehicleEntity changeStatusParam = new JyBizTaskUnloadVehicleEntity();
        changeStatusParam.setBizId(dto.getBizId());
        changeStatusParam.setUpdateUserErp(dto.getOperateUserErp());
        changeStatusParam.setUpdateUserName(dto.getOperateUserName());
        changeStatusParam.setUpdateTime(dto.getOperateTime());
        changeStatusParam.setUnloadStartTime(dto.getOperateTime());
        changeStatusParam.setVehicleStatus(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode());
        if(!jyBizTaskUnloadVehicleService.changeStatus(changeStatusParam)){
            throw new JyBizException(String.format("更新任务状态异常！bizId:%s",dto.getBizId()));
        }
        //分配调度任务
        if(!distributeAndStartScheduleTask(dto)){
            throw new JyBizException(String.format("分配调度任务失败！bizId:%s",dto.getBizId()));
        }
        return true;
    }

    /**
     * 分配卸车调度任务
     * @param dto
     * @return
     */
    private boolean distributeAndStartScheduleTask(JyBizTaskUnloadDto dto){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        req.setDistributionType(JyScheduleTaskDistributionTypeEnum.GROUP.getCode());
        req.setDistributionTarget(dto.getGroupCode());
        req.setDistributionTime(dto.getOperateTime());
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.distributeAndStartScheduleTask(req);
        return jyScheduleTaskResp != null;
    }
}
