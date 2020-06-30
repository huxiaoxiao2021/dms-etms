package com.jd.bluedragon.distribution.loadAndUnload.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDetailScanResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanRequest;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanResult;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarTransBoardDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 卸车任务实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:06
 */
@Service("unloadCarService")
public class UnloadCarServiceImpl implements UnloadCarService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${unload.board.bindings.count.max}")
    private Integer unloadBoardBindingsMaxCount;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private BoardCommonManager boardCommonManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisCommonUtil redisCommonUtil;

    @Autowired
    private UnloadCarDao unloadCarDao;

    @Autowired
    private UnloadCarTransBoardDao unloadCarTransBoardDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Override
    public InvokeResult<UnloadCarScanResult> getUnloadCarBySealCarCode(String sealCarCode) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<>();
        if(StringUtils.isEmpty(sealCarCode)){
            result.parameterError("封车编码不存在!");
            return result;
        }
        UnloadCar unloadCar = unloadCarDao.selectBySealCarCode(sealCarCode);
        if(unloadCar == null){
            result.error("封车编码【" + sealCarCode + "】的卸车任务不存在，请检查");
            return result;
        }
        UnloadCarScanResult unloadCarScanResult = new UnloadCarScanResult();
        unloadCarScanResult.setSealCarCode(unloadCar.getSealCarCode());
        unloadCarScanResult.setPackageTotalCount(unloadCar.getPackageNum());
