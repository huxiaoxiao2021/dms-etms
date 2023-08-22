package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpContrabandReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionContrabandEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionContrabandDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExpContrabandNoticCustomerMQ;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.exception.JyContrabandExceptionService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.ASCPContants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.SwitchWaybillDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: ext.xuwenrui
 * @Date: 2023/8/15 10:18
 * @Description: 违禁品上报 service 实现
 */
@Service
public class JyContrabandExceptionServiceImpl implements JyContrabandExceptionService {
    private final Logger logger = LoggerFactory.getLogger(JyContrabandExceptionServiceImpl.class);

    public static final String WAYBILL_TRACK_SECURITY_CHECK_DESC="安检查验";
    public static final String WAYBILL_TRACK_SECURITY_CHECK_DETAIN_PACKAGE_DESC = "安检查验扣件";

    public static final String WAYBILL_TRACK_SECURITY_CHECK_RETURNED_PACKAGE_DESC = "安检查验退运";


    @Autowired
    private JyAttachmentDetailService jyAttachmentDetailService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private JyExceptionContrabandDao jyExceptionContrabandDao;

    @Autowired
    @Qualifier("jyExceptionContrabandUploadProducer")
    private DefaultJMQProducer jyExceptionContrabandUploadProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("jyExceptionContrabandNoticCustomerProducer")
    private DefaultJMQProducer jyExceptionContrabandNoticCustomerProducer;

