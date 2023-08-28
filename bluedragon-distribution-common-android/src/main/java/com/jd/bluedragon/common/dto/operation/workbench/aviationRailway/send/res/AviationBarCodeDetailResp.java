package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;


import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description
 * @date 2023-08-21 17:26
 */
public class AviationBarCodeDetailResp implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;
    
    private List<SendScanBarCodeDto> sendScanBarCodeDtoList;

    public List<SendScanBarCodeDto> getSendScanBarCodeDtoList() {
        return sendScanBarCodeDtoList;
    }

    public void setSendScanBarCodeDtoList(List<SendScanBarCodeDto> sendScanBarCodeDtoList) {
        this.sendScanBarCodeDtoList = sendScanBarCodeDtoList;
    }
}
