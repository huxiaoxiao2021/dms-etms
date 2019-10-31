package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.dto.BoardDto;
import com.jd.bluedragon.distribution.board.service.BoardCombinationServiceImpl;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.domain.JdResponse;
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
import java.util.Date;
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

    @JProfiler(jKey = "dmsWeb.jsf.dmsver.groupBoardService.createBoards",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BoardDto> createBoards(AddBoardRequest var1){

        List<BoardDto> boardDtos = new ArrayList<>();
        BoardDto board = new BoardDto();
        Response<List<Board>> tcResponse = groupBoardService.createBoards(var1);
        if(tcResponse == null || tcResponse.getData() == null || tcResponse.getData().size() <= 0){
            logger.error("创建板号失败");
            return null;
        }
        for(int j = 0; j < tcResponse.getData().size();j++){
            Date tcDate = tcResponse.getData().get(j).getCreateTime();
            board.setDate(DateHelper.formatDate(tcDate,"yyyy-MM-dd"));
            board.setTime(DateHelper.formatDate(tcDate,"HH:mm:ss"));
            board.setCode(tcResponse.getData().get(j).getCode());
            board.setDestination(tcResponse.getData().get(j).getDestination());
            board.setDestinationId(tcResponse.getData().get(j).getDestinationId());
            board.setStatus(tcResponse.getData().get(j).getStatus());
            boardDtos.add(board);
        }
        return boardDtos;
    }

}
