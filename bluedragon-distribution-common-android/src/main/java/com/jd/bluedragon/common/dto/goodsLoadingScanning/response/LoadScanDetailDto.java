package com.jd.bluedragon.common.dto.goodsLoadingScanning.response;

import java.io.Serializable;
import java.util.List;

public class LoadScanDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String batchCode;

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
}
