package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.constant.WeightValidateSwitchEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;

public interface DeviceConfigInfoJsfServiceManager {

    BaseDmsAutoJsfResponse maintainWeightSwitch(WeightValidateSwitchEnum weightValidateSwitchEnum);


    BaseDmsAutoJsfResponse maintainSiteWeightSwitch(Integer[] siteCodesArray, WeightValidateSwitchEnum weightValidateSwitchEnum);

}
