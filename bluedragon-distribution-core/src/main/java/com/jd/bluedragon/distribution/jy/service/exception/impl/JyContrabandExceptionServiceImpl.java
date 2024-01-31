package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpContrabandReq;
import com.jd.bluedragon.common.dto.jyexpection.response.AbnormalReasonResp;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionContrabandEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpNoticCustomerExpReasonEnum;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.IAbnPdaAPIManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.BlockerQueryWSJsfManager;
import com.jd.bluedragon.core.jsf.waybill.WaybillReverseManager;
import com.jd.bluedragon.distribution.api.request.ArTransportModeChangeDto;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.exception.JyExceptionContrabandDao;
import com.jd.bluedragon.distribution.jy.dto.collectpackage.CancelCollectPackageDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExpContrabandNoticCustomerMQ;
import com.jd.bluedragon.distribution.jy.manager.AbnormalReasonOfZKManager;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.collectpackage.JyBizTaskCollectPackageService;
import com.jd.bluedragon.distribution.jy.service.exception.JyContrabandExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.bluedragon.distribution.sorting.domain.SortingDto;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.enums.WaybillFlowTypeEnum;
import com.jd.bluedragon.utils.ASCPContants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.cp.wbms.client.dto.SubmitWaybillResponse;
import com.jd.cp.wbms.client.enums.RejectionEnum;
import com.jd.etms.blocker.dto.BlockerApplyDto;
import com.jd.etms.blocker.dto.CommonDto;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.handler.WaybillSyncParameter;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wl.cs.abnormal.portal.api.dto.reason.AbnormalReasonDto;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.AttachInfoParam;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.CodeTypeEnum;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ReportRecord;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.UserTypeEnum;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
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
import java.util.stream.Collectors;

import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.SCRAP_WAYBILL_INTERCEPT_HINT_CODE;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isScrapWaybill;
import static com.jd.bluedragon.common.dto.operation.workbench.enums.ContrabandImageUrlEnum.*;
import static com.jd.bluedragon.common.dto.operation.workbench.enums.JyExceptionContrabandEnum.ContrabandTypeEnum.AIR_TO_LAND;
import static com.jd.bluedragon.enums.WaybillFlowTypeEnum.HK_OR_MO;
import static com.jd.bluedragon.enums.WaybillFlowTypeEnum.INTERNATION;
import static com.jd.bluedragon.utils.BusinessHelper.getWaybillFlowType;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;


/**
 * @Author: ext.xuwenrui
 * @Date: 2023/8/15 10:18
 * @Description: 违禁品上报 service 实现
 */
@Service
public class JyContrabandExceptionServiceImpl implements JyContrabandExceptionService {
    private final Logger logger = LoggerFactory.getLogger(JyContrabandExceptionServiceImpl.class);



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
    @Qualifier("dmsContrabandMainLandNoticeKFProducer")
    private DefaultJMQProducer dmsContrabandMainLandNoticeKFProducer;

    @Autowired
    @Qualifier("dmsContrabandHKOrMONoticeKFProducer")
    private DefaultJMQProducer dmsContrabandHKOrMONoticeKFProducer;

    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private WaybillStatusService waybillStatusService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private WaybillReverseManager waybillReverseManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private DmsConfigManager dmsConfigManager;
    @Autowired
    private BlockerQueryWSJsfManager blockerQueryWSJsfManager;
    @Autowired
    private JyBizTaskCollectPackageService jyBizTaskCollectPackageService;
    @Autowired
    private SortingService sortingService;

    @Autowired
    @Qualifier("abnormalReasonManagerOfZK")
    private AbnormalReasonOfZKManager abnormalReasonManagerOfZK;

    @Autowired
    private IAbnPdaAPIManager iAbnPdaAPIManager;

    @Autowired
    @Qualifier("arTransportModeChangeProducer")
    private DefaultJMQProducer arTransportModeChangeProducer;

    private static final String SORTING_REPORT_SYSTEM = "20";
    private static final String SORTING_REPORT_ATTACH_TYPE = "img";

    private static final Integer ALL_SUCCESS = 5;

    private static final String CONTRABAND_FIRST_REASON_LEVEL_CODE = "370000";


