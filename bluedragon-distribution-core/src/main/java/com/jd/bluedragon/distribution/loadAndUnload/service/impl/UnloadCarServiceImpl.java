package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.unloadCar.*;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.domain.UnloadCarCompleteMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.constants.TransportServiceConstants;
import com.jd.bluedragon.distribution.external.enums.AppVersionEnums;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.service.impl.LoadScanServiceImpl;
import com.jd.bluedragon.distribution.loadAndUnload.*;
import com.jd.bluedragon.distribution.loadAndUnload.dao.*;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.loadAndUnload.*;
import com.jd.bluedragon.distribution.loadAndUnload.constants.UnloadCarConstant;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanRecordDao;
import com.jd.bluedragon.distribution.loadAndUnload.domain.DistributeTaskRequest;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.exception.UnloadPackageBoardException;
import com.jd.bluedragon.distribution.loadAndUnload.neum.UnloadCarWarnEnum;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarCommonService;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarDistributeCommonService;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarTransBoardCommonService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.transfer.service.TransferService;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.coo.ucc.common.utils.JsonUtils;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.tms.basic.dto.BasicVehicleDto;
import com.jd.tms.data.dto.CargoDetailDto;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 卸车任务实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:06
 */
@Service("unloadCarService")
public class UnloadCarServiceImpl implements UnloadCarService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //卸车任务未分配
    private static final int UNLOAD_CAR_UN_DISTRIBUTE = 0;

    //卸车任务已分配
    private static final int UNLOAD_CAR_DISTRIBUTE = 1;

    //所有卸车任务
    private static final int UNLOAD_CAR_ALL = 2;

    /**
     * 成功并提示信息编码
     * */
    private static final Integer CODE_SUCCESS_HIT = 201;

    public static final String UNLOAD_SCAN_LOCK_BEGIN = "UNLOAD_SCAN_LOCK_";

    private final static int WAYBILL_LOCK_TIME = 120;

    /**
     * 快运中心网点编码
     */
    public static final Integer EXPRESS_CENTER_SITE_ID = 6420;

    @Value("${unload.board.bindings.count.max:100}")
    private Integer unloadBoardBindingsMaxCount;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private BoardCommonManager boardCommonManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Autowired
    private UnloadCarCommonService unloadCarDao;

    @Autowired
    private UnloadCarTransBoardCommonService unloadCarTransBoardDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private UnloadCarDistributeCommonService unloadCarDistributionDao;

    @Autowired
    private VosManager vosManager;

    @Autowired
    protected BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private LoadScanServiceImpl loadScanService;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private UnloadScanDao unloadScanDao;

    @Autowired
    private UnloadScanRecordDao unloadScanRecordDao;

    @Autowired
    @Qualifier(value = "unloadCompleteProducer")
    private DefaultJMQProducer unloadCompleteProducer;

    @Resource
    private WaybillStagingCheckManager waybillStagingCheckManager;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    private CargoDetailServiceManager cargoDetailServiceManager;

    @Autowired
    private BoardCombinationService boardCombinationService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration ;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private TransferService transferService;

    @Override
    public InvokeResult<UnloadCarScanResult> getUnloadCarBySealCarCode(String sealCarCode) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        UnloadCarScanResult unloadCarScanResult = new UnloadCarScanResult();
        unloadCarScanResult.setSealCarCode(sealCarCode);
        setPackageCount(unloadCarScanResult);
        result.setData(unloadCarScanResult);
        return result;
    }

    @Override
    public InvokeResult<UnloadCarScanResult> getUnloadScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        if (StringUtils.isEmpty(request.getSealCarCode())) {
            result.parameterError("封车编码不存在!");
            return result;
        }
        if (request.getOperateSiteCode() == null) {
            result.parameterError("操作场地站点ID不存在!");
            return result;
        }
        UnloadCarScanResult unloadCarScanResult = new UnloadCarScanResult();
        unloadCarScanResult.setSealCarCode(request.getSealCarCode());
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            logger.info("卸车扫描1：获取到了大宗权限request={}", JsonHelper.toJson(request));
            unloadCarScanResult.setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }
        setPackageCount(unloadCarScanResult);
        result.setData(unloadCarScanResult);
        return result;
    }

    @Override
    public InvokeResult<UnloadScanDetailDto> unloadScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadScanDetailDto> result = new InvokeResult<>();
        if (StringUtils.isEmpty(request.getSealCarCode())) {
            result.parameterError("封车编码不存在!");
            return result;
        }
        if (request.getOperateSiteCode() == null) {
            result.parameterError("操作场地站点ID不存在!");
            return result;
        }
        UnloadScanDetailDto unloadScanDetailDto = new UnloadScanDetailDto();
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            logger.info("卸车扫描：获取到了大宗权限request={}", JsonHelper.toJson(request));
            unloadScanDetailDto.setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }
        // 获取卸车详细指标
        setUnloadScanDetail(unloadScanDetailDto, request.getSealCarCode());
        result.setData(unloadScanDetailDto);
        return result;
    }

    private void setUnloadScanDetail(UnloadScanDetailDto unloadScanDetailDto, String sealCarCode) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.loadAndUnload.service.impl.UnloadCarServiceImpl.setUnloadScanDetail", false, true);
        try {
            List<UnloadScan> unloadScans = unloadScanDao.findUnloadScanBySealCarCode(sealCarCode);
            if (CollectionUtils.isEmpty(unloadScans)) {
                unloadScanDetailDto.setTotalWaybillNum(0);
                unloadScanDetailDto.setTotalPackageNum(0);
                unloadScanDetailDto.setLoadWaybillAmount(0);
                unloadScanDetailDto.setLoadPackageAmount(0);
                unloadScanDetailDto.setUnloadWaybillAmount(0);
                unloadScanDetailDto.setUnloadPackageAmount(0);
                unloadScanDetailDto.setUnloadScanDtoList(Collections.<UnloadScanDto>emptyList());
                return;
            }
            // 空任务标识
            boolean isEmptyTask = false;
            if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
                isEmptyTask = true;
            }

            unloadScanDetailDto.setTotalWaybillNum(unloadScans.size());
            // 已卸运单数
            int loadWaybillNum = 0;
            // 已卸包裹数
            int loadPackageNum = 0;
            // 封车任务下总包裹数
            int totalPackageNum = 0;
            // 封车任务下多货包裹数
            int surplusPackageNum = 0;
            List<UnloadScanDto> unloadScanDtoList = new ArrayList<>();
            for (UnloadScan unloadScan : unloadScans) {
                Integer forceAmount = unloadScan.getForceAmount();
                Integer surplusAmount = unloadScan.getSurplusAmount();
                Integer loadAmount = unloadScan.getLoadAmount();
                if (forceAmount == null) {
                    forceAmount = 0;
                }
                if (surplusAmount == null) {
                    surplusAmount = 0;
                }
                if (loadAmount == null) {
                    loadAmount = 0;
                }
                // 所有应卸加起来就是总包裹数
                totalPackageNum = totalPackageNum + forceAmount;
                // 转换数据
                unloadScanDtoList.add(convertData(unloadScan, isEmptyTask));
                if (loadAmount == 0 && surplusAmount == 0) {
                    continue;
                }
                // 统计已扫数据
                loadPackageNum = loadPackageNum + loadAmount + surplusAmount;
                // 如果是空任务
                if (isEmptyTask) {
                    // 运单总包裹数 = 多卸数，则已卸单+1
                    if (unloadScan.getPackageAmount().equals(surplusAmount)) {
                        loadWaybillNum = loadWaybillNum + 1;
                    }
                } else {
                    // 正常任务有多扫
                    if (surplusAmount > 0) {
                        // 运单总包裹数 = 已卸数+多卸数，则已卸单+1
                        if (unloadScan.getPackageAmount().equals(loadAmount + surplusAmount)) {
                            loadWaybillNum = loadWaybillNum + 1;
                        }
                        // 统计正常任务下多扫的包裹数
                        surplusPackageNum = surplusPackageNum + surplusAmount;
                        // 正常任务也没有多扫，应卸=已卸，已卸单+1
                    } else if (forceAmount.equals(loadAmount)) {
                        loadWaybillNum = loadWaybillNum + 1;
                    }
                }
            }
            if (isEmptyTask) {
                unloadScanDetailDto.setTotalPackageNum(loadPackageNum);
                unloadScanDetailDto.setUnloadWaybillAmount(0);
                unloadScanDetailDto.setUnloadPackageAmount(0);
            } else {
                unloadScanDetailDto.setTotalPackageNum(totalPackageNum + surplusPackageNum);
                unloadScanDetailDto.setUnloadWaybillAmount(unloadScans.size() - loadWaybillNum);
                unloadScanDetailDto.setUnloadPackageAmount(unloadScanDetailDto.getTotalPackageNum() - loadPackageNum);
            }
            unloadScanDetailDto.setLoadWaybillAmount(loadWaybillNum);
            unloadScanDetailDto.setLoadPackageAmount(loadPackageNum);
            // 按照指定次序排列
            Collections.sort(unloadScanDtoList, new Comparator<UnloadScanDto>() {
                @Override
                public int compare(UnloadScanDto o1, UnloadScanDto o2) {
                    return o2.getStatus() - o1.getStatus();
                }
            });
            unloadScanDetailDto.setUnloadScanDtoList(unloadScanDtoList);
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("处理卸车扫描列表--发生异常,sealCarCode={},e=", sealCarCode, e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private UnloadScanDto convertData(UnloadScan unloadScan, boolean isEmptyTask) {
        UnloadScanDto unloadScanDto = new UnloadScanDto();
        unloadScanDto.setWayBillCode(unloadScan.getWaybillCode());
        unloadScanDto.setPackageAmount(unloadScan.getPackageAmount());
        unloadScanDto.setForceAmount(unloadScan.getForceAmount());
        if (isEmptyTask) {
            Integer surplusAmount = unloadScan.getSurplusAmount();
            if (surplusAmount == null) {
                surplusAmount = 0;
            }
            unloadScanDto.setLoadAmount(surplusAmount);
        } else {
            unloadScanDto.setLoadAmount(unloadScan.getLoadAmount());
        }
        unloadScanDto.setUnloadAmount(unloadScan.getUnloadAmount());
        unloadScanDto.setStatus(unloadScan.getStatus());
        return unloadScanDto;
    }

    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.barCodeScan",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        result.setData(convertToUnloadCarResult(request));
        if (logger.isDebugEnabled()) {
            logger.debug("卸车扫描：参数request={}", JsonHelper.toJson(request));
        }
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(request.getSealCarCode());
            if (unloadCar == null) {
                result.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return result;
            }
            // 包裹是否扫描成功
            packageIsScanBoard(request);
            // 多货包裹标识
            boolean isSurplusPackage = false;
            if(!request.getIsForceCombination()){
                // 验货校验
                inspectionIntercept(request);

                // 获取锁
                if (!lock(request.getSealCarCode(), waybillCode)) {
                    logger.warn("原始包裹卸车扫描接口--获取锁失败：sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
                    result.customMessage(JdCResponse.CODE_FAIL, "多人同时操作该包裹所在的运单，请稍后重试！");
                    return result;
                }
                // 是否多货包裹校验
                isSurplusPackage = surfacePackageCheck(request,result,null);
                // 保存包裹卸车记录和运单暂存
                saveUnloadDetail(request, isSurplusPackage, unloadCar);

                // 路由校验、生成板号
                routerCheck(request,result);
                BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
                BeanUtils.copyProperties(request,boardCommonRequest);
                // 是否发货校验
                boardCommonManager.isSendCheck(boardCommonRequest);
                // 包裹数限制
                boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);

                // ver组板拦截
                InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
                if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    result.customMessage(invokeResult.getCode(),invokeResult.getMessage());

                    return result;
                }
                //拦截校验
                InvokeResult<String> interceptResult = interceptValidateLoadCar(request.getBarCode());
                if(interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                    return result;
                }
            }else {
                isSurplusPackage = surfacePackageCheck(request,result,null);
            }

            if(StringUtils.isEmpty(request.getBoardCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,LoadIllegalException.BOARD_NOTE_EXIST_INTERCEPT_MESSAGE);
                return result;
            }

            // 卸车处理并回传TC组板关系
            dealUnloadAndBoxToBoard(request, isSurplusPackage);

            //设置包裹数
            setPackageCount(result.getData());
            //跨越-增加目的转运中心+自提 校验
            kyexpressCheck(request,waybillCode);
        }catch (LoadIllegalException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,e.getMessage());
            return result;
        }catch (Exception e){
            if (e instanceof UnloadPackageBoardException) {
                result.customMessage(InvokeResult.RESULT_PACKAGE_ALREADY_BIND, e.getMessage());
                return result;
            }
            logger.error("barCodeScan接口发生异常：e=", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        } finally {
            // 释放锁
            unLock(request.getSealCarCode(), waybillCode);
        }
        return result;
    }

    /**
     * 跨越需求 增加目的转运中心+自提校验
     * @param request
     */
    //如果前面有运单信息 无需再次调用接口获取
    private String kyexpressCheck(UnloadCarScanRequest request,String waybillCode) {
        if(logger.isInfoEnabled()){
            logger.info("跨越校验输入参数,{},waybillCode:{}",JsonHelper.toJson(request),waybillCode);
        }
        Integer operateSiteCode = request.getOperateSiteCode();
        //操作所属站点code和目的转运中心code 跨越路由
        boolean isEndSite = waybillService.isStartOrEndSite(operateSiteCode,waybillCode,-1);
        if(isEndSite){
            //获取是否是自提运单
            Waybill waybill = this.waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
            if(waybill != null){
                //判断是否是自提运单
                boolean isPickUpOrNo = BusinessUtil.isPickUpOrNo(waybill.getWaybillSign());
                logger.warn("跨越物流判断,运单号为:{},是否自提为:{}",waybill.getWaybillCode(),isPickUpOrNo);
                if(isPickUpOrNo) return "此单为自提单,请单独处理至自提区";
            }
        }
        return null;
    }


    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.packageCodeScan",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadCarScanResult> packageCodeScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        result.setData(convertToUnloadCarResult(request));
        if (logger.isDebugEnabled()) {
            logger.debug("卸车扫描：参数request={}", JsonHelper.toJson(request));
        }
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            result.getData().setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        try {
            // 如果勾选【包裹号转大宗】
            if (request.getTransfer() != null
                    && GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL.equals(request.getTransfer())) {
                int packageNum = WaybillUtil.getPackNumByPackCode(request.getBarCode());
                logger.info("卸车扫描--勾选【包裹号转大宗】,sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.getData().setPackageSize(packageNum);
                return result;
            }
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(request.getSealCarCode());
            if (unloadCar == null) {
                result.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return result;
            }
            // 包裹是否扫描成功
            packageIsScanBoard(request);
            // 多货包裹标识
            boolean isSurplusPackage = false;
            if(!request.getIsForceCombination()){
                // 验货校验
                inspectionIntercept(request);

                // 获取锁
                if (!lock(request.getSealCarCode(), waybillCode)) {
                    logger.warn("包裹卸车扫描接口--获取锁失败：sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
                    result.customMessage(JdCResponse.CODE_FAIL, "多人同时操作该包裹所在的运单，请稍后重试！");
                    return result;
                }
                // 是否多货包裹校验
                isSurplusPackage = surfacePackageCheck(request,result,null);
                // 保存包裹卸车记录和运单暂存
                saveUnloadDetail(request, isSurplusPackage, unloadCar);


                // 路由校验、生成板号
                routerCheck(request,result);
                BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
                BeanUtils.copyProperties(request,boardCommonRequest);
                // 是否发货校验
                boardCommonManager.isSendCheck(boardCommonRequest);
                // 包裹数限制
                boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);

                // ver组板拦截
                InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
                if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    result.customMessage(invokeResult.getCode(),invokeResult.getMessage());

                    return result;
                }
                //拦截校验
                InvokeResult<String> interceptResult = interceptValidateLoadCar(request.getBarCode());
                if(interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                    return result;
                }
            }else {
                isSurplusPackage = surfacePackageCheck(request,result,null);
            }

            if(StringUtils.isEmpty(request.getBoardCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,LoadIllegalException.BOARD_NOTE_EXIST_INTERCEPT_MESSAGE);
                return result;
            }

            // 卸车处理并回传TC组板关系
            dealUnloadAndBoxToBoard(request,isSurplusPackage);

            //设置包裹数
            setPackageCount(result.getData());
        }catch (LoadIllegalException e){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,e.getMessage());
            return result;
        }catch (Exception e){
            if (e instanceof UnloadPackageBoardException) {
                result.customMessage(InvokeResult.RESULT_PACKAGE_ALREADY_BIND, e.getMessage());
                return result;
            }
            logger.error("packageCodeScan接口发生异常：e=", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        } finally {
            // 释放锁
            unLock(request.getSealCarCode(), waybillCode);
        }
        return result;
    }

    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.packageCodeScanNew",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadScanDetailDto> packageCodeScanNew(UnloadCarScanRequest request) {
        InvokeResult<UnloadScanDetailDto> dtoInvokeResult = new InvokeResult<>();
        UnloadScanDetailDto resData = new UnloadScanDetailDto();
        resData.setWarnMsg(new HashMap<String, String>());//异常消息初始化空对象
        dtoInvokeResult.setData(resData);


        //当前组板号，，后面request中板号在组板转移时替换成了老板号
        String currentBoardCode = request.getBoardCode();


        //任务创建时间变更为第一个包裹的扫描时间
        if (CollectionUtils.isEmpty(unloadScanRecordDao.findRecordBySealCarCode(request.getSealCarCode()))) {
            UnloadCar uc = new UnloadCar();
            Date currTime = new Date();
            uc.setStartTime(currTime);
            uc.setUpdateTime(currTime);
            uc.setUpdateUserErp(request.getOperateUserErp());
            uc.setUpdateUserName(request.getOperateUserName());
            uc.setSealCarCode(request.getSealCarCode());
            try {
                if (unloadCarDao.updateStartTime(uc) < 1) {
                    if (logger.isInfoEnabled()) {
                        logger.error("UnloadCarServiceImpl.packageCodeScanNew--error--首次扫描包裹时修改任务开始时间失败--参数=【{}】", JsonHelper.toJson(uc));
                    }
                    dtoInvokeResult.customMessage(JdCResponse.CODE_FAIL, "当前任务下首次扫描包裹时修改该任务开始时间失败，本次扫描未成功");
                    return dtoInvokeResult;
                }
            } catch (LoadIllegalException ex) {
                logger.error("卸车包裹扫描发生异常，ex=", ex);
                dtoInvokeResult.customMessage(JdCResponse.CODE_FAIL, ex.getMessage());
                return dtoInvokeResult;
            }
        }
        //判断是否是跨越的取消订单
        String kyCancelCheckStr = kyexpressCancelCheck(request);
        if(StringUtils.isNotBlank(kyCancelCheckStr)){
            if(logger.isInfoEnabled()) {
                logger.info("跨越kyexpressCancelCheck-return,request为:{}",JsonHelper.toJson(request));
            }
            dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, kyCancelCheckStr);
            return dtoInvokeResult;
        }
        //流水线模式：只验货不组板
        if(Constants.ASSEMBLY_LINE_TYPE.equals(request.getType())){
            return assemblyLineScan(request, dtoInvokeResult);
        }

        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        result.setData(convertToUnloadCarResult(request));
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            result.getData().setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("卸车扫描：参数request={}", JsonHelper.toJson(request));
        }
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCodeWithStatus(request.getSealCarCode());
            if (unloadCar == null) {
                buildUnloadScanResponse(result.getData(), dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return dtoInvokeResult;
            }
            if (unloadCar.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_UN_START.getType())) {
                BeanUtils.copyProperties(result, dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "任务未开始，请联系卸车负责人开始任务");
                return dtoInvokeResult;
            }
            if (unloadCar.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_END.getType())) {
                BeanUtils.copyProperties(result, dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "任务已结束，扫描失败");
                return dtoInvokeResult;
            }

            // 包裹是否扫描成功
            packageIsScanBoard(request);
            // 多货包裹标识
            boolean isSurplusPackage = false;
            if(!request.getIsForceCombination()){
                // 验货校验
                inspectionIntercept(request);

                // 获取锁
                if (!lock(request.getSealCarCode(), waybillCode)) {
                    logger.warn("新版包裹卸车扫描接口--获取锁失败：sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
                    dtoInvokeResult.customMessage(JdCResponse.CODE_FAIL, "多人同时操作该包裹所在的运单，请稍后重试！");
                    return dtoInvokeResult;
                }
                // 是否多货包裹校验
                isSurplusPackage = surfacePackageCheck(request,result,dtoInvokeResult);
                // 保存包裹卸车记录和运单暂存
                saveUnloadDetail(request, isSurplusPackage, unloadCar);


                //拦截校验
                InvokeResult<String> interceptResult = interceptValidateUnloadCar(request.getBarCode(), dtoInvokeResult);
                if(interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());
                    dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                    return dtoInvokeResult;
                }


                //是否专网标识  专网不组板
                boolean privateNetworkFlag = false;
                if(privateNetworkCheck(waybillCode)) {
                    privateNetworkFlag = true;
                    Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();
                    warnMsg.put(UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getLevel(), UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getDesc());
                }
                boolean tempStorageFlag = false;
                // 增加运单暂存校验，如果支持暂存：只验收包裹、不组板 直接返回提示语
                if (waybillStagingCheckManager.stagingCheck(request.getBarCode(), request.getOperateSiteCode())) {
                    tempStorageFlag = true;
                    Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();
                    warnMsg.put(UnloadCarWarnEnum.PDA_STAGING_CONFIRM_MESSAGE.getLevel(), UnloadCarWarnEnum.PDA_STAGING_CONFIRM_MESSAGE.getDesc());
                }
                //在此处增加跨越校验
                //跨越-增加目的转运中心+自提 校验  dtoInvokeResult返回话术
                String kyResult = kyexpressCheck(request,waybillCode);
                if(StringUtils.isNotBlank(kyResult)){
                    String msg = kyResult;
                    if(logger.isInfoEnabled()) {
                        logger.info("跨越kyexpressCheck-return,waybillCode为:{}",waybillCode);
                    }
                    dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,msg);
                    return dtoInvokeResult;
                }
                if(privateNetworkFlag || tempStorageFlag ){
                    if(logger.isInfoEnabled()) {
                        logger.info("packageCodeScanNew--卸车人工扫描包裹=【{}】，校验是否专网=【{}】, 是否暂存=【{}】",
                                request.getBarCode(), privateNetworkFlag, tempStorageFlag);
                    }
                    return dtoInvokeResult;
                }
                // 路由校验、生成板号
                routerCheck(request,result);
                //开板后获取板号
                currentBoardCode = request.getBoardCode();
                BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
                BeanUtils.copyProperties(request,boardCommonRequest);
                // 是否发货校验
                boardCommonManager.isSendCheck(boardCommonRequest);
                // 包裹数限制
                boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);

                buildUnloadScanResponse(result.getData(), dtoInvokeResult);
                // ver组板拦截
                InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
                if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    dtoInvokeResult.customMessage(invokeResult.getCode(),invokeResult.getMessage());
                    return dtoInvokeResult;
                }
            }else {
                isSurplusPackage = surfacePackageCheck(request,result,dtoInvokeResult);
                buildUnloadScanResponse(result.getData(), dtoInvokeResult);
            }

            if(StringUtils.isEmpty(request.getBoardCode())){
                dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,LoadIllegalException.BOARD_NOTE_EXIST_INTERCEPT_MESSAGE);
                return dtoInvokeResult;
            }

            // 卸车处理并回传TC组板关系
            dealUnloadAndBoxToBoard(request,isSurplusPackage);

            // 获取卸车运单扫描信息
            setUnloadScanDetailList(result.getData(), dtoInvokeResult, request.getSealCarCode());

            //返回组板 单/件数量
            setBoardCount(dtoInvokeResult, currentBoardCode);
        }catch (LoadIllegalException e){
            dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,e.getMessage());
            return dtoInvokeResult;
        }catch (Exception e){
            if (e instanceof UnloadPackageBoardException) {
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PACKAGE_ALREADY_BIND, e.getMessage());
                return dtoInvokeResult;
            }
            logger.error("packageCodeScanNew接口发生异常：e=", e);
            dtoInvokeResult.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        } finally {
            // 释放锁
            unLock(request.getSealCarCode(), waybillCode);
        }
        return dtoInvokeResult;
    }

    /**
     *  卸车扫描结果组装
     * @param scanResult
     * @param dtoInvokeResult
     */
    private void buildUnloadScanResponse(UnloadCarScanResult scanResult, InvokeResult<UnloadScanDetailDto> dtoInvokeResult) {
        if(scanResult == null) {
            return;
        }
        UnloadScanDetailDto resData = dtoInvokeResult.getData();
        if(resData == null) {
            resData = new UnloadScanDetailDto();
            BeanUtils.copyProperties(scanResult, resData);
        }else {
            resData.setWaybillAuthority(scanResult.getWaybillAuthority());
            resData.setBoardCode(scanResult.getBoardCode());
            resData.setReceiveSiteName(scanResult.getReceiveSiteName());
            resData.setReceiveSiteCode(scanResult.getReceiveSiteCode());
            resData.setSealCarCode(scanResult.getSealCarCode());
        }
        dtoInvokeResult.setData(resData);
    }
    //获取组板包裹数
    private void setBoardCount(InvokeResult<UnloadScanDetailDto> dtoInvokeResult, String boardCode) {
        if(StringUtils.isNotBlank(boardCode)) {
            Response<List<String>>  tcResponse = boardCombinationService.getBoxesByBoardCode(boardCode);
            if(tcResponse == null){
                logger.error("UnloadCarServiceImpl.setBoardCount--组板组件返回结果为空！,板号=【{}】", boardCode);
            }else if(com.jd.ql.dms.common.domain.JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())){
                //查询成功
                if(tcResponse.getData() != null && !tcResponse.getData().isEmpty()){
                    Integer boxCount = tcResponse.getData().size();
                    Set waybillSet = new HashSet();
                    for (String packageCode : tcResponse.getData()){
                        waybillSet.add(WaybillUtil.getWaybillCode(packageCode));
                    }
                    UnloadScanDetailDto resData = dtoInvokeResult.getData();
                    if(resData == null) {
                        resData = new UnloadScanDetailDto();
                        resData.setBoardBoxCount(boxCount);
                        resData.setBoardWaybillCount(waybillSet.size());
                        dtoInvokeResult.setData(resData);
                    }else {
                        resData.setBoardBoxCount(boxCount);
                        resData.setBoardWaybillCount(waybillSet.size());
                    }
                    //todo test log zhengchengfa
                    logger.info("test--log--20210420--组板包裹数={}，组板运单数={}，组板包裹信息=【{}】,组板运单信息=【{}】",
                            boxCount, waybillSet.size(), JsonHelper.toJson(tcResponse.getData()), JsonHelper.toJson(waybillSet));
                }else{
                    if(logger.isInfoEnabled()) {
                        logger.info("UnloadCarServiceImpl.setBoardCount--未查询到板号明细,板号=【{}】", boardCode);
                    }
                }
            }else {
                logger.error("UnloadCarServiceImpl.setBoardCount-根据板号查询组板明细错误--error--！,板号=【{}】, 错误信息=【{}】",
                        boardCode, tcResponse.getMesseage());
            }
        }else {
            logger.error("UnloadCarServiceImpl.setBoardCount--板号为空，无法获取板箱/板单件数！,dtoInvokeResult=【{}】", JsonHelper.toJson(dtoInvokeResult));
        }
    }

    /**
     * 获取每次扫描后的卸车扫描列表
     * @param scanResult 初始返回对象
     * @param dtoInvokeResult 新版返回对象
     * @param sealCarCode 封车编码
     */
    private void setUnloadScanDetailList(UnloadCarScanResult scanResult, InvokeResult<UnloadScanDetailDto> dtoInvokeResult,
                                         String sealCarCode) {
        UnloadScanDetailDto resData = dtoInvokeResult.getData();
        if(resData == null) {
            resData = new UnloadScanDetailDto();
            BeanUtils.copyProperties(scanResult, resData);
        }else {
            resData.setWaybillAuthority(scanResult.getWaybillAuthority());
            resData.setBoardCode(scanResult.getBoardCode());
            resData.setReceiveSiteName(scanResult.getReceiveSiteName());
            resData.setReceiveSiteCode(scanResult.getReceiveSiteCode());
            resData.setSealCarCode(scanResult.getSealCarCode());
        }
        setUnloadScanDetail(resData, sealCarCode);
        dtoInvokeResult.setData(resData);
    }

    private void saveUnloadDetail(UnloadCarScanRequest request, boolean isSurplusPackage, UnloadCar unloadCar) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.loadAndUnload.service.impl.UnloadCarServiceImpl.saveUnloadDetail", false, true);
        try {
            // 转板时不再重复暂存
            if (Constants.IS_COMBITION_TRANSFER.equals(request.getIsCombinationTransfer())) {
                return;
            }
            // 卸车扫描包裹重复性校验
            UnloadScanRecord unloadScanRecord = unloadScanRecordDao.findRecordBySealAndPackCode(request.getSealCarCode(),
                    request.getBarCode());
            if (unloadScanRecord != null) {
                return;
            }
            // 包裹暂存
            UnloadScanRecord newUnloadRecord = createUnloadScanRecord(request.getBarCode(), request.getSealCarCode(),
                    request.getTransfer(), null, null, isSurplusPackage, null, request, unloadCar);
            unloadScanRecordDao.insert(newUnloadRecord);

            boolean flowDisAccord = false;
            if (request.getSealCarCode().startsWith(Constants.PDA_UNLOAD_TASK_PREFIX) || isSurplusPackage) {
                flowDisAccord = true;
            }
            // 运单暂存
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            int packageNum = WaybillUtil.getPackNumByPackCode(request.getBarCode());
            UnloadScan unloadScan = unloadScanDao.findUnloadBySealAndWaybillCode(request.getSealCarCode(), waybillCode);
            // 运单之前操作过
            if (unloadScan != null) {
                if (flowDisAccord) {
                    Integer surplusAmount = unloadScan.getSurplusAmount();
                    if (surplusAmount == null) {
                        surplusAmount = 0;
                    }
                    unloadScan.setSurplusAmount(surplusAmount + 1);
                } else {
                    unloadScan.setLoadAmount(unloadScan.getLoadAmount() + 1);
                }
                // 有应卸才有未卸
                if (unloadScan.getForceAmount() > 0) {
                    unloadScan.setUnloadAmount(unloadScan.getForceAmount() - unloadScan.getLoadAmount());
                }
                unloadScan.setPackageAmount(packageNum);
                unloadScan.setUpdateTime(new Date());
                unloadScan.setUpdateUserName(request.getOperateUserName());
                unloadScan.setUpdateUserErp(request.getOperateUserErp());
                int status = getWaybillStatus(unloadScan);
                unloadScan.setStatus(status);
                if (unloadScan.getWeight() <= 0) {
                    // 设置运单重量和体积
                    setWeightAndVolume(unloadScan);
                }
                unloadScanDao.updateByPrimaryKey(unloadScan);
            } else {
                // 运单之前没有操作过
                int forceAmount = 0;
                List<String> taskPackages = searchAllPackageByWaybillCode(request.getSealCarCode(), waybillCode);
                if (CollectionUtils.isNotEmpty(taskPackages)) {
                    forceAmount = taskPackages.size();
                }
                UnloadScan newUnload = createUnloadScan(request.getBarCode(), request.getSealCarCode(), forceAmount,
                        1, request.getOperateUserName(), request.getOperateUserErp(), flowDisAccord);
                unloadScanDao.insert(newUnload);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("包裹卸车扫描--运单暂存和插入卸车包裹记录表发生异常,packageCode={},sendCode={},sealCarCode={},isSurplus={},e=",
                    request.getBarCode(), null, request.getSealCarCode(), isSurplusPackage, e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private void batchSaveUnloadDetail(List<String> packageList, List<String> surplusPackages, UnloadCarScanRequest request,
                                       UnloadCar unloadCar, String waybillCode) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.loadAndUnload.service.impl.UnloadCarServiceImpl.batchSaveUnloadDetail", false, true);
        try {
            boolean flowDisAccord = false;
            // 获取运单下一路由
            Integer nextSiteCode = boardCommonManager.getNextSiteCodeByRouter(waybillCode, request.getOperateSiteCode());
            // 根据站点ID获取站点名称
            String nextSiteName = null;
            if (nextSiteCode != null) {
                BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(nextSiteCode);
                if (baseSiteInfoDto != null) {
                    nextSiteName = baseSiteInfoDto.getSiteName();
                }
            }
            // 正常包裹集合 = 运单包裹总集合 - 多货包裹集合
            List<String> normalPackages = ListUtils.subtract(packageList, surplusPackages);
            UnloadScanRecord unloadScanRecord = createUnloadScanRecord(request.getBarCode(), request.getSealCarCode(),
                    request.getTransfer(), nextSiteCode, nextSiteName, false, null, request, unloadCar);
            // 先保存正常的包裹集合
            if (CollectionUtils.isNotEmpty(normalPackages)) {
                // 每一批次200条
                List<List<String>> packageCodeList = ListUtils.partition(normalPackages, 200);
                for (List<String> packList : packageCodeList) {
                    unloadScanRecord.setPackageCodeList(packList);
                    unloadScanRecordDao.batchInsertByWaybill(unloadScanRecord);
                }
            }
            // 再保存多货包裹记录
            if (CollectionUtils.isNotEmpty(surplusPackages)) {
                // 只要此运单上有多货包裹，就是多扫状态
                flowDisAccord = true;
                unloadScanRecord.setFlowDisaccord(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y);
                // 每一批次200条
                List<List<String>> packageCodeList = ListUtils.partition(surplusPackages, 200);
                for (List<String> packList : packageCodeList) {
                    unloadScanRecord.setPackageCodeList(packList);
                    unloadScanRecordDao.batchInsertByWaybill(unloadScanRecord);
                }
            }

            // 空任务下都算多扫
            if (request.getSealCarCode().startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
                flowDisAccord = true;
            }
            // 保存运单暂存记录
            int packageNum = WaybillUtil.getPackNumByPackCode(request.getBarCode());
            UnloadScan unloadScan = unloadScanDao.findUnloadBySealAndWaybillCode(request.getSealCarCode(), waybillCode);
            if (unloadScan == null) {
                unloadScan = createUnloadScan(request.getBarCode(), request.getSealCarCode(), normalPackages.size(),
                        packageNum, request.getOperateUserName(), request.getOperateUserErp(), flowDisAccord);
                unloadScanDao.insert(unloadScan);
            } else {
                Integer loadAmount = unloadScan.getLoadAmount();
                if (loadAmount == null) {
                    loadAmount = 0;
                }
                int normalPackageListSize = 0;
                if (CollectionUtils.isNotEmpty(normalPackages)) {
                    normalPackageListSize = normalPackages.size();
                }
                unloadScan.setLoadAmount(loadAmount + normalPackageListSize);
                Integer surplusAmount = unloadScan.getSurplusAmount();
                if (surplusAmount == null) {
                    surplusAmount = 0;
                }
                int surplusPackageListSize = 0;
                if (CollectionUtils.isNotEmpty(surplusPackages)) {
                    surplusPackageListSize = surplusPackages.size();
                }
                unloadScan.setSurplusAmount(surplusAmount + surplusPackageListSize);
                unloadScan.setUnloadAmount(0);
                unloadScan.setPackageAmount(packageNum);
                // 设置状态
                int status = getWaybillStatus(unloadScan);
                unloadScan.setStatus(status);
                if (unloadScan.getWeight() <= 0) {
                    // 设置运单重量和体积
                    setWeightAndVolume(unloadScan);
                }
                unloadScanDao.updateByPrimaryKey(unloadScan);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("大宗卸车扫描--运单暂存和批量插入卸车包裹记录表发生异常,waybillCode={},sendCode={},sealCarCode={},e=",
                    waybillCode, null, request.getSealCarCode(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }



    /**
     * 获取运单状态
     * @param unloadScan  卸车扫描运单暂存对象
     * @return 状态
     */
    private Integer getWaybillStatus(UnloadScan unloadScan) {
        Integer packageAmount = unloadScan.getPackageAmount();
        Integer forceAmount = unloadScan.getForceAmount();
        Integer loadAmount = unloadScan.getLoadAmount();
        Integer surplusAmount = unloadScan.getSurplusAmount();

        if (forceAmount == null) {
            forceAmount = 0;
        }
        if (loadAmount == null) {
            loadAmount = 0;
        }
        if (surplusAmount == null) {
            surplusAmount = 0;
        }
        // 如果是多扫
        if (surplusAmount > 0) {
            // 已卸+多卸=总包裹数
            if (packageAmount.equals(loadAmount + surplusAmount)) {
                return GoodsLoadScanConstants.UNLOAD_SCAN_YELLOW;
            } else {
                return GoodsLoadScanConstants.UNLOAD_SCAN_ORANGE;
            }
        } else {
            int unloadAmount = forceAmount - loadAmount;
            // 已卸和未卸都大于0  -- 没扫齐 -- 红色
            if (loadAmount > 0 && unloadAmount > 0) {
                return GoodsLoadScanConstants.UNLOAD_SCAN_RED;
            }
            // 应卸=已卸，并且未卸=0
            if (forceAmount.equals(loadAmount) && unloadAmount == 0) {
                return GoodsLoadScanConstants.UNLOAD_SCAN_GREEN;
            }
        }
        return GoodsLoadScanConstants.UNLOAD_SCAN_BLANK;
    }

    private UnloadScan createUnloadScan(String packageCode, String sealCarCode, Integer forceAmount, Integer loadAmount,
                                        String userName, String userErp, boolean flowDisAccord) {
        UnloadScan unloadScan = new UnloadScan();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        int packageAmount = WaybillUtil.getPackNumByPackCode(packageCode);
        unloadScan.setWaybillCode(waybillCode);
        unloadScan.setSealCarCode(sealCarCode);
        unloadScan.setPackageAmount(packageAmount);
        unloadScan.setForceAmount(forceAmount);
        if (flowDisAccord) {
            unloadScan.setSurplusAmount(loadAmount);
            unloadScan.setLoadAmount(0);
        } else {
            unloadScan.setLoadAmount(loadAmount);
            unloadScan.setSurplusAmount(0);
        }
        if (forceAmount != null && forceAmount != 0) {
            unloadScan.setUnloadAmount(forceAmount > loadAmount ? forceAmount - loadAmount : 0);
        } else {
            unloadScan.setUnloadAmount(0);
        }

        // 空任务的应卸和未卸都是0
        if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            unloadScan.setForceAmount(0);
            unloadScan.setUnloadAmount(0);
        }
        // 设置状态
        int status = getWaybillStatus(unloadScan);
        // 设置运单重量和体积
        if (StringUtils.isNotBlank(waybillCode)) {
            setWeightAndVolume(unloadScan);
        } else {
            unloadScan.setWeight(0d);
            unloadScan.setVolume(0d);
        }
        unloadScan.setStatus(status);
        unloadScan.setYn(Constants.YN_YES);
        unloadScan.setCreateTime(new Date());
        unloadScan.setUpdateTime(new Date());
        unloadScan.setCreateUserName(userName);
        unloadScan.setCreateUserErp(userErp);
        unloadScan.setUpdateUserName(userName);
        unloadScan.setUpdateUserErp(userErp);
        return unloadScan;
    }

    private UnloadScanRecord createUnloadScanRecord(String packageCode, String sealCarCode, Integer transfer, Integer nextSiteCode,
                                       String nextSiteName, boolean flowDisAccord, String batchCode, UnloadCarScanRequest request, UnloadCar unloadCar) {
        UnloadScanRecord unloadScanRecord = new UnloadScanRecord();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        unloadScanRecord.setTaskId(unloadCar.getUnloadCarId());
        unloadScanRecord.setSealCarCode(sealCarCode);
        unloadScanRecord.setLicenseNumber(unloadCar.getVehicleNumber());
        unloadScanRecord.setWaybillCode(waybillCode);
        unloadScanRecord.setPackageCode(packageCode);
        // 不组板的情况下使用此下一网点参数
        if (nextSiteCode != null) {
            unloadScanRecord.setEndSiteCode(nextSiteCode);
            unloadScanRecord.setEndSiteName(nextSiteName);
        } else {
            // 组板的情况下使用此下一网点参数
            unloadScanRecord.setEndSiteCode(request.getReceiveSiteCode());
            unloadScanRecord.setEndSiteName(request.getReceiveSiteName());
        }
        unloadScanRecord.setBatchCode(batchCode);
        unloadScanRecord.setCreateSiteCode(request.getOperateSiteCode());
        unloadScanRecord.setCreateSiteName(request.getOperateSiteName());
        // 大宗标识
        unloadScanRecord.setTransfer(transfer == null ? 0 : transfer);
        // 多扫标识
        if (flowDisAccord) {
            unloadScanRecord.setFlowDisaccord(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y);
        } else {
            unloadScanRecord.setFlowDisaccord(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_N);
        }
        unloadScanRecord.setYn(Constants.YN_YES);
        unloadScanRecord.setCreateTime(new Date());
        unloadScanRecord.setUpdateTime(new Date());
        unloadScanRecord.setCreateUserName(request.getOperateUserName());
        unloadScanRecord.setCreateUserErp(request.getOperateUserErp());
        unloadScanRecord.setCreateUserCode(request.getOperateUserCode());
        unloadScanRecord.setUpdateUserName(request.getOperateUserName());
        unloadScanRecord.setUpdateUserErp(request.getOperateUserErp());
        unloadScanRecord.setUpdateUserCode(request.getOperateUserCode());
        return unloadScanRecord;
    }

    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.waybillScan",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadCarScanResult> waybillScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        result.setData(convertToUnloadCarResult(request));
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            result.getData().setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }
        String packageCode = request.getBarCode();
        String sealCarCode = request.getSealCarCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        request.setWaybillCode(waybillCode);

        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(request.getSealCarCode());
            if (unloadCar == null) {
                result.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return result;
            }
            // 获取锁
            if (!lock(sealCarCode, waybillCode)) {
                logger.warn("大宗批量卸车扫描接口--获取锁失败：sealCarCode={},packageCode={}", sealCarCode, packageCode);
                result.setCode(JdCResponse.CODE_FAIL);
                result.setMessage("多人同时操作该包裹所在的运单，请稍后重试！");
                return result;
            }
            // 运单是否扫描成功
            if (checkWaybillScanIsRepeat(request, result)) {
                return result;
            }
            // 验货校验--发验货消息及全流程跟踪
            inspectionIntercept(request);

            // 调用运单接口获取包裹集合
            List<String> packageList = getPackageCodesByWaybillCode(waybillCode);
            if (CollectionUtils.isEmpty(packageList)) {
                logger.error("运单卸车扫描--根据运单号查询包裹集合返回空:sealCarCode={},packageCode={}", sealCarCode, packageCode);
                result.setCode(JdCResponse.CODE_FAIL);
                result.setMessage("根据运单号查询包裹集合返回空");
                return result;
            }
            // 过滤运单上的重复数据，筛选出有效的包裹
            List<UnloadScanRecord> recordList = unloadScanRecordDao.findRecordsBySealAndWaybillCode(request.getSealCarCode(), waybillCode);
            List<String> loadPackages;
            if (CollectionUtils.isNotEmpty(recordList)) {
                loadPackages = new ArrayList<>();
                for (UnloadScanRecord scanRecord : recordList) {
                    loadPackages.add(scanRecord.getPackageCode());
                }
                packageList = ListUtils.subtract(packageList, loadPackages);
                if (CollectionUtils.isEmpty(packageList)) {
                    logger.error("运单卸车扫描--该运单属于重复扫:sealCarCode={},packageCode={}", sealCarCode, packageCode);
                    result.setCode(JdCResponse.CODE_FAIL);
                    result.setMessage("该运单属于重复扫");
                    return result;
                }
            }

            // 筛选出多货包裹
            List<String> surplusPackages;
            if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
                surplusPackages = packageList;
            } else {
                surplusPackages = getSurplusPackageCodes(packageList, sealCarCode, waybillCode);
            }

            // 批量保存卸车包裹明细和运单明细
            batchSaveUnloadDetail(packageList, surplusPackages, request, unloadCar, waybillCode);

            // B网快运发货规则校验
            InvokeResult<String> interceptResult = interceptValidateLoadCar(packageCode);
            if (interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
                logger.error("运单卸车扫描--B网快运发货规则校验错误:error={},sealCarCode={},packageCode={}", interceptResult.getMessage(), sealCarCode, packageCode);
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                return result;
            }

            // 计算最新的包裹明细
            waybillInspectSuccessAfter(request, packageList.size(), surplusPackages);
            // 设置包裹数
            setPackageCount(result.getData());
        } catch (LoadIllegalException e) {
            logger.error("运单卸车扫描--发生异常:sealCarCode={},packageCode={},error=", sealCarCode, packageCode, e);
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("运单卸车扫描--发生异常:sealCarCode={},packageCode={},error=", sealCarCode, packageCode, e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        } finally {
            // 释放锁
            unLock(sealCarCode, waybillCode);
        }
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        return result;
    }

    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.waybillScanNew",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadScanDetailDto> waybillScanNew(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        result.setData(convertToUnloadCarResult(request));
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            result.getData().setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }

        //初始化返回必要字段，避免后续多处使用NPE
        InvokeResult<UnloadScanDetailDto> invokeResult = new InvokeResult<>();
        UnloadScanDetailDto resData = new UnloadScanDetailDto();
        resData.setWarnMsg(new HashMap<String, String>());//异常消息初始化空对象
        invokeResult.setData(resData);


        String packageCode = request.getBarCode();
        String sealCarCode = request.getSealCarCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        request.setWaybillCode(waybillCode);

        try {
            int packageNum = WaybillUtil.getPackNumByPackCode(request.getBarCode());
            if(packageNum < uccPropertyConfiguration.getDazongPackageOperateMax()){
                invokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "此单非大宗超量运单，请进行逐包裹扫描操作！");
                return invokeResult;
            }
            //校验是否为KA运单 如果是不支持按大宗操作 66为3 强制拦截
            JdVerifyResponse jdVerifyResponse = loadScanService.checkIsKaWaybillOrNo(waybillCode,packageCode);
            if(!Objects.equals(JdVerifyResponse.CODE_SUCCESS,jdVerifyResponse.getCode())){
                invokeResult.setCode(jdVerifyResponse.getCode());
                invokeResult.setMessage(jdVerifyResponse.getMessage());
                return invokeResult;
            }

            UnloadCar unloadCar = unloadCarDao.selectBySealCarCodeWithStatus(request.getSealCarCode());
            if (unloadCar == null) {
                invokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return invokeResult;
            }
            if (unloadCar.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_UN_START.getType())) {
                invokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "任务未开始，请联系卸车负责人开始任务");
                return invokeResult;
            }
            if (unloadCar.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_END.getType())) {
                invokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "任务已结束，扫描失败");
                return invokeResult;
            }

            // 获取锁
            if (!lock(sealCarCode, waybillCode)) {
                logger.warn("大宗批量卸车扫描接口--获取锁失败：sealCarCode={},packageCode={}", sealCarCode, packageCode);
                invokeResult.setCode(JdCResponse.CODE_FAIL);
                invokeResult.setMessage("多人同时操作该包裹所在的运单，请稍后重试！");
                return invokeResult;
            }
            // 运单是否扫描成功
            if (checkWaybillScanIsRepeat(request, result)) {
                BeanUtils.copyProperties(result, invokeResult);
                return invokeResult;
            }
            // 验货校验--发验货消息及全流程跟踪
            inspectionIntercept(request);

            // 调用运单接口获取包裹集合
            List<String> packageList = getPackageCodesByWaybillCode(waybillCode);
            if (CollectionUtils.isEmpty(packageList)) {
                logger.error("运单卸车扫描--根据运单号查询包裹集合返回空:sealCarCode={},packageCode={}", sealCarCode, packageCode);
                invokeResult.setCode(JdCResponse.CODE_FAIL);
                invokeResult.setMessage("根据运单号查询包裹集合返回空");
                return invokeResult;
            }

            // 过滤运单上的重复数据，筛选出有效的包裹
            List<UnloadScanRecord> recordList = unloadScanRecordDao.findRecordsBySealAndWaybillCode(request.getSealCarCode(), waybillCode);
            List<String> loadPackages;
            if (CollectionUtils.isNotEmpty(recordList)) {
                loadPackages = new ArrayList<>();
                for (UnloadScanRecord scanRecord : recordList) {
                    loadPackages.add(scanRecord.getPackageCode());
                }
                packageList = ListUtils.subtract(packageList, loadPackages);
                if (CollectionUtils.isEmpty(packageList)) {
                    logger.error("运单卸车扫描--该运单属于重复扫:sealCarCode={},packageCode={}", sealCarCode, packageCode);
                    invokeResult.setCode(JdCResponse.CODE_FAIL);
                    invokeResult.setMessage("该运单属于重复扫");
                    return invokeResult;
                }
            }

            // 筛选出多货包裹
            List<String> surplusPackages;
            if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
                surplusPackages = packageList;
            } else {
                surplusPackages = getSurplusPackageCodes(packageList, sealCarCode, waybillCode);
            }

            // 批量保存卸车包裹明细和运单明细
            batchSaveUnloadDetail(packageList, surplusPackages, request, unloadCar, waybillCode);

            //专网判断
            Boolean privateNetworkFlag = privateNetworkCheck(waybillCode, invokeResult, GoodsLoadScanConstants.BUSINESS_DIMENSION_WAYBILLCODE);
            /**新增暂存校验**/
            boolean tempStorageFlag = false;
            if (waybillStagingCheckManager.stagingCheck(packageCode, request.getOperateSiteCode())) {
                tempStorageFlag = true;
                Map<String, String> warnMsg = invokeResult.getData().getWarnMsg();
                warnMsg.put(UnloadCarWarnEnum.PDA_STAGING_CONFIRM_MESSAGE.getLevel(), UnloadCarWarnEnum.PDA_STAGING_CONFIRM_MESSAGE.getDesc());
            }
            if(privateNetworkFlag || tempStorageFlag) {
                if(logger.isInfoEnabled()) {
                    logger.info("waybillScanNew--卸车扫运单=【{}】，校验是否专网=【{}】, 是否暂存=【{}】", waybillCode, privateNetworkFlag, tempStorageFlag);
                }
//                return invokeResult;
            }

            // B网快运发货规则校验
            InvokeResult<String> interceptResult = interceptValidateUnloadCar(packageCode, invokeResult);
            if (interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
                logger.error("运单卸车扫描--B网快运发货规则校验错误:error={},sealCarCode={},packageCode={}", interceptResult.getMessage(), sealCarCode, packageCode);
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                return invokeResult;
            }

            // 计算最新的包裹明细
            waybillInspectSuccessAfter(request, packageList.size(), surplusPackages);
            // 获取卸车运单扫描信息
            setUnloadScanDetailList(result.getData(), invokeResult, request.getSealCarCode());
            invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);

        } catch (LoadIllegalException e) {
            logger.error("运单卸车扫描--发生异常:sealCarCode={},packageCode={},error=", sealCarCode, packageCode, e);
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
            return invokeResult;
        } catch (Exception e) {
            logger.error("运单卸车扫描--发生异常:sealCarCode={},packageCode={},error=", sealCarCode, packageCode, e);
            invokeResult.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
            return invokeResult;
        } finally {
            // 释放锁
            unLock(sealCarCode, waybillCode);
        }

        return invokeResult;
    }


    private List<String> getPackageCodesByWaybillCode(String waybillCode) {
        String key = CacheKeyConstants.CACHE_KEY_WAYBILL_PACKAGE_CODES + waybillCode;
        List<String> packageCodes = jimdbCacheService.getList(key, String.class);
        // 如果缓存为空，从远程获取
        if (CollectionUtils.isEmpty(packageCodes)) {
            packageCodes = waybillPackageManager.getWaybillPackageCodes(waybillCode);
            // 如果不为空，缓存7天
            if (CollectionUtils.isNotEmpty(packageCodes)) {
                jimdbCacheService.setEx(key, packageCodes, 7, TimeUnit.DAYS);
                return packageCodes;
            }
        }
        return packageCodes;
    }



    /**
     * 判断包裹是否扫描并组板成功
     * @param request
     * @throws LoadIllegalException
     */
    private void packageIsScanBoard(UnloadCarScanRequest request) throws LoadIllegalException {
        String sealCarCode = request.getSealCarCode();
        String boardCode = request.getBoardCode();
        String packageCode = request.getBarCode();

        //拦截的包裹不能重复扫描
        try {
            logger.info("packageIsScanBoard-校验拦截缓存【"+packageCode+"】【"+sealCarCode+"】");
            String key = CacheKeyConstants.REDIS_PREFIX_SEAL_PACK_INTERCEPT + sealCarCode + Constants.SEPARATOR_HYPHEN + packageCode;
            String isExistIntercept = redisClientCache.get(key);
            if(StringUtils.isNotBlank(isExistIntercept)){
                throw new LoadIllegalException(LoadIllegalException.BORCODE_SEALCAR_INTERCEPT_EXIST_MESSAGE);
            }
        }catch (LoadIllegalException e){
            throw new LoadIllegalException(e.getMessage());
        }

        boolean isSurfacePackage = false;
        try {
            // 是否多货包裹
            isSurfacePackage = isSurfacePackage(sealCarCode, packageCode);
        }catch (LoadIllegalException e){
            throw new LoadIllegalException(e.getMessage());
        }

        if(StringUtils.isEmpty(request.getBoardCode())){
            return;
        }
        boolean scanIsSuccess = false;
        String scanIsSuccessStr = null;
        try {
            String key = CacheKeyConstants.REDIS_PREFIX_BOARD_PACK + boardCode + Constants.SEPARATOR_HYPHEN + packageCode;
            scanIsSuccessStr = redisClientCache.get(key);
            scanIsSuccess = Boolean.parseBoolean(scanIsSuccessStr);
        }catch (Exception e){
            logger.error("获取缓存【{}】异常","",e);
        }

        if(scanIsSuccess){
            // 包裹已扫描组板成功则提示拦截
            throw new LoadIllegalException(String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE,packageCode,boardCode));
        }
    }
    //操作中心为始发中心+揽收类型为网点自送+运单状态为取消
    private String kyexpressCancelCheck(UnloadCarScanRequest request)
    {
        if(logger.isInfoEnabled()){
            logger.info("跨越校验是否是取消订单函数输入参数:{}", JsonHelper.toJson(request));
        }
        Integer operateSiteCode = request.getOperateSiteCode();
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        //路由的第一站
        boolean isStartSite = waybillService.isStartOrEndSite(operateSiteCode,waybillCode,0);
        if(isStartSite){//操作中心为始发中心
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,true,true,false);
            logger.info("kyexpressCancelCheck-查询运单是否是取消状态:{}",JsonHelper.toJson(baseEntity));
            if(baseEntity != null &&  baseEntity.getData() != null){
                BigWaybillDto bigWaybillDto = baseEntity.getData();
                Waybill waybill = bigWaybillDto.getWaybill();
                if(waybill != null){
                    //揽收类型为网点自送 71为是2 网点自提
                    boolean isPickSelftOrNo = BusinessUtil.isWdzsOrNo(waybill.getWaybillSign());
                    if(isPickSelftOrNo){
                        //运单状态为取消
                        WaybillManageDomain waybillState = bigWaybillDto.getWaybillState();
                        if(waybillState != null){
                            if(logger.isInfoEnabled()){
                                logger.info("运单状态是否为取消:{}",JsonHelper.toJson(waybillState));
                            }
                            if(waybillState.getWaybillState() != null && WaybillStatus.WAYBILL_STATUS_CANCEL.equals(waybillState.getWaybillState())){
                                return String.format(LoadIllegalException.INIT_PACKAGE_CANCEL,waybillCode);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 判断运单是否重复扫描
     */
    private boolean checkWaybillScanIsRepeat(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result)
            throws LoadIllegalException {
        String sealCarCode = request.getSealCarCode();
        String waybillCode = request.getWaybillCode();

        // 根据运单号获取卸车扫描记录
        String key = CacheKeyConstants.REDIS_PREFIX_SEAL_WAYBILL + sealCarCode + Constants.SEPARATOR_HYPHEN + waybillCode;
        String isExistIntercept = redisClientCache.get(key);
        if (StringUtils.isNotBlank(isExistIntercept)) {
            logger.warn("运单卸车扫描--该运单已经扫描过，请勿重复扫:sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
            result.setCode(JdCResponse.CODE_FAIL);
            result.setMessage("该运单已经扫描过，请勿重复扫！");
            return true;
        }
        // 空任务不判断未扫
        if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            return false;
        }
        int unScanPackageCount;
        try {
            // 获取未扫包裹
            unScanPackageCount = getUnScanPackageCount(sealCarCode);
        }catch (LoadIllegalException e){
            logger.error("运单卸车扫描--发生错误:sealCarCode={},packageCode={},error=", request.getSealCarCode(), request.getBarCode(), e);
            result.setCode(JdCResponse.CODE_FAIL);
            result.setMessage(e.getMessage());
            return true;
        }
        // 校验封车任务下是否已经全部扫描完
        if (unScanPackageCount <= 0 ) {
            logger.warn("运单卸车扫描--没有未扫包裹，请操作完成卸车任务,sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
            result.setCode(JdCResponse.CODE_FAIL);
            result.setMessage(String.format(LoadIllegalException.UNSCAN_PACK_ISNULL_INTERCEPT_MESSAGE, sealCarCode));
            return true;
        }
        return false;
    }


    /**
     * 获取封车编码下未扫包裹数
     * @param sealCarCode
     * @return
     */
    private int getUnScanPackageCount(String sealCarCode) throws LoadIllegalException {
        int totalCount = 0;
        int scanPackageCount = 0;
        try {
            String scanCountStr = redisClientCache.get(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(sealCarCode));
            if(StringUtils.isNotEmpty(scanCountStr)){
                scanPackageCount = Integer.valueOf(scanCountStr);
            }
        }catch (Exception e){
            logger.error("获取封车编码【{}】的缓存异常",sealCarCode,e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
            totalCount = unloadCar.getPackageNum();
        }catch (Exception e){
            logger.error(InvokeResult.SERVER_ERROR_MESSAGE,e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return totalCount - scanPackageCount;

    }

    /**
     * 设置扫描返回对象
     * @param request
     * @return
     */
    private UnloadCarScanResult convertToUnloadCarResult(UnloadCarScanRequest request) {
        UnloadCarScanResult scanResult = new UnloadCarScanResult();
        scanResult.setSealCarCode(request.getSealCarCode());
        scanResult.setBoardCode(request.getBoardCode());
        scanResult.setBarCode(request.getBarCode());
        scanResult.setReceiveSiteCode(request.getReceiveSiteCode());
        scanResult.setReceiveSiteName(request.getReceiveSiteName());
        return scanResult;
    }

    @Override
    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.searchUnloadDetail",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<UnloadCarDetailScanResult>> searchUnloadDetail(String sealCarCode) {
        InvokeResult<List<UnloadCarDetailScanResult>> result = new InvokeResult<List<UnloadCarDetailScanResult>>();
        try {
            //获取封车编码下已扫描运单明细
            Map<String, Integer> scanMap = getScanWaybillBySealCarCode(sealCarCode);
            //获取封车编码下所有运单明细
            Map<String, Integer> allMap = getAllWaybillBySealCarCode(sealCarCode);
            if(allMap == null || allMap.isEmpty()){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"封车编码【" + sealCarCode + "】绑定的批次没有包裹!");
                return result;
            }
            // 组装卸车明细
            List<UnloadCarDetailScanResult> detailScanList = new ArrayList<>();
            for (String waybillCode : allMap.keySet()){
                UnloadCarDetailScanResult detailScanResult = new UnloadCarDetailScanResult();
                detailScanResult.setSealCarCode(sealCarCode);
                detailScanResult.setWaybillCode(waybillCode);
                if(scanMap.containsKey(waybillCode)){
                    detailScanResult.setPackageScanCount(scanMap.get(waybillCode));
                    detailScanResult.setPackageUnScanCount(allMap.get(waybillCode)-scanMap.get(waybillCode));
                }else {
                    detailScanResult.setPackageScanCount(0);
                    detailScanResult.setPackageUnScanCount(allMap.get(waybillCode));
                }
                detailScanList.add(detailScanResult);
            }
            Collections.sort(detailScanList, new Comparator<UnloadCarDetailScanResult>() {
                @Override
                public int compare(UnloadCarDetailScanResult o1, UnloadCarDetailScanResult o2) {
                    if (o1.getPackageUnScanCount() == null
                            ||o1.getPackageUnScanCount() == 0
                            ||o2.getPackageUnScanCount() == null
                            ||o2.getPackageUnScanCount() == 0) {
                        return -1;
                    }
                    return o1.getPackageUnScanCount() - o2.getPackageUnScanCount();
                }
            });
            result.setData(detailScanList);
        }catch (Exception e){
            String errorMessage = "封车编码【" + sealCarCode + "】查询扫描明细异常!";
            logger.error(errorMessage,sealCarCode,e);
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,errorMessage);
        }

        return result;
    }

    /**
     * 设置包裹数
     *  1、已扫包裹 2、未扫包裹 3、多货包裹 4、总包裹
     * @param unloadCarScanResult
     */
    private void setPackageCount(UnloadCarScanResult unloadCarScanResult){
        String sealCarCode = unloadCarScanResult.getSealCarCode();
        Integer scanCount = 0;
        Integer surplusCount = 0;
        try {
            String scanCountStr = redisClientCache.get(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(sealCarCode));
            if(StringUtils.isNotEmpty(scanCountStr)){
                scanCount = Integer.valueOf(scanCountStr);
            }
            String surplusCountStr = redisClientCache.get(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT.concat(sealCarCode));
            if(StringUtils.isNotEmpty(surplusCountStr)){
                surplusCount = Integer.valueOf(surplusCountStr);
            }
        }catch (Exception e){
            logger.error("获取封车编码【{}】的缓存异常",sealCarCode,e);
        }
        if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            unloadCarScanResult.setPackageTotalCount(scanCount);
            unloadCarScanResult.setPackageScanCount(scanCount);
            unloadCarScanResult.setPackageUnScanCount(0);
            unloadCarScanResult.setSurplusPackageScanCount(surplusCount);
        } else {
            Integer totalCount = 0;
            UnloadCar unloadCar = null;
            try {
                unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
            } catch (Exception e) {
                logger.error(InvokeResult.SERVER_ERROR_MESSAGE, e);
            }
            if (unloadCar != null && unloadCar.getPackageNum() != null) {
                totalCount = unloadCar.getPackageNum();
            }
            unloadCarScanResult.setPackageTotalCount(totalCount);
            unloadCarScanResult.setPackageScanCount(scanCount);
            unloadCarScanResult.setPackageUnScanCount(totalCount - scanCount);
            unloadCarScanResult.setSurplusPackageScanCount(surplusCount);
        }
    }

    /**
     * 获取封车编码下已扫描运单信息
     * <p>
     *    key:运单号
     *    value: 封车编码下同一运单已扫的包裹数
     * </p>
     * @param sealCarCode
     * @return
     */
    private Map<String,Integer> getScanWaybillBySealCarCode(String sealCarCode){
        Map<String,Integer> scanMap = new HashMap<>();
        List<String> boardCodeList = unloadCarTransBoardDao.searchBoardsBySealCode(sealCarCode);
        if(CollectionUtils.isEmpty(boardCodeList)){
            logger.warn("封车编码【{}】未扫描包裹",sealCarCode);
            return scanMap;
        }
        for (String boardCode : boardCodeList){
            Response<List<String>> response = groupBoardManager.getBoxesByBoardCode(boardCode);
            if(response == null || response.getCode() != ResponseEnum.SUCCESS.getIndex()
                    || CollectionUtils.isEmpty(response.getData())){
                continue;
            }
            for (String packageCode : response.getData()){
                if(!WaybillUtil.isPackageCode(packageCode)){
                    continue;
                }
                String waybillCode = WaybillUtil.getWaybillCode(packageCode);
                if(scanMap.containsKey(waybillCode)){
                    scanMap.put(waybillCode,scanMap.get(waybillCode) + 1);
                }else {
                    scanMap.put(waybillCode,1);
                }
            }
        }
        return scanMap;
    }

    /**
     * 获取封车编码下所有运单信息
     * <p>
     *     key:运单号
     *     value: 封车编码下同一运单的包裹数
     * </p>
     * @param sealCarCode
     * @return
     */
    private Map<String,Integer> getAllWaybillBySealCarCode(String sealCarCode){
        List<String> allPackage = searchAllPackage(sealCarCode);
        Map<String,Integer> allMap = new HashMap<>();
        for (String packageCode : allPackage){
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            if(allMap.containsKey(waybillCode)){
                allMap.put(waybillCode,allMap.get(waybillCode) + 1);
            }else {
                allMap.put(waybillCode,1);
            }
        }
        return allMap;
    }

    /**
     * 封车编码下所有包裹
     * @param sealCarCode
     * @return
     */
    private List<String> searchAllPackage(String sealCarCode){
        List<String> allPackage = new ArrayList<>();
        UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        if(unloadCar == null || StringUtils.isEmpty(unloadCar.getBatchCode())){
            logger.warn("封车编码【{}】未绑定批次!",sealCarCode);
            return allPackage;
        }
        Integer createSiteCode = unloadCar.getStartSiteCode();
        String[] batchSplit = unloadCar.getBatchCode().split(",");
        if(batchSplit == null || batchSplit.length == 0){
            logger.warn("封车编码【{}】绑定批次为空!",sealCarCode);
            return allPackage;
        }
        for (String batchCode : batchSplit){
            allPackage.addAll(querySendPackageBySendCode(createSiteCode,batchCode));
        }
        return allPackage;
    }

    /**
     * 封车编码下指定运单下的所有包裹
     * @param sealCarCode 封车编码
     * @param waybillCode 运单号
     * @return 包裹号集合
     */
    public List<String> searchAllPackageByWaybillCode(String sealCarCode, String waybillCode){
        List<String> allPackage = new ArrayList<>();
        UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        if (unloadCar == null || StringUtils.isEmpty(unloadCar.getBatchCode())) {
            logger.warn("封车编码【{}】未绑定批次!",sealCarCode);
            return allPackage;
        }
        Integer createSiteCode = unloadCar.getStartSiteCode();
        String[] batchSplit = unloadCar.getBatchCode().split(",");
        if(batchSplit.length == 0){
            logger.warn("封车编码【{}】绑定批次为空!", sealCarCode);
            return allPackage;
        }
        for (String batchCode : batchSplit){
            allPackage.addAll(queryPackageCodeBySendAndWaybillCode(createSiteCode, batchCode, waybillCode));
        }
        return allPackage;
    }

    /**
     * 获取已发货批次下包裹号
     * @param createSiteCode
     * @param batchCode
     * @return
     */
    private List<String> querySendPackageBySendCode(Integer createSiteCode, String batchCode) {
        List<String> packageCodeList = new ArrayList<>();
        try {
            //获取当前操作网点的信息，用于判断是否集配站或者分拣、城配等。后续迁移时需要考虑分拣发过来的封车信息是从运输取还是从分拣单独拉取数据。此次为了避免线上的影响非集配站的依然按照原逻辑获取。
            BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(createSiteCode);
            if (siteOrgDto == null) {
                logger.warn("根据封车消息创建卸车任务--isExpressCenterSite--查询基础资料信息为空dmsSiteId[{}],batchCode", createSiteCode,batchCode);
            }
            //操作站点为集配站或者操作类型为营业部，，且流向转运中心
            if(siteOrgDto != null && (Constants.JI_PEI_CODE_9605.equals(siteOrgDto.getSubType()) || Constants.TERMINAL_SITE_TYPE_4.equals(siteOrgDto.getSiteType()))){
                if(Constants.TERMINAL_SITE_TYPE_4.equals(siteOrgDto.getSiteType())) {
                    logger.info("{}为营业部批次，需要从运输获取相关信息，封车网点为{}",batchCode,createSiteCode);
                }else {
                    logger.info("{}为集配站批次，需要从运输获取相关信息，封车网点为{}",batchCode,createSiteCode);
                }
                //集配站的批次号，通过运输接口获取相应的批次下包裹信息。
                CargoDetailDto cargoDetailDto = new CargoDetailDto();
                //批次号
                cargoDetailDto.setBatchCode(batchCode);
                //有效数据
                cargoDetailDto.setYn(1);
                //每次去运输获取数据的量;
                int limitSize = 1000;
                int currentSize  = limitSize ;
                int offset = 0;
                //只有获取的数据与设置分页数据相同时再去获取数据，当获取的数据少于分页数据时(获取数据为空也属于此种情况)，说明也取尽可不用再获取了。
                while(currentSize == limitSize){
                    com.jd.tms.data.dto.CommonDto<List<CargoDetailDto>> cargoDetailReturn = cargoDetailServiceManager.getCargoDetailInfoByBatchCode(cargoDetailDto,offset,limitSize);
                    if(cargoDetailReturn != null && cargoDetailReturn.getCode() == CommonDto.CODE_SUCCESS && cargoDetailReturn.getData() != null && !cargoDetailReturn.getData().isEmpty()){
                        List<CargoDetailDto> cargoDetailDtoList =  cargoDetailReturn.getData();
                        logger.info("{}为集配站批次，需要从运输获取相关信息，封车网点为{},获取到的包裹信息数量为{}",batchCode,createSiteCode,cargoDetailDtoList.size());
                        for(CargoDetailDto cargoDetailDtoTemp:cargoDetailDtoList){
                            packageCodeList.add(cargoDetailDtoTemp.getPackageCode());
                        }
                        currentSize = cargoDetailDtoList.size();
                        logger.info("批次号{}获取批次下的包裹数据条件：offset={},limitSize={},获取到的数据条数为{}",batchCode,offset,limitSize,currentSize);
                        offset =  offset + limitSize;
                    }else{
                        logger.warn("批次号{}获取批次下的包裹数据条件：offset={},limitSize={},获取到的数据条数为0或者调用接口返回的code异常code={},不再获取数据",batchCode,offset,limitSize,cargoDetailReturn == null ? "返回对象为空" : cargoDetailReturn.getCode());
                        currentSize = 0;
                    }
                }
            }else{
                //从分拣获取批次下的包裹数据。
                SendDetailDto params = new SendDetailDto();
                params.setCreateSiteCode(createSiteCode);
                params.setSendCode(batchCode);
                params.setIsCancel(0);
                packageCodeList = sendDatailDao.queryPackageCodeBySendCode(params);
            }
        }catch (Exception e){
            logger.error("查询批次【{}】的包裹数异常",batchCode,e);
        }
//        try {
//            SendDetailDto params = new SendDetailDto();
//            params.setCreateSiteCode(createSiteCode);
//            params.setSendCode(batchCode);
//            params.setIsCancel(0);
//            packageCodeList = sendDatailDao.queryPackageCodeBySendCode(params);
//        }catch (Exception e){
//            logger.error("查询批次【{}】的包裹数异常",batchCode,e);
//        }
        return packageCodeList;
    }

    /**
     * 获取已发货批次下和指定运单下的包裹号
     * @param createSiteCode 当前网点
     * @param batchCode 批次号
     * @param waybillCode 运单号
     * @return 包裹号集合
     */
    private List<String> queryPackageCodeBySendAndWaybillCode(Integer createSiteCode, String batchCode, String waybillCode) {
        List<String> packageCodeList = new ArrayList<>();
        try {
            SendDetailDto params = new SendDetailDto();
            params.setCreateSiteCode(createSiteCode);
            params.setSendCode(batchCode);
            params.setWaybillCode(waybillCode);
            params.setIsCancel(0);
            packageCodeList = sendDatailDao.queryPackageCodeBySendAndWaybillCode(params);
        }catch (Exception e){
            logger.error("查询批次【{}】的包裹数异常",batchCode,e);
        }
        return packageCodeList;
    }

    /**
     * <p>
     *     1、推TC组板关系
     *     2、卸车逻辑处理
     *     3、组板全程跟踪
     * </p>
     * @param request
     * @param isSurplusPackage 多货包裹标识
     */
    private void dealUnloadAndBoxToBoard(UnloadCarScanRequest request,boolean isSurplusPackage) throws LoadIllegalException {
        AddBoardBox addBoardBox = new AddBoardBox();
        String boardCode="";
        try {
            addBoardBox.setBoardCode(request.getBoardCode());
            addBoardBox.setBoxCode(request.getBarCode());
            addBoardBox.setOperatorErp(request.getOperateUserErp());
            addBoardBox.setOperatorName(request.getOperateUserName());
            addBoardBox.setSiteCode(request.getOperateSiteCode());
            addBoardBox.setSiteName(request.getOperateSiteName());
            addBoardBox.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
            addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());
            Response<Integer> response = groupBoardManager.addBoxToBoard(addBoardBox);

            if (response == null) {
                logger.warn("推组板关系失败!");
                throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
            }
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            BeanUtils.copyProperties(request, boardCommonRequest);
            if (response.getCode() == ResponseEnum.SUCCESS.getIndex()) {
                //组板成功
                logger.info("组板成功、板号:【{}】包裹号:【{}】站点:【{}】", request.getBoardCode(), request.getBarCode(), request.getOperateSiteCode());
                setCacheOfBoardAndPack(request.getBoardCode(), request.getBarCode());
                boxToBoardSuccessAfter(request, null, isSurplusPackage);
                // 组板全程跟踪
                boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
                return;
            }
            /**
             * 组板失败
             *  500：失败-当前箱已经绑过板
             *  直接强制组板到新版上
             *
             *  二期优化增加【组板转移】提示
             * */
            /****/
            if (response.getCode() == 500) {
                if (null == request.getIsCombinationTransfer() || Constants.IS_COMBITION_TRANSFER.equals(request.getIsCombinationTransfer())) {
                    //调用TC的板号转移接口
                    InvokeResult<String> invokeResult = boardCommonManager.boardMove(boardCommonRequest);
                    if (invokeResult == null) {
                        throw new LoadIllegalException(LoadIllegalException.BOARD_MOVED_INTERCEPT_MESSAGE);
                    }
                    //重新组板失败
                    if (invokeResult.getCode() != ResponseEnum.SUCCESS.getIndex()) {
                        logger.warn("组板转移成功.原板号【{}】新板号【{}】失败原因【{}】",
                                invokeResult.getData(), request.getBoardCode(), response.getMesseage());
                        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
                    }
                    //重新组板成功处理
                    logger.info("组板转移成功.原板号【{}】新板号【{}】包裹号【{}】站点【{}】",
                            invokeResult.getData(), request.getBoardCode(), request.getBarCode(), request.getOperateSiteCode());
                    setCacheOfBoardAndPack(request.getBoardCode(), request.getBarCode());
                    boxToBoardSuccessAfter(request, invokeResult.getData(), isSurplusPackage);
                    return;
                } else {
                    Board board = loadScanService.getBoardCodeByPackageCode(request.getOperateSiteCode().intValue(), request.getBarCode());
                    if (null != board) {
                        boardCode = board.getCode();
                    }
                    throw new UnloadPackageBoardException(String.format(LoadIllegalException.PACKAGE_ALREADY_BIND, boardCode));
                }
            }
        } catch (LoadIllegalException ex) {
            throw new LoadIllegalException(ex.getMessage());
        }catch (Exception e){
            if (e instanceof UnloadPackageBoardException) {
                throw new UnloadPackageBoardException(String.format(LoadIllegalException.PACKAGE_ALREADY_BIND,boardCode));
            }
            logger.error("推TC组板关系异常，入参【{}】",JsonHelper.toJson(addBoardBox),e);
        }
        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
    }

    /**
     * 设置板包裹缓存：默认7天
     * @param boardCode
     * @param packageCode
     */
    private void setCacheOfBoardAndPack(String boardCode, String packageCode) {
        try {
            String key = CacheKeyConstants.REDIS_PREFIX_BOARD_PACK + boardCode + Constants.SEPARATOR_HYPHEN + packageCode;
            redisClientCache.setEx(key,String.valueOf(true),7,TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("设置板【{}】包裹【{}】缓存异常",boardCode,packageCode,e);
        }
    }

    /**
     * 组板成功后获取包裹数量
     * @param request
     * @param oldBoardCode 旧板号
     * @param isSurplusPackage 多货包裹标识
     */
    private void boxToBoardSuccessAfter(UnloadCarScanRequest request,String oldBoardCode,boolean isSurplusPackage) {
        try {
            String boardCode = request.getBoardCode();
            String sealCarCode = request.getSealCarCode();
            Integer surplusCount = 0;
            Integer scanCount = 0;
            // 新板包裹数变更
            if (isSurplusPackage) {
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_SURPLUS_PACKAGE_COUNT.concat(boardCode), 1);
                surplusCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT.concat(sealCarCode), 1);
            } else {
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(boardCode), 1);
                scanCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(sealCarCode), 1);
            }
            updatePackCount(request, scanCount, surplusCount);

            // 老板包裹数变更
            if (StringUtils.isNotEmpty(oldBoardCode)) {
                UnloadCarTransBoard oldUnloadBoard = unloadCarTransBoardDao.searchByBoardCode(oldBoardCode);
                if (oldUnloadBoard == null || StringUtils.isEmpty(oldUnloadBoard.getSealCarCode())) {
                    //老板未绑定封车编码则不更新
                    return;
                }
                String oldSealCarCode = oldUnloadBoard.getSealCarCode();
                if (isSurplusPackage) {
                    updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_SURPLUS_PACKAGE_COUNT.concat(oldBoardCode), -1);
                    surplusCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT.concat(oldSealCarCode), -1);
                } else {
                    updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(oldBoardCode), -1);
                    scanCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(oldSealCarCode), -1);
                }
                request.setSealCarCode(oldSealCarCode);
                request.setBoardCode(oldBoardCode);
                updatePackCount(request, scanCount, surplusCount);
            }
        } catch (LoadIllegalException ex) {
            throw new LoadIllegalException(ex.getMessage());
        }catch (Exception e){
            logger.error("卸车扫描处理异常,参数【{}】",JsonHelper.toJson(request),e);
        }
    }

    /**
     * 运单验货扫描成功后获取包裹数量
     * @param request 查询参数
     * @param totalPackageNum 运单总包裹数
     * @param surplusPackages 多货包裹集合
     */
    private void waybillInspectSuccessAfter(UnloadCarScanRequest request, int totalPackageNum, List<String> surplusPackages) {
        try {
            String waybillCode = request.getWaybillCode();
            String sealCarCode = request.getSealCarCode();

            // 如果有多货包裹
            if (CollectionUtils.isNotEmpty(surplusPackages)) {
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT
                        .concat(sealCarCode), surplusPackages.size());
                // 已扫 = 总包裹数 - 多扫
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(sealCarCode),
                        totalPackageNum - surplusPackages.size());
            } else {
                // 已扫 = 总包裹数
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(sealCarCode),
                        totalPackageNum);
            }
            // 设置运单扫描记录
            String key = CacheKeyConstants.REDIS_PREFIX_SEAL_WAYBILL + sealCarCode + Constants.SEPARATOR_HYPHEN + waybillCode;
            redisClientCache.setEx(key, StringUtils.EMPTY,7,TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("按运单卸车扫描处理异常,参数【{}】", JsonHelper.toJson(request), e);
        }
    }

    /**
     * 更新缓存返回更新后value
     * @param cacheKey
     * @param addCount
     * @return
     */
    private int updateCache(String cacheKey,int addCount) {
        int count = 0;
        try {
            Long returnLong = redisClientCache.incrBy(cacheKey, addCount);
            redisClientCache.expire(cacheKey,7,TimeUnit.DAYS);
            count = returnLong.intValue();
        }catch (Exception e){
            logger.error("更新【{}】缓存异常",cacheKey,e);
        }
        return count;
    }

    /**
     * 更新卸车组板表
     * @param request
     * @param scanCount
     * @param surplusCount
     */
    private void updatePackCount(UnloadCarScanRequest request, Integer scanCount, Integer surplusCount) {
        UnloadCarTransBoard unloadCarTransBoard = new UnloadCarTransBoard();
        unloadCarTransBoard.setSealCarCode(request.getSealCarCode());
        unloadCarTransBoard.setBoardCode(request.getBoardCode());
        unloadCarTransBoard.setPackageScanCount(scanCount);
        unloadCarTransBoard.setSurplusPackageScanCount(surplusCount);
        unloadCarTransBoard.setOperateTime(new Date(request.getOperateTime()));
        unloadCarTransBoard.setCreateTime(new Date());
        unloadCarTransBoard.setUpdateTime(new Date());
        unloadCarTransBoard.setYn(1);
        UnloadCarTransBoard unloadCarBoard = unloadCarTransBoardDao.searchBySealCodeAndBoardCode(request.getSealCarCode(),
                request.getBoardCode());
        if(unloadCarBoard != null){
            unloadCarTransBoardDao.updateCount(unloadCarTransBoard);
        }else {
            unloadCarTransBoardDao.add(unloadCarTransBoard);
        }
    }

    /**
     * 路由校验
     *  1、第一次生成板号
     *  2、非第一次目的地校验
     * @param request
     * @param result
     */
    private void routerCheck(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result) throws LoadIllegalException {
        if(StringUtils.isEmpty(request.getBoardCode())){
            //第一次则生成板号
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            BeanUtils.copyProperties(request,boardCommonRequest);
            InvokeResult<Board> invokeResult = boardCommonManager.createBoardCode(boardCommonRequest);
            if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                throw new LoadIllegalException(invokeResult.getMessage());
            }
            Board board = invokeResult.getData();
            if(board == null || StringUtils.isEmpty(board.getCode())){
                throw new LoadIllegalException(LoadIllegalException.BOARD_CREATE_FAIL_INTERCEPT_MESSAGE);
            }
            request.setBoardCode(board.getCode());
            request.setReceiveSiteCode(board.getDestinationId());
            request.setReceiveSiteName(board.getDestination());
            UnloadCarScanResult unloadCarScanResult = result.getData();
            unloadCarScanResult.setSealCarCode(request.getSealCarCode());
            unloadCarScanResult.setBarCode(request.getBarCode());
            unloadCarScanResult.setBoardCode(board.getCode());
            unloadCarScanResult.setReceiveSiteCode(board.getDestinationId());
            unloadCarScanResult.setReceiveSiteName(board.getDestination());
            return;
        }
        // 非第一次则校验目的地是否一致
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        Integer nextSiteCode;
        Integer destinationId = null;
        try {
            nextSiteCode = boardCommonManager.getNextSiteCodeByRouter(waybillCode,request.getOperateSiteCode());
            if(nextSiteCode == null){
                // 此处直接返回，因为ver组板校验链会判断
                return;
            }
            Response<Board> response = groupBoardManager.getBoard(request.getBoardCode());
            if(response != null && response.getCode() == ResponseEnum.SUCCESS.getIndex()
                    && response.getData() != null){
                destinationId = response.getData().getDestinationId();
            }
        }catch (Exception e){
            logger.error("运单号【{}】的路由下一跳和板号【{}】目的地校验异常",waybillCode,request.getBoardCode(),e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        if(destinationId == null){
            throw new LoadIllegalException(LoadIllegalException.BOARD_RECIEVE_EMPEY_INTERCEPT_MESSAGE);
        }
        if(!nextSiteCode.equals(destinationId)){
            throw new LoadIllegalException(LoadIllegalException.FORBID_BOARD_INTERCEPT_MESSAGE);
        }
    }

    /**
     * 多货包裹校验
     * @param request
     * @param result
     * @return
     */
    private boolean surfacePackageCheck(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result, InvokeResult<UnloadScanDetailDto> dtoInvokeResult) throws LoadIllegalException {
        boolean isSurplusPackage = false;
        try {
            isSurplusPackage = isSurfacePackage(request.getSealCarCode(),request.getBarCode());
        }catch (Exception e){
            throw new LoadIllegalException(e.getMessage());
        }
        if(isSurplusPackage && !request.getSealCarCode().startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)){
            // 空任务不弹框提示
            // 201 成功并页面提示
            result.customMessage(CODE_SUCCESS_HIT, LoadIllegalException.PACK_NOTIN_SEAL_INTERCEPT_MESSAGE);
            //下面判断中现用接口中对应遍历均已经初始化对象，不会出现NPE，废弃老接口中传null，避免NPE问题再次判空
            if(dtoInvokeResult != null && dtoInvokeResult.getData() != null && dtoInvokeResult.getData().getWarnMsg() != null) {
                Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();
                warnMsg.put(UnloadCarWarnEnum.PACK_NOTIN_SEAL_INTERCEPT_MESSAGE.getLevel(), UnloadCarWarnEnum.PACK_NOTIN_SEAL_INTERCEPT_MESSAGE.getDesc());
            }
        }
        return isSurplusPackage;
    }

    /**
     * 是否是多货包裹
     * @param sealCarCode
     * @param packageCode
     * @return
     * @throws LoadIllegalException
     */
    private boolean isSurfacePackage(String sealCarCode, String packageCode) throws LoadIllegalException {
        boolean exist = false;
        String key = CacheKeyConstants.REDIS_PREFIX_SEALCAR_SURPLUS_PACK + sealCarCode + Constants.SEPARATOR_HYPHEN + packageCode;
        try {
            String existStr = redisClientCache.get(key);
            if(StringUtils.isNotEmpty(existStr)){
                return Boolean.valueOf(existStr);
            }
        }catch (Exception e){
            logger.error("获取缓存【{}】异常",key,e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        // 空任务都是多扫
        if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            exist = true;
        } else {
            List<String> allPackage = searchAllPackage(sealCarCode);
            if (CollectionUtils.isEmpty(allPackage)) {
                throw new LoadIllegalException(LoadIllegalException.SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE);
            }
            if (!allPackage.contains(packageCode)) {
                // 不包含则是多货包裹
                exist = true;
            }
        }
        try {
            redisClientCache.setEx(key,String.valueOf(exist),7,TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("设置缓存【{}】异常",key,e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return exist;
    }

    /**
     * 检查运单下属于多货的包裹集合
     * @param totalPackages 运单总包裹号集合
     * @param sealCarCode 封车编码
     * @param waybillCode 运单号
     * @return 多货的包裹集合
     */
    private List<String> getSurplusPackageCodes(List<String> totalPackages, String sealCarCode, String waybillCode) {
        // 查询封车任务下指定运单下的所有包裹
        List<String> sealPackages = searchAllPackageByWaybillCode(sealCarCode, waybillCode);
        if (CollectionUtils.isEmpty(sealPackages)) {
            if (!sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
                throw new LoadIllegalException(LoadIllegalException.SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE);
            }
            return totalPackages;
        }
        return getDifferenceList(totalPackages, sealPackages);
    }

    /**
     * 根据运单总包裹集合与封车任务下总包裹集合的差集，得到多货包裹集合
     * @param totalPackages 运单有效包裹集合
     * @param sealPackages 封车任务下总包裹集合
     * @return 多货包裹集合
     */
    private List<String> getDifferenceList(List<String> totalPackages, List<String> sealPackages) {
        List<String> different = new ArrayList<>();
        // 将应卸包裹放入map
        Map<String, String> map = new HashMap<>(sealPackages.size());
        for (String packageCode : sealPackages) {
            map.put(packageCode, packageCode);
        }
        for (String packageCode : totalPackages) {
            if (map.get(packageCode) == null) {
                different.add(packageCode);
            }
        }
        return different;
    }

    /**
     * 验货拦截及验货处理
     * @param request
     */
    private void inspectionIntercept(UnloadCarScanRequest request) throws LoadIllegalException {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        // 加盟商余额校验
        if(allianceBusiDeliveryDetailService.checkExist(waybillCode)
                && !allianceBusiDeliveryDetailService.checkMoney(waybillCode)){
            throw new LoadIllegalException(LoadIllegalException.ALLIANCE_INTERCEPT_MESSAGE);
        }
        // 验货任务
        pushInspectionTask(request);
    }

    /**
     * 推验货任务
     * @param request
     */
    private void pushInspectionTask(UnloadCarScanRequest request) {
        InspectionRequest inspection=new InspectionRequest();
        inspection.setUserCode(request.getOperateUserCode());
        inspection.setUserName(request.getOperateUserName());
        inspection.setSiteCode(request.getOperateSiteCode());
        inspection.setSiteName(request.getOperateSiteName());
        inspection.setOperateTime(DateHelper.formatDateTime(new Date()));
        inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        // 按运单维度验货
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            inspection.setPackageBarOrWaybillCode(request.getWaybillCode());
        } else {
            inspection.setPackageBarOrWaybillCode(request.getBarCode());
        }
        inspection.setBizSource(InspectionBizSourceEnum.UNLOAD_CAR_INSPECTION.getCode());

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        taskRequest.setKeyword1(String.valueOf(request.getOperateUserCode()));
        taskRequest.setKeyword2(request.getBarCode());
        taskRequest.setType(Task.TASK_TYPE_INSPECTION);
        taskRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
        taskRequest.setSiteCode(request.getOperateSiteCode());
        taskRequest.setSiteName(request.getOperateSiteName());
        taskRequest.setUserCode(request.getOperateUserCode());
        taskRequest.setUserName(request.getOperateUserName());

        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task = this.taskService.toTask(taskRequest, eachJson);

        taskService.add(task, true);
    }

    @Override
    public PagerResult<UnloadCarTask> queryByCondition(UnloadCarCondition condition) {

        PagerResult<UnloadCarTask> result = new PagerResult<UnloadCarTask>();
        List<UnloadCarTask> unloadCarTasks = new ArrayList<>();
        int total = 0;
        try {
            List<Integer> status = new ArrayList<>();
            //查询卸车任务
            if (condition.getDistributeType().equals(UNLOAD_CAR_UN_DISTRIBUTE)) {
                status.add(UnloadCarStatusEnum.UNLOAD_CAR_UN_DISTRIBUTE.getType());
            } else if (condition.getDistributeType().equals(UNLOAD_CAR_DISTRIBUTE)) {
                status.add(UnloadCarStatusEnum.UNLOAD_CAR_UN_START.getType());
                status.add(UnloadCarStatusEnum.UNLOAD_CAR_STARTED.getType());
            } else if (condition.getDistributeType().equals(UNLOAD_CAR_ALL)) {
                status.add(UnloadCarStatusEnum.UNLOAD_CAR_UN_DISTRIBUTE.getType());
                status.add(UnloadCarStatusEnum.UNLOAD_CAR_UN_START.getType());
                status.add(UnloadCarStatusEnum.UNLOAD_CAR_STARTED.getType());
            }
            condition.setStatus(status);
            total = unloadCarDao.queryCountByCondition(condition);
            unloadCarTasks = unloadCarDao.queryByCondition(condition);
            for (UnloadCarTask unloadCarTask : unloadCarTasks) {
                String sealCarCode = unloadCarTask.getSealCarCode();
                //查询卸车任务的协助人
                List<String> helpers = unloadCarDistributionDao.selectHelperBySealCarCode(sealCarCode);
                unloadCarTask.setHelperErps(StringUtils.strip(helpers.toString(),"[]"));
            }
        }catch (Exception e){
            logger.error("分页查询卸车任务异常,查询条件【{}】",e,JsonHelper.toJson(condition));
        }
        result.setRows(unloadCarTasks);
        result.setTotal(total);
        return result;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean distributeTask(DistributeTaskRequest request) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("unloadUserErp",request.getUnloadUserErp().trim());
        params.put("unloadUserName",request.getUnloadUserName());
        params.put("railWayPlatForm",request.getRailWayPlatForm());
        params.put("unloadCarIds",request.getUnloadCarIds());
        params.put("updateUserErp",request.getUpdateUserErp());
        params.put("updateUserName",request.getUpdateUserName());
        params.put("operateUserErp",request.getUpdateUserErp());
        params.put("operateUserName",request.getUpdateUserName());
        params.put("distributeTime",new Date());
        params.put("version", AppVersionEnums.PDA_OLD.getVersion());
        int result = unloadCarDao.distributeTaskByParams(params);
        if (result < 1) {
            logger.warn("分配任务失败，请求体：{}",JsonHelper.toJson(request));
            return false;
        }
        //同步卸车负责人与卸车任务之间关系
        for (int i=0;i<request.getSealCarCodes().size();i++) {
            transferService.saveOperatePdaVersion(request.getSealCarCodes().get(i), AppVersionEnums.PDA_OLD.getVersion());
            UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
            unloadCarDistribution.setSealCarCode(request.getSealCarCodes().get(i));
            unloadCarDistribution.setUnloadUserErp(request.getUnloadUserErp());
            unloadCarDistribution.setUnloadUserName(request.getUnloadUserName());
            unloadCarDistribution.setUnloadUserType(UnloadUserTypeEnum.UNLOAD_MASTER.getType());
            unloadCarDistribution.setUpdateTime(new Date());
            unloadCarDistribution.setCreateTime(new Date());
            // 使用卸车任务的创建时间作为负责人进入的时间
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCodeWithStatus(request.getSealCarCodes().get(i));
            if (unloadCar != null) {
                unloadCarDistribution.setUpdateTime(unloadCar.getCreateTime());
                unloadCarDistribution.setCreateTime(unloadCar.getCreateTime());
            }

            // 先逻辑删除旧的负责人
            unloadCarDistributionDao.deleteUnloadMaster(unloadCarDistribution);
            // 如果新负责人还是协助人，需要删除
            unloadCarDistributionDao.deleteUnloadHelper(unloadCarDistribution);
            // 添加新的负责人
            unloadCarDistributionDao.add(unloadCarDistribution);

        }
        return true;
    }

    @Override
    public boolean insertUnloadCar(TmsSealCar tmsSealCar) {
        logger.info("封车消息过来了：sealCarCode={}, operateSiteId={}, batchCode={}", tmsSealCar.getSealCarCode(), tmsSealCar.getOperateSiteId(), tmsSealCar.getBatchCodes());

        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setSealCarCode(tmsSealCar.getSealCarCode());
        List<UnloadCar> unloadCarList = unloadCarDao.selectByUnloadCar(unloadCar);
        if (CollectionUtils.isNotEmpty(unloadCarList)) {
            logger.warn("封车编码【{}】已存在", unloadCar.getSealCarCode());
            return false;
        }

        List<String> batchCodes = tmsSealCar.getBatchCodes();
        if(CollectionUtils.isEmpty(batchCodes)){
            logger.warn("封车编码【{}】下的批次为空!",unloadCar.getSealCarCode());
            return false;
        }
        try {
            Integer nextSiteCode = null;
            CommonDto<SealCarDto> sealCarDto = vosManager.querySealCarInfoBySealCarCode(tmsSealCar.getSealCarCode());
            if (CommonDto.CODE_SUCCESS == sealCarDto.getCode() && sealCarDto.getData() != null) {
                unloadCar.setEndSiteCode(sealCarDto.getData().getEndSiteId());
                unloadCar.setEndSiteName(sealCarDto.getData().getEndSiteName());
                nextSiteCode = sealCarDto.getData().getEndSiteId();
            } else {
                logger.error("调用运输的接口获取下游机构信息失败，请求体：{}，返回值：{}",tmsSealCar.getSealCarCode(),JsonHelper.toJson(sealCarDto));
                return false;
            }
            // 通过工具类从批次号上截取目的场地ID
//            Integer nextSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(batchCodes.get(0));
            if (nextSiteCode == null) {
                logger.warn("封车编码【{}】批次号【{}】没有符合的下一场地!返回对象中的下一网点id为{},下一网点code为{},下一网点名称为{}", unloadCar.getSealCarCode(), batchCodes.get(0),sealCarDto.getData().getEndSiteId(),sealCarDto.getData().getEndSiteCode(),sealCarDto.getData().getEndSiteName());
                return false;
            }
            boolean isExpressCenterSite = isExpressCenterSite(nextSiteCode);
            Set<String> requiredBatchCodes = new HashSet<>();
            for (String batchCode : batchCodes){
                //目的场地为转运的，需要初始化目的转运场地的卸车任务。
                if(BusinessHelper.isSendCode(batchCode) || isExpressCenterSite){
                    requiredBatchCodes.add(batchCode);
                }
            }
            if(CollectionUtils.isEmpty(requiredBatchCodes)){
                logger.warn("封车编码【{}】没有符合的批次!",unloadCar.getSealCarCode());
                return false;
            }
            unloadCar.setBatchCode(getStrByBatchCodes(new ArrayList<String>(requiredBatchCodes)));
            // 只有操作站点是快运中心时，才初始化运单暂存
//            logger.info("封车消息操作站点：sealCarCode={}, operateSiteId={}, nextSiteCode={}", tmsSealCar.getSealCarCode(), tmsSealCar.getOperateSiteId(), nextSiteCode);
            if (isExpressCenterSite) {
                logger.info("当前封车消息属于快运中心：sealCarCode={}", tmsSealCar.getSealCarCode());
                boolean isSuccess = batchSaveUnloadScan(tmsSealCar, unloadCar);
                logger.info("当前封车消息属于快运中心：sealCarCode={},isSuccess={}", tmsSealCar.getSealCarCode(), isSuccess);
                if (!isSuccess) {
                    logger.warn("封车编码{}写入运单维度数据不成功！",unloadCar.getSealCarCode());
                    return false;
                }
            } else {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("createSiteCode", tmsSealCar.getOperateSiteId());
                params.put("sendCodes", requiredBatchCodes);
                Integer waybillNum = sendDatailDao.queryWaybillNumBybatchCodes(params);
                Integer packageNum = sendDatailDao.queryPackageNumBybatchCodes(params);
                unloadCar.setWaybillNum(waybillNum);
                unloadCar.setPackageNum(packageNum);
            }
        } catch (Exception e) {
            logger.error("封车编码{}查询运单数或者包裹数失败!",unloadCar.getSealCarCode(),e);
            return false;
        }
        unloadCar.setSealCarCode(tmsSealCar.getSealCarCode());
        unloadCar.setVehicleNumber(tmsSealCar.getVehicleNumber());
        unloadCar.setSealTime(tmsSealCar.getOperateTime());
        unloadCar.setStartSiteCode(tmsSealCar.getOperateSiteId());
        unloadCar.setStartSiteName(tmsSealCar.getOperateSiteName());
        unloadCar.setCreateTime(new Date());
        unloadCar.setTransWay(tmsSealCar.getTransWay());

//        CommonDto<SealCarDto> sealCarDto = vosManager.querySealCarInfoBySealCarCode(tmsSealCar.getSealCarCode());
//        if (CommonDto.CODE_SUCCESS == sealCarDto.getCode() && sealCarDto.getData() != null) {
//            unloadCar.setEndSiteCode(sealCarDto.getData().getEndSiteId());
//            unloadCar.setEndSiteName(sealCarDto.getData().getEndSiteName());
//        } else {
//            logger.error("调用运输的接口获取下游机构信息失败，请求体：{}，返回值：{}",tmsSealCar.getSealCarCode(),JsonHelper.toJson(sealCarDto));
//            return false;
//        }
        try {
//            unloadCarDao.add(unloadCar);
            this.fillUnloadCarTaskDuration(unloadCar);
        } catch (Exception e) {
            logger.error("卸车任务数据插入失败，数据：{},返回值:{}",JsonHelper.toJson(tmsSealCar),e);
            return false;
        }
        return true;
    }

    @Override
    public UnloadCar selectBySealCarCode(String sealCarCode) {
        return unloadCarDao.selectBySealCarCode(sealCarCode);
    }

    private boolean batchSaveUnloadScan(TmsSealCar tmsSealCar, UnloadCar unloadCar) {
        CallerInfo info = Profiler.registerInfo("com.jd.bluedragon.distribution.loadAndUnload.service.impl.UnloadCarServiceImpl.batchSaveUnloadScan", false, true);
        try {
            // 初始化运单暂存表
            UnloadScan unloadScan = createUnloadScan(null, tmsSealCar.getSealCarCode(), 0, 0,
                    tmsSealCar.getOperateUserName(), tmsSealCar.getOperateUserCode(), false);
            unloadScan.setStatus(GoodsLoadScanConstants.UNLOAD_SCAN_BLANK);

            List<String> totalPackageCodes = new ArrayList<>();
            List<String> packageCodes;
            // 循环获取每个批次下的包裹号集合
            for (String batchCode : tmsSealCar.getBatchCodes()) {
                packageCodes = querySendPackageBySendCode(tmsSealCar.getOperateSiteId(), batchCode);
                totalPackageCodes.addAll(packageCodes);
            }
            if (CollectionUtils.isEmpty(totalPackageCodes)) {
                logger.warn("封车编码{}下的批次号未获取到对应的包裹信息,不写入卸车任务运单详情信息表.",tmsSealCar.getSealCarCode());
                return false;
            }
            Map<String, WaybillPackageNumInfo> waybillMap = new HashMap<>();
            // 对包裹号集合按照运单维度分组，并统计每个所属运单下的应卸包裹数
            for (String packageCode : totalPackageCodes) {
                String waybillCode = WaybillUtil.getWaybillCode(packageCode);
                WaybillPackageNumInfo waybillInfo = waybillMap.get(waybillCode);
                if (waybillInfo != null) {
                    waybillInfo.setForceAmount(waybillInfo.getForceAmount() + 1);
                } else {
                    waybillInfo = new WaybillPackageNumInfo();
                    waybillInfo.setWaybillCode(waybillCode);
                    // 从包裹号上截取包裹数
                    int packageNum = WaybillUtil.getPackNumByPackCode(packageCode);
                    waybillInfo.setPackageAmount(packageNum);
                    waybillInfo.setForceAmount(1);
                    waybillMap.put(waybillCode, waybillInfo);
                }
            }

            // 设置封车任务下总运单数
            unloadCar.setWaybillNum(waybillMap.size());
            // 设置封车任务下总包裹数
            unloadCar.setPackageNum(totalPackageCodes.size());
            List<WaybillPackageNumInfo> waybillPackageNumInfoList = new ArrayList<>(waybillMap.values());
            // 分批保存
            List<List<WaybillPackageNumInfo>> partitionList = ListUtils.partition(waybillPackageNumInfoList, 200);
            for (List<WaybillPackageNumInfo> list : partitionList) {
                unloadScan.setWaybillPackageNumInfoList(list);
                unloadScanDao.batchInsert(unloadScan);
            }
            return true;
        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("根据封车消息初始化运单暂存表发生异常,sealCarCode={},e=", tmsSealCar.getSealCarCode(), e);
            return false;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    @Override
    public InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq) {

        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<UnloadCarTaskDto> unloadCarTaskDtos = new ArrayList<>();
        result.setData(unloadCarTaskDtos);

        UnloadCar unload = new UnloadCar();
        unload.setUnloadUserErp(unloadCarTaskReq.getUser().getUserErp());
        unload.setEndSiteCode(unloadCarTaskReq.getCurrentOperate().getSiteCode());
        List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskByParams(unload);
        int serialNumber = 1;
        for (UnloadCar unloadCar : unloadCars) {
            UnloadCarTaskDto unloadCarTaskDto = new UnloadCarTaskDto();
            unloadCarTaskDto.setSerialNumber(serialNumber);
            unloadCarTaskDto.setTaskCode(unloadCar.getSealCarCode());
            unloadCarTaskDto.setCarCode(unloadCar.getVehicleNumber());
            unloadCarTaskDto.setPlatformName(unloadCar.getRailWayPlatForm());
            unloadCarTaskDto.setBatchCode(unloadCar.getBatchCode() == null ? "" : unloadCar.getBatchCode());
            if (unloadCar != null && unloadCar.getBatchCode() != null) {
                unloadCarTaskDto.setBatchNum(getBatchNumber(unloadCar.getBatchCode()));
            } else {
                unloadCarTaskDto.setBatchNum(0);
            }

            unloadCarTaskDto.setWaybillNum(unloadCar.getWaybillNum() == null ? 0 : unloadCar.getWaybillNum());
            unloadCarTaskDto.setPackageNum(unloadCar.getPackageNum() == null ? 0 : unloadCar.getPackageNum());
            unloadCarTaskDto.setTaskStatus(unloadCar.getStatus());
            unloadCarTaskDto.setTaskStatusName(UnloadCarStatusEnum.getEnum(unloadCar.getStatus()).getName());
            unloadCarTaskDto.setType(unloadCar.getType());
            serialNumber++;
            unloadCarTaskDtos.add(unloadCarTaskDto);
        }

        return result;
    }

    @Override
    public InvokeResult<List<UnloadCarTaskDto>> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq) {
        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);

        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setSealCarCode(unloadCarTaskReq.getTaskCode());
        unloadCar.setStatus(unloadCarTaskReq.getTaskStatus());

        unloadCar.setUnloadUserErp(unloadCarTaskReq.getUser().getUserErp());
        unloadCar.setEndSiteCode(unloadCarTaskReq.getCurrentOperate().getSiteCode());

        unloadCar.setUpdateUserErp(unloadCarTaskReq.getUser().getUserErp());
        unloadCar.setUpdateUserName(unloadCarTaskReq.getUser().getUserName());
        Date updateTime = DateHelper.parseDateTime(unloadCarTaskReq.getOperateTime());
        unloadCar.setUpdateTime(updateTime);
        // 根据封车编码查询封车记录
        UnloadCar unload = new UnloadCar();
        unload.setSealCarCode(unloadCar.getSealCarCode());
        List<UnloadCar> unloadCarList = unloadCarDao.selectByUnloadCar(unload);
        if (CollectionUtils.isEmpty(unloadCarList)) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("卸车任务不存在");
            logger.error("修改任务状态失败，请求信息：{}", JsonHelper.toJson(unloadCarTaskReq));
            return result;
        }
        UnloadCar unloadCarEntity = unloadCarList.get(0);
        if (Integer.valueOf(UnloadCarStatusEnum.UNLOAD_CAR_END.getType()).equals(unloadCarEntity.getStatus())) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("卸车任务已完成，请勿重复操作");
            logger.error("修改任务状态失败，请求信息：{}", JsonHelper.toJson(unloadCarTaskReq));
            return result;
        }
        if (!unloadCar.getUnloadUserErp().equals(unloadCarEntity.getUnloadUserErp())) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("卸车完成只能由卸车负责人操作，协助人无法操作");
            logger.error("修改任务状态失败，负责人erp={},请求信息={}", unloadCarEntity.getUnloadUserErp(), JsonHelper.toJson(unloadCarTaskReq));
            return result;
        }
        //校验卸车任务是否超时
        if (UnloadCarStatusEnum.UNLOAD_CAR_END.getType() == unloadCarTaskReq.getTaskStatus()) {
            UnloadCar unloadCarParam = new UnloadCar();
            unloadCarParam.setSealCarCode(unloadCarTaskReq.getTaskCode());
            List<UnloadCar> list = unloadCarDao.selectByCondition(unloadCarParam);
            if(CollectionUtils.isNotEmpty(list)) {
                UnloadCar dbRes = list.get(0);
                //时效：单位min
                if(dbRes.getStartTime() != null && dbRes.getDuration() != null) {
                    Long planEndTime = dbRes.getStartTime().getTime() + dbRes.getDuration() * 60 * 1000l;
                    Long updateTimeTemp = updateTime.getTime();
                    unloadCar.setEndStatus(updateTimeTemp > planEndTime ? UnloadCarConstant.UNLOAD_CAR_COMPLETE_TIMEOUT : UnloadCarConstant.UNLOAD_CAR_COMPLETE_NORMAL);
                }
            }
        }
        try {
            int count = unloadCarDao.updateUnloadCarTaskStatus(unloadCar);
            if (count < 1) {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
                logger.error("修改任务状态失败，请求信息：{}",JsonHelper.toJson(unloadCarTaskReq));
                return result;
            }
        } catch (LoadIllegalException e) {
            logger.error("修改任务状态失败，e=", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(e.getMessage());
            return result;
        }

        if (UnloadCarStatusEnum.UNLOAD_CAR_END.getType() == unloadCarTaskReq.getTaskStatus()) {
            UnloadCarCompleteMqDto dto = new UnloadCarCompleteMqDto();
            dto.setSealCarCode(unloadCarTaskReq.getTaskCode());
            dto.setStatus(3);
            try {
                unloadCompleteProducer.send(dto.getSealCarCode(), JsonHelper.toJson(dto));
            } catch (JMQException e) {
                logger.error("卸车完成消息异常,封车号={}", unloadCarTaskReq.getTaskCode(), e);
            }
        }
        return this.getUnloadCarTask(unloadCarTaskReq);
    }

    @Override
    public InvokeResult<Void> startUnloadTask(UnloadCarTaskReq unloadCarTaskReq) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setSealCarCode(unloadCarTaskReq.getTaskCode());
        unloadCar.setStatus(UnloadCarStatusEnum.UNLOAD_CAR_STARTED.getType());
        if (!unloadCarTaskReq.getTaskCode().startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            unloadCar.setUnloadUserErp(unloadCarTaskReq.getUser().getUserErp());
            unloadCar.setEndSiteCode(unloadCarTaskReq.getCurrentOperate().getSiteCode());
        }
        unloadCar.setUpdateUserErp(unloadCarTaskReq.getUser().getUserErp());
        unloadCar.setUpdateUserName(unloadCarTaskReq.getUser().getUserName());
        Date updateTime = DateHelper.parseDate(unloadCarTaskReq.getOperateTime());
        unloadCar.setUpdateTime(updateTime);
        //更新任务模式
        if (null != unloadCarTaskReq.getType()) {
            unloadCar.setType(unloadCarTaskReq.getType());
        }
        try {
            int count = unloadCarDao.updateUnloadCarTaskStatus(unloadCar);
            if (count < 1) {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
                logger.error("修改任务状态失败，请求信息：{}", JsonHelper.toJson(unloadCarTaskReq));
                return result;
            }
        } catch (LoadIllegalException e) {
            logger.error("修改任务状态失败，e=", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(e.getMessage());
            return result;
        }
        return result;
    }

    @Override
    public InvokeResult<List<HelperDto>> getUnloadCarTaskHelpers(String sealCarCode) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<HelperDto> helperDtos = new ArrayList<HelperDto>();
        result.setData(helperDtos);

        try {
            List<UnloadCarDistribution> unloadCarDistributions = unloadCarDistributionDao.selectUnloadCarTaskHelpers(sealCarCode);
            if (CollectionUtils.isEmpty(unloadCarDistributions)) {
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage("未查询到协助人");
                logger.warn("该任务：{}未查询到协助人",sealCarCode);
                return result;
            }
            for (UnloadCarDistribution unloadCarDistribution : unloadCarDistributions) {
                HelperDto helper = new HelperDto();
                helper.setHelperERP(unloadCarDistribution.getUnloadUserErp());
                helper.setHelperName(unloadCarDistribution.getUnloadUserName());
                helperDtos.add(helper);
            }
        } catch (Exception e) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            logger.error("该任务：{}查询协助人异常",sealCarCode);
            return result;
        }

        return result;
    }

    @Override
    public InvokeResult<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        List<HelperDto> helperDtos = new ArrayList<HelperDto>();
        result.setData(helperDtos);

        //校验操作人是否为卸车任务负责人
        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setSealCarCode(taskHelpersReq.getTaskCode());
        unloadCar.setUnloadUserErp(taskHelpersReq.getUser().getUserErp());
        List<UnloadCar> unloadCars = unloadCarDao.selectByUnloadCar(unloadCar);
        if (CollectionUtils.isEmpty(unloadCars)) {
            result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            result.setMessage("该操作仅限卸车负责人使用！");
            return result;
        }
        if (StringUtils.equals(taskHelpersReq.getUser().getUserErp(), taskHelpersReq.getHelperERP())) {
            result.parameterError("卸车协助人不允许添加本任务的负责人");
            return result;
        }
        try {
            if (taskHelpersReq.getOperateType() == OperateTypeEnum.DELETE_HELPER.getType()) {
                //删除协助人

                UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
                unloadCarDistribution.setSealCarCode(taskHelpersReq.getTaskCode());
                unloadCarDistribution.setUnloadUserErp(taskHelpersReq.getHelperERP());
                Date updateTime = DateHelper.parseDate(taskHelpersReq.getOperateTime());
                unloadCarDistribution.setUpdateTime(updateTime);

                if (!unloadCarDistributionDao.deleteUnloadCarTaskHelpers(unloadCarDistribution)) {
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage("删除协助人失败");
                    return result;
                }
            } else if (taskHelpersReq.getOperateType() == OperateTypeEnum.INSERT_HELPER.getType()) {

                //校验协助人的ERP
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(taskHelpersReq.getHelperERP());
                if (baseStaffSiteOrgDto == null || baseStaffSiteOrgDto.getStaffName() == null) {
                    logger.error("根据协助人的erp未查询到员工信息，请求信息：{}", JsonHelper.toJson(taskHelpersReq));
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage("未查询到员工信息");
                    return result;
                }

                //单个任务不超过20个人且添加协助人验重
                InvokeResult<List<HelperDto>> helpers = this.getUnloadCarTaskHelpers(taskHelpersReq.getTaskCode());
                if (helpers != null && helpers.getData() != null && helpers.getData().size() >= 20) {
                    logger.warn("根据任务号：{}查询到的协助人已经达到20人", taskHelpersReq.getTaskCode());
                    result.setCode(InvokeResult.RESULT_MULTI_ERROR);
                    result.setMessage("单个任务的协助人不能超过20个！");
                    return result;
                } else if (helpers != null && helpers.getData() != null && helpers.getData().size() > 0) {
                    List<HelperDto> helperDtoList = helpers.getData();
                    for (HelperDto helperDto : helperDtoList) {
                        if (taskHelpersReq.getHelperERP().equals(helperDto.getHelperERP())) {
                            logger.warn("根据任务号：{}查询到，该协助人已存在", taskHelpersReq.getTaskCode());
                            result.setCode(InvokeResult.RESULT_MULTI_ERROR);
                            result.setMessage("该协助人已添加！");
                            return result;
                        }
                    }
                }
                //添加协助人存入redis
                addUnloadCarHelperCache(taskHelpersReq.getUser().getUserErp(), taskHelpersReq.getHelperERP());
                //添加协助人
                UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
                unloadCarDistribution.setSealCarCode(taskHelpersReq.getTaskCode());
                unloadCarDistribution.setUnloadUserErp(taskHelpersReq.getHelperERP());
                unloadCarDistribution.setUnloadUserName(baseStaffSiteOrgDto.getStaffName());
                unloadCarDistribution.setUnloadUserType(1);
                unloadCarDistribution.setCreateTime(DateHelper.parseDate(taskHelpersReq.getOperateTime()));

                int count = unloadCarDistributionDao.add(unloadCarDistribution);
                if (count < 1) {
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage("添加协助人失败");
                    return result;
                }
            }
        } catch (LoadIllegalException ex) {
            logger.error("更新协助人发生异常：ex=", ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(ex.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("更新协助人发生异常：{}", JsonHelper.toJson(taskHelpersReq));
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }

        return this.getUnloadCarTaskHelpers(taskHelpersReq.getTaskCode());
    }

    /**
     * 卸车协助人数据存储redis, 历史协助人查询
     * @param mainUserErp  负责人erp
     * @param helperUserErp  协助人erp
     */
    private void addUnloadCarHelperCache(String mainUserErp, String helperUserErp) {
        if(StringUtils.isNotBlank(mainUserErp) && StringUtils.isNotBlank(helperUserErp)) {
            String key = CacheKeyConstants.CACHE_KEY_UNLOAD_MAIN_ERP + mainUserErp;
            List<String> helperErpCacheList = jimdbCacheService.getList(key, String.class);
            if (CollectionUtils.isEmpty(helperErpCacheList)) {
                List<String> helperListNew = new ArrayList<>();
                helperListNew.add(helperUserErp);
                jimdbCacheService.setEx(key, helperListNew, 30, TimeUnit.DAYS);
            }else {
                if(helperErpCacheList.contains(helperUserErp)) {
                    return;
                }
                if(helperErpCacheList.size() >= UnloadCarConstant.UNLOAD_HELPER_HISTORY_SIZE) {
                    helperErpCacheList.remove(0);
                }
                helperErpCacheList.add(helperUserErp);
                jimdbCacheService.setEx(key, helperErpCacheList, 30, TimeUnit.DAYS);
            }
            logger.info("test--log--创建人erp={}, 添加协助人erp={}, 已有协助人历史=【{}】", mainUserErp, helperUserErp,
                    JsonHelper.toJson(helperErpCacheList));
        }
    }

    @Override
    public JdCResponse<List<String>> getUnloadCarHistoryHelper(String erp) {
        String key = CacheKeyConstants.CACHE_KEY_UNLOAD_MAIN_ERP + erp;
        List<String> helperErpCacheList = jimdbCacheService.getList(key, String.class);
        JdCResponse<List<String>> jdcResponse = new JdCResponse<>();
        if(CollectionUtils.isEmpty(helperErpCacheList)) {
            if(logger.isInfoEnabled()) {
                logger.info("根据负责人erp={}查询历史协助人信息为空", erp);
            }
            jdcResponse.setMessage("查询历史记录为空");
        }else {
            jdcResponse.setData(helperErpCacheList);
        }
        jdcResponse.toSucceed();
        logger.info("test--log--根据ero查询历史协助人信息，返回{}", erp, JsonHelper.toJson(helperErpCacheList));
        return jdcResponse;
    }

    @Override
    public InvokeResult<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq) {
        InvokeResult<List<UnloadCarTaskDto>> result = new InvokeResult<>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        List<UnloadCarTaskDto> unloadCarTaskDtos = new ArrayList<>();
        result.setData(unloadCarTaskDtos);

        try {
            //根据责任人/协助人查找任务编码
            List<String> sealCarCodes = unloadCarDistributionDao.selectTasksByUser(taskHelpersReq.getUser().getUserErp());
            if (CollectionUtils.isEmpty(sealCarCodes)) {
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage("未查询到任务");
                return result;
            }
            //根据任务编码查询
            List<UnloadCar> unloadCars = unloadCarDao.getUnloadCarTaskScan(sealCarCodes);
            if (CollectionUtils.isEmpty(unloadCars)) {
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage("未查询到任务");
                return result;
            }
            int serialNumber = 1;
            for (UnloadCar unloadCar : unloadCars) {
                UnloadCarTaskDto unloadCarTaskDto = new UnloadCarTaskDto();
                unloadCarTaskDto.setSerialNumber(serialNumber);
                unloadCarTaskDto.setTaskCode(unloadCar.getSealCarCode());
                unloadCarTaskDto.setPlatformName(unloadCar.getRailWayPlatForm());
                unloadCarTaskDto.setCarCode(unloadCar.getVehicleNumber());
                unloadCarTaskDto.setBatchCode(unloadCar.getBatchCode() == null ? "" : unloadCar.getBatchCode());
                if (unloadCar != null && unloadCar.getBatchCode() != null) {
                    unloadCarTaskDto.setBatchNum(getBatchNumber(unloadCar.getBatchCode()));
                } else {
                    unloadCarTaskDto.setBatchNum(0);
                }
                unloadCarTaskDto.setPackageNum(unloadCar.getPackageNum() == null ? 0 : unloadCar.getPackageNum());
                unloadCarTaskDto.setWaybillNum(unloadCar.getWaybillNum() == null ? 0 : unloadCar.getWaybillNum());
                unloadCarTaskDto.setTaskStatus(unloadCar.getStatus());
                unloadCarTaskDto.setTaskStatusName(UnloadCarStatusEnum.getEnum(unloadCar.getStatus()).getName());
                unloadCarTaskDto.setType(unloadCar.getType());
                serialNumber++;
                unloadCarTaskDtos.add(unloadCarTaskDto);
            }
        } catch (Exception e) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            logger.error("根据责任人或协助人查询任务失败：{}",JsonHelper.toJson(taskHelpersReq));
            return result;
        }

        return result;
    }

    @Override
    public void distributeUnloadCarTask(TmsSealCar tmsSealCar) {
        //解封车消息报文
        //{"sealCarCode":"SC21051894360237","status":20,"operateUserCode":"zhengying34","operateUserName":"郑英","operateTime":"2021-05-18 11:26:50",
        // "sealCarType":30,"batchCodes":null,"transBookCode":null,"volume":null,"weight":null,"transWay":null,"vehicleNumber":"川AR8810","operateSiteId":821602,
        // "operateSiteCode":"028F029","operateSiteName":"成都新都分拣中心","warehouseCode":null,"largeCargoDetails":null,"pieceCount":null}
//          封车消息报文
//        {"sealCarCode":"SC21051894361044","status":10,"operateUserCode":"00491749","operateUserName":"陈诗铭","operateTime":"2021-05-18 11:26:55","sealCarType":30,
//        "batchCodes":["R1394494286313459712"],"transBookCode":null,"volume":null,"weight":null,"transWay":2,"vehicleNumber":"粤A9D2L6","operateSiteId":122503,
//        "operateSiteCode":"595Y013","operateSiteName":"泉州狮城营业部","warehouseCode":null,"largeCargoDetails":null,"pieceCount":null}
        // 根据封车编码查询卸车任务
        UnloadCar param = new UnloadCar();
        param.setSealCarCode(tmsSealCar.getSealCarCode());
        List<UnloadCar> unloadCars = unloadCarDao.selectByUnloadCar(param);
        //如果未查询到判断是否验收的卸车任务,是的话 需要根据创建卸车任务，并初始化相应的运单维度数据.
        if (CollectionUtils.isEmpty(unloadCars)) {
            UnloadCar unloadCar = new UnloadCar();
            logger.warn("消费解封车消息时，根据封车编码没有找到对应的卸车任务,接下来走补偿逻辑，tmsSealCar={}", JsonHelper.toJson(tmsSealCar));
            //解封车操作网点
            Integer curSiteId = tmsSealCar.getOperateSiteId();
            if (curSiteId == null) {
                logger.warn("封车编码【{}】解封车消息未写入操作网点,不写入卸车任务!", tmsSealCar.getSealCarCode());
                return ;
            }
            //封车网点
            Integer fromSiteId = null;
            //封车编码对应的批次号
            List<String> batchCodes = null;
            CommonDto<SealCarDto> sealCarDto = vosManager.querySealCarInfoBySealCarCode(tmsSealCar.getSealCarCode());
            if (CommonDto.CODE_SUCCESS == sealCarDto.getCode() && sealCarDto.getData() != null) {
                unloadCar.setEndSiteCode(sealCarDto.getData().getEndSiteId());
                unloadCar.setEndSiteName(sealCarDto.getData().getEndSiteName());
                batchCodes = sealCarDto.getData().getBatchCodes();
                fromSiteId = sealCarDto.getData().getSealSiteId();
            } else {
                logger.warn("调用运输的接口获取下游机构信息失败，请求体：{}，返回值：{}",tmsSealCar.getSealCarCode(),JsonHelper.toJson(sealCarDto));
                return ;
            }
            if (fromSiteId == null) {
                logger.warn("封车编码【{}】根据封车编码未获取到封车网点,不写入卸车任务!", tmsSealCar.getSealCarCode());
                return ;
            }
            if (batchCodes == null) {
                logger.warn("封车编码【{}】没有获取到对应的批次信息!", tmsSealCar.getSealCarCode());
                return ;
            }
            boolean isExpressCenterSite = this.isExpressCenterSite(curSiteId);
            //解封车时当前操作网点非快运网点的,不写入卸车任务
            if(!isExpressCenterSite){
                logger.warn("封车编码【{}】当前解封车网点非快运网点,不写入卸车任务!", tmsSealCar.getSealCarCode());
                return;
            }
            //写入卸车任务时,需要获取封车信息中的封车网点.
            tmsSealCar.setOperateSiteId(fromSiteId);
            tmsSealCar.setBatchCodes(batchCodes);
            List<UnloadScan> unloadScans = unloadScanDao.findUnloadScanBySealCarCode(tmsSealCar.getSealCarCode());
            if(CollectionUtils.isNotEmpty(unloadScans)){
                unloadCar.setWaybillNum(unloadScans.size());
                int totalForceAmount = 0;
                for(UnloadScan unloadScan : unloadScans){
                    totalForceAmount += unloadScan.getForceAmount();
                }
                unloadCar.setPackageNum(totalForceAmount);
                logger.warn("封车编码【{}】解封车创建卸车任务时已存在运单维度数据，暂不创建卸车运单维度数据!", tmsSealCar.getSealCarCode());
            }else{
                boolean unloadScanSaveFlag = this.batchSaveUnloadScan(tmsSealCar, unloadCar);
                if(!unloadScanSaveFlag){
                    logger.warn("封车编码【{}】解封车创建卸车任务时不存在运单维度数据，暂不创建卸车任务!", tmsSealCar.getSealCarCode());
                    return;
                }
            }
            //组装卸车任务参数
            unloadCar.setBatchCode(getStrByBatchCodes(new ArrayList<String>(batchCodes)));
            unloadCar.setSealCarCode(tmsSealCar.getSealCarCode());
            unloadCar.setVehicleNumber(tmsSealCar.getVehicleNumber());
            unloadCar.setSealTime(tmsSealCar.getOperateTime());
            unloadCar.setStartSiteCode(tmsSealCar.getOperateSiteId());
            unloadCar.setStartSiteName(tmsSealCar.getOperateSiteName());
            unloadCar.setCreateTime(new Date());
            unloadCar.setUnloadUserErp(tmsSealCar.getOperateUserCode());
            unloadCar.setUnloadUserName(tmsSealCar.getOperateUserName());
            unloadCar.setDistributeTime(new Date());
            unloadCar.setOperateUserErp(tmsSealCar.getOperateUserCode());
            unloadCar.setOperateUserName(tmsSealCar.getOperateUserName());
            unloadCar.setStatus(UnloadCarStatusEnum.UNLOAD_CAR_UN_START.getType());
            //同步卸车负责人与卸车任务之间关系
            UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
            unloadCarDistribution.setSealCarCode(tmsSealCar.getSealCarCode());
            unloadCarDistribution.setUnloadUserErp(tmsSealCar.getOperateUserCode());
            unloadCarDistribution.setUnloadUserName(tmsSealCar.getOperateUserName());
            unloadCarDistribution.setUnloadUserType(UnloadUserTypeEnum.UNLOAD_MASTER.getType());
            unloadCarDistribution.setCreateTime(new Date());
            try {
//                unloadCarDao.add(unloadCar);
                this.fillUnloadCarTaskDuration(unloadCar);
                unloadCarDistributionDao.add(unloadCarDistribution);
            } catch (Exception e) {
                logger.error("卸车任务数据插入失败，数据：{},返回值:{}",JsonHelper.toJson(tmsSealCar),e);
            }
            //自动生成任务逻辑走完,不论成功与否,不再重试，后续逻辑也不用走
            logger.warn("消费解封车消息时，根据封车编码没有找到对应的卸车任务,已触发卸车任务生成封车编码={}", tmsSealCar.getSealCarCode());
            return;
        }
        UnloadCar unloadCar = unloadCars.get(0);
        if (UnloadCarStatusEnum.UNLOAD_CAR_UN_DISTRIBUTE.getType() != unloadCar.getStatus()) {
            return;
        }
        // 卸车任务ID列表
        List<Integer> unloadCarIds = new ArrayList<>();
        unloadCarIds.add(unloadCar.getUnloadCarId().intValue());
        // 封车编码列表
        List<String> sealCarCodes = new ArrayList<>();
        sealCarCodes.add(tmsSealCar.getSealCarCode());

        // 组装分配卸车任务参数
        DistributeTaskRequest request = new DistributeTaskRequest();
        request.setUnloadCarIds(unloadCarIds);
        request.setUnloadUserErp(tmsSealCar.getOperateUserCode());
        request.setUnloadUserName(tmsSealCar.getOperateUserName());
        request.setRailWayPlatForm(null);
        request.setUpdateUserErp(tmsSealCar.getOperateUserCode());
        request.setUpdateUserName(tmsSealCar.getOperateUserName());
        request.setSealCarCodes(sealCarCodes);
        this.distributeTask(request);
    }

    private int getBatchNumber(String batchCode) {
        String[] batchList = batchCode.split(",");
        return batchList.length;
    }

    //将批次号的数组类型转换为字符串类型
    private String getStrByBatchCodes(List<String> batchCodes) {
        String sendCode = "";
        for(int i=0;i<batchCodes.size();i++){
            if(i < batchCodes.size()-1){
                sendCode += batchCodes.get(i) + ",";
            }else{
                sendCode += batchCodes.get(i);
            }
        }
        return sendCode;
    }

    @Override
    public InvokeResult<String> interceptValidateUnloadCar(String barCode, InvokeResult<UnloadScanDetailDto> dtoInvokeResult) {
        logger.info("UnloadCarServiceImpl-interceptValidateUnloadCar-barCode:{}",barCode);
        InvokeResult<String> result = new InvokeResult<String>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        if(StringUtils.isBlank(barCode)){
            return result;
        }

        //该处调用前已做对象初始化，无NPE
        Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();

        try{
            logger.info("interceptValidate卸车根据包裹号：{}",barCode);
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
            if(StringUtils.isNotBlank(waybillCode)) {
                //取消拦截校验
                JdCancelWaybillResponse jdResponse = waybillService.dealCancelWaybill(waybillCode);
                if (jdResponse != null && jdResponse.getCode() != null && !jdResponse.getCode().equals(JdResponse.CODE_OK)) {
                    logger.info("包裹【{}】所在运单已被拦截【{}】", barCode, jdResponse.getMessage());
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage(jdResponse.getMessage());
                    return result;
                }
            }
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if(baseEntity == null || baseEntity.getResultCode() != 1 || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null ){
                logger.error("interceptValidate卸车根据单号获取运单信息失败单号：{}",waybillCode);
                return result;
            }
            Waybill waybillNoCache = baseEntity.getData().getWaybill();
            String waybillSign = waybillNoCache.getWaybillSign();

            if(StringUtils.isBlank(waybillSign)){
                logger.error("interceptValidate卸车根据单号获取运单信息失败单号：{}",waybillCode);
                return result;
            }
            //信任运单标识
            boolean isTrust = BusinessUtil.isNoNeedWeight(waybillSign);
            //是否是KA的重量逻辑校验 66->3
            boolean isNewWeightLogic = BusinessUtil.needWeighingSquare(waybillSign);
            //纯配快运零担
            boolean isB2BPure = BusinessUtil.isCPKYLD(waybillSign);

            // 返单标识
            boolean isRefund = BusinessUtil.isRefund(waybillSign);

            //B网营业厅
            boolean isBnet = BusinessUtil.isBusinessHall(waybillSign);
            //waybillsign66位为3 增加新的拦截校验
            if(isNewWeightLogic){
                logger.info("waybillsign66为3增加新的拦截校验,barcode:{},waybillSin:{},result:{}",barCode,waybillSign, JsonUtils.toJson(result));
                result = kaWaybillCheck(barCode,waybillSign,result);
                //如果reusltcode不为200 说明已经被上面方法改变 校验不通过
                if(!Objects.equals(InvokeResult.RESULT_SUCCESS_CODE,result.getCode())){
                    return result;
                }
            }else{
                //无重量禁止发货判断
                if(!isTrust && isB2BPure && !isRefund){
                    if (waybillNoCache.getAgainWeight() == null ||  waybillNoCache.getAgainWeight() <= 0) {
                        logger.info("interceptValidate卸车无重量禁止发货单号：{}",waybillCode);
                        // warnMsg.put(UnloadCarWarnEnum.NO_WEIGHT_FORBID_SEND_MESSAGE.getLevel(), barCode + UnloadCarWarnEnum.NO_WEIGHT_FORBID_SEND_MESSAGE.getDesc());
                        warnMsg.put(UnloadCarWarnEnum.NO_WEIGHT_FORBID_SEND_MESSAGE.getLevel(), HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_WEAK_INTERCEPT, 0));
                    }
                }
                //寄付临欠
                boolean isSendPayTemporaryDebt = BusinessUtil.isJFLQ(waybillSign);
                if(!isTrust && isBnet && isSendPayTemporaryDebt && (waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0
                        || StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0)){
                    // 非返单才提示
                    if (!isRefund) {
                        logger.warn("interceptValidate卸车运费临时欠款无重量体积禁止发货单号：{}", waybillCode);
                        result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                        // result.setMessage(LoadIllegalException.FREIGTH_TEMPORARY_PAY_NO_WEIGHT_VOLUME_FORBID_SEND_MESSAGE);
                        result.setMessage(HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_WEAK_INTERCEPT, 0));
                        return result;
                    }
                }
            }

            //寄付
            boolean isSendPay = BusinessUtil.isWaybillConsumableOnlyConfirm(waybillSign);
            //B网营业厅（原单作废，逆向单不计费）
            boolean isBnetCancel = BusinessUtil.isYDZF(waybillSign);
            //B网营业厅（原单拒收因京东原因产生的逆向单，不计费）
            boolean isBnetJDCancel = BusinessUtil.isJDJS(waybillSign);
            //防疫物资绿色通道
            boolean isFYWZ = BusinessUtil.isFYWZ(waybillSign);
            //运费寄付无运费金额禁止发货
            if(isBnet && isSendPay && !isBnetCancel && !isBnetJDCancel && StringUtils.isNotBlank(waybillNoCache.getFreight()) && !NumberHelper.gt0(waybillNoCache.getFreight()) && !isFYWZ){
                logger.warn("interceptValidate卸车运费寄付无运费金额禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.FREIGTH_SEND_PAY_NO_MONEY_FORBID_SEND_MESSAGE);
                return result;
            }

            //是仓配零担
            boolean isWarehouse = BusinessUtil.isCPLD(waybillSign);
            //到付
            boolean isArrivePay = BusinessUtil.isDF(waybillSign);
            if((isB2BPure || isWarehouse) && isArrivePay && !isBnetCancel && !isBnetJDCancel && StringUtils.isNotBlank(waybillNoCache.getFreight()) && !NumberHelper.gt0(waybillNoCache.getFreight())){
                logger.warn("interceptValidate卸车运费到付无运费金额禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.FREIGTH_ARRIVE_PAY_NO_MONEY_FORBID_SEND_MESSAGE);
                return result;
            }


            //有包装服务
            if(waybillConsumableRecordService.needConfirmed(waybillCode)){
                logger.warn("interceptValidate卸车包装服务运单未确认包装完成禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.PACK_SERVICE_NO_CONFIRM_FORBID_SEND_MESSAGE);
                return result;
            }

            //金鹏订单
            if(!storagePackageMService.checkWaybillCanSend(waybillCode, waybillSign)){
                logger.warn("interceptValidate卸车金鹏订单未上架集齐禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.JIN_PENG_NO_TOGETHER_FORBID_SEND_MESSAGE);
                return result;
            }

            if(!businessHallFreightSendReceiveCheck(waybillCode, waybillSign)){
                logger.warn("interceptValidate卸车B网营业厅寄付未揽收完成禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.BNET_SEND_PAY_NO_RECEIVE_FINISH_MESSAGE);
                return result;
            }

        }catch (Exception e){
            logger.error("判断包裹拦截异常 {}",barCode,e);
        }
        return result;
    }


    @Override
    public InvokeResult<String> interceptValidateLoadCar(String barCode) {
        logger.info("UnloadCarServiceImpl-interceptValidateLoadCar-barCode:{}",barCode);
        InvokeResult<String> result = new InvokeResult<String>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        if(StringUtils.isBlank(barCode)){
            return result;
        }
        try{
            logger.info("interceptValidate卸车根据包裹号：{}",barCode);
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
            if(StringUtils.isNotBlank(waybillCode)) {
                //取消拦截校验
                JdCancelWaybillResponse jdResponse = waybillService.dealCancelWaybill(waybillCode);
                if (jdResponse != null && jdResponse.getCode() != null && !jdResponse.getCode().equals(JdResponse.CODE_OK)) {
                    logger.info("包裹【{}】所在运单已被拦截【{}】", barCode, jdResponse.getMessage());
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage(jdResponse.getMessage());
                    return result;
                }
            }
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if(baseEntity == null || baseEntity.getResultCode() != 1 || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null ){
                logger.error("interceptValidate卸车根据单号获取运单信息失败单号：{}",waybillCode);
                return result;
            }
            Waybill waybillNoCache = baseEntity.getData().getWaybill();
            String waybillSign = waybillNoCache.getWaybillSign();

            if(StringUtils.isBlank(waybillSign)){
                logger.error("interceptValidate卸车根据单号获取运单信息失败单号：{}",waybillCode);
                return result;
            }
            //信任运单标识
            boolean isTrust = BusinessUtil.isNoNeedWeight(waybillSign);
            //是否是KA的重量逻辑校验 66->3
            boolean isNewWeightLogic = BusinessUtil.needWeighingSquare(waybillSign);
            //纯配快运零担
            boolean isB2BPure = BusinessUtil.isCPKYLD(waybillSign);

            // 返单标识
            boolean isRefund = BusinessUtil.isRefund(waybillSign);

            //B网营业厅
            boolean isBnet = BusinessUtil.isBusinessHall(waybillSign);
            //waybillsign66位为3 增加新的拦截校验
            if(isNewWeightLogic){
                logger.info("waybillsign66为3增加新的拦截校验,barcode:{},waybillSin:{},result:{}",barCode,waybillSign, JsonUtils.toJson(result));
                result = kaWaybillCheck(barCode,waybillSign,result);
                //如果reusltcode不为200 说明已经被上面方法改变 校验不通过
                if(!Objects.equals(InvokeResult.RESULT_SUCCESS_CODE,result.getCode())){
                    return result;
                }
            }else{
                //无重量禁止发货判断
                if(!isTrust && isB2BPure && !isRefund){
                    if (waybillNoCache.getAgainWeight() == null ||  waybillNoCache.getAgainWeight() <= 0) {
                        logger.info("interceptValidate卸车无重量禁止发货单号：{}",waybillCode);
                        result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                        // result.setMessage(LoadIllegalException.NO_WEIGHT_FORBID_SEND_MESSAGE);
                        result.setMessage(HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_OR_VOLUME, 0));
                        return result;
                    }
                }
                //寄付临欠
                boolean isSendPayTemporaryDebt = BusinessUtil.isJFLQ(waybillSign);
                if(!isTrust && isBnet && isSendPayTemporaryDebt && (waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0
                        || StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0)){
                    // 非返单才提示
                    if (!isRefund) {
                        logger.warn("interceptValidate卸车运费临时欠款无重量体积禁止发货单号：{}", waybillCode);
                        result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                        // result.setMessage(LoadIllegalException.FREIGTH_TEMPORARY_PAY_NO_WEIGHT_VOLUME_FORBID_SEND_MESSAGE);
                        result.setMessage(HintService.getHintWithFuncModule(HintCodeConstants.WAYBILL_WITHOUT_WEIGHT_OR_VOLUME, 0));
                        return result;
                    }
                }
            }

            //寄付
            boolean isSendPay = BusinessUtil.isWaybillConsumableOnlyConfirm(waybillSign);
            //B网营业厅（原单作废，逆向单不计费）
            boolean isBnetCancel = BusinessUtil.isYDZF(waybillSign);
            //B网营业厅（原单拒收因京东原因产生的逆向单，不计费）
            boolean isBnetJDCancel = BusinessUtil.isJDJS(waybillSign);
            //防疫物资绿色通道
            boolean isFYWZ = BusinessUtil.isFYWZ(waybillSign);
            //运费寄付无运费金额禁止发货
            if(isBnet && isSendPay && !isBnetCancel && !isBnetJDCancel && StringUtils.isNotBlank(waybillNoCache.getFreight()) && !NumberHelper.gt0(waybillNoCache.getFreight()) && !isFYWZ){
                logger.warn("interceptValidate卸车运费寄付无运费金额禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.FREIGTH_SEND_PAY_NO_MONEY_FORBID_SEND_MESSAGE);
                return result;
            }

            //是仓配零担
            boolean isWarehouse = BusinessUtil.isCPLD(waybillSign);
            //到付
            boolean isArrivePay = BusinessUtil.isDF(waybillSign);
            if((isB2BPure || isWarehouse) && isArrivePay && !isBnetCancel && !isBnetJDCancel && StringUtils.isNotBlank(waybillNoCache.getFreight()) && !NumberHelper.gt0(waybillNoCache.getFreight())){
                logger.warn("interceptValidate卸车运费到付无运费金额禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.FREIGTH_ARRIVE_PAY_NO_MONEY_FORBID_SEND_MESSAGE);
                return result;
            }


            //有包装服务
            if(waybillConsumableRecordService.needConfirmed(waybillCode)){
                logger.warn("interceptValidate卸车包装服务运单未确认包装完成禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.PACK_SERVICE_NO_CONFIRM_FORBID_SEND_MESSAGE);
                return result;
            }

            //金鹏订单
            if(!storagePackageMService.checkWaybillCanSend(waybillCode, waybillSign)){
                logger.warn("interceptValidate卸车金鹏订单未上架集齐禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.JIN_PENG_NO_TOGETHER_FORBID_SEND_MESSAGE);
                return result;
            }

            if(!businessHallFreightSendReceiveCheck(waybillCode, waybillSign)){
                logger.warn("interceptValidate卸车B网营业厅寄付未揽收完成禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.BNET_SEND_PAY_NO_RECEIVE_FINISH_MESSAGE);
                return result;
            }

        }catch (Exception e){
            logger.error("判断包裹拦截异常 {}",barCode,e);
        }
        return result;
    }

    /***
     * ka货物重量校验逻辑
     * @param barCode     包裹编号
     * @param waybillSign
     * @param result
     * @return
     */
    @Override
    public InvokeResult<String> kaWaybillCheck(String barCode, String waybillSign, InvokeResult<String> result)  {
        DeliveryPackageD deliveryPackageD = waybillPackageManager.getPackageInfoByPackageCode(barCode);
        if(deliveryPackageD != null){
            //非信任重量  信任重量不做重量体积拦截.---去除 信任非信任的判断逻辑，直接按照业务类型是否进行称重进行判断。
//            if(!Objects.equals(Constants.isTrust,deliveryPackageD.getTrustType())){
                //是否需要校验体重 业务类型1
                if(BusinessUtil.isNeedCheckWeightOrNo(waybillSign)){
                    if(deliveryPackageD.getAgainWeight() == null || deliveryPackageD.getAgainWeight()<=0){
                        logger.info("此包裹{}无重量体积，请到转运工作台按包裹录入重量体积",barCode);
                        result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                        result.setMessage(HintService.getHint(HintCodeConstants.ZY_PACKAGE_WITHOUT_WEIGHT_OR_VOLUME));
                        return result;
                    }
                }
                //是否需要校验体重 业务类型2
                if(BusinessUtil.isNeedCheckWeightBusiness2OrNo(waybillSign)){
                    if(deliveryPackageD.getAgainWeight() == null || deliveryPackageD.getAgainWeight()<=0){
                        logger.info("此包裹{}无重量体积，请到转运工作台按包裹录入重量体积",barCode);
                        result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                        result.setMessage(HintService.getHint(HintCodeConstants.ZY_PACKAGE_WITHOUT_WEIGHT_OR_VOLUME));
                        return result;
                    }
                }
//            }
        }
        return result;
    }


    /**
     * B网营业厅增加寄付揽收完成校验
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    private boolean businessHallFreightSendReceiveCheck(String waybillCode,String waybillSign) {
        if(! BusinessUtil.isBusinessHallFreightSendAndForward(waybillSign)) {
            return Boolean.TRUE;
        }
        Set<Integer> stateSet = new HashSet<>();
        stateSet.add(Constants.WAYBILL_TRACE_STATE_RECEIVE);
        List result = waybillTraceManager.getAllOperationsByOpeCodeAndState(waybillCode, stateSet);
        return com.jd.service.common.utils.CollectionUtils.isNotEmpty(result)? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     *拦截设置缓存
     * @param sealCarCode
     * @param borCode
     */
    private void setCacheOfSealCarAndPackageIntercet(String sealCarCode, String borCode){
        try {
            String key = CacheKeyConstants.REDIS_PREFIX_SEAL_PACK_INTERCEPT + sealCarCode + Constants.SEPARATOR_HYPHEN + borCode;
            redisClientCache.setEx(key, borCode,7,TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("设置封车【{}】包裹【{}】拦截重复缓存异常",sealCarCode,borCode,e);
        }
    }

    /**
     * 【工具】-【功能开关配置】-【验货】名单，判断PDA登录ERP或登录ERP所属场地是否有配置验货白名单
     */
    private boolean hasInspectFunction(Integer createSiteId, String userErp) {
        // 验货功能
        Integer menuCode = FuncSwitchConfigEnum.FUNCTION_INSPECTION.getCode();
        // 场地维度
        int dimensionCode = DimensionEnum.SITE.getCode();
        // 查询当前扫描人所在场地是否有验货权限
        boolean flag = funcSwitchConfigService.checkIsConfiguredWithCache(menuCode, createSiteId, dimensionCode, null);
        // 如果场地没有权限，则查询个人是否配置
        if (!flag) {
            // 个人维度
            dimensionCode = DimensionEnum.PERSON.getCode();
            flag = funcSwitchConfigService.checkIsConfiguredWithCache(menuCode, createSiteId, dimensionCode, userErp);
        }
        return flag;
    }

    private boolean lock(String sealCarCode, String waybillCode) {
        String lockKey = UNLOAD_SCAN_LOCK_BEGIN + sealCarCode + "_" + waybillCode;
        logger.info("开始获取锁lockKey={}", lockKey);
        try {
            if (!jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, WAYBILL_LOCK_TIME, TimeUnit.SECONDS)) {
                Thread.sleep(100);
                return jimdbCacheService.setNx(lockKey, StringUtils.EMPTY, WAYBILL_LOCK_TIME, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("卸车扫描unLock异常:sealCarCode={},waybillCode={},e=", sealCarCode, waybillCode, e);
            jimdbCacheService.del(lockKey);
        }
        return true;
    }

    private void unLock(String sealCarCode, String waybillCode) {
        String lockKey = UNLOAD_SCAN_LOCK_BEGIN + sealCarCode + "_" + waybillCode;
        try {
            jimdbCacheService.del(lockKey);
        } catch (Exception e) {
            logger.error("卸车扫描unLock异常:sealCarCode={},waybillCode={},e=", sealCarCode, waybillCode, e);
        } finally {
            jimdbCacheService.del(lockKey);
        }
    }

    /**
     * 设置运单重量和体积
     * @param unloadScan 运单暂存对象
     */
    private void setWeightAndVolume(UnloadScan unloadScan) {
        // 查询运单详情
        Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(unloadScan.getWaybillCode());
        if (waybill != null) {
            Double weight = waybill.getAgainWeight();
            String volume = waybill.getSpareColumn2();
            logger.info("卸车-设置运单重量和体积:sealCarCode={},waybillCode={},复重={},复量方={}|原重={},原量方={}", unloadScan.getSealCarCode(),
                    unloadScan.getWaybillCode(), weight, volume, waybill.getGoodWeight(), waybill.getGoodVolume());
            // 复重：againWeight 无值则取重量：goodWeight
            unloadScan.setWeight(weight == null ? waybill.getGoodWeight() : weight);
            // 复量方：spareColumn2 无值则取体积：goodVolume
            unloadScan.setVolume(StringUtils.isBlank(volume) ? waybill.getGoodVolume() : Double.parseDouble(volume));
        } else {
            logger.error("卸车-设置运单重量和体积--查询运单接口返回空:sealCarCode={},waybillCode={}", unloadScan.getSealCarCode(),
                    unloadScan.getWaybillCode());
        }
    }

    /**
     * 判断当前操作场地是否是快运中心
     * @param dmsSiteId 当前场地ID
     */
    public boolean isExpressCenterSite(Integer dmsSiteId) {
        // 根据当前网点ID查询网点基础信息
        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(dmsSiteId);
        if (siteOrgDto == null) {
            logger.error("根据封车消息创建卸车任务--isExpressCenterSite--查询基础资料信息为空dmsSiteId[{}]", dmsSiteId);
            return false;
        }
        if (EXPRESS_CENTER_SITE_ID.equals(siteOrgDto.getSubType())) {
            return true;
        }
        return false;
    }

    /**
     * 卸车任务流水线模式扫描接口
     *
     * @param request
     * @return
     */
    @Override
    public InvokeResult<UnloadScanDetailDto> assemblyLineScan(UnloadCarScanRequest request, InvokeResult<UnloadScanDetailDto> dtoInvokeResult) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        result.setData(convertToUnloadCarResult(request));
        // 判断当前扫描人员是否有按单操作权限
        if (hasInspectFunction(request.getOperateSiteCode(), request.getOperateUserErp())) {
            result.getData().setWaybillAuthority(GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("卸车扫描：参数request={}", JsonHelper.toJson(request));
        }
//        InvokeResult<UnloadScanDetailDto> dtoInvokeResult = new InvokeResult<>();
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCodeWithStatus(request.getSealCarCode());
            if (unloadCar == null) {
                buildUnloadScanResponse(result.getData(), dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return dtoInvokeResult;
            }
            if (unloadCar.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_UN_START.getType())) {
                BeanUtils.copyProperties(result, dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "任务未开始，请联系卸车负责人开始任务");
                return dtoInvokeResult;
            }
            if (unloadCar.getStatus().equals(UnloadCarStatusEnum.UNLOAD_CAR_END.getType())) {
                BeanUtils.copyProperties(result, dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "任务已结束，扫描失败");
                return dtoInvokeResult;
            }
            // 包裹是否扫描成功
            packageIsScanBoard(request);
            // 多货包裹标识
            boolean isSurplusPackage = false;
            // 验货校验
            inspectionIntercept(request);

            // 获取锁
            if (!lock(request.getSealCarCode(), waybillCode)) {
                logger.warn("新版包裹卸车扫描接口--获取锁失败：sealCarCode={},packageCode={}", request.getSealCarCode(), request.getBarCode());
                dtoInvokeResult.customMessage(JdCResponse.CODE_FAIL, "多人同时操作该包裹所在的运单，请稍后重试！");
                return dtoInvokeResult;
            }
            // 是否多货包裹校验
            isSurplusPackage = surfacePackageCheck(request, result,dtoInvokeResult);
            // 保存包裹卸车记录和运单暂存
            saveUnloadDetail(request, isSurplusPackage, unloadCar);


            //拦截校验
            InvokeResult<String> interceptResult = interceptValidateUnloadCar(request.getBarCode(), dtoInvokeResult);
            if (interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
                dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                return dtoInvokeResult;
            }


            //是否专网标识  专网不组板
            boolean privateNetworkFlag = false;
            if(privateNetworkCheck(waybillCode)) {
                privateNetworkFlag = true;
                Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();
                warnMsg.put(UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getLevel(), UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getDesc());
            }
            boolean tempStorageFlag = false;
            // 增加运单暂存校验，如果支持暂存：只验收包裹、不组板 直接返回提示语
            if (waybillStagingCheckManager.stagingCheck(request.getBarCode(), request.getOperateSiteCode())) {
                tempStorageFlag = true;
                Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();
                warnMsg.put(UnloadCarWarnEnum.PDA_STAGING_CONFIRM_MESSAGE.getLevel(), UnloadCarWarnEnum.PDA_STAGING_CONFIRM_MESSAGE.getDesc());
            }
            //添加跨越
            //跨越-增加目的转运中心+自提 校验  dtoInvokeResult返回话术
            String kyResult = kyexpressCheck(request,waybillCode);
            if(StringUtils.isNotBlank(kyResult)){
                if(logger.isInfoEnabled()) {
                    logger.info("跨越kyexpressCheck-return-assemblyLineScan,waybillCode为:{}",waybillCode);
                }
                dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, kyResult);
                return dtoInvokeResult;
            }
            if(privateNetworkFlag || tempStorageFlag){
                if(logger.isInfoEnabled()) {
                    logger.info("packageCodeScanNew--卸车流水线扫描包裹=【{}】，校验是否专网=【{}】, 是否暂存=【{}】",
                            request.getBarCode(), privateNetworkFlag, tempStorageFlag);
                }
                return dtoInvokeResult;
            }
            buildUnloadScanResponse(result.getData(), dtoInvokeResult);
            setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());

            // 获取卸车运单扫描信息
            setUnloadScanDetailList(result.getData(), dtoInvokeResult, request.getSealCarCode());
        } catch (LoadIllegalException e) {
            dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, e.getMessage());
            return dtoInvokeResult;
        } catch (Exception e) {
            if (e instanceof UnloadPackageBoardException) {
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PACKAGE_ALREADY_BIND, e.getMessage());
                return dtoInvokeResult;
            }
            logger.error("packageCodeScanNew接口发生异常：e=", e);
            dtoInvokeResult.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.SERVER_ERROR_MESSAGE);
        } finally {
            // 释放锁
            unLock(request.getSealCarCode(), waybillCode);
        }
        return dtoInvokeResult;
    }

    /**
     * 校验是否为专网单
     * @param businessCode   单号
     * @param dtoInvokeResult  返回结果
     * @param scanDimension  单号维度： 1-包裹号； 2-运单号
     */
    private Boolean privateNetworkCheck(String businessCode, InvokeResult<UnloadScanDetailDto> dtoInvokeResult, int scanDimension) {
        if(StringUtils.isBlank(businessCode)) {
            if(logger.isInfoEnabled()) {
                logger.info("UnloadCarServiceImpl.privateNetworkCheck--warn--校验是否为专网单子时，单号businessCode为空， 参数dtoInvokeResult=【{}】", JsonHelper.toJson(businessCode));
            }
            return false;
        }
        String waybillCode = (scanDimension == 1) ? WaybillUtil.getWaybillCode(businessCode) : ((scanDimension == 2) ? businessCode : "");
        if(StringUtils.isBlank(waybillCode) || !WaybillUtil.isWaybillCode(waybillCode)) {
            if(logger.isInfoEnabled()) {
                logger.info("UnloadCarServiceImpl.privateNetworkCheck--warn--校验是否为专网单子时，单号=【{}】转化为运单号=【{}】异常, 单号维度值（1-包裹号；2-运单号）scanDimension=【{}】", businessCode, waybillCode, scanDimension);
            }
            return false;
        }

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryWaybillT(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);

        if(baseEntity == null || baseEntity.getResultCode() != 1 || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null ){
            logger.error("UnloadCarServiceImpl.privateNetworkCheck--error--校验是否为专网运单失败，运单号=【{}】, 返回结果=【{}】", waybillCode, baseEntity == null ? "" : JsonHelper.toJson(baseEntity));
            dtoInvokeResult.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            String temp = (scanDimension == 1) ? "包裹" : ((scanDimension == 2) ? "运单" : "");
            dtoInvokeResult.setMessage(temp + "【" + businessCode + "】已经扫描成功，专网运单识别出现异常");
            return false;
        }
        Waybill waybill = baseEntity.getData().getWaybill();
        String waybillSign = waybill.getWaybillSign();
        if(StringUtils.isBlank(waybillSign)){
            logger.error("UnloadCarServiceImpl.privateNetworkCheck--error--校验是否为专网运单时未查询到运单waybillSign标识，运单号=【{}】, 返回结果=【{}】", waybillCode, JsonHelper.toJson(baseEntity));
            dtoInvokeResult.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            String temp = (scanDimension == 1) ? "包裹" : ((scanDimension == 2) ? "运单" : "");
            dtoInvokeResult.setMessage(temp + "【" + businessCode + "】已经扫描成功，专网运单识别出现异常, 原因是未发现该运单打标信息");
            return false;
        }

        //是否专网
        if(BusinessUtil.isPrivateNetwork(waybillSign)) {
            Map<String, String> warnMsg = dtoInvokeResult.getData().getWarnMsg();
            warnMsg.put(UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getLevel(), UnloadCarWarnEnum.PRIVATE_NETWORK_PACKAGE.getDesc());
            return true;
        }
        return false;
    }

    /**
     * 根据运单号校验专网，，true为专网
     * @param waybillCode
     * @return
     */
    private boolean privateNetworkCheck(String waybillCode) {
        if(StringUtils.isBlank(waybillCode)) {
            return false;
        }

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(true);
        wChoice.setQueryWaybillM(true);
        wChoice.setQueryWaybillT(true);
        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);

        if(baseEntity == null || baseEntity.getResultCode() != 1 || baseEntity.getData() == null || baseEntity.getData().getWaybill() == null ){
            if(logger.isInfoEnabled()) {
                logger.info("UnloadCarServiceImpl.privateNetworkCheck1--warn--根据运单号校验是否为专网运单失败，运单号=【{}】, 返回结果=【{}】", waybillCode, baseEntity == null ? "" : JsonHelper.toJson(baseEntity));
            }
            return false;
        }
        Waybill waybill = baseEntity.getData().getWaybill();
        String waybillSign = waybill.getWaybillSign();
        if(StringUtils.isBlank(waybillSign)){
            logger.error("UnloadCarServiceImpl.privateNetworkCheck1--error--校验是否为专网运单时未查询到运单waybillSign标识，运单号=【{}】, 返回结果=【{}】", waybillCode, JsonHelper.toJson(baseEntity));
            return false;
        }
        //是否专网
        if(BusinessUtil.isPrivateNetwork(waybillSign)) {
            return true;
        }
        return false;
    }


    public void fillUnloadCarTaskDuration(UnloadCar unloadCar){
        //时效
        Integer duration = 0;
        if(unloadCar.getTransWay() != null && StringUtils.isNotBlank(unloadCar.getVehicleNumber())) {
            BasicVehicleDto basicVehicleDto = basicQueryWSManager.getVehicleByVehicleNumber(unloadCar.getVehicleNumber());
            if(basicVehicleDto != null && basicVehicleDto.getVehicleType() != null) {
                Integer vehicleType = basicVehicleDto.getVehicleType();
                //todo zcf 线路类型待定
                Integer lineType = 123;
                //todo zcf 车型+运输方式+线路类型 调用路由jsf接口获取时效   待定
                duration = 0;
            }
        }
        if(duration <= 0) {
            unloadCar.setDuration(UnloadCarConstant.UNLOAD_CAR_DURATION_DEFAULT);
            unloadCar.setDurationType(UnloadCarConstant.UNLOAD_CAR_DURATION_TYPE_DEFAULT);
        }else {
            unloadCar.setDuration(duration);
            unloadCar.setDurationType(UnloadCarConstant.UNLOAD_CAR_DURATION_TYPE_ROUTE);
        }
        unloadCarDao.add(unloadCar);
    }

    @Override
    public UnloadCarTaskDto getUnloadCarTaskDuration(UnloadCarTaskReq unloadCarTaskReq) {
        UnloadCarTaskDto res = new UnloadCarTaskDto();
        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setSealCarCode(unloadCarTaskReq.getTaskCode());
        List<UnloadCar> list = unloadCarDao.selectByCondition(unloadCar);
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        UnloadCar dbRes = list.get(0);
        res.setCarCode(dbRes.getSealCarCode());
        Long startTime = dbRes.getStartTime() == null ? null : dbRes.getStartTime().getTime();
        res.setStartTime(startTime);
        //时效：单位min
        Integer duration = dbRes.getDuration();
        Long planEndTime = startTime == null ? null : startTime + duration * 60 * 1000l;
        res.setPlanEndTime(planEndTime);
        return res;
    }
    @Override
    public List<UnloadCar> getTaskInfoBySealCarCodes(List<String> sealCarCodes) {
        return unloadCarDao.getTaskInfoBySealCarCodes(sealCarCodes);
    }

    @Override
    public List<String> newAppOperateIntercept(List<String> sealCarCodeList) {
        if(CollectionUtils.isEmpty(sealCarCodeList)) {
            return null;
        }
        List<String> res = new ArrayList<>();
        for(String sealCarCode : sealCarCodeList) {
            String key = TransportServiceConstants.CACHE_PREFIX_PDA_ACTUAL_OPERATE_VERSION + sealCarCode;
            String pdaVersion = redisClientOfJy.get(key);
            if(pdaVersion != null && !AppVersionEnums.PDA_OLD.getVersion().equals(pdaVersion)) {
                res.add(sealCarCode);
            }
        }
        return res;
    }

}
