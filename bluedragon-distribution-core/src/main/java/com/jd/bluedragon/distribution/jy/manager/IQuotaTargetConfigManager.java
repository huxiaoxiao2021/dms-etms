package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.common.annotation.CacheMethod;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

public interface IQuotaTargetConfigManager {
    
    Double getLostOneTableSendScanTarget(String quotaCode, String year, String businessType, String class2Type);
}
