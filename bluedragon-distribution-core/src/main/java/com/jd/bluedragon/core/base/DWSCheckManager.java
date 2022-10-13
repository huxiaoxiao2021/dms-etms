package com.jd.bluedragon.core.base;

import java.util.Date;

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
}
