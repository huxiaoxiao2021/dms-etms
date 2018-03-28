package com.jd.bluedragon.distribution.rest.board;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.board.domain.Board;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryVerification;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import javax.annotation.Resource;
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
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    BoardCombinationService boardCombinationService;

    @Resource(name = "cityDeliveryVerification")
    private DeliveryVerification cityDeliveryVerification;

    @GET
    @Path("/boardCombination/barCodeValidation")
    public JdResponse<BoardResponse> validation(@QueryParam("boardCode") String boardCode) {
        JdResponse<BoardResponse> response =new JdResponse<BoardResponse>();
        response.setData(new BoardResponse());
        BoardResponse boardResponse = response.getData();

        Assert.notNull(boardCode, "boardCode must not be null");

        this.logger.info("boardCode: " + boardCode);

        Board board = null;
        try {
            board = this.boardCombinationService.getBoardByBoardCode(boardCode);
            if (board == null) {
                boardResponse.setBoardCode(boardCode);
                boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_NOT_FOUND,BoardResponse.MESSAGE_BOARD_NOT_FOUND);
                response.toFail();
                return response;
            }
            //板标已完结
            if (board.getBoardStatus() == 1) {
                boardResponse.setBoardCode(boardCode);
                boardResponse.addStatusInfo(BoardResponse.CODE_BOARD_FINISHED, BoardResponse.MESSAGE_BOARD_FINISHED);
                response.toFail();
                return response;
            }

            boardResponse.setBoardCode(boardCode);
            boardResponse.setReceiveSiteCode(null);
            boardResponse.setReceiveSiteName(null);

            return response;
        } catch (Exception e) {
            logger.error("板号校验失败!",e);
        }

        response.toError("板号校验失败!");
        return response;
    }

    /**
     * 组板功能
     */
    @POST
    @Path("/boardCombination/combination")
    public JdResponse<BoardResponse> combination(BoardCombinationRequest request) {
        JdResponse<BoardResponse> response =new JdResponse<BoardResponse>();
        response.setData(new BoardResponse());
        BoardResponse boardResponse = response.getData();

        try {
            //基本参数校验
            if (request == null) {
                this.logger.error("NewSealVehicleResource seal --> 传入参数非法");
            }

            //查询发货记录判断是否已经发货
            SendM sendM = new SendM();
            sendM.setBoxCode(request.getBoxOrPackageCode());
            sendM.setCreateSiteCode(request.getSiteCode());
            sendM.setReceiveSiteCode(request.getReceiveSiteCode());

            List<SendM> sendMList = boardCombinationService.selectBySendSiteCode(sendM);

            if (null != sendMList && sendMList.size() > 0) {
                logger.error("箱号/包裹" + sendMList.get(0).getBoxCode() + "已经在批次" + sendMList.get(0).getSendCode() + "中发货");
                boardResponse.addStatusInfo(BoardResponse.CODE_BOX_PACKAGE_SENDED, "箱号/包裹" + sendMList.get(0).getBoxCode() + "已经在批次"
                        + sendMList.get(0).getSendCode() + "中发货");
                response.toFail();
                return response;
            }

            //一单多件不齐校验
            if(!request.isForceCombination()){
                DeliveryVerification.VerificationResult verificationResult=cityDeliveryVerification.verification(request.getBoxOrPackageCode(),null,false);
                if(!verificationResult.getCode()){//按照箱发货，校验派车单是否齐全，判断是否强制发货
                    boardResponse.addStatusInfo(BoardResponse.CODE_PACAGES_NOT_ENOUGH,verificationResult.getMessage());
                    response.toConfirm();
                    return response;
                }
            }

            //将组板数据推送给TC
             String errStr = boardCombinationService.sendBoardBindings();

            //数量限制校验

            if(errStr.equals("已经绑定")){
                boardResponse.addStatusInfo(BoardResponse.CODE_BOX_PACKAGE_BINDINGED, "箱号/包裹" + request.getBoxOrPackageCode() + "已经绑定了");
                response.toFail();
                return response;
            }else if(StringHelper.isEmpty(errStr)){
                boardResponse.setBoardCode(request.getBoardCode());
                boardResponse.setBoxCode(request.getBoxOrPackageCode());
                return response;
            }

        } catch (Exception e) {
            logger.error("组板失败!",e);
        }

        response.toError("组板失败!");
        return response;
    }

}
