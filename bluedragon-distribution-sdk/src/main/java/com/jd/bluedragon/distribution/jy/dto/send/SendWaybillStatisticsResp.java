package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

public class SendWaybillStatisticsResp implements Serializable {

    private static final long serialVersionUID = 2651092353037678971L;
    /**
     * 货物分类统计数据
     */
    private List<SendGoodsCategoryDto> goodsCategoryDtoList;
    /**
     * 异常扫描类型统计数据
     */
    private List<SendExcepScanDto> excepScanDtoList;

    /**
     * 运单列表
     */
    private List<SendWaybillDto> waybillDtoList;

    public List<SendGoodsCategoryDto> getGoodsCategoryDtoList() {
        return goodsCategoryDtoList;
    }

    public void setGoodsCategoryDtoList(List<SendGoodsCategoryDto> goodsCategoryDtoList) {
        this.goodsCategoryDtoList = goodsCategoryDtoList;
    }

    public List<SendExcepScanDto> getExcepScanDtoList() {
        return excepScanDtoList;
    }

    public void setExcepScanDtoList(List<SendExcepScanDto> excepScanDtoList) {
        this.excepScanDtoList = excepScanDtoList;
    }

    public List<SendWaybillDto> getWaybillDtoList() {
        return waybillDtoList;
    }

    public void setWaybillDtoList(List<SendWaybillDto> waybillDtoList) {
        this.waybillDtoList = waybillDtoList;
    }
}