    @Override
    public JdCResponse<List<AbnormalReasonResp>> getAbnormalReason() {
        JdCResponse<List<AbnormalReasonResp>> response = new JdCResponse<>();
        List<AbnormalReasonDto> abnormalReasonDtoList = abnormalReasonManagerOfZK.queryAbnormalReasonListBySystemCode();
        if (!CollectionUtils.isEmpty(abnormalReasonDtoList)) {
            List<AbnormalReasonResp> abnormalReasonRespList =
                    abnormalReasonDtoList.stream()
                            // 过滤掉一级为非违禁品的异常类型
                            .filter(item -> !Objects.equals(item.getReasonLevel(), INTEGER_ONE.byteValue())
                                    || Objects.equals(String.valueOf(item.getCode()), CONTRABAND_FIRST_REASON_LEVEL_CODE))
                            .map(item -> {
                                AbnormalReasonResp abnormalReasonResp = new AbnormalReasonResp();
                                BeanUtils.copyProperties(item, abnormalReasonResp);
                                return abnormalReasonResp;
                            }).collect(Collectors.toList());
            response.setData(abnormalReasonRespList);
        }
        return response;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.JyContrabandExceptionServiceImpl.processTaskOfContraband", mState = {JProEnum.TP})
    public JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req) {
        logger.info("processTaskOfContraband req:{}", JSON.toJSONString(req));
        String existKey = String.format(CacheKeyConstants.CONTRABAND_LOCK_KEY,req.getBarCode());
        try {
            if (!redisClientOfJy.set(existKey, "1", 10, TimeUnit.SECONDS, false)) {
                return JdCResponse.fail("该违禁品上报正在提交,请稍后再试!");
            }

            BigWaybillDto bigWaybillDto = this.getBigWaybillDtoByWaybillCode(WaybillUtil.getWaybillCode(req.getBarCode()));
            this.validateReq(req,bigWaybillDto);
            JyExceptionContrabandEntity entity = buildEntity(req);
            this.saveImages(req, entity);
            jyExceptionContrabandDao.insertSelective(entity);
            //退回 调用百川逆向换单
            if (JyExceptionContrabandEnum.ContrabandTypeEnum.RETURN.getCode().equals(req.getContrabandType())) {
                BlockerApplyDto applyDto = covertBlockerApplyDto(entity);
                CommonDto<String> commonDto = blockerQueryWSJsfManager.applyIntercept(applyDto);
                if(commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS){
                    return JdCResponse.fail(req.getBarCode()+" 此单拦截失败!");
                }
                DmsWaybillReverseDTO reverseDTO = this.covertDmsWaybillReverseDTO(entity);
                JdResult<SubmitWaybillResponse> submitWaybillResult = waybillReverseManager.submitWaybill(reverseDTO);
                if(submitWaybillResult == null || !submitWaybillResult.getCode().equals(JdResult.CODE_SUC)){
                    return JdCResponse.fail(req.getBarCode()+" 此单逆向换单失败!");
                }

            }

            JyExceptionContrabandDto dto = new JyExceptionContrabandDto();
            BeanUtils.copyProperties(entity,dto);
            jyExceptionContrabandUploadProducer.send(entity.getBizId(), JsonHelper.toJson(dto));
            logger.info("违禁品上报发送MQ-{}",JSON.toJSONString(dto));

            // 新增自动取消集包:根据包裹号获取箱号
            SortingDto sortingDto = sortingService.getLastSortingInfoByPackageCode(dto.getBarCode());
            if (logger.isInfoEnabled()){
                logger.info("安检岗自动取消集包-根据包裹号查询集包箱号数据：包裹号：{},集包数据：{}", dto.getBarCode(), JsonHelper.toJson(sortingDto));
            }
            if (Objects.nonNull(sortingDto)){
                CancelCollectPackageDto cancelCollectPackageDto = buildCancelCollectPackageDto(dto, sortingDto);
                // 调用取消集包接口（新版，可删除扫描记录）
                boolean b = jyBizTaskCollectPackageService.cancelJyCollectPackage(cancelCollectPackageDto);
                if(b){
                    logger.info("该包裹关联集包取消成功！包裹号：{}", dto.getBarCode());
                }else{
                    logger.info("该包裹关联集包已经被取消或不存在！包裹号：{}", dto.getBarCode());
                }
            }

            // 该判断为了兼容新老版本，新版app上线后可删除
            if (!StringUtils.isEmpty(req.getFirstReasonLevelCode())) {
                // 调用质控接口，上报异常
                List<ReportRecord> reportRecords =  convertReportRecord(req);
                logger.info("违禁品上报调用质控jsf, req={}", JsonHelper.toJson(reportRecords));
                JdCResponse<List<String>> reportResponse = iAbnPdaAPIManager.report(reportRecords);
                if (reportResponse == null || !ALL_SUCCESS.equals(reportResponse.getCode())) {
                    return JdCResponse.fail(req.getBarCode()+" 违禁品上报质控系统失败，请联系分拣小秘!");
                }

                // 如果是航空转路由，向路由发送消息
                if (AIR_TO_LAND.getCode().equals(req.getContrabandType())) {
                    doSendArTransportModeChangeMq(req,bigWaybillDto);
                }
            }
        } catch (JyBizException e) {
            logger.error("jy取消集包服务异常{}", req.getBarCode(), e);
            return JdCResponse.fail("违禁品正常上报,取消集包失败:" + e.getMessage());
        } catch (Exception e) {
            logger.error("提交违禁品上报报错:", e);
            return JdCResponse.fail(e.getMessage());
        }finally {
            redisClientOfJy.del(existKey);
        }
        return JdCResponse.ok();
    }

