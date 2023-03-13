package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionScrappedDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.manager.ExpInfoSummaryJsfManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyBizTaskExceptionLogService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("jyScrappedExceptionService")
public class JyScrappedExceptionServiceImpl extends JyExceptionStrategy implements JyScrappedExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyScrappedExceptionServiceImpl.class);

    @Autowired
    private PositionQueryJsfManager positionQueryJsfManager;
    @Autowired
    private BaseMajorManager baseMajorManager;


    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClient;
    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;
    @Autowired
    private JyExceptionService jyExceptionService;
    @Autowired
    private JyExceptionScrappedDao jyExceptionScrappedDao;
    @Autowired
    private JyBizTaskExceptionLogService jyBizTaskExceptionLogService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.uploadScan", mState = {JProEnum.TP})
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source, BaseStaffSiteOrgDto baseStaffByErp, String bizId) {

        if(!WaybillUtil.isPackageCode(req.getBarCode()) || !WaybillUtil.isWaybillCode(req.getBarCode()) ){
            return JdCResponse.fail("请扫描异常包裹的三无码或运单号!");
        }
        String waybillCode =WaybillUtil.getWaybillCode(req.getBarCode());
        String existKey = "DMS.SCRAPPED.UPLOAD_SCAN:" + bizId;
        if (!redisClient.set(existKey, "1", 10, TimeUnit.SECONDS, false)) {
            return JdCResponse.fail("该异常上报正在提交,请稍后再试!");
        }
        try {
            JyBizTaskExceptionEntity byBizId = jyBizTaskExceptionDao.findByBizId(bizId);
            if (byBizId != null) {
                return JdCResponse.fail("该异常已上报!");
            }
            JyBizTaskExceptionEntity taskEntity = new JyBizTaskExceptionEntity();
            taskEntity.setBizId(bizId);
            taskEntity.setType(JyBizTaskExceptionTypeEnum.SCRAPPED.getCode());
            taskEntity.setSource(source.getCode());
            taskEntity.setBarCode(waybillCode);
            taskEntity.setTags(JyBizTaskExceptionTagEnum.SCRAPPED.getCode());

            taskEntity.setSiteCode(new Long(position.getSiteCode()));
            taskEntity.setSiteName(position.getSiteName());
            taskEntity.setFloor(position.getFloor());
            taskEntity.setAreaCode(position.getAreaCode());
            taskEntity.setAreaName(position.getAreaName());
            taskEntity.setGridCode(position.getGridCode());
            taskEntity.setGridNo(position.getGridNo());

            taskEntity.setStatus(JyExpStatusEnum.TO_PICK.getCode());
            //taskEntity.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.PENDING_ENTRY.getCode());
            taskEntity.setCreateUserErp(req.getUserErp());
            taskEntity.setCreateUserName(baseStaffByErp.getStaffName());
            taskEntity.setCreateTime(new Date());
            taskEntity.setTimeOut(JyBizTaskExceptionTimeOutEnum.UN_TIMEOUT.getCode());
            taskEntity.setYn(1);

            JyExceptionScrappedPO scrappedPo = new JyExceptionScrappedPO();
            scrappedPo.setBizId(bizId);
            scrappedPo.setWaybillCode(waybillCode);
            scrappedPo.setSiteCode(position.getSiteCode());
            scrappedPo.setSiteName(position.getSiteName());
            scrappedPo.setCreateErp(req.getUserErp());
            scrappedPo.setCreateTime(new Date());
            try {
                jyBizTaskExceptionDao.insertSelective(taskEntity);
                jyExceptionScrappedDao.insertSelective(scrappedPo);
                jyExceptionService.recordLog(JyBizTaskExceptionCycleTypeEnum.UPLOAD, taskEntity);
            } catch (Exception e) {
                logger.error("写入报废提报数据出错了,request=" + JSON.toJSONString(req), e);
                return JdCResponse.fail("异常提报数据保存出错了,请稍后重试！");
            }

        }finally {
            redisClient.del(existKey);
        }
        return JdCResponse.ok();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.getJyExceptionScrappedTypeList", mState = {JProEnum.TP})
    public JdCResponse<List<JyExceptionScrappedTypeDto>> getJyExceptionScrappedTypeList() {
        JdCResponse<List<JyExceptionScrappedTypeDto>> result = new JdCResponse<>();
        List<JyExceptionScrappedTypeDto> dtoList = new ArrayList<>();
        try{
            JyExceptionScrappedTypeEnum[] types = JyExceptionScrappedTypeEnum.values();
            for (JyExceptionScrappedTypeEnum type:types) {
                JyExceptionScrappedTypeDto dto = new JyExceptionScrappedTypeDto();
                dto.setScrappedTypCode(type.getCode());
                dto.setScrappedTypeName(type.getName());
                dtoList.add(dto);
            }
        }catch (Exception e){
            logger.error("获取报废异常列表接口异常-{}",e.getMessage(),e);
            return JdCResponse.fail("获取报废异常列表接口异常!");
        }
        result.setData(dtoList);
        result.toSucceed("请求成功!");
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.processTaskOfscrapped", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfscrapped(ExpScrappedDetailReq req) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("岗位码有误!");
        }
        try{
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

            JyExceptionScrappedPO po = new JyExceptionScrappedPO();
            po.setBizId(req.getBizId());
            po.setExceptionType(req.getScrappedTypCode());
            po.setGoodsImageUrl(req.getGoodsImageUrl());
            po.setCertifyImageUrl(req.getCertifyImageUrl());
            po.setUpdateErp(req.getUserErp());
            po.setUpdateTime(new Date());
            jyExceptionScrappedDao.updateByBizId(po);

            //修改状态为处理中、审批中
            JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
            update.setBizId(req.getBizId());
            update.setStatus(JyExpStatusEnum.PROCESSING.getCode());
            update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.APPROVEING.getCode());
            update.setUpdateUserErp(req.getUserErp());
            update.setUpdateUserName(baseStaffByErp.getStaffName());
            update.setUpdateTime(new Date());
            jyBizTaskExceptionDao.updateByBizId(update);
            jyBizTaskExceptionLogService.recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS,update);
        }catch (Exception e){
            logger.error("报废处理任务接口异常-{}",e.getMessage(),e);
            return JdCResponse.fail("报废处理任务接口异常!");
        }
        response.toSucceed("请求成功");
        response.setData(Boolean.TRUE);
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.getTaskDetailOfscrapped", mState = {JProEnum.TP})
    public JdCResponse<ExpScrappedDetailDto> getTaskDetailOfscrapped(ExpTaskByIdReq req) {
        if (StringUtils.isBlank(req.getBizId())) {
            return JdCResponse.fail("业务ID不能为空!");
        }
        try{
            JyBizTaskExceptionEntity entity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
            if (entity == null) {
                return JdCResponse.fail("当前任务不存在!");
            }
            JyExceptionScrappedPO PO = jyExceptionScrappedDao.selectOneByBizId(req.getBizId());
            if(PO == null){
                return JdCResponse.fail("当前报废信息不存在!");
            }
            ExpScrappedDetailDto dto = coverToScrappedDetailDto(PO);
            return JdCResponse.ok(dto);
        }catch (Exception e){
            logger.error("获取报废详情接口异常-{}",e.getMessage(),e);
            return JdCResponse.fail("获取报废详情异常!");
        }
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

    private ExpScrappedDetailDto coverToScrappedDetailDto(JyExceptionScrappedPO po){
        ExpScrappedDetailDto dto = new ExpScrappedDetailDto();
        dto.setBizId(dto.getBizId());
        dto.setSiteId(po.getSiteCode());
        dto.setScrappedTypCode(po.getExceptionType());
        dto.setGoodsImageUrl(po.getGoodsImageUrl());
        dto.setCertifyImageUrl(po.getCertifyImageUrl());
        dto.setFirstChecker(po.getFirstChecker());
        dto.setFirstCheckStatus(po.getFirstCheckStatus());
        dto.setFirstCheckTime(po.getFirstCheckTime());
        dto.setSecondChecker(po.getSecondChecker());
        dto.setSecondCheckStatus(po.getSecondCheckStatus());
        dto.setSecondCheckTime(po.getSecondCheckTime());
        dto.setThirdChecker(po.getThirdChecker());
        dto.setThirdCheckStatus(po.getThirdCheckStatus());
        dto.setThirdCheckTime(po.getThirdCheckTime());
        return dto;
    }
}
