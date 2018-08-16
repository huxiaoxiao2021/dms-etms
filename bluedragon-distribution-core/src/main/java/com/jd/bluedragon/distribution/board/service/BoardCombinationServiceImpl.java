package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryVerification;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.service.BoardMeasureService;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xumei3 on 2018/3/27.
 */

@Service("boardCombinationService")
public class BoardCombinationServiceImpl implements BoardCombinationService {
    private static final Log logger = LogFactory.getLog(BoardCombinationServiceImpl.class);

    @Resource(name = "cityDeliveryVerification")
    private DeliveryVerification cityDeliveryVerification;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private GroupBoardService groupBoardService;

    @Autowired
    private BoardMeasureService boardMeasureService;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private JsfSortingResourceService jsfSortingResourceService;

    @Autowired
    private SortingService sortingService;

    @Autowired
    private BoxService boxService;

    @Autowired
    RedisCommonUtil redisCommonUtil;

    private static final Integer STATUS_BOARD_CLOSED = 2;

    //操作组板的单位类型 0：分拣中心； 1：TC
    private static final Integer BOARD_COMBINATION_SITE_TYPE = 0;

    //批量查询板号信息，每次查询最大数量
    private static final Integer QUERY_BOARD_PAGE_SIZE = 100;

    @Value("${board.combination.bindings.count.max}")
    private Integer boardBindingsMaxCount;


    /**
     * 板号校验，如果校验成功则返回目的地信息
     *
     * @param boardCode
     * @return
     * @throws Exception
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.getBoardByBoardCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoardResponse getBoardByBoardCode(String boardCode) throws Exception {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setBoardCode(boardCode);

        //板号正则校验
        if (!SerialRuleUtil.isBoardCode(boardCode)) {
            logger.error("板号正则校验不通过：" + boardCode);
            boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_NOT_IRREGULAR, BoardResponse.MESSAGE_BOARD_NOT_IRREGULAR);
            return boardResponse;
        }

        //调用TC接口获取板的信息
        Response<Board> tcResponse = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.getBoardByCode.TCJSF", false, true);
        try {
            tcResponse = groupBoardService.getBoardByCode(boardCode);
        } catch (Exception e) {
            Profiler.functionError(info);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }

        if (tcResponse.getCode() != 200) {
            this.logger.error("调用TC接口获取板号信息失败,板号：" + boardCode);
            boardResponse.addStatusInfo(tcResponse.getCode(), tcResponse.getMesseage());
            return boardResponse;
        }

        //获取板号为空
        if (tcResponse.getData() == null) {
            this.logger.error("板号" + boardCode + "不存在");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_NOT_FOUND, BoardResponse.MESSAGE_BOARD_NOT_FOUND);
            return boardResponse;
        }

        //板号已完结
        if (STATUS_BOARD_CLOSED.equals(tcResponse.getData().getStatus())) {
            this.logger.warn("板号" + boardCode + "的状态为已经完结");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_CLOSED, BoardResponse.MESSAGE_BOARD_CLOSED);
            return boardResponse;
        }

        //板号正常，返回目的地信息
        if (logger.isInfoEnabled()) {
            logger.info("调用TC获取板信息成功!板号：" + boardCode + ",板状态：" + tcResponse.getData().getStatus() +
                    ",目的地名称：" + tcResponse.getData().getDestination() +
                    ",开板时间：" + tcResponse.getData().getCreateTime());
        }
        boardResponse.setReceiveSiteName(tcResponse.getData().getDestination());
        boardResponse.setReceiveSiteCode(tcResponse.getData().getDestinationId());

        return boardResponse;
    }


    /**
     * 校验板号是否可以操作发货
     * @param boardCode
     * @return
     * @throws Exception
     */
    public BoardResponse checkBoardCanSend(String boardCode) throws Exception {
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setBoardCode(boardCode);

        //板号正则校验
        if (!SerialRuleUtil.isBoardCode(boardCode)) {
            logger.error("板号正则校验不通过：" + boardCode);
            boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_NOT_IRREGULAR, BoardResponse.MESSAGE_BOARD_NOT_IRREGULAR);
            return boardResponse;
        }

