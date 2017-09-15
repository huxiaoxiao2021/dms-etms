package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.jsf.domain.MixedPackageConfig;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;

import java.util.List;

public interface JsfSortingResourceService {
	public SortingJsfResponse check(SortingCheck sortingCheck);
    public SortingJsfResponse isCancel(String packageCode);
    List<MixedPackageConfig> getMixedConfigsBySitesAndTypes(Integer createSiteCode, Integer receiveSiteCode, Integer transportType, Integer ruleType);

}
