package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.MoveBoxRequest;
import com.jd.transboard.api.dto.OperatorInfo;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lijie
 * @date 2019/11/1 10:08
 */
@Service("groupBoardManager")
public class GroupBoardManagerImpl implements GroupBoardManager {

    private static final Logger log = LoggerFactory.getLogger(GroupBoardManagerImpl.class);

    @Autowired
    @Qualifier("groupBoardService")
    private GroupBoardService groupBoardService;

    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.resuseBoards",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public void resuseBoards(List<String> boardList, OperatorInfo operatorInfo) {

        Response<String> response = groupBoardService.resuseBoards(boardList,operatorInfo);
        if(response != null && response.getCode() == 200 ){
            log.info("取消板关闭状态成功，板号分别为：{},操作人的ERP为：{}" , boardList.toString() ,operatorInfo.getOperatorErp());
            return;
        }
        log.warn("取消板关闭状态失败，板号分别为：{},操作人的ERP为：{}" ,boardList.toString() ,operatorInfo.getOperatorErp());
    }

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.addBoxToBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<Integer> addBoxToBoard(AddBoardBox addBoardBox) {

        return groupBoardService.addBoxToBoard(addBoardBox);
    }

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.moveBoxToNewBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<String> moveBoxToNewBoard(MoveBoxRequest moveBoxRequest) {

        return groupBoardService.moveBoxToNewBoard(moveBoxRequest);
    }

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.getBoxesByBoardCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<List<String>> getBoxesByBoardCode(String boardCode) {
        return groupBoardService.getBoxesByBoardCode(boardCode);
    }

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
