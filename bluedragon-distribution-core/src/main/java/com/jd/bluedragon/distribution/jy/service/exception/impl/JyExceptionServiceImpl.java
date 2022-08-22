package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dto.exception.JyExpTaskChangeMessage;
import com.jd.bluedragon.distribution.jy.dto.exception.JyExpTaskMessage;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionEntity;
import com.jd.bluedragon.distribution.jy.manager.ExpInfoSummaryJsfManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ps.data.epf.dto.CommonDto;
import com.jd.ps.data.epf.dto.ExpInfoSumaryInputDto;
import com.jd.ps.data.epf.dto.ExpefNotify;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
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

@Service
public class JyExceptionServiceImpl implements JyExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);
    private static final String TASK_CACHE_PRE = "DMS:JYAPP:EXP:TASK_CACHE:";
    private static final String RECEIVING_POSITION_COUNT_PRE = "DMS:JYAPP:EXP:RECEIVING_POSITION_COUNT_PRE:";
    private static final String RECEIVING_SITE_COUNT_PRE = "DMS:JYAPP:EXP:RECEIVING_SITE_COUNT_PRE:";

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
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
    private WaybillCacheService waybillCacheService;
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

    /**
     * 通用异常上报入口-扫描
     *
     */
    @Override
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
        String bizId = getBizId(JyBizTaskExceptionTypeEnum.SANWU, req.getBarCode());
        JyBizTaskExceptionEntity byBizId = jyBizTaskExceptionDao.findByBizId(bizId);
        if (byBizId != null) {
            return JdCResponse.fail("已存在当前条码的任务,请勿重复提交!");
        }

        req.setSiteId(position.getSiteCode());

        ExpTaskDetailCacheDto taskCache = new ExpTaskDetailCacheDto();
        taskCache.setExpBarcode(req.getBarCode());
        taskCache.setExpCreateTime(System.currentTimeMillis());
        taskCache.setSource(source.getText());

