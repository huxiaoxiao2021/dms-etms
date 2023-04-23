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
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionLogDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionScrappedDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionLogEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExNoticeCustomerMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionScrappedPO;
import com.jd.bluedragon.distribution.jy.manager.AbnormalReasonOfZKManager;
import com.jd.bluedragon.distribution.jy.manager.PositionQueryJsfManager;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ASCPContants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.lsb.flow.domain.jme.JmeFile;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wl.cs.abnormal.portal.api.dto.reason.AbnormalReasonDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("jyScrappedExceptionService")
public class JyScrappedExceptionServiceImpl extends JyExceptionStrategy implements JyScrappedExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyScrappedExceptionServiceImpl.class);

    private final Integer REASON_LEVEL_CODE_ONE = 20009;
    private final Integer REASON_LEVEL_CODE_TWO = 232300;
    private final Integer REASON_LEVEL_CODE_THREE = 232301;

    @Autowired
    private PositionQueryJsfManager positionQueryJsfManager;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private JyExceptionScrappedDao jyExceptionScrappedDao;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private HrUserManager hrUserManager;

    @Autowired
    private FlowServiceManager flowServiceManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    @Qualifier("dmsScrapNoticeKFProducer")
    private DefaultJMQProducer dmsScrapNoticeKFProducer;

    @Autowired
    @Qualifier("scrapApproveProducer")
    private DefaultJMQProducer scrapApproveProducer;

    @Autowired
    private JyBizTaskExceptionLogDao jyBizTaskExceptionLogDao;

    @Autowired
    @Qualifier("abnormalReasonManagerOfZK")
    private AbnormalReasonOfZKManager abnormalReasonManagerOfZK;

    @Override
    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.SCRAPPED.getCode();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.uploadScan", mState = {JProEnum.TP})
    public JdCResponse<Object> uploadScan(JyBizTaskExceptionEntity taskEntity,ExpUploadScanReq req, PositionDetailRecord position
            , JyExpSourceEnum source, String bizId) {

        logger.info("报废上报信息req-{} 岗位码信息position-{} bizId-{}", JSON.toJSONString(req), JSON.toJSONString(position), bizId);
        if (!checkBarCode(req.getBarCode())) {
            return JdCResponse.fail("请扫描正确的运单号或包裹号!");
        }
        String waybillCode = WaybillUtil.getWaybillCode(req.getBarCode());
        //校验生鲜单号 自营OR外单
        String msg = checkFresh(waybillCode);
        if (StringUtils.isNotBlank(msg)) {
            return JdCResponse.fail(msg);
        }
        taskEntity.setBarCode(waybillCode);
        taskEntity.setType(JyBizTaskExceptionTypeEnum.SCRAPPED.getCode());

        JyExceptionScrappedPO scrappedPo = new JyExceptionScrappedPO();
        scrappedPo.setBizId(bizId);
        scrappedPo.setWaybillCode(waybillCode);
        scrappedPo.setExceptionType(JyExceptionScrappedTypeEnum.SCRAPPED_FRESH.getCode());
        scrappedPo.setSiteCode(position.getSiteCode());
        scrappedPo.setSiteName(position.getSiteName());
        scrappedPo.setCreateErp(req.getUserErp());
        scrappedPo.setCreateTime(new Date());
        logger.info("写入生鲜报废异常提报-taskEntity-{} -expEntity-{}",
                JSON.toJSONString(scrappedPo));
        jyExceptionScrappedDao.insertSelective(scrappedPo);

        return JdCResponse.ok();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.getJyExceptionScrappedTypeList", mState = {JProEnum.TP})
    public JdCResponse<List<JyExceptionScrappedTypeDto>> getJyExceptionScrappedTypeList() {
        JdCResponse<List<JyExceptionScrappedTypeDto>> result = new JdCResponse<>();
        List<JyExceptionScrappedTypeDto> dtoList = new ArrayList<>();
        try {
            JyExceptionScrappedTypeEnum[] types = JyExceptionScrappedTypeEnum.values();
            for (JyExceptionScrappedTypeEnum type : types) {
                JyExceptionScrappedTypeDto dto = new JyExceptionScrappedTypeDto();
                dto.setScrappedTypCode(type.getCode());
                dto.setScrappedTypeName(type.getName());
                dtoList.add(dto);
            }
        } catch (Exception e) {
            logger.error("获取报废异常列表接口异常-{}", e.getMessage(), e);
            return JdCResponse.fail("获取报废异常列表接口异常!");
        }
        result.setData(dtoList);
        result.toSucceed("请求成功!");
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.processTaskOfscrapped", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfscrapped(ExpScrappedDetailReq req) {
        logger.info("任务处理processTaskOfscrapped-{}",JSON.toJSONString(req));
        JdCResponse<Boolean> response = new JdCResponse<>();
        PositionDetailRecord position = getPosition(req.getPositionCode());
        if (position == null) {
            return JdCResponse.fail("岗位码有误!");
        }
        if(req.getSaveType() == null){
            return JdCResponse.fail("保存状态有误!");
        }
        try {
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
            po.setSaveType(req.getSaveType());
            if (JyExpSaveTypeEnum.SAVE.getCode().equals(req.getSaveType())) {
                po.setSubmitTime(new Date());
            }
            //暂存的话，直接返回
            if(JyExpSaveTypeEnum.TEMP_SAVE.getCode().equals(req.getSaveType())){
                logger.info("报废业务数据暂存更新数据--{}",JSON.toJSONString(po));
                jyExceptionScrappedDao.updateByBizId(po);
                return JdCResponse.ok();
            }
            logger.info("报废业务数据提交数据--{}",JSON.toJSONString(po));
            jyExceptionScrappedDao.updateByBizId(po);
            //修改状态为处理中、审批中
            JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
            update.setBizId(req.getBizId());
            update.setStatus(JyExpStatusEnum.PROCESSING.getCode());
            update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.APPROVING.getCode());
            update.setUpdateUserErp(req.getUserErp());
            update.setUpdateUserName(baseStaffByErp.getStaffName());
            update.setUpdateTime(new Date());
            logger.info("报废任务更新数据--{}",JSON.toJSONString(update));
            jyBizTaskExceptionDao.updateByBizId(update);
            // 异步处理审批任务
            syncSendApproveTask(req);
            recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_SUBMIT, update);
        } catch (Exception e) {
            logger.error("报废处理任务接口异常-{}", e.getMessage(), e);
            return JdCResponse.fail("报废处理任务接口异常!");
        }
        response.toSucceed("请求成功");
        response.setData(Boolean.TRUE);
        return response;
    }

    private void syncSendApproveTask(ExpScrappedDetailReq req) {
        if(logger.isInfoEnabled()){
            logger.info("生鲜报废单:{}的审批异步消息发送!", req.getBizId());
        }
        scrapApproveProducer.sendOnFailPersistent(req.getBizId(), JsonHelper.toJson(req));
    }

    @Override
    public void dealApproveResult(HistoryApprove historyApprove) {
        // 审批工单号
        String processInstanceNo = historyApprove.getProcessInstanceNo();
        // 审批人ERP
        String approveErp = historyApprove.getApprover();
        // 当前审批节点编码
        String nodeName = historyApprove.getNodeName();
        // 当前节点的审批结果状态
        int approveStatus = Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue()) ? JyApproveStatusEnum.PASS.getCode()
                : Objects.equals(historyApprove.getState(), ApprovalResult.REJECT.getValue()) ? JyApproveStatusEnum.REJECT.getCode()
                : JyApproveStatusEnum.UNKNOWN.getCode();
        if(Objects.equals(approveStatus, JyApproveStatusEnum.UNKNOWN.getCode())){
            logger.warn("当前节点:{}的审批状态为:{}不合法!", historyApprove.getNodeName(), historyApprove.getState());
            return;
        }

        // 查询审批数据:key-审批次数approveCount,value-业务主键bizId
        ImmutablePair<Integer, String> pairResult = queryApproveData(processInstanceNo);
        if(pairResult == null){
            logger.warn("根据审批工单号:{}未查询到审批工单详情!", processInstanceNo);
            return;
        }
        // 审批次数
        Integer approveCount = pairResult.left;
        // 生效报废业务主键bizId
        String bizId = pairResult.right;

        // 审批流程是否完结标识
        // 1、任一节点驳回则审批流程结束 
        // 2、多个节点的审批则最终节点的审批通过为流程结束
        boolean flowEndFlag = Objects.equals(historyApprove.getState(), ApprovalResult.REJECT.getValue());;
        // 审批流程最终结果:true-审批通过,false-审批驳回
        boolean approveFinalResult = Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
        
        switch (JyExScrapApproveStageEnum.convertApproveEnum(nodeName)) {
            case FIRST:
                logger.info("生鲜报废工单号:{}的一级审批结果:{}", bizId, historyApprove.getState());
                approveFinalResult = approveFinalResult && Objects.equals(approveCount, JyExScrapApproveStageEnum.FIRST.getCount()) 
                        && Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
                flowEndFlag = flowEndFlag || Objects.equals(approveCount, JyExScrapApproveStageEnum.FIRST.getCount());
                updateApproveResult(bizId, approveStatus, approveErp, JyExScrapApproveStageEnum.FIRST.getCode());
                break;
            case SECOND:
                logger.info("生鲜报废工单号:{}的二级审批结果:{}", bizId, historyApprove.getState());
                approveFinalResult = approveFinalResult && Objects.equals(approveCount, JyExScrapApproveStageEnum.SECOND.getCount())
                        && Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
                flowEndFlag = flowEndFlag || Objects.equals(approveCount, JyExScrapApproveStageEnum.SECOND.getCount());
                updateApproveResult(bizId, approveStatus, approveErp, JyExScrapApproveStageEnum.SECOND.getCode());
                break;
            case THIRD:
                logger.info("生鲜报废工单号:{}的三级审批结果:{}", bizId, historyApprove.getState());
                approveFinalResult = approveFinalResult && Objects.equals(approveCount, JyExScrapApproveStageEnum.THIRD.getCount())
                        && Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
                flowEndFlag = flowEndFlag || Objects.equals(approveCount, JyExScrapApproveStageEnum.THIRD.getCount());
                updateApproveResult(bizId, approveStatus, approveErp, JyExScrapApproveStageEnum.THIRD.getCode());
                break;
            default:
                logger.warn("未知节点编码:{}", bizId);
                return;
        }
        // 更新异常任务表审批状态
        updateTaskApproveResult(bizId, flowEndFlag, approveFinalResult);
        // 审批通过异步通知客服
        noticeKF(bizId, approveFinalResult);
    }

    private void noticeKF(String bizId, boolean approveFinalResult) {
        if(approveFinalResult){
            JyBizTaskExceptionEntity exScrapTaskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
            if(exScrapTaskEntity == null){
                logger.warn("根据业务主键:{}未查询到异常任务明细!", bizId);
                return;
            }
            JyExceptionScrappedPO exScrapEntity = jyExceptionScrappedDao.selectOneByBizId(bizId);
            if(exScrapEntity == null){
                logger.warn("根据业务主键:{}未查询到生鲜报废明细!", bizId);
                return;
            }
            JyExNoticeCustomerMQ jyExNoticeCustomerMQ = new JyExNoticeCustomerMQ();

            jyExNoticeCustomerMQ.setSourceSystemCode(ASCPContants.SOURCE_SYSTEM_CODE);
            jyExNoticeCustomerMQ.setDealType(ASCPContants.DEAL_TYPE);
            jyExNoticeCustomerMQ.setBusinessId(ASCPContants.BUSINESS_ID);
            jyExNoticeCustomerMQ.setCodeInfo(exScrapTaskEntity.getBarCode());
            jyExNoticeCustomerMQ.setWaybillCode(exScrapTaskEntity.getBarCode());
            jyExNoticeCustomerMQ.setCodeType(ASCPContants.CODE_TYPE);
            jyExNoticeCustomerMQ.setDeptCode(Objects.nonNull(exScrapTaskEntity.getSiteCode())?exScrapTaskEntity.getSiteCode().toString():"");
            jyExNoticeCustomerMQ.setDeptName(exScrapTaskEntity.getSiteName());
            jyExNoticeCustomerMQ.setExptCreateTime(DateHelper.formatDateTime(exScrapTaskEntity.getCreateTime()));
            jyExNoticeCustomerMQ.setExptCreator(exScrapTaskEntity.getCreateUserErp());
            jyExNoticeCustomerMQ.setExptId(exScrapTaskEntity.getBizId());
            jyExNoticeCustomerMQ.setAttachmentAddr(exScrapEntity.getGoodsImageUrl());
            addAbnormalReason(jyExNoticeCustomerMQ);
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(exScrapTaskEntity.getSiteCode().intValue());
            if(baseSite != null){
                jyExNoticeCustomerMQ.setStartOrgCode(baseSite.getOrgId().toString());
                jyExNoticeCustomerMQ.setStartOrgName(baseSite.getOrgName());
                jyExNoticeCustomerMQ.setProvinceAgencyCode(baseSite.getProvinceId()!= null? baseSite.getProvinceId().toString():"");
                jyExNoticeCustomerMQ.setProvinceAgencyName(StringUtils.isNotBlank(baseSite.getProvinceName())?baseSite.getProvinceName():"");
            }
            logger.info("生鲜报废单号:{}审批通过,异步通知客服系! 消息体-{}", bizId,JsonHelper.toJson(jyExNoticeCustomerMQ));
            dmsScrapNoticeKFProducer.sendOnFailPersistent(bizId, JsonHelper.toJson(jyExNoticeCustomerMQ));
            // add log
            recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_CUSTOMER, exScrapTaskEntity);
        }
    }

    /**
     * 补充异常原因
     * @param mq
     */
    private void  addAbnormalReason(JyExNoticeCustomerMQ mq){
        List<AbnormalReasonDto> abnormalReasonDtos = abnormalReasonManagerOfZK.queryAbnormalReasonListBySystemCode();
        if(CollectionUtils.isEmpty(abnormalReasonDtos)){
            logger.warn("质控异常原因为空");
            return ;
        }
        for (AbnormalReasonDto dto:abnormalReasonDtos) {
            if(dto.getCode().equals(REASON_LEVEL_CODE_ONE)){
                mq.setExptOneLevel(dto.getReasonLevel().toString());
                mq.setExptOneLevelName(dto.getName());
            }else if(dto.getCode().equals(REASON_LEVEL_CODE_TWO)){
                mq.setExptTwoLevel(dto.getReasonLevel().toString());
                mq.setExptTwoLevelName(dto.getName());
            }else if(dto.getCode().equals(REASON_LEVEL_CODE_THREE)){
                mq.setExptThreeLevel(dto.getReasonLevel().toString());
                mq.setExptThreeLevelName(dto.getName());
            }else {
                logger.warn("未知类型");
            }
        }


    }

    private void updateTaskApproveResult(String bizId, boolean flowEndFlag, boolean approveFinalResult) {
        if(!flowEndFlag){
            return;
        }
        JyBizTaskExceptionEntity entity = new JyBizTaskExceptionEntity();
        entity.setBizId(bizId);
        int processStatus = approveFinalResult
                ? JyBizTaskExceptionProcessStatusEnum.APPROVE_PASS.getCode() : JyBizTaskExceptionProcessStatusEnum.APPROVE_REJECT.getCode();
        entity.setProcessingStatus(processStatus);
        entity.setStatus(JyExpStatusEnum.COMPLETE.getCode());
        entity.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(entity);
    }

    private ImmutablePair<Integer, String> queryApproveData(String processInstanceNo) {
        ApproveRequestOrder approveRequestOrder = flowServiceManager.getRequestOrder(processInstanceNo);
        if(approveRequestOrder == null || approveRequestOrder.getArgs() == null){
            logger.warn("根据审批工单号{}未查询到生鲜报废的审批流程!", processInstanceNo);
            return null;
        }
        Map<String, Object> argsMap = approveRequestOrder.getArgs();
        Map<String, Object> flowControlMap = JsonHelper.json2MapNormal(JsonHelper.toJson(argsMap.get(FlowConstants.FLOW_DATA_MAP_KEY_FLOW_CONTROL)));
        if(flowControlMap == null){
            logger.warn("根据申请单号{}未查询到设置的流程对象!", processInstanceNo);
            return null;
        }
        Map<String, Object> businessMap = JsonHelper.json2MapNormal(JsonHelper.toJson(argsMap.get(FlowConstants.FLOW_DATA_MAP_KEY_BUSINESS_DATA)));
        if(businessMap == null){
            logger.warn("根据申请单号{}未查询到设置的业务数据对象!", processInstanceNo);
            return null;
        }
        return ImmutablePair.of(Integer.valueOf(String.valueOf(flowControlMap.get(FlowConstants.FLOW_DATA_MAP_SCRAP_COUNT))),
                String.valueOf(businessMap.get(FlowConstants.FLOW_BUSINESS_NO_KEY)));
    }

    /**
     * 更新异常报废表数据
     *
     * @param bizId         业务bizId
     * @param approveStatus 审批状态
     * @param approveErp    审批人
     * @param approveStage  审批阶段
     */
    private void updateApproveResult(String bizId, int approveStatus, String approveErp, int approveStage) {
        if (Objects.equals(approveStatus, JyApproveStatusEnum.UNKNOWN.getCode())) {
            logger.warn("生效报废审批结果未知!");
            return;
        }
        // 更新生鲜报废明细表数据
        JyExceptionScrappedPO po = new JyExceptionScrappedPO();
        po.setBizId(bizId);
        Date now = new Date();
        po.setUpdateTime(now);
        if (Objects.equals(JyExScrapApproveStageEnum.FIRST.getCode(), approveStage)) {
            po.setFirstChecker(approveErp);
            po.setFirstCheckStatus(approveStatus);
            po.setFirstCheckTime(now);
        }
        if (Objects.equals(JyExScrapApproveStageEnum.SECOND.getCode(), approveStage)) {
            po.setSecondChecker(approveErp);
            po.setSecondCheckStatus(approveStatus);
            po.setSecondCheckTime(now);
        }
        if (Objects.equals(JyExScrapApproveStageEnum.THIRD.getCode(), approveStage)) {
            po.setThirdChecker(approveErp);
            po.setThirdCheckStatus(approveStatus);
            po.setThirdCheckTime(now);
        }
        jyExceptionScrappedDao.updateByBizId(po);
    }

    @Override
    public JdCResponse<List<ExpScrappedDetailDto>> getTaskListOfscrapped(List<String> bizIds) {
        if(CollectionUtils.isEmpty(bizIds)){
            return JdCResponse.fail("bizIds 不能为空!");
        }
        List<JyExceptionScrappedPO> list = jyExceptionScrappedDao.getTaskListOfscrapped(bizIds);
        if(CollectionUtils.isEmpty(list)){
            return JdCResponse.fail("获取报废列表数据为空!");
        }
        List<ExpScrappedDetailDto> dtos = new ArrayList<>();
        list.stream().forEach(item->{
            dtos.add(coverToScrappedDetailDto(item));
        });
        return JdCResponse.ok(dtos);
    }
    
    /**
     * 审批处理
     *
     * @param req
     */
    @Override
    public void dealApprove(ExpScrappedDetailReq req) {
        JyBizTaskExceptionEntity entity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (entity == null || StringUtils.isEmpty(entity.getCreateUserErp())) {
            logger.warn("未查询到:{}的报废任务!", req.getBizId());
            return;
        }
        // 提交审批
        Map<String, Object> oaMap = buildOA(req, entity); // OA数据
        Map<String, Object> businessMap = buildBusiness(entity); // 业务数据
        Map<String, Object> flowMap = buildFlow(entity); // 流程数据
        String approveOrderCode = flowServiceManager.startFlow(
                oaMap, businessMap, flowMap, FlowConstants.FLOW_CODE_FRESH_SCRAP, entity.getHandlerErp(), entity.getBizId());
        if (logger.isInfoEnabled()) {
            logger.info("提交审批完成，审批工单号:{}", approveOrderCode);
        }
        if(StringUtils.isNotEmpty(approveOrderCode)){
            // update scrap approve info
            updateScrapData(flowMap, entity.getBizId());
            // add log
            recordLog(JyBizTaskExceptionCycleTypeEnum.PROCESS_APPROVE, entity);
        }
    }

    private void updateScrapData(Map<String, Object> flowMap, String bizId) {
        JyExceptionScrappedPO entity = new JyExceptionScrappedPO();
        entity.setBizId(bizId);
        if(flowMap.get(FlowConstants.FLOW_DATA_MAP_FIRST_TRIGGER_ERP) != null){
            String firstTrigger = String.valueOf(flowMap.get(FlowConstants.FLOW_DATA_MAP_FIRST_TRIGGER_ERP));
            entity.setFirstChecker(hrUserManager.getSuperiorErp(firstTrigger));
            entity.setFirstCheckStatus(JyApproveStatusEnum.TODO.getCode());
        }
        if(flowMap.get(FlowConstants.FLOW_DATA_MAP_SECOND_TRIGGER_ERP) != null){
            String secondTrigger = String.valueOf(flowMap.get(FlowConstants.FLOW_DATA_MAP_SECOND_TRIGGER_ERP));
            entity.setSecondChecker(hrUserManager.getSuperiorErp(secondTrigger));
            entity.setSecondCheckStatus(JyApproveStatusEnum.TODO.getCode());
        }
        if(flowMap.get(FlowConstants.FLOW_DATA_MAP_THIRD_TRIGGER_ERP) != null){
            String thirdTrigger = String.valueOf(flowMap.get(FlowConstants.FLOW_DATA_MAP_THIRD_TRIGGER_ERP));
            entity.setThirdChecker(hrUserManager.getSuperiorErp(thirdTrigger));
            entity.setThirdCheckStatus(JyApproveStatusEnum.TODO.getCode());
        }
        jyExceptionScrappedDao.updateByBizId(entity);
    }

    /**
     * 流程数据
     *
     * @param entity
     * @return
     */
    private Map<String, Object> buildFlow(JyBizTaskExceptionEntity entity) {
        Map<String, Object> flowControlMap = Maps.newHashMap();
        // 查询场地当月报废数量
        entity.setProcessBeginTime(DateHelper.getFirstDateOfMonth());
        Integer count = jyBizTaskExceptionDao.queryScrapCountByCondition(entity);
        // 报废审批级别数量阈值，默认：50,100
        String exScrapApproveLevelCountLimit = uccPropertyConfiguration.getExScrapApproveLevelCountLimit();
        String[] split = exScrapApproveLevelCountLimit.split(Constants.SEPARATOR_COMMA);

        // 场地当月报废数量小于50走一级审批
        flowControlMap.put(FlowConstants.FLOW_DATA_MAP_FIRST_TRIGGER_ERP, entity.getHandlerErp());
        flowControlMap.put(FlowConstants.FLOW_DATA_MAP_SCRAP_COUNT, JyExScrapApproveStageEnum.FIRST.getCount());
        if(count <= Integer.parseInt(split[0])){
            return flowControlMap;
        }
        // 场地当月报废数量大于50小于100走二级审批
        String superiorErp = hrUserManager.getSuperiorErp(entity.getHandlerErp());
        if(StringUtils.isEmpty(superiorErp)){ // 上级领导不存在则走一级审批
            return flowControlMap;
        }
        flowControlMap.put(FlowConstants.FLOW_DATA_MAP_SECOND_TRIGGER_ERP, superiorErp);
        flowControlMap.put(FlowConstants.FLOW_DATA_MAP_SCRAP_COUNT, JyExScrapApproveStageEnum.SECOND.getCount());
        if(count <= Integer.parseInt(split[1])){
            return flowControlMap;
        }
        // 场地当月报废数量大于100走三级审批
        superiorErp = hrUserManager.getSuperiorErp(superiorErp);
        if(StringUtils.isEmpty(superiorErp)){ // 上级领导不存在则走二级审批
            return flowControlMap;
        }
        flowControlMap.put(FlowConstants.FLOW_DATA_MAP_THIRD_TRIGGER_ERP, superiorErp);
        flowControlMap.put(FlowConstants.FLOW_DATA_MAP_SCRAP_COUNT, JyExScrapApproveStageEnum.THIRD.getCount());
        return flowControlMap;
    }

    /**
     * 业务数据
     *
     * @param entity
     * @return
     */
    private Map<String, Object> buildBusiness(JyBizTaskExceptionEntity entity) {
        Map<String, Object> businessMap = Maps.newHashMap();
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
        if (StringUtils.isNotEmpty(req.getGoodsImageUrl())) {
            int index = 0;
            for (String goodsImageUrl : req.getGoodsImageUrl().split(Constants.SEPARATOR_SEMICOLON)) {
                index++;
                JmeFile jmeFile = new JmeFile();
                jmeFile.setFileName("物品照片" + index);
                jmeFile.setFileUrl(goodsImageUrl);
                annexList.add(jmeFile);
            }
        }
        if (StringUtils.isNotEmpty(req.getCertifyImageUrl())) {
            int index = 0;
            for (String certifyImageUrl : req.getCertifyImageUrl().split(Constants.SEPARATOR_SEMICOLON)) {
                index++;
                JmeFile jmeFile = new JmeFile();
                jmeFile.setFileName("证明照片" + index);
                jmeFile.setFileUrl(certifyImageUrl);
                annexList.add(jmeFile);
            }
        }
        return oaMap;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.getTaskDetailOfscrapped", mState = {JProEnum.TP})
    public JdCResponse<ExpScrappedDetailDto> getTaskDetailOfscrapped(ExpTaskByIdReq req) {
        if (StringUtils.isBlank(req.getBizId())) {
            return JdCResponse.fail("业务ID不能为空!");
        }
        try {
            JyExceptionScrappedPO PO = jyExceptionScrappedDao.selectOneByBizId(req.getBizId());
            if (PO == null) {
                return JdCResponse.fail("当前报废信息不存在!");
            }
            ExpScrappedDetailDto dto = coverToScrappedDetailDto(PO);
            return JdCResponse.ok(dto);
        } catch (Exception e) {
            logger.error("获取报废详情接口异常-{}", e.getMessage(), e);
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

    private ExpScrappedDetailDto coverToScrappedDetailDto(JyExceptionScrappedPO po) {
        ExpScrappedDetailDto dto = new ExpScrappedDetailDto();
        dto.setBizId(po.getBizId());
        dto.setSiteId(po.getSiteCode());
        dto.setScrappedTypCode(po.getExceptionType());
        JyExceptionScrappedTypeEnum scrappedTypeEnum = JyExceptionScrappedTypeEnum.valueOf(po.getExceptionType());
        dto.setScrappedTypName(scrappedTypeEnum.getName());
        dto.setSaveType(po.getSaveType());
        dto.setSubmitTime(po.getSubmitTime());
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

    /**
     * 检验生鲜单号
     *
     * @param waybillCode
     * @return
     */
    private String checkFresh(String waybillCode) {
        //根据运单获取waybillSign
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> dataByChoice
                = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
        if (dataByChoice == null
                || dataByChoice.getData() == null
                || dataByChoice.getData().getWaybill() == null
                || org.apache.commons.lang3.StringUtils.isBlank(dataByChoice.getData().getWaybill().getWaybillSign())) {
            logger.warn("查询运单waybillSign失败!-{}", waybillCode);
            return "获取运单失败!";
        }
        Integer goodNumber = dataByChoice.getData().getWaybill().getGoodNumber();
        //一单多件校验
        if(!Objects.equals(goodNumber,1)){
            return "一单多件禁止上报!";
        }
        String waybillSign = dataByChoice.getData().getWaybill().getWaybillSign();
        String sendPay = dataByChoice.getData().getWaybill().getSendPay();
        //自营生鲜运单判断
        if (BusinessUtil.isSelf(waybillSign)) {
            if (BusinessUtil.isSelfSX(sendPay)) {
                //todo  调用终端妥投校验接口
                logger.info("自营生鲜运单");
                return "";
            }
        } else {//外单
            if (BusinessUtil.isNotSelfSX(waybillSign)) {
                logger.info("外单生鲜运单");
                return "";
            }
        }
        return "非生鲜运单，请检查后再操作!";
    }

    /**
     * 校验单号是包裹号或者是运单号
     * @param barCode
     * @return
     */
    private boolean checkBarCode(String barCode){
        return WaybillUtil.isPackageCode(barCode) || WaybillUtil.isWaybillCode(barCode);
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
}
