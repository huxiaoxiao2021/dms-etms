package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.unloadCar.UnloadScanDetailDto;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.TransportServiceRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.BlockResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.jy.dto.common.JyOperateFlowMqData;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.common.JyOperateFlowService;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.neum.UnloadCarWarnEnum;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.coo.ucc.common.utils.JsonUtils;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.MoveBoxRequest;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteMatchQo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jd.ql.dms.common.constants.OperateDeviceTypeConstants.PDA;

/**
 * 组板公用操作实现
 *
 * @author: hujiping
 * @date: 2020/6/29 11:00
 */
@Service("boardCommonService")
public class BoardCommonManagerImpl implements BoardCommonManager {

    private static final Logger logger = LoggerFactory.getLogger(BoardCommonManagerImpl.class);

    private static final Integer SINGLE_BOARD_CODE = 1;

    /**
     * 操作组板的单位类型
     *  0：分拣中心； 1：TC
     * */
    public static final Integer BOARD_COMBINATION_SITE_TYPE = 0;

    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private RouterService routerService;
    
    @Autowired
    private JyOperateFlowService jyOperateFlowService;

    /**
     * 包裹是否发货校验
     * @param request
     * @return
     */
    @Override
    public InvokeResult isSendCheck(BoardCommonRequest request) throws LoadIllegalException {
        InvokeResult result = new InvokeResult();
        SendDetail sendDetail = new SendDetail();
        sendDetail.setPackageBarcode(request.getBarCode());
        sendDetail.setCreateSiteCode(request.getOperateSiteCode());
        sendDetail.setReceiveSiteCode(request.getReceiveSiteCode());
        List<SendDetail> sendDetailList = sendDatailDao.findByWaybillCodeOrPackageCode(sendDetail);

        if (sendDetailList != null && sendDetailList.size() > 0) {
            logger.warn("包裹【{}】已经在批次【{}】发货,站点【{}】",
                    request.getBarCode(),sendDetailList.get(0).getSendCode(),request.getOperateSiteCode());
            throw new LoadIllegalException(String.format(LoadIllegalException.BOARD_PACK_SEND_INTERCEPT_MESSAGE,request.getBarCode()));
        }
        return result;
    }

