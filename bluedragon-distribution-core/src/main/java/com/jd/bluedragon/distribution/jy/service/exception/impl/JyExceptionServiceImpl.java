package com.jd.bluedragon.distribution.jy.service.exception.impl;
import com.google.common.collect.Lists;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskExceptionSourceEnum;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class JyExceptionServiceImpl implements JyExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);
    private static final String TASK_CACHE_PRE = "DMS:JYAPP:EXP:TASK_CACHE:";

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private PositionQueryJsfService positionQueryJsfService;
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
     * @param req
     */
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {

        ExpTaskDetailCacheDto taskCache = new ExpTaskDetailCacheDto();
        taskCache.setExpBarcode(req.getBarCode());

//        9.	卸车入口：根据操作异常上报人员此前扫描验货的3个包裹号获取到对应上游发货批次号，后续作为批次号信息辅助录入
//        10.	通用扫描入口（右上角点点点）：上报时不记录任何信息
//        11.	发货入口：操作异常上报人员此前扫描发货的3个包裹对应的发货目的地id，后续作为下级地信息辅助录入
        if (CollectionUtils.isNotEmpty(req.getRecentPackageCodeList())) {
            // 发货
            if (Objects.equals(req.getSource(), JyBizTaskExceptionSourceEnum.SEND.getCode())) {
                List<Integer> receiveSiteList = queryRecentSendInfo(req);
                if (CollectionUtils.isNotEmpty(receiveSiteList)) {
                    taskCache.setRecentReceiveSiteList(receiveSiteList);
                }
            }
            // 卸车
            if (Objects.equals(req.getSource(), JyBizTaskExceptionSourceEnum.UN_LOAD.getCode())) {
                List<String> sendCodeList = queryRecentInspectInfo(req);
                if (CollectionUtils.isNotEmpty(sendCodeList)) {
                    taskCache.setRecentSendCodeList(sendCodeList);
                }
            }
        }

        JSONObject json = (JSONObject) JSONObject.toJSON(taskCache);

        String redisKey = TASK_CACHE_PRE + req.getBarCode();
        String s = redisClient.get(redisKey);
        if (StringUtils.isNotBlank(s)) {
            JSONObject cacheJson = JSON.parseObject(s);
            cacheJson.putAll(json);
            json = cacheJson;
        }
        redisClient.set(redisKey, json.toJSONString());
        redisClient.expire(redisKey, 30, TimeUnit.DAYS);

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
        Result<PositionDetailRecord> positionDetailRecordResult = positionQueryJsfService.queryOneByPositionCode(req.getPositionCode());
        PositionDetailRecord data = positionDetailRecordResult.getData();
        if (data == null){
            logger.error("createScheduleTask req:{}",req.getPositionCode());
            result.toFail("无效的网格码");
        }
        String gridRid = data.getSiteCode()+"-"+data.getFloor()+"-"+data.getGridCode();
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

        TagDto tag = new TagDto();
        tag.setCode(0);
        tag.setName("三无");
        tag.setStyle("");

        List<StatisticsByGridDto> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StatisticsByGridDto dto = new StatisticsByGridDto();
            dto.setFloor(0);
            dto.setGridNo("GN" +i);
            dto.setGriCode("GC" + i);
            dto.setAreaCode("AC" + i);
            dto.setAreaName("AN" + i);
            dto.setPendingNum(i+10);
            dto.setTimeoutNum(i);
            dto.setTags(Arrays.asList(tag));
            list.add(dto);
        }

        return JdCResponse.ok(list);
    }

    /**
     * 取件进行中数据统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req) {
        List<ProcessingNumByGridDto> list = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            ProcessingNumByGridDto dto = new ProcessingNumByGridDto();
            dto.setFloor(i % 2);
            dto.setGridNo("");
            dto.setGriCode("");
            dto.setAreaCode("");
            dto.setProcessingNum(0);

        }
        return JdCResponse.ok(list);
    }

    /**
     * 任务列表接口
     *
     */
    @Override
    public JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req) {
        List<ExpTaskDto> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            ExpTaskDto dto = new ExpTaskDto();
            dto.setTaskId("T" + i);
            dto.setBarCode("SW" + i);
            dto.setStayTime("10");
            dto.setFloor("" + (i % 2));
            dto.setGridCode("GC" + i);
            dto.setGridNo("GN" + i);
            dto.setAreaName("AN" + i);
            dto.setReporterName("");
            dto.setTags("");
            dto.setSaved("");

            list.add(dto);
        }

        return JdCResponse.ok(list);
    }

    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> receive(ExpReceiveReq req) {

        return JdCResponse.ok();
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {

        ExpTaskDetailDto dto = new ExpTaskDetailCacheDto();
        dto.setWeight("");
        dto.setVolume("");
        dto.setTogetherPackageCodes("");
        dto.setBatchNo("");
        dto.setInnerDesc("");
        dto.setOuterDesc("");
        dto.setFrom("");
        dto.setTo("");
        dto.setVolumeDetail("");
        dto.setSn("");
        dto.setGoodsNo("");
        dto.setYardSixNine("");
        dto.setGoodsNum("");
        dto.setSealNumber("");
        dto.setPrice("");
        dto.setStorage("");
        dto.setTaskId("");
        dto.setSaveType("");
        dto.setUserErp("");
        dto.setImageUrls("");

        return JdCResponse.ok(dto);
    }

    /**
     * 处理任务接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> processTask(ExpTaskDetailReq req) {

        return JdCResponse.ok();
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
        Result<PositionDetailRecord> positionDetailRecordResult = positionQueryJsfService.queryOneByPositionCode(req.getPositionCode());
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
    private List<Integer> queryRecentSendInfo(ExpUploadScanReq req) {
        List<Integer> siteIdList = new ArrayList<>();
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
    private List<String> queryRecentInspectInfo(ExpUploadScanReq req) {
        List<String> sendCodeList = new ArrayList<>();
        for (String packageCode : req.getRecentPackageCodeList()) {
            if (!WaybillUtil.isPackageCode(packageCode)) {
                continue;
            }
            // 查询上游场地

        }
        return null;
    }

}
