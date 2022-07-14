package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.List;

public class ScanStatisticsInnerDto implements Serializable {
    private static final long serialVersionUID = 44194299928389938L;
    private List<GoodsCategoryDto> goodsCategoryDtoList;
    private List<UnloadWaybillDto> unloadWaybillDtoList;

    public List<GoodsCategoryDto> getGoodsCategoryDtoList() {
        return goodsCategoryDtoList;
    }

    public void setGoodsCategoryDtoList(List<GoodsCategoryDto> goodsCategoryDtoList) {
        this.goodsCategoryDtoList = goodsCategoryDtoList;
    }

    public List<UnloadWaybillDto> getUnloadWaybillDtoList() {
        return unloadWaybillDtoList;
    }

    public void setUnloadWaybillDtoList(List<UnloadWaybillDto> unloadWaybillDtoList) {
        this.unloadWaybillDtoList = unloadWaybillDtoList;
    }
}
