package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.jy.config.WaybillConfig;
import com.jd.bluedragon.distribution.jy.dao.config.WaybillConfigDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadVehicleBoardDao;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageRespDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskStageStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskStageTypeEnum;
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
import com.jd.bluedragon.utils.ObjectHelper;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.common.utils.CacheKeyConstants.*;

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
    private StoragePackageMService storagePackageMService;

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
    @Qualifier("jyUnloadScanProducer")
    private DefaultJMQProducer unloadScanProducer;

    @Resource
    private Cluster redisClientCache;




    /**
     * 操作中心为始发中心+揽收类型为网点自送+运单状态为取消
     */
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

    public void checkPackageOverWeight(DeliveryPackageD packageD, Waybill waybill, ScanPackageRespDto response) {
        String packageWeightLimit = uccPropertyConfiguration.getPackageWeightLimit();
        BigDecimal packageWeight = getPackageWeight(packageD, waybill);
        if (packageWeight != null && packageWeight.compareTo(new BigDecimal(packageWeightLimit)) > 0) {
            log.info("包裹超重:packageCode={},weight={},limit={}", packageD.getPackageBarcode(), packageWeight.toPlainString(), packageWeightLimit);
            Map<String, String> warnMsg = response.getWarnMsg();
            warnMsg.put(UnloadCarWarnEnum.PACKAGE_OVER_WEIGHT_MESSAGE.getLevel(), String.format(UnloadCarWarnEnum.PACKAGE_OVER_WEIGHT_MESSAGE.getDesc(), packageWeight.toPlainString()));
        }
    }

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
     * 判断包裹是否扫描并组板成功
     */
    public void packageIsScanBoard(String bizId, String barCode, String boardCode) throws LoadIllegalException {
        // 拦截的包裹不能重复扫描
        try {
            log.info("packageIsScanBoard-校验拦截缓存【{}】【{}】", barCode, bizId);
            String key = REDIS_PREFIX_SEAL_PACK_INTERCEPT + bizId + Constants.SEPARATOR_HYPHEN + barCode;
            String isExistIntercept = redisClientCache.get(key);
            if (StringUtils.isNotBlank(isExistIntercept)) {
                throw new LoadIllegalException(LoadIllegalException.BORCODE_SEALCAR_INTERCEPT_EXIST_MESSAGE);
            }
        } catch (LoadIllegalException e) {
            throw new LoadIllegalException(e.getMessage());
        }

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
            log.error("获取缓存【{}】异常", "", e);
        }
        if (scanIsSuccess) {
            // 包裹已扫描组板成功则提示拦截
            throw new LoadIllegalException(String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE, barCode, boardCode));
        }
    }

    /**
     * 验货拦截及验货处理
     */
    public void inspectionIntercept(String barCode, Waybill waybill, UnloadScanDto unloadScanDto) throws LoadIllegalException {
        // 加盟商余额校验
        if (allianceBusiDeliveryDetailService.checkExist(waybill.getWaybillCode())
                && !allianceBusiDeliveryDetailService.checkMoney(waybill.getWaybillCode())) {
            throw new LoadIllegalException(LoadIllegalException.ALLIANCE_INTERCEPT_MESSAGE);
        }
        // 验货任务
        unloadScanProducer.sendOnFailPersistent(barCode, JsonHelper.toJson(unloadScanDto));
    }

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
            if (!storagePackageMService.checkWaybillCanSend(waybill.getWaybillCode(), waybillSign)) {
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
            InvokeResult<Board> invokeResult = boardCommonManager.createBoardCode(boardCommonRequest);
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
            return true;

        }
        // 非第一次则校验目的地是否一致
        String waybillCode = WaybillUtil.getWaybillCode(request.getScanCode());
        Integer nextSiteCode;
        Integer destinationId = null;
        try {
            nextSiteCode = request.getNextSiteCode();
            if (nextSiteCode == null) {
                // 此处直接返回，因为ver组板校验链会判断
                return true;
            }
            Response<Board> result = groupBoardManager.getBoard(request.getBoardCode());
            if (result != null && result.getCode() == ResponseEnum.SUCCESS.getIndex() && result.getData() != null) {
                destinationId = result.getData().getDestinationId();
            }
        } catch (Exception e) {
            log.error("运单号【{}】的路由下一跳和板号【{}】目的地校验异常", waybillCode, request.getBoardCode(), e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        if (destinationId == null) {
            throw new LoadIllegalException(LoadIllegalException.BOARD_RECIEVE_EMPEY_INTERCEPT_MESSAGE);
        }
        request.setBoardDestinationId(destinationId);
        if (!nextSiteCode.equals(destinationId)) {
            Map<String, String> warnMsg = response.getWarnMsg();
            warnMsg.put(UnloadCarWarnEnum.FLOW_DISACCORD.getLevel(), UnloadCarWarnEnum.FLOW_DISACCORD.getDesc());
            return false;
        }
        return true;
    }

    /**
     * 是否发货校验
     */
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
    public void packageCountCheck(ScanPackageDto scanPackageDto) {
        Integer unloadBoardBindingsMaxCount = uccPropertyConfiguration.getUnloadBoardBindingsMaxCount();
        boardCommonManager.packageCountCheck(scanPackageDto.getBoardCode(), unloadBoardBindingsMaxCount);
    }

    /**
     * ver组板拦截
     */
    public String boardCombinationCheck(ScanPackageDto request, ScanPackageRespDto result) {
        BoardCommonRequest boardCommonRequest = createBoardCommonRequest(request);
        InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
        if (invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE) {
            if (SortingResponse.CODE_CROUTER_ERROR.equals(invokeResult.getCode())) {
                result.getConfirmMsg().put(invokeResult.getMessage(), invokeResult.getMessage());
                throw new UnloadPackageBoardException(invokeResult.getMessage());
            }
            return invokeResult.getMessage();
        }
        return null;
    }

    private BoardCommonRequest createBoardCommonRequest(ScanPackageDto request) {
        BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
        boardCommonRequest.setBarCode(request.getScanCode());
        boardCommonRequest.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
        boardCommonRequest.setOperateSiteName(request.getCurrentOperate().getSiteName());
        boardCommonRequest.setReceiveSiteCode(request.getNextSiteCode());
        boardCommonRequest.setReceiveSiteName(request.getNextSiteName());
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
                addBoardBox.setBarCodeType(BarCodeTypeEnum.PACKAGE_TYPE.getCode());
            } else if (BusinessHelper.isBoxcode(request.getScanCode())) {
                addBoardBox.setBarCodeType(BarCodeTypeEnum.BOX_TYPE.getCode());
            }
            if (request.getNextSiteCode() != null && !request.getNextSiteCode().equals(request.getBoardDestinationId())) {
                addBoardBox.setFlowDisaccord(1);
            }
            Response<Integer> response = groupBoardManager.addBoxToBoard(addBoardBox);
            if (response == null) {
                log.warn("推组板关系失败!");
                throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
            }
            BoardCommonRequest boardCommonRequest = createBoardCommonRequest(request);
            if (response.getCode() == ResponseEnum.SUCCESS.getIndex()) {
                // 保存任务和板的关系
                JyUnloadVehicleBoardEntity entity = convertUnloadVehicleBoard(request);
                jyUnloadVehicleBoardDao.insertSelective(entity);
                // 设置板上已组包裹数
                result.setComBoardCount(response.getData());
                // 组板成功
                log.info("组板成功、板号:【{}】包裹号:【{}】站点:【{}】", request.getBoardCode(), request.getScanCode(), request.getCurrentOperate().getSiteCode());
                setCacheOfBoardAndPack(request.getBoardCode(), request.getScanCode());
                boxToBoardSuccessAfter(request, null);
                // 组板实操时间多加1秒，防止和验货实操时间相同导致全流程跟踪显示顺序错乱
                boardCommonRequest.setOperateTime(System.currentTimeMillis() + 1000L);
                // 组板全程跟踪
                boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
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
                    // 调用TC的板号转移接口
                    InvokeResult<String> invokeResult = boardCommonManager.boardMove(boardCommonRequest);
                    log.info("组板转移结果【{}】", JsonHelper.toJson(invokeResult));
                    if (invokeResult == null) {
                        throw new LoadIllegalException(LoadIllegalException.BOARD_MOVED_INTERCEPT_MESSAGE);
                    }
                    // 重新组板失败
                    if (invokeResult.getCode() != ResponseEnum.SUCCESS.getIndex()) {
                        log.warn("组板转移失败.原板号【{}】新板号【{}】失败原因【{}】",
                                invokeResult.getData(), request.getBoardCode(), invokeResult.getMessage());
                        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
                    }
                    // 保存任务和板的关系
                    JyUnloadVehicleBoardEntity entity = convertUnloadVehicleBoard(request);
                    jyUnloadVehicleBoardDao.insertSelective(entity);
                    // 设置板上已组包裹数
                    result.setComBoardCount(1);
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
            }
        } catch (Exception e) {
            if (e instanceof UnloadPackageBoardException) {
                throw new UnloadPackageBoardException(String.format(LoadIllegalException.PACKAGE_ALREADY_BIND, boardCode));
            }
            log.warn("推TC组板关系异常，入参【{}】", JsonHelper.toJson(addBoardBox), e);
        }
        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
    }


    private JyUnloadVehicleBoardEntity convertUnloadVehicleBoard(ScanPackageDto scanPackageDto) {
        Date now = new Date();
        JyUnloadVehicleBoardEntity entity = new JyUnloadVehicleBoardEntity();
        entity.setUnloadVehicleBizId(scanPackageDto.getBizId());
        // 获取阶段子任务ID
        String stageBizId = getStageBizId(scanPackageDto);
        if (StringUtils.isNotBlank(stageBizId)) {
            entity.setUnloadVehicleStageBizId(stageBizId);
        }
        if (scanPackageDto.getPrevSiteCode() != null) {
            entity.setStartSiteId(Long.valueOf(scanPackageDto.getPrevSiteCode()));
            entity.setStartSiteName(scanPackageDto.getPrevSiteName());
        }
        entity.setBoardCode(scanPackageDto.getBoardCode());
        entity.setEndSiteId((long) scanPackageDto.getCurrentOperate().getSiteCode());
        entity.setEndSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        entity.setGoodsAreaCode(scanPackageDto.getGoodsAreaCode());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setCreateUserErp(scanPackageDto.getUser().getUserErp());
        entity.setCreateUserName(scanPackageDto.getUser().getUserName());
        entity.setUpdateUserErp(scanPackageDto.getUser().getUserErp());
        entity.setUpdateUserName(scanPackageDto.getUser().getUserName());
        return entity;
    }

    private String getStageBizId(ScanPackageDto scanPackageDto) {
        JyBizTaskUnloadVehicleStageEntity condition =new JyBizTaskUnloadVehicleStageEntity();
        condition.setUnloadVehicleBizId(scanPackageDto.getBizId());
        condition.setType(scanPackageDto.isSupplementary() ? JyBizTaskStageTypeEnum.SUPPLEMENT.getCode() : JyBizTaskStageTypeEnum.HANDOVER.getCode());
        if (!scanPackageDto.isSupplementary()) {
            condition.setStatus(JyBizTaskStageStatusEnum.DOING.getCode());
        }
        JyBizTaskUnloadVehicleStageEntity entity = jyBizTaskUnloadVehicleStageService.queryCurrentStage(condition);
        if (ObjectHelper.isNotNull(entity)){
            return entity.getBizId();
        }
        return null;
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
    public void setCacheOfSealCarAndPackageIntercept(String bizId, String barCode) {
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
    public void setStartSiteForJyUnloadVehicle(ScanPackageDto request, ScanPackageRespDto response,
                                                JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        String key = TYS_UNLOAD_PREFIX_SITE + Constants.SEPARATOR_HYPHEN + Constants.PDA_UNLOAD_TASK_PREFIX
                + Constants.SEPARATOR_HYPHEN + request.getBizId();
        // 如果是无任务卸车
        if (unloadVehicleEntity.getStartSiteId() == null) {
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

    public void assembleReturnData(ScanPackageDto request, ScanPackageRespDto response, JyBizTaskUnloadVehicleEntity unloadVehicleEntity, UnloadScanDto unloadScanDto) {
        response.setSupplementary(unloadScanDto.getSupplementary());
        response.setGoodsAreaCode(request.getGoodsAreaCode());
        if (StringUtils.isNotBlank(unloadVehicleEntity.getStartSiteName())) {
            response.setPrevSiteName(unloadVehicleEntity.getStartSiteName());
            response.setPrevSiteId(unloadVehicleEntity.getStartSiteId());
        } else if (request.getPrevSiteCode() != null) {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getPrevSiteCode());
            if (baseSite != null) {
                request.setPrevSiteName(baseSite.getSiteName());
            }
        }
        if (request.getNextSiteCode() != null) {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(request.getPrevSiteCode());
            if (baseSite != null) {
                request.setNextSiteName(baseSite.getSiteName());
                response.setEndSiteName(baseSite.getSiteName());
            }
        }
    }

    /**
     * 校验是否为KA运单 如果是不支持按大宗操作 66为3 强制拦截
     */
    public boolean checkIsKaWaybill(Waybill waybill) {
        return BusinessUtil.needWeighingSquare(waybill.getWaybillSign());
    }

    /**
     * 判断运单是否重复扫描
     */
    public boolean checkWaybillScanIsRepeat(String bizId, String waybillCode)
            throws LoadIllegalException {
        // 根据运单号获取卸车扫描记录
        String key = REDIS_PREFIX_SEAL_WAYBILL + bizId + Constants.SEPARATOR_HYPHEN + waybillCode;
        String isExistIntercept = redisClientCache.get(key);
        return StringUtils.isNotBlank(isExistIntercept);
    }

    /**
     * 运单验货扫描成功后设置缓存
     */
    public void waybillInspectSuccessAfter(String bizId, String waybillCode) {
        try {
            // 设置运单扫描记录
            String key = REDIS_PREFIX_SEAL_WAYBILL + bizId + Constants.SEPARATOR_HYPHEN + waybillCode;
            redisClientCache.setEx(key, StringUtils.EMPTY, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("按运单卸车扫描处理异常,参数bizId={},waybillCode={}", bizId, waybillCode, e);
        }
    }


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


}
