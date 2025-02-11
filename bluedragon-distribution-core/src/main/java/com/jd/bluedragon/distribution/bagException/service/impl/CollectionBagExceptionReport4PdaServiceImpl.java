package com.jd.bluedragon.distribution.bagException.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.box.request.BoxCollectionReportRequest;
import com.jd.bluedragon.common.dto.box.request.QueryBoxCollectionReportRequest;
import com.jd.bluedragon.common.dto.box.response.QueryBoxCollectionReportResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.bagException.dao.CollectionBagExceptionReportDao;
import com.jd.bluedragon.distribution.bagException.domain.CollectionBagExceptionReport;
import com.jd.bluedragon.distribution.bagException.dto.SiteWaybillTraceInfoDto;
import com.jd.bluedragon.distribution.bagException.enums.CollectionBagExceptionReportTypeEnum;
import com.jd.bluedragon.distribution.bagException.service.CollectionBagExceptionReport4PdaService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.alibaba.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.DmsDisSendJsfService;
import com.jd.ql.dms.report.domain.dmsDisSend.DmsDisSend;
import com.jd.ql.dms.report.domain.dmsDisSend.DmsDisSendQueryCondition;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 集包异常举报
 *
 * @author fanggang7
 * @time 2020-09-27 17:03:50 周日
 */
