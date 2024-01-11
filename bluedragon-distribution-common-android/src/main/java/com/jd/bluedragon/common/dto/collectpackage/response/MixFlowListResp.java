package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;
import java.util.List;

public class MixFlowListResp implements Serializable {

    /**
     * 目的地流向集合
     */
    private List<CollectPackageFlowDto> collectPackageFlowDtoList;

    public List<CollectPackageFlowDto> getCollectPackageFlowDtoList() {
        return collectPackageFlowDtoList;
    }

    public void setCollectPackageFlowDtoList(List<CollectPackageFlowDto> collectPackageFlowDtoList) {
        this.collectPackageFlowDtoList = collectPackageFlowDtoList;
    }
}
