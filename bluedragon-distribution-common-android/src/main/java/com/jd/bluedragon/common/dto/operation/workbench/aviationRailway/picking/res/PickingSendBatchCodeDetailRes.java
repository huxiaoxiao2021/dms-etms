package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2024/4/2 14:23
 * @Description
 */
public class PickingSendBatchCodeDetailRes implements Serializable {
    private static final long serialVersionUID = 4997976038143915037L;
    //待封车批次list
    private List<PickingSendBatchCodeDetailDto> pickingSendBatchCodeDetailDtoList;

    public List<PickingSendBatchCodeDetailDto> getPickingSendBatchCodeDetailDtoList() {
        return pickingSendBatchCodeDetailDtoList;
    }

    public void setPickingSendBatchCodeDetailDtoList(List<PickingSendBatchCodeDetailDto> pickingSendBatchCodeDetailDtoList) {
        this.pickingSendBatchCodeDetailDtoList = pickingSendBatchCodeDetailDtoList;
    }
}
