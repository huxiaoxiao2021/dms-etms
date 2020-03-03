package com.jd.bluedragon.core.base;

import com.jd.dms.ver.domain.WaybillInterceptTips;

import java.util.List;

public interface VerCommonJsfApiManager {

    List<WaybillInterceptTips> getSortMachineInterceptTips(String barCode);
}
