package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckRecord;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckResponse;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetail;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckRequest;

import java.util.Date;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/8/24 3:51 PM
 */
public interface DWSCheckManager {

    /**
     * 校验设备称重是否准确
     *
     * @param machineCode
     * @param weightTime
     * @return
     */
    Boolean checkDWSMachineWeightIsAccurate(String machineCode, Date weightTime);

    /**
     * 获取设备校验细节
     */
    DwsCheckResponse getLastDwsCheckByTime(DWSCheckRequest checkRequest);
}
