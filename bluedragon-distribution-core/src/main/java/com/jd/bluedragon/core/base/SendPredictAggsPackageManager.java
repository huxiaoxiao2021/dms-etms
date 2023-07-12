package com.jd.bluedragon.core.base;

import com.jd.dms.wb.report.api.jysendpredict.dto.SendPredictAggsQuery;
import com.jd.dms.wb.report.api.jysendpredict.dto.SendPredictToScanPackage;
import com.jd.dms.workbench.utils.sdk.base.Result;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/11 20:29
 * @Description:
 */
public interface SendPredictAggsPackageManager {

    List<SendPredictToScanPackage> getSendPredictToScanPackageList(SendPredictAggsQuery query);

}
