package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.*;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.DeliveryWSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.businessIntercept.config.BusinessInterceptConfig;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptDisposeRecord;
import com.jd.bluedragon.distribution.businessIntercept.dto.BusinessInterceptReport;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptTypeEnum;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.jy.constants.JyCacheKeyConstants;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionInterceptDetailDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.dto.exception.JyExpTaskMessage;
import com.jd.bluedragon.distribution.jy.enums.CustomerNotifyStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetail;
import com.jd.bluedragon.distribution.jy.exception.model.JyExceptionInterceptDetailKv;
import com.jd.bluedragon.distribution.jy.exception.query.JyBizTaskExceptionQuery;
import com.jd.bluedragon.distribution.jy.exception.query.JyExceptionInterceptDetailQuery;
import com.jd.bluedragon.distribution.jy.manager.ExpInfoSummaryJsfManager;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.*;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.JyExceptionBusinessInterceptDetailKvService;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.JyExceptionBusinessInterceptDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.capabilityDomain.businessIntercept.helper.JyExceptionBusinessInterceptTaskConstants;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.print.domain.ExchangeWaybillRequest;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.print.service.WaybillInterceptReverseService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ps.data.epf.dto.CommonDto;
import com.jd.ps.data.epf.dto.ExpInfoSumaryInputDto;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.erp.dto.delivery.DeliveredReqDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JySealCarDetail;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskChangeStatusReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskStatusEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.SCRAP_WAYBILL_INTERCEPT_HINT_CODE;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isScrapWaybill;

