package com.jd.bluedragon.distribution.jy.service.unload;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.jy.config.WaybillConfig;
import com.jd.bluedragon.distribution.jy.dao.config.WaybillConfigDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadVehicleBoardDao;
import com.jd.bluedragon.distribution.jy.dto.unload.BoardScanTypeDto;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageRespDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadCollectDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskStageStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskStageTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.ScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.ScanTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectBatchUpdateTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.exception.UnloadPackageBoardException;
import com.jd.bluedragon.distribution.loadAndUnload.neum.UnloadCarWarnEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.jim.cli.Cluster;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BarCodeTypeEnum;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.common.utils.CacheKeyConstants.*;

/**
 * @author lvyuan21
 */
@Slf4j
@Service("jyUnloadVehicleCheckTysService")
public class JyUnloadVehicleCheckTysService {

    /**
     * 卸车空任务上一流向key前缀
     */
    public static final String TYS_UNLOAD_PREFIX_SITE = "tys.unload.prefix.site";

    /**
     * 查询上游流向次数
     */
    public static final int TYS_START_SITE_QUERY_COUNT = 3;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private StoragePackageMService storagePackageMservice;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JyBizTaskUnloadVehicleStageService jyBizTaskUnloadVehicleStageService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration ;

    @Autowired
    private JyUnloadVehicleBoardDao jyUnloadVehicleBoardDao;

    @Autowired
    private WaybillConfigDao waybillConfigDao;

    @Autowired
    private BoardCommonManager boardCommonManager;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Resource
    protected BaseMajorManager baseMajorManager;

    @Autowired
    private IJyUnloadVehicleManager jyUnloadVehicleManager;

    @Autowired
    @Qualifier("jyUnloadScanProducer")
    private DefaultJMQProducer unloadScanProducer;

    @Resource
    private Cluster redisClientCache;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    @Autowired
    @Qualifier("jyTysTaskBoardRelationGenerate")
    private DefaultJMQProducer jyTysTaskBoardRelationGenerate;

    @Autowired
    private JyCollectService jyCollectService;

    @Autowired
    @Qualifier(value = "jyCollectDataInitSplitProducer")
    private DefaultJMQProducer jyCollectDataInitSplitProducer;
    @Autowired
    @Qualifier(value = "jyCollectStatusBatchUpdateWaybillSplitProducer")
    private DefaultJMQProducer jyCollectStatusBatchUpdateWaybillSplitProducer;


