package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.*;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.exception.JyExpTaskMessage;
import com.jd.bluedragon.distribution.jy.enums.CustomerNotifyStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.manager.ExpInfoSummaryJsfManager;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategyFactory;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ps.data.epf.dto.CommonDto;
import com.jd.ps.data.epf.dto.ExpInfoSumaryInputDto;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JySealCarDetail;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskChangeStatusReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskStatusEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private UccPropertyConfiguration uccPropertyConfiguration;
    
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

        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("网格码有误!");
        }

        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            return JdCResponse.fail("登录人ERP有误!" + req.getUserErp());
        }
        String bizId = getBizId(req.getType(), req.getBarCode());
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
            JyExceptionStrategy exceptionService = jyExceptionStrategyFactory.getStrategy(req.getType());
            JdCResponse<Object> response = exceptionService.uploadScan(taskEntity,req, position, source, bizId);
            if(!JdCResponse.CODE_SUCCESS.equals(response.getCode())){
                return response;
            }

            taskEntity.setBizId(bizId);
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

        }catch (Exception e) {
            logger.error("写入异常提报数据出错了,request=" + JSON.toJSONString(req), e);
            return JdCResponse.fail("异常提报数据保存出错了,请稍后重试！");
        } finally {
            redisClient.del(existKey);
        }

        return JdCResponse.ok();
    }

    /**
     * 操作日志记录
     * @param cycle
     * @param entity
     */
    private void recordLog(JyBizTaskExceptionCycleTypeEnum cycle,JyBizTaskExceptionEntity entity){
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

        int completeExpDayNumLimit = uccPropertyConfiguration.getCompleteExpDayNumLimit();
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
            int completeExpDayNumLimit = uccPropertyConfiguration.getCompleteExpDayNumLimit();
            req.setLimitDay(completeExpDayNumLimit);
        }
        logger.info("queryExceptionTaskList 查询条件-{}",JSON.toJSONString(req));
        List<JyBizTaskExceptionEntity> taskList = jyBizTaskExceptionDao.queryExceptionTaskList(req);
        List<ExpTaskDto> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskList)) {
            //获取所有要查询的bizId 集合
            List<String> bizIds = taskList.stream().map(JyBizTaskExceptionEntity::getBizId).collect(Collectors.toList());
            JdCResponse<List<ExpScrappedDetailDto>> listOfscrappedResponse = jyScrappedExceptionService.getTaskListOfscrapped(bizIds);
            logger.info("listOfscrappedResponse -{}",JSON.toJSONString(listOfscrappedResponse));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (JyBizTaskExceptionEntity entity : taskList) {
                // 拼装dto
                ExpTaskDto dto = getTaskDto(entity);

                // 待处理状态数据
                if ((Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), entity.getStatus())) || (Objects.equals(JyExpStatusEnum.COMPLETE.getCode(), entity.getStatus()))) {
                    //处理三无待打印状态  待打印特殊处理
                    if(Objects.equals(JyBizTaskExceptionTypeEnum.SANWU.getCode(),entity.getType())){

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
                    }else if(Objects.equals(JyBizTaskExceptionTypeEnum.SCRAPPED.getCode(),dto.getType())){
                        //生鲜报废的特殊处理
                        if(!JdCResponse.CODE_SUCCESS.equals(listOfscrappedResponse.getCode())){
                            continue;
                        }
                        for (ExpScrappedDetailDto detailDto: listOfscrappedResponse.getData()) {
                            if(entity.getBizId().equals(detailDto.getBizId())){
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
                        }
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
        // 三无系统只处理大写字母
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
        String bizId =getBizId(req.getType(), req.getBarCode());
        JyBizTaskExceptionEntity taskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if (taskEntity == null) {
            return JdCResponse.fail("该条码无相关任务!" + req.getBarCode());
        }
        if (!Objects.equals(JyExpStatusEnum.TO_PICK.getCode(), taskEntity.getStatus())) {
            return JdCResponse.fail("当前任务"+req.getBarCode()+"已被领取,请勿重复操作!");
        }
        if (!Objects.equals(gridRid, taskEntity.getDistributionTarget())) {
//            return JdCResponse.fail("领取人的岗位与任务被分配的岗位不匹配!" + taskEntity.getDistributionTarget());
            return JdCResponse.fail("该条码无相关任务!" + req.getBarCode());
        }

        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(bizId);
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
        sendScheduleTaskStatusMsg(bizId, baseStaffByErp.getAccountNumber(), JyScheduleTaskStatusEnum.STARTED, scheduleTaskChangeStatusProducer);


        // 拼装已领取的任务
        Object taskDto = getTaskDto(taskEntity);
        return JdCResponse.ok(taskDto);
    }

    /**
     * 按条码查询
     *
     */
    @Override
    public JdCResponse<ExpTaskDto> queryByBarcode(Integer type, String barcode) {

        String bizId = getBizId(type, barcode);
        JyBizTaskExceptionEntity taskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if (taskEntity == null) {
            return JdCResponse.fail("该条码无相关任务!" + barcode);
        }
        ExpTaskDto taskDto = getTaskDto(taskEntity);
        return JdCResponse.ok(taskDto);
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
        Integer notifyStatus = jyExCustomerNotifyMQ.getNotifyStatus();
        switch (CustomerNotifyStatusEnum.convertApproveEnum(notifyStatus)) {
            case SELF_REPAIR_ORDER:
                // 自营补单 todo 调用终端妥投接口
                break;
            case SELF_REPAIR_PRICE:
                // 自营补差 todo 调用终端妥投接口
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
        String bizId = jyExCustomerNotifyMQ.getBusinessId();
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

    private void pushScrapTrace(JyBizTaskExceptionEntity exTaskEntity) {
        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP);
        status.setRemark(WaybillStatus.WAYBILL_TRACK_WASTE_SCRAP_MSG);
        status.setWaybillCode(exTaskEntity.getBarCode());
        status.setPackageCode(exTaskEntity.getBarCode());
        status.setOperateTime(exTaskEntity.getProcessBeginTime());

        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(exTaskEntity.getHandlerErp());
        status.setOperatorId(baseStaff == null ? 0 : baseStaff.getStaffNo());
        status.setOperator(baseStaff == null ? exTaskEntity.getHandlerErp() : baseStaff.getStaffName());
        status.setCreateSiteCode(exTaskEntity.getSiteCode().intValue());

        Task tTask = new Task();
        tTask.setKeyword1(exTaskEntity.getBarCode());
        tTask.setKeyword2(String.valueOf(status.getOperateType()));
        tTask.setCreateSiteCode(exTaskEntity.getSiteCode().intValue());
        tTask.setCreateTime(exTaskEntity.getProcessBeginTime());
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setBody(JsonHelper.toJson(status));
        tTask.setFingerprint(Md5Helper.encode(status.getCreateSiteCode() + "_"
                + status.getWaybillCode() + "-" + status.getOperateType() + "-" + status.getOperateTime().getTime()));
        taskService.add(tTask);
    }

    private void createSanWuTask(ExpefNotify mqDto) {
        String bizId = getBizId(JyBizTaskExceptionTypeEnum.SANWU.getCode(), mqDto.getBarCode());
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
        String bizId = getBizId(JyBizTaskExceptionTypeEnum.SANWU.getCode(), mqDto.getBarCode());
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
        String bizId = getBizId(JyBizTaskExceptionTypeEnum.SANWU.getCode(),mqDto.getBarCode());
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
    private String getGridRid(PositionDetailRecord data) {
        return data.getSiteCode() + "-" + data.getFloor() + "-" + data.getGridCode();
    }

    private PositionDetailRecord getPosition(String positionCode) {
        if (StringUtils.isBlank(positionCode)) {
            return null;
        }
        Result<PositionDetailRecord> positionResult = positionQueryJsfManager.queryOneByPositionCode(positionCode);
        if (positionResult == null || positionResult.isFail() || positionResult.getData() == null) {
            return null;
        }
        // 处理jsf泛型丢失问题z
        return JSON.parseObject(JSON.toJSONString(positionResult.getData()), PositionDetailRecord.class);
    }

    private String getBizId(Integer type, String barCode) {
        if(JyBizTaskExceptionTypeEnum.SCRAPPED.getCode().equals(type)){
            return JyBizTaskExceptionTypeEnum.SCRAPPED.name() + "_" + barCode;
        }
        return JyBizTaskExceptionTypeEnum.SANWU.name() + "_" + barCode;
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
}
