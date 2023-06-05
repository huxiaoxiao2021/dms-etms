package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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
import com.jd.bluedragon.distribution.jy.exception.JyAssignExpTaskMQ;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionLogEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionEntity;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.jy.service.exception.JySanwuExceptionService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.BaseUserSignRecordVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JySealCarDetail;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskChangeStatusReq;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskStatusEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("jySanwuExceptionService")
public class JySanwuExceptionServiceImpl extends JyExceptionStrategy implements JySanwuExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JySanwuExceptionServiceImpl.class);
    private static final String TASK_CACHE_PRE = "DMS:JYAPP:EXP:TASK_CACHE01:";
    private static final String SPLIT = ",";

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private JyExceptionDao jyExceptionDao;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClient;
    @Autowired
    @Qualifier("jyUnloadVehicleManager")
    private IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;

    @Autowired
    private PositionQueryJsfManager positionQueryJsfManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private UserSignRecordDao userSignRecordDao;

    @Autowired
    @Qualifier("assignTaskExpProducer")
    private DefaultJMQProducer assignTaskExpProducer;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyBizTaskExceptionLogDao jyBizTaskExceptionLogDao;

    @Autowired
    @Qualifier("scheduleTaskChangeStatusProducer")
    private DefaultJMQProducer scheduleTaskChangeStatusProducer;

    @Override
    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.SANWU.getCode();
    }

    /**
     * 通用异常上报入口-扫描
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.uploadScan", mState = {JProEnum.TP})
    public JdCResponse<Object> uploadScan(JyBizTaskExceptionEntity taskEntity, ExpUploadScanReq req, PositionDetailRecord position,
                                          JyExpSourceEnum source,  String bizId) {
        logger.info("三无上报信息req-{} 岗位码信息position-{} bizId-{}", JSON.toJSONString(req), JSON.toJSONString(position), bizId);
        if (!BusinessUtil.isSanWuCode(req.getBarCode())) {
            return JdCResponse.fail("请扫描异常包裹的三无码或运单号!");
        }
        //三无系统只处理大写字母
        req.setBarCode(req.getBarCode().toUpperCase());
        //三无异常处理逻辑
        if (!BusinessUtil.isSanWuCode(req.getBarCode())) {
            return JdCResponse.fail("扫描格式错误!");
        }

        req.setSiteId(position.getSiteCode());

        ExpTaskDetailCacheDto taskCache = new ExpTaskDetailCacheDto();
        taskCache.setExpBarcode(req.getBarCode());
        taskCache.setExpCreateTime(System.currentTimeMillis());
        taskCache.setSource(source.getText());

        //        9.	卸车入口：根据操作异常上报人员此前扫描验货的3个包裹号获取到对应上游发货批次号，后续作为批次号信息辅助录入
        //        10.	通用扫描入口（右上角点点点）：上报时不记录任何信息
        //        11.	发货入口：操作异常上报人员此前扫描发货的3个包裹对应的发货目的地id，后续作为下级地信息辅助录入
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

        taskEntity.setType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
        taskEntity.setBarCode(req.getBarCode());
        taskEntity.setTags(JyBizTaskExceptionTagEnum.SANWU.getCode());

        JyExceptionEntity expEntity = new JyExceptionEntity();
        expEntity.setBizId(bizId);
        expEntity.setBarCode(req.getBarCode());
        expEntity.setSiteCode(new Long(position.getSiteCode()));
        expEntity.setSiteName(position.getSiteName());
        expEntity.setCreateUserErp(req.getUserErp());
        expEntity.setCreateTime(new Date());
        logger.info("写入三无异常提报-taskEntity-{} -expEntity-{}", JSON.toJSONString(taskEntity),
                JSON.toJSONString(expEntity));
        jyExceptionDao.insertSelective(expEntity);

        return JdCResponse.ok();
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

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getExpTaskStatisticsOfWaitReceiveByPage", mState = {JProEnum.TP})
    public JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>> getExpTaskStatisticsOfWaitReceiveByPage(ExpTaskStatisticsReq req) {
        if(logger.isInfoEnabled()){
            logger.info("getExpTaskStatisticsOfWaitReceiveByPage获取待领取任务列表入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>> response = new JdCResponse<>();
        response.toSucceed(JdCResponse.MESSAGE_SUCCESS);
        if(!checkWaitReceiveSanwuParam(response,req)){
            return response;
        }
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            response.toFail("网格码有误!");
            return response;
        }
        if (req.getPageNumber() == null || req.getPageNumber() <= 0) {
            req.setPageNumber(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(10);
        }
        try{
            //补充其他字段值
            int waitReceiveSanwuTaskTimeOfHours = uccPropertyConfiguration.getWaitReceiveSanwuTaskTimeOfHours();
            Date newDate = DateHelper.addHours(new Date(), -waitReceiveSanwuTaskTimeOfHours);
            //超时时间
            req.setTimeOutTime(newDate);
            req.setGridRid(getGridRid(position));
            req.setStatus(JyExpStatusEnum.TO_PICK.getCode());
            req.setType(JyBizTaskExceptionTypeEnum.SANWU.getCode());
            if(logger.isInfoEnabled()){
                logger.info("查询待取件统计数据入参:{}", JSON.toJSONString(req));
            }
            List<StatisticsTimeOutExpTaskDto> statistics = jyBizTaskExceptionDao.getStatisticsExceptionTaskList(req);
            if(logger.isInfoEnabled()){
                logger.info("查询待取件统计数据出参:{}", JSON.toJSONString(statistics));
            }
            if (CollectionUtils.isEmpty(statistics)) {
                response.toFail("获取任务为空!");
                return response;
            }
            List<ExpTaskStatisticsOfWaitReceiveDto> waitReceiveDtos = new ArrayList<>();
            for (StatisticsTimeOutExpTaskDto statisticOfGrid: statistics ) {
                ExpTaskStatisticsOfWaitReceiveDto expTaskStatisticsOfWaitReceiveDto = getExpTaskOfWaitReceiveDto(statisticOfGrid);
                waitReceiveDtos.add(expTaskStatisticsOfWaitReceiveDto);
            }
            response.setData(waitReceiveDtos);
        }catch (Exception e){
            logger.error("获取超时未领取任务异常-param-{}",JSON.toJSONString(req),e);
            response.setMessage("获取超时未领取任务异常!");
            response.setCode(JdCResponse.CODE_ERROR);
        }
        return response;
    }

    @Override
    public JdCResponse<List<ExpTaskDto>> getWaitReceiveSanwuExpTaskByPage(ExpTaskStatisticsDetailReq req) {
        if(logger.isInfoEnabled()){
            logger.info("getWaitReceiveSanwuExpTaskByPage 获取待领取任务列表入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<List<ExpTaskDto>> response = new JdCResponse<>();
        response.toSucceed(JdCResponse.MESSAGE_SUCCESS);
        if(!checkWaitReceiveSanwuExpTask(response,req)){
            return response;
        }
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            response.toFail("网格码有误!");
            return response;
        }
        if (req.getPageNumber() == null || req.getPageNumber() <= 0) {
            req.setPageNumber(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(10);
        }
        try{
            //补充其他字段值
            int waitReceiveSanwuTaskTimeOfHours = uccPropertyConfiguration.getWaitReceiveSanwuTaskTimeOfHours();
            Date newDate = DateHelper.addHours(new Date(), -waitReceiveSanwuTaskTimeOfHours);
            //超时时间
            req.setTimeOutTime(newDate);
            req.setGridRid(getGridRid(position));
            req.setStatus(JyExpStatusEnum.TO_PICK.getCode());
            req.setType(JyBizTaskExceptionTypeEnum.SANWU.getCode());

            if(logger.isInfoEnabled()){
                logger.info("查询待取件任务列表数据入参:{}", JSON.toJSONString(req));
            }
            List<JyBizTaskExceptionEntity>  taskList = jyBizTaskExceptionDao.getStatisticsExceptionTaskDetailList(req);
            if(logger.isInfoEnabled()){
                logger.info("查询待取件任务列表出参:{}", JSON.toJSONString(taskList));
            }
            if (CollectionUtils.isEmpty(taskList)) {
                response.toFail("获取任务列表为空!");
                return response;
            }
            List<ExpTaskDto> waitReceiveDtos = new ArrayList<>();
            for (JyBizTaskExceptionEntity taskExceptionEntity: taskList ) {
                ExpTaskDto taskDto = getTaskDto(taskExceptionEntity);
                waitReceiveDtos.add(taskDto);
            }
            response.setData(waitReceiveDtos);
        }catch (Exception e){
            logger.error("获取超时未领取任务列表异常-param-{}",JSON.toJSONString(req),e);
            response.setMessage("获取超时未领取任务列表异常!");
            response.setCode(JdCResponse.CODE_ERROR);
        }
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getExpSignInUserByPage", mState = {JProEnum.TP})
    public JdCResponse<List<ExpSignUserResp>> getExpSignInUserByPage(ExpSignUserReq req) {
        if(logger.isInfoEnabled()){
            logger.info("getExpSignInUserByPage 获取异常岗签到用户入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<List<ExpSignUserResp>> response = new JdCResponse<>();
        response.toSucceed(JdCResponse.MESSAGE_SUCCESS);
        UserSignRecordQuery query = new UserSignRecordQuery();
        if (req.getPageNumber() == null || req.getPageNumber() <= 0) {
            req.setPageNumber(1);
        }
        if (req.getPageSize() == null || req.getPageSize() <= 0) {
            req.setPageSize(10);
        }
        query.setPageNumber(req.getPageNumber());
        query.setPageSize(req.getPageSize());
        if(StringUtils.isNotBlank(req.getExpUserErp())){
            query.setUserErp(req.getExpUserErp());
        }
        List<ExpSignUserResp> expSignUserResps = new ArrayList<>();
        try{
            PositionDetailRecord position = getPosition(req.getPositionCode());
            if (position == null || StringUtils.isBlank(position.getRefGridKey())) {
                response.toFail("网格码有误!");
                return response;
            }
            String refGridKey = position.getRefGridKey();
            //获取当前零点时间
            Date zeroTime = DateHelper.getCurrentDayWithOutTimes();
            query.setRefGridKey(refGridKey);
            query.setSignInTime(zeroTime);
            if(logger.isInfoEnabled()){
                logger.info("getExpSignInUserByPage 获取异常岗签到信息入参-{}",JSON.toJSONString(query));
            }
            List<BaseUserSignRecordVo> baseUserSignRecordVos = userSignRecordDao.querySignInUserByCondition(query);
            if(logger.isInfoEnabled()){
                logger.info("getExpSignInUserByPage 获取异常岗签到信息出参-{}",JSON.toJSONString(baseUserSignRecordVos));
            }
            if(CollectionUtils.isEmpty(baseUserSignRecordVos)){
                response.toFail("获取当前岗位码【{"+req.getPositionCode()+"}】签到用户列表为空!");
                return response;
            }
            baseUserSignRecordVos.stream().forEach(item ->{
                ExpSignUserResp resp = new ExpSignUserResp();
                resp.setExpUserCode(item.getUserCode());
                resp.setExpUserErp(item.getUserName());
                expSignUserResps.add(resp);
            });
            response.setData(expSignUserResps);
            return  response;
        }catch (Exception e){
            logger.error(" 获取异常岗签到用户信息异常-parm-{}-{}",JSON.toJSONString(req),e.getMessage(),e);
            response.toError("获取异常岗签到用户信息异常!");
            return response;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.assignExpTask", mState = {JProEnum.TP})
    public JdCResponse<Boolean> assignExpTask(ExpTaskAssignRequest req) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(Boolean.TRUE);
        if(CollectionUtils.isEmpty(req.getBizIds())){
            response.toFail("指派任务不能为空!");
            response.setData(Boolean.FALSE);
            return response;
        }
        if(StringUtils.isBlank(req.getAssignHandlerErp())){
            response.toFail("任务指派人不能为空!");
            response.setData(Boolean.FALSE);
            return response;
        }
        try{
            BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getAssignHandlerErp());
            if (baseStaffByErp == null) {
                response.toFail("任务指派人不存在!");
                response.setData(Boolean.FALSE);
                return response;
            }
            //一次分配任务数量限制
            int assignExpTaskQuantityLimit = uccPropertyConfiguration.getAssignExpTaskQuantityLimit();
            if(assignExpTaskQuantityLimit < req.getBizIds().size()){
                response.toFail("一次指派任务条数不能超过限制{"+assignExpTaskQuantityLimit+"}!");
                response.setData(Boolean.FALSE);
                return response;
            }

            for (int i = 0; i < req.getBizIds().size(); i++) {
                JyAssignExpTaskMQ expMQ = coverToAssignExpTaskMQ(req.getBizIds().get(i),req.getAssignHandlerErp(),req.getUserErp());
                logger.info("指派异常任务信息-{}",JSON.toJSONString(expMQ));
                assignTaskExpProducer.send(expMQ.getBizId(), JsonHelper.toJson(expMQ));
            }
            return response;
        }catch(Exception e){
            logger.error("assignTaskExpProducer 发送指派任务MQ消息异常!",e);
            response.toFail("发送指派任务MQ消息异常!");
            response.setData(Boolean.FALSE);
            return response;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.dealAssignTaskData", mState = {JProEnum.TP})
    public void dealAssignTaskData(JyAssignExpTaskMQ mq) {
        JyBizTaskExceptionEntity bizEntity = jyBizTaskExceptionDao.findByBizId(mq.getBizId());
        if (bizEntity == null) {
            logger.warn("当前异常任务不存在-{}",mq.getBizId());
            return ;
        }
        if (!Objects.equals(JyExpStatusEnum.TO_PICK.getCode(), bizEntity.getStatus())) {
            logger.warn("当前异常任务状态非待取件状态-{}",mq.getBizId());
            return ;
        }

        //修改状态为 status处理中-processingStatus匹配中
        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(mq.getBizId());
        update.setTaskAssign(JyExpTaskAssignTypeEnum.ASSIGN.getCode());
        update.setStatus(JyExpStatusEnum.PROCESSING.getCode());
        update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITING_MATCH.getCode());
        update.setHandlerErp(mq.getAssignHandlerErp());
        update.setUpdateUserErp(mq.getPrincipalErp());
        update.setUpdateTime(new Date());
        update.setProcessBeginTime(new Date());

        jyBizTaskExceptionDao.updateByBizId(update);
        recordLog(JyBizTaskExceptionCycleTypeEnum.RECEIVE,update);
        //发送修改状态消息
        sendScheduleTaskStatusMsg(mq.getBizId(), mq.getAssignHandlerErp(), JyScheduleTaskStatusEnum.STARTED, scheduleTaskChangeStatusProducer);
        //发送咚咚消息给指派人
        pushToDongDong(mq.getAssignHandlerErp());
    }

    private void pushToDongDong(String erp){
        String title = "指派任务下发";
        String content = "您有一个新的指派任务，请及时查看处理";
        List<String> erps = Arrays.asList(erp);
        NoticeUtils.noticeToTimelineWithNoUrl(title, content, erps);
    }

    private Boolean checkWaitReceiveSanwuExpTask(JdCResponse<List<ExpTaskDto>> response,ExpTaskStatisticsDetailReq req){
        if(req == null){
            response.toFail("入参不能为空!");
            return false;
        }
        if(req.getSiteId() == null){
            response.toFail("站点id不能为空!");
            return false;
        }
        if(req.getFloor() == null){
            response.toFail("楼层不能为空!");
            return false;
        }
        if(StringUtils.isBlank(req.getGridCode())){
            response.toFail("网格编号不能为空!");
            return false;
        }
        if(StringUtils.isBlank(req.getGridNo())){
            response.toFail("网格号不能为空!");
            return false;
        }
        if(StringUtils.isBlank(req.getAreaCode())){
            response.toFail("作业区编码不能为空!");
            return false;
        }
        return true;
    }

    private JyAssignExpTaskMQ coverToAssignExpTaskMQ(String bizId,String erp,String principalErp){
        JyAssignExpTaskMQ mq = new JyAssignExpTaskMQ();
        mq.setBizId(bizId);
        mq.setAssignHandlerErp(erp);
        mq.setPrincipalErp(principalErp);
        return mq;
    }

    private ExpTaskStatisticsOfWaitReceiveDto getExpTaskOfWaitReceiveDto(StatisticsTimeOutExpTaskDto statisticOfGrid){
        ExpTaskStatisticsOfWaitReceiveDto receiveDto = new ExpTaskStatisticsOfWaitReceiveDto();
        receiveDto.setSiteCode(statisticOfGrid.getSiteCode());
        receiveDto.setFloor(statisticOfGrid.getFloor());
        receiveDto.setAreaName(statisticOfGrid.getAreaName());
        receiveDto.setGridCode(statisticOfGrid.getGridCode());
        receiveDto.setGridNo(statisticOfGrid.getGridNo());
        receiveDto.setCount(statisticOfGrid.getTimeoutNum());
        return receiveDto;
    }

    private ExpTaskStatisticsDetailReq coverToExpTaskStatisticsDetailReq(ExpTaskStatisticsReq req,StatisticsTimeOutExpTaskDto statisticOfGrid){
        ExpTaskStatisticsDetailReq statisticsDetailReq = new ExpTaskStatisticsDetailReq();
        statisticsDetailReq.setSiteId(req.getSiteId());
        statisticsDetailReq.setType(req.getType());
        statisticsDetailReq.setStatus(req.getStatus());
        statisticsDetailReq.setFloor(statisticOfGrid.getFloor());
        statisticsDetailReq.setGridCode(statisticOfGrid.getGridCode());
        statisticsDetailReq.setGridNo(statisticOfGrid.getGridNo());
        statisticsDetailReq.setAreaCode(statisticOfGrid.getAreaCode());
        statisticsDetailReq.setGridRid(req.getGridRid());
        statisticsDetailReq.setTimeOutTime(req.getTimeOutTime());
        return statisticsDetailReq;
    }

    private Boolean checkWaitReceiveSanwuParam(JdCResponse<List<ExpTaskStatisticsOfWaitReceiveDto>>response, ExpTaskStatisticsReq req){
        if(req == null){
            response.toFail("入参不能为空!");
            return false;
        }
        if(req.getSiteId() == null){
            response.toFail("站点id不能为空!");
            return false;
        }
        return true;
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

    /**
     * 拼接唯一网格标识
     */
    private String getGridRid(PositionDetailRecord data) {
        return data.getSiteCode() + "-" + data.getFloor() + "-" + data.getGridCode();
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
        return dto;
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
}
