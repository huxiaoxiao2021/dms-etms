package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;
import java.util.List;

public class LoadScanDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String batchCode;

    private Double totalWeight;

    private Double totalVolume;

    private Integer totalPackageNum;

    private List<GoodsDetailDto> goodsDetailDtoList;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public List<GoodsDetailDto> getGoodsDetailDtoList() {
        return goodsDetailDtoList;
    }

    public void setGoodsDetailDtoList(List<GoodsDetailDto> goodsDetailDtoList) {
        this.goodsDetailDtoList = goodsDetailDtoList;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Integer getTotalPackageNum() {
        return totalPackageNum;
    }

    public void setTotalPackageNum(Integer totalPackageNum) {
        this.totalPackageNum = totalPackageNum;
    }
}