    @Autowired
    public WaybillQueryManager waybillQueryManager;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyContrabandExceptionServiceImpl.processTaskOfContraband", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req) {
        try {
            logger.info("processTaskOfContraband req:{}", JSON.toJSONString(req));
            this.validateReq(req);
            JyExceptionContrabandEntity entity = buildEntity(req);
            this.saveImages(req, entity);
            jyExceptionContrabandDao.insertSelective(entity);

            JyExceptionContrabandDto dto = new JyExceptionContrabandDto();
            BeanUtils.copyProperties(entity,dto);
            jyExceptionContrabandUploadProducer.send(entity.getBizId(), JsonHelper.toJson(dto));
            logger.info("违禁品上报发送MQ-{}",JSON.toJSONString(dto));
        } catch (Exception e) {
            logger.error("提交违禁品上报报错:", e);
            return JdCResponse.fail(e.getMessage());
        }
        return JdCResponse.ok();
    }

    @Override
    public void dealContrabandUploadData(JyExceptionContrabandDto dto) throws InterruptedException {



        JyExceptionContrabandEnum.ContrabandTypeEnum enumResult = JyExceptionContrabandEnum.ContrabandTypeEnum.getEnumByCode(dto.getContrabandType());
        if(enumResult == null){
            return;
        }
        //写运单维度的全程跟踪
        sendWaybillBDTrance(dto, WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK,WAYBILL_TRACK_SECURITY_CHECK_DESC);
       switch (enumResult){
           case DETAIN_PACKAGE:
               Thread.sleep(5000);

               //

               break;
           case AIR_TO_LAND:

               sendPackageBDTrance(dto, WaybillStatus.WAYBILL_TRACK_RETURNED_PACKAGE,WAYBILL_TRACK_SECURITY_CHECK_RETURNED_PACKAGE_DESC);
               break;
       }


        if(Objects.equals(JyExceptionContrabandEnum.ContrabandTypeEnum.DETAIN_PACKAGE.getCode(),dto.getContrabandType())){


        }else if(Objects.equals(JyExceptionContrabandEnum.ContrabandTypeEnum.AIR_TO_LAND.getCode(),dto.getContrabandType())){

        }
        JyExpContrabandNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(dto);
        try {
            jyExceptionContrabandNoticCustomerProducer.send(dto.getBizId(), JsonHelper.toJson(mq));
            logger.info("违禁品上报发送客服-{}",JsonHelper.toJson(mq));
        } catch (JMQException e) {
            e.printStackTrace();
        }
    }

    /**
     * 运单维度全程跟踪
     * @param dto
     * @param opreateType
     * @param operatorDesp
     */
    private void sendWaybillBDTrance(JyExceptionContrabandDto dto,int opreateType,String operatorDesp){

        if(StringUtils.isBlank(dto.getBarCode())){
            logger.warn("单号为空!");
            return ;
        }
        String waybillCode = WaybillUtil.getWaybillCode(dto.getBarCode());
        BdTraceDto traceDto = new BdTraceDto();
        traceDto.setPackageBarCode(waybillCode);
        traceDto.setWaybillCode(waybillCode);
        traceDto.setOperateType(opreateType);
        traceDto.setOperatorDesp(operatorDesp);
        traceDto.setOperatorSiteId(dto.getSiteCode());
        traceDto.setOperatorSiteName(dto.getSiteName());
        traceDto.setOperatorUserName(dto.getCreateStaffName());

        traceDto.setOperatorTime(new Date());
        traceDto.setWaybillTraceType(1);
        logger.info("发送运单全程跟踪信息-{}",JSON.toJSONString(traceDto));
        waybillQueryManager.sendBdTrace(traceDto);
    }

    /**
     * 包裹维度全程跟踪
     * @param dto
     * @param opreateType
     * @param operatorDesp
     */
    private void sendPackageBDTrance(JyExceptionContrabandDto dto,int opreateType,String operatorDesp){

        if(StringUtils.isBlank(dto.getBarCode())){
            logger.warn("单号为空!");
            return ;
        }
        if(!WaybillUtil.isPackageCode(dto.getBarCode())){
            logger.warn("barCode 不是包裹号!");
        }
        BdTraceDto traceDto = new BdTraceDto();
        traceDto.setPackageBarCode(dto.getBarCode());
        traceDto.setWaybillCode(dto.getBarCode());
        traceDto.setOperateType(opreateType);
        traceDto.setOperatorDesp(operatorDesp);
        traceDto.setOperatorSiteId(dto.getSiteCode());
        traceDto.setOperatorSiteName(dto.getSiteName());
        traceDto.setOperatorUserName(dto.getCreateStaffName());

        traceDto.setOperatorTime(new Date());
        traceDto.setWaybillTraceType(3);
        logger.info("发送运单全程跟踪信息-{}",JSON.toJSONString(traceDto));
        waybillQueryManager.sendBdTrace(traceDto);
    }

    /**
     * 发送客服破损数据组装
     *
     * @return
     */
    private JyExpContrabandNoticCustomerMQ coverToDamageNoticCustomerMQ(JyExceptionContrabandDto dto) {
        JyExpContrabandNoticCustomerMQ mq = new JyExpContrabandNoticCustomerMQ();
        mq.setBusinessId(ASCPContants.CONTRABAND_BUSINESS_ID);
        mq.setExptId(ASCPContants.CONTRABAND_BUSINESS_ID + "_" + dto.getBizId());
        mq.setDealType(ASCPContants.DEAL_TYPE);
        mq.setCodeInfo(WaybillUtil.getWaybillCode(dto.getBarCode()));
        mq.setCodeType(ASCPContants.CODE_TYPE);
        mq.setExptCreateTime(DateHelper.formatDateTime(new Date()));
        mq.setExptOneLevel(ASCPContants.CONTRABAND_EXPT_ONE_LEVEL);
        mq.setExptOneLevelName(ASCPContants.CONTRABAND_EXPT_ONE_LEVEL_NAME);
        mq.setExptTwoLevel(ASCPContants.CONTRABAND_EXPT_TWO_LEVEL);
        mq.setExptTwoLevelName(ASCPContants.CONTRABAND_EXPT_TWO_LEVEL_NAME);
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(new Integer(dto.getSiteCode()));
        if (baseSite != null) {
            mq.setStartOrgCode(baseSite.getOrgId().toString());
            mq.setStartOrgName(baseSite.getOrgName());
            mq.setProvinceAgencyCode(StringUtils.isNotBlank(baseSite.getProvinceAgencyCode()) ? baseSite.getProvinceAgencyCode().toString() : "");
            mq.setProvinceAgencyName(StringUtils.isNotBlank(baseSite.getProvinceAgencyName()) ? baseSite.getProvinceAgencyName() : "");
        }

        Waybill waybill = waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(dto.getBarCode()));
        if(waybill == null){
           logger.error("获取运单信息失败-{}",dto.getBarCode());
           throw new RuntimeException("获取运单信息失败!");
        }

        String waybillSign = waybill.getWaybillSign();
        if (BusinessUtil.isSelf(waybillSign)) {
            mq.setWaybillType(ASCPContants.WAYBILL_TYPE_SELF);
        } else {//外单
            mq.setWaybillType(ASCPContants.WAYBILL_TYPE_OTHER);
        }
        mq.setWaybillCode(WaybillUtil.getWaybillCode(dto.getBarCode()));
        return mq;
    }

    private JyExceptionContrabandEntity buildEntity(ExpContrabandReq req) {
        JyExceptionContrabandEntity entity = new JyExceptionContrabandEntity();
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaff != null) {
            entity.setSiteCode(baseStaff.getSiteCode());
            entity.setSiteName(baseStaff.getSiteName());
            entity.setCreateErp(baseStaff.getAccountNumber());
            entity.setCreateStaffName(baseStaff.getStaffName());
            entity.setCreateTime(new Date());
            entity.setUpdateErp(baseStaff.getAccountNumber());
            entity.setUpdateStaffName(baseStaff.getAccountNumber());
            entity.setUpdateTime(entity.getCreateTime());
        }

        entity.setContrabandType(req.getContrabandType());
        String bizId = req.getBarCode() + "_" + entity.getSiteCode();
        entity.setBizId(bizId);
        entity.setBarCode(req.getBarCode());
        entity.setDescription(req.getDescription());
        return entity;
    }

    /**
     * 保存图片
     *
     * @param entity
     */
    private void saveImages(ExpContrabandReq req, JyExceptionContrabandEntity entity) {
        if (!CollectionUtils.isEmpty(req.getImageUrlList())) {
            List<JyAttachmentDetailEntity> annexList = Lists.newArrayList();
            for (String proveUrl : req.getImageUrlList()) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                annexEntity.setBizId(entity.getBizId());
                annexEntity.setSiteCode(entity.getSiteCode());
                annexEntity.setBizType(JyAttachmentBizTypeEnum.CONTRABAND_UPLOAD_EXCEPTION.getCode());
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
     * 校验参数
     *
     * @param req
     */
    private void validateReq(ExpContrabandReq req) {
        if (req == null) {
            throw new RuntimeException("请求参数不能为空");
        }
        if (req.getContrabandType() == null) {
            throw new RuntimeException("违禁品类型不能为空");
        }
        boolean noneMatchContrabandType = Arrays.stream(JyExceptionContrabandEnum.ContrabandTypeEnum.values())
                .noneMatch(o -> o.getCode().equals(req.getContrabandType()));
        if (noneMatchContrabandType) {
            throw new RuntimeException("违禁品类型错误");
        }
        if (StringUtils.isEmpty(req.getBarCode())) {
            throw new RuntimeException("包裹号不能为空");
        }
        if (StringUtils.isEmpty(req.getUserErp())) {
            throw new RuntimeException("用户ERP不能为空");
        }

        JyExceptionContrabandEntity query = new JyExceptionContrabandEntity();
        query.setSiteCode(req.getSiteId());
        query.setBarCode(req.getBarCode());
        query.setContrabandType(req.getContrabandType());
        List<JyExceptionContrabandEntity> list = jyExceptionContrabandDao.selectByParams(query);
        if (!CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("请勿重复提交");
        }
    }
}
