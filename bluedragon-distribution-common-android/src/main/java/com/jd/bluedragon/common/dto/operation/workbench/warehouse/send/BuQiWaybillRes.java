package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.io.Serializable;
import java.util.List;

public class BuQiWaybillRes implements Serializable {


    private static final long serialVersionUID = -5809332610524693231L;


    private List<BuQiWaybillDto> buQiWaybillDtoList;

    /**
     * 不齐运单总数
     */
    private Integer buQiWaybillTotalSum;

    public List<BuQiWaybillDto> getBuQiWaybillDtoList() {
        return buQiWaybillDtoList;
    }

    public void setBuQiWaybillDtoList(List<BuQiWaybillDto> buQiWaybillDtoList) {
        this.buQiWaybillDtoList = buQiWaybillDtoList;
    }

    public Integer getBuQiWaybillTotalSum() {
        return buQiWaybillTotalSum;
    }

    public void setBuQiWaybillTotalSum(Integer buQiWaybillTotalSum) {
        this.buQiWaybillTotalSum = buQiWaybillTotalSum;
    }
}
