package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @author liwenji
 * @description 异常包裹查询结果
 * @date 2023-08-18 14:47
 */
public class AviationSendAbnormalPackResp implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private List<String> barCodeList;

    public List<String> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<String> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
