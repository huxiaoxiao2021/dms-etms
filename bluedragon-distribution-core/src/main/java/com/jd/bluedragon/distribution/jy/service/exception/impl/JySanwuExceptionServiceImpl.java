package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionEntity;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.jy.service.exception.JySanwuExceptionService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JySealCarDetail;
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getWaitReceiveSanwuExceptionByPage", mState = {JProEnum.TP})
    public JdCResponse<List<ExpTaskOfWaitReceiveDto>> getWaitReceiveSanwuExceptionByPage(ExpTaskStatisticsReq req) {
        if(logger.isInfoEnabled()){
            logger.info("getWaitReceiveSanwuExceptionByPage获取待领取任务列表入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<List<ExpTaskOfWaitReceiveDto>> response = new JdCResponse<>();
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
            if(logger.isInfoEnabled()){
                logger.info("查询待取件统计数据参数:{}", JSON.toJSONString(req));
            }
            List<StatisticsByGridDto> statistics = jyBizTaskExceptionDao.getStatisticsExceptionTaskList(req);
            if(logger.isInfoEnabled()){
                logger.info("查询待取件统计数据参数:{}", JSON.toJSONString(statistics));
            }
            if (CollectionUtils.isEmpty(statistics)) {
                response.toFail("获取任务为空!");
                return response;
            }
            List<ExpTaskOfWaitReceiveDto> waitReceiveDtos = new ArrayList<>();
            for (StatisticsByGridDto statisticOfGrid: statistics ) {
                ExpTaskOfWaitReceiveDto expTaskOfWaitReceiveDto = getExpTaskOfWaitReceiveDto(statisticOfGrid);
                //获取三无任务列表
                ExpTaskStatisticsDetailReq statisticsDetailReq = coverToExpTaskStatisticsDetailReq(req, statisticOfGrid);
                if(logger.isInfoEnabled()){
                    logger.info("查询网格下的超时待取件任务列表入参:{}", JSON.toJSONString(statisticsDetailReq));
                }
                List<JyBizTaskExceptionEntity> statisticsExceptionTaskDetailList = jyBizTaskExceptionDao.getStatisticsExceptionTaskDetailList(statisticsDetailReq);
                if(logger.isInfoEnabled()){
                    logger.info("查询网格下的超时待取件任务列表出参:{}", JSON.toJSONString(statisticsExceptionTaskDetailList));
                }
                List<ExpTaskDto> expTaskDtos = new ArrayList<>();
                for (JyBizTaskExceptionEntity taskExceptionEntity :statisticsExceptionTaskDetailList) {
                    ExpTaskDto taskDto = getTaskDto(taskExceptionEntity);
                    expTaskDtos.add(taskDto);
                    expTaskOfWaitReceiveDto.setTaskDtos(expTaskDtos);
                }
                waitReceiveDtos.add(expTaskOfWaitReceiveDto);
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
    public JdCResponse<List<ExpSignUserResp>> getExpSignInUserByPage(ExpSignUserReq req) {
        if(logger.isInfoEnabled()){
            logger.info("getExpSignInUserByPage 获取异常岗签到用户入参-{}",JSON.toJSONString(req));
        }
        JdCResponse<List<ExpSignUserResp>> response = new JdCResponse<>();
        response.toSucceed(JdCResponse.MESSAGE_SUCCESS);
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null || StringUtils.isBlank(position.getRefGridKey())) {
            response.toFail("网格码有误!");
            return response;
        }
        String refGridKey = position.getRefGridKey();
        //获取当前零点时间
        Date zeroTimes = DateHelper.getCurrentDayWithOutTimes();


        return null;

    }

    private ExpTaskOfWaitReceiveDto getExpTaskOfWaitReceiveDto(StatisticsByGridDto statisticOfGrid){
        ExpTaskOfWaitReceiveDto receiveDto = new ExpTaskOfWaitReceiveDto();
        receiveDto.setCount(statisticOfGrid.getTimeoutNum());
        receiveDto.setAreaName(statisticOfGrid.getAreaName());
        receiveDto.setGridCode(statisticOfGrid.getGridCode());
        receiveDto.setGridNo(statisticOfGrid.getGridNo());
        return receiveDto;
    }

    private ExpTaskStatisticsDetailReq coverToExpTaskStatisticsDetailReq(ExpTaskStatisticsReq req,StatisticsByGridDto statisticOfGrid){
        ExpTaskStatisticsDetailReq statisticsDetailReq = new ExpTaskStatisticsDetailReq();
        statisticsDetailReq.setSiteId(req.getSiteId());
        statisticsDetailReq.setType(req.getType());
        statisticsDetailReq.setStatus(req.getStatus());
        statisticsDetailReq.setFloor(statisticsDetailReq.getFloor());
        statisticsDetailReq.setGridCode(statisticOfGrid.getGridCode());
        statisticsDetailReq.setGridNo(statisticOfGrid.getGridNo());
        statisticsDetailReq.setAreaCode(statisticsDetailReq.getAreaCode());
        statisticsDetailReq.setGridRid(req.getGridRid());
        statisticsDetailReq.setTimeOutTime(req.getTimeOutTime());
        return statisticsDetailReq;
    }

    private Boolean checkWaitReceiveSanwuParam(JdCResponse<List<ExpTaskOfWaitReceiveDto>>response, ExpTaskStatisticsReq req){
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

}
