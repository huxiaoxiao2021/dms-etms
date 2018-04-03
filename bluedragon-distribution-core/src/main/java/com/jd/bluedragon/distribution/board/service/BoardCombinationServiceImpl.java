package com.jd.bluedragon.distribution.board.service;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
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
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
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
    private GoddessService goddessService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;


    @Autowired
    RedisCommonUtil redisCommonUtil;

    private static final Integer STATUS_BOARD_CLOSED = 2;

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
        }catch (Exception e){
            Profiler.functionError(info);
            throw e;
        }finally {
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
        if (tcResponse.getData().getStatus() == STATUS_BOARD_CLOSED) {
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

        //数量限制校验，每次的数量记录的redis中
        Integer count = redisCommonUtil.getData(CacheKeyConstants.REDIS_PREFIX_BOARD_BINDINGS_COUNT + "-" + boardCode);
        logger.info("板号：" + boardCode + "已经绑定的包裹/箱号个数为：" + count);

        //超上限提示
        if (count >= boardBindingsMaxCount) {
            logger.error("板号：" + boardCode + "已经绑定的包裹/箱号个数为：" + count + "达到上限.");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOXORPACKAGE_REACH_LIMIT, BoardResponse.MESSAGE_BOXORPACKAGE_REACH_LIMIT);

            return JdResponse.CODE_FAIL;
        }

        //查询发货记录判断是否已经发货
        //// FIXME: 2018/3/31 优化点：发货放在缓存--->组件1   校验流程组装--->组件2
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getBoxOrPackageCode());
        sendM.setCreateSiteCode(request.getSiteCode());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());

        List<SendM> sendMList = this.selectBySendSiteCode(sendM);

        if (null != sendMList && sendMList.size() > 0) {
            logger.error("箱号/包裹" + sendMList.get(0).getBoxCode() + "已经在批次" + sendMList.get(0).getSendCode() + "中发货");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOX_PACKAGE_SENDED, BoardResponse.MESSAGE_BOX_PACKAGE_SENDED);

            addSystemLog(boardResponse);
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

        //调用TC接口将组板数据推送给TC
        Response<Integer> tcResponse = null;
        CallerInfo info = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.addBoxToBoard.TCJSF", false, true);
        try {
            AddBoardBox addBoardBox = new AddBoardBox();
            addBoardBox.setBoardCode(request.getBoardCode());
            addBoardBox.setBoxCode(request.getBoxOrPackageCode());
            addBoardBox.setOperatorErp(request.getUserCode()+"");
            addBoardBox.setOperatorName(request.getUserName());
            addBoardBox.setSiteCode(request.getSiteCode());
            addBoardBox.setSiteName(request.getSiteName());
            groupBoardService.addBoxToBoard(addBoardBox);
        }catch (Exception e){
            Profiler.functionError(info);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }

        if (tcResponse.getCode() == 500) {
            this.logger.error("板号" + boardCode + "已绑定到其他板号下.");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOX_PACKAGE_BINDINGED, BoardResponse.MESSAGE_BOX_PACKAGE_BINDINGED);
            addSystemLog(boardResponse);

            return JdResponse.CODE_FAIL;
        }

        if (tcResponse.getCode() == 501) {
            this.logger.error("板号" + boardCode + "已完结,请更换板号.");
            boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_CLOSED, BoardResponse.MESSAGE_BOARD_CLOSED);
            addSystemLog(boardResponse);

            return JdResponse.CODE_FAIL;
        }

        if (tcResponse.getCode() != 200) {
            this.logger.error("组板数据推送给TC失败,板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode);
            boardResponse.addStatusInfo(tcResponse.getCode(), tcResponse.getMesseage());
            addSystemLog(boardResponse);

            return JdResponse.CODE_FAIL;
        }

        //组板成功
        logger.info("组板成功!板号：" + boardCode + ",箱号/包裹号：" + boxOrPackageCode);

        //缓存+1
        redisCommonUtil.cacheData(CacheKeyConstants.REDIS_PREFIX_BOARD_BINDINGS_COUNT + "-" + boardCode, count + 1);

        //记录操作日志
        addSystemLog(boardResponse);
        addOperationLog(request);

        CallerInfo infoTrace = Profiler.registerInfo("DMSWEB.BoardCombinationServiceImpl.boardSendTrace", false, true);
        try {
            //发送全称跟踪
            WaybillStatus waybillStatus = this.getWaybillStatus(request);
            // 添加到task表
            taskService.add(toTask(waybillStatus));

        } catch (Exception e){
            Profiler.functionError(infoTrace);
            logger.error("发送全称跟踪失败.",e);
        } finally {
            Profiler.registerInfoEnd(infoTrace);
        }

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
     * 记录组板操作日志
     *
     * @param
     * @param
     */
    public void addSystemLog(BoardResponse response) {
        if (response == null || response.getBoxCode() == null || response.getBoardCode() == null) {
            return;
        }

        Goddess goddess = new Goddess();
        goddess.setHead(response.getBoardCode() + "-" + response.getBoxCode());
        goddess.setDateTime(new Date());
        goddess.setKey(response.getBoxCode());
        goddess.setBody(JsonHelper.toJson(response));

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
        task.setKeyword2(String.valueOf(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION));
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
    private WaybillStatus getWaybillStatus(BoardCombinationRequest request) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        //设置站点相关属性
        tWaybillStatus.setPackageCode(request.getBoxOrPackageCode());

        tWaybillStatus.setCreateSiteCode(request.getSiteCode());
        tWaybillStatus.setCreateSiteName(request.getSiteName());

        tWaybillStatus.setOperatorId(request.getUserCode());
        tWaybillStatus.setOperator(request.getUserName());
        tWaybillStatus.setOperateTime(new Date());
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);


        tWaybillStatus.setRemark("包裹号：" + tWaybillStatus.getPackageCode() + "已进行组板，板号" + request.getBoardCode());
        return tWaybillStatus;
    }

    /**
     * 增加分拣中心操作日志
     *
     * @param request
     */
    public void addOperationLog(BoardCombinationRequest request) {
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
        operationLog.setLogType(OperationLog.BOARD_COMBINATITON);

        this.operationLogService.add(operationLog);
    }
}
