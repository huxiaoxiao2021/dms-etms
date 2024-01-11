package com.jd.bluedragon.common.dto.collectpackage.response;


import com.jd.bluedragon.common.dto.comboard.request.ExcepScanDto;

import java.io.Serializable;
import java.util.List;

public class StatisticsUnderTaskQueryResp implements Serializable {
    private static final long serialVersionUID = -4443098556645988714L;

    /**
     * 箱号
     */
    private String boxCode;
    /**
     * 循环集包袋号
     */
    private String materialCode;

    /**
     * 扫描类型统计
     */
    private List<ExcepScanDto> excepScanDtoList;
    /**
     * 流向信息
     */
    List<CollectPackageFlowDto> collectPackageFlowDtoList;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public List<ExcepScanDto> getExcepScanDtoList() {
        return excepScanDtoList;
    }

    public void setExcepScanDtoList(List<ExcepScanDto> excepScanDtoList) {
        this.excepScanDtoList = excepScanDtoList;
    }

    public List<CollectPackageFlowDto> getCollectPackageFlowDtoList() {
        return collectPackageFlowDtoList;
    }

    public void setCollectPackageFlowDtoList(List<CollectPackageFlowDto> collectPackageFlowDtoList) {
        this.collectPackageFlowDtoList = collectPackageFlowDtoList;
    }
}
