package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.jyexpection.response.JyDamageExceptionToProcessCountDto;
import com.jd.bluedragon.common.dto.jyexpection.response.JyExceptionPackageTypeDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ConsumableManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.abnormal.domain.StrandReportRequest;
import com.jd.bluedragon.distribution.abnormal.service.StrandService;
import com.jd.bluedragon.distribution.api.response.base.ResultCodeConstant;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyDamageConsumableDao;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionDamageDao;
import com.jd.bluedragon.distribution.jy.dto.Consumable;
import com.jd.bluedragon.distribution.jy.dto.JyExceptionDamageDto;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ASCPContants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.common.utils.PageDto;
import com.jdl.basic.common.utils.Result;
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
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    @Qualifier("dmsDamageMainLandNoticeKFProducer")
    private DefaultJMQProducer dmsDamageMainLandNoticeKFProducer;

    @Autowired
    @Qualifier("dmsDamageHKOrMONoticeKFProducer")
    private DefaultJMQProducer dmsDamageHKOrMONoticeKFProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private StrandService strandService;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private ConsumableManager consumableManager;

    @Autowired
    private JyDamageConsumableDao jyDamageConsumableDao;

    @Autowired
    private WorkStationGridManager workStationGridManager;

    private static final Integer EXPRESS_DELIVERY = 1;

    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.DAMAGE.getCode();
    }

    @Override
    public JdCResponse<Object> uploadScan(JyBizTaskExceptionEntity exceptionEntity, ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source, String bizId) {
        JdCResponse<Object> response = new JdCResponse<>();
        response.toSucceed();
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.exceptionTaskCheckByExceptionType", mState = {JProEnum.TP})
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req,Waybill waybill) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed();
        return response;
    }



    /**
     * 检验更换包装逻辑 true: 运单通过校验  false:运单校验不通过，不能更改包装
     *
     * @return
     */
    private boolean checkDamageChangePackageRepair(String waybillCode) {

        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if (waybill == null) {
            logger.warn("运单{}获取运单信息失败!", waybillCode);
            return false;
        }

        //自营生鲜运单判断
        if (BusinessUtil.isSelf(waybill.getWaybillSign())) {
            if (BusinessUtil.isSelfSX(waybill.getSendPay())) {
                logger.info("自营生鲜运单-{}",waybillCode);
                return false;
            }
        } else {//外单
            if (BusinessUtil.isNotSelfSX(waybill.getWaybillSign())) {
                logger.info("外单生鲜运单-{}",waybillCode);
                return false;
            }
        }

        //获取增值服务信息
        BaseEntity<List<WaybillVasDto>> baseEntity = waybillQueryManager.getWaybillVasInfosByWaybillCode(waybillCode);
        logger.info("运单getWaybillVasInfosByWaybillCode返回的结果为：{},运单号：{}", JsonHelper.toJson(baseEntity),waybillCode);
        if (baseEntity != null && !CollectionUtils.isEmpty(baseEntity.getData())) {
            List<WaybillVasDto> waybillVas = baseEntity.getData();
            //校验是否是特安单
            if (BusinessHelper.matchWaybillVasDto(Constants.TE_AN_SERVICE, waybillVas)) {
                logger.warn("特安单!-{}", waybillCode);
                return false;
            }
            //校验是否是医药单
            if (BusinessHelper.matchWaybillVasDto(Constants.PRODUCT_TYPE_MEDICINE_SPECIAL_DELIVERY, waybillVas)
                    || BusinessUtil.isBMedicine(waybill.getWaybillSign())) {
                logger.info("医药单!-{}", waybillCode);
                return false;
            }
        }

        boolean hKorMOWaybill = isHKorMOWaybill(waybillCode, waybill);
        if(hKorMOWaybill){
            logger.warn("港澳单!-{}", waybillCode);
            return false;
        }
        return true;
    }





    @Override
    @Transactional
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.dealExpDamageInfoByAbnormalReportOutCall", mState = {JProEnum.TP})
    public void dealExpDamageInfoByAbnormalReportOutCall(QcReportJmqDto qcReportJmqDto) {
        logger.info("dealExpDamageInfoByAbnormalReportOutCall -外呼通知内容-{}", JSON.toJSONString(qcReportJmqDto));
        if (StringUtils.isBlank(qcReportJmqDto.getAbnormalDocumentNum()) || StringUtils.isBlank(qcReportJmqDto.getCreateDept())) {
            logger.warn("abnormalDocumentNum or createDept 为空！");
            return;
        }
        String barCode = qcReportJmqDto.getAbnormalDocumentNum();
        String bizId = getBizId(barCode, new Integer(qcReportJmqDto.getCreateDept()));
        String existKey = "DMS.EXCEPTION.DAMAGE:" + bizId;
        try {
            if (!redisClient.set(existKey, "1", 1, TimeUnit.SECONDS, false)) {
                logger.error("该破损任务正在处理。。。。");
                return;
            }
            JyBizTaskExceptionEntity exceptionEntity = jyBizTaskExceptionDao.findByBizId(bizId);
            logger.info("{}-原有的异常任务内容-{}", bizId, JSON.toJSONString(exceptionEntity));
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
            logger.info("{}-原有的破损内容-{}", bizId, JSON.toJSONString(damageEntity));
            if (damageEntity == null) {
                logger.warn("根据 {} 查询破损数据为空！", bizId);
                return;
            }

            Waybill waybill = waybillService.getWaybillByWayCode(barCode);
            if(waybill == null){
                logger.warn("查询运单waybillSign失败!-{}", barCode);
                throw new JyBizException("获取运单信息失败！");
            }

            JyBizTaskExceptionEntity updateExp = new JyBizTaskExceptionEntity();
            updateExp.setBizId(bizId);
            updateExp.setUpdateUserErp(qcReportJmqDto.getCreateUser());
            updateExp.setUpdateTime(new Date());
            updateExp.setStatus(JyExpStatusEnum.PROCESSING.getCode());
            updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_INTERVENTION.getCode());

            JyExceptionDamageEntity updateDamageEntity = new JyExceptionDamageEntity();
            updateDamageEntity.setBizId(bizId);
            updateDamageEntity.setUpdateErp(qcReportJmqDto.getCreateUser());
            updateDamageEntity.setUpdateTime(new Date());
            updateDamageEntity.setSaveType(JyExceptionDamageEnum.SaveTypeEnum.SBUMIT_FEEBACK.getCode());

            boolean sendMQFlag = sendMQNoticCustomerCheck(barCode, qcReportJmqDto,exceptionEntity, damageEntity, updateExp, updateDamageEntity,waybill);
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

            if (sendMQFlag) {
                sendMQToCustomer(exceptionEntity,damageEntity,waybill);
            }else {
                //不给客服发消息 也要提示有新消息
                writeToProcessDamage(bizId);
            }
            //称重
            this.dealExpDamageWeightVolumeUpload(damageEntity, true);
            //滞留上报
            this.dealExpDamageStrandReport(exceptionEntity);
        } catch (Exception e) {
            logger.error("根据质控异常提报mq处理破损数据异常!param-{}", JSON.toJSONString(qcReportJmqDto), e);
        } finally {
            redisClient.del(existKey);
        }
    }


    /**
     * 发送破损数据给客服
     * @param exceptionEntity
     * @param damageEntity
     * @param waybill
     */
    private void  sendMQToCustomer(JyBizTaskExceptionEntity exceptionEntity,JyExceptionDamageEntity damageEntity,Waybill waybill){
        try{
            boolean isHKorMOWaybill = isHKorMOWaybill(exceptionEntity.getBarCode(), waybill);
            // 内破外破
            if(Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(),damageEntity.getDamageType())){
                //内物严重破损
                if(Objects.equals(JyExceptionDamageEnum.InsideOutsideDamagedRepairTypeEnum.INSIDE_SEVERE_DAMAGE.getCode(),damageEntity.getRepairType())){
                    //外单
                    if(!BusinessUtil.isSelf(waybill.getWaybillSign())) {
                        if (isHKorMOWaybill) {
                            JyExpDamageNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(exceptionEntity, waybill, JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_DAMAGE_NO_DOWNLOAD.getCode(), true);
                            if (mq != null) {
                                logger.info("内破外破-内物严重破损-外单-港澳单");
                                dmsDamageHKOrMONoticeKFProducer.sendOnFailPersistent(exceptionEntity.getBizId(), JsonHelper.toJson(mq));
                            }
                        } else {
                            JyExpDamageNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(exceptionEntity, waybill, JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_PART_DAMAGE.getCode(), false);
                            if (mq != null) {
                                logger.info("内破外破-内物严重破损-外单-大陆单");
                                dmsDamageMainLandNoticeKFProducer.sendOnFailPersistent(exceptionEntity.getBizId(), JsonHelper.toJson(mq));
                            }
                        }

                    }
                }else if(Objects.equals(JyExceptionDamageEnum.InsideOutsideDamagedRepairTypeEnum.WORTHLESS.getCode(),damageEntity.getRepairType())){
                    //无残余价值
                    if (isHKorMOWaybill) {
                        JyExpDamageNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(exceptionEntity, waybill, JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_DAMAGE_NO_DOWNLOAD.getCode(), true);
                        if (mq != null) {
                            logger.info("内破外破-无残余价值-港澳单");
                            dmsDamageHKOrMONoticeKFProducer.sendOnFailPersistent(exceptionEntity.getBizId(), JsonHelper.toJson(mq));
                        }
                    } else {
                        JyExpDamageNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(exceptionEntity, waybill, JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_SERIOUS_DAMAGE.getCode(), false);
                        if (mq != null) {
                            logger.info("内破外破-无残余价值-大陆单");
                            dmsDamageMainLandNoticeKFProducer.sendOnFailPersistent(exceptionEntity.getBizId(), JsonHelper.toJson(mq));
                        }
                    }

                }
            }else if(Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode(),damageEntity.getDamageType())){ //外包装破损
                if(Objects.equals(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode(),damageEntity.getRepairType())){
                    //更换包装
                    if(!isHKorMOWaybill){
                        JyExpDamageNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(exceptionEntity, waybill, JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_CHANGE_PACKAGE.getCode(), false);
                        if (mq != null) {
                            logger.info("外包装破损-更换包装-大陆单");
                            dmsDamageMainLandNoticeKFProducer.sendOnFailPersistent(exceptionEntity.getBizId(), JsonHelper.toJson(mq));
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.error("发送客服破损数据异常!",e);
        }
    }


    /**
     * 组装发送给客服破损数据
     *
     * @return
     */
    private JyExpDamageNoticCustomerMQ coverToDamageNoticCustomerMQ(JyBizTaskExceptionEntity entity,Waybill waybill,String twoLevelExceptionCode,boolean isHKorMO) {
        JyExpDamageNoticCustomerMQ mq = new JyExpDamageNoticCustomerMQ();

        mq.setDealType(ASCPContants.DEAL_TYPE);
        mq.setCodeInfo(entity.getBarCode());
        mq.setCodeType(ASCPContants.CODE_TYPE);
        mq.setExptCreateTime(DateHelper.formatDateTime(entity.getCreateTime()));
        mq.setExptCreator(entity.getCreateUserErp());
        JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum twoLevelEnum = JyExpNoticCustomerExpReasonEnum
                .ExpReasonTwoLevelEnum.getEnumByCode(twoLevelExceptionCode);
        if(twoLevelEnum == null){
            return null;
        }

        if(isHKorMO){
            logger.info("港澳单---{}",entity.getBarCode());
            mq.setBusinessId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_HK_HO.getCode());
            mq.setExptId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_HK_HO.getCode() + "_" + entity.getBizId());
            switch (twoLevelEnum){
                case HA_MO_DAMAGE_NO_DOWNLOAD:
                    mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.HA_MO_DELIVERY_EXCEPTION_REPORT.getCode());
                    mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.HA_MO_DELIVERY_EXCEPTION_REPORT.getName());
                    mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_DAMAGE_NO_DOWNLOAD.getCode());
                    mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_DAMAGE_NO_DOWNLOAD.getName());
                    break;

                default:
                    logger.warn("未知的二级原因-{}",twoLevelEnum);
                    return null;
            }
        }else{
            logger.info("大陆单---{}",entity.getBarCode());
            mq.setBusinessId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_MAIN_LAND.getCode());
            mq.setExptId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_MAIN_LAND.getCode() + "_" + entity.getBizId());

            switch (twoLevelEnum){

                case MAIN_LAND_PART_DAMAGE:
                    mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_DAMAGE_NO_DOWNLOAD.getCode());
                    mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_DAMAGE_NO_DOWNLOAD.getName());
                    mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_PART_DAMAGE.getCode());
                    mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_PART_DAMAGE.getName());
                    break;
                case MAIN_LAND_SERIOUS_DAMAGE:
                    mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_DAMAGE_NO_DOWNLOAD.getCode());
                    mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_DAMAGE_NO_DOWNLOAD.getName());
                    mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_SERIOUS_DAMAGE.getCode());
                    mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_SERIOUS_DAMAGE.getName());
                    break;
                case MAIN_LAND_CHANGE_PACKAGE:
                    mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_SALE_PACKAGE_DAMAGE.getCode());
                    mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_SALE_PACKAGE_DAMAGE.getName());
                    mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_CHANGE_PACKAGE.getCode());
                    mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_CHANGE_PACKAGE.getName());
                    break;

                default:
                    logger.warn("未知的二级原因-{}",twoLevelEnum);
                    return null;
            }
        }

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(entity.getSiteCode().intValue());
        if (baseSite != null) {
            mq.setStartOrgCode(baseSite.getOrgId().toString());
            mq.setStartOrgName(baseSite.getOrgName());
            mq.setProvinceAgencyCode(StringUtils.isNotBlank(baseSite.getProvinceAgencyCode()) ? baseSite.getProvinceAgencyCode().toString() : "");
            mq.setProvinceAgencyName(StringUtils.isNotBlank(baseSite.getProvinceAgencyName()) ? baseSite.getProvinceAgencyName() : "");
        }

        String waybillSign = waybill.getWaybillSign();
        if (BusinessUtil.isSelf(waybillSign)) {
            mq.setWaybillType(ASCPContants.WAYBILL_TYPE_SELF);
        } else {//外单
            mq.setWaybillType(ASCPContants.WAYBILL_TYPE_OTHER);
        }
        mq.setWaybillCode(entity.getBarCode());
        //组装图片附件
        mq.setAttachmentAddr(assembleAttachmentAddr(entity));
        logger.info("组装发送给客服破损数据-{}",JSON.toJSONString(mq));
        return mq;
    }

    /**
     * 组装客服需要的图片附件数据
     * @param entity
     * @return
     */
    private String assembleAttachmentAddr(JyBizTaskExceptionEntity entity){
        String attachmentAddr ="";
        JyExceptionDamageEntity damageEntity = new JyExceptionDamageEntity();
        damageEntity.setBizId(entity.getBizId());
        damageEntity.setSiteCode(entity.getSiteCode().intValue());

        List<String> allImageUrlList = new ArrayList();
        List<String> beforImageUrlList = this.getImageUrlList(damageEntity, JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode());
        if(!CollectionUtils.isEmpty(beforImageUrlList)){
            allImageUrlList.addAll(beforImageUrlList);
        }
        List<String> afterImageUrlList = this.getImageUrlList(damageEntity, JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode());
        if(!CollectionUtils.isEmpty(afterImageUrlList)){
            allImageUrlList.addAll(afterImageUrlList);
        }

        if(!CollectionUtils.isEmpty(allImageUrlList)){
            attachmentAddr = allImageUrlList.stream().collect(Collectors.joining(","));
        }
        return  attachmentAddr;
    }

    /**
     * 检验条件是否满足给客服发送消息
     *
     * @param barCode
     * @param qcReportJmqDto
     * @param damageEntity
     * @param updateExp
     * @param updateDamageEntity
     * @return
     */
    private boolean sendMQNoticCustomerCheck(String barCode, QcReportJmqDto qcReportJmqDto, JyBizTaskExceptionEntity exceptionEntity
            , JyExceptionDamageEntity damageEntity, JyBizTaskExceptionEntity updateExp, JyExceptionDamageEntity updateDamageEntity,Waybill waybill) {

        //自营单上报破损时，非全部破损情况提交后，不与客服交互，任务状态变更为直接下传 （选择 外破内破-》无残余价值）
        if (BusinessUtil.isSelf(waybill.getWaybillSign())) {
            if (!(Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(), damageEntity.getDamageType())
                    && Objects.equals(JyExceptionDamageEnum.InsideOutsideDamagedRepairTypeEnum.WORTHLESS.getCode(), damageEntity.getRepairType()))) {
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(JyExceptionDamageEnum.FeedBackTypeEnum.HANDOVER.getCode());
                logger.warn("自营单-非全部破损，不与客服交互!");
                return false;
            }
        }else {// 外单 选择外包装破损 -》修复 或者 直接下传 不发送客服
            if(Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode(), damageEntity.getDamageType())){
                //修复
                if(Objects.equals(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPAIR.getCode(), damageEntity.getRepairType())){
                    updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                    updateDamageEntity.setFeedBackType(JyExceptionDamageEnum.FeedBackTypeEnum.REPAIR_HANDOVER.getCode());
                    logger.warn("外单 选择外包装破损 -》修复");
                    return false;
                }else if(Objects.equals(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.HANDOVER.getCode(), damageEntity.getRepairType())){
                    updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                    updateDamageEntity.setFeedBackType(JyExceptionDamageEnum.FeedBackTypeEnum.HANDOVER.getCode());
                    logger.warn("外单 选择外包装破损 -》直接下传");
                    return false;
                }else {
                    logger.info("其他状态。。");
                }
            }
        }

        //一单多包裹的情况下，若上报外破内破，不发送消息给客服，直接下传
        if (waybill.getGoodNumber() > 1) {
            if (Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(), damageEntity.getDamageType())) {
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(JyExceptionDamageEnum.FeedBackTypeEnum.HANDOVER.getCode());
                logger.warn("一单多包裹的情况下，上报外破内破，不与客服交互！");
                return false;
            }
        }

        Integer damageCount = jyExceptionDamageDao.getDamageCountByBarCode(barCode);
        logger.info("{}-提报的次数-{}", barCode, damageCount);
        //非第一次上报
        if (damageCount != null && damageCount > 1) {
            JyExceptionDamageEntity earliestOne = jyExceptionDamageDao.getEarliestOneDamageRecordByBarCode(barCode);
            logger.info("{}-上次提报的内容-{}", barCode, JSON.toJSONString(earliestOne));
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
                    && Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(), damageEntity.getDamageType())
                    && Objects.equals(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode(), damageEntity.getRepairType())) {
                //已上报内破外破的包裹号，第二次报备外包装需更换时，不发送消息给客服，按上次反馈执行
                updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
                updateDamageEntity.setFeedBackType(earliestOne.getFeedBackType());
                logger.warn("非第一次上报，报备外包装需更换时不与客服交互");
                return false;
            }
        }
        //如果异常处理选择破损类型为：仅外包装破损；修复方式为：修复 任务状态变更为待执行-修复下传
        if(Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode(),damageEntity.getDamageType())
            && Objects.equals(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPAIR.getCode(),damageEntity.getRepairType())){
            updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
            updateDamageEntity.setFeedBackType(JyExceptionDamageEnum.FeedBackTypeEnum.REPAIR_HANDOVER.getCode());
            logger.warn("破损类型选择-仅外包装破损-修复");
            return false;
        }
        //如果异常处理选择 外破内破 - 内物轻微破损 不发送消息给客服 任务状态变更为待执行-直接下传
        if(Objects.equals(JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode(),damageEntity.getDamageType())
                && Objects.equals(JyExceptionDamageEnum.InsideOutsideDamagedRepairTypeEnum.INSIDE_MILD_DAMAGE.getCode(),damageEntity.getRepairType())){
            updateExp.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode());
            updateDamageEntity.setFeedBackType(JyExceptionDamageEnum.FeedBackTypeEnum.HANDOVER.getCode());
            logger.warn(" 外破内破-内物轻微破损 不与客服交互！");
            return false;
        }

        return true;
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
        logger.info("称重信息WeightVolumeEntity -{}", JsonHelper.toJson(entity));
        InvokeResult<Boolean> result = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
        logger.info("处理破损运单称重结果-{}", JsonHelper.toJson(result));
    }


    /**
     * 处理运单滞留上报信息
     */
    private void dealExpDamageStrandReport(JyBizTaskExceptionEntity exceptionEntity) {
        StrandReportRequest request = new StrandReportRequest();
        request.setBarcode(exceptionEntity.getBarCode());
        request.setSiteCode(exceptionEntity.getSiteCode().intValue());
        request.setSiteName(exceptionEntity.getSiteName());
        request.setUserName(exceptionEntity.getUpdateUserName());
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(exceptionEntity.getUpdateUserErp());
        if(baseStaff != null){
            request.setUserCode(baseStaff.getStaffNo());
        }
        request.setReasonCode(103);//滞留原因管理-破损异常
        request.setReasonMessage("破损异常");
        request.setReportType(ReportTypeEnum.WAYBILL_CODE.getCode());
        request.setBusinessType(10);//业务类型-正向
        request.setOperateTime(DateHelper.formatDateTime(new Date()));
        //调用滞留上报接口
        logger.info("破损调用运单滞留上报入参-{}", JsonHelper.toJson(request));
        InvokeResult<Boolean> report = strandService.report(request);
        logger.info("破损调用运单滞留上报结果-{}", JsonHelper.toJson(report));
    }




    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.getToProcessDamageCount", mState = {JProEnum.TP})
    public JdCResponse<JyDamageExceptionToProcessCountDto> getToProcessDamageCount(String positionCode) {
        String gridId = this.getGridRid(positionCode);
        if (StringUtils.isEmpty(gridId)) {
            return JdCResponse.fail("网格码不存在");
        }
        JyDamageExceptionToProcessCountDto toProcessCount = new JyDamageExceptionToProcessCountDto();
        String feedBackAddKey = JyExceptionDamageEnum.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId;
        String feedBackKey = JyExceptionDamageEnum.TO_PROCESS_DAMAGE_EXCEPTION + gridId;

        // 获取待处理破损异常新增数量
        Set<String> oldBizIdAddSet = redisClient.sMembers(feedBackAddKey);
        if (CollectionUtils.isEmpty(oldBizIdAddSet)) {
            toProcessCount.setToProcessAddCount(0);
        } else {
            toProcessCount.setToProcessAddCount(oldBizIdAddSet.size());
        }
        // 获取待处理破损异常数量
        Set<String> oldBizIdSet = redisClient.sMembers(feedBackKey);
        if (CollectionUtils.isEmpty(oldBizIdSet)) {
            toProcessCount.setToProcessCount(0);
            if (!CollectionUtils.isEmpty(oldBizIdAddSet)) {
                // 新增转未处理
                redisClient.sAdd(feedBackKey, oldBizIdAddSet.toArray(new String[0]));
                redisClient.del(feedBackAddKey);
            }
            return JdCResponse.ok(toProcessCount);
        }
        toProcessCount.setToProcessCount(oldBizIdSet.size());
        // 新增转未处理
        if (!CollectionUtils.isEmpty(oldBizIdAddSet)) {
            oldBizIdSet.addAll(oldBizIdAddSet);
            redisClient.sAdd(feedBackKey, oldBizIdSet.toArray(new String[0]));
            redisClient.del(feedBackAddKey);
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
        redisClient.del(JyExceptionDamageEnum.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId);
        redisClient.del(JyExceptionDamageEnum.TO_PROCESS_DAMAGE_EXCEPTION + gridId);
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

        damageDetail.setDamageTypeName(JyExceptionDamageEnum.DamagedTypeEnum.getNameByCode(damageDetail.getDamageType()));
        if (JyExceptionDamageEnum.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode().equals(damageDetail.getDamageType())) {
            damageDetail.setRepairTypeName(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.getNameByCode(damageDetail.getRepairType()));
        } else if (JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode().equals(damageDetail.getDamageType())) {
            damageDetail.setRepairTypeName(JyExceptionDamageEnum.InsideOutsideDamagedRepairTypeEnum.getNameByCode(damageDetail.getRepairType()));
        }
        damageDetail.setFeedBackTypeName(JyExceptionDamageEnum.FeedBackTypeEnum.getNameByCode(damageDetail.getFeedBackType()));
        //只有客服反馈为修复下传和更换包装下传时才有耗材信息
        if (damageDetail.getFeedBackType().equals(JyExceptionDamageEnum.FeedBackTypeEnum.REPAIR_HANDOVER.getCode())
        || damageDetail.getFeedBackType().equals(JyExceptionDamageEnum.FeedBackTypeEnum.REPLACE_PACKAGING_HANDOVER.getCode())) {
            List<JyDamageConsumableEntity> jyDamageConsumableEntities = jyDamageConsumableDao.selectByBizId(damageDetail.getBizId());
            List<Consumable> consumables = new ArrayList<>();
            jyDamageConsumableEntities.forEach(entity -> {
                Consumable consumable = new Consumable();
                consumable.setCode(entity.getConsumableCode());
                consumable.setName(entity.getConsumableName());
                consumable.setBarcode(entity.getConsumableBarcode());
                consumables.add(consumable);
            });
            damageDetail.setConsumables(consumables);
        }
        // 查询修复前图片
        damageDetail.setActualImageUrlList(this.getImageUrlList(oldEntity, JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode()));
        // 查询修复后图片
        if (JyExceptionDamageEnum.FeedBackTypeEnum.REPAIR_HANDOVER.getCode().equals(oldEntity.getFeedBackType())
            || JyExceptionDamageEnum.FeedBackTypeEnum.REPLACE_PACKAGING_HANDOVER.getCode().equals(oldEntity.getFeedBackType())) {
            damageDetail.setDealImageUrlList(this.getImageUrlList(oldEntity, JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode()));
        }
        return JdCResponse.ok(damageDetail);
    }

    @Override
    @Transactional
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.dealCustomerReturnDamageResult", mState = {JProEnum.TP})
    public void dealCustomerReturnDamageResult(JyExpCustomerReturnMQ returnMQ) {
        if((returnMQ.getExptId().split("_", 2)).length < 2){
            return ;
        }
        String bizId = returnMQ.getExptId().split("_", 2)[1];
        //回传状态
        String resultType = returnMQ.getResultType();
        JyExpCustomerReturnResultEnum resultEnum = JyExpCustomerReturnResultEnum.convertApproveEnum(resultType);
        if(resultEnum == null){
            logger.error("此任务-{}-客服返回状态不在处理范围内", bizId);
            return;
        }
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
        //新增任务信息，用于提示客户端
        writeToProcessDamage(bizId);
    }


    private List<String> getImageUrlList(JyExceptionDamageEntity oldEntity, String bizType) {
        JyAttachmentDetailQuery queryBefore = new JyAttachmentDetailQuery();
        queryBefore.setBizId(oldEntity.getBizId());
        queryBefore.setSiteCode(oldEntity.getSiteCode());
        queryBefore.setBizType(bizType);
        // 删除老数据
        List<JyAttachmentDetailEntity> entityList = jyAttachmentDetailService.queryDataListByCondition(queryBefore);
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
        JyBizTaskExceptionEntity entity = jyBizTaskExceptionDao.findByBizId(bizId);
        if (entity == null) {
            return;
        }
        String gridId = entity.getDistributionTarget();
        String feedBackAddKey = JyExceptionDamageEnum.TO_PROCESS_DAMAGE_EXCEPTION_ADD + gridId;
        Set<String> oldBizIdSet = redisClient.sMembers(feedBackAddKey);
        if (CollectionUtils.isEmpty(oldBizIdSet)) {
            redisClient.sAdd(feedBackAddKey, bizId);
            return;
        }
        oldBizIdSet.add(bizId);
        redisClient.sAdd(feedBackAddKey, oldBizIdSet.toArray(new String[0]));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JySanwuExceptionServiceImpl.processTaskOfDamage", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req) {
        logger.error("JySanwuExceptionServiceImpl.processTaskOfDamage req:{}", JSON.toJSONString(req));
        //仅当外包装破损且选择修复或更换外包装时 走终端校验耗材条码
        if (JyExceptionDamageEnum.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode().equals(req.getDamageType())
        && (JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPAIR.getCode().equals(req.getRepairType())
                || JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode().equals(req.getRepairType()))
        && req.getConsumables() != null) {//只有纸箱，防水袋，文件封需要填条码校验
            List<String> barcodes = req.getConsumables().stream().filter(e->
                e.getCode().equals(JyExceptionDamageEnum.ConsumableEnum.PAPER_BOX.getCode())
                ||e.getCode().equals(JyExceptionDamageEnum.ConsumableEnum.FILE_SEALING.getCode())
                ||e.getCode().equals(JyExceptionDamageEnum.ConsumableEnum.WATERPROOF_BAG.getCode())
            ).map(e -> e.getBarcode()).collect(Collectors.toList());
            if (Objects.nonNull(barcodes) && barcodes.size() > 0) {
                if (Boolean.FALSE.equals(consumableManager.checkConsumable(barcodes, req.getUserErp()))) {
                    return JdCResponse.fail("耗材条码有误");
                }
            }
        }
        JyExceptionDamageEntity entity = new JyExceptionDamageEntity();
        JyDamageConsumableEntity consumableEntity = new JyDamageConsumableEntity();
        try {
            this.setBaseInfoToEntity(req, entity);
            JyExceptionDamageEntity oldEntity = jyExceptionDamageDao.selectOneByBizId(req.getBizId());
            if (oldEntity != null) {
                entity.setId(oldEntity.getId());
            }
            if (oldEntity == null) {
                oldEntity = new JyExceptionDamageEntity();
                this.saveDamage(req, entity, oldEntity);
                if (req.getConsumables() != null) {
                    this.saveConsumables(req);
                }
                return JdCResponse.ok();
            }
            JyExceptionDamageEnum.FeedBackTypeEnum reqFeedBackTypeEnum = JyExceptionDamageEnum.FeedBackTypeEnum.getEnumByCode(oldEntity.getFeedBackType());
            if (reqFeedBackTypeEnum == null)  return JdCResponse.fail("客服反馈类型匹配失败" + req.getBizId());
            //只有更换包装下传和修复下传才保存包装耗材信息
            switch (reqFeedBackTypeEnum) {
                case DEFAULT:
                    // 提交破损信息
                    this.saveDamage(req, entity, oldEntity);
                    if (req.getConsumables() != null) {
                        this.saveConsumables(req);
                    }
                    break;
                case REPAIR_HANDOVER:
                    // 1.修复下传
                    this.repairOrReplacePackagingHandover(req, entity);
                    //称重
                    this.dealExpDamageWeightVolumeUploadAfterRepair(req, entity);
                    if (req.getConsumables() != null) {
                        this.saveConsumables(req);
                    }
                    break;
                case HANDOVER:
                    //2.直接下传
                    this.finishFlow(req, entity);
                    jyDamageConsumableDao.deleteByBizId(entity.getBizId());
                    break;
                case REPLACE_PACKAGING_HANDOVER:
                    //3.更换包装下传
                    this.repairOrReplacePackagingHandover(req, entity);
                    //称重
                    this.dealExpDamageWeightVolumeUploadAfterRepair(req, entity);
                    if (req.getConsumables() != null) {
                        this.saveConsumables(req);
                    }
                    break;
                case DESTROY:
                    //4.报废
                    this.finishFlow(req, entity);
                    //报废全程跟踪
                    this.sendScrapTraceOfDamage(req.getBizId());
                    jyDamageConsumableDao.deleteByBizId(entity.getBizId());
                    break;
                case REVERSE_RETURN:
                    //5.逆向退回
                    // 该运单在提报场地操作换单打印成功后，任务状态变更为已完成
                    // this.finishFlow(req, entity);
                    jyDamageConsumableDao.deleteByBizId(entity.getBizId());
                    break;
                case REPLENISH:
                    //6.补单/补差
                    this.finishFlow(req, entity);
                    //调用妥投接口
                    jyExceptionService.delivered(req.getBizId());
                    jyDamageConsumableDao.deleteByBizId(entity.getBizId());
                    break;
                default:
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
        logger.info("破损修复后或更换包装后触发称重-{}", JSON.toJSONString(entity));
        dealExpDamageWeightVolumeUpload(entity, false);

    }

    private void finishFlow(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
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
    }

    private void repairOrReplacePackagingHandover(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
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
        this.validateSaveDamageParams(req, oldEntity);
        this.copyRequestToEntity(req, entity);
        this.saveImages(req, entity);
        this.saveOrUpdate(entity);
    }

    private void validateSaveDamageParams(ExpDamageDetailReq req, JyExceptionDamageEntity oldEntity) {
        JyBizTaskExceptionEntity expTask = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (expTask == null) {
            throw new RuntimeException("bizId不存在!" + req.getBizId());
        }
        if (Objects.equals(JyExpStatusEnum.PROCESSING.getCode(), expTask.getStatus())) {
            throw new RuntimeException("任务在处理中不能重复提交!" + req.getBizId());
        }
        if (!JyExceptionDamageEnum.SaveTypeEnum.DRAFT.getCode().equals(req.getSaveType())
                && !JyExceptionDamageEnum.SaveTypeEnum.SBUMIT_NOT_FEEBACK.getCode().equals(req.getSaveType())) {
            throw new RuntimeException("保存类型错误!" + req.getBizId());
        }
        if (oldEntity != null && JyExceptionDamageEnum.SaveTypeEnum.SBUMIT_NOT_FEEBACK.getCode().equals(oldEntity.getSaveType())) {
            throw new RuntimeException("不能重复提交!" + req.getBizId());
        }
    }

    private void saveOrUpdate(JyExceptionDamageEntity entity) {
        if (entity.getId() == null) {
            // 修改task类型为磨损
            this.updateTaskExpType(entity);
            jyExceptionDamageDao.insertSelective(entity);
        } else {
            // 破损提交状态修改为处理中
//            if (JyExceptionDamageEnum.SaveTypeEnum.SBUMIT_NOT_FEEBACK.getCode().equals(entity.getSaveType())) {
//                this.updateTaskExpStatusToProcessing(entity);
//            }
            this.clearNotNeedUpdateFiled(entity);
            jyExceptionDamageDao.updateByBizId(entity);
        }
    }

    /**
     * 修改task类型为磨损
     *
     * @param entity
     */
    private void updateTaskExpType(JyExceptionDamageEntity entity) {
        JyBizTaskExceptionEntity updateExp = new JyBizTaskExceptionEntity();
        updateExp.setBizId(entity.getBizId());
        updateExp.setType(JyBizTaskExceptionTypeEnum.DAMAGE.getCode());
        updateExp.setUpdateUserErp(entity.getCreateErp());
        updateExp.setUpdateTime(new Date());
        jyBizTaskExceptionDao.updateByBizId(updateExp);
    }

    private void clearNotNeedUpdateFiled(JyExceptionDamageEntity entity) {
        entity.setSiteCode(null);
        entity.setSiteName(null);
    }

    private void saveImages(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        if (!CollectionUtils.isEmpty(req.getActualImageUrlList())) {
            this.saveImages(req.getActualImageUrlList(), JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode(), entity);
        } else if (!CollectionUtils.isEmpty(req.getDealImageUrlList())) {
            this.saveImages(req.getDealImageUrlList(), JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode(), entity);
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
            List<JyAttachmentDetailEntity> oldImageUrlList = jyAttachmentDetailService.queryDataListByCondition(query);
            if (!CollectionUtils.isEmpty(oldImageUrlList)) {
                JyAttachmentDetailQuery delParams = new JyAttachmentDetailQuery();
                List<String> deleteBizIdList = oldImageUrlList.stream().map(JyAttachmentDetailEntity::getBizId).collect(Collectors.toList());
                delParams.setBizIdList(deleteBizIdList);
                delParams.setSiteCode(entity.getSiteCode());
                delParams.setBizType(bitType);
                delParams.setUserErp(entity.getUpdateErp());
                jyAttachmentDetailService.deleteBatch(delParams);
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
    private void setBaseInfoToEntity(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        // 获取erp相关信息
        BaseStaffSiteOrgDto baseStaffByErp = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaffByErp == null) {
            throw new RuntimeException("登录人ERP有误!" + req.getUserErp());
        }
        this.copyErpToEntity(baseStaffByErp, entity);
        
        PositionDetailRecord positionDetail = jyExceptionService.getPosition(req.getPositionCode());
        if (positionDetail == null) {
            throw new RuntimeException("岗位码不能为空!" + req.getUserErp());
        }
        entity.setSiteCode(positionDetail.getSiteCode());
        entity.setSiteName(positionDetail.getSiteName());

        this.setAboutTaskInfo(req, entity);
    }

    /**
     * 校验或设置Task相关信息
     *
     * @param req
     * @param entity
     * @return
     */
    private void setAboutTaskInfo(ExpDamageDetailReq req, JyExceptionDamageEntity entity) {
        // 获取task相关信息
        JyBizTaskExceptionEntity bizEntity = jyBizTaskExceptionDao.findByBizId(req.getBizId());
        if (bizEntity == null) {
            throw new RuntimeException("无相关任务!bizId=" + req.getBizId());
        }
        entity.setBizId(bizEntity.getBizId());
        if (entity.getId() == null) {
            entity.setBarCode(bizEntity.getBarCode());
        }
    }

    private void copyErpToEntity(BaseStaffSiteOrgDto baseStaffByErp, JyExceptionDamageEntity entity) {
        if (entity.getId() == null) {
            entity.setCreateErp(baseStaffByErp.getAccountNumber());
            entity.setCreateTime(new Date());
            entity.setCreateStaffName(baseStaffByErp.getStaffName());
        }
//        entity.setSiteCode(baseStaffByErp.getSiteCode());
//        entity.setSiteName(baseStaffByErp.getSiteName());
        entity.setUpdateErp(baseStaffByErp.getAccountNumber());
        entity.setUpdateTime(new Date());
        entity.setUpdateStaffName(baseStaffByErp.getStaffName());

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
        if (StringUtils.isEmpty(barCode)) {
            return JdCResponse.fail("barCode不能为空"); 
        }
        JdCResponse<List<JyExceptionPackageTypeDto>> result = new JdCResponse<>();
        List<JyExceptionPackageTypeDto> dtoList = new ArrayList<>();
        //保存异常包裹为第一层级
        Arrays.stream(JyExceptionDamageEnum.ExceptionPackageTypeEnum.values()).forEach(type -> {
            if (JyExceptionDamageEnum.ExceptionPackageTypeEnum.DAMAGE.getCode().equals(type.getCode())) {
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
        Arrays.stream(JyExceptionDamageEnum.DamagedTypeEnum.values()).forEach(type -> {
            if (JyExceptionDamageEnum.DamagedTypeEnum.OUTSIDE_PACKING_DAMAGE.getCode().equals(type.getCode())) {
                list.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName(), getOutPackingDamagedRepairTypeList(barCode)));
            } else if (JyExceptionDamageEnum.DamagedTypeEnum.INSIDE_OUTSIDE_DAMAGE.getCode().equals(type.getCode())) {
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
        list.add(new JyExceptionPackageTypeDto(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPAIR.getCode(),
                JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPAIR.getName()));
        if (this.checkDamageChangePackageRepair(barCode)) {
            list.add(new JyExceptionPackageTypeDto(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode(),
                    JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getName()));
            list.add(new JyExceptionPackageTypeDto(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.HANDOVER.getCode(),
                    JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.HANDOVER.getName(), true));
        } else {
            list.add(new JyExceptionPackageTypeDto(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getCode(),
                    JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.REPLACE_PACKAGING.getName(), true));
            list.add(new JyExceptionPackageTypeDto(JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.HANDOVER.getCode(),
                    JyExceptionDamageEnum.OutPackingDamagedRepairTypeEnum.HANDOVER.getName()));
        }
        return list;
    }

    /**
     * 获取内破外破修复类型列表
     */
    private List<JyExceptionPackageTypeDto> getInsideOutsideDamagedRepairTypeList() {
        List<JyExceptionPackageTypeDto> list = new ArrayList<>();
        Arrays.stream(JyExceptionDamageEnum.InsideOutsideDamagedRepairTypeEnum.values()).forEach(type -> list.add(new JyExceptionPackageTypeDto(type.getCode(), type.getName())));
        return list;
    }


    private Map<String, List<JyAttachmentDetailEntity>> getDamageImageListByBizIds(List<JyExceptionDamageEntity> detailList, Boolean isCompleted) {
        if (CollectionUtils.isEmpty(detailList)) {
            return new HashMap<>();
        }
        List<String> bizIdList = detailList.stream().map(JyExceptionDamageEntity::getBizId).collect(Collectors.toList());
        JyAttachmentDetailQuery query = new JyAttachmentDetailQuery();
        query.setBizIdList(bizIdList);
        query.setSiteCode(detailList.get(0).getSiteCode());
        if (isCompleted) {
            query.setBizType(JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_AFTER.getCode());
        } else {
            query.setBizType(JyAttachmentBizTypeEnum.DAMAGE_EXCEPTION_PACKAGE_BEFORE.getCode());
        }
        query.setPageSize(200);
        List<JyAttachmentDetailEntity> entityList = jyAttachmentDetailService.queryDataListByCondition(query);
        if (CollectionUtils.isEmpty(entityList)) {
            return new HashMap<>();
        }
        return entityList.stream().collect(Collectors.groupingBy(JyAttachmentDetailEntity::getBizId));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.getDamageDetailListByBizIds", mState = {JProEnum.TP})
    public Map<String, JyExceptionDamageDto> getDamageDetailMapByBizIds(List<String> bizIdList, Integer status) {
        Map<String, JyExceptionDamageDto> damageDtoMap = new HashMap<>();
        if (CollectionUtils.isEmpty(bizIdList) || status == null) {
            return damageDtoMap;
        }
        // 批量查询破损数据
        List<JyExceptionDamageEntity> entityList = jyExceptionDamageDao.getTaskListOfDamage(bizIdList);
        if (CollectionUtils.isEmpty(entityList)) {
            return damageDtoMap;
        }
        // 批量查询图片数据
        Boolean isCompleted = JyExpStatusEnum.COMPLETE.getCode() == status;
        Map<String, List<JyAttachmentDetailEntity>> attachmentDetailEntityMap = this.getDamageImageListByBizIds(entityList, isCompleted);
        for (JyExceptionDamageEntity entity : entityList) {
            JyExceptionDamageDto damageDto = new JyExceptionDamageDto();
            damageDto.setFeedBackType(entity.getFeedBackType());
            List<JyAttachmentDetailEntity> attachmentDetailEntityList = attachmentDetailEntityMap.get(entity.getBizId());
            if (!CollectionUtils.isEmpty(attachmentDetailEntityList)) {
                List<String> imageList = attachmentDetailEntityList.stream().map(JyAttachmentDetailEntity::getAttachmentUrl).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(imageList)) {
                    damageDto.setImageUrlList(imageList);
                }
            }
            damageDtoMap.put(entity.getBizId(), damageDto);
        }
        return damageDtoMap;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyDamageExceptionServiceImpl.dealDamageExpTaskStatus", mState = {JProEnum.TP})
    public void dealDamageExpTaskStatus(String waybillCode,Integer siteCode) {
        try{
            logger.info("处理破损数据任务状态-{}",waybillCode);
            waybillCode = WaybillUtil.getWaybillCode(waybillCode);
            String cacheKey =  Constants.EXP_WAYBILL_CACHE_KEY_PREFIX + waybillCode;
            String cacheValue = redisClientOfJy.get(cacheKey);
            logger.info("破损数据缓存任务-{}",cacheValue);
            if(StringUtils.isBlank(cacheValue)){
                return;
            }
            logger.info("破损运单命中-{}",waybillCode);
            List<String> bizIds = new ArrayList<>();
            if(siteCode != null){
                String bizId = getBizId(waybillCode, siteCode);
                JyBizTaskExceptionEntity expTask = jyBizTaskExceptionDao.findByBizId(bizId);
                if(expTask == null){
                    return ;
                }
                if(Objects.equals(JyExpStatusEnum.TO_PICK.getCode(),expTask.getStatus())
                        || Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(),expTask.getStatus())
                        || Objects.equals(JyExpStatusEnum.PROCESSING.getCode(),expTask.getStatus())){
                    bizIds.add(bizId);
                }

                if(CollectionUtils.isEmpty(bizIds)){
                  return ;
                }
                logger.info("操作场地不为空 更改任务状态-{}",bizIds);
            }else {
                List<JyBizTaskExceptionEntity> findBizIds = jyBizTaskExceptionDao.selectListByBarCode(waybillCode);
                if(CollectionUtils.isEmpty(findBizIds)){
                    return ;
                }
                for (JyBizTaskExceptionEntity entity :findBizIds) {
                    if(Objects.equals(JyExpStatusEnum.TO_PICK.getCode(),entity.getStatus())
                            || Objects.equals(JyExpStatusEnum.TO_PROCESS.getCode(),entity.getStatus())
                            || Objects.equals(JyExpStatusEnum.PROCESSING.getCode(),entity.getStatus())){
                        bizIds.add(entity.getBizId());
                    }
                }
                logger.info("操作场地空 更改任务状态-{}",bizIds);
            }
            if(CollectionUtils.isEmpty(bizIds)){
                return ;
            }
            jyBizTaskExceptionDao.updateExceptionTaskStatusByBizIds(JyExpStatusEnum.COMPLETE.getCode(), JyBizTaskExceptionProcessStatusEnum.DONE.getCode(),bizIds);
        }catch (Exception e){
            logger.error("处理破损异常任务状态异常-{}",waybillCode,e);
        }
    }

    @Override
    @Transactional
    public JdCResponse<Boolean> dealDamageExpTaskOverTwoDags() {
        logger.info("超48小时的破损任务-");
        JdCResponse response = new JdCResponse();
        response.toSucceed();
        //查询 处理中-客服介入中的超48小时的任务列表
        int hours = dmsConfigManager.getPropertyConfig().getJyExceptionDamageTaskCustomerNotReturnHours();
        JyBizTaskExceptionEntity query = new JyBizTaskExceptionEntity();
        query.setType(JyBizTaskExceptionTypeEnum.DAMAGE.getCode());
        query.setStatus(JyExpStatusEnum.PROCESSING.getCode());
        query.setProcessingStatus(JyBizTaskExceptionProcessStatusEnum.WAITER_INTERVENTION.getCode());
        query.setTs(DateHelper.addHours(new Date(),-hours));
        logger.info("查询超48小时破损任务入参-{}",JSON.toJSONString(query));
        List<String> bizIds = jyBizTaskExceptionDao.getExceptionTaskListOverTime(query);
        if(!CollectionUtils.isEmpty(bizIds)){
            logger.info("超48小时破损任务更新-{}",JSON.toJSONString(bizIds));
            //处理中-待执行
            jyBizTaskExceptionDao.updateExceptionTaskStatusByBizIds(JyExpStatusEnum.PROCESSING.getCode(),JyBizTaskExceptionProcessStatusEnum.WAITER_EXECUTION.getCode(),bizIds);
            //直接下传
            jyExceptionDamageDao.updateDamageStatusByBizIds(JyExceptionDamageEnum.FeedBackTypeEnum.HANDOVER.getCode(),bizIds);
        }
        return response;

    }

    @Override
    public JdCResponse<List<Consumable>> getConsumables() {
        List<Consumable> consumables = new ArrayList<>();
        for (JyExceptionDamageEnum.ConsumableEnum e : JyExceptionDamageEnum.ConsumableEnum.values()) {
            Consumable consumable = new Consumable();
            consumable.setCode(e.getCode());
            consumable.setName(e.getName());
            consumables.add(consumable);
        }
        JdCResponse<List<Consumable>> response
                = new JdCResponse(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS, consumables);
        return response;
    }

    private void saveConsumables(ExpDamageDetailReq req) {
        List<JyDamageConsumableEntity> consumableEntities = new ArrayList<>();
        List<com.jd.bluedragon.common.dto.jyexpection.response.Consumable> consumables = req.getConsumables();
        PositionDetailRecord positionDetail = jyExceptionService.getPosition(req.getPositionCode());
        consumables.forEach(consumable -> {
            JyDamageConsumableEntity consumableEntity = new JyDamageConsumableEntity();
            consumableEntity.setDamageBizId(req.getBizId());
            consumableEntity.setProvinceAgencyCode(positionDetail.getProvinceAgencyCode());
            consumableEntity.setProvinceAgencyName(positionDetail.getProvinceAgencyName());
            consumableEntity.setSiteCode(positionDetail.getSiteCode());
            consumableEntity.setSiteName(positionDetail.getSiteName());
            consumableEntity.setAreaHubCode(positionDetail.getAreaHubCode());
            consumableEntity.setAreaHubName(positionDetail.getAreaHubName());
            consumableEntity.setAreaCode(positionDetail.getAreaCode());
            consumableEntity.setAreaName(positionDetail.getAreaName());
            consumableEntity.setGridNo(positionDetail.getGridNo());
            consumableEntity.setGridCode(positionDetail.getGridCode());
            consumableEntity.setGridName(positionDetail.getGridName());
            consumableEntity.setOwnerUserErp(getOwnerUserErpByGridCode(positionDetail.getGridCode()));
            consumableEntity.setConsumableCode(consumable.getCode());
            consumableEntity.setConsumableName(JyExceptionDamageEnum.ConsumableEnum.getNameByCode(consumable.getCode()));
            consumableEntity.setConsumableBarcode(consumable.getBarcode());
            consumableEntity.setOperatorErp(req.getUserErp());
            consumableEntity.setOperateTime(new Date());
            consumableEntities.add(consumableEntity);
        });
        jyDamageConsumableDao.deleteByBizId(req.getBizId());
        jyDamageConsumableDao.insertBatch(consumableEntities);
    }

    private String getOwnerUserErpByGridCode(String gridCode) {
        WorkStationGridQuery query = new WorkStationGridQuery();
        query.setGridCode(gridCode);
        Result<PageDto<WorkStationGrid>> pageDtoResult = workStationGridManager.queryPageList(query);
        if (pageDtoResult.isSuccess() && Objects.nonNull(pageDtoResult.getData())) {
            List<WorkStationGrid> result = pageDtoResult.getData().getResult();
            if (Objects.nonNull(result) && result.size() > 0) {
                return result.get(0).getOwnerUserErp();
            }
        }
        return null;
    }
}
