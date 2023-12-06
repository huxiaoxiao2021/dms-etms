package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.basic.ExcelUtils;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.jss.oss.OssUrlNetTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckScrollResult;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jd.wlai.center.service.outter.domian.AutoReportingPicDto;
import com.jd.wlai.center.service.outter.domian.AutoReportingRequest;
import com.jd.wlai.center.service.outter.domian.AutoReportingResponse;
import com.jdl.express.weight.report.api.CommonDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoDTO;
import com.jdl.express.weight.report.api.rule.queries.sorting.ReportInfoQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 抽检处理实现
 *
 * @author hujiping
 * @date 2021/8/18 9:44 上午
 */
@Service("spotCheckDealService")
public class SpotCheckDealServiceImpl implements SpotCheckDealService {

    private static final Logger logger = LoggerFactory.getLogger(SpotCheckDealServiceImpl.class);
    
    private static final int OUT_EXCESS_STATUS = 3; // 外部门定义的未超标值

    @Value("${jss.pda.image.bucket}")
    private String bucket;

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private AutoReportingManager autoReportingManager;

    @Autowired
    private WeightReportCommonRuleManager weightReportCommonRuleManager;

    @Autowired
    @Qualifier("spotCheckIssueProducer")
    private DefaultJMQProducer spotCheckIssueProducer;

    @Autowired
    @Qualifier("spotCheckIssueZDProducer")
    private DefaultJMQProducer spotCheckIssueZDProducer;

    @Autowired
    @Qualifier("dwsAIDistinguishSmallProducer")
    private DefaultJMQProducer dwsAIDistinguishSmallProducer;

    @Autowired
    @Qualifier("dwsIssueDealProducer")
    private DefaultJMQProducer dwsIssueDealProducer;

    @Autowired
    private JssService jssService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;
    
    @Autowired
    @Qualifier("dwsCalibrateDealSpotCheckProducer")
    private DefaultJMQProducer dwsCalibrateDealSpotCheckProducer;

