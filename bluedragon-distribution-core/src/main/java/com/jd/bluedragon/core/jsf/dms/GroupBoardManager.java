package com.jd.bluedragon.core.jsf.dms;

import com.jd.transboard.api.dto.OperatorInfo;

import java.util.List;

/**
 * @author lijie
 * @date 2019/11/1 10:04
 */
public interface GroupBoardManager {

    void resuseBoards(List<String> boardList, OperatorInfo operatorInfo);
}