@Service
public class JyExceptionServiceImpl implements JyExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);
    private static final String TASK_CACHE_PRE = "DMS:JYAPP:EXP:TASK_CACHE01:";
    private static final String RECEIVING_POSITION_COUNT_PRE = "DMS:JYAPP:EXP:RECEIVING_POSITION_COUNT_PRE02:";
    private static final String RECEIVING_SITE_COUNT_PRE = "DMS:JYAPP:EXP:RECEIVING_SITE_COUNT_PRE03:";

    // 统计数据缓存时间：半小时
    private static final int COUNT_CACHE_SECOND = 30 * 60;

    // 任务明细缓存时间
    private static final int TASK_DETAIL_CACHE_DAYS = 30;
    private static final String SPLIT = ",";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    private final String DEVICE_ID = "sorting";
    private final Integer SYSTEM_SOURCE = 19;
    private final Integer OPERATE_ID = -1;
    private final String OPERATE_NAME = "分拣中心";

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private JyBizTaskExceptionLogDao jyBizTaskExceptionLogDao;
    @Autowired
    private JyExceptionDao jyExceptionDao;
    @Autowired
    private PositionQueryJsfManager positionQueryJsfManager;
    // 三无接口
    @Autowired
    private ExpInfoSummaryJsfManager expInfoSummaryJsfManager;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClient;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    @Qualifier("scheduleTaskChangeStatusWorkerProducer")
    private DefaultJMQProducer scheduleTaskChangeStatusWorkerProducer;
    @Autowired
    @Qualifier("scheduleTaskAddWorkerProducer")
    private DefaultJMQProducer scheduleTaskAddWorkerProducer;
    @Autowired
    @Qualifier("scheduleTaskChangeStatusProducer")
    private DefaultJMQProducer scheduleTaskChangeStatusProducer;
    @Autowired
    @Qualifier("scheduleTaskAddProducer")
    private DefaultJMQProducer scheduleTaskAddProducer;
    @Autowired
    @Qualifier("jyUnloadVehicleManager")
    private IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;

    @Autowired
    @Qualifier("dmsScrapNoticeProducer")
    private DefaultJMQProducer dmsScrapNoticeProducer;

    @Autowired
    @Qualifier("dmsUnCollectOverTimeNoticeProducer")
    private DefaultJMQProducer dmsUnCollectOverTimeNoticeProducer;
    @Autowired
    private JyScrappedExceptionServiceImpl jyScrappedExceptionService;

    @Autowired
    private TaskService taskService;

    @Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;

    @Autowired
    private JyExceptionStrategyFactory jyExceptionStrategyFactory;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private DeliveryWSManager deliveryWSManager;

    @Autowired
    private WorkStationGridManager workStationGridManager;

    @Autowired
    private WorkGridManager workGridManager;

    @Autowired
    private JySanwuExceptionService jySanwuExceptionService;

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private JyExceptionBusinessInterceptDetailService jyExceptionBusinessInterceptDetailService;

    @Autowired
    private JyExceptionInterceptDetailDao jyExceptionInterceptDetailDao;

    @Autowired
    private JyExceptionBusinessInterceptDetailKvService jyExceptionBusinessInterceptDetailKvService;

    @Autowired
    private WaybillInterceptReverseService waybillInterceptReverseService;

    @Autowired
    @Qualifier("businessInterceptConfig")
    private BusinessInterceptConfig businessInterceptConfig;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    /**
     * 通用异常上报入口-扫描
     *
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionServiceImpl.uploadScan", mState = {JProEnum.TP})
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {
        JyExpSourceEnum source = JyExpSourceEnum.getEnumByCode(req.getSource());
        if (source == null) {
            return JdCResponse.fail("异常提报source有误!");
        }
        if (StringUtils.isBlank(req.getBarCode())) {
            return JdCResponse.fail("扫描条码不能为空!");
        }
        if (checkScrapWaybill(req.getBarCode())) {
            return JdCResponse.fail(HintService.getHint(SCRAP_WAYBILL_INTERCEPT_HINT_CODE));
        }

        //三无异常处理逻辑
        if (!(BusinessUtil.isSanWuCode(req.getBarCode())  || WaybillUtil.isPackageCode(req.getBarCode()) || WaybillUtil.isWaybillCode(req.getBarCode()))) {
            return JdCResponse.fail("请扫描三无号或运单号!");
        }

        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("网格码有误!");
        }

        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            return JdCResponse.fail("登录人ERP有误!" + req.getUserErp());
        }
        //处理运单号
        if(WaybillUtil.isWaybillCode(req.getBarCode())){
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(req.getBarCode());
            if (waybill == null) {
                logger.warn("JyExceptionServiceImpl-uploadScan运单不存在：{}" , JSON.toJSONString(req));
                return JdCResponse.fail("运单不存在!");
            }
            if (BusinessHelper.isBwxWaybill(waybill.getWaybillSign())){
                return JdCResponse.fail("该单为保温箱运单，请正常发货流转!");
            }
        }

        String bizId = getBizId(req.getBarCode(),position.getSiteCode());
        String existKey = "DMS.EXCEPTION.UPLOAD_SCAN:" + bizId;
        if (!redisClient.set(existKey, "1", 10, TimeUnit.SECONDS, false)) {
            return JdCResponse.fail("该异常上报正在提交,请稍后再试!");
        }

        try{

            JyBizTaskExceptionEntity byBizId = jyBizTaskExceptionDao.findByBizId(bizId);
            if (byBizId != null) {
                return JdCResponse.fail("该异常已上报!");
            }
            JyBizTaskExceptionEntity taskEntity = new JyBizTaskExceptionEntity();
            if(req.getType() == null){
                req.setType(JyBizTaskExceptionTypeEnum.UNKNOW.getCode());
            }
            taskEntity.setType(req.getType());
            if(BusinessUtil.isSanWuCode(req.getBarCode())){
                req.setType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
            }

            //兼容老逻辑 type 不为空
            if(req.getType() != null && !Objects.equals(req.getType(),-1)){
                JyExceptionStrategy exceptionService = jyExceptionStrategyFactory.getStrategy(req.getType());
                JdCResponse<Object> response = exceptionService.uploadScan(taskEntity,req, position, source, bizId);
                if(!JdCResponse.CODE_SUCCESS.equals(response.getCode())){
                    return response;
                }
            }

            taskEntity.setBizId(bizId);
            taskEntity.setBarCode(req.getBarCode());
            taskEntity.setSource(source.getCode());
            taskEntity.setSiteCode(new Long(position.getSiteCode()));
            taskEntity.setSiteName(position.getSiteName());
            taskEntity.setFloor(position.getFloor());
            taskEntity.setAreaCode(position.getAreaCode());
            taskEntity.setAreaName(position.getAreaName());
            taskEntity.setGridCode(position.getGridCode());
            taskEntity.setGridNo(position.getGridNo());
            taskEntity.setStatus(JyExpStatusEnum.TO_PICK.getCode());
            taskEntity.setCreateUserErp(req.getUserErp());
            taskEntity.setCreateUserName(baseStaffByErp.getStaffName());
            taskEntity.setCreateTime(new Date());
            taskEntity.setTimeOut(JyBizTaskExceptionTimeOutEnum.UN_TIMEOUT.getCode());
            taskEntity.setYn(1);
            jyBizTaskExceptionDao.insertSelective(taskEntity);
            recordLog(JyBizTaskExceptionCycleTypeEnum.UPLOAD, taskEntity);

            // 发送 mq 通知调度系统
            JyExpTaskMessage taskMessage = new JyExpTaskMessage();
            taskMessage.setTaskType(JyScheduleTaskTypeEnum.EXCEPTION.getCode());
            taskMessage.setTaskStatus(JyScheduleTaskStatusEnum.INIT.getCode());
            taskMessage.setBizId(bizId);
            taskMessage.setOpeUser(req.getUserErp());
            taskMessage.setOpeUserName(baseStaffByErp.getStaffName());
            taskMessage.setOpeTime(new Date().getTime());

            String body = JSON.toJSONString(taskMessage);
            scheduleTaskAddProducer.sendOnFailPersistent(bizId, body);
            logger.info("异常岗-写入任务发送mq完成:body={}", body);

            //如果是运单号，将运单号放入缓存 妥投时校验运单是否妥投 && 增加验货全程跟踪
            if(WaybillUtil.isWaybillCode(req.getBarCode())){
                String cacheKey =  Constants.EXP_WAYBILL_CACHE_KEY_PREFIX+req.getBarCode();
                Boolean result = redisClientOfJy.set(cacheKey, "1", 7, TimeUnit.DAYS, false);
                logger.info("异常上报 运单放入缓存结果-{}",result);
                //记录运单验货全程跟踪 暂时不写
                //sendForwardInspectionTrance(taskEntity,baseStaffByErp);
            }

        }catch (Exception e) {
            logger.error("写入异常提报数据出错了,request=" + JSON.toJSONString(req), e);
            return JdCResponse.fail("异常提报数据保存出错了,请稍后重试！");
        } finally {
            redisClient.del(existKey);
        }

        return JdCResponse.ok();
    }

    private boolean checkScrapWaybill(String barCode) {
        // 报废运单拦截
        if (WaybillUtil.isWaybillCode(barCode) || WaybillUtil.isPackageCode(barCode)) {
            Waybill waybill = waybillService.getWaybillByWayCode(WaybillUtil.getWaybillCode(barCode));
            if (waybill != null && StringUtils.isNotEmpty(waybill.getWaybillSign())) {
                // waybillSign的19位等于2是报废运单 拦截
                String waybillSign = waybill.getWaybillSign();
                return isScrapWaybill(waybillSign);
            }
        }
        return false;
    }

    /**
     *  记录运单验货全程跟踪
     * @param entity
     */
    private void sendForwardInspectionTrance(JyBizTaskExceptionEntity entity,BaseStaffSiteOrgDto baseStaffByErp){
        CallerInfo info = Profiler.registerInfo("DMSWEB.JyExceptionServiceImpl.sendForwardInspectionTrance", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            WaybillStatus waybillStatus = new WaybillStatus();
            //设置站点相关属性
            waybillStatus.setPackageCode(entity.getBarCode());
            waybillStatus.setCreateSiteCode(entity.getSiteCode().intValue());
            waybillStatus.setCreateSiteName(entity.getSiteName());
            waybillStatus.setCreateSiteType(Constants.DMS_SITE_TYPE);
            if(StringUtils.isNotBlank(baseStaffByErp.getUserCode())){
                waybillStatus.setOperatorId(new Integer(baseStaffByErp.getUserCode()));
            }
            waybillStatus.setOperator(entity.getCreateUserName());
            waybillStatus.setOperateTime(new Date());
            waybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_INSPECTION);
            waybillStatus.setRemark("验货");
            waybillStatus.setOrgId(baseStaffByErp.getOrgId());
            waybillStatus.setOrgName(baseStaffByErp.getOrgName());
            // 添加到task表
            logger.info("异常上报发送验货全称跟踪");
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("异常上报发送验货全称跟踪失败！",  e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 转换成全称跟踪的Task
     *
     * @param waybillStatus
     * @return
     */
    private Task toTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_WAYBILL);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getWaybillCode());
        task.setKeyword2(waybillStatus.getPackageCode());
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL);
        task.setOwnSign(BusinessHelper.getOwnSign());
        StringBuffer fingerprint = new StringBuffer();
        fingerprint
                .append(waybillStatus.getCreateSiteCode())
                .append("_")
                .append((waybillStatus.getReceiveSiteCode() == null ? "-1"
                        : waybillStatus.getReceiveSiteCode())).append("_")
                .append(waybillStatus.getOperateType()).append("_")
                .append(waybillStatus.getWaybillCode()).append("_")
                .append(waybillStatus.getOperateTime()).append("_");
        if (waybillStatus.getPackageCode() != null
                && !"".equals(waybillStatus.getPackageCode())) {
            fingerprint.append("_").append(waybillStatus.getPackageCode());
        }
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        return task;
    }

    /**
     * 操作日志记录
     * @param cycle
     * @param entity
     */
    public void recordLog(JyBizTaskExceptionCycleTypeEnum cycle,JyBizTaskExceptionEntity entity){
        String msg ="%s%s操作,状态变更为%s-%s";
        try{
            JyBizTaskExceptionEntity task = jyBizTaskExceptionDao.findByBizId(entity.getBizId());
            JyBizTaskExceptionLogEntity bizLog = new JyBizTaskExceptionLogEntity();
            bizLog.setBizId(task.getBizId());
            bizLog.setCycleType(cycle.getCode());
            bizLog.setType(task.getType());
            bizLog.setOperateTime(task.getUpdateTime()==null?task.getCreateTime():task.getUpdateTime());
            bizLog.setOperateUser(StringUtils.isEmpty(task.getUpdateUserErp())?task.getCreateUserErp():task.getUpdateUserErp());
            bizLog.setOperateUserName(StringUtils.isEmpty(task.getUpdateUserName())?task.getCreateUserName():task.getUpdateUserErp());

            String status ="";
            String processStatus="";
            if(task.getStatus() != null && JyExpStatusEnum.getEnumByCode(task.getStatus()) != null){
                status=JyExpStatusEnum.getEnumByCode(task.getStatus()).getText();
            }
            if(task.getProcessingStatus() != null && JyBizTaskExceptionProcessStatusEnum.valueOf(task.getProcessingStatus()) != null){
                processStatus = JyBizTaskExceptionProcessStatusEnum.valueOf(task.getProcessingStatus()).getName();
            }
            String erp =StringUtils.isNotBlank(entity.getUpdateUserErp())?entity.getUpdateUserErp():"";
            bizLog.setRemark(String.format(msg,erp,cycle.getName(),status,processStatus));
            jyBizTaskExceptionLogDao.insertSelective(bizLog);
        }catch (Exception e){
            logger.error("保存日志信息出错 req-{}-{}",JSON.toJSONString(entity),e.getMessage(),e);
        }
    }

    /**
     * 按取件状态统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {
        logger.info("按取件状态统计 statisticsByStatus 入参 -{}",JSON.toJSONString(req));
        JdCResponse<List<StatisticsByStatusDto>> result = new JdCResponse<>();
        //岗位码相关
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("网格码有误!");
        }
        String gridRid = getGridRid(position);

        int completeExpDayNumLimit = dmsConfigManager.getPropertyConfig().getCompleteExpDayNumLimit();
        List<StatisticsByStatusDto> statisticStatusResps = jyBizTaskExceptionDao.getCommonStatusStatistic(gridRid);
        List<StatisticsByStatusDto> specialStatusStatistic = jyBizTaskExceptionDao.getSpecialStatusStatistic(gridRid, req.getUserErp());
        logger.info("getCompleteStatusStatistic-入参gridRid-{} limit-{}",gridRid,completeExpDayNumLimit);
        List<StatisticsByStatusDto> completeStatusStatistic = jyBizTaskExceptionDao.getCompleteStatusStatistic(gridRid,completeExpDayNumLimit);
        logger.info("getCompleteStatusStatistic-出参-{}",JSON.toJSONString(completeStatusStatistic));
        statisticStatusResps.addAll(specialStatusStatistic);
        statisticStatusResps.addAll(completeStatusStatistic);
        HashMap<Integer, Integer> countMap = new HashMap<>();
        for (StatisticsByStatusDto s : statisticStatusResps) {
            countMap.put(s.getStatus(), s.getCount());
        }
        List<StatisticsByStatusDto> countList = new ArrayList<>();
        for (JyExpStatusEnum value : JyExpStatusEnum.values()) {
            StatisticsByStatusDto dto = new StatisticsByStatusDto();
            dto.setStatus(value.getCode());
            dto.setCount(0);
            if (countMap.containsKey(dto.getStatus())) {
                dto.setCount(countMap.get(value.getCode()));
            }
            countList.add(dto);
        }
        result.setData(countList);
        result.toSucceed();
        return result;
    }

    /**
     * 网格待取件列表统计接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req) {
        //岗位码相关
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("网格码有误!");
        }

        // 待取件
        req.setStatus(JyExpStatusEnum.TO_PICK.getCode());

        if (req.getPageNumber() == null || req.getPageNumber() <= 0) {
            req.setPageNumber(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(10);
        }

        req.setSiteId(position.getSiteCode());
        req.setGridRid(getGridRid(position));

        logger.info("查询待取件统计数据参数:{}", JSON.toJSONString(req));
        List<StatisticsByGridDto> statisticsByGrid = jyBizTaskExceptionDao.getStatisticsByGrid(req);
        if (CollectionUtils.isEmpty(statisticsByGrid)) {
            return JdCResponse.ok(statisticsByGrid);
        }

        // 标签处理
        try {
            processTags(req, statisticsByGrid);
        } catch (Exception e) {
            logger.error("异常岗-查询待取件统计数据-处理标签出错了", e);
        }

        return JdCResponse.ok(statisticsByGrid);
    }

    private void processTags(StatisticsByGridReq req, List<StatisticsByGridDto> statisticsByGrid) {
        List<JyBizTaskExceptionEntity> tagsByGrid = jyBizTaskExceptionDao.getTagsByGrid(req);

        // 标签优先级
        final List<String> tagPriority = new ArrayList<>();
        for (JyBizTaskExceptionTagEnum value : JyBizTaskExceptionTagEnum.values()) {
            tagPriority.add(value.getCode());
        }

        // 取出所有网格的 属于前三优先级的标签
        Multimap<String, String> gridTags = HashMultimap.create();
        for (JyBizTaskExceptionEntity entity : tagsByGrid) {
            if (StringUtils.isNotBlank(entity.getTags())) {
                String[] split = entity.getTags().split(SPLIT);
                String key  = entity.getFloor() + ":" + entity.getAreaCode() + ":" + entity.getGridCode();
                for (String s : split) {
                    gridTags.put(key, s);
                }
            }
        }

        // 按标签优先级排序并取前三
        ArrayListMultimap<String, TagDto> gridTagList = ArrayListMultimap.create();
        for (String key : gridTags.keys()) {
            List<String> tags = new ArrayList<>(gridTags.get(key));
            // 排序标签
            Collections.sort(tags, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return tagPriority.indexOf(o1) - tagPriority.indexOf(o2);
                }
            });
            // 取前三个标签
            tags = tags.subList(0, Math.min(3, tags.size()));
            // 转换标签格式
            List<TagDto> tagList = getTags(StringUtils.join(tags, ','));
            gridTagList.putAll(key, tagList);
        }

        // 填充标签
        for (StatisticsByGridDto dto : statisticsByGrid) {
            String key  = dto.getFloor() + ":" + dto.getAreaCode() + ":" + dto.getGridCode();
            dto.setTags(gridTagList.get(key));
        }
    }

    /**
     * 取件进行中数据统计
     *
     */
    @Override
    public JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req) {
        //岗位码相关
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("网格码有误!");
        }

        List<ProcessingNumByGridDto> list = new ArrayList<>();

        // 场地内所有进行中的 岗位
        String siteCountKey = RECEIVING_SITE_COUNT_PRE + position.getSiteCode();
        Set<String> receivingPositionSet = redisClient.sMembers(siteCountKey);

        logger.info("取件进行中-场地内所有进行中的岗位,siteCountKey={},receivingPositionSet={}", siteCountKey, siteCountKey);

        if (receivingPositionSet == null || receivingPositionSet.isEmpty()) {
            return JdCResponse.ok(list);
        }

        for (String key : receivingPositionSet) {
            String[] split = key.split("\\|");
            try {
                ProcessingNumByGridDto dto = new ProcessingNumByGridDto();
                dto.setFloor(new Integer(split[0]));
                dto.setGriCode(split[1]);
                dto.setProcessingNum(0);
                list.add(dto);

                // 岗位内 进行中的ERP
                Map<String, String> receivingCountByPosition = redisClient.hGetAll(key);
                if (receivingCountByPosition == null || receivingCountByPosition.isEmpty()) {
                    continue;
                }
                logger.info("取件进行中的-岗位内进行中的ERP,gridKey={},receivingPositionSet={}", key, receivingCountByPosition);
                // 比较开始进行时间距当前时间是否 小于 COUNT_REDIS_SECOND
                for (String value : receivingCountByPosition.values()) {
                    if (!StringUtils.isBlank(value)) {
                        if (System.currentTimeMillis() - Long.parseLong(value) < (COUNT_CACHE_SECOND * 1000)) {
                            dto.setProcessingNum(dto.getProcessingNum() + 1);
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("解析取件进行中数据出错了" + key, e);
            }
        }


        return JdCResponse.ok(list);
    }

    /**
     * 任务列表接口
     *
     */
    @Override
    public JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req) {
        if (req.getStatus() == null || JyExpStatusEnum.getEnumByCode(req.getStatus()) == null) {
            return JdCResponse.fail("status参数有误!");
        }

        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("岗位码有误!");
        }

        if (req.getPageNumber() == null || req.getPageNumber() <= 0) {
            req.setPageNumber(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(10);
        }

        req.setSiteId(position.getSiteCode());
        req.setGridRid(getGridRid(position));
        // 待处理 只查询 处理状态=待录入
        if (Objects.equals(req.getStatus(), JyExpStatusEnum.TO_PROCESS.getCode())) {
            req.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PENDING_ENTRY.getCode());
            req.setHandlerErp(req.getUserErp());
        }else if(Objects.equals(req.getStatus(), JyExpStatusEnum.COMPLETE.getCode())){
            int completeExpDayNumLimit = dmsConfigManager.getPropertyConfig().getCompleteExpDayNumLimit();
            req.setLimitDay(completeExpDayNumLimit);
        }
        logger.info("queryExceptionTaskList 查询条件-{}",JSON.toJSONString(req));
        List<JyBizTaskExceptionEntity> taskList = jyBizTaskExceptionDao.queryExceptionTaskList(req);
        List<ExpTaskDto> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskList)) {
            //获取所有要查询的bizId 集合
            Map<String, ExpScrappedDetailDto> scrappedDtoMap = this.getTaskListOfScrapped(taskList);

            // 读取破损数据
            Map<String, JyExceptionDamageDto> damageDtoMap = getDamageDetailMapByBizTaskList(taskList, req.getStatus());

            // 读取拦截数据明细
            final Map<String, JyExceptionInterceptDetail> interceptDetailMapGbBizId = getInterceptDetailMapByBizTaskList(req, taskList, req.getStatus());
            for (JyBizTaskExceptionEntity entity : taskList) {
                // 拼装dto
                ExpTaskDto dto = this.copyEntityToExpTaskDto(entity, scrappedDtoMap);
                // 待处理状态数据
                if ((Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), entity.getStatus()))
                        || (Objects.equals(JyExpStatusEnum.COMPLETE.getCode(), entity.getStatus()))) {
                    //处理三无待打印状态  待打印特殊处理
                    if (Objects.equals(JyBizTaskExceptionTypeEnum.SANWU.getCode(),entity.getType())) {
                        this.dealSanwuTaskList(dto,entity);
                    } else if (Objects.equals(JyBizTaskExceptionTypeEnum.SCRAPPED.getCode(),dto.getType())) {
                        ExpScrappedDetailDto detailDto = scrappedDtoMap.get(entity.getBizId());
                        if (detailDto != null) {
                            this.dealScrappedTaskList(dto, entity, detailDto);
                        }
                    }
                }
                // 读取破损数据
                if(Objects.equals(JyBizTaskExceptionTypeEnum.DAMAGE.getCode(),entity.getType())){
                    // 如果待处理只设置状态
                    if (Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(), entity.getStatus())){
                        dto.setSaved(true);
                    } else {
                        JyExceptionDamageDto damageDto = damageDtoMap.get(entity.getBizId());
                        if (damageDto != null) {
                            if (CollectionUtils.isNotEmpty(damageDto.getImageUrlList())) {
                                dto.setImageUrls(String.join(";", damageDto.getImageUrlList()));
                            }
                            dto.setFeedBackType(damageDto.getFeedBackType());
                            dto.setFeedBackTypeName(JyExceptionDamageEnum.FeedBackTypeEnum.getNameByCode(damageDto.getFeedBackType()));
                        }
                    }
                }
                // 读取拦截数据
                if(Objects.equals(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode(), entity.getType())){
                    final JyExceptionInterceptDetail jyExceptionInterceptDetail = interceptDetailMapGbBizId.get(entity.getBizId());
                    if (jyExceptionInterceptDetail != null) {
                        dto.setSaved(Objects.equals(JyExpSaveTypeEnum.TEMP_SAVE.getCode(), jyExceptionInterceptDetail.getSaveType()));
                        dto.setCreateTime(DateUtil.formatDateTime(jyExceptionInterceptDetail.getCreateTime()));
                    }
                }
                list.add(dto);
            }
        }
        logger.info("getExceptionTaskPageList 结果-{}",JSON.toJSONString(list));
        // 仅待取件列表 记录"进行中"的人数
        if (!Objects.equals(req.getStatus(), JyExpStatusEnum.TO_PICK.getCode())) {
            return JdCResponse.ok(list);
        }

        // 记录取件进行中
        // 按岗位统计
        String gridKey = req.getFloor() + "|" + req.getGridCode();
        redisClient.hSet(gridKey, req.getUserErp(), System.currentTimeMillis() + "");
        redisClient.expire(gridKey, COUNT_CACHE_SECOND, TimeUnit.SECONDS);


        // 记录场地进行中的 网格码
        String siteCountKey = RECEIVING_SITE_COUNT_PRE + position.getSiteCode();
        redisClient.sAdd(siteCountKey, gridKey);
        redisClient.expire(siteCountKey, COUNT_CACHE_SECOND, TimeUnit.SECONDS);

        logger.info("取件进行中的人数,gridKey={},erp={}", gridKey, req.getUserErp());
        return JdCResponse.ok(list);
    }

    /**
     * 复制数据处理状态
     * @param entity
     * @param scrappedDtoMap
     * @return
     */
    private ExpTaskDto copyEntityToExpTaskDto(JyBizTaskExceptionEntity entity, Map<String, ExpScrappedDetailDto> scrappedDtoMap){
        ExpTaskDto dto = new ExpTaskDto();
        BeanUtils.copyProperties(entity,dto);
        // 停留时间：当前时间-分配时间
        dto.setStayTime(getStayTime(entity.getDistributionTime()));
        dto.setReporterName(entity.getCreateUserName());
        dto.setTags(getTags(entity.getTags()));
        // 在这里处理是因为老逻辑setSaved 不受task状态影响
        if(Objects.nonNull(entity.getType())
                && (JyBizTaskExceptionTypeEnum.SCRAPPED.getCode().equals(entity.getType()))){
            ExpScrappedDetailDto scrappedDetailDto = scrappedDtoMap.get(entity.getBizId());
            if(scrappedDetailDto != null && scrappedDetailDto.getSaveType() != null){
                dto.setSaved(Objects.equals(scrappedDetailDto.getSaveType(),JyExpSaveTypeEnum.TEMP_SAVE.getCode()));
            }
        }else{
            String s = redisClient.get(TASK_CACHE_PRE + entity.getBizId());
            boolean saved = !StringUtils.isBlank(s) && Objects.equals(JSON.parseObject(s, ExpTaskDetailCacheDto.class).getSaveType(), "0");
            dto.setSaved(saved);
        }
        dto.setProcessingStatusDesc(JyBizTaskExceptionProcessStatusEnum.getEnumNameByCode(dto.getProcessingStatus()));
        return dto;
    }
    /**
     * 三无列表数据处理
     */
    private void dealSanwuTaskList(ExpTaskDto dto, JyBizTaskExceptionEntity entity) {
        if(Objects.equals(JyBizTaskExceptionProcessStatusEnum.WAITING_MATCH.getCode(),entity.getProcessingStatus())){
            dto.setCreateTime(entity.getCreateTime() == null ? null : dateFormat.format(entity.getCreateTime()));
        }else{
            // 待打印时间
            dto.setCreateTime(entity.getProcessEndTime() == null ? null : dateFormat.format(entity.getProcessEndTime()));
        }

        // 查询照片地址
        String key = TASK_CACHE_PRE + entity.getBizId();
        String taskCache = redisClient.get(key);
        logger.info("taskCache---{}",taskCache);
        if (StringUtils.isNotBlank(taskCache)) {
            ExpTaskDetailCacheDto cacheDto = JSON.parseObject(taskCache, ExpTaskDetailCacheDto.class);
            logger.info("ExpTaskDetailCacheDto---{}",JSON.toJSONString(cacheDto));
            if (cacheDto != null && StringUtils.isNotBlank(cacheDto.getImageUrls())) {
                dto.setImageUrls(cacheDto.getImageUrls());
            }
        }
    }
    /**
     * 破损列表数据处理
     */
    private void dealScrappedTaskList(ExpTaskDto dto, JyBizTaskExceptionEntity entity, ExpScrappedDetailDto detailDto) {
        //处理中的报废任务
        if((Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), entity.getStatus()))){
            if(StringUtils.isNotBlank(detailDto.getFirstChecker()) &&
                    ((Objects.equals(detailDto.getFirstCheckStatus(),JyApproveStatusEnum.TODO.getCode()))
                            ||(Objects.equals(detailDto.getFirstCheckStatus(),JyApproveStatusEnum.REJECT.getCode())))){
                dto.setCheckerErp(detailDto.getFirstChecker());
            }else if(StringUtils.isNotBlank(detailDto.getSecondChecker())&&
                    ((Objects.equals(detailDto.getSecondCheckStatus(),JyApproveStatusEnum.TODO.getCode()))
                            ||(Objects.equals(detailDto.getSecondCheckStatus(),JyApproveStatusEnum.REJECT.getCode())))){
                dto.setCheckerErp(detailDto.getSecondChecker());
            }else {
                dto.setCheckerErp(detailDto.getThirdChecker());
            }
            //提交时间
            dto.setCheckTime(detailDto.getSubmitTime());
            dto.setImageUrls(detailDto.getGoodsImageUrl());
        }else { //完结的报废任务
            if(Objects.nonNull(detailDto.getThirdCheckTime())){
                dto.setCheckTime(detailDto.getThirdCheckTime());
                dto.setCheckerErp(detailDto.getThirdChecker());
            }else if(Objects.nonNull(detailDto.getSecondCheckTime())){
                dto.setCheckTime(detailDto.getSecondCheckTime());
                dto.setCheckerErp(detailDto.getSecondChecker());
            }else {
                dto.setCheckTime(detailDto.getFirstCheckTime());
                dto.setCheckerErp(detailDto.getFirstChecker());
            }
        }
    }

    /**
     * 根据bizId 批量查询生鲜数据
     * @param taskList
     * @return
     */
    private Map<String, ExpScrappedDetailDto> getTaskListOfScrapped(List<JyBizTaskExceptionEntity> taskList) {
        List<String> bizIds = taskList.stream()
                .filter(t -> Objects.equals(JyBizTaskExceptionTypeEnum.SCRAPPED.getCode(), t.getType())
                        && (Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), t.getStatus())
                        || Objects.equals(JyExpStatusEnum.COMPLETE.getCode(), t.getStatus())))
                .map(JyBizTaskExceptionEntity::getBizId).collect(Collectors.toList());
        JdCResponse<List<ExpScrappedDetailDto>> listOfscrappedResponse = jyScrappedExceptionService.getTaskListOfscrapped(bizIds);
        if (JdCResponse.CODE_SUCCESS.equals(listOfscrappedResponse.getCode()) && CollectionUtils.isNotEmpty(listOfscrappedResponse.getData())) {
            return listOfscrappedResponse.getData().stream().collect(Collectors.toMap(ExpScrappedDetailDto::getBizId, s->s));
        }
        return new HashMap<>();
    }

    /**
     * 根据bizId 批量查询破损数据
     * @param taskList
     * @param status
     * @return
     */
    private Map<String, JyExceptionDamageDto> getDamageDetailMapByBizTaskList(List<JyBizTaskExceptionEntity> taskList, Integer status) {
        List<String> bizIdList = taskList.stream()
                .filter(t-> Objects.equals(JyBizTaskExceptionTypeEnum.DAMAGE.getCode(),t.getType())
                        && (Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), t.getStatus())
                        || Objects.equals(JyExpStatusEnum.COMPLETE.getCode(), t.getStatus())))
                .map(JyBizTaskExceptionEntity::getBizId).collect(Collectors.toList());
        logger.info("setDataForDamageList bizIdList:{}, status:{}", JSON.toJSONString(bizIdList), status);
        return jyDamageExceptionService.getDamageDetailMapByBizIds(bizIdList, status);
    }

    /**
     * 根据bizId 批量查询拦截明细数据
     * @param taskList
     * @param status
     * @return
     */
    private Map<String, JyExceptionInterceptDetail> getInterceptDetailMapByBizTaskList(ExpTaskPageReq req, List<JyBizTaskExceptionEntity> taskList, Integer status) {
        List<String> bizIdList = taskList.stream()
                .filter(t-> Objects.equals(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode(),t.getType())
                        && (Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(), t.getStatus())
                        || Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), t.getStatus())
                        || Objects.equals(JyExpStatusEnum.COMPLETE.getCode(), t.getStatus())))
                .map(JyBizTaskExceptionEntity::getBizId).collect(Collectors.toList());
        logger.info("getInterceptDetailMapByBizTaskList:{}, status:{}", JSON.toJSONString(bizIdList), status);
        final JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
        jyExceptionInterceptDetailQuery.setSiteId(req.getSiteId());
        jyExceptionInterceptDetailQuery.setBizIdList(bizIdList);
        jyExceptionInterceptDetailQuery.setYn(Constants.YN_YES);
        Map<String, JyExceptionInterceptDetail> mapResult = new HashMap<>();
        if (CollectionUtils.isEmpty(bizIdList)) {
            return mapResult;
        }
        final List<JyExceptionInterceptDetail> jyExceptionInterceptDetails = jyExceptionInterceptDetailDao.queryList(jyExceptionInterceptDetailQuery);
        if(CollectionUtils.isNotEmpty(jyExceptionInterceptDetails)){
            mapResult = jyExceptionInterceptDetails.stream().collect(Collectors.toMap(JyExceptionInterceptDetail::getBizId, Function.identity()));
        }
        return mapResult;
    }

    /**
     * 释放进行中的人数
     *
     */
    @Override
    public JdCResponse<Object> releaseReceivingCount(ExpTaskPageReq req) {
        // 仅待取件的 任务 支持进行中计数
        if (StringUtils.isBlank(req.getGridCode())) {
            return JdCResponse.ok();
        }

        // 记录取件进行中
        // 按岗位统计
        String gridKey = req.getFloor() + "|" + req.getGridCode();
        redisClient.hSet(gridKey, req.getUserErp(), "0");
        redisClient.expire(gridKey, COUNT_CACHE_SECOND, TimeUnit.SECONDS);
        logger.info("释放进行中的人数,gridKey={},erp={}", gridKey, req.getUserErp());

        return JdCResponse.ok();
    }


    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> receive(ExpReceiveReq req) {
        logger.info("任务领取-receive-{}",JSON.toJSONString(req));
        if (StringUtils.isBlank(req.getBarCode())) {
            return JdCResponse.fail("条码不能为空!");
        }
        Object taskDto = null;
        try {
            // 三无系统只处理大写字母
            // req.setBarCode(WaybillUtil.getWaybillCode(req.getBarCode().toUpperCase()));
            req.setBarCode(req.getBarCode().toUpperCase());

            String positionCode = req.getPositionCode();
            PositionDetailRecord position = getPosition(positionCode);
            if (position == null) {
                return JdCResponse.fail("岗位码有误!" + positionCode);
            }

            BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
            if (baseStaffByErp == null) {
                return JdCResponse.fail("登录人ERP有误!" + req.getUserErp());
            }

            // 校验操作人的岗位 与 任务被分配岗位是否匹配
            String gridRid = getGridRid(position);
            String bizId = getBizId(WaybillUtil.getWaybillCode(req.getBarCode().toUpperCase()), position.getSiteCode());
            JyBizTaskExceptionEntity jyBizTaskExceptionExist = jyBizTaskExceptionDao.findByBizId(bizId);
            if (jyBizTaskExceptionExist == null) {
                // 如果为空，则进一步按条件查询
                final JyBizTaskExceptionQuery jyBizTaskExceptionQuery = new JyBizTaskExceptionQuery();
                jyBizTaskExceptionQuery.setSiteCode(req.getSiteId().longValue());
                jyBizTaskExceptionQuery.setBarCode(req.getBarCode());
                jyBizTaskExceptionExist = jyBizTaskExceptionDao.selectOneByCondition(jyBizTaskExceptionQuery);
                if (jyBizTaskExceptionExist == null) {
                    return JdCResponse.fail("该条码无相关任务!" + req.getBarCode());
                }
            }

            if (!Objects.equals(JyExpStatusEnum.TO_PICK.getCode(), jyBizTaskExceptionExist.getStatus())) {
                return JdCResponse.fail("当前任务"+req.getBarCode()+"已被领取,请勿重复操作!");
            }
            if (!Objects.equals(gridRid, jyBizTaskExceptionExist.getDistributionTarget())) {
    //            return JdCResponse.fail("领取人的岗位与任务被分配的岗位不匹配!" + jyBizTaskExceptionExist.getDistributionTarget());
                return JdCResponse.fail("该条码无相关任务!" + req.getBarCode());
            }

            JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
            update.setBizId(jyBizTaskExceptionExist.getBizId());
            update.setStatus(JyExpStatusEnum.TO_PROCESS.getCode());
            update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PENDING_ENTRY.getCode());
            update.setHandlerErp(req.getUserErp());
            update.setUpdateUserErp(req.getUserErp());
            update.setUpdateUserName(baseStaffByErp.getStaffName());
            update.setUpdateTime(new Date());
            update.setProcessBeginTime(new Date());

            jyBizTaskExceptionDao.updateByBizId(update);
            recordLog(JyBizTaskExceptionCycleTypeEnum.RECEIVE,update);
            //发送修改状态消息
            sendScheduleTaskStatusMsg(jyBizTaskExceptionExist.getBizId(), baseStaffByErp.getAccountNumber(), JyScheduleTaskStatusEnum.STARTED, scheduleTaskChangeStatusProducer);


            // 拼装已领取的任务
            taskDto = getTaskDto(jyBizTaskExceptionExist);
        } catch (Exception e) {
            logger.error("receive exception {}", JsonHelper.toJson(req), e);
            return JdCResponse.fail("系统异常");
        }
        return JdCResponse.ok(taskDto);
    }

    /**
     * 按条码查询
     *
     */
    @Override
    public JdCResponse<ExpTaskDto> queryByBarcode(ExpReceiveReq req) {
        JdCResponse<ExpTaskDto> validateResult = this.validateExpReceiveReq(req);
        if (validateResult != null){
            return validateResult;
        }
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("岗位码有误!" + req.getPositionCode());
        }
        String bizId = getBizId(req.getBarCode(), position.getSiteCode());
        JyBizTaskExceptionEntity taskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if (taskEntity == null) {
            return JdCResponse.fail("该条码无相关任务!" + req.getBarCode());
        }
        ExpTaskDto taskDto = getTaskDto(taskEntity);
        if(ObjectUtils.notEqual(taskEntity.getHandlerErp(), req.getUserErp())){
            return JdCResponse.fail("您未领取该条码任务!" + req.getBarCode());
        }
        return JdCResponse.ok(taskDto);
    }

    /**
     * 校验扫码请求参数
     * @param req
     * @return
     */
    private JdCResponse<ExpTaskDto> validateExpReceiveReq(ExpReceiveReq req) {
        if (req == null) {
            return JdCResponse.fail("参数不能为空!");
        }
        if (StringUtils.isBlank(req.getBarCode())) {
            return JdCResponse.fail("条形码不能为空!");
        }
        if (StringUtils.isBlank(req.getPositionCode())) {
            return JdCResponse.fail("岗位码不能为空!");
        }
        return null;
    }

    /**
     * 任务明细
     *
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {
        if (StringUtils.isBlank(req.getBizId())) {
            return JdCResponse.fail("业务ID不能为空!");
        }

        JyBizTaskExceptionEntity entity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (entity == null) {
            return JdCResponse.fail("当前任务不存在!");
        }
        ExpTaskDetailDto dto = new ExpTaskDetailCacheDto();
        dto.setBizId(entity.getBizId());

        String redisKey = TASK_CACHE_PRE + req.getBizId();
        String s = redisClient.get(redisKey);
        logger.info("三无异常岗-查询到的缓存数据为:{},redisKey={}", s, redisKey);
        if (StringUtils.isBlank(s)) {
            return JdCResponse.ok(dto);
        }

        ExpTaskDetailCacheDto cacheDto = JSON.parseObject(s, ExpTaskDetailCacheDto.class);
        BeanUtils.copyProperties(cacheDto, dto);
        if (StringUtils.isBlank(dto.getBatchNo()) && CollectionUtils.isNotEmpty(cacheDto.getRecentSendCodeList())) {
            StringBuilder batchNo = new StringBuilder();
            for (String value : cacheDto.getRecentSendCodeList()) {
                if (batchNo.length() > 0) {
                    batchNo.append(SPLIT);
                }
                batchNo.append(value);
            }
            dto.setBatchNo(batchNo.toString());
        }
        if (StringUtils.isBlank(dto.getTo()) && CollectionUtils.isNotEmpty(cacheDto.getRecentReceiveSiteList())) {
            StringBuilder to = new StringBuilder();
            for (Integer value : cacheDto.getRecentReceiveSiteList()) {
                if (to.length() > 0) {
                    to.append(SPLIT);
                }
                to.append(value);
            }
            dto.setTo(to.toString());
        }

        // 设置 上架日期
        if ((Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), entity.getStatus()) && Objects.equals(JyBizTaskExceptionProcessStatusEnum.WAITING_PRINT.getCode(), entity.getProcessingStatus()))
                ||(Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), entity.getStatus()) && (Objects.equals(JyBizTaskExceptionTypeEnum.SANWU.getCode(),entity.getType())))){
            JyExceptionEntity query = new JyExceptionEntity();
            query.setSiteCode(entity.getSiteCode());
            query.setBizId(entity.getBizId());
            query.setBarCode(entity.getBarCode());
            JyExceptionEntity jyExceptionEntity = jyExceptionDao.queryByBarCodeAndSite(query);
            if (jyExceptionEntity != null && jyExceptionEntity.getShelfTime() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dto.setShelfTime(dateFormat.format(jyExceptionEntity.getShelfTime()));
            }
        }

        return JdCResponse.ok(dto);
    }

    /**
     * 处理任务接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> processTask(ExpTaskDetailReq req) {

        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("岗位码有误!");
        }

        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            return JdCResponse.fail("登录人ERP有误!" + req.getUserErp());
        }

        JyBizTaskExceptionEntity bizEntity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (bizEntity == null) {
            return JdCResponse.fail("无相关任务!bizId=" + req.getBizId());
        }
        if (!Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(), bizEntity.getStatus())) {
            return JdCResponse.fail("当前任务已被处理,请勿重复操作!bizId=" + req.getBizId());
        }

        JSONObject cacheObj = new JSONObject();
        String key = TASK_CACHE_PRE + req.getBizId();
        String s = redisClient.get(key);
        logger.info("三无异常岗:处理任务-查询到的缓存{}", s);
        if (StringUtils.isNotBlank(s)) {
            cacheObj = JSON.parseObject(s);
        }
        JSONObject reqObj = (JSONObject) JSONObject.toJSON(req);
        cacheObj.putAll(reqObj);

        redisClient.set(key, cacheObj.toJSONString());
        // 处理任务后 更新任务明细过期时间：继续保留30天
        redisClient.expire(key, TASK_DETAIL_CACHE_DAYS, TimeUnit.DAYS);

        if (logger.isInfoEnabled()) {
            logger.info("三无异常岗:处理任务-缓存数据与提交数据拼装后{}", cacheObj.toJSONString());
        }
        // 存储类型 0暂存 1提交
        if ("0".equals(req.getSaveType())) {
            return JdCResponse.ok();
        }

        //提交任务时：部分校验
        // 校验场地ID
        ExpTaskDetailCacheDto cacheDto = cacheObj.toJavaObject(ExpTaskDetailCacheDto.class);
        if (StringUtils.isNotBlank(cacheDto.getTo())){
            for (String toId : cacheDto.getTo().split(SPLIT)) {
                if (!BusinessUtil.isSiteCode(toId)){
                    JdCResponse<Object> fail = JdCResponse.fail("下级地编号不合法!" + cacheDto.getTo());
                    fail.setData("to");
                    fail.setCode(JdCResponse.CODE_PARTIAL_SUCCESS);
                    return fail;
                }
                Integer toSiteCode = Integer.valueOf(toId);
                BaseStaffSiteOrgDto toSite = baseMajorManager.getBaseSiteBySiteId(toSiteCode);
                if (toSite == null){
                    JdCResponse<Object> fail = JdCResponse.fail("下级地编号不存在!" + cacheDto.getTo());
                    fail.setData("to");
                    fail.setCode(JdCResponse.CODE_PARTIAL_SUCCESS);
                    return fail;
                }
            }
        }
        // 校验批次号
        if (StringUtils.isNotBlank(cacheDto.getBatchNo())) {
            for (String bno : cacheDto.getBatchNo().split(SPLIT)) {
                if (!BusinessUtil.isSendCode(bno)){
                    JdCResponse<Object> fail = JdCResponse.fail("批次号不合法!" + cacheDto.getBatchNo());
                    fail.setData("batchNo");
                    fail.setCode(JdCResponse.CODE_PARTIAL_SUCCESS);
                    return fail;
                }
            }
        }


        cacheDto.setExpBarcode(bizEntity.getBarCode());
        cacheDto.setExpCreateTime(bizEntity.getCreateTime() == null ? System.currentTimeMillis() : bizEntity.getCreateTime().getTime());
        JyExpSourceEnum source = JyExpSourceEnum.getEnumByCode(bizEntity.getSource());
        cacheDto.setSource(source==null?"通用": source.getText());
        // 调用 三无接口
        ExpInfoSumaryInputDto dto = getExpInfoDto(cacheDto);
        try {
            CommonDto commonDto = expInfoSummaryJsfManager.addExpInfoDetail(dto);

            if (logger.isInfoEnabled()) {
                logger.info("三无异常岗:处理任务-提报三无参数:{},响应:{}", JSON.toJSONString(dto), JSON.toJSONString(commonDto));
            }
            if (!Objects.equals(commonDto.getCode(), CommonDto.CODE_SUCCESS)) {
                return JdCResponse.fail("提报三无系统失败:" + commonDto.getMessage());
            }
        } catch (Exception e) {
            logger.error("调用三无接口异常-参数:" + JSON.toJSONString(dto), e);
            return JdCResponse.fail("提报三无系统失败请稍后再试!");
        }
        //修改状态为 status处理中-processingStatus匹配中
        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(req.getBizId());
        update.setStatus(JyExpStatusEnum.PROCESSING.getCode());
        update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_MATCH.getCode());
        update.setUpdateUserErp(req.getUserErp());
        update.setUpdateUserName(baseStaffByErp.getStaffName());
        update.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(update);
        recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_SUBMIT,update);
        return JdCResponse.ok();
    }

    @Override
    public void expefNotifyProcesser(ExpefNotify mqDto) {
        if (null == mqDto.getNotifyType()){
            logger.warn("三无系统通知数据缺少通知类型，消息丢弃");
            return;
        }
        try{
            switch (mqDto.getNotifyType()){
                case CREATED:
                    //新增异常任务
                    createSanWuTask(mqDto);
                    break;
                case MATCH_SUCCESS:
                    matchSuccessProcess(mqDto);
                    break;
                case PROCESSED:
                    sanwuComplate(mqDto);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("xxx",e);
        }

    }

    @Override
    public void printSuccess(JyExceptionPrintDto printDto) {
        logger.info("JyExceptionServiceImpl.printSuccess -{}",JSON.toJSONString(printDto));
        if (printDto.getSiteCode() == null){
            return;
        }
        if (Objects.equals(printDto.getOperateType(), WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType())){
            logger.info("JyExceptionServiceImpl.printSuccess 包裹补打");
            if (StringUtils.isNotEmpty(printDto.getPackageCode()) || StringUtils.isNotEmpty(printDto.getWaybillCode())){
                JyExceptionEntity conditon = new JyExceptionEntity();
                conditon.setSiteCode(Long.valueOf(printDto.getSiteCode()));
                if(StringUtils.isNotBlank(printDto.getPackageCode())){
                    conditon.setPackageCode(printDto.getPackageCode());
                }
                if(StringUtils.isNotBlank(printDto.getWaybillCode())){
                    conditon.setWaybillCode(printDto.getWaybillCode());
                }
                logger.info("printSuccess 查询条件 -{}",JSON.toJSONString(conditon));
                List<JyExceptionEntity> jyExceptionEntities = jyExceptionDao.queryByPackageCodeAndSite(conditon);
                if (CollectionUtils.isEmpty(jyExceptionEntities)){
                    return;
                }
                for (JyExceptionEntity entity:jyExceptionEntities){
                    updateExceptionResult(entity.getBizId(),printDto.getUserErp(),printDto.getOperateTime(),false);
                }
            }

        }else if(Objects.equals(printDto.getOperateType(), WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType())){
            logger.info("JyExceptionServiceImpl.printSuccess 换单打印");
            if (StringUtils.isEmpty(printDto.getWaybillCode())){
                return;
            }
            if (!WaybillUtil.isWaybillCode(printDto.getWaybillCode())){
                return;
            }
            JyExceptionEntity conditon = new JyExceptionEntity();
            conditon.setSiteCode(Long.valueOf(printDto.getSiteCode()));
            conditon.setWaybillCode(printDto.getWaybillCode());
            List<JyExceptionEntity> jyExceptionEntities = jyExceptionDao.queryByPackageCodeAndSite(conditon);
            if (CollectionUtils.isEmpty(jyExceptionEntities)){
                return;
            }
            for (JyExceptionEntity entity:jyExceptionEntities){
                updateExceptionResult(entity.getBizId(),printDto.getUserErp(),printDto.getOperateTime(),false);
            }
        }


    }

    @Override
    public void queryOverTimeExceptionAndNotice() {
        Date queryEndTime = DateHelper.addDate(new Date(), -1);
        Date queryStartTime = DateHelper.addDate(queryEndTime, -30); // 默认查询30天之内的数据
        Map<String ,Object> params = Maps.newHashMap();
        params.put("queryStartTime", queryStartTime);
        params.put("queryEndTime", queryEndTime);
        List<JyExceptionAgg> jyExceptionAggs = jyBizTaskExceptionDao.queryUnCollectAndOverTimeAgg(params);
        if(CollectionUtils.isEmpty(jyExceptionAggs)){
            logger.warn("根据条件:{}未查询到未领取的报废任务!", DateHelper.formatDateTime(queryStartTime) + Constants.SEPARATOR_HYPHEN + DateHelper.formatDateTime(queryEndTime));
            return;
        }
        // 将查询的数据转换成map：key是场地，value是场地下的所有agg数据
        Map<Integer, List<JyExceptionAgg>> map = Maps.newHashMap();
        for (JyExceptionAgg temp : jyExceptionAggs) {
            Integer siteCode = temp.getSiteCode();
            if(map.containsKey(siteCode)){
                map.get(siteCode).add(temp);
            }else {
                List<JyExceptionAgg> singleList = Lists.newArrayList();
                singleList.add(temp);
                map.put(temp.getSiteCode(), singleList);
            }
        }
        // 异步推送咚咚
        int count = 0;
        for (Integer siteCode : map.keySet()) {
            if(logger.isInfoEnabled()){
                logger.info("未领取的超时任务异步咚咚通知场地:{}的负责人!", siteCode);
            }
            // 防止大批量处理任务，设置休眠降低发送频率
            trySleep(count ++);
            String businessId = siteCode + Constants.SEPARATOR_HYPHEN + DateHelper.formatDate(new Date());
            dmsUnCollectOverTimeNoticeProducer.sendOnFailPersistent(businessId, JsonHelper.toJson(map.get(siteCode)));
        }
    }

    private void trySleep(int count) {
        if(count % 5 == Constants.NUMBER_ZERO){
            try {
                Thread.sleep(100);
            }catch (Exception e){
                logger.error("休眠异常!", e);
            }
        }
    }

    @Override
    public void queryFreshScrapDetailAndNotice() {
        Date queryStartTime = DateHelper.getCurrentDayWithOutTimes();
        Date queryEndTime = new Date();
        List<String> handlerErpList = queryScrapHandlerErp(queryStartTime, queryEndTime);
        if(CollectionUtils.isEmpty(handlerErpList)){
            logger.warn("未查询到当天18点之前已领取的报废任务!");
            return;
        }
        List<Message> messageList = Lists.newArrayList();
        for (String handlerErp : handlerErpList) {
            Message message = new Message();
            message.setTopic(dmsScrapNoticeProducer.getTopic());
            JyExScrapNoticeMQ jyExScrapNoticeMQ = new JyExScrapNoticeMQ();
            jyExScrapNoticeMQ.setHandlerErp(handlerErp);
            jyExScrapNoticeMQ.setQueryStartTime(queryStartTime);
            jyExScrapNoticeMQ.setQueryEndTime(queryEndTime);
            message.setText(JsonHelper.toJson(jyExScrapNoticeMQ));
            message.setBusinessId(handlerErp + Constants.SEPARATOR_HYPHEN + DateHelper.formatDate(new Date()));
            messageList.add(message);
        }
        if(logger.isInfoEnabled()){
            logger.info("已领取的报废任务咚咚通知领取人:{}", JsonHelper.toJson(handlerErpList));
        }
        dmsScrapNoticeProducer.batchSendOnFailPersistent(messageList);
    }

    @Override
    public List<String> queryScrapHandlerErp(Date queryStartTime, Date queryEndTime) {
        Map<String ,Date> params = Maps.newHashMap();
        params.put("queryStartTime", queryStartTime);
        params.put("queryEndTime", queryEndTime);
        return jyBizTaskExceptionDao.queryScrapHandlerErp(params);
    }

    @Override
    public List<JyBizTaskExceptionEntity> queryScrapDetailByCondition(String handlerErp, Date queryStartTime, Date queryEndTime) {
        Map<String ,Object> params = Maps.newHashMap();
        params.put("handlerErp", handlerErp);
        params.put("queryStartTime", queryStartTime);
        params.put("queryEndTime", queryEndTime);
        return jyBizTaskExceptionDao.queryScrapDetailByCondition(params);
    }

    @Override
    public void dealCustomerNotifyResult(JyExCustomerNotifyMQ jyExCustomerNotifyMQ) {
        // 回传状态
        String resultType = jyExCustomerNotifyMQ.getResultType();
        switch (CustomerNotifyStatusEnum.convertApproveEnum(resultType)) {
            case SELF_REPAIR_ORDER:
                // 自营补单  调用终端妥投接口
                delivered(jyExCustomerNotifyMQ.getExptId());
                break;
            case SELF_REPAIR_PRICE:
                // 自营补差  调用终端妥投接口
                delivered(jyExCustomerNotifyMQ.getExptId());
                break;
            case AGREE_LP:
                // 外单客户同意理赔
                break;
            case NOT_AGREE_LP:
                // 外单客户不同意理赔
                break;
            case CANCEL_ORDER:
                // 取消订单: 写报废全程跟踪，发拦截成功消息
                kfNotifyCancelDeal(jyExCustomerNotifyMQ);
                break;
            default:
                logger.info("客服回传其他状态不做处理!");
                return;
        }
        // 更新异常任务表状态
        //updateExceptionResult(jyExCustomerNotifyMQ.getBusinessId(), jyExCustomerNotifyMQ.getOperateErp(), new Date(), true);
    }

    private void kfNotifyCancelDeal(JyExCustomerNotifyMQ jyExCustomerNotifyMQ) {
        String bizId = jyExCustomerNotifyMQ.getExptId();
        JyBizTaskExceptionEntity exTaskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if(exTaskEntity == null){
            logger.warn("根据业务主键:{}未查询到异常任务数据!", bizId);
            return;
        }
        // 报废全程跟踪
        pushScrapTrace(exTaskEntity);
        // 发送拦截成功消息
        sendInterceptMQ(exTaskEntity);
    }

    private void sendInterceptMQ(JyBizTaskExceptionEntity exTaskEntity) {
        if(logger.isInfoEnabled()){
            logger.info("生效报废单:{}触发拦截成功消息!", exTaskEntity.getBarCode());
        }
        bdBlockerCompleteMQ.sendOnFailPersistent(exTaskEntity.getBizId(),
                BusinessUtil.bdBlockerCompleteMQ(exTaskEntity.getBarCode(), DmsConstants.ORDER_TYPE_REVERSE, DmsConstants.MESSAGE_TYPE_BAOFEI, DateHelper.formatDateTimeMs(new Date())));
    }

    @Override
    public void pushScrapTrace(JyBizTaskExceptionEntity exTaskEntity) {
        if(StringUtils.isBlank(exTaskEntity.getBarCode())){
            logger.warn("单号为空!");
            return ;
        }
        String waybillCode = WaybillUtil.getWaybillCode(exTaskEntity.getBarCode());
        BdTraceDto traceDto = new BdTraceDto();
        traceDto.setPackageBarCode(waybillCode);
        traceDto.setWaybillCode(waybillCode);
        traceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP);
        traceDto.setOperatorDesp(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP_MSG);
        traceDto.setOperatorSiteId(exTaskEntity.getSiteCode().intValue());
        traceDto.setOperatorSiteName(exTaskEntity.getSiteName());
        traceDto.setOperatorUserName(exTaskEntity.getHandlerErp());
        traceDto.setOperatorTime(new Date());
        traceDto.setWaybillTraceType(1);
        logger.info("发送运单报废全程跟踪信息-{}",JSON.toJSONString(traceDto));
        waybillQueryManager.sendBdTrace(traceDto);
    }

    private void createSanWuTask(ExpefNotify mqDto) {
        String bizId = getBizId(mqDto.getBarCode(),mqDto.getSiteCode());
        JyBizTaskExceptionEntity byBizId = jyBizTaskExceptionDao.findByBizId(bizId);
        if (byBizId != null) {
            logger.warn("已存在当前条码的任务,请勿重复提交!");
            return ;
        }
        JyBizTaskExceptionEntity taskEntity = new JyBizTaskExceptionEntity();
        taskEntity.setBizId(bizId);
        taskEntity.setType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
        taskEntity.setSource(JyExpSourceEnum.SANWU_PC.getCode());
        taskEntity.setBarCode(mqDto.getBarCode());
        taskEntity.setTags(JyBizTaskExceptionTagEnum.SANWU.getCode());
        taskEntity.setSiteCode(new Long(mqDto.getSiteCode()));
        taskEntity.setSiteName(mqDto.getSiteName());

        taskEntity.setStatus(JyExpStatusEnum.TO_PROCESS.getCode());
        taskEntity.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_MATCH.getCode());
        taskEntity.setProcessBeginTime(mqDto.getNotifyTime());
        taskEntity.setCreateUserErp(mqDto.getNotifyErp());
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(mqDto.getNotifyErp());
        if (baseStaffByErp == null){
            logger.error("获取操作人信息失败！");
            return;
        }
        taskEntity.setCreateUserName(baseStaffByErp.getStaffName());
        taskEntity.setCreateTime(new Date());
        taskEntity.setUpdateUserErp(mqDto.getNotifyErp());
        taskEntity.setUpdateUserName(baseStaffByErp.getStaffName());
        taskEntity.setUpdateTime(new Date());
        taskEntity.setTimeOut(JyBizTaskExceptionTimeOutEnum.UN_TIMEOUT.getCode());
        taskEntity.setYn(1);

        JyExceptionEntity expEntity = new JyExceptionEntity();
        expEntity.setBizId(bizId);
        expEntity.setBarCode(mqDto.getBarCode());
        expEntity.setSiteCode(new Long(mqDto.getSiteCode()));
        expEntity.setSiteName(mqDto.getSiteName());
        expEntity.setCreateUserErp(mqDto.getNotifyErp());
        expEntity.setCreateUserName(baseStaffByErp.getStaffName());
        expEntity.setCreateTime(new Date());

        try {
            jyBizTaskExceptionDao.insertSelective(taskEntity);
            jyExceptionDao.insertSelective(expEntity);
            recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_SUBMIT,taskEntity);
            //发送 mq 通知调度系统
            sendToSchedule(mqDto, bizId, baseStaffByErp);
        } catch (Exception e) {
            logger.error("写入异常提报数据出错了,request=" + JSON.toJSONString(mqDto), e);
        }
    }

    private void sendToSchedule(ExpefNotify mqDto, String bizId, BaseStaffSiteOrgDto baseStaffByErp) throws JMQException {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setTaskType(JyScheduleTaskTypeEnum.EXCEPTION.getCode());
        req.setBizId(bizId);
        req.setOpeUser(mqDto.getNotifyErp());
        req.setOpeUserName(baseStaffByErp.getStaffName());
        req.setOpeTime(new Date());
        req.setTaskStatus(JyScheduleTaskStatusEnum.STARTED.getCode());
        scheduleTaskAddWorkerProducer.sendOnFailPersistent(bizId,JsonHelper.toJson(req));
    }

    /**
     * 异常任务完成处理
     * @param
     */
    private void sanwuComplate(ExpefNotify mqDto) {
        String bizId = getBizId(mqDto.getBarCode(),mqDto.getSiteCode());
        updateExceptionResult(bizId,mqDto.getNotifyErp(),mqDto.getNotifyTime(),true);
    }

    @Override
    public void updateExceptionResult(String bizId, String operateErp, Date dateTime, boolean precessComplete) {
        JyBizTaskExceptionEntity bizTaskException = jyBizTaskExceptionDao.findByBizId(bizId);
        if (bizTaskException == null){
            logger.error("获取异常业务任务数据失败！");
            return;
        }
        JyExceptionEntity jyExceptionEntity = new JyExceptionEntity();
        jyExceptionEntity.setBizId(bizId);
        jyExceptionEntity.setSiteCode(bizTaskException.getSiteCode());
        JyExceptionEntity entity = jyExceptionDao.queryByBarCodeAndSite(jyExceptionEntity);
        if (entity == null){
            logger.error("获取异常业务数据失败！");
            return;
        }
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(operateErp);
        // biz表修改状态
        JyBizTaskExceptionEntity conditon = new JyBizTaskExceptionEntity();
        conditon.setBizId(bizTaskException.getBizId());
        conditon.setStatus(JyExpStatusEnum.COMPLETE.getCode());
        conditon.setUpdateTime(dateTime);
        conditon.setUpdateUserErp(baseStaffByErp == null ? operateErp : baseStaffByErp.getAccountNumber());
        conditon.setUpdateUserName(baseStaffByErp == null ? null : baseStaffByErp.getStaffName());
        if (precessComplete){
            conditon.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.DONE.getCode());
            conditon.setProcessEndTime(dateTime);
            recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESSED,conditon);
        }
        jyBizTaskExceptionDao.updateByBizId(conditon);
        recordLog(JyBizTaskExceptionCycleTypeEnum.CLOSE,conditon);
        //发送修改状态消息
        sendScheduleTaskStatusMsg(bizTaskException.getBizId(), operateErp, JyScheduleTaskStatusEnum.CLOSED, scheduleTaskChangeStatusWorkerProducer);
    }

    @Override
    public JdCResponse<Boolean> checkExceptionPrincipal(ExpBaseReq req) {
        if(logger.isInfoEnabled()){
            logger.info("checkExceptionPrincipal-入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed(JdCResponse.MESSAGE_SUCCESS);
        response.setData(Boolean.FALSE);
        if(req == null || StringUtils.isBlank(req.getPositionCode()) || StringUtils.isBlank(req.getUserErp())){
            return response.fail("入参不能为空!");
        }

        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return response.fail("网格码有误!");
        }
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            return response.fail("登录人ERP有误!" + req.getUserErp());
        }

        String refGridKey = position.getRefGridKey();
        if(StringUtils.isBlank(refGridKey)){
            return response.fail("网格关联业务主键为空!");
        }
        WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
        workStationGridQuery.setBusinessKey(refGridKey);
        com.jdl.basic.common.utils.Result<WorkStationGrid> result = workStationGridManager.queryByGridKey(workStationGridQuery);
        if(result == null || result.getData() == null || StringUtils.isBlank(result.getData().getOwnerUserErp())){
            return response.fail("获取网格场地信息失败!");
        }
        String ownerUserErp = result.getData().getOwnerUserErp();
        //校验场地网格负责人
        if(Objects.equals(req.getUserErp(),ownerUserErp)){
            response.setData(Boolean.TRUE);
        }
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyExceptionServiceImpl.exceptionTaskCheckByExceptionType", mState = {JProEnum.TP})
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req) {
        if(logger.isInfoEnabled()){
            logger.info("exceptionTaskCheckByExceptionType-入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<Boolean> response = new JdCResponse<>();
        try{
            if(req == null || req.getType() == null || StringUtils.isBlank(req.getBarCode())){
                response.toFail("入参不能为空!");
                return response;
            }
            Waybill waybill = waybillService.getWaybillByWayCode(WaybillUtil.getWaybillCode(req.getBarCode()));
            if(waybill == null){
                response.toFail("运单信息为空!");
                return response;
            }
            JyExceptionStrategy strategy = jyExceptionStrategyFactory.getStrategy(req.getType());
            return strategy.exceptionTaskCheckByExceptionType(req,waybill);

        }catch (Exception e){
            logger.error("根据异常类型校验单号异常-param-{}",JSON.toJSONString(req),e);
            response.toError("根据异常类型校验单号异常!");
            return response;
        }
    }

    private void sendScheduleTaskStatusMsg(String bizId, String userErp,
                                           JyScheduleTaskStatusEnum status, DefaultJMQProducer producer) {
        //通知任务调度系统状态修改
        JyScheduleTaskChangeStatusReq req = new JyScheduleTaskChangeStatusReq();
        try{
            req.setBizId(bizId);
            req.setChangeTime(new Date());
            req.setOpeUser(userErp);
            req.setTaskStatus(status);
            req.setTaskType(JyScheduleTaskTypeEnum.EXCEPTION);
            producer.sendOnFailPersistent(bizId, JsonHelper.toJson(req));
            logger.info("异常岗-任务领取发送状态更新发送mq完成:body={}", JsonHelper.toJson(req));
        }catch (Exception e) {
            logger.error("异常岗-任务领取发送状态更新发送mq失败,message:{} :  ", JsonHelper.toJson(req),e);
        }
    }

    /**
     * 匹配成功处理
     * @param mqDto
     */
    private void matchSuccessProcess(ExpefNotify mqDto) {
        String bizId = getBizId(mqDto.getBarCode(),mqDto.getSiteCode());
        JyBizTaskExceptionEntity bizTaskException = jyBizTaskExceptionDao.findByBizId(bizId);
        if (bizTaskException == null){
            logger.error("获取异常业务任务数据失败！");
            return;
        }
        JyExceptionEntity jyExceptionEntity = new JyExceptionEntity();
        jyExceptionEntity.setBizId(bizId);
        jyExceptionEntity.setSiteCode(Long.valueOf(bizTaskException.getSiteCode()));
        JyExceptionEntity entity = jyExceptionDao.queryByBarCodeAndSite(jyExceptionEntity);
        if (entity == null){
            logger.error("获取异常业务数据失败！");
            return;
        }
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(mqDto.getNotifyErp());
        if (baseStaffByErp == null){
            logger.error("获取操作人信息失败！");
            return;
        }
        //业务表记录匹配成功单号
        entity.setPackageCode(mqDto.getPackageCode());
        entity.setWaybillCode(WaybillUtil.getWaybillCode(mqDto.getPackageCode()));
        entity.setUpdateTime(mqDto.getNotifyTime());
        entity.setUpdateUserErp(mqDto.getNotifyErp());
        entity.setUpdateUserName(baseStaffByErp.getStaffName());
        jyExceptionDao.update(entity);
        // biz表修改状态
        JyBizTaskExceptionEntity conditon = new JyBizTaskExceptionEntity();
        conditon.setStatus(JyExpStatusEnum.PROCESSING.getCode());
        conditon.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_PRINT.getCode());
        conditon.setProcessEndTime(mqDto.getNotifyTime());
        conditon.setUpdateTime(mqDto.getNotifyTime());
        conditon.setUpdateUserErp(mqDto.getNotifyErp());
        conditon.setBizId(bizTaskException.getBizId());
        conditon.setUpdateUserName(baseStaffByErp.getStaffName());
        jyBizTaskExceptionDao.updateByBizId(conditon);
        recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESSED,conditon);
    }


    /**
     * 近期发货的下游场地
     */
    private Collection<Integer> queryRecentSendInfo(ExpUploadScanReq req) {
        Set<Integer> siteIdList = new HashSet<>();

        if (CollectionUtils.isNotEmpty(req.getRecentPackageCodeList())) {
            for (String barcode : req.getRecentPackageCodeList()) {
                if (!WaybillUtil.isPackageCode(barcode)) {
                    continue;
                }
                SendDetail param = new SendDetail();
                param.setPackageBarcode(barcode);
                param.setCreateSiteCode(req.getSiteId());
                //未取消 & 已发货
                param.setIsCancel(0);
                param.setStatus(1);
                SendDetail sendDetail = sendDetailService.queryOneSendDatailBySendM(param);
                if (sendDetail != null && sendDetail.getReceiveSiteCode() != null) {
                    siteIdList.add(sendDetail.getReceiveSiteCode());
                    return siteIdList;
                }
            }
        }
        if (StringUtils.isNotBlank(req.getBizId())) {
            JyBizTaskSendVehicleDetailEntity entity = new JyBizTaskSendVehicleDetailEntity();
            entity.setStartSiteId((long) req.getSiteId());
            entity.setSendVehicleBizId(req.getBizId());
            List<JyBizTaskSendVehicleDetailEntity> detailEntityList = jyBizTaskSendVehicleDetailDao.findByMainVehicleBiz(entity, null);
            if (CollectionUtils.isNotEmpty(detailEntityList)) {
                for (JyBizTaskSendVehicleDetailEntity e : detailEntityList) {
                    siteIdList.add(e.getStartSiteId().intValue());
                }
            }
            return siteIdList;
        }


        return siteIdList;
    }

    /**
     * 近期验货的上游发货批次
     */
    private Collection<String> queryRecentInspectInfo(ExpUploadScanReq req) {
        Set<String> sendCodeList = new HashSet<>();

        // 按包裹号 查询上游
        if (CollectionUtils.isNotEmpty(req.getRecentPackageCodeList())) {
            for (String packageCode : req.getRecentPackageCodeList()) {
                if (!WaybillUtil.isPackageCode(packageCode)) {
                    continue;
                }
                // 查询上游 发货批次
                String sendCode = querySendCode(req.getSiteId(), packageCode, null);
                if (sendCode != null) {
                    sendCodeList.add(sendCode);
                    break;
                }
            }
            return sendCodeList;
        }

        // 按封车编码查询上游批次
        if (StringUtils.isNotBlank(req.getBizId())) {
            // 查询上游 发货批次
            String sendCode = querySendCode(req.getSiteId(), null, req.getBizId());
            if (sendCode != null) {
                sendCodeList.add(sendCode);
            }
        }

        return sendCodeList;
    }

    // 查询上游 发货批次
    private String querySendCode(Integer siteId, String packageCode, String bizId) {
        Pager<JySealCarDetail> query = new Pager<>();
        query.setPageSize(1);
        query.setPageNo(1);
        JySealCarDetail search = new JySealCarDetail();
        search.setEndSiteId(String.valueOf(siteId));
        if (packageCode != null) {
            search.setPackageBarcode(packageCode);
        } else if (bizId != null) {
            search.setSealCarCode(bizId);
        }
        query.setSearchVo(search);
        Pager<JySealCarDetail> unloadDetail = jyUnloadVehicleManager.querySearCarDetail(query);
        if (logger.isInfoEnabled()) {
            logger.info("三无异常岗:查询封车批次参数={},响应={}", JSON.toJSONString(query), JSON.toJSONString(unloadDetail));
        }
        if (unloadDetail != null && unloadDetail.getData() != null && CollectionUtils.isNotEmpty(unloadDetail.getData())) {
            // 测试环境无数据，uat环境新增 sendCode 字段
            return unloadDetail.getData().get(0).getSendCode();
        }
        return null;
    }

    /**
     * 格式化停留时间
     */
    private String getStayTime(Date createTime) {
        if (createTime == null) {
            return "";
        }
        long millis = System.currentTimeMillis() - createTime.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis - TimeUnit.HOURS.toMillis(hours));
        return hours + "时" + minutes + "分";
    }

    /**
     * 格式化标签
     */
    private List<TagDto> getTags(String tags) {
        List<TagDto> list = new ArrayList<>();
        if (StringUtils.isBlank(tags)) {
            return list;
        }
        String[] split = tags.split(SPLIT);
        for (String s : split) {
            JyBizTaskExceptionTagEnum tagEnum = JyBizTaskExceptionTagEnum.getByCode(s);
            if (tagEnum == null) {
                continue;
            }
            //组长指派的三无任务标签特殊处理
            if(Objects.equals(JyBizTaskExceptionTagEnum.ASSIGN.getCode(),tagEnum.getCode())){
                TagDto dto = new TagDto();
                dto.setName(tagEnum.getName());
                dto.setCode(tagEnum.ordinal());
                dto.setStyle("error");
                list.add(dto);
                continue;
            }
            TagDto dto = new TagDto();
            dto.setName(tagEnum.getName());
            dto.setCode(tagEnum.ordinal());
            dto.setStyle("info");
            list.add(dto);
        }
        return list;
    }


    /**
     * 拼接唯一网格标识
     */
    public String getGridRid(PositionDetailRecord data) {
        return data.getSiteCode() + "-" + data.getFloor() + "-" + data.getGridCode();
    }

    public PositionDetailRecord getPosition(String positionCode) {
        if (StringUtils.isBlank(positionCode)) {
            return null;
        }
        com.jdl.basic.common.utils.Result <PositionDetailRecord> positionResult = positionQueryJsfManager.queryOneByPositionCode(positionCode);
        if (positionResult == null || positionResult.isFail() || positionResult.getData() == null) {
            return null;
        }
        // 处理jsf泛型丢失问题z
        return JSON.parseObject(JSON.toJSONString(positionResult.getData()), PositionDetailRecord.class);
    }

    private String getBizId(String barCode,Integer siteId) {
        if(BusinessUtil.isSanWuCode(barCode)){
            return JyBizTaskExceptionTypeEnum.SANWU.name() + "_" + barCode;
        }else {
            boolean bizIdSwith = dmsConfigManager.getPropertyConfig().isJyExceptionCreateBizIdSwitch();
            if(bizIdSwith){
                String bizid = JyBizTaskExceptionTypeEnum.SCRAPPED.name() + "_" + barCode;
                JyBizTaskExceptionEntity task = jyBizTaskExceptionDao.findByBizId(bizid);
                if(task != null){
                    return bizid;
                }
            }
            return  barCode+"_"+siteId;
        }
    }



    /**
     * 拼装三无录入对象
     */
    private ExpInfoSumaryInputDto getExpInfoDto(ExpTaskDetailCacheDto cacheDto) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ExpInfoSumaryInputDto dto = new ExpInfoSumaryInputDto();
        // 提报人
        dto.setReporterErp(cacheDto.getUserErp());
        // 三无码
        dto.setExpCode(cacheDto.getExpBarcode().toUpperCase());
        // 上报时间
        if (cacheDto.getExpCreateTime() != null) {
            dto.setHappenTimeNew(format.format(new Date(cacheDto.getExpCreateTime())));
        }
        // 重量
        dto.setProductWeight(cacheDto.getWeight());
        // 内件描述
        dto.setProductName(cacheDto.getInnerDesc());
        // 外包装描述
        dto.setProductState(cacheDto.getOuterDesc());
        // 发现环节
        dto.setDiscoveryLink(cacheDto.getSource());
        // 上级地
        dto.setPreNodeCodeNew(cacheDto.getFrom());
        // 长宽高
        dto.setLwh(cacheDto.getVolumeDetail());
        // SN码
        dto.setSnCode(cacheDto.getSn());
        // 商品编码
        dto.setProductCode(cacheDto.getGoodsNo());
        // 69码
        dto.setCode69(cacheDto.getYardSixNine());
        // 件数
        dto.setProductNum(cacheDto.getGoodsNum());
        // 车牌号
        dto.setVehicleNumber(cacheDto.getVehicleNumber());
        // 封签号 或批次号
        dto.setSealCodeOrBatchCode(cacheDto.getSealNumber());
        // 下级地
        dto.setFollowNodeCode(cacheDto.getTo());
        // 价值
        dto.setProductPrice(cacheDto.getPrice());
        // 储位
        dto.setStoreLocation(cacheDto.getStorage());
        // 同车包裹号
        dto.setSameCarPackageCode(cacheDto.getTogetherPackageCodes());
        // 图片
        dto.setImageUrls(cacheDto.getImageUrls());
        dto.setLength(cacheDto.getLength());
        dto.setWidth(cacheDto.getWidth());
        dto.setHeight(cacheDto.getHeight());
        return dto;
    }


    // bizTask转dto
    private ExpTaskDto getTaskDto(JyBizTaskExceptionEntity entity) {
        ExpTaskDto dto = new ExpTaskDto();
        dto.setBizId(entity.getBizId());
        dto.setSource(entity.getSource());
        dto.setType(entity.getType());
        dto.setBarCode(entity.getBarCode());
        // 停留时间：当前时间-分配时间
        dto.setStayTime(getStayTime(entity.getDistributionTime()));
        dto.setTimeOut(entity.getTimeOut());
        dto.setFloor(entity.getFloor());
        dto.setGridCode(entity.getGridCode());
        dto.setGridNo(entity.getGridNo());
        dto.setAreaName(entity.getAreaName());
        dto.setReporterName(entity.getCreateUserName());
        dto.setTags(getTags(entity.getTags()));
        dto.setStatus(entity.getStatus());
        dto.setProcessingStatus(entity.getProcessingStatus());
        if(Objects.nonNull(entity.getType())
                && JyBizTaskExceptionTypeEnum.SCRAPPED.getCode().equals(entity.getType())){
            ExpTaskByIdReq req = new ExpTaskByIdReq();
            req.setBizId(entity.getBizId());
            JdCResponse<ExpScrappedDetailDto> response = jyScrappedExceptionService.getTaskDetailOfscrapped(req);
            if(response.isSucceed() && response.getData()!= null && response.getData().getSaveType() != null){
                dto.setSaved(Objects.equals(response.getData().getSaveType(),JyExpSaveTypeEnum.TEMP_SAVE.getCode()));
            }
        } else if(Objects.equals(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode(), entity.getType())) {
            JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
            jyExceptionInterceptDetailQuery.setSiteId(entity.getSiteCode().intValue());
            jyExceptionInterceptDetailQuery.setBizId(entity.getBizId());
            final JyExceptionInterceptDetail jyExceptionInterceptDetailExist = jyExceptionInterceptDetailDao.selectOne(jyExceptionInterceptDetailQuery);
            if (jyExceptionInterceptDetailExist != null) {
                dto.setSaved(Objects.equals(jyExceptionInterceptDetailExist.getSaveType(), JyExpSaveTypeEnum.TEMP_SAVE.getCode()));
            }
        }else{
            String s = redisClient.get(TASK_CACHE_PRE + entity.getBizId());
            boolean saved = !StringUtils.isBlank(s) && Objects.equals(JSON.parseObject(s, ExpTaskDetailCacheDto.class).getSaveType(), "0");
            dto.setSaved(saved);
        }
        return dto;
    }

    private String getPositionCountKey(PositionDetailRecord position) {
        return RECEIVING_POSITION_COUNT_PRE + "|" + position.getSiteCode() + "|"
                + position.getFloor() + "|" + position.getAreaCode() + "|" + position.getGridCode() + "|" + position.getGridNo();
    }

    /**
     * 调用终端妥投接口
     */
    @Override
    public void delivered(String bizId){
        JyBizTaskExceptionEntity exTaskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if(exTaskEntity == null){
            logger.warn("根据业务主键:{}未查询到异常任务数据!", bizId);
            return;
        }
        DeliveredReqDTO reqDTO = new DeliveredReqDTO();
        reqDTO.setWaybillCode(WaybillUtil.getWaybillCode(exTaskEntity.getBarCode()));
        reqDTO.setUserCode(exTaskEntity.getHandlerErp());
        reqDTO.setSiteId(Objects.nonNull(exTaskEntity.getSiteCode())?exTaskEntity.getSiteCode().intValue():0);
        reqDTO.setSiteName(exTaskEntity.getSiteName());
        reqDTO.setDeviceId(DEVICE_ID);
        reqDTO.setSystemSource(SYSTEM_SOURCE);
        reqDTO.setOperateTime(new Date());
        reqDTO.setOperatorId(OPERATE_ID);
        reqDTO.setOperatorName(OPERATE_NAME);
        if(exTaskEntity.getSiteCode() != null){
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(exTaskEntity.getSiteCode().intValue());
            if(baseSite != null){
                reqDTO.setSiteType(baseSite.getSiteType());
            }
        }
        deliveryWSManager.delivered(reqDTO);
    }

    /**
     * 获取bizId
     *
     * @param businessInterceptReport 拦截记录
     * @return bizId结果包装
     * @author fanggang7
     * @time 2024-01-21 20:21:11 周日
     */
    @Override
    public String getBizId(BusinessInterceptReport businessInterceptReport) {
        return String.format(JyExceptionBusinessInterceptTaskConstants.BIZ_ID_TEMPLATE, businessInterceptReport.getSiteCode(), businessInterceptReport.getPackageCode(), businessInterceptReport.getInterceptType(), businessInterceptReport.getOperateWorkStationGridKey(), businessInterceptReport.getOperateTime());
    }

    /**
     * 消费拦截报表明细数据
     *
     * @param businessInterceptReport
     * @return 处理结果
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    @Override
    public Result<Boolean> handleDmsBusinessInterceptReportUpload(BusinessInterceptReport businessInterceptReport) {
        if(logger.isInfoEnabled()){
            logger.info("JyExceptionServiceImpl.handleDmsBusinessInterceptReportUpload param: {}", JsonHelper.toJson(businessInterceptReport));
        }
        Result<Boolean> result = Result.success();

        // 按场地-包裹加锁
        String concurrencyCacheKey = String.format(JyCacheKeyConstants.JY_EXCEPTION_TASK_INTERCEPT_SITE_PACKAGE_CONCURRENCY_KEY, businessInterceptReport.getPackageCode(), businessInterceptReport.getSiteCode());
        try {
            if (!dmsConfigManager.getPropertyConfig().isInterceptExceptionSiteIdEnable(businessInterceptReport.getSiteCode())) {
                return result;
            }
            final Boolean exists = redisClientOfJy.exists(concurrencyCacheKey);
            if(Objects.equals(exists, Boolean.TRUE)){
                return result.toFail("上一个相同场地包裹数据还未处理完成，请稍后再试");
            }
            redisClientOfJy.setEx(concurrencyCacheKey, "1", 1, TimeUnit.MINUTES);

            // 1. 当前场地未完结的已有相同包裹的任务
            final JyBizTaskExceptionQuery jyBizTaskExceptionQuery = new JyBizTaskExceptionQuery();
            jyBizTaskExceptionQuery.setType(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode());
            jyBizTaskExceptionQuery.setBarCode(businessInterceptReport.getPackageCode());
            jyBizTaskExceptionQuery.setSiteCode(businessInterceptReport.getSiteCode().longValue());
            // jyBizTaskExceptionQuery.setExcludeStatusList(new ArrayList<>(Arrays.asList(JyExpStatusEnum.COMPLETE.getCode())));
            jyBizTaskExceptionQuery.setYn(Constants.YN_YES);
            // final List<JyBizTaskExceptionEntity> currentSiteSamePackageTaskList = jyBizTaskExceptionDao.selectListByCondition(jyBizTaskExceptionQuery);
            final JyBizTaskExceptionEntity currentSiteSamePackageTaskExist = jyBizTaskExceptionDao.selectOneByCondition(jyBizTaskExceptionQuery);

            String bizId = this.getBizId(businessInterceptReport);

            boolean needInsertNewTask = false;

            // 如果已有任务
            if (currentSiteSamePackageTaskExist != null) {
                logger.info("currentSiteSamePackageTaskExist {}", JsonHelper.toJson(currentSiteSamePackageTaskExist));
                final JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
                jyExceptionInterceptDetailQuery.setSiteId(businessInterceptReport.getSiteCode());
                jyExceptionInterceptDetailQuery.setBizId(currentSiteSamePackageTaskExist.getBizId());
                jyExceptionInterceptDetailQuery.setPackageCode(businessInterceptReport.getPackageCode());
                jyExceptionInterceptDetailQuery.setYn(Constants.YN_YES);
                final JyExceptionInterceptDetail jyExceptionInterceptDetailExist = jyExceptionInterceptDetailDao.selectOne(jyExceptionInterceptDetailQuery);
                // 1.1 拦截类型不同 丢弃
                if (!Objects.equals(JyExpStatusEnum.COMPLETE.getCode(), currentSiteSamePackageTaskExist.getStatus())) {
                    if (!Objects.equals(jyExceptionInterceptDetailExist.getInterceptType(), businessInterceptReport.getInterceptType())) {
                        logger.warn("JyExceptionServiceImpl.handleDmsBusinessInterceptReportUpload 该包裹的上一个拦截类型的任务还未处理完成，忽略此数据 {}", JsonHelper.toJson(businessInterceptReport));
                        return result.toSuccess("该包裹的上一个拦截类型的任务还未处理完成，忽略此数据");
                    } else {
                        // 1.2 拦截类型相同
                        // 1.2.1 网格工序相同 更新任务+明细数据
                        if(Objects.equals(jyExceptionInterceptDetailExist.getOperateWorkStationGridKey(), businessInterceptReport.getOperateWorkStationGridKey())){
                            // 如果任务是完结状态+需要换单打印处理的拦截任务，则不重置任务，直接跳过
                            final List<Integer> disposeNodeListByInterceptType = businessInterceptConfig.getDisposeNodeListByInterceptType(jyExceptionInterceptDetailExist.getInterceptType());
                            if (CollectionUtils.isNotEmpty(disposeNodeListByInterceptType) && disposeNodeListByInterceptType.contains(businessInterceptConfigHelper.getInterceptDisposeNodeExchangeWaybill())) {
                                logger.info("JyExceptionServiceImpl.handleDmsBusinessInterceptReportUpload 换单打印后扫描老单号，忽略此数据 {}", JsonHelper.toJson(businessInterceptReport));
                                return result.toSuccess("换单打印后扫描老单号，忽略此数据");
                            }
                            resetOriginalInterceptTask(currentSiteSamePackageTaskExist, businessInterceptReport);
                        }
                        // 1.2.2 网格不同 删除原任务+明细数据，新增任务数据
                        else {
                            // 删除原数据
                            logicDelCurrentSite(currentSiteSamePackageTaskExist);
                            // 需新增数据
                            needInsertNewTask = true;
                        }
                    }
                    // 将其他场地数据更新为完成
                    finishOtherSiteTaskAnd2Fail(businessInterceptReport.getPackageCode(), businessInterceptReport.getSiteCode());
                } else {
                    // 1.2 拦截类型相同
                    // 1.2.1 网格工序相同 更新任务+明细数据
                    if(Objects.equals(jyExceptionInterceptDetailExist.getOperateWorkStationGridKey(), businessInterceptReport.getOperateWorkStationGridKey())){
                        // 如果任务是完结状态+需要换单打印处理的拦截任务，则不重置任务，直接跳过
                        final List<Integer> disposeNodeListByInterceptType = businessInterceptConfig.getDisposeNodeListByInterceptType(jyExceptionInterceptDetailExist.getInterceptType());
                        if (CollectionUtils.isNotEmpty(disposeNodeListByInterceptType) && disposeNodeListByInterceptType.contains(businessInterceptConfigHelper.getInterceptDisposeNodeExchangeWaybill())) {
                            logger.info("JyExceptionServiceImpl.handleDmsBusinessInterceptReportUpload 换单打印后扫描老单号，忽略此数据 {}", JsonHelper.toJson(businessInterceptReport));
                            return result.toSuccess("换单打印后扫描老单号，忽略此数据");
                        }
                    }
                }
            } else {
                needInsertNewTask = true;
            }

            // 如果需要新增明细
            if(needInsertNewTask){

                // 3.5 同一个场地同一个包裹，不同实操网格触发异常任务，删除老任务，生成新的任务
                // 3.1 插入任务
                JyBizTaskExceptionEntity taskEntity = new JyBizTaskExceptionEntity();
                taskEntity.setBizId(bizId);

                // 3.2 插入明细
                JyExceptionInterceptDetail jyExceptionInterceptDetail = new JyExceptionInterceptDetail();
                jyExceptionInterceptDetail.setBizId(bizId);

                if(StringUtils.isNotBlank(businessInterceptReport.getOperateWorkStationGridKey())){
                    final WorkStationGrid workStationGrid = this.getWorkStationGrid(businessInterceptReport.getOperateWorkStationGridKey());
                    if (workStationGrid == null) {
                        return result.toFail("获取拦截数据操作工序信息失败");
                    }
                    this.assembleJyBizExceptionTask(taskEntity, businessInterceptReport, workStationGrid);
                    this.assembleJyExceptionInterceptDetail(jyExceptionInterceptDetail, businessInterceptReport, workStationGrid);
                } else if(StringUtils.isNotBlank(businessInterceptReport.getOperateWorkGridKey())){
                    final WorkGrid workGrid = this.getWorkGrid(businessInterceptReport.getOperateWorkGridKey());
                    if (workGrid == null) {
                        return result.toFail("获取拦截数据操作网格信息失败");
                    }
                    this.assembleJyBizExceptionTask(taskEntity, businessInterceptReport, workGrid);
                    this.assembleJyExceptionInterceptDetail(jyExceptionInterceptDetail, businessInterceptReport, workGrid);
                }
                jyBizTaskExceptionDao.insertSelective(taskEntity);
                jyExceptionInterceptDetailDao.insertSelective(jyExceptionInterceptDetail);

                // 3.3 记录日志
                recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_SUBMIT, taskEntity);

                // 3.4 插入拦截包裹和场地关系 插入kv时防重
                /*JyExceptionInterceptDetailKv jyExceptionInterceptDetailKv = new JyExceptionInterceptDetailKv();
                this.assembleJyExceptionInterceptDetailKv(jyExceptionInterceptDetailKv, jyExceptionInterceptDetail);
                jyExceptionBusinessInterceptDetailKvService.addOneNoRepeat(jyExceptionInterceptDetailKv);*/

                // 3.6 更新其他场地数据
                finishOtherSiteTaskAnd2Fail(businessInterceptReport.getPackageCode(), businessInterceptReport.getSiteCode());
                // 3.7 发送 mq 通知调度系统分配
                sendToSchedule(businessInterceptReport, bizId);
            }

        } catch (Exception e) {
            logger.error("JyExceptionServiceImpl.handleDmsBusinessInterceptReportUpload param: {}", JsonHelper.toJson(businessInterceptReport), e);
            result.toFail("系统异常");
        } finally {
            redisClientOfJy.del(concurrencyCacheKey);
        }

        return result;
    }

    private void finishOtherSiteTaskAnd2Fail(String packageCode, Integer siteCode){
        // 查找其他场地未完结的任务
        final JyBizTaskExceptionQuery jyBizTaskExceptionQuery = new JyBizTaskExceptionQuery();
        jyBizTaskExceptionQuery.setBarCode(packageCode);
        jyBizTaskExceptionQuery.setType(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode());
        jyBizTaskExceptionQuery.setExcludeSiteCode(siteCode.longValue());
        jyBizTaskExceptionQuery.setExcludeStatusList(new ArrayList<>(Arrays.asList(JyExpStatusEnum.COMPLETE.getCode())));
        final List<JyBizTaskExceptionEntity> otherSiteTaskList = jyBizTaskExceptionDao.selectListByCondition(jyBizTaskExceptionQuery);
        if (CollectionUtils.isNotEmpty(otherSiteTaskList)) {
            final List<String> otherSiteTaskBizIdList = otherSiteTaskList.stream().map(JyBizTaskExceptionEntity::getBizId).collect(Collectors.toList());
            // 更新为完结+处理失败
            jyBizTaskExceptionDao.updateExceptionTaskStatusByBizIds(JyExpStatusEnum.COMPLETE.getCode(), JyBizTaskExceptionProcessStatusEnum.PROCESS_FAIL.getCode(), otherSiteTaskBizIdList);
        }
    }

    /**
     * 重置原始任务和明细
     * @param taskExist
     * @param businessInterceptReport
     */
    private void resetOriginalInterceptTask(JyBizTaskExceptionEntity taskExist, BusinessInterceptReport businessInterceptReport) {
        // 3.1 更新任务
        JyBizTaskExceptionEntity taskEntityUpdate = new JyBizTaskExceptionEntity();
        taskEntityUpdate.setBizId(taskExist.getBizId());
        taskEntityUpdate.setStatus(JyExpStatusEnum.TO_PICK.getCode());
        taskEntityUpdate.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PENDING_ENTRY.getCode());
        taskEntityUpdate.setCreateTime(new Date(businessInterceptReport.getOperateTime()));
        taskEntityUpdate.setCreateUserErp(businessInterceptReport.getOperateUser());
        taskEntityUpdate.setCreateUserName(businessInterceptReport.getOperateUserName());
        jyBizTaskExceptionDao.updateByBizId(taskEntityUpdate);

        // 3.2 更新明细
        JyExceptionInterceptDetail jyExceptionInterceptDetailUpdate = new JyExceptionInterceptDetail();
        jyExceptionInterceptDetailUpdate.setBizId(taskExist.getBizId());
        jyExceptionInterceptDetailUpdate.setCreateTime(taskEntityUpdate.getCreateTime());
        jyExceptionInterceptDetailUpdate.setCreateUserCode(taskEntityUpdate.getCreateUserErp());
        jyExceptionInterceptDetailUpdate.setCreateUserName(taskEntityUpdate.getCreateUserName());
        jyExceptionInterceptDetailUpdate.setSaveType(-1);
        jyExceptionInterceptDetailDao.updateByBizId(jyExceptionInterceptDetailUpdate);
    }

    private void logicDelCurrentSite(JyBizTaskExceptionEntity taskExist){
        // 删除任务
        JyBizTaskExceptionEntity taskEntityUpdate = new JyBizTaskExceptionEntity();
        taskEntityUpdate.setBizId(taskExist.getBizId());
        taskEntityUpdate.setYn(Constants.YN_NO);
        taskEntityUpdate.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PROCESS_FAIL.getCode());
        jyBizTaskExceptionDao.updateByBizId(taskEntityUpdate);

        // 删除明细
        JyExceptionInterceptDetail jyExceptionInterceptDetailUpdate = new JyExceptionInterceptDetail();
        jyExceptionInterceptDetailUpdate.setSiteId(taskExist.getSiteCode().intValue());
        jyExceptionInterceptDetailUpdate.setBizId(taskExist.getBizId());
        jyExceptionInterceptDetailUpdate.setYn(Constants.YN_NO);
        jyExceptionInterceptDetailDao.updateByBizId(jyExceptionInterceptDetailUpdate);
    }

    /**
     * 查询网格工序信息
     */
    private WorkStationGrid getWorkStationGrid(String workStationGridKey) {
        final WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
        workStationGridQuery.setBusinessKey(workStationGridKey);
        final com.jdl.basic.common.utils.Result<WorkStationGrid> workStationGridResult = workStationGridManager.queryByGridKey(workStationGridQuery);
        if (!workStationGridResult.isSuccess()) {
            return null;
        }
        return workStationGridResult.getData();
    }

    /**
     * 查询网格信息
     */
    private WorkGrid getWorkGrid(String workGridKey) {
        final com.jdl.basic.common.utils.Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKeyWithCache(workGridKey);
        if (!workGridResult.isSuccess()) {
            return null;
        }
        return workGridResult.getData();
    }

    /**
     * 根据网格工序设置实体信息
     */
    private void assembleJyBizExceptionTask(JyBizTaskExceptionEntity taskEntity, BusinessInterceptReport businessInterceptReport, WorkStationGrid workStationGrid){
        assembleJyBizExceptionTaskBase(taskEntity);
        assembleJyBizExceptionTaskFromBusinessInterceptReport(taskEntity, businessInterceptReport);
        assembleJyBizExceptionTaskFromWorkStationGrid(taskEntity, workStationGrid);
    }

    private void assembleJyBizExceptionTaskBase(JyBizTaskExceptionEntity taskEntity){
        taskEntity.setType(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode());
        taskEntity.setSource(JyExpSourceEnum.OPERATE_INTERCEPT.getCode());
        taskEntity.setTags(JyBizTaskExceptionTagEnum.INTERCEPT.getCode());
        taskEntity.setStatus(JyExpStatusEnum.TO_PICK.getCode());
        taskEntity.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PENDING_ENTRY.getCode());
    }

    private void assembleJyBizExceptionTaskFromBusinessInterceptReport(JyBizTaskExceptionEntity taskEntity, BusinessInterceptReport businessInterceptReport){
        taskEntity.setBarCode(businessInterceptReport.getPackageCode());
        taskEntity.setSiteCode(businessInterceptReport.getSiteCode().longValue());
        taskEntity.setSiteName(businessInterceptReport.getSiteName());
        taskEntity.setCreateUserErp(businessInterceptReport.getOperateUser());
        taskEntity.setCreateUserName(businessInterceptReport.getOperateUserName());
        taskEntity.setCreateTime(new Date(businessInterceptReport.getOperateTime()));
    }

    private void assembleJyBizExceptionTaskFromWorkStationGrid(JyBizTaskExceptionEntity taskEntity, WorkStationGrid workStationGrid){
        taskEntity.setFloor(workStationGrid.getFloor());
        taskEntity.setAreaCode(workStationGrid.getAreaCode());
        taskEntity.setAreaName(workStationGrid.getAreaName());
        taskEntity.setGridCode(workStationGrid.getGridCode());
        taskEntity.setGridNo(workStationGrid.getGridNo());
    }

    /**
     * 根据网格设置实体信息
     */
    private void assembleJyBizExceptionTask(JyBizTaskExceptionEntity taskEntity, BusinessInterceptReport businessInterceptReport, WorkGrid workGrid){
        assembleJyBizExceptionTaskBase(taskEntity);
        assembleJyBizExceptionTaskFromBusinessInterceptReport(taskEntity, businessInterceptReport);
        assembleJyBizExceptionTaskFromWorkGrid(taskEntity, workGrid);
    }

    private void assembleJyBizExceptionTaskFromWorkGrid(JyBizTaskExceptionEntity taskEntity, WorkGrid workGrid){
        taskEntity.setFloor(workGrid.getFloor());
        taskEntity.setAreaCode(workGrid.getAreaCode());
        taskEntity.setAreaName(workGrid.getAreaName());
        taskEntity.setGridCode(workGrid.getGridCode());
        taskEntity.setGridNo(workGrid.getGridNo());
    }

    private void assembleJyExceptionInterceptDetail(JyExceptionInterceptDetail jyExceptionInterceptDetail, BusinessInterceptReport businessInterceptReport, WorkStationGrid workStationGrid){
        assembleJyExceptionInterceptDetailFromBusinessInterceptReport(jyExceptionInterceptDetail, businessInterceptReport);
        assembleJyExceptionInterceptDetailFromWorkStationGrid(jyExceptionInterceptDetail, workStationGrid);
    }
    private void assembleJyExceptionInterceptDetailFromBusinessInterceptReport(JyExceptionInterceptDetail jyExceptionInterceptDetail, BusinessInterceptReport businessInterceptReport){
        jyExceptionInterceptDetail.setInterceptBizId(businessInterceptReport.getBizId());
        jyExceptionInterceptDetail.setBarCode(businessInterceptReport.getBarCode());
        jyExceptionInterceptDetail.setBizSource(businessInterceptReport.getBizSource());
        jyExceptionInterceptDetail.setSiteId(businessInterceptReport.getSiteCode());
        jyExceptionInterceptDetail.setSiteName(businessInterceptReport.getSiteName());
        jyExceptionInterceptDetail.setProvinceAgencyCode(businessInterceptReport.getProvinceAgencyCode());
        jyExceptionInterceptDetail.setProvinceAgencyName(businessInterceptReport.getProvinceAgencyName());
        jyExceptionInterceptDetail.setAreaHubCode(businessInterceptReport.getAreaHubCode());
        jyExceptionInterceptDetail.setAreaHubName(businessInterceptReport.getAreaHubName());
        jyExceptionInterceptDetail.setPackageCode(businessInterceptReport.getPackageCode());
        jyExceptionInterceptDetail.setPackageTotal(businessInterceptReport.getPackageTotal());
        jyExceptionInterceptDetail.setWaybillCode(businessInterceptReport.getWaybillCode());
        jyExceptionInterceptDetail.setBoxCode(businessInterceptReport.getBoxCode());
        jyExceptionInterceptDetail.setOperateNode(businessInterceptReport.getOperateNode());
        jyExceptionInterceptDetail.setInterceptEffectTime(businessInterceptReport.getInterceptEffectTime());
        jyExceptionInterceptDetail.setInterceptType(businessInterceptReport.getInterceptType());
        jyExceptionInterceptDetail.setDeviceCode(businessInterceptReport.getDeviceCode());
        jyExceptionInterceptDetail.setDeviceType(businessInterceptReport.getDeviceType());
        jyExceptionInterceptDetail.setDeviceSubType(businessInterceptReport.getDeviceSubType());
        jyExceptionInterceptDetail.setOperatePositionCode(businessInterceptReport.getOperatePositionCode());
        jyExceptionInterceptDetail.setOperateWorkStationGridKey(businessInterceptReport.getOperateWorkStationGridKey());
        jyExceptionInterceptDetail.setOperateWorkGridKey(businessInterceptReport.getOperateWorkGridKey());
        jyExceptionInterceptDetail.setInterceptCode(businessInterceptReport.getInterceptCode());
        jyExceptionInterceptDetail.setInterceptMessage(businessInterceptReport.getInterceptMessage());
        if (businessInterceptReport.getCreateUserId() != null) {
            jyExceptionInterceptDetail.setCreateUserId(businessInterceptReport.getOperateUserId());
        }
        jyExceptionInterceptDetail.setCreateUserCode(businessInterceptReport.getOperateUser());
        jyExceptionInterceptDetail.setCreateUserName(businessInterceptReport.getOperateUserName());
        if (businessInterceptReport.getCreateUserId() != null) {
            jyExceptionInterceptDetail.setUpdateUserId(businessInterceptReport.getUpdateUserId());
        }
        jyExceptionInterceptDetail.setUpdateUserCode(businessInterceptReport.getUpdateUser());
        jyExceptionInterceptDetail.setUpdateUserName(businessInterceptReport.getUpdateUserName());
        jyExceptionInterceptDetail.setCreateTime(new Date(businessInterceptReport.getCreateTime()));
    }


    private void assembleJyExceptionInterceptDetailFromWorkStationGrid(JyExceptionInterceptDetail jyExceptionInterceptDetail, WorkStationGrid workStationGrid){
        jyExceptionInterceptDetail.setOperateAreaCode(workStationGrid.getAreaCode());
        jyExceptionInterceptDetail.setOperateAreaName(workStationGrid.getAreaName());
        jyExceptionInterceptDetail.setOperateAreaName(workStationGrid.getAreaName());
        jyExceptionInterceptDetail.setOperateAreaName(workStationGrid.getAreaName());
        jyExceptionInterceptDetail.setOperateWorkCode(workStationGrid.getWorkCode());
        jyExceptionInterceptDetail.setOperateWorkName(workStationGrid.getWorkName());
    }

    private void assembleJyExceptionInterceptDetail(JyExceptionInterceptDetail jyExceptionInterceptDetail, BusinessInterceptReport businessInterceptReport, WorkGrid workGrid){
        assembleJyExceptionInterceptDetailFromBusinessInterceptReport(jyExceptionInterceptDetail, businessInterceptReport);
        assembleJyExceptionInterceptDetailFromWorkGrid(jyExceptionInterceptDetail, workGrid);
    }

    private void assembleJyExceptionInterceptDetailFromWorkGrid(JyExceptionInterceptDetail jyExceptionInterceptDetail, WorkGrid workGrid){
        jyExceptionInterceptDetail.setOperateAreaCode(workGrid.getAreaCode());
        jyExceptionInterceptDetail.setOperateAreaName(workGrid.getAreaName());
        jyExceptionInterceptDetail.setOperateAreaName(workGrid.getAreaName());
        jyExceptionInterceptDetail.setOperateAreaName(workGrid.getAreaName());
    }

    private void assembleJyExceptionInterceptDetailKv(JyExceptionInterceptDetailKv jyExceptionInterceptDetailKv, JyExceptionInterceptDetail jyExceptionInterceptDetail){
        jyExceptionInterceptDetailKv.setKeyword(this.getInterceptDetailKvKeyword(jyExceptionInterceptDetail.getPackageCode(), jyExceptionInterceptDetail.getInterceptType()));
        jyExceptionInterceptDetailKv.setValue(jyExceptionInterceptDetail.getSiteId().toString());
    }

    private String getInterceptDetailKvKeyword(String packageCode, Integer interceptType){
        return String.format(JyExceptionBusinessInterceptTaskConstants.PACKAGE_CODE_ASSOCIATE_SITE_KEY, packageCode, interceptType);
    }

    private void sendToSchedule(BusinessInterceptReport businessInterceptReport, String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setTaskType(JyScheduleTaskTypeEnum.EXCEPTION.getCode());
        req.setBizId(bizId);
        req.setOpeUser(businessInterceptReport.getCreateUser());
        req.setOpeUserName(businessInterceptReport.getCreateUserName());
        req.setOpeTime(new Date(businessInterceptReport.getOperateTime()));
        req.setTaskStatus(JyScheduleTaskStatusEnum.INIT.getCode());
        logger.info("scheduleTaskAddWorkerProducer send {}", JsonHelper.toJson(req));
        scheduleTaskAddWorkerProducer.sendOnFailPersistent(bizId,JsonHelper.toJson(req));
    }

    /**
     * 消费拦截处理消息
     *
     * @param businessInterceptDisposeRecord 拦截处理数据
     * @return 处理结果
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    @Override
    public Result<Boolean> handleDmsBusinessInterceptDispose(BusinessInterceptDisposeRecord businessInterceptDisposeRecord) {
        if(logger.isInfoEnabled()){
            logger.info("JyExceptionServiceImpl.handleDmsBusinessInterceptDispose param: {}", JsonHelper.toJson(businessInterceptDisposeRecord));
        }
        Result<Boolean> result = Result.success();
        try {
            if (!dmsConfigManager.getPropertyConfig().isInterceptExceptionSiteIdEnable(businessInterceptDisposeRecord.getSiteCode())) {
                return result;
            }
            final Date currentDate = new Date();
            // 1. 处理同场地
            // 1.1 当前场地未完结的已有相同包裹的任务
            final JyBizTaskExceptionQuery jyBizTaskExceptionQuery = new JyBizTaskExceptionQuery();
            jyBizTaskExceptionQuery.setType(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode());
            jyBizTaskExceptionQuery.setBarCode(businessInterceptDisposeRecord.getPackageCode());
            jyBizTaskExceptionQuery.setSiteCode(businessInterceptDisposeRecord.getSiteCode().longValue());
            // 非0重量拦截类型的，待领取状态，不置为完结状态，只有处理中的才置为完结状态
            // 0重量拦截的，待处理状态，需要置为完结状态
            jyBizTaskExceptionQuery.setExcludeStatusList(new ArrayList<>(Arrays.asList(JyExpStatusEnum.COMPLETE.getCode())));
            final JyBizTaskExceptionEntity currentSiteSamePackageTaskExist = jyBizTaskExceptionDao.selectOneByCondition(jyBizTaskExceptionQuery);

            if (currentSiteSamePackageTaskExist != null) {
                final List<Integer> needHandleInterceptTypeList = businessInterceptConfig.getNeedHandleInterceptTypeList(businessInterceptDisposeRecord.getDisposeNode());
                JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
                jyExceptionInterceptDetailQuery.setSiteId(currentSiteSamePackageTaskExist.getSiteCode().intValue());
                jyExceptionInterceptDetailQuery.setBizId(currentSiteSamePackageTaskExist.getBizId());
                final JyExceptionInterceptDetail exceptionInterceptDetailExist = jyExceptionInterceptDetailDao.selectOne(jyExceptionInterceptDetailQuery);
                if (needHandleInterceptTypeList.contains(exceptionInterceptDetailExist.getInterceptType())) {
                    //如果是撤销拦截的，不论何种状态都置为完结状态、撤销拦截处理状态
                    if(businessInterceptConfig.getRecallDisposeNodeList().contains(businessInterceptDisposeRecord.getDisposeNode())){
                        this.finishInterceptTaskSuccess(currentSiteSamePackageTaskExist, businessInterceptDisposeRecord, currentDate, JyBizTaskExceptionProcessStatusEnum.INTERCEPT_RECALL.getCode());
                    } else {
                        // 0重量拦截的，待处理状态，需要置为完结状态
                        if (Objects.equals(exceptionInterceptDetailExist.getInterceptType(), BusinessInterceptConfig.WITHOUT_WEIGHT_INTERCEPT_TYPE)) {
                            List<Integer> zeroWeightInterceptTypeNeedChangeTaskStatusList = new ArrayList<>(Arrays.asList(JyExpStatusEnum.TO_PROCESS.getCode(), JyExpStatusEnum.PROCESSING.getCode()));
                            if (zeroWeightInterceptTypeNeedChangeTaskStatusList.contains(currentSiteSamePackageTaskExist.getStatus())) {
                                // 完结已有任务
                                this.finishInterceptTaskSuccess(currentSiteSamePackageTaskExist, businessInterceptDisposeRecord, currentDate);
                            }
                        }
                        // 非0重量拦截类型的，待领取状态，不置为完结状态，只有处理中的才置为完结状态
                        else {
                            List<Integer> excludeZeroWeightInterceptTypeNeedChangeTaskStatusList = new ArrayList<>(Arrays.asList(JyExpStatusEnum.PROCESSING.getCode()));
                            if (excludeZeroWeightInterceptTypeNeedChangeTaskStatusList.contains(currentSiteSamePackageTaskExist.getStatus())) {
                                // 完结已有任务
                                this.finishInterceptTaskSuccess(currentSiteSamePackageTaskExist, businessInterceptDisposeRecord, currentDate);
                            }
                        }
                    }
                }
            } else {
                // 补充补打新单号处理逻辑
                /*if (Objects.equals(businessInterceptConfigHelper.getInterceptDisposeNodeReprint(), businessInterceptDisposeRecord.getDisposeNode())) {
                    JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
                    jyExceptionInterceptDetailQuery.setSiteId(businessInterceptDisposeRecord.getSiteCode());
                    jyExceptionInterceptDetailQuery.setWaybillCodeNew(businessInterceptDisposeRecord.getWaybillCode());
                    final List<JyExceptionInterceptDetail> jyExceptionInterceptDetailExistList = jyExceptionInterceptDetailDao.queryList(jyExceptionInterceptDetailQuery);
                    if (CollectionUtils.isNotEmpty(jyExceptionInterceptDetailExistList)) {
                        for (JyExceptionInterceptDetail jyExceptionInterceptDetail : jyExceptionInterceptDetailExistList) {
                            // 匹配新单的包裹序号和老单的包裹序号
                            if (!Objects.equals(WaybillUtil.getPackIndexByPackCode(businessInterceptDisposeRecord.getPackageCode()), WaybillUtil.getPackIndexByPackCode(jyExceptionInterceptDetail.getPackageCode()))) {
                                continue;
                            }
                            final JyBizTaskExceptionQuery jyBizTaskQuery = new JyBizTaskExceptionQuery();
                            jyBizTaskQuery.setBizId(jyExceptionInterceptDetail.getBizId());
                            jyBizTaskQuery.setBarCode(jyExceptionInterceptDetail.getPackageCode());
                            jyBizTaskQuery.setSiteCode(jyExceptionInterceptDetail.getSiteId().longValue());
                            jyBizTaskExceptionQuery.setExcludeStatusList(new ArrayList<>(Arrays.asList(JyExpStatusEnum.COMPLETE.getCode())));
                            final JyBizTaskExceptionEntity bizTaskExceptionExist = jyBizTaskExceptionDao.selectOneByCondition(jyBizTaskQuery);
                            if (bizTaskExceptionExist != null) {
                                this.finishInterceptTaskSuccess(bizTaskExceptionExist, businessInterceptDisposeRecord, currentDate);
                            }
                        }
                    }
                }*/
            }

            // 2. 关闭其他场地任务数据
            finishOtherSiteTaskAnd2Fail(businessInterceptDisposeRecord.getPackageCode(), businessInterceptDisposeRecord.getSiteCode());
        } catch (Exception e) {
            logger.error("JyExceptionServiceImpl.handleDmsBusinessInterceptDispose param: {}", JsonHelper.toJson(businessInterceptDisposeRecord), e);
            result.toFail("系统异常");
        }

        return result;
    }

    private void finishInterceptTaskSuccess(JyBizTaskExceptionEntity taskExist, BusinessInterceptDisposeRecord businessInterceptDisposeRecord, Date currentDate){
        this.finishInterceptTaskSuccess(taskExist, businessInterceptDisposeRecord, currentDate, null);
    }

    private void finishInterceptTaskSuccess(JyBizTaskExceptionEntity taskExist, BusinessInterceptDisposeRecord businessInterceptDisposeRecord, Date currentDate, Integer targetProcessStatus){
        if (targetProcessStatus == null) {
            targetProcessStatus = JyBizTaskExceptionProcessStatusEnum.DONE.getCode();
        }
        JyBizTaskExceptionEntity bizTaskExceptionUpdate = new JyBizTaskExceptionEntity();
        bizTaskExceptionUpdate.setBizId(taskExist.getBizId());
        bizTaskExceptionUpdate.setStatus(JyExpStatusEnum.COMPLETE.getCode());
        bizTaskExceptionUpdate.setProcessingStatus(targetProcessStatus);
        bizTaskExceptionUpdate.setUpdateUserErp(businessInterceptDisposeRecord.getDisposeUser());
        bizTaskExceptionUpdate.setUpdateUserName(businessInterceptDisposeRecord.getDisposeUserName());
        bizTaskExceptionUpdate.setUpdateTime(new Date(businessInterceptDisposeRecord.getDisposeNode()));
        jyBizTaskExceptionDao.updateByBizId(bizTaskExceptionUpdate);
        // 3.2 更新明细
        JyExceptionInterceptDetail jyExceptionInterceptDetail = new JyExceptionInterceptDetail();
        jyExceptionInterceptDetail.setBizId(taskExist.getBizId());
        jyExceptionInterceptDetail.setSiteId(businessInterceptDisposeRecord.getSiteCode());
        jyExceptionInterceptDetail.setDisposeTime(businessInterceptDisposeRecord.getDisposeTime());
        jyExceptionInterceptDetail.setDisposeNode(businessInterceptDisposeRecord.getDisposeNode());
        jyExceptionInterceptDetail.setDisposeUserCode(businessInterceptDisposeRecord.getDisposeUser());
        jyExceptionInterceptDetail.setDisposeUserId(businessInterceptDisposeRecord.getDisposeUserId() != null ? businessInterceptDisposeRecord.getDisposeUserId().longValue() : null);
        jyExceptionInterceptDetail.setDisposeUserName(businessInterceptDisposeRecord.getDisposeUserName());
        jyExceptionInterceptDetail.setUpdateUserId(jyExceptionInterceptDetail.getDisposeUserId());
        jyExceptionInterceptDetail.setUpdateUserCode(jyExceptionInterceptDetail.getDisposeUserCode());
        jyExceptionInterceptDetail.setUpdateUserName(jyExceptionInterceptDetail.getDisposeUserName());
        jyExceptionInterceptDetail.setUpdateTime(currentDate);
        jyExceptionInterceptDetailDao.updateByBizId(jyExceptionInterceptDetail);

        // 3.3 插入流水记录
        recordLog(JyBizTaskExceptionCycleTypeEnum.CLOSE, bizTaskExceptionUpdate);
    }

    /**
     * 获取拦截任务明细
     *
     * @param req
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    @Override
    public Result<JyExceptionInterceptDetailDto> getTaskDetailOfIntercept(ExpTaskCommonReq req) {
        if(logger.isInfoEnabled()){
            logger.info("JyExceptionServiceImpl.getTaskDetailOfIntercept param: {}", JsonHelper.toJson(req));
        }
        Result<JyExceptionInterceptDetailDto> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4GetTaskDetailOfIntercept(req);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 业务条件校验
            final JyBizTaskExceptionEntity jyBizTaskExceptionExist = jyBizTaskExceptionDao.findByBizId(req.getBizId());
            if (jyBizTaskExceptionExist == null) {
                return result.toFail("未查找到数据");
            }

            // 3. 执行逻辑
            final JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
            jyExceptionInterceptDetailQuery.setSiteId(req.getCurrentOperate().getSiteCode());
            jyExceptionInterceptDetailQuery.setBizId(req.getBizId());
            final Result<JyExceptionInterceptDetail> detailResult = jyExceptionBusinessInterceptDetailService.selectOne(jyExceptionInterceptDetailQuery);
            if (!detailResult.isSuccess()) {
                logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept queryDetail fail {}", JsonHelper.toJson(req));
                return result.toFail("查询拦截异常任务明细失败");
            }

            if (detailResult.getData() == null) {
                logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept queryDetail null {}", JsonHelper.toJson(req));
                return result.toFail("查询拦截异常任务明细为空");
            }

            final JyExceptionInterceptDetail jyExceptionInterceptDetail = detailResult.getData();
            final JyExceptionInterceptDetailDto jyExceptionInterceptDetailDto = new JyExceptionInterceptDetailDto();
            this.assembleJyExceptionInterceptDetailDto(jyExceptionInterceptDetailDto, jyBizTaskExceptionExist, jyExceptionInterceptDetail);
            result.setData(jyExceptionInterceptDetailDto);
        } catch (Exception e) {
            result.toFail("接口异常");
            logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept exception param {} exception {}", JsonHelper.toJson(req), e.getMessage(), e);
        }
        return result;
    }

    private void assembleJyExceptionInterceptDetailDto(JyExceptionInterceptDetailDto jyExceptionInterceptDetailDto, JyBizTaskExceptionEntity jyBizTaskExceptionExist, JyExceptionInterceptDetail jyExceptionInterceptDetailExist) {
        jyExceptionInterceptDetailDto.setBizId(jyExceptionInterceptDetailExist.getBizId());
        jyExceptionInterceptDetailDto.setBarCode(jyExceptionInterceptDetailExist.getBarCode());
        jyExceptionInterceptDetailDto.setCreateUserId(jyExceptionInterceptDetailExist.getCreateUserId().intValue());
        jyExceptionInterceptDetailDto.setCreateUserErp(jyExceptionInterceptDetailExist.getCreateUserCode());
        jyExceptionInterceptDetailDto.setCreateUserName(jyExceptionInterceptDetailExist.getCreateUserName());
        jyExceptionInterceptDetailDto.setTaskType(JyBizTaskExceptionTypeEnum.INTERCEPT.getCode());
        jyExceptionInterceptDetailDto.setTaskTypeName(JyBizTaskExceptionTypeEnum.INTERCEPT.getName());
        jyExceptionInterceptDetailDto.setInterceptType(jyExceptionInterceptDetailExist.getInterceptType());
        jyExceptionInterceptDetailDto.setInterceptTypeName(businessInterceptConfig.getInterceptTypeNodeConfig().get(jyExceptionInterceptDetailExist.getInterceptType().toString()));

        // 已处理过的，显示实际的处理方式
        List<Integer> disposeNodeList = new ArrayList<>();
        List<String> disposeNodeNameList = new ArrayList<>();
        if(jyExceptionInterceptDetailExist.getDisposeNode() != null && jyExceptionInterceptDetailExist.getDisposeNode() > 0){
            disposeNodeList.add(jyExceptionInterceptDetailExist.getDisposeNode());
        } else {
            // 未处理的，显示可以处理的所有方式
            disposeNodeList = businessInterceptConfig.getDisposeNodeListByInterceptType(jyExceptionInterceptDetailExist.getInterceptType());
        }
        if (CollectionUtils.isNotEmpty(disposeNodeList)) {
            disposeNodeNameList = disposeNodeList.stream().map(item -> {
                return businessInterceptConfig.getDisposeNodeConfig().get(item.toString());
            }).collect(Collectors.toList());
        }
        jyExceptionInterceptDetailDto.setDisposeNodeName(disposeNodeNameList);
        jyExceptionInterceptDetailDto.setDisposeNode(disposeNodeList);
        jyExceptionInterceptDetailDto.setDisposeNodeName(disposeNodeNameList);

        // 体积、重量
        if(JyExpSaveTypeEnum.ENUM_LIST.contains(jyExceptionInterceptDetailExist.getSaveType())){
            if(isBiggerThanZero(jyExceptionInterceptDetailExist.getInputLength())){
                jyExceptionInterceptDetailDto.setInputLength(jyExceptionInterceptDetailExist.getInputLength());
            }
            if(isBiggerThanZero(jyExceptionInterceptDetailExist.getInputWidth())){
                jyExceptionInterceptDetailDto.setInputWidth(jyExceptionInterceptDetailExist.getInputWidth());
            }
            if(isBiggerThanZero(jyExceptionInterceptDetailExist.getInputHeight())){
                jyExceptionInterceptDetailDto.setInputHeight(jyExceptionInterceptDetailExist.getInputHeight());
            }
            if(isBiggerThanZero(jyExceptionInterceptDetailExist.getInputVolume())){
                jyExceptionInterceptDetailDto.setInputVolume(jyExceptionInterceptDetailExist.getInputVolume());
            }
            if(isBiggerThanZero(jyExceptionInterceptDetailExist.getInputWeight())){
                jyExceptionInterceptDetailDto.setInputWeight(jyExceptionInterceptDetailExist.getInputWeight());
            }
        }
    }

    private boolean isBiggerThanZero(BigDecimal val){
        return val != null && BigDecimal.ZERO.compareTo(val) < 0;
    }

    private Result<Void> checkParam4ExpTaskCommonReq(ExpTaskCommonReq req){
        Result<Void> result = Result.success();
        if(req == null){
            return result.toFail("参数错误，参数不能为空");
        }
        final CurrentOperate currentOperate = req.getCurrentOperate();
        if(currentOperate == null){
            return result.toFail("参数错误，currentOperate不能为空");
        }
        if (currentOperate.getOperatorId() == null) {
            return result.toFail("参数错误，currentOperate.operatorId不能为空");
        }
        final User user = req.getUser();
        if(user == null){
            return result.toFail("参数错误，user不能为空");
        }
        if (StringUtils.isBlank(user.getUserName())) {
            return result.toFail("参数错误，user.userName不能为空");
        }
        if (StringUtils.isBlank(req.getBizId())) {
            return result.toFail("参数错误，bizId不能为空");
        }
        return result;
    }

    private Result<Void> checkParam4GetTaskDetailOfIntercept(ExpTaskCommonReq req){
        Result<Void> result = Result.success();
        final Result<Void> checkCommonResult = this.checkParam4ExpTaskCommonReq(req);
        if (!checkCommonResult.isSuccess()) {
            return result.toFail(checkCommonResult.getMessage(), checkCommonResult.getCode());
        }
        return result;
    }

    /**
     * 拦截任务处理
     *
     * @param req
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    @Override
    public Result<Boolean> processTaskOfIntercept(ExpInterceptTaskProcessReq req) {
        if(logger.isInfoEnabled()){
            logger.info("JyExceptionServiceImpl.processTaskOfIntercept param: {}", JsonHelper.toJson(req));
        }
        Result<Boolean> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4ProcessTaskOfIntercept(req);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 业务条件校验
            final JyBizTaskExceptionEntity jyBizTaskExceptionExist = jyBizTaskExceptionDao.findByBizId(req.getBizId());
            if (jyBizTaskExceptionExist == null) {
                return result.toFail("未查找到数据");
            }

            final JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
            jyExceptionInterceptDetailQuery.setSiteId(req.getCurrentOperate().getSiteCode());
            jyExceptionInterceptDetailQuery.setBizId(req.getBizId());
            final Result<JyExceptionInterceptDetail> detailResult = jyExceptionBusinessInterceptDetailService.selectOne(jyExceptionInterceptDetailQuery);
            if (!detailResult.isSuccess()) {
                logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept queryDetail fail {}", JsonHelper.toJson(req));
                return result.toFail("查询拦截异常任务明细失败");
            }

            final JyExceptionInterceptDetail jyExceptionInterceptDetailExist = detailResult.getData();
            if (jyExceptionInterceptDetailExist == null) {
                logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept queryDetail null {}", JsonHelper.toJson(req));
                return result.toFail("查询拦截异常任务明细为空");
            }

            // 3. 执行逻辑
            final Date currentDate = new Date();
            JyExceptionInterceptDetail jyExceptionInterceptDetailUpdate = new JyExceptionInterceptDetail();
            jyExceptionInterceptDetailUpdate.setBizId(req.getBizId());
            jyExceptionInterceptDetailUpdate.setSiteId(jyExceptionInterceptDetailExist.getSiteId());
            final User user = req.getUser();
            jyExceptionInterceptDetailUpdate.setSaveType(JyExpSaveTypeEnum.TEMP_SAVE.getCode());
            jyExceptionInterceptDetailUpdate.setUpdateUserId((long)user.getUserCode());
            jyExceptionInterceptDetailUpdate.setUpdateUserCode(user.getUserErp());
            jyExceptionInterceptDetailUpdate.setUpdateUserName(user.getUserName());
            jyExceptionInterceptDetailUpdate.setUpdateTime(currentDate);
            // 3.2. 如果是需要触发换单的拦截单
            final List<Integer> disposeNodeMatchedList = businessInterceptConfig.getDisposeNodeListByInterceptType(jyExceptionInterceptDetailExist.getInterceptType());
            if(disposeNodeMatchedList.contains(BusinessInterceptConfig.DISPOSE_NODE_EXCHANGE_WAYBILL_PRINT)){
                final ExchangeWaybillRequest exchangeWaybillRequest = new ExchangeWaybillRequest();
                exchangeWaybillRequest.setUser(BeanConverter.convertToSdkUser(req.getUser()));
                exchangeWaybillRequest.setCurrentOperate(BeanConverter.convertToSdkCurrentOperate(req.getCurrentOperate()));
                exchangeWaybillRequest.setWaybillCode(jyExceptionInterceptDetailExist.getWaybillCode());
                final InvokeResult<String> exchangeNewWaybillResult = waybillInterceptReverseService.exchangeNewWaybill(exchangeWaybillRequest);
                if (exchangeNewWaybillResult == null) {
                    logger.error("JyExceptionServiceImpl_processTaskOfIntercept_fail exchangeNewWaybill_return_null {}", JsonHelper.toJson(exchangeWaybillRequest));
                    return result.toFail("任务处理失败，该异常拦截任务需要执行换单，但换单失败");
                }
                if (!exchangeNewWaybillResult.codeSuccess()) {
                    logger.error("JyExceptionServiceImpl_processTaskOfIntercept_fail exchangeNewWaybill_return_fail {}", JsonHelper.toJson(exchangeWaybillRequest));
                    return result.toFail("任务处理失败，该异常拦截任务需要执行换单，但换单失败，失败原因: " + exchangeNewWaybillResult.getMessage());
                }

                // 更新上新单号
                jyExceptionInterceptDetailUpdate.setWaybillCodeNew(exchangeNewWaybillResult.getData());
            }
            // 3.1. 将处理状态改为【继续处理】
            jyExceptionInterceptDetailDao.updateByBizId(jyExceptionInterceptDetailUpdate);

            // 3.2 状态置为处理中
            if (!Objects.equals(BusinessInterceptTypeEnum.ZERO_WEIGHT.getCode(), jyExceptionInterceptDetailExist.getInterceptType())) {
                JyBizTaskExceptionEntity bizTaskExceptionUpdate = new JyBizTaskExceptionEntity();
                bizTaskExceptionUpdate.setBizId(req.getBizId());
                // 0重量的不设置为处理中
                bizTaskExceptionUpdate.setStatus(JyExpStatusEnum.PROCESSING.getCode());
                assembleJyBizExceptionTaskProcessingStatus(jyExceptionInterceptDetailExist, bizTaskExceptionUpdate);
                bizTaskExceptionUpdate.setUpdateUserErp(user.getUserErp());
                bizTaskExceptionUpdate.setUpdateUserName(user.getUserName());
                bizTaskExceptionUpdate.setUpdateTime(currentDate);
                jyBizTaskExceptionDao.updateByBizId(bizTaskExceptionUpdate);
            }
            result.toSuccess(true, "处理成功");
        } catch (Exception e) {
            result.toFail("接口异常");
            logger.error("JyExceptionServiceImpl.processTaskOfIntercept exception param {} exception {}", JsonHelper.toJson(req), e.getMessage(), e);
        }
        return result;
    }

    private Result<Void> checkParam4ProcessTaskOfIntercept(ExpInterceptTaskProcessReq req){
        Result<Void> result = Result.success();
        final Result<Void> checkCommonResult = this.checkParam4ExpTaskCommonReq(req);
        if (!checkCommonResult.isSuccess()) {
            return result.toFail(checkCommonResult.getMessage(), checkCommonResult.getCode());
        }
        return result;
    }


    private void assembleJyBizExceptionTaskProcessingStatus(JyExceptionInterceptDetail jyExceptionInterceptDetailExist, JyBizTaskExceptionEntity bizTaskExceptionUpdate) {
        final List<Integer> disposeNodeMatchedList = businessInterceptConfig.getDisposeNodeListByInterceptType(jyExceptionInterceptDetailExist.getInterceptType());
        // 需要 换单打印
        if(disposeNodeMatchedList.contains(BusinessInterceptConfig.DISPOSE_NODE_EXCHANGE_WAYBILL_PRINT)){
            bizTaskExceptionUpdate.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_EXCHANGE_PRINT.getCode());
        }
        // 需要 补称重
        if(disposeNodeMatchedList.contains(BusinessInterceptConfig.DISPOSE_NODE_WEIGHT_VOLUME)){
            bizTaskExceptionUpdate.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_UPLOAD_WEIGHT_VOLUME.getCode());
        }
        // 需要 补打
        if(disposeNodeMatchedList.contains(BusinessInterceptConfig.DISPOSE_NODE_REPRINT)){
            bizTaskExceptionUpdate.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_REPRINT.getCode());
        }
    }

    /**
     * 拦截任务-上传重量体积
     *
     * @param req
     * @author fanggang7
     * @time 2024-01-17 18:39:37 周三
     */
    @Override
    public Result<Boolean> processTaskOfInterceptSubmitWeightVolume(ExpInterceptTaskProcessSubmitWeightVolumeReq req) {
        if(logger.isInfoEnabled()){
            logger.info("JyExceptionServiceImpl.processTaskOfInterceptSubmitWeightVolume param: {}", JsonHelper.toJson(req));
        }
        Result<Boolean> result = Result.success();

        try {
            // 1. 参数校验
            final Result<Void> checkResult = this.checkParam4ProcessTaskOfInterceptSubmitWeightVolume(req);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            // 2. 业务条件校验
            final JyBizTaskExceptionEntity jyBizTaskExceptionExist = jyBizTaskExceptionDao.findByBizId(req.getBizId());
            if (jyBizTaskExceptionExist == null) {
                return result.toFail("未查找到数据");
            }

            final JyExceptionInterceptDetailQuery jyExceptionInterceptDetailQuery = new JyExceptionInterceptDetailQuery();
            final CurrentOperate currentOperate = req.getCurrentOperate();
            jyExceptionInterceptDetailQuery.setSiteId(currentOperate.getSiteCode());
            jyExceptionInterceptDetailQuery.setBizId(req.getBizId());
            final Result<JyExceptionInterceptDetail> detailResult = jyExceptionBusinessInterceptDetailService.selectOne(jyExceptionInterceptDetailQuery);
            if (!detailResult.isSuccess()) {
                logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept queryDetail fail {}", JsonHelper.toJson(req));
                return result.toFail("查询拦截异常任务明细失败");
            }

            final JyExceptionInterceptDetail jyExceptionInterceptDetailExist = detailResult.getData();
            if (jyExceptionInterceptDetailExist == null) {
                logger.error("JyExceptionServiceImpl.getTaskDetailOfIntercept queryDetail null {}", JsonHelper.toJson(req));
                return result.toFail("查询拦截异常任务明细为空");
            }

            // 3. 执行逻辑
            final Date currentDate = new Date();
            JyExceptionInterceptDetail jyExceptionInterceptDetail = new JyExceptionInterceptDetail();
            jyExceptionInterceptDetail.setBizId(req.getBizId());
            jyExceptionInterceptDetail.setSiteId(jyExceptionInterceptDetailExist.getSiteId());
            jyExceptionInterceptDetail.setSaveType(req.getSaveType());
            final User user = req.getUser();
            jyExceptionInterceptDetail.setUpdateUserId((long)user.getUserCode());
            jyExceptionInterceptDetail.setUpdateUserCode(user.getUserErp());
            jyExceptionInterceptDetail.setUpdateUserName(user.getUserName());
            jyExceptionInterceptDetail.setUpdateTime(currentDate);

            // 3.1 保存重量体积数据到明细中
            jyExceptionInterceptDetail.setInputLength(req.getLength());
            jyExceptionInterceptDetail.setInputWidth(req.getWidth());
            jyExceptionInterceptDetail.setInputHeight(req.getHeight());
            if(req.getVolume() != null){
                jyExceptionInterceptDetail.setInputVolume(req.getVolume());
            } else {
                if(req.getLength() != null && req.getWidth() != null && req.getHeight() != null){
                    jyExceptionInterceptDetail.setInputVolume(req.getLength().multiply(req.getWidth()).multiply(req.getHeight()).setScale(2, RoundingMode.HALF_UP));
                }
            }
            jyExceptionInterceptDetail.setInputWeight(req.getWeight());
            jyExceptionInterceptDetailDao.updateByBizId(jyExceptionInterceptDetail);

            // 3.3 若是提交保存，状态置为完结
            if (Objects.equals(req.getSaveType(), JyExpSaveTypeEnum.SAVE.getCode())) {
                // 3.2 提交到重量体积接口
                final WeightVolumeCondition weightVolumeCondition = new WeightVolumeCondition();
                weightVolumeCondition.setBarCode(jyExceptionInterceptDetailExist.getPackageCode());
                weightVolumeCondition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE.name());
                weightVolumeCondition.setSourceCode(FromSourceEnum.DMS_PDA.name());
                weightVolumeCondition.setLength(req.getLength().doubleValue());
                weightVolumeCondition.setWidth(req.getWidth().doubleValue());
                weightVolumeCondition.setHeight(req.getHeight().doubleValue());
                weightVolumeCondition.setVolume(req.getVolume().doubleValue());
                weightVolumeCondition.setWeight(req.getWeight().doubleValue());
                weightVolumeCondition.setOperatorCode(user.getUserErp());
                weightVolumeCondition.setOperatorId(user.getUserCode());
                weightVolumeCondition.setOperatorName(user.getUserName());
                weightVolumeCondition.setOperateSiteCode(currentOperate.getSiteCode());
                weightVolumeCondition.setOperateSiteName(currentOperate.getSiteName());
                weightVolumeCondition.setOperateTime(currentDate.getTime());
                final Result<Boolean> uploadWeightAndVolumeResult = dmsWeightVolumeService.checkAndUpload(weightVolumeCondition);
                if (!uploadWeightAndVolumeResult.isSuccess()) {
                    return result.toFail("提交失败。原因： " + uploadWeightAndVolumeResult.getMessage());
                }

                JyBizTaskExceptionEntity bizTaskExceptionUpdate = new JyBizTaskExceptionEntity();
                bizTaskExceptionUpdate.setBizId(req.getBizId());
                bizTaskExceptionUpdate.setStatus(JyExpStatusEnum.COMPLETE.getCode());
                bizTaskExceptionUpdate.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.DONE.getCode());
                bizTaskExceptionUpdate.setUpdateUserErp(user.getUserErp());
                bizTaskExceptionUpdate.setUpdateUserName(user.getUserName());
                bizTaskExceptionUpdate.setUpdateTime(currentDate);
                jyBizTaskExceptionDao.updateByBizId(bizTaskExceptionUpdate);
            }

            String returnMessage = "提交成功";
            if (Objects.equals(req.getSaveType(), JyExpSaveTypeEnum.TEMP_SAVE.getCode())) {
                returnMessage = "已暂存";
            }
            result.toSuccess(true, returnMessage);
        } catch (Exception e) {
            result.toFail("接口异常");
            logger.error("JyExceptionServiceImpl.processTaskOfInterceptSubmitWeightVolume exception param {} exception {}", JsonHelper.toJson(req), e.getMessage(), e);
        }
        return result;
    }

    private Result<Void> checkParam4ProcessTaskOfInterceptSubmitWeightVolume(ExpInterceptTaskProcessSubmitWeightVolumeReq req){
        Result<Void> result = Result.success();
        final Result<Void> checkCommonResult = this.checkParam4ExpTaskCommonReq(req);
        if (!checkCommonResult.isSuccess()) {
            return result.toFail(checkCommonResult.getMessage(), checkCommonResult.getCode());
        }
        if (req.getSaveType() == null) {
            return result.toFail("参数错误，saveType不能为空");
        }
        if (!JyExpSaveTypeEnum.ENUM_LIST.contains(req.getSaveType())) {
            return result.toFail("参数错误，saveType值不合法");
        }
        if (Objects.equals(JyExpSaveTypeEnum.SAVE.getCode(), req.getSaveType())) {
            if (req.getLength() == null) {
                return result.toFail("参数错误，length不能为空");
            }
            if (req.getWidth() == null) {
                return result.toFail("参数错误，width不能为空");
            }
            if (req.getHeight() == null) {
                return result.toFail("参数错误，height不能为空");
            }
            if (req.getWeight() == null) {
                return result.toFail("参数错误，weight不能为空");
            }
        }
        return result;
    }
}