    /**
     * 包裹数限制
     * @param boardCode 板号
     * @param maxCount 包裹数最大限制
     * @return
     */
    @Override
    public InvokeResult packageCountCheck(String boardCode, Integer maxCount) throws LoadIllegalException {
        InvokeResult result = new InvokeResult();
        //数量限制校验，每次的数量记录的redis中
        int count = 0;
        try {
            String countStr = jimdbCacheService.get(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_PACKAGE_COUNT.concat(boardCode));
            if(StringUtils.isNotEmpty(countStr)){
                count = Integer.valueOf(countStr);
            }
        }catch (Exception e){
            logger.error("从缓存中获取板号【{}】绑定的包裹数异常!",boardCode,e);
            throw new LoadIllegalException(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        if(logger.isDebugEnabled()){
            logger.debug("板号【{}】已经绑定的包裹个数为【{}】" ,boardCode, count);
        }
        //超上限提示
        if (count >= maxCount) {
            logger.warn("板号【{}】已经绑定的包裹数【{}】达到上限",boardCode,count);
            throw new LoadIllegalException(String.format(LoadIllegalException.BOARD_PACKNUM_EXCEED_INTERCEPT_MESSAGE,boardCode,count));
        }
        return result;
    }

    /**
     * VER组板拦截校验
     * @param request
     * @return
     */
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.sendBoardBindings.boardCombinationCheck",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public InvokeResult boardCombinationCheck(BoardCommonRequest request) {
        InvokeResult result = new InvokeResult();
        //如果是箱号则取其中任一包裹进行校验
        BoardCombinationJsfResponse response = null;
        if (!request.getIsForceCombination()) {
            BoardCombinationRequest checkParam = new BoardCombinationRequest();
            checkParam.setSiteCode(request.getOperateSiteCode());
            checkParam.setReceiveSiteCode(request.getReceiveSiteCode());
            checkParam.setBusinessType(request.getBusinessType());
            checkParam.setUserCode(request.getOperateUserCode());
            checkParam.setUserName(request.getOperateUserName());

            if (WaybillUtil.isPackageCode(request.getBarCode())) {
                checkParam.setBoxOrPackageCode(request.getBarCode());
            } else if (BusinessHelper.isBoxcode(request.getBarCode())) {
                Box box = boxService.findBoxByCode(request.getBarCode());
                if (box != null) {
                    List<Sorting> sortings = sortingService.findByBoxCodeAndFetchNum(box.getCode(), box.getCreateSiteCode(), 1);
                    if (sortings == null || sortings.isEmpty()) {
                        result.customMessage(com.jd.ql.dms.common.domain.JdResponse.CODE_CONFIRM, BoardResponse.MESSAGE_BOX_NO_SORTING);
                        return result;
                    }
                    checkParam.setBoxOrPackageCode(sortings.get(0).getPackageCode());

                } else {
                    result.customMessage(BoardResponse.CODE_BOX_NOT_EXIST, BoardResponse.MESSAGE_BOX_NOT_EXIST);
                    return result;
                }
            } else {
                result.customMessage(BoardResponse.CODE_BOX_PACKAGECODE_ERROR, BoardResponse.MESSAGE_BOX_PACKAGECODE_ERROR);
                return result;
            }
            try {
                response = sortingCheckService.boardCombinationCheckAndReportIntercept(checkParam);
                if (logger.isDebugEnabled()) {
                    logger.debug("组板校验,板号【{}】,箱号/包裹号【{}】,站点【{}】.校验结果【{}】",
                            request.getBoardCode(),request.getBarCode(),request.getOperateSiteCode(),response.getMessage());
                }
            } catch (Exception e) {
                logger.error("调用组板验证服务失败：{}", com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(checkParam), e);
            }

            if (response != null && !response.getCode().equals(200)) {
                if (response.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
                    result.customMessage(com.jd.ql.dms.common.domain.JdResponse.CODE_CONFIRM, response.getMessage());
                } else {
                    result.customMessage(response.getCode(), response.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 发送组板全程跟踪
     * @param request
     * @param operateType 操作类型
     */
    @Override
    public void sendWaybillTrace(BoardCommonRequest request, Integer operateType) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        //设置站点相关属性
        tWaybillStatus.setPackageCode(request.getBarCode());

        tWaybillStatus.setCreateSiteCode(request.getOperateSiteCode());
        tWaybillStatus.setCreateSiteName(request.getOperateSiteName());

        tWaybillStatus.setOperatorId(request.getOperateUserCode());
        tWaybillStatus.setOperator(request.getOperateUserName());
        Date operateTime = request.getOperateTime() == null ? new Date() : new Date(request.getOperateTime());
        tWaybillStatus.setOperateTime(operateTime);
        tWaybillStatus.setOperateType(operateType);
        tWaybillStatus.setOperatorData(BeanConverter.convertToOperatorData(request));

        if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION)) {
            String boardReceiveSiteName = request.getReceiveSiteName();
            if(StringUtils.isBlank(boardReceiveSiteName)) {
                //兜底逻辑，上游没有传流向时，发送全流程跟踪查库获取板流向
                Response<Board> result = groupBoardManager.getBoard(request.getBoardCode());
                if (result != null && result.getCode() == ResponseEnum.SUCCESS.getIndex() && result.getData() != null) {
                    boardReceiveSiteName = result.getData().getDestination();
                }
            }
            tWaybillStatus.setRemark("包裹号：" + tWaybillStatus.getPackageCode() + "已进行组板，板号" + request.getBoardCode() + "，等待送往" + boardReceiveSiteName);
        } else if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL)) {
            tWaybillStatus.setRemark("已取消组板，板号" + request.getBoardCode());
        }

        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(tWaybillStatus.getPackageCode());
        task.setKeyword2(String.valueOf(tWaybillStatus.getOperateType()));
        task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(tWaybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());

        taskService.add(task);
    }

    /**
     * 创建单个组板
     * @param request
     * @return
     */
    @Override
    public InvokeResult<Board> createBoardCode(BoardCommonRequest request) {
        InvokeResult<Board> result = new InvokeResult<Board>();
        result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"生成板号失败!");
        AddBoardRequest addBoardRequest = new AddBoardRequest();
        try {
            if(!WaybillUtil.isWaybillCode(request.getBarCode())
                    && !WaybillUtil.isPackageCode(request.getBarCode())){
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"单号不符合规则");
                return result;
            }
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            Integer nextSiteCode = request.getReceiveSiteCode();
            if (nextSiteCode == null || nextSiteCode <= 0) {
                nextSiteCode = getNextSiteCodeByRouter(waybillCode, request.getOperateSiteCode());
            }
            if(nextSiteCode == null){
                logger.warn("根据运单号【{}】操作站点【{}】获取路由下一节点为空!",waybillCode,request.getOperateSiteCode());
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,
                        "此单路由信息获取失败,无法判断流向生成板号,请扫描其他包裹号尝试开板");
                return result;
            }
            String nextSiteName = request.getReceiveSiteName();
            if (StringUtils.isBlank(nextSiteName)) {
                BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(nextSiteCode);
                if (baseSite == null || StringUtils.isEmpty(baseSite.getSiteName())) {
                    logger.warn("根据站点【{}】获取站点名称为空!", nextSiteCode);
                    result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "站点【" + nextSiteCode + "】不存在!");
                    return result;
                }
                nextSiteName = baseSite.getSiteName();
            }
            addBoardRequest.setBoardCount(SINGLE_BOARD_CODE);
            addBoardRequest.setDestinationId(nextSiteCode);
            addBoardRequest.setDestination(nextSiteName);
            addBoardRequest.setOperatorErp(request.getOperateUserErp());
            addBoardRequest.setOperatorName(request.getOperateUserName());
            addBoardRequest.setSiteCode(request.getOperateSiteCode());
            addBoardRequest.setSiteName(request.getOperateSiteName());
            addBoardRequest.setBizSource(request.getBizSource());

            Response<List<Board>> response = groupBoardManager.createBoards(addBoardRequest);
            if(response != null && response.getCode() == ResponseEnum.SUCCESS.getIndex()
                    && !response.getData().isEmpty()){
                result.success();
                result.setData(response.getData().get(0));
            }
        }catch (Exception e){
            logger.error("根据参数【{}】生成板号异常,errMsg={}", JsonHelper.toJson(addBoardRequest), e.getMessage(), e);
        }
        return result;
    }

    /**
     * 组板转移
     * <p>
     *  1、将包裹号/箱号从原来的板上取消，绑定到新板
     *  2、发送取消旧板的全称跟踪和组到新板的全称跟踪
     * <p/>
     * @param request
     * @return
     */
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.boardMove", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<String> boardMove(BoardCommonRequest request) {
        InvokeResult result = new InvokeResult();
        MoveBoxRequest moveBoxRequest = new MoveBoxRequest();
        //新板标
        moveBoxRequest.setBoardCode(request.getBoardCode());
        moveBoxRequest.setBoxCode(request.getBarCode());
        moveBoxRequest.setSiteCode(request.getOperateSiteCode());
        moveBoxRequest.setOperatorErp(request.getOperateUserErp());
        moveBoxRequest.setOperatorName(request.getOperateUserName());
        moveBoxRequest.setBizSource(request.getBizSource());
        Response<String> tcResponse = groupBoardManager.moveBoxToNewBoard(moveBoxRequest);
        //组新板成功
        if (tcResponse != null && tcResponse.getCode() == ResponseEnum.SUCCESS.getIndex()) {
            String boardOld = tcResponse.getData();
            String boardNew = request.getBoardCode();
            //取消组板的全称跟踪 -- 旧板号
            request.setBoardCode(boardOld);
            sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL);
            //组板的全称跟踪 -- 新板号
            request.setBoardCode(boardNew);
            sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
            result.setData(tcResponse.getData());
        }else {
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"组板失败!");
        }
        return result;
    }

    /**
     * 组板转移(不校验板状态)
     * <p>
     *  1、将包裹号/箱号从原来的板上取消，绑定到新板
     *  2、发送取消旧板的全称跟踪和组到新板的全称跟踪
     * <p/>
     * @param request
     * @return
     */
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.boardMoveIgnoreStatus", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<String> boardMoveIgnoreStatus(BoardCommonRequest request) {
        InvokeResult<String> result = new InvokeResult<>();
        MoveBoxRequest moveBoxRequest = new MoveBoxRequest();
        // 新板标
        moveBoxRequest.setBoardCode(request.getBoardCode());
        moveBoxRequest.setBoxCode(request.getBarCode());
        moveBoxRequest.setSiteCode(request.getOperateSiteCode());
        moveBoxRequest.setOperatorErp(request.getOperateUserErp());
        moveBoxRequest.setOperatorName(request.getOperateUserName());
        moveBoxRequest.setBizSource(request.getBizSource());
        Response<String> tcResponse = groupBoardManager.moveBoxToNewBoardIgnoreStatus(moveBoxRequest);
        // 组新板成功
        if (tcResponse != null && tcResponse.getCode() == ResponseEnum.SUCCESS.getIndex()) {
            String boardOld = tcResponse.getData();
            String boardNew = request.getBoardCode();
            // 取消组板的全称跟踪 -- 旧板号
            request.setBoardCode(boardOld);
            sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL);

            // 组板的全称跟踪 -- 新板号
            request.setBoardCode(boardNew);
            sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);

            result.setData(tcResponse.getData());
        } else {
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"组板失败!");
        }
        return result;
    }

    /**
     * 获取路由下一跳
     * @param waybillCode 运单号
     * @param siteCode 当前站点
     * @return
     */
    @Override
    public Integer getNextSiteCodeByRouter(String waybillCode, Integer siteCode) {
        try {
            RouteNextDto routeNextDto = routerService.matchRouterNextNode(siteCode, waybillCode);
            return routeNextDto == null? null : routeNextDto.getFirstNextSiteId();
        }catch (Exception e){
            logger.error("根据运单号【{}】获取路由信息异常",waybillCode,e);
        }
        return null;
    }

    @Override
    public InvokeResult<String> interceptValidateUnloadCar(String barCode) {
        logger.info("UnloadCarServiceImpl-interceptValidateUnloadCar-barCode:{}",barCode);
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
                        logger.warn("interceptValidate卸车无重量禁止发货单号：{}",waybillCode);
                        result.setData(barCode + UnloadCarWarnEnum.NO_WEIGHT_FORBID_SEND_MESSAGE.getDesc());
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
                        result.setMessage(LoadIllegalException.FREIGTH_TEMPORARY_PAY_NO_WEIGHT_VOLUME_FORBID_SEND_MESSAGE);
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


    @Override
    public InvokeResult<Boolean> loadUnloadInterceptValidate(String waybillCode, String waybillSign) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        result.setData(false);
        JdCancelWaybillResponse jdResponse = waybillService.dealCancelWaybill(waybillCode);
        if (jdResponse != null && jdResponse.getCode() != null && !jdResponse.getCode().equals(JdResponse.CODE_OK)) {
            logger.info("运单【{}】已被拦截【{}】", waybillCode, jdResponse.getMessage());
//            result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            result.setData(true);
            result.setMessage(jdResponse.getMessage());
            return result;
        }
        //有包装服务
        if(waybillConsumableRecordService.needConfirmed(waybillCode)){
            logger.warn("loadUnloadInterceptValidate 装卸车包装服务运单未确认包装完成禁止发货单号：{}",waybillCode);
//            result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            result.setData(true);
            result.setMessage(LoadIllegalException.PACK_SERVICE_NO_CONFIRM_FORBID_SEND_MESSAGE);
            return result;
        }
        //金鹏订单
        if(!storagePackageMService.checkWaybillCanSend(waybillCode, waybillSign)){
            logger.warn("loadUnloadInterceptValidate 装卸车金鹏订单未上架集齐禁止发货单号：{}",waybillCode);
//            result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            result.setData(true);
            result.setMessage(LoadIllegalException.JIN_PENG_NO_TOGETHER_FORBID_SEND_MESSAGE);
            return result;
        }
        return result;
    }

    @Override
    public InvokeResult<Boolean> loadUnloadInterceptValidate(TransportServiceRequest request) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.success();
        result.setData(false);
        if(request == null
                || !WaybillUtil.isWaybillCode(request.getWaybillCode())
                || (StringUtils.isNotEmpty(request.getPackageCode()) && !WaybillUtil.isPackageCode(request.getPackageCode()))
                || StringUtils.isEmpty(request.getWaybillSign())){
            result.parameterError();
            return result;
        }
        String packageCode = request.getPackageCode();
        String waybillCode = request.getWaybillCode();
        String waybillSign = request.getWaybillSign();

        if(StringUtils.isNotEmpty(packageCode)){
            // 包裹维度拦截特殊校验
            loadUnloadPackIntercept(packageCode, waybillSign, result);
        }else {
            // 运单维度拦截特殊校验
            loadUnloadWaybillIntercept(waybillCode, waybillSign, result);
        }
        return result;
    }

    private void loadUnloadWaybillIntercept(String waybillCode, String waybillSign, InvokeResult<Boolean> result) {
        // 运单维度快运改址拦截
        if(BusinessUtil.isKyAddressModifyWaybill(waybillSign)){
//            BlockResponse blockResponse = waybillService.checkWaybillBlock(waybillCode, CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT);
            List<Integer> featureTypeList = new ArrayList<>(Arrays.asList(CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT, CancelWaybill.FEATURE_TYPE_KY_CHANGE_ADDRESS_CHANGE_WAYBILL)); // 拦截类型集合
            BlockResponse blockResponse = waybillService.checkWaybillBlockByFeatureTypes(waybillCode, featureTypeList);

            if (BlockResponse.BLOCK.equals(blockResponse.getCode())) {
                String hintMessage;
                // 改址换单拦截
                Integer currentFeatureType = blockResponse.getFeatureType();
                if(Objects.equals(currentFeatureType, CancelWaybill.FEATURE_TYPE_KY_CHANGE_ADDRESS_CHANGE_WAYBILL)){
                    hintMessage = HintService.getHint(HintCodeConstants.CHANGE_ADDRESS_CHANGE_WAYBILL_INTERCEPT);
                }else {
                    List<String> blockPackageList = blockResponse.getBlockPackages();
                    if(CollectionUtils.isNotEmpty(blockPackageList) && blockPackageList.size() < 5){ // 运单下拦截状态的包裹数小于5则提示具体包裹
                        List<Integer> lockPackIndexList = Lists.newArrayList();
                        for (String packCode : blockPackageList) {
                            lockPackIndexList.add(WaybillUtil.getPackIndexByPackCode(packCode));
                        }
                        Map<String, String> argsMap = new HashMap<>();
                        argsMap.put(HintArgsConstants.ARG_FIRST, JsonHelper.toJson(lockPackIndexList));
                        hintMessage = HintService.getHint(HintCodeConstants.WAYBILL_KY_ADDRESS_MODIFY_INTERCEPT_HINT, argsMap);
                    }else {
                        hintMessage = HintService.getHint(HintCodeConstants.WAYBILL_KY_ADDRESS_MODIFY_INTERCEPT);
                    }
                }
                result.setData(true);
                result.setMessage(hintMessage);
                return;
            }
        }
        loadUnloadCommonIntercept(WaybillUtil.getWaybillCode(waybillCode), waybillSign, result);
    }

    private void loadUnloadPackIntercept(String packageCode, String waybillSign, InvokeResult<Boolean> result) {
        // 包裹维度快运改址拦截
        if(BusinessUtil.isKyAddressModifyWaybill(waybillSign)){
//            BlockResponse blockResponse = waybillService.checkPackageBlock(packageCode, CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT);
            List<Integer> featureTypeList = new ArrayList<>(Arrays.asList(CancelWaybill.FEATURE_TYPE_KY_ADDRESS_MODIFY_INTERCEPT, CancelWaybill.FEATURE_TYPE_KY_CHANGE_ADDRESS_CHANGE_WAYBILL)); // 拦截类型集合
            BlockResponse blockResponse = waybillService.checkPackageBlockByFeatureTypes(packageCode, featureTypeList);

            if (BlockResponse.BLOCK.equals(blockResponse.getCode())) {
                result.setData(true);
                Integer currentFeatureType = blockResponse.getFeatureType();
                if(Objects.equals(currentFeatureType, CancelWaybill.FEATURE_TYPE_KY_CHANGE_ADDRESS_CHANGE_WAYBILL)){
                    result.setMessage(HintService.getHint(HintCodeConstants.CHANGE_ADDRESS_CHANGE_WAYBILL_INTERCEPT));
                }else {
                    result.setMessage(HintService.getHint(HintCodeConstants.PACK_KY_ADDRESS_MODIFY_INTERCEPT));
                }
                return;
            }
        }
        // 公共校验
        loadUnloadCommonIntercept(WaybillUtil.getWaybillCode(packageCode), waybillSign, result);
    }

    private void loadUnloadCommonIntercept(String waybillCode, String waybillSign, InvokeResult<Boolean> result) {
        // waybillCancel拦截（运单维度）
        JdCancelWaybillResponse jdResponse = waybillService.dealCancelWaybill(waybillCode);
        if (jdResponse != null && jdResponse.getCode() != null && !jdResponse.getCode().equals(JdResponse.CODE_OK)) {
            logger.info("运单【{}】已被拦截【{}】", waybillCode, jdResponse.getMessage());
            result.setData(true);
            result.setMessage(jdResponse.getMessage());
            return ;
        }
        // 有包装服务
        if(waybillConsumableRecordService.needConfirmed(waybillCode)){
            logger.warn("loadUnloadInterceptValidate 装卸车包装服务运单未确认包装完成禁止发货单号：{}",waybillCode);
            result.setData(true);
            result.setMessage(LoadIllegalException.PACK_SERVICE_NO_CONFIRM_FORBID_SEND_MESSAGE);
            return;
        }
        // 金鹏订单
        if(!storagePackageMService.checkWaybillCanSend(waybillCode, waybillSign)){
            logger.warn("loadUnloadInterceptValidate 装卸车金鹏订单未上架集齐禁止发货单号：{}",waybillCode);
            result.setData(true);
            result.setMessage(LoadIllegalException.JIN_PENG_NO_TOGETHER_FORBID_SEND_MESSAGE);
        }
    }

}
