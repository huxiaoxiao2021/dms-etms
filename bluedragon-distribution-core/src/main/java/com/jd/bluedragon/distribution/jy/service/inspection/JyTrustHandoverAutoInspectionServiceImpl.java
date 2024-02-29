package com.jd.bluedragon.distribution.jy.service.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.PackageArriveAutoInspectionDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * @Author zhengchengfa
 * @Date 2024/2/29 21:39
 * @Description
 */
public class JyTrustHandoverAutoInspectionServiceImpl implements JyTrustHandoverAutoInspectionService {


    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyTrustHandoverAutoInspectionServiceImpl.packageArriveAndAutoInspection",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean packageArriveAndAutoInspection(PackageArriveAutoInspectionDto packageArriveAutoInspectionDto) {
        return false;
    }
}
