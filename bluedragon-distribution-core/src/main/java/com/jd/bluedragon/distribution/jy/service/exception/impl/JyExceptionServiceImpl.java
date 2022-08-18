package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.distribution.jy.api.biz.exception.BizTaskExceptionService;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dto.exception.ExpefResultNotify;
import com.jd.bluedragon.distribution.jy.enums.ExpefNotifyTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private BasicPrimaryWS basicPrimaryWS;
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClient;


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

        BaseStaffSiteOrgDto baseStaffByErp = basicPrimaryWS.getBaseStaffByErp(req.getUserErp());
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

        // TODO 发送 mq 通知调度系统

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
        // TODO 标签处理

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

        BaseStaffSiteOrgDto baseStaffByErp = basicPrimaryWS.getBaseStaffByErp(req.getUserErp());
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

        return JdCResponse.ok();
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {

        String redisKey = TASK_CACHE_PRE + req.getTaskId();
        String s = redisClient.get(redisKey);
        if (StringUtils.isBlank(s)) {
            return JdCResponse.fail("无相关任务明细!" + req.getTaskId());
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
        String key = TASK_CACHE_PRE + req.getTaskId();
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
            redisClient.del(key);
            // TODO 调用 三无接口
        }

        return JdCResponse.ok();
    }

    @Override
    public void expefResultNotify(ExpefResultNotify mqDto) {
        if (null == mqDto.getNotifyType()){
            logger.warn("三无系统通知数据缺少通知类型，消息丢弃");
            return;
        }
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
        BaseStaffSiteOrgDto baseStaffByErp = basicPrimaryWS.getBaseStaffByErp(mqDto.getNotifyErp());
        if (baseStaffByErp == null){
            logger.error("获取操作人信息失败！");
            return;
        }
        switch (ExpefNotifyTypeEnum.valueOf(mqDto.getNotifyType())){
            case MATCH_SUCCESS:
                matchSuccessProcess(mqDto, entity, bizTaskException, baseStaffByErp);
                break;
            case PROCESSED:
                complate(mqDto, bizTaskException,baseStaffByErp);
                break;
            default:
                break;
        }
    }
    /**
     * 异常任务完成处理
     * @param mqDto
     * @param
     * @param bizTaskException
     * @param baseStaffByErp
     */
    private void complate(ExpefResultNotify mqDto, JyBizTaskExceptionEntity bizTaskException, BaseStaffSiteOrgDto baseStaffByErp) {
        // biz表修改状态
        JyBizTaskExceptionEntity conditon = new JyBizTaskExceptionEntity();
        conditon.setStatus(JyExpStatusEnum.COMPLATE.getCode());
        conditon.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.DONE.getCode());
        conditon.setUpdateTime(mqDto.getNotifyTime());
        conditon.setUpdateUserErp(mqDto.getNotifyErp());
        conditon.setBizId(bizTaskException.getBizId());
        conditon.setUpdateUserName(baseStaffByErp.getErp());
        jyBizTaskExceptionDao.updateByBizId(conditon);
        // todo 通知任务调度系统任务完成

    }

    /**
     * 匹配成功处理
     * @param mqDto
     * @param entity
     * @param bizTaskException
     * @param baseStaffByErp
     */
    private void matchSuccessProcess(ExpefResultNotify mqDto, JyExceptionEntity entity, JyBizTaskExceptionEntity bizTaskException, BaseStaffSiteOrgDto baseStaffByErp) {
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
     * 组织entity
     * @param req
     * @return
     */
    private JyBizTaskExceptionEntity convertDto2Entity(ExpUploadScanReq req){
        JyBizTaskExceptionEntity entity = new JyBizTaskExceptionEntity();

        //entity.setType(req.getType().getCode());
        //entity.setBizId(req.getBizId());
        //entity.setType(req.getType().getCode());
        //entity.setBarCode(req.getBarCode());
        //entity.setTags(JyBizTaskExceptionTagEnum.valueOf(req.getTags()));
        //entity.setSource(req.getSource().getCode());

        //岗位码相关
        Result<PositionDetailRecord> positionDetailRecordResult = positionQueryJsfManager.queryOneByPositionCode(req.getPositionCode());
        PositionDetailRecord data = positionDetailRecordResult.getData();
        if (data == null){
            logger.error("createScheduleTask req:{}",req.getPositionCode());
            throw new RuntimeException("无效的网格码");
        }
        entity.setFloor(data.getFloor());
        entity.setAreaCode(data.getAreaCode());
        entity.setAreaName(data.getAreaName());
        entity.setGridCode(data.getGridCode());
        entity.setGridNo(data.getGridNo());

        BaseStaffSiteOrgDto baseStaffByErp = basicPrimaryWS.getBaseStaffByErp(req.getUserErp());
        entity.setCreateUserErp(req.getUserErp());
        entity.setCreateUserName(baseStaffByErp.getStaffName());
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateUserErp(req.getUserErp());
        entity.setUpdateUserName(baseStaffByErp.getStaffName());
        entity.setUpdateTime(now);
        return entity;
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
}
