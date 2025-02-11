package com.jd.bluedragon.distribution.qualityControl.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.enums.QualityControlInletEnum;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.dto.SiteCodeAssociationDto;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.distribution.message.OwnReverseTransferDomain;
import com.jd.bluedragon.distribution.qualityControl.AbnormalBizSourceEnum;
import com.jd.bluedragon.distribution.qualityControl.QcVersionFlagEnum;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.qualityControl.domain.abnormalReportRecordMQ;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportJmqDto;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.bluedragon.distribution.reverse.domain.CancelReturnGroupWhiteListConf;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.etms.waybill.dto.RelationWaybillBodyDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import com.jd.ldop.business.api.AbnormalOrderApi;
import com.jd.ldop.business.api.dto.request.AbnormalOrderDTO;
import com.jd.ldop.business.api.dto.response.Response;
import com.jd.ldop.business.api.dto.response.ResponseStatus;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.jd.bluedragon.common.dto.jyexpection.request.AbnormalCallbackRequest;
import com.jd.bluedragon.common.dto.jyexpection.response.AbnormalCallbackResponse;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.EXCHANGE_WAYBILL_PRINT_LIMIT_1_SITE_WHITE_LIST;
import static com.jd.bluedragon.Constants.EXCHANGE_WAYBILL_PRINT_LIMIT_1_SWITCH;
import static com.jd.bluedragon.Constants.CANCEL_RETURN_GROUP_WHITE_LIST_CONF;
import static com.jd.bluedragon.Constants.STR_ALL;
import static com.jd.bluedragon.core.hint.constants.HintCodeConstants.SCRAP_WAYBILL_INTERCEPT_HINT_CODE;
import static com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum.CANCEL;
import static com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum.COMPENSATE;
import static com.jd.bluedragon.dms.utils.BusinessUtil.isScrapWaybill;
import static org.apache.commons.lang3.math.NumberUtils.*;

/**
 * Created by dudong on 2014/12/1.
 */
