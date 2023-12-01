package com.jd.bluedragon.common.dto.collectpackage.response;

import java.io.Serializable;
import java.util.List;

public class StatisticsUnderFlowQueryResp implements Serializable {
    private static final long serialVersionUID = -2159954394167104416L;

    /**
     * 包裹列表
     */
    List<CollectPackageDto> collectPackageDtoList;

    public List<CollectPackageDto> getCollectPackageDtoList() {
        return collectPackageDtoList;
    }

    public void setCollectPackageDtoList(List<CollectPackageDto> collectPackageDtoList) {
        this.collectPackageDtoList = collectPackageDtoList;
    }
}