//        unloadCarScanResult.setPackageScanCount(0);
//        unloadCarScanResult.setPackageUnScanCount(unloadCar.getPackageNum());
//        unloadCarScanResult.setSurplusPackageScanCount(0);
        result.setData(unloadCarScanResult);
        return result;
    }

    @Override
    public InvokeResult<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest request) {
        InvokeResult<UnloadCarScanResult> result = new InvokeResult<UnloadCarScanResult>();
        result.setData(new UnloadCarScanResult());
        // 验货校验
        if(inspectionIntercept(request,result)){
            return result;
        }
        BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
        BeanUtils.copyProperties(request,boardCommonRequest);
        // 路由校验、生成板号
        if(routerCheck(boardCommonRequest,result)){
            return result;
        }
        // 是否发货校验
        result = boardCommonManager.isSendCheck(boardCommonRequest);
        if(result.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            return result;
        }
        // 包裹数限制
        result = boardCommonManager.packageCountCheck(request.getBoardCode(),unloadBoardBindingsMaxCount);
        if(result.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            return result;
        }
        // ver组板拦截
        result = boardCommonManager.boardCombinationCheck(boardCommonRequest);
        if(result.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            return result;
        }
        // 卸车处理并回传TC板包裹关系
        dealUnloadAndBoxToBoard(request,result);
        if(result.getCode() != InvokeResult.RESULT_SUCCESS_CODE){
            return result;
        }

        //额外参数处理
        dealExcessParams(request,result);

        return result;
    }

    @Override
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
            result.setData(detailScanList);
        }catch (Exception e){
            String errorMessage = "封车编码【" + sealCarCode + "】查询扫描明细异常!";
            logger.error(errorMessage,sealCarCode,e);
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,errorMessage);
        }

        return result;
    }

    /**
     * 获取封车编码下已扫描运单信息
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
     * @param sealCarCode
     * @return
     */
    private Map<String,Integer> getAllWaybillBySealCarCode(String sealCarCode){
        UnloadCar unloadCar = unloadCarDao.searchBatchsBySealCode(sealCarCode);
        if(unloadCar == null || StringUtils.isEmpty(unloadCar.getBatchCode())){
            return null;
        }
        Integer createSiteCode = unloadCar.getStartSiteCode();
        String[] batchSplit = unloadCar.getBatchCode().split(",");
        if(batchSplit == null || batchSplit.length == 0){
            return null;
        }
        Map<String,Integer> allMap = new HashMap<>();
        List<String> allPackage = new ArrayList<>();
        for (String batchCode : batchSplit){
            allPackage.addAll(querySendPackageBySendCode(createSiteCode,batchCode));
        }
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
     * 获取已发货批次下包裹号
     * @param createSiteCode
     * @param batchCode
     * @return
     */
    private List<String> querySendPackageBySendCode(Integer createSiteCode, String batchCode) {

        return null;
    }

    /**
     * 额外参数处理
     * @param request
     * @param result
     */
    private void dealExcessParams(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result) {
        UnloadCarScanResult unloadCarScanResult = result.getData();
        unloadCarScanResult.setSealCarCode(request.getSealCarCode());
//        unloadCarScanResult.setPackageTotalCount();
//        unloadCarScanResult.setPackageScanCount();
//        unloadCarScanResult.setPackageUnScanCount();
//        unloadCarScanResult.setSurplusPackageScanCount();
    }

    /**
     * <p>
     *     1、推TC组板关系
     *     2、卸车逻辑处理
     * </p>
     * @param request
     * @param result
     * @return
     */
    private void dealUnloadAndBoxToBoard(UnloadCarScanRequest request, InvokeResult<UnloadCarScanResult> result) {
        result.customMessage(com.jd.ql.dms.common.domain.JdResponse.CODE_FAIL, "组板失败!");
        AddBoardBox addBoardBox = new AddBoardBox();
        try {
            addBoardBox.setBoardCode(request.getBoardCode());
            addBoardBox.setBoxCode(request.getBarCode());
            addBoardBox.setOperatorErp(request.getOperateUserErp());
            addBoardBox.setOperatorName(request.getOperateUserName());
            addBoardBox.setSiteCode(Integer.valueOf(request.getOperateSiteCode()));
            addBoardBox.setSiteName(request.getOperateSiteName());
            addBoardBox.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
            Response<Integer> response = groupBoardManager.addBoxToBoard(addBoardBox);

            String logInfo;
            if(response == null){
                return;
            }
            if(response.getCode() == ResponseEnum.SUCCESS.getIndex()){
                logInfo = "组板成功!板号：" + request.getBoardCode() + ",包裹号：" + request.getBarCode() + ",站点：" + request.getOperateSiteCode();
                //组板成功
                logger.debug(logInfo);
                boxToBoardSuccessAfter(request);
                return;
            }

            /**
             * 组板失败
             *  500：失败-当前箱已经绑过板
             *  则提示是否转移，如果确定转移，则从原来的板上取消，移动到新板上
             * */
            if (response.getCode() == 500) {
                //提示是否组到新板
                if (!request.getIsForceCombination()) {
                    result.customMessage(BoardResponse.CODE_BOARD_CHANGE, response.getMesseage() + BoardResponse.Message_BOARD_CHANGE);
                    return;
                }
                //确定转移,调用TC的板号转移接口
                InvokeResult<String> invokeResult = boardCommonManager.boardMove(request);
                if(invokeResult == null){
                    result.customMessage(com.jd.ql.dms.common.domain.JdResponse.CODE_FAIL, "组板转移服务异常!");
                    return;
                }

                //重新组板失败
                if (invokeResult.getCode() != ResponseEnum.SUCCESS.getIndex()) {
                    logInfo = "组板转移失败,原板号：" + invokeResult.getData() + ",新板号：" + request.getBoardCode() + ",包裹号：" + request.getBarCode() +
                            ",站点：" + request.getOperateSiteCode() + ".失败原因:" + response.getMesseage();
                    logger.warn(logInfo);
                    result.customMessage(response.getCode(), response.getMesseage());
                }

                //重新组板成功处理
                logInfo = "组板转移成功.原板号:" + invokeResult.getData() + ",新板号:" + request.getBoardCode() + ",包裹号：" + request.getBarCode() +
                        ",站点：" + request.getOperateSiteCode();
                logger.debug(logInfo);
                boxToBoardAgainSuccessAfter(request,invokeResult.getData());
                return;
            }

            logInfo = "组板失败,板号：" + request.getBoardCode() + ",包裹号：" + request.getBarCode() +
                    ",站点：" + request.getOperateSiteCode() + ".失败原因:" + response.getMesseage();
            logger.warn(logInfo);
            result.customMessage(response.getCode(), response.getMesseage());

        }catch (Exception e){
            logger.error("推TC组板关系异常，入参【{}】",JsonHelper.toJson(addBoardBox),e);
        }
    }

    /**
     * 重新组板成功处理
     * @param request
     * @param oldBoardCode
     */
    private void boxToBoardAgainSuccessAfter(UnloadCarScanRequest request, String oldBoardCode) {
        try {
            //原板号缓存-1
            redisCommonUtil.decr(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_BINDINGS_COUNT + "-" + oldBoardCode);
            //缓存+1
            redisCommonUtil.incr(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_BINDINGS_COUNT + "-" + request.getBoardCode());



            // 组板全程跟踪
            boardCommonManager.sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
        }catch (Exception e){
            logger.error("卸车扫描处理异常,参数【{}】",JsonHelper.toJson(request),e);
        }
    }

    /**
     * 组板成功处理
     * @param request
     */
    private void boxToBoardSuccessAfter(UnloadCarScanRequest request) {
        try {
            //缓存+1
            redisCommonUtil.incr(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_BINDINGS_COUNT + "-" + request.getBoardCode());


            redisCommonUtil.incr(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_PACKAGE_COUNT + "-" + request.getSealCarCode());
            redisCommonUtil.incr(CacheKeyConstants.REDIS_PREFIX_UNLOAD_SEAL_SURPLUS_PACKAGE_COUNT + "-" + request.getSealCarCode());

            unloadCarTransBoardDao.add(convertToUnloadCarTransBoard(request));

            // 组板全程跟踪
            boardCommonManager.sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
        }catch (Exception e){
            logger.error("卸车扫描处理异常,参数【{}】",JsonHelper.toJson(request),e);
        }
    }

    /**
     * 路由校验
     * @param request
     * @param result
     */
    private boolean routerCheck(BoardCommonRequest request, InvokeResult<UnloadCarScanResult> result) {
        if(StringUtils.isEmpty(request.getBoardCode())){
            //第一次则生成板号
            Board board = boardCommonManager.createBoardCode(request);
            if(board == null || StringUtils.isEmpty(board.getCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"生成板号异常!");
                return true;
            }
            UnloadCarScanResult unloadCarScanResult = result.getData();
            unloadCarScanResult.setBoardCode(board.getCode());
            unloadCarScanResult.setReceiveSiteCode(board.getDestinationId());
            unloadCarScanResult.setReceiveSiteName(board.getDestination());
            return false;
        }
        // 非第一次则校验目的地是否一致
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        try {
            Integer nextSiteCode = boardCommonManager.getNextSiteCodeByRouter(waybillCode,request.getOperateSiteCode());
            if(nextSiteCode == null){
                logger.warn("根据运单号【{}】操作站点【{}】获取路由下一节点为空!",waybillCode,request.getOperateSiteCode());
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"运单" + waybillCode + "的路由下一节点为空!");
                return true;
            }
            Integer destinationId = null;
            Response<Board> response = groupBoardManager.getBoard(request.getBoardCode());
            if(response != null && response.getCode() == ResponseEnum.SUCCESS.getIndex()
                    && response.getData() != null){
                destinationId = response.getData().getDestinationId();
            }
            if(destinationId == null){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"板目的地为空!");
                return true;
            }
            if(!nextSiteCode.equals(destinationId)){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"禁止非同一方向组板!");
                return true;
            }
        }catch (Exception e){
            logger.error("运单号【{}】的路由下一跳和板号【{}】目的地校验异常",waybillCode,request.getBoardCode(),e);
            result.error(InvokeResult.SERVER_ERROR_MESSAGE);
            return true;
        }
        return false;
    }


    /**
     * 验货拦截及验货处理
     * @param request
     * @param result
     */
    private boolean inspectionIntercept(UnloadCarScanRequest request,InvokeResult<UnloadCarScanResult> result) {
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        // 加盟商余额校验
        if(allianceBusiDeliveryDetailService.checkExist(waybillCode)
                && !allianceBusiDeliveryDetailService.checkMoney(waybillCode)){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"加盟商预付款余额不足，请联系加盟商处理！");
            return true;
        }
        // 集包袋校验
        if(WaybillUtil.isWaybillCode(request.getBarCode())
                && inspectionService.checkIsBindMaterial(request.getBarCode())){
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,JdResponse.MESSAGE_CHECK_MATERIAL_ERROR);
            return true;
        }
        // 验货任务
        pushInspectionTask(request);
        return false;
    }

    /**
     * 推验货任务
     * @param request
     */
    private void pushInspectionTask(UnloadCarScanRequest request) {
        InspectionRequest inspection=new InspectionRequest();
        inspection.setUserCode(request.getOperateUserCode());
        inspection.setUserName(request.getOperateUserName());
        inspection.setSiteCode(Integer.valueOf(request.getOperateSiteCode()));
        inspection.setSiteName(request.getOperateSiteName());
        inspection.setOperateTime(DateHelper.formatDateTime(request.getOperateTime()));
        inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        inspection.setPackageBarOrWaybillCode(request.getBarCode());

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        taskRequest.setKeyword1(String.valueOf(request.getOperateUserCode()));
        taskRequest.setKeyword2(request.getBarCode());
        taskRequest.setType(Task.TASK_TYPE_INSPECTION);
        taskRequest.setOperateTime(DateHelper.formatDateTime(request.getOperateTime()));
        taskRequest.setSiteCode(Integer.valueOf(request.getOperateSiteCode()));
        taskRequest.setSiteName(request.getOperateSiteName());
        taskRequest.setUserCode(request.getOperateUserCode());
        taskRequest.setUserName(request.getOperateUserName());

        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task = this.taskService.toTask(taskRequest, eachJson);

        taskService.add(task, true);
    }
}
