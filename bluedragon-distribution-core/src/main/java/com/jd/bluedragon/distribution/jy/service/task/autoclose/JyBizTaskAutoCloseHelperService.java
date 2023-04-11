package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;

/**
 * 任务关闭接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
public interface JyBizTaskAutoCloseHelperService {

    String getUnloadBizLastScanTimeKey(String bizId);

    boolean pushBizTaskAutoCloseTask4WaitUnloadNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist);

    boolean pushBizTaskAutoCloseTask4UnloadingNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist);

}
