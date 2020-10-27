package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
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
                response = sortingCheckService.boardCombinationCheck(checkParam);
                if (logger.isDebugEnabled()) {
                    logger.debug("组板校验,板号【{}】,箱号/包裹号【{}】,站点【{}】.校验结果【{}】",
                            request.getBoardCode(),request.getBarCode(),request.getOperateSiteCode(),response.getMessage());
                }
            } catch (Exception e) {
                logger.error("调用组板验证服务失败：{}", com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(checkParam), e);
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
        tWaybillStatus.setOperateTime(new Date(request.getOperateTime()));
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
            Integer nextSiteCode = getNextSiteCodeByRouter(waybillCode, request.getOperateSiteCode());
            if(nextSiteCode == null){
                logger.warn("根据运单号【{}】操作站点【{}】获取路由下一节点为空!",waybillCode,request.getOperateSiteCode());
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,
                        "此单路由信息获取失败,无法判断流向生成板号,请扫描其他包裹号尝试开板");
                return result;
            }
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(nextSiteCode);
            if(baseSite == null || StringUtils.isEmpty(baseSite.getSiteName())){
                logger.warn("根据站点【{}】获取站点名称为空!",nextSiteCode);
                result.customMessage(InvokeResult.RESULT_INTERCEPT_CODE,"站点【" + nextSiteCode + "】不存在!");
                return result;
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
                result.success();
                result.setData(response.getData().get(0));
            }
        }catch (Exception e){
            logger.error("根据参数【{}】生成板号异常", JsonHelper.toJson(addBoardRequest),e);
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
            String router = waybillCacheService.getRouterByWaybillCode(waybillCode);
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
                if(siteCode.equals(Integer.valueOf(routerSplit[i]))){
                    return Integer.valueOf(routerSplit[i+1]);
                }
            }
        }catch (Exception e){
            logger.error("根据运单号【{}】获取路由信息异常",waybillCode,e);
        }
        return null;
    }
}
