package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.transboard.api.dto.OperatorInfo;
import com.jd.transboard.api.dto.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private static final Log logger = LogFactory.getLog(GroupBoardManagerImpl.class);

    @Autowired
    @Qualifier("groupBoardService")
    private GroupBoardService groupBoardService;

    @JProfiler(jKey = "dmsWeb.jsf.dmsver.groupBoardService.resuseBoards",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public void resuseBoards(List<String> boardList, OperatorInfo operatorInfo) {

        Response<String> response = groupBoardService.resuseBoards(boardList,operatorInfo);
        if(response != null && response.getCode() == 200 ){
            logger.info("取消板关闭状态成功，板号分别为：" + boardList.toString() + ",操作人的ERP为：" + operatorInfo.getOperatorErp());
        }
        logger.error("取消板关闭状态失败，板号分别为：" + boardList.toString() + ",操作人的ERP为：" + operatorInfo.getOperatorErp());
    }
}
