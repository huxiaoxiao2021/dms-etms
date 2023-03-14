package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTaskByIdReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpScrappedDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionScrappedTypeDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.core.base.HrUserManager;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionScrappedDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionScrappedPO;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyBizTaskExceptionLogService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.jim.cli.Cluster;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.lsb.flow.domain.jme.JmeFile;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private HrUserManager hrUserManager;
    
    @Autowired
    private FlowServiceManager flowServiceManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

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
            update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.APPROVING.getCode());
            update.setUpdateUserErp(req.getUserErp());
            update.setUpdateUserName(baseStaffByErp.getStaffName());
            update.setUpdateTime(new Date());
            jyBizTaskExceptionDao.updateByBizId(update);
            // 审批处理
            dealApprove(req);
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
    public void dealApproveTest(ExpScrappedDetailReq req) {
        dealApprove(req);
    }

    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void dealApproveResult(HistoryApprove historyApprove) {
        // 业务bizId（在审批提交时传入的工单号就是业务bizId）
        String bizId = historyApprove.getProcessInstanceNo();
        // 审批状态
        Integer state = historyApprove.getState();
        // 审批人ERP
        String approveErp = historyApprove.getApprover();
        // 当前审批节点编码
        String nodeName = historyApprove.getNodeName();
        // 审批结果状态
        int approveStatus = Objects.equals(state, ApprovalResult.AGREE.getValue()) ? JyApproveStatusEnum.PASS.getCode()
                : Objects.equals(state, ApprovalResult.REJECT.getValue()) ? JyApproveStatusEnum.REJECT.getCode()
                : JyApproveStatusEnum.UNKNOWN.getCode();
        
        switch (JyExScrapApproveStageEnum.convertApproveEnum(nodeName)){
            case FIRST:
                logger.info("生鲜报废工单号:{}的一级审批结果:{}", bizId, historyApprove.getState());
                updateApproveResult(bizId, approveStatus, approveErp, JyExScrapApproveStageEnum.FIRST.getCode());
                break;
            case SECOND:
                logger.info("生鲜报废工单号:{}的二级审批结果:{}", bizId, historyApprove.getState());
                updateApproveResult(bizId, approveStatus, approveErp, JyExScrapApproveStageEnum.SECOND.getCode());
                break;
            case THIRD:
                logger.info("生鲜报废工单号:{}的三级审批结果:{}", bizId, historyApprove.getState());
                updateApproveResult(bizId, approveStatus, approveErp, JyExScrapApproveStageEnum.THIRD.getCode());
                break;
            default:
                logger.warn("未知节点编码:{}", bizId);
        }
    }

    /**
     * 更新审批结果
     *
     * @param bizId 业务bizId
     * @param approveStatus 审批状态
     * @param approveErp 审批人
     * @param approveStage 审批阶段
     */
    private void updateApproveResult(String bizId, int approveStatus, String approveErp, int approveStage) {
        if(Objects.equals(approveStatus, JyApproveStatusEnum.UNKNOWN.getCode())){
            logger.warn("生效报废审批结果未知!");
            return;
        }
        // 更新生鲜报废明细表数据
        JyExceptionScrappedPO po = new JyExceptionScrappedPO();
        po.setBizId(bizId);
        Date now = new Date();
        po.setUpdateTime(now);
        if(Objects.equals(JyExScrapApproveStageEnum.FIRST.getCode(), approveStage)){
            po.setFirstChecker(approveErp);
            po.setFirstCheckStatus(approveStatus);
            po.setFirstCheckTime(now);
        }
        if(Objects.equals(JyExScrapApproveStageEnum.SECOND.getCode(), approveStage)){
            po.setSecondChecker(approveErp);
            po.setSecondCheckStatus(approveStatus);
            po.setSecondCheckTime(now);
        }
        if(Objects.equals(JyExScrapApproveStageEnum.THIRD.getCode(), approveStage)){
            po.setThirdChecker(approveErp);
            po.setThirdCheckStatus(approveStatus);
            po.setThirdCheckTime(now);
        }
        jyExceptionScrappedDao.updateByBizId(po);
        
        // 更新异常任务表数据
        JyBizTaskExceptionEntity entity = new JyBizTaskExceptionEntity();
        entity.setBizId(bizId);
        int processStatus = Objects.equals(JyApproveStatusEnum.PASS.getCode(), approveStatus) 
                ? JyBizTaskExceptionProcessStatusEnum.APPROVE_PASS.getCode() : JyBizTaskExceptionProcessStatusEnum.APPROVE_REJECT.getCode();
        entity.setProcessingStatus(processStatus);
        entity.setUpdateTime(now);
        jyBizTaskExceptionDao.updateByBizId(entity);
    }

    /**
     * 审批处理
     * 
     * @param req
     */
    private void dealApprove(ExpScrappedDetailReq req) {
        JyBizTaskExceptionEntity entity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if(entity == null || StringUtils.isEmpty(entity.getCreateUserErp())){
            logger.warn("未查询到:{}的报废任务!", req.getBizId());
            return;
        }
        // 提交审批
        String approveOrderCode = flowServiceManager.startFlow(
                buildOA(req, entity), // OA数据
                buildBusiness(entity), // 业务数据
                buildFlow(entity), // 流程数据
                FlowConstants.FLOW_CODE_FRESH_SCRAP, entity.getHandlerErp(), entity.getBizId());
        if(logger.isInfoEnabled()){
            logger.info("提交审批完成，审批工单号:{}", approveOrderCode);
        }
    }

    /**
     * 流程数据
     * 
     * @param entity
     * @return
     */
    private Map<String, Object> buildFlow(JyBizTaskExceptionEntity entity) {
        Map<String, Object> flowControlMap = Maps.newHashMap();
        flowControlMap.put("firstTriggerErp", entity.getHandlerErp());
        int approveCount;
        // 查询场地当月报废数量
        entity.setProcessBeginTime(DateHelper.getFirstDateOfMonth());
        Integer count = jyBizTaskExceptionDao.queryScrapCountByCondition(entity);
        // 报废审批级别数量阈值，默认：50,100
        String exScrapApproveLevelCountLimit = uccPropertyConfiguration.getExScrapApproveLevelCountLimit();
        String[] split = exScrapApproveLevelCountLimit.split(Constants.SEPARATOR_COMMA);
        if(count <= Integer.parseInt(split[0])){
            approveCount = JyExScrapApproveStageEnum.FIRST.getCount();
        }else{
            approveCount = JyExScrapApproveStageEnum.SECOND.getCount();
            String superiorErp = hrUserManager.getSuperiorErp(entity.getHandlerErp());
            flowControlMap.put("secondTriggerErp", superiorErp);
            if(count > Integer.parseInt(split[1])){
                approveCount = JyExScrapApproveStageEnum.THIRD.getCount();
                superiorErp = hrUserManager.getSuperiorErp(superiorErp);
                flowControlMap.put("thirdTriggerErp", superiorErp);
            }
        }
        flowControlMap.put("approveCount", approveCount);
        return flowControlMap;
    }

    /**
     * 业务数据
     * 
     * @param entity
     * @return
     */
    private Map<String, Object> buildBusiness(JyBizTaskExceptionEntity entity) {
        Map<String,Object> businessMap = Maps.newHashMap();
        // 设置业务唯一编码
        businessMap.put(FlowConstants.FLOW_BUSINESS_NO_KEY, entity.getBizId());
        return businessMap;
    }

    /**
     * oa数据
     * 
     * @param req
     * @param entity
     * @return
     */
    private Map<String, Object> buildOA(ExpScrappedDetailReq req, JyBizTaskExceptionEntity entity) {
        Map<String, Object> oaMap = Maps.newHashMap();
        oaMap.put(FlowConstants.FLOW_OA_JMEREQNAME, FlowConstants.FLOW_FLOW_WORK_THEME_FRESH_SCRAP);
        oaMap.put(FlowConstants.FLOW_OA_JMEREQCOMMENTS, FlowConstants.FLOW_FLOW_WORK_REMARK_FRESH_SCRAP);
        List<String> mainColList = new ArrayList<>();
        oaMap.put(FlowConstants.FLOW_OA_JMEMAINCOLLIST, mainColList);
        mainColList.add("生鲜报废单号:" + entity.getBarCode());
        mainColList.add("生鲜报废场地:" + entity.getSiteName());
        mainColList.add("生鲜报废提交人:" + entity.getHandlerErp());
        mainColList.add("生鲜报废网格:" + entity.getGridCode());
        List<JmeFile> annexList = Lists.newArrayList();
        oaMap.put(FlowConstants.FLOW_OA_ANNEX, annexList);
        if(StringUtils.isNotEmpty(req.getGoodsImageUrl())){
            int index = 0;
            for (String goodsImageUrl : req.getGoodsImageUrl().split(Constants.SEPARATOR_COMMA)) {
                index ++;
                JmeFile jmeFile = new JmeFile();
                jmeFile.setFileName("物品照片" + index);
                jmeFile.setFileUrl(goodsImageUrl);
                annexList.add(jmeFile);
            }
        }
        if(StringUtils.isNotEmpty(req.getCertifyImageUrl())){
            int index = 0;
            for (String certifyImageUrl : req.getCertifyImageUrl().split(Constants.SEPARATOR_COMMA)) {
                index ++;
                JmeFile jmeFile = new JmeFile();
                jmeFile.setFileName("证明照片" + index);
                jmeFile.setFileUrl(certifyImageUrl);
                annexList.add(jmeFile);
            }
        }
        return oaMap;
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
