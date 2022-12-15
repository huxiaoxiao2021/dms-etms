package com.jd.bluedragon.common.dto.comboard.response;

import com.jd.bluedragon.common.dto.base.request.User;
import java.io.Serializable;
import java.util.List;

public class SendFlowDataResp implements Serializable {
    private static final long serialVersionUID = 1154813635165160700L;
    /**
     * 岗位码下扫描实操的人
     */
    private List<User>  scanUserList;
    private List<SendFlowDto> sendFlowDtoList;

    public List<SendFlowDto> getSendFlowDtoList() {
        return sendFlowDtoList;
    }

    public void setSendFlowDtoList(List<SendFlowDto> sendFlowDtoList) {
        this.sendFlowDtoList = sendFlowDtoList;
    }

    public List<User> getScanUserList() {
        return scanUserList;
    }

    public void setScanUserList(List<User> scanUserList) {
        this.scanUserList = scanUserList;
    }
}
