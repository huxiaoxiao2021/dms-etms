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
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionScrappedDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.dto.exception.JyExpTaskMessage;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.manager.ExpInfoSummaryJsfManager;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
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

@Service("jyScrappedExceptionService")
public class JyScrappedExceptionServiceImpl implements JyScrappedExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyScrappedExceptionServiceImpl.class);
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
    private JyExceptionService jyExceptionService;
    @Autowired
    private JyExceptionScrappedDao jyExceptionScrappedDao;

    @Override
    public JdCResponse<Object> uploadScanofScrapped(ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source, BaseStaffSiteOrgDto baseStaffByErp, String bizId) {

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

    @Override
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
