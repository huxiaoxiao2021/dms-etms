package com.jd.bluedragon.distribution.spotcheck.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.spotcheck.domain.*;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.AbnormalResultMq;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.DutyTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.MathUtils;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
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
import java.math.BigDecimal;
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

    /**外部 访问域名 */
    private static final String STORAGE_DOMAIN_COM = "storage.jd.com";
    /**内部 访问域名 */
    private static final String STORAGE_DOMAIN_LOCAL = "storage.jd.local";

    private static final int OUT_EXCESS_STATUS = 3; // 外部门定义的未超标值

    @Value("${jss.pda.image.bucket}")
    private String bucket;

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

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
    @Qualifier("dmsWeightVolumeExcess")
    private DefaultJMQProducer dmsWeightVolumeExcess;

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
    public boolean checkIsHasSpotCheck(String waybillCode) {
        boolean isHasSpotCheck = false;
        try {
            String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK, waybillCode);
            if(StringUtils.isNotEmpty(jimdbCacheService.get(key))){
                isHasSpotCheck = true;
            }else {
                SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
                condition.setWaybillCode(waybillCode);
                condition.setExcessStatusList(Arrays.asList(ExcessStatusEnum.EXCESS_ENUM_NO.getCode(), ExcessStatusEnum.EXCESS_ENUM_YES.getCode()));
                isHasSpotCheck = spotCheckQueryManager.querySpotCheckCountByCondition(condition) > Constants.NUMBER_ZERO;
            }
        }catch (Exception e){
            logger.error("根据运单号{}查询是否操作过抽检异常!", waybillCode, e);
        }
        return isHasSpotCheck;
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
            // 图片缓存处理
            if(!spotCheckPicUrlCacheDealIsSuc(packageCode, siteCode, pictureUrl)){
                CallerInfo multiUploadCallerInfo = Profiler.registerInfo("dmsWeb.spotCheck.SpotCheckDealService.dealPictureUrl.multiUpload",
                        Constants.UMP_APP_NAME_DMSWEB,false,true);
                logger.warn("站点：{}包裹号：{}的图片已存在!", siteCode, packageCode);
                Profiler.registerInfoEnd(multiUploadCallerInfo);
                return;
            }
            // 执行抽检改造
            executeReformPicDeal(packageCode, siteCode, pictureUrl);

        }catch (Exception e){
            logger.error("包裹:{}的图片处理异常!", packageCode, e);
            Profiler.functionError(callerInfo);
            throw e;
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    private boolean spotCheckPicUrlCacheDealIsSuc(String packageCode, Integer siteCode, String pictureUrl) {
        String key = String.format(CacheKeyConstants.CACHE_SPOT_CHECK_PICTURE, packageCode, siteCode);
        if(jimdbCacheService.exists(key)){
            return false;
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setPackageCode(packageCode); // 包裹号和站点确定唯一
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

    private void executeReformPicDeal(String packageCode, Integer siteCode, String pictureUrl) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
        if(isMultiPack(waybill, packageCode)){
            // 一单多件
            // 1、更新包裹明细维度数据
            // 2、更新总记录维度数据
            // 3、一单多件不下发图片故不再此下发超标MQ
            WeightVolumeSpotCheckDto detailRecord = new WeightVolumeSpotCheckDto();
            detailRecord.setPackageCode(packageCode);
            detailRecord.setReviewSiteCode(siteCode);
            detailRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            detailRecord.setPictureAddress(pictureUrl);
            detailRecord.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyReform(detailRecord);
            WeightVolumeSpotCheckDto totalRecord = new WeightVolumeSpotCheckDto();
            totalRecord.setPackageCode(waybillCode);
            totalRecord.setReviewSiteCode(siteCode);
            totalRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            totalRecord.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyReform(totalRecord);
            // 4.下发超标数据
            if(isExecuteDwsAIDistinguish(siteCode)){
                if(checkIsExcessFromRedis(waybillCode)){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error("阻塞1s异常!", e);
                    }
                    totalRecord.setWaybillCode(waybillCode);
                    totalRecord.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
                    totalRecord.setIsGatherTogether(Constants.CONSTANT_NUMBER_ONE);
                    dwsIssueDealProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(totalRecord));
                }
            }
        }else {
            // 一单一件
            // 1、更新总记录维度数据
            WeightVolumeSpotCheckDto summaryRecord = new WeightVolumeSpotCheckDto();
            summaryRecord.setPackageCode(waybillCode);
            summaryRecord.setReviewSiteCode(siteCode);
            summaryRecord.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            summaryRecord.setPictureAddress(pictureUrl);
            summaryRecord.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
            spotCheckServiceProxy.insertOrUpdateProxyReform(summaryRecord);
            // 2、一单一件下发超标数据要有图片，故图片上传在抽检数据之后则需要下发超标MQ
            if(!checkIsExcessFromRedis(waybillCode)){
                // 未超标则直接返回
                return;
            }
            WeightVolumeSpotCheckDto spotCheckDto = searchSpotCheck(packageCode, waybillCode, siteCode);
            if(spotCheckDto == null){
                return;
            }
            spotCheckDto.setIsHasPicture(Constants.CONSTANT_NUMBER_ONE);
            spotCheckDto.setPictureAddress(pictureUrl); // 此处设置防止es未查询到上面刚插入的数据
            spotCheckIssue(spotCheckDto);
        }
    }

    /**
     * 获取抽检汇总记录
     *
     * @param packageCode
     * @param waybillCode
     * @param siteCode
     * @return
     */
    private WeightVolumeSpotCheckDto searchSpotCheck(String packageCode, String waybillCode, Integer siteCode) {
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setWaybillCode(waybillCode);
        condition.setPackageCode(waybillCode);
        condition.setReviewSiteCode(siteCode);
        condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        List<WeightVolumeSpotCheckDto> spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
        if(CollectionUtils.isEmpty(spotCheckList)){
            // 已抽检的数据落es，在1s内立即查询会查询不到数据，故此处睡眠1s后在查询一次
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("阻塞1s异常!", e);
            }
            spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
            if(CollectionUtils.isEmpty(spotCheckList)){
                logger.warn("未查询到包裹号:{}站点:{}的已超标抽检数据!", packageCode, siteCode);
                return null;
            }
        }
        return spotCheckList.get(0);
    }

    @Override
    public boolean isExecuteSpotCheckReform(Integer siteCode) {
        try {
            String newSpotCheckSiteCodes = uccPropertyConfiguration.getSpotCheckReformSiteCodes();
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
            String dwsAIDistinguishSiteCodes = uccPropertyConfiguration.getDeviceAIDistinguishSwitch();
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
        if(!uccPropertyConfiguration.getAiDistinguishSwitch()){
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
                ? null : Integer.valueOf(reportInfo.getConvertCoefficient()));
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
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                && isExecuteDwsAIDistinguish(spotCheckDto.getReviewSiteCode())){
            if(logger.isInfoEnabled()){
                logger.info("设备抽检图片AI识别下发运单:{}站点:{}的数据!", spotCheckDto.getWaybillCode(), spotCheckDto.getReviewSiteCode());
            }
            String picUrl = StringUtils.isEmpty(spotCheckDto.getPictureAddress())
                    ? getSpotCheckPackUrlFromCache(spotCheckDto.getPackageCode(), spotCheckDto.getReviewSiteCode()) : spotCheckDto.getPictureAddress();
            if(StringUtils.isEmpty(picUrl)){
                return;
            }
            List<DwsAIDistinguishMQ.Package> list = new ArrayList<>();
            DwsAIDistinguishMQ.Package packageUrl = new DwsAIDistinguishMQ.Package();
            packageUrl.setPackageCode(spotCheckDto.getPackageCode());
            packageUrl.setPicUrl(picUrl.replace(STORAGE_DOMAIN_COM, STORAGE_DOMAIN_LOCAL));
            list.add(packageUrl);
            DwsAIDistinguishMQ dwsAIDistinguishMQ = new DwsAIDistinguishMQ();
            dwsAIDistinguishMQ.setUuid(spotCheckDto.getWaybillCode().concat(Constants.SEPARATOR_HYPHEN).concat(String.valueOf(System.currentTimeMillis())));
            dwsAIDistinguishMQ.setWaybillCode(spotCheckDto.getWaybillCode());
            dwsAIDistinguishMQ.setSiteCode(spotCheckDto.getReviewSiteCode());
            dwsAIDistinguishMQ.setPackages(list);
            dwsAIDistinguishSmallProducer.sendOnFailPersistent(dwsAIDistinguishMQ.getWaybillCode(), JsonHelper.toJson(dwsAIDistinguishMQ));
            return;
        }
        // 执行下发
        executeIssue(spotCheckDto);
    }

    @Override
    public void executeIssue(WeightVolumeSpotCheckDto spotCheckDto) {
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
            spotCheckIssueZDProducer.sendOnFailPersistent(spotCheckIssueZDMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueZDMQ));
        }
        // 下发超标数据至称重再造系统条件
        // 1、图片
        // 1.1、人工抽检：图片肯定有（都是按运单维度操作，肯定有5张图片）
        // 1.2、设备抽检：一单一件下发一张则必须要有图片，一单多件不下发图片则不需要图片限制条件
        if(
                (SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                        || !Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE))
                && !Objects.equals(spotCheckDto.getIsHasPicture(), Constants.CONSTANT_NUMBER_ONE)
        ){
            return;
        }
        // 设置已下发缓存
        setIssueWaybillCache(spotCheckDto.getWaybillCode());
        // 构建超标消息并下发
        buildAndIssue(spotCheckDto, false);
    }

    /**
     * 构建并下发数据
     *
     * @param spotCheckDto
     * @param isBrush 是否刷数
     */
    private void buildAndIssue(WeightVolumeSpotCheckDto spotCheckDto, boolean isBrush) {
        SpotCheckIssueMQ spotCheckIssueMQ = new SpotCheckIssueMQ();
        spotCheckIssueMQ.setFlowSystem(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        spotCheckIssueMQ.setInitiationLink(String.valueOf(SpotCheckConstants.DMS_SPOT_CHECK_ISSUE));
        spotCheckIssueMQ.setSysSource(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())
                ? String.valueOf(2) : String.valueOf(1)); // 1：设备抽检 2：人工抽检
        spotCheckIssueMQ.setErrCode(isBrush ? "20220905update" : Constants.EMPTY_FILL);
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
        spotCheckIssueMQ.setConvertCoefficient(spotCheckDto.getVolumeRate() == null ? null : String.valueOf(spotCheckDto.getVolumeRate()));
        spotCheckIssueMQ.setConfirmWeightSource(spotCheckDto.getContrastSource());
        spotCheckIssueMQ.setDiffWeight(spotCheckDto.getDiffWeight() == null ? null : decimalFormat.format(spotCheckDto.getDiffWeight()));
        spotCheckIssueMQ.setStanderDiff(spotCheckDto.getDiffStandard());
        spotCheckIssueMQ.setExceedType(spotCheckDto.getExcessType());
        spotCheckIssueMQ.setStatus(spotCheckDto.getSpotCheckStatus());
        spotCheckIssueMQ.setAppendix(Constants.CONSTANT_NUMBER_ONE);
        spotCheckIssueMQ.setUrl(picUrlDeal(spotCheckDto));
        spotCheckIssueMQ.setStartTime(new Date());
        if(logger.isInfoEnabled()){
            logger.info("下发运单号:{}的抽检超标数据至称重再造流程,明细如下:{}", spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        }
        spotCheckIssueProducer.sendOnFailPersistent(spotCheckIssueMQ.getWaybillCode(), JsonHelper.toJson(spotCheckIssueMQ));
        // 更新抽检主记录数据
        spotCheckDto.setIsIssueDownstream(Constants.CONSTANT_NUMBER_ONE);
        spotCheckServiceProxy.insertOrUpdateProxyReform(spotCheckDto);
    }

    private List<String> picUrlDeal(WeightVolumeSpotCheckDto spotCheckDto) {
        List<String> picList = new ArrayList<>();
        if(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){
            // 人工抽检（只有运单维度，无包裹维度）
            List<String> picUrlList = Arrays.asList(spotCheckDto.getPictureAddress().split(Constants.SEPARATOR_SEMICOLON));
            if(picUrlList.size() < 5){
                logger.warn("运单:{}的抽检图片未集齐!", spotCheckDto.getWaybillCode());
                return picList;
            }
            if(Objects.equals(spotCheckDto.getExcessType(), SpotCheckConstants.EXCESS_TYPE_WEIGHT)){
                // 1）、重量超标：2张，面单和重量
                picList.add(replaceInOut(picUrlList.get(0)));
                picList.add(replaceInOut(picUrlList.get(1)));
                return picList;
            }else {
                // 2）、体积超标：5张，面单、全景、长、宽、高
                for (String picUrl : picUrlList) {
                    picList.add(replaceInOut(picUrl));
                }
                return picList;
            }
        }
        if(SpotCheckSourceFromEnum.EQUIPMENT_SOURCE_NUM.contains(spotCheckDto.getReviewSource())){
            // 设备抽检：
            // 1）、一单一件下发一张
            // 2）、一单多件不下发图片
            if(!Objects.equals(spotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE)){
                picList.add(replaceInOut(spotCheckDto.getPictureAddress()));
            }
        }
        return picList;
    }

    private String replaceInOut(String inAddress) {
        if(StringUtils.isEmpty(inAddress)){
            return Constants.EMPTY_FILL;
        }
        return inAddress.replace(STORAGE_DOMAIN_LOCAL, STORAGE_DOMAIN_COM);
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
    public void brushSpotCheck(List<WeightVolumeSpotCheckDto> list, String userErp) {
        try {
            if(CollectionUtils.isEmpty(list)){
                return;
            }
            if(Objects.equals(userErp, "liuduo8")){
                for (WeightVolumeSpotCheckDto dto : list) {
                    if(!WaybillUtil.isWaybillCode(dto.getWaybillCode())){
                        continue;
                    }
                    WeightVolumeSpotCheckDto newSpotCheckDto = getSpotCheck(dto, true);
                    if(newSpotCheckDto == null){
                        continue;
                    }
                    // 调用超标服务并更新
                    checkExcessAndUpdate(newSpotCheckDto);
                    // 刷数并重新下发
                    buildAndIssue(newSpotCheckDto, true);
                }
            }else if(Objects.equals(userErp, "hujiping1")) {
                for (WeightVolumeSpotCheckDto dto : list) {
                    if (!WaybillUtil.isWaybillCode(dto.getWaybillCode())) {
                        continue;
                    }
                    if(dto.getReviewWeight() == null || dto.getReviewVolume() == null){
                        logger.warn("自己填写重量体积不存在,单号:{}", dto.getWaybillCode());
                        continue;
                    }
                    // 组装超标新es实体
                    WeightVolumeSpotCheckDto newSpotCheckDto = getSpotCheck(dto, false);
                    if(newSpotCheckDto == null){
                        continue;
                    }
                    // 使用excel的重量体积下发
                    newSpotCheckDto.setReviewWeight(MathUtils.keepScale(dto.getReviewWeight(),2));
                    newSpotCheckDto.setReviewVolume(MathUtils.keepScale(dto.getReviewVolume(),2));
                    if(logger.isInfoEnabled()){
                        logger.info("自己填写重量体积下发fxm,单号:{}", newSpotCheckDto.getWaybillCode());
                    }
                    issueFxm(newSpotCheckDto);
                }
            }else {
                for (WeightVolumeSpotCheckDto dto : list) {
                    if (!WaybillUtil.isWaybillCode(dto.getWaybillCode())) {
                        continue;
                    }
                    // 组装超标新es实体
                    WeightVolumeSpotCheckDto newSpotCheckDto = null;

                    if(Objects.equals(dto.getExcessType(), 1)){
                        // 重量超标
                        if(Objects.equals(dto.getContrastSource(), 1)){
                            // 重量超标&&计费计费重量，推送分拣重量
                            newSpotCheckDto = getSpotCheck(dto, true);
                        }
                    }else if(Objects.equals(dto.getExcessType(), 2)){
                        // 体积超标
                        if(Objects.equals(dto.getContrastSource(), 1)){
                            // 重量超标&&计费计费重量，推送核对重量
                            newSpotCheckDto = getSpotCheck(dto, false);
                        }
                    }else if(Objects.equals(dto.getExcessType(), 3)){
                        // 未超标&&计费计费重量，推送核对重量
                        if(Objects.equals(dto.getContrastSource(), 1)){
                            newSpotCheckDto = getSpotCheck(dto, false);
                        }
                    }

                    if(newSpotCheckDto == null){
                        continue;
                    }
                    if(logger.isInfoEnabled()){
                        logger.info("系统获取重量体积下发fxm,单号:{}", newSpotCheckDto.getWaybillCode());
                    }
                    // 下发fxm中转给计费来刷数
                    issueFxm(newSpotCheckDto);
                }
            }

        }catch (Exception e){
            logger.error("修数异常!", e);
        }
    }

    private WeightVolumeSpotCheckDto getSpotCheck(WeightVolumeSpotCheckDto dto, boolean isGetSummaryWeight) {
        WeightVolumeSpotCheckDto newSpotCheckDto = null;
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setWaybillCode(dto.getWaybillCode());
        condition.setReviewSiteCode(dto.getReviewSiteCode());
        List<WeightVolumeSpotCheckDto> spotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
        if(CollectionUtils.isEmpty(spotCheckList)){
            return null;
        }
        Double weight = Constants.DOUBLE_ZERO;
        Double volume = Constants.DOUBLE_ZERO;
        for (WeightVolumeSpotCheckDto spotCheckDto : spotCheckList) {
            if(Objects.equals(spotCheckDto.getRecordType(), SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode())){
                newSpotCheckDto = spotCheckDto;
            }else {
                weight += spotCheckDto.getReviewWeight() == null ? Constants.DOUBLE_ZERO : spotCheckDto.getReviewWeight();
                volume += spotCheckDto.getReviewVolume() == null ? Constants.DOUBLE_ZERO : spotCheckDto.getReviewVolume();
            }
        }
        if(newSpotCheckDto == null){
            return null;
        }
        if(isGetSummaryWeight){
            newSpotCheckDto.setReviewWeight(MathUtils.keepScale(weight,2));
            newSpotCheckDto.setReviewVolume(MathUtils.keepScale(volume,2));
        }else {
            newSpotCheckDto.setReviewWeight(MathUtils.keepScale(newSpotCheckDto.getContrastWeight(),2));
            newSpotCheckDto.setReviewVolume(MathUtils.keepScale(newSpotCheckDto.getContrastVolume(),2));
        }
        return newSpotCheckDto;
    }

    private void issueFxm(WeightVolumeSpotCheckDto spotCheckDto) {

        AbnormalResultMq abnormalResultMq = new AbnormalResultMq();
        abnormalResultMq.setId(spotCheckDto.getWaybillCode() + Constants.UNDERLINE_FILL + spotCheckDto.getReviewDate());
        abnormalResultMq.setAbnormalId(spotCheckDto.getWaybillCode() + Constants.UNDERLINE_FILL + spotCheckDto.getReviewDate());
        abnormalResultMq.setSource(2);
        abnormalResultMq.setFrom("2");
        abnormalResultMq.setBusinessType(1);
        abnormalResultMq.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        // 复核数据
        abnormalResultMq.setBillCode(spotCheckDto.getWaybillCode());
//        abnormalResultMq.setBusinessObjectId(spotCheckDto.getMerchantCode());
        abnormalResultMq.setBusinessObject(spotCheckDto.getMerchantName());
        abnormalResultMq.setOperateTime(new Date());
        abnormalResultMq.setIsTrustMerchant(spotCheckDto.getIsTrustMerchant());
        abnormalResultMq.setReviewWeight(spotCheckDto.getReviewWeight());
        abnormalResultMq.setReviewVolume(spotCheckDto.getReviewVolume());
        abnormalResultMq.setReviewErp(spotCheckDto.getReviewUserErp());
        abnormalResultMq.setReviewFirstLevelId(spotCheckDto.getReviewOrgCode());
        abnormalResultMq.setReviewFirstLevelName(spotCheckDto.getReviewOrgName());
        abnormalResultMq.setReviewSecondLevelId(spotCheckDto.getReviewSiteCode());
        abnormalResultMq.setReviewSecondLevelName(spotCheckDto.getReviewSiteName());
        abnormalResultMq.setReviewDate(new Date(spotCheckDto.getReviewDate()));
        abnormalResultMq.setReviewDutyErp(spotCheckDto.getReviewUserErp());
        abnormalResultMq.setReviewDutyType(DutyTypeEnum.DMS.getCode());
        // 核对数据
        abnormalResultMq.setFirstLevelId(spotCheckDto.getContrastOrgCode() == null ? null : String.valueOf(spotCheckDto.getContrastOrgCode()));
        abnormalResultMq.setFirstLevelName(spotCheckDto.getContrastOrgName());
        abnormalResultMq.setSecondLevelId(spotCheckDto.getContrastAreaCode());
        abnormalResultMq.setSecondLevelName(spotCheckDto.getContrastAreaName());
        abnormalResultMq.setThreeLevelId(spotCheckDto.getContrastWarZoneCode());
        abnormalResultMq.setThreeLevelName(spotCheckDto.getContrastWarZoneName());
        abnormalResultMq.setWeight(spotCheckDto.getContrastWeight() == null
                ? new BigDecimal(Constants.DOUBLE_ZERO) : BigDecimal.valueOf(spotCheckDto.getContrastWeight()));
        abnormalResultMq.setVolume(spotCheckDto.getContrastVolume() == null
                ? new BigDecimal(Constants.DOUBLE_ZERO) : BigDecimal.valueOf(spotCheckDto.getContrastVolume()));
//        abnormalResultMq.setDutyType(weightVolumeCollectDto.getDutyType());
//        abnormalResultMq.setDutyErp(weightVolumeCollectDto.getBillingErp());
        // 差异数据
        abnormalResultMq.setDiffStandard(spotCheckDto.getDiffStandard());
        abnormalResultMq.setWeightDiff(spotCheckDto.getDiffWeight());
        // 只给计费
        abnormalResultMq.setTo("1");
        if(logger.isInfoEnabled()){
            logger.info("下发运单号:{}的抽检超标数据,明细如下:{}", abnormalResultMq.getBillCode(), JsonHelper.toJson(abnormalResultMq));
        }
        dmsWeightVolumeExcess.sendOnFailPersistent(abnormalResultMq.getAbnormalId(), JsonHelper.toJson(abnormalResultMq));
    }

    private void checkExcessAndUpdate(WeightVolumeSpotCheckDto newSpotCheckDto) {
        String waybillCode = newSpotCheckDto.getWaybillCode();
        Double reviewWeight = newSpotCheckDto.getReviewWeight();
        Double reviewVolume = newSpotCheckDto.getReviewVolume();

        ReportInfoQuery reportInfoQuery = new ReportInfoQuery();
        reportInfoQuery.setWaybillCode(waybillCode);
        reportInfoQuery.setChannel(SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(newSpotCheckDto.getReviewSource())
                ? SpotCheckConstants.ARTIFICIAL_SPOT_CHECK : SpotCheckConstants.EQUIPMENT_SPOT_CHECK);
        reportInfoQuery.setMeasureWeight(NumberHelper.formatMoney(reviewWeight));
        reportInfoQuery.setMeasureVolume(NumberHelper.formatMoney(reviewVolume));
        CommonDTO<ReportInfoDTO> commonDTO = weightReportCommonRuleManager.getReportInfo(reportInfoQuery);
        ReportInfoDTO reportInfo = (commonDTO == null || !Objects.equals(commonDTO.getCode(), CommonDTO.CODE_SUCCESS)) ? null : commonDTO.getData();
        // 组装核对数据
        newSpotCheckDto.setContrastSource(reportInfo == null ? null : reportInfo.getRecheckSource());
        newSpotCheckDto.setContrastWeight((reportInfo == null || reportInfo.getRecheckWeight() == null)
                ? null : MathUtils.keepScale(Double.parseDouble(reportInfo.getRecheckWeight()), 2));
        newSpotCheckDto.setContrastVolume((reportInfo == null || reportInfo.getRecheckVolume() == null)
                ? null : MathUtils.keepScale(Double.parseDouble(reportInfo.getRecheckVolume()), 2));
        newSpotCheckDto.setContrastOrgCode(reportInfo == null ? null : reportInfo.getDutyOrgId());
        newSpotCheckDto.setContrastOrgName(reportInfo == null ? null : reportInfo.getDutyOrgName());
        newSpotCheckDto.setContrastWarZoneCode(reportInfo == null ? null : reportInfo.getDutyProvinceCompanyCode());
        newSpotCheckDto.setContrastWarZoneName(reportInfo == null ? null : reportInfo.getDutyProvinceCompanyName());
        newSpotCheckDto.setContrastAreaCode(reportInfo == null ? null : reportInfo.getDutyAreaCode());
        newSpotCheckDto.setContrastAreaName(reportInfo == null ? null : reportInfo.getDutyAreaName());
        newSpotCheckDto.setContrastSiteCode(reportInfo == null ? null : reportInfo.getDutySiteId());
        newSpotCheckDto.setContrastSiteName(reportInfo == null ? null : reportInfo.getDutySiteName());
        newSpotCheckDto.setContrastStaffAccount(reportInfo == null ? null : reportInfo.getDutyStaffAccount());
        newSpotCheckDto.setContrastStaffName(reportInfo == null ? null : reportInfo.getDutyStaffName());
        newSpotCheckDto.setContrastStaffType(reportInfo == null ? null : reportInfo.getDutyStaffType());
        Integer dutyType = reportInfo == null ? null : reportInfo.getDutyType();
        newSpotCheckDto.setContrastDutyType(dutyType != null ? dutyType : 99);
        // 超标数据
        int excessStatus = ExcessStatusEnum.EXCESS_ENUM_NO.getCode();
        if(reportInfo != null && reportInfo.getExceedType() != null){
            excessStatus = Objects.equals(reportInfo.getExceedType(), OUT_EXCESS_STATUS)
                    ? ExcessStatusEnum.EXCESS_ENUM_NO.getCode() : ExcessStatusEnum.EXCESS_ENUM_YES.getCode();
        }
        newSpotCheckDto.setIsExcess(excessStatus);
        newSpotCheckDto.setExcessType(reportInfo == null ? null : reportInfo.getExceedType());
        newSpotCheckDto.setDiffWeight((reportInfo == null || reportInfo.getDiffWeight() == null) ? null : Double.parseDouble(reportInfo.getDiffWeight()));
        newSpotCheckDto.setDiffStandard(reportInfo == null ? null : reportInfo.getDiffStandard());
        newSpotCheckDto.setVolumeRate((reportInfo == null || reportInfo.getConvertCoefficient() == null)
                ? null : Integer.valueOf(reportInfo.getConvertCoefficient()));

        newSpotCheckDto.setSpotCheckStatus(SpotCheckStatusEnum.SPOT_CHECK_STATUS_VERIFY.getCode());
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