    /**
     * 操作中心为始发中心+揽收类型为网点自送+运单状态为取消
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.kyExpressCancelCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String kyExpressCancelCheck(Integer operateSiteCode, Waybill waybill) {
        log.info("kyExpressCancelCheck-查询运单是否是取消状态:{}", JsonHelper.toJson(waybill));
        // 路由的第一站是否为始发中心
        boolean isStartSite = isStartOrEndSite(operateSiteCode, waybill, 0);
        if (isStartSite) {
            // 揽收类型为网点自送 71为是2 网点自提
            boolean isPickSelfOrNo = BusinessUtil.isWdzsOrNo(waybill.getWaybillSign());
            if (isPickSelfOrNo) {
                //运单状态为取消
                Integer waybillState = waybill.getWaybillState();
                if (WaybillStatus.WAYBILL_STATUS_CANCEL.equals(waybillState)) {
                    return String.format("商家自送运单【%s】已取消,验收失败", waybill.getWaybillCode());
                }
            }
        }
        return null;
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.isStartOrEndSite", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean isStartOrEndSite(Integer operateSiteCode, Waybill waybill, int locationFlag) {
        //操作所属站点code和目的转运中心code
        Integer finalRouterCode = getFinalOrFirstRouterFromDb(waybill, locationFlag);
        if (operateSiteCode != null && Objects.equals(operateSiteCode, finalRouterCode)) {
            log.info("isStartOrEndSite-return true;operateSiteCode={},waybillCode={}", operateSiteCode, waybill.getWaybillCode());
            return true;
        } else {
            log.info("isStartOrEndSite-return false;operateSiteCode={},waybillCode={}", operateSiteCode, waybill.getWaybillCode());
            return false;
        }
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.getFinalOrFirstRouterFromDb", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer getFinalOrFirstRouterFromDb(Waybill waybill, int locationFlag) {
        WaybillExt waybillExt = waybill.getWaybillExt();
        if (waybillExt != null) {
            if (locationFlag == -1) {
                //将dmsid转成纯数字id
                log.info("getFinalOrFirstRouterFromDb-router is null;return endDmsId:{}", waybillExt.getEndDmsId());
                return waybillExt.getEndDmsId();
            } else {
                log.info("getFinalOrFirstRouterFromDb-router is null;return startDmsId:{}", waybillExt.getStartDmsId());
                return waybillExt.getStartDmsId();
            }
        }
        return null;
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.checkPackageOverWeight", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void checkPackageOverWeight(DeliveryPackageD packageD, Waybill waybill, ScanPackageRespDto response) {
        String packageWeightLimit = uccPropertyConfiguration.getPackageWeightLimit();
        BigDecimal packageWeight = getPackageWeight(packageD, waybill);
        if (packageWeight != null && packageWeight.compareTo(new BigDecimal(packageWeightLimit)) > 0) {
            log.info("包裹超重:packageCode={},weight={},limit={}", response.getBarCode(), packageWeight.toPlainString(), packageWeightLimit);
            Map<String, String> warnMsg = response.getWarnMsg();
            warnMsg.put(UnloadCarWarnEnum.PACKAGE_OVER_WEIGHT_MESSAGE.getLevel(), String.format(UnloadCarWarnEnum.PACKAGE_OVER_WEIGHT_MESSAGE.getDesc(), packageWeight.toPlainString()));
        }
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.checkWaybillOverWeight", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void checkWaybillOverWeight(Waybill waybill) {
        String waybillWeightLimit = uccPropertyConfiguration.getWaybillWeightLimit();
        if (waybill.getAgainWeight() != null && waybill.getAgainWeight() > 0) {
            if (waybill.getAgainWeight() > Double.parseDouble(waybillWeightLimit)) {
                throw new UnloadPackageBoardException("运单重量大于" + waybillWeightLimit + "KG，请重新称重量方，谢谢。是否强制继续组板？");
            }
        } else if (waybill.getGoodWeight() != null && waybill.getGoodWeight() > 0) {
            if (waybill.getGoodWeight() > Double.parseDouble(waybillWeightLimit)) {
                throw new UnloadPackageBoardException("运单重量大于" + waybillWeightLimit + "KG，请重新称重量方，谢谢。是否强制继续组板？");
            }
        }
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.getPackageWeight", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BigDecimal getPackageWeight(DeliveryPackageD packageD, Waybill waybill) {
        BigDecimal packageWeight = null;
        // 获取包裹重量
        if (packageD != null) {
            // 先取复重
            if (packageD.getAgainWeight() != null && packageD.getAgainWeight() > 0) {
                packageWeight = BigDecimal.valueOf(packageD.getAgainWeight());
                // 没有复重则取原重
            } else if (packageD.getGoodWeight() != null && packageD.getGoodWeight() > 0) {
                packageWeight = BigDecimal.valueOf(packageD.getGoodWeight());
            }
        }
        // 如果包裹重量为空，从运单平除获取
        if (packageWeight == null) {
            if (waybill.getGoodNumber() == null || waybill.getGoodNumber() == 0) {
                return null;
            }
            // 先取复重
            if (waybill.getAgainWeight() != null && waybill.getAgainWeight() > 0) {
                packageWeight = BigDecimal.valueOf(waybill.getAgainWeight()).divide(BigDecimal.valueOf(waybill.getGoodNumber()), 4, RoundingMode.HALF_UP);
                // 不存在则取原重
            } else if (waybill.getGoodWeight() != null && waybill.getGoodWeight() > 0) {
                packageWeight = BigDecimal.valueOf(waybill.getGoodWeight()).divide(BigDecimal.valueOf(waybill.getGoodNumber()), 4, RoundingMode.HALF_UP);
            }
        }
        return packageWeight;
    }

    /**
     * 判断包裹是否扫描成功
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.packageIsScan", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void scanCodeIsScan(ScanPackageDto request) throws LoadIllegalException {
        if (request.getIsForceCombination()) {
            return;
        }
        String bizId = request.getBizId();
        String barCode = request.getScanCode();
        String boardCode = request.getBoardCode();
        // 拦截的包裹不能重复扫描
        String isExistIntercept = null;
        try {
            log.info("packageIsScanBoard-校验拦截缓存【{}】【{}】", barCode, bizId);
            String key = REDIS_PREFIX_SEAL_PACK_INTERCEPT + bizId + Constants.SEPARATOR_HYPHEN + barCode;
            isExistIntercept = redisClientCache.get(key);
        } catch (Exception e) {
            log.error("packageIsScan|获取缓存【{}】异常", "", e);
        }
        if (StringUtils.isNotBlank(isExistIntercept)) {
            throw new LoadIllegalException(LoadIllegalException.BORCODE_SEALCAR_INTERCEPT_EXIST_MESSAGE);
        }
        packageIsComBoard(barCode, boardCode);
    }

    /**
     * 判断包裹是否组板成功
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.packageIsComBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void packageIsComBoard(String barCode, String boardCode) throws LoadIllegalException {
        // 拦截的包裹不能重复组板
        if (StringUtils.isEmpty(boardCode)) {
            return;
        }
        boolean scanIsSuccess = false;
        String scanIsSuccessStr;
        try {
            String key = REDIS_PREFIX_BOARD_PACK + boardCode + Constants.SEPARATOR_HYPHEN + barCode;
            scanIsSuccessStr = redisClientCache.get(key);
            scanIsSuccess = Boolean.parseBoolean(scanIsSuccessStr);
        } catch (Exception e) {
            log.error("packageIsComBoard|获取缓存【{}】异常", "", e);
        }
        if (scanIsSuccess) {
            // 包裹已扫描组板成功则提示拦截
            throw new LoadIllegalException(String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE, barCode, boardCode));
        }
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.inspectAndCollectDeal", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void inspectionInterceptAndCollectDeal(String barCode, Waybill waybill, UnloadScanDto unloadScanDto, ScanPackageDto scanPackageDto, InvokeResult<ScanPackageRespDto> invokeResult, Integer scanCodeType) throws LoadIllegalException {
        // 加盟商余额校验
        this.allianceBusiDeliveryCheck(waybill);
        //处理验货
        this.inspection(barCode, unloadScanDto);
        //验货后处理集齐逻辑
        UnloadScanCollectDealDto unloadScanCollectDealDto = generateCollectParam(barCode, unloadScanDto, scanPackageDto, invokeResult, scanCodeType, waybill);
        this.collectDeal(unloadScanCollectDealDto, invokeResult);
    }

    private UnloadScanCollectDealDto generateCollectParam(String barCode, UnloadScanDto unloadScanDto,
                                                          ScanPackageDto scanPackageDto,
                                                          InvokeResult<ScanPackageRespDto> invokeResult,
                                                          Integer scanCodeType,
                                                          Waybill waybill ) {
        UnloadScanCollectDealDto res = new UnloadScanCollectDealDto();
        res.setScanCode(barCode);
        res.setScanCodeType(scanCodeType);
        res.setUser(scanPackageDto.getUser());
        res.setCurrentOperate(scanPackageDto.getCurrentOperate());
        res.setBizId(unloadScanDto.getBizId());
        if(unloadScanDto.getManualCreatedFlag() != null && unloadScanDto.getManualCreatedFlag() == 1) {
            res.setManualCreateTaskFlag(true);
        }else {
            res.setManualCreateTaskFlag(false);
        }
        res.setGoodNumber((!Objects.isNull(waybill) && !Objects.isNull(waybill.getGoodNumber())) ? waybill.getGoodNumber() : 0);
        res.setOldSiteId(waybill.getOldSiteId());
        res.setNextSiteId(scanPackageDto.getNextSiteCode());
        return res;
    }


    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.allianceBusiDeliveryCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void allianceBusiDeliveryCheck(Waybill waybill) throws LoadIllegalException {
        // 加盟商余额校验
        if (allianceBusiDeliveryDetailService.checkExist(waybill.getWaybillCode())
                && !allianceBusiDeliveryDetailService.checkMoney(waybill.getWaybillCode())) {
            throw new LoadIllegalException(LoadIllegalException.ALLIANCE_INTERCEPT_MESSAGE);
        }
    }

    /**
     * 验货拦截及验货处理
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.inspectionIntercept", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void inspection(String barCode, UnloadScanDto unloadScanDto) throws LoadIllegalException {
        // 验货任务
        unloadScanProducer.sendOnFailPersistent(barCode, JsonHelper.toJson(unloadScanDto));
    }


    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.interceptValidateUnloadCar", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String interceptValidateUnloadCar(Waybill waybill, DeliveryPackageD deliveryPackageD, ScanPackageRespDto response, String barCode) {
        log.info("JyUnloadCarCheckServiceImpl-interceptValidateUnloadCar-barCode:{}", barCode);
        InvokeResult<String> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);

        // 该处调用前已做对象初始化，无NPE
        Map<String, String> warnMsg = response.getWarnMsg();
        try {
            String waybillSign = waybill.getWaybillSign();
            // 信任运单标识
            boolean isTrust = BusinessUtil.isNoNeedWeight(waybillSign);
            // 如果66位不是信任运单，再次校验【56位是否是信任商家】和【66位是否是0可以称重】
            if (!isTrust) {
                // 信任商家标识 && 可以称重标识
                isTrust = BusinessUtil.isTrustBusi(waybillSign) && BusinessUtil.isAllowWeight(waybillSign);
            }
            // 是否是KA的重量逻辑校验 66->3
            boolean isNewWeightLogic = BusinessUtil.needWeighingSquare(waybillSign);
            // 纯配快运零担
            boolean isB2bPure = BusinessUtil.isCPKYLD(waybillSign);
            // 返单标识
            boolean isRefund = BusinessUtil.isRefund(waybillSign);

            // B网营业厅
            boolean isBnet = BusinessUtil.isBusinessHall(waybillSign);
            // waybillSign 66位为3 增加新的拦截校验
            if (isNewWeightLogic) {
                log.info("waybillSign 66为3增加新的拦截校验,barcode:{},waybillSin:{},result:{}", barCode, waybillSign, JsonUtils.toJSONString(result));
                String kaCheckStr = kaWaybillCheck(waybillSign, deliveryPackageD);
                if (StringUtils.isNotBlank(kaCheckStr)) {
                    return kaCheckStr;
                }
            } else {
                // 无重量禁止发货判断
                if (!isTrust && isB2bPure && !isRefund) {
                    if (waybill.getAgainWeight() == null || waybill.getAgainWeight() <= 0) {
                        log.warn("interceptValidate卸车无重量禁止发货单号：{}", barCode);
                        warnMsg.put(UnloadCarWarnEnum.NO_WEIGHT_FORBID_SEND_MESSAGE.getLevel(), barCode + UnloadCarWarnEnum.NO_WEIGHT_FORBID_SEND_MESSAGE.getDesc());
                    }
                }
                // 寄付临欠
                boolean isSendPayTemporaryDebt = BusinessUtil.isJFLQ(waybillSign);
                if (!isTrust && isBnet && isSendPayTemporaryDebt && (waybill.getAgainWeight() == null || waybill.getAgainWeight() <= 0
                        || StringUtils.isEmpty(waybill.getSpareColumn2()) || Double.parseDouble(waybill.getSpareColumn2()) <= 0)) {
                    // 非返单才提示
                    if (!isRefund) {
                        log.warn("interceptValidate卸车运费临时欠款无重量体积禁止发货单号：{}", barCode);
                        return LoadIllegalException.FREIGTH_TEMPORARY_PAY_NO_WEIGHT_VOLUME_FORBID_SEND_MESSAGE;
                    }
                }
            }
            if (!businessHallFreightSendReceiveCheck(waybill.getWaybillCode(), waybillSign)) {
                log.warn("interceptValidate卸车B网营业厅寄付未揽收完成禁止发货单号：{}", barCode);
                return LoadIllegalException.BNET_SEND_PAY_NO_RECEIVE_FINISH_MESSAGE;
            }

            JdCancelWaybillResponse jdResponse = waybillService.dealCancelWaybill(waybill.getWaybillCode());
            if (jdResponse != null && jdResponse.getCode() != null && !jdResponse.getCode().equals(JdResponse.CODE_OK)) {
                log.warn("loadUnloadInterceptValidate 运单【{}】已被拦截【{}】", barCode, jdResponse.getMessage());
                return jdResponse.getMessage();
            }
            // 有包装服务
            if (waybillConsumableRecordService.needConfirmed(waybill.getWaybillCode())) {
                log.warn("loadUnloadInterceptValidate 装卸车包装服务运单未确认包装完成禁止发货单号：{}", barCode);
                return LoadIllegalException.PACK_SERVICE_NO_CONFIRM_FORBID_SEND_MESSAGE;
            }
            // 金鹏订单
            if (!storagePackageMservice.checkWaybillCanSend(waybill.getWaybillCode(), waybillSign)) {
                log.warn("loadUnloadInterceptValidate 装卸车金鹏订单未上架集齐禁止发货单号：{}", barCode);
                return LoadIllegalException.JIN_PENG_NO_TOGETHER_FORBID_SEND_MESSAGE;
            }
        } catch (Exception e) {
            log.error("判断包裹拦截异常 {}", barCode, e);
        }
        return null;
    }

    /***
     * ka货物重量校验逻辑
     * @param waybillSign     标位
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.kaWaybillCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String kaWaybillCheck(String waybillSign, DeliveryPackageD deliveryPackageD) {
        if (deliveryPackageD != null) {
            // 非信任重量  信任重量不做重量体积拦截.---去除 信任非信任的判断逻辑，直接按照业务类型是否进行称重进行判断。
            // 是否需要校验体重 业务类型1
            if (BusinessUtil.isNeedCheckWeightOrNo(waybillSign)) {
                if (deliveryPackageD.getAgainWeight() == null || deliveryPackageD.getAgainWeight() <= 0) {
                    return LoadIllegalException.PACKAGE_NO_WEIGHT;
                }
            }
            // 是否需要校验体重 业务类型2
            if (BusinessUtil.isNeedCheckWeightBusiness2OrNo(waybillSign)) {
                if (deliveryPackageD.getAgainWeight() == null || deliveryPackageD.getAgainWeight() <= 0) {
                    return LoadIllegalException.PACKAGE_NO_WEIGHT;
                }
            }
        }
        return null;
    }

    /**
     * 跨越需求 增加目的转运中心+自提校验
     * 如果前面有运单信息 无需再次调用接口获取
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.kyExpressCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String kyExpressCheck(Waybill waybill, Integer operateSiteCode) {
        // 操作所属站点code和目的转运中心code 跨越路由
        boolean isEndSite = isStartOrEndSite(operateSiteCode, waybill, -1);
        if (isEndSite) {
            //判断是否是自提运单
            boolean isPickUpOrNo = BusinessUtil.isPickUpOrNo(waybill.getWaybillSign());
            log.warn("跨越物流判断,运单号为:{},是否自提为:{}", waybill.getWaybillCode(), isPickUpOrNo);
            if (isPickUpOrNo) {
                return "此单为自提单,请单独处理至自提区";
            }
        }
        return null;
    }

    /**
     * B网营业厅增加寄付揽收完成校验
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.businessHallFreightSendReceiveCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    private boolean businessHallFreightSendReceiveCheck(String waybillCode, String waybillSign) {
        if (!BusinessUtil.isBusinessHallFreightSendAndForward(waybillSign)) {
            return Boolean.TRUE;
        }
        List<PackageStateDto> result = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode,
                Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
        return CollectionUtils.isNotEmpty(result) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 根据运单号校验专网，true为专网
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.privateNetworkCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean privateNetworkCheck(Waybill waybill, ScanPackageRespDto response) {
        String waybillSign = waybill.getWaybillSign();
        // 是否专网
        boolean isPrivateNetwork = BusinessUtil.isPrivateNetwork(waybillSign);
        if (isPrivateNetwork) {
            Map<String, String> warnMsg = response.getWarnMsg();
            warnMsg.put(UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getLevel(), UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getDesc());
        }
        return isPrivateNetwork;
    }

    /**
     * 路由校验
     * 1、第一次生成板号
     * 2、非第一次目的地校验
     *
     * @param request  请求参数
     * @param response 原始返回结果
     * @return ture:  校验成功，   false  校验失败
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.routerCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean routerCheck(ScanPackageRespDto response, ScanPackageDto request) throws LoadIllegalException {
        if (StringUtils.isEmpty(request.getBoardCode())) {
            //第一次则生成板号
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            boardCommonRequest.setOperateUserErp(request.getUser().getUserErp());
            boardCommonRequest.setOperateUserName(request.getUser().getUserName());
            boardCommonRequest.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
            boardCommonRequest.setOperateSiteName(request.getCurrentOperate().getSiteName());
            boardCommonRequest.setReceiveSiteCode(request.getNextSiteCode());
            boardCommonRequest.setReceiveSiteName(request.getNextSiteName());
            boardCommonRequest.setBizSource(BizSourceEnum.PDA.getValue());
            boardCommonRequest.setBarCode(request.getScanCode());
            InvokeResult<Board> invokeResult = null;
            if(log.isInfoEnabled()) {
                log.info("JyUnloadVehicleCheckTysService.routerCheck-按箱号卸车扫描开板-param={}", JsonUtils.toJSONString(boardCommonRequest));
            }
            if(boardCommonRequest.getReceiveSiteCode() == null) {
                throw new LoadIllegalException("验货成功。获取下一流向为空，无法建板");
            }
            if(BusinessUtil.isBoxcode(request.getScanCode())) {
                invokeResult = boardCommonManager.createBoardCodeByBox(boardCommonRequest);
            }else {
                invokeResult = boardCommonManager.createBoardCode(boardCommonRequest);
            }
            if (invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE) {
                throw new LoadIllegalException(invokeResult.getMessage());
            }
            Board board = invokeResult.getData();
            if (board == null || StringUtils.isEmpty(board.getCode())) {
                throw new LoadIllegalException(LoadIllegalException.BOARD_CREATE_FAIL_INTERCEPT_MESSAGE);
            }
            request.setBoardCode(board.getCode());
            request.setNextSiteCode(board.getDestinationId());
            request.setNextSiteName(board.getDestination());
            request.setBoardDestinationId(board.getDestinationId());

            response.setBizId(request.getBizId());
            response.setBoardCode(board.getCode());
            response.setEndSiteId(Long.valueOf(board.getDestinationId()));
            response.setEndSiteName(board.getDestination());
            response.setCreateBoardSuccessFlag(true);

            //存储开板箱包缓存
            this.setBoardTypeCache(request);
            return true;

        }
        // 非第一次则校验目的地是否一致
        String waybillCode = WaybillUtil.getWaybillCode(request.getScanCode());
        if (request.getNextSiteCode() == null) {
            // 此处直接返回，因为ver组板校验链会判断
//            throw new LoadIllegalException("验货成功，未找到包裹下游流向场地，无法进行后续组板");
            throw new UnloadPackageBoardException("验货成功，未找到包裹下游流向场地，是否强制继续组板？");
        }
        Integer destinationId = null;
        Response<Board> result = groupBoardManager.getBoard(request.getBoardCode());
        if (result != null && result.getCode() == ResponseEnum.SUCCESS.getIndex() && result.getData() != null) {
            destinationId = result.getData().getDestinationId();
        }
        if (destinationId == null) {
            throw new LoadIllegalException(LoadIllegalException.BOARD_RECIEVE_EMPEY_INTERCEPT_MESSAGE);
        }
        request.setBoardDestinationId(destinationId);
        if (!request.getNextSiteCode().equals(destinationId)) {
            Map<String, String> warnMsg = response.getWarnMsg();
            warnMsg.put(UnloadCarWarnEnum.FLOW_DISACCORD.getLevel(), UnloadCarWarnEnum.FLOW_DISACCORD.getDesc());
            return false;
        }
        return true;
    }

    /**
     * 是否发货校验
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.isSendCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void isSendCheck(ScanPackageDto scanPackageDto) {
        BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
        boardCommonRequest.setBarCode(scanPackageDto.getScanCode());
        boardCommonRequest.setOperateSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        boardCommonRequest.setReceiveSiteCode(scanPackageDto.getNextSiteCode());
        boardCommonManager.isSendCheck(boardCommonRequest);
    }

    /**
     * 板上包裹数校验
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.packageCountCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void packageCountCheck(ScanPackageDto scanPackageDto) {
        Integer unloadBoardBindingsMaxCount = uccPropertyConfiguration.getUnloadBoardBindingsMaxCount();
        boardCommonManager.packageCountCheck(scanPackageDto.getBoardCode(), unloadBoardBindingsMaxCount);
    }

    /**
     * 单个任务组板数量校验
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.boardCountCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void boardCountCheck(ScanPackageDto request, String stageBizId) {
        // 只有开新板才校验
        if (request.isCreateNewBoard()) {
            Integer unloadTaskBoardMaxCount = uccPropertyConfiguration.getUnloadTaskBoardMaxCount();
            int count = jyUnloadVehicleBoardDao.countByBizIdAndStageBizId(request.getBizId(), stageBizId);
            if (count >= unloadTaskBoardMaxCount) {
                throw new LoadIllegalException("该任务绑定的组板数量已达上限" + count);
            }
        }
    }

    /**
     * ver组板拦截
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.boardCombinationCheck", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String boardCombinationCheck(ScanPackageDto request) {
        BoardCommonRequest boardCommonRequest = createBoardCommonRequest(request);
        if(boardCommonRequest.getReceiveSiteCode() == null) {
            return "验货成功，未找到包裹下游流向场地，无法进行后续组板";
        }
        InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
        if (invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE) {
            if (JdCResponse.CODE_CONFIRM.equals(invokeResult.getCode())) {
                throw new UnloadPackageBoardException(invokeResult.getMessage());
            }
            return invokeResult.getMessage();
        }
        return null;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.createBoardCommonRequest", mState = {JProEnum.TP, JProEnum.FunctionError})
    private BoardCommonRequest createBoardCommonRequest(ScanPackageDto request) {
        BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
        boardCommonRequest.setBarCode(request.getScanCode());
        boardCommonRequest.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
        boardCommonRequest.setOperateSiteName(request.getCurrentOperate().getSiteName());
//        if (request.isCreateNewBoard()) {
        boardCommonRequest.setReceiveSiteCode(request.getNextSiteCode());
        boardCommonRequest.setReceiveSiteName(request.getNextSiteName());
//        } else {
//            boardCommonRequest.setReceiveSiteCode(request.getReceiveSiteCode());
//            boardCommonRequest.setReceiveSiteName(request.getReceiveSiteName());
//        }
        boardCommonRequest.setOperateUserErp(request.getUser().getUserErp());
        boardCommonRequest.setOperateUserName(request.getUser().getUserName());
        boardCommonRequest.setOperateUserCode(request.getUser().getUserCode());
        boardCommonRequest.setBoardCode(request.getBoardCode());
        boardCommonRequest.setBizSource(BizSourceEnum.PDA.getValue());
        return boardCommonRequest;
    }

    /**
     * <p>
     * 1、推TC组板关系
     * 2、卸车逻辑处理
     * 3、组板全程跟踪
     * </p>
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.dealUnloadAndBoxToBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void dealUnloadAndBoxToBoard(ScanPackageDto request, ScanPackageRespDto result) throws LoadIllegalException {
        AddBoardBox addBoardBox = new AddBoardBox();
        String boardCode = "";
        try {
            addBoardBox.setBoardCode(request.getBoardCode());
            addBoardBox.setBoxCode(request.getScanCode());
            addBoardBox.setOperatorErp(request.getUser().getUserErp());
            addBoardBox.setOperatorName(request.getUser().getUserName());
            addBoardBox.setSiteCode(request.getCurrentOperate().getSiteCode());
            addBoardBox.setSiteName(request.getCurrentOperate().getSiteName());
            addBoardBox.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
            addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());
            if (WaybillUtil.isPackageCode(request.getScanCode())) {
                addBoardBox.setBarcodeType(BarCodeTypeEnum.PACKAGE_TYPE.getCode());
            } else if (BusinessHelper.isBoxcode(request.getScanCode())) {
                addBoardBox.setBarcodeType(BarCodeTypeEnum.BOX_TYPE.getCode());
            }
            if (request.getNextSiteCode() != null && !request.getNextSiteCode().equals(request.getBoardDestinationId())) {
                addBoardBox.setFlowDisaccord(1);
            }
            Response<Integer> response = groupBoardManager.addBoxToBoardIgnoreStatus(addBoardBox);
            if (response == null) {
                log.warn("推组板关系失败!");
                throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_EXCEPTION_INTERCEPT_MESSAGE);
            }
            BoardCommonRequest boardCommonRequest = createBoardCommonRequest(request);
            if (response.getCode() == ResponseEnum.SUCCESS.getIndex()) {
                // 保存任务和板的关系
                result.setAddBoardSuccessFlag(saveUnloadVehicleBoard(request));
                // 设置板上已组包裹数
                result.setComBoardCount(response.getData());
                // 组板成功
                log.info("组板成功、板号:【{}】包裹号:【{}】站点:【{}】", request.getBoardCode(), request.getScanCode(), request.getCurrentOperate().getSiteCode());
                setCacheOfBoardAndPack(request.getBoardCode(), request.getScanCode());
                boxToBoardSuccessAfter(request, null);
                // 组板实操时间多加1秒，防止和验货实操时间相同导致全流程跟踪显示顺序错乱
                boardCommonRequest.setOperateTime(System.currentTimeMillis() + 1000L);
                // 组板全程跟踪
                boardCommonManager.sendWaybillTraceMq(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
                return;
            }
            /*
             * 组板失败
             *  500：失败-当前箱已经绑过板
             *  直接强制组板到新版上
             *
             *  二期优化增加【组板转移】提示
             * */
            if (response.getCode() == JdCResponse.CODE_ERROR) {
                log.warn("添加板箱关系失败,板号={},barCode={},原因={}", request.getBoardCode(), request.getScanCode(), response.getMesseage());
                if (request.getIsCombinationTransfer()) {

                    BoardScanTypeDto boardScanTypeDto = this.getBoardTypeCache(request.getCurrentOperate().getSiteCode(), request.getBoardCode());
                    if(!checkScanBoardType(boardScanTypeDto, request.getScanCode())) {
                        String warnMsg = "该板不支持组箱号包裹号，请重新开板尝试";
                        if(CacheKeyConstants.BOARD_SCAN_TYPE_PACKAGE.equals(boardScanTypeDto.getBoardType())) {
                            warnMsg = "该板为扫包裹类型，禁止扫箱号,请重新开板";
                        }
                        if(CacheKeyConstants.BOARD_SCAN_TYPE_BOX.equals(boardScanTypeDto.getBoardType())) {
                            warnMsg = "该板为扫箱类型，禁止扫包裹，请重新开板";
                        }
                        throw new RuntimeException(warnMsg);
                    };
                    // 调用TC的板号转移接口
                    InvokeResult<String> invokeResult = boardCommonManager.boardMoveIgnoreStatus(boardCommonRequest);
                    log.info("组板转移结果【{}】", JsonHelper.toJson(invokeResult));
                    if (invokeResult == null) {
                        throw new LoadIllegalException(LoadIllegalException.BOARD_MOVED_INTERCEPT_MESSAGE);
                    }
                    // 重新组板失败
                    if (invokeResult.getCode() != ResponseEnum.SUCCESS.getIndex()) {
                        log.warn("组板转移失败.原板号【{}】新板号【{}】失败原因【{}】",
                                invokeResult.getData(), request.getBoardCode(), invokeResult.getMessage());
                        throw new LoadIllegalException(LoadIllegalException.BOARD_MOVED_FAIL_INTERCEPT_MESSAGE);
                    }
                    // 保存任务和板的关系
                    result.setAddBoardSuccessFlag(saveUnloadVehicleBoard(request));
                    // 设置板上已组包裹数，组板转移需要重新查询新板上已组包裹数
                    setComBoardCount(request, result);
                    // 重新组板成功处理
                    log.info("组板转移成功.原板号【{}】新板号【{}】包裹号【{}】站点【{}】",
                            invokeResult.getData(), request.getBoardCode(), request.getScanCode(), request.getCurrentOperate().getSiteCode());
                    setCacheOfBoardAndPack(request.getBoardCode(), request.getScanCode());
                    boxToBoardSuccessAfter(request, invokeResult.getData());
                    return;
                } else {
                    Response<Board> boardResponse = groupBoardManager.getBoardByBoxCode(request.getScanCode(), request.getCurrentOperate().getSiteCode());
                    if (null != boardResponse) {
                        Board board = boardResponse.getData();
                        boardCode = board.getCode();
                    }
                    throw new UnloadPackageBoardException(String.format(LoadIllegalException.PACKAGE_ALREADY_BIND, boardCode));
                }
            } else {
                log.warn("添加板箱关系失败,板号={},barCode={},resultCode={},原因={}", request.getBoardCode(), request.getScanCode(), response.getCode(), response.getMesseage());
                throw new LoadIllegalException(response.getMesseage());
            }
        } catch (LoadIllegalException ex) {
            throw new LoadIllegalException(ex.getMessage());
        } catch (Exception e) {
            if (e instanceof UnloadPackageBoardException) {
                throw new UnloadPackageBoardException(String.format(LoadIllegalException.PACKAGE_ALREADY_BIND, boardCode));
            }
            log.error("推TC组板关系异常,入参:addBoardBox={},error=", JsonHelper.toJson(addBoardBox), e);
        }
        log.warn("组板失败：req={}", JsonUtils.toJSONString(request));
        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.setComBoardCount", mState = {JProEnum.TP, JProEnum.FunctionError})
    private void setComBoardCount(ScanPackageDto request, ScanPackageRespDto result) {
        Response<List<String>> tcResponse = groupBoardManager.getBoxesByBoardCode(request.getBoardCode());
        if (tcResponse != null && InvokeResult.RESULT_SUCCESS_CODE == tcResponse.getCode()) {
            if (CollectionUtils.isNotEmpty(tcResponse.getData())) {
                result.setComBoardCount(tcResponse.getData().size());
            }
        }
    }
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.saveUnloadVehicleBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    private boolean saveUnloadVehicleBoard(ScanPackageDto scanPackageDto) {
        //暂时不考虑异步
//        jyTysTaskBoardRelationGenerate.sendOnFailPersistent(scanPackageDto.getBoardCode(), JsonHelper.toJson(scanPackageDto));
        InvokeResult<Boolean> invokeResultRes = this.saveUnloadVehicleBoardHandler(scanPackageDto);
        if(!invokeResultRes.codeSuccess()) {
            log.warn("JyUnloadVehicleCheckTysService.saveUnloadVehicleBoard--转运卸车岗验货成功、组板成功，推送板关系失败，scanPackageDto={}，res={}",
                    JsonHelper.toJson(scanPackageDto), JsonHelper.toJson(invokeResultRes));
            throw new JyBizException(invokeResultRes.getMessage());
        }
        if(invokeResultRes.getData() != null && invokeResultRes.getData()) {
            if(log.isInfoEnabled()){
                log.info("JyUnloadVehicleCheckTysService.saveUnloadVehicleBoard--成功创建任务板关系，scanPackageDto={}", JsonHelper.toJson(scanPackageDto));
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param scanPackageDto
     * @return  只有成功插入数据为true
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.saveUnloadVehicleBoardHandler", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> saveUnloadVehicleBoardHandler(ScanPackageDto scanPackageDto) {
        InvokeResult<Boolean> res = new InvokeResult<>();
        res.success();
        res.setData(false);

        try{
            // 查询是否已经保存过此板
            JyUnloadVehicleBoardEntity entity = new JyUnloadVehicleBoardEntity();
            entity.setUnloadVehicleBizId(scanPackageDto.getBizId());
            entity.setBoardCode(scanPackageDto.getBoardCode());
            JyUnloadVehicleBoardEntity result = jyUnloadVehicleBoardDao.selectByBizIdAndBoardCode(entity);
            if (result == null) {
                //并发写入锁
                String key = REDIS_PREFIX_TASK_BOARD_CREATE + scanPackageDto.getBizId() + scanPackageDto.getBoardCode();
                InvokeResult<Void> lockRes = taskBoardRelationGenerateLock(key);
                if(InvokeResult.RESULT_SUCCESS_CODE != lockRes.getCode()) {
                    res.error(lockRes.getMessage());
                    return res;
                }

                result = jyUnloadVehicleBoardDao.selectByBizIdAndBoardCode(entity);
                if (result != null) {
                    //释放锁
                    unlockIgnoreException(key);
                    return res;
                }
                //释放锁
                createUnloadVehicleBoard(entity, scanPackageDto);
                int i = jyUnloadVehicleBoardDao.insertSelective(entity);
                res.setData(i > 0 ? true : false);
                unlockIgnoreException(key);
            }
            return res;
        }catch (Exception e) {
            log.error("JyUnloadVehicleCheckTysService.saveUnloadVehicleBoardHandler--服务异常,request={},errmsg={}",
                    JsonHelper.toJson(scanPackageDto), e.getMessage(), e);
            res.error("jy创建卸车任务板关系服务异常");
            return res;
        }
    }


    private InvokeResult<Void> taskBoardRelationGenerateLock(String key) {
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        try{
            Boolean getLockFlag = redisClientCache.set(key, "1", REDIS_PREFIX_TASK_BOARD_CREATE_TIMEOUT_SECONDS, TimeUnit.SECONDS, false);
            if(getLockFlag != null && getLockFlag) {
                return res;
            }
            Thread.sleep(REDIS_PREFIX_TASK_BOARD_CREATE_WAIT_SPIN_TIMESTAMP);
            getLockFlag = redisClientCache.set(key, "1", REDIS_PREFIX_TASK_BOARD_CREATE_TIMEOUT_SECONDS, TimeUnit.SECONDS, false);
            if(getLockFlag == null && !getLockFlag) {
                log.warn("JyUnloadVehicleCheckTysService.taskBoardRelationGenerateLock-未获取jy卸车任务板关系创建锁，key={}", key);
                res.error("多人同时操作，未获取到锁");
            }
            return res;
        }catch (Exception ex) {
            log.error("JyUnloadVehicleCheckTysService.taskBoardRelationGenerateLock-获取jy卸车任务板关系创建锁服务异常，key={}，errMsg={}", key, ex.getMessage(), ex);
            res.error("获取任务板关系锁服务异常，稍后重试");
            return res;
        }
    }


    private void createUnloadVehicleBoard(JyUnloadVehicleBoardEntity entity, ScanPackageDto scanPackageDto) {
        Date now = new Date();
        entity.setUnloadVehicleBizId(scanPackageDto.getBizId());
        if (scanPackageDto.getStageBizId() == null) {
            JyBizTaskUnloadVehicleStageEntity result = queryCurrentStage(scanPackageDto.getBizId(), scanPackageDto.isTaskFinish());
            if (result != null) {
                scanPackageDto.setStageBizId(result.getBizId());
            }
        }
        entity.setUnloadVehicleStageBizId(scanPackageDto.getStageBizId());
        entity.setBoardCode(scanPackageDto.getBoardCode());
        entity.setStartSiteId((long) scanPackageDto.getCurrentOperate().getSiteCode());
        entity.setStartSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        if (scanPackageDto.getNextSiteCode() != null) {
            entity.setEndSiteId(scanPackageDto.getNextSiteCode().longValue());
        }
        entity.setEndSiteName(scanPackageDto.getNextSiteName());
        entity.setGoodsAreaCode(scanPackageDto.getGoodsAreaCode());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setCreateUserErp(scanPackageDto.getUser().getUserErp());
        entity.setCreateUserName(scanPackageDto.getUser().getUserName());
        entity.setUpdateUserErp(scanPackageDto.getUser().getUserErp());
        entity.setUpdateUserName(scanPackageDto.getUser().getUserName());
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.setStageBizId", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> setStageBizId(ScanPackageDto request, UnloadScanDto unloadScanDto) {
        InvokeResult<Boolean> res = new InvokeResult<>();
        res.success();

        JyBizTaskUnloadVehicleStageEntity entity = queryCurrentStage(unloadScanDto.getBizId(), unloadScanDto.getSupplementary());
        if (entity == null) {

            String key = REDIS_PREFIX_STAGE_TASK_CREATE + unloadScanDto.getBizId() + unloadScanDto.getSupplementary();
            //排它锁
            InvokeResult<Void> lockRes = createStageTaskLock(key);
            if(InvokeResult.RESULT_SUCCESS_CODE != lockRes.getCode()) {
                res.error(lockRes.getMessage());
                return res;
            }
            entity = queryCurrentStage(unloadScanDto.getBizId(), unloadScanDto.getSupplementary());
            //锁内二次确认
            if(entity != null) {
                unlockIgnoreException(key);
                InvokeResult<Void> invokeResult = scanAccrualNodeCheck(request, unloadScanDto, entity);
                if(!invokeResult.codeSuccess()) {
                    res.error(invokeResult.getMessage());
                    return res;
                }
                unloadScanDto.setStageBizId(entity.getBizId());
                return res;
            }
            entity = new JyBizTaskUnloadVehicleStageEntity();
            createUnloadVehicleStage(entity, unloadScanDto);
            jyBizTaskUnloadVehicleStageService.insertSelective(entity);
            unloadScanDto.setStageBizId(entity.getBizId());
            //释放锁
            unlockIgnoreException(key);
        } else {
            InvokeResult<Void> invokeResult = scanAccrualNodeCheck(request, unloadScanDto, entity);
            if(!invokeResult.codeSuccess()) {
                res.error(invokeResult.getMessage());
                return res;
            }
            unloadScanDto.setStageBizId(entity.getBizId());
        }

        return res;
    }

    /**
     *  补扫动作在计提周期节点前后的校验
     * @param unloadScanDto
     * @param entity
     * @return
     */
    private InvokeResult<Void> scanAccrualNodeCheck(ScanPackageDto request, UnloadScanDto unloadScanDto, JyBizTaskUnloadVehicleStageEntity entity) {
        String methodDesc = "JyUnloadVehicleCheckTysService.supplementScanAccrualNodeCheck--补扫任务计提周期校验--";
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        //无需校验上一周期时间， 任务完成3天后禁止补扫
        Date accrualSettlementTime = DateHelper.getCurrentMonthAccrualSettlementTime();
        if(StringUtils.isEmpty(request.getBoardCode())) {
            //无板号两种场景： （1）人工模式补扫开板；（2）流水线模式补扫
            if(unloadScanDto.getSupplementary()
                    && entity.getEndTime() != null && entity.getEndTime().getTime() < accrualSettlementTime.getTime()
                    && System.currentTimeMillis() >= accrualSettlementTime.getTime()) {
                log.warn("{},无板号场景，该任务{}完成时间{}，当前时间已过计提周期{}，禁止扫描{}", methodDesc, entity.getBizId(), entity.getEndTime(), accrualSettlementTime, JsonUtils.toJSONString(unloadScanDto));
                res.error("该任务已过计提周期，禁止补扫，可自建任务扫描");
                return res;
            }
            return res;
        }else {
            JyUnloadVehicleBoardEntity jyUnloadVehicleBoardEntity = jyUnloadVehicleBoardDao.selectByBoardCode(request.getBoardCode());
            if(jyUnloadVehicleBoardEntity == null || jyUnloadVehicleBoardEntity.getUnloadVehicleStageBizId() == null) {
                return res;
            }
            if(log.isInfoEnabled()) {
                log.info("{},扫描请求={}，扫描对象组装={}，查询操作的任务={}，扫描板号实际绑定的任务={}",
                        JsonUtils.toJSONString(request), JsonUtils.toJSONString(unloadScanDto), JsonUtils.toJSONString(entity), JsonUtils.toJSONString(jyUnloadVehicleBoardEntity));
            }
            //补扫任务&操作的板是自己任务创建： 卡结算周期，过后禁止补扫
            if(jyUnloadVehicleBoardEntity.getUnloadVehicleStageBizId().equals(entity.getBizId())
                    && unloadScanDto.getSupplementary()
                    && entity.getEndTime() != null && entity.getEndTime().getTime() < accrualSettlementTime.getTime()
                    && System.currentTimeMillis() >= accrualSettlementTime.getTime()) {
                log.warn("{},该任务{}完成时间{}，当前时间已过计提周期{}，禁止扫描{}", methodDesc, entity.getBizId(), entity.getEndTime(), accrualSettlementTime, JsonUtils.toJSONString(unloadScanDto));
                res.error("该任务已过计提周期，禁止补扫，可自建任务扫描");
                return res;
            }
            //补扫或交班任务，操作的板是其他子任务创建，校验板号实际绑定任务是否已过计提周期，过后禁止操作
            JyBizTaskUnloadVehicleStageEntity stageEntity = jyBizTaskUnloadVehicleStageService.queryByBizId(jyUnloadVehicleBoardEntity.getUnloadVehicleStageBizId());
            if(JyBizTaskStageStatusEnum.COMPLETE.getCode().equals(stageEntity.getStatus())
                    && stageEntity.getEndTime() != null && stageEntity.getEndTime().getTime() < accrualSettlementTime.getTime()
                    && System.currentTimeMillis() >= accrualSettlementTime.getTime()) {
                log.warn("{},该任务{}完成时间{}，当前时间已过计提周期{}，禁止扫描{}", methodDesc, entity.getBizId(), entity.getEndTime(), accrualSettlementTime, JsonUtils.toJSONString(unloadScanDto));
                String msg = unloadScanDto.getSupplementary() ? "当前操作板为任务完成前创建，此任务已过计提周期无法扫描，可开新板进行扫描" : "当前操作板为交班前创建，此任务已过计提周期无法扫描，可开新板进行扫描";
                res.error(msg);
                return res;
            }
        }
        return res;
    }


    private void unlockIgnoreException(String key) {
        try{
            redisClientCache.del(key);
        }catch (Exception e) {
            //异常不抛出，超时时间几秒自动释放
            log.error("JyUnloadVehicleCheckTysService.createStageTaskUnlock--redis释放锁失败", key);
        }
    }

    private InvokeResult<Void> createStageTaskLock(String key) {
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        try{
            Boolean getLockFlag = false;
            getLockFlag = redisClientCache.set(key, "1", REDIS_PREFIX_STAGE_TASK_CREATE_TIMEOUT_SECONDS, TimeUnit.SECONDS, false);
            if(getLockFlag != null && getLockFlag) {
                return res;
            }

            Long startTime = System.currentTimeMillis();
            long waitSpin = REDIS_PREFIX_STAGE_TASK_CREATE_WAIT_SPIN_TIMESTAMP;//自旋时间
            while(System.currentTimeMillis() - startTime <= waitSpin) {
                Thread.sleep(20);
                getLockFlag = redisClientCache.set(key, "1", REDIS_PREFIX_STAGE_TASK_CREATE_TIMEOUT_SECONDS, TimeUnit.SECONDS, false);
                if(getLockFlag != null && getLockFlag) {
                    break;
                }
            }
            if(getLockFlag == null || !getLockFlag) {
                log.warn("JyUnloadVehicleCheckTysService.createStageTaskLock-未获取创建子任务锁，key={}", key);
                res.error("多人同时创建该任务，稍后重试");
            }
            return res;
        }catch (Exception ex) {
            log.error("JyUnloadVehicleCheckTysService.createStageTaskLock-获取创建子任务锁服务异常，key={}，errMsg={}", key, ex.getMessage(), ex);
            res.error("获取锁服务异常，稍后重试");
            return res;
        }
    }

    private JyBizTaskUnloadVehicleStageEntity queryCurrentStage(String bizId, boolean isSupplementary) {
        JyBizTaskUnloadVehicleStageEntity entity = new JyBizTaskUnloadVehicleStageEntity();
        entity.setUnloadVehicleBizId(bizId);
        entity.setType(isSupplementary ? JyBizTaskStageTypeEnum.SUPPLEMENT.getCode() : JyBizTaskStageTypeEnum.HANDOVER.getCode());
        if (!isSupplementary) {
            entity.setStatus(JyBizTaskStageStatusEnum.DOING.getCode());
        }
        return jyBizTaskUnloadVehicleStageService.queryCurrentStage(entity);
    }

    private void createUnloadVehicleStage(JyBizTaskUnloadVehicleStageEntity entity, UnloadScanDto unloadScanDto) {
        Date now = new Date(unloadScanDto.getCreateTime().getTime() - 1000L);
        entity.setUnloadVehicleBizId(unloadScanDto.getBizId());
        // 用于判断当前子任务的序号
        List<Long> idList = jyBizTaskUnloadVehicleStageService.countByUnloadVehicleBizId(unloadScanDto.getBizId());
        int serialNumber = CollectionUtils.isEmpty(idList) ? 1 : idList.size() + 1;
        entity.setBizId(unloadScanDto.getBizId() + Constants.SEPARATOR_HYPHEN + serialNumber);
        if(unloadScanDto.getSupplementary()) {
            entity.setType(JyBizTaskStageTypeEnum.SUPPLEMENT.getCode());
            entity.setStatus(JyBizTaskStageStatusEnum.COMPLETE.getCode());
            entity.setEndTime(now);
        }else {
            entity.setStatus(JyBizTaskStageStatusEnum.DOING.getCode());
            entity.setType(JyBizTaskStageTypeEnum.HANDOVER.getCode());
        }
        entity.setStartTime(now);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setCreateUserErp(unloadScanDto.getCreateUserErp());
        entity.setCreateUserName(unloadScanDto.getCreateUserName());
        entity.setUpdateUserErp(unloadScanDto.getCreateUserErp());
        entity.setUpdateUserName(unloadScanDto.getCreateUserName());
        entity.setYn(Constants.YN_YES);
    }


    /**
     * 设置板包裹缓存：默认7天
     * @param boardCode 板号
     * @param packageCode 包裹号
     */
    private void setCacheOfBoardAndPack(String boardCode, String packageCode) {
        try {
            int unloadCacheDurationHours = uccPropertyConfiguration.getUnloadCacheDurationHours();
            String key = REDIS_PREFIX_BOARD_PACK + boardCode + Constants.SEPARATOR_HYPHEN + packageCode;
            redisClientCache.setEx(key, String.valueOf(true), unloadCacheDurationHours, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("设置板【{}】包裹【{}】缓存异常", boardCode, packageCode, e);
        }
    }


    /**
     * 组板成功后获取包裹数量
     *
     * @param oldBoardCode 旧板号
     */
    private void boxToBoardSuccessAfter(ScanPackageDto request, String oldBoardCode) {
        try {
            String boardCode = request.getBoardCode();
            // 新板包裹数变更
            updateCache(REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(boardCode), 1);
            // 老板包裹数变更
            if (StringUtils.isNotEmpty(oldBoardCode)) {
                updateCache(REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(oldBoardCode), -1);
            }
        } catch (Exception e) {
            log.error("卸车扫描处理异常,参数【{}】", JsonHelper.toJson(request), e);
        }
    }

    /**
     * 更新缓存返回更新后value
     */
    private void updateCache(String cacheKey, int addCount) {
        try {
            redisClientCache.incrBy(cacheKey, addCount);
            redisClientCache.expire(cacheKey, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("更新【{}】缓存异常", cacheKey, e);
        }
    }

    /**
     * 拦截设置缓存
     */
    public void setCacheOfSealCarAndScanCodeIntercept(String bizId, String barCode) {
        try {
            int unloadCacheDurationHours = uccPropertyConfiguration.getUnloadCacheDurationHours();
            String key = REDIS_PREFIX_SEAL_PACK_INTERCEPT + bizId + Constants.SEPARATOR_HYPHEN + barCode;
            redisClientCache.setEx(key, barCode, unloadCacheDurationHours, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("设置封车【{}】包裹【{}】拦截重复缓存异常", bizId, barCode, e);
        }
    }

    /**
     * 无任务设置设置上游流向
     * @param request 请求参数
     * @param response 返回对象
     * @param unloadVehicleEntity 卸车任务
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.setStartSiteForJyUnloadVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void setStartSiteForJyUnloadVehicle(ScanPackageDto request, ScanPackageRespDto response,
                                                JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        String key = TYS_UNLOAD_PREFIX_SITE + Constants.SEPARATOR_HYPHEN + Constants.PDA_UNLOAD_TASK_PREFIX
                + Constants.SEPARATOR_HYPHEN + request.getBizId();
        // 如果是无任务卸车
        if (Constants.START_SITE_INITIAL_VALUE.equals(unloadVehicleEntity.getStartSiteId())) {
            // 从缓存中取包裹的顺序，判断是否到第三次了
            String packageCount = redisClientCache.get(key);
            if (packageCount != null) {
                int count = redisClientCache.incr(key).intValue();
                if (count <= TYS_START_SITE_QUERY_COUNT) {
                    log.info("setStartSiteForJyUnloadVehicle|第{}次获取路由结果：bizId={},packageCode={},router={}", count,
                            request.getBizId(), request.getScanCode(), request.getPrevSiteCode());
                    if (request.getPrevSiteCode() != null) {
                        // 更新无任务上游站点
                        updateJyUnloadVehicleStartSite(request, response, unloadVehicleEntity);
                        // 获取到以后删除缓存
                        redisClientCache.del(key);
                    } else {
                        // 第三次还没获取到，不再获取
                        if (count == TYS_START_SITE_QUERY_COUNT) {
                            log.info("setStartSiteForJyUnloadVehicle|三次都未获取到上一流向，不再获取：bizId={},packageCode={}",
                                    request.getBizId(), request.getScanCode());
                            // 更新无任务上游站点为当前站点
                            request.setPrevSiteCode(unloadVehicleEntity.getEndSiteId().intValue());
                            request.setPrevSiteName(unloadVehicleEntity.getEndSiteName());
                            updateJyUnloadVehicleStartSite(request, response, unloadVehicleEntity);
                            redisClientCache.del(key);
                        }
                    }
                }
            } else {
                log.info("setStartSiteForJyUnloadVehicle|第一次获取路由结果：bizId={},packageCode={},router={}", request.getBizId(), request.getScanCode(), request.getPrevSiteCode());
                if (request.getPrevSiteCode() != null) {
                    // 更新无任务上游站点
                    updateJyUnloadVehicleStartSite(request, response, unloadVehicleEntity);
                } else {
                    log.info("setStartSiteForJyUnloadVehicle|第一次未获取到上一流向：packageCode={},bizId={}", request.getScanCode(), request.getBizId());
                    // 放入缓存中，记录包裹顺序
                    redisClientCache.incr(key);
                }
            }
        }
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.checkGoodsArea", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String checkGoodsArea(ScanPackageDto request, ScanPackageRespDto response) {
        int currentSiteCode = request.getCurrentOperate().getSiteCode();
        Integer nextSiteCode = request.getNextSiteCode();
        String goodsAreaCode = jyUnloadVehicleManager.getGoodsAreaCode(currentSiteCode, nextSiteCode);
        if (StringUtils.isBlank(goodsAreaCode)) {
            return null;
        }
        if (StringUtils.isNotBlank(request.getGoodsAreaCode())) {
            if (!goodsAreaCode.equals(request.getGoodsAreaCode())) {
                if(uccPropertyConfiguration.getEnableGoodsAreaOfTysScan()){
                    response.setGoodsAreaCode(goodsAreaCode);
                }
                return "扫描包裹非本货区，请移除本区！";
            }
        }else {
            request.setGoodsAreaCode(goodsAreaCode);
        }
        response.setGoodsAreaCode(goodsAreaCode);
        return null;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.updateJyUnloadVehicleStartSite", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void updateJyUnloadVehicleStartSite(ScanPackageDto request, ScanPackageRespDto response,
                                               JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        unloadVehicleEntity.setUpdateTime(new Date());
        unloadVehicleEntity.setUpdateUserErp(request.getUser().getUserErp());
        unloadVehicleEntity.setUpdateUserName(request.getUser().getUserName());
        unloadVehicleEntity.setStartSiteId(Long.valueOf(request.getPrevSiteCode()));
        unloadVehicleEntity.setStartSiteName(request.getPrevSiteName());
        jyBizTaskUnloadVehicleService.saveOrUpdateOfBaseInfo(unloadVehicleEntity);
        response.setPrevSiteId(Long.valueOf(request.getPrevSiteCode()));
        response.setPrevSiteName(unloadVehicleEntity.getStartSiteName());
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.assembleReturnData", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void assembleReturnData(ScanPackageDto request, ScanPackageRespDto response, JyBizTaskUnloadVehicleEntity unloadVehicleEntity, UnloadScanDto unloadScanDto) {
        request.setStageBizId(unloadScanDto.getStageBizId());
        response.setStageBizId(unloadScanDto.getStageBizId());
        response.setStageFirstScan(checkIsStageFirstScan(unloadScanDto.getStageBizId()));
        response.setFirstScan(checkIsFirstScan(request.getBizId()));
        // 按件扫描
        if (ScanTypeEnum.SCAN_ONE.getCode().equals(request.getType())) {
            // 包裹号
            if (WaybillUtil.isPackageCode(request.getScanCode())) {
                response.setPackageAmount(1);
                response.setWaybillAmount(1);
                // 箱号
            } else if (BusinessUtil.isBoxcode(request.getScanCode())) {

            }
            // 按单扫描
        } else if (ScanTypeEnum.SCAN_WAYBILL.getCode().equals(request.getType())) {
            response.setPackageAmount(request.getGoodsNumber());
            response.setWaybillAmount(1);
        }
        response.setSupplementary(unloadScanDto.getSupplementary());
        response.setGoodsAreaCode(request.getGoodsAreaCode());
        if (!Constants.START_SITE_INITIAL_VALUE.equals(unloadVehicleEntity.getStartSiteId())) {
            response.setPrevSiteName(unloadVehicleEntity.getStartSiteName());
            response.setPrevSiteId(unloadVehicleEntity.getStartSiteId());
        } else if (request.getPrevSiteCode() != null) {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getPrevSiteCode());
            if (baseSite != null) {
                request.setPrevSiteName(baseSite.getSiteName());
            }
        }
        if (request.getNextSiteCode() != null) {
            response.setEndSiteId(Long.valueOf(request.getNextSiteCode()));
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getNextSiteCode());
            if (baseSite != null) {
                request.setNextSiteName(baseSite.getSiteName());
                response.setEndSiteName(baseSite.getSiteName());
            }
        }
    }

    /**
     * 校验是否为KA运单 如果是不支持按大宗操作 66为3 强制拦截
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.checkIsKaWaybill", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean checkIsKaWaybill(Waybill waybill) {
        return BusinessUtil.needWeighingSquare(waybill.getWaybillSign());
    }

    /**
     * 判断是否是卸车任务扫描的第一个包裹
     * @param bizId 卸车任务业务主键
     * @return 是或否
     */
    private boolean checkIsFirstScan(String bizId) {
        // 判断是否是
        String key = JY_UNLOAD_TASK_FIRST_SCAN_KEY + bizId;
        String isFirstScan = redisClientCache.get(key);
        if (isFirstScan == null) {
            redisClientCache.setEx(key, StringUtils.EMPTY, 7, TimeUnit.DAYS);
            return true;
        }
        return false;
    }

    /**
     * 判断是否是卸车任务子阶段扫描的第一个包裹
     * @param stageBizId 卸车任务子阶段业务主键
     * @return 是或否
     */
    private boolean checkIsStageFirstScan(String stageBizId) {
        // 判断是否是
        String key = JY_UNLOAD_TASK_FIRST_SCAN_KEY + stageBizId;
        String isFirstScan = redisClientCache.get(key);
        if (isFirstScan == null) {
            redisClientCache.setEx(key, StringUtils.EMPTY, 7, TimeUnit.DAYS);
            return true;
        }
        return false;
    }

    /**
     * 判断运单是否重复扫描
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.checkWaybillScanIsRepeat", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean checkWaybillScanIsRepeat(String bizId, String waybillCode)
            throws LoadIllegalException {
        // 根据运单号获取卸车扫描记录
        String key = REDIS_PREFIX_SEAL_WAYBILL + bizId + Constants.SEPARATOR_HYPHEN + waybillCode;
        String isExistIntercept = redisClientCache.get(key);
        return isExistIntercept != null;
    }

    /**
     * 运单验货扫描成功后设置缓存
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.waybillInspectSuccessAfter", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void waybillInspectSuccessAfter(String bizId, String waybillCode) {
        try {
            // 设置运单扫描记录
            String key = REDIS_PREFIX_SEAL_WAYBILL + bizId + Constants.SEPARATOR_HYPHEN + waybillCode;
            redisClientCache.setEx(key, StringUtils.EMPTY, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("按运单卸车扫描处理异常,参数bizId={},waybillCode={}", bizId, waybillCode, e);
        }
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "dms.web.JyUnloadVehicleCheckTysService.checkIsMeetWaybillStandard", mState = {JProEnum.TP, JProEnum.FunctionError})
    public String checkIsMeetWaybillStandard(Waybill waybill) {
        // 默认使用ucc的配置
        int waybillLimit = uccPropertyConfiguration.getDazongPackageOperateMax();
        int packageNum = waybill.getGoodNumber();
        // 查询运单件数配置表最新的配置记录
        WaybillConfig configPo = waybillConfigDao.findLatestWaybillConfig();
        if (configPo != null  && configPo.getNewValue() != null) {
            // 如果配置表配置过运单件数，则使用运单配置表的限制
            waybillLimit = configPo.getNewValue();
        }
        if (packageNum < waybillLimit) {
            return "该运单总包裹数小于" + waybillLimit + "，请逐包裹进行扫描！";
        }
        return null;
    }


    /**
     * 方法作用： 记录组板扫描的箱类型还是包裹类型：转运场地按包裹按箱不可混装
     * （1）组板时机存储cache：  存在问题：
     *      场景一：开板成功被拦截，没有组板，cache为空
     *      场景二：历史板，没有数据
     *    无法区分以上两个场景，可能出现混装
     * （2）按开板计算
     *     代价： 组板前被拦截的场景，DB可能存在空板
     * @param scanPackageDto
     */
    public void setBoardTypeCache(ScanPackageDto scanPackageDto) {
        String cacheKey= CacheKeyConstants.REDIS_PREFIX_BOARD_SCAN_TYPE + scanPackageDto.getCurrentOperate().getSiteCode() +":" + scanPackageDto.getBoardCode();
        //记录板号扫的包裹还是箱号（冷链医药围板箱组板单独码放，不能和包裹混组）
        if(StringUtils.isNotBlank(redisClientOfJy.get(cacheKey))) {
            return;
        }
        BoardScanTypeDto boardScanTypeDto = new BoardScanTypeDto();
        boardScanTypeDto.setBoard(scanPackageDto.getBoardCode());
        boardScanTypeDto.setFirstScanCode(scanPackageDto.getScanCode());
        boardScanTypeDto.setFirstScanTime(System.currentTimeMillis());
        boardScanTypeDto.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        boardScanTypeDto.setBizId(scanPackageDto.getBizId());
        if(BusinessUtil.isBoxcode(scanPackageDto.getScanCode())) {
            boardScanTypeDto.setBoardType(CacheKeyConstants.BOARD_SCAN_TYPE_BOX);
        }else if(WaybillUtil.isPackageCode(scanPackageDto.getScanCode())) {
            boardScanTypeDto.setBoardType(CacheKeyConstants.BOARD_SCAN_TYPE_PACKAGE);
        }else {
            boardScanTypeDto.setBoardType(CacheKeyConstants.BOARD_SCAN_TYPE_ELSE);
        }
        String msg = JsonUtils.toJSONString(boardScanTypeDto);
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleCheckTysService.setBoardTypeCache-转运卸车岗开板确定板类型redis缓存={}过期时间35天", msg);
        }
        //卸车岗任务完成之后可扫包裹，无时间控制，该过期时间不宜过短
        redisClientOfJy.setEx(cacheKey, msg, 35, TimeUnit.DAYS);
    }

    public BoardScanTypeDto getBoardTypeCache(Integer siteCode, String boardCode) {
        if(siteCode == null || StringUtils.isEmpty(boardCode)) {
            return new BoardScanTypeDto();
        }
        String cacheKey= CacheKeyConstants.REDIS_PREFIX_BOARD_SCAN_TYPE + siteCode +":" + boardCode;
        String cacheValue = redisClientOfJy.get(cacheKey);
        BoardScanTypeDto res = JSONObject.parseObject(cacheValue, BoardScanTypeDto.class);

        //历史板或没有类型的场景都认为是扫包裹的板,强拦截扫描箱号
        if(res == null) {
            res = new BoardScanTypeDto();
        }
        if(StringUtils.isBlank(res.getBoardType())) {
            res.setBoardType(CacheKeyConstants.BOARD_SCAN_TYPE_PACKAGE);
        }
        return res;
    }


    private boolean checkScanBoardType(BoardScanTypeDto boardScanTypeDto, String scanCode) {
        boolean res = false;
        String boardType = boardScanTypeDto.getBoardType();
        if(WaybillUtil.isPackageCode(scanCode)) {
            res = CacheKeyConstants.BOARD_SCAN_TYPE_PACKAGE.equals(boardType);
        }else if(BusinessUtil.isBoxcode(scanCode)) {
            res = CacheKeyConstants.BOARD_SCAN_TYPE_BOX.equals(boardType);
        }
        if(log.isInfoEnabled()) {
            log.info("checkScanBoardType校验扫描板类型不符：当前扫描code={},板类型为{}，开板数据为：{}", scanCode, boardScanTypeDto.getBoardType(), JsonUtils.toJSONString(boardScanTypeDto));
        }
        return res;
    }



    /**
     * 转运卸车处理集齐逻辑
     */
    public void collectDeal(UnloadScanCollectDealDto unloadScanCollectDealDto, InvokeResult<ScanPackageRespDto> invokeResult) {
        if(invokeResult == null) {
            invokeResult = new InvokeResult<>();
        }
        ScanPackageRespDto resData = invokeResult.getData();
        if(resData == null) {
            resData = new ScanPackageRespDto();
        }
        if(uccPropertyConfiguration.getTysUnloadCarCollectDemoteSwitch()) {
            //默认关闭开关，手动开启降级 true
            if(log.isInfoEnabled()) {
                log.info("JyUnloadVehicleCheckTysService.collectDeal：转运集齐功能降级处理中");
            }
            resData.setCollectDemoteSwitch(true);
            return;
        }
        resData.setCollectDemoteSwitch(false);
        String siteWhitelist = uccPropertyConfiguration.getJyCollectSiteWhitelist();
        if(StringUtils.isBlank(siteWhitelist)) {
            if(log.isInfoEnabled()) {
                log.info("转运卸车集齐服务场地白名单未配置，默认走全场， param={}", JsonUtils.toJSONString(unloadScanCollectDealDto));
            }
        }else if(!siteWhitelist.contains(String.format("%s%s%s", ",", unloadScanCollectDealDto.getCurrentOperate().getSiteCode(), ","))){
            if(log.isInfoEnabled()) {
                log.info("转运卸车集齐服务场地白名单未配置当前场地，不做集齐服务处理， param={}，白名单={}", JsonUtils.toJSONString(unloadScanCollectDealDto), siteWhitelist);
            }
            return;
        }

        CallerInfo info = Profiler.registerInfo("DMSWEB.JyUnloadVehicleCheckTysService.collectDeal", false, true);
        try{
            if (!ScanCodeTypeEnum.SCAN_WAYBILL.getCode().equals(unloadScanCollectDealDto.getScanCodeType())
                    && !ScanCodeTypeEnum.SCAN_PACKAGE.getCode().equals(unloadScanCollectDealDto.getScanCodeType())) {
                log.warn("{}非包裹或运单，暂不处理集齐逻辑 unloadScanDto={}", unloadScanCollectDealDto.getScanCode(), JsonUtils.toJSONString(unloadScanCollectDealDto));
                return;
            }
            if(unloadScanCollectDealDto.getManualCreateTaskFlag() == null) {
                JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(unloadScanCollectDealDto.getBizId());
                if(unloadVehicleEntity != null && unloadVehicleEntity.getManualCreatedFlag().equals(1)) {
                    unloadScanCollectDealDto.setManualCreateTaskFlag(true);
                }else {
                    unloadScanCollectDealDto.setManualCreateTaskFlag(false);
                }
            }
            if(log.isInfoEnabled()) {
                log.info("JyUnloadVehicleCheckTysService.collectDeal:转运卸车集齐处理下传参数：{}, 当前结果集数据为{}", JsonUtils.toJSONString(unloadScanCollectDealDto), JsonUtils.toJSONString(invokeResult));
            }
            //自建任务
            if(unloadScanCollectDealDto.getManualCreateTaskFlag()) {
                taskNullCollectDeal(unloadScanCollectDealDto, invokeResult);
            }else {
                sealCarTaskCollectDeal(unloadScanCollectDealDto, invokeResult);
            }
            if(log.isInfoEnabled()) {
                log.info("JyUnloadVehicleCheckTysService.collectDeal:集齐服务处理后结果集数据为{}", JsonUtils.toJSONString(invokeResult));
            }
        }catch (Exception e) {
            log.error("JyUnloadVehicleCheckTysService.collectDeal--转运卸车运单集齐服务异常，该异常存在于卸车主流程，异常报错处理，不卡流程，参数请求对象={}，参数返回对象={},errMsg={}",
                    JsonUtils.toJSONString(unloadScanCollectDealDto), JsonUtils.toJSONString(invokeResult), e.getMessage(), e);
            resData.setUnloadCollectErrWarn("集齐服务处理异常");
            resData.setUnloadCollectDto(null);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 自建任务集齐处理
     */
    private void taskNullCollectDeal(UnloadScanCollectDealDto unloadScanCollectDealDto, InvokeResult<ScanPackageRespDto> invokeResult) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.JyUnloadVehicleCheckTysService.taskNullCollectDeal", false, true);
        //
        String methodDesc = "JyUnloadVehicleCheckTysService.taskNullCollectDeal--转运卸车无任务扫描处理集齐逻辑：";
        ScanPackageRespDto resData = invokeResult.getData();
        if(resData == null) {
            resData = new ScanPackageRespDto();
            invokeResult.setData(resData);
        }
        UnloadCollectDto collectDto = new UnloadCollectDto();
        resData.setUnloadCollectDto(collectDto);

        //自建任务扫描时集齐Model初始化运单下所有包裹，走异步  （consumer保证幂等  场地+封车编码+单号）
        taskNullScanInitCollectSendMq(unloadScanCollectDealDto);
        //修改集齐状态 + 处理返回集齐结果 （按单验初始化时直接修改集齐状态）
        if (ScanCodeTypeEnum.SCAN_WAYBILL.getCode().equals(unloadScanCollectDealDto.getScanCodeType())) {
            collectDto.setCollectType(CollectTypeEnum.TASK_JIQI.getCode());
            collectDto.setCollectStatisticsNum(unloadScanCollectDealDto.getGoodNumber());
            collectDto.setWaybillCode(unloadScanCollectDealDto.getScanCode());
        }else if(ScanCodeTypeEnum.SCAN_PACKAGE.getCode().equals(unloadScanCollectDealDto.getScanCodeType())) {
            //修改扫描code集齐状态、
            if(!jyCollectService.updateSingleCollectStatus(unloadScanCollectDealDto)) {//todo update要支持上面init-consumer没处理时的场景
                log.error("{} 按包裹扫描修改集齐状态失败，param={}", methodDesc, JsonUtils.toJSONString(unloadScanCollectDealDto));
                throw new JyBizException("修改集齐状态失败");
            }
            resData.setUnloadCollectDto(jyCollectService.scanQueryCollectTypeStatistics(unloadScanCollectDealDto));
        }
        Profiler.registerInfoEnd(info);
    }
    /**
     * 封车下发任务集齐处理
     */
    private void sealCarTaskCollectDeal(UnloadScanCollectDealDto unloadScanCollectDealDto, InvokeResult<ScanPackageRespDto> invokeResult) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.JyUnloadVehicleCheckTysService.sealCarTaskCollectDeal", false, true);
        String methodDesc = "JyUnloadVehicleCheckTysService.sealCarTaskCollectDeal--转运卸车下发任务扫描处理集齐逻辑：";

        ScanPackageRespDto resData = invokeResult.getData();
        UnloadCollectDto collectDto = new UnloadCollectDto();
        resData.setUnloadCollectDto(collectDto);

        //判断为在末级别,则发送初始化的MQ，按运单集齐
        CollectDto collectDto1 = new CollectDto();
        collectDto1.setWaybillCode(WaybillUtil.getWaybillCode(unloadScanCollectDealDto.getScanCode()));
        collectDto1.setBizId(unloadScanCollectDealDto.getBizId());
        collectDto1.setCollectNodeSiteCode(unloadScanCollectDealDto.getCurrentOperate().getSiteCode());
        collectDto1.setOperatorErp(unloadScanCollectDealDto.getUser().getUserErp());
        jyCollectService.sealCarWaybillCollectInitSendMq(collectDto1);

        if (ScanCodeTypeEnum.SCAN_WAYBILL.getCode().equals(unloadScanCollectDealDto.getScanCodeType())) {
            //按运单修改集齐状态mq： 异步
            this.updateWaybillCollectStatusSendMq(unloadScanCollectDealDto);
            collectDto.setWaybillCode(unloadScanCollectDealDto.getScanCode());
            collectDto.setCollectType(CollectTypeEnum.TASK_JIQI.getCode());
            collectDto.setCollectStatisticsNum(unloadScanCollectDealDto.getGoodNumber());
        }else if(ScanCodeTypeEnum.SCAN_PACKAGE.getCode().equals(unloadScanCollectDealDto.getScanCodeType())) {
            //修改扫描code集齐状态： 同步
            if(!jyCollectService.updateSingleCollectStatus(unloadScanCollectDealDto)) {
                log.error("按包裹扫描修改集齐状态失败，param={}，res={}", methodDesc, JsonUtils.toJSONString(unloadScanCollectDealDto));
                throw new JyBizException("修改集齐状态失败");
            }
            //查询集齐类型统计
            resData.setUnloadCollectDto(jyCollectService.scanQueryCollectTypeStatistics(unloadScanCollectDealDto));
        }
        Profiler.registerInfoEnd(info);
    }

    /**
     * 按运单修改集齐状态
     */
    private void updateWaybillCollectStatusSendMq(UnloadScanCollectDealDto unloadScanCollectDealDto) {
        BatchUpdateCollectStatusDto mqDto = new BatchUpdateCollectStatusDto();
        mqDto.setBizId(unloadScanCollectDealDto.getBizId());
        mqDto.setOperateTime(System.currentTimeMillis());
        mqDto.setBatchType(CollectBatchUpdateTypeEnum.WAYBILL_BATCH.getCode());
        mqDto.setScanCode(unloadScanCollectDealDto.getScanCode());
        mqDto.setScanSiteCode(unloadScanCollectDealDto.getCurrentOperate().getSiteCode());
        String businessId = mqDto.getBizId() + ":" + mqDto.getScanCode();
        String msg = com.jd.bluedragon.utils.JsonHelper.toJson(mqDto);
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleCheckTysService.taskNullScanInitCollectSendMq无任务扫描发送集齐数据初始化jmq, msg={}", msg);
        }
        //自建任务扫描初始化businessId是bizId + 扫描单号；  封车初始化businessId是bizId
        jyCollectStatusBatchUpdateWaybillSplitProducer.sendOnFailPersistent(businessId, msg);
    }

    /**
     * 生成任务初始化集齐对象，发送初始化jmq
     */
    private void taskNullScanInitCollectSendMq(UnloadScanCollectDealDto unloadScanCollectDealDto) {

        InitCollectDto initCollectDto = new InitCollectDto();
        initCollectDto.setBizId(unloadScanCollectDealDto.getBizId());
        initCollectDto.setOperateTime(System.currentTimeMillis());
        initCollectDto.setOperateNode(CollectInitNodeEnum.NULL_TASK_INIT.getCode());
        initCollectDto.setTaskNullScanCodeType(unloadScanCollectDealDto.getScanCodeType());
        initCollectDto.setTaskNullScanCode(unloadScanCollectDealDto.getScanCode());
        initCollectDto.setTaskNullScanSiteCode(unloadScanCollectDealDto.getCurrentOperate().getSiteCode());
        initCollectDto.setOperatorErp(unloadScanCollectDealDto.getUser().getUserErp());
        initCollectDto.setWaybillCode(WaybillUtil.getWaybillCode(unloadScanCollectDealDto.getScanCode()));
        //自建任务扫描初始化businessId是bizId + 扫描单号+ 扫描类型；  封车初始化businessId是bizId
        StringBuilder sb = new StringBuilder();
        sb.append(initCollectDto.getBizId()).append(":")
                .append(initCollectDto.getTaskNullScanCode()).append(":")
                .append(initCollectDto.getTaskNullScanCodeType()).append(":");
        String businessId = sb.toString();
        String msg = com.jd.bluedragon.utils.JsonHelper.toJson(initCollectDto);
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleCheckTysService.taskNullScanInitCollectSendMq无任务扫描发送集齐数据初始化jmq, businessId={}, msg={}", businessId, msg);
        }
        jyCollectDataInitSplitProducer.sendOnFailPersistent(businessId, msg);
    }
}
