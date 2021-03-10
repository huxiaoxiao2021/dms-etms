package com.jd.bluedragon.distribution.rest.board;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BoardStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xumei3 on 2018/3/27.
 */

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BoardCombinationResource {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoardCombinationService boardCombinationService;
    @Autowired
    private BoardCommonManager boardCommonManager;

    @GET
    @Path("/boardCombination/barCodeValidation")
    public JdResponse<BoardResponse> validation(@QueryParam("boardCode") String boardCode) {
        JdResponse<BoardResponse> result =new JdResponse<BoardResponse>();
        result.setData(new BoardResponse());
        BoardResponse boardResponse = result.getData();

        log.info("组板操作扫描的板号为:{}", boardCode);

        //参数为空校验
        if(StringHelper.isEmpty(boardCode)){
            result.toError("参数错误.板号为空!");
            return result;
        }

        try {
            boardResponse = boardCombinationService.getBoardByBoardCode(boardCode);
            if(boardResponse.getStatusInfo() != null && boardResponse.getStatusInfo().size() >0){
                result.toFail(boardResponse.buildStatusMessages());
            }
            result.setData(boardResponse);
        } catch (Exception e) {
            log.error("板号校验失败!",e);
            boardResponse.addStatusInfo(JdResponse.CODE_ERROR,"板号校验失败，系统异常！");
            result.toError("板号校验失败，系统异常！");
        }

        return result;
    }

    /**
     * 组板功能
     */
    @POST
    @Path("/boardCombination/combination")
    public JdResponse<BoardResponse> combination(BoardCombinationRequest request) {
        JdResponse<BoardResponse> result = new JdResponse<BoardResponse>();
          result.setData(new BoardResponse());
        BoardResponse boardResponse = result.getData();

        //参数校验
        String errStr = this.combinationVertify(request);
        if(StringHelper.isNotEmpty(errStr)){
            result.toFail(errStr);
            boardResponse.addStatusInfo(JdResponse.CODE_FAIL,errStr);
            return result;
        }

        return combinationNew(request);
    }

    /**
     * 新组板功能，适配按运单组板，防止老版本PDA调用误操作运单号
     *
     */
    @POST
    @Path("/boardCombination/combination/new")
    public JdResponse<BoardResponse> combinationNew(BoardCombinationRequest request) {
        JdResponse<BoardResponse> result = new JdResponse<BoardResponse>();
        result.setData(new BoardResponse());
        BoardResponse boardResponse = result.getData();

        //参数校验
        String errStr = this.combinationVertifyNew(request);
        if(StringHelper.isNotEmpty(errStr)){
            result.toFail(errStr);
            boardResponse.addStatusInfo(JdResponse.CODE_FAIL,errStr);
            return result;
        }

        try {
            //操作组板，返回状态码
            Integer statusCode = boardCombinationService.sendBoardBindings(request,boardResponse);
            if(JdResponse.CODE_FAIL.equals(statusCode)){
                result.toFail(boardResponse.buildStatusMessages());
            }else if(JdResponse.CODE_CONFIRM.equals(statusCode)){
                result.toConfirm(boardResponse.buildStatusMessages());
            }else if(JdResponse.CODE_SUCCESS.equals(statusCode)){
                return result;
            }
        } catch (Exception e) {
            log.error("组板失败!", e);
            boardResponse.addStatusInfo(JdResponse.CODE_ERROR,"组板失败，系统异常！");
            result.toError("组板失败，系统异常！");
        }

        return result;
    }

    /**
     * 组板(自动生成板号)
     */
    public JdResponse<BoardResponse> combinationBoardNew(CombinationBoardRequest request) {
        JdResponse<BoardResponse> result = new JdResponse<>();
        result.setData(new BoardResponse());
        BoardResponse boardResponse = result.getData();

        // 参数校验
        String errStr = checkCombinationBoardParam(request);
        if (StringHelper.isNotEmpty(errStr)) {
            boardResponse.addStatusInfo(JdResponse.CODE_FAIL, errStr);
            result.toFail(errStr);
            return result;
        }
        try {
            Board oldBoard = null;
            // 查询之前是否组过板
            JdResponse<Board> response = getBoardByBoxCode(request.getCurrentOperate().getSiteCode(), request.getBoxOrPackageCode());
            if (response != null && JdResponse.CODE_SUCCESS.equals(response.getCode())) {
                oldBoard = response.getData();
            }
            // 第一次则生成板号
            if (StringUtils.isBlank(request.getBoardCode())) {
                // 如果之前组过，并且板没有关闭则返回继续使用
                if (oldBoard != null && !oldBoard.getStatus().equals(BoardStatus.CLOSED.getIndex())) {
                    boardResponse.setBoardCode(oldBoard.getCode());
                    boardResponse.setReceiveSiteCode(oldBoard.getDestinationId());
                    boardResponse.setReceiveSiteName(oldBoard.getDestination());
                    return result;
                }

            } else {
                // 如果之前已经组板，并且就是现在的板，则提示请勿重复扫描
                if (oldBoard != null && request.getBoardCode().equals(oldBoard.getCode())) {
                    boardResponse.addStatusInfo(JdResponse.CODE_FAIL, String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE,
                            request.getBoxOrPackageCode(), request.getBoardCode()));
                    result.toFail(String.format(LoadIllegalException.PACKAGE_IS_SCAN_INTERCEPT_MESSAGE,
                            request.getBoxOrPackageCode(), request.getBoardCode()));
                    return result;
                }
            }
            // 对象转换
            BoardCombinationRequest combinationRequest = convertToBoardCombinationRequest(request);
            // 操作组板，返回状态码
            Integer statusCode = boardCombinationService.sendBoardBindingsNew(combinationRequest, boardResponse, oldBoard, request);
            if (JdResponse.CODE_FAIL.equals(statusCode)) {
                result.toFail(boardResponse.buildStatusMessages());
            } else if (JdResponse.CODE_CONFIRM.equals(statusCode)) {
                result.toConfirm(boardResponse.buildStatusMessages());
            } else if (JdResponse.CODE_SUCCESS.equals(statusCode)) {
                return result;
            }
        } catch (Exception e) {
            log.error("组板失败!", e);
            boardResponse.addStatusInfo(JdResponse.CODE_ERROR, "组板失败，系统异常！");
            result.toError("组板失败，系统异常！");
        }
        return result;
    }

    /**
     * 取消组板
     * @param request
     * @return
     */
    @POST
    @Path("/boardCombination/combination/cancel")
    public JdResponse<BoardResponse> cancel(BoardCombinationRequest request){
        JdResponse<BoardResponse> result = new JdResponse<BoardResponse>();
        result.setData(new BoardResponse());
        BoardResponse boardResponse = result.getData();
        result.toSucceed("取消组板成功");

        //参数校验
        String errStr = this.combinationVertify(request);
        if(StringHelper.isNotEmpty(errStr)){
            result.toFail(errStr);
            boardResponse.addStatusInfo(JdResponse.CODE_FAIL,errStr);
            return result;
        }

        try {
            boardResponse = boardCombinationService.boardCombinationCancel(request);
            if(boardResponse.getStatusInfo() != null && boardResponse.getStatusInfo().size() >0){
                result.toFail(boardResponse.buildStatusMessages());
            }
            result.setData(boardResponse);
        } catch (Exception e) {
            log.error("取消组板失败!",e);
            boardResponse.addStatusInfo(JdResponse.CODE_ERROR,"取消组板失败，系统异常！");
            result.toError("取消组板失败，系统异常！");
        }

        return result;
    }

    /**
     * 取消组板(最新版)
     */
    public JdResponse<BoardResponse> combinationBoardCancelNew(CombinationBoardRequest request) {
        JdResponse<BoardResponse> result = new JdResponse<>();
        result.setData(new BoardResponse());
        BoardResponse boardResponse = result.getData();
        result.toSucceed("取消组板成功");

        // 参数校验
        String errStr = checkCancelCombinationBoardParam(request);
        if (StringHelper.isNotEmpty(errStr)) {
            result.toFail(errStr);
            boardResponse.addStatusInfo(JdResponse.CODE_FAIL,errStr);
            return result;
        }
        String boxCode = request.getBoxOrPackageCode();
        Integer siteCode = request.getCurrentOperate().getSiteCode();
        try {
            // 获取包裹号所属板号
            Response<Board> tcResponse = boardCombinationService.getBoardByBoxCode(siteCode, boxCode);
            if (tcResponse == null) {
                result.toError("根据包裹号和操作站点查询板信息返回空");
                return result;
            }
            if (!JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())) {
                result.toFail(tcResponse.getMesseage());
                return result;
            }
            if (tcResponse.getData() == null) {
                result.toFail("此包裹号未组板！");
                return result;
            }
            // 组装参数
            Board board = tcResponse.getData();
            request.setBoardCode(board.getCode());
            request.setReceiveSiteName(board.getDestination());
            request.setReceiveSiteCode(board.getDestinationId());
            // 转换实体
            BoardCombinationRequest combinationRequest = convertToBoardCombinationRequest(request);
            // 取消组板
            boardResponse = boardCombinationService.boardCombinationCancel(combinationRequest);
            if (boardResponse.getStatusInfo() != null && boardResponse.getStatusInfo().size() > 0) {
                result.toFail(boardResponse.buildStatusMessages());
            }
            boardResponse.setBoardCode(board.getCode());
            boardResponse.setReceiveSiteName(board.getDestination());
            boardResponse.setReceiveSiteCode(board.getDestinationId());
            result.setData(boardResponse);
        } catch (Exception e) {
            log.error("取消组板失败!", e);
            boardResponse.addStatusInfo(JdResponse.CODE_ERROR, "取消组板失败，系统异常！");
            result.toError("取消组板失败，系统异常！");
        }

        return result;
    }

    /**
     * 查询箱子所属板号
     * @param boxCode
     * @return
     */
    @GET
    @Path("/boardCombination/belong/{siteCode}/{boxCode}")
    public JdResponse<Board> getBoardByBoxCode(@PathParam("siteCode") Integer siteCode, @PathParam("boxCode") String boxCode){
        JdResponse<Board> result = new JdResponse<Board>();
        result.toSucceed("查询箱子所属板号成功!");

        //参数校验

        if(BusinessHelper.isBoxcode(boxCode) || WaybillUtil.isPackageCode(boxCode)){
            try {
                Response<Board> tcResponse = boardCombinationService.getBoardByBoxCode(siteCode, boxCode);
                if(tcResponse == null){
                    result.toFail("组板组件返回结果为空！");
                }else if(JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())){
                    //查询成功
                    if(tcResponse.getData() != null){
                        result.setData(tcResponse.getData());
                    }else{
                        result.toFail("未查询到板号");
                    }
                }else {
                    //查询失败
                    result.toFail(tcResponse.getMesseage());
                }
            } catch (Exception e) {
                log.error("查询箱子所属板号失败!",e);
                result.toError("查询箱子所属板号失败，系统异常！");
            }
        }else{
            result.toFail("请扫描正确的包裹号或箱号!");
        }

        return result;
    }

    @POST
    @Path("/boardLablePrint/createBoard")
    public InvokeResult<List<BoardDto>> createBoard(AddBoardRequest request){

        InvokeResult<List<BoardDto>> invokeResult = new InvokeResult<>();
        if(request == null || request.getDestination() == null || request.getDestinationId() == null || request.getBoardCount() == null){
            invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            invokeResult.setMessage(InvokeResult.PARAM_ERROR);
            this.log.warn("建板请求的参数有误");
            return invokeResult;
        }
        this.log.info("建板请求的板号数量:{},场站sitecode:{},目的地destinationId:{}",request.getBoardCount(), request.getSiteCode(),request.getDestinationId());
        return boardCombinationService.createBoard(request);
    }

    @GET
    @Path("/boardLablePrint/getBoard/{boardCode}")
    public InvokeResult<BoardDto> getBoard(@PathParam("boardCode") String boardCode){
        if(boardCode == null){
            this.log.warn("请求的板号为空");
            InvokeResult<BoardDto> invokeResult = new InvokeResult<>();
            invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            invokeResult.setMessage(InvokeResult.PARAM_ERROR);
            return invokeResult;
        }
        this.log.info("请求板信息的板号为：:{}", boardCode);
        return boardCombinationService.getBoard(boardCode);

    }

    /**
     * 查询板号下的组板明细
     * @param boardCode
     * @return
     */
    @GET
    @Path("/boardCombination/detail/{boardCode}")
    public JdResponse<BoardResponse> getBoxCodesByBoardCode(@PathParam("boardCode") String boardCode){
        JdResponse<BoardResponse> result = new JdResponse<BoardResponse>();
        result.toSucceed("查询箱子所属板号成功!");

        //参数校验
        if(BusinessUtil.isBoardCode(boardCode)){
            try {
                Response<List<String>>  tcResponse = boardCombinationService.getBoxesByBoardCode(boardCode);
                if(tcResponse == null){
                    result.toFail("组板组件返回结果为空！");
                }else if(JdResponse.CODE_SUCCESS.equals(tcResponse.getCode())){
                    //查询成功
                    if(tcResponse.getData() != null && !tcResponse.getData().isEmpty()){
                        result.setData(dealBoardDetails(tcResponse, boardCode));
                    }else{
                        result.toFail("未查询到板号明细");
                    }
                }else {
                    //查询失败
                    result.toFail(tcResponse.getMesseage());
                }
            } catch (Exception e) {
                log.error("查询箱子所属板号失败!",e);
                result.toError("查询箱子所属板号失败，系统异常！");
            }
        }else{
            result.toFail("请扫描正确的板号!");
        }

        return result;
    }

    /**
     * 处理组板明细数据
     * @param tcResponse
     * @param boardCode
     * @return
     */
    private BoardResponse dealBoardDetails(Response<List<String>>  tcResponse, String boardCode){
        BoardResponse boardResponse = new BoardResponse();
        boardResponse.setBoardCode(boardCode);
        boardResponse.setBoardDetails(tcResponse.getData());
        int boxNum = 0;
        int packageNum = 0;
        for (String temp : tcResponse.getData()){
            if(WaybillUtil.isPackageCode(temp)){
                packageNum++;
            }
            if(BusinessHelper.isBoxcode(temp)){
                boxNum++;
            }
        }
        boardResponse.setBoxNum(boxNum);
        boardResponse.setPackageNum(packageNum);
        return boardResponse;
    }

    /**
     * 组板操作参数校验
     * @param request
     * @return
     */
    private String combinationVertify(BoardCombinationRequest request){
        //参数为空校验
        if (request == null) {
            return "参数为空.";
        }

        if(StringHelper.isEmpty(request.getBoardCode())){
            return "板号为空.";
        }
        if(StringHelper.isEmpty(request.getBoxOrPackageCode())){
            return "参数箱号/包裹号为空.";
        }
        if(request.getSiteCode() == null || request.getSiteCode() == 0){
            return "参数操作站点为空.";
        }
        if(request.getUserCode() == null || request.getUserCode() == 0){
            return "参数操作人为空.";
        }

        //箱号/运单号是否合法
        if (!BusinessUtil.isBoxcode(request.getBoxOrPackageCode())
                && !WaybillUtil.isPackageCode(request.getBoxOrPackageCode())) {
            this.log.warn("箱号/包裹号正则校验不通过：{}", request.getBoxOrPackageCode());
           return "箱号/包裹号不合法.";
        }

        return null;
    }

    /**
     * 组板操作参数校验，增加了运单号的校验
     * @param request
     * @return
     */
    private String combinationVertifyNew(BoardCombinationRequest request){
        //参数为空校验
        if (request == null) {
            return "参数为空.";
        }

        if(StringHelper.isEmpty(request.getBoardCode())){
            return "板号为空.";
        }
        if(StringHelper.isEmpty(request.getBoxOrPackageCode())){
            return "参数箱号/包裹号为空.";
        }
        if(request.getSiteCode() == null || request.getSiteCode() == 0){
            return "参数操作站点为空.";
        }
        if(request.getUserCode() == null || request.getUserCode() == 0){
            return "参数操作人为空.";
        }

        //箱号/包裹号/运单号是否合法
        if (!BusinessUtil.isBoxcode(request.getBoxOrPackageCode())
                && !WaybillUtil.isPackageCode(request.getBoxOrPackageCode()) && ! WaybillUtil.isWaybillCode(request.getBoxOrPackageCode())) {
            this.log.warn("箱号/包裹号正则校验不通过：{}", request.getBoxOrPackageCode());
            return "箱号/运单号/包裹号不合法.";
        }

        return null;
    }

    /**
     * 组板操作参数校验(自动生成板号)
     */
    private String checkCombinationBoardParam(CombinationBoardRequest request){
        if (request == null) {
            return "参数不能为空";
        }
        if (request.getCurrentOperate() == null || request.getCurrentOperate().getSiteCode() == 0
                || StringUtils.isBlank(request.getCurrentOperate().getSiteName())) {
            return "操作站点编码和名称都不能为空";
        }
        if (request.getUser() == null || StringUtils.isBlank(request.getUser().getUserErp())
                || StringUtils.isBlank(request.getUser().getUserName())) {
            return "用户erp和名称都不能为空";
        }
        // 箱号/包裹号/运单号是否合法
        if (!BusinessUtil.isBoxcode(request.getBoxOrPackageCode())
                && !WaybillUtil.isPackageCode(request.getBoxOrPackageCode()) && ! WaybillUtil.isWaybillCode(request.getBoxOrPackageCode())) {
            this.log.warn("箱号/包裹号正则校验不通过：{}", request.getBoxOrPackageCode());
            return "箱号/运单号/包裹号不合法.";
        }
        return null;
    }

    /**
     * 取消组板参数校验
     */
    private String checkCancelCombinationBoardParam(CombinationBoardRequest request){
        if (request == null) {
            return "参数不能为空";
        }
        if (request.getCurrentOperate() == null || request.getCurrentOperate().getSiteCode() == 0
                || StringUtils.isBlank(request.getCurrentOperate().getSiteName())) {
            return "操作站点编码和名称都不能为空";
        }
        if (request.getUser() == null || StringUtils.isBlank(request.getUser().getUserErp())
                || StringUtils.isBlank(request.getUser().getUserName()) || request.getUser().getUserCode() == 0) {
            return "用户erp和名称、编码都不能为空";
        }
        // 箱号/包裹号/运单号是否合法
        if (!BusinessUtil.isBoxcode(request.getBoxOrPackageCode())
                && !WaybillUtil.isPackageCode(request.getBoxOrPackageCode()) && ! WaybillUtil.isWaybillCode(request.getBoxOrPackageCode())) {
            this.log.warn("箱号/包裹号正则校验不通过：{}", request.getBoxOrPackageCode());
            return "箱号/运单号/包裹号不合法.";
        }
        return null;
    }

    private BoardCombinationRequest convertToBoardCombinationRequest(CombinationBoardRequest param) {

        BoardCombinationRequest boardCombinationRequest = new BoardCombinationRequest();

        boardCombinationRequest.setBoxOrPackageCode(param.getBoxOrPackageCode());
        boardCombinationRequest.setBoardCode(param.getBoardCode());
        boardCombinationRequest.setReceiveSiteCode(param.getReceiveSiteCode());
        boardCombinationRequest.setReceiveSiteName(param.getReceiveSiteName());
        boardCombinationRequest.setIsForceCombination(param.isForceCombination());
        boardCombinationRequest.setSiteCode(param.getCurrentOperate().getSiteCode());
        boardCombinationRequest.setSiteName(param.getCurrentOperate().getSiteName());
        boardCombinationRequest.setUserCode(param.getUser().getUserCode());
        boardCombinationRequest.setUserName(param.getUser().getUserName());
        if (param.getFlowDisaccord() != null) {
            boardCombinationRequest.setFlowDisaccord(param.getFlowDisaccord());
        }

        return boardCombinationRequest;

    }

    public JdResponse<Void> combinationComplete(CombinationBoardRequest request) {
        JdResponse<Void> jdResponse = new JdResponse<>();
        try {
            Response<Boolean> closeBoardResponse = boardCombinationService.closeBoard(request.getBoardCode());
            // 关板失败
            if (!JdResponse.CODE_SUCCESS.equals(closeBoardResponse.getCode()) || !closeBoardResponse.getData()) {
                log.warn("组板完成调用TC关板失败,板号：{}，关板结果：{}", request.getBoardCode(), JsonHelper.toJson(closeBoardResponse));
                jdResponse.toFail(closeBoardResponse.getMesseage());
                return jdResponse;
            }
            jdResponse.toSucceed();
            return jdResponse;
        } catch (Exception e) {
            log.error("组板完成调用TC关板异常：板号={}" , request.getBoardCode(), e);
            jdResponse.toError("组板完成发生异常");
            return jdResponse;
        }
    }
}
