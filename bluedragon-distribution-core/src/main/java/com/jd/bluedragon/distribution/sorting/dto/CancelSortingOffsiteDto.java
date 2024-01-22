package com.jd.bluedragon.distribution.sorting.dto;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;

/**
 * @author pengchong28
 * @description 当前操作场地和实际集包场地不同时取消集包数据对象
 * @date 2024/1/22
 */
public class CancelSortingOffsiteDto extends Sorting {
    /**
     * 当前正在操作的场地code
     */
    private Integer currentSiteCode;
    /**
     * 是否跳过取消集包之前的检查条件，默认为：false    false-不跳过  true-跳过
     */
    private Boolean skipSendCheck = false;

    public Integer getCurrentSiteCode() {
        return currentSiteCode;
    }

    public void setCurrentSiteCode(Integer currentSiteCode) {
        this.currentSiteCode = currentSiteCode;
    }

    public Boolean getSkipSendCheck() {
        return skipSendCheck;
    }

    public void setSkipSendCheck(Boolean skipSendCheck) {
        this.skipSendCheck = skipSendCheck;
    }
}
