package com.jd.bluedragon.core.jsf.dms.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.board.service.BoardCombinationServiceImpl;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijie
 * @date 2019/10/31 21:37
 */
@Service("groupBoardManager")
public class GroupBoardManagerImpl implements GroupBoardManager {

    private static final Log logger = LogFactory.getLog(BoardCombinationServiceImpl.class);

    @Autowired
    @Qualifier("groupBoardService")
    private GroupBoardService groupBoardService;

    //【提醒】jKey填写的不一定对！！！
    @JProfiler(jKey = "dmsWeb.jsf.dmsver.groupBoardService.createBoards",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BoardDto> createBoards(AddBoardRequest var1){

        List<BoardDto> boardDtos = new ArrayList<>();
        BoardDto board;
        Response<List<Board>> tcResponse = groupBoardService.createBoards(var1);
        if(tcResponse != null && tcResponse.getCode() == 200 && tcResponse.getData() != null){
            for(int j = 0; j < tcResponse.getData().size();j++){
                board = boardToBoardDto(tcResponse.getData().get(j));
                boardDtos.add(board);
            }
            return boardDtos;
        }else{
            logger.warn("groupBoardService.createBoards未新建板号，tcResponse:" + JsonHelper.toJson(tcResponse));
            return Lists.newArrayList();
        }

    }

    @JProfiler(jKey = "dmsWeb.jsf.dmsver.groupBoardService.getBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public BoardDto getBoard(String boardCode) {

        Response<Board> tcResponse = groupBoardService.getBoardByCode(boardCode);
        if(tcResponse != null && tcResponse.getCode() == 200 && tcResponse.getData() != null){
            return boardToBoardDto(tcResponse.getData());
        }else{
            logger.warn("groupBoardService.getBoard未获取到板信息，tcResponse:" + JsonHelper.toJson(tcResponse));
            return new BoardDto();
        }
    }

    public BoardDto boardToBoardDto(Board board){
        BoardDto boardDto = new BoardDto();
        boardDto.setDate(DateHelper.formatDate(board.getCreateTime(),"yyyy-MM-dd"));
        boardDto.setTime(DateHelper.formatDate(board.getCreateTime(),"HH:mm:ss"));
        boardDto.setCode(board.getCode());
        boardDto.setDestination(board.getDestination());
        boardDto.setDestinationId(board.getDestinationId());
        boardDto.setStatus(board.getStatus());
        return boardDto;
    }

}
