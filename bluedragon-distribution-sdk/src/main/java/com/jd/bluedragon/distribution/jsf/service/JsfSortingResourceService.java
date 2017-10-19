package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.jsf.domain.MixedPackageConfigResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;

import java.util.List;

public interface JsfSortingResourceService {
	public SortingJsfResponse check(SortingCheck sortingCheck);
    public SortingJsfResponse isCancel(String packageCode);
    List<MixedPackageConfigResponse> getMixedConfigsBySitesAndTypes(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType);
    public Integer getWaybillCancelByWaybillCode(String waybillCode);
}
