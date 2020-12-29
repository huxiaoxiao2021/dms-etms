package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.unloadCar.*;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.service.impl.LoadScanServiceImpl;
import com.jd.bluedragon.distribution.loadAndUnload.*;
import com.jd.bluedragon.distribution.loadAndUnload.dao.*;
import com.jd.bluedragon.distribution.loadAndUnload.domain.DistributeTaskRequest;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.exception.UnloadPackageBoardException;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
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
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

    @Value("${unload.board.bindings.count.max:50}")
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
    private UnloadCarDao unloadCarDao;

    @Autowired
    private UnloadCarTransBoardDao unloadCarTransBoardDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private UnloadCarDistributionDao unloadCarDistributionDao;

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
    private WaybillService waybillService;


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
        List<UnloadScan> unloadScans = unloadScanDao.findUnloadScanByBySealCarCode(sealCarCode);
        if (CollectionUtils.isNotEmpty(unloadScans)) {
            unloadScanDetailDto.setTotalWaybillNum(unloadScans.size());
            // 已卸运单数
            int loadWaybillNum = 0;
            // 已卸包裹数
            int loadPackageNum = 0;
            // 已扫运单数
            int scanWaybillNum = 0;
            // 已扫包裹数
            int scanPackageNum = 0;
            // 封车任务下总包裹数
            int totalPackageNum = 0;
            List<UnloadScanDto> unloadScanDtoList = new ArrayList<>();
            for (UnloadScan unloadScan : unloadScans) {
                // 所有应卸加起来就是总包裹数
                totalPackageNum = totalPackageNum + unloadScan.getForceAmount();
                // 统计已扫数据
                if (unloadScan.getLoadAmount() > 0) {
                    scanWaybillNum = scanWaybillNum + 1;
                    scanPackageNum = scanPackageNum + unloadScan.getLoadAmount();
                    // 统计已卸数据，应卸=已卸
                    if (unloadScan.getForceAmount().equals(unloadScan.getLoadAmount())) {
                        loadWaybillNum = loadWaybillNum + 1;
                        loadPackageNum = loadPackageNum + unloadScan.getLoadAmount();
                    }
                }
                // 转换数据
                unloadScanDtoList.add(convertData(unloadScan));
            }
            unloadScanDetailDto.setTotalPackageNum(totalPackageNum);
            unloadScanDetailDto.setUnloadWaybillAmount(unloadScans.size() - scanWaybillNum);
            unloadScanDetailDto.setUnloadPackageAmount(unloadScanDetailDto.getTotalPackageNum() - scanPackageNum);
            // 按照指定次序排列
            Collections.sort(unloadScanDtoList, new Comparator<UnloadScanDto>() {
                @Override
                public int compare(UnloadScanDto o1, UnloadScanDto o2) {
                    return o2.getStatus().compareTo(o1.getStatus());
                }
            });
            unloadScanDetailDto.setUnloadScanDtoList(unloadScanDtoList);
        }
    }
    
    private UnloadScanDto convertData(UnloadScan unloadScan) {
        UnloadScanDto unloadScanDto = new UnloadScanDto();
        unloadScanDto.setWayBillCode(unloadScan.getWaybillCode());
        unloadScanDto.setPackageAmount(unloadScan.getPackageAmount());
        unloadScanDto.setForceAmount(unloadScan.getForceAmount());
        unloadScanDto.setLoadAmount(unloadScan.getLoadAmount());
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
        logger.info("卸车扫描1：参数request={}", JsonHelper.toJson(request));
        try {
            // 包裹是否扫描成功
            packageIsScanBoard(request);
            if(!request.getIsForceCombination()){
                // 验货校验
                inspectionIntercept(request);
                // 路由校验、生成板号
                routerCheck(request,result);
                BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
                BeanUtils.copyProperties(request,boardCommonRequest);
                // 是否发货校验
                boardCommonManager.isSendCheck(boardCommonRequest);
                // 包裹数限制
                boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);
                // 是否多货包裹校验
                surfacePackageCheck(request,result);
                // ver组板拦截
                InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
                if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    result.customMessage(invokeResult.getCode(),invokeResult.getMessage());

                    return result;
                }
                //拦截校验
                InvokeResult<String> interceptResult = interceptValidateUnloadCar(request.getBarCode());
                if(interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                    return result;
                }
            }else {
                surfacePackageCheck(request,result);
            }

            if(StringUtils.isEmpty(request.getBoardCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,LoadIllegalException.BOARD_NOTE_EXIST_INTERCEPT_MESSAGE);
                return result;
            }
            // 多货包裹标识
            boolean isSurplusPackage = false;
            if(result.getCode() == CODE_SUCCESS_HIT){
                isSurplusPackage = true;
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
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }


    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.packageCodeScan",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadCarScanResult> packageCodeScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        result.setData(convertToUnloadCarResult(request));
        logger.info("卸车扫描1：参数request={}", JsonHelper.toJson(request));
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
            if(!request.getIsForceCombination()){
                // 验货校验
                inspectionIntercept(request);
                // 路由校验、生成板号
                routerCheck(request,result);
                BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
                BeanUtils.copyProperties(request,boardCommonRequest);
                // 是否发货校验
                boardCommonManager.isSendCheck(boardCommonRequest);
                // 包裹数限制
                boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);
                // 是否多货包裹校验
                surfacePackageCheck(request,result);
                // ver组板拦截
                InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
                if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    result.customMessage(invokeResult.getCode(),invokeResult.getMessage());

                    return result;
                }
                //拦截校验
                InvokeResult<String> interceptResult = interceptValidateUnloadCar(request.getBarCode());
                if(interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                    return result;
                }
            }else {
                surfacePackageCheck(request,result);
            }

            if(StringUtils.isEmpty(request.getBoardCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,LoadIllegalException.BOARD_NOTE_EXIST_INTERCEPT_MESSAGE);
                return result;
            }
            // 多货包裹标识
            boolean isSurplusPackage = false;
            if(result.getCode() == CODE_SUCCESS_HIT){
                isSurplusPackage = true;
            }

            // 卸车处理并回传TC组板关系
            dealUnloadAndBoxToBoard(request,isSurplusPackage);
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            // 查询包裹所在批次号
            String sendCode = getBatchCode(waybillCode, request.getOperateSiteCode());
            // 保存包裹卸车记录和运单暂存
            saveUnloadDetail(request, isSurplusPackage, sendCode, unloadCar.getVehicleNumber());
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
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.packageCodeScan",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadScanDetailDto> packageCodeScanNew(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        result.setData(convertToUnloadCarResult(request));
        logger.info("卸车扫描：参数request={}", JsonHelper.toJson(request));
        InvokeResult<UnloadScanDetailDto> dtoInvokeResult = new InvokeResult<>();
        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(request.getSealCarCode());
            if (unloadCar == null) {
                BeanUtils.copyProperties(result, dtoInvokeResult);
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
                return dtoInvokeResult;
            }
            // 包裹是否扫描成功
            packageIsScanBoard(request);
            if(!request.getIsForceCombination()){
                // 验货校验
                inspectionIntercept(request);
                // 路由校验、生成板号
                routerCheck(request,result);
                BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
                BeanUtils.copyProperties(request,boardCommonRequest);
                // 是否发货校验
                boardCommonManager.isSendCheck(boardCommonRequest);
                // 包裹数限制
                boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);
                // 是否多货包裹校验
                surfacePackageCheck(request,result);
                BeanUtils.copyProperties(result, dtoInvokeResult);
                // ver组板拦截
                InvokeResult invokeResult = boardCommonManager.boardCombinationCheck(boardCommonRequest);
                if(invokeResult.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
                    dtoInvokeResult.customMessage(invokeResult.getCode(),invokeResult.getMessage());
                    return dtoInvokeResult;
                }
                //拦截校验
                InvokeResult<String> interceptResult = interceptValidateUnloadCar(request.getBarCode());
                if(interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
                    setCacheOfSealCarAndPackageIntercet(request.getSealCarCode(), request.getBarCode());
                    dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                    return dtoInvokeResult;
                }
            }else {
                surfacePackageCheck(request,result);
                BeanUtils.copyProperties(result, dtoInvokeResult);
            }

            if(StringUtils.isEmpty(request.getBoardCode())){
                dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,LoadIllegalException.BOARD_NOTE_EXIST_INTERCEPT_MESSAGE);
                return dtoInvokeResult;
            }
            // 多货包裹标识
            boolean isSurplusPackage = false;
            if(result.getCode() == CODE_SUCCESS_HIT){
                isSurplusPackage = true;
            }

            // 卸车处理并回传TC组板关系
            dealUnloadAndBoxToBoard(request,isSurplusPackage);
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            // 查询包裹所在批次号
            String sendCode = getBatchCode(waybillCode, request.getOperateSiteCode());
            // 保存包裹卸车记录和运单暂存
            saveUnloadDetail(request, isSurplusPackage, sendCode, unloadCar.getVehicleNumber());
            // 获取卸车运单扫描信息
            UnloadScanDetailDto unloadScanDetailDto = new UnloadScanDetailDto();
            setUnloadScanDetail(unloadScanDetailDto, request.getSealCarCode());
        }catch (LoadIllegalException e){
            dtoInvokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,e.getMessage());
            return dtoInvokeResult;
        }catch (Exception e){
            if (e instanceof UnloadPackageBoardException) {
                dtoInvokeResult.customMessage(InvokeResult.RESULT_PACKAGE_ALREADY_BIND, e.getMessage());
                return dtoInvokeResult;
            }
            dtoInvokeResult.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return dtoInvokeResult;
    }

    private void saveUnloadDetail(UnloadCarScanRequest request, boolean isSurplusPackage, String sendCode, String licenseNumber) {
        // 包裹暂存
        UnloadScanRecord newUnloadRecord = createUnloadScanRecord(request.getBarCode(), request.getSealCarCode(),
                request.getTransfer(), null, isSurplusPackage, sendCode, request, licenseNumber);
        unloadScanRecordDao.insert(newUnloadRecord);

        boolean flowDisAccord = false;
        if (request.getSealCarCode().startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            flowDisAccord = true;
        }
        // 运单暂存
        if (isSurplusPackage) {
            UnloadScan newUnload = createUnloadScan(request.getBarCode(), request.getSealCarCode(), 0,
                    1, request.getOperateUserName(), request.getOperateUserErp(), true);
            unloadScanDao.insert(newUnload);
        } else {
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            int packageNum = WaybillUtil.getPackNumByPackCode(request.getBarCode());
            UnloadScan unloadScan = unloadScanDao.findUnloadByBySealAndWaybillCode(request.getSealCarCode(), waybillCode);
            if (unloadScan != null) {
                unloadScan.setLoadAmount(unloadScan.getLoadAmount() + 1);
                unloadScan.setUnloadAmount(unloadScan.getForceAmount() - unloadScan.getLoadAmount());
                unloadScan.setPackageAmount(packageNum);
                // 设置状态
                int status = getWaybillStatus(unloadScan.getForceAmount(), unloadScan.getLoadAmount(), packageNum, flowDisAccord);
                unloadScan.setStatus(status);
                unloadScan.setUpdateTime(new Date());
                unloadScan.setUpdateUserName(request.getOperateUserName());
                unloadScan.setUpdateUserErp(request.getOperateUserErp());
                unloadScanDao.updateByPrimaryKey(unloadScan);
            }
        }
    }

    private void batchSaveUnloadDetail(List<String> packageList, List<String> surplusPackages, UnloadCarScanRequest request,
                                       String sendCode, String licenseNumber, String waybillCode) {
        // 正常包裹集合 = 运单包裹总集合 - 多货包裹集合
        List<String> normalPackages = ListUtils.subtract(packageList, surplusPackages);
        UnloadScanRecord unloadScanRecord = createUnloadScanRecord(request.getBarCode(), request.getSealCarCode(),
                request.getTransfer(), null, false, sendCode, request, licenseNumber);
        // 先保存正常的包裹集合
        if (CollectionUtils.isNotEmpty(normalPackages)) {
            List<List<String>> packageCodeList = ListUtils.partition(surplusPackages, 1000);
            for (List<String> packList : packageCodeList) {
                unloadScanRecord.setPackageCodeList(packList);
                unloadScanRecordDao.batchInsertByWaybill(unloadScanRecord);
            }
        }
        // 再保存多货包裹记录
        if (CollectionUtils.isNotEmpty(surplusPackages)) {
            List<List<String>> packageCodeList = ListUtils.partition(surplusPackages, 1000);
            for (List<String> packList : packageCodeList) {
                unloadScanRecord.setFlowDisaccord(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FOLW_DISACCORD_Y);
                unloadScanRecord.setPackageCodeList(packList);
                unloadScanRecordDao.batchInsertByWaybill(unloadScanRecord);
            }
        }
        // 保存运单暂存记录
        boolean flowDisAccord = false;
        if (request.getSealCarCode().startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            flowDisAccord = true;
        }
        UnloadScan unloadScan = unloadScanDao.findUnloadByBySealAndWaybillCode(request.getSealCarCode(), waybillCode);
        if (unloadScan == null) {
            unloadScan = createUnloadScan(request.getBarCode(), request.getSealCarCode(), normalPackages.size(),
                    packageList.size(), request.getOperateUserName(), request.getOperateUserErp(), flowDisAccord);
            unloadScanDao.insert(unloadScan);
        } else {
            unloadScan.setLoadAmount(packageList.size());
            unloadScan.setUnloadAmount(0);
            int packageNum = WaybillUtil.getPackNumByPackCode(request.getBarCode());
            unloadScan.setPackageAmount(packageNum);
            // 设置状态 todo 运单既有应卸，也有多货，怎么显示状态
            int status = getWaybillStatus(unloadScan.getForceAmount(), unloadScan.getLoadAmount(), packageNum, flowDisAccord);
            unloadScan.setStatus(status);
            unloadScanDao.updateByPrimaryKey(unloadScan);
        }
    }

    private String getBatchCode(String waybillCode, Integer createSiteCode) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setWaybillCode(waybillCode);
        sendDetail.setCreateSiteCode(createSiteCode);
        String sendCode = null;
        SendDetail detail = sendDatailDao.findOneByWaybillCode(sendDetail);
        if (detail != null) {
            sendCode = detail.getSendCode();
        }
        return sendCode;
    }

    /**
     * 获取运单状态
     * @param forceAmount  应卸
     * @param loadAmount   已卸
     * @param packageAmount 总包裹
     * @param flowDisAccord  是否多扫
     * @return 状态
     */
    private Integer getWaybillStatus(Integer forceAmount, Integer loadAmount, Integer packageAmount, boolean flowDisAccord) {
        if (forceAmount == null) {
            forceAmount = 0;
        }
        if (loadAmount == null) {
            loadAmount = 0;
        }
        // 如果是多扫
        if (flowDisAccord) {
            // 已卸=总包裹数
            if (packageAmount.equals(loadAmount)) {
                return GoodsLoadScanConstants.GOODS_SCAN_LOAD_YELLOW;
            } else {
                return GoodsLoadScanConstants.GOODS_SCAN_LOAD_ORANGE;
            }
        } else {
            int unloadAmount = forceAmount - loadAmount;
            // 已卸和未卸都大于0  -- 没扫齐 -- 红色
            if (loadAmount > 0 && unloadAmount > 0) {
                return GoodsLoadScanConstants.GOODS_SCAN_LOAD_RED;
            }
            // 应卸=已卸，并且未卸=0
            if (forceAmount.equals(loadAmount) && unloadAmount == 0) {
                return GoodsLoadScanConstants.GOODS_SCAN_LOAD_GREEN;
            }
        }
        return GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK;
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
        unloadScan.setLoadAmount(loadAmount);
        if (forceAmount != null && forceAmount != 0) {
            unloadScan.setUnloadAmount(forceAmount > loadAmount ? forceAmount - loadAmount : 0);
        }
        // 多扫的应卸=0
        if (flowDisAccord) {
            unloadScan.setForceAmount(0);
        }

        // 空任务的应卸和未卸都是0
        if (sealCarCode.startsWith(Constants.PDA_UNLOAD_TASK_PREFIX)) {
            unloadScan.setForceAmount(0);
            unloadScan.setUnloadAmount(0);
            flowDisAccord = true;
        }
        // 设置状态
        int status = getWaybillStatus(unloadScan.getForceAmount(), unloadScan.getLoadAmount(), packageAmount, flowDisAccord);
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
                                        boolean flowDisAccord, String batchCode, UnloadCarScanRequest request, String licenseNumber) {
        UnloadScanRecord unloadScanRecord = new UnloadScanRecord();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        unloadScanRecord.setSealCarCode(sealCarCode);
        unloadScanRecord.setLicenseNumber(licenseNumber);
        unloadScanRecord.setWaybillCode(waybillCode);
        unloadScanRecord.setPackageCode(packageCode);
        if (request.getReceiveSiteCode() != null) {
            unloadScanRecord.setEndSiteCode(request.getReceiveSiteCode());
            unloadScanRecord.setEndSiteName(request.getReceiveSiteName());
        } else {
            if (nextSiteCode != null) {
                unloadScanRecord.setEndSiteCode(nextSiteCode);
                // todo 调用青龙基础资料接口将网点编码转成网点名称
            }
        }
        if (!sealCarCode.startsWith("PDA")) {
            unloadScanRecord.setBatchCode(batchCode);
        }

        unloadScanRecord.setCreateSiteCode(request.getOperateSiteCode());
        unloadScanRecord.setCreateSiteName(request.getOperateSiteName());
        unloadScanRecord.setTransfer(transfer == null ? 0 : transfer);
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
            // B网快运发货规则校验
            InvokeResult<String> interceptResult = interceptValidateUnloadCar(packageCode);
            if (interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
                logger.error("运单卸车扫描--B网快运发货规则校验错误:error={},sealCarCode={},packageCode={}", interceptResult.getMessage(), sealCarCode, packageCode);
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                return result;
            }

            // 调用运单接口获取包裹集合
            List<String> packageList = getPackageCodesByWaybillCode(waybillCode);
            if (CollectionUtils.isEmpty(packageList)) {
                logger.error("运单卸车扫描--根据运单号查询包裹集合返回空:sealCarCode={},packageCode={}", sealCarCode, packageCode);
                result.setCode(JdCResponse.CODE_FAIL);
                result.setMessage("根据运单号查询包裹集合返回空");
                return result;
            }
            // 筛选出多货包裹
            List<String> surplusPackages = getSurplusPackageCodes(packageList, sealCarCode, waybillCode);
            // 计算最新的包裹明细
            waybillInspectSuccessAfter(request, packageList.size(), surplusPackages);
            // 查询运单所在批次号
            String sendCode = getBatchCode(waybillCode, request.getOperateSiteCode());
            // 批量保存卸车包裹明细和运单明细
            batchSaveUnloadDetail(packageList, surplusPackages, request, sendCode, unloadCar.getVehicleNumber(), waybillCode);
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

        InvokeResult<UnloadScanDetailDto> invokeResult = new InvokeResult<>();
        BeanUtils.copyProperties(result, invokeResult);

        String packageCode = request.getBarCode();
        String sealCarCode = request.getSealCarCode();
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        request.setWaybillCode(waybillCode);

        try {
            UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(request.getSealCarCode());
            if (unloadCar == null) {
                invokeResult.customMessage(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "封车编码不合法");
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
            // B网快运发货规则校验
            InvokeResult<String> interceptResult = interceptValidateUnloadCar(packageCode);
            if (interceptResult != null && !Objects.equals(interceptResult.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
                logger.error("运单卸车扫描--B网快运发货规则校验错误:error={},sealCarCode={},packageCode={}", interceptResult.getMessage(), sealCarCode, packageCode);
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult.getMessage());
                return invokeResult;
            }

            // 调用运单接口获取包裹集合
            List<String> packageList = getPackageCodesByWaybillCode(waybillCode);
            if (CollectionUtils.isEmpty(packageList)) {
                logger.error("运单卸车扫描--根据运单号查询包裹集合返回空:sealCarCode={},packageCode={}", sealCarCode, packageCode);
                invokeResult.setCode(JdCResponse.CODE_FAIL);
                invokeResult.setMessage("根据运单号查询包裹集合返回空");
                return invokeResult;
            }
            // 筛选出多货包裹
            List<String> surplusPackages = getSurplusPackageCodes(packageList, sealCarCode, waybillCode);
            // 计算最新的包裹明细
            waybillInspectSuccessAfter(request, packageList.size(), surplusPackages);

            // 查询运单所在批次号
            String sendCode = getBatchCode(waybillCode, request.getOperateSiteCode());
            // 批量保存卸车包裹明细和运单明细
            batchSaveUnloadDetail(packageList, surplusPackages, request, sendCode, unloadCar.getVehicleNumber(), waybillCode);
            // 获取卸车运单扫描信息
            UnloadScanDetailDto unloadScanDetailDto = new UnloadScanDetailDto();
            setUnloadScanDetail(unloadScanDetailDto, request.getSealCarCode());
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
        invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
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

        int unScanPackageCount = 0;
        boolean isSurfacePackage = false;
        try {
            // 获取未扫包裹
            unScanPackageCount = getUnScanPackageCount(sealCarCode);
            // 是否多货包裹
            isSurfacePackage = isSurfacePackage(sealCarCode, packageCode);
        }catch (LoadIllegalException e){
            throw new LoadIllegalException(e.getMessage());
        }
        if(unScanPackageCount <= 0 && !isSurfacePackage){
            // 未扫包裹小于0提示拦截
            throw new LoadIllegalException(String.format(LoadIllegalException.UNSCAN_PACK_ISNULL_INTERCEPT_MESSAGE,sealCarCode));
        }
        if(StringUtils.isEmpty(request.getBoardCode())){
            return;
        }
        boolean scanIsSuccess = false;
        String scanIsSuccessStr = null;
        try {
            String key = CacheKeyConstants.REDIS_PREFIX_BOARD_PACK + boardCode + Constants.SEPARATOR_HYPHEN + packageCode;
            scanIsSuccessStr = redisClientCache.get(key);
        }catch (Exception e){
            logger.error("获取缓存【{}】异常","",e);
        }
        if(StringUtils.isEmpty(scanIsSuccessStr)){
            // 包裹未扫描
            scanIsSuccess = false;
        }
        try {
            scanIsSuccess = Boolean.valueOf(scanIsSuccessStr);
        }catch (Exception e){
            logger.warn("组板【{}】包裹【{}】缓存转换异常",boardCode,packageCode);
        }
        if(scanIsSuccess){
            // 包裹已扫描组板成功则提示拦截
            throw new LoadIllegalException(String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE,packageCode,boardCode));
        }
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
        Integer totalCount = 0;
        UnloadCar unloadCar = null;
        try {
            unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        }catch (Exception e){
            logger.error(InvokeResult.SERVER_ERROR_MESSAGE,e);
        }
        if(unloadCar != null && unloadCar.getPackageNum() != null){
            totalCount = unloadCar.getPackageNum();
        }
        unloadCarScanResult.setPackageTotalCount(totalCount);
        unloadCarScanResult.setPackageScanCount(scanCount);
        unloadCarScanResult.setPackageUnScanCount(totalCount-scanCount);
        unloadCarScanResult.setSurplusPackageScanCount(surplusCount);
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
    private List<String> searchAllPackageByWaybillCode(String sealCarCode, String waybillCode){
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
            SendDetailDto params = new SendDetailDto();
            params.setCreateSiteCode(createSiteCode);
            params.setSendCode(batchCode);
            params.setIsCancel(0);
            packageCodeList = sendDatailDao.queryPackageCodeBySendCode(params);
        }catch (Exception e){
            logger.error("查询批次【{}】的包裹数异常",batchCode,e);
        }
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
            Response<Integer> response = groupBoardManager.addBoxToBoard(addBoardBox);

            if(response == null){
                logger.warn("推组板关系失败!");
                throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
            }
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            BeanUtils.copyProperties(request,boardCommonRequest);
            if(response.getCode() == ResponseEnum.SUCCESS.getIndex()){
                //组板成功
                logger.info("组板成功、板号:【{}】包裹号:【{}】站点:【{}】" ,request.getBoardCode(), request.getBarCode(),request.getOperateSiteCode());
                setCacheOfBoardAndPack(request.getBoardCode(),request.getBarCode());
                boxToBoardSuccessAfter(request,null,isSurplusPackage);
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
            if(response.getCode() == 500){
                if (null == request.getIsCombinationTransfer() || Constants.IS_COMBITION_TRANSFER.equals(request.getIsCombinationTransfer())) {
                    //调用TC的板号转移接口
                    InvokeResult<String> invokeResult = boardCommonManager.boardMove(boardCommonRequest);
                    if(invokeResult == null){
                        throw new LoadIllegalException(LoadIllegalException.BOARD_MOVED_INTERCEPT_MESSAGE);
                    }
                    //重新组板失败
                    if (invokeResult.getCode() != ResponseEnum.SUCCESS.getIndex()) {
                        logger.warn("组板转移成功.原板号【{}】新板号【{}】失败原因【{}】",
                                invokeResult.getData(),request.getBoardCode(),response.getMesseage());
                        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
                    }
                    //重新组板成功处理
                    logger.info("组板转移成功.原板号【{}】新板号【{}】包裹号【{}】站点【{}】",
                            invokeResult.getData(),request.getBoardCode(),request.getBarCode(),request.getOperateSiteCode());
                    setCacheOfBoardAndPack(request.getBoardCode(),request.getBarCode());
                    boxToBoardSuccessAfter(request,invokeResult.getData(),isSurplusPackage);
                    return;
                } else {
                    Board board = loadScanService.getBoardCodeByPackageCode(request.getOperateSiteCode().intValue(), request.getBarCode());
                    if (null != board) {
                        boardCode = board.getCode();
                    }
                    throw new UnloadPackageBoardException(String.format(LoadIllegalException.PACKAGE_ALREADY_BIND, boardCode));
                }
            }
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
            if(isSurplusPackage){
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_SURPLUS_PACKAGE_COUNT.concat(boardCode),1);
                surplusCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT.concat(sealCarCode),1);
            }else {
                updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(boardCode),1);
                scanCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(sealCarCode),1);
            }
            updatePackCount(request, scanCount, surplusCount);

            // 老板包裹数变更
            if(StringUtils.isNotEmpty(oldBoardCode)){
                UnloadCarTransBoard oldUnloadBoard = unloadCarTransBoardDao.searchByBoardCode(oldBoardCode);
                if(oldUnloadBoard == null || StringUtils.isEmpty(oldUnloadBoard.getSealCarCode())){
                    //老板未绑定封车编码则不更新
                    return;
                }
                String oldSealCarCode = oldUnloadBoard.getSealCarCode();
                if(isSurplusPackage){
                    updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_SURPLUS_PACKAGE_COUNT.concat(oldBoardCode),-1);
                    surplusCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT.concat(oldSealCarCode),-1);
                }else {
                    updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(oldBoardCode),-1);
                    scanCount = updateCache(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT.concat(oldSealCarCode),-1);
                }
                request.setSealCarCode(oldSealCarCode);
                request.setBoardCode(oldBoardCode);
                updatePackCount(request, scanCount, surplusCount);
            }
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
    private void surfacePackageCheck(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result) throws LoadIllegalException {
        boolean isSurplusPackage = false;
        try {
            isSurplusPackage = isSurfacePackage(request.getSealCarCode(),request.getBarCode());
        }catch (Exception e){
            throw new LoadIllegalException(e.getMessage());
        }
        if(isSurplusPackage){
            // 201 成功并页面提示
            result.customMessage(CODE_SUCCESS_HIT,LoadIllegalException.PACK_NOTIN_SEAL_INTERCEPT_MESSAGE);
        }
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
        List<String> allPackage = searchAllPackage(sealCarCode);
        if(CollectionUtils.isEmpty(allPackage)){
            throw new LoadIllegalException(String.format(LoadIllegalException.SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE,sealCarCode));
        }
        if(!allPackage.contains(packageCode)){
            // 不包含则是多货包裹
            exist = true;
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
        List<String> allPackage = searchAllPackageByWaybillCode(sealCarCode, waybillCode);
        if(CollectionUtils.isEmpty(allPackage)){
            throw new LoadIllegalException(String.format(LoadIllegalException.SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE, sealCarCode));
        }
        return getDifferenceList(totalPackages, allPackage);
    }

    /**
     * 根据运单总包裹集合与封车任务下总包裹集合的差集，得到多货包裹集合
     * @param totalPackages 运单总包裹集合
     * @param sealPackages 封车任务下总包裹集合
     * @return 多货包裹集合
     */
    private List<String> getDifferenceList(List<String> totalPackages, List<String> sealPackages) {
        List<String> different = new ArrayList<>();

        Map<String, String> map = new HashMap<>(totalPackages.size());
        for (String packageCode : totalPackages) {
            map.put(packageCode, packageCode);
        }
        for (String packageCode : sealPackages) {
            if (map.get(packageCode) != null) {
                continue;
            }
            different.add(packageCode);
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
        inspection.setOperateTime(DateHelper.formatDateTime(new Date(request.getOperateTime())));
        inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        // 按运单维度验货
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            inspection.setPackageBarOrWaybillCode(request.getWaybillCode());
        } else {
            inspection.setPackageBarOrWaybillCode(request.getBarCode());
        }
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        taskRequest.setKeyword1(String.valueOf(request.getOperateUserCode()));
        taskRequest.setKeyword2(request.getBarCode());
        taskRequest.setType(Task.TASK_TYPE_INSPECTION);
        taskRequest.setOperateTime(DateHelper.formatDateTime(new Date(request.getOperateTime())));
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean distributeTask(DistributeTaskRequest request) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("unloadUserErp",request.getUnloadUserErp());
        params.put("railWayPlatForm",request.getRailWayPlatForm());
        params.put("unloadCarIds",request.getUnloadCarIds());
        params.put("updateUserErp",request.getUpdateUserErp());
        params.put("updateUserName",request.getUpdateUserName());
        params.put("operateUserErp",request.getUpdateUserErp());
        params.put("operateUserName",request.getUpdateUserName());
        params.put("distributeTime",new Date());
        int result = unloadCarDao.distributeTaskByParams(params);
        if (result < 1) {
            logger.warn("分配任务失败，请求体：{}",JsonHelper.toJson(request));
            return false;
        }

        //同步卸车负责人与卸车任务之间关系
        for (int i=0;i<request.getSealCarCodes().size();i++) {
            UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
            unloadCarDistribution.setSealCarCode(request.getSealCarCodes().get(i));
            unloadCarDistribution.setUnloadUserErp(request.getUnloadUserErp());
            unloadCarDistribution.setUnloadUserName(request.getUnloadUserName());
            unloadCarDistribution.setUnloadUserType(UnloadUserTypeEnum.UNLOAD_MASTER.getType());
            unloadCarDistribution.setCreateTime(new Date());
            unloadCarDistributionDao.add(unloadCarDistribution);
        }
        return true;
    }

    @Override
    public boolean insertUnloadCar(TmsSealCar tmsSealCar) {

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
        Set<String> requiredBatchCodes = new HashSet<>();
        for (String batchCode : batchCodes){
            if(BusinessHelper.isSendCode(batchCode)){
                requiredBatchCodes.add(batchCode);
            }
        }
        if(CollectionUtils.isEmpty(requiredBatchCodes)){
            logger.warn("封车编码【{}】没有符合的批次!",unloadCar.getSealCarCode());
            return false;
        }
        unloadCar.setBatchCode(getStrByBatchCodes(new ArrayList<String>(requiredBatchCodes)));

        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("createSiteCode",tmsSealCar.getOperateSiteId());
            params.put("sendCodes",requiredBatchCodes);
            // 初始化运单暂存表
            Map<String, WaybillPackageNumInfo> map = sendDatailDao.queryPackageNumByWaybillCode(params);
            if (map == null || map.isEmpty()) {
                return false;
            }

            UnloadScan unloadScan = createUnloadScan(null, tmsSealCar.getSealCarCode(), 0, 0,
                    tmsSealCar.getOperateUserName(), tmsSealCar.getOperateUserCode(), false);
            unloadScan.setStatus(GoodsLoadScanConstants.GOODS_SCAN_LOAD_BLANK);
            List<WaybillPackageNumInfo> waybillPackageNumInfoList = new ArrayList<>();
            // 封车任务下总包裹数
            int totalPackageNum = 0;
            for (WaybillPackageNumInfo waybillPackageNumInfo : map.values()) {
                // 从包裹号上截取包裹数
                int packageNum = WaybillUtil.getPackNumByPackCode(waybillPackageNumInfo.getPackageCode());
                waybillPackageNumInfo.setPackageAmount(packageNum);
                waybillPackageNumInfoList.add(waybillPackageNumInfo);
                totalPackageNum = totalPackageNum + waybillPackageNumInfo.getForceAmount();
            }
            // 设置封车任务下总运单数
            unloadCar.setWaybillNum(map.size());
            //
            unloadCar.setPackageNum(totalPackageNum);
            unloadScan.setWaybillPackageNumInfoList(waybillPackageNumInfoList);
            unloadScanDao.batchInsert(unloadScan);

        } catch (Exception e) {
            logger.error("查询运单数或者包裹数失败!",e);
            return false;
        }
        unloadCar.setSealCarCode(tmsSealCar.getSealCarCode());
        unloadCar.setVehicleNumber(tmsSealCar.getVehicleNumber());
        unloadCar.setSealTime(tmsSealCar.getOperateTime());
        unloadCar.setStartSiteCode(tmsSealCar.getOperateSiteId());
        unloadCar.setStartSiteName(tmsSealCar.getOperateSiteName());
        unloadCar.setCreateTime(new Date());

        CommonDto<SealCarDto> sealCarDto = vosManager.querySealCarInfoBySealCarCode(tmsSealCar.getSealCarCode());
        if (CommonDto.CODE_SUCCESS == sealCarDto.getCode() && sealCarDto.getData() != null) {
            unloadCar.setEndSiteCode(sealCarDto.getData().getEndSiteId());
            unloadCar.setEndSiteName(sealCarDto.getData().getEndSiteName());
        } else {
            logger.error("调用运输的接口获取下游机构信息失败，请求体：{}，返回值：{}",tmsSealCar.getSealCarCode(),JsonHelper.toJson(sealCarDto));
            return false;
        }
        try {
            unloadCarDao.add(unloadCar);
        } catch (Exception e) {
            logger.error("卸车任务数据插入失败，数据：{},返回值:{}",JsonHelper.toJson(tmsSealCar),e);
            return false;
        }
        return true;
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
            unloadCarTaskDto.setBatchCode(unloadCar.getBatchCode());
            if (unloadCar != null && unloadCar.getBatchCode() != null) {
                unloadCarTaskDto.setBatchNum(getBatchNumber(unloadCar.getBatchCode()));
            }
            unloadCarTaskDto.setWaybillNum(unloadCar.getWaybillNum());
            unloadCarTaskDto.setPackageNum(unloadCar.getPackageNum());
            unloadCarTaskDto.setTaskStatus(unloadCar.getStatus());
            unloadCarTaskDto.setTaskStatusName(UnloadCarStatusEnum.getEnum(unloadCar.getStatus()).getName());

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
        unloadCar.setUpdateUserErp(unloadCarTaskReq.getUser().getUserErp());
        unloadCar.setUpdateUserName(unloadCarTaskReq.getUser().getUserName());
        unloadCar.setEndSiteCode(unloadCarTaskReq.getCurrentOperate().getSiteCode());
        Date updateTime = DateHelper.parseDate(unloadCarTaskReq.getOperateTime());
        unloadCar.setUpdateTime(updateTime);
        int count = unloadCarDao.updateUnloadCarTaskStatus(unloadCar);
        if (count < 1) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            logger.error("修改任务状态失败，请求信息：{}",JsonHelper.toJson(unloadCarTaskReq));
            return result;
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
        unloadCar.setStatus(unloadCarTaskReq.getTaskStatus());
        unloadCar.setUnloadUserErp(unloadCarTaskReq.getUser().getUserErp());
        unloadCar.setUpdateUserErp(unloadCarTaskReq.getUser().getUserErp());
        unloadCar.setUpdateUserName(unloadCarTaskReq.getUser().getUserName());
        unloadCar.setEndSiteCode(unloadCarTaskReq.getCurrentOperate().getSiteCode());
        Date updateTime = DateHelper.parseDate(unloadCarTaskReq.getOperateTime());
        unloadCar.setUpdateTime(updateTime);
        int count = unloadCarDao.updateUnloadCarTaskStatus(unloadCar);
        if (count < 1) {
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            logger.error("修改任务状态失败，请求信息：{}",JsonHelper.toJson(unloadCarTaskReq));
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
                if (baseStaffSiteOrgDto == null || baseStaffSiteOrgDto.getStaffName() == null){
                    logger.error("根据协助人的erp未查询到员工信息，请求信息：{}",JsonHelper.toJson(taskHelpersReq));
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage("未查询到员工信息");
                    return result;
                }

                //单个任务不超过20个人且添加协助人验重
                InvokeResult<List<HelperDto>> helpers = this.getUnloadCarTaskHelpers(taskHelpersReq.getTaskCode());
                if (helpers != null && helpers.getData() != null && helpers.getData().size() >= 20) {
                    logger.warn("根据任务号：{}查询到的协助人已经达到20人",taskHelpersReq.getTaskCode());
                    result.setCode(InvokeResult.RESULT_MULTI_ERROR);
                    result.setMessage("单个任务的协助人不能超过20个！");
                    return result;
                } else if (helpers != null && helpers.getData() != null && helpers.getData().size() > 0) {
                    List<HelperDto> helperDtoList = helpers.getData();
                    for (HelperDto helperDto : helperDtoList) {
                        if (taskHelpersReq.getHelperERP().equals(helperDto.getHelperERP())) {
                            logger.warn("根据任务号：{}查询到，该协助人已存在",taskHelpersReq.getTaskCode());
                            result.setCode(InvokeResult.RESULT_MULTI_ERROR);
                            result.setMessage("该协助人已添加！");
                            return result;
                        }
                    }
                }

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

        } catch (Exception e) {
            logger.error("更新协助人发生异常：{}", JsonHelper.toJson(taskHelpersReq));
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }

        return this.getUnloadCarTaskHelpers(taskHelpersReq.getTaskCode());
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
                unloadCarTaskDto.setBatchCode(unloadCar.getBatchCode());
                if (unloadCar != null && unloadCar.getBatchCode() != null) {
                    unloadCarTaskDto.setBatchNum(getBatchNumber(unloadCar.getBatchCode()));
                }
                unloadCarTaskDto.setPackageNum(unloadCar.getPackageNum());
                unloadCarTaskDto.setWaybillNum(unloadCar.getWaybillNum());
                unloadCarTaskDto.setTaskStatus(unloadCar.getStatus());
                unloadCarTaskDto.setTaskStatusName(UnloadCarStatusEnum.getEnum(unloadCar.getStatus()).getName());

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
    public InvokeResult<String> interceptValidateUnloadCar(String barCode) {
        InvokeResult<String> result = new InvokeResult<String>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        if(StringUtils.isBlank(barCode)){
            return result;
        }
        try{
            logger.info("interceptValidate卸车根据包裹号：{}",barCode);
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
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
            logger.info("B网快运发货规则校验1：waybillSign={},againWeight={}", waybillSign, waybillNoCache.getAgainWeight());
            if (waybillNoCache.getAgainWeight() == null) {
                logger.info("B网快运发货规则校验0：againWeight is null");
            }
            if(StringUtils.isBlank(waybillSign)){
                logger.error("interceptValidate卸车根据单号获取运单信息失败单号：{}",waybillCode);
                return result;
            }
            //信任运单标识
            boolean isTrust = BusinessUtil.isNoNeedWeight(waybillSign);
            logger.info("B网快运发货规则校验2：isTrust={}", isTrust);

            //纯配快运零担
            boolean isB2BPure = BusinessUtil.isCPKYLD(waybillSign);
            logger.info("B网快运发货规则校验3：isB2BPure={}", isB2BPure);

            //无重量禁止发货判断
            if(!isTrust && isB2BPure){
                if (waybillNoCache.getAgainWeight() == null ||  waybillNoCache.getAgainWeight() <= 0) {
                    logger.info("interceptValidate卸车无重量禁止发货单号：{}",waybillCode);
                    result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                    result.setMessage(LoadIllegalException.NO_WEIGHT_FORBID_SEND_MESSAGE);
                    return result;
                }
            }
            //B网营业厅
            boolean isBnet = BusinessUtil.isBusinessHall(waybillSign);
            //寄付
            boolean isSendPay = BusinessUtil.isWaybillConsumableOnlyConfirm(waybillSign);
            //B网营业厅（原单作废，逆向单不计费）
            boolean isBnetCancel = BusinessUtil.isYDZF(waybillSign);
            //B网营业厅（原单拒收因京东原因产生的逆向单，不计费）
            boolean isBnetJDCancel = BusinessUtil.isJDJS(waybillSign);
            //运费寄付无运费金额禁止发货
            if(isBnet && isSendPay && !isBnetCancel && !isBnetJDCancel && StringUtils.isNotBlank(waybillNoCache.getFreight()) && !NumberHelper.gt0(waybillNoCache.getFreight())){
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

            //寄付临欠
            boolean isSendPayTemporaryDebt = BusinessUtil.isJFLQ(waybillSign);
            if(!isTrust && isBnet && isSendPayTemporaryDebt && (waybillNoCache.getAgainWeight() == null || waybillNoCache.getAgainWeight() <= 0
                    || StringUtils.isEmpty(waybillNoCache.getSpareColumn2()) || Double.parseDouble(waybillNoCache.getSpareColumn2()) <= 0)){
                logger.warn("interceptValidate卸车运费临时欠款无重量体积禁止发货单号：{}",waybillCode);
                result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
                result.setMessage(LoadIllegalException.FREIGTH_TEMPORARY_PAY_NO_WEIGHT_VOLUME_FORBID_SEND_MESSAGE);
                return result;
            }

            //有包装服务
            boolean isPackService = BusinessUtil.isNeedConsumable(waybillSign);
            if(isPackService && !waybillConsumableRecordService.isConfirmed(waybillCode)){
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
}