        //调用TC接口获取板的信息
        Response<Board> tcResponse = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.getBoardByCode.TCJSF", false, true);
        try {
            tcResponse = groupBoardService.getBoardByCode(boardCode);
        } catch (Exception e) {
            Profiler.functionError(info);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }

        if (tcResponse.getCode() != 200) {
            this.logger.error("调用TC接口获取板号信息失败,板号：" + boardCode);
            boardResponse.addStatusInfo(tcResponse.getCode(), tcResponse.getMesseage());
            return boardResponse;
        }

        //获取板号为空
        if (tcResponse.getData() == null) {
            this.logger.error("板号" + boardCode + "不存在");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_NOT_FOUND, BoardResponse.MESSAGE_BOARD_NOT_FOUND);
            return boardResponse;
        }

        return boardResponse;
    }

    /**
     * 组板操作
     *
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.sendBoardBindings", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer sendBoardBindings(BoardCombinationRequest request, BoardResponse boardResponse) throws Exception {
        boardResponse.setBoardCode(request.getBoardCode());
        if (SerialRuleUtil.isMatchBoxCode(request.getBoxOrPackageCode())) {
            boardResponse.setBoxCode(request.getBoxOrPackageCode());
        } else {
            boardResponse.setPackageCode(request.getBoxOrPackageCode());
        }

        String boardCode = request.getBoardCode();
        String boxOrPackageCode = request.getBoxOrPackageCode();
        String logInfo = "";

        //数量限制校验，每次的数量记录的redis中
        Integer count = redisCommonUtil.getData(CacheKeyConstants.REDIS_PREFIX_BOARD_BINDINGS_COUNT + "-" + boardCode);
        logger.info("板号：" + boardCode + "已经绑定的包裹/箱号个数为：" + count);

        //超上限提示
        if (count >= boardBindingsMaxCount) {
            logger.warn("板号：" + boardCode + "已经绑定的包裹/箱号个数为：" + count + "达到上限.");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOXORPACKAGE_REACH_LIMIT, BoardResponse.MESSAGE_BOXORPACKAGE_REACH_LIMIT);

            return JdResponse.CODE_FAIL;
        }

        //查询发货记录判断是否已经发货
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getBoxOrPackageCode());
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());

        List<SendM> sendMList = this.selectBySendSiteCode(sendM);

        if (null != sendMList && sendMList.size() > 0) {
            logInfo = "箱号/包裹" + sendMList.get(0).getBoxCode() + "已经在批次" + sendMList.get(0).getSendCode() + "中发货，站点：" + request.getSiteCode();

            logger.warn(logInfo);
            boardResponse.addStatusInfo(BoardResponse.CODE_BOX_PACKAGE_SENDED, BoardResponse.MESSAGE_BOX_PACKAGE_SENDED);

            addSystemLog(request, logInfo);
            return JdResponse.CODE_FAIL;
        }

        //一单多件不齐校验
        if (!request.getIsForceCombination()) {
            DeliveryVerification.VerificationResult verificationResult = cityDeliveryVerification.verification(request.getBoxOrPackageCode(), null, false);
            if (!verificationResult.getCode()) {//按照箱发货，校验派车单是否齐全，判断是否强制发货
                boardResponse.addStatusInfo(BoardResponse.CODE_PACAGES_NOT_ENOUGH, verificationResult.getMessage());

                return JdResponse.CODE_CONFIRM;
            }
        }

        //调Ver的接口进行组板拦截
        //如果是箱号则取其中任一包裹进行校验
        BoardCombinationJsfResponse response = null;
        if (!request.getIsForceCombination()) {
            BoardCombinationRequest checkParam = new BoardCombinationRequest();
            checkParam.setSiteCode(request.getSiteCode());
            checkParam.setReceiveSiteCode(request.getReceiveSiteCode());
            checkParam.setBusinessType(request.getBusinessType());
            checkParam.setUserCode(request.getUserCode());
            checkParam.setUserName(request.getUserName());

            if (BusinessHelper.isPackageCode(request.getBoxOrPackageCode())) {
                checkParam.setBoxOrPackageCode(request.getBoxOrPackageCode());
            } else if (BusinessHelper.isBoxcode(request.getBoxOrPackageCode())) {
                Box box = boxService.findBoxByCode(request.getBoxOrPackageCode());
                if (box != null) {
                    List<Sorting> sortings = sortingService.findByBoxCodeAndFetchNum(box.getCode(), box.getCreateSiteCode(), 1);
                    if (sortings == null || sortings.isEmpty()) {
                        boardResponse.addStatusInfo(BoardResponse.CODE_BOX_NO_SORTING, BoardResponse.MESSAGE_BOX_NO_SORTING);
                        return JdResponse.CODE_CONFIRM;
                    }
                    checkParam.setBoxOrPackageCode(sortings.get(0).getPackageCode());

                } else {
                    boardResponse.addStatusInfo(BoardResponse.CODE_BOX_NOT_EXIST, BoardResponse.MESSAGE_BOX_NOT_EXIST);
                    return JdResponse.CODE_FAIL;
                }
            } else {
                boardResponse.addStatusInfo(BoardResponse.CODE_BOX_PACKAGECODE_ERROR, BoardResponse.MESSAGE_BOX_PACKAGECODE_ERROR);
                return JdResponse.CODE_FAIL;
            }

            CallerInfo info1 = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.sendBoardBindings.boardCombinationCheck", false, true);
            try {
                response = jsfSortingResourceService.boardCombinationCheck(checkParam);
                logInfo = "组板校验,板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode +
                        ",IsForceCombination:" + request.getIsForceCombination() +
                        ",站点：" + request.getSiteCode() + ".校验结果:" + response.getMessage();

                this.logger.info(logInfo);
                addSystemLog(request, logInfo);
            } catch (Exception ex) {
                Profiler.functionError(info1);
                logger.error("调用总部VER验证JSF服务失败", ex);
                return JdResponse.CODE_ERROR;
            } finally {
                Profiler.registerInfoEnd(info1);
            }

            if (!response.getCode().equals(200)) {//如果校验不OK
                if (response.getCode() >= 39000) {
                    boardResponse.addStatusInfo(response.getCode(), response.getMessage());
                    return JdResponse.CODE_CONFIRM;
                } else {
                    boardResponse.addStatusInfo(response.getCode(), response.getMessage());
                    return JdResponse.CODE_FAIL;
                }
            }
        }

        //调用TC接口将组板数据推送给TC
        Response<Integer> tcResponse = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.addBoxToBoard.TCJSF", false, true);
        try {
            AddBoardBox addBoardBox = new AddBoardBox();
            addBoardBox.setBoardCode(request.getBoardCode());
            addBoardBox.setBoxCode(request.getBoxOrPackageCode());
            addBoardBox.setOperatorErp(request.getUserCode() + "");
            addBoardBox.setOperatorName(request.getUserName());
            addBoardBox.setSiteCode(request.getSiteCode());
            addBoardBox.setSiteName(request.getSiteName());
            addBoardBox.setSiteType(BOARD_COMBINATION_SITE_TYPE);
            tcResponse = groupBoardService.addBoxToBoard(addBoardBox);
        } catch (Exception e) {
            Profiler.functionError(info);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }

        if (tcResponse.getCode() != 200) {
            //如果返回值的code是500，表示已经组过板，则提示是否转移，如果确定转移，则从原来的板上取消，移动到新板上
            if (tcResponse.getCode() == 500) {
                //提示是否组到新板
                if (!request.getIsForceCombination()) {
                    boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_CHANGE, tcResponse.getMesseage() + BoardResponse.Message_BOARD_CHANGE);
                    return JdResponse.CODE_CONFIRM;
                }
                //确定转移,调用TC的板号转移接口
                Response<String> boardMoveResponse = boardMove(request);

                if (boardMoveResponse.getCode() != 200) {
                    //重新组板失败
                    logInfo = "组板转移失败,原板号：" + boardMoveResponse.getData() + ",新板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode +
                            ",站点：" + request.getSiteCode() + ".失败原因:" + tcResponse.getMesseage();

                    this.logger.warn(logInfo);
                    boardResponse.addStatusInfo(tcResponse.getCode(), tcResponse.getMesseage());
                    addSystemLog(request, logInfo);

                    return JdResponse.CODE_FAIL;
                }

                logInfo = "组板转移成功.原板号:" + boardMoveResponse.getData() + ",新板号:" + boardCode + ",箱号/包裹号：" + boxOrPackageCode +
                        ",站点：" + request.getSiteCode();
                logger.info(logInfo);

                addSystemLog(request, logInfo);
                addOperationLog(request, OperationLog.BOARD_COMBINATITON);
                return JdResponse.CODE_SUCCESS;
            }

            logInfo = "组板失败,板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode +
                    ",站点：" + request.getSiteCode() + ".失败原因:" + tcResponse.getMesseage();

            this.logger.warn(logInfo);
            boardResponse.addStatusInfo(tcResponse.getCode(), tcResponse.getMesseage());
            addSystemLog(request, logInfo);

            return JdResponse.CODE_FAIL;
        }

        logInfo = "组板成功!板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode + ",站点：" + request.getSiteCode();
        //组板成功
        logger.info(logInfo);

        //缓存+1
        redisCommonUtil.incr(CacheKeyConstants.REDIS_PREFIX_BOARD_BINDINGS_COUNT + "-" + boardCode);

        //记录操作日志
        addSystemLog(request, logInfo);

        addOperationLog(request, OperationLog.BOARD_COMBINATITON);

        //发送全称跟踪
        sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);

        return JdResponse.CODE_SUCCESS;
    }

    /**
     * 回传板标的发货状态
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.closeBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Boolean> closeBoard(String boardCode) {
        return groupBoardService.closeBoard(boardCode);
    }

    /**
     * 查询发货信息
     *
     * @param sendM
     * @return
     */
    public List<SendM> selectBySendSiteCode(SendM sendM) {
        //加上参数校验
        return this.sendMDao.selectBySendSiteCode(sendM);
    }

    /**
     * 获取组板明细
     *
     * @param boardCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.getBoxesByBoardCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<String>> getBoxesByBoardCode(String boardCode) {
        return groupBoardService.getBoxesByBoardCode(boardCode);
    }

    /**
     * 获取箱号所属的板号
     *
     * @param siteCode
     * @param boxCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.getBoardCodeByBoxCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Board> getBoardByBoxCode(Integer siteCode, String boxCode) {
        return groupBoardService.getBoardByBoxCode(boxCode, siteCode);
    }

    /**
     * 清除组板时加的板号缓存
     *
     * @param boardCode
     * @return
     */
    @Override
    public boolean clearBoardCache(String boardCode) {
        redisCommonUtil.del(CacheKeyConstants.REDIS_PREFIX_BOARD_BINDINGS_COUNT + "-" + boardCode);//清除组板时加的板号缓存
        return true;
    }

    /**
     * 取消组板操作
     * 调用TC接口取消组板
     * 记录操作日志
     * 发送取消组板的全称跟踪
     *
     * @param request
     */
    public BoardResponse boardCombinationCancel(BoardCombinationRequest request) throws Exception {
        BoardResponse boardResponse = new BoardResponse();

        String boardCode = request.getBoardCode();
        String boxOrPackageCode = request.getBoxOrPackageCode();
        String logInfo = "";

        //取消组板，组织参数，必备参数：板号、箱号/包裹号、操作单位信息、操作人信息
        BoardBoxRequest boardBox = new BoardBoxRequest();
        boardBox.setBoardCode(request.getBoardCode());
        boardBox.setBoxCode(request.getBoxOrPackageCode());
        boardBox.setOperatorErp(request.getUserCode() + "");
        boardBox.setOperatorName(request.getUserName());
        boardBox.setSiteCode(request.getSiteCode());

        //调用TC接口取消组板
        Response<String> tcResponse = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.boardCombinationCancel.TCJSF", Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            tcResponse = groupBoardService.removeBoardBox(boardBox);
        } catch (Exception e) {
            Profiler.functionError(info);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }

        //调用TC接口返回值，更新下板号
        if (tcResponse != null && StringHelper.isNotEmpty(tcResponse.getData())) {
            boardCode = tcResponse.getData();
            request.setBoardCode(boardCode);
        }

        //取消组板失败
        if (tcResponse.getCode() != 200) {
            logInfo = "取消组板失败,板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode +
                    ",站点：" + request.getSiteCode() + ".失败原因:" + tcResponse.getMesseage();

            this.logger.warn(logInfo);
            boardResponse.addStatusInfo(tcResponse.getCode(), tcResponse.getMesseage());
            addSystemLog(request, logInfo);

            return boardResponse;
        }


        //取消组板成功
        //记录操作日志
        logInfo = "取消组板成功!板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode + ",站点：" + request.getSiteCode();
        addSystemLog(request, logInfo);
        addOperationLog(request, OperationLog.BOARD_COMBINATITON_CANCEL);

        //缓存-1 //
        redisCommonUtil.decr(CacheKeyConstants.REDIS_PREFIX_BOARD_BINDINGS_COUNT + "-" + boardCode);

        //发送取消组板的全称跟踪
        sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL);

        return boardResponse;
    }

    /**
     * 记录组板操作日志
     *
     * @param
     * @param
     */
    public void addSystemLog(BoardCombinationRequest request, String log) {
        if (request == null || request.getBoxOrPackageCode() == null || request.getBoardCode() == null) {
            return;
        }
        Goddess goddess = new Goddess();
        goddess.setHead(request.getBoardCode() + "-" + request.getBoxOrPackageCode());
        goddess.setKey(request.getBoxOrPackageCode());
        goddess.setDateTime(new Date());

        goddess.setBody(JsonHelper.toJson(log));
        goddessService.save(goddess);
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

    /**
     * 组装回全称跟踪对象
     *
     * @param request
     * @return
     */
    private WaybillStatus getWaybillStatus(BoardCombinationRequest request, Integer operateType) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        //设置站点相关属性
        tWaybillStatus.setPackageCode(request.getBoxOrPackageCode());

        tWaybillStatus.setCreateSiteCode(request.getSiteCode());
        tWaybillStatus.setCreateSiteName(request.getSiteName());

        tWaybillStatus.setOperatorId(request.getUserCode());
        tWaybillStatus.setOperator(request.getUserName());
        tWaybillStatus.setOperateTime(new Date());
        tWaybillStatus.setOperateType(operateType);

        if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION)) {
            tWaybillStatus.setRemark("包裹号：" + tWaybillStatus.getPackageCode() + "已进行组板，板号" + request.getBoardCode());
        } else if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL)) {
            tWaybillStatus.setRemark("已取消组板，板号" + request.getBoardCode());
        }

        return tWaybillStatus;
    }

    /**
     * 发送全称跟踪
     *
     * @param request
     * @param operateType
     */
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.boardSendTrace", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    private void sendWaybillTrace(BoardCombinationRequest request, Integer operateType) {
        try {
            WaybillStatus waybillStatus = this.getWaybillStatus(request, operateType);
            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e) {
            logger.error("组板操作发送全称跟踪失败.", e);
        }
    }

    /**
     * 增加分拣中心操作日志
     *
     * @param request
     */
    public void addOperationLog(BoardCombinationRequest request, Integer logType) {
        OperationLog operationLog = new OperationLog();
        if (SerialRuleUtil.isMatchBoxCode(request.getBoxOrPackageCode())) {
            operationLog.setBoxCode(request.getBoxOrPackageCode());
        } else {
            operationLog.setWaybillCode(BusinessHelper.getWaybillCodeByPackageBarcode(request.getBoxOrPackageCode()));
            operationLog.setPackageCode(request.getBoxOrPackageCode());
        }
        operationLog.setRemark(request.getBoardCode());
        operationLog.setCreateSiteCode(request.getSiteCode());
        operationLog.setCreateSiteName(request.getSiteName());
        operationLog.setCreateUser(request.getUserName());
        operationLog.setCreateUserCode(request.getUserCode());
        operationLog.setCreateTime(new Date());
        operationLog.setOperateTime(new Date());
        operationLog.setLogType(logType);

        this.operationLogService.add(operationLog);
    }


    /**
     * 组板转移，将包裹号/箱号从原来的板上取消，绑定到新板
     * 调用TC的接口实现转移，发送取消旧板的全称跟踪和组到新板的全称跟踪
     *
     * @param request
     * @return
     */
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.boardMove", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    private Response<String> boardMove(BoardCombinationRequest request) {
        MoveBoxRequest moveBoxRequest = new MoveBoxRequest();
        //新板标
        moveBoxRequest.setBoardCode(request.getBoardCode());
        moveBoxRequest.setBoxCode(request.getBoxOrPackageCode());
        moveBoxRequest.setSiteCode(request.getSiteCode());
        moveBoxRequest.setOperatorErp(request.getUserCode() + "");
        moveBoxRequest.setOperatorName(request.getUserName());

        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.moveBoxToNewBoard.TCJSF", Constants.UMP_APP_NAME_DMSWEB, false, true);
        Response<String> tcResponse = groupBoardService.moveBoxToNewBoard(moveBoxRequest);
        Profiler.registerInfoEnd(info);
        //组新板成功
        if (tcResponse != null && tcResponse.getCode() == 200) {
            String boardOld = tcResponse.getData();
            String boardNew = request.getBoardCode();
            //取消组板的全称跟踪 -- 旧板号
            request.setBoardCode(boardOld);
            sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL);

            //组板的全称跟踪 -- 新板号
            request.setBoardCode(boardNew);
            sendWaybillTrace(request, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
        }
        return tcResponse;
    }

    /**
     * 批量查询板号信息，分页查询，每次查询100条，
     *
     * @param boardList
     * @return boardCodes
     * @throws Exception
     */
    @Override
    @JProfiler(jKey = "DMSWEB.BoardCombinationServiceImpl.getBoardVolumeByBoardCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BoardMeasureDto> getBoardVolumeByBoardCode(List<String> boardList) throws Exception {
        List<BoardMeasureDto> boardCodes = null;
        if (boardList != null && !boardList.isEmpty()) {
            int totalNum = boardList.size();
            int startNum = 0;
            boardCodes = new ArrayList<BoardMeasureDto>(boardList.size());
            do {
                int endNum = startNum + QUERY_BOARD_PAGE_SIZE;
                if (endNum > totalNum) {
                    endNum = totalNum;
                }
                CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.BoardMeasureService.getBoardMeasure.TCJSF");
                try {
                    Response<List<BoardMeasureDto>> tcResponse = boardMeasureService.getBoardMeasure(boardList.subList(startNum, endNum));
                    if (tcResponse != null && JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())) {
                        if (tcResponse.getData() != null && !tcResponse.getData().isEmpty()) {
                            boardCodes.addAll(tcResponse.getData());
                        }
                    } else {
                        logger.warn("批量查询板号信息出错,返回结果：" + JsonHelper.toJson(tcResponse) + ";板号信息：" + boardList.subList(startNum, endNum).toString());
                    }
                } catch (Exception e) {
                    Profiler.functionError(info);
                    logger.error("批量查询板号信息出错,板号：" + boardList.subList(startNum, endNum).toString(), e);
                    throw e;

                } finally {
                    Profiler.functionError(info);
                }
                startNum = startNum + QUERY_BOARD_PAGE_SIZE;
            } while (startNum < totalNum);
        }

        return boardCodes;
    }

}
