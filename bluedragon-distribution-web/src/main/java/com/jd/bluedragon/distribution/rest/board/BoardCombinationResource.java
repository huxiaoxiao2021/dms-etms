package com.jd.bluedragon.distribution.rest.board;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

    @GET
    @Path("/boardCombination/barCodeValidation")
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.validation", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.combination", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.combinationNew", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
     * 取消组板
     * @param request
     * @return
     */
    @POST
    @Path("/boardCombination/combination/cancel")
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.cancel", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
     * 查询箱子所属板号
     * @param boxCode
     * @return
     */
    @GET
    @Path("/boardCombination/belong/{siteCode}/{boxCode}")
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.getBoardByBoxCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.createBoard", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.getBoard", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @JProfiler(jKey = "DMS.WEB.BoardCombinationResource.getBoxCodesByBoardCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
}
