package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.transboard.api.dto.OperatorInfo;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

    @Autowired
    @Qualifier("groupBoardService")
    private GroupBoardService groupBoardService;

    @JProfiler(jKey = "dmsWeb.jsf.dmsver.groupBoardService.resuseBoards",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public String resuseBoards(List<String> var1, OperatorInfo var2) {

        Response<String> response = groupBoardService.resuseBoards(var1,var2);
        if(response == null || response.getData().isEmpty()){
            return null;
        }
        return response.getData();
    }
}
