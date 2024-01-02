package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizSendTaskAssociationDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendLineTypeCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("jyBizTaskSendVehicleService")
public class JyBizTaskSendVehicleServiceImpl implements JyBizTaskSendVehicleService{

    private static final Logger logger = LoggerFactory.getLogger(JyBizTaskSendVehicleServiceImpl.class);

    @Autowired
    JyBizTaskSendVehicleDao jyBizTaskSendVehicleDao;

    @Autowired
    private DmsConfigManager dmsConfigManager;
    private UccPropertyConfiguration ucc;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;


    @Override
    public String genMainTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

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

    /**
     * 更新到来时间或者即将到来时间，取最小值为准更新
     * @param entity
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateComeTimeOrNearComeTime",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateComeTimeOrNearComeTime(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateComeTimeOrNearComeTime(entity);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByTransWorkAndStartSite(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.findByTransWorkAndStartSite(entity);
    }

    @Override
    public List<JyBizTaskSendVehicleEntity> findByTransWork(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.findByTransWork(entity);
    }

    @Override
    public int initTaskSendVehicle(JyBizTaskSendVehicleEntity entity) {
        JyBizTaskSendVehicleEntity sendTaskQ = new JyBizTaskSendVehicleEntity(entity.getTransWorkCode(), entity.getStartSiteId());
        if (this.findByTransWorkAndStartSite(sendTaskQ) == null) {
            return jyBizTaskSendVehicleDao.initTaskSendVehicle(entity);
        }

        return 0;
    }

    @Override
    public int initAviationTaskSendVehicle(JyBizTaskSendVehicleEntity entity) {
        if (this.findByBookingCode(entity.getBookingCode(), entity.getStartSiteId()) == null) {
            return jyBizTaskSendVehicleDao.initTaskSendVehicle(entity);
        }
        return 0;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.sumTaskByVehicleStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatus(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByVehicleStatus(entity, sendVehicleBizList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.sumTaskByLineType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendLineTypeCountDto> sumTaskByLineType(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByLineType(entity, sendVehicleBizList);
    }

    @Override
    public List<JyBizTaskSendCountDto> sumTaskByVehicleStatusForTransfer(
        JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList) {
        return jyBizTaskSendVehicleDao.sumTaskByVehicleStatusForTransfer(entity, sendVehicleBizList);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.querySendTaskOfPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                                List<String> sendVehicleBizList,
                                                                JyBizTaskSendSortTypeEnum typeEnum,
                                                                Integer pageNum, Integer pageSize,
                                                                List<Integer> statuses) {
        Integer limit = pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > dmsConfigManager.getPropertyConfig().getJyTaskPageMax()) {
            return new ArrayList<>();
        }

        return jyBizTaskSendVehicleDao.querySendTaskOfPage(entity, sendVehicleBizList, typeEnum, offset, limit, statuses);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.countByCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer countByCondition(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList, List<Integer> statuses) {
        return jyBizTaskSendVehicleDao.countByCondition(entity, sendVehicleBizList, statuses);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateLastPlanDepartTimeAndLineType",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateLastPlanDepartTimeAndLineType(JyBizTaskSendVehicleEntity entity) {
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

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateStatusWithoutCompare",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateStatusWithoutCompare(JyBizTaskSendVehicleEntity entity, Integer oldStatus) {
        return jyBizTaskSendVehicleDao.updateStatusWithoutCompare(entity, oldStatus);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.updateBizTaskSendStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public int updateBizTaskSendStatus(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateBizTaskSendStatus(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.findSendTaskByDestOfPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendVehicleEntity> findSendTaskByDestOfPage(JyBizTaskSendVehicleDetailEntity entity, Integer pageNum, Integer pageSize) {
        Integer limit = pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > dmsConfigManager.getPropertyConfig().getJyTaskPageMax()) {
            return new ArrayList<>();
        }
        return jyBizTaskSendVehicleDao.findSendTaskByDestOfPage(entity, offset, limit);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.countSendTaskByDest",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer countSendTaskByDest(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDao.countSendTaskByDest(entity);
    }

    @Override
    public List<JyBizTaskSendVehicleEntity> findSendTaskByTransWorkCode(List<String> transWorkCodeList,Long startSiteId) {
        return jyBizTaskSendVehicleDao.findSendTaskByTransWorkCode(transWorkCodeList,startSiteId);
    }

	@Override
	public int countBizNumForCheckLineType(JyBizTaskSendVehicleEntity checkQuery, List<String> bizIdList,List<Integer> lineTypes) {
        return jyBizTaskSendVehicleDao.countBizNumForCheckLineType(checkQuery,bizIdList,lineTypes);
	}

    @Override
    public List<JyBizTaskSendVehicleEntity> findSendTaskByBizIds(List<String> bizIds) {
        return jyBizTaskSendVehicleDao.findSendTaskByBizIds(bizIds);
    }

    /**
     * 按流向和状态分页查询发货任务
     * @param entity
     * @param pageNum
     * @param pageSize
     * @param statuses
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.findSendTaskByDestAndStatusesWithPage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizTaskSendVehicleEntity> findSendTaskByDestAndStatusesWithPage(JyBizTaskSendVehicleDetailQueryEntity entity, List<Integer> statuses, Integer pageNum, Integer pageSize) {
        Integer limit = pageSize;
        Integer offset = (pageNum - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > dmsConfigManager.getPropertyConfig().getJyTaskPageMax()) {
            return new ArrayList<>();
        }
        return jyBizTaskSendVehicleDao.findSendTaskByDestAndStatusesWithPage(entity,statuses, offset, limit);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByBookingCode(String bookingCode, Long startSiteId) {
        return jyBizTaskSendVehicleDao.findByBookingCode(bookingCode, startSiteId, false);
    }

    @Override
    public JyBizTaskSendVehicleEntity findByBookingCodeIgnoreYn(String bookingCode, Long startSiteId) {
        return jyBizTaskSendVehicleDao.findByBookingCode(bookingCode, startSiteId, true);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.countDetailSendTaskByCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer countDetailSendTaskByCondition(JyBizTaskSendVehicleDetailEntity entity) {
        return jyBizTaskSendVehicleDao.countDetailSendTaskByCondition(entity);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.pageFindDetailSendTaskByCondition",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyBizSendTaskAssociationDto> pageFindDetailSendTaskByCondition(JyBizTaskSendVehicleDetailQueryEntity entity, Integer pageNo, Integer pageSize) {

        Integer limit = pageSize;
        Integer offset = (pageNo - 1) * pageSize;
        // 超过最大分页数据量 直接返回空数据
        if (offset + limit > ucc.getJyTaskPageMax()) {
            return new ArrayList<>();
        }
        return jyBizTaskSendVehicleDao.pageFindDetailSendTaskByCondition(entity, offset, limit);
    }

    /**
     * 定时清理超3小时处于待发货状态的自建任务
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.timingHandlerCleanToSendStatusManualTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void timingHandlerCleanToSendStatusManualTask() {
        // 默认超时时间为3小时
        int defaultTimeOut = Constants.CONSTANT_NUMBER_THREE;
        // 待发货状态
        Integer vehicleStatus = JyBizTaskSendStatusEnum.TO_SEND.getCode();
        // 定时清理超3小时处于待发货状态的自建任务
        cleanSpecifyStatusManualTask(defaultTimeOut, Constants.TO_SEND_MANUAL_TASK_TIME_OUT, vehicleStatus);
    }


    /**
     * 定时清理超72小时处于发货中状态并且没有绑定或删除的自建任务
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskSendVehicleService.timingHandlerCleanSendingStatusManualTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void timingHandlerCleanSendingStatusManualTask() {
        // 默认超时时间为72小时
        int defaultTimeOut = Constants.SEVENTY_TWO_HOURS;
        // 发货中状态
        Integer vehicleStatus = JyBizTaskSendStatusEnum.SENDING.getCode();
        // 定时清理超72小时处于发货中状态并且没有绑定或删除的自建任务
        cleanSpecifyStatusManualTask(defaultTimeOut, Constants.SENDING_MANUAL_TASK_TIME_OUT, vehicleStatus);
    }

    public void cleanSpecifyStatusManualTask(int defaultTimeOut, String configKey, Integer vehicleStatus) {
        // 默认超时时间
        int timeout = defaultTimeOut;
        // 默认每次最多执行1000条
        int totalCount = Constants.CONSTANT_ONE_THOUSAND;
        // 默认每页查200条
        int pageSize = Constants.CONSTANT_TWO_HUNDRED;
        // 当前日期
        Date currentDate = new Date();
        // 组装查询参数
        JyBizTaskSendVehicleDetailQueryEntity params = new JyBizTaskSendVehicleDetailQueryEntity();
        // 任务状态
        params.setVehicleStatus(vehicleStatus);
        // 自建任务
        params.setManualCreatedFlag(Constants.NUMBER_ONE);
        // 未绑定运输任务
        params.setBindFlag(Constants.NUMBER_ZERO);
        params.setPageSize(pageSize);
        params.setUpdateTime(currentDate);
        params.setUpdateUserName(Constants.SYS_NAME);
        params.setUpdateUserErp(Constants.SYS_NAME);
        params.setYn(Constants.YN_NO);
        try {
            // 查询配置信息-处于指定状态的自建任务停留时长
            SysConfig overTimeConfig = sysConfigService.findConfigContentByConfigName(configKey);
            if (overTimeConfig != null) {
                if (logger.isInfoEnabled()) {
                    logger.info("cleanSpecifyStatusManualTask|查询配置信息-指定状态的自建任务停留时长:content={}", overTimeConfig.getConfigContent());
                }
                timeout = Integer.parseInt(overTimeConfig.getConfigContent());
            }
            // 超时日期
            Date overTimeDate = DateHelper.addHours(currentDate, - timeout);
            params.setCreateTime(overTimeDate);
            // 分批获取数据
            for (int i = 1; i <= totalCount / pageSize; i ++) {
                List<String> toSendStatusBizIdList = jyBizTaskSendVehicleDao.findSpecifyStatusManualTaskByStayOverTime(params);
                if (CollectionUtils.isEmpty(toSendStatusBizIdList)) {
                    logger.warn("cleanSpecifyStatusManualTask|根据条件查询处于指定状态停留超时的自建任务列表返回空:当前第{}页,params={}", i, JsonHelper.toJson(params));
                    break;
                }
                // 设置要更新的bizId列表
                params.setBizIdList(toSendStatusBizIdList);
                // 批量更新明细任务
                jyBizTaskSendVehicleDetailDao.batchUpdateByBizIds(params);
                // 批量更新主任务
                jyBizTaskSendVehicleDao.batchUpdateByBizIds(params);
            }
        } catch (Exception e) {
            logger.error("cleanSpecifyStatusManualTask|定时清理处于指定状态停留超时的自建任务出现异常:params={},e={}", JsonHelper.toJson(params), e);
        }
    }

}
