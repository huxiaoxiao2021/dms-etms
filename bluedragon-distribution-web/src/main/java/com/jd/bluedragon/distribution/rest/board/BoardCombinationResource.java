package com.jd.bluedragon.distribution.rest.board;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by xumei3 on 2018/3/27.
 */

@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class BoardCombinationResource {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    BoardCombinationService boardCombinationService;


    @GET
    @Path("/boardCombination/barCodeValidation")
    public JdResponse<BoardResponse> validation(@QueryParam("boardCode") String boardCode) {
        JdResponse<BoardResponse> result =new JdResponse<BoardResponse>();

        //参数为空校验
        if(StringHelper.isEmpty(boardCode)){
            result.toError("参数错误.板号为空!");
            return result;
        }

        try {
            BoardResponse boardResponse = boardCombinationService.getBoardByBoardCode(boardCode);
            if(boardResponse.getStatusInfo() != null && boardResponse.getStatusInfo().size() >0){
                result.toFail(boardResponse.getStatusMessages());
            }
            result.setData(boardResponse);
        } catch (Exception e) {
            logger.error("板号校验失败!",e);
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
            return result;
        }

        try {
            //操作组板，返回状态码
            Integer statusCode = boardCombinationService.sendBoardBindings(request,boardResponse);
            if(statusCode == JdResponse.CODE_FAIL){
                result.toFail(boardResponse.getStatusMessages());
            }else if(statusCode == JdResponse.CODE_CONFIRM){
                result.toConfirm(boardResponse.getStatusMessages());
            }else if(statusCode == JdResponse.CODE_SUCCESS){
                return result;
            }
        } catch (Exception e) {
            logger.error("组板失败!", e);
            result.toError("组板失败，系统异常！");
        }

        return result;
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
            return "参数箱号/包裹号为空.";
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

        return null;
    }
}
