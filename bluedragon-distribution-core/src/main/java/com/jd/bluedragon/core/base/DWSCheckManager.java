package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckRequest;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckAroundRecord;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckPackageRequest;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckResponse;

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

    /**
     * 批量查询设备状态
     *
     * @param list
     * @return
     */
    List<DwsCheckAroundRecord> batchSelectMachineStatus(List<DwsCheckPackageRequest> list);
}