@Service
public class QualityControlService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int PACKAGE_CODE_TYPE = 1;
    private static final int WAYBILL_CODE_TYPE = 2;
    private static final int BOX_CODE_TYPE = 3;
    private static final int SEND_CODE_TYPE = 4;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    @Qualifier("bdExceptionToQcMQ")
    private DefaultJMQProducer bdExceptionToQcMQ;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ReversePrintService reversePrintService;

    @Autowired
    AbnormalWayBillService abnormalWayBillService;

    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Autowired
    private AbnormalOrderApi abnormalOrderApi;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private UserSignRecordService userSignRecordService;

    @Autowired
    @Qualifier("abnormalReportRecordProducer")
    private DefaultJMQProducer abnormalReportRecordProducer;

    @Autowired
    private PositionManager positionManager;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private WaybillService waybillService;

    @Resource(name = "checkPrintInterceptReasonIdSetForOld")
    private Set<Integer> checkPrintInterceptReasonIdSetForOld;

    @Autowired
    private WaybillCancelService waybillCancelService;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private JyOperateFlowService jyOperateFlowService;


    /**
     * 协商再投状态校验
     *
     * @param request
     * @return
     */
    public InvokeResult<RedeliveryMode> redeliveryCheck(RedeliveryCheckRequest request) {
        InvokeResult<RedeliveryMode> result=new InvokeResult<RedeliveryMode>();

        RedeliveryMode data=new RedeliveryMode();
        data.setIsCompleted(true);

        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setData(data);

        if(StringUtils.isEmpty(request.getCode()) || null==request.getCodeType() || request.getCodeType()<1){
            log.warn("PDA调用协商再投状态验证接口失败-参数错误。入参:{}",JsonHelper.toJson(request));
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("请扫描者包裹号、运单号或箱号！");
            return result;
        }

        try{
            List<String> waybillCodeList=new ArrayList<String>();

            //如果是包裹或运单
            if (request.getCodeType()==1 || request.getCodeType()==2){
                String waybillCode= WaybillUtil.getWaybillCode(request.getCode());
                waybillCodeList.add(waybillCode);
            }

            //如果是箱号
            if (request.getCodeType()==3){
                waybillCodeList = sortingService.getWaybillCodeListByBoxCode(request.getCode());
            }

            if(waybillCodeList != null && waybillCodeList.size() > 0){
                for (String waybillCode :waybillCodeList){
                    Waybill waybillData = waybillQueryManager.getWaybillByWayCode(waybillCode);
                    //补打拦截
                    if (waybillData != null
                            && checkPrintInterceptReasonIdSetForOld != null
                            && request.getSupExceptionId() != null
                            && checkPrintInterceptReasonIdSetForOld.contains(request.getSupExceptionId())
                            && waybillService.hasPrintIntercept(waybillCode, waybillData.getWaybillSign())) {
                        //取消拦截  存在时跳过 不进行补打拦截提示
                        JdCancelWaybillResponse jdCancelResponse = waybillService.dealCancelWaybill(waybillCode);
                        if (jdCancelResponse == null || jdCancelResponse.getCode() == null || jdCancelResponse.getCode().equals(JdResponse.CODE_OK)) {
                            data.setIsCompleted(false);
                            data.setWaybillCode(waybillCode);
                            result.setData(data);
                            result.setMessage("此单号["+ waybillCode +"]"+ HintService.getHint(HintCodeConstants.EX_REPORT_CHECK_CHANGE_ADDRESS));
                            break;
                        }
                    }
                    //协商再投拦截
                    if (waybillData != null
                            && waybillData.getBusiId() != null
                            && getRedeliveryState(waybillCode, waybillData.getBusiId()) == 0) {
                        data.setIsCompleted(false);
                        data.setWaybillCode(waybillCode);
                        result.setData(data);
                        result.setMessage("此单号["+ waybillCode +"]为【发起协商再投未处理】状态，需商家审核完成才能提交异常！");
                        break;
                    }
                    else {
                        log.warn("PDA调用协商再投状态验证接口失败-无商家信息。运单号:{},入参:{}",waybillCode,JsonHelper.toJson(request));
                    }
                }
            }
            else {
                log.warn("PDA调用协商再投状态验证接口失败-无运单信息。入参:{}",JsonHelper.toJson(request));
                result.setCode(InvokeResult.RESULT_NULL_WAYBILLCODE_CODE);
                result.setMessage(InvokeResult.RESULT_NULL_WAYBILLCODE_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("PDA调用协商再投状态验证接口失败。异常信息:{}",ex.getMessage(),ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }

        return result;
    }

    public InvokeResult<Boolean> exceptionSubmit(QualityControlRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        if(StringUtils.isEmpty(request.getQcValue()) || !WaybillCodeRuleValidateUtil.isEffectiveOperateCode(request.getQcValue())){
            log.warn("PDA调用异常配送接口插入质控任务表失败-参数错误[{}]",JsonHelper.toJson(request));
            result.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
            result.setMessage("请扫描运单号或者包裹号！");
            return result;
        }
        try{
            final Result<Void> checkCanSubmitResult = this.checkCanSubmit(request);
            if (!checkCanSubmitResult.isSuccess()) {
                result.customMessage(QualityControlResponse.CODE_WRONG_STATUS, checkCanSubmitResult.getMessage());
                return result;
            }
            convertThenAddTask(request);
        }catch(Exception ex){
            log.error("PDA调用异常配送接口插入质控任务表失败，原因 " , ex);
            result.setCode(QualityControlResponse.CODE_SERVICE_ERROR);
            result.setMessage(QualityControlResponse.MESSAGE_SERVICE_ERROR);
            return result;
        }
        result.setCode(QualityControlResponse.CODE_OK);
        result.setMessage(QualityControlResponse.MESSAGE_OK);
        return result;
    }


    @JProfiler(jKey = "DMSWEB.QualityControlService.abnormalH5Callback",jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<AbnormalCallbackResponse> abnormalH5Callback(AbnormalCallbackRequest request) {
        JdVerifyResponse<AbnormalCallbackResponse> jdVerifyResponse = new JdVerifyResponse<>();
        jdVerifyResponse.toSuccess();
        // 质控H5页面提报业务主键，与与simple_wp_abnormal_record主题消息体中的id字段一一对应，每次请求都是唯一的
        String businessId = request.getBusinessId();
        if (log.isInfoEnabled()) {
            log.info("abnormalH5Callback|质控H5页面回调接口请求参数:businessId={},barCodeList={}", businessId, request.getBarCodeList());
        }
        // 校验参数
        if (StringUtils.isBlank(businessId)) {
            log.warn("abnormalH5Callback|质控H5页面回调接口请求参数中businessId为空:request={}", JsonHelper.toJsonMs(request));
            jdVerifyResponse.toFail("businessId不能为空");
            return jdVerifyResponse;
        }
        try {
            // 设置缓存,格式为：key是业务主键，value是网格数据
            cacheService.setEx(CacheKeyConstants.CACHE_KEY_ABNORMAL_H5_CALLBACK + businessId,
                    JsonHelper.toJsonMs(request.getOperatorData()), Constants.NUMBER_ONE, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("abnormalH5Callback|质控H5页面回调接口设置缓存出现异常:request={}", JsonHelper.toJsonMs(request), e);
            jdVerifyResponse.toError();
        }
        return jdVerifyResponse;
    }

    private Result<Void> checkCanSubmit(QualityControlRequest request){
        Result<Void> result = Result.success();
        try {
            log.info("checkCanSubmit match {} {}", request.getQcValue(), request.getDistCenterID());
            String waybillCode=WaybillUtil.getWaybillCode(request.getQcValue());
            // 获取原单数据
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryWaybillExtend(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,wChoice);
            if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() !=null
                    && BusinessUtil.isColdChainExpressScrap(baseEntity.getData().getWaybill().getWaybillSign())){
                return result.toFail(HintService.getHint(HintCodeConstants.COLD_CHAIN_EXPRESS_SCRAP_NO_SUBMIT_EXCEPTION_MSG, HintCodeConstants.COLD_CHAIN_EXPRESS_SCRAP_NO_SUBMIT_EXCEPTION));
            }
            String oldWaybillCode = getOldWaybillCode(baseEntity);
            // 只针对分拣系统， 理赔拦截和取消订单拦截只能换单一次
            if (Objects.equals(QualityControlInletEnum.DMS_SORTING.getCode(), request.getInletFlag())
                    && StringUtils.isEmpty(oldWaybillCode)
                    && checkExchangeNum(request, oldWaybillCode)) {
                result.toFail(InvokeResult.WAYBILL_EXCHANGE_NUM_MESSAGE);
                return result;
            }
            // ucc开关
            if(!dmsConfigManager.getPropertyConfig().matchExceptionSubmitCheckSite(request.getDistCenterID())){
                return result;
            }
            // 报废运单拦截
            if (StringUtils.isNotEmpty(waybillCode)) {
                Waybill waybill = waybillService.getWaybillByWayCode(waybillCode);
                if (waybill != null && StringUtils.isNotEmpty(waybill.getWaybillSign())) {
                    // waybillSign的19位等于2是报废运单 拦截
                    String waybillSign = waybill.getWaybillSign();
                    if (isScrapWaybill(waybillSign)) {
                        return result.toFail(HintService.getHint(SCRAP_WAYBILL_INTERCEPT_HINT_CODE));
                    }
                }
            }
            final List<CancelWaybill> waybillCancelList = waybillCancelService.getByWaybillCode(waybillCode);
            if(!StringUtils.isEmpty(oldWaybillCode) || CollectionUtils.isNotEmpty(waybillCancelList)){
                return result;
            }
            String tipMsg = HintService.getHint(HintCodeConstants.EXCEPTION_NO_SUBMIT_CHECK_INTERCEPT_MSG, HintCodeConstants.EXCEPTION_NO_SUBMIT_CHECK_INTERCEPT);
            return result.toFail(tipMsg);
            // 运单拦截中心下发的存在全部拦截。如果需要判断存在部分拦截则放开下面注释代码
           /*
            final long matchCount = waybillCancelList.parallelStream().filter(item -> uccPropertyConfiguration.matchExceptionSubmitCheckWaybillInterceptType(item.getInterceptType())).count();
            if(matchCount <= 0){
                return result.toFail(tipMsg);
            }*/
            // 增加取消拦截校验
        } catch (Exception e) {
            log.error("checkCanSubmit exception {}", JsonHelper.toJson(request), e);
        }
        return result;
    }

    private boolean checkExchangeNum(QualityControlRequest request, String oldWaybillCode) {
        if (!sysConfigService.getConfigByName(EXCHANGE_WAYBILL_PRINT_LIMIT_1_SWITCH)) {
            return false;
        }
        // 场地白名单
        Integer siteCode = request.getDistCenterID();
        SysConfig siteConfig = sysConfigService.findConfigContentByConfigName(EXCHANGE_WAYBILL_PRINT_LIMIT_1_SITE_WHITE_LIST);
        if (null != siteCode && null != siteConfig && StringUtils.isNotEmpty(siteConfig.getConfigContent())) {
            List<String> siteWhiteList = Arrays.asList(siteConfig.getConfigContent().split(","));
            if (siteWhiteList.contains(siteCode.toString())) {
                return false;
            }
        }

        // 获取运单拦截信息
        List<CancelWaybill> cancelWaybillList = waybillCancelService.getByWaybillCode(oldWaybillCode);
        if (org.springframework.util.CollectionUtils.isEmpty(cancelWaybillList)) {
            return false;
        }

        for (CancelWaybill cancelWaybill : cancelWaybillList) {
            // 拦截信息为取消订单拦拦截或理赔拦截，则获取换单打印的次数
            if (Objects.equals(CANCEL.getCode(), cancelWaybill.getInterceptType())
                    || Objects.equals(COMPENSATE.getCode(), cancelWaybill.getInterceptType())) {
                // 调用运单接口，获取所有换单打印记录，如果大于1，则不能换单
                JdResult<List<RelationWaybillBodyDto>> result = waybillQueryManager.getRelationWaybillList(oldWaybillCode);
                if (result.isSucceed() && !org.springframework.util.CollectionUtils.isEmpty(result.getData()) && result.getData().size() > 1) {
                    return true;
                }
            }
        }

        return false;
    }

    public TaskResult dealQualityControlTask(Task task) {
        QualityControlRequest request = null;
        List<SendDetail> sendDetails = null;
        SendDetail sendDetail = null;
        String boxCode = null;
        try {
            request = JsonHelper.fromJson(task.getBody(), QualityControlRequest.class);
            switch (Integer.valueOf(request.getQcType()).intValue()) {
                case PACKAGE_CODE_TYPE:
                    toSortingReturn(request);
                    String waybillCode = WaybillUtil.getWaybillCode(request.getQcValue());
                    if(StringHelper.isEmpty(waybillCode)){
                        throw new NullPointerException("通过包裹号 [" + request.getQcValue() + "] 提取运单号为空");
                    }
                    sendDetails = new ArrayList<SendDetail>();
                    sendDetail = new SendDetail();
                    sendDetail.setWaybillCode(waybillCode);
                    sendDetails.add(sendDetail);

                    this.handleSecurityCheckWaybillTrace(request);
                    break;
                case WAYBILL_CODE_TYPE:
                    toSortingReturn(request);
                    sendDetails = new ArrayList<SendDetail>();
                    sendDetail = new SendDetail();
                    sendDetail.setWaybillCode(request.getQcValue());
                    sendDetails.add(sendDetail);

                    this.handleSecurityCheckWaybillTrace(request);
                    break;
                case BOX_CODE_TYPE:
                    boxCode = request.getQcValue();
                    sendDetails = sendDatailDao.queryWaybillsByBoxCode(boxCode);
                    break;
                case SEND_CODE_TYPE:
                    sendDetails = sendDatailDao.queryWaybillsBySendCode(request.getQcValue());
                    break;
            }
        } catch (Exception ex) {
            log.error("调用异常配送接口获取包裹信息失败,发生异常。" , ex);
            return TaskResult.FAILED;
        }

        if (null == sendDetails || sendDetails.size() <= 0) {
            log.warn("调用异常配送接口获取包裹信息失败,数据为空:{}" , JsonHelper.toJson(request));
            return TaskResult.FAILED;
        }

        try {
            //如果是对接新质控系统，走新逻辑
            if (request.getQcVersionFlag() != null && request.getQcVersionFlag() == QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType()) {
                toNewQualityControlWaybillTrace(sendDetails, request);
            } else {
                toQualityControlAndWaybillTrace(sendDetails, request, boxCode);  // 推质控和全程跟踪
            }

            // 保存异常处理记录
            convert2AbnormalWayBills(sendDetails, request);

        } catch (Exception ex) {
            log.error("分拣中心异常节点推全程跟踪、质控发生异常。" , ex);
            return TaskResult.REPEAT;
        }


        return TaskResult.SUCCESS;
    }


    /** 发质控和全程跟踪 */
    public void toQualityControlAndWaybillTrace(List<SendDetail> sendDetails, QualityControlRequest request, String boxCode){
        //获取 同步运单状态接口需要的额外参数
        BaseStaffSiteOrgDto operateSite =  baseMajorManager.getBaseSiteBySiteId(request.getDistCenterID());

        //过滤数据，按运单维度处理
        //已经处理过的运单
        Set<String> set = new HashSet<>();

        for(SendDetail sendDetail : sendDetails){
            //过滤数据，按运单维度处理
            if(set.contains(sendDetail.getWaybillCode())){
                continue;
            }
            set.add(sendDetail.getWaybillCode());

            QualityControl qualityControl = convert2QualityControl(sendDetail.getWaybillCode(), request, boxCode);
            log.info("分拣中心异常页面发质控和全程跟踪开始，消息体：{}" , JsonHelper.toJson(qualityControl));

            // 更新运单状态
            updateWaybillStatus(sendDetail, request, operateSite);
            bdExceptionToQcMQ.sendOnFailPersistent(request.getQcValue(), JsonHelper.toJson(qualityControl));

            //异常处理 节点发MQ 换新单   2016年8月16日18:18:40   by guoyongzhi  逆向整合之：3.2.6	拦截订单，触发新单
            log.info("执行自营换新单  convert2ExchangeNewWaybill exchangeOwnWaybill ");
            OwnReverseTransferDomain domain=convert2ExchangeNewWaybill(sendDetail.getWaybillCode(), request);
            reversePrintService.exchangeOwnWaybill(domain);

        }
    }

    /** 发质控和全程跟踪
     *
     * 对接新质控系统，只变更运单状态和触发换新单
     *
     * */
    public void toNewQualityControlWaybillTrace(List<SendDetail> sendDetails, QualityControlRequest request){
        //获取 同步运单状态接口需要的额外参数
        BaseStaffSiteOrgDto operateSite =  baseMajorManager.getBaseSiteBySiteId(request.getDistCenterID());

        Set<String> set = new HashSet<>();
        for (SendDetail sendDetail : sendDetails) {
            String waybillCode = sendDetail.getWaybillCode();
            //过滤数据，按运单维度处理
            if (set.contains(waybillCode)) {
                continue;
            }
            set.add(sendDetail.getWaybillCode());

            // QualityControl qualityControl = convert2QualityControl(waybillCode, request, null);
            // log.info("分拣中心新异常提交结果同步运单状态开始，消息体：{}", JsonHelper.toJson(qualityControl));
            // 更新运单状态
            updateWaybillStatus(sendDetail, request, operateSite);
            //异常处理 节点发MQ 换新单
            log.info("执行自营换新单 convert2ExchangeNewWaybill exchangeOwnWaybill ");
            OwnReverseTransferDomain domain = convert2ExchangeNewWaybill(waybillCode, request);
            reversePrintService.exchangeOwnWaybill(domain);
        }
    }

    /**
     * 异常处理 节点发MQ 换新单
     * @param waybillCode
     * @param request
     * @return
     */
    public OwnReverseTransferDomain convert2ExchangeNewWaybill(String waybillCode, QualityControlRequest request){
        OwnReverseTransferDomain ownReverseTransferDomain=new OwnReverseTransferDomain();
        ownReverseTransferDomain.setWaybillCode(waybillCode);
        ownReverseTransferDomain.setSiteId(request.getDistCenterID());
        ownReverseTransferDomain.setUserId(request.getUserID());
        ownReverseTransferDomain.setUserRealName(request.getUserName());
        ownReverseTransferDomain.setSiteName(request.getDistCenterName());
        CancelReturnGroupWhiteListConf conf = null;
        SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(CANCEL_RETURN_GROUP_WHITE_LIST_CONF);
        if (sysConfig != null && !StringUtils.isEmpty(sysConfig.getConfigContent())) {
             conf = JsonHelper.fromJson(sysConfig.getConfigContent(), CancelReturnGroupWhiteListConf.class);
        }
        // 破损标识
        if (needDamagedPackageFlag(waybillCode, request, conf)) {
            ownReverseTransferDomain.setDamagedPackageFlag(INTEGER_ONE);
            twiceExchangeWaybill(waybillCode, request, conf, ownReverseTransferDomain);
        }else {
            ownReverseTransferDomain.setDamagedPackageFlag(INTEGER_ZERO);
        }
        return ownReverseTransferDomain;
    }

    /**
     * 是否为破损订单 1. 只针对一单一件的场景 2. 针对特定异常编码
     *
     * @param waybillCode
     * @param request
     * @param conf
     * @return
     */
    public boolean needDamagedPackageFlag(String waybillCode, QualityControlRequest request, CancelReturnGroupWhiteListConf conf) {
        // 只针对一单一件的场景
        com.jd.etms.waybill.domain.Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null || !Objects.equals(waybill.getGoodNumber(), INTEGER_ONE)) {
            return false;
        }
        if (conf == null || org.apache.commons.collections.CollectionUtils.isEmpty(conf.getAbnormalCauseList())) {
            return false;
        }
        if (conf.getAbnormalCauseList().contains(request.getQcCode())) {
            return true;
        }
        return false;
    }

    private void twiceExchangeWaybill(String waybillCode, QualityControlRequest request, CancelReturnGroupWhiteListConf conf, OwnReverseTransferDomain ownReverseTransferDomain) {
        // 场地白名单+破损+二次换单 的情况 waybillCode传原单 newWaybillCode传逆向单号
        if (conf == null || CollectionUtils.isEmpty(conf.getSiteWhiteList())) {
            return;
        }
        if (!conf.getSiteWhiteList().contains(String.valueOf(request.getDistCenterID())) && !conf.getSiteWhiteList().contains(STR_ALL)) {
            return;
        }
        // 查询关联单号
        JdResult<List<RelationWaybillBodyDto>> result = waybillQueryManager.getRelationWaybillList(waybillCode);
        if (result.isSucceed() && !CollectionUtils.isEmpty(result.getData()) && INTEGER_TWO.equals(result.getData().size())) {
            // 该接口查询的关联单号，会返回当前查询的运单，所以当总数为2条时就为二次换单
            for (RelationWaybillBodyDto waybillBodyDto : result.getData()) {
                if (StringUtils.isNotEmpty(waybillBodyDto.getWaybillCode()) && !waybillCode.equals(waybillBodyDto.getWaybillCode())) {
                    ownReverseTransferDomain.setNewWaybillCode(waybillCode);
                    ownReverseTransferDomain.setWaybillCode(waybillBodyDto.getWaybillCode());
                    break;
                }
            }
        }
    }

    /**
     * 更新运单异常状态
     * 此节点节点运单只接收运单维度
     * @param boxCode 箱号
     * @param request
     * @param operateSite
     */
    private void updateWaybillStatus(SendDetail sendDetail, QualityControlRequest request, BaseStaffSiteOrgDto operateSite) {

        String waybillCode = sendDetail.getWaybillCode();
        String boxCode = sendDetail.getBoxCode();

        Task tTask = new Task();
        tTask.setBoxCode(boxCode);

        tTask.setCreateSiteCode(request.getDistCenterID());
        tTask.setKeyword2(waybillCode);
        tTask.setReceiveSiteCode(request.getDistCenterID());
        tTask.setType(WaybillStatus.WAYBILL_TRACK_QC);
        tTask.setTableName(Task.TABLE_NAME_WAYBILL);
        tTask.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
        tTask.setOwnSign(BusinessHelper.getOwnSign());
        tTask.setKeyword1(waybillCode);//回传运单状态
        tTask.setFingerprint(Md5Helper.encode(request.getDistCenterID() + "_" + WaybillStatus.WAYBILL_TRACK_QC + "_"
                + waybillCode + "-" + request.getOperateTime() ));


        WaybillStatus tWaybillStatus = new WaybillStatus();
        tWaybillStatus.setOperatorId(request.getUserID());
        tWaybillStatus.setOperator(request.getUserName());
        tWaybillStatus.setOperateTime(request.getOperateTime());
        tWaybillStatus.setCreateSiteCode(request.getDistCenterID());
        tWaybillStatus.setCreateSiteName(request.getDistCenterName());
        tWaybillStatus.setCreateSiteType(operateSite.getSiteType());
        tWaybillStatus.setOrgId(operateSite.getOrgId());
        tWaybillStatus.setOrgName(operateSite.getOrgName());
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_QC);
        tWaybillStatus.setWaybillCode(waybillCode);

        //组装异常原因
        // FIXME: 2020/2/11 待优化
        String qcName = request.getQcName();
        if(StringUtils.isNotBlank(qcName)){
            if (qcName.indexOf('-') != -1 && qcName.split("-").length == 2) {
                tWaybillStatus.setReasonId(Integer.valueOf(qcName.split("-")[0]));
                tWaybillStatus.setRemark(qcName.split("-")[1]);
            } else {
                tWaybillStatus.setReasonId(request.getQcCode());
                tWaybillStatus.setRemark(qcName);
            }


        }

        tWaybillStatus.setPackageCode(waybillCode); //异常 节点运单只接收运单维度

        if (AbnormalBizSourceEnum.ABNORMAL_HANDLE.getType().equals(request.getBizSource())
                || AbnormalBizSourceEnum.ABNORMAL_REPORT_H5.getType().equals(request.getBizSource())) {
            // 透传操作流水主键
            sendDetail.setOperateFlowId(jyOperateFlowService.createOperateFlowId());
            // 透传网格数据
            sendDetail.setOperatorData(request.getOperatorData());
            if (log.isInfoEnabled()) {
                log.info("convert2AbnormalWayBills|配送异常生成主键:sendDetail={}", JsonHelper.toJson(sendDetail));
            }
            tWaybillStatus.setOperateFlowId(sendDetail.getOperateFlowId());
            tWaybillStatus.setOperatorData(sendDetail.getOperatorData());
            // 发送操作轨迹
            jyOperateFlowService.sendOperateTrack(tWaybillStatus);
        }

        tTask.setBody(JsonHelper.toJson(tWaybillStatus));

        taskService.add(tTask);
    }


    public QualityControl convert2QualityControl(String waybillCode, QualityControlRequest request, String boxCode){
        BaseDataDict baseDataDict = baseMajorManager.getBaseDataDictById(request.getQcCode());
        QualityControl qualityControl = new QualityControl();
        qualityControl.setBlameDept(request.getDistCenterID());
        qualityControl.setBlameDeptName(request.getDistCenterName());
        qualityControl.setCreateTime(request.getOperateTime());
        qualityControl.setCreateUserId(request.getUserID());
        qualityControl.setCreateUserErp(request.getUserERP());
        qualityControl.setCreateUserName(request.getUserName());
        qualityControl.setMessageType(baseDataDict.getTypeGroup());
        if(null != boxCode){
            qualityControl.setBoxCode(boxCode);
        }else{
            qualityControl.setBoxCode("null");
        }
        qualityControl.setWaybillCode(waybillCode);
        qualityControl.setTypeCode(baseDataDict.getTypeCode() + "");
        qualityControl.setExtraCode("null");
        qualityControl.setSystemName(QualityControl.SYSTEM_NAME);
        qualityControl.setReturnState("null");
        return qualityControl;
    }


    /** 扫描包裹号或者运单号后生成分拣退货Task任务 */
    public void toSortingReturn(QualityControlRequest request){
        if(request.getIsSortingReturn()){
            try{
                ReturnsRequest sortingReturn = new ReturnsRequest();
                sortingReturn.setSiteCode(request.getDistCenterID());
                sortingReturn.setSiteName(request.getDistCenterName());
                sortingReturn.setUserCode(request.getUserID());
                sortingReturn.setUserName(request.getUserName());
                sortingReturn.setPackageCode(request.getQcValue());
                sortingReturn.setBusinessType(10);  // 只有正向的，三方的使用老的分拣退货页面，还有-1的是分拣验证时插入的数据
                sortingReturn.setOperateTime(DateHelper.formatDateTime(request.getOperateTime()));
                sortingReturn.setShieldsError(request.getQcName());
                Task task = new Task();
                task.setKeyword1(request.getDistCenterID() + "");
                task.setKeyword2(request.getQcValue());
                task.setCreateSiteCode(request.getDistCenterID());
                task.setReceiveSiteCode(request.getDistCenterID());
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setType(Task.TASK_TYPE_RETURNS);
                task.setTableName(Task.getTableName(task.getType()));
                task.setSequenceName(Task.getSequenceName(task.getTableName()));
                StringBuilder fringerprint = new StringBuilder();
                fringerprint.append(task.getKeyword1()).append("_").append(task.getKeyword2()).append("_").append(task.getCreateSiteCode());
                task.setFingerprint(Md5Helper.encode(fringerprint.toString()));
                task.setBody(Constants.PUNCTUATION_OPEN_BRACKET + JsonHelper.toJson(sortingReturn) + Constants.PUNCTUATION_CLOSE_BRACKET);
                taskService.add(task);
            }catch(Exception e){
                log.error("质控异常生成分拣退货数据异常:{}",JsonHelper.toJson(request));
                log.error("质控异常生成分拣退货数据异常，原因 " , e);
            }
        }
    }

    /**
     * 获取运单协商再投状态
     * @param
     * @return 0：未处理 1：已处理
     */
    @JProfiler(jKey = "DMSWEB.QualityControlService.getRedeliveryState",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public int getRedeliveryState(String waybillCode,Integer businessID) {
        int res = 1;
        Response<AbnormalOrderDTO> dto = abnormalOrderApi.queryByCustomerIdDeliveryIdMainTypeId(waybillCode,businessID,20);
        log.info("获取运单协商再投状态: 入参waybillCode={} businessID={} JSF接口返回:{}"
                ,waybillCode,businessID,JsonHelper.toJson(dto));
        if(null != dto && ResponseStatus.SUCCESS.equals(dto.getStatus()) &&
                null != dto.getResult() && null != dto.getResult().getAbnormalState()){
            res = dto.getResult().getAbnormalState();
        }

        return res;
    }

    /**
     * 构造运单异常操作集合
     * @param sendDetails
     * @param request
     * @return
     */
    private void convert2AbnormalWayBills(List<SendDetail> sendDetails, QualityControlRequest request) {
        OperateBizSubTypeEnum operateBizSubTypeEnum = null;
        if (AbnormalBizSourceEnum.ABNORMAL_HANDLE.getType().equals(request.getBizSource())) {
            operateBizSubTypeEnum = OperateBizSubTypeEnum.ABNORMAL_HANDLE;
        } else if (AbnormalBizSourceEnum.ABNORMAL_REPORT_H5.getType().equals(request.getBizSource())) {
            operateBizSubTypeEnum = OperateBizSubTypeEnum.ABNORMAL_REPORT_H5;
        }

        for (SendDetail sendDetail : sendDetails){
            AbnormalWayBill abnormalWayBill = new AbnormalWayBill();
            abnormalWayBill.setWaybillCode(sendDetail.getWaybillCode());
            abnormalWayBill.setPackageCode(sendDetail.getPackageBarcode());
            abnormalWayBill.setCreateUserCode(request.getUserID());
            abnormalWayBill.setCreateUserErp(request.getUserERP());
            abnormalWayBill.setCreateUser(request.getUserName());
            abnormalWayBill.setCreateSiteCode(request.getDistCenterID());
            abnormalWayBill.setCreateSiteName(request.getDistCenterName());
            abnormalWayBill.setQcType(request.getQcType());
            abnormalWayBill.setQcValue(request.getQcValue());
            abnormalWayBill.setQcCode(request.getQcCode());
            abnormalWayBill.setQcName(request.getQcName());
            abnormalWayBill.setAbnormalReasonFirstId(request.getAbnormalReasonFirstId() == null ? 0 : request.getAbnormalReasonFirstId());
            abnormalWayBill.setAbnormalReasonFirstName(request.getAbnormalReasonFirstName() == null ? Constants.EMPTY_FILL : request.getAbnormalReasonFirstName());
            abnormalWayBill.setAbnormalReasonSecondId(request.getAbnormalReasonSecondId() == null  ? 0 : request.getAbnormalReasonSecondId());
            abnormalWayBill.setAbnormalReasonSecondName(request.getAbnormalReasonSecondName() == null ? Constants.EMPTY_FILL : request.getAbnormalReasonSecondName());
            abnormalWayBill.setAbnormalReasonThirdId(request.getQcCode() == null  ? 0 : request.getQcCode().longValue());
            abnormalWayBill.setAbnormalReasonThirdName(request.getQcValue() == null ? Constants.EMPTY_FILL : request.getQcValue());
            abnormalWayBill.setSortingReturn(request.getIsSortingReturn());
            abnormalWayBill.setOperateTime(request.getOperateTime());
            if (request.getWaveBusinessId() == null) {
                //查路由系统班次号tangcq 2018年6月29日18:35:32
                abnormalWayBill.setWaveBusinessId(vrsRouteTransferRelationManager.queryWaveInfoByWaybillCodeAndNodeCode(abnormalWayBill.getWaybillCode(), abnormalWayBill.getCreateSiteCode()));
            } else {
                abnormalWayBill.setWaveBusinessId(request.getWaveBusinessId());
            }
            abnormalWayBill.setOperateFlowId(sendDetail.getOperateFlowId());
            abnormalWayBill.setOperatorData(request.getOperatorData());
            if (log.isInfoEnabled()) {
                log.info("convert2AbnormalWayBills|发送配送异常流水:abnormalWayBill={}", JsonHelper.toJson(abnormalWayBill));
            }

            // 执行插入
            abnormalWayBillService.insertAbnormalWayBill(abnormalWayBill);

            // 只有异常处理和异常提报(新)需要处理
            if (operateBizSubTypeEnum != null) {
                // 记录配送异常操作流水
                jyOperateFlowService.sendAbnormalOperateFlowData(abnormalWayBill, operateBizSubTypeEnum);
            }

        }
    }

    public void convertThenAddTask(QualityControlRequest request) throws Exception {

        Task qcTask = new Task();
        qcTask.setKeyword1(request.getQcType() + "");
        qcTask.setKeyword2(request.getQcValue());
        qcTask.setOwnSign(BusinessHelper.getOwnSign());
        qcTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        qcTask.setType(Task.TASK_TYPE_REVERSE_QUALITYCONTROL);
        qcTask.setTableName(Task.getTableName(qcTask.getType()));
        qcTask.setSequenceName(Task.getSequenceName(qcTask.getTableName()));
        qcTask.setBody(JsonHelper.toJson(request));
        qcTask.setCreateTime(new Date());
        qcTask.setCreateSiteCode(Integer.parseInt(String.valueOf(request.getDistCenterID())));
        qcTask.setExecuteCount(0);
        StringBuilder fringerprint = new StringBuilder();
        fringerprint.append(request.getDistCenterID() + "_" + qcTask.getType() + "_" + qcTask.getKeyword1() + "_" + qcTask.getKeyword2());
        qcTask.setFingerprint(Md5Helper.encode(fringerprint.toString()));

        taskService.add(qcTask);
    }

    /*
    * 根据站点和条码判断是否生成分拣退货任务
    * */
    public void generateSortingReturnTask(Integer siteCode, String oldWaybillCode, String newPackageCode, Date operateTime) {
        if (StringHelper.isEmpty(oldWaybillCode) || StringHelper.isEmpty(newPackageCode)) {
            return;
        }
        AbnormalWayBill abnormalWayBill = null;
        String oldPackageCode = null;
        //根据新单组装包裹号
        String index = WaybillUtil.getPackageSuffix(newPackageCode);
        if (StringHelper.isNotEmpty(index)) {
            oldPackageCode = oldWaybillCode + index;
        }

        try {
            if (StringHelper.isNotEmpty(oldPackageCode)) {
                //先用包裹号查询abnormal_waybill表
                abnormalWayBill = this.abnormalWayBillService.getAbnormalWayBillByQcValue(siteCode, oldPackageCode);
            }
            //如果包裹维度没有，查询运单维度
            if (abnormalWayBill == null) {
                abnormalWayBill = this.abnormalWayBillService.getAbnormalWayBillByQcValue(siteCode, oldWaybillCode);
            }

        } catch (Exception e) {
            log.error("获取异常提报记录信息失败，参数:{}, {}, {}", siteCode, oldWaybillCode, newPackageCode, e);
        }

        if (abnormalWayBill != null && this.isGenerateSortingReturnTask(abnormalWayBill.getQcCode())) {
            ReturnsRequest sortingReturn = new ReturnsRequest();
            sortingReturn.setSiteCode(abnormalWayBill.getCreateSiteCode());
            sortingReturn.setSiteName(abnormalWayBill.getCreateSiteName());
            sortingReturn.setUserCode(abnormalWayBill.getCreateUserCode());
            sortingReturn.setUserName(abnormalWayBill.getCreateUser());

            if (StringHelper.isNotEmpty(oldPackageCode) && WaybillUtil.isPackageCode(oldPackageCode)) {
                sortingReturn.setPackageCode(oldPackageCode);
            } else {
                sortingReturn.setPackageCode(oldWaybillCode);
            }
            sortingReturn.setBusinessType(10);
            sortingReturn.setOperateTime(DateHelper.formatDateTime(operateTime));
            sortingReturn.setShieldsError(abnormalWayBill.getQcName());
            Task task = new Task();
            task.setKeyword1(abnormalWayBill.getCreateSiteCode() + "");
            task.setKeyword2(abnormalWayBill.getQcValue());
            task.setCreateSiteCode(abnormalWayBill.getCreateSiteCode());
            task.setReceiveSiteCode(abnormalWayBill.getCreateSiteCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setType(Task.TASK_TYPE_RETURNS);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setFingerprint(Md5Helper.encode(task.getKeyword1() + "_" + task.getKeyword2() + "_" + task.getCreateSiteCode()));
            task.setBody(Constants.PUNCTUATION_OPEN_BRACKET + JsonHelper.toJson(sortingReturn) + Constants.PUNCTUATION_CLOSE_BRACKET);

            try{
                taskService.add(task);
            }catch(Exception e){
                log.error("质控异常生成分拣退货数据异常:{}", JsonHelper.toJson(task), e);
            }
        }
    }

    /*
    * 从配置中获取需要发送分拣退货任务的原因列表
    * */
    private boolean isGenerateSortingReturnTask(Integer reasonId){
        SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_ABNORMAL_REASON_ID_GENERATE_SORTING_RETURN_TASK);
        if (content != null && content.getKeyCodes() != null && ! content.getKeyCodes().isEmpty()) {
            return content.getKeyCodes().contains(reasonId.toString());
    }
        return false;
    }

    private String reportSystem = "dms";

    /**
     * 处理异常提报数据
     * @param qcReportJmqDto 消息提
     * @return 处理结果
     * @author fanggang7
     * @time 2022-02-18 15:38:54 周五
     */
    public Result<Boolean> handleQcReportConsume(QcReportJmqDto qcReportJmqDto) {
        log.info("handleQcReportConsume param: {}", JsonHelper.toJson(qcReportJmqDto));
        Result<Boolean> result = Result.success();
        try {
            if (StringUtils.isBlank(qcReportJmqDto.getReportSystem()) || !Objects.equals(qcReportJmqDto.getReportSystem(), reportSystem)){
                return result;
            }
            final BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffIgnoreIsResignByErp(qcReportJmqDto.getCreateUser());
            if(baseStaff == null){
                log.error("handleQcReportConsume 未找到此erp:{}信息", qcReportJmqDto.getCreateUser());
                return result.toFail(String.format("未找到此erp:%s信息", qcReportJmqDto.getCreateUser()));
            }

            String barCodes = qcReportJmqDto.getPackageNumber();
            String[] barCodeList = barCodes.split(Constants.SEPARATOR_COMMA);
            // 从异常提报(新)质控H5页面回调接口的缓存中获取网格信息
            OperatorData operatorData = getOperatorDataFromCache(qcReportJmqDto);

            for (String barCode : barCodeList) {
                final QualityControlRequest qualityControlRequest = new QualityControlRequest();
                qualityControlRequest.setQcVersionFlag(QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
                qualityControlRequest.setQcValue(barCode);
                qualityControlRequest.setQcType(PACKAGE_CODE_TYPE);
                qualityControlRequest.setUserERP(qcReportJmqDto.getCreateUser());
                qualityControlRequest.setUserName(baseStaff.getStaffName());
                qualityControlRequest.setUserID(baseStaff.getStaffNo());
                qualityControlRequest.setDistCenterID(Integer.parseInt(qcReportJmqDto.getCreateDept()));
                qualityControlRequest.setDistCenterName(qcReportJmqDto.getCreateDeptName());
                qualityControlRequest.setOperateTime(new Date(qcReportJmqDto.getCreateTime()));
                qualityControlRequest.setQcCode(qcReportJmqDto.getAbnormalThirdId().intValue());
                qualityControlRequest.setQcName(qcReportJmqDto.getAbnormalThirdName());
                qualityControlRequest.setAbnormalReasonFirstId(qcReportJmqDto.getAbnormalFirstId());
                qualityControlRequest.setAbnormalReasonFirstName(qcReportJmqDto.getAbnormalFirstName());
                qualityControlRequest.setAbnormalReasonSecondId(qcReportJmqDto.getAbnormalSecondId());
                qualityControlRequest.setAbnormalReasonSecondName(qcReportJmqDto.getAbnormalSecondName());
                qualityControlRequest.setAbnormalReasonThirdId(qcReportJmqDto.getAbnormalThirdId());
                qualityControlRequest.setAbnormalReasonThirdName(qcReportJmqDto.getAbnormalThirdName());
                qualityControlRequest.setIsSortingReturn(false);
                qualityControlRequest.setTrackContent("订单扫描异常【" + qcReportJmqDto.getAbnormalThirdName() + "】");
                // 设置菜单来源
                qualityControlRequest.setBizSource(AbnormalBizSourceEnum.ABNORMAL_REPORT_H5.getType());
                // 设置网格信息
                qualityControlRequest.setOperatorData(operatorData);

                Task task = new Task();
                task.setBody(JsonHelper.toJson(qualityControlRequest));
                log.info("dealQualityControlTask param: {}", JsonHelper.toJson(task));
                final TaskResult taskResult = this.dealQualityControlTask(task);
                log.info("dealQualityControlTask param: {} result: {}", JsonHelper.toJson(task), JsonHelper.toJson(taskResult));

                // 找到操作人登录网格并发送MQ消息
                QcfindGridAndSendMQ(qcReportJmqDto);

                if(!TaskResult.toBoolean(taskResult)){
                    log.error("handleQcReportConsume fail packageCode {} param {} ", barCode, JsonHelper.toJson(qcReportJmqDto));
                    return result.toFail();
                } else {
                    result.setData(true);
                }
            }

        } catch (Exception e) {
            log.error("handleQcReportConsume exception ", e);
            result.toFail("handleQcReportConsume exception " + e.getMessage());
        }
        return result;
    }

    private OperatorData getOperatorDataFromCache(QcReportJmqDto qcReportJmqDto) {
        if (qcReportJmqDto.getId() == null) {
            log.warn("handleQcReportConsume|异常上报ID为空:businessId={}", qcReportJmqDto.getId());
            return null;
        }
        try {
            // 从缓存中获取网格信息
            String operatorDataStr = cacheService.get(CacheKeyConstants.CACHE_KEY_ABNORMAL_H5_CALLBACK + qcReportJmqDto.getId());
            if (StringUtils.isBlank(operatorDataStr)) {
                log.warn("handleQcReportConsume|根据异常上报ID查询网格缓存为空:businessId={}", qcReportJmqDto.getId());
                return null;
            }
            // json字符串反序列为对象
            OperatorData operatorData = JsonHelper.fromJson(operatorDataStr, OperatorData.class);
            return operatorData;
        } catch (Exception e) {
            log.error("handleQcReportConsume|根据异常上报ID查询网格缓存出现异常:qcReportJmqDto={}", JsonHelper.toJsonMs(qcReportJmqDto), e);
        }
        return null;
    }

    private void QcfindGridAndSendMQ(QcReportJmqDto qcReportJmqDto) {
        try{
            String positionCode = getCreateGridCodeByUser(qcReportJmqDto.getCreateUser(), qcReportJmqDto.getCreateTime());
            if (StringUtils.isEmpty(positionCode)) {
                return;
            }
            // 推送MQ
            abnormalReportRecordMQ body = BeanUtils.copy(qcReportJmqDto, abnormalReportRecordMQ.class);
            body.setCreateGridCode(positionCode);
            abnormalReportRecordProducer.sendOnFailPersistent(qcReportJmqDto.getPackageNumber(),JsonHelper.toJson(body));
        }catch (Exception e) {
            log.error("ualityControlService.QcfindGridAndSendMQ 异常:{}",JsonHelper.toJson(qcReportJmqDto),e);
        }
    }

    public Result<Void> checkMqParam(QcReportJmqDto qcReportJmqDto) {
        Result<Void> result = Result.success();
        if(StringUtils.isBlank(qcReportJmqDto.getPackageNumber())){
            return result.toFail("参数错误，packageNumber为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateUser())){
            return result.toFail("参数错误，createUser为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDept())){
            return result.toFail("参数错误，createDept为空");
        }
        if(qcReportJmqDto.getCreateTime() == null){
            return result.toFail("参数错误，createTime为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDeptName())){
            return result.toFail("参数错误，createDeptName为空");
        }
        if(qcReportJmqDto.getAbnormalThirdId() == null){
            return result.toFail("参数错误，abnormalThirdId为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getAbnormalThirdName())){
            return result.toFail("参数错误，abnormalThirdName为空");
        }
        return result;
    }

    /**
     * 处理异常提报数据
     * @param qcReportJmqDto 消息提
     * @return 处理结果
     * @author fanggang7
     * @time 2022-02-18 15:38:54 周五
     */
    public Result<Boolean> handleQcOutCallReportConsume(QcReportOutCallJmqDto qcReportJmqDto) {
        log.info("handleQcReportConsume param: {}", JsonHelper.toJson(qcReportJmqDto));
        Result<Boolean> result = Result.success();
        try {
            if (StringUtils.isBlank(qcReportJmqDto.getReportSystem()) || !Objects.equals(qcReportJmqDto.getReportSystem(), reportSystem)){
                return result;
            }
            final BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(qcReportJmqDto.getCreateUser());
            if(baseStaff == null){
                log.error("未找到此erp:{}信息", qcReportJmqDto.getCreateUser());
                return result.toFail(String.format("未找到此erp:%s信息", qcReportJmqDto.getCreateUser()));
            }

            String barCodes = qcReportJmqDto.getPackageNumber();
            String[] barCodeList = barCodes.split(Constants.SEPARATOR_COMMA);

            for (String barCode : barCodeList) {
                final QualityControlRequest qualityControlRequest = new QualityControlRequest();
                qualityControlRequest.setQcVersionFlag(QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
                qualityControlRequest.setQcValue(barCode);
                qualityControlRequest.setQcType(PACKAGE_CODE_TYPE);
                qualityControlRequest.setUserERP(qcReportJmqDto.getCreateUser());
                qualityControlRequest.setUserName(baseStaff.getStaffName());
                qualityControlRequest.setUserID(baseStaff.getStaffNo());
                qualityControlRequest.setDistCenterID(Integer.parseInt(qcReportJmqDto.getCreateDept()));
                qualityControlRequest.setDistCenterName(qcReportJmqDto.getCreateDeptName());
                qualityControlRequest.setOperateTime(new Date(qcReportJmqDto.getCreateTime()));
                qualityControlRequest.setQcCode(qcReportJmqDto.getAbnormalThirdId().intValue());
                qualityControlRequest.setQcName(qcReportJmqDto.getAbnormalThirdName());
                qualityControlRequest.setAbnormalReasonFirstId(qcReportJmqDto.getAbnormalFirstId());
                qualityControlRequest.setAbnormalReasonFirstName(qcReportJmqDto.getAbnormalFirstName());
                qualityControlRequest.setAbnormalReasonSecondId(qcReportJmqDto.getAbnormalSecondId());
                qualityControlRequest.setAbnormalReasonSecondName(qcReportJmqDto.getAbnormalSecondName());
                qualityControlRequest.setAbnormalReasonThirdId(qcReportJmqDto.getAbnormalThirdId());
                qualityControlRequest.setAbnormalReasonThirdName(qcReportJmqDto.getAbnormalThirdName());
                qualityControlRequest.setIsSortingReturn(false);
                qualityControlRequest.setTrackContent("订单扫描异常【" + qcReportJmqDto.getAbnormalThirdName() + "】");
                // 设置菜单来源
                qualityControlRequest.setBizSource(AbnormalBizSourceEnum.ABNORMAL_OUT_CALL.getType());
                Task task = new Task();
                task.setBody(JsonHelper.toJson(qualityControlRequest));
                log.info("dealQualityControlTask param: {}", JsonHelper.toJson(task));
                final TaskResult taskResult = this.dealQualityControlTask(task);
                log.info("dealQualityControlTask param: {} result: {}", JsonHelper.toJson(task), JsonHelper.toJson(taskResult));

                // 找到操作人登录网格并发送MQ消息
                QcOutCallfindGridAndSendMQ(qcReportJmqDto);

                if(!TaskResult.toBoolean(taskResult)){
                    log.error("handleQcOutCallReportConsume fail packageCode {} param {} ", barCode, JsonHelper.toJson(qcReportJmqDto));
                    return result.toFail();
                } else {
                    result.setData(true);
                }
            }
        } catch (Exception e) {
            log.error("handleQcOutCallReportConsume exception ", e);
            result.toFail("handleQcOutCallReportConsume exception " + e.getMessage());
        }
        return result;
    }

    private void QcOutCallfindGridAndSendMQ(QcReportOutCallJmqDto qcReportJmqDto) {
        try{
            String positionCode = getCreateGridCodeByUser(qcReportJmqDto.getCreateUser(), qcReportJmqDto.getCreateTime());
            if (StringUtils.isEmpty(positionCode)) {
                return;
            }
            // 推送MQ
            abnormalReportRecordMQ body = BeanUtils.copy(qcReportJmqDto, abnormalReportRecordMQ.class);
            body.setCreateGridCode(positionCode);
            abnormalReportRecordProducer.sendOnFailPersistent(qcReportJmqDto.getPackageNumber(),JsonHelper.toJson(body));
        }catch (Exception e) {
            log.error("ualityControlService.QcfindGridAndSendMQ 异常:{}",JsonHelper.toJson(qcReportJmqDto),e);
        }
    }

    private String getCreateGridCodeByUser(String createUser, Long createTime) {
        // 查询登录人提报时间所在网格
        UserSignQueryRequest condition = new UserSignQueryRequest();
        condition.setUserCode(createUser);
        condition.setSignInTimeEnd(new Date(createTime));
        JdCResponse<UserSignRecordData> response = userSignRecordService.queryLastUserSignRecordData(condition);
        if (!response.isSucceed() || response.getData() == null) {
            log.warn("ualityControlService.QcfindGridAndSendMQ 查询操作人最近一次登录时间失败:{} {}",JsonHelper.toJson(condition),response.getMessage());
            return null;
        }

        UserSignRecordData userSignRecordData = response.getData();
        if (StringUtils.isEmpty(userSignRecordData.getRefGridKey())) {
            log.warn("ualityControlService.QcfindGridAndSendMQ 查询操作人最近一次登录未获取到网格:{}",JsonHelper.toJson(condition));
            return null;
        }
        
        return userSignRecordData.getRefGridKey();
    }

    public Result<Void> checkMqParam(QcReportOutCallJmqDto qcReportJmqDto) {
        Result<Void> result = Result.success();
        if(StringUtils.isBlank(qcReportJmqDto.getPackageNumber())){
            return result.toFail("参数错误，packageNumber为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateUser())){
            return result.toFail("参数错误，createUser为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDept())){
            return result.toFail("参数错误，createDept为空");
        }
        if(qcReportJmqDto.getCreateTime() == null){
            return result.toFail("参数错误，createTime为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getCreateDeptName())){
            return result.toFail("参数错误，createDeptName为空");
        }
        if(qcReportJmqDto.getAbnormalThirdId() == null){
            return result.toFail("参数错误，abnormalThirdId为空");
        }
        if(StringUtils.isBlank(qcReportJmqDto.getAbnormalThirdName())){
            return result.toFail("参数错误，abnormalThirdName为空");
        }
        return result;
    }

    /**
     * 处理案件全程跟踪
     * @param qualityControlRequest
     * @author fanggang7
     * @time 2023-08-08 18:10:21 周一
     */
    private void handleSecurityCheckWaybillTrace(QualityControlRequest qualityControlRequest) {
        try {
            // 读取系统配置
            final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_SECURITY_CHECK_SITE_ASSOCIATION + qualityControlRequest.getDistCenterID());
            if (sysConfig == null) {
                return;
            }
            final SiteCodeAssociationDto siteCodeAssociationDto = JSON.parseObject(sysConfig.getConfigContent(), SiteCodeAssociationDto.class);
            if (siteCodeAssociationDto == null) {
                return;
            }
            // 判断是否是在配置范围中
            final List<Integer> siteCodeAssociationList = siteCodeAssociationDto.getSa();
            if(CollectionUtils.isEmpty(siteCodeAssociationList)){
                return;
            }
            log.info("handleSecurityCheckWaybillTrace match {}", JsonHelper.toJson(qualityControlRequest));
            qualityControlRequest.setAbnormalReasonThirdId(qualityControlRequest.getQcCode() == null  ? 0 : qualityControlRequest.getQcCode().longValue());
            boolean waybillIsProhibitedAbnormal = checkWaybillIsProhibitedAbnormal(qualityControlRequest);
            if(!waybillIsProhibitedAbnormal){
                return;
            }
            // 如果已经发过全程跟踪，则不再发送
            if(this.checkSendSecurityCheckWaybillTraceAlready(qualityControlRequest)){
                return;
            }
            // 发送全程跟踪消息
            this.sendWaybillTrace(qualityControlRequest, WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK);
        } catch (Exception e) {
            log.error("handleSecurityCheckWaybillTrace exception {}", JsonHelper.toJson(qualityControlRequest), e);
        }
    }

    private boolean checkWaybillIsProhibitedAbnormal(QualityControlRequest qualityControlRequest) {
        // 区分是否有异常举报，1. 老版本异常上报 三级原因：违禁品无法发货 - 27000 2. 新版H5 外呼-违禁品（20009-20010）二级
        try {
            if(Objects.equals(Constants.SECURITY_CHECK_OLD_VERSION_ABNORMAL_REASON_THIRD_ID, (long) qualityControlRequest.getQcCode()) || Objects.equals(Constants.SECURITY_CHECK_OLD_VERSION_ABNORMAL_REASON_THIRD_ID, qualityControlRequest.getAbnormalReasonThirdId())){
                return true;
            }
            final List<Long> secondIds = Constants.SECURITY_CHECK_NEW_VERSION_ABNORMAL_REASON_MAP.get(qualityControlRequest.getAbnormalReasonFirstId());
            if(CollectionUtils.isNotEmpty(secondIds)
                    && secondIds.contains(qualityControlRequest.getAbnormalReasonSecondId())){
                return true;
            }
        } catch (Exception e) {
            log.error("checkWaybillIsProhibitedAbnormal exception {}", JsonHelper.toJson(qualityControlRequest), e);
        }
        return false;
    }

    private boolean checkSendSecurityCheckWaybillTraceAlready(QualityControlRequest qualityControlRequest){
        String waybillCode = WaybillUtil.getWaybillCode(qualityControlRequest.getQcValue());
        final String exist = cacheService.get(String.format(CacheKeyConstants.CACHE_KEY_ASIA_SPORT_SECURITY_CHECK_WAYBILL, waybillCode));
        if (exist != null) {
            log.info("QualityControlService.checkSendSecurityCheckWaybillTraceAlready exist cache {} ", JsonHelper.toJson(qualityControlRequest));
            return true;
        }
        final List<PackageStateDto> waybillTrackExistList = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, WaybillStatus.WAYBILL_TRACK_SECURITY_CHECK_STATE.toString());
        if(CollectionUtils.isNotEmpty(waybillTrackExistList)){
            log.info("QualityControlService.checkSendSecurityCheckWaybillTraceAlready exist {} ", JsonHelper.toJson(qualityControlRequest));
            return true;
        }
        return false;
    }

    /**
     * 发送全程跟踪
     * @param operateType
     */
    private void sendWaybillTrace(QualityControlRequest qualityControlRequest, Integer operateType) {
        try {
            final BarCodeType barCodeType = BusinessUtil.getBarCodeType(qualityControlRequest.getQcValue());
            if(!Objects.equals(barCodeType, BarCodeType.PACKAGE_CODE) && !Objects.equals(barCodeType, BarCodeType.WAYBILL_CODE)){
                return;
            }
            WaybillStatus waybillStatus = new WaybillStatus();
            //设置站点相关属性
            waybillStatus.setPackageCode(qualityControlRequest.getQcValue());

            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(qualityControlRequest.getDistCenterID());
            waybillStatus.setCreateSiteCode(qualityControlRequest.getDistCenterID());
            waybillStatus.setCreateSiteName(siteOrgDto != null ? siteOrgDto.getSiteName() : Constants.EMPTY_FILL);

            waybillStatus.setOperatorId(qualityControlRequest.getUserID());
            waybillStatus.setOperator(qualityControlRequest.getUserName());
            // 操作时间为发货时间的前10秒
            long operateTime = (qualityControlRequest.getOperateTime() != null ? qualityControlRequest.getOperateTime().getTime() : System.currentTimeMillis()) - 1000L;
            waybillStatus.setOperateTime(new Date(operateTime));

            waybillStatus.setOperateType(operateType);
            Map<String, Object> extendParamMap = new HashMap<>();
            extendParamMap.put("traceDisplay", 0);
            extendParamMap.put("auditResult", 2);
            waybillStatus.setExtendParamMap(extendParamMap);

            waybillStatus.setRemark(String.format("您的快件在【%s】已二次安检，不通过", waybillStatus.getCreateSiteName()));


            // 添加到task表
            taskService.add(toTask(waybillStatus));
            cacheService.setEx(String.format(CacheKeyConstants.CACHE_KEY_ASIA_SPORT_SECURITY_CHECK_WAYBILL, WaybillUtil.getWaybillCode(qualityControlRequest.getQcValue())), Constants.STRING_FLG_TRUE, CacheKeyConstants.CACHE_KEY_ASIA_SPORT_SECURITY_CHECK_WAYBILL_TIMEOUT, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("发货发送安检全称跟踪失败 {}", JsonHelper.toJson(qualityControlRequest), e);
        }
    }

    /**
     * 转换成全称跟踪的Task
     *
     * @param waybillStatus
     * @return
     */
    private Task toTask(WaybillStatus waybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(waybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(waybillStatus.getOperateType()));
        task.setCreateSiteCode(waybillStatus.getCreateSiteCode());
        task.setBody(com.jd.bluedragon.utils.JsonHelper.toJson(waybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        return task;
    }
    private String getOldWaybillCode(BaseEntity<BigWaybillDto> baseEntity){
        //根据运单号校验是否存在原单号(是否是逆向单)，如果是 则可以正常提交异常处理，否则进行拦截校验
        if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() !=null && baseEntity.getData().getWaybill().getWaybillExt() !=null) {
            WaybillExt waybillExt=baseEntity.getData().getWaybill().getWaybillExt();
            if(StringUtils.isNotBlank(waybillExt.getOldWaybillCode())){
                return waybillExt.getOldWaybillCode();
            }
        }
        return null;
    }
}
