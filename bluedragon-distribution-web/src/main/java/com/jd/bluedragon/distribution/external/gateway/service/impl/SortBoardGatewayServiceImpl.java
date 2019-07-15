package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.board.request.CombinationBoardRequest;
import com.jd.bluedragon.common.dto.board.response.BoardCheckDto;
import com.jd.bluedragon.common.dto.board.response.BoardDetailDto;
import com.jd.bluedragon.common.dto.board.response.BoardInfoDto;
import com.jd.bluedragon.distribution.api.request.BoardCombinationRequest;
import com.jd.bluedragon.distribution.api.response.BoardResponse;
import com.jd.bluedragon.distribution.rest.board.BoardCombinationResource;
import com.jd.bluedragon.external.gateway.service.SortBoardGatewayService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.domain.JdResponseStatusInfo;
import com.jd.transboard.api.dto.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SortBoardGatewayServiceImpl
 * 组板相关处理
 *
 * @author jiaowenqiang
 * @date 2019/7/9
 */
@Service("sortBoardGatewayServiceImpl")
public class SortBoardGatewayServiceImpl implements SortBoardGatewayService {


    @Autowired
    BoardCombinationResource boardCombinationResource;

    /**
     * 组板校验
     */
    @Override
    public JdCResponse<BoardCheckDto> validateBoardCode(String boardCode) {

        JdCResponse<BoardCheckDto> jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.validation(boardCode);

        //data转化
        BoardCheckDto boardCheckDto = new BoardCheckDto();
        boardCheckDto.setReceiveSiteCode(response.getData().getReceiveSiteCode());
        boardCheckDto.setReceiveSiteName(response.getData().getReceiveSiteName());
        jdCResponse.setData(boardCheckDto);

        if (response.getCode() != 200) {
            jdCResponse.setMessage(convertMessage(response.getData().getStatusInfo()));
        } else {
            jdCResponse.setMessage(response.getMessage());
        }

        //code转化
        if (response.getCode() > 30000 && response.getCode() < 40000) {
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
        } else {
            jdCResponse.setCode(response.getCode());
        }

        return jdCResponse;
    }

    /**
     * 组板
     */
    @Override
    public JdCResponse combinationBoard(CombinationBoardRequest request) {

        JdCResponse jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.combinationNew(convertToBoardCombinationRequest(request));

        if (response.getCode() != 200) {
            jdCResponse.setMessage(convertMessage(response.getData().getStatusInfo()));
        } else {
            jdCResponse.setMessage(response.getMessage());
        }

        //code转化
        if (response.getCode() > 30000 && response.getCode() < 40000) {
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
        } else {
            jdCResponse.setCode(response.getCode());
        }
        jdCResponse.setCode(response.getCode());


        return jdCResponse;
    }

    /**
     * 取消组板
     */
    @Override
    public JdCResponse combinationBoardCancel(CombinationBoardRequest request) {

        JdCResponse jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.cancel(convertToBoardCombinationRequest(request));

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }

    /**
     * 根据板号查询板号下的组板明细
     */
    @Override
    public JdCResponse<BoardDetailDto> queryBoardDetail(String boardCode) {

        JdCResponse<BoardDetailDto> jdCResponse = new JdCResponse<>();
        JdResponse<BoardResponse> response = boardCombinationResource.getBoxCodesByBoardCode(boardCode);

        if (response.getCode() == 200) {
            BoardDetailDto boardDetailDto = new BoardDetailDto();
            boardDetailDto.setBoardDetails(response.getData().getBoardDetails());
            boardDetailDto.setBoxNum(response.getData().getBoxNum());
            boardDetailDto.setPackageNum(response.getData().getPackageNum());
            jdCResponse.setData(boardDetailDto);
        }

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }

    /**
     * 查询箱子或包裹所属板号
     */
    @Override
    public JdCResponse<BoardInfoDto> queryBoardInfo(Integer siteCode, String packageOrBoxCode) {

        JdCResponse<BoardInfoDto> jdCResponse = new JdCResponse<>();
        JdResponse<Board> response = boardCombinationResource.getBoardByBoxCode(siteCode, packageOrBoxCode);

        if (response.getCode() == 200) {
            BoardInfoDto boardInfoDto = new BoardInfoDto();
            boardInfoDto.setCode(response.getData().getCode());
            boardInfoDto.setDestination(response.getData().getDestination());
            jdCResponse.setData(boardInfoDto);
        }

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }


    //实体转化
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

        return boardCombinationRequest;

    }

    //message转化
    private String convertMessage(List<JdResponseStatusInfo> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (JdResponseStatusInfo s : list) {
            stringBuilder.append("   ");
            stringBuilder.append(s.getStatusMessage());
        }

        return stringBuilder.toString();
    }


}
