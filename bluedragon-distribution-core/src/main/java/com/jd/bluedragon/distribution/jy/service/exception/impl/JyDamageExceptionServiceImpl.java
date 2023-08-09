package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionPackageTypeDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionProcessStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionPackageType;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDamageDao;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.enums.CustomerReturnResultEnum;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.ASCPContants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description: 异常-破损 service 实现
 */
@Service
public class JyDamageExceptionServiceImpl extends JyExceptionStrategy implements JyDamageExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyDamageExceptionServiceImpl.class);

    @Autowired
    private JyExceptionDamageDao jyExceptionDamageDao;

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyAttachmentDetailService jyAttachmentDetailService;

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClient;

    @Autowired
    @Qualifier("dmsDamageNoticeKFProducer")
    private DefaultJMQProducer dmsDamageNoticeKFProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private StrandService strandService;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private WaybillService waybillService;


    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.DAMAGE.getCode();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.exceptionTaskCheckByExceptionType", mState = {JProEnum.TP})
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req) {
        if (logger.isInfoEnabled()) {
            logger.info("选择异常类型时进行校验 入参-{}", JSON.toJSONString(req));
        }
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed("请求成功");
//        String waybillCode = req.getBarCode();
//        Waybill waybill = waybillService.getWaybillByWayCode(waybillCode);
//        if(waybill == null || waybill.getWaybillExt() == null){
//            response.toFail("运单接口调用失败!");
//            response.setData(Boolean.FALSE);
//            return response;
//        }
//        WaybillExt waybillExt = waybill.getWaybillExt();
//        if((StringUtils.isNotBlank(waybillExt.getStartFlowDirection()) && (Objects.equals("HK",waybillExt.getStartFlowDirection()) || Objects.equals("MO",waybillExt.getStartFlowDirection())))
//                || (StringUtils.isNotBlank(waybillExt.getEndFlowDirection()) && (Objects.equals("HK",waybillExt.getEndFlowDirection()) || Objects.equals("MO",waybillExt.getEndFlowDirection())))){
//            logger.info("港澳单-{}",waybill);
//            response.toFail("港澳单不允许上报!");
//            response.setData(Boolean.FALSE);
//            return response;
//        }
        return response;
    }

    /**
     * 检验更换包装逻辑 true: 运单通过校验  false:运单校验不通过，不能更改包装
     *
     * @return
     */
    private boolean checkDamageChangePackageRepair(String waybillCode) {

        //获取增值服务信息
        BaseEntity<List<WaybillVasDto>> baseEntity = waybillQueryManager.getWaybillVasInfosByWaybillCode(waybillCode);
        logger.info("运单getWaybillVasInfosByWaybillCode返回的结果为：{}", JsonHelper.toJson(baseEntity));
        if (baseEntity == null || baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() || baseEntity.getData() == null) {
            logger.warn("运单{}获取增值服务信息失败!", waybillCode);
            return false;
        }
        List<WaybillVasDto> waybillVas = baseEntity.getData();
        //校验是否是特安单
        if (BusinessHelper.matchWaybillVasDto(Constants.TE_AN_SERVICE, waybillVas)) {
            return false;
        }

        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if (waybill == null) {
            logger.warn("运单{}获取运单信息失败!", waybillCode);
            return false;
        }
        //校验是否是医药单
        if (BusinessHelper.matchWaybillVasDto(Constants.PRODUCT_TYPE_MEDICINE_SPECIAL_DELIVERY, waybillVas)
                || BusinessUtil.isBMedicine(waybill.getWaybillSign())) {
            return false;
        }
        //自营生鲜运单判断
        if (BusinessUtil.isSelf(waybill.getWaybillSign())) {
            if (BusinessUtil.isSelfSX(waybill.getSendPay())) {
                logger.info("自营生鲜运单");
                return false;
            }
        } else {//外单
            if (BusinessUtil.isNotSelfSX(waybill.getWaybillSign())) {
                logger.info("外单生鲜运单");
                return false;
            }
        }
        return true;
    }


    /**
     * 根据运单获取
     *
     * @param waybillCode
     * @return
     */
    private String getWaybillSignByWaybillCode(String waybillCode) {
        //根据运单获取waybillSign
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> dataByChoice
                = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
        if (dataByChoice == null
                || dataByChoice.getData() == null
                || dataByChoice.getData().getWaybill() == null
                || org.apache.commons.lang3.StringUtils.isBlank(dataByChoice.getData().getWaybill().getWaybillSign())) {
            logger.warn("查询运单waybillSign失败!-{}", waybillCode);
            return null;
        }
        return dataByChoice.getData().getWaybill().getWaybillSign();
    }


    @Override
    @Transactional
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.dealExpDamageInfoByAbnormalReportOutCall", mState = {JProEnum.TP})
    public void dealExpDamageInfoByAbnormalReportOutCall(QcReportOutCallJmqDto qcReportJmqDto) {
        logger.info("dealExpDamageInfoByAbnormalReportOutCall -外呼通知内容-{}",JSON.toJSONString(qcReportJmqDto));
        if (StringUtils.isBlank(qcReportJmqDto.getAbnormalDocumentNum())) {
            logger.warn("abnormalDocumentNum 为空！");
            return;
        }

        String barCode = qcReportJmqDto.getAbnormalDocumentNum();
        String bizId = getBizId(barCode, new Integer(qcReportJmqDto.getCreateDept()));
        String existKey = "DMS.EXCEPTION.DAMAGE:" + bizId;
        if (!redisClient.set(existKey, "1", 10, TimeUnit.SECONDS, false)) {
            logger.error("该破损任务正在处理。。。。");
            return;
        }
        try {
            JyBizTaskExceptionEntity exceptionEntity = jyBizTaskExceptionDao.findByBizId(bizId);
            logger.info("{}-原有的异常任务内容-{}",bizId,JSON.toJSONString(exceptionEntity));
            if (exceptionEntity == null) {
                logger.warn("根据 {} 查询异常破损任务为空！", bizId);
                return;
            }
            //判断当前的任务状态是否是待处理状态
            if (!Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(), exceptionEntity.getStatus())) {
                logger.warn("根据 {} 异常破损任务状态不在待处理状态！", bizId);
                return;
            }
            JyExceptionDamageEntity damageEntity = jyExceptionDamageDao.selectOneByBizId(bizId);
            logger.info("{}-原有的破损内容-{}",bizId,JSON.toJSONString(damageEntity));
            if (damageEntity == null) {
                logger.warn("根据 {} 查询破损数据为空！", bizId);
                return;
            }

            JyBizTaskExceptionEntity updateExp = new JyBizTaskExceptionEntity();
            updateExp.setBizId(bizId);
            //updateExp.setType(JyBizTaskExceptionTypeEnum.DAMAGE.getCode());
            updateExp.setUpdateUserErp(qcReportJmqDto.getCreateUser());
            updateExp.setUpdateTime(new Date());
            //更新破损任务状态为 处理中-客服介入中
            updateExp.setStatus(JyExpStatusEnum.PROCESSING.getCode());
            updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_INTERVENTION.getCode());

            JyExceptionDamageEntity updateDamageEntity = new JyExceptionDamageEntity();
            updateDamageEntity.setBizId(bizId);
            updateDamageEntity.setUpdateErp(qcReportJmqDto.getCreateUser());
            updateDamageEntity.setUpdateTime(new Date());
            updateDamageEntity.setSaveType(JyExceptionPackageType.SaveTypeEnum.SBUMIT_FEEBACK.getCode());

            //boolean sendMQFlag = sendMQNoticCustomerCheck(barCode, qcReportJmqDto,exceptionEntity, damageEntity, updateExp, updateDamageEntity);
            logger.info("最新的异常任务-{}",JSON.toJSONString(updateExp));
            logger.info("最新的破损数据-{}",JSON.toJSONString(updateDamageEntity));


            if (jyBizTaskExceptionDao.updateByBizId(updateExp) < 1) {
                logger.warn("破损任务数据更新失败！");
                return;
            }
            if (jyExceptionDamageDao.updateByBizId(updateDamageEntity) < 1) {
                logger.warn("破损数据更新失败！");
                return;
            }

            if (true) {
                JyExpDamageNoticCustomerMQ damageNoticCustomerMQ = this.coverToDamageNoticCustomerMQ(exceptionEntity);
                //发送破损消息通知客服
                logger.info("发送破损消息通知客服-MQ-{}",JSON.toJSONString(damageNoticCustomerMQ));
                dmsDamageNoticeKFProducer.send(bizId, JsonHelper.toJson(damageNoticCustomerMQ));
            }
            //称重
            //this.dealExpDamageWeightVolumeUpload(damageEntity, true);
            //滞留上报
            //this.dealExpDamageStrandReport(exceptionEntity.getBarCode());

        } catch (Exception e) {
            logger.error("根据质控异常提报mq处理破损数据异常!param-{}",JSON.toJSONString(qcReportJmqDto),e);
        } finally {
            redisClient.del(existKey);
        }
    }

    /**
     * 检验条件是否满足给客服发送消息
     * @param barCode
     * @param qcReportJmqDto
     * @param damageEntity
     * @param updateExp
     * @param updateDamageEntity
     * @return
     */
    private boolean sendMQNoticCustomerCheck(String barCode,QcReportOutCallJmqDto qcReportJmqDto, JyBizTaskExceptionEntity exceptionEntity
            ,JyExceptionDamageEntity damageEntity,JyBizTaskExceptionEntity updateExp,JyExceptionDamageEntity updateDamageEntity){

        //自营单上报破损时，非全部破损情况提交后，不与客服交互，任务状态变更为直接下传 （选择 外破内破-》无残余价值）
        if(isSelfByWaybillCode(barCode)){
            if(!(Objects.equals(JyExceptionPackageType.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(),damageEntity.getDamageType())
                    && Objects.equals(JyExceptionPackageType.InsideOutsideDamagedRepairTypeEnum.WORTHLESS.getCode(),damageEntity.getRepairType()))){
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(JyExceptionPackageType.FeedBackTypeEnum.HANDOVER.getCode());
                logger.warn("自营单-非全部破损，不与客服交互!");
                return false;
            }
        }

        //一单多包裹的情况下，若上报外破内破，不发送消息给客服，直接下传
        if (checkManyPackageNumberByWaybillCode(barCode)) {
            if (Objects.equals(JyExceptionPackageType.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(), damageEntity.getDamageType())) {
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(JyExceptionPackageType.FeedBackTypeEnum.HANDOVER.getCode());
                logger.warn("一单多包裹的情况下，上报外破内破，不与客服交互！");
                return  false;
            }
        }

        Integer damageCount = jyExceptionDamageDao.getDamageCountByBarCode(barCode);
        logger.info("{}-提报的次数-{}",barCode,damageCount);
        //非第一次上报
        if(damageCount  != null && damageCount >1){
            JyExceptionDamageEntity earliestOne = jyExceptionDamageDao.getEarliestOneDamageRecordByBarCode(barCode);
            logger.info("{}-上次提报的内容-{}",barCode,JSON.toJSONString(earliestOne));
            //同一个单号，另一个场地也上报  如果上报同类型， 不发送消息给客服，按上次反馈执行
            if (earliestOne != null
                    && !Objects.equals(qcReportJmqDto.getCreateDept(), earliestOne)
                    && Objects.equals(damageEntity.getDamageType(), earliestOne.getDamageType())
                    && Objects.equals(damageEntity.getRepairType(), earliestOne.getRepairType())) {
                //更新破损任务状态为待执行
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(earliestOne.getFeedBackType());
                logger.warn("非第一次上报，不与客服交互");
                return false;
            } else if (earliestOne != null
                    && Objects.equals(JyExceptionPackageType.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(), damageEntity.getDamageType())
                    && Objects.equals(JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode(), damageEntity.getRepairType())) {
                //已上报内破外破的包裹号，第二次报备外包装需更换时，不发送消息给客服，按上次反馈执行
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(earliestOne.getFeedBackType());
                logger.warn("非第一次上报，报备外包装需更换时不与客服交互");
                return false;
            }
        }
        return true;
    }

    /**
     * 校验自营订单
     *
     * @param waybillCode
     * @return
     */
    private Boolean isSelfByWaybillCode(String waybillCode) {
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if (waybill == null) {
            logger.warn("运单{}获取运单信息失败!", waybillCode);
            throw new JyBizException("获取运单信息失败！");
        }
        return BusinessUtil.isSelf(waybill.getWaybillSign());
    }

    /**
     * 校验运单是否是多包裹运单
     *
     * @param waybillCode
     * @return
     */
    private boolean checkManyPackageNumberByWaybillCode(String waybillCode) {
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> dataByChoice
                = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, false);
        if (dataByChoice == null
                || dataByChoice.getData() == null
                || dataByChoice.getData().getWaybill() == null
                || org.apache.commons.lang3.StringUtils.isBlank(dataByChoice.getData().getWaybill().getWaybillSign())) {
            logger.warn("查询运单waybillSign失败!-{}", waybillCode);
            return false;
        }
        Integer goodNumber = dataByChoice.getData().getWaybill().getGoodNumber();
        //一单多件校验
        if (!Objects.equals(goodNumber, 1)) {
            logger.info("waybillCode-{},一单多件", waybillCode);
            return true;
        }
        return false;
    }

    /**
     * @param damageEntity
     * @param before       true: 处理前  false: 处理后
     */
    private void dealExpDamageWeightVolumeUpload(JyExceptionDamageEntity damageEntity, boolean before) {
        WeightVolumeEntity entity = new WeightVolumeEntity();
        entity.setBarCode(damageEntity.getBarCode());
        entity.setWaybillCode(damageEntity.getBarCode());
        entity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL);
        entity.setSourceCode(FromSourceEnum.EXP_DAMAGE);
        if (before) {
            entity.setWeight(damageEntity.getWeightRepairBefore() != null ? damageEntity.getWeightRepairBefore().doubleValue() : null);
            entity.setVolume(damageEntity.getVolumeRepairBefore() != null ? damageEntity.getVolumeRepairBefore().doubleValue() : null);
        } else {
            entity.setWeight(damageEntity.getWeightRepairAfter() != null ? damageEntity.getWeightRepairAfter().doubleValue() : null);
            entity.setVolume(damageEntity.getVolumeRepairAfter() != null ? damageEntity.getVolumeRepairAfter().doubleValue() : null);
        }

        entity.setOperateSiteCode(damageEntity.getSiteCode());
        entity.setOperateSiteName(damageEntity.getSiteName());
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(damageEntity.getCreateErp());
        if (baseStaff != null) {
            entity.setOperatorId(baseStaff.getStaffNo());
            entity.setOperatorName(baseStaff.getStaffName());
        }
        entity.setOperatorCode(damageEntity.getCreateErp());
        entity.setOperateTime(new Date());
        if (logger.isInfoEnabled()) {
            logger.info("称重信息WeightVolumeEntity -{}", JsonHelper.toJson(entity));
        }
        InvokeResult<Boolean> result = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
        if (logger.isInfoEnabled()) {
            logger.info("处理破损运单称重结果-{}", JsonHelper.toJson(result));
        }
    }


    /**
     * 处理运单滞留上报信息
     */
    private void dealExpDamageStrandReport(String barcode) {
        StrandReportRequest request = new StrandReportRequest();
        request.setBarcode(barcode);
        request.setReasonCode(103);//滞留原因管理-破损异常
        request.setReportType(ReportTypeEnum.WAYBILL_CODE.getCode());
        request.setBusinessType(10);//业务类型-正向
        //调用滞留上报接口
        logger.info("破损调用运单滞留上报入参-{}", JsonHelper.toJson(request));
        InvokeResult<Boolean> report = strandService.report(request);
        logger.info("破损调用运单滞留上报结果-{}", JsonHelper.toJson(report));
    }

    /**
     * 发送客服破损数据组装
     *
     * @return
     */
    private JyExpDamageNoticCustomerMQ coverToDamageNoticCustomerMQ(JyBizTaskExceptionEntity entity) {
        JyExpDamageNoticCustomerMQ mq = new JyExpDamageNoticCustomerMQ();
        mq.setBusinessId(ASCPContants.DAMAGE_BUSINESS_ID);
        mq.setExptId(ASCPContants.DAMAGE_BUSINESS_ID + "_" + entity.getBizId());
        mq.setDealType(ASCPContants.DEAL_TYPE);
        mq.setCodeInfo(entity.getBarCode());
        mq.setCodeType(ASCPContants.CODE_TYPE);
        mq.setExptCreateTime(DateHelper.formatDateTime(entity.getCreateTime()));
        mq.setExptOneLevel(ASCPContants.DAMAGE_EXPT_ONE_LEVEL);
        mq.setExptOneLevelName(ASCPContants.DAMAGE_EXPT_ONE_LEVEL_NAME);
        mq.setExptTwoLevel(ASCPContants.DAMAGE_EXPT_TWO_LEVEL);
        mq.setExptTwoLevelName(ASCPContants.DAMAGE_EXPT_TWO_LEVEL_NAME);
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(entity.getSiteCode().intValue());
        if (baseSite != null) {
            mq.setStartOrgCode(baseSite.getOrgId().toString());
            mq.setStartOrgName(baseSite.getOrgName());
            mq.setProvinceAgencyCode(StringUtils.isNotBlank(baseSite.getProvinceAgencyCode()) ? baseSite.getProvinceAgencyCode().toString() : "");
            mq.setProvinceAgencyName(StringUtils.isNotBlank(baseSite.getProvinceAgencyName()) ? baseSite.getProvinceAgencyName() : "");
        }
        com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> dataByChoice
                = waybillQueryManager.getDataByChoice(entity.getBarCode(), true, true, true, false);
        if (dataByChoice != null
                && dataByChoice.getData() != null
                && dataByChoice.getData().getWaybill() != null
                && org.apache.commons.lang3.StringUtils.isBlank(dataByChoice.getData().getWaybill().getWaybillSign())) {
            logger.warn("查询运单waybillSign失败!-{}", entity.getBarCode());
            String waybillSign = dataByChoice.getData().getWaybill().getWaybillSign();
            if (BusinessUtil.isSelf(waybillSign)) {
                mq.setWaybillType(ASCPContants.WAYBILL_TYPE_SELF);
            } else {//外单
                mq.setWaybillType(ASCPContants.WAYBILL_TYPE_OTHER);
            }
        }
        mq.setWaybillCode(entity.getBarCode());
        return mq;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getToProcessDamageCount", mState = {JProEnum.TP})
    public JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(String positionCode) {
        logger.info("getToProcessDamageCount positionCode :{}", positionCode);
        String gridId = this.getGridRid(positionCode);
        if (StringUtils.isEmpty(gridId)) {
            return JdCResponse.fail("网格码不存在");
        }
        logger.info("getToProcessDamageCount gridId :{}", gridId);
        JyDamageExceptionToProcessCountDto toProcessCount = new JyDamageExceptionToProcessCountDto();
        // 获取待处理破损异常新增数量
        String bizIdAddSetStr = redisClient.get(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId);
        logger.info("getToProcessDamageCount bizIdAddSetStr :{}", bizIdAddSetStr);
        Set<String> oldBizIdAddSet = null;
        if (StringUtils.isEmpty(bizIdAddSetStr)) {
            toProcessCount.setToProcessAddCount(0);
        } else {
            oldBizIdAddSet = Arrays.stream(bizIdAddSetStr.split(",")).collect(Collectors.toSet());
            toProcessCount.setToProcessAddCount(oldBizIdAddSet.size());
        }
        // 获取待处理破损异常数量
        String oldBizIdSetStr = redisClient.get(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + gridId);
        logger.info("getToProcessDamageCount oldBizIdSetStr :{}", oldBizIdSetStr);
        Set<String> oldBizIdSet = new HashSet<>();
        if (StringUtils.isEmpty(oldBizIdSetStr)) {
            toProcessCount.setToProcessCount(0);
            if (!CollectionUtils.isEmpty(oldBizIdAddSet)) {
                // 新增转未处理
                redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + gridId, String.join(",", oldBizIdAddSet));
                redisClient.del(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId);
            }
            return JdCResponse.ok(toProcessCount);
        }
        oldBizIdSet = Arrays.stream(oldBizIdSetStr.split(",")).collect(Collectors.toSet());
        toProcessCount.setToProcessCount(oldBizIdSet.size());
        // 新增转未处理
        if (!CollectionUtils.isEmpty(oldBizIdAddSet)) {
            oldBizIdSet.addAll(oldBizIdAddSet);
            redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + positionCode, String.join(",", oldBizIdSet));
            redisClient.del(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId);
        }
        return JdCResponse.ok(toProcessCount);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.readToProcessDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> readToProcessDamage(String positionCode) {
        String gridId = this.getGridRid(positionCode);
        if (StringUtils.isEmpty(gridId)) {
            return JdCResponse.fail("网格码不存在");
        }
        logger.info("readToProcessDamage gridId :{}", gridId);
        redisClient.del(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId);
        redisClient.del(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION + gridId);
        return JdCResponse.ok();
    }

    private String getGridRid(String positionCode) {
        PositionDetailRecord positionDetail = jyExceptionService.getPosition(positionCode);
        logger.info("getGridRid positionDetail:{}", JSON.toJSONString(positionDetail));
        if (positionDetail == null) {
            return null;
        }
        return jyExceptionService.getGridRid(positionDetail);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getTaskDetailOfDamage", mState = {JProEnum.TP})
    public JdCResponse<JyExceptionDamageDto> getTaskDetailOfDamage(ExpDamageDetailReq req) {
        logger.info("getTaskDetailOfDamage req params:{}", JSON.toJSONString(req));
        if (req == null || StringUtils.isEmpty(req.getBizId())) {
            return JdCResponse.fail("请求参数不能为空");
        }
        JyExceptionDamageEntity oldEntity = jyExceptionDamageDao.selectOneByBizId(req.getBizId());
        logger.info("getTaskDetailOfDamage oldEntity:{}", JSON.toJSONString(oldEntity));
        if (oldEntity == null) {
            return JdCResponse.ok();
        }
        JyExceptionDamageDto damageDetail = new JyExceptionDamageDto();
        BeanUtils.copyProperties(oldEntity, damageDetail);

        damageDetail.setDamageTypeName(JyExceptionPackageType.DamagedTypeEnum.getNameByCode(damageDetail.getDamageType()));
        if (JyExceptionPackageType.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode().equals(damageDetail.getDamageType())) {
            damageDetail.setRepairTypeName(JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.getNameByCode(damageDetail.getRepairType()));
        } else if (JyExceptionPackageType.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode().equals(damageDetail.getDamageType())) {
            damageDetail.setRepairTypeName(JyExceptionPackageType.InsideOutsideDamagedRepairTypeEnum.getNameByCode(damageDetail.getRepairType()));
        }
        damageDetail.setFeedBackTypeName(JyExceptionPackageType.FeedBackTypeEnum.getNameByCode(damageDetail.getFeedBackType()));

        // 查询修复前图片
        damageDetail.setActualImageUrlList(this.getImageUrlList(oldEntity, JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode()));
        // 查询修复后图片
        if (JyExceptionPackageType.FeedBackTypeEnum.REPLACE_PACKAGING_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
            damageDetail.setActualImageUrlList(this.getImageUrlList(oldEntity, JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode()));
        }
        return JdCResponse.ok(damageDetail);
    }

    @Override
    @Transactional
    public void dealCustomerReturnDamageResult(JyExpCustomerReturnMQ returnMQ) {
        String bizId = returnMQ.getExptId();
        JyBizTaskExceptionEntity expTask = jyBizTaskExceptionDao.findByBizId(bizId);
        if (expTask == null) {
            logger.error("此任务-{}-不存在", bizId);
            return;
        }
        //判断任务状态是否是处理中、客服介入中
        if (!(Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), expTask.getStatus())
                && Objects.equals(JyBizTaskExceptionProcessStatusEnum.WAITER_INTERVENTION.getCode(), expTask.getProcessingStatus()))) {
            logger.error("此任务-{}-状态不正确", bizId);
            return;
        }
        //回传状态
        String resultType = returnMQ.getResultType();
        CustomerReturnResultEnum resultEnum = CustomerReturnResultEnum.convertApproveEnum(resultType);
        JyExceptionDamageEntity damageEntity = new JyExceptionDamageEntity();
        damageEntity.setBizId(bizId);
        damageEntity.setFeedBackType(resultEnum.getType());
        damageEntity.setUpdateTime(new Date());
        logger.info("根据客服回传状态更新破损数据 入参-{}", JSON.toJSON(damageEntity));
        jyExceptionDamageDao.updateByBizId(damageEntity);

        //更新任务状态为处理中、待执行
        JyBizTaskExceptionEntity updateExpTask = new JyBizTaskExceptionEntity();
        updateExpTask.setBizId(bizId);
        updateExpTask.setUpdateTime(new Date());
        updateExpTask.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
        logger.info("根据客服回传状态更新任务数据 入参-{}", JSON.toJSON(updateExpTask));
        jyBizTaskExceptionDao.updateByBizId(updateExpTask);
    }


    private List<String> getImageUrlList(JyExceptionDamageEntity oldEntity, String bizType) {
        JyAttachmentDetailQuery queryBefore = new JyAttachmentDetailQuery();
        queryBefore.setBizId(oldEntity.getBizId());
        queryBefore.setSiteCode(oldEntity.getSiteCode());
        queryBefore.setBizType(bizType);
        // 删除老数据
        List<JyAttachmentDetailEntity> entityList = jyAttachmentDetailService.queryDataListByCondition(queryBefore);
        logger.info("getTaskDetailOfDamage getImageUrlList entityList:{}", JSON.toJSONString(entityList));
        if (!CollectionUtils.isEmpty(entityList)) {
            return entityList.stream().map(JyAttachmentDetailEntity::getAttachmentUrl)
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 写入待处理破损异常
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.writeToProcessDamage", mState = {JProEnum.TP})
    public void writeToProcessDamage(String bizId) {
        logger.info("writeToProcessDamage bizId:{}", bizId);
        JyBizTaskExceptionEntity entity = jyBizTaskExceptionDao.findByBizId(bizId);
        logger.info("writeToProcessDamage entity:{}", entity);
        if (entity == null) {
            return;
        }
        String bizIdSetStr = redisClient.get(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + entity.getDistributionTarget());
        logger.info("writeToProcessDamage bizIdSetStr:{}", bizIdSetStr);
        if (StringUtils.isEmpty(bizIdSetStr)) {
            Set<String> bizIdSet = new HashSet<>();
            bizIdSet.add(bizId);
            redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + entity.getDistributionTarget(), String.join(",", bizIdSet));
            return;
        }
        Set<String> oldBizIdSet = Arrays.stream(bizIdSetStr.split(",")).collect(Collectors.toSet());
        oldBizIdSet.add(bizId);
        redisClient.set(JyExceptionPackageType.TO_PROCESS_DAMAGE_EXCEPTION_ADD + entity.getDistributionTarget(), String.join(",", oldBizIdSet));
        logger.info("writeToProcessDamage write successfully");
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.processTaskOfDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req) {
        JyExceptionDamageEntity entity = new JyExceptionDamageEntity();
        logger.info("processTaskOfDamage req params:{}", JSON.toJSONString(req));
        try {
            JyExceptionDamageEntity oldEntity = jyExceptionDamageDao.selectOneByBizId(req.getBizId());
            logger.info("processTaskOfDamage oldEntity :{}", JSON.toJSONString(oldEntity));
            if (oldEntity != null) {
                entity.setId(oldEntity.getId());
            }
            // 提交破损信息
            if (oldEntity == null || JyExceptionPackageType.FeedBackTypeEnum.DEFAULT.getCode().equals(oldEntity.getFeedBackType())) {
                this.saveDamage(req, entity, oldEntity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REPAIR_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                // 1.修复下传
                this.repairOrReplacePackagingHandover(req, entity);
                //称重
                this.dealExpDamageWeightVolumeUploadAfterRepair(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //2.直接下传
                this.finishFlow(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REPLACE_PACKAGING_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
                //3.更换包装下传
                this.repairOrReplacePackagingHandover(req, entity);
                //称重
                this.dealExpDamageWeightVolumeUploadAfterRepair(req, entity);
            } else if (JyExceptionPackageType.FeedBackTypeEnum.DESTROY.getCode().equals(oldEntity.getFeedBackType())) {
                //4.报废
                this.finishFlow(req, entity);
                //报废全程跟踪
                this.sendScrapTraceOfDamage(req.getBizId());
            } else if (JyExceptionPackageType.FeedBackTypeEnum.REVERSE_RETURN.getCode().equals(oldEntity.getFeedBackType())) {
                //5.逆向退回
//                该运单在提报场地操作换单打印成功后，任务状态变更为已完成
//                this.finishFlow(req, entity);
            } else {
                return JdCResponse.fail("客服反馈类型匹配失败" + req.getBizId());
            }
        } catch (Exception e) {
            logger.error("提交破损包裹报错:", e);
            return JdCResponse.fail(e.getMessage());
        }
        return JdCResponse.ok();
    }

    /**
     * 推送破损运单的报废全程跟踪
     *
     * @param bizId
     */
    private void sendScrapTraceOfDamage(String bizId) {
        JyBizTaskExceptionEntity exTaskEntity = jyBizTaskExceptionDao.findByBizId(bizId);
        if (exTaskEntity == null) {
            logger.warn("根据业务主键:{}未查询到异常任务数据!", bizId);
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("推送破损运单的报废全程跟踪--{}", bizId);
        }
        jyExceptionService.pushScrapTrace(exTaskEntity);
    }

    /**
     * 破损修复后或更换包装后触发称重
     *
     * @param req
     * @param entity
     */
    private void dealExpDamageWeightVolumeUploadAfterRepair(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {

        entity.setVolumeRepairAfter(req.getVolumeRepairAfter());
        entity.setWeightRepairAfter(req.getWeightRepairAfter());
        if (logger.isInfoEnabled()) {
            logger.info("破损修复后或更换包装后触发称重-{}", JSON.toJSONString(entity));
        }
        dealExpDamageWeightVolumeUpload(entity, false);

    }

    private void finishFlow(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        logger.info("finishFlow req params:{}", JSON.toJSONString(req));
        if (StringUtils.isNotBlank(req.getUserErp())) {
            this.validateOrSetErpInfo(req, entity);
        }
        this.saveOrUpdate(entity);
        this.updateTask(entity);
    }

    private void updateTask(JyExceptionDamageEntity entity) {
        JyBizTaskExceptionEntity update = new JyBizTaskExceptionEntity();
        update.setBizId(entity.getBizId());
        update.setStatus(JyExpStatusEnum.COMPLETE.getCode());
        update.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.DONE.getCode());
        update.setUpdateUserErp(entity.getUpdateErp());
        update.setUpdateUserName(entity.getUpdateStaffName());
        update.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(update);
        jyExceptionService.recordLog(JyBizTaskExceptionCycleTypeEnum.CLOSE, update);
        logger.info("updateTask sucessfullly");

    }

    private void repairOrReplacePackagingHandover(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        if (StringUtils.isNotBlank(req.getUserErp())) {
            this.validateOrSetErpInfo(req, entity);
        }
        entity.setVolumeRepairAfter(req.getVolumeRepairAfter());
        entity.setWeightRepairAfter(req.getWeightRepairAfter());
        this.saveImages(req, entity);
        this.saveOrUpdate(entity);
        this.updateTask(entity);
    }

    /**
     * 提交破损信息
     *
     * @param req
     * @return
     */
    private void saveDamage(ExpDamageDetailReq req, JyExceptionDamageEntity entity, JyExceptionDamageEntity oldEntity) {
        logger.info("start saveDamage...");
        this.validateSaveDamageParams(req, oldEntity);
        this.dealTaskInfo(req, entity);
        if (StringUtils.isNotBlank(req.getUserErp())) {
            this.validateOrSetErpInfo(req, entity);
        }
        this.copyRequestToEntity(req, entity);
        this.saveImages(req, entity);
        this.saveOrUpdate(entity);
    }

    private void validateSaveDamageParams(ExpDamageDetailReq req, JyExceptionDamageEntity oldEntity) {
        if (!JyExceptionPackageType.SaveTypeEnum.DRAFT.getCode().equals(req.getSaveType())
                && !JyExceptionPackageType.SaveTypeEnum.SBUMIT_NOT_FEEBACK.getCode().equals(req.getSaveType())) {
            throw new RuntimeException("保存类型错误!" + req.getBizId());
        }
        if (oldEntity != null && JyExceptionPackageType.SaveTypeEnum.SBUMIT_NOT_FEEBACK.getCode().equals(oldEntity.getSaveType())) {
            throw new RuntimeException("不能重复提交!" + req.getBizId());
        }
    }

    private void saveOrUpdate(JyExceptionDamageEntity entity) {
        logger.info("saveOrUpdate entity :{}", JSON.toJSONString(entity));
        if (entity.getId() == null) {
            logger.info("saveOrUpdate save...");
            jyExceptionDamageDao.insertSelective(entity);
        } else {
            logger.info("saveOrUpdate update...");
            this.clearNotNeedUpdateFiled(entity);
            jyExceptionDamageDao.updateByBizId(entity);
        }
        logger.info("saveOrUpdate entity sucessfully");

    }

    private void clearNotNeedUpdateFiled(JyExceptionDamageEntity entity) {
        entity.setSiteCode(null);
        entity.setSiteName(null);
    }

    private void saveImages(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        if (!CollectionUtils.isEmpty(req.getActualImageUrlList())) {
            this.saveImages(req.getActualImageUrlList(), JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode(), entity);
        } else if (!CollectionUtils.isEmpty(req.getDealImageUrlList())) {
            this.saveImages(req.getActualImageUrlList(), JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode(), entity);
        }
    }

    /**
     * 保存图片
     *
     * @param imageUrlList
     * @param bitType
     * @param entity
     */
    private void saveImages(List<String> imageUrlList, String bitType, JyExceptionDamageEntity entity) {
        if (!CollectionUtils.isEmpty(imageUrlList)) {
            JyAttachmentDetailQuery query = new JyAttachmentDetailQuery();
            query.setBizId(entity.getBizId());
            query.setSiteCode(entity.getSiteCode());
            query.setBizType(bitType);
            // 删除老数据
            logger.info("saveImages entity :{}", JSON.toJSONString(entity));
            List<JyAttachmentDetailEntity> oldImageUrlList = jyAttachmentDetailService.queryDataListByCondition(query);
            logger.info("saveImages oldImageUrlList :{}", JSON.toJSONString(oldImageUrlList));
            if (!CollectionUtils.isEmpty(oldImageUrlList)) {
                oldImageUrlList.forEach(i -> jyAttachmentDetailService.delete(i));
            }
            List<JyAttachmentDetailEntity> annexList = Lists.newArrayList();
            for (String proveUrl : imageUrlList) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                annexEntity.setBizId(entity.getBizId());
                annexEntity.setSiteCode(entity.getSiteCode());
                annexEntity.setBizType(bitType);
                annexEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
                annexEntity.setCreateUserErp(entity.getUpdateErp());
                annexEntity.setUpdateUserErp(entity.getUpdateErp());
                annexEntity.setAttachmentUrl(proveUrl);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.SECOND, 1);
                annexEntity.setCreateTime(calendar.getTime());
                annexEntity.setUpdateTime(new Date());
                annexList.add(annexEntity);
            }
            jyAttachmentDetailService.batchInsert(annexList);
        }
    }


    /**
     * 校验或设置Erp相关信息
     *
     * @param req
     * @param entity
     * @return
     */
    private void validateOrSetErpInfo(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        // 获取erp相关信息
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        logger.info("validateOrSetErpInfo baseStaffByErp :{}", JSON.toJSONString(baseStaffByErp));
        if (baseStaffByErp == null) {
            throw new RuntimeException("登录人ERP有误!" + req.getUserErp());
        }
        this.copyErpToEntity(baseStaffByErp, entity);
    }

    /**
     * 校验或设置Task相关信息
     *
     * @param req
     * @param entity
     * @return
     */
    private void dealTaskInfo(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        // 获取task相关信息
        JyBizTaskExceptionEntity bizEntity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        logger.info("dealTaskInfo bizEntity :{}", JSON.toJSONString(bizEntity));
        if (bizEntity == null) {
            throw new RuntimeException("无相关任务!bizId=" + req.getBizId());
        }
        this.copyTaskToEntity(bizEntity, entity);
    }

    private void copyErpToEntity(BaseStaffSiteOrgDto baseStaffByErp, JyExceptionDamageEntity entity) {
        if (entity.getId() == null) {
            entity.setCreateErp(baseStaffByErp.getAccountNumber());
            entity.setCreateTime(new Date());
            entity.setCreateStaffName(baseStaffByErp.getStaffName());
        }
        entity.setSiteCode(baseStaffByErp.getSiteCode());
        entity.setSiteName(baseStaffByErp.getSiteName());
        entity.setUpdateErp(baseStaffByErp.getAccountNumber());
        entity.setUpdateTime(new Date());
        entity.setUpdateStaffName(baseStaffByErp.getStaffName());

    }

    private void copyTaskToEntity(JyBizTaskExceptionEntity task, JyExceptionDamageEntity entity) {
        entity.setBizId(task.getBizId());
        if (entity.getId() == null) {
            entity.setBarCode(task.getBarCode());
        }
    }

    private void copyRequestToEntity(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        entity.setSaveType(req.getSaveType());
        entity.setVolumeRepairBefore(req.getVolumeRepairBefore());
        entity.setWeightRepairBefore(req.getWeightRepairBefore());
        entity.setDamageType(req.getDamageType());
        entity.setRepairType(req.getRepairType());
    }

    private String getBizId(String barCode, Integer siteId) {
        if (BusinessUtil.isSanWuCode(barCode)) {
            return JyBizTaskExceptionTypeEnum.SANWU.name() + "_" + barCode;
        } else {
            return barCode + "_" + siteId;
        }
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyScrappedExceptionServiceImpl.getJyExceptionPackageTypeList", mState = {JProEnum.TP})
    public JdCResponse<List<JyExceptionPackageTypeDto>> getJyExceptionPackageTypeList(String barCode) {
        JdCResponse<List<JyExceptionPackageTypeDto>> result = new JdCResponse<>();
        List<JyExceptionPackageTypeDto> dtoList = new ArrayList<>();
        //保存异常包裹为第一层级
        Arrays.stream(JyExceptionPackageType.ExceptionPackageTypeEnum.values()).forEach(type -> {
            if (JyExceptionPackageType.ExceptionPackageTypeEnum.DAMAGE.getCode().equals(type.getCode())) {
                dtoList.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName(), getDamagedTypeList(barCode)));
            } else {
                dtoList.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName()));
            }
        });
        result.setData(dtoList);
        result.toSucceed("请求成功!");
        return result;
    }

    /**
     * 获取破损类型列表
     */
    private List<JyExceptionPackageTypeDto> getDamagedTypeList(String barCode) {
        List<JyExceptionPackageTypeDto> list = new ArrayList<>();
        Arrays.stream(JyExceptionPackageType.DamagedTypeEnum.values()).forEach(type -> {
            if (JyExceptionPackageType.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode().equals(type.getCode())) {
                list.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName(), getOutPackingDamagedRepairTypeList(barCode)));
            } else if (JyExceptionPackageType.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode().equals(type.getCode())) {
                list.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName(), getInsideOutsideDamagedRepairTypeList()));
            }
        });
        return list;
    }

    /**
     * 获取外包装破损修复类型列表
     */
    private List<JyExceptionPackageTypeDto> getOutPackingDamagedRepairTypeList(String barCode) {
        List<JyExceptionPackageTypeDto> list = new ArrayList<>();
        list.add(new JyExceptionPackageTypeDto(JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.REPAIR.getCode(),
                JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.REPAIR.getName()));
        if (StringUtils.isEmpty(barCode) || this.checkDamageChangePackageRepair(barCode)) {
            list.add(new JyExceptionPackageTypeDto(JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode(),
                    JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getName(), true));
        } else {
            list.add(new JyExceptionPackageTypeDto(JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.HANDOVER.getCode(),
                    JyExceptionPackageType.OutPackingDamagedRepairTypeEnum.HANDOVER.getName(), true));
        }
        return list;
    }

    /**
     * 获取内破外破修复类型列表
     */
    private List<JyExceptionPackageTypeDto> getInsideOutsideDamagedRepairTypeList() {
        List<JyExceptionPackageTypeDto> list = new ArrayList<>();
        Arrays.stream(JyExceptionPackageType.InsideOutsideDamagedRepairTypeEnum.values()).forEach(type -> list.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName())));
        return list;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.getDamageImageListByBizIds", mState = {JProEnum.TP})
    public Map<String, List<JyAttachmentDetailEntity>> getDamageImageListByBizIds(List<JyExceptionDamageEntity> detailList, Boolean isFinish) {
        if (CollectionUtils.isEmpty(detailList)) {
            return new HashMap<>();
        }
        List<String> bizIdList = detailList.stream().map(JyExceptionDamageEntity::getBizId).collect(Collectors.toList());
        logger.info("getDamageImageListByBizIds bizIdList :{},detailList:{}", JSON.toJSONString(bizIdList), JSON.toJSONString(detailList));
        JyAttachmentDetailQuery query = new JyAttachmentDetailQuery();
        query.setBizIdList(bizIdList);
        query.setSiteCode(detailList.get(0).getSiteCode());
        if (isFinish) {
            query.setBizType(JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode());
        } else {
            query.setBizType(JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode());
        }
        query.setLimit(200);
        List<JyAttachmentDetailEntity> entityList = jyAttachmentDetailService.queryDataListByCondition(query);
        if (CollectionUtils.isEmpty(entityList)) {
            return new HashMap<>();
        }
        logger.info("getDamageImageListByBizIds entityList :{}", JSON.toJSONString(entityList));
        return entityList.stream().collect(Collectors.groupingBy(JyAttachmentDetailEntity::getBizId));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.getDamageDetailListByBizIds", mState = {JProEnum.TP})
    public List<JyExceptionDamageEntity> getDamageDetailListByBizIds(List<String> bizIdList) {
        if (CollectionUtils.isEmpty(bizIdList)) {
            return new ArrayList<>();
        }
        logger.info("getDamageDetailListByBizIds bizIdList :{}", JSON.toJSONString(bizIdList));
        List<JyExceptionDamageEntity> entityList = jyExceptionDamageDao.getTaskListOfDamage(bizIdList);
        if (CollectionUtils.isEmpty(entityList)) {
            return new ArrayList<>();
        }
        logger.info("getDamageDetailListByBizIds entityList :{}", JSON.toJSONString(entityList));
//        return entityList.stream().collect(Collectors.toMap(JyExceptionDamageEntity::getBizId, entity -> entity));
        return entityList;
    }
}
