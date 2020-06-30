package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.MoveBoxRequest;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private SendMDao sendMDao;

    @Autowired
    private RedisCommonUtil redisCommonUtil;

    @Autowired
    private BoxService boxService;

    @Autowired
    private SortingService sortingService;

    /**
     * 箱/包裹是否发货校验
     * @param request
     * @return
     */
    @Override
    public InvokeResult isSendCheck(BoardCommonRequest request) {
        InvokeResult result = new InvokeResult();
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getBarCode());
        sendM.setCreateSiteCode(request.getOperateSiteCode());
        sendM.setReceiveSiteCode(request.getReceiveSiteCode());
        List<SendM> sendMList = sendMDao.selectBySendSiteCode(sendM);

        if (sendMList != null && sendMList.size() > 0) {
            String logInfo = "包裹" + request.getBarCode() + "已经在批次" + sendMList.get(0).getSendCode()
                    + "中发货，站点：" + request.getOperateSiteCode();
            logger.warn(logInfo);
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,logInfo);
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
    public InvokeResult packageCountCheck(String boardCode, Integer maxCount) {
        InvokeResult result = new InvokeResult();
        //数量限制校验，每次的数量记录的redis中
        Integer count = redisCommonUtil.getData(CacheKeyConstants.REDIS_PREFIX_UNLOAD_BOARD_BINDINGS_COUNT + "-" + boardCode);
        logger.debug("板号【{}】已经绑定的包裹个数为【{}】" ,boardCode, count);
        //超上限提示
        if (count >= maxCount) {
            String logInfo = "板号" + boardCode + "已经绑定的包裹个数为" + count + "达到上限";
            logger.warn(logInfo);
            result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,logInfo);
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
            String logInfo = null;
            try {
                response = jsfSortingResourceService.boardCombinationCheck(checkParam);
                logInfo = "组板校验,板号：" + request.getBoardCode() + ",箱号/包裹号：" + request.getBarCode() +
                        ",IsForceCombination:" + request.getIsForceCombination() +
                        ",站点：" + request.getOperateSiteCode() + ".校验结果:" + response.getMessage();

                logger.debug(logInfo);
            } catch (Exception e) {
                logger.error("调用总部VER验证JSF服务失败：{}", com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(checkParam), e);
            }

            if (!response.getCode().equals(200)) {
                if (response.getCode() >= 39000) {
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
        tWaybillStatus.setOperateTime(request.getOperateTime());
        tWaybillStatus.setOperateType(operateType);

        if (operateType.equals(WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION)) {
            tWaybillStatus.setRemark("包裹号：" + tWaybillStatus.getPackageCode() + "已进行组板，板号" + request.getBoardCode());
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
    public Board createBoardCode(BoardCommonRequest request) {
        AddBoardRequest addBoardRequest = new AddBoardRequest();
        try {
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            Integer nextSiteCode = getNextSiteCodeByRouter(waybillCode, request.getOperateSiteCode());
            if(nextSiteCode == null){
                logger.warn("根据运单号【{}】操作站点【{}】获取路由下一节点为空!",waybillCode,request.getOperateSiteCode());
                return null;
            }
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(nextSiteCode);
            if(baseSite == null || StringUtils.isEmpty(baseSite.getSiteName())){
                logger.warn("根据站点【{}】获取站点名称为空!",nextSiteCode);
                return null;
            }
            addBoardRequest.setBoardCount(SINGLE_BOARD_CODE);
            addBoardRequest.setDestinationId(nextSiteCode);
            addBoardRequest.setDestination(baseSite.getSiteName());
            addBoardRequest.setOperatorErp(request.getOperateUserErp());
            addBoardRequest.setOperatorName(request.getOperateUserName());
            addBoardRequest.setSiteCode(request.getOperateSiteCode());
            addBoardRequest.setSiteName(request.getOperateSiteName());
            Response<List<Board>> response = groupBoardManager.createBoards(addBoardRequest);
            if(response != null && response.getCode() == ResponseEnum.SUCCESS.getIndex()
                    && !response.getData().isEmpty()){
                return response.getData().get(0);
            }
        }catch (Exception e){
            logger.error("根据参数【{}】生成板号异常", JsonHelper.toJson(addBoardRequest),e);
        }
        return null;
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
     * 获取路由下一跳
     * @param waybillCode 运单号
     * @param siteCode 当前站点
     * @return
     */
    @Override
    public Integer getNextSiteCodeByRouter(String waybillCode, Integer siteCode) {
        try {
            String router = jsfSortingResourceService.getRouterByWaybillCode(waybillCode);
            if(StringUtils.isEmpty(router)){
                logger.warn("根据运单号【{}】获取路由信息为空",waybillCode);
                return null;
            }
            String[] routerSplit = router.split(Constants.WAYBILL_ROUTER_SPLIT);
            if(routerSplit == null){
                logger.warn("根据运单号【{}】获取路由信息为空",waybillCode);
                return null;
            }
            for (int i = 0; i < routerSplit.length - 1; i++) {
                if(siteCode.equals(routerSplit[i])){
                    return Integer.valueOf(routerSplit[i+1]);
                }
            }
        }catch (Exception e){
            logger.error("根据运单号【{}】获取路由信息异常",waybillCode,e);
        }
        return null;
    }
}