@Service
public class CollectionBagExceptionReport4PdaServiceImpl implements CollectionBagExceptionReport4PdaService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CollectionBagExceptionReportDao collectionBagExceptionReportDao;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private DmsDisSendJsfService dmsDisSendJsfService;

    // 揽收品类
    private final static Integer DICT_PRODUCT_TYPE = 70550731;
    // 易碎类型
    @Value("${waybill.cargo_type.dict_product_type_fragile:24}")
    private Integer DICT_PRODUCT_TYPE_FRAGILE;
    private final static String DICT_PRODUCT_TYPE_FRAGILE_NAME = "易碎物品";

    /**
     * 查询集包建箱是否有异常
     *
     * @param query 请求参数
     * @return QueryBoxCollectionReportResponse
     * @author fanggang7
     * @time 2020-09-23 21:26:39 周三
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CollectionBagExceptionReport4PdaServiceImpl.queryBagCollectionHasException",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<QueryBoxCollectionReportResponse> queryBagCollectionHasException(QueryBoxCollectionReportRequest query) {
        if(log.isInfoEnabled()){
            log.info("CollectionBagExceptionReportServiceImpl.queryBagCollectionHasException param {}", JSON.toJSONString(query));
        }
        JdCResponse<QueryBoxCollectionReportResponse> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        QueryBoxCollectionReportResponse reportResponse = new QueryBoxCollectionReportResponse();
        try {
            // 验证参数
            JdCResponse<Boolean> checkResult = this.checkParamForQueryBagCollectionHasException(query);
            if (checkResult.isFail()){
                result.init(JdCResponse.CODE_FAIL, checkResult.getMessage());
                return result;
            }

            String packageCode = query.getPackageCode();
            Integer siteCode = query.getCurrentOperate().getSiteCode();
            reportResponse.setPackageCode(packageCode);
            reportResponse.setSiteCode(siteCode);
            this.setNoExceptionResponse(reportResponse);
            // 过滤是否能举报的必要条件：1. 外单标位 2. 非生鲜（运单及货物类型）、非易碎
            String waybillCode = WaybillUtil.getWaybillCodeByPackCode(query.getPackageCode());
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
            if(waybill == null){
                result.init(JdCResponse.CODE_FAIL, "查询失败，根据此包裹查不到运单信息");
                return result;
            }
            JdCResponse<Boolean> checkCanReportResult = this.canReportException(waybill);
            boolean canReportFlag = checkCanReportResult.getData();
            if(!canReportFlag){
                result.setMessage(checkCanReportResult.getMessage());
                this.setNoExceptionResponse(reportResponse);
            }
            // 查询包裹称重流水，得到长宽高数据
            PackFlowDetail latestPackFlowDetail = this.getLatestPackFlowDetail(query.getPackageCode());
            if(latestPackFlowDetail != null){
                // 设置长宽高信息
                reportResponse.setWeight(latestPackFlowDetail.getpWeight());
                reportResponse.setLength(latestPackFlowDetail.getpLength());
                reportResponse.setWidth(latestPackFlowDetail.getpWidth());
                reportResponse.setHeight(latestPackFlowDetail.getpHigh());
            } else{
                if(canReportFlag) {
                    result.setMessage("不能举报，原因：不符合条件，无称重记录");
                }
                this.setNoExceptionResponse(reportResponse);
                if(log.isInfoEnabled()) {
                    log.info("waybillPackageManager.getOpeDetailByCode 无称重明细 packageCode {}", packageCode);
                }
                result.setData(reportResponse);
                return result;
            }

            if(!canReportFlag){
                result.setData(reportResponse);
                return result;
            }
            CallerInfo infotmp1 = Profiler.registerInfo("DMS.BASE.CollectionBagExceptionReport4PdaServiceImpl.getPreSendSiteIdtmp1", Constants.UMP_APP_NAME_DMSWEB, false, true);
            // 查询包裹起始、目的地信息及上游箱号。
            Integer preSendSiteId = this.getPreSendSiteId(packageCode, siteCode);
            if(preSendSiteId != null) {
                CallerInfo infotmp2 = Profiler.registerInfo("DMS.BASE.CollectionBagExceptionReport4PdaServiceImpl.getPreSendSiteIdtmp2", Constants.UMP_APP_NAME_DMSWEB, false, true);
                // 查询分拣数据得到上游箱号
                String lastUpstreamBoxCode = this.getLastUpstreamBoxCode(query.getPackageCode(), preSendSiteId, siteCode);
                boolean isBoxCode = BusinessUtil.isBoxcode(lastUpstreamBoxCode);
                if(isBoxCode){
                    reportResponse.setUpstreamBoxCode(lastUpstreamBoxCode);
                }
                Profiler.registerInfoEnd(infotmp2);
            }
            Profiler.registerInfoEnd(infotmp1);

            // 查询分拣数据
            if(preSendSiteId == null){
                SiteWaybillTraceInfoDto preSendSiteTrackData = this.getPreSendSiteTrackData(packageCode, siteCode);
                PackageState sendData = preSendSiteTrackData.getSendData();
                PackageState boxingData = preSendSiteTrackData.getBoxingData();
                if(sendData != null){
                    preSendSiteId = sendData.getOperatorSiteId();
                    if(boxingData != null){
                        reportResponse.setUpstreamBoxCode("-");
                    }
                }
            }

            reportResponse.setBoxStartId(preSendSiteId);
            reportResponse.setBoxEndSiteId(siteCode);

            if(reportResponse.getBoxStartId() != null) {
                BaseStaffSiteOrgDto createSiteData = baseMajorManager.getBaseSiteBySiteId(reportResponse.getBoxStartId());
                reportResponse.setBoxStartSiteName(createSiteData != null ? createSiteData.getSiteName() : "未查到站点" + reportResponse.getBoxStartId() + "的数据");
            }
            if(reportResponse.getBoxEndSiteId() != null) {
                BaseStaffSiteOrgDto receiveSiteData = baseMajorManager.getBaseSiteBySiteId(reportResponse.getBoxEndSiteId());
                reportResponse.setBoxEndSiteName(receiveSiteData != null ? receiveSiteData.getSiteName() :  "未查到站点" + reportResponse.getBoxEndSiteId() + "的数据");
            }

            // 判断3种情况的逻辑，1. 虚假集包 2. 上游未集包 3. 无异常
            this.judgeBoxException(result, reportResponse, waybill);
        } catch (Exception e) {
            log.error("CollectionBagExceptionReportServiceImpl.queryBagCollectionHasException exception {}", e.getMessage(), e);
            result.toFail("查询异常");
        }
        result.setData(reportResponse);
        return result;
    }

    /**
     * 根据包裹号及操作场地查询上游发货场地
     * @param packageCode 包裹号
     * @param currentSiteCode 场地
     * @return 上游发货场地
     * @author fanggang7
     * @time 2020-10-16 15:11:22 周五
     */
    private Integer getPreSendSiteId(String packageCode, Integer currentSiteCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.CollectionBagExceptionReport4PdaServiceImpl.getPreSendSiteId", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            // 查询包裹起始、目的地信息及上游箱号。
            Integer createSiteCode = null;
            DmsDisSendQueryCondition dmsDisSendQueryCondition = new DmsDisSendQueryCondition();
            dmsDisSendQueryCondition.setPackageCode(packageCode);
            dmsDisSendQueryCondition.setDesSendSiteId(currentSiteCode);
            dmsDisSendQueryCondition.setIsCancel(0);
            com.jd.ql.dms.report.domain.BaseEntity<List<DmsDisSend>> disSendResult =
                    dmsDisSendJsfService.queryByConditionFromEs(dmsDisSendQueryCondition, 100);
            if(disSendResult != null && disSendResult.getCode() == com.jd.ql.dms.report.domain.BaseEntity.CODE_SUCCESS
                    && CollectionUtils.isNotEmpty(disSendResult.getData())){
                DmsDisSend dmsDisSend = disSendResult.getData().get(0);
                createSiteCode = dmsDisSend.getDmsSiteId();
            }
            return createSiteCode;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private SiteWaybillTraceInfoDto getPreSendSiteTrackData(String packageCode, Integer currentSiteCode) {
        SiteWaybillTraceInfoDto siteWaybillTraceInfoDto = new SiteWaybillTraceInfoDto();
        PackageState sendData = null;
        PackageState boxingData = null;
        // 从es查不到，可能是上游未营业部，再查全程跟踪
        BaseEntity<List<PackageState>> waybillTrackResult = waybillTraceManager.getPkStateByPCode(packageCode);
        // 解析全程跟踪数据
        if(waybillTrackResult != null && waybillTrackResult.getData() != null && waybillTrackResult.getData().size() > 0){
            List<PackageState> packageStateList = waybillTrackResult.getData();
            // 按全程跟踪的创建时间降序排列排序
            Collections.sort(packageStateList, new Comparator<PackageState>() {
                @Override
                public int compare(PackageState v1, PackageState v2) {
                    return v2.getCreateTime().compareTo(v1.getCreateTime());
                }
            });
            // 遍历，找到当前分拣中心之前的操作数据
            boolean findCurrentSiteOpLog = false;
            boolean findSendBySite = false;
            List<String> matchedSendStatus = new ArrayList<>(Arrays.asList(Constants.WAYBILL_TRACE_STATE_SEND_BY_SITE));
            List<String> matchedGenBoxStatus = new ArrayList<>(Arrays.asList(Constants.WAYBILL_TRACE_STATE_BOXING_BY_SITE));
            Integer preSiteCode = null;
            for(PackageState packageState : packageStateList){
                if(Objects.equals(packageState.getOperatorSiteId(), currentSiteCode)) {
                    //  找到当前分拣中心的操作记录，往后就是上游节点的数据
                    findCurrentSiteOpLog = true;
                }
                if (findCurrentSiteOpLog && !Objects.equals(packageState.getOperatorSiteId(), currentSiteCode)){
                    preSiteCode = packageState.getOperatorSiteId();
                }
                if(Objects.equals(packageState.getOperatorSiteId(), preSiteCode) && findCurrentSiteOpLog && matchedSendStatus.contains(packageState.getState())){
                    sendData = packageState;
                    findSendBySite = true;
                }
                if(Objects.equals(packageState.getOperatorSiteId(), preSiteCode) && findCurrentSiteOpLog && findSendBySite && matchedGenBoxStatus.contains(packageState.getState())){
                    boxingData = packageState;
                    break;
                }
            }
            if(!findCurrentSiteOpLog || !findSendBySite) {
                log.warn("CollectionBagExceptionReportServiceImpl 未找到上游操作地: {}", JSON.toJSONString(waybillTrackResult));
            }
        }
        siteWaybillTraceInfoDto.setSendData(sendData);
        siteWaybillTraceInfoDto.setBoxingData(boxingData);
        return siteWaybillTraceInfoDto;
    }

    /**
     * 获取当前场地的上游箱号
     * @param packageCode 包裹号
     * @param preSendSiteId 上游场地
     * @param receiveSiteCode 当前场地
     * @return 上游箱号
     */
    private String getLastUpstreamBoxCode(String packageCode, Integer preSendSiteId, Integer receiveSiteCode) {
        // 查询分拣数据
        Sorting sortingParam = new Sorting();
        sortingParam.setCreateSiteCode(preSendSiteId);
        sortingParam.setPackageCode(packageCode);
        List<Sorting> boxPackList = sortingService.findBoxPackList(sortingParam);
        Sorting lastSortingBoxData = this.getLastSortingBoxData(boxPackList, receiveSiteCode);
        if(lastSortingBoxData != null){
            return lastSortingBoxData.getBoxCode();
        }
        return null;
    }

    /**
     * 获取发货数据上一操作场地的数据
     * @param boxPackList 分拣数据
     * @param receiveSiteCode 分拣目的地
     * @return 分拣数据
     */
    private Sorting getLastSortingBoxData(List<Sorting> boxPackList, Integer receiveSiteCode) {
        if(CollectionUtils.isEmpty(boxPackList)){
            return null;
        }
        // 按操作时间降序
        Collections.sort(boxPackList, new Comparator<Sorting>() {
            @Override
            public int compare(Sorting v1, Sorting v2) {
                return v2.getCreateTime().compareTo(v1.getCreateTime());
            }
        });
        for (Sorting sorting : boxPackList) {
            if (Objects.equals(sorting.getReceiveSiteCode(), receiveSiteCode)){
                return sorting;
            }
        }
        return null;
    }

    private void setSampleData(QueryBoxCollectionReportRequest query, QueryBoxCollectionReportResponse reportResponse) {
        reportResponse.setLength(25.34);
        reportResponse.setWidth(29.46);
        reportResponse.setHeight(2.52);
        reportResponse.setWeight(454.32);
        reportResponse.setUpstreamBoxCode("BC35436436");
        reportResponse.setPackageCode(query.getPackageCode());
        reportResponse.setSiteCode(234);
        reportResponse.setBoxEndSiteId(235);
        reportResponse.setBoxStartSiteName("通州分拣中心");
        reportResponse.setBoxEndSiteName("顺义分拣中心");
        reportResponse.setBoxEndSiteId(2345);
        switch (query.getPackageCode()){
            case "JD0003334827912-1-3-": {
                reportResponse.setReportType(CollectionBagExceptionReportTypeEnum.UPSTREAM_FAKE.getCode());
                reportResponse.setReportTypeName(CollectionBagExceptionReportTypeEnum.UPSTREAM_FAKE.getName());
                break;
            }
            case "JD0003334827912-2-3-": {
                reportResponse.setReportType(CollectionBagExceptionReportTypeEnum.UPSTREAM_NOT_DONE.getCode());
                reportResponse.setReportTypeName(CollectionBagExceptionReportTypeEnum.UPSTREAM_NOT_DONE.getName());
                break;
            }
            case "JD0003334827912-3-3-": {
                reportResponse.setReportType(CollectionBagExceptionReportTypeEnum.NO_EXCEPTION.getCode());
                reportResponse.setReportTypeName(CollectionBagExceptionReportTypeEnum.NO_EXCEPTION.getName());
                break;
            }
        }
    }

    /**
     * 检查是否能举报，条件：1.非外单 2. 非生鲜（运单及货物类型）3. 非易碎
     * @param waybill 运单数据
     * @return 检查结果
     */
    private JdCResponse<Boolean> canReportException(Waybill waybill){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null, true);
        String waybillSign = waybill.getWaybillSign();
        boolean isForeignWaybill = BusinessUtil.isSopOrExternal(waybillSign);
        if(!isForeignWaybill){
            result.init(JdCResponse.CODE_FAIL, "不能举报，原因：不符合条件，不是外单", false);
            return result;
        }
        boolean freshWaybill = BusinessUtil.isFreshWaybill(waybillSign);
        if(freshWaybill){
            result.init(JdCResponse.CODE_FAIL, "不能举报，原因：不符合条件，是生鲜运单", false);
            return result;
        }
        boolean fragileWaybill = false;
        int dictProductTypeFragile = DICT_PRODUCT_TYPE_FRAGILE;
        /*List<BaseDataDict> dictList = baseMajorManager.getValidBaseDataDictList(DICT_PRODUCT_TYPE, 2, DICT_PRODUCT_TYPE);
        for (BaseDataDict baseDataDict : dictList) {
            if (Objects.equals(DICT_PRODUCT_TYPE_FRAGILE_NAME, baseDataDict.getTypeName())) {
                dictProductTypeFragile = baseDataDict.getTypeCode();
            }
        }*/
        // 查询是否为易碎品
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillT(true);
        BaseEntity<BigWaybillDto> bigWaybillDtoBaseEntity = waybillQueryManager.getDataByChoice(waybill.getWaybillCode(), wChoice);
        if(EnumBusiCode.BUSI_SUCCESS.getCode() == bigWaybillDtoBaseEntity.getResultCode()){
            BigWaybillDto bigWaybillDto = bigWaybillDtoBaseEntity.getData();
            WaybillTransWay waybillTransWay = bigWaybillDto.getWaybillTransWay();
            log.info("CollectionBagExceptionReportServiceImpl waybillTransWay {}", JSON.toJSONString(waybillTransWay));
            if(waybillTransWay != null && Objects.equals(waybillTransWay.getCargoType(), dictProductTypeFragile)){
                fragileWaybill = true;
            }
        }
        if(fragileWaybill){
            result.init(JdCResponse.CODE_FAIL, "不能举报，原因：不符合条件，是易碎类型运单", false);
        }

        return result;
    }

    /**
     * 判定异常类型
     * @param reportResponse
     * @param waybill
     * @return
     */
    private void judgeBoxException(JdCResponse<QueryBoxCollectionReportResponse> result, QueryBoxCollectionReportResponse reportResponse, Waybill waybill){
        // 无上游，也为无异常
        if(reportResponse.getBoxStartId() == null || reportResponse.getBoxEndSiteId() == null) {
            this.setNoExceptionResponse(reportResponse);
            result.setMessage("不符合条件，未找到上游操作信息");
            return;
        }
        // 长宽高及重量未获取到为无异常
        if(reportResponse.getLength() == null || reportResponse.getWidth() == null || reportResponse.getHeight() == null
                || reportResponse.getWeight() == null){
            this.setNoExceptionResponse(reportResponse);
            return;
        }
        // 上游有建箱，则为虚假集包
        if(StringUtils.isNotEmpty(reportResponse.getUpstreamBoxCode())){
            reportResponse.setReportType(CollectionBagExceptionReportTypeEnum.UPSTREAM_FAKE.getCode());
            reportResponse.setReportTypeName(CollectionBagExceptionReportTypeEnum.UPSTREAM_FAKE.getName());
        } else {
            // 小件
            if (this.checkIsSmallPackage(reportResponse)) {
                reportResponse.setReportType(CollectionBagExceptionReportTypeEnum.UPSTREAM_NOT_DONE.getCode());
                reportResponse.setReportTypeName(CollectionBagExceptionReportTypeEnum.UPSTREAM_NOT_DONE.getName());
            } else {
                this.setNoExceptionResponse(reportResponse);
            }
        }
    }

    /**
     * 设置为无异常数据
     * @param reportResponse 判定结果
     */
    private void setNoExceptionResponse(QueryBoxCollectionReportResponse reportResponse){
        reportResponse.setReportType(CollectionBagExceptionReportTypeEnum.NO_EXCEPTION.getCode());
        reportResponse.setReportTypeName(CollectionBagExceptionReportTypeEnum.NO_EXCEPTION.getName());
    }

    /**
     * 判断是否为小件包裹
     * 包裹重量小于3KG，
     * 且长宽高最长边小于30厘米
     * 且长宽高之和小于70厘米
     * @param reportResponse
     * @return
     */
    private final Double smallHeight = 3d;
    private final Double smallWidth = 30d;
    private final Double smallWidthSum = 70d;
    private boolean checkIsSmallPackage(QueryBoxCollectionReportResponse reportResponse){
        if(reportResponse.getWeight() > smallHeight){
            return false;
        }
        if(reportResponse.getLength() > smallWidth || reportResponse.getWeight() > smallWidth || reportResponse.getHeight() > smallWidth){
            return false;
        }
        if(BigDecimal.valueOf(reportResponse.getLength()).add(BigDecimal.valueOf(reportResponse.getWeight())).add(BigDecimal.valueOf(reportResponse.getHeight())).doubleValue() > smallWidthSum){
            return false;
        }
        return true;
    }

    private JdCResponse<Boolean> checkParamForQueryBagCollectionHasException(QueryBoxCollectionReportRequest query){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(StringUtils.isEmpty(query.getPackageCode())){
            result.toFail("参数错误，packageCode不能为空");
            return result;
        }

        JdCResponse<Boolean> checkParamForUserResult = this.checkParamForUser(query.getUser());
        if(checkParamForUserResult.isFail()){
            return checkParamForUserResult;
        }
        JdCResponse<Boolean> checkParamForCurrentOperateResult = this.checkParamForCurrentOperate(query.getCurrentOperate());
        if(checkParamForCurrentOperateResult.isFail()){
            return checkParamForCurrentOperateResult;
        }

        return result;
    }

    private PackFlowDetail getLatestPackFlowDetail(String waybillCodeOrPackageCode){
        // 查询包裹称重流水，得到长宽高数据
        Page<PackFlowDetail> packagePage = new Page<>();
        packagePage.setPageSize(1000);
        // 查询是否为一单一件，是则按运单号查所有流水
        boolean isOneBillOnePackFlag = WaybillUtil.getPackNumByPackCode(waybillCodeOrPackageCode) == 1;
        if(isOneBillOnePackFlag){
            waybillCodeOrPackageCode = WaybillUtil.getWaybillCodeByPackCode(waybillCodeOrPackageCode);
        }
        Page<PackFlowDetail> packageOpePageList = waybillPackageManager.getOpeDetailByCode(waybillCodeOrPackageCode, packagePage);
        if(packageOpePageList != null) {
            List<PackFlowDetail> packageOpeList = packageOpePageList.getResult();
            if (CollectionUtils.isNotEmpty(packageOpeList)) {
                // 按测量时间降序排列
                Collections.sort(packageOpeList, new Comparator<PackFlowDetail>() {
                    @Override
                    public int compare(PackFlowDetail v1, PackFlowDetail v2) {
                        return v2.getWeighTime().compareTo(v1.getWeighTime());
                    }
                });
                return packageOpeList.get(0);
            }
        }
        return null;
    }

    /**
     * 举报集包异常
     *
     * @param reportRequest 请求参数
     * @return QueryBoxCollectionReportResponse
     * @author fanggang7
     * @time 2020-09-23 21:26:39 周三
     */
    @Override
    @JProfiler(jKey = "DMSWEB.CollectionBagExceptionReport4PdaServiceImpl.reportBagCollectionException",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> reportBagCollectionException(BoxCollectionReportRequest reportRequest) {
        if(log.isInfoEnabled()){
            log.info("CollectionBagExceptionReportServiceImpl.reportBagCollectionException param {}", JSON.toJSONString(reportRequest));
        }
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        try {
            // 验证参数
            JdCResponse<Boolean> checkResult = this.checkParamForReportBagCollectionException(reportRequest);
            if (checkResult.isFail()){
                result.init(JdCResponse.CODE_FAIL, checkResult.getMessage());
                return result;
            }
            int siteCode = reportRequest.getCurrentOperate().getSiteCode();
            // 验证举报条件
            // 过滤是否能举报的必要条件：1. 外单标位 2. 非生鲜（运单及货物类型）、非易碎
            String waybillCode = WaybillUtil.getWaybillCodeByPackCode(reportRequest.getPackageCode());
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
            if(waybill == null){
                result.init(JdCResponse.CODE_FAIL, "查询失败，根据此包裹查不到运单信息");
                return result;
            }
            JdCResponse<Boolean> checkCanReportResult = this.canReportException(waybill);
            boolean canReportFlag = checkCanReportResult.getData();
            if(!canReportFlag){
                result.init(JdCResponse.CODE_FAIL, "不能举报，原因：此包裹不符合条件（非外单、非生鲜、非易碎）");
                return result;
            }
            // 转化数据
            CollectionBagExceptionReport exceptionReport = this.genModelForReport(reportRequest);
            // 查询包裹称重流水，得到长宽高数据
            PackFlowDetail latestPackFlowDetail = this.getLatestPackFlowDetail(reportRequest.getPackageCode());
            if(latestPackFlowDetail != null){
                exceptionReport.setLength(latestPackFlowDetail.getpLength());
                exceptionReport.setWidth(latestPackFlowDetail.getpWidth());
                exceptionReport.setHeight(latestPackFlowDetail.getpHigh());
                exceptionReport.setWeight(latestPackFlowDetail.getpWeight());
            } else {
                if(log.isInfoEnabled()) {
                    log.info("waybillPackageManager.getOpeDetailByCode 无称重明细 packageCode {}", reportRequest.getPackageCode());
                }
                result.init(JdCResponse.CODE_FAIL, "不能举报，原因：未查询到此包裹的称重明细数据");
                return result;
            }

            CallerInfo infotmp3 = Profiler.registerInfo("DMS.BASE.CollectionBagExceptionReport4PdaServiceImpl.getPreSendSiteIdtmp3", Constants.UMP_APP_NAME_DMSWEB, false, true);
            // 查询包裹起始、目的地信息及上游箱号
            Integer preSendSiteId = this.getPreSendSiteId(reportRequest.getPackageCode(), siteCode);
            if(preSendSiteId != null){
                CallerInfo infotmp4 = Profiler.registerInfo("DMS.BASE.CollectionBagExceptionReport4PdaServiceImpl.getPreSendSiteIdtmp4", Constants.UMP_APP_NAME_DMSWEB, false, true);
                // 查询分拣数据得到上游箱号
                String lastUpstreamBoxCode = this.getLastUpstreamBoxCode(reportRequest.getPackageCode(), preSendSiteId, siteCode);
                boolean isBoxCode = BusinessUtil.isBoxcode(lastUpstreamBoxCode);
                if(isBoxCode){
                    exceptionReport.setUpstreamBoxCode(lastUpstreamBoxCode);
                }
                Profiler.registerInfoEnd(infotmp4);
            }
            Profiler.registerInfoEnd(infotmp3);
            if(preSendSiteId == null){
                SiteWaybillTraceInfoDto preSendSiteTrackData = this.getPreSendSiteTrackData(reportRequest.getPackageCode(), siteCode);
                PackageState sendData = preSendSiteTrackData.getSendData();
                PackageState boxingData = preSendSiteTrackData.getBoxingData();
                if(sendData != null){
                    preSendSiteId = sendData.getOperatorSiteId();
                    if(boxingData != null){
                        exceptionReport.setUpstreamBoxCode("-");
                    }
                }
            }
            if (preSendSiteId == null) {
                result.init(JdCResponse.CODE_FAIL, "不能举报，原因：未查询到此包裹的上游发货场地数据");
                return result;
            }

            exceptionReport.setBoxStartId((long)preSendSiteId);
            exceptionReport.setBoxEndId((long)siteCode);

            // 落库
            collectionBagExceptionReportDao.insertSelective(exceptionReport);
            result.toSucceed("举报成功");
        } catch (Exception e) {
            log.error("CollectionBagExceptionReportServiceImpl.reportBagCollectionException exception {}", e.getMessage(), e);
            result.toFail("举报异常");
        }
        return result;
    }

    /**
     * 根据举报参数构建举报记录
     * @param reportRequest 请求参数
     * @return 构建的举报结果
     * @author fanggang7
     * @time 2020-09-25 00:06:28 周五
     */
    private CollectionBagExceptionReport genModelForReport(BoxCollectionReportRequest reportRequest){
        CollectionBagExceptionReport model = new CollectionBagExceptionReport();
        model.setPackageCode(reportRequest.getPackageCode());
        model.setReportType(reportRequest.getReportType());
        model.setReportImg(JSON.toJSONString(reportRequest.getReportImgUrls()));
        Date currentDate = new Date();
        User user = reportRequest.getUser();
        model.setCreateUserErp(user.getUserErp());
        model.setCreateUserName(user.getUserName());
        model.setOperatorErp(user.getUserErp());
        model.setOperatorName(user.getUserName());
        model.setOperateTime(currentDate);
        model.setCreateTime(currentDate);
        model.setUpdateTime(currentDate);

        CurrentOperate currentOperate = reportRequest.getCurrentOperate();
        model.setSiteCode(currentOperate.getSiteCode());
        model.setSiteName(currentOperate.getSiteName());

        BaseStaffSiteOrgDto operateSiteData = baseMajorManager.getBaseSiteBySiteId(currentOperate.getSiteCode());
        if(operateSiteData != null){
            model.setOrgCode(operateSiteData.getOrgId());
            model.setOrgName(operateSiteData.getOrgName());
            model.setSiteName(operateSiteData.getSiteName());
            model.setProvinceAgencyCode(operateSiteData.getProvinceAgencyCode());
            model.setProvinceAgencyName(operateSiteData.getProvinceAgencyName());
            model.setAreaHubCode(operateSiteData.getAreaCode());
            model.setAreaHubName(operateSiteData.getAreaName());
        }

        // 根据包裹号查询上游箱号，补全数据

        return model;
    }

    /**
     * 举报接口参数校验
     * @param reportRequest 请求参数
     * @return 校验结果
     * @author fanggang7
     * @time 2020-09-25 00:05:44 周五
     */
    private JdCResponse<Boolean> checkParamForReportBagCollectionException(BoxCollectionReportRequest reportRequest){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(StringUtils.isEmpty(reportRequest.getPackageCode())){
            result.toFail("参数错误，packageCode不能为空");
            return result;
        }

        if(reportRequest.getReportType() == null){
            result.toFail("参数错误，reportType不能为空");
            return result;
        }
        List<String> reportImgUrls = reportRequest.getReportImgUrls();
        if(CollectionUtils.isEmpty(reportImgUrls)){
            result.toFail("参数错误，reportImgUrls不能为空");
            return result;
        }
        if(reportImgUrls.size() > 3){
            result.toFail("最多上传3张照片");
            return result;
        }
        JdCResponse<Boolean> checkParamForUserResult = this.checkParamForUser(reportRequest.getUser());
        if(checkParamForUserResult.isFail()){
            return checkParamForUserResult;
        }
        JdCResponse<Boolean> checkParamForCurrentOperateResult = this.checkParamForCurrentOperate(reportRequest.getCurrentOperate());
        if(checkParamForCurrentOperateResult.isFail()){
            return checkParamForCurrentOperateResult;
        }

        return result;
    }

    private JdCResponse<Boolean> checkParamForUser(User user){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(user == null){
            result.toFail("参数错误，user不能为空");
            return result;
        }
        if(StringUtils.isEmpty(user.getUserErp())){
            result.toFail("参数错误，userErp不能为空");
            return result;
        }
        if(StringUtils.isEmpty(user.getUserName())){
            result.toFail("参数错误，userName不能为空");
            return result;
        }
        return result;
    }

    private JdCResponse<Boolean> checkParamForCurrentOperate(CurrentOperate currentOperate){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, null);
        if(currentOperate == null){
            result.toFail("参数错误，currentOperate不能为空");
            return result;
        }
        if(currentOperate.getSiteCode() < 0){
            result.toFail("参数错误，siteCode不能为空");
            return result;
        }
        if(StringUtils.isEmpty(currentOperate.getSiteName())){
            result.toFail("参数错误，siteName不能为空");
            return result;
        }
        return result;
    }
}
