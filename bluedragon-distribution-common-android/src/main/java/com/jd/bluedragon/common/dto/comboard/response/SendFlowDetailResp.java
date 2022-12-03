package com.jd.bluedragon.common.dto.comboard.response;


import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

public class SendFlowDetailResp implements Serializable {
    private static final long serialVersionUID = -3031759375359452317L;
    /**
     * 岗位码下扫描实操的人
     */
    private List<User> scanUserList;
    private SendFlowDto sendFlowDto;
    

    public SendFlowDto getSendFlowDto() {
        return sendFlowDto;
    }

    public void setSendFlowDto(SendFlowDto sendFlowDto) {
        this.sendFlowDto = sendFlowDto;
    }

    public List<User> getScanUserList() {
        return scanUserList;
    }

    public void setScanUserList(List<User> scanUserList) {
        this.scanUserList = scanUserList;
    }
}
