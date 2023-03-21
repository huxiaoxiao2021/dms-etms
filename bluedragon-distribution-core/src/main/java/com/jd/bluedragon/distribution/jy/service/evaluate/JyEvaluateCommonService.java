package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dao.group.JyTaskGroupMemberDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberQuery;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JyEvaluateCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JyEvaluateCommonService.class);

    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;
    @Autowired
    private VosManager vosManager;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;
    @Autowired
    private JyTaskGroupMemberDao jyTaskGroupMemberDao;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.saveEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void saveEvaluateInfo(List<JyEvaluateRecordEntity> recordList) {
        jyEvaluateRecordDao.batchInsert(recordList);
    }


    /**
     * 通过封车编码获取封车信息
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.findSealCarInfoBySealCarCodeOfTms", mState = {JProEnum.TP, JProEnum.FunctionError})
    public SealCarDto findSealCarInfoBySealCarCodeOfTms(String sealCarCode) {
        CommonDto<SealCarDto> sealCarDtoCommonDto = vosManager.querySealCarInfoBySealCarCode(sealCarCode);
        LOGGER.info("JyEvaluateCommonService|获取封车信息返回数据sealCarCode={},result={}",sealCarCode, JsonHelper.toJson(sealCarDtoCommonDto));
        if (sealCarDtoCommonDto == null || Constants.RESULT_SUCCESS != sealCarDtoCommonDto.getCode()) {
            throw new JyBizException("根据封车编码查询运输封车详情返回空");
        }
        return sealCarDtoCommonDto.getData();
    }


    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.findUnloadTaskByBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskUnloadVehicleEntity findUnloadTaskByBizId(String sourceBizId) {
        // 根据卸车bizId查询卸车任务详情
        JyBizTaskUnloadVehicleEntity unloadVehicle = jyBizTaskUnloadVehicleService.findByBizId(sourceBizId);
        if (unloadVehicle == null) {
            LOGGER.warn("JyEvaluateCommonService|查询卸车任务返回空:sourceBizId={}", sourceBizId);
            throw new JyBizException("查询卸车任务返回空");
        }
        return unloadVehicle;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.findSendTaskByTransWorkItemCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyBizTaskSendVehicleDetailEntity findSendTaskByTransWorkItemCode(String transWorkItemCode) {
        // 根据派车单号查询发货任务
        JyBizTaskSendVehicleDetailEntity query = new JyBizTaskSendVehicleDetailEntity();
        query.setTransWorkItemCode(transWorkItemCode);
        JyBizTaskSendVehicleDetailEntity sendVehicleDetail = jyBizTaskSendVehicleDetailService.findByTransWorkItemCode(query);
        if (sendVehicleDetail == null) {
            LOGGER.warn("JyEvaluateCommonService|查询发货任务返回空,transWorkItemCode={}", transWorkItemCode);
            throw new JyBizException("查询发货任务返回空");
        }
        return sendVehicleDetail;
    }


    /**
     * 查询调度任务
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.getJyScheduleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JyScheduleTaskResp getJyScheduleTask(String bizId, String taskType) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(taskType);
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        if (scheduleTask == null) {
            LOGGER.warn("JyEvaluateCommonService|查询调度任务返回空,bizId={},taskType={}", bizId, taskType);
            throw new JyBizException("查询调度任务返回空");
        }
        return scheduleTask;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.queryMemberListByTaskId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<JyTaskGroupMemberEntity> queryMemberListByTaskId(String refTaskId) {
        JyTaskGroupMemberQuery query = new JyTaskGroupMemberQuery();
        query.setRefTaskId(refTaskId);
        List<JyTaskGroupMemberEntity> taskGroupMembers = jyTaskGroupMemberDao.queryMemberListByTaskId(query);
//        if (CollectionUtils.isEmpty(taskGroupMembers)) {
//            LOGGER.warn("JyEvaluateCommonService|查询发货任务协助人返回空,targetTaskId={}", refTaskId);
//            throw new JyBizException("查询发货任务协助人返回空");
//        }
        return taskGroupMembers;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.getSiteInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseStaffSiteOrgDto getSiteInfo(Integer siteCode) {
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (siteOrgDto == null) {
            LOGGER.warn("JyEvaluateCommonService|查询任务所属区域返回空,siteCode={}", siteCode);
            throw new JyBizException("查询任务所属区域返回空");
        }
        return siteOrgDto;
    }

}
