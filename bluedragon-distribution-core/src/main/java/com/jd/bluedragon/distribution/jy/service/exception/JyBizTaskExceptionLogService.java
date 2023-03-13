package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionCycleTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/10 18:23
 * @Description:
 */
public interface JyBizTaskExceptionLogService {

    void recordLog(JyBizTaskExceptionCycleTypeEnum cycle, JyBizTaskExceptionEntity entity);
}
