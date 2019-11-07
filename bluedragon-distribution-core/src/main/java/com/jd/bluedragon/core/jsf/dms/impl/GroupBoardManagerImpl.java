package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.board.service.BoardCombinationServiceImpl;
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

    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.createBoards",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<Board>> createBoards(AddBoardRequest addBoardRequest){

        return groupBoardService.createBoards(addBoardRequest);
    }

    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.getBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Board> getBoard(String boardCode) {

        return groupBoardService.getBoardByCode(boardCode);
    }

}