//        9.	卸车入口：根据操作异常上报人员此前扫描验货的3个包裹号获取到对应上游发货批次号，后续作为批次号信息辅助录入
//        10.	通用扫描入口（右上角点点点）：上报时不记录任何信息
//        11.	发货入口：操作异常上报人员此前扫描发货的3个包裹对应的发货目的地id，后续作为下级地信息辅助录入
        if (CollectionUtils.isNotEmpty(req.getRecentPackageCodeList())) {
            // 发货
            if (Objects.equals(source, JyExpSourceEnum.SEND)) {
                Collection<Integer> receiveSiteList = queryRecentSendInfo(req);
                if (CollectionUtils.isNotEmpty(receiveSiteList)) {
                    taskCache.setRecentReceiveSiteList(receiveSiteList);
                }
            }
            // 卸车
            if (Objects.equals(source, JyExpSourceEnum.UNLOAD)) {
                Collection<String> sendCodeList = queryRecentInspectInfo(req);
                if (CollectionUtils.isNotEmpty(sendCodeList)) {
                    taskCache.setRecentSendCodeList(sendCodeList);
                }
            }
        }

        JSONObject json = (JSONObject) JSONObject.toJSON(taskCache);

        String redisKey = TASK_CACHE_PRE + bizId;
        String s = redisClient.get(redisKey);
        if (StringUtils.isNotBlank(s)) {
            JSONObject cacheJson = JSON.parseObject(s);
            cacheJson.putAll(json);
            json = cacheJson;
        }
        redisClient.set(redisKey, json.toJSONString());
        redisClient.expire(redisKey, 30, TimeUnit.DAYS);

        JyBizTaskExceptionEntity taskEntity = new JyBizTaskExceptionEntity();
        taskEntity.setBizId(bizId);
        taskEntity.setType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
        taskEntity.setSource(source.getCode());
        taskEntity.setBarCode(req.getBarCode());
        taskEntity.setTags(JyBizTaskExceptionTagEnum.SANWU.getCode());

        taskEntity.setSiteCode(new Long(position.getSiteCode()));
        taskEntity.setSiteName(position.getSiteName());
        taskEntity.setFloor(position.getFloor());
        taskEntity.setAreaCode(position.getAreaCode());
        taskEntity.setAreaName(position.getAreaName());
        taskEntity.setGridCode(position.getGridCode());
        taskEntity.setGridNo(position.getGridNo());

        taskEntity.setStatus(JyExpStatusEnum.TO_PICK.getCode());
        taskEntity.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PENDING_ENTRY.getCode());
        taskEntity.setCreateUserErp(req.getUserErp());
        taskEntity.setCreateUserName(baseStaffByErp.getStaffName());
        taskEntity.setCreateTime(new Date());
        taskEntity.setTimeOut(JyBizTaskExceptionTimeOutEnum.UN_TIMEOUT.getCode());
        taskEntity.setYn(1);

        JyExceptionEntity expEntity = new JyExceptionEntity();
        expEntity.setBizId(bizId);
        expEntity.setBarCode(req.getBarCode());
        expEntity.setSiteCode(new Long(position.getSiteCode()));
        expEntity.setSiteName(position.getSiteName());
        expEntity.setCreateUserErp(req.getUserErp());
        expEntity.setCreateUserName(baseStaffByErp.getStaffName());
        expEntity.setCreateTime(new Date());


        try {
            jyBizTaskExceptionDao.insertSelective(taskEntity);
            jyExceptionDao.insertSelective(expEntity);
        } catch (Exception e) {
            logger.error("写入异常提报数据出错了,request=" + JSON.toJSONString(req), e);
            return JdCResponse.fail("异常提报数据保存出错了,请稍后重试！");
        }

        // 发送 mq 通知调度系统
        JyExpTaskMessage taskMessage = new JyExpTaskMessage();
        taskMessage.setTaskType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
        taskMessage.setTaskStatus(JyExpStatusEnum.TO_PICK.getCode());
        taskMessage.setBizId(bizId);
        taskMessage.setOpeUser(req.getUserErp());
        taskMessage.setOpeUserName(baseStaffByErp.getStaffName());
        taskMessage.setOpeTime(new Date().getTime());

        String body = JSON.toJSONString(taskMessage);
        scheduleTaskAddProducer.sendOnFailPersistent(bizId, body);
        logger.info("异常岗-写入任务发送mq完成:body={}", body);

        return JdCResponse.ok();
    }

    /**
     * 按取件状态统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {
        JdCResponse<List<StatisticsByStatusDto>> result = new JdCResponse<>();
        //岗位码相关
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("网格码有误!");
        }
        String gridRid = getGridRid(position);
        List<StatisticsByStatusDto> statisticStatusResps = jyBizTaskExceptionDao.getStatusStatistic(gridRid);
        result.setData(statisticStatusResps);
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
            req.setPageNumber(10);
        }

        req.setGridRid(getGridRid(position));

        List<StatisticsByGridDto> statisticsByGrid = jyBizTaskExceptionDao.getStatisticsByGrid(req);
        if (CollectionUtils.isEmpty(statisticsByGrid)) {
            return JdCResponse.ok(statisticsByGrid);
        }

        // 标签处理
        List<JyBizTaskExceptionEntity> tagsByGrid = jyBizTaskExceptionDao.getTagsByGrid(req);

        // 排前三的标签
        final List<String> top3Tags = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            top3Tags.add(JyBizTaskExceptionTagEnum.values()[i].getCode());
        }

        // 取出所有网格的 属于前三的标签
        Multimap<String, String> gridTags = HashMultimap.create();
        for (JyBizTaskExceptionEntity entity : tagsByGrid) {
            if (StringUtils.isNotBlank(entity.getTags())) {
                String[] split = entity.getTags().split(",");
                for (String tag : split) {
                    if (top3Tags.contains(tag)) {
                        String key  = entity.getFloor() + ":" + entity.getAreaCode() + ":" + entity.getGridCode();
                        gridTags.put(key, entity.getTags());
                    }
                }
            }
        }

        // 转换标签格式
        ArrayListMultimap<String, TagDto> gridTagList = ArrayListMultimap.create();
        for (String key : gridTags.keys()) {
            List<String> tags = new ArrayList<>(gridTags.get(key));
            // 排序标签
            Collections.sort(tags, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return top3Tags.indexOf(o1) - top3Tags.indexOf(o2);
                }
            });
            List<TagDto> tagList = getTags(StringUtils.join(tags, ','));
            gridTagList.putAll(key, tagList);
        }

        // 填充标签
        for (StatisticsByGridDto dto : statisticsByGrid) {
            String key  = dto.getFloor() + ":" + dto.getAreaCode() + ":" + dto.getGridCode();
            dto.setTags(gridTagList.get(key));
        }

        return JdCResponse.ok(statisticsByGrid);
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

        // 按场地统计
        String siteCountKey = RECEIVING_SITE_COUNT_PRE + position.getSiteCode();
        Map<String, String> map = redisClient.hGetAll(siteCountKey);

        if (map==null||map.isEmpty()) {
            return JdCResponse.ok(list);
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] split = key.split("\\|");

            try {
                ProcessingNumByGridDto dto = new ProcessingNumByGridDto();
                dto.setFloor(new Integer(split[2]));
                dto.setAreaCode(split[3]);
                dto.setGriCode(split[4]);
                dto.setGridNo(split[5]);
                dto.setProcessingNum(StringUtils.isNumeric(entry.getValue()) ? 0 : new Integer(entry.getValue()));
                list.add(dto);
            } catch (Exception e) {
                logger.error("解析取件进行中数据出错了" + entry.getKey(), e);
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
            req.setPageNumber(10);
        }

        req.setSiteId(position.getSiteCode());

        List<JyBizTaskExceptionEntity> taskList = jyBizTaskExceptionDao.queryExceptionTaskList(req);
        List<ExpTaskDto> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskList)) {
            for (JyBizTaskExceptionEntity entity : taskList) {
                ExpTaskDto dto = new ExpTaskDto();
                dto.setBizId(entity.getBizId());
                dto.setBarCode(entity.getBarCode());
                dto.setStayTime(getStayTime(entity.getCreateTime()));
                dto.setFloor(entity.getFloor());
                dto.setGridCode(entity.getGridCode());
                dto.setGridNo(entity.getGridNo());
                dto.setAreaName(entity.getAreaName());
                dto.setReporterName(entity.getCreateUserName());
                dto.setTags(getTags(entity.getTags()));

                String s = redisClient.get(TASK_CACHE_PRE + entity.getBizId());
                boolean saved = !StringUtils.isBlank(s) && Objects.equals(JSON.parseObject(s, ExpTaskDetailCacheDto.class).getSaveType(), "0");
                dto.setSaved(saved);

            }
        }

        // 记录取件进行中
        // 按岗位统计
        String positionCountKey = RECEIVING_POSITION_COUNT_PRE +"|"+ position.getSiteCode() + "|"
                + position.getFloor() + "|" + position.getAreaCode() + "|" + position.getGridCode() + "|" + position.getGridNo();
        redisClient.sAdd(positionCountKey, req.getUserErp());
        Long size = redisClient.sCard(positionCountKey);

        // 按场地统计
        String siteCountKey = RECEIVING_SITE_COUNT_PRE + position.getSiteCode();
        redisClient.hSet(siteCountKey, positionCountKey, String.valueOf(size));

        redisClient.expire(positionCountKey, 30, TimeUnit.DAYS);
        redisClient.expire(siteCountKey, 30, TimeUnit.DAYS);

        return JdCResponse.ok(list);
    }

    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> receive(ExpReceiveReq req) {

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
        String bizId = getBizId(JyBizTaskExceptionTypeEnum.SANWU, req.getBarCode());
        JyBizTaskExceptionEntity taskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if (!Objects.equals(gridRid, taskEntity.getDistributionTarget())) {
            return JdCResponse.fail("领取人的岗位与任务被分配的岗位不匹配!" + taskEntity.getDistributionTarget());
        }

        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(bizId);
        update.setStatus(JyExpStatusEnum.TO_PROCESS.getCode());
        update.setHandlerErp(req.getUserErp());
        update.setUpdateUserErp(req.getUserErp());
        update.setUpdateUserName(baseStaffByErp.getStaffName());

        jyBizTaskExceptionDao.updateByBizId(update);

        JyExpTaskChangeMessage message = new JyExpTaskChangeMessage();
        message.setTaskType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
        message.setTaskStatus(JyExpStatusEnum.TO_PROCESS.getCode());
        message.setBizId(bizId);
        message.setOpeUser(req.getUserErp());
        message.setOpeUserName(baseStaffByErp.getStaffName());
        message.setOpeTime(new Date().getTime());

        String body = JSON.toJSONString(message);
        scheduleTaskChangeStatusProducer.sendOnFailPersistent(bizId, body);
        logger.info("异常岗-任务领取发送状态更新发送mq完成:body={}", body);

        return JdCResponse.ok();
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {

        String redisKey = TASK_CACHE_PRE + req.getBizId();
        String s = redisClient.get(redisKey);
        if (StringUtils.isBlank(s)) {
            return JdCResponse.fail("无相关任务明细!" + req.getBizId());
        }

        ExpTaskDetailCacheDto cacheDto = JSON.parseObject(s, ExpTaskDetailCacheDto.class);
        ExpTaskDetailDto dto = new ExpTaskDetailCacheDto();
        BeanUtils.copyProperties(cacheDto, dto);

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
        JSONObject cacheObj = new JSONObject();
        String key = TASK_CACHE_PRE + req.getBizId();
        String s = redisClient.get(key);
        if (StringUtils.isNotBlank(s)) {
            cacheObj = JSON.parseObject(s);
        }
        JSONObject reqObj = (JSONObject) JSONObject.toJSON(req);
        cacheObj.putAll(reqObj);

        // 存储类型 0暂存 1提交
        if ("0".equals(req.getSaveType())) {
            redisClient.set(key, cacheObj.toJSONString());
            redisClient.expire(key, 30, TimeUnit.DAYS);
        }else {
            ExpTaskDetailCacheDto cacheDto = cacheObj.toJavaObject(ExpTaskDetailCacheDto.class);

            // 调用 三无接口
            ExpInfoSumaryInputDto dto = getExpInfoDto(cacheDto);
            CommonDto commonDto = expInfoSummaryJsfManager.addExpInfoDetail(dto);
            if (!Objects.equals(commonDto.getCode(), CommonDto.CODE_SUCCESS)) {
                return JdCResponse.fail("提报三无系统失败:" + commonDto.getMessage());
            }
            redisClient.del(key);
        }

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
                    complate(mqDto);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("xxx",e);
        }

    }

    private void createSanWuTask(ExpefNotify mqDto) {
        String bizId = getBizId(JyBizTaskExceptionTypeEnum.SANWU, mqDto.getBarCode());
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
        taskEntity.setCreateUserErp(mqDto.getNotifyErp());
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(mqDto.getNotifyErp());
        if (baseStaffByErp == null){
            logger.error("获取操作人信息失败！");
            return;
        }
        taskEntity.setCreateUserName(baseStaffByErp.getStaffName());
        taskEntity.setCreateTime(new Date());
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
        scheduleTaskAddWorkerProducer.send(bizId,JsonHelper.toJson(req));
    }

    /**
     * 异常任务完成处理
     * @param
     */
    private void complate(ExpefNotify mqDto) {
        JyExceptionEntity jyExceptionEntity = new JyExceptionEntity();
        jyExceptionEntity.setBarCode(mqDto.getBarCode());
        jyExceptionEntity.setSiteCode(Long.valueOf(mqDto.getSiteCode()));
        JyExceptionEntity entity = jyExceptionDao.queryByBarCodeAndSite(jyExceptionEntity);
        if (entity == null){
            logger.error("获取异常业务数据失败！");
            return;
        }
        JyBizTaskExceptionEntity bizTaskException = jyBizTaskExceptionDao.findByBizId(entity.getBizId());
        if (bizTaskException == null){
            logger.error("获取异常业务任务数据失败！");
            return;
        }
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(mqDto.getNotifyErp());
        if (baseStaffByErp == null){
            logger.error("获取操作人信息失败！");
            return;
        }
        // biz表修改状态
        JyBizTaskExceptionEntity conditon = new JyBizTaskExceptionEntity();
        conditon.setStatus(JyExpStatusEnum.COMPLATE.getCode());
        conditon.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.DONE.getCode());
        conditon.setUpdateTime(mqDto.getNotifyTime());
        conditon.setUpdateUserErp(baseStaffByErp.getErp());
        conditon.setBizId(bizTaskException.getBizId());
        conditon.setUpdateUserName(baseStaffByErp.getStaffName());
        jyBizTaskExceptionDao.updateByBizId(conditon);
        //发送修改状态消息
        sendScheduleTaskCloseMsg(bizTaskException, baseStaffByErp,JyScheduleTaskStatusEnum.CLOSED,scheduleTaskChangeStatusWorkerProducer);
    }

    private void sendScheduleTaskCloseMsg(JyBizTaskExceptionEntity bizTaskException, BaseStaffSiteOrgDto baseStaffByErp,JyScheduleTaskStatusEnum status,DefaultJMQProducer producer) {
        //通知任务调度系统状态修改
        JyScheduleTaskChangeStatusReq req = new JyScheduleTaskChangeStatusReq();
        try{
            req.setBizId(bizTaskException.getBizId());
            req.setChangeTime(new Date());
            req.setOpeUser(baseStaffByErp.getErp());
            req.setOpeUserName(baseStaffByErp.getStaffName());
            req.setTaskStatus(status);
            req.setTaskType(JyScheduleTaskTypeEnum.EXCEPTION);
            producer.send(bizTaskException.getBizId(), JsonHelper.toJson(req));
        }catch (Exception e) {
            logger.error("拣运异常任务关闭消息MQ发送失败,message:{} :  ", JsonHelper.toJson(req),e);
        }
    }

    /**
     * 匹配成功处理
     * @param mqDto
     */
    private void matchSuccessProcess(ExpefNotify mqDto) {
        JyExceptionEntity jyExceptionEntity = new JyExceptionEntity();
        jyExceptionEntity.setBarCode(mqDto.getBarCode());
        jyExceptionEntity.setSiteCode(Long.valueOf(mqDto.getSiteCode()));
        JyExceptionEntity entity = jyExceptionDao.queryByBarCodeAndSite(jyExceptionEntity);
        if (entity == null){
            logger.error("获取异常业务数据失败！");
            return;
        }
        JyBizTaskExceptionEntity bizTaskException = jyBizTaskExceptionDao.findByBizId(entity.getBizId());
        if (bizTaskException == null){
            logger.error("获取异常业务任务数据失败！");
            return;
        }
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(mqDto.getNotifyErp());
        if (baseStaffByErp == null){
            logger.error("获取操作人信息失败！");
            return;
        }
        //业务表记录匹配成功单号
        entity.setPackageCode(mqDto.getPackageCode());
        entity.setUpdateTime(mqDto.getNotifyTime());
        entity.setUpdateUserErp(mqDto.getNotifyErp());
        entity.setUpdateUserName(baseStaffByErp.getStaffName());
        jyExceptionDao.update(entity);
        // biz表修改状态
        JyBizTaskExceptionEntity conditon = new JyBizTaskExceptionEntity();
        conditon.setStatus(JyExpStatusEnum.TO_PRINT.getCode());
        conditon.setUpdateTime(mqDto.getNotifyTime());
        conditon.setUpdateUserErp(mqDto.getNotifyErp());
        conditon.setBizId(bizTaskException.getBizId());
        jyBizTaskExceptionDao.updateByBizId(conditon);
    }


    /**
     * 近期发货的下游场地
     */
    private Collection<Integer> queryRecentSendInfo(ExpUploadScanReq req) {
        Set<Integer> siteIdList = new HashSet<>();
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
            if (sendDetail == null) {
                continue;
            }
            siteIdList.add(sendDetail.getReceiveSiteCode());
        }
        return siteIdList;
    }

    /**
     * 近期验货的上游发货批次
     */
    private Collection<String> queryRecentInspectInfo(ExpUploadScanReq req) {
        Set<String> sendCodeList = new HashSet<>();
        for (String packageCode : req.getRecentPackageCodeList()) {
            if (!WaybillUtil.isPackageCode(packageCode)) {
                continue;
            }
            // 查询上游场地

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
        return hours + ":" + minutes;
    }

    /**
     * 格式化标签
     */
    private List<TagDto> getTags(String tags) {
        List<TagDto> list = new ArrayList<>();
        if (StringUtils.isBlank(tags)) {
            return list;
        }
        String[] split = tags.split(",");
        for (String s : split) {
            TagDto dto = new TagDto();
            dto.setName(s);
            dto.setCode(0);
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
        return positionResult.getData();
    }

    private String getBizId(JyBizTaskExceptionTypeEnum en, String barCode) {
        return en.name() + "_" + barCode;
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
        dto.setExpCode(cacheDto.getExpBarcode());
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
//            dto.setVehicleNumber(cacheDto.getSealNumber());
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
        return dto;
    }
}
