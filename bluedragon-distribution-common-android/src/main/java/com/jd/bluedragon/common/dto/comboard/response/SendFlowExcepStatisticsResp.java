package com.jd.bluedragon.common.dto.comboard.response;

import com.jd.bluedragon.common.dto.comboard.request.ExcepScanDto;

import java.io.Serializable;
import java.util.List;

public class SendFlowExcepStatisticsResp implements Serializable {
    private static final long serialVersionUID = 3640077516446616345L;
    private List<ExcepScanDto> excepScanDtoList;
    private List<SendFlowDto> sendFlowDtoList;

    public List<ExcepScanDto> getExcepScanDtoList() {
        return excepScanDtoList;
    }

    public void setExcepScanDtoList(List<ExcepScanDto> excepScanDtoList) {
        this.excepScanDtoList = excepScanDtoList;
    }

    public List<SendFlowDto> getSendFlowDtoList() {
        return sendFlowDtoList;
    }

    public void setSendFlowDtoList(List<SendFlowDto> sendFlowDtoList) {
        this.sendFlowDtoList = sendFlowDtoList;
    }
}
