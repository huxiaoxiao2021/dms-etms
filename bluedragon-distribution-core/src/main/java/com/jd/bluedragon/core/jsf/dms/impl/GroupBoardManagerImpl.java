package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.send.ws.client.dmc.Result;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;

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

    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.closeBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Response<Boolean> closeBoard(String boardCode) {
        return groupBoardService.closeBoard(boardCode);
    }

    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.getBoardByBoxCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Response<Board> getBoardByBoxCode(String boxCode, Integer siteCode) {
        return groupBoardService.getBoardByBoxCode(boxCode,siteCode);
    }

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.getPackageCodeUnderComBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<PackageDto> getPackageCodeUnderComBoard(String boardCode, String waybillCode) {
        ComBoardRequest comBoardRequest =new ComBoardRequest();
        comBoardRequest.setBoardCode(boardCode);
        comBoardRequest.setWaybillCode(waybillCode);
        Response<List<PackageDto>> packageDtoResponse =groupBoardService.getPackageCodeUnderComBoard(comBoardRequest);
        if (ObjectHelper.isNotNull(packageDtoResponse) && JdCResponse.CODE_SUCCESS.equals(packageDtoResponse.getCode())){
            return packageDtoResponse.getData();
        }
        return null;
    }

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.getBoardStatisticsByBoardCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response<BoardBoxStatisticsResDto> getBoardStatisticsByBoardCode(String boardCode) {
        return groupBoardService.getBoardStatisticsByBoardCode(boardCode);

    }

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.tc.groupBoardService.batchRemoveBardBoxByBoxCodes",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Response batchRemoveBardBoxByBoxCodes(RemoveBoardBoxDto removeBoardBoxDto) {
        return groupBoardService.batchRemoveBardBoxByBoxCodes(removeBoardBoxDto);

    }

}
