package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy.JyBizTaskCloseServiceStrategy;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 任务自动关闭服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
@Service("jyBizTaskAutoCloseService")
@Slf4j
public class JyBizTaskAutoCloseServiceImpl implements JyBizTaskAutoCloseService {

    private final Logger logger = LoggerFactory.getLogger(JyBizTaskAutoCloseServiceImpl.class);

    @Autowired
    private JyBizTaskCloseServiceStrategy jyBizTaskCloseServiceStrategy;
    
    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JyBizTaskStrandReportService jyBizTaskStrandReportService;

    @Autowired
    private JyBizTaskAutoCloseHelperService jyBizTaskAutoCloseHelperService;

    /**
     * 消费自动关闭消息
     *
     * @param autoCloseTaskMq mq报文
     * @return 消费结果
     * @author fanggang7
     * @time 2023-03-28 15:50:49 周二
     */
    @Override
    public Result<Boolean> consumeJyBizTaskAutoCloseMq(AutoCloseTaskMq autoCloseTaskMq) {
        log.info("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq param {}", JSON.toJSONString(autoCloseTaskMq));
        Result<Boolean> result = Result.success();
        try {
            switch (JyAutoCloseTaskBusinessTypeEnum.queryEnumByCode(autoCloseTaskMq.getTaskBusinessType())){
                case WAIT_UNLOAD_NOT_FINISH:
                case UNLOADING_NOT_FINISH:
                    result.setData(unloadTaskAutoCloseDeal(autoCloseTaskMq));
                    break;
                case STRAND_NOT_SUBMIT:
                    result.setData(strandTaskAutoCloseDeal(autoCloseTaskMq));
                    break;
                default:
                    logger.warn("未知业务类型:{}不予处理!", autoCloseTaskMq.getTaskBusinessType());
            }
        } catch (Exception e) {
            log.error("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq exception {}", JSON.toJSONString(autoCloseTaskMq), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private boolean unloadTaskAutoCloseDeal(AutoCloseTaskMq autoCloseTaskMq) {
        final JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist = jyBizTaskUnloadVehicleService.findByBizId(autoCloseTaskMq.getBizId());
        if (jyBizTaskUnloadVehicleExist == null) {
            log.warn("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq task not exist param {}", JSON.toJSONString(autoCloseTaskMq));
            return false;
        }
        if (Objects.equals(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), autoCloseTaskMq.getChangeStatus())) {
            return jyBizTaskAutoCloseHelperService.pushBizTaskAutoCloseTask4WaitUnloadNotFinish(autoCloseTaskMq, jyBizTaskUnloadVehicleExist);
        }
        if (Objects.equals(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode(), autoCloseTaskMq.getChangeStatus())) {
            return jyBizTaskAutoCloseHelperService.pushBizTaskAutoCloseTask4UnloadingNotFinish(autoCloseTaskMq, jyBizTaskUnloadVehicleExist);
        }
        return false;
    }

    private boolean strandTaskAutoCloseDeal(AutoCloseTaskMq autoCloseTaskMq) {
        JyBizTaskStrandReportEntity taskEntity = jyBizTaskStrandReportService.queryOneByBiz(autoCloseTaskMq.getBizId());
        if(taskEntity == null){
            log.warn("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq task not exist param {}", JSON.toJSONString(autoCloseTaskMq));
            return false;
        }
        return jyBizTaskAutoCloseHelperService.pushBizTaskAutoCloseTask4StrandNotFinish(autoCloseTaskMq, taskEntity);
    }

    /**
     * 自动关闭任务
     *
     * @param task 任务数据
     * @return 处理结果
     */
    @Override
    public boolean handleTimingCloseTask(Task task) {
        AutoCloseTaskPo autoCloseTaskPo = JsonHelper.fromJson(task.getBody(), AutoCloseTaskPo.class);
        if(autoCloseTaskPo == null){
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail taskBody is null");
            return false;
        }
        if (StringUtils.isBlank(autoCloseTaskPo.getBizId())) {
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail bizId is null");
            return false;
        }
        if (autoCloseTaskPo.getTaskBusinessType() == null) {
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail taskType is null");
            return false;
        }
        autoCloseTaskPo.setTask(task);
        final Result<Void> result = jyBizTaskCloseServiceStrategy.closeTask(autoCloseTaskPo);
        return result.isSuccess();
    }
}