    @Override
    public DmsBaseDict getProductType(String waybillSign) {
        List<DmsBaseDict> list = dmsBaseDictService.queryListByParentId(Constants.PRODUCT_PARENT_ID);
        HashMap<String, DmsBaseDict> map = new HashMap<String, DmsBaseDict>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getTypeName(), list.get(i));
        }
        DmsBaseDict dmsBaseDict = null;
        //当waybillSign的40位为0时，根据waybillSign的31位值判断产品类型
        if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_40, '0')) {
            dmsBaseDict = map.get("31" + "-" + waybillSign.charAt(30));
        } else if (BusinessUtil.isFastTrans(waybillSign)) {
            //当waybillSign的40位为1，2，3，4，5时，根据waybillSign的80位值判断产品类型
            dmsBaseDict = map.get("80" + "-" + waybillSign.charAt(79));
        }
        return dmsBaseDict;
    }

    @Override
    public boolean gatherTogether(SpotCheckContext spotCheckContext) {
        String spotCheckPackCache = spotCheckPackSetStr(spotCheckContext.getWaybillCode(), spotCheckContext.getReviewSiteCode());
        if(StringUtils.isEmpty(spotCheckPackCache)){
            return false;
        }
        Set spotCheckPackSet = JsonHelper.fromJson(spotCheckPackCache, Set.class);
        if(CollectionUtils.isEmpty(spotCheckPackSet) || spotCheckPackSet.contains(spotCheckContext.getPackageCode())){
            return false;
        }
        return spotCheckContext.getPackNum() - spotCheckPackSet.size() == Constants.CONSTANT_NUMBER_ONE;
    }

    @Override
    public void sendWaybillTrace(SpotCheckContext spotCheckContext) {
        Task tTask = new Task();
        tTask.setKeyword1(spotCheckContext.getWaybillCode());
        tTask.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK));
        tTask.setCreateSiteCode(spotCheckContext.getReviewSiteCode());
        tTask.setCreateTime(spotCheckContext.getOperateTime());
        tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);

        WaybillStatus status=new WaybillStatus();
        status.setOperateType(WaybillStatus.WAYBILL_STATUS_WEIGHT_VOLUME_SPOT_CHECK);
        status.setWaybillCode(spotCheckContext.getWaybillCode());
        status.setPackageCode(spotCheckContext.getPackageCode());
        status.setOperateTime(spotCheckContext.getOperateTime());
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        status.setOperator(spotCheckReviewDetail.getReviewUserErp());
        status.setRemark(String.format(SpotCheckConstants.SPOT_CHECK_TRACE_REMARK, spotCheckReviewDetail.getReviewWeight(), spotCheckReviewDetail.getReviewVolume()));
        status.setCreateSiteCode(spotCheckContext.getReviewSiteCode());
        tTask.setBody(JsonHelper.toJson(status));
        taskService.add(tTask);
    }

    @Override
    public boolean checkIsHasSpotCheck(SpotCheckContext spotCheckContext) {
        String waybillCode = spotCheckContext.getWaybillCode();
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, waybillCode);
            if(StringUtils.isNotEmpty(jimdbCacheService.get(key))){
                return true;
            }else {
                Optional<WeightVolumeSpotCheckDto> existOptional = spotCheckContext.getSpotCheckRecords().stream()
                        .filter(item -> (Objects.equals(item.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_NO.getCode()) 
                                || Objects.equals(item.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())) 
                                && Objects.equals(item.getRecordType(), SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode()))
                        .findAny();
                return existOptional.isPresent();
            }
        }catch (Exception e){
            logger.error("根据运单号{}查询是否操作过抽检异常!", waybillCode, e);
        }
        return false;
    }

    @Override
    public boolean checkIsExcessFromRedis(String waybillCode) {
        boolean isExcess = false;
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, waybillCode);
            String executeStatus = jimdbCacheService.get(key);
            if(StringUtils.isNotEmpty(executeStatus)){
                return Objects.equals(executeStatus, String.valueOf(ExcessStatusEnum.EXCESS_ENUM_YES.getCode()));
            }
        }catch (Exception e){
            logger.error("根据运单号{}查询是否超标异常!", waybillCode, e);
        }
        return isExcess;
    }

    @Override
    public String spotCheckPackSetStr(String waybillCode, Integer siteCode) {
        String packListKey = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PACK_LIST, siteCode, waybillCode);
        String packSetStr;
        try {
            packSetStr = jimdbCacheService.get(packListKey);
            if(StringUtils.isNotEmpty(packSetStr)){
                return packSetStr;
            }else {
                SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
                condition.setWaybillCode(waybillCode);
                condition.setReviewSiteCode(siteCode);
                condition.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
                List<String> spotCheckedPackList = spotCheckQueryManager.getSpotCheckPackByCondition(condition);
                if(CollectionUtils.isEmpty(spotCheckedPackList)){
                    return null;
                }
                packSetStr = jimdbCacheService.get(packListKey);
                if(StringUtils.isNotEmpty(packSetStr)){
                    spotCheckedPackList.addAll(Objects.requireNonNull(JsonHelper.jsonToList(packSetStr, String.class)));
                }
                // 已抽检包裹集合
                String spotCheckPackSetStr = JsonHelper.toJson(new HashSet<>(spotCheckedPackList));
                jimdbCacheService.setEx(packListKey, spotCheckPackSetStr, 30, TimeUnit.MINUTES);
                return spotCheckPackSetStr;
            }
        }catch (Exception e){
            logger.error("获取场地:{}运单号:{}下的包裹抽检缓存异常!", siteCode, waybillCode);
        }
        return null;
    }

    private void setIssueWaybillCache(String waybillCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_FXM_SEND_WAYBILL, waybillCode);
            jimdbCacheService.setEx(key, Constants.CONSTANT_NUMBER_ONE, 15, TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("设置运单号:{}下发fxm的缓存异常!", waybillCode, e);
        }
    }

    @Override
    public void dealPictureUrl(String packageCode, Integer siteCode, String pictureUrl) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.spotCheck.SpotCheckDealService.dealPictureUrl",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            boolean isMultiPack = isMultiPack(waybill, packageCode);
            // 图片缓存处理
            if(!spotCheckPicUrlCacheDealIsSuc(isMultiPack, packageCode, siteCode, pictureUrl)){
                logger.warn("站点：{}包裹号：{}的图片已存在!", siteCode, packageCode);
                return;
            }
            // multi pack deal
            if(isMultiPack){
                multiPackDeal(packageCode, siteCode, pictureUrl);
                return;
            }
            // single pack deal
            singlePackDeal(packageCode, siteCode, pictureUrl);
        }catch (Exception e){
            logger.error("包裹:{}的图片处理异常!", packageCode, e);
            Profiler.functionError(callerInfo);
            throw e;
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    private boolean spotCheckPicUrlCacheDealIsSuc(boolean isMultiPack, String packageCode, Integer siteCode, String pictureUrl) {
        String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
        if(jimdbCacheService.exists(key)){
            return false;
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setPackageCode(isMultiPack ? packageCode : WaybillUtil.getWaybillCode(packageCode)); // 一单一件：包裹号字段存的是运单号
        condition.setReviewSiteCode(siteCode);
        condition.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
        List<WeightVolumeSpotCheckDto> spotCheckDtos = spotCheckQueryManager.queryAllSpotCheckByCondition(condition);
        if(CollectionUtils.isNotEmpty(spotCheckDtos)
                && spotCheckDtos.get(0) != null
                && StringUtils.isNotEmpty(spotCheckDtos.get(0).getPictureAddress())){
            return false;
        }
        jimdbCacheService.setEx(key, pictureUrl, 30, TimeUnit.MINUTES);
        return true;
    }
    
    private void singlePackDeal(String packageCode, Integer siteCode, String pictureUrl) {
        // 一单一件
        // 1、更新总记录维度数据
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        WeightVolumeSpotCheckDto summaryRecord = new WeightVolumeSpotCheckDto();
        summaryRecord.setPackageCode(waybillCode);
        summaryRecord.setWaybillCode(waybillCode);
        summaryRecord.setReviewSiteCode(siteCode);
        summaryRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
        summaryRecord.setPictureAddress(pictureUrl);
        summaryRecord.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        spotCheckServiceProxy.insertOrUpdateProxyReform(summaryRecord);
        // 未超标则直接返回
        if(!checkIsExcessFromRedis(waybillCode)){
            return;
        }
        // 2、下发AI识别
        pushAIDistinguish(packageCode, siteCode, pictureUrl);
    }

    private void multiPackDeal(String packageCode, Integer siteCode, String pictureUrl) {
        // 一单多件
        // 1、更新包裹明细维度数据
        WeightVolumeSpotCheckDto detailRecord = new WeightVolumeSpotCheckDto();
        detailRecord.setPackageCode(packageCode);
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        detailRecord.setWaybillCode(waybillCode);
        detailRecord.setReviewSiteCode(siteCode);
        detailRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
        detailRecord.setPictureAddress(pictureUrl);
        detailRecord.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
        spotCheckServiceProxy.insertOrUpdateProxyReform(detailRecord);
        // 2、更新总记录维度数据
        WeightVolumeSpotCheckDto totalRecord = new WeightVolumeSpotCheckDto();
        totalRecord.setPackageCode(waybillCode);
        totalRecord.setWaybillCode(waybillCode);
        totalRecord.setReviewSiteCode(siteCode);
        totalRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
        totalRecord.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        spotCheckServiceProxy.insertOrUpdateProxyReform(totalRecord);
        // 3.下发AI识别
        if(checkIsExcessFromRedis(waybillCode)){
            try {
                // here sleep for consumer can query es data
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("阻塞1s异常!", e);
            }
            totalRecord.setWaybillCode(waybillCode);
            totalRecord.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
            totalRecord.setIsGatherTogether(Constants.CONSTANT_NUMBER_ONE);
            // 异步处理dws一单多件图片下发AI逻辑
            dwsIssueDealProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(totalRecord));
        }
    }

    @Override
    public boolean isExecuteSpotCheckReform(Integer siteCode) {
        try {
            String newSpotCheckSiteCodes = dmsConfigManager.getPropertyConfig().getSpotCheckReformSiteCodes();
            // 全部关闭
            if(StringUtils.isEmpty(newSpotCheckSiteCodes)){
                return false;
            }
            // 全部开启
            if(Objects.equals(newSpotCheckSiteCodes, String.valueOf(true))){
                return true;
            }
            return Arrays.asList(newSpotCheckSiteCodes.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
        }catch (Exception e){
            logger.error("获取抽检改造站点ID异常!", e);
        }
        return false;
    }

    @Override
    public boolean isExecuteDwsAIDistinguish(Integer siteCode) {
        try {
            String dwsAIDistinguishSiteCodes = dmsConfigManager.getPropertyConfig().getDeviceAIDistinguishSwitch();
            // 全部关闭
            if(StringUtils.isEmpty(dwsAIDistinguishSiteCodes)){
                return false;
            }
            // 全部开启
            if(Objects.equals(dwsAIDistinguishSiteCodes, String.valueOf(true))){
                return true;
            }
            return Arrays.asList(dwsAIDistinguishSiteCodes.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
        }catch (Exception e){
            logger.error("获取开通AI图片识别场地ID异常!", e);
        }
        return false;
    }

    @Override
    public ImmutablePair<Integer, String> singlePicAutoDistinguish(String waybillCode, Double weight, String picUrl, Integer uploadPicType, Integer excessType) {
        if(!dmsConfigManager.getPropertyConfig().getAiDistinguishSwitch()){
            return ImmutablePair.of(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        }
        boolean isDistinguish = false;
        // AI图片识别前提：1&&2
        // 1、重量超标：1 （只识别图片类型是：重量和面单）
        // 2、体积超标：2 （只识别图片类型是：面单）
        if(Objects.equals(excessType, SpotCheckConstants.EXCESS_TYPE_WEIGHT)
                && (Objects.equals(SpotCheckPicTypeEnum.PIC_WEIGHT.getCode(), uploadPicType) || Objects.equals(SpotCheckPicTypeEnum.PIC_FACE.getCode(), uploadPicType))){
            isDistinguish = true;
        }
        if(Objects.equals(excessType, SpotCheckConstants.EXCESS_TYPE_VOLUME)
                && Objects.equals(SpotCheckPicTypeEnum.PIC_FACE.getCode(), uploadPicType)){
            isDistinguish = true;
        }
        // 是否执行AI识别
        if(!isDistinguish){
            return ImmutablePair.of(InvokeResult.RESULT_SUCCESS_CODE, InvokeResult.RESULT_SUCCESS_MESSAGE);
        }
        AutoReportingRequest autoReportingRequest = new AutoReportingRequest();
        String currentTimeStamp = String.valueOf(System.currentTimeMillis());
        autoReportingRequest.setUuid(currentTimeStamp);
        autoReportingRequest.setExpressBillNo(waybillCode);
        autoReportingRequest.setWeight(weight == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(weight));
        List<AutoReportingPicDto> singleList = new ArrayList<>();
        AutoReportingPicDto autoReportingPicDto = new AutoReportingPicDto();
        autoReportingPicDto.setUuid(currentTimeStamp);
        autoReportingPicDto.setPicUrl(picUrl);
        int picType = Objects.equals(uploadPicType, SpotCheckPicTypeEnum.PIC_WEIGHT.getCode())
                ? SpotCheckConstants.SPOT_CHECK_AI_TYPE_WEIGHT : SpotCheckConstants.SPOT_CHECK_AI_TYPE_FACE;
        autoReportingPicDto.setPicType(String.valueOf(picType));
        singleList.add(autoReportingPicDto);
        autoReportingRequest.setPicList(singleList);

        int code = InvokeResult.RESULT_SUCCESS_CODE;
        String message = InvokeResult.RESULT_SUCCESS_MESSAGE;
        AutoReportingResponse autoReportingResponse = autoReportingManager.reportingCheck(autoReportingRequest);
        if(autoReportingResponse == null || !Objects.equals(autoReportingResponse.getStatus(), String.valueOf(InvokeResult.RESULT_SUCCESS_CODE))
                || CollectionUtils.isEmpty(autoReportingResponse.getPicList())){
            code = SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE;
            message = "AI图片识别失败，请联系分拣小秘!";
            return ImmutablePair.of(code, message);
        }
        AutoReportingPicDto reportResult = autoReportingResponse.getPicList().get(0);
        if(reportResult == null || !Objects.equals(reportResult.getStatus(), String.valueOf(InvokeResult.RESULT_SUCCESS_CODE))){
            code = SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE;
            message = reportResult == null ? "AI图片识别失败，请联系分拣小秘！" : reportResult.getMessage();
            return ImmutablePair.of(code, message);
        }
        if(logger.isInfoEnabled()){
            logger.info("运单号:{}重量:{}图片类型:{}超标类型:{}的AI图片识别结果:{}",
                    waybillCode, weight, uploadPicType, excessType, JsonHelper.toJson(autoReportingResponse));
        }
        // 识别成功后，继续比对
        // 1、重量图片：比对录入重量和图片重量是否一致
        // 2、面单图片：比对录入单号和图片单号是否一致
        AutoReportingPicDto autoReportResult = autoReportingResponse.getPicList().get(0);
        String recognizedInfo = autoReportResult.getRecognizedInfo();
        if(Objects.equals(picType, SpotCheckConstants.SPOT_CHECK_AI_TYPE_WEIGHT)
                && (StringUtils.isEmpty(recognizedInfo) || !Objects.equals(weight, Double.parseDouble(recognizedInfo)))){
            return ImmutablePair.of(SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE,
                    String.format(SpotCheckConstants.SPOT_CHECK_AI_WEIGHT_HIT, weight, recognizedInfo == null ? Constants.EMPTY_FILL : recognizedInfo));
        }
        if(Objects.equals(picType, SpotCheckConstants.SPOT_CHECK_AI_TYPE_FACE)
                && (StringUtils.isEmpty(recognizedInfo) || !Objects.equals(waybillCode, recognizedInfo))){
            return ImmutablePair.of(SpotCheckConstants.SPOT_CHECK_AI_EXC_CODE,
                    String.format(SpotCheckConstants.SPOT_CHECK_AI_FACE_HIT, waybillCode, recognizedInfo == null ? Constants.EMPTY_FILL : recognizedInfo));
        }
        return ImmutablePair.of(code, message);
    }

    @Override
    public InvokeResult<SpotCheckResult> checkIsExcessReform(SpotCheckContext spotCheckContext) {
        InvokeResult<SpotCheckResult> result = new InvokeResult<SpotCheckResult>();
        SpotCheckResult spotCheckResult = new SpotCheckResult();
        result.success();
        result.setData(spotCheckResult);

        ReportInfoQuery reportInfoQuery = new ReportInfoQuery();
        reportInfoQuery.setWaybillCode(spotCheckContext.getWaybillCode());
        reportInfoQuery.setChannel(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        reportInfoQuery.setMeasureWeight(NumberHelper.formatMoney(spotCheckReviewDetail.getReviewTotalWeight()));
        reportInfoQuery.setMeasureVolume(NumberHelper.formatMoney(spotCheckReviewDetail.getReviewTotalVolume()));
        CommonDTO<ReportInfoDTO> commonDTO = weightReportCommonRuleManager.getReportInfo(reportInfoQuery);
        if(logger.isInfoEnabled()){
            logger.info("运单号:{}的核对数据:{}", reportInfoQuery.getWaybillCode(), JsonHelper.toJson(commonDTO.getData()));
        }
        if(commonDTO == null || !Objects.equals(commonDTO.getCode(), CommonDTO.CODE_SUCCESS) || commonDTO.getData() == null){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,
                    commonDTO == null ? InvokeResult.SERVER_ERROR_MESSAGE : commonDTO.getMessage());
            return result;
        }
        ReportInfoDTO reportInfo = commonDTO.getData();
        spotCheckResult.setExcessStatus(Objects.equals(reportInfo.getExceedType(), OUT_EXCESS_STATUS)
                ? ExcessStatusEnum.EXCESS_ENUM_NO.getCode() : ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        spotCheckResult.setExcessType(reportInfo.getExceedType());
        spotCheckResult.setIsMultiPack(spotCheckContext.getIsMultiPack());
        return result;
    }

    @Override
    public void assembleContrastData(SpotCheckContext spotCheckContext) {
        String waybillCode = spotCheckContext.getWaybillCode();
        SpotCheckReviewDetail spotCheckReviewDetail = spotCheckContext.getSpotCheckReviewDetail();
        Double reviewWeight = spotCheckReviewDetail.getReviewTotalWeight();
        Double reviewVolume = spotCheckReviewDetail.getReviewTotalVolume();

        ReportInfoQuery reportInfoQuery = new ReportInfoQuery();
        reportInfoQuery.setWaybillCode(waybillCode);
        reportInfoQuery.setChannel(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE.contains(spotCheckContext.getSpotCheckSourceFrom())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        reportInfoQuery.setMeasureWeight(NumberHelper.formatMoney(reviewWeight));
        reportInfoQuery.setMeasureVolume(NumberHelper.formatMoney(reviewVolume));
        CommonDTO<ReportInfoDTO> commonDTO = weightReportCommonRuleManager.getReportInfo(reportInfoQuery);
        if(logger.isInfoEnabled()){
            logger.info("运单号:{}的核对数据:{}", reportInfoQuery.getWaybillCode(), JsonHelper.toJson(commonDTO.getData()));
        }
        ReportInfoDTO reportInfo = (commonDTO == null || !Objects.equals(commonDTO.getCode(), CommonDTO.CODE_SUCCESS)) ? null : commonDTO.getData();
        // 组装核对数据
        SpotCheckContrastDetail spotCheckContrastDetail = spotCheckContext.getSpotCheckContrastDetail();
        spotCheckContrastDetail.setContrastSourceFrom(reportInfo == null ? null : reportInfo.getRecheckSource());
        spotCheckContrastDetail.setContrastWeight((reportInfo == null || reportInfo.getRecheckWeight() == null)
                ? null : MathUtils.keepScale(Double.parseDouble(reportInfo.getRecheckWeight()), 2));
        spotCheckContrastDetail.setContrastVolume((reportInfo == null || reportInfo.getRecheckVolume() == null)
                ? null : MathUtils.keepScale(Double.parseDouble(reportInfo.getRecheckVolume()), 2));
        spotCheckContrastDetail.setContrastOrgId(reportInfo == null ? null : reportInfo.getDutyOrgId());
        spotCheckContrastDetail.setContrastOrgName(reportInfo == null ? null : reportInfo.getDutyOrgName());
        spotCheckContrastDetail.setContrastProvinceAgencyCode(reportInfo == null ? null : reportInfo.getDutyProvinceAgencyCode());
        spotCheckContrastDetail.setContrastProvinceAgencyName(reportInfo == null ? null : reportInfo.getDutyProvinceAgencyName());
        spotCheckContrastDetail.setContrastWarZoneCode(reportInfo == null ? null : reportInfo.getDutyProvinceCompanyCode());
        spotCheckContrastDetail.setContrastWarZoneName(reportInfo == null ? null : reportInfo.getDutyProvinceCompanyName());
        spotCheckContrastDetail.setContrastAreaCode(reportInfo == null ? null : reportInfo.getDutyAreaCode());
        spotCheckContrastDetail.setContrastAreaName(reportInfo == null ? null : reportInfo.getDutyAreaName());
        spotCheckContrastDetail.setContrastSiteCode(reportInfo == null ? null : reportInfo.getDutySiteId());
        spotCheckContrastDetail.setContrastSiteName(reportInfo == null ? null : reportInfo.getDutySiteName());
        spotCheckContrastDetail.setContrastOperateUserErp(reportInfo == null ? null : reportInfo.getDutyStaffAccount());
        spotCheckContrastDetail.setContrastOperateUserName(reportInfo == null ? null : reportInfo.getDutyStaffName());
        spotCheckContrastDetail.setContrastOperateUserAccountType(reportInfo == null ? null : reportInfo.getDutyStaffType());
        spotCheckContrastDetail.setDutyType(reportInfo == null ? null : reportInfo.getDutyType());
        // 超标数据
        int excessStatus = ExcessStatusEnum.EXCESS_ENUM_NO.getCode();
        if(reportInfo != null && reportInfo.getExceedType() != null){
            excessStatus = Objects.equals(reportInfo.getExceedType(), OUT_EXCESS_STATUS)
                    ? ExcessStatusEnum.EXCESS_ENUM_NO.getCode() : ExcessStatusEnum.EXCESS_ENUM_YES.getCode();
        }
        spotCheckContext.setExcessStatus(excessStatus);
        spotCheckContext.setExcessType(reportInfo == null ? null : reportInfo.getExceedType());
        spotCheckContext.setDiffWeight((reportInfo == null || reportInfo.getDiffWeight() == null) ? null : Double.parseDouble(reportInfo.getDiffWeight()));
        spotCheckContext.setDiffStandard(reportInfo == null ? null : reportInfo.getDiffStandard());
        spotCheckContext.setVolumeRate((reportInfo == null || reportInfo.getConvertCoefficient() == null)
                ? null : reportInfo.getConvertCoefficient());
    }

    @Override
    public void spotCheckIssue(WeightVolumeSpotCheckDto spotCheckDto) {
        // 下发前置条件：超标&&集齐
        if(!Objects.equals(spotCheckDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            return;
        }
        if(!Objects.equals(spotCheckDto.getIsGatherTogether(), Constants.CONSTANT_NUMBER_ONE)){
            return;
        }
        // 设备抽检需校验图片是否合规
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){
            if(logger.isInfoEnabled()){
                logger.info("设备抽检图片AI识别下发运单:{}站点:{}的数据!", spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode());
            }
            String picUrl = StringUtils.isEmpty(spotCheckDto.getPictureAddress())
                    ? getSpotCheckPackUrlFromCache(spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode()) : spotCheckDto.getPictureAddress();
            if(StringUtils.isEmpty(picUrl)){
                return;
            }
            pushAIDistinguish(spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode(), picUrl);
            return;
        }
        // 执行下发
        executeIssue(spotCheckDto);
    }

    private void pushAIDistinguish(String packageCode, Integer siteCode, String picUrl) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        List<DwsAIDistinguishMQ.Package> list = new ArrayList<>();
        DwsAIDistinguishMQ.Package packageUrl = new DwsAIDistinguishMQ.Package();
        packageUrl.setPackageCode(packageCode);
        packageUrl.setPicUrl(BusinessHelper.switchOssUrlByType(picUrl, OssUrlNetTypeEnum.IN.getType()));
        list.add(packageUrl);
        DwsAIDistinguishMQ dwsAIDistinguishMQ = new DwsAIDistinguishMQ();
        dwsAIDistinguishMQ.setUuid(waybillCode.concat(Constants.SEPARATOR_HYPHEN).concat(String.valueOf(System.currentTimeMillis())));
        dwsAIDistinguishMQ.setWaybillCode(waybillCode);
        dwsAIDistinguishMQ.setSiteCode(siteCode);
        dwsAIDistinguishMQ.setPackages(list);
        dwsAIDistinguishSmallProducer.sendOnFailPersistent(dwsAIDistinguishMQ.getWaybillCode(), JsonHelper.toJson(dwsAIDistinguishMQ));
    }

    /**
     * 执行下发逻辑
     *  scene：
     *      1、人工抽检调用
     *      2、设备抽检调用
     *          1）、AI图片合格后调用
     *          2）、设备状态合格后调用
     * @param spotCheckDto
     */
    @Override
    public void executeIssue(WeightVolumeSpotCheckDto spotCheckDto) {
        if(!Objects.equals(spotCheckDto.getIsExcess(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode())){
            // 抽检不超标不下发
            logger.warn("单号:{}的抽检数据不执行下发!", spotCheckDto.getPackageCode());
            return;
        }
        // 校验运单是否已下发
        if(checkWaybillHasIssued(spotCheckDto.getWaybillCode())){
            logger.info("spotCheckWaybill has issued will not send {}", spotCheckDto.getWaybillCode());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("称重流程再造：下发运单号:{}站点:{}的超标数据!", spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode());
        }
        // 体积超标下发终端条件：体积超标&&设备抽检
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                && Objects.equals(spotCheckDto.getExcessType(), SpotCheckConstants.EXCESS_TYPE_VOLUME)){
            SpotCheckIssueZDMQ spotCheckIssueZDMQ = new SpotCheckIssueZDMQ();
            spotCheckIssueZDMQ.setWaybillCode(spotCheckDto.getWaybillCode());
            spotCheckIssueZDMQ.setSiteCode(spotCheckDto.getReviewSiteCode());
            spotCheckIssueZDMQ.setOperateErp(spotCheckDto.getReviewUserErp());
            spotCheckIssueZDMQ.setOperateUserName(spotCheckDto.getReviewUserName());
            spotCheckIssueZDMQ.setReviewWeight(spotCheckDto.getReviewWeight());
            spotCheckIssueZDMQ.setReviewVolume(spotCheckDto.getReviewVolume());
            spotCheckIssueZDMQ.setReviewTime(spotCheckDto.getReviewDate());
            if(logger.isInfoEnabled()){
                logger.info("体积超标,下发运单号:{}站点:{}的超标数据{}", spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode(), JsonHelper.toJson(spotCheckIssueZDMQ));
            }
            spotCheckIssueZDProducer.sendOnFailPersistent(spotCheckIssueZDMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueZDMQ));
        }
        // 下发超标数据至称重再造系统条件
        // 1、图片
        // 1.1、人工抽检：图片肯定有（都是按运单维度操作，肯定有5张图片）
        // 1.2、设备抽检：一单一件下发一张则必须要有图片，一单多件不下发图片则不需要图片限制条件
        if(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){ // 人工抽检
            if(!Objects.equals(spotCheckDto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE) && !Objects.equals(spotCheckDto.getIsHasVideo(), Constants.CONSTANT_NUMBER_ONE)){
                // 人工抽检无图片并且无视频不下发，视频和图片有其一即可下发
                return;
            }
        }else if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){ // 设备抽检
            if(!Objects.equals(spotCheckDto.getMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())){
                // 设备抽检设备不合格不下发
                logger.warn("单号:{}设备编码:{}的抽检数据不执行下发!", spotCheckDto.getPackageCode(), spotCheckDto.getMachineCode());
                return;
            }
            // 图片AI识别判断是否下发
            if(!checkIsDownByPicAI(spotCheckDto)){
                logger.warn("设备抽检的单号:{}的图片不合格,不执行下发!", spotCheckDto.getWaybillCode());
                return;
            }
            if(!Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE)
                    && !Objects.equals(spotCheckDto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE)){
                // 设备1单1件抽检无图片不下发
                return;
            }
        }
        // 设置已下发缓存
        setIssueWaybillCache(spotCheckDto.getWaybillCode());
        // 构建超标消息并下发
        buildAndIssue(spotCheckDto);
    }

    private boolean checkIsDownByPicAI(WeightVolumeSpotCheckDto spotCheckDto) {
        if(Objects.equals(spotCheckDto.getPicIsQualify(), Constants.CONSTANT_NUMBER_ONE)){
            return true;
        }
        // 目前图片不合格的场景：只有图片类型是软包&&超标类型是重量超标会下发，其他场景都不下发
        return StringUtils.isNotEmpty(spotCheckDto.getPictureAIDistinguishReason())
                && spotCheckDto.getPictureAIDistinguishReason().contains(SpotCheckConstants.PIC_AI_REASON_RB)
                && Objects.equals(spotCheckDto.getExcessType(), SpotCheckConstants.EXCESS_TYPE_WEIGHT);
    }


    /**
     * 构建并下发数据
     *
     * @param spotCheckDto
     */
    private void buildAndIssue(WeightVolumeSpotCheckDto spotCheckDto) {
        SpotCheckIssueMQ spotCheckIssueMQ = new SpotCheckIssueMQ();
        spotCheckIssueMQ.setFlowSystem(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        spotCheckIssueMQ.setInitiationLink(String.valueOf(SpotCheckConstants.DMS_SPOT_CHECK_ISSUE));
        spotCheckIssueMQ.setSysSource(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                ? String.valueOf(2) : String.valueOf(1)); // 1：设备抽检 2：人工抽检
        spotCheckIssueMQ.setFlowId(spotCheckIssueMQ.getFlowSystem() + Constants.UNDERLINE_FILL + spotCheckDto.getWaybillCode()
                + Constants.UNDERLINE_FILL + spotCheckDto.getReviewSiteCode());
        spotCheckIssueMQ.setOperateType(Constants.CONSTANT_NUMBER_ONE);
        spotCheckIssueMQ.setWaybillCode(spotCheckDto.getWaybillCode());
        spotCheckIssueMQ.setDutyType(spotCheckDto.getContrastDutyType());
        spotCheckIssueMQ.setDutyRegionCode(String.valueOf(spotCheckDto.getContrastOrgCode()));
        spotCheckIssueMQ.setDutyRegion(spotCheckDto.getContrastOrgName());
        spotCheckIssueMQ.setDutyProvinceCompanyCode(spotCheckDto.getContrastWarZoneCode());
        spotCheckIssueMQ.setDutyProvinceCompanyName(spotCheckDto.getContrastWarZoneName());
        spotCheckIssueMQ.setDutyAreaCode(spotCheckDto.getContrastAreaCode());
        spotCheckIssueMQ.setDutyAreaName(spotCheckDto.getContrastAreaName());
        spotCheckIssueMQ.setDutyOrgCode(String.valueOf(spotCheckDto.getContrastSiteCode()));
        spotCheckIssueMQ.setDutyOrgName(spotCheckDto.getContrastSiteName());
        spotCheckIssueMQ.setDutyStaffAccount(spotCheckDto.getContrastStaffAccount());
        spotCheckIssueMQ.setDutyStaffName(spotCheckDto.getContrastStaffName());
        spotCheckIssueMQ.setDutyStaffType(spotCheckDto.getContrastStaffType());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        spotCheckIssueMQ.setConfirmWeight(spotCheckDto.getContrastWeight() == null ? null : decimalFormat.format(spotCheckDto.getContrastWeight()));
        spotCheckIssueMQ.setConfirmVolume(spotCheckDto.getContrastVolume() == null ? null : decimalFormat.format(spotCheckDto.getContrastVolume()));
        spotCheckIssueMQ.setStartStaffAccount(spotCheckDto.getReviewUserErp());
        spotCheckIssueMQ.setStartStaffName(spotCheckDto.getReviewUserName());
        spotCheckIssueMQ.setStartStaffType(Constants.CONSTANT_NUMBER_ONE);
        spotCheckIssueMQ.setStartRegionCode(String.valueOf(spotCheckDto.getReviewOrgCode()));
        spotCheckIssueMQ.setStartRegion(spotCheckDto.getReviewOrgName());
        spotCheckIssueMQ.setOrgCode(String.valueOf(spotCheckDto.getReviewSiteCode()));
        spotCheckIssueMQ.setOrgName(spotCheckDto.getReviewSiteName());
        spotCheckIssueMQ.setReConfirmLong(spotCheckDto.getReviewLength() == null ? null : String.valueOf(spotCheckDto.getReviewLength()));
        spotCheckIssueMQ.setReConfirmWidth(spotCheckDto.getReviewWidth() == null ? null : String.valueOf(spotCheckDto.getReviewWidth()));
        spotCheckIssueMQ.setReConfirmHigh(spotCheckDto.getReviewHeight() == null ? null : String.valueOf(spotCheckDto.getReviewHeight()));
        spotCheckIssueMQ.setReConfirmWeight(spotCheckDto.getReviewWeight() == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(spotCheckDto.getReviewWeight()));
        spotCheckIssueMQ.setReConfirmVolume(spotCheckDto.getReviewVolume() == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(spotCheckDto.getReviewVolume()));
        spotCheckIssueMQ.setConvertCoefficient(spotCheckDto.getVolumeRate() == null ? spotCheckDto.getVolumeRateStr() : spotCheckDto.getVolumeRate().toString());
        spotCheckIssueMQ.setConfirmWeightSource(spotCheckDto.getContrastSource());
        spotCheckIssueMQ.setDiffWeight(spotCheckDto.getDiffWeight() == null ? null : decimalFormat.format(spotCheckDto.getDiffWeight()));
        spotCheckIssueMQ.setStanderDiff(spotCheckDto.getDiffStandard());
        spotCheckIssueMQ.setExceedType(spotCheckDto.getExcessType());
        spotCheckIssueMQ.setStatus(Constants.CONSTANT_NUMBER_ONE);
        // 组装附件列表
        List<SpotCheckAppendixDto> appendixDtoList = transformAppendixData(spotCheckDto);
        // 新版抽检附件传参方式:传了appendixList，appendix和url字段就不用传了
        spotCheckIssueMQ.setAppendixList(appendixDtoList);
        spotCheckIssueMQ.setStartTime(new Date());
        if(logger.isInfoEnabled()){
            logger.info("下发运单号:{}的抽检超标数据至称重再造流程,明细如下:{}", spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        }
        spotCheckIssueProducer.sendOnFailPersistent(spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        // 更新抽检主记录数据
        spotCheckDto.setIsIssueDownstream(Constants.CONSTANT_NUMBER_ONE);
        spotCheckServiceProxy.insertOrUpdateProxyReform(spotCheckDto);
    }

    private List<SpotCheckAppendixDto> transformAppendixData(WeightVolumeSpotCheckDto spotCheckDto) {
        List<String> picList = picUrlDeal(spotCheckDto);
        List<SpotCheckAppendixDto> appendixDtoList = new ArrayList<>(6);
        // 图片
        if (CollectionUtils.isNotEmpty(picList)) {
            for (String picUrl : picList) {
                SpotCheckAppendixDto spotCheckAppendixDto = new SpotCheckAppendixDto();
                spotCheckAppendixDto.setAppendixType(SpotCheckAppendixTypeEnum.REPORT_PICTURE.getCode());
                spotCheckAppendixDto.setAppendixUrl(picUrl);
                appendixDtoList.add(spotCheckAppendixDto);
            }
        }
        // 视频
        if (Constants.NUMBER_ONE.equals(spotCheckDto.getIsHasVideo())) {
            SpotCheckAppendixDto spotCheckAppendixDto = new SpotCheckAppendixDto();
            spotCheckAppendixDto.setAppendixType(SpotCheckAppendixTypeEnum.REPORT_VIDEO.getCode());
            spotCheckAppendixDto.setAppendixUrl(spotCheckDto.getVideoPicture());
            appendixDtoList.add(spotCheckAppendixDto);
        }
        return appendixDtoList;
    }

    private List<String> picUrlDeal(WeightVolumeSpotCheckDto spotCheckDto) {
        List<String> picList = new ArrayList<>();
        if (StringUtils.isBlank(spotCheckDto.getPictureAddress())) {
            return picList;
        }
        if (SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())) {
            // 人工抽检（只有运单维度，无包裹维度）
            String[] picUrlList = spotCheckDto.getPictureAddress().split(Constants.SEPARATOR_SEMICOLON);
            // 重量超标：2张(面单和重量)  体积超标：5张(面单、全景、长、宽、高)
            for (String picUrl : picUrlList) {
                picList.add(BusinessHelper.switchOssUrlByType(picUrl, OssUrlNetTypeEnum.OUT.getType()));
            }
            return picList;
        }
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){
            // 设备抽检：
            // 1）、一单一件下发一张
            // 2）、一单多件不下发图片
            if(!Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE)){
                picList.add(BusinessHelper.switchOssUrlByType(spotCheckDto.getPictureAddress(), OssUrlNetTypeEnum.OUT.getType()));
            }
        }
        return picList;
    }

    @Override
    public String uploadExcessPicture(String originalFileName, InputStream inStream){
        String extName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String url = null;
        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc;
            while ((rc = inStream.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            inStream.close();
            swapStream.close();
            url = jssService.uploadFile(bucket, in2b, extName);
            logger.info("图片上传成功 : url[{}]", url);
        } catch (Exception e) {
            logger.error("图片上传时发生异常", e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error("图片上传输入流关闭异常", e);
                }
            }
            if (swapStream != null) {
                try {
                    swapStream.close();
                } catch (IOException e) {
                    logger.error("图片上传输出流关闭异常", e);
                }
            }
        }
        return url;
    }

    /**
     * 校验运单是否已下发
     *
     * @param waybillCode
     * @return
     */
    private boolean checkWaybillHasIssued(String waybillCode) {
        try {
            String cacheFxmSendWaybillKey = String.format(CacheKeyConstants.CACHE_FXM_SEND_WAYBILL, waybillCode);
            return jimdbCacheService.exists(cacheFxmSendWaybillKey);
        }catch (Exception e){
            logger.error("校验运单号:{}是否下发超标mq异常!", waybillCode);
        }
        return false;
    }

    @Override
    public String getSpotCheckPackUrlFromCache(String packageCode, Integer siteCode) {
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
            return jimdbCacheService.get(key);
        }catch (Exception e){
            logger.error("根据包裹号:{}站点:{}获取图片url异常!", packageCode, siteCode, e);
        }
        return null;
    }
    
    @Override
    public void dealSpotCheckWithDwsCalibrateData(DwsMachineCalibrateMQ dwsMachineCalibrateMQ) {
        String key = String.format(CacheKeyConstants.CACHE_KEY_DWS_CALIBRATE_SPOT_DEAL, dwsMachineCalibrateMQ.getBusinessId());
        if(!cacheCalibrateDealCacheIsSuc(key)){
            return;
        }
        Integer currentMachineStatus = dwsMachineCalibrateMQ.getMachineStatus();
        Integer previousMachineStatus = dwsMachineCalibrateMQ.getPreviousMachineStatus();
        Long currentCalibrateTime = dwsMachineCalibrateMQ.getCalibrateTime();
        Long previousCalibrateTime = dwsMachineCalibrateMQ.getPreviousCalibrateTime();

        // 抽检数据是否合格
        boolean isEligible = Objects.equals(currentMachineStatus, JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())
                && Objects.equals(previousMachineStatus, JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())
                && currentCalibrateTime != null && previousCalibrateTime != null
                && (currentCalibrateTime - previousCalibrateTime <= dmsConfigManager.getPropertyConfig().getMachineCalibrateIntervalTimeOfSpotCheck());

        // 获取时间范围内的抽检数据后分批处理
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setMachineCode(dwsMachineCalibrateMQ.getMachineCode());
        // 如果上次校准时间不存在，则默认取24h之前的时间
        Date previousMachineEligibleTime = previousCalibrateTime == null
                ? DateHelper.newTimeRangeHoursAgo(new Date(), 24) : new Date(previousCalibrateTime); // 上传设备校准时间
        Date currentMachineEligibleTime = new Date(dwsMachineCalibrateMQ.getCalibrateTime()); // 本次设备校准的时间
        condition.setReviewStartTime(previousMachineEligibleTime);
        condition.setReviewEndTime(currentMachineEligibleTime);
        condition.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        condition.setExcessStatusList(Lists.newArrayList(ExcessStatusEnum.EXCESS_ENUM_NO.getCode(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode()));

        int index = 1;
        while (index++ <= 100) {
            SpotCheckScrollResult spotCheckScrollResult = spotCheckQueryManager.querySpotCheckByScroll(condition);
            if(spotCheckScrollResult == null || CollectionUtils.isEmpty(spotCheckScrollResult.getList())){
                logger.warn("根据条件:{}按照scroll方式查询已超标抽检明细数据为空!", JsonHelper.toJson(condition));
                break;
            }
            
            // 拆分小list处理，降低消费端压力
            List<List<WeightVolumeSpotCheckDto>> partitionList = Lists.partition(spotCheckScrollResult.getList(), 100);
            partitionList.forEach(singletonList -> {
                List<Message> messageList = Lists.newArrayList();
                for (WeightVolumeSpotCheckDto spotCheckDto : singletonList) {
                    if(spotCheckDto.getMachineStatus() != null){
                        // 不处理的情况（此时表示状态已处理过）
                        logger.warn("此次抽检数据不做下发处理...单号:{}场地:{}抽检数据的设备状态:{}本次消息的businessId:{}消息中设备是否合格:{}",
                                spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode(), spotCheckDto.getMachineStatus(),
                                dwsMachineCalibrateMQ.getBusinessId(), isEligible);
                        continue;
                    }
                    // 设置设备状态
                    spotCheckDto.setMachineStatus(isEligible
                            ? JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode() : JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode());
                    Message message = new Message();
                    message.setTopic(dwsCalibrateDealSpotCheckProducer.getTopic());
                    message.setBusinessId(spotCheckDto.getPackageCode() + Constants.SEPARATOR_VERTICAL_LINE + spotCheckDto.getReviewSiteCode());
                    message.setText(JsonHelper.toJson(spotCheckDto));
                    messageList.add(message);
                }
                // 批量推送更新设备状态和下发处理的消息
                dwsCalibrateDealSpotCheckProducer.batchSendOnFailPersistent(messageList);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.error("线程sleep异常!", e);
                }
            });
            
            // 设置scrollId
            condition.setScrollId(spotCheckScrollResult.getScrollId());
        }
    }

    private boolean cacheCalibrateDealCacheIsSuc(String key) {
        try {
            return jimdbCacheService.setNx(key, 1,  30, TimeUnit.MINUTES);
        }catch (Exception e){
            logger.error("设置缓存key:{}异常!", key);
        }
        return false;
    }

    @Override
    public boolean spotCheckIssueIsRelyOnMachineStatus(Integer siteCode) {
        String spotCheckIssueByMachineStatusSwitch = dmsConfigManager.getPropertyConfig().getSpotCheckIssueRelyOnMachineStatusSiteSwitch();
        if(Objects.equals(spotCheckIssueByMachineStatusSwitch, "ALL")){
            return true;
        }
        return Arrays.asList(spotCheckIssueByMachineStatusSwitch.split(Constants.SEPARATOR_COMMA)).contains(String.valueOf(siteCode));
    }

    @Override
    public void executeIssueOfCompensate(String excelName) {
        long startTime = System.currentTimeMillis();
        if(StringUtils.isEmpty(excelName)){
            logger.error("文件名不能为空!");
            return;
        }
        InputStream inputStream = jssService.downloadFile("dms-export/excel", excelName);
        if(inputStream == null){
            logger.error("未查询到文件:{}", excelName);
            return;
        }
        // 表头字段对应对象字段名
        List<String> headList = Lists.newLinkedList();
        headList.add("waybillCode");
        headList.add("reviewWeight");
        headList.add("reviewVolume");
        headList.add("contrastWeight");
        headList.add("contrastVolume");
        List<WeightVolumeSpotCheckDto> spotCheckDtos = null;
        try {
            spotCheckDtos = ExcelUtils.readFromExcel(inputStream, ExcelUtils.EXCEL_SUFFIX_XLSX, WeightVolumeSpotCheckDto.class, headList);
        }catch (Exception e){
            logger.error("读取文件异常!");
        }
        if(CollectionUtils.isEmpty(spotCheckDtos)){
            logger.error("读取文件:{}为空!", excelName);
            return;
        }
        for (WeightVolumeSpotCheckDto item : spotCheckDtos) {
            buildCompensateAndIssue(item);
        }
        logger.info("批量下发异常数据完成,总耗时:{}", System.currentTimeMillis() - startTime);
    }
    
    private void buildCompensateAndIssue(WeightVolumeSpotCheckDto spotCheckDto) {
        SpotCheckIssueMQ spotCheckIssueMQ = new SpotCheckIssueMQ();
        spotCheckIssueMQ.setFlowSystem(SpotCheckConstants.ARTIFICIAL_SPOT_CHECK); // 现阶段只处理设备抽检数据，后续可调整
        spotCheckIssueMQ.setInitiationLink("3"); // 此值固定
        spotCheckIssueMQ.setOperateType(3); // 此值固定
        spotCheckIssueMQ.setWaybillCode(spotCheckDto.getWaybillCode());
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        spotCheckIssueMQ.setConfirmWeight(spotCheckDto.getContrastWeight() == null ? null : decimalFormat.format(spotCheckDto.getContrastWeight()));
        spotCheckIssueMQ.setConfirmVolume(spotCheckDto.getContrastVolume() == null ? null : decimalFormat.format(spotCheckDto.getContrastVolume()));
        spotCheckIssueMQ.setReConfirmWeight(spotCheckDto.getReviewWeight() == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(spotCheckDto.getReviewWeight()));
        spotCheckIssueMQ.setReConfirmVolume(spotCheckDto.getReviewVolume() == null ? String.valueOf(Constants.DOUBLE_ZERO) : String.valueOf(spotCheckDto.getReviewVolume()));
        if(logger.isInfoEnabled()){
            logger.info("重新下发运单号:{}的抽检超标数据至称重再造流程,明细如下:{}", spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        }
        spotCheckIssueProducer.sendOnFailPersistent(spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
    }

    private boolean isMultiPack(Waybill waybill, String packageCode) {
        int packNum;
        if(WaybillUtil.isPackageCode(packageCode)){
            packNum = WaybillUtil.getPackNumByPackCode(packageCode);
        }else {
            packNum = (waybill == null || waybill.getGoodNumber() ==  null) ? Constants.NUMBER_ZERO : waybill.getGoodNumber();
        }
        return packNum > Constants.CONSTANT_NUMBER_ONE;
    }
}
