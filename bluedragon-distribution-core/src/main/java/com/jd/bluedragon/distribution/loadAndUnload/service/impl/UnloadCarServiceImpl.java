package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.common.dto.unloadCar.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarDetailScanResult;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanRequest;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanResult;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.*;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTransBoard;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDistributionDao;
import com.jd.bluedragon.distribution.loadAndUnload.domain.*;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarTransBoardDao;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.unloadCar.domain.UnloadCarCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
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

    @JProfiler(jKey = "dmsWeb.loadAndUnload.UnloadCarServiceImpl.barCodeScan",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        result.setData(convertToUnloadCarResult(request));
        try {
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
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
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
            if(scanMap == null || scanMap.isEmpty()){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"封车编码【" + sealCarCode + "】未扫描包裹!");
                return result;
            }
            //获取封车编码下所有运单明细
            Map<String, Integer> allMap = getAllWaybillBySealCarCode(sealCarCode);
            if(allMap == null || allMap.isEmpty()){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"封车编码【" + sealCarCode + "】没有对应批次号!");
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
                    if (o1.getPackageUnScanCount() == 0 || o2.getPackageUnScanCount() == 0) {
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
        List<String> boardCodeList = unloadCarTransBoardDao.searchBoardsBySealCode(sealCarCode);
        if(CollectionUtils.isEmpty(boardCodeList)){
            return null;
        }
        Map<String,Integer> scanMap = new HashMap<>();
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
        UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        if(unloadCar == null || StringUtils.isEmpty(unloadCar.getBatchCode())){
            return null;
        }
        Integer createSiteCode = unloadCar.getStartSiteCode();
        String[] batchSplit = unloadCar.getBatchCode().split(",");
        if(batchSplit == null || batchSplit.length == 0){
            return null;
        }
        List<String> allPackage = new ArrayList<>();
        for (String batchCode : batchSplit){
            allPackage.addAll(querySendPackageBySendCode(createSiteCode,batchCode));
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
                boxToBoardSuccessAfter(request,null,isSurplusPackage);
                // 组板全程跟踪
                boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
                return;
            }
            /**
             * 组板失败
             *  500：失败-当前箱已经绑过板
             *  直接强制组板到新版上
             * */
            if (response.getCode() == 500) {
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
                boxToBoardSuccessAfter(request,invokeResult.getData(),isSurplusPackage);
                return;
            }
            logger.warn("组板失败.板号【{}】包裹号【{}】站点【{}】.失败原因:【{}】",
                    request.getBoardCode(),request.getBarCode(),request.getOperateSiteCode(),response.getMesseage());

        }catch (Exception e){
            logger.error("推TC组板关系异常，入参【{}】",JsonHelper.toJson(addBoardBox),e);
        }
        throw new LoadIllegalException(LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
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
                UnloadCarTransBoard oldUnloadBoard = unloadCarTransBoardDao.searchBySealCode(oldBoardCode);
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
                request.setBarCode(oldBoardCode);
                updatePackCount(request, scanCount, surplusCount);
            }
        }catch (Exception e){
            logger.error("卸车扫描处理异常,参数【{}】",JsonHelper.toJson(request),e);
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
        UnloadCarTransBoard unloadCarBoard = unloadCarTransBoardDao.searchBySealCode(request.getSealCarCode());
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
        try {
            Integer nextSiteCode = boardCommonManager.getNextSiteCodeByRouter(waybillCode,request.getOperateSiteCode());
            if(nextSiteCode == null){
                // 此处直接返回，因为ver组板校验链会判断
                return;
            }
            Integer destinationId = null;
            Response<Board> response = groupBoardManager.getBoard(request.getBoardCode());
            if(response != null && response.getCode() == ResponseEnum.SUCCESS.getIndex()
                    && response.getData() != null){
                destinationId = response.getData().getDestinationId();
            }
            if(destinationId == null){
                throw new LoadIllegalException(LoadIllegalException.BOARD_RECIEVE_EMPEY_INTERCEPT_MESSAGE);
            }
            if(!nextSiteCode.equals(destinationId)){
                throw new LoadIllegalException(LoadIllegalException.FORBID_BOARD_INTERCEPT_MESSAGE);
            }
        }catch (Exception e){
            logger.error("运单号【{}】的路由下一跳和板号【{}】目的地校验异常",waybillCode,request.getBoardCode(),e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
    }

    /**
     * 多货包裹校验
     * @param request
     * @param result
     * @return
     */
    private void surfacePackageCheck(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result) throws LoadIllegalException {
        String sealCarCode = request.getSealCarCode();
        List<String> allPackage = searchAllPackage(sealCarCode);
        if(CollectionUtils.isEmpty(allPackage)){
            throw new LoadIllegalException(String.format(LoadIllegalException.SEAL_NOT_SCANPACK_INTERCEPT_MESSAGE,sealCarCode));
        }
        if(!allPackage.contains(request.getBarCode())){
            // 201 成功并页面提示
            result.customMessage(CODE_SUCCESS_HIT,LoadIllegalException.PACK_NOTIN_SEAL_INTERCEPT_MESSAGE);
        }
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
        inspection.setPackageBarOrWaybillCode(request.getBarCode());

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
    public List<UnloadCarTask> queryByCondition(UnloadCarCondition condition) {

        List<UnloadCarTask> unloadCarTasks = new ArrayList<>();

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
        unloadCarTasks = unloadCarDao.queryByCondition(condition);
        for (UnloadCarTask unloadCarTask : unloadCarTasks) {
            String sealCarCode = unloadCarTask.getSealCarCode();
            //查询卸车任务的协助人
            List<String> helpers = unloadCarDistributionDao.selectHelperBySealCarCode(sealCarCode);
            unloadCarTask.setHelperErps(StringUtils.strip(helpers.toString(),"[]"));
        }
        return unloadCarTasks;
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
            unloadCarDistribution.setUnloadUserErp(request.getUpdateUserErp());
            unloadCarDistribution.setUnloadUserName(request.getUpdateUserName());
            unloadCarDistribution.setUnloadUserType(UnloadUserTypeEnum.UNLOAD_MASTER.getType());
            unloadCarDistribution.setCreateTime(new Date());
            unloadCarDistributionDao.add(unloadCarDistribution);
        }
        return true;
    }

    @Override
    public boolean insertUnloadCar(TmsSealCar tmsSealCar) {

        UnloadCar unloadCar = new UnloadCar();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("createSiteCode",tmsSealCar.getOperateSiteId());
            params.put("sendCodes",tmsSealCar.getBatchCodes());
            Integer waybillNum = sendDatailDao.queryWaybillNumBybatchCodes(params);
            Integer packageNum = sendDatailDao.queryPackageNumBybatchCodes(params);
            unloadCar.setWaybillNum(waybillNum);
            unloadCar.setPackageNum(packageNum);
        } catch (Exception e) {
            logger.error("查询运单数或者包裹数失败，返回值：{}",e);
            return false;
        }
        unloadCar.setSealCarCode(tmsSealCar.getSealCarCode());
        unloadCar.setVehicleNumber(tmsSealCar.getVehicleNumber());
        unloadCar.setSealTime(tmsSealCar.getOperateTime());
        unloadCar.setStartSiteCode(tmsSealCar.getOperateSiteId());
        unloadCar.setStartSiteName(tmsSealCar.getOperateSiteName());

        CommonDto<SealCarDto> sealCarDto = vosManager.querySealCarInfoBySealCarCode(tmsSealCar.getSealCarCode());
        if (CommonDto.CODE_SUCCESS == sealCarDto.getCode() && sealCarDto.getData() != null) {
            unloadCar.setEndSiteCode(sealCarDto.getData().getEndSiteId());
            unloadCar.setEndSiteName(sealCarDto.getData().getEndSiteName());
        } else {
            logger.error("调用运输的接口获取下游机构信息失败，请求体：{}，返回值：{}",tmsSealCar.getSealCarCode(),JsonHelper.toJson(sealCarDto));
            return false;
        }
        unloadCar.setBatchCode(getStrByBatchCodes(tmsSealCar.getBatchCodes()));
        unloadCar.setCreateTime(new Date());
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
    public InvokeResult<List<HelperDto>> getUnloadCarTaskHelpers(String sealCarCode) {
        InvokeResult<List<HelperDto>> result = new InvokeResult<>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        List<HelperDto> helperDtos = new ArrayList<HelperDto>();
        result.setData(helperDtos);

        try {
            List<UnloadCarDistribution> unloadCarDistributions = unloadCarDistributionDao.selectUnloadCarTaskHelpers(sealCarCode);
            if (CollectionUtils.isEmpty(unloadCarDistributions)) {
                result.setCode(InvokeResult.RESULT_NULL_CODE);
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

                //校验协助人
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(taskHelpersReq.getHelperERP());
                if (baseStaffSiteOrgDto == null || baseStaffSiteOrgDto.getStaffName() == null){
                    logger.error("根据协助人的erp未查询到员工信息，请求信息：{}",JsonHelper.toJson(taskHelpersReq));
                    result.setCode(InvokeResult.RESULT_NULL_CODE);
                    result.setMessage("未查询到员工信息");
                    return result;
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
}
