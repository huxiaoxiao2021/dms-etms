package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionScrappedDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.manager.ExpInfoSummaryJsfManager;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionServiceStrategy;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("jyScrappedExceptionService")
public class JyScrappedExceptionServiceImpl implements JyExceptionServiceStrategy {

    private final Logger logger = LoggerFactory.getLogger(JyScrappedExceptionServiceImpl.class);


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

    @Override
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
            taskEntity.setBarCode(req.getBarCode());
            taskEntity.setTags(JyBizTaskExceptionTagEnum.SCRAPPED.getCode());

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

            JyExceptionScrappedPO scrappedPo = new JyExceptionScrappedPO();
            scrappedPo.setBizId(bizId);
            scrappedPo.setWaybillCode(req.getBarCode());
            scrappedPo.setSiteCode(position.getSiteCode());
            scrappedPo.setSiteName(position.getSiteName());
            scrappedPo.setCreateErp(req.getUserErp());
            scrappedPo.setCreateTime(new Date());
            try {
                jyBizTaskExceptionDao.insertSelective(taskEntity);
                jyExceptionScrappedDao.insertSelective(scrappedPo);
                jyExceptionService.recordLog(JyBizTaskExceptionCycleTypeEnum.UPLOAD, taskEntity);
            } catch (Exception e) {
                logger.error("写入异常提报数据出错了,request=" + JSON.toJSONString(req), e);
                return JdCResponse.fail("异常提报数据保存出错了,请稍后重试！");
            }

        }finally {
            redisClient.del(existKey);
        }
        return JdCResponse.ok();
    }


    public JdCResponse<List<JyExceptionScrappedTypeDto>> getJyExceptionScrappedTypeList() {
        JdCResponse<List<JyExceptionScrappedTypeDto>> result = new JdCResponse<>();
        List<JyExceptionScrappedTypeDto> dtoList = new ArrayList<>();
        JyExceptionScrappedTypeEnum[] types = JyExceptionScrappedTypeEnum.values();
        for (JyExceptionScrappedTypeEnum type:types) {
            JyExceptionScrappedTypeDto dto = new JyExceptionScrappedTypeDto();
            dto.setScrappedTypCode(type.getCode());
            dto.setScrappedTypeName(type.getName());
            dtoList.add(dto);
        }
        result.setData(dtoList);
        result.toSucceed("请求成功!");
        return result;
    }

    /**
     * 通用异常上报入口-扫描
     *
     */




}
