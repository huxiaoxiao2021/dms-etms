package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;


import java.util.List;

/**
 * @author liwenji
 * @description
 * @date 2023-08-21 17:26
 */
public class AviationBarCodeDetailResp {
    
    private List<SendScanBarCodeDto> sendScanBarCodeDtoList;

    public List<SendScanBarCodeDto> getSendScanBarCodeDtoList() {
        return sendScanBarCodeDtoList;
    }

    public void setSendScanBarCodeDtoList(List<SendScanBarCodeDto> sendScanBarCodeDtoList) {
        this.sendScanBarCodeDtoList = sendScanBarCodeDtoList;
    }
}
