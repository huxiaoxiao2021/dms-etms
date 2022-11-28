package com.jd.bluedragon.common.dto.comboard.response;


import java.io.Serializable;

public class SendFlowDetailResp implements Serializable {
    private static final long serialVersionUID = -3031759375359452317L;
    /**
     * 岗位码下扫描实操的人数
     */
    private Integer scanUserCount;
    private SendFlowDto sendFlowDto;

    public Integer getScanUserCount() {
        return scanUserCount;
    }

    public void setScanUserCount(Integer scanUserCount) {
        this.scanUserCount = scanUserCount;
    }

    public SendFlowDto getSendFlowDto() {
        return sendFlowDto;
    }

    public void setSendFlowDto(SendFlowDto sendFlowDto) {
        this.sendFlowDto = sendFlowDto;
    }

}