    private BigWaybillDto getBigWaybillDtoByWaybillCode(String waybillCode) {
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryGoodList(true);
        choice.setQueryWaybillExtend(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
        if (baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
            return baseEntity.getData();
        }
        return null;
    }

    private void doSendArTransportModeChangeMq(ExpContrabandReq req, BigWaybillDto bigWaybillDto) {
        try{
            ArTransportModeChangeDto arTransportModeChangeDto = convertMessage(req, bigWaybillDto);
            logger.info("航空转陆运向路由发送消息{}", JsonHelper.toJson(arTransportModeChangeDto));
            arTransportModeChangeProducer.send(req.getBarCode(), JsonHelper.toJson(arTransportModeChangeDto));
        }catch (Exception e) {
            logger.error("包裹{}航空转陆运发送消息异常",req.getBarCode());
        }
    }

    private ArTransportModeChangeDto convertMessage(ExpContrabandReq request, BigWaybillDto bigWaybillDto) {
        ArTransportModeChangeDto dto = new ArTransportModeChangeDto();
        dto.setWaybillCode(WaybillUtil.getWaybillCode(request.getBarCode()));
        dto.setOperatorErp(request.getUserErp());
        dto.setSiteCode(request.getSiteId());
        Waybill waybill = bigWaybillDto.getWaybill();
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteId());
        if (siteOrgDto != null) {
            dto.setSiteName(siteOrgDto.getSiteName());
            dto.setAreaId(siteOrgDto.getOrgId());
        }
        dto.setFirstLevelCode(request.getFirstReasonLevelCode());
        dto.setFirstLevelName(request.getFirstReasonLevelName());
        dto.setSecondLevelCode(request.getSecondReasonLevelCode());
        dto.setSecondLevelName(request.getSecondReasonLevelName());
        dto.setThirdLevel(request.getThirdReasonLevelName());
        dto.setOperateTime(DateHelper.formatDateTime(new Date()));
        // 青龙业主编码
        dto.setCustomerCode(waybill.getCustomerCode());
        // 青龙寄件人pin码
        dto.setConsignerPin(waybill.getMemberId());
        dto.setConsigner(waybill.getConsigner());
        dto.setConsignerId(waybill.getConsignerId());
        dto.setConsignerMobile(waybill.getConsignerMobile());
        dto.setPickupSiteId(waybill.getPickupSiteId());
        dto.setPickupSiteName(waybill.getPickupSiteName());
        // 商家ID
        dto.setBusiId(waybill.getBusiId());
        // 商家名称
        dto.setBusiName(waybill.getBusiName());
        dto.setConsignmentName(waybillQueryManager.getConsignmentNameByWaybillDto(bigWaybillDto));
        //省区及枢纽
        dto.setProvinceAgencyCode(siteOrgDto == null ? null : siteOrgDto.getProvinceAgencyCode());
        dto.setProvinceAgencyName(siteOrgDto == null ? null : siteOrgDto.getProvinceAgencyName());
        dto.setAreaHubCode(siteOrgDto == null ? null : siteOrgDto.getAreaCode());
        dto.setAreaHubName(siteOrgDto == null ? null : siteOrgDto.getAreaName());
        dto.setPackageCode(request.getBarCode());
        return dto;
    }
    private List<ReportRecord> convertReportRecord(ExpContrabandReq req) {
        ReportRecord reportRecord=new ReportRecord();
        reportRecord.setCode(req.getBarCode());
        if (WaybillUtil.isWaybillCode(req.getBarCode())) {
            reportRecord.setCodeTypeEnum(CodeTypeEnum.WAYBILL);
        } else if (WaybillUtil.isPackageCode(req.getBarCode())) {
            reportRecord.setCodeTypeEnum(CodeTypeEnum.PACKAGE);
        }

        reportRecord.setFirstLevelExceptionId(Long.valueOf(req.getFirstReasonLevelCode()));
        reportRecord.setFirstLevelExceptionName(req.getFirstReasonLevelName());
        reportRecord.setSecondLevelExceptionId(Long.valueOf(req.getSecondReasonLevelCode()));
        reportRecord.setSecondLevelExceptionName(req.getSecondReasonLevelName());
        reportRecord.setThirdLevelExceptionId(Long.valueOf(req.getThirdReasonLevelCode()));
        reportRecord.setThirdLevelExceptionName(req.getThirdReasonLevelName());

        reportRecord.setRemark(req.getDescription());
        reportRecord.setCreateUser(req.getUserErp());
        reportRecord.setCreateDept(String.valueOf(req.getSiteId()));
        reportRecord.setCreateDeptName(req.getSiteName());
        reportRecord.setCreateTime(new Date());
        reportRecord.setReportSystem(SORTING_REPORT_SYSTEM);
        reportRecord.setUserType(UserTypeEnum.ERP);
        BaseStaffSiteOrgDto baseSiteBySiteId = baseMajorManager.getBaseSiteBySiteId(req.getSiteId());
        if (baseSiteBySiteId != null) {
            reportRecord.setCreateProvinceAgencyCode(baseSiteBySiteId.getProvinceAgencyCode());
            reportRecord.setCreateProvinceAgencyName(baseSiteBySiteId.getProvinceAgencyName());
        }
        List<AttachInfoParam> attachInfoParams = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getWaybillImageUrlList())) {
            for (String url : req.getWaybillImageUrlList()) {
                AttachInfoParam attachInfoParam = new AttachInfoParam();
                attachInfoParam.setFilePath(url);
                attachInfoParam.setAttachType(SORTING_REPORT_ATTACH_TYPE);
                attachInfoParam.setEvidenceCode(WAYBILL_IMAGE_TYPE.getCode());
                attachInfoParam.setFileName(WAYBILL_IMAGE_TYPE.getDesc());
                attachInfoParams.add(attachInfoParam);
            }
        }
        if (!CollectionUtils.isEmpty(req.getContrabandImageUrlList())) {
            for (String url : req.getContrabandImageUrlList()) {
                AttachInfoParam attachInfoParam = new AttachInfoParam();
                attachInfoParam.setFilePath(url);
                attachInfoParam.setAttachType(SORTING_REPORT_ATTACH_TYPE);
                attachInfoParam.setEvidenceCode(CONTRABAND_IMAGE_TYPE.getCode());
                attachInfoParam.setFileName(CONTRABAND_IMAGE_TYPE.getDesc());
                attachInfoParams.add(attachInfoParam);
            }
        }
        if (!CollectionUtils.isEmpty(req.getPanoramaImageUrlList())) {
            for (String url : req.getPanoramaImageUrlList()) {
                AttachInfoParam attachInfoParam = new AttachInfoParam();
                attachInfoParam.setFilePath(url);
                attachInfoParam.setAttachType(SORTING_REPORT_ATTACH_TYPE);
                attachInfoParam.setEvidenceCode(PANORAMA_IMAGE_TYPE.getCode());
                attachInfoParam.setFileName(PANORAMA_IMAGE_TYPE.getDesc());
                attachInfoParams.add(attachInfoParam);
            }
        }
        reportRecord.setAttachInfoParams(attachInfoParams);
        return Collections.singletonList(reportRecord);
    }

    /**
     * 封装 BlockerApplyDto
     * @return
     */
    private BlockerApplyDto covertBlockerApplyDto(JyExceptionContrabandEntity entity){

        BlockerApplyDto dto = new BlockerApplyDto();
        dto.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        dto.setExceptionId(Constants.WAYBILL_EXCEPTION_ID_8);//运营拦截
        dto.setInterceptType(Constants.WAYBILL_INTERCEPT_TYPE_4); // 分拣中心拦截
        dto.setReason(Constants.WAYBILL_INTERCEPT_REASON);
        dto.setApplyTime(new Date());
        dto.setOperatorUserName(entity.getCreateStaffName());
        dto.setOperatorSiteName(entity.getSiteName());
        dto.setCustomerCancelTime(new Date());
        return dto;
    }

    private DmsWaybillReverseDTO covertDmsWaybillReverseDTO(JyExceptionContrabandEntity entity){
        DmsWaybillReverseDTO dto = new DmsWaybillReverseDTO();
        dto.setSource(Constants.CHANGE_WAYBILL_OPERATE_SOURCE_SORT_CENTER);
        dto.setReverseType(RejectionEnum.WHOLE.getCode());
        dto.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        dto.setSortCenterId(entity.getSiteCode());
        dto.setSiteId(entity.getSiteCode());
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        if(waybill != null){
            dto.setPackageCount(waybill.getGoodNumber());
        }

        // 运单类型
        WaybillFlowTypeEnum waybillFlowType = getWaybillFlowType(waybill);
        if(HK_OR_MO.equals(waybillFlowType) || INTERNATION.equals(waybillFlowType)){
            dto.setReverseReasonCode(Constants.INTERCEPT_REVERSE_CODE_1);
        }
        dto.setReturnType(Constants.REVERSE_TYPE_REJECT_BACK);
        dto.setOperateTime(new Date());
        dto.setOperateUser(entity.getCreateStaffName());
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(entity.getCreateErp());
        if(baseStaff != null){
            dto.setOperateUserId(baseStaff.getStaffNo());
        }
        return dto;
    }

    @Override
    public void dealContrabandUploadData(JyExceptionContrabandDto dto)  {

        try{
            JyExceptionContrabandEnum.ContrabandTypeEnum enumResult = JyExceptionContrabandEnum.ContrabandTypeEnum.getEnumByCode(dto.getContrabandType());
            if(enumResult == null){
                return;
            }
            String waybillCode = WaybillUtil.getWaybillCode(dto.getBarCode());
            String cacheKey = String.format(CacheKeyConstants.CACHE_KEY_JY_CONTRABAND_BDTRANCE, waybillCode);
            //写运单维度的全程跟踪
            String cacheValue = redisClientOfJy.get(cacheKey);
            //2天时间内多次上报不同包裹，不重复写运单维度全程跟踪
            if(StringUtils.isBlank(cacheValue)){
                sendContrabandSecurityCheckWaybillBDTrance(dto);
            }
            //第一条全程跟踪和第二条全程跟踪间隔五秒
            boolean sendMQFlag = false;
            switch (enumResult){
               case DETAIN_PACKAGE://扣件
                   if(StringUtils.isBlank(cacheValue)){
                       sendContrabandDetainPackageWaybillBDTrance(dto);
                   }
                   break;
               case AIR_TO_LAND:  //航空转陆运
               case RETURN://退回
                   sendContrabandReturnPackageBDTrance(dto, enumResult);
                   sendMQFlag = true;
                   break;
                default:
                    logger.warn("未知的违禁品类型--");
                    return ;
           }
            int contrabandWaybillCacheTime = dmsConfigManager.getPropertyConfig().getContrabandWaybillCacheTime();
            logger.info("缓存时长-{}",contrabandWaybillCacheTime);
            //缓存当前单号
            Boolean setResult = redisClientOfJy.set(cacheKey, "1", contrabandWaybillCacheTime, TimeUnit.MINUTES, false);
            logger.info("单号 {}，放入缓存结果-{}",waybillCode,setResult);

           if(!sendMQFlag) {
               return;
           }

           Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
           if(waybill == null){
               logger.warn("获取运单信息失败！-{}",waybillCode);
               return;
           }

            // 是否为港澳单
            WaybillFlowTypeEnum waybillFlowType = getWaybillFlowType(waybill);
            JyExpContrabandNoticCustomerMQ mq = coverToDamageNoticCustomerMQ(dto, waybill, waybillFlowType);
            if (mq == null) {
                return ;
            }
            logger.info("发送违禁品运单-{}", waybillCode);
            dmsContrabandMainLandNoticeKFProducer.sendOnFailPersistent(dto.getBizId(), JsonHelper.toJson(mq));

        } catch (Exception e) {
            logger.error("违禁品上报发送客服异常！",e);
        }

    }

    @Override
    public int getDamageCountByTime(String startTime, String endTime) {
        Map<String,Object> map = new HashMap();
        map.put("startTime",DateHelper.parseDate(startTime,Constants.DATE_TIME_FORMAT));
        map.put("endTime",DateHelper.parseDate(endTime,Constants.DATE_TIME_FORMAT));
        return jyExceptionContrabandDao.getContrabandCountByTime(map);
    }

    /**
     * 发送客服违禁品数据组装
     * @return
     */
    private JyExpContrabandNoticCustomerMQ coverToDamageNoticCustomerMQ(JyExceptionContrabandDto dto, Waybill waybill, WaybillFlowTypeEnum waybillFlowType) {

        String waybillCode = WaybillUtil.getWaybillCode(dto.getBarCode());

        JyExpContrabandNoticCustomerMQ mq = new JyExpContrabandNoticCustomerMQ();
        mq.setDealType(ASCPContants.DEAL_TYPE);
        mq.setCodeInfo(waybillCode);
        mq.setCodeType(ASCPContants.CODE_TYPE);
        mq.setExptCreateTime(DateHelper.formatDateTime(new Date()));
        mq.setExptCreator(dto.getCreateErp());
        JyExceptionContrabandEnum.ContrabandTypeEnum contrabandType = JyExceptionContrabandEnum.ContrabandTypeEnum.getEnumByCode(dto.getContrabandType());
        switch (waybillFlowType) {
            case HK_OR_MO : {
                logger.info("港澳单---{}", waybillCode);
                mq.setBusinessId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_HK_HO.getCode());
                mq.setExptId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_HK_HO.getCode() + "_" + dto.getBizId());
                mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.HA_MO_DELIVERY_EXCEPTION_REPORT.getCode());
                mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.HA_MO_DELIVERY_EXCEPTION_REPORT.getName());
                switch (contrabandType) {
                    case RETURN:
                        mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_CONTRABAND_RETURN.getCode());
                        mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_CONTRABAND_RETURN.getName());
                        break;
                    case AIR_TO_LAND:
                        mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_CONTRABAND_CHANGE_LAND.getCode());
                        mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.HA_MO_CONTRABAND_CHANGE_LAND.getName());
                        break;
                    default:
                        logger.warn("此违禁品上报类型不做处理-{}", contrabandType);
                        return null;
                }
            }
            break;
            case INTERNATION: {
                mq.setBusinessId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_INTERNATION.getCode());
                mq.setExptId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_INTERNATION.getCode() + "_" + dto.getBizId());
                mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.INTERNATION_SORTING_CONTRABAND.getCode());
                mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.INTERNATION_SORTING_CONTRABAND.getName());
                switch (contrabandType) {
                    case RETURN:
                        mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.INTERNATION_SORTING_RETURN.getCode());
                        mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.INTERNATION_SORTING_RETURN.getName());
                        break;
                    case AIR_TO_LAND:
                        mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.INTERNATION_SORTING_AIR_CHANGE_LAND.getCode());
                        mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.INTERNATION_SORTING_AIR_CHANGE_LAND.getName());
                        break;
                    default:
                        logger.warn("此违禁品上报类型不做处理-{}", contrabandType);
                        return null;
                }
            }
            break;
            default:{
                logger.info("大陆单---{}", waybillCode);
                mq.setBusinessId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_MAIN_LAND.getCode());
                mq.setExptId(JyExpNoticCustomerExpReasonEnum.ExpBusinessIDEnum.BUSINESS_ID_MAIN_LAND.getCode() + "_" + dto.getBizId());
                mq.setExptOneLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_CONTRABAND.getCode());
                mq.setExptOneLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonOneLevelEnum.MAIN_LAND_CONTRABAND.getName());
                switch (contrabandType) {
                    case RETURN:
                        mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_RETURN.getCode());
                        mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_RETURN.getName());
                        break;
                    case AIR_TO_LAND:
                        mq.setExptTwoLevel(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_AIR_CHANGE_LAND.getCode());
                        mq.setExptTwoLevelName(JyExpNoticCustomerExpReasonEnum.ExpReasonTwoLevelEnum.MAIN_LAND_AIR_CHANGE_LAND.getName());
                        break;
                    default:
                        logger.warn("此违禁品上报类型不做处理-{}", contrabandType);
                        return null;
                }
            }
        }
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(dto.getSiteCode().intValue());
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
        mq.setWaybillCode(waybillCode);
        mq.setAttachmentAddr(assembleAttachmentAddr(dto));
        logger.info("组装违禁品发送客服MQ-{}",JSON.toJSONString(mq));
        return mq;
    }


    /**
     * 组装客服需要的图片附件数据
     * @param dto
     * @return
     */
    private String assembleAttachmentAddr(JyExceptionContrabandDto dto){
        String attachmentAddr = "";
        JyAttachmentDetailQuery queryBefore = new JyAttachmentDetailQuery();
        queryBefore.setBizId(dto.getBizId());
        queryBefore.setSiteCode(dto.getSiteCode());
        queryBefore.setBizType(JyAttachmentBizTypeEnum.CONTRABAND_UPLOAD_EXCEPTION.getCode());
        // 删除老数据
        List<String> imagsList = new ArrayList<>();
        List<JyAttachmentDetailEntity> entityList = jyAttachmentDetailService.queryDataListByCondition(queryBefore);
        if (!CollectionUtils.isEmpty(entityList)) {
            imagsList =  entityList.stream().map(JyAttachmentDetailEntity::getAttachmentUrl)
                    .collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(imagsList)){
            attachmentAddr = imagsList.stream().collect(Collectors.joining(","));
        }
        return  attachmentAddr;
    }


    /**
     * 违禁品运单维度安检全程跟踪
     * @param dto
     */
    private void sendContrabandSecurityCheckWaybillBDTrance(JyExceptionContrabandDto dto){

        if(StringUtils.isBlank(dto.getBarCode())){
            logger.warn("单号为空!");
            return ;
        }
        String waybillCode = WaybillUtil.getWaybillCode(dto.getBarCode());
        BdTraceDto traceDto = new BdTraceDto();
        traceDto.setPackageBarCode(waybillCode);
        traceDto.setWaybillCode(waybillCode);
        traceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK);
        traceDto.setOperatorDesp(WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK_DESC);
        traceDto.setOperatorSiteId(dto.getSiteCode());
        traceDto.setOperatorSiteName(dto.getSiteName());
        traceDto.setOperatorUserName(dto.getCreateStaffName());
        traceDto.setOperatorTime(new Date());
        traceDto.setWaybillTraceType(Constants.WAYBILL_TRACE_TYPE);
        Map<String, Object> extendParameter  = new HashMap<>();
        extendParameter.put("traceDisplay",Constants.WAYBILL_TRACE_DISPLAY);
        traceDto.setExtendParameter(extendParameter);
        logger.info("违禁品运单维度安检全程跟踪-{}",JSON.toJSONString(traceDto));
        waybillQueryManager.sendBdTrace(traceDto);
    }

    /**
     * 违禁品运单维度扣件全程跟踪
     * @param dto
     */
    private void sendContrabandDetainPackageWaybillBDTrance(JyExceptionContrabandDto dto) {

        if(StringUtils.isBlank(dto.getBarCode())){
            logger.warn("单号为空!");
            return ;
        }
        String waybillCode = WaybillUtil.getWaybillCode(dto.getBarCode());
        List<WaybillSyncParameter> parameterList = new ArrayList<>(1);
        WaybillSyncParameter parameter = new WaybillSyncParameter();
        parameter.setOperatorCode(waybillCode);
        parameter.setOperatorId(dto.getCreateUserId());
        parameter.setOperatorName(dto.getCreateStaffName());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,5);
        Date time = calendar.getTime();
        parameter.setOperateTime(time);
        parameter.setZdId(dto.getSiteCode());
        parameter.setZdType(Constants.DMS_SITE_TYPE);
        parameter.setZdName(dto.getSiteName());
        parameter.setWaybillTraceType(Constants.WAYBILL_TRACE_TYPE);
        Map<String,Object> extendSyncParam =new HashMap<>();
        extendSyncParam.put("operateDesc", WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK_DETAIN_PACKAGE_DESC);

        parameter.setExtendSyncParam(extendSyncParam);
        parameterList.add(parameter);
        logger.info("违禁品运单维度扣件全程跟踪-{}",JSON.toJSONString(parameterList));
        waybillStatusService.sendWaybillTrackByOperatorCode(parameterList, WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK_DETAIN_PACKAGE);
    }

    /**
     * 违禁品包裹维度退回全程跟踪
     *
     * @param dto
     * @param enumResult
     */
    private void sendContrabandReturnPackageBDTrance(JyExceptionContrabandDto dto, JyExceptionContrabandEnum.ContrabandTypeEnum enumResult){

        if(StringUtils.isBlank(dto.getBarCode())){
            logger.warn("单号为空!");
            return ;
        }
        if(!WaybillUtil.isPackageCode(dto.getBarCode())){
            logger.warn("barCode 不是包裹号!");
            return;
        }
        BdTraceDto traceDto = new BdTraceDto();
        traceDto.setPackageBarCode(dto.getBarCode());
        traceDto.setWaybillCode(WaybillUtil.getWaybillCode(dto.getBarCode()));
        traceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_RETURNED_PACKAGE);
        String operatorDesc = "";
        if (enumResult.equals(AIR_TO_LAND)) {
            operatorDesc = WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK_RETURNED_PACKAGE_DESC_2;
        }else {
            operatorDesc = WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK_RETURNED_PACKAGE_DESC_1;
        }
        traceDto.setOperatorDesp(operatorDesc);
        traceDto.setOperatorSiteId(dto.getSiteCode());
        traceDto.setOperatorSiteName(dto.getSiteName());
        traceDto.setOperatorUserName(dto.getCreateStaffName());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,5);
        Date time = calendar.getTime();
        traceDto.setOperatorTime(time);
        traceDto.setWaybillTraceType(Constants.WAYBILL_TRACE_TYPE_PACKAGE);
        logger.info("违禁品包裹维度退回全程跟踪-{}",JSON.toJSONString(traceDto));
        waybillQueryManager.sendBdTrace(traceDto);
    }




    private JyExceptionContrabandEntity buildEntity(ExpContrabandReq req) {
        JyExceptionContrabandEntity entity = new JyExceptionContrabandEntity();
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(req.getUserErp());
        if (baseStaff != null) {
            entity.setSiteCode(req.getSiteId());
            entity.setSiteName(req.getSiteName());
            entity.setCreateUserId(baseStaff.getStaffNo());
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
        List<JyAttachmentDetailEntity> annexList = Lists.newArrayList();
        // 老版本照片
        if (!CollectionUtils.isEmpty(req.getImageUrlList())) {
            for (String proveUrl : req.getImageUrlList()) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                convertAttachmentDetail(annexEntity, proveUrl, entity);
                annexEntity.setBizType(JyAttachmentBizTypeEnum.CONTRABAND_UPLOAD_EXCEPTION.getCode());
                annexList.add(annexEntity);
            }
        }
        // 包裹面单
        if (!CollectionUtils.isEmpty(req.getWaybillImageUrlList())) {
            for (String url : req.getWaybillImageUrlList()) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                convertAttachmentDetail(annexEntity, url, entity);
                annexEntity.setBizType(JyAttachmentBizTypeEnum.WAYBILL_CONTRABAND_UPLOAD_EXCEPTION.getCode());
                annexList.add(annexEntity);
            }
        }

        // 包裹全景
        if (!CollectionUtils.isEmpty(req.getPanoramaImageUrlList())) {
            for (String url : req.getPanoramaImageUrlList()) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                convertAttachmentDetail(annexEntity, url, entity);
                annexEntity.setBizType(JyAttachmentBizTypeEnum.PANORAMA_CONTRABAND_UPLOAD_EXCEPTION.getCode());
                annexList.add(annexEntity);
            }
        }

        // 违禁品照片
        if (!CollectionUtils.isEmpty(req.getContrabandImageUrlList())) {
            for (String url : req.getContrabandImageUrlList()) {
                JyAttachmentDetailEntity annexEntity = new JyAttachmentDetailEntity();
                convertAttachmentDetail(annexEntity, url, entity);
                annexEntity.setBizType(JyAttachmentBizTypeEnum.CONTRABAND_UPLOAD_EXCEPTION.getCode());
                annexList.add(annexEntity);
            }
        }
        jyAttachmentDetailService.batchInsert(annexList);
    }

    private void convertAttachmentDetail(JyAttachmentDetailEntity annexEntity, String proveUrl, JyExceptionContrabandEntity entity) {
        annexEntity.setBizId(entity.getBizId());
        annexEntity.setSiteCode(entity.getSiteCode());
        annexEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
        annexEntity.setCreateUserErp(entity.getUpdateErp());
        annexEntity.setUpdateUserErp(entity.getUpdateErp());
        annexEntity.setAttachmentUrl(proveUrl);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);
        annexEntity.setCreateTime(calendar.getTime());
        annexEntity.setUpdateTime(new Date());
    }

    /**
     * 校验参数
     *
     * @param req
     * @param waybill
     */
    private void validateReq(ExpContrabandReq req, BigWaybillDto bigWaybillDto) {
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
        if (bigWaybillDto == null || bigWaybillDto.getWaybill() == null) {
            throw new RuntimeException("获取运单信息失败!");
        }
        Waybill waybill = bigWaybillDto.getWaybill();
        if (BusinessHelper.isBwxWaybill(waybill.getWaybillSign())){
            throw new RuntimeException("该单为保温箱运单，请正常发货流转!");
        }
        WaybillFlowTypeEnum waybillFlowType = getWaybillFlowType(waybill);
        if (JyExceptionContrabandEnum.ContrabandTypeEnum.RETURN.getCode().equals(req.getContrabandType())) {
            if(!waybillFlowType.equals(HK_OR_MO) && !waybillFlowType.equals(WaybillFlowTypeEnum.INTERNATION)){
                throw new RuntimeException("仅港澳件和国际件出口支持违禁品退回!");
            }
        }
        if (WaybillUtil.isPackageCode(req.getBarCode())) {
            logger.info("validateReq pass ....");
            DeliveryPackageD packageD = waybillPackageManager.getPackageInfoByPackageCode(req.getBarCode());
            logger.info("validateReq packageD:{}", JSON.toJSONString(packageD));
            if (packageD == null) {
                throw new RuntimeException("包裹号错误");
            }
        } else {
            logger.info("validateReq not pass ....");
            throw new RuntimeException("包裹号错误");
        }
        if (StringUtils.isEmpty(req.getUserErp())) {
            throw new RuntimeException("用户ERP不能为空");
        }
        PositionDetailRecord positionDetail = jyExceptionService.getPosition(req.getPositionCode());
        if (positionDetail == null) {
            throw new RuntimeException("岗位码不能为空!");
        }
        req.setSiteId(positionDetail.getSiteCode());
        req.setSiteName(positionDetail.getSiteName());

        JyExceptionContrabandDto query = new JyExceptionContrabandDto();
        query.setSiteCode(req.getSiteId());
        query.setBarCode(req.getBarCode());
        query.setContrabandType(req.getContrabandType());
        JyExceptionContrabandEntity entity = jyExceptionContrabandDao.selectOneByParams(query);
        if (entity != null) {
            String msg = entity.getBarCode() + " 已经提报，请勿重复提交！";
            throw new RuntimeException(msg);
        }

        // 报废运单拦截
        if (isScrapWaybill(waybill.getWaybillSign())) {
            throw new RuntimeException(HintService.getHint(SCRAP_WAYBILL_INTERCEPT_HINT_CODE));
        }
    }

    private boolean isInternationWaybill(String waybillCode) {
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null) {
            throw new RuntimeException("获取运单信息失败!");
        }
        return isInternationWaybill(waybillCode, waybill);
    }


    /**
     * 港澳件出口判断：始发地区为大陆，目的地区为港澳
     * @param waybillCode
     */
    private boolean isHKorMOExitWaybill (String waybillCode){
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null) {
            throw new RuntimeException("获取运单信息失败!");
        }
        WaybillExt waybillExt = waybill.getWaybillExt();
        if (waybill.getWaybillExt() != null) {
            if ((StringUtils.isNotBlank(waybillExt.getStartFlowDirection()) && !(Objects.equals("HK", waybillExt.getStartFlowDirection()) || Objects.equals("MO", waybillExt.getStartFlowDirection())))
                    && (StringUtils.isNotBlank(waybillExt.getEndFlowDirection()) && (Objects.equals("HK", waybillExt.getEndFlowDirection()) || Objects.equals("MO", waybillExt.getEndFlowDirection())))) {
                logger.info("始发地区为大陆，目的地区为港澳");
                return true;
            }
        }
        return false;
    }

    /**
     * 港澳单校验逻辑
     * @param waybillCode
     * @param waybill
     * @return
     */
    public boolean isHKorMOWaybill(String waybillCode,Waybill waybill){
        if(waybill != null &&  waybill.getWaybillExt() != null){
            WaybillExt waybillExt = waybill.getWaybillExt();
            if((StringUtils.isNotBlank(waybillExt.getStartFlowDirection()) && (Objects.equals("HK",waybillExt.getStartFlowDirection()) || Objects.equals("MO",waybillExt.getStartFlowDirection())))
                    || (StringUtils.isNotBlank(waybillExt.getEndFlowDirection()) && (Objects.equals("HK",waybillExt.getEndFlowDirection()) || Objects.equals("MO",waybillExt.getEndFlowDirection())))){
                logger.info("港澳单-{}",waybillCode);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为国际运单
     *
     * @param waybillCode 运单编号
     * @param waybill 运单对象
     * @return 是否为国际运单：true-是，false-否
     */
    private boolean isInternationWaybill(String waybillCode, Waybill waybill) {
        if(waybill != null &&  waybill.getWaybillExt() != null){
            WaybillExt waybillExt = waybill.getWaybillExt();
            if(StringUtils.isNotBlank(waybillExt.getStartFlowDirection())
                    && (Objects.equals("CN",waybillExt.getStartFlowDirection()))
                    && StringUtils.isNotBlank(waybillExt.getEndFlowDirection())
                    && !Objects.equals("CN",waybillExt.getEndFlowDirection())
                    && !Objects.equals("MO",waybillExt.getEndFlowDirection())
                    && !Objects.equals("HK",waybillExt.getEndFlowDirection())){
                logger.info("国际单-{}",waybillCode);
                return true;
            }
        }
        return false;
    }

    /**
     * 构建取消集包DTO对象
     * @param dto 禁运异常DTO对象
     * @return 取消集包DTO对象
     */
    private CancelCollectPackageDto buildCancelCollectPackageDto(JyExceptionContrabandDto dto, SortingDto sortingDto){
        CancelCollectPackageDto cancelCollectPackageDto = new CancelCollectPackageDto();
        cancelCollectPackageDto.setBizId(dto.getBizId());
        //不能设置箱号，设置箱号，按照箱号取消集包
        cancelCollectPackageDto.setBoxCode(sortingDto.getBoxCode());
        cancelCollectPackageDto.setPackageCode(dto.getBarCode());
        cancelCollectPackageDto.setSiteCode(sortingDto.getCreateSiteCode());
        cancelCollectPackageDto.setSiteName(sortingDto.getCreateSiteName());
        cancelCollectPackageDto.setUpdateUserCode(dto.getCreateUserId());
        cancelCollectPackageDto.setUpdateUserErp(dto.getCreateErp());
        cancelCollectPackageDto.setUpdateUserName(dto.getCreateStaffName());
        cancelCollectPackageDto.setUpdateTime(new Date());
        cancelCollectPackageDto.setCurrentSiteCode(dto.getSiteCode());
        cancelCollectPackageDto.setSkipSendCheck(true);
        return cancelCollectPackageDto;
    }
}
