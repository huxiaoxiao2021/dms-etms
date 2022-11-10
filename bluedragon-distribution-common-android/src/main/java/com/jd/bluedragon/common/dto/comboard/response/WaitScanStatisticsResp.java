package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class WaitScanStatisticsResp implements Serializable {
    private static final long serialVersionUID = -8347077835477023307L;
    private List<GoodsCategoryDto> goodsCategoryDtoList;
    private List<SendFlowWaitScanDto> sendFlowWaitScanDtoList;

    public List<GoodsCategoryDto> getGoodsCategoryDtoList() {
        return goodsCategoryDtoList;
    }

    public void setGoodsCategoryDtoList(List<GoodsCategoryDto> goodsCategoryDtoList) {
        this.goodsCategoryDtoList = goodsCategoryDtoList;
    }

    public List<SendFlowWaitScanDto> getSendFlowWaitScanDtoList() {
        return sendFlowWaitScanDtoList;
    }

    public void setSendFlowWaitScanDtoList(List<SendFlowWaitScanDto> sendFlowWaitScanDtoList) {
        this.sendFlowWaitScanDtoList = sendFlowWaitScanDtoList;
    }
}
