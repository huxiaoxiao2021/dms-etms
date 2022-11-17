package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.List;

public class ScanStatisticsInnerDto implements Serializable {
    private static final long serialVersionUID = 44194299928389938L;
    /**
     * 货物分类统计数据
     */
    private List<GoodsCategoryDto> goodsCategoryDtoList;
    /**
     * 运单列表
     */
    private List<UnloadWaybillDto> unloadWaybillDtoList;
    /**
     * 异常扫描类型统计数据
     */
    private List<ExcepScanDto> excepScanDtoList;

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

    public List<ExcepScanDto> getExcepScanDtoList() {
        return excepScanDtoList;
    }

    public void setExcepScanDtoList(List<ExcepScanDto> excepScanDtoList) {
        this.excepScanDtoList = excepScanDtoList;
    }
}
