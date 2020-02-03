package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.modules.sorting.entity.WaybillInterceptTips;

import java.util.List;

public interface AutomaticSortingExceptionJsfManager {

    List<WaybillInterceptTips> getSortingExceptionTips(String barCode, Integer siteCode);
}
