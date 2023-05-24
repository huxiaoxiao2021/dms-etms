package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-22 15:41
 */
public class MixScanTaskToSealDestAgg {
    
    /**
     * 封车的流向数量
     */
    private Integer sealedTotal;

    /**
     * 流向总数
     */
    private Integer destTotal;

    /**
     * 车辆发货流向明细
     */
    private List<CarToSealDetail> carToSealList;

    public Integer getSealedTotal() {
        return sealedTotal;
    }

    public void setSealedTotal(Integer sealedTotal) {
        this.sealedTotal = sealedTotal;
    }

    public Integer getDestTotal() {
        return destTotal;
    }

    public void setDestTotal(Integer destTotal) {
        this.destTotal = destTotal;
    }

    public List<CarToSealDetail> getCarToSealList() {
        return carToSealList;
    }

    public void setCarToSealList(List<CarToSealDetail> carToSealList) {
        this.carToSealList = carToSealList;
    }
}
