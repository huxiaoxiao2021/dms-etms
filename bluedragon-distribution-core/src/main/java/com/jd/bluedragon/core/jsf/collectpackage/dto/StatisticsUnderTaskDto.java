package com.jd.bluedragon.core.jsf.collectpackage.dto;

import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageFlowDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;

import java.util.List;

public class StatisticsUnderTaskDto {
    private String bizId;

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


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

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
